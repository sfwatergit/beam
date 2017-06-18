package beam.playground.jdeqsim.akkaeventsampling.Events;


import akka.actor.UntypedActor;
import beam.playground.jdeqsim.akkaeventsampling.messages.NotifyEventSubscriber;
import beam.playground.jdeqsim.akkaeventsampling.messages.SchedulerActorMessage;
import org.apache.log4j.Logger;
import org.matsim.api.core.v01.events.Event;

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

    @Override
    public void onReceive(Object message) throws Throwable {
        if (message instanceof NotifyEventSubscriber) {
            log.debug("Message Received in EventManager");
            NotifyEventSubscriber msg = (NotifyEventSubscriber) message;
            for (Map.Entry<String, List<Event>> entry : msg.getEventsCollection().entrySet()) {
                String key = entry.getKey();
                List<Event> eventList = msg.getEventsCollection().get(key);
                if (key.equalsIgnoreCase(SchedulerActorMessage.GENERIC_EVENT)) {
                    sendNotification(genericEventHandlerList, eventList);
                } else if (key.equalsIgnoreCase(SchedulerActorMessage.LINK_LEAVE_EVENT)) {
                    sendNotification(linkLeaveEventHandlerList, eventList);
                } else if (key.equalsIgnoreCase(SchedulerActorMessage.LINK_ENTER_EVENT)) {
                    sendNotification(linkEnterEventHandlerList, eventList);
                }
            }

        }
    }

    private void sendNotification(List<IEventListener> subscriber, List<Event> eventList) {
        log.debug("Message Received in EventManager Notifier" + eventList.size());
        for (IEventListener eventListener : subscriber) {
            eventListener.callBack(eventList);
        }
    }
}
