package beam.playground.jdeqsimPerformance.simpleeventsim;

import akka.actor.UntypedActor;
import org.matsim.api.core.v01.events.Event;

/**
 * Created by asif on 6/17/2017.
 */
public class ConsumerActor2 extends UntypedActor{

    int noOfEventsReceived = 0;
    Event eventReceived = null;
    long lastEventReceiptTime = 0;

    //Queue<Event> eventQueue = new PriorityQueue<>(100, new EventTimeComparator());
    //List<Event> eventList = new ArrayList<>();

    @Override
    public void onReceive(Object message) throws Throwable {
        if(message instanceof Event){

            eventReceived = (Event)message;
            //eventQueue.add(eventReceived);
            //eventList.add(event);
            /*if(eventReceived instanceof PhysSimTimeSyncEvent){
                System.out.println("Physsynevent received " + eventReceived);
            }*/
            noOfEventsReceived++;
            lastEventReceiptTime = System.currentTimeMillis();
        }else if(message instanceof String){
            if(((String) message).equalsIgnoreCase("SIM_COMPLETED")){

                System.out.println("Last Event Timestamp " + eventReceived.getTime());
                System.out.println("Last Event Received Timestamp " + lastEventReceiptTime);
                System.out.println("Current Timestamp " + System.currentTimeMillis() + " - No of Events Received " + noOfEventsReceived);
            }else if(((String) message).equalsIgnoreCase("GET_LAST_EVENT_TIME")){

                System.out.println("Going to return the last event time received - no of events => " + noOfEventsReceived);
                SimulationTimeStamp timeStamp = new SimulationTimeStamp(lastEventReceiptTime);
                getSender().tell(timeStamp, getSelf());
            }
        }else if(message instanceof SimulationTimes){

            SimulationTimes simulationTimes = (SimulationTimes)message;

            long startTime = simulationTimes.getProducerStartTime();
            long endTime = simulationTimes.getProducerEndTime();
            long timeDuration = simulationTimes.getProducerEndTime() - simulationTimes.getProducerStartTime();
            long noOfEvents = simulationTimes.getNoOfEvents();

            System.out.println("Actor Name => " + getSelf().toString());

            System.out.println("No of events received " + noOfEventsReceived);
            System.out.println("Start " + startTime);
            //System.out.println("End " + endTime);
            System.out.println("Events Generated " + noOfEvents/timeDuration);
            System.out.println("End Time of Consumer " + lastEventReceiptTime);

            long timeLapse = lastEventReceiptTime - startTime;
            double timeLapseInSec = timeLapse/1000.0;
            double noOfEventsPerSec = (double)noOfEventsReceived/timeLapseInSec;


            System.out.println("time lapse in seconds " + timeLapseInSec + ", in millis " + timeLapse);
            System.out.println("No of Events Per Second -> Producer to Consumer -> " + noOfEventsPerSec);

            System.out.println("--");


        }
    }
}
