package com.papiocloud.pipelines.plugin.credentials;

import spock.lang.Specification;

class GitHubTokenBindingSpec extends Specification {

    def "will throw an exception if permission specified the app doesn't have"() {
        when:
        new GitHubTokenBinding([
                contents: "write",
                members : "read"
        ])

        then:
        def e = thrown(IllegalArgumentException)
        e.message == "Access level of READ for 'members' is not supported by this GitHub app"
    }

    def "will throw an exception if permission specified is more than the app has"() {
        when:
        new GitHubTokenBinding([
                contents    : "write",
                environments: "write"
        ])

        then:
        def e = thrown(IllegalArgumentException)
        e.message == "Access level of WRITE for 'environments' is not supported by this GitHub app"
    }
}
