package io.stenic.jpipe.plugin

import io.stenic.jpipe.event.Event

class DockerPlugin extends Plugin {

    private String credentialId;
    private String server;
    private String repository;
    private String buildArgs;
    private Boolean push;
    private String filePath;
    private String testScript;
    private List extraTargets;
    private Boolean useCache;
    private Boolean doCleanup;

    DockerPlugin(Map opts = [:]) {
        this.repository = opts.get('repository', '');
        this.credentialId = opts.get('credentialId', '');
        this.server = opts.get('server', 'http://index.docker.io');
        this.buildArgs = opts.get('buildArgs', '');
        this.push = opts.get('push', this.credentialId != '');
        this.filePath = opts.get('filePath', '.');
        this.extraTargets = opts.get('extraTargets', []);
        this.testScript = opts.get('testScript', '');
        this.useCache = opts.get('useCache', true);
        this.doCleanup = opts.get('doCleanup', false);
    }

    public Map getSubscribedEvents() {
        return [
            "${Event.BUILD}": [
                [{ event -> this.doDockerBuild(event) }, 0],
            ],
            "${Event.TEST}": [
                [{ event -> this.doTest(event) }, 0],
            ],
            "${Event.PUBLISH}": [
                [{ event -> this.doDockerPush(event) }, -10],
                [{ event -> this.doDockerCleanup(event) }, 100],
            ],
        ]
    }

    public void doDockerBuild(Event event) {
        event.script.docker.withRegistry(this.server, this.credentialId) {
            if (this.useCache) {
                try {
                    event.script.docker.image("${this.repository}:cache").pull()
                    this.extraTargets.each { target ->
                        event.script.docker.image("${this.repository}:cache-${target}").pull()
                    }
                } catch(Exception e) {}
            }

            event.script.docker.build(
                "${this.repository}:${event.version}",
                "${this.buildArgs} ${this.filePath}"
            )
            this.extraTargets.each { target ->
                event.script.docker.build(
                    "${this.repository}:${target}",
                    "--target=${target} ${this.buildArgs} ${this.filePath}"
                )
            }
        }
    }

    public void doTest(Event event) {
        if (this.testScript != '') {
            event.script.docker.image("${this.repository}:${event.version}").inside {
                event.script.sh this.testScript
            }
        }
    }

    public void doDockerPush(Event event) {
        if (this.push) {
            event.script.docker.withRegistry(this.server, this.credentialId) {
                event.script.docker.image("${this.repository}:${event.version}").push()
                if (this.useCache) {
                    try {
                        event.script.docker.image("${this.repository}:${event.version}").push('cache')
                        this.extraTargets.each { target ->
                            event.script.docker.image("${this.repository}:${target}").push("cache-${target}")
                        }
                    } catch(Exception e) {}
                }
            }
        }
    }

    public void doDockerCleanup(Event event) {
        if (!this.doCleanup) {
            return
        }
        try {
            event.script.sh "docker rmi ${this.repository}:${event.version}"
            this.extraTargets.each { target ->
                event.script.sh "docker rmi ${this.repository}:${target}"
            }
            if (this.useCache) {
                event.script.sh "docker rmi ${this.repository}:cache"
                this.extraTargets.each { target ->
                    event.script.sh "docker rmi ${this.repository}:cache-${target}"
                }
            }
            event.script.sh 'docker rmi -f $(docker images -f "dangling=true" -q)'
        } catch(Exception e) {} 
    }
}
