pipeline {
  agent none

  stages {
    stage('Build & Test Plugin') {
      agent any
      steps {
        sh './gradlew build --no-daemon'
      }
    }

    stage('Build Image') {
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
        branch 'main'
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
            // Create and push tag (ensures we are not overriding an existing release)
            sh 'git tag -a "${RELEASE_VERSION}" -m "${RELEASE_VERSION}"'
            gitPush(followTags: true)
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
