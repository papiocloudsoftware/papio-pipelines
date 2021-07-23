## `withGitHubToken`: Injects an API token to be used for interacting with GitHub

This step is a wrapper step that will inject the GITHUB_TOKEN environment variable into the
build available for use in any code inside the nested block configuration.

### Named Arguments

Arguments supplied to `withGitHubToken` control the permissions the token will have on the organization/repository.
The default permissions, if none supplied, are `contents: "write"`.  This grants the token access to read/write
to the repository which includes commits, tags, branches, etc.  Valid values for permissions are `read`, `write`, or `none`.

| Name                   | Type   | Description                                       | Default |
| ---------------------- | ------ | ------------------------------------------------- | ------- |
| `all`                  | String | Grants access to the APIs below                   | `none`  |
| `checks`               | String | Grants access to the [checks] APIs                | `none`  |
| `contents`             | String | Grants access to the [repository contents] APIs   | `write` |
| `deployments`          | String | Grants access to the [deployment] APIs            | `none`  |
| `environments`         | String | Grants access to the [environment] APIs           | `none`  |
| `issues`               | String | Grants access to the [issues] APIs                | `none`  |
| `organizationPackages` | String | Grants access to the [organization packages] APIs | `none`  |
| `packages`             | String | Grants access to the [user packages] APIs         | `none`  |
| `pullRequests`         | String | Grants access to the [pull requests] APIs         | `none`  |
| `statuses`             | String | Grants access to the [statuses] APIs              | `none`  |

[checks]: https://docs.github.com/en/rest/reference/checks
[repository contents]: https://docs.github.com/en/rest/reference/repos
[deployments]: https://docs.github.com/en/rest/reference/repos#deployments
[environments]: https://docs.github.com/en/rest/reference/repos#environments
[issues]: https://docs.github.com/en/rest/reference/issues
[organization packages]: https://docs.github.com/en/rest/reference/packages
[user packages]: https://docs.github.com/en/rest/reference/packages
[pull requests]: https://docs.github.com/en/rest/reference/pulls
[statuses]: https://docs.github.com/en/rest/reference/repos#statuses

### Examples

#### Basic Usage
    // Basic usage, grants read/write of repository contents
    pipeline {
      agent any
      stages {
        ...
        stage("Release") {
          steps {
            withGitHubToken {
              sh 'yarn release' // uses GITHUB_TOKEN to push changelog back to repository
            }
          }
        }
      }
    }

#### Comment on Issue
    // Upon failure, post a comment to the issue
    pipeline {
      agent any
      stages {
        ...
      }
      post {
        failure {
          withGitHubToken(issues: "write") {
            ... // Parse issues from commit message
            sh 'curl -X POST -H "Authorization: token $GITHUB_TOKEN" -d "{\"body\": \"Failure: $BUILD_URL\"}" https://api.github.com/repos/<owner>/<repo>/issues/<number>/comments
          }
        }
      }
    }
