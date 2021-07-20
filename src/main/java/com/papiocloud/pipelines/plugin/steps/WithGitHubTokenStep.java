package com.papiocloud.pipelines.plugin.steps;

import com.google.common.collect.ImmutableSet;
import com.papiocloud.pipelines.plugin.credentials.GitHubTokenBinding;
import hudson.Extension;
import hudson.model.TaskListener;
import net.sf.json.JSONObject;
import org.jenkinsci.plugins.credentialsbinding.impl.BindingStep;
import org.jenkinsci.plugins.workflow.job.WorkflowRun;
import org.jenkinsci.plugins.workflow.steps.Step;
import org.jenkinsci.plugins.workflow.steps.StepContext;
import org.jenkinsci.plugins.workflow.steps.StepDescriptor;
import org.jenkinsci.plugins.workflow.steps.StepExecution;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;
import org.kohsuke.stapler.StaplerRequest;

import java.util.*;

public class WithGitHubTokenStep extends Step {

    private final Map<String, String> permissions;

    @DataBoundConstructor
    public WithGitHubTokenStep() {
        this.permissions = new HashMap<>();
        this.permissions.put("contents", "write");
    }

    @DataBoundSetter
    public void setChecks(String level) {
        this.permissions.put("checks", level);
    }

    @DataBoundSetter
    public void setContents(String level) {
        this.permissions.put("contents", level);
    }

    @DataBoundSetter
    public void setDeployments(String level) {
        this.permissions.put("deployments", level);
    }

    @DataBoundSetter
    public void setEnvironments(String level) {
        this.permissions.put("environments", level);
    }

    @DataBoundSetter
    public void setIssues(String level) {
        this.permissions.put("issues", level);
    }

    @DataBoundSetter
    public void setOrganizationPackages(String level) {
        this.permissions.put("organization_packages", level);
    }

    @DataBoundSetter
    public void setPackages(String level) {
        this.permissions.put("packages", level);
    }

    @DataBoundSetter
    public void setPullRequests(String level) {
        this.permissions.put("pull_requests", level);
    }

    @DataBoundSetter
    public void setStatuses(String level) {
        this.permissions.put("statuses", level);
    }

    @Override
    public StepExecution start(StepContext context) throws Exception {
        BindingStep bindingStep = new BindingStep(Arrays.asList(new GitHubTokenBinding(this.permissions)));
        return bindingStep.start(context);
    }

    @Extension
    public static class DescriptorImpl extends StepDescriptor {

        @Override
        public String getFunctionName() {
            return "withGitHubToken";
        }

        @Override
        public String getDisplayName() {
            return "Retrieve a token for GitHub API access";
        }

        @Override
        public boolean takesImplicitBlockArgument() {
            return true;
        }

        @Override
        public Step newInstance(StaplerRequest req, JSONObject formData) throws FormException {
            Map<String, String> permissions = new HashMap<>();
            String permissionsArg = formData.getString("permissions");
            if (permissionsArg != null && !permissionsArg.trim().isEmpty()) {
                for (String permission : permissionsArg.split("[,]")) {
                    String[] permParts = permission.split("[:]");
                    permissions.put(permParts[0], permParts[1]);
                }
            }
            WithGitHubTokenStep withGitHubToken = new WithGitHubTokenStep();
            withGitHubToken.permissions.putAll(permissions);
            return withGitHubToken;
        }

        @Override
        public Set<? extends Class<?>> getRequiredContext() {
            return ImmutableSet.of(WorkflowRun.class, TaskListener.class);
        }

        @Override
        public String argumentsToString(Map<String, Object> namedArgs) {
            Object permissions = namedArgs.get("permissions");
            if (permissions instanceof List) {
                return String.join(",", (List) permissions);
            } else {
                return null;
            }
        }
    }
}
