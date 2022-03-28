package com.papiocloud.pipelines.plugin.steps.pr;

import com.google.common.collect.ImmutableSet;
import com.papiocloud.pipelines.plugin.steps.AbstractSynchronousStep;
import hudson.model.TaskListener;
import jenkins.scm.api.SCMHead;
import org.jenkinsci.plugins.github_branch_source.PullRequestSCMHead;
import org.jenkinsci.plugins.workflow.job.WorkflowRun;
import org.jenkinsci.plugins.workflow.steps.StepContext;

import java.io.PrintStream;
import java.util.Set;

public abstract class AbstractPullRequestStep extends AbstractSynchronousStep {

    protected static final Set<? extends Class<?>> REQUIRED_CONTEXT = ImmutableSet.of(TaskListener.class, WorkflowRun.class);

    @Override
    protected final void run(StepContext context) throws Exception {
        WorkflowRun run = context.get(WorkflowRun.class);
        TaskListener listener = context.get(TaskListener.class);
        PrintStream logger = listener.getLogger();
        SCMHead scmHead = SCMHead.HeadByItem.findHead(run.getParent());
        if (scmHead instanceof PullRequestSCMHead) {
            doPullRequestStep(run);
        } else {
            logger.println("Skipping PR step, build is not part of a pull request");
        }
    }

    abstract void doPullRequestStep(WorkflowRun run) throws Exception;
}
