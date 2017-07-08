package beam.playground.jdeqsimPerformance.simpleeventsim;

/**
 * Created by asif on 7/7/2017.
 */
public class SimulationTimes {

    Long producerStartTime;
    Long producerEndTime;
    Long noOfEvents;


    public SimulationTimes(Long producerStartTime, Long producerEndTime, Long noOfEvents) {
        this.producerStartTime = producerStartTime;
        this.producerEndTime = producerEndTime;
        this.noOfEvents = noOfEvents;
    }

    public Long getProducerStartTime() {
        return producerStartTime;
    }

    public void setProducerStartTime(Long producerStartTime) {
        this.producerStartTime = producerStartTime;
    }

    public Long getProducerEndTime() {
        return producerEndTime;
    }

    public void setProducerEndTime(Long producerEndTime) {
        this.producerEndTime = producerEndTime;
    }

    public Long getNoOfEvents() {
        return noOfEvents;
    }

    public void setNoOfEvents(Long noOfEvents) {
        this.noOfEvents = noOfEvents;
    }

    @Override
    public String toString() {
        return "Simulation Times [Producer Start Time " + getProducerStartTime() +
                ", Producer End Time " + getProducerEndTime() +
                ", No of Generated Events " + getNoOfEvents() + "]";
    }
}
