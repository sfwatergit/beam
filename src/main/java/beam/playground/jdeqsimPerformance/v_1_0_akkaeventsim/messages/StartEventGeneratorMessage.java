package beam.playground.jdeqsimPerformance.v_1_0_akkaeventsim.messages;

import java.io.Serializable;

/**
 * Created by salma_000 on 7/5/2017.
 */
public class StartEventGeneratorMessage implements Serializable {

    private static int identifier = 0;
    private long timeInMilliSec = 5;
    private boolean oneTimeJob = false;
    private String eventType;
    private int id;


    public StartEventGeneratorMessage(long timeInMilliSec, String eventType) {
        this.timeInMilliSec = timeInMilliSec;
        this.eventType = eventType;
        id = identifier++;

    }


    public StartEventGeneratorMessage(long timeInMilliSec, boolean oneTimeJob) {
        this.timeInMilliSec = timeInMilliSec;
        this.oneTimeJob = oneTimeJob;
        id = identifier++;
    }


    public String getEventType() {
        return eventType;
    }

    public int getId() {
        return id;
    }

    public boolean isOneTimeJob() {
        return oneTimeJob;
    }

    public long getTimeInMilliSec() {
        return timeInMilliSec;
    }
}
