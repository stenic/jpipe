
import spock.lang.Specification
import io.stenic.jpipe.event.EventDispatcher
import io.stenic.jpipe.event.EventSubscriber

class EventDispatcherSpec extends Specification {

    EventDispatcher dispatcher = null;

    Closure mockListener = { event -> return true }
    Closure mockListener1 = { event -> return true }
    Closure mockListener2 = { event -> return true }

    def setup() {
        dispatcher = new EventDispatcher()
    }

    def "[EventDispatcher] addListener"() {
        when:
            dispatcher.addListener("test", mockListener, 0)
        then:
            assert dispatcher.getListeners("test").size() == 1
    }

    def "[EventDispatcher] addListener eventName"() {
        when:
            dispatcher.addListener("test", mockListener1, 0)
            dispatcher.addListener("test2", mockListener2, 0)
        then:
            assert dispatcher.getListeners("test").size() == 1
    }

    def "[EventDispatcher] getSorted default"() {
        when:
            dispatcher.addListener("test", mockListener1, 0)
            dispatcher.addListener("test", mockListener2, 0)
        then:
            assert dispatcher.getListeners("test") == [mockListener1, mockListener2]
    }

    def "[EventDispatcher] getSorted weighted"() {
        when:
            dispatcher.addListener("test", mockListener2, 10)
            dispatcher.addListener("test", mockListener1, 0)
        then:
            assert dispatcher.getListeners("test") == [mockListener1, mockListener2]
    }

    def "[EventDispatcher] getListeners empty"() {
        when:
            dispatcher.addListener("test", mockListener, 0)
        then:
            assert dispatcher.getListeners("test2").size() == 0
    }

    class MockSubscriber implements EventSubscriber {
        Map getSubscribedEvents() {
            return [
                "test": [[mockListener]],
                "test1": [[mockListener1], [mockListener2, -10]],
            ]
        }
    }

    def "[EventDispatcher] addSubscriber"() {
        when:
            dispatcher.addSubscriber(new MockSubscriber())
        then:
            assert dispatcher.getListeners("test").size() == 1
            assert dispatcher.getListeners("test1").size() == 2
            assert dispatcher.getListeners("test1") == [mockListener2, mockListener1]
    }
}