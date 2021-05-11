# Papio Cloud GitHub Jenkins

This is the base image used for the [Papio Pipelines GitHub App](https://github.com/marketplace/papio-pipelines). 
The image uses the LTS Jenkins Alpine Image and installs the plugins defined in [./resources/plugins.txt]() 
These plugins are the core of what makes up the Piplines App Instance. A custom [entry script](./resources/entrypoint.sh) is used that extracts the plugins and creates timestamp files for them.
This is faster than having the Jenkins code itself do it but runs the risk of breaking in the 
future.

The image is published
to [DockerHub](https://hub.docker.com/r/papiocloudsoftware/github-jenkins)
