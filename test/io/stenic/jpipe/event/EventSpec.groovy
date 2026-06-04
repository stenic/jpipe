
import spock.lang.Specification
import io.stenic.jpipe.event.Event
import io.stenic.jpipe.event.EventDispatcher

class EventSpec extends Specification {

    def "[Event] exposes SKIPPED constant"() {
        expect:
            assert Event.SKIPPED == "Skipped"
    }

    def "[Event] carries dispatcher so plugins can publish follow-up events"() {
        given:
            def dispatcher = new EventDispatcher()
            def received = []
            dispatcher.addListener(Event.SKIPPED, { e -> received << e.data.reason }, 0)

            def event = new Event()
            event.setEventDispatcher(dispatcher)

        when:
            event.data.reason = 'Skipped by [skip ci]'
            event.eventDispatcher.dispatch(Event.SKIPPED, event)

        then:
            assert received == ['Skipped by [skip ci]']
    }

    def "[Event] dispatcher reference is optional (null-safe)"() {
        given:
            def event = new Event()

        expect:
            // Plugins should null-check before dispatch; verify the field defaults to null.
            assert event.eventDispatcher == null
    }
}
