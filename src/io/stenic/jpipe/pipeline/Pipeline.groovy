package io.stenic.jpipe.pipeline

import io.stenic.jpipe.plugin.PluginManager
import io.stenic.jpipe.event.EventDispatcher
import io.stenic.jpipe.event.Event

class Pipeline implements Serializable {
    protected final def script
    private PluginManager pluginManager = new PluginManager()
    private EventDispatcher eventDispatcher = new EventDispatcher()

    Pipeline(script) {
        this.script = script;
    }

    public void addPlugins(List plugins) {
        plugins.each { this.pluginManager.register(it) }
    }

    public void run(Map config, env) {
        this.pluginManager.getPlugins().each { this.eventDispatcher.addSubscriber(it) }

        def Event event = new Event()
        
        event.env = env;
        event.version = env.BUILD_ID;
        event.setScript(this.script)

        this.script.stage("Prepare") {
            this.eventDispatcher.dispatch("Prepare", event)
            this.script.currentBuild.displayName = event.version
        }

        this.script.stage("Build") {
            this.script.echo "Version: ${event.version}";
            this.eventDispatcher.dispatch("Build", event)
        }

        this.script.stage("Test") {
            this.eventDispatcher.dispatch("Test", event)
        }
        
        this.script.stage("Publish") {
            this.eventDispatcher.dispatch("Publish", event)
        }
        
        this.script.stage("Deploy") {
            this.eventDispatcher.dispatch("Deploy", event)
        }
        
        return 
    }
}
