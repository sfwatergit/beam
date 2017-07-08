package beam.playground.jdeqsimPerformance.simpleeventsim;

import akka.actor.ActorRef;
import akka.actor.UntypedActor;
import beam.playground.jdeqsimPerformance.akkaeventsim.EventTimeComparator;
import org.matsim.api.core.v01.events.Event;

import java.util.*;

/**
 * Created by asif on 6/17/2017.
 */
public class ConsumerActor extends UntypedActor{

    long noOfEventsReceived = 0;
    Event eventReceived = null;
    long lastEventReceiptTime = 0;
    long firstEventReceivedTime = 0;

    Queue<Event> eventQueue = new PriorityQueue<>(100, new EventTimeComparator());
    List<Event> eventList = new ArrayList<>();
    List<Event> eventLinkedList = new LinkedList<>();
    ActorRef nextConsumer = null;

    int startMessageCount = 0;
    int endMessageCount = 0;

    boolean simulationCompletedFlag = false;

    ConsumerActor(){
    }

    ConsumerActor(ActorRef nextConsumer){

        this.nextConsumer = nextConsumer;
    }

    @Override
    public void onReceive(Object message) throws Throwable {

        if(message instanceof Event){

            if(simulationCompletedFlag == true) {
                System.out.println("Message received after simulationCompletedFlag is set " + simulationCompletedFlag);
            }
            if(noOfEventsReceived == 0){
                firstEventReceivedTime = System.currentTimeMillis();
                //System.out.println(getSelf().path().toString() + " -> First Event Received at " + firstEventReceivedTime);
            }
            eventReceived = (Event)message;
            //eventQueue.add(eventReceived);
            //eventList.add(eventReceived);
            //eventLinkedList.add(eventReceived);
            noOfEventsReceived++;
            lastEventReceiptTime = System.currentTimeMillis();

            if(noOfEventsReceived == 10000000){

                //System.out.println(getSelf().path().toString() + " -> Last Event Received at " + lastEventReceiptTime);
            }

            if(nextConsumer != null){
                nextConsumer.tell(message, getSelf());
            }


        }else if(message instanceof List){

            if(noOfEventsReceived == 0){
                firstEventReceivedTime = System.currentTimeMillis();
                System.out.println(getSelf().path().toString() + " -> First Event Received at " + firstEventReceivedTime);
            }
            List<Event> eventsReceived = (List<Event>)message;
            noOfEventsReceived += eventsReceived.size();
            lastEventReceiptTime = System.currentTimeMillis();

            if(noOfEventsReceived == 10000000){

                System.out.println(getSelf().path().toString() + " -> Last Event Received at " + lastEventReceiptTime + ", noOfEventsReceived: " + noOfEventsReceived);

            }

            if(nextConsumer != null){
                nextConsumer.tell(message, getSelf());
            }
        }else if(message instanceof SimulationTimes){

            simulationCompletedFlag = true;
            SimulationTimes simulationTimes = (SimulationTimes)message;

            System.out.println(getSelf().toString() + ", First Event Received at " + firstEventReceivedTime +
                    ", Last Event Recived at " + lastEventReceiptTime +
                    ", No Of Events " + noOfEventsReceived);


            if(nextConsumer != null){

                nextConsumer.tell(message, getSelf());
            }else{

                Util.calculateRateOfEventsReceived(getSelf().path().toString(), firstEventReceivedTime, lastEventReceiptTime, noOfEventsReceived);

            }
        }else if(message instanceof String){


            if(((String) message).equalsIgnoreCase("START")){
                startMessageCount++;

            }else if(((String) message).equalsIgnoreCase("END")){
                endMessageCount++;
                if(endMessageCount == startMessageCount){

                    Util.calculateRateOfEventsReceived(getSelf().path().toString(), firstEventReceivedTime, lastEventReceiptTime, noOfEventsReceived);

                }
            }

            if(nextConsumer != null){
                nextConsumer.tell(message, getSelf());
            }
        }

    }
}
