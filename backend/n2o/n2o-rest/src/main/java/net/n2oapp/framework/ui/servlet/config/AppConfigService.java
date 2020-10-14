package net.n2oapp.framework.ui.servlet.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.n2oapp.framework.api.MetadataEnvironment;
import net.n2oapp.framework.api.metadata.header.Header;
import net.n2oapp.framework.api.metadata.header.N2oHeader;
import net.n2oapp.framework.api.metadata.pipeline.ReadCompileBindTerminalPipeline;
import net.n2oapp.framework.api.register.SourceInfo;
import net.n2oapp.framework.config.metadata.compile.context.HeaderContext;
import net.n2oapp.framework.ui.servlet.ExposedResourceBundleMessageSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.http.server.reactive.ServerHttpResponse;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.*;


public class AppConfigService {

    private static final Logger log = LoggerFactory.getLogger(AppConfigService.class);
    private AppConfigJsonWriter appConfigJsonWriter;
    private ExposedResourceBundleMessageSource messageSource;
    private ReadCompileBindTerminalPipeline pipeline;
    private MetadataEnvironment environment;
    private String headerSourceId;

    public Mono<Map<String, Object>> doGet(ServerHttpResponse res) {
        Map<String, Object> addedValues = new HashMap<>();
        addedValues.put("menu", getMenu());
        addedValues.put("messages", getMessages());

        return Mono.just(addedValues);
    }

    private Map<String, Object> getMenu() {
        return new ObjectMapper().convertValue(getHeader(), Map.class);
    }

    private Header getHeader() {
        if (headerSourceId != null && !headerSourceId.isEmpty())
            return pipeline.get(new HeaderContext(headerSourceId), null);
        List<SourceInfo> headers = environment.getMetadataRegister().find(N2oHeader.class);
        if (headers == null || headers.isEmpty()) {
            return pipeline.get(new HeaderContext("default"), null);
        }
        return pipeline.get(new HeaderContext(headers.get(0).getId()), null);
    }

    private Map<String, String> getMessages() {
        MessageSourceAccessor accessor = new MessageSourceAccessor(messageSource);
        Locale locale = LocaleContextHolder.getLocale();
        Set<String> messagesBaseNames = messageSource.getBasenameSet();
        Map<String, String> map = new TreeMap<>();
        for (String baseName : messagesBaseNames) {
            Set<String> keys = messageSource.getKeys(baseName, locale);
            for (String key : keys) {
                map.put(key, accessor.getMessage(key, locale));
            }
        }
        return map;
    }

    public void setAppConfigJsonWriter(AppConfigJsonWriter appConfigJsonWriter) {
        this.appConfigJsonWriter = appConfigJsonWriter;
    }

    public AppConfigJsonWriter getAppConfigJsonWriter() {
        return appConfigJsonWriter;
    }

    public void setMessageSource(ExposedResourceBundleMessageSource messageSource) {
        this.messageSource = messageSource;
    }

    public void setPipeline(ReadCompileBindTerminalPipeline pipeline) {
        this.pipeline = pipeline;
    }

    public void setHeaderSourceId(String headerSourceId) {
        this.headerSourceId = headerSourceId;
    }

    public void setEnvironment(MetadataEnvironment environment) {
        this.environment = environment;
    }
}
