package net.n2oapp.framework.ui.servlet.page;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Controller
public class PageController {
    private final PageService pageService;

    public PageController(PageService pageService) {
        this.pageService = pageService;
    }

    @GetMapping(path = {"/n2o/page**", "/n2o/page/**"})
    public void getPage(HttpServletRequest request, HttpServletResponse response) throws IOException {
        pageService.safeDoGet(request, response);
    }
}
