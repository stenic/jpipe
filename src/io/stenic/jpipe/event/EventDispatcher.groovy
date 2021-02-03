package io.stenic.jpipe.event

class EventDispatcher implements Serializable {
    private Map listeners = [:];

    public Event dispatch(String eventName, Event event) {
        this.getListeners(eventName).each{ it(event) }

        return event
    }

    public void addListener(String eventName, Closure listener, Integer priority) {
        if (!this.listeners[eventName]) {
            this.listeners[eventName] = [:];
        }
        if (!this.listeners[eventName][priority]) {
            this.listeners[eventName][priority] = [];
        }
        
        this.listeners[eventName][priority] += listener
    }

    public void addSubscriber(EventSubscriber subscriber)
    {
        subscriber.getSubscribedEvents().each { eventName, listeners ->
            listeners.each { it -> 
                this.addListener(eventName, it[0], it[1] ?: 0)
            }
        }
    }

    public Collection getListeners(String eventName) {
        return this.listeners.get(eventName, [:])
            ?.sort()
            ?.values()
            ?.flatten();
    }
}
