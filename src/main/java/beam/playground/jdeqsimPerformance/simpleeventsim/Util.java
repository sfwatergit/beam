package beam.playground.jdeqsimPerformance.simpleeventsim;

/**
 * Created by asif on 7/7/2017.
 */
public class Util {

    public static void calculateRateOfEventsReceived(String actorName, SimulationTimes simulationTimes, long firstEventReceivedTime, long lastEventReceiptTime, long noOfEventsReceived){


        /*long startTime = simulationTimes.getProducerStartTime();
        long endTime = simulationTimes.getProducerEndTime();
        long timeDuration = simulationTimes.getProducerEndTime() - simulationTimes.getProducerStartTime();
        long noOfEvents = simulationTimes.getNoOfEvents();*/


        long timeLapse = lastEventReceiptTime - firstEventReceivedTime;
        double timeLapseInSec = timeLapse/1000.0;
        double noOfEventsPerSec = (double)noOfEventsReceived/timeLapseInSec;

        System.out.println("Actor Name => " + actorName);
        //System.out.println("No of events received " + noOfEventsReceived);
        //System.out.println("Last Event Received at " + lastEventReceiptTime);
        //System.out.println("Time Difference = " + timeLapseInSec + " sec [" + timeLapse + "]");
        System.out.println("Rate of Events Received Per Second = " + noOfEventsPerSec);
        //System.out.println(simulationTimes.toString());
        System.out.println("--");
        //consumerActor2.tell(simulationTimes, getSelf());
    }

    public static void calculateRateOfEventsReceived(String actorName, long firstEventReceivedTime, long lastEventReceiptTime, long noOfEventsReceived){

        long timeLapse = lastEventReceiptTime - firstEventReceivedTime;
        double timeLapseInSec = timeLapse/1000.0;
        double noOfEventsPerSec = (double)noOfEventsReceived/timeLapseInSec;

        System.out.println("Actor Name => " + actorName);
        System.out.println("Rate of Events Received Per Second = " + noOfEventsPerSec + ", Total Received = " + noOfEventsReceived +
                ", Time Interval (" + firstEventReceivedTime + " - " + lastEventReceiptTime + " = " + timeLapse + "ms, [" + timeLapseInSec + "s])");
        System.out.println("--");
    }
}
