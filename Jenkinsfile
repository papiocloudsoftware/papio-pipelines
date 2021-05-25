pipeline {
  agent any

  stages {
    stage('Build') {
      steps {
        sh 'docker build -t papiocloudsoftware/github-jenkins .'
      }
    }


    stage('Release?') {
      // Make sure no agent configured while gathering input
      agent none
      options {
        timeout(time: 5, unit: 'MINUTES')
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
            sh 'echo ${DOCKERHUB_PSW} | docker login -u ${DOCKERHUB_USR} --password-stdin'
            // https://www.jenkins.io/doc/book/pipeline/syntax/#script
            script {
              for (tag in ["latest", env.RELEASE_VERSION]) {
                sh(script: "docker tag papiocloudsoftware/github-jenkins papiocloudsoftware/github-jenkins:${tag}")
                sh(script: "docker push papiocloudsoftware/github-jenkins:${tag}")
              }
            }
          }
        }
      }
    }
  }
}