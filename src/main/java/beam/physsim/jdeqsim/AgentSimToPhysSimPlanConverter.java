package beam.physsim.jdeqsim;

import beam.agentsim.events.PathTraversalEvent;
import beam.router.Modes;
import beam.router.RoutingModel;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.events.Event;
import org.matsim.api.core.v01.events.PersonEntersVehicleEvent;
import org.matsim.api.core.v01.events.PersonLeavesVehicleEvent;
import org.matsim.api.core.v01.population.*;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.events.handler.BasicEventHandler;
import org.matsim.core.scenario.ScenarioUtils;
import org.matsim.vehicles.Vehicle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by asif on 8/18/2017.
 */
public class AgentSimToPhysSimPlanConverter implements BasicEventHandler {

    Scenario scenario;
    Population population;
    PopulationFactory populationFactory;
    Map<Id<Vehicle>, Id<Person>> vehiclePersonMap = new HashMap<>();

    Map<Long, Person> persons = new HashMap<>();

    List<PathTraversalEvent> pathTraversalEventList = new ArrayList<>();

    public AgentSimToPhysSimPlanConverter(){

        // Is this factory connected to main factory loaded in BeamSim or a new factory
        scenario = ScenarioUtils.createScenario(ConfigUtils.createConfig());
        population = scenario.getPopulation();
        populationFactory = scenario.getPopulation().getFactory();

    }

    @Override
    public void reset(int iteration) {



        System.out.println(AgentSimToPhysSimPlanConverter.class.getName() + " -> ITERATION -> " + iteration);
        // send plans to physsim

        // we have to get physsim reference from registry for example
        // either for now have physsim as sub package in this same project called singlecpu
        System.out.println("pathTraversalEventList -> " + pathTraversalEventList.toString());
        System.out.println(getClass().getName() +  " -> Persons -> " + population.getPersons().toString());
        System.out.println(getClass().getName() +  " -> VehiclePersonMap -> " + vehiclePersonMap.toString());
    }

    @Override
    public void handleEvent(Event event) {

        // Logic will be like the below
        /*
        Scenario ->
        population from scratch(empty) ->
        for every person we will have a hashmap in population ->
                we will check existing person and will add the activity and leg/routes to it or we will create new
                //////////////////////



                a) Any event handled will fall within a plan
        b) If a plan already exists corresponding to an event, use that plan to gather activity and leg information
        from that event

        1. Create Person
        2. Create an activity from the event
        3. Create a leg from the event
        4. Check if a plan already exists for the person from the event
        4a. Use the existing plan
        4b. Otherwise create the new plan and use for future events for that person too
        */

        // Load the network which we have created today beamville.xml
        // change the interface of jdeqsim, instead of scenario it will expect
        // a network
        // a collection of persons
        // a getActivityDurationInterpretation
        //

        // whenenver we initialize jdeqsim we will need these three things all this info
        // configgroup
        /////////////////////////


        /*double time = event.getTime();
        String eventType = event.getEventType();
        long personId = Long.parseLong(event.getAttributes().get("person"));
        String link = event.getAttributes().get("link");
        String activityType = event.getAttributes().get("actType");*/

        System.out.println(AgentSimToPhysSimPlanConverter.class.getName() + " -> Event -> " + event.toString());

        if(event instanceof PersonEntersVehicleEvent){

            // add person and vehicle to person vehicle map
            PersonEntersVehicleEvent pevEvent = (PersonEntersVehicleEvent)event;
            vehiclePersonMap.put(pevEvent.getVehicleId(), pevEvent.getPersonId());
            System.out.println(getClass().getName() + "- " + PersonEntersVehicleEvent.class.getName() +  " -> VehiclePersonMap -> " + vehiclePersonMap.toString());
        }else if(event instanceof PersonLeavesVehicleEvent) {
            // remove person and vehicle from person vehicle map
            PersonLeavesVehicleEvent pevEvent = (PersonLeavesVehicleEvent)event;
            Id<Person> personId = vehiclePersonMap.get(pevEvent.getVehicleId());
            if(personId != null && pevEvent.getPersonId() == personId){
                vehiclePersonMap.remove(pevEvent.getVehicleId());
            }
            System.out.println(getClass().getName() + "- " + PersonLeavesVehicleEvent.class.getName() + " -> VehiclePersonMap -> " + vehiclePersonMap.toString());
        }else if(event instanceof PathTraversalEvent){
            PathTraversalEvent ptEvent = (PathTraversalEvent)event;
            System.out.println(AgentSimToPhysSimPlanConverter.class.getName() + " -> PathTraversalEvent -> " + PathTraversalEvent.class.getName() + event.toString());

            String mode = ptEvent.getAttributes().get(ptEvent.ATTRIBUTE_MODE());

            if(mode != null && mode.equalsIgnoreCase("car")) {
                pathTraversalEventList.add(ptEvent);

                String links = ptEvent.getAttributes().get(ptEvent.ATTRIBUTE_LINK_IDS());
                String departureTime = ptEvent.getAttributes().get(ptEvent.ATTRIBUTE_DEPARTURE_TIME());
                String vehicleId = ptEvent.getAttributes().get(ptEvent.ATTRIBUTE_VEHICLE_ID());
                double time = ptEvent.getTime();
                String eventType = ptEvent.getEventType();


                RoutingModel.BeamLeg beamLeg = ptEvent.beamLeg();

                beamLeg.duration();
                beamLeg.endTime();
                Modes.BeamMode beamMode = beamLeg.mode();
                RoutingModel.BeamPath beamPath = beamLeg.travelPath();

                System.out.println("mode " + beamMode.matsimMode() + ", " + beamMode.otpMode() + ", " + beamMode.r5Mode());
                System.out.println("mode='" + beamMode.matsimMode() + ", dep_time='" + beamLeg.startTime());

                Id<Vehicle> vehicleId1 = Id.createVehicleId(vehicleId);
                Id<Person> personId = vehiclePersonMap.get(vehicleId1);

                if (personId != null) {
                    boolean personAlreadyExist = false;

                    if (population.getPersons() != null) {
                        personAlreadyExist = population.getPersons().containsKey(personId); // person already exists
                    }
                    Leg leg = populationFactory.createLeg(beamLeg.mode().matsimMode());
                    leg.setDepartureTime(beamLeg.startTime());
                    leg.setTravelTime(0);


                    Person person = null;
                    if (personAlreadyExist) {
                        person = population.getPersons().get(personId);
                        Plan plan = person.getSelectedPlan();
                        //plan.addActivity();
                        plan.addLeg(leg);

                    } else {
                        person = populationFactory.createPerson(personId);
                        Plan plan = populationFactory.createPlan();
                        //plan.addActivity();
                        plan.addLeg(leg);
                        plan.setPerson(person);
                        person.addPlan(plan);
                        person.setSelectedPlan(plan);
                        population.addPerson(person);
                    }
                }
            }
        }
    }
}

/*
1. We need plans for people using car mode
2. What kind of event
3. REFERENCE in this class

 */