package beam.playground.jdeqsimPerformance.akkaeventsim.util;

/**
 * Created by salma_000 on 7/21/2017.
 */
public class PerformanceParameter {
    private long noOfEvents = 0;
    private long startTime = 0;
    private long endTime = 0;

    public void setNoOfEvents(long noOfEvents) {
        this.noOfEvents = noOfEvents;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }


    public void updateStatistics(long receivedEvents) {
        if (this.noOfEvents == 0) {
            this.startTime = System.currentTimeMillis();
        }
        this.endTime = System.currentTimeMillis();
        this.noOfEvents += receivedEvents;
    }

    public void calculateRateOfEventsReceived(String actorName) {

        long timeLapse = this.endTime - this.startTime;
        double timeLapseInSec = timeLapse / 1000.0;
        double noOfEventsPerSec = (double) this.noOfEvents / timeLapseInSec;

        System.out.println(actorName + " -> Rate/sec = " + noOfEventsPerSec + ", Total Received = " + this.noOfEvents +
                ", Time Interval (" + this.startTime + " - " + this.endTime + " = " + timeLapse + "ms, [" + timeLapseInSec + "s])");
        System.out.println("--");
    }
}
