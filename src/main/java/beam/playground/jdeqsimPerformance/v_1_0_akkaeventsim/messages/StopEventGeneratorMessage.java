package beam.playground.jdeqsimPerformance.v_1_0_akkaeventsim.messages;

import java.io.Serializable;

/**
 * Created by salma_000 on 7/5/2017.
 */
public class StopEventGeneratorMessage implements Serializable {
    private int jobId;

    public StopEventGeneratorMessage(int id) {
        this.jobId = id;
    }

    public int getJobId() {
        return jobId;
    }
}
