package beam.playground.jdeqsimPerformance.v_1_0_akkaeventsim;


import akka.actor.UntypedActor;
import beam.playground.jdeqsimPerformance.v_1_0_akkaeventsim.listener.IEventListener;
import beam.playground.jdeqsimPerformance.v_1_0_akkaeventsim.listener.ILinkEnterEventListener;
import beam.playground.jdeqsimPerformance.v_1_0_akkaeventsim.listener.ILinkLeaveEventListener;
import beam.playground.jdeqsimPerformance.v_1_0_akkaeventsim.messages.EventManagerWorkerMessage;
import beam.playground.jdeqsimPerformance.v_1_0_akkaeventsim.messages.GenerateEventMessage;
import org.apache.log4j.Logger;
import org.matsim.api.core.v01.events.Event;
import org.matsim.api.core.v01.events.LinkEnterEvent;
import org.matsim.api.core.v01.events.LinkLeaveEvent;

import java.util.List;

public class Worker extends UntypedActor {
    public static final String ACTOR_NAME = "Worker";
    private static final Logger log = Logger.getLogger(Worker.class);

    @Override
    public void onReceive(Object message) throws Throwable {
        if (message instanceof EventManagerWorkerMessage) {
            EventManagerWorkerMessage msg = (EventManagerWorkerMessage) message;

            switch (msg.getEventManagerMessage().getEventType()) {
                case GenerateEventMessage.LINK_ENTER_EVENT:
                    for (ILinkEnterEventListener listener : EventManagerActor.linkEnterEventHandlerList) {
                        listener.linkEnterEventCallBack((List<LinkEnterEvent>) msg.getEventManagerMessage().getEventList());
                    }
                    break;
                case GenerateEventMessage.LINK_LEAVE_EVENT:
                    for (ILinkLeaveEventListener listener : EventManagerActor.linkLeaveEventHandlerList) {
                        listener.linkLeaveEventCallBack((List<LinkLeaveEvent>) msg.getEventManagerMessage().getEventList());
                    }
                    break;
                case GenerateEventMessage.ALL_EVENT:
                    for (IEventListener listener : EventManagerActor.allEventHandlerList) {
                        listener.callBack((List<Event>) msg.getEventManagerMessage().getEventList());
                    }

                    break;

                default:
                    throw new IllegalArgumentException("Invalid event type received at EventManagerActorWork: " + msg.getEventManagerMessage().getEventType());
            }

        }
    }
}
