package com.papiocloud.pipelines.plugin.credentials;

import com.cloudbees.plugins.credentials.CredentialsProvider;
import com.cloudbees.plugins.credentials.domains.DomainRequirement;
import com.papiocloud.pipelines.plugin.util.OrganizationFolderUtil;
import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.Run;
import hudson.model.TaskListener;
import jenkins.branch.OrganizationFolder;
import org.jenkinsci.Symbol;
import org.jenkinsci.plugins.credentialsbinding.Binding;
import org.jenkinsci.plugins.credentialsbinding.BindingDescriptor;
import org.jenkinsci.plugins.github_branch_source.GitHubAppCredentials;
import org.jenkinsci.plugins.plaincredentials.StringCredentials;
import org.jenkinsci.plugins.workflow.job.WorkflowJob;
import org.jenkinsci.plugins.workflow.job.WorkflowRun;
import org.kohsuke.github.*;
import org.kohsuke.github.extras.authorization.JWTTokenProvider;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class GitHubTokenBinding extends Binding<StringCredentials> {

    public static final String GITHUB_TOKEN = "GITHUB_TOKEN";

    public static final Map<String, GHPermissionType> APP_PERMISSIONS = Collections.unmodifiableMap(
            new HashMap<String, GHPermissionType>() {{
                put("checks", GHPermissionType.WRITE);
                put("contents", GHPermissionType.WRITE);
                put("deployments", GHPermissionType.WRITE);
                put("environments", GHPermissionType.READ);
                put("issues", GHPermissionType.WRITE);
                put("organization_packages", GHPermissionType.WRITE);
                put("packages", GHPermissionType.WRITE);
                put("pull_requests", GHPermissionType.WRITE);
                put("statuses", GHPermissionType.WRITE);
            }}
    );

    private final Map<String, GHPermissionType> permissions;

    public GitHubTokenBinding(Map<String, String> permissions) {
        super(GITHUB_TOKEN, GITHUB_TOKEN);
        this.permissions = convertToPermissionTypes(permissions);
    }

    @Override
    protected Class<StringCredentials> type() {
        return StringCredentials.class;
    }

    @Override
    public SingleEnvironment bindSingle(@Nonnull Run<?, ?> build, @Nullable FilePath workspace, @Nullable Launcher launcher, @Nonnull TaskListener listener) throws IOException, InterruptedException {
        WorkflowRun run = (WorkflowRun) build;
        OrganizationFolder folder = OrganizationFolderUtil.getFolderForRun(run);
        if (folder == null) {
            throw new IOException("No organization folder configured for run!");
        }
        String loginId = folder.getName();
        WorkflowJob job = run.getParent();
        String repo = job.getParent().getDisplayName();
        // 2. Lookup the GitHubAppCredentials for the job and get the private key and app id
        Collection<GitHubAppCredentials> appCredentials = CredentialsProvider.lookupCredentials(
                GitHubAppCredentials.class, job, run.getExecution().getAuthentication(), new DomainRequirement[0]);

        for (GitHubAppCredentials credentials : appCredentials) {
            // 3. Create GitHub instance and get the installation for the folder
            GitHubBuilder builder = new GitHubBuilder();
            try {
                builder.withAuthorizationProvider(
                        new JWTTokenProvider(credentials.getAppID(), credentials.getPrivateKey().getPlainText())
                );
            } catch (GeneralSecurityException e) {
                throw new IOException(e);
            }
            GitHub gitHub = builder.build();

            // 4. Get the token from the installation for the repo with the permissions
            GHAppInstallation install = gitHub.getApp().getInstallationByRepository(loginId, repo);
            GHAppInstallationToken token = install.createToken(this.permissions).create();
            return new SingleEnvironment(token.getToken());
        }

        throw new IOException("Unable to locate GitHub App credentials for run");
    }

    private static Map<String, GHPermissionType> convertToPermissionTypes(Map<String, String> permissions) {
        Map<String, GHPermissionType> permissionsMap = new HashMap<>();
        if (permissions != null) {
            for (Map.Entry<String, String> permission : permissions.entrySet()) {
                String permName = permission.getKey().toLowerCase();
                GHPermissionType accessLevel = GHPermissionType.valueOf(permission.getValue().toUpperCase());
                GHPermissionType appAccessLevel = APP_PERMISSIONS.getOrDefault(permName, GHPermissionType.NONE);
                if (accessLevel.ordinal() < appAccessLevel.ordinal()) {
                    throw new IllegalArgumentException(
                            String.format("Access level of %s for '%s' is not supported by this GitHub app", accessLevel, permName)
                    );
                }
                permissionsMap.put(permName, accessLevel);
            }
        }
        return permissionsMap;
    }

    @Symbol("gitHubToken")
    @Extension
    public static class DescriptorImpl extends BindingDescriptor<StringCredentials> {

        @Override
        protected Class<StringCredentials> type() {
            return StringCredentials.class;
        }

        @Override
        public String getDisplayName() {
            return GITHUB_TOKEN;
        }

        @Override
        public boolean requiresWorkspace() {
            return false;
        }
    }
}
