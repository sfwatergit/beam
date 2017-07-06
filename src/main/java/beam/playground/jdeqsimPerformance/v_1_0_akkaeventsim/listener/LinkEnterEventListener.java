package beam.playground.jdeqsimPerformance.v_1_0_akkaeventsim.listener;

import org.matsim.api.core.v01.events.LinkEnterEvent;

import java.util.List;

/**
 * Created by salma_000 on 7/6/2017.
 */
public class LinkEnterEventListener implements ILinkEnterEventListener {
    private static int callBackCount = 0;

    @Override
    public void linkEnterEventCallBack(List<LinkEnterEvent> eventList) {
        callBackCount++;
        System.out.println("Link Enter Event" + callBackCount);
        /*for(LinkEnterEvent event:eventList){
            System.out.println(event.toString());
        }*/
    }
}
