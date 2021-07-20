package com.papiocloud.pipelines.plugin

import hudson.model.Result
import jenkins.model.Jenkins
import org.eclipse.jgit.api.Git
import org.jenkinsci.plugins.workflow.cps.CpsFlowDefinition
import org.jenkinsci.plugins.workflow.job.WorkflowJob
import org.jenkinsci.plugins.workflow.job.WorkflowRun
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import org.jvnet.hudson.test.JenkinsRule
import spock.lang.Specification

/**
 * Abstract unit test class. Sets up the Jenkins test rule and provides
 * the test instance of Jenkins along with the url endpoint.
 */
class AbstractJenkinsSpec extends Specification {

    @Rule
    TemporaryFolder tmp = new TemporaryFolder()
    @Rule
    JenkinsRule j = new JenkinsRule()
    Jenkins jenkins
    URL jenkinsUrl
    File mockGitRepo

    def setup() {
        jenkins = j.jenkins
        jenkinsUrl = j.getURL()

        mockGitRepo = tmp.newFolder("mock-repo")
        Git git = Git.init().setDirectory(mockGitRepo).call()
        File readme = new File(mockGitRepo, "README.md")
        readme.text = "Hello, World!"
        git.add().addFilepattern(readme.name).call()
        git.commit().setMessage("Initial commit").call()
    }

    WorkflowRun createAndRunJob(String script) {
        WorkflowJob testJob = jenkins.createProject(WorkflowJob, "test")
        testJob.setDefinition(new CpsFlowDefinition(script))

        WorkflowRun run = testJob.scheduleBuild2(0).get()
        if (run.result == Result.FAILURE) {
            println run.getLog()
            throw run.execution.causeOfFailure
        }
        return run
    }

}
