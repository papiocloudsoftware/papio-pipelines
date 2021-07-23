package com.papiocloud.pipelines.plugin.steps;

import com.google.common.collect.ImmutableSet;
import com.papiocloud.pipelines.plugin.util.OrganizationFolderUtil;
import hudson.Extension;
import jenkins.branch.OrganizationFolder;
import org.jenkinsci.plugins.github_branch_source.GitHubAppCredentials;
import org.jenkinsci.plugins.github_branch_source.GitHubSCMSource;
import org.jenkinsci.plugins.workflow.job.WorkflowRun;
import org.jenkinsci.plugins.workflow.libs.LibraryStep;
import org.jenkinsci.plugins.workflow.libs.SCMSourceRetriever;
import org.jenkinsci.plugins.workflow.steps.Step;
import org.jenkinsci.plugins.workflow.steps.StepContext;
import org.jenkinsci.plugins.workflow.steps.StepDescriptor;
import org.jenkinsci.plugins.workflow.steps.StepExecution;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;

import java.util.Collection;
import java.util.Set;

public class GitHubLibraryStep extends Step {

    private String owner;
    private String repository;
    private String ref = "main";

    @DataBoundConstructor
    public GitHubLibraryStep(String repository) {
        String[] parts = repository.split("[/]");
        if (parts.length == 1) {
            this.repository = parts[0];
        } else if (parts.length == 2) {
            this.owner = parts[0];
            this.repository = parts[1];
        } else {
            throw new IllegalArgumentException(
                    String.format("Unknown GitHub repository format: %s", repository));
        }

        String[] repoParts = this.repository.split("[@]");
        if (repoParts.length == 2) {
            this.repository = repoParts[0];
            this.ref = repoParts[1];
        } else if (repoParts.length > 2) {
            throw new IllegalArgumentException(
                    String.format("Unknown GitHub repository format: %s", repository));
        }
    }

    @DataBoundSetter
    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getOwner() {
        return this.owner;
    }

    @DataBoundSetter
    public void setRepository(String repository) {
        this.repository = repository;
    }

    public String getRepository() {
        return repository;
    }

    @DataBoundSetter
    public void setRef(String ref) {
        this.ref = ref;
    }

    public String getRef() {
        return this.ref;
    }

    @Override
    public StepExecution start(StepContext context) throws Exception {
        // Get the owner
        WorkflowRun run = context.get(WorkflowRun.class);
        OrganizationFolder folder = OrganizationFolderUtil.getFolderForRun(run);
        if (folder == null) {
            throw new IllegalStateException("Unable to get OrganizationFolder for run!");
        }
        if (this.owner == null) {
            this.owner = folder.getName();
        }

        GitHubSCMSource source = new GitHubSCMSource(this.owner, this.repository, null, false);
        Collection<GitHubAppCredentials> credentials = OrganizationFolderUtil.getAppCredentials(run);
        if (!credentials.isEmpty()) {
            source.setCredentialsId(credentials.iterator().next().getId());
        }

        LibraryStep libraryStep = new LibraryStep(String.format("%s/%s@%s", this.owner, this.repository, this.ref));
        libraryStep.setChangelog(false);
        libraryStep.setRetriever(new SCMSourceRetriever(source));
        return libraryStep.start(context);
    }

    @Extension
    public static class DescriptorImpl extends StepDescriptor {

        @Override
        public Set<? extends Class<?>> getRequiredContext() {
            return ImmutableSet.of(WorkflowRun.class);
        }

        @Override
        public String getDisplayName() {
            return "Load a Jenkins shared library from GitHub";
        }

        @Override
        public String getFunctionName() {
            return "gitHubLibrary";
        }
    }
}
