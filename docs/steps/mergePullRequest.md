## `mergePullRequest`: Attempts to merge if the build was triggered from a pull request

This step can be used on success of the build to automatically merge a pull request. It will
still adhere to the status checks and requirements configured for the repo. So the PR can only be 
merged if it was mergeable in the first place.

### Named Arguments

*This step takes no arguments*

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
        success {
          mergePullRequest()
        }
      }
    }

> NOTE: that this step uses code from the [pipeline-github](https://plugins.jenkins.io/pipeline-github/) plugin and 
> therefore, in a scripted pipeline/shared library, the `pullRequest` object will be available during a PR build for
> full access to all features from the plugin. This step is purely a declarative wrapper around the plugin functionality.
