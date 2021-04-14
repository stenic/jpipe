package io.stenic.jpipe.plugin

import io.stenic.jpipe.event.Event

class TriggerBuildPlugin extends Plugin {

    private String jobName;
    private String eventName;
    private Integer eventWeight;
    private Map params;
    private Boolean addVersionParam;
    
    TriggerBuildPlugin(Map opts = [:]) {
        this.jobName = opts.get('jobName');
        this.eventName = opts.get('eventName', 'Deploy');
        this.eventWeight = opts.get('eventWeight', 20);
        this.params = opts.get('params', [:]);
        this.addVersionParam = opts.get('addVersion', true);
    }

    public Map getSubscribedEvents() {
        return [
            "${this.eventName}": [
                [{ event -> this.doTriggerBuild(event) }, this.eventWeight],
            ],
        ]
    }

    public void doTriggerBuild(Event event) {
        def params = [];
        this.params.each { params += event.script.string(name: it.key, value: it.value) }
        if (this.addVersionParam) {
            params += event.script.string(name: 'version', value: event.version)
        }

        event.script.build(
            job: this.jobName,
            parameters: params
        )
    }
}
