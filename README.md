# Papio Cloud GitHub Jenkins

This repo generates the base image used for the [Papio Pipelines GitHub App](https://github.com/marketplace/papio-pipelines). 
The image uses the LTS Jenkins Alpine Image and installs the plugins defined in [./resources/plugins.txt]() 
Along with the `papio-pipelines` plugin that is created from the source in this repo.
These plugins are the core of what makes up the Piplines App Instance. 

A custom [entry script](./resources/entrypoint.sh) is used that extracts the plugins and creates timestamp files for them.
This is faster than having the Jenkins code itself do it but runs the risk of breaking in the 
future.

The image is published
to [DockerHub](https://hub.docker.com/r/papiocloudsoftware/papio-pipelines)

## Contributing

To contribute, fork the repository and create an [issue.](https://github.com/papiocloudsoftware/papio-pipelines/issues)
Try to be as specific as possible with the request for functionality along with Jenkinsfile snippets if you can.
All Java code must include [spock](https://spockframework.org/) unit tests. 

## Custom Steps

As time permits, I will try my best to contribute some of these steps back to the plugins
they are relevant for or publish this plugin as an official Jenkins Plugin.  But for now,
they will be documented here.

| Function Name | Description                                                            | Implementation     |
| ------------- | ---------------------------------------------------------------------- | ------------------ |
| [gitPush]     | Pushes any local commits (or optionally tags) to the remote git source | [PushToRemoteStep] |



[PushToRemoteStep]: ./src/main/java/com/papiocloud/pipelines/plugins/steps/PushToRemoteStep.java
[gitPush]: ./docs/steps/gitPush.md
