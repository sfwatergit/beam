package beam.playground.jdeqsimPerformance.simpleeventsim;

import akka.actor.ActorRef;
import akka.actor.UntypedActor;
import beam.playground.jdeqsimPerformance.EventGenerator;
import org.matsim.api.core.v01.events.Event;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by asif on 6/17/2017.
 */
public class ProducerActorBuffer extends UntypedActor{


    long startTime = 0;
    long endTime = 0;
    long noOfEvents = 10000000;
    EventGenerator eventGenerator = new EventGenerator();
    ActorRef consumer = null;

    List<Event> events = new ArrayList<>();

    ProducerActorBuffer(ActorRef consumer){
        this.consumer = consumer;
    }


    private void generateEvents(){
        long _startTime = System.currentTimeMillis();
        startTime = System.currentTimeMillis();

        for(int i = 0; i < noOfEvents; i++){

            long currentTime = System.currentTimeMillis();

            if(currentTime - startTime > 1000){
                Event event = eventGenerator.generatePhysSimTimeSyncEvent();
                startTime = currentTime;
                events.add(event);
            }else{
                Event event = eventGenerator.generateEvent();
                events.add(event);
            }


            if(events.size() == 10) {
                consumer.tell(events, getSelf());
                events.clear();
            }
        }

        if(!events.isEmpty()){
            consumer.tell(events, getSelf());
        }

        endTime = System.currentTimeMillis();
        SimulationTimes simulationTimes = new SimulationTimes(_startTime, endTime, noOfEvents);
        consumer.tell(simulationTimes, getSelf());
    }

    @Override
    public void onReceive(Object message) throws Throwable {

        if(message instanceof String){
            if(((String) message).equalsIgnoreCase("GENERATE_EVENTS")){
                generateEvents();
            }
        }
    }
}
