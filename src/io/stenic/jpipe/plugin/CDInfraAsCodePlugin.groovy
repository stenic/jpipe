package io.stenic.jpipe.plugin

import io.stenic.jpipe.event.Event
import org.jenkinsci.plugins.scriptsecurity.sandbox.RejectedAccessException

class CDInfraAsCodePlugin extends Plugin {

    private String credentialId;
    private String repository;
    private String branch;
    private String filePath;
    private String yamlPath;
    private String cdBranch;

    CDInfraAsCodePlugin(Map opts = [:]) {
        this.repository = opts.get('repository', '');
        this.credentialId = opts.get('credentialId', '');
        this.cdBranch = opts.get('cdBranch', 'main');
        this.branch = opts.get('branch', 'master');
        this.yamlPath = opts.get('yamlPath', 'image.tag');
        this.filePath = opts.get('filePath', 'values.yaml');
    }

    public Map getSubscribedEvents() {
        return [
            "${Event.DEPLOY}": [
                [{ event -> this.doYamlUpdate(event) }, 10],
            ],
        ]
    }

    public Boolean doYamlUpdate(Event event) {
        if (event.env.BRANCH_NAME != this.cdBranch) {
            event.script.println("Skipping CDInfraAsCodePlugin")
            return true;
        }

        if (this.credentialId == '') {
            this.credentialId = script.scm.getUserRemoteConfigs()[0].getCredentialsId();
        }

        event.script.dir( "${System.currentTimeMillis()}" ) {
            event.script.git(
                url: this.repository,
                branch: this.branch,
                credentialsId: this.credentialId,
                changelog: false
            );

            event.script.docker.image("mikefarah/yq:4").inside {
                event.script.sh "ls -lah; pwd";
            }

            // script.sshagent(credentials: [this.credentialId]) {
            //     script.sh "git add ${this.filePath}"
            //     script.sh "git commit -m 'Update '"
            //     script.sh "git push origin ${this.branch}"
            // }
        }

        return true
    }
}
