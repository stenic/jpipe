package io.stenic.jpipe.plugin

import io.stenic.jpipe.event.Event

class SonarQubePlugin extends Plugin {

    private String credentialsId;
    private String projectKey;
    
    SonarQubePlugin(Map opts = [:]) {
        this.credentialsId = opts.get('credentialId', 'sonarqube-token');
        this.projectKey = opts.get('projectKey');
    }

    public Map getSubscribedEvents() {
        return [
            "${Event.TEST}": [
                [{ event -> this.doSonarScan(event) }, -10],
            ],
        ]
    }

    public void doSonarScan(Event event) {
        event.script.docker.image('sonarsource/sonar-scanner-cli:latest').inside('--privileged') {
            event.script.withSonarQubeEnv(credentialsId: this.credentialsId) {
                event.script.sh "sonar-scanner -Dsonar.projectKey=${this.projectKey}"
            }
        }
    }
}
