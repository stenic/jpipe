#!groovy​

library identifier: 'custom-lib@main', retriever: modernSCM(
  [$class: 'GitSCMSource',
   remote: 'git@github.com:stenic/jpipe.git',
   credentialsId: 'github-jenkins-key']), changelog: false

ansiColor('xterm') {
  basePipeline projectName: 'pipeline-test',
      testScript: "mvn clean test",
      prereleaseBranches: 'event',
      dockerCredentialId: "registry-harbor-homelab",
      dockerRepository: 'harbor.dev.stenic.io/test/pipeline-test',
      dockerServer: 'http://harbor.dev.stenic.io'
}