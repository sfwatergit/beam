package beam.playground.jdeqsimPerformance.v_1_0_akkaeventsim;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import beam.playground.jdeqsimPerformance.v_1_0_akkaeventsim.listener.EventListener;
import beam.playground.jdeqsimPerformance.v_1_0_akkaeventsim.messages.GenerateEventMessage;
import beam.playground.jdeqsimPerformance.v_1_0_akkaeventsim.messages.StartEventGeneratorMessage;
import beam.playground.jdeqsimPerformance.v_1_0_akkaeventsim.messages.StopEventGeneratorMessage;
import org.apache.log4j.Logger;

/**
 * Created by salma_000 on 7/3/2017.
 */
public class EventSimMain {
    private static final Logger log = Logger.getLogger(EventSimMain.class);
    public static long startTime;
    public static void main(String[] args) {
        ActorSystem system = loadActorSystem();

        ActorRef bufferActorRef = createEventBufferActor(system);
        ActorRef EventGeneratorActorWrapperRef = createEventGeneratorActorWrapper(system, bufferActorRef);

        //EventManagerActor.registerLinkEnterEventListener(new LinkEnterEventListener());
        //EventManagerActor.registerLinkLeaveEventListener(new LinkLeaveEventListener());
        EventManagerActor.registerEventListener(new EventListener());

        startLinkLeaveScheduler(EventGeneratorActorWrapperRef);
        startLinkEnterScheduler(EventGeneratorActorWrapperRef);

        /*StartEventGeneratorMessage jobMessage2 = new StartEventGeneratorMessage(200, GenerateEventMessage.GENERIC_EVENT);
        startEventGeneration(EventGeneratorActorWrapperRef, jobMessage2);*/

        StartEventGeneratorMessage jobMessage3 = new StartEventGeneratorMessage(1000, GenerateEventMessage.PHY_SIM_TIME_SYNC_EVENT);
        startEventGeneration(EventGeneratorActorWrapperRef, jobMessage3);
        startTime = System.currentTimeMillis();

        /*try {
            Thread.sleep(50000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        StopEventGeneratorMessage jobStopMessage = new StopEventGeneratorMessage(jobMessage.getId());
        stopEventGeneration(EventGeneratorActorWrapperRef, jobStopMessage);*/


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

    private static void startLinkLeaveScheduler(ActorRef EventGeneratorActorWrapperRef) {
        for (int i = 1; i < 40; i++) {
            StartEventGeneratorMessage jobMessage1 = new StartEventGeneratorMessage(1, GenerateEventMessage.LINK_LEAVE_EVENT);
            startEventGeneration(EventGeneratorActorWrapperRef, jobMessage1);

        }

    }

    private static void startLinkEnterScheduler(ActorRef EventGeneratorActorWrapperRef) {
        for (int i = 1; i < 40; i++) {
            StartEventGeneratorMessage jobMessage1 = new StartEventGeneratorMessage(1, GenerateEventMessage.LINK_ENTER_EVENT);
            startEventGeneration(EventGeneratorActorWrapperRef, jobMessage1);

        }

    }
}
