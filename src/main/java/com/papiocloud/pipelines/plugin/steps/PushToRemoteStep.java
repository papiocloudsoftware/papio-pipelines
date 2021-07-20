package com.papiocloud.pipelines.plugin.steps;

import com.google.common.collect.ImmutableSet;
import hudson.EnvVars;
import hudson.Extension;
import hudson.FilePath;
import hudson.model.TaskListener;
import hudson.plugins.git.GitSCM;
import hudson.scm.SCM;
import org.eclipse.jgit.transport.RemoteConfig;
import org.eclipse.jgit.transport.URIish;
import org.jenkinsci.plugins.gitclient.GitClient;
import org.jenkinsci.plugins.gitclient.PushCommand;
import org.jenkinsci.plugins.workflow.job.WorkflowRun;
import org.jenkinsci.plugins.workflow.steps.*;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Enables pushing to the GitSCM remote configured on the WorkflowRun.
 * <code>
 *     pipeline {
 *       ...
 *       stage("Push To Remote") {
 *         steps {
 *           gitPush(followTags: true)
 *         }
 *       }
 *     }
 * </code>
 */
public class PushToRemoteStep extends Step {

    private String remote = "origin";
    private Boolean followTags = false;

    @DataBoundConstructor
    public PushToRemoteStep() {
    }

    public String getRemote() {
        return remote;
    }

    @DataBoundSetter
    public void setRemote(String remote) {
        this.remote = remote;
    }

    public Boolean getFollowTags() {
        return followTags;
    }

    @DataBoundSetter
    public void setFollowTags(Boolean followTags) {
        this.followTags = followTags;
    }

    @Override
    public StepExecution start(final StepContext context) throws Exception {
        return new SynchronousStepExecution<Void>(context) {
            @Override
            protected Void run() throws Exception {
                PushToRemoteStep.this.run(context);
                return null;
            }
        };
    }

    protected void run(StepContext context) throws Exception {
        WorkflowRun run = context.get(WorkflowRun.class);
        EnvVars envVars = context.get(EnvVars.class);
        TaskListener listener = context.get(TaskListener.class);
        boolean found = false;
        for (SCM scm : run.getSCMs()) {
            if (scm instanceof GitSCM) {
                found = true;
                GitSCM gitSCM = (GitSCM) scm;
                RemoteConfig remote = gitSCM.getRepositoryByName(this.remote);
                if (remote == null) {
                    throw new IOException(String.format("Remote with name '%s' not found", this.remote));
                }

                String remoteBranch = envVars.get(GitSCM.GIT_BRANCH, null);
                if (remoteBranch == null) {
                    throw new IOException("Could not determine branch to push to");
                }

                GitClient client = gitSCM.createClient(
                        listener,
                        envVars,
                        run,
                        context.get(FilePath.class)
                );
                List<URIish> uris = new ArrayList<>(remote.getPushURIs());
                if (uris.isEmpty()) {
                    uris.addAll(remote.getURIs());
                    if (uris.size() > 1) {
                        throw new IOException("No push URIs configured but remote has multiple pull URIs");
                    }
                }
                if (uris.isEmpty()) {
                    throw new IOException(String.format("No remote URIs configured for '%s'", remote.getName()));
                }
                for (URIish uri : uris) {
                    PushCommand command = client.push();
                    command.to(uri);
                    command.ref(remoteBranch);
                    if (followTags) {
                        command.tags(true);
                    }
                    command.execute();
                }
                break;
            }
        }
        if (!found) {
            throw new IOException("GitSCM not found on run");
        }
    }

    @Extension
    public static class DescriptorImpl extends StepDescriptor {
        @Override
        public Set<? extends Class<?>> getRequiredContext() {
            return ImmutableSet.of(WorkflowRun.class, TaskListener.class, EnvVars.class, FilePath.class);
        }

        @Override
        public String getFunctionName() {
            return "gitPush";
        }

        @Override
        public String argumentsToString(Map<String, Object> namedArgs) {
            Object script = namedArgs.get("script");
            return script instanceof String ? (String) script : null;
        }
    }
}
