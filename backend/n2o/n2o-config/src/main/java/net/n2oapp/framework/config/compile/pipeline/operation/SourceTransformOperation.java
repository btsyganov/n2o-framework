package net.n2oapp.framework.config.compile.pipeline.operation;

import net.n2oapp.criteria.dataset.DataSet;
import net.n2oapp.framework.api.MetadataEnvironment;
import net.n2oapp.framework.api.metadata.aware.MetadataEnvironmentAware;
import net.n2oapp.framework.api.metadata.aware.PipelineOperationTypeAware;
import net.n2oapp.framework.api.metadata.compile.BindProcessor;
import net.n2oapp.framework.api.metadata.compile.CompileContext;
import net.n2oapp.framework.api.metadata.compile.CompileProcessor;
import net.n2oapp.framework.api.metadata.compile.SourceTransformerFactory;
import net.n2oapp.framework.api.metadata.pipeline.PipelineOperation;
import net.n2oapp.framework.api.metadata.pipeline.PipelineOperationType;
import net.n2oapp.framework.api.metadata.validate.ValidateProcessor;

import java.util.function.Supplier;

/**
 * Операция по трансформации исходных метаданных в конвеере
 */
public class SourceTransformOperation<S> implements PipelineOperation<S, S>, PipelineOperationTypeAware, MetadataEnvironmentAware {

    private SourceTransformerFactory factory;

    public SourceTransformOperation() {
    }

    public SourceTransformOperation(SourceTransformerFactory factory) {
        this.factory = factory;
    }
    
    @Override
    public S execute(CompileContext<?,?> context, DataSet data, Supplier<S> supplier, CompileProcessor compileProcessor,
                     BindProcessor bindProcessor,
                     ValidateProcessor validateProcessor) {
        S value = supplier.get();
        if (value == null)
            return null;
        return factory.transform(value);
    }

    @Override
    public void setEnvironment(MetadataEnvironment environment) {
        this.factory = environment.getSourceTransformerFactory();
    }

    @Override
    public PipelineOperationType getPipelineOperationType() {
        return PipelineOperationType.SOURCE_TRANSFORM;
    }
}
