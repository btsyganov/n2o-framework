package net.n2oapp.framework.ui.servlet.page;

import net.n2oapp.framework.api.metadata.compile.CompileContext;
import net.n2oapp.framework.api.metadata.meta.page.Page;
import net.n2oapp.framework.api.metadata.pipeline.ReadCompileBindTerminalPipeline;
import net.n2oapp.framework.api.register.route.MetadataRouter;
import net.n2oapp.framework.api.util.SubModelsProcessor;
import net.n2oapp.framework.mvc.n2o.AbstractService;
import org.springframework.http.server.reactive.ServerHttpRequest;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Сервлет возвращающий страницу по запросу /n2o/page/*
 */
public class PageService extends AbstractService {
    private MetadataRouter router;
    private ReadCompileBindTerminalPipeline pipeline;
    private SubModelsProcessor subModelsProcessor;

    public Mono<Page> safeDoGet0(ServerHttpRequest request) {
        if (!request.getPath().value().startsWith("/n2o/page"))
            throw new IllegalStateException("Request should start with /n2o/page");
        String path = request.getPath().value().substring(9);

        Map<String, String[]> queryParams = new HashMap<>();
        for (Map.Entry<String, List<String>> kv : request.getQueryParams().entrySet()) {
            queryParams.put(kv.getKey(), kv.getValue().toArray(new String[0]));
        }

        CompileContext<Page, ?> context = router.get(path, Page.class, queryParams);
        Page page = pipeline.get(context, context.getParams(path, queryParams), subModelsProcessor);
        return Mono.just(page);
    }

    public void setRouter(MetadataRouter router) {
        this.router = router;
    }

    public void setPipeline(ReadCompileBindTerminalPipeline pipeline) {
        this.pipeline = pipeline;
    }

    public void setSubModelsProcessor(SubModelsProcessor subModelsProcessor) {
        this.subModelsProcessor = subModelsProcessor;
    }
}
