package net.n2oapp.framework.engine.data;


import net.n2oapp.criteria.dataset.DataSet;
import net.n2oapp.framework.api.StringUtils;
import net.n2oapp.framework.api.data.InvocationProcessor;
import net.n2oapp.framework.api.data.OperationExceptionHandler;
import net.n2oapp.framework.api.exception.N2oException;
import net.n2oapp.framework.api.metadata.global.dao.object.InvocationParameter;
import net.n2oapp.framework.api.metadata.local.CompiledObject;
import net.n2oapp.framework.engine.processor.N2oActionException;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Процессор действий
 */
public class N2oOperationProcessor {

    private InvocationProcessor invocationProcessor;
    private OperationExceptionHandler exceptionHandler;

    public N2oOperationProcessor(InvocationProcessor invocationProcessor,
                                 OperationExceptionHandler exceptionHandler) {
        this.invocationProcessor = invocationProcessor;
        this.exceptionHandler = exceptionHandler;
    }

    public DataSet invoke(CompiledObject.Operation action, DataSet inDataSet) {
        validateRequiredFields(action.getInParametersMap().values(), inDataSet);
        return invoke(
                action,
                inDataSet,
                action.getInParametersMap().values(),
                action.getOutParametersMap().values()
        );
    }

    public DataSet invoke(CompiledObject.Operation operation,
                          DataSet inDataSet,
                          Collection<? extends InvocationParameter> inParameters,
                          Collection<? extends InvocationParameter> outParameters) {
        try {
            return invocationProcessor.invoke(
                    operation.getInvocation(),
                    inDataSet,
                    inParameters,
                    outParameters
            );
        } catch (Exception e) {
            throw exceptionHandler.handle(operation, inDataSet, e);
        }
    }


    private void validateRequiredFields(Collection<? extends InvocationParameter> inParameters, DataSet inDataSet) {
        if (inParameters == null || inParameters.isEmpty()) {
            return;
        }

        List<String> requiredFields = inParameters.stream()
                .filter(in -> in.getRequired() != null && in.getRequired())
                .map(InvocationParameter::getId)
                .collect(Collectors.toList());

        boolean allMatch = requiredFields.stream()
                .allMatch(inDataSet::containsKey);
        if (!allMatch) {
            throw new IllegalStateException(String.format("Action required fields[%s]",
                    requiredFields.stream().collect(Collectors.joining(","))));
        }
    }
}
