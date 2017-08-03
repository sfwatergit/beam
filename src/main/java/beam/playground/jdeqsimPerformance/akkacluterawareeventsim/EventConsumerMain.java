package beam.playground.jdeqsimPerformance.akkacluterawareeventsim;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import beam.playground.jdeqsimPerformance.akkaeventsim.EventBufferActor;
import beam.playground.jdeqsimPerformance.akkaeventsim.EventManagerActor;
import beam.playground.jdeqsimPerformance.akkaeventsim.events.LinkEnterEventCountHandlerImpl;
import beam.playground.jdeqsimPerformance.akkaeventsim.events.LinkEventCountHandlerImpl;
import beam.playground.jdeqsimPerformance.akkaeventsim.events.LinkLeaveEventHandlerImpl;
import beam.playground.jdeqsimPerformance.akkaeventsim.events.handlers.LinkCountEventHandler;
import beam.playground.jdeqsimPerformance.akkaeventsim.events.handlers.LinkEnterEventCountHandler;
import beam.playground.jdeqsimPerformance.akkaeventsim.events.handlers.LinkLeaveEventCountHandler;
import beam.playground.jdeqsimPerformance.akkaeventsim.generators.RealTimeEventGenerator;
import beam.playground.jdeqsimPerformance.akkaeventsim.messages.GenerateEventMessage;
import com.typesafe.config.ConfigFactory;

/**
 * Created by salma_000 on 8/1/2017.
 */
public class EventConsumerMain {
    private LoggingAdapter log = null;
    private ActorSystem system;
    private ActorRef eventBufferActor;
    private ActorRef eventManagerActor;
    private ActorRef remoteEventGeneratorActor;
    private String handlerName;
    private String handlerName2;
    private String handlerName3;

    /*
     * Default Constructor
	 */
    public EventConsumerMain() {
        system = ActorSystem.create("EventConsumerSys", ConfigFactory.load()
                .getConfig("EventConsumerSys"));
        log = Logging.getLogger(system, this);

    }

    public static void main(String[] args) throws InterruptedException {

        EventConsumerMain eCM = new EventConsumerMain();
        eCM.setupLocalAndRemoteActor();
        eCM.registerEventHandler();
        eCM.listenEventSimComplete();

    }

    public void setupLocalAndRemoteActor() {
        log.info("Creating a actor with remote deployment");

        // create a local actor and pass the reference of the remote actor
        eventManagerActor = system.actorOf(Props.create(EventManagerActor.class), "EventManagerActor");
        eventBufferActor = system.actorOf(Props.create(EventBufferActor.class, eventManagerActor), "EventBufferActor");
        remoteEventGeneratorActor = system.actorOf(Props.create(RealTimeEventGenerator.class, eventBufferActor), "remoteEventGeneratorActor");
        remoteEventGeneratorActor.tell(new GenerateEventMessage(1000000), ActorRef.noSender());

    }

    public void shutdown() {
        log.info("Shutting down the ClientActorSystem");
        system.shutdown();
    }

    public void registerEventHandler() {
        handlerName = "LinkEnterEventHandler";
        LinkEnterEventCountHandler handler = new LinkEnterEventCountHandlerImpl();
        EventManagerActor.addHandler(handler, handlerName);

        handlerName2 = "LinkCountEventHandler";
        LinkCountEventHandler handler2 = new LinkEventCountHandlerImpl();
        EventManagerActor.addHandler(handler2, handlerName2);

        handlerName3 = "LinkLeaveEventHandler";
        LinkLeaveEventCountHandler handler3 = new LinkLeaveEventHandlerImpl();
        EventManagerActor.addHandler(handler3, handlerName3);
    }

    public void listenEventSimComplete() {
        while (EventManagerActor.isCompleted() == false) {

            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        LinkEnterEventCountHandler _handler = (LinkEnterEventCountHandler) EventManagerActor.getEventHandler(handlerName);
        LinkCountEventHandler _handler2 = (LinkCountEventHandler) EventManagerActor.getEventHandler(handlerName2);
        LinkLeaveEventCountHandler _handler3 = (LinkLeaveEventCountHandler) EventManagerActor.getEventHandler(handlerName3);
        System.out.println(handlerName + " -> count -> " + _handler.getCount());
        System.out.println(handlerName2 + " -> count -> " + _handler2.getCount());
        System.out.println(handlerName3 + " -> count -> " + _handler3.getCount());
        /*
        Is there a way to shutdown the system in the main.
         */
        system.terminate();
    }

}
