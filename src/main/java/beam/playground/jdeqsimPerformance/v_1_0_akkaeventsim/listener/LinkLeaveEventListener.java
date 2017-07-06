package beam.playground.jdeqsimPerformance.v_1_0_akkaeventsim.listener;

import org.matsim.api.core.v01.events.LinkLeaveEvent;

import java.util.List;

/**
 * Created by salma_000 on 7/6/2017.
 */
public class LinkLeaveEventListener implements ILinkLeaveEventListener {
    private static int callBackCount = 0;

    @Override
    public void linkLeaveEventCallBack(List<LinkLeaveEvent> eventList) {
        callBackCount++;
        System.out.println("Link Leave Event->>>" + callBackCount);
        /*for(LinkLeaveEvent event:eventList){
            System.out.println(event.toString());
        }*/
    }
}
