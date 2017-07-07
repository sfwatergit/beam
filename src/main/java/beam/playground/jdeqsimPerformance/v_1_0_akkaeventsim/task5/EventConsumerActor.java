package beam.playground.jdeqsimPerformance.v_1_0_akkaeventsim.task5;

import akka.actor.UntypedActor;
import beam.playground.jdeqsim.akkaeventsampling.EventTimeComparator;
import beam.playground.jdeqsimPerformance.v_1_0_akkaeventsim.event_generator_v_2_0.messages.EventSimCompleteMessage;
import org.matsim.api.core.v01.events.Event;

import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;

/**
 * Created by salma_000 on 7/5/2017.
 */
public class EventConsumerActor extends UntypedActor {
    public static final String ACTOR_NAME = "EventConsumerActor";
    Queue<Event> eventQueue = new PriorityQueue<>(10000, new EventTimeComparator());
    private int noOfEventChunkReceived = 0;

    @Override
    public void onReceive(Object message) throws Throwable {
        if (message instanceof List) {

            List<Event> msg = (List<Event>) message;
            noOfEventChunkReceived++;

            if (noOfEventChunkReceived == 1) {
                System.out.println("First message received time in nano sec " + System.nanoTime() + "Event Count " + noOfEventChunkReceived);
            }
            for (int i = 0; i < 10; i++)
                eventQueue.add(msg.get(i));

        } else if (message instanceof EventSimCompleteMessage) {
            System.out.println("Last message received time in nano sec " + System.nanoTime());
            EventSimCompleteMessage msg = (EventSimCompleteMessage) message;
            long duration = System.nanoTime() - EventGeneratorActor.Gen_Start_Time;
            System.out.println("Total duration in Nano Sec " + (duration));

            double consumerRate = (noOfEventChunkReceived) / (duration / 1000000);
            double consumerRateNaneSec = (noOfEventChunkReceived) / (duration);
            System.out.println("Total Event count " + noOfEventChunkReceived);
            System.out.println("Event generated per nano seconds" + consumerRateNaneSec);
            System.out.println("Event generated per milli seconds" + consumerRate);
            System.out.println(this.getSelf().path());


        }

    }
}
