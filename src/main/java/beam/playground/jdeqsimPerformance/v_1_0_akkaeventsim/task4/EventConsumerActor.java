package beam.playground.jdeqsimPerformance.v_1_0_akkaeventsim.task4;

import akka.actor.UntypedActor;
import beam.playground.jdeqsim.akkaeventsampling.EventTimeComparator;
import beam.playground.jdeqsimPerformance.v_1_0_akkaeventsim.event_generator_v_2_0.messages.EventSimCompleteMessage;
import org.matsim.api.core.v01.events.Event;

import java.util.PriorityQueue;
import java.util.Queue;

/**
 * Created by salma_000 on 7/5/2017.
 */
public class EventConsumerActor extends UntypedActor {
    public static final String ACTOR_NAME = "EventConsumerActor";
    Queue<Event> eventQueue = new PriorityQueue<>(10000, new EventTimeComparator());
    private long noOfEventReceived = 0;

    @Override
    public void onReceive(Object message) throws Throwable {
        if (message instanceof Event) {
            Event msg = (Event) message;
            noOfEventReceived++;

            if (noOfEventReceived == 1)
                System.out.println("First message received time in nano sec " + System.nanoTime() + "Event Count " + noOfEventReceived);
            eventQueue.add(msg);

        } else if (message instanceof EventSimCompleteMessage) {
            System.out.println("Last message received time in nano sec " + System.nanoTime());
            EventSimCompleteMessage msg = (EventSimCompleteMessage) message;
            long duration = System.nanoTime() - EventGeneratorActor.Gen_Start_Time;
            System.out.println("Total duration in Nano Sec " + (duration));

            double consumerRate = (noOfEventReceived) / (duration / 1000000);
            double consumerRateNaneSec = (noOfEventReceived) / (duration);
            System.out.println("Total Event count " + noOfEventReceived);
            System.out.println("Event generated per nano seconds" + consumerRateNaneSec);
            System.out.println("Event generated per milli seconds" + consumerRate);
            System.out.println(this.getSelf().path());


        }

    }
}
