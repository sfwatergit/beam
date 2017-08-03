package beam.playground.jdeqsimPerformance.akkaeventsim.messages;

/**
 * Created by salma_000 on 8/2/2017.
 */

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "event")
public class EventSerializable {

    @XmlAttribute
    double time;
    @XmlAttribute
    String type;
    @XmlAttribute
    String vehicle;
    @XmlAttribute
    String link;

    public double getTime() {
        return time;
    }

    public String getType() {
        return type;
    }

    public String getVehicle() {
        return vehicle;
    }

    public String getLink() {
        return link;
    }
}


