#!groovy​

library identifier: 'custom-lib@develop', retriever: modernSCM(
  [$class: 'GitSCMSource',
   remote: 'git@github.com:stenic/jpipe.git',
   credentialsId: 'github-jenkins-key']), changelog: false

ansiColor('xterm') {
  basePipeline projectName: 'junit',
      testScript: "mvn clean test",
      prereleaseBranches: 'develop'
}
