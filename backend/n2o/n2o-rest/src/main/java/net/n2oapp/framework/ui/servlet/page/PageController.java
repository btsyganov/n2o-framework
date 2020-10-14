package net.n2oapp.framework.ui.servlet.page;

import net.n2oapp.framework.api.metadata.meta.page.Page;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
public class PageController {
    private final PageService pageService;

    public PageController(PageService pageService) {
        this.pageService = pageService;
    }

    @GetMapping(path = {"/n2o/page**", "/n2o/page/**"})
    public Mono<Page> getPage(ServerHttpRequest request) {
        return pageService.safeDoGet0(request);
    }
}
