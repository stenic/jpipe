package io.stenic.jpipe.plugin

import io.stenic.jpipe.event.Event
import java.lang.Exception

class SonarQubePlugin extends Plugin {

    private String credentialsId;
    private String projectKey;
    private Boolean allowFailure;
    private String dockerImage;
    private String command;
    private ArrayList<String> extraArguments;
    private ArrayList<String> dockerArguments;

    SonarQubePlugin(Map opts = [:]) {
        this.credentialsId = opts.get('credentialId', 'sonarqube-token');
        this.projectKey = opts.get('projectKey');
        this.allowFailure = opts.get('allowFailure', true);
        this.dockerImage = opts.get('dockerImage', 'sonarsource/sonar-scanner-cli:latest');
        this.command = opts.get('command', 'sonar-scanner');
        this.extraArguments = opts.get('extraArguments', []);
        this.dockerArguments = opts.get('dockerArguments', ['--privileged']);
    }

    public Map getSubscribedEvents() {
        return [
            "${Event.TEST}": [
                [{ event -> this.doSonarScan(event) }, -10],
            ],
        ]
    }

    public void doSonarScan(Event event) {
        try {
            event.script.docker.image(this.dockerImage).inside(this.dockerArguments.join(' ')) {
                event.script.withSonarQubeEnv(credentialsId: this.credentialsId) {
                    event.script.sh "${this.command} -Dsonar.projectKey=${this.projectKey} -Dsonar.projectVersion=${event.version} ${this.extraArguments.join(' ')}"
                }
                try {
                    event.script.sh "rm -rf .scannerwork"
                } catch (Exception e) {}
            }
        } catch (Exception e) {
            if (this.allowFailure) {
                throw e;
            }
        }
    }
}
