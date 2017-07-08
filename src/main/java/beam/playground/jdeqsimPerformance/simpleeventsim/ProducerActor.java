package beam.playground.jdeqsimPerformance.simpleeventsim;

import akka.actor.ActorRef;
import akka.actor.UntypedActor;
import beam.playground.jdeqsimPerformance.EventGenerator;
import org.matsim.api.core.v01.events.Event;

/**
 * Created by asif on 6/17/2017.
 */
public class ProducerActor extends UntypedActor{


    long startTime = 0;
    long endTime = 0;
    long noOfEvents = 10000000;
    EventGenerator eventGenerator = new EventGenerator();
    ActorRef consumer = null;

    ProducerActor(ActorRef consumer){

        this.consumer = consumer;
    }

    ProducerActor(ActorRef consumer, long noOfEvents){

        this.consumer = consumer;
        this.noOfEvents = noOfEvents;
    }


    private void generateEvents(){
        long _startTime = System.currentTimeMillis();
        startTime = System.currentTimeMillis();

        for(int i = 0; i < noOfEvents; i++){

            long currentTime = System.currentTimeMillis();
            if(currentTime - startTime > 1000){
                Event event = eventGenerator.generatePhysSimTimeSyncEvent();
                consumer.tell(event, ActorRef.noSender());
                startTime = currentTime;
            }else{
                Event event = eventGenerator.generateEvent();
                consumer.tell(event, ActorRef.noSender());
            }
        }
        endTime = System.currentTimeMillis();

    }

    @Override
    public void onReceive(Object message) throws Throwable {

        if(message instanceof String){
            if(((String) message).equalsIgnoreCase("GENERATE_EVENTS")){
                consumer.tell("START", getSelf());
                generateEvents();
                consumer.tell("END", getSelf());
            }
        }else if(message instanceof String){


            if(((String) message).equalsIgnoreCase("END")){
                System.out.println("Going to send end message to consumer");
                consumer.tell("END", getSelf());
            }
        }
    }
}
