package beam.playground.jdeqsimPerformance.akkaeventsim.subscribers;

import akka.actor.UntypedActor;
import beam.playground.jdeqsimPerformance.akkaeventsim.events.handlers.LinkCountEventHandler;
import beam.playground.jdeqsimPerformance.akkaeventsim.messages.EndSimulationMessage;
import beam.playground.jdeqsimPerformance.akkaeventsim.messages.GetHandlerMessage;
import beam.playground.jdeqsimPerformance.akkaeventsim.messages.SimulationCompleteMessage;
import beam.playground.jdeqsimPerformance.akkaeventsim.util.PerformanceParameter;
import org.matsim.api.core.v01.events.Event;
import org.matsim.api.core.v01.events.LinkEnterEvent;
import org.matsim.api.core.v01.events.LinkLeaveEvent;
import org.matsim.api.core.v01.events.handler.LinkEnterEventHandler;
import org.matsim.api.core.v01.events.handler.LinkLeaveEventHandler;
import org.matsim.core.events.handler.EventHandler;

/**
 * Created by asif on 7/9/2017.
 */
public class EventSubscriber extends UntypedActor {

    private PerformanceParameter performanceParameter = new PerformanceParameter();
    private EventHandler eventHandler = null;

    public EventSubscriber(EventHandler eventHandler) {
        this.eventHandler = eventHandler;
    }

    @Override
    public void onReceive(Object message) throws Throwable {
        if (message instanceof Event) {
            handleEvent(message);
        }
        handleMessage(message);
    }

    private void handleEvent(Object message) {
        this.performanceParameter.updateStatistics(1);
        Event event = (Event) message;
        if (eventHandler instanceof LinkCountEventHandler) {
            LinkCountEventHandler handler = (LinkCountEventHandler) eventHandler;
            handler.handleEvent(event);
        }
        if (eventHandler instanceof LinkEnterEventHandler) {
            LinkEnterEvent linkEnterEvent = (LinkEnterEvent) event;
            LinkEnterEventHandler handler = (LinkEnterEventHandler) eventHandler;
            handler.handleEvent(linkEnterEvent);
        } else if (eventHandler instanceof LinkLeaveEventHandler) {
            LinkLeaveEvent linkLeaveEvent = (LinkLeaveEvent) event;
            LinkLeaveEventHandler handler = (LinkLeaveEventHandler) eventHandler;
            handler.handleEvent(linkLeaveEvent);
        }
    }

    private void handleMessage(Object message) {
        if (message instanceof EndSimulationMessage) {
            this.performanceParameter.calculateRateOfEventsReceived(getSelf().path().toString());
        } else if (message instanceof GetHandlerMessage) {
            getSender().tell(eventHandler, getSelf());
        } else if (message instanceof SimulationCompleteMessage) {
            getSender().tell("TRUE", getSelf());
        } else {
            unhandled(message);
        }
    }
}
