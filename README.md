# Papio Cloud GitHub Jenkins

This repo generates the base image used for
the [Papio Pipelines GitHub App](https://github.com/marketplace/papio-pipelines). The image uses the LTS Jenkins Alpine
Image and installs the plugins defined in [./resources/plugins.txt]()
Along with the `papio-pipelines` plugin that is created from the source in this repo. These plugins are the core of what
makes up the Piplines App Instance.

A custom [entry script](./resources/entrypoint.sh) is used that extracts the plugins and creates timestamp files for
them. This is faster than having the Jenkins code itself do it but runs the risk of breaking in the future.

The image is published to [DockerHub](https://hub.docker.com/r/papiocloudsoftware/papio-pipelines)

## Permissions and Access

When installing *Pipelines* it will request access for the following

| Access                      | Level        | Purpose                                                                               |
| --------------------------- | ------------ | ------------------------------------------------------------------------------------- |
| **code**                    | *read/write* | Allows *Pipelines* to do builds from source as well as push back tags/release commits |
| **checks**                  | *read/write* | [#21] allows for Jenkins to publish status checks that can be used for branch rules   |
| **commit status**           | *read/write* | Each commit that triggers a build will get a success/failure status tied to it        |
| **deployments**             | *read/write* | [#22]/[#23] allow for running from deployment events/triggering events                |
| **environments**            | *read*       | [#27] allow for interfacing with GitHub Environments same as GitHub Workflows         |
| **issues**                  | *read/write* | [#24] allows for posting comments to issues associated with the build                 |
| **metadata**                | *read*       | **Required** - Grants the app access to read plan metadata for installation           |
| **(organization) packages** | *read/write* | [#25] allows for publishing/resolving GitHub packages (public/private)                |
| **pull requsets**           | *read/write* | Allows for interfacing with Pull Requests (comments/merging/declining)                |

[#21]: https://github.com/papiocloudsoftware/papio-pipelines/issues/21
[#22]: https://github.com/papiocloudsoftware/papio-pipelines/issues/22
[#23]: https://github.com/papiocloudsoftware/papio-pipelines/issues/23
[#24]: https://github.com/papiocloudsoftware/papio-pipelines/issues/24
[#25]: https://github.com/papiocloudsoftware/papio-pipelines/issues/25
[#26]: https://github.com/papiocloudsoftware/papio-pipelines/issues/26
[#27]: https://github.com/papiocloudsoftware/papio-pipelines/issues/27

### Custom Steps

As time permits, I will try my best to contribute some of these steps back to the plugins they are relevant for or
publish this plugin as an official Jenkins Plugin. But for now, they will be documented here.

| Function Name        | Description                                                              | Implementation           |
| -------------------- | ------------------------------------------------------------------------ | ------------------------ |
| [gitPush]            | Pushes any local commits (or optionally tags) to the remote git source   | [PushToRemoteStep]       |
| [withGitHubToken]    | Injects an API token to be used for interacting with GitHub              | [WithGitHubTokenStep]    |
| [gitHubLibrary]      | Loads a [Jenkins Shared Library] from GitHub into the build              | [GitHubLibraryStep]      |
| [mergePullRequest]   | Attempts to merge if the build was triggered from a pull request         | [MergePullRequestStep]   |
| [commentPullRequest] | Makes a comment on the PR if the build was triggered from a pull request | [CommentPullRequestStep] |

[PushToRemoteStep]: ./src/main/java/com/papiocloud/pipelines/plugin/steps/PushToRemoteStep.java
[gitPush]: ./docs/steps/gitPush.md
[WithGitHubTokenStep]: ./src/main/java/com/papiocloud/pipelines/plugin/steps/WithGitHubTokenStep.java
[withGitHubToken]: ./docs/steps/withGitHubToken.md
[gitHubLibrary]: ./docs/steps/gitHubLibrary.md
[Jenkins Shared Library]: https://www.jenkins.io/doc/book/pipeline/shared-libraries/
[GitHubLibraryStep]: ./src/main/java/com/papiocloud/pipelines/plugin/steps/GitHubLibraryStep.java
[mergePullRequest]: ./docs/steps/mergePullRequest.md
[MergePullRequestStep]: ./src/main/java/com/papiocloud/pipelines/plugin/steps/pr/MergePullRequestStep.java
[commentPullRequest]: ./docs/steps/commentPullRequest.md
[CommentPullRequestStep]: ./src/main/java/com/papiocloud/pipelines/plugin/steps/CommentPullRequestStep.java

## Contributing

To contribute, fork the repository and create an [issue.](https://github.com/papiocloudsoftware/papio-pipelines/issues)
Try to be as specific as possible with the request for functionality along with Jenkinsfile snippets if you can. All
Java code must include [spock](https://spockframework.org/) unit tests. 
