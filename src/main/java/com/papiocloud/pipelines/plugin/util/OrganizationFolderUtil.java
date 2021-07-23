package com.papiocloud.pipelines.plugin.util;

import com.cloudbees.plugins.credentials.CredentialsProvider;
import com.cloudbees.plugins.credentials.domains.DomainRequirement;
import hudson.model.Item;
import hudson.model.ItemGroup;
import hudson.model.Job;
import hudson.model.Run;
import jenkins.branch.OrganizationFolder;
import org.jenkinsci.plugins.github_branch_source.GitHubAppCredentials;
import org.jenkinsci.plugins.workflow.job.WorkflowJob;
import org.jenkinsci.plugins.workflow.job.WorkflowRun;

import java.util.Collection;

public class OrganizationFolderUtil {

    public static OrganizationFolder getFolderForRun(Run run) {
        return getFolderForJob(run.getParent());
    }

    public static OrganizationFolder getFolderForJob(Job job) {
        if (job == null) {
            return null;
        }
        ItemGroup parent = job.getParent();
        while (parent instanceof OrganizationFolder == false && parent instanceof Item) {
            Item parentItem = (Item) parent;
            parent = parentItem.getParent();
        }
        if (parent instanceof OrganizationFolder) {
            return (OrganizationFolder) parent;
        }
        return null;
    }

    public static Collection<GitHubAppCredentials> getAppCredentials(WorkflowRun run) {
        return CredentialsProvider.lookupCredentials(
                GitHubAppCredentials.class,
                run.getParent(),
                run.getExecution().getAuthentication(),
                new DomainRequirement[0]);

    }

}
