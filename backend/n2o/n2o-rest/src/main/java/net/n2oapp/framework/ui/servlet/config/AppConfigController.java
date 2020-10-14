package net.n2oapp.framework.ui.servlet.config;

import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestController
public class AppConfigController {

    private final AppConfigService service;

    public AppConfigController(AppConfigService service) {
        this.service = service;
    }

    @GetMapping(path = {"/n2o/config"})
    public Mono<Map<String, Object>> doGet(ServerHttpResponse response) {
        return service.doGet(response);
    }
}
