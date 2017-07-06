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
        for (int i = 1; i < 1000; i++) {
            StartEventGeneratorMessage jobMessage1 = new StartEventGeneratorMessage(i, GenerateEventMessage.LINK_LEAVE_EVENT);
            startEventGeneration(EventGeneratorActorWrapperRef, jobMessage1);

        }
        /*StartEventGeneratorMessage jobMessage1 = new StartEventGeneratorMessage(900, GenerateEventMessage.LINK_LEAVE_EVENT);
        startEventGeneration(EventGeneratorActorWrapperRef, jobMessage1);
        StartEventGeneratorMessage jobMessage2 = new StartEventGeneratorMessage(850, GenerateEventMessage.LINK_LEAVE_EVENT);
        startEventGeneration(EventGeneratorActorWrapperRef, jobMessage2);
        StartEventGeneratorMessage jobMessage3 = new StartEventGeneratorMessage(800, GenerateEventMessage.LINK_LEAVE_EVENT);
        startEventGeneration(EventGeneratorActorWrapperRef, jobMessage3);
        StartEventGeneratorMessage jobMessage4 = new StartEventGeneratorMessage(750, GenerateEventMessage.LINK_LEAVE_EVENT);
        startEventGeneration(EventGeneratorActorWrapperRef, jobMessage4);
        StartEventGeneratorMessage jobMessage5 = new StartEventGeneratorMessage(700, GenerateEventMessage.LINK_LEAVE_EVENT);
        startEventGeneration(EventGeneratorActorWrapperRef, jobMessage5);
        StartEventGeneratorMessage jobMessage6 = new StartEventGeneratorMessage(650, GenerateEventMessage.LINK_LEAVE_EVENT);
        startEventGeneration(EventGeneratorActorWrapperRef, jobMessage6);
        StartEventGeneratorMessage jobMessage7 = new StartEventGeneratorMessage(600, GenerateEventMessage.LINK_LEAVE_EVENT);
        startEventGeneration(EventGeneratorActorWrapperRef, jobMessage7);
        StartEventGeneratorMessage jobMessage8 = new StartEventGeneratorMessage(, GenerateEventMessage.LINK_LEAVE_EVENT);
        startEventGeneration(EventGeneratorActorWrapperRef, jobMessage8);
        StartEventGeneratorMessage jobMessage9 = new StartEventGeneratorMessage(20, GenerateEventMessage.LINK_LEAVE_EVENT);
        startEventGeneration(EventGeneratorActorWrapperRef, jobMessage9);
        StartEventGeneratorMessage jobMessage10 = new StartEventGeneratorMessage(10, GenerateEventMessage.LINK_LEAVE_EVENT);
        startEventGeneration(EventGeneratorActorWrapperRef, jobMessage10);
        StartEventGeneratorMessage jobMessage11 = new StartEventGeneratorMessage(5, GenerateEventMessage.LINK_LEAVE_EVENT);
        startEventGeneration(EventGeneratorActorWrapperRef, jobMessage11);*/
    }

    private static void startLinkEnterScheduler(ActorRef EventGeneratorActorWrapperRef) {
        for (int i = 1; i < 1000; i++) {
            StartEventGeneratorMessage jobMessage1 = new StartEventGeneratorMessage(i, GenerateEventMessage.LINK_ENTER_EVENT);
            startEventGeneration(EventGeneratorActorWrapperRef, jobMessage1);

        }
        /*StartEventGeneratorMessage jobMessage1 = new StartEventGeneratorMessage(100, GenerateEventMessage.LINK_ENTER_EVENT);
        startEventGeneration(EventGeneratorActorWrapperRef, jobMessage1);
        StartEventGeneratorMessage jobMessage2 = new StartEventGeneratorMessage(80, GenerateEventMessage.LINK_LEAVE_EVENT);
        startEventGeneration(EventGeneratorActorWrapperRef, jobMessage2);
        StartEventGeneratorMessage jobMessage3 = new StartEventGeneratorMessage(60, GenerateEventMessage.LINK_ENTER_EVENT);
        startEventGeneration(EventGeneratorActorWrapperRef, jobMessage3);
        StartEventGeneratorMessage jobMessage4 = new StartEventGeneratorMessage(40, GenerateEventMessage.LINK_LEAVE_EVENT);
        startEventGeneration(EventGeneratorActorWrapperRef, jobMessage4);*/
    }
}
