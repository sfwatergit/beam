package beam.playground.jdeqsim.akkaeventsampling.messages;

import java.io.Serializable;


public class WorkerMessageRequest implements IRequest, Serializable {
    private LoadBalancerMessageRequest routerMessage;

    public WorkerMessageRequest(LoadBalancerMessageRequest routerMessage) {
        this.routerMessage = routerMessage;
    }

    public LoadBalancerMessageRequest getRouterMessage() {
        return routerMessage;
    }
}
