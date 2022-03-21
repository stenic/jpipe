package io.stenic.jpipe.plugin

import io.stenic.jpipe.event.Event
import org.jenkinsci.plugins.scriptsecurity.sandbox.RejectedAccessException

class CDInfraAsCodePlugin extends Plugin {

    private String yqDockerImage;
    private String credentialId;
    private String repository;
    private String branch;
    private String filePath;
    private String yamlPath;
    private String cdBranch;
    private String gitUser;
    private String gitEmail;

    CDInfraAsCodePlugin(Map opts = [:]) {
        this.repository = opts.get('repository', '');
        this.credentialId = opts.get('credentialId', '');
        this.cdBranch = opts.get('cdBranch', 'main');
        this.branch = opts.get('branch', 'master');
        this.yamlPath = opts.get('yamlPath', '.image.tag');
        this.filePath = opts.get('filePath', 'values.yaml');
        this.gitUser = opts.get('gitUser', 'jpipe-ci');
        this.gitEmail = opts.get('gitEmail', 'jpipe@stenic.io');
        this.yqDockerImage = opts.get('dockerImage', 'mikefarah/yq:4');
    }

    public Map getSubscribedEvents() {
        return [
            "${Event.DEPLOY}": [
                [{ event -> this.doYamlUpdate(event) }, 10],
            ],
        ]
    }

    public Boolean doYamlUpdate(Event event) {
        if (this.cdBranch == "" || event.env.BRANCH_NAME != this.cdBranch) {
            event.script.println("Skipping CDInfraAsCodePlugin")
            return true;
        }

        if (this.credentialId == '') {
            this.credentialId = event.script.scm.getUserRemoteConfigs()[0].getCredentialsId();
        }

        event.script.dir( "${System.currentTimeMillis()}" ) {
            event.script.git(
                url: this.repository,
                branch: this.branch,
                credentialsId: this.credentialId,
                changelog: false
            );

            event.script.docker.image(this.yqDockerImage).inside("--entrypoint=''") {
                event.script.sh "yq eval --inplace '${this.yamlPath} = \"${event.version}\"' ${this.filePath}";
            }

            event.script.sshagent(credentials: [this.credentialId]) {
                event.script.sh "git config user.email '${this.gitEmail}'"
                event.script.sh "git config user.name '${this.gitUser}'"

                event.script.sh "git add ${this.filePath}"
                event.script.sh "git commit --allow-empty -m 'Update version ${this.filePath} to ${event.version}'"
                event.script.sh "git push origin ${this.branch}"
            }
        }

        return true
    }
}
