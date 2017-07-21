package beam.playground.jdeqsimPerformance.akkaeventsim.subscribers;

import akka.actor.UntypedActor;
import beam.playground.jdeqsimPerformance.akkaeventsim.events.handlers.LinkCountEventHandler;
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

    public EventSubscriber(EventHandler eventHandler){

        this.eventHandler = eventHandler;
    }
    @Override
    public void onReceive(Object message) throws Throwable {

        if(message instanceof Event){
            handleEvent(message);
        }else if(message instanceof String){
            handleMessage(message);
        }
    }

    public void handleEvent(Object message){
        this.performanceParameter.updateStatistics(1);

        Event event = (Event)message;

        if(eventHandler instanceof LinkEnterEventHandler && event.getEventType().equalsIgnoreCase(LinkEnterEvent.EVENT_TYPE)){
            LinkEnterEvent linkEnterEvent = (LinkEnterEvent)event;
            LinkEnterEventHandler handler = (LinkEnterEventHandler)eventHandler;
            handler.handleEvent(linkEnterEvent);
        }else if(eventHandler instanceof LinkLeaveEventHandler && event.getEventType().equalsIgnoreCase(LinkLeaveEvent.EVENT_TYPE)){
            LinkLeaveEvent linkLeaveEvent = (LinkLeaveEvent)event;
            LinkLeaveEventHandler handler = (LinkLeaveEventHandler)eventHandler;
            handler.handleEvent(linkLeaveEvent);
        }

        if (eventHandler instanceof LinkCountEventHandler) {

            LinkCountEventHandler handler = (LinkCountEventHandler) eventHandler;
            handler.handleEvent(event);
        }
    }

    public void handleMessage(Object message){

        String msg = (String) message;
        if(msg.equalsIgnoreCase("END")){

            this.performanceParameter.calculateRateOfEventsReceived(getSelf().path().toString());
        }else if(msg.equalsIgnoreCase("GET_HANDLER")){

            getSender().tell(eventHandler, getSelf());
        }else if(msg.equalsIgnoreCase("IS_COMPLETED")){
            getSender().tell("TRUE", getSelf());
        }
    }



}
