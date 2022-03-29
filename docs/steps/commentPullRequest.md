## `commentPullRequest`: Makes a comment on the PR if the build was triggered from a pull request

This step can be used at any point in the build to post a comment to a pull request if the build
is part of a pull request.  A common scenario would be to post a failure message to the PR with a link to the
failing build. This would be useful if build success is a requirement for merge.

### Arguments

 1. The text to post as a comment 

### Examples

    pipeline {
      agent any
      stages {
        ...
        stage("Build") {
          ...
        }
      }
      post {
        failure {
          commentPullRequest("[Failing Build](${env.BUILD_URL})")
        }
      }
    }

> NOTE: that this step uses code from the [pipeline-github](https://plugins.jenkins.io/pipeline-github/) plugin and
> therefore, in a scripted pipeline/shared library, the `pullRequest` object will be available during a PR build for
> full access to all features from the plugin. This step is purely a declarative wrapper around the plugin functionality.
