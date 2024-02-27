package io.stenic.jpipe.plugin

import io.stenic.jpipe.event.Event

class ConventionalCommitPlugin extends Plugin {

    protected String dockerImage = 'ghcr.io/stenic/jpipe-release:1.3'
    private Boolean useSemanticRelease = false

    private String releaseBranches
    private String prereleaseBranches
    private String extraArgs
    private Boolean useLegacyStrategy
    private List extraEnv

    ConventionalCommitPlugin(Map config = [:]) {
        this.releaseBranches = config.get('releaseBranches', 'master,main')
        this.prereleaseBranches = config.get('prereleaseBranches', 'develop')
        this.useLegacyStrategy = config.get('useLegacyStrategy', false)
        this.extraArgs = config.get('extraArgs', '')
        this.extraEnv = config.get('extraEnv', [])
    }

    public Map getSubscribedEvents() {
        return [
            "${Event.PREPARE}": [
                [{ event -> this.doResolveVersion(event) }, 20],
            ],
            "${Event.PUBLISH}": [
                [{ event -> this.doPublish(event) }, 0],
            ],
        ]
    }

    public void doResolveVersion(Event event) {
        event.version = this.findVersion(event.script, event.env)
    }

    private String findVersion(script, env) {
        script.docker.image(this.dockerImage).inside {
            this.runRelease(script, "--dry-run ${this.extraArgs}")

            // Find version
            if (script.fileExists(file: './VERSION')) {
                def version = script.readFile('./VERSION').trim()
                script.sh 'rm -rf ./VERSION'

                version = "v${version}"
                if (this.tagExists(script, version)) {
                    return this.deduplicate(script, version)
                }

                this.useSemanticRelease = true

                return version
            }

            String cleanBranch = env.BRANCH_NAME.replaceAll('[^0-9a-zA-Z-]', '-').toLowerCase()
            String lastTag = this.getLastTag(script).replaceFirst(/-[0-9]+$/, '')
            if (lastTag == '' || this.useLegacyStrategy) {
                return this.deduplicate(script, cleanBranch)
            }

            if (!this.releaseBranches.split(',').contains(env.BRANCH_NAME)) {
                lastTag += "-${cleanBranch}"
            }

            return this.deduplicate(script, lastTag)
        }
    }

    private String deduplicate(script, String version) {
        def i = 1
        def validate = "${version}-${i}"

        while (this.tagExists(script, validate)) {
            i++
            validate = "${version}-${i}"
        }

        return validate
    }

    private runRelease(script, cmdArgs) {
        Boolean configCreated = false
        String configFile = 'release.config.cjs'
        if (!script.fileExists("./${configFile}")) {
            def releasercCfg = script.libraryResource "io/stenic/jpipe/release/${configFile}"
            script.writeFile file: configFile, text: releasercCfg
            configCreated = true
        }

        List env = this.extraEnv + [
            "RELEASE_BRANCHES=${this.releaseBranches}",
            "PRERELEASE_BRANCHES=${this.prereleaseBranches}",
            "GIT_URL=${script.scm.getUserRemoteConfigs()[0].getUrl()}",
        ]

        script.withEnv(env) {
            script.sshagent(credentials: [script.scm.getUserRemoteConfigs()[0].getCredentialsId()]) {
                script.sh "semantic-release ${cmdArgs}"
            }
        }

        if (configCreated) {
            script.sh "rm -rf ./${configFile}"
        }
    }

    private Boolean tagExists(script, tag) {
        return script.sh(script: "git rev-list ${tag} >/dev/null", returnStatus: true) == 0
    }

    private String getLastTag(script) {
        def lastTag = script.sh(script: 'git describe --tags --abbrev=0', returnStdout: true).trim()
        def p = ~'^(v[0-9]+.[0-9]+.[0-9]+)(-.*)?'
        def matcher = p.matcher(lastTag)

        if (matcher.matches()) {
            return matcher[0][1]
        }

        return ''
    }

    public void doPublish(Event event) {
        this.publishVersion(event.version, event.script, event.env)
    }

    Boolean publishVersion(version, script, env) {
        if (this.useSemanticRelease) {
            script.docker.image(this.dockerImage).inside {
                this.runRelease(script, '')
                if (script.fileExists(file: './VERSION')) {
                    script.sh 'rm -rf ./VERSION'
                }
            }

            return true
        }

        script.sh "git tag ${version}"
        def gitUrl = script.scm.getUserRemoteConfigs()[0].getUrl()
        def credentialsId = script.scm.getUserRemoteConfigs()[0].getCredentialsId()
        if (gitUrl.startsWith('https')) {
            script.withCredentials([script.usernamePassword(
                credentialsId: credentialsId,
                usernameVariable: 'GIT_USERNAME',
                passwordVariable: 'GIT_PASSWORD'
            )]) {
                def gitUrlWithCreds = gitUrl.replaceFirst(/https:\/\//, "https://${env.GIT_USERNAME}:${env.GIT_PASSWORD}@")
                script.sh "git push ${gitUrlWithCreds} ${version}"
            }
        } else {
            script.sshagent(credentials: [credentialsId]) {
                script.sh "git push origin ${version}"
            }
        }

        return true
    }

}
