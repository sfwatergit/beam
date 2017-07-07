package beam.playground.jdeqsimPerformance.v_1_0_akkaeventsim.task5;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import beam.playground.jdeqsimPerformance.v_1_0_akkaeventsim.event_generator_v_2_0.messages.EventSimCompleteMessage;
import org.matsim.api.core.v01.events.Event;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by salma_000 on 7/7/2017.
 */
public class EventReceiver extends UntypedActor {
    public static String ACTOR_NAME = "EventReceiver";
    private static long eventCount = 0;
    public List<ActorRef> actorRefList = new ArrayList<ActorRef>();
    List<Event> bufferList = new ArrayList<>(10);
    int defaultActorSelection = 0;
    private int numberOfActor = 2;

    @Override
    public void preStart() throws Exception {
        for (int i = 0; i < numberOfActor; i++) {
            actorRefList.add(getContext().actorOf(Props.create(EventConsumerActor.class), EventConsumerActor.ACTOR_NAME + i));
        }
    }

    @Override
    public void onReceive(Object message) throws Throwable {
        if (message instanceof Event) {
            eventCount++;
            if (eventCount % 10 == 0) {

                ActorRef consumer = actorRefList.get(defaultActorSelection);
                consumer.tell(bufferList, ActorRef.noSender());
                if (defaultActorSelection == 0) {
                    defaultActorSelection = 1;
                } else if (defaultActorSelection == 1) {
                    defaultActorSelection = 0;
                }
            } else {
                bufferList.add((Event) message);
            }

        } else if (message instanceof EventSimCompleteMessage) {
            for (int i = 0; i < numberOfActor; i++) {
                ActorRef consumer = actorRefList.get(i);
                consumer.tell(message, ActorRef.noSender());
            }
        }
    }
}
