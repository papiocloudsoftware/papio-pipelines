package com.papiocloud.pipelines.plugin.util;

import hudson.model.Item;
import hudson.model.ItemGroup;
import hudson.model.Job;
import hudson.model.Run;
import jenkins.branch.OrganizationFolder;

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

}
