package io.stenic.jpipe.event

class Event implements Serializable {
    public static final String PREPARE = "Prepare";
    public static final String BUILD = "Build";
    public static final String TEST = "Test";
    public static final String PUBLISH = "Publish";
    public static final String DEPLOY = "Deploy";

    public String version;
    public def env;

    public def script;
    public setScript(script) {
        this.script = script;
    }
}
