package io.stenic.jpipe.plugin

import io.stenic.jpipe.event.Event

class CleanDirPlugin extends Plugin {
    public Map getSubscribedEvents() {
        return [
            "${Event.PREPARE}": [
                [{ event -> this.doDeleteDir(event) }, -1000],
            ],
        ]
    }

    public void doDeleteDir(Event event) {
        event.script.deleteDir()
    }
}
