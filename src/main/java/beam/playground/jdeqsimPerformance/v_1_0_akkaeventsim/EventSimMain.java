package beam.playground.jdeqsimPerformance.v_1_0_akkaeventsim;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import beam.playground.jdeqsimPerformance.v_1_0_akkaeventsim.messages.GenerateEventMessage;
import beam.playground.jdeqsimPerformance.v_1_0_akkaeventsim.messages.StartEventGeneratorMessage;
import beam.playground.jdeqsimPerformance.v_1_0_akkaeventsim.messages.StopEventGeneratorMessage;
import org.apache.log4j.Logger;

/**
 * Created by salma_000 on 7/3/2017.
 */
public class EventSimMain {
    private static final Logger log = Logger.getLogger(EventSimMain.class);

    public static void main(String[] args) {
        ActorSystem system = loadActorSystem();

        ActorRef bufferActorRef = createEventBufferActor(system);
        ActorRef EventGeneratorActorWrapperRef = createEventGeneratorActorWrapper(system, bufferActorRef);

        StartEventGeneratorMessage jobMessage = new StartEventGeneratorMessage(200, GenerateEventMessage.LINK_ENTER_EVENT);
        startEventGeneration(EventGeneratorActorWrapperRef, jobMessage);
       /* StartEventGeneratorMessage jobMessage1 = new StartEventGeneratorMessage(50, GenerateEventMessage.LINK_LEAVE_EVENT);
        startEventGeneration(EventGeneratorActorWrapperRef, jobMessage1);
        StartEventGeneratorMessage jobMessage2 = new StartEventGeneratorMessage(200, GenerateEventMessage.GENERIC_EVENT);
        startEventGeneration(EventGeneratorActorWrapperRef, jobMessage2);*/

        StartEventGeneratorMessage jobMessage3 = new StartEventGeneratorMessage(1000, GenerateEventMessage.PHY_SIM_TIME_SYNC_EVENT);
        startEventGeneration(EventGeneratorActorWrapperRef, jobMessage3);
        try {
            Thread.sleep(50000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        StopEventGeneratorMessage jobStopMessage = new StopEventGeneratorMessage(jobMessage.getId());
        stopEventGeneration(EventGeneratorActorWrapperRef, jobStopMessage);


    }

    private static ActorSystem loadActorSystem() {
        return ActorSystem.create("EventSimV_1_0");
    }

    private static ActorRef createEventGeneratorActorWrapper(ActorSystem system, ActorRef eventBufferActor) {
        return system.actorOf(Props.create(EventGeneratorWrapperActor.class, eventBufferActor), EventGeneratorWrapperActor.ACTOR_NAME);
    }

    private static void startEventGeneration(ActorRef eventGeneratorWrapper, StartEventGeneratorMessage jobMessage) {
        eventGeneratorWrapper.tell(jobMessage, ActorRef.noSender());
    }

    private static void stopEventGeneration(ActorRef eventGeneratorWrapper, StopEventGeneratorMessage jobStopMessage) {
        eventGeneratorWrapper.tell(jobStopMessage, ActorRef.noSender());
    }

    private static ActorRef createEventBufferActor(ActorSystem system) {
        return system.actorOf(Props.create(EventsBufferActor.class), EventsBufferActor.ACTOR_NAME);
    }


}
