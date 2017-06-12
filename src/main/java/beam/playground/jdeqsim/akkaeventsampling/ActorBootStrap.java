package beam.playground.jdeqsim.akkaeventsampling;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import beam.playground.jdeqsim.akkaeventsampling.messages.SchedulerActorStartJobMessage;
import beam.playground.jdeqsim.akkaeventsampling.messages.SchedulerActorStopJobMessage;
import org.apache.log4j.Logger;
import org.matsim.api.core.v01.Scenario;
import org.matsim.core.api.experimental.events.EventsManager;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.mobsim.jdeqsim.JDEQSimConfigGroup;
import org.matsim.core.mobsim.jdeqsim.JDEQSimulation;
import org.matsim.core.scenario.ScenarioUtils;


public class ActorBootStrap {
    private static final Logger log = Logger.getLogger(ActorBootStrap.class);

    public static void main(String[] args) {
        if (args.length != 1) {
            log.error("Expected config file path as input arguments but found " + args.length);
        } else {
            Scenario scenario = ScenarioUtils.loadScenario(loadConfig(args[0]));

            ActorSystem system = startActorSystem();
            ActorRef eventLoadBalancer = startEventRouter(system);

            ActorRef scheduleActorUtilRef = startAndGetSchedulerUtilActorRef(system, eventLoadBalancer);

            SchedulerActorStartJobMessage jobMessage = new SchedulerActorStartJobMessage(500, "specialEvent");
            startSchedulerJob(scheduleActorUtilRef, jobMessage);


            CustomEventManager customEventManager = new CustomEventManager(eventLoadBalancer);
            EventsManager eventsManager = customEventManager;
            eventsManager.initProcessing();

            JDEQSimConfigGroup jdeqSimConfigGroup = new JDEQSimConfigGroup();
            JDEQSimulation jdeqSimulation = new JDEQSimulation(jdeqSimConfigGroup, scenario, eventsManager);
            jdeqSimulation.run();
            eventsManager.finishProcessing();

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            SchedulerActorStopJobMessage jobStopMessage = new SchedulerActorStopJobMessage(jobMessage.getId());
            stopSchedulerJob(scheduleActorUtilRef, jobStopMessage);
        }
    }

    private static Config loadConfig(String configFilePath) {
        //String defaultFileName = "C:/Users/salma_000/Desktop/MatSim/matsim-master/examples/scenarios/equil/config.xml";
        return ConfigUtils.loadConfig(configFilePath);
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
