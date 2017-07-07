package beam.playground.jdeqsimPerformance.v_1_0_akkaeventsim.task5;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import beam.playground.jdeqsimPerformance.v_1_0_akkaeventsim.EventManagerActor;
import beam.playground.jdeqsimPerformance.v_1_0_akkaeventsim.event_generator_v_2_0.messages.GenerateActorMessage;
import beam.playground.jdeqsimPerformance.v_1_0_akkaeventsim.listener.EventListener;
import beam.playground.jdeqsimPerformance.v_1_0_akkaeventsim.messages.GenerateEventMessage;
import org.apache.log4j.Logger;

/**
 * Created by salma_000 on 7/5/2017.
 */
public class task5Main {
    private static final Logger log = Logger.getLogger(task5Main.class);
    public static long startTime;

    public static void main(String[] args) {
        ActorSystem system = loadActorSystem();
        ActorRef generateActorRef = createGeneratorActor(system);
        startTime = System.currentTimeMillis();
        EventManagerActor.registerEventListener(new EventListener());

        generateActorRef.tell(new GenerateActorMessage(10000000, GenerateEventMessage.LINK_ENTER_EVENT), ActorRef.noSender());

        //EventManagerActor.registerLinkEnterEventListener(new LinkEnterEventListener());
        //EventManagerActor.registerLinkLeaveEventListener(new LinkLeaveEventListener());


    }

    private static ActorSystem loadActorSystem() {
        return ActorSystem.create("EventSimV_2_0");
    }


    private static ActorRef createGeneratorActor(ActorSystem system) {
        return system.actorOf(Props.create(EventGeneratorActor.class), EventGeneratorActor.ACTOR_NAME);
    }


}
