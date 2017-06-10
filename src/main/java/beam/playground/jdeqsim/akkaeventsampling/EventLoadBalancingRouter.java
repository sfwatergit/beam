package beam.playground.jdeqsim.akkaeventsampling;

import akka.actor.*;
import akka.routing.RoundRobinPool;
import beam.playground.jdeqsim.akkaeventsampling.messages.ProcessingActorRequest;
import beam.playground.jdeqsim.akkaeventsampling.messages.RouterMessageRequest;
import beam.playground.jdeqsim.akkaeventsampling.messages.WorkerMessageRequest;
import org.apache.log4j.Logger;
import org.matsim.api.core.v01.events.Event;
import scala.concurrent.duration.Duration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;


public class EventLoadBalancingRouter extends UntypedActor {
    public static final String ACTOR_NAME = "EventLoadBalancingRouter";
    private static final Logger log = Logger.getLogger(EventLoadBalancingRouter.class);
    private ActorRef worker;
    private ActorRef processActor;
    private List<Event> buffer;

    public void preStart() throws Exception {
        this.processActor = getContext().actorOf(Props.create(ProcessActor.class), ProcessActor.ACTOR_NAME);
        SupervisorStrategy supervisorStrategy = new OneForOneStrategy(5, Duration.create(1, TimeUnit.MINUTES), Collections.<Class<? extends Throwable>>singletonList(Exception.class));
        worker = getContext().actorOf(new RoundRobinPool(10).withSupervisorStrategy(supervisorStrategy).props(Props.create(Worker.class)), Worker.ACTOR_NAME);
    }

    @Override
    public void onReceive(Object message) throws Throwable {
        if (message instanceof RouterMessageRequest) {
            RouterMessageRequest msg = (RouterMessageRequest) message;
            if (!msg.getEvent().getEventType().equalsIgnoreCase("specialEvent")) {
                worker.forward(new WorkerMessageRequest(msg), getContext());
            } else if (msg.getEvent().getEventType().equalsIgnoreCase("specialEvent")) {
                buffer = new ArrayList<>(Dictionary.eventList);
                this.processActor.tell(new ProcessingActorRequest(buffer), ActorRef.noSender());
                log.debug("Dictionary size" + Dictionary.eventList.size());
                log.debug("Buffer size" + buffer.size());
                Dictionary.eventList.clear();
            }
        }
    }
}
