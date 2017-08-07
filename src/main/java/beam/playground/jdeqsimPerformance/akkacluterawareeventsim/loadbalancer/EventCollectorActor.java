package beam.playground.jdeqsimPerformance.akkacluterawareeventsim.loadbalancer;

import akka.actor.ActorRef;
import akka.actor.UntypedActor;
import beam.playground.jdeqsim.akkaeventsampling.EventTimeComparator;
import beam.playground.jdeqsimPerformance.akkaeventsim.messages.BufferedEventMessage;
import beam.playground.jdeqsimPerformance.akkaeventsim.messages.EndSimulationMessage;
import org.matsim.api.core.v01.events.Event;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;

/**
 * Created by salma_000 on 8/3/2017.
 */
public class EventCollectorActor extends UntypedActor {
    private ActorRef eventManager = null;
    private Hashtable<Integer, List<Event>> collectedEventsCollection = new Hashtable<>();
    private Hashtable<Integer, EndSimulationMessage> simEndEventsCollection = new Hashtable<>();

    public EventCollectorActor(ActorRef eventManagerActor) {
        this.eventManager = eventManagerActor;
    }

    @Override
    public void onReceive(Object message) throws Throwable {
        if (message instanceof BufferedEventMessage) {
            BufferedEventMessage msg = (BufferedEventMessage) message;
            collectedEventsCollection.put(collectedEventsCollection.size() + 1, msg.getEventList());
            if (collectedEventsCollection.size() == EventConsumerLoadBalancer.No_Of_Worker) {
                mergeAndSendEventsToEventManager();
                collectedEventsCollection.clear();
            }
        } else if (message instanceof EndSimulationMessage) {
            EndSimulationMessage msg = (EndSimulationMessage) message;
            simEndEventsCollection.put(simEndEventsCollection.size() + 1, msg);
            if (simEndEventsCollection.size() == EventConsumerLoadBalancer.No_Of_Worker) {
                if (collectedEventsCollection.size() > 0) {
                    mergeAndSendEventsToEventManager();
                }
                this.eventManager.tell(new EndSimulationMessage(), getSelf());
                simEndEventsCollection.clear();
            }
        }

    }

    private void mergeAndSendEventsToEventManager() {
        List<Event> eventCollectedList = new ArrayList<>();
        for (int i = 1; i <= collectedEventsCollection.size(); i++) {
            List<Event> temp = collectedEventsCollection.get(i);
            eventCollectedList.addAll(temp);
        }
        Collections.sort(eventCollectedList, new EventTimeComparator());
        this.eventManager.tell(new BufferedEventMessage(eventCollectedList), getSelf());
    }
}
