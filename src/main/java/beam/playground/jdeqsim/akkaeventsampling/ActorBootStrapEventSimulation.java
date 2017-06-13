package beam.playground.jdeqsim.akkaeventsampling;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import beam.playground.jdeqsim.akkaeventsampling.messages.SchedulerActorMessage;
import beam.playground.jdeqsim.akkaeventsampling.messages.SchedulerActorStartJobMessage;
import beam.playground.jdeqsim.akkaeventsampling.messages.SchedulerActorStopJobMessage;
import org.apache.log4j.Logger;

public class ActorBootStrapEventSimulation {
    private static final Logger log = Logger.getLogger(ActorBootStrapEventSimulation.class);

    public static void main(String[] args) {

        ActorSystem system = startActorSystem();
        ActorRef router = startEventRouter(system);

        ActorRef scheduleActorUtilRef = startAndGetSchedulerUtilActorRef(system, router);

        SchedulerActorStartJobMessage jobMessage = new SchedulerActorStartJobMessage(500, SchedulerActorMessage.GENERATE_EVENT);
        startSchedulerJob(scheduleActorUtilRef, jobMessage);
        SchedulerActorStartJobMessage jobMessage1 = new SchedulerActorStartJobMessage(300, SchedulerActorMessage.GENERATE_EVENT);
        startSchedulerJob(scheduleActorUtilRef, jobMessage1);
        SchedulerActorStartJobMessage jobMessage2 = new SchedulerActorStartJobMessage(400, SchedulerActorMessage.GENERATE_EVENT);
        startSchedulerJob(scheduleActorUtilRef, jobMessage2);

        SchedulerActorStartJobMessage specialEventJobMessage = new SchedulerActorStartJobMessage(10000, SchedulerActorMessage.SPECIAL_EVENT);
        startSchedulerJob(scheduleActorUtilRef, specialEventJobMessage);


        try {
            Thread.sleep(50000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        SchedulerActorStopJobMessage jobStopMessage = new SchedulerActorStopJobMessage(jobMessage.getId());
        stopSchedulerJob(scheduleActorUtilRef, jobStopMessage);
        SchedulerActorStopJobMessage jobStopMessage1 = new SchedulerActorStopJobMessage(jobMessage1.getId());
        stopSchedulerJob(scheduleActorUtilRef, jobStopMessage1);
        SchedulerActorStopJobMessage jobStopMessage2 = new SchedulerActorStopJobMessage(jobMessage2.getId());
        stopSchedulerJob(scheduleActorUtilRef, jobStopMessage2);
        SchedulerActorStopJobMessage specialEventJobStopMessage = new SchedulerActorStopJobMessage(specialEventJobMessage.getId());
        stopSchedulerJob(scheduleActorUtilRef, specialEventJobStopMessage);
    }

    private static ActorRef startEventRouter(ActorSystem system) {
        return system.actorOf(Props.create(EventLoadBalancing.class), EventLoadBalancing.ACTOR_NAME);
    }

    private static ActorSystem startActorSystem() {
        return ActorSystem.create("EventSamplingActorSystem");
    }

    private static void startSchedulerJob(ActorRef schedulerActorUtilRef, SchedulerActorStartJobMessage jobMessage) {
        schedulerActorUtilRef.tell(jobMessage, ActorRef.noSender());
    }

    private static ActorRef startAndGetSchedulerUtilActorRef(ActorSystem system, ActorRef router) {
        return system.actorOf(Props.create(SchedulerActorUtil.class, router), SchedulerActorUtil.ACTOR_NAME);
    }

    private static void stopSchedulerJob(ActorRef schedulerActorUtilRef, SchedulerActorStopJobMessage jobStopMessage) {
        schedulerActorUtilRef.tell(jobStopMessage, ActorRef.noSender());
    }
}
