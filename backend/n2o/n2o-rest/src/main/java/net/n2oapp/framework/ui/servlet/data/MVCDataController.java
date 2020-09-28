package net.n2oapp.framework.ui.servlet.data;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Controller
public class MVCDataController {

    private final DataService dataService;

    public MVCDataController(DataService dataService) {
        this.dataService = dataService;
    }

    @GetMapping(path = {"/n2o/data**", "/n2o/data/**"})
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        dataService.doGet(request, response);
    }

    @PostMapping(path = {"/n2o/data**", "/n2o/data/**"})
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        dataService.doPost(request, response);
    }
}
