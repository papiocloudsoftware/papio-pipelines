package com.papiocloud.pipelines.plugin.steps.pr;

import com.google.common.collect.ImmutableSet;
import com.papiocloud.pipelines.plugin.steps.AbstractSynchronousStep;
import hudson.Extension;
import hudson.model.TaskListener;
import jenkins.scm.api.SCMHead;
import org.eclipse.egit.github.core.CommitComment;
import org.eclipse.egit.github.core.RepositoryId;
import org.jenkinsci.plugins.github_branch_source.PullRequestSCMHead;
import org.jenkinsci.plugins.pipeline.github.GitHubHelper;
import org.jenkinsci.plugins.pipeline.github.client.ExtendedIssueService;
import org.jenkinsci.plugins.pipeline.github.client.ExtendedMergeStatus;
import org.jenkinsci.plugins.pipeline.github.client.ExtendedPullRequest;
import org.jenkinsci.plugins.pipeline.github.client.ExtendedPullRequestService;
import org.jenkinsci.plugins.workflow.job.WorkflowJob;
import org.jenkinsci.plugins.workflow.job.WorkflowRun;
import org.jenkinsci.plugins.workflow.steps.StepContext;
import org.jenkinsci.plugins.workflow.steps.StepDescriptor;
import org.kohsuke.stapler.DataBoundConstructor;
import org.slf4j.LoggerFactory;

import java.io.PrintStream;
import java.util.Set;
import java.util.logging.Logger;

/**
 * Comments on a PR
 * <code>
 *     pipeline {
 *       ...
 *       post {
 *           failure {
 *               commentPullRequest("[PR Build Failure](${env.BUILD_URL})")
 *           }
 *       }
 *     }
 * </code>
 */
public class CommentPullRequestStep extends AbstractPullRequestStep {

    private String comment;

    @DataBoundConstructor
    public CommentPullRequestStep(String comment) {
        this.comment = comment;
    }

    @Override
    void doPullRequestStep(WorkflowRun run) throws Exception {
        WorkflowJob job = run.getParent();
        PullRequestSCMHead pullRequestHead = GitHubHelper.getPullRequest(job);
        RepositoryId base = GitHubHelper.getRepositoryId(job);

        ExtendedIssueService service = new ExtendedIssueService(
                GitHubHelper.getGitHubClient(job)
        );

        service.createComment(base, pullRequestHead.getNumber(), comment);
    }

    @Extension
    public static class DescriptorImpl extends StepDescriptor {

        @Override
        public String getDisplayName() {
            return "Comments on the pull request if the build was triggered by one";
        }

        @Override
        public String getFunctionName() {
            return "commentPullRequest";
        }

        @Override
        public Set<? extends Class<?>> getRequiredContext() {
            return REQUIRED_CONTEXT;
        }

    }
}
