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
    private String report;
    
    TrivyPlugin(Map opts = [:]) {
        this.report = opts.get('report', 'table');
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

        List args = [
            '--no-progress',
            '--exit-code=1',
            "--severity ${this.severity.join(',')}",
        ]
        if (this.report == 'html') {
            args.add('--format template  --template "@contrib/html.tpl" -o /report/report.html')
        }
        if (this.ignoreUnfixed == true) {
            args.add('--ignore-unfixed')
        }
        args.add(this.extraFlags)

        event.script.dir(event.script.pwd(tmp: true)) {
            String imgName = this.containerImage.split('/').last()
            try {
                event.script.sh """
                    docker run \
                        -v /var/run/docker.sock:/var/run/docker.sock \
                        -v \$(pwd)/.trivy-report-${imgName}:/report \
                        aquasec/trivy:${this.trivyVersion} \
                        image ${args.join(' ')} ${this.containerImage}:${event.version}
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

            if (this.report == 'html') {
                event.script.publishHTML(target: [
                    allowMissing: true,
                    alwaysLinkToLastBuild: true,
                    keepAll: true,
                    reportDir: ".trivy-report-${imgName}",
                    reportFiles: 'report.html',
                    reportName: "Trivy - ${imgName}",
                ])
            }
        }
    }
}
