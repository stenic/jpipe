package io.stenic.jpipe.plugin

import io.stenic.jpipe.event.Event

class ConventionalCommitPlugin extends Plugin {

    protected String dockerImage = 'stenicbv/release:0.1.13';
    private Boolean useSemanticRelease = false;

    private String releaseBranches;
    private String prereleaseBranches;

    ConventionalCommitPlugin(Map config = [:]) {
        this.releaseBranches = config.get('releaseBranches', 'master,main')
        this.prereleaseBranches = config.get('prereleaseBranches', 'develop')
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
            this.runRelease(script, '--dry-run')

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

            return this.deduplicate(script, env.BRANCH_NAME.replaceAll('[^0-9a-zA-Z-]', '-'))
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
        String configFile = 'release.config.js'
        if (!script.fileExists("./${configFile}")) {
            def releasercCfg = script.libraryResource "io/stenic/jpipe/release/${configFile}"
            script.writeFile file: configFile, text: releasercCfg
            configCreated = true
        }

        script.withEnv([
            "RELEASE_BRANCHES=${this.releaseBranches}",
            "PRERELEASE_BRANCHES=${this.prereleaseBranches}",
        ]) {
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

        script.sshagent(credentials: [script.scm.getUserRemoteConfigs()[0].getCredentialsId()]) {
            script.sh "git tag ${version}"
            script.sh "git push origin ${version}"
        }

        return true
    }

}
