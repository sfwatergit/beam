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
    long sentEvents = 0;
    EventGenerator eventGenerator = new EventGenerator();
    ActorRef consumer = null;
    int bufferSize = 10;

    List<Event> events = new ArrayList<>();

    ProducerActorBuffer(ActorRef consumer){

        this.consumer = consumer;
    }

    ProducerActorBuffer(ActorRef consumer, int bufferSize){

        this.consumer = consumer;
        this.bufferSize = bufferSize;
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


            if(events.size() == bufferSize) {
                consumer.tell(new ArrayList<>(events), getSelf());
                sentEvents += events.size();
                events.clear();
            }
        }

        endTime = System.currentTimeMillis();
        //System.out.println("Events sent " + sentEvents);
        if(!events.isEmpty()){
            consumer.tell(events, getSelf());
            sentEvents += events.size();
            endTime = System.currentTimeMillis();
        }
        //System.out.println("Total Events sent " + sentEvents);

        //SimulationTimes simulationTimes = new SimulationTimes(_startTime, endTime, noOfEvents);


        Util.calculateRateOfEventsReceived(getSelf().path().toString(), _startTime, endTime, noOfEvents);
    }

    @Override
    public void onReceive(Object message) throws Throwable {

        if(message instanceof String){
            if(((String) message).equalsIgnoreCase("GENERATE_EVENTS")){
                consumer.tell("START", getSelf());
                generateEvents();
                consumer.tell("END", getSelf());
            }
        }
    }
}
