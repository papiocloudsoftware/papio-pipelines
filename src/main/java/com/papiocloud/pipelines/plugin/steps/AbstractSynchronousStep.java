package com.papiocloud.pipelines.plugin.steps;

import org.jenkinsci.plugins.workflow.steps.Step;
import org.jenkinsci.plugins.workflow.steps.StepContext;
import org.jenkinsci.plugins.workflow.steps.StepExecution;
import org.jenkinsci.plugins.workflow.steps.SynchronousStepExecution;

public abstract class AbstractSynchronousStep extends Step {

    @Override
    public final StepExecution start(StepContext context) {
        return new SynchronousStepExecution<Void>(context) {
            @Override
            protected Void run() throws Exception {
                AbstractSynchronousStep.this.run(context);
                return null;
            }
        };
    }

    protected abstract void run(StepContext context) throws Exception;
}
