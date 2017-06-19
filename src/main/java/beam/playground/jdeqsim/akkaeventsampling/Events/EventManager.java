package beam.playground.jdeqsim.akkaeventsampling.Events;


import akka.actor.UntypedActor;
import beam.playground.jdeqsim.akkaeventsampling.EventLoadBalancing;
import beam.playground.jdeqsim.akkaeventsampling.messages.NotifyEventSubscriber;
import beam.playground.jdeqsim.akkaeventsampling.messages.SchedulerActorMessage;
import org.apache.log4j.Logger;
import org.matsim.api.core.v01.events.Event;
import org.matsim.api.core.v01.events.GenericEvent;
import org.matsim.api.core.v01.events.LinkEnterEvent;
import org.matsim.api.core.v01.events.LinkLeaveEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class EventManager extends UntypedActor {
    public static final String ACTOR_NAME = "EventManager_Actor";
    private static final Logger log = Logger.getLogger(EventManager.class);
    protected static ArrayList<IEventListener> linkEnterEventHandlerList = new ArrayList<IEventListener>(5);
    protected static ArrayList<IEventListener> linkLeaveEventHandlerList = new ArrayList<IEventListener>(5);
    protected static ArrayList<IEventListener> genericEventHandlerList = new ArrayList<IEventListener>(5);


    public static boolean registerListener(IEventListener listener, String eventType) {
        if (eventType.equalsIgnoreCase(SchedulerActorMessage.GENERIC_EVENT)) {
            genericEventHandlerList.add(listener);
            return true;
        } else if (eventType.equalsIgnoreCase(SchedulerActorMessage.LINK_LEAVE_EVENT)) {
            linkLeaveEventHandlerList.add(listener);
            return true;
        } else if (eventType.equalsIgnoreCase(SchedulerActorMessage.LINK_ENTER_EVENT)) {
            linkEnterEventHandlerList.add(listener);
            return true;
        }
        return false;
    }

    private static void printSortedEvents(List<Event> eventList, String eventType) {
        log.debug("Event Type:" + eventType + "Event List Size: " + eventList.size());
        for (Event event : eventList) {
            if (eventType.equalsIgnoreCase(SchedulerActorMessage.GENERIC_EVENT)) {
                log.debug("Generic Event: " + ((GenericEvent) event).toString());
            } else if (eventType.equalsIgnoreCase(SchedulerActorMessage.LINK_LEAVE_EVENT)) {
                log.debug("Link Leave Event: " + ((LinkLeaveEvent) event).toString());
            } else if (eventType.equalsIgnoreCase(SchedulerActorMessage.LINK_ENTER_EVENT)) {
                log.debug("Link Enter Event: " + ((LinkEnterEvent) event).toString());
            }
        }
    }

    @Override
    public void onReceive(Object message) throws Throwable {
        if (message instanceof NotifyEventSubscriber) {
            //log.debug("Message Received in EventManager");
            NotifyEventSubscriber msg = (NotifyEventSubscriber) message;
            for (Map.Entry<String, List<Event>> entry : msg.getEventsCollection().entrySet()) {
                String key = entry.getKey();
                List<Event> eventList = msg.getEventsCollection().get(key);
                EventManager.printSortedEvents(eventList, key);
                if (key.equalsIgnoreCase(SchedulerActorMessage.GENERIC_EVENT)) {
                    EventLoadBalancing.receiveGenericEventCount = EventLoadBalancing.receiveGenericEventCount + eventList.size();
                    sendNotification(genericEventHandlerList, eventList);
                } else if (key.equalsIgnoreCase(SchedulerActorMessage.LINK_LEAVE_EVENT)) {
                    EventLoadBalancing.receiveLinkLeaveEventCount = EventLoadBalancing.receiveLinkLeaveEventCount + eventList.size();
                    sendNotification(linkLeaveEventHandlerList, eventList);
                } else if (key.equalsIgnoreCase(SchedulerActorMessage.LINK_ENTER_EVENT)) {
                    EventLoadBalancing.receiveLinkEnterEventCount = EventLoadBalancing.receiveLinkEnterEventCount + eventList.size();
                    sendNotification(linkEnterEventHandlerList, eventList);
                }
            }

        }
    }

    private void sendNotification(List<IEventListener> subscriber, List<Event> eventList) {

        for (IEventListener eventListener : subscriber) {
            eventListener.callBack(eventList);
        }
    }


}
