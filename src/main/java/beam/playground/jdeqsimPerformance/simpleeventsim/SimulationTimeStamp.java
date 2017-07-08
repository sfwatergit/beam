package beam.playground.jdeqsimPerformance.simpleeventsim;

/**
 * Created by asif on 7/7/2017.
 */
public class SimulationTimeStamp {

    Long time = 0l;

    SimulationTimeStamp(Long time){
        this.time = time;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }
}
