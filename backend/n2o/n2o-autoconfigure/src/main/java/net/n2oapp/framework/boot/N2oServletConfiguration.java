package net.n2oapp.framework.boot;

import net.n2oapp.framework.api.MetadataEnvironment;
import net.n2oapp.framework.api.context.ContextProcessor;
import net.n2oapp.framework.api.metadata.pipeline.ReadCompileBindTerminalPipeline;
import net.n2oapp.framework.api.register.route.MetadataRouter;
import net.n2oapp.framework.api.ui.ErrorMessageBuilder;
import net.n2oapp.framework.api.util.SubModelsProcessor;
import net.n2oapp.framework.config.compile.pipeline.N2oPipelineSupport;
import net.n2oapp.framework.mvc.cache.ClientCacheTemplate;
import net.n2oapp.framework.mvc.cache.LifetimeClientCacheTemplate;
import net.n2oapp.framework.ui.controller.DataController;
import net.n2oapp.framework.ui.servlet.ExposedResourceBundleMessageSource;
import net.n2oapp.framework.ui.servlet.ModifiedClientCacheTemplate;
import net.n2oapp.framework.ui.servlet.config.AppConfigJsonWriter;
import net.n2oapp.framework.ui.servlet.config.AppConfigService;
import net.n2oapp.framework.ui.servlet.data.DataService;
import net.n2oapp.framework.ui.servlet.page.PageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;

import java.util.Optional;

@Configuration
public class N2oServletConfiguration {
    @Value("${n2o.header.id:}")
    private String headerId;

    @Value("${n2o.api.url:/n2o}")
    private String n2oApiUrl;

    @Bean
    @ConditionalOnProperty(name = "n2o.ui.cache.page.enabled", havingValue = "true")
    public ClientCacheTemplate pageClientCacheTemplate(CacheManager cacheManager, Environment env) {
        boolean enabled = env.getProperty("n2o.ui.cache.page.enabled", Boolean.class, false);
        if (!enabled)
            return null;
        long lifetime = env.getProperty("n2o.ui.cache.page.lifetime", Long.class, 1000 * 60 * 10L);
        String mode = env.getProperty("n2o.ui.cache.page.mode", String.class, "lifetime");
        switch (mode) {
            case "lifetime":
                return new LifetimeClientCacheTemplate(lifetime);
            case "modified":
                return new ModifiedClientCacheTemplate(cacheManager);
            default:
                throw new UnsupportedOperationException("Unknown page client cache mode " + mode);
        }
    }

    @Bean
    public PageService pageServlet(MetadataEnvironment env, MetadataRouter router,
                                   ErrorMessageBuilder errorMessageBuilder,
                                   SubModelsProcessor subModelsProcessor,
                                   Optional<ClientCacheTemplate> pageClientCacheTemplate) {
        PageService pageService = new PageService();
        ReadCompileBindTerminalPipeline pipeline = N2oPipelineSupport.readPipeline(env)
                .read().transform().validate().cache().copy()
                .compile().transform().cache().copy()
                .bind();
        pageService.setPipeline(pipeline);
        pageService.setRouter(router);
        pageService.setObjectMapper(ObjectMapperConstructor.metaObjectMapper());
        pageService.setErrorMessageBuilder(errorMessageBuilder);
        pageService.setSubModelsProcessor(subModelsProcessor);
        pageClientCacheTemplate.ifPresent(pageService::setClientCacheTemplate);
        return pageService;
    }

    @Bean
    public DataService dataServlet(DataController controller,
                                   ErrorMessageBuilder errorMessageBuilder) {
        DataService dataService = new DataService(controller);
        dataService.setObjectMapper(ObjectMapperConstructor.metaObjectMapper());
        dataService.setErrorMessageBuilder(errorMessageBuilder);
        return dataService;
    }

    @Bean
    public AppConfigService appConfigService(ConfigurableEnvironment configurableEnvironment,
                                             ContextProcessor contextProcessor,
                                             ExposedResourceBundleMessageSource clientMessageSource,
                                             MetadataEnvironment env) {
        AppConfigJsonWriter writer = new AppConfigJsonWriter();
        writer.setContextProcessor(contextProcessor);
        writer.setPropertyResolver(configurableEnvironment);
        writer.setObjectMapper(ObjectMapperConstructor.metaObjectMapper());
        writer.setPath("classpath*:META-INF/config.json");
        writer.setOverridePath("classpath*:META-INF/config-build.json");

        AppConfigService appConfigService = new AppConfigService();
        appConfigService.setAppConfigJsonWriter(writer);
        appConfigService.setMessageSource(clientMessageSource);
        appConfigService.setEnvironment(env);
        ReadCompileBindTerminalPipeline pipeline = N2oPipelineSupport.readPipeline(env)
                .read().transform().validate().cache().copy()
                .compile().transform().cache().copy()
                .bind();
        appConfigService.setPipeline(pipeline);
        appConfigService.setHeaderSourceId(headerId);
        return appConfigService;
    }
}
