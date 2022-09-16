package io.stenic.jpipe.plugin

import io.stenic.jpipe.event.Event

class TrivyPlugin extends Plugin {

    private Boolean allowFailure;
    private String containerImage;
    private Integer eventWeight;
    private String extraFlags;
    private Boolean ignoreUnfixed;
    private List severity;
    private String trivyVersion;
    
    TrivyPlugin(Map opts = [:]) {
        this.allowFailure = opts.get('allowFailure', false);
        this.trivyVersion = opts.get('trivyVersion', 'latest');
        this.containerImage = opts.get('containerImage', '');
        this.extraFlags = opts.get('extraFlags', '');
        this.ignoreUnfixed = opts.get('ignoreUnfixed', true);
        this.severity = opts.get('severity', ['HIGH', 'CRITICAL', 'MEDIUM', 'LOW', 'UNKNOWN']);
        this.eventWeight = opts.get('eventWeight', 20);
    }

    public Map getSubscribedEvents() {
        return [
            "${Event.TEST}": [
                [{ event -> this.doRunImageScan(event) }, this.eventWeight],
            ],
        ]
    }

    public void doRunImageScan(Event event) {
        if (this.containerImage == '') {
            event.script.println("Skipping TrivyPlugin: no containerImage defined")
            return
        }

        try {
            event.script.sh """
                docker run \
                    -v /var/run/docker.sock:/var/run/docker.sock \
                    aquasec/trivy:${this.trivyVersion} \
                    image \
                        --no-progress \
                        --exit-code 1 \
                        --severity ${this.severity.join(',')} \
                        ${(this.ignoreUnfixed == true) ? '--ignore-unfixed' : ''} \
                        ${this.extraFlags} \
                    ${this.containerImage}:${event.version}
            """
        } catch (Exception e) {
            if (this.allowFailure) {
                event.script.catchError(buildResult: 'SUCCESS', stageResult: 'UNSTABLE') {
                    throw e
                }
            } else {
                throw e
            }
        }
    }
}
