package io.stenic.jpipe.plugin

import io.stenic.jpipe.event.Event

class TriggerBuildPlugin extends Plugin {

    private String jobName;
    private String eventName;
    private Integer eventWeight;
    
    TriggerBuildPlugin(Map opts = [:]) {
        this.jobName = opts.get('jobName');
        this.eventName = opts.get('eventName', 'Deploy');
        this.eventWeight = opts.get('eventWeight', 20);
    }

    public Map getSubscribedEvents() {
        return [
            "${this.eventName}": [
                [{ event -> this.doTriggerBuild(event) }, this.eventWeight],
            ],
        ]
    }

    public void doTriggerBuild(Event event) {
        event.script.build(
            job: this.jobName,
            parameters: [event.script.string(name: 'version', value: event.version)]
        )
    }
}
