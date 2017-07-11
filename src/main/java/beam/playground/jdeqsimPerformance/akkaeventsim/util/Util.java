package beam.playground.jdeqsimPerformance.akkaeventsim.util;

import beam.playground.jdeqsimPerformance.simpleeventsim.SimulationTimes;

/**
 * Created by asif on 7/7/2017.
 */
public class Util {

    public static void calculateRateOfEventsReceived(String actorName, SimulationTimes simulationTimes, long firstEventReceivedTime, long lastEventReceiptTime, long noOfEventsReceived){




        long timeLapse = lastEventReceiptTime - firstEventReceivedTime;
        double timeLapseInSec = timeLapse/1000.0;
        double noOfEventsPerSec = (double)noOfEventsReceived/timeLapseInSec;

        System.out.println("Actor Name => " + actorName);
        System.out.println("Rate of Events Received Per Second = " + noOfEventsPerSec);
        System.out.println("--");
    }

    public static void calculateRateOfEventsReceived(String actorName, long firstEventReceivedTime, long lastEventReceiptTime, long noOfEventsReceived){

        long timeLapse = lastEventReceiptTime - firstEventReceivedTime;
        double timeLapseInSec = timeLapse/1000.0;
        double noOfEventsPerSec = (double)noOfEventsReceived/timeLapseInSec;

        //System.out.println("Actor Name => " + actorName);
        System.out.println(actorName + " -> Rate/sec = " + noOfEventsPerSec + ", Total Received = " + noOfEventsReceived +
                ", Time Interval (" + firstEventReceivedTime + " - " + lastEventReceiptTime + " = " + timeLapse + "ms, [" + timeLapseInSec + "s])");
        System.out.println("--");
    }
}
