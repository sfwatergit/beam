package beam.playground.jdeqsimPerformance.akkaclustereventsim;


import akka.actor.*;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.remote.RemoteScope;
import com.typesafe.config.ConfigFactory;

public class ClientActorSystem {

    private LoggingAdapter log = null;
    private ActorSystem system;
    private ActorRef actor;
    private ActorRef remoteActor;

    /*
     * Default Constructor
     */
    public ClientActorSystem() {
        system = ActorSystem.create("ClientSys", ConfigFactory.load()
                .getConfig("ClientSys"));
        log = Logging.getLogger(system, this);

    }

    public static void main(String[] args) throws InterruptedException {

        ClientActorSystem cAS = new ClientActorSystem();
        //cAS.remoteActorRefDemo();
        //cAS.remoteActorSelectionDemo();
        //cAS.remoteActorCreationDemo2();
        cAS.remoteActorCreationDemo3();
        Thread.sleep(1000);
        cAS.shutdown();
    }

    /*
     * Method demonstrates how to get a reference to the ServerActor deployed
     * on a remote node and how to pass the message to the same.
     * Key here is system.actorFor()
     */
    @SuppressWarnings("serial")
    public void remoteActorRefDemo() {
        log.info("Creating a reference to remote actor");
        // creating a reference to the remote ServerActor
        // by passing the complete remote actor path
        remoteActor = system
                .actorFor("akka.tcp://ServerSys@192.168.0.87:2553/user/serverActor");

        log.info("ServerActor with hashcode #" + remoteActor.hashCode());

        // create a local actor and pass the reference of the remote actor
        actor = system.actorOf(Props.create(ClientActor.class, remoteActor), "remoteActorRefDemo");

        // send a message to the local client actor
        actor.tell("Start-RemoteActorRef", ActorRef.noSender());
    }

    /*
     * Method demonstrates how to create an instance of ServerActor on remote
     * node and how to pass the message to the same. This method demonstrates one
     * way to create the server node address
     * Key here is system.actorOf()
     *
     * refer to the ServerActorSystem for information on new server actor creation
     * identified via hashcode's
     */
    @SuppressWarnings("serial")
    public void remoteActorCreationDemo1() {
        log.info("Creating a actor using remote deployment mechanism");

        // create the address object that points to the remote server
        Address addr = new Address("akka.tcp", "ServerSys", "192.168.0.87", 2553);

        // creating the ServerActor on the specified remote server
        final ActorRef serverActor = system.actorOf(Props.create(ServerActor.class).withDeploy(new Deploy(new RemoteScope(addr))));
        /*final ActorRef serverActor = system.actorOf(new Props(ServerActor.class)
				.withDeploy(new Deploy(new RemoteScope(addr))));
*/
        // create a local actor and pass the reference of the remote actor
        actor = system.actorOf(Props.create(ClientActor.class, serverActor), "remoteActorCreationDemo1");
        // send a message to the local client actor
        actor.tell("Start-RemoteActorCreationDemo1", ActorRef.noSender());
    }

    /*
     * Method demonstrates how to create an instance of ServerActor on remote
     * node and how to pass the message to the same. This method demonstrates an
     * alternate way to create the server node address
     * Key here is system.actorOf()
     *
     * Refer to the ServerActorSystem for information on new server actor creation
     * identified via hashcode's
     */
    @SuppressWarnings("serial")
    public void remoteActorCreationDemo2() {
        log.info("Creating a actor with remote deployment");

        // alternate way to create the address object that points to the remote
        // server
        Address addr = AddressFromURIString
                .parse("akka.tcp://ServerSys@192.168.0.87:2553");

        // creating the ServerActor on the specified remote server
        //final ActorRef serverActor = system.actorOf(new Props(ServerActor.class).withDeploy(new Deploy(new RemoteScope(addr))));
        final ActorRef serverActor = system.actorOf(Props.create(ServerActor.class).withDeploy(new Deploy(new RemoteScope(addr))));

        // create a local actor and pass the reference of the remote actor
        actor = system.actorOf(Props.create(ClientActor.class, serverActor), "remoteActorCreationDemo2");
        // send a message to the local client actor
        actor.tell("Start-RemoteActorCreationDemo2", ActorRef.noSender());
    }

    /*
     * Method demonstrates the way to create an actor using remote deployment but the address
     * is not hard coded. The deployment information is picked from the application.conf
     *  akka{
     *    actor {
     * 	    deployment {
     *           /remoteServerActor {
     *                   remote = "akka://ServerSys@127.0.0.1:2552"
     *          }
     *       }
     *     }
     *   }
     *   The deployment name "remoteServerActor" is passed as the parameter to the system.actorOf()
     *   method
     *
     *   This option allows to move the actor creation in an existing application from local node to
     *   any remote node
     *
     * Refer to the ServerActorSystem for information on new server actor creation
     * identified via hashcode's
     *
     */
    @SuppressWarnings("serial")
    public void remoteActorCreationDemo3() {
        log.info("Creating a actor with remote deployment");

        // creating the ServerActor on the specified remote server
        //final ActorRef serverActor = system.actorOf(new Props(ServerActor.class),"remoteServerActor");
        final ActorRef serverActor = system.actorOf(Props.create(ServerActor.class), "remoteServerActor");

        // create a local actor and pass the reference of the remote actor
        actor = system.actorOf(Props.create(ClientActor.class, serverActor), "remoteActorCreationDemo3");

        // send a message to the local client actor
        actor.tell("Start-RemoteActorCreationDemo3", ActorRef.noSender());
    }

    public void shutdown() {

        log.info("Sending PoisonPill to ServerActorSystem");
		/*if(remoteActor!=null)
			remoteActor;*/
        log.info("Shutting down the ClientActorSystem");
        system.shutdown();
    }

    public void startup() {
    }

}
