package beam.playground.jdeqsim.akkaeventsampling;

import akka.actor.UntypedActor;
import beam.playground.jdeqsim.akkaeventsampling.messages.ProcessingActorRequest;
import beam.playground.jdeqsimPerformance.EventCSVWriter;
import org.apache.log4j.Logger;
import org.matsim.api.core.v01.events.Event;

import java.util.Collections;
import java.util.List;

public class ProcessActor extends UntypedActor {
    public static final String ACTOR_NAME = "Processing_Actor";
    private static final Logger log = Logger.getLogger(ProcessActor.class);

    EventCSVWriter csvWriter = EventCSVWriter.getInstance();

    @Override
    public void onReceive(Object message) throws Throwable {
        if (message instanceof ProcessingActorRequest) {
            List<Event> eventList = ((ProcessingActorRequest) message).getEventList();
            Collections.sort(eventList, new EventTimeComparator());
            log.debug(eventList.get(0).getTime());
            log.debug(eventList.get(eventList.size() - 1).getTime());

            logEventList(eventList);
        }else if(message instanceof String){

            String msg = (String)message;
            if(msg == "SIM_COMPLETED"){
                log.debug("SIM_COMPLETED received");
                if(EventCSVWriter.isCsvPrinted == false) {
                    log.debug("Printing CSV");
                    csvWriter.printLinkDataToCSV();
                }else{
                    log.debug("CSV already printed");
                }
            }
        } else {
            unhandled(message);
        }
    }

    public void logEventList(List<Event> eventList){

        for(Event event : eventList){
            log.debug("Event -> " + event.getTime() + ", Attributes -> " + event.getAttributes());
            csvWriter.logEvent(event);
        }
    }
}
