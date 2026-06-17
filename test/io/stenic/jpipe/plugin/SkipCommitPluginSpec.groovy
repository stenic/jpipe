
import spock.lang.Specification
import io.stenic.jpipe.event.Event
import io.stenic.jpipe.event.EventDispatcher
import io.stenic.jpipe.event.PipelineHalted
import io.stenic.jpipe.plugin.SkipCommitPlugin

class SkipCommitPluginSpec extends Specification {

    // Minimal stand-in for currentBuild. Captures the result the plugin sets and
    // exposes a configurable previous build + cause.
    static class FakeBuild {
        String description
        def result
        def previousBuild
        def causeLookup = [:]   // class -> cause (null = not present)
        def rawBuild

        FakeBuild() {
            this.rawBuild = [getCause: { type -> causeLookup[type] }]
        }

        def getPreviousBuild() { return previousBuild }
    }

    def newEvent(FakeBuild build, String commitMsg) {
        def event = new Event()
        event.setEventDispatcher(new EventDispatcher())
        event.script = [
            sh: { args -> commitMsg },
            currentBuild: build,
            env: [:],
        ]
        return event
    }

    def "[SkipCommitPlugin] proceeds on a normal commit"() {
        given:
            def plugin = new SkipCommitPlugin()
            def event = newEvent(new FakeBuild(), "feat: a normal change")

        expect:
            assert plugin.doSkipCommit(event) == true
    }

    def "[SkipCommitPlugin] halts the pipeline on [skip ci]"() {
        given:
            def plugin = new SkipCommitPlugin()
            def build = new FakeBuild()
            def event = newEvent(build, "chore(release): 1.2.3 [skip ci]")

        when:
            plugin.doSkipCommit(event)

        then:
            def halted = thrown(PipelineHalted)
            assert halted.message == 'Skipped by [skip ci]'
            assert build.description == 'Skipped by [skip ci]'
    }

    def "[SkipCommitPlugin] also matches [ci skip]"() {
        given:
            def plugin = new SkipCommitPlugin()
            def build = new FakeBuild()
            def event = newEvent(build, "docs: tweak readme [ci skip]")

        when:
            plugin.doSkipCommit(event)

        then:
            thrown(PipelineHalted)
    }

    def "[SkipCommitPlugin] carries the previous build result forward"() {
        given:
            def plugin = new SkipCommitPlugin()
            def build = new FakeBuild(previousBuild: [result: 'UNSTABLE'])
            def event = newEvent(build, "chore(release): 9.9.9 [skip ci]")

        when:
            plugin.doSkipCommit(event)

        then:
            thrown(PipelineHalted)
            assert build.result == 'UNSTABLE'
    }

    def "[SkipCommitPlugin] reflects a previous failure on the skipped run"() {
        given:
            def plugin = new SkipCommitPlugin()
            def build = new FakeBuild(previousBuild: [result: 'FAILURE'])
            def event = newEvent(build, "chore(release): 9.9.9 [skip ci]")

        when:
            plugin.doSkipCommit(event)

        then:
            thrown(PipelineHalted)
            assert build.result == 'FAILURE'
    }

    def "[SkipCommitPlugin] defaults to SUCCESS when there is no previous build"() {
        given:
            def plugin = new SkipCommitPlugin()
            def build = new FakeBuild(previousBuild: null)
            def event = newEvent(build, "chore(release): 1.0.0 [skip ci]")

        when:
            plugin.doSkipCommit(event)

        then:
            thrown(PipelineHalted)
            assert build.result == 'SUCCESS'
    }

    def "[SkipCommitPlugin] emits SKIPPED with a reason so notifiers can publish status"() {
        given:
            def plugin = new SkipCommitPlugin()
            def build = new FakeBuild()
            def event = newEvent(build, "chore(release): 2.0.0 [skip ci]")

            def reasons = []
            event.eventDispatcher.addListener(Event.SKIPPED, { e -> reasons << e.data.reason }, 0)

        when:
            plugin.doSkipCommit(event)

        then:
            thrown(PipelineHalted)
            assert reasons == ['Skipped by [skip ci]']
    }

    def "[SkipCommitPlugin] does not skip when triggered by a user"() {
        given:
            def plugin = new SkipCommitPlugin()
            def build = new FakeBuild()
            build.causeLookup[hudson.model.Cause$UserIdCause] = [id: 'someone']
            def event = newEvent(build, "chore(release): 3.0.0 [skip ci]")

        expect:
            assert plugin.doSkipCommit(event) == true
    }
}
