package beam.playground.jdeqsimPerformance;

import org.matsim.api.core.v01.events.LinkEnterEvent;
import org.matsim.contrib.util.CompactCSVWriter;
import org.matsim.core.utils.io.IOUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by asif on 5/30/2017.
 */

public class EventCSVWriter {

	CompactCSVWriter csvWriter;
	boolean isHeaderWritten = false;

    EventCSVWriter(){
		long time = Calendar.getInstance().getTimeInMillis();
		File f = new File("C:\\ns\\output\\testfile_" + time + ".csv");
		char separator = ',';
		BufferedWriter writer = IOUtils.getBufferedWriter(f.getPath());

		this.csvWriter = new CompactCSVWriter(writer, separator);
	}

	public void logEvent(LinkEnterEvent event){

    	if(!isHeaderWritten) {

			System.out.println("Writting header");
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

		System.out.println("Logging events -> " + event + " " + event.getAttributes().values());
		List<String> tokens = new ArrayList<>();

		for (String attr : event.getAttributes().values()) {
			tokens.add(attr);
		}

		String[] tokensA = new String[tokens.size()];
		tokens.toArray(tokensA);

		csvWriter.writeNext(tokensA);
		csvWriter.flush();
			//csvWriter.close();

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


