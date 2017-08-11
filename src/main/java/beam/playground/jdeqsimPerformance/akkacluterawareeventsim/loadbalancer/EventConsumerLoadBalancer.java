package beam.playground.jdeqsimPerformance.akkacluterawareeventsim.loadbalancer;

import akka.actor.*;
import akka.routing.Broadcast;
import akka.routing.RoundRobinPool;
import beam.playground.jdeqsimPerformance.akkaeventsim.EventBufferActor;
import beam.playground.jdeqsimPerformance.akkaeventsim.messages.EndSimulationMessage;
import beam.playground.jdeqsimPerformance.akkaeventsim.messages.GeneratedEventMessage;
import beam.playground.jdeqsimPerformance.akkaeventsim.messages.StartSimulationMessage;
import scala.concurrent.duration.Duration;

import java.util.Collections;
import java.util.concurrent.TimeUnit;

/**
 * Created by salma_000 on 8/3/2017.
 */
public class EventConsumerLoadBalancer extends UntypedActor {
    public static int No_Of_Worker = 10;
    private ActorRef worker;

    public EventConsumerLoadBalancer(ActorRef eventCollectorRef) {
        SupervisorStrategy supervisorStrategy = new OneForOneStrategy(5, Duration.create(1, TimeUnit.MINUTES), Collections.singletonList(Exception.class));
        worker = getContext().actorOf(new RoundRobinPool(No_Of_Worker).withSupervisorStrategy(supervisorStrategy).props(Props.create(EventBufferWorker.class, eventCollectorRef)));

    }

    @Override
    public void onReceive(Object message) throws Throwable {
        if (message instanceof GeneratedEventMessage) {
            GeneratedEventMessage msg = (GeneratedEventMessage) message;
            if (msg.getEventType().equalsIgnoreCase(EventBufferActor.PHY_SIM_TIME_SYNC_EVENT)) {
                broadcastMessage(message);
            } else {
                worker.forward(message, getContext());
            }

        }
        handleMessage(message);
    }

    private void handleMessage(Object message) {
        if (message instanceof StartSimulationMessage) {
            broadcastMessage(message);
        } else if (message instanceof EndSimulationMessage) {
            broadcastMessage(message);
        } else {
            unhandled(message);
        }
    }

    private void broadcastMessage(Object message) {
        worker.tell(new Broadcast(message), ActorRef.noSender());
    }
}
