package net.n2oapp.framework.ui.servlet.page;

import net.n2oapp.framework.api.metadata.compile.CompileContext;
import net.n2oapp.framework.api.metadata.meta.page.Page;
import net.n2oapp.framework.api.metadata.pipeline.ReadCompileBindTerminalPipeline;
import net.n2oapp.framework.api.register.route.MetadataRouter;
import net.n2oapp.framework.api.util.SubModelsProcessor;
import net.n2oapp.framework.mvc.n2o.N2oServlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Сервлет возвращающий страницу по запросу /n2o/page/*
 */
public class PageService extends N2oServlet {
    private MetadataRouter router;
    private ReadCompileBindTerminalPipeline pipeline;
    private SubModelsProcessor subModelsProcessor;

    @Override
    public void safeDoGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        if (!req.getServletPath().startsWith("/n2o/page"))
            throw new IllegalStateException("Request should start with /n2o/page");
        String path = req.getServletPath().substring(9);
        CompileContext<Page, ?> context = router.get(path, Page.class, req.getParameterMap());
        Page page = pipeline.get(context, context.getParams(path, req.getParameterMap()), subModelsProcessor);
        resp.setContentType("application/json");
        objectMapper.writeValue(resp.getWriter(), page);
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
