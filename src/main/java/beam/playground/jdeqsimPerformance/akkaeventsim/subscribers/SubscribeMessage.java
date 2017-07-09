package beam.playground.jdeqsimPerformance.akkaeventsim.subscribers;

import akka.actor.ActorRef;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by asif on 7/9/2017.
 */
public class SubscribeMessage {

    List<ActorRef> subscribers = new ArrayList<>();
    List<String> eventTypes = new ArrayList<>();

    public SubscribeMessage(){

    }

    public SubscribeMessage(List<ActorRef> subscribers, List<String> eventTypes){

        this.subscribers = subscribers;
        this.eventTypes = eventTypes;
    }

    public List<ActorRef> getSubscribers() {
        return subscribers;
    }

    public void setSubscribers(List<ActorRef> subscribers) {
        this.subscribers = subscribers;
    }

    public List<String> getEventTypes() {
        return eventTypes;
    }

    public void setEventTypes(List<String> eventTypes) {
        this.eventTypes = eventTypes;
    }
}


