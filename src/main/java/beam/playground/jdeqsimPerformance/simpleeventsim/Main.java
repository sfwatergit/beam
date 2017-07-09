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

        ActorRef lastConsumer = null;
        int noOfConsumers = 1;
//        noOfConsumers = 2;
        noOfConsumers = 3;
        noOfConsumers = 4;
//        noOfConsumers = 5;
        int counter = noOfConsumers;

        for(int i = 0; i < noOfConsumers; i++){

            if(lastConsumer == null) {
                lastConsumer = system.actorOf(Props.create(ConsumerActor.class), "Consumer" + counter--);
            }else{
                ActorRef consumer = system.actorOf(Props.create(ConsumerActor.class, lastConsumer), "Consumer" + counter--);
                lastConsumer = consumer;
            }
        }

        //System.out.println(">>> " + (noOfConsumers + 1) + " Actor System - Actor System with 1 Producer " + noOfConsumers + " Consumer (with PQ)");
        //System.out.println(">>> " + (noOfConsumers + 1) + " Actor System - Actor System with 1 Producer " + noOfConsumers + " Consumer (with LinkedList)");
        System.out.println(">>> " + (noOfConsumers + 1) + " Actor System - Actor System with 1 Producer " + noOfConsumers + " Consumer (with ArrayList)");
        System.out.println("---------");
        ActorRef producer1 = system.actorOf(Props.create(ProducerActor.class, lastConsumer), "Producer1");
        producer1.tell("GENERATE_EVENTS", ActorRef.noSender());

    }



    public static void task1(){

        ActorSystem system = ActorSystem.create("simpleakkaeventsim2");

        // Actor System with 1 consumer

        ActorRef consumer1 = system.actorOf(Props.create(ConsumerActor.class), "Consumer1");

        int bufferSize = 1000;
        bufferSize = 10000;
        bufferSize = 20000;
        bufferSize = 30000;
        bufferSize = 40000;
        bufferSize = 50000;
//        bufferSize = 60000;
//        bufferSize = 70000;
//        bufferSize = 80000;
//        bufferSize = 90000;
        bufferSize = 100000;
        bufferSize = 500000;
//        bufferSize = 600000;
//        bufferSize = 800000;
//        bufferSize = 900000;
        bufferSize = 1000000;
        bufferSize = 5000000;
        bufferSize = 1;
        bufferSize = 10;
        bufferSize = 100;


        System.out.println(">>> 2 Actor System - With bufferSize=" + bufferSize);
        System.out.println("---------");
        ActorRef producer = system.actorOf(Props.create(ProducerActorBuffer.class, consumer1, bufferSize), "Producer");
        producer.tell("GENERATE_EVENTS", ActorRef.noSender());
    }

    public static void task2(){

        ActorSystem system = ActorSystem.create("simpleakkaeventsim3");

        // Actor System with 1 consumer
        ActorRef consumer1 = system.actorOf(Props.create(ConsumerActor.class), "Consumer1");

        long totalNoOfEvents = 10000000;
        long noOfProducers = 2;
        long noOfEventsPerProducer = totalNoOfEvents/noOfProducers;

        System.out.println(">>>" + (noOfProducers + 1) + " Actor System - " + noOfProducers + " Producers , 1 Consumer");
        System.out.println("---------");

        for(int i = 1; i <= noOfProducers; i++){
            //System.out.println("Starting Producer " + i + " at " + System.currentTimeMillis());
            ActorRef producer = system.actorOf(Props.create(ProducerActor.class, consumer1, noOfEventsPerProducer), "Producer" + i);
            producer.tell("GENERATE_EVENTS", ActorRef.noSender());
        }
    }

    public static void main(String args[]){

        task0();
        //task1();
        //task2();
    }
}
