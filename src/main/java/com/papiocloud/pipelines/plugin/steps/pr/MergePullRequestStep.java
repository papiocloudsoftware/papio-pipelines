package com.papiocloud.pipelines.plugin.steps.pr;

import hudson.Extension;
import org.eclipse.egit.github.core.RepositoryId;
import org.jenkinsci.plugins.github_branch_source.PullRequestSCMHead;
import org.jenkinsci.plugins.pipeline.github.GitHubHelper;
import org.jenkinsci.plugins.pipeline.github.client.ExtendedMergeStatus;
import org.jenkinsci.plugins.pipeline.github.client.ExtendedPullRequest;
import org.jenkinsci.plugins.pipeline.github.client.ExtendedPullRequestService;
import org.jenkinsci.plugins.workflow.job.WorkflowJob;
import org.jenkinsci.plugins.workflow.job.WorkflowRun;
import org.jenkinsci.plugins.workflow.steps.*;
import org.kohsuke.stapler.DataBoundConstructor;

import java.util.Set;

/**
 * Auto merges a PR if build is from a pull request
 * <code>
 *     pipeline {
 *       ...
 *       post {
 *           success {
 *               mergePullRequest()
 *           }
 *       }
 *     }
 * </code>
 */
public class MergePullRequestStep extends AbstractPullRequestStep {

    @DataBoundConstructor
    public MergePullRequestStep() {
    }

    @Override
    void doPullRequestStep(WorkflowRun run) throws Exception {
        WorkflowJob job = run.getParent();
        PullRequestSCMHead pullRequestHead = GitHubHelper.getPullRequest(job);
        RepositoryId base = GitHubHelper.getRepositoryId(job);

        ExtendedPullRequestService service = new ExtendedPullRequestService(
                GitHubHelper.getGitHubClient(job)
        );

        ExtendedPullRequest pullRequest = service.getPullRequest(base, pullRequestHead.getNumber());
        if (pullRequest.isMergeable()) {
            ExtendedMergeStatus status = service.merge(base, pullRequestHead.getNumber(), null, null, null, null);
            if (!status.isMerged()) {
                throw new Exception(status.getMessage());
            }
        } else {
            throw new Exception("Pull request could not be auto merged");
        }
    }

    @Extension
    public static class DescriptorImpl extends StepDescriptor {

        @Override
        public String getDisplayName() {
            return "Merges the pull request if the build was triggered by one";
        }

        @Override
        public String getFunctionName() {
            return "mergePullRequest";
        }

        @Override
        public Set<? extends Class<?>> getRequiredContext() {
            return REQUIRED_CONTEXT;
        }

    }
}
