package net.n2oapp.framework.api.metadata;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import net.n2oapp.framework.api.metadata.aware.JsonPropertiesAware;

import java.util.Map;

/**
 * Клиентская модель компонента
 */
@Getter
@Setter
public abstract class Component implements Compiled, JsonPropertiesAware {

    @JsonProperty
    private String src;
    @JsonProperty
    private String className;
    @JsonProperty
    private Map<String, String> style;
    private Map<String, Object> properties;
}
