
import spock.lang.Specification
import io.stenic.jpipe.pipeline.Pipeline
import io.stenic.jpipe.plugin.Plugin
import io.stenic.jpipe.event.Event
import io.stenic.jpipe.event.PipelineHalted

class PipelineSpec extends Specification {

    // Fake Jenkins script: records the stages that actually run and executes
    // each stage closure inline (so handlers fire), mirroring stage() {}.
    static class FakeScript {
        List ranStages = []
        List greenStages = []
        def currentBuild = [displayName: '', result: null, description: '']

        def stage(String name, Closure body) {
            ranStages << name
            body()
            // Reached only if the body did not throw out of the stage.
            greenStages << name
        }

        def echo(msg) {}
    }

    // Plugin that halts the pipeline from a given stage.
    static class HaltingPlugin extends Plugin {
        String stage
        HaltingPlugin(String stage) { this.stage = stage }
        Map getSubscribedEvents() {
            return ["${this.stage}": [[{ e -> throw new PipelineHalted('stop here') }, 0]]]
        }
    }

    // Plugin that just registers a listener so the optional stage runs.
    static class NoopPlugin extends Plugin {
        String stage
        NoopPlugin(String stage) { this.stage = stage }
        Map getSubscribedEvents() {
            return ["${this.stage}": [[{ e -> }, 0]]]
        }
    }

    def "[Pipeline] runs core stages on a normal build"() {
        given:
            def script = new FakeScript()
            def pipeline = new Pipeline(script)

        when:
            pipeline.run([:], [BUILD_ID: '123'])

        then:
            assert script.ranStages == ['Prepare', 'Build']
            assert script.greenStages == ['Prepare', 'Build']
    }

    def "[Pipeline] runs optional stages when a listener is registered"() {
        given:
            def script = new FakeScript()
            def pipeline = new Pipeline(script)
            pipeline.addPlugins([new NoopPlugin('Test'), new NoopPlugin('Deploy')])

        when:
            pipeline.run([:], [BUILD_ID: '123'])

        then:
            assert script.ranStages == ['Prepare', 'Build', 'Test', 'Deploy']
    }

    def "[Pipeline] a halt in Prepare keeps the stage green and skips later stages"() {
        given:
            def script = new FakeScript()
            def pipeline = new Pipeline(script)
            pipeline.addPlugins([new HaltingPlugin('Prepare')])

        when:
            pipeline.run([:], [BUILD_ID: '123'])

        then:
            // Prepare ran and stayed green (no exception escaped the stage),
            // and no further stage was started.
            assert script.ranStages == ['Prepare']
            assert script.greenStages == ['Prepare']
    }

    def "[Pipeline] a halt does not abort the run"() {
        given:
            def script = new FakeScript()
            def pipeline = new Pipeline(script)
            pipeline.addPlugins([new HaltingPlugin('Prepare')])

        when:
            pipeline.run([:], [BUILD_ID: '123'])

        then:
            // run() returns normally; PipelineHalted is swallowed.
            notThrown(PipelineHalted)
    }
}
