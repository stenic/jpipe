package io.stenic.jpipe.event

class Event implements Serializable {
    public static final String PREPARE = "Prepare";
    public static final String BUILD = "Build";
    public static final String TEST = "Test";
    public static final String PUBLISH = "Publish";
    public static final String DEPLOY = "Deploy";
    public static final String SKIPPED = "Skipped";

    public String version;
    public def env;

    public def script;
    public setScript(script) {
        this.script = script;
    }

    // Optional dispatcher reference so plugins can publish follow-up events
    // (e.g. SkipCommitPlugin emitting SKIPPED before aborting the build).
    // Set by Pipeline.run() before dispatching.
    public EventDispatcher eventDispatcher;
    public setEventDispatcher(EventDispatcher eventDispatcher) {
        this.eventDispatcher = eventDispatcher;
    }

    // Free-form payload for event-specific data (e.g. SKIPPED carries 'reason').
    public Map data = [:];
}
