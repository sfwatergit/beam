package beam.agentsim.events;

public interface RefuelEventAttrs {

    String EVENT_TYPE= "RefuelEvent";
    String ATTRIBUTE_VEHICLE_ID = "vehicle";
    String ATTRIBUTE_ENERGY_DELIVERED = "fuel";
    String ATTRIBUTE_SESSION_DURATION = "duration";
    String ATTRIBUTE_COST = "cost";
    String ATTRIBUTE_LOCATION_X = "location.x";
    String ATTRIBUTE_LOCATION_Y = "location.y";
    String ATTRIBUTE_PARKING_TYPE = "parking_type";
    String ATTRIBUTE_PRICING_MODEL = "pricing_model";
    String ATTRIBUTE_CHARGING_TYPE = "charging_type";
    String ATTRIBUTE_PARKING_TAZ = "parking_taz";

}
