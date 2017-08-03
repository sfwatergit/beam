package beam.playground.jdeqsimPerformance.akkacluterawareeventsim;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import beam.playground.jdeqsimPerformance.akkaeventsim.generators.RealTimeEventGenerator;
import com.typesafe.config.ConfigFactory;

/**
 * Created by salma_000 on 8/1/2017.
 */
public class EventGeneratorMain {
    private LoggingAdapter log = null;
    private ActorSystem system;

    /*
     * default constructor
     */
    public EventGeneratorMain() {

        system = ActorSystem.create("EventGeneratorSys", ConfigFactory.load()
                .getConfig("EventGeneratorSys"));
        log = Logging.getLogger(system, this);
        // create the actor
        ActorRef actor = system.actorOf(Props.create(RealTimeEventGenerator.class, ActorRef.noSender()), "eventGeneratorActor");
        log.debug("Remote Actor path" + actor.path());
    }

    public static void main(String[] args) {

        new EventGeneratorMain();

    }
}
