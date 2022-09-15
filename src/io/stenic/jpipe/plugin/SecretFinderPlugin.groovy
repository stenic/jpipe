package io.stenic.jpipe.plugin

import io.stenic.jpipe.event.Event
import java.lang.Exception

class SecretFinderPlugin extends Plugin {

    public static final String TURTLEHOG = "TURTLEHOG";

    private Boolean allowFailure;
    private String trufflehogImage;
    private List scanners;

    SecretFinderPlugin(Map opts = [:]) {
        this.allowFailure = opts.get('allowFailure', false);
        this.trufflehogImage = opts.get('trufflehogImage', 'trufflesecurity/trufflehog:latest');
        this.scanners = opts.get('scanners', [this.TURTLEHOG]);
    }

    public Map getSubscribedEvents() {
        return [
            "${Event.TEST}": [
                [{ event -> this.doScan(event) }, -20],
            ],
        ]
    }

    public void doScan(Event event) {
        try {
            if (this.scanners.contains(this.TURTLEHOG)) {
                doTrufflehogScan(event)
            }
        } catch (Exception e) {
            if (!this.allowFailure) {
                throw e;
            }
        }
    }

    private void doTrufflehogScan(Event event) {
        event.script.sh "docker run -it -v \$(pwd):/pwd ${this.trufflehogImage} git file:///pwd"
    }
}
