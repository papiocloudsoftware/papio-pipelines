pipeline {
  agent none

  stages {
    stage('Build') {
      agent any
      steps {
        sh 'docker build -t papiocloudsoftware/papio-pipelines .'
      }
    }


    stage('Release?') {
      // Make sure no agent configured while gathering input
      options {
        timeout(time: 5, unit: 'MINUTES')
      }
      // https://www.jenkins.io/doc/book/pipeline/syntax/#when
      when {
        beforeInput true
        branch 'agent-fix'
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
            sh 'echo ${DOCKERHUB_PSW} | docker login -u ${DOCKERHUB_USR} --password-stdin'
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
