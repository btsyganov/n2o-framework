package net.n2oapp.framework.boot;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.n2oapp.framework.api.context.Context;
import net.n2oapp.framework.api.context.ContextProcessor;
import net.n2oapp.framework.api.data.*;
import net.n2oapp.framework.engine.data.*;
import net.n2oapp.framework.engine.data.java.JavaDataProviderEngine;
import net.n2oapp.framework.engine.data.java.ObjectLocator;
import net.n2oapp.framework.engine.data.json.TestDataProviderEngine;
import net.n2oapp.framework.engine.data.rest.SpringRestDataProviderEngine;
import net.n2oapp.framework.engine.data.rest.json.RestEngineTimeModule;
import net.n2oapp.framework.engine.modules.stack.DataProcessingStack;
import net.n2oapp.framework.engine.modules.stack.SpringDataProcessingStack;
import net.n2oapp.framework.engine.validation.N2oValidationModule;
import net.n2oapp.framework.engine.validation.engine.ValidationProcessor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Конфигурация провайдеров данных
 */
@Configuration
@ComponentScan(basePackages = "net.n2oapp.framework.engine", lazyInit = true)
public class N2oEngineConfiguration {

    @Value("${n2o.engine.pageStartsWith0}")
    private boolean pageStartsWith0;

    @Value("${n2o.engine.rest.url}")
    private String baseRestUrl;

    @Value("${n2o.engine.rest.dateformat.serialize}")
    private String serializingFormat;

    @Value("${n2o.engine.rest.dateformat.deserialize}")
    private String[] deserializingFormats;

    @Value("${n2o.engine.rest.dateformat.exclusion-keys}")
    private String[] exclusionKeys;

    @Value("${n2o.engine.timeout}")
    private String timeoutInMillis;

    @Value("${n2o.config.path}")
    private String configPath;

    @Value("${n2o.engine.test.classpath}")
    private String resourcePath;

    @Bean
    @ConditionalOnMissingBean
    public DataProcessingStack dataProcessingStack(ApplicationContext context) {
        SpringDataProcessingStack dataProcessingStack = new SpringDataProcessingStack();
        dataProcessingStack.setApplicationContext(context);
        return dataProcessingStack;
    }

    @Bean
    @ConditionalOnMissingBean
    public N2oInvocationFactory actionInvocationFactory(ApplicationContext context) {
        N2oInvocationFactory actionInvocationFactory = new N2oInvocationFactory();
        actionInvocationFactory.setApplicationContext(context);
        return actionInvocationFactory;
    }

    @Bean
    @ConditionalOnMissingBean
    public ContextProcessor contextProcessor(Context context) {
        return new ContextProcessor(context);
    }

    @Bean
    @ConditionalOnMissingBean
    public InvocationProcessor invocationProcessor(N2oInvocationFactory invocationFactory,
                                                   ContextProcessor contextProcessor,
                                                   DomainProcessor domainProcessor) {
        return new N2oInvocationProcessor(invocationFactory, contextProcessor, domainProcessor);
    }

    @Bean
    public ValidationProcessor validationProcessor(InvocationProcessor invocationProcessor) {
        return new ValidationProcessor(invocationProcessor);
    }

    @Bean
    public N2oValidationModule validationModule(ValidationProcessor processor) {
        return new N2oValidationModule(processor);
    }

    @Bean
    @ConditionalOnMissingBean
    public QueryProcessor queryProcessor(N2oInvocationFactory invocationFactory,
                                         ContextProcessor contextProcessor,
                                         DomainProcessor domainProcessor,
                                         QueryExceptionHandler exceptionHandler) {
        N2oQueryProcessor n2oQueryProcessor = new N2oQueryProcessor(invocationFactory, contextProcessor,
                domainProcessor, exceptionHandler);
        n2oQueryProcessor.setCriteriaResolver(new N2oCriteriaConstructor(pageStartsWith0));
        n2oQueryProcessor.setPageStartsWith0(pageStartsWith0);
        return n2oQueryProcessor;
    }

    @Bean
    @ConditionalOnMissingBean
    public OperationExceptionHandler operationExceptionHandler() {
        return new N2oOperationExceptionHandler();
    }

    @Bean
    @ConditionalOnMissingBean
    public QueryExceptionHandler queryExceptionHandler() {
        return new N2oQueryExceptionHandler();
    }

    @Bean
    public N2oOperationProcessor actionProcessor(InvocationProcessor invocationProcessor,
                                                 OperationExceptionHandler operationExceptionHandler) {
        return new N2oOperationProcessor(invocationProcessor, operationExceptionHandler);
    }

    @Bean
    public JavaDataProviderEngine javaDataProviderEngine(Optional<List<ObjectLocator>> locators) {
        JavaDataProviderEngine javaDataProviderEngine = new JavaDataProviderEngine();
        javaDataProviderEngine.setLocators(locators.orElse(Collections.EMPTY_LIST));
        return javaDataProviderEngine;
    }

    @Bean
    @ConditionalOnMissingBean(name = "restDataProviderEngine")
    public SpringRestDataProviderEngine springRestDataProviderEngine(RestTemplateBuilder builder) {
        ObjectMapper restObjectMapper = restObjectMapper();
        SpringRestDataProviderEngine springRestDataProviderEngine = new SpringRestDataProviderEngine(
                restTemplate(builder, httpMessageConverter(restObjectMapper)),
                restObjectMapper);
        springRestDataProviderEngine.setBaseRestUrl(baseRestUrl);
        return springRestDataProviderEngine;
    }

    @Bean
    @ConditionalOnMissingBean
    public TestDataProviderEngine testDataProviderEngine() {
        TestDataProviderEngine testDataProviderEngine = new TestDataProviderEngine();
        testDataProviderEngine.setPathOnDisk(configPath);
        testDataProviderEngine.setClasspathResourcePath(resourcePath);
        return testDataProviderEngine;
    }

    private ObjectMapper restObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setDateFormat(new SimpleDateFormat(serializingFormat));
        RestEngineTimeModule module = new RestEngineTimeModule(deserializingFormats, exclusionKeys);
        objectMapper.registerModules(module);
        return objectMapper;
    }

    private MappingJackson2HttpMessageConverter httpMessageConverter(ObjectMapper restObjectMapper) {
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        converter.setObjectMapper(restObjectMapper);
        return converter;
    }

    private RestTemplate restTemplate(RestTemplateBuilder builder, MappingJackson2HttpMessageConverter converter) {
        return builder.messageConverters(converter).build();
    }

}