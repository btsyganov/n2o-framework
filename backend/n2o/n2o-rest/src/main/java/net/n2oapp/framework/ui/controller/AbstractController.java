package net.n2oapp.framework.ui.controller;

import net.n2oapp.criteria.api.Direction;
import net.n2oapp.criteria.api.Sorting;
import net.n2oapp.criteria.dataset.DataSet;
import net.n2oapp.criteria.filters.FilterType;
import net.n2oapp.framework.api.MetadataEnvironment;
import net.n2oapp.framework.api.criteria.N2oPreparedCriteria;
import net.n2oapp.framework.api.criteria.Restriction;
import net.n2oapp.framework.api.metadata.Compiled;
import net.n2oapp.framework.api.metadata.compile.CompileContext;
import net.n2oapp.framework.api.metadata.event.action.UploadType;
import net.n2oapp.framework.api.metadata.global.dao.N2oQuery;
import net.n2oapp.framework.api.metadata.local.CompiledObject;
import net.n2oapp.framework.api.metadata.local.CompiledQuery;
import net.n2oapp.framework.api.register.route.MetadataRouter;
import net.n2oapp.framework.api.ui.ActionRequestInfo;
import net.n2oapp.framework.api.ui.QueryRequestInfo;
import net.n2oapp.framework.api.user.UserContext;
import net.n2oapp.framework.config.compile.pipeline.N2oPipelineSupport;
import net.n2oapp.framework.config.metadata.compile.context.ActionContext;
import net.n2oapp.framework.config.metadata.compile.context.QueryContext;
import net.n2oapp.framework.config.register.route.N2oRouter;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

import static net.n2oapp.framework.mvc.n2o.AbstractService.USER;

public abstract class AbstractController {
    private MetadataRouter router;
    private MetadataEnvironment environment;

    public AbstractController(MetadataEnvironment environment) {
        this.environment = environment;
        this.router = new N2oRouter(environment, environment.getReadCompilePipelineFunction().apply(new N2oPipelineSupport(environment)));
    }

    public AbstractController(MetadataEnvironment environment, MetadataRouter router) {
        this.environment = environment;
        this.router = router;
    }

    public void setEnvironment(MetadataEnvironment environment) {
        this.environment = environment;
    }

    public void setRouter(MetadataRouter router) {
        this.router = router;
    }

    @SuppressWarnings("unchecked")
    protected ActionRequestInfo createActionRequestInfo(String path,
                                                        Map<String, String[]> queryParams,
                                                        Map<String, String[]> headerParams,
                                                        Object body,
                                                        UserContext user) {
        ActionContext actionCtx = (ActionContext) router.get(path, CompiledObject.class, queryParams);
        DataSet queryData = actionCtx.getParams(path, queryParams);
        CompiledObject object = environment.getReadCompileBindTerminalPipelineFunction()
                .apply(new N2oPipelineSupport(environment))
                .get(actionCtx, queryData);
        CompiledObject.Operation operation = object.getOperations().get(actionCtx.getOperationId());

        DataSet bodyData = convertToDataSet(body, actionCtx.getOperationMapping());
        putParams(headerParams, bodyData, actionCtx.getOperationMapping());
        putParams(queryParams, bodyData, actionCtx.getOperationMapping());
        putParams(queryData, bodyData, actionCtx.getOperationMapping());

        ActionRequestInfo<DataSet> requestInfo = new ActionRequestInfo<>();
        requestInfo.setContext(actionCtx);
        requestInfo.setQueryData(queryData);
        requestInfo.setData(bodyData);
        requestInfo.setUser(user);
        requestInfo.setObject(object);
        requestInfo.setOperation(operation);
        requestInfo.setRedirect(actionCtx.getRedirect());
        requestInfo.setMessageOnSuccess(actionCtx.isMessageOnSuccess());
        requestInfo.setMessageOnFail(actionCtx.isMessageOnFail());
        requestInfo.setSuccessAlertWidgetId(actionCtx.getSuccessAlertWidgetId());
        requestInfo.setFailAlertWidgetId(actionCtx.getFailAlertWidgetId());
        requestInfo.setMessagesForm(actionCtx.getMessagesForm());
        //requestInfo.setChoice(); todo
        return requestInfo;
    }

    private void putParams(Map<String, String[]> params, DataSet data, Map<String, String> mapping) {
        if (params != null && mapping != null) {
            for (Map.Entry<String, String> entry : mapping.entrySet()) {
                String[] value = params.get(entry.getKey());
                if (value != null) {
                    if (value.length == 1) {
                        data.put(entry.getValue(), value[0]);
                    } else {
                        data.put(entry.getValue(), Arrays.asList(value));
                    }
                }
            }
        }
    }

    private void putParams(DataSet params, DataSet data, Map<String, String> mapping) {
        if (params != null && mapping != null) {
            for (Map.Entry<String, String> entry : mapping.entrySet()) {
                Object value = params.get(entry.getKey());
                if (value != null)
                    data.put(entry.getValue(), value);
            }
        }
    }

    private DataSet convertToDataSet(Object body, Map<String, String> mapping) {
        DataSet result = (body instanceof DataSet) ? (DataSet) body :
                new DataSet((Map<? extends String, ?>) body);
        if (mapping != null) {
            for (Map.Entry<String, String> entry : mapping.entrySet()) {
                Object value = result.get(entry.getKey());
                if (value != null && (result.get(entry.getValue()) == null || result.containsKey(entry.getKey()))) {
                    result.put(entry.getValue(), value);
                }
            }
        }
        return result;
    }

