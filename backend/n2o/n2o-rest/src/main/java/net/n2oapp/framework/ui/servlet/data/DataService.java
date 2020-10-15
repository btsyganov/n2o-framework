package net.n2oapp.framework.ui.servlet.data;


import net.n2oapp.criteria.dataset.DataSet;
import net.n2oapp.framework.api.exception.N2oException;
import net.n2oapp.framework.api.rest.GetDataResponse;
import net.n2oapp.framework.api.rest.SetDataResponse;
import net.n2oapp.framework.api.user.UserContext;
import net.n2oapp.framework.mvc.n2o.AbstractService;
import net.n2oapp.framework.ui.controller.DataController;
import org.apache.commons.io.IOUtils;
import org.springframework.http.server.reactive.ServerHttpRequest;
import reactor.core.publisher.Mono;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Универсальный сервлет обработки данных в json
 */
public class DataService extends AbstractService {
    private final DataController controller;

    public DataService(DataController controller) {
        super();
        this.controller = controller;
    }

    public Mono<GetDataResponse> getData(ServerHttpRequest request) {
        Map<String, String[]> queryParams = new HashMap<>();
        for (Map.Entry<String, List<String>> kv : request.getQueryParams().entrySet()) {
            queryParams.put(kv.getKey(), kv.getValue().toArray(new String[0]));
        }

        if (!request.getPath().value().startsWith("/n2o/data"))
            throw new IllegalStateException("Request should starts with /n2o/data");
        String path = request.getPath().value().substring(9);

        GetDataResponse result = controller.getData(path, queryParams, null);
        return Mono.just(result);
    }

    @Override
    protected void safeDoGet(HttpServletRequest req, HttpServletResponse res)
            throws IOException {
        GetDataResponse result = controller.getData(getRequestPath(req.getServletPath()), req.getParameterMap(),
                (UserContext) req.getAttribute(USER));
        res.setStatus(result.getStatus());
        res.setContentType("application/json");
        objectMapper.writeValue(res.getWriter(), result);

    }

    @Override
    protected void safeDoPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
        SetDataResponse result = controller.setData(getRequestPath(req.getServletPath()),
                req.getParameterMap(),
                getHeaders(req),
                getRequestBody(req),
                (UserContext) req.getAttribute(USER));
        res.setStatus(result.getStatus());
        res.setContentType("application/json");
        objectMapper.writeValue(res.getWriter(), result);
    }

    private Object getRequestBody(HttpServletRequest request) {
        try {
            if (request.getReader() == null) return new DataSet();
            String body = IOUtils.toString(request.getReader()).trim();
            if (body.startsWith("[")) {
                return objectMapper.<List<DataSet>>readValue(body,
                        objectMapper.getTypeFactory().constructCollectionType(List.class, DataSet.class)
                );
            } else {
                return objectMapper.readValue(body, DataSet.class);
            }
        } catch (IOException e) {
            throw new N2oException(e);
        }
    }

    private String getRequestPath(String servletPath) {
        if (!servletPath.startsWith("/n2o/data"))
            throw new IllegalStateException("Request should starts with /n2o/data");
        String path = servletPath.substring(9);
        return path;
    }

    private Map<String, String[]> getHeaders(HttpServletRequest req) {
        Map<String, String[]> result = new HashMap<>();
        Enumeration<String> iter = req.getHeaderNames();
        while (iter.hasMoreElements()) {
            String name = iter.nextElement();
            result.put(name, new String[]{req.getHeader(name)});
        }
        return result;
    }

}
