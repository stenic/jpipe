package io.stenic.jpipe.plugin

import io.stenic.jpipe.event.EventSubscriber

abstract class Plugin implements EventSubscriber {
    final protected String name;
    protected def script;

    public setScript(script) {
        this.script = script;
    }
}
