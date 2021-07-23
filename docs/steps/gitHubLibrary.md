## `gitHubLibrary`: Loads a [Jenkins Shared Library] from GitHub into the build

This step is a wrapper step around the `library` dsl.  It automatically connects to the build's
organization (or other organization if specified).  Private shared library repositories are supported.

### Default Argument

It's possible to supply a single `String` argument to the DSL. The format of the text must be one of
the following:

 * `"<repository>"` - Simplest case, connect to the repository named `"<repository>"` in the build's organization using the `main` branch.
 * `"<repository>@<ref>"` - Connect to the repository named `"<repository>"` in the build's organization using the branch, tag, or commit specified by `<ref>`.
 * `"<owner>/<repository>"` - Connects to the repository named `"<repository>"` in the owned by the `<owner>` organization using the `main` branch. 
 * `"<owner>/<repository>@<ref>"` - Connects to the repository named `"<repository>"` in the owned by the `<owner>` organization using the branch, tag, or commit specified by `<ref>`.

> NOTE: Cross organization private repository access is not supported.

### Named Arguments

In addition to supplying the configuration as a string to the function, it is possible to provide the configuration as
key/value pairs.

| Name         | Type   | Description                                                   | Default    |
| ------------ | ------ | ------------------------------------------------------------- | ---------- |
| `repository` | String | The name of the repository for the shared library             | *Required* |
| `owner`      | String | The name of the owning user/org of the shared library         | build org  |
| `ref`        | String | The name of the branch, tag, or commit for the shared library | `main`     |

[Jenkins Shared Library]: https://www.jenkins.io/doc/book/pipeline/shared-libraries/

### Examples

#### Basic Usage
    gitHubLibrary("my-shared-lib")

    pipeline {
      agent any
      stages {
        ...
        stage("Build") {
          steps {
            myCustomBuildSteps() // Runs vars/myCustomBuildSteps.groovy from the my-shared-lib repository
          }
        }
      }
    }

#### Cross Org, Explicit Config
    gitHubLibrary(
      owner: "other-org",
      repository: "other-org-shared-lib",
      ref: "latest" // 'latest' tag/branch
    )
    pipeline {
      agent any
      stages {
        ...
      }
    }