    private void prepareSelectedId(QueryRequestInfo requestInfo) {
        String selectedId = requestInfo.getData().getString("selectedId");
        if (requestInfo.getQuery() == null) return;
        if (selectedId == null) return;
        N2oQuery.Field fieldPK = requestInfo.getQuery().getFieldsMap().get(N2oQuery.Field.PK);
        String domain = fieldPK != null ? fieldPK.getDomain() : null;
        requestInfo.setSelectedId(environment.getDomainProcessor().doDomainConversion(domain, selectedId));
    }

    private N2oPreparedCriteria prepareCriteria(CompiledQuery query, DataSet data, QueryContext queryCtx) {
        N2oPreparedCriteria criteria = new N2oPreparedCriteria();
        Integer page = data.getInteger("page");
        criteria.setPage(page != null ? page : 1);
        Integer size = data.getInteger("size");
        criteria.setSize(
                size != null ? size : (queryCtx.getQuerySize() != null ? queryCtx.getQuerySize() : 10)
        );
        Integer count = data.getInteger("count");
        criteria.setCount(count);
        if (query != null) {
            criteria.setSortings(getSortings(data, queryCtx.getSortingMap()));
            prepareRestrictions(query, criteria, data);
        }
        return criteria;
    }


    private List<Sorting> getSortings(DataSet data, Map<String, String> sortingMap) {
        List<Sorting> sortings = new ArrayList<>();
        DataSet sortingsData = data.getDataSet("sorting");
        if (sortingsData == null)
            return sortings;
        for (String key : sortingsData.flatKeySet()) {
            String fieldId = sortingMap == null || !sortingMap.containsKey(key) ? key : sortingMap.get(key);
            String value = sortingsData.getString(key);
            Direction direction = value != null ? Direction.valueOf(value.toUpperCase()) : Direction.ASC;
            sortings.add(new Sorting(fieldId, direction));
        }
        return sortings;
    }

    private void prepareRestrictions(CompiledQuery query, N2oPreparedCriteria criteria, DataSet data) {
        for (Map.Entry<String, String> paramEntry : query.getParamToFilterIdMap().entrySet()) {
            Object value = data.get(paramEntry.getKey());
            if (value != null) {
                String filterId = paramEntry.getValue();
                if (query.getInvertFiltersMap().containsKey(filterId)) {
                    Map.Entry<String, FilterType> typeEntry = query.getInvertFiltersMap().get(filterId);
                    String fieldId = typeEntry.getKey();
                    FilterType filterType = typeEntry.getValue();
                    Restriction restriction = new Restriction(fieldId, value, filterType);
                    criteria.addRestriction(restriction);
                } else {
                    criteria.putAdditionalField(filterId, value);
                }
            }
        }
    }

    @Deprecated
    protected QueryRequestInfo createQueryRequestInfo(HttpServletRequest request) {
        CompiledQuery query;
        QueryContext queryCtx = (QueryContext) getRoutingResult(request, CompiledQuery.class);
        DataSet data = queryCtx.getParams(request.getPathInfo(), request.getParameterMap());
        query = environment.getReadCompileBindTerminalPipelineFunction()
                .apply(new N2oPipelineSupport(environment))
                .get(queryCtx, data);
        QueryRequestInfo requestInfo = new QueryRequestInfo();
        requestInfo.setUser(getUser(request));
        requestInfo.setQuery(query);
        requestInfo.setData(data);
        requestInfo.setUpload(queryCtx.getUpload() != null ? queryCtx.getUpload() : UploadType.query);
        requestInfo.setCriteria(prepareCriteria(requestInfo.getQuery(), data, queryCtx));
        requestInfo.setSuccessAlertWidgetId(queryCtx.getSuccessAlertWidgetId());
        requestInfo.setFailAlertWidgetId(queryCtx.getFailAlertWidgetId());
        requestInfo.setMessagesForm(queryCtx.getMessagesForm());
        request.setAttribute("messageForm", queryCtx.getFailAlertWidgetId());
        prepareSelectedId(requestInfo);
        return requestInfo;
    }

    protected QueryRequestInfo createQueryRequestInfo(String path, Map<String, String[]> params, UserContext user) {
        CompiledQuery query;
        QueryContext queryCtx = (QueryContext) router.get(path, CompiledQuery.class, params);
        DataSet data = queryCtx.getParams(path, params);
        query = environment.getReadCompileBindTerminalPipelineFunction()
                .apply(new N2oPipelineSupport(environment))
                .get(queryCtx, data);
        QueryRequestInfo requestInfo = new QueryRequestInfo();
        requestInfo.setUser(user);
        requestInfo.setQuery(query);
        requestInfo.setData(data);
        requestInfo.setUpload(queryCtx.getUpload() != null ? queryCtx.getUpload() : UploadType.query);
        requestInfo.setCriteria(prepareCriteria(requestInfo.getQuery(), data, queryCtx));
        requestInfo.setSuccessAlertWidgetId(queryCtx.getSuccessAlertWidgetId());
        requestInfo.setFailAlertWidgetId(queryCtx.getFailAlertWidgetId());
        requestInfo.setMessagesForm(queryCtx.getMessagesForm());
        requestInfo.setSize(requestInfo.getCriteria().getSize());
        prepareSelectedId(requestInfo);
        return requestInfo;
    }

    private UserContext getUser(HttpServletRequest req) {
        UserContext user = (UserContext) req.getAttribute(USER);
        if (user == null)
            throw new IllegalStateException("User is not initialized");
        return user;
    }

    private <D extends Compiled> CompileContext<D, ?> getRoutingResult(HttpServletRequest req, Class<D> compiledClass) {
        String path = req.getPathInfo();
        return router.get(path, compiledClass, req.getParameterMap());
    }
}
