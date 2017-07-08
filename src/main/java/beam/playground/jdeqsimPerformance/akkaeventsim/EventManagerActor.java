package beam.playground.jdeqsimPerformance.akkaeventsim;

import akka.actor.UntypedActor;
import beam.playground.jdeqsimPerformance.LogEnterLinkEvents;
import org.matsim.api.core.v01.events.Event;
import org.matsim.api.core.v01.events.LinkEnterEvent;
import org.matsim.api.core.v01.events.handler.LinkEnterEventHandler;
import org.matsim.core.events.handler.EventHandler;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by asif on 6/17/2017.
 */
public class EventManagerActor extends UntypedActor{

    List<EventHandler> eventHandlers = new ArrayList<>();
    LogEnterLinkEvents eventHandler = new LogEnterLinkEvents();
    long totalEventsReceived = 0;
    public EventManagerActor(){

        eventHandlers.add(eventHandler);
    }

    @Override
    public void onReceive(Object message) throws Throwable {
        if(message instanceof List){
            List<Event> events = (List<Event>)message;

            totalEventsReceived = totalEventsReceived + events.size();
            long currentTime = System.currentTimeMillis();
            System.out.println("Events received ->>> " + events.size() + ", total events received " + totalEventsReceived + " current time " + currentTime);
            for(EventHandler eventHandler: eventHandlers) {

                for(Event event : events) {

                    if(eventHandler instanceof LinkEnterEventHandler && event instanceof LinkEnterEvent) {
                        LinkEnterEvent _event = (LinkEnterEvent)event;
                        LinkEnterEventHandler _eventHandler = (LinkEnterEventHandler)eventHandler;
                        _eventHandler.handleEvent(_event);
                    }

                    //System.out.println("Event -> " + event);
                }
            }
        }else if(message instanceof String){
            String _message = (String)message;
            if(_message.equals("SIM_COMPLETED")){
                System.out.println("Sim completed received");
                eventHandler.getCSVWriter().printLinkDataToCSV();
                //eventHandler.getCSVWriter().printLinkData();
            }
        }
    }
}
