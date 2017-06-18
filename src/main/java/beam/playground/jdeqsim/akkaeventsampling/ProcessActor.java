package beam.playground.jdeqsim.akkaeventsampling;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import beam.playground.jdeqsim.akkaeventsampling.Events.EventManager;
import beam.playground.jdeqsim.akkaeventsampling.messages.NotifyEventSubscriber;
import beam.playground.jdeqsim.akkaeventsampling.messages.ProcessingActorRequest;
import org.apache.log4j.Logger;
import org.matsim.api.core.v01.events.Event;

import java.util.Collections;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

public class ProcessActor extends UntypedActor {
    public static final String ACTOR_NAME = "Processing_Actor";
    private static final Logger log = Logger.getLogger(ProcessActor.class);
    private ActorRef eventManagerActor;

    public void preStart() throws Exception {
        this.eventManagerActor = getContext().actorOf(Props.create(EventManager.class), EventManager.ACTOR_NAME);
    }

    @Override
    public void onReceive(Object message) throws Throwable {
        if (message instanceof ProcessingActorRequest) {
            ProcessingActorRequest msg = (ProcessingActorRequest) message;
            log.debug("Message Received in ProcessActor");

            Hashtable<String, List<Event>> sortedEventsCollection = new Hashtable<>();
            for (Map.Entry<String, List<Event>> entry : msg.getEventsCollection().entrySet()) {
                String key = entry.getKey();
                List<Event> eventList = msg.getEventsCollection().get(key);
                Collections.sort(eventList, new EventTimeComparator());
                sortedEventsCollection.put(key, eventList);
            }
            this.eventManagerActor.tell(new NotifyEventSubscriber(sortedEventsCollection), ActorRef.noSender());

        } else {
            unhandled(message);
        }
    }


}
