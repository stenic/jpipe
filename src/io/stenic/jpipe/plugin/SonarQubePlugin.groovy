package io.stenic.jpipe.plugin

import io.stenic.jpipe.event.Event
import java.lang.Exception

class SonarQubePlugin extends Plugin {

    private String credentialsId;
    private String projectKey;
    private Boolean allowFailure;

    SonarQubePlugin(Map opts = [:]) {
        this.credentialsId = opts.get('credentialId', 'sonarqube-token');
        this.projectKey = opts.get('projectKey');
        this.allowFailure = opts.get('allowFailure', true);
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
            event.script.docker.image('sonarsource/sonar-scanner-cli:latest').inside('--privileged') {
                event.script.withSonarQubeEnv(credentialsId: this.credentialsId) {
                    event.script.sh "sonar-scanner -Dsonar.projectKey=${this.projectKey} -Dsonar.projectVersion=${event.version}"
                }
            }
        } catch (Exception e) {
            if (!this.allowFailure) {
                throw e;
            }
        }
    }
}
