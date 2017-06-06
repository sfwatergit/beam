package beam.playground.jdeqsimPerformance;

import org.matsim.api.core.v01.events.Event;
import org.matsim.contrib.util.CompactCSVWriter;
import org.matsim.core.utils.io.IOUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.util.*;

/**
 * Created by asif on 5/30/2017.
 */

public class EventCSVWriter {

	CompactCSVWriter csvWriter;
	boolean isHeaderWritten = false;
	Map<Integer, Map<String, Integer>> bins = new HashMap<>();
	Map<String, Map<Integer, Integer>> linkData = new HashMap<>();

    EventCSVWriter(){
		long time = Calendar.getInstance().getTimeInMillis();
		File f = new File("C:\\ns\\output\\testfile_" + time + ".csv");
		char separator = ',';
		BufferedWriter writer = IOUtils.getBufferedWriter(f.getPath());

		this.csvWriter = new CompactCSVWriter(writer, separator);
	}

	public Map<Integer, Map<String, Integer>> getBins(){
    	return bins;
	}

	public void logEvent(Event event){

    	if(!isHeaderWritten) {

			System.out.println("Writing header");
			List<String> tokens = new ArrayList<>();

			for (String attr : event.getAttributes().keySet()) {
				tokens.add(attr);
			}

			String[] tokensA = new String[tokens.size()];
			tokens.toArray(tokensA);

			csvWriter.writeNext(tokensA);
			csvWriter.flush();
			//csvWriter.close();
			isHeaderWritten = true;
		}

		//System.out.println("Logging events -> " + event + " " + event.getAttributes().values());
		List<String> tokens = new ArrayList<>();

		for (String attr : event.getAttributes().values()) {
			tokens.add(attr);
		}

		String[] tokensA = new String[tokens.size()];
		tokens.toArray(tokensA);

		csvWriter.writeNext(tokensA);
		csvWriter.flush();

		//processEvent(event);
		processLinkEvent(event);
			//csvWriter.close();
    }


    public void processEvent(Event event){

		int binSize = 300;
		Double time = event.getTime(); // time in seconds
		Double _binNumber = (time/binSize);
		int binNumber = _binNumber.intValue();

		String link = event.getAttributes().get("link");

		System.out.println("Link -> " + link + ", Bin Number -> " + binNumber);
		Map<String, Integer> binData = bins.get(binNumber);
		System.out.println("binData -> " + binData);

		if(binData == null){

			binData = new HashMap<>();
			binData.put(link, 1);
		}else{
			Integer linkCounter = binData.get(link);
			if(linkCounter == null){
				binData.put(link, 1);
			}else{
				binData.put(link, linkCounter + 1);
			}
		}
		bins.put(binNumber, binData);
	}

	public Integer getBinNumber(Event event){
		int binSize = 300;
		Double time = event.getTime(); // time in seconds
		Double _binNumber = (time/binSize);
		int binNumber = _binNumber.intValue();

		return binNumber;
	}

	public void processLinkEvent(Event event){

		String link = event.getAttributes().get("link");
		Map<Integer, Integer> _linkData = linkData.get(link);

		Integer binNumber = getBinNumber(event);

		if(_linkData == null){

			_linkData = new HashMap<>();
			_linkData.put(binNumber, 1);
		}else{
			Integer binCounter = _linkData.get(binNumber);
			if(binCounter == null){
				_linkData.put(binNumber, 1);
			}else {
				_linkData.put(binNumber, binCounter + 1);
			}
		}
    	linkData.put(link, _linkData);
	}

	public void printBins(){
    	SortedSet<Integer> keys = new TreeSet<Integer>(bins.keySet());
    	for(Integer key: keys){
    		Map<String, Integer> binData = bins.get(key);
    		SortedSet<String> binDataKeys = new TreeSet<String>(binData.keySet());
    		for(String binDataKey: binDataKeys){
    			System.out.println("BIN -> " + key + ", LINK -> " + binDataKey + ", COUNT -> " + binData.get(binDataKey));
			}
		}

	}

	public void printLinkData(){
		SortedSet<String> keys = new TreeSet<String>(linkData.keySet());
		for(String key: keys){
			Map<Integer, Integer> _linkBinData = linkData.get(key);
			SortedSet<Integer> binDataKeys = new TreeSet<Integer>(_linkBinData.keySet());
			for(Integer binDataKey: binDataKeys){
				System.out.println("Link -> " + key + ", BIN -> " + binDataKey + ", COUNT -> " + _linkBinData.get(binDataKey));
			}
		}
	}

	public static void main(String[] args) {

		long time = Calendar.getInstance().getTimeInMillis();
		File f = new File("C:\\ns\\output\\testfile_" + time + ".csv");
		CompactCSVWriter writer = new CompactCSVWriter(IOUtils.getBufferedWriter(f.getPath()), ',');

		List<String> tokens = new ArrayList<>();
		tokens.add("a");
		tokens.add("b");
		tokens.add("c");

		String[] tokensA = new String[tokens.size()];
		tokens.toArray(tokensA);

		writer.writeNext("time", "id", "x", "y");
		writer.writeNext(tokensA);
		writer.writeNext("Hello");
		writer.close();
	}
}


