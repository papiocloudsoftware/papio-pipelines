package com.papiocloud.pipelines.plugin.steps

import spock.lang.Specification
import spock.lang.Unroll

class GitHubLibraryStepSpec extends Specification {

    @Unroll
    def "throws exception if repository is in wrong format"() {
        when:
        new GitHubLibraryStep(repository)

        then:
        def e = thrown(IllegalArgumentException)
        e.message == "Unknown GitHub repository format: $repository"

        where:
        _ | repository
        _ | "a/b/c/d"
        _ | "a/b/c"
        _ | "a/b@d@e"
    }

    def "can configure the owner, repository, and ref from the repository using <owner>/<repo>@<ref> format"() {
        given:
        GitHubLibraryStep step = new GitHubLibraryStep("owner/repo@feature/the-ref")

        expect:
        step.owner == "owner"
        step.repository == "repo"
        step.ref == "feature/the-ref"
    }

    def "ref defaults to 'main' if none specified"() {
        given:
        GitHubLibraryStep step = new GitHubLibraryStep("owner/repo")

        expect:
        step.owner == "owner"
        step.repository == "repo"
        step.ref == "main"
    }

}
