package beam.playground.jdeqsim.akkaeventsampling;

import akka.actor.*;
import akka.routing.RoundRobinPool;
import beam.playground.jdeqsim.akkaeventsampling.Events.PhySimTimeSyncEvent;
import beam.playground.jdeqsim.akkaeventsampling.messages.LoadBalancerMessageRequest;
import beam.playground.jdeqsim.akkaeventsampling.messages.ProcessingActorRequest;
import beam.playground.jdeqsim.akkaeventsampling.messages.SchedulerActorMessage;
import beam.playground.jdeqsim.akkaeventsampling.messages.WorkerMessageRequest;
import org.apache.log4j.Logger;
import org.matsim.api.core.v01.events.Event;
import scala.concurrent.duration.Duration;

import java.util.Collections;
import java.util.Hashtable;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


public class EventLoadBalancing extends UntypedActor {
    public static final String ACTOR_NAME = "EventLoadBalancing";
    private static final Logger log = Logger.getLogger(EventLoadBalancing.class);
    public static int receiveLinkEnterEventCount = 0;
    public static int receiveLinkLeaveEventCount = 0;
    public static int receiveGenericEventCount = 0;
    private ActorRef worker;
    private ActorRef processActor;

    public void preStart() throws Exception {
        this.processActor = getContext().actorOf(Props.create(ProcessActor.class), ProcessActor.ACTOR_NAME);
        SupervisorStrategy supervisorStrategy = new OneForOneStrategy(5, Duration.create(1, TimeUnit.MINUTES), Collections.<Class<? extends Throwable>>singletonList(Exception.class));
        worker = getContext().actorOf(new RoundRobinPool(10).withSupervisorStrategy(supervisorStrategy).props(Props.create(Worker.class)), Worker.ACTOR_NAME);
    }

    @Override
    public void onReceive(Object message) throws Throwable {
        if (message instanceof LoadBalancerMessageRequest) {
            LoadBalancerMessageRequest msg = (LoadBalancerMessageRequest) message;
            if (!msg.getEvent().getEventType().equalsIgnoreCase(SchedulerActorMessage.PHY_SIM_TIME_SYNC_EVENT)) {

                worker.forward(new WorkerMessageRequest(msg), getContext());
            } else if (msg.getEvent().getEventType().equalsIgnoreCase(SchedulerActorMessage.PHY_SIM_TIME_SYNC_EVENT)) {


                long syncStartTime = ((PhySimTimeSyncEvent) msg.getEvent()).getStartSyncTime();
                long syncEndTime = ((PhySimTimeSyncEvent) msg.getEvent()).getEndSyncTime();
                List<Event> genericEventList = Dictionary.genericEventList.stream()
                        .filter(event -> event.getTime() >= (syncStartTime) && event.getTime() < syncEndTime)
                        .collect(Collectors.toList());
                Dictionary.genericEventList.removeAll(genericEventList);
                List<Event> linkEnterEventList = Dictionary.linkEnterEventList.stream()
                        .filter(event -> event.getTime() >= (syncStartTime) && event.getTime() < syncEndTime)
                        .collect(Collectors.toList());
                Dictionary.linkEnterEventList.removeAll(linkEnterEventList);
                List<Event> linkLeaveEventList = Dictionary.linkLeaveEventList.stream()
                        .filter(event -> event.getTime() >= (syncStartTime) && event.getTime() < syncEndTime)
                        .collect(Collectors.toList());
                Dictionary.linkLeaveEventList.removeAll(linkLeaveEventList);
                Hashtable<String, List<Event>> eventsCollection = new Hashtable<>();
                eventsCollection.put(SchedulerActorMessage.GENERIC_EVENT, genericEventList);
                eventsCollection.put(SchedulerActorMessage.LINK_ENTER_EVENT, linkEnterEventList);
                eventsCollection.put(SchedulerActorMessage.LINK_LEAVE_EVENT, linkLeaveEventList);
                if (genericEventList.size() == 0 && linkEnterEventList.size() == 0 && linkLeaveEventList.size() == 0) {
                    log.debug("Total Receive Link Enter Events" + EventLoadBalancing.receiveLinkEnterEventCount);
                    log.debug("Total Receive Link Leave Events" + EventLoadBalancing.receiveLinkLeaveEventCount);
                    log.debug("Total Receive Generic Events" + EventLoadBalancing.receiveGenericEventCount);
                    log.debug("Total Not Process Link Enter Events" + Dictionary.linkEnterEventList.size());
                    log.debug("Total Not Process Link Leave Events" + Dictionary.linkLeaveEventList.size());
                    log.debug("Total Not Process Generic Events" + Dictionary.genericEventList.size());
                }
                this.processActor.tell(new ProcessingActorRequest(eventsCollection, syncStartTime, syncEndTime), ActorRef.noSender());

            }
        } else {
            unhandled(message);
        }

    }
}
