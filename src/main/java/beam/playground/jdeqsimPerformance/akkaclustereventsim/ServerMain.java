package beam.playground.jdeqsimPerformance.akkaclustereventsim;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import com.typesafe.config.ConfigFactory;

public class ServerMain {

    private LoggingAdapter log = null;
    private ActorSystem system;

    /*
     * default constructor
     */
    public ServerMain() {

        system = ActorSystem.create("ServerSys", ConfigFactory.load()
                .getConfig("ServerSystemConfig1"));
        log = Logging.getLogger(system, this);
        // create the actor
        ActorRef actor = system.actorOf(Props.create(ServerActor.class), "serverActor");
        log.debug("Remote Actor path" + actor.path());
    }

    public static void main(String[] args) {

        new ServerMain();

    }

}
