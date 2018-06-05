package beam.analysis.plots;

import beam.agentsim.events.ModeChoiceEvent;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.general.Dataset;
import org.jfree.data.general.DatasetUtilities;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.events.Event;
import org.matsim.api.core.v01.events.PersonEntersVehicleEvent;
import org.matsim.api.core.v01.population.Person;
import org.matsim.core.controler.events.IterationEndsEvent;
import scala.math.BigDecimal;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

/**
 * @author abid
 */
public class RideHailingWaitingSingleStats implements IGraphStats {

    private static final String graphTitle = "Ride Hail Waiting Time";
    private static final String xAxisTitle = "Hour";
    private static final String yAxisTitle = "Waiting Time (seconds)";
    private static final String fileName = "RideHailWaitingSingleStats";

    private double lastMaximumTime = 0;
    private double NUMBER_OF_CATEGORIES = 6.0;

    private Map<String, Event> rideHailingWaiting = new HashMap<>();

    private Map<Integer, Double> hoursTimesMap = new HashMap<>();

    @Override
    public void resetStats() {
        lastMaximumTime = 0;

        rideHailingWaiting.clear();
        hoursTimesMap.clear();
    }

    @Override
    public void processStats(Event event) {

        if (event instanceof ModeChoiceEvent){

            String mode = event.getAttributes().get("mode");
            if(mode.equalsIgnoreCase("ride_hailing")) {

                ModeChoiceEvent modeChoiceEvent = (ModeChoiceEvent) event;
                Id<Person> personId = modeChoiceEvent.getPersonId();
                rideHailingWaiting.put(personId.toString(), event);
            }
        } else if(event instanceof PersonEntersVehicleEvent) {

            PersonEntersVehicleEvent personEntersVehicleEvent = (PersonEntersVehicleEvent)event;
            Id<Person> personId = personEntersVehicleEvent.getPersonId();
            String _personId = personId.toString();

            if(rideHailingWaiting.containsKey(personId.toString())) {

                ModeChoiceEvent modeChoiceEvent = (ModeChoiceEvent) rideHailingWaiting.get(_personId);
                double difference = personEntersVehicleEvent.getTime() - modeChoiceEvent.getTime();
                processRideHailingWaitingTimes(modeChoiceEvent, difference);

                // Remove the personId from the list of ModeChoiceEvent
                rideHailingWaiting.remove(_personId);
            }
        }
    }

    @Override
    public void createGraph(IterationEndsEvent event) throws IOException {
        double[][] data = new double[1][24];
        for(Integer key : hoursTimesMap.keySet()){
            data[0][key] = hoursTimesMap.get(key);
        }
        CategoryDataset dataset = DatasetUtilities.createCategoryDataset("","",data);
        if (dataset != null)
            createModesFrequencyGraph(dataset, event.getIteration());

        writeToCSV(event.getIteration(), hoursTimesMap);
    }

    @Override
    public void createGraph(IterationEndsEvent event, String graphType) throws IOException {
        throw new IOException("Not implemented");
    }



    private void processRideHailingWaitingTimes(Event event, double waitingTime) {
        int hour = GraphsStatsAgentSimEventsListener.getEventHour(event.getTime());

        waitingTime = waitingTime/60;

        if (waitingTime > lastMaximumTime) {
            lastMaximumTime = waitingTime;
        }

        Double timeList = hoursTimesMap.get(hour);
        if (timeList == null) {
            timeList = waitingTime;
        }
        else {
            timeList += waitingTime;
        }
        hoursTimesMap.put(hour, timeList);
    }


    private void createModesFrequencyGraph(CategoryDataset dataset, int iterationNumber) throws IOException {

        boolean legend = false;
        final JFreeChart chart = GraphUtils.createStackedBarChartWithDefaultSettings(dataset, graphTitle, xAxisTitle, yAxisTitle, fileName + ".png", legend);

        GraphUtils.setColour(chart, 1);
        // Writing graph to image file
        String graphImageFile = GraphsStatsAgentSimEventsListener.CONTROLLER_IO.getIterationFilename(iterationNumber, fileName + ".png");
        GraphUtils.saveJFreeChartAsPNG(chart, graphImageFile, GraphsStatsAgentSimEventsListener.GRAPH_WIDTH, GraphsStatsAgentSimEventsListener.GRAPH_HEIGHT);
    }



    private void writeToCSV(int iterationNumber, Map<Integer, Double> hourModeFrequency) throws IOException {
        String csvFileName = GraphsStatsAgentSimEventsListener.CONTROLLER_IO.getIterationFilename(iterationNumber, fileName + ".csv");
        BufferedWriter out = null;
        try {
            out = new BufferedWriter(new FileWriter(new File(csvFileName)));
            String heading = "WaitingTime\\Hour";
            for (int hours = 1; hours <= 24; hours++) {
                heading += "," + hours;
            }
            out.write(heading);
            out.newLine();
            String line;
            for (int i = 0; i < 24; i++) {
                Double inner = hourModeFrequency.get(i);
                line = (inner == null ) ? ",0" : "," + Math.round(inner*100.0)/100.0;
                out.write(line);
            }
            out.flush();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (out != null) {
                out.close();
            }
        }
    }

}