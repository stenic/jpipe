package io.stenic.jpipe.plugin

import io.stenic.jpipe.event.Event

class EcrPlugin extends Plugin {

    protected String dockerImage = 'amazon/aws-cli'
    private String credentialsId
    private String repository
    private String region

    EcrPlugin(Map opts = [:]) {
        this.repository = opts.get('repository', '')
        this.credentialsId = opts.get('credentialsId', '')
        this.region = opts.get('region', '')
    }

    public Map getSubscribedEvents() {
        return [
            "${Event.PREPARE}": [
                [{ event -> this.doCreateRepository(event) }, 10],
            ],
        ]
    }

    public void doCreateRepository(Event event) {
        event.script.docker.image(this.dockerImage).inside("--entrypoint=''") {
            event.script.withCredentials([[
                $class: 'AmazonWebServicesCredentialsBinding',
                credentialsId: this.credentialsId,
                accessKeyVariable: 'AWS_ACCESS_KEY_ID',
                secretKeyVariable: 'AWS_SECRET_ACCESS_KEY'
            ]]) {
                event.script.withEnv(["AWS_DEFAULT_REGION=${this.region}"]) {
                    try {
                        event.script.sh "aws ecr describe-repositories --repository-names ${this.repository}"
                    } catch (Exception e) {
                        event.script.sh "aws ecr create-repository --repository-name ${this.repository}"
                    }
                }
            }
        }
    }

}
