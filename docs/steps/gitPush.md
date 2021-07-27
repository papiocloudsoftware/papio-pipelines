## `gitPush`: Push commits/tags to the git remote

This step can be used after automated processes create commits during a pipeline run. 
Examples include generation of CHANGELOG, or tagging a deployment/release. 

### Named Arguments

| Name         | Type    | Description                                        | Default  |
| ------------ | ------- | -------------------------------------------------- | -------- |
| `remote`     | String  | Name of the git remote to push to                  | `origin` |
| `followTags` | Boolean | Flag to indicate if tags should be pushed or not   | `false`  |
| `force`      | Boolean | Flag to specify that the push should use `--force` | `false`  |

> WARNING: using the `force` flag can have unintended side effects, use with caution!

### Examples

    pipeline {
      agent any
      stages {
        ...
        stage("Generate Commit") {
          ...
          sh 'git commit -m "Example"'
          sh 'git tag v${BUILD_ID}'
        }
     
        stage("Push To Remote") {
          gitPush(followTags: true)
        }
      }
    }
