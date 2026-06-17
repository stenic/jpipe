package io.stenic.jpipe.pipeline

import io.stenic.jpipe.plugin.PluginManager
import io.stenic.jpipe.event.EventDispatcher
import io.stenic.jpipe.event.Event
import io.stenic.jpipe.event.PipelineHalted

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
        event.setEventDispatcher(this.eventDispatcher)

        // A plugin can stop the pipeline early by throwing PipelineHalted (e.g.
        // SkipCommitPlugin on a [skip ci] commit). dispatchStage() catches it
        // inside the stage closure, so the stage stays green and the run keeps
        // whatever result the plugin set instead of aborting the build. The
        // framework stays agnostic about why the pipeline was halted.
        boolean halted = false

        halted = this.dispatchStage("Prepare", event, halted, {
            this.script.currentBuild.displayName = event.version
        })

        halted = this.dispatchStage("Build", event, halted, {
            this.script.echo "Version: ${event.version}";
        })

        if (this.eventDispatcher.getListeners("Test").size() > 0) {
            halted = this.dispatchStage("Test", event, halted)
        }

        if (this.eventDispatcher.getListeners("Publish").size() > 0) {
            halted = this.dispatchStage("Publish", event, halted)
        }

        if (this.eventDispatcher.getListeners("Deploy").size() > 0) {
            halted = this.dispatchStage("Deploy", event, halted)
        }

        return
    }

    // Runs a single stage: dispatches the event and runs an optional body.
    // If a prior stage halted the pipeline, this is a no-op. If a handler
    // throws PipelineHalted, it is swallowed inside the stage closure so the
    // stage still renders green, and 'true' is returned to stop later stages.
    private boolean dispatchStage(String name, Event event, boolean halted, Closure body = null) {
        if (halted) {
            return true
        }

        boolean stopped = false
        this.script.stage(name) {
            try {
                this.eventDispatcher.dispatch(name, event)
                if (body != null) {
                    body()
                }
            } catch (PipelineHalted h) {
                this.script.echo "Pipeline halted: ${h.message}"
                stopped = true
            }
        }
        return stopped
    }
}
