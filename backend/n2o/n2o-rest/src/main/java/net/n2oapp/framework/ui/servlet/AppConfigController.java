package net.n2oapp.framework.ui.servlet;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Controller
public class AppConfigController {

    private final AppConfigService service;

    public AppConfigController(AppConfigService service) {
        this.service = service;
    }

    @GetMapping(path = {"/n2o/config"})
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        service.doGet(request, response);
    }
}
