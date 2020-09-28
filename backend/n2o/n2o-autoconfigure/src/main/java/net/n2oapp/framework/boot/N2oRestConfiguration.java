package net.n2oapp.framework.boot;

import net.n2oapp.framework.api.MetadataEnvironment;
import net.n2oapp.framework.api.register.route.MetadataRouter;
import net.n2oapp.framework.api.rest.ControllerFactory;
import net.n2oapp.framework.api.ui.ErrorMessageBuilder;
import net.n2oapp.framework.ui.controller.DataController;
import net.n2oapp.framework.ui.controller.N2oControllerFactory;
import net.n2oapp.framework.ui.controller.action.SetController;
import net.n2oapp.framework.ui.controller.query.GetController;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * Конфигурация контроллеров
 */
@Configuration
@ServletComponentScan("net.n2oapp.framework")
@ComponentScan(basePackages = "net.n2oapp.framework.ui", lazyInit = true)
public class N2oRestConfiguration {

    @Value("${n2o.ui.message.stacktrace:true}")
    private Boolean showStacktrace;


    @Bean
    ControllerFactory controllerFactory(Map<String, SetController> setControllers,
                                        Map<String, GetController> getControllers) {
        Map<String, Object> controllers = new HashMap<>();
        controllers.putAll(setControllers);
        controllers.putAll(getControllers);
        return new N2oControllerFactory(controllers);
    }

    @Bean
    public DataController dataController(ControllerFactory controllerFactory,
                                         MetadataEnvironment environment,
                                         MetadataRouter router) {
        return new DataController(controllerFactory, environment, router);
    }

    @Bean
    public ErrorMessageBuilder errorMessageBuilder(MetadataEnvironment environment) {
        return new ErrorMessageBuilder(environment.getMessageSource(), showStacktrace);
    }

}
