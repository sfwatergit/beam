package beam.playground.jdeqsimPerformance.simpleeventsim;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;

/**
 * Created by asif on 7/8/2017.
 */
public class Main {




    public static void task0(){
        ActorSystem system = ActorSystem.create("simpleakkaeventsim");


        // Actor System with 1 Producer 1 consumer
        System.out.println("2 Actor System - Actor System with 1 Producer 1 consumer\n---------");
        ActorRef consumer1 = system.actorOf(Props.create(ConsumerActor.class), "Consumer1");

        // Actor System with 1 Producer 2 consumers
        /*System.out.println("3 Actor System - Actor System with 1 Producer 2 consumers\n---------");
        ActorRef consumer2 = system.actorOf(Props.create(ConsumerActor.class), "Consumer2");
        ActorRef consumer1 = system.actorOf(Props.create(ConsumerActor.class, consumer2), "Consumer1");*/

        // Actor System with 1 Producer 3 consumers
        /*System.out.println("4 Actor System - Actor System with 1 Producer 3 consumers\n---------");
        ActorRef consumer3 = system.actorOf(Props.create(ConsumerActor.class), "Consumer3");
        ActorRef consumer2 = system.actorOf(Props.create(ConsumerActor.class, consumer3), "Consumer2");
        ActorRef consumer1 = system.actorOf(Props.create(ConsumerActor.class, consumer2), "Consumer1");*/

        // Actor System with 1 Producer 4 consumers
        /*System.out.println("5 Actor System - Actor System with 1 Producer 4 consumers\n---------");
        ActorRef consumer4 = system.actorOf(Props.create(ConsumerActor.class), "Consumer4");
        ActorRef consumer3 = system.actorOf(Props.create(ConsumerActor.class, consumer4), "Consumer3");
        ActorRef consumer2 = system.actorOf(Props.create(ConsumerActor.class, consumer3), "Consumer2");
        ActorRef consumer1 = system.actorOf(Props.create(ConsumerActor.class, consumer2), "Consumer1");*/

        // Actor System with 1 Producer 5 consumers
        /*System.out.println("6 Actor System - Actor System with 1 Producer 5 consumers\n---------");
        ActorRef consumer5 = system.actorOf(Props.create(ConsumerActor.class), "Consumer5");
        ActorRef consumer4 = system.actorOf(Props.create(ConsumerActor.class, consumer5), "Consumer4");
        ActorRef consumer3 = system.actorOf(Props.create(ConsumerActor.class, consumer4), "Consumer3");
        ActorRef consumer2 = system.actorOf(Props.create(ConsumerActor.class, consumer3), "Consumer2");
        ActorRef consumer1 = system.actorOf(Props.create(ConsumerActor.class, consumer2), "Consumer1");*/


        ActorRef producer1 = system.actorOf(Props.create(ProducerActor.class, consumer1), "Producer1");
        producer1.tell("GENERATE_EVENTS", ActorRef.noSender());

    }



    public static void task1(){

        ActorSystem system = ActorSystem.create("simpleakkaeventsim2");

        // Actor System with 1 consumer

        ActorRef consumer1 = system.actorOf(Props.create(ConsumerActor.class), "Consumer1");

        /*System.out.println(">>> 2 Actor System - With bufferSize=10\n---------");
        ActorRef producer = system.actorOf(Props.create(ProducerActorBuffer.class, consumer1), "Producer");
        producer.tell("GENERATE_EVENTS", ActorRef.noSender());*/

        /*System.out.println(">>> 2 Actor System - With bufferSize=100\n---------");
        ActorRef producer = system.actorOf(Props.create(ProducerActorBuffer.class, consumer1, 100), "Producer");
        producer.tell("GENERATE_EVENTS", ActorRef.noSender());*/

/*
        System.out.println(">>> 2 Actor System - With bufferSize=1000\n---------");
        ActorRef producer = system.actorOf(Props.create(ProducerActorBuffer.class, consumer1, 1000), "Producer");
        producer.tell("GENERATE_EVENTS", ActorRef.noSender());
*/

        System.out.println(">>> 2 Actor System - With bufferSize=10000\n---------");
        ActorRef producer = system.actorOf(Props.create(ProducerActorBuffer.class, consumer1, 10000), "Producer");
        producer.tell("GENERATE_EVENTS", ActorRef.noSender());
    }

    public static void task2(){

        ActorSystem system = ActorSystem.create("simpleakkaeventsim3");

        // Actor System with 1 consumer
        System.out.println("2 Actor System \n---------");
        ActorRef consumer1 = system.actorOf(Props.create(ConsumerActor.class), "Consumer1");


        long noOfEvents = 5000000;

        ActorRef producer1 = system.actorOf(Props.create(ProducerActor.class, consumer1, noOfEvents), "Producer1");
        ActorRef producer2 = system.actorOf(Props.create(ProducerActor.class, consumer1, noOfEvents), "Producer2");

        System.out.println("Starting Producer 1 " + System.currentTimeMillis());
        producer1.tell("GENERATE_EVENTS", ActorRef.noSender());
        System.out.println("Starting Producer 2 " + System.currentTimeMillis());
        producer2.tell("GENERATE_EVENTS", ActorRef.noSender());

        //SimulationTimes simulationTimes = new SimulationTimes(_startTime, endTime, noOfEvents);
        //consumer.tell(simulationTimes, getSelf());
        /*producer1.tell("END", ActorRef.noSender());
        producer2.tell("END", ActorRef.noSender());*/
    }

    public static void main(String args[]){

        //task0();
        //task1();
        task2();
    }
}
