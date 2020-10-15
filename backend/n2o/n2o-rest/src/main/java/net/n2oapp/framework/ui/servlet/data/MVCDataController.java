package net.n2oapp.framework.ui.servlet.data;

import net.n2oapp.framework.api.rest.GetDataResponse;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
public class MVCDataController {

    private final DataService dataService;

    public MVCDataController(DataService dataService) {
        this.dataService = dataService;
    }

    @GetMapping(path = {"/n2o/data**", "/n2o/data/**"})
    public Mono<GetDataResponse> doGet(ServerHttpRequest request) {
        return dataService.getData(request);
    }

    @PostMapping(path = {"/n2o/data**", "/n2o/data/**"})
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        dataService.doPost(request, response);
    }
}
