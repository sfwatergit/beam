package beam.playground.jdeqsimPerformance.compareEvents;

import com.opencsv.CSVReader;

import java.io.FileReader;
import java.io.IOException;
import java.util.*;

/**
 * Created by asif on 6/13/2017.
 */
public class CompareEventCSV {

    /*
    1. Load a CSV file
    2. Create a collection based on the CSV file
    */
    int binSize = 300;

    public Map<String, Map<String, Integer>> readCSVFile(String fileName){

        Map<String, Map<String, Integer>> linkDataMap = new HashMap<>();
        //String fileName1 = "C:\\ns\\output\\testfile_bin_1496875267302.csv";
        try {
            FileReader fileReader = new FileReader(fileName);
            CSVReader csvReader = new CSVReader(fileReader, ';');
            List<String[]> eventsData = csvReader.readAll();

            System.out.println("Size -> " + eventsData.size());
            System.out.println(eventsData);

            for(int j = 1; j < eventsData.size(); j++){

                String[] linkData = eventsData.get(j);
                String linkName = linkData[0];
                Map<String, Integer> linkEventCountsPerBin = new HashMap<>();

                int startBin = 0;
                int endBin = binSize;
                for(int i=1; i < linkData.length; i++){

                    String binLabel = "[" + startBin + "," + endBin + ")";

                    if(linkData[i].isEmpty())
                        linkEventCountsPerBin.put(binLabel, 0);
                    else
                        linkEventCountsPerBin.put(binLabel, Integer.parseInt(linkData[i]));

                    startBin = endBin;
                    endBin = startBin + binSize;
                }
                linkDataMap.put(linkName, linkEventCountsPerBin);
            }


            //System.out.println(linkDataMap);
        }catch (IOException ioe){
            ioe.printStackTrace();
        }
        return linkDataMap;
    }

    public void printData(Map<String, Map<Integer, Integer>> linkDataMap){
        for(String key: getSortedKeys(linkDataMap.keySet())){
            System.out.println("Link Name " + key);
            Map<Integer, Integer> linkData = linkDataMap.get(key);

            SortedSet<Integer> keySet2 = new TreeSet(linkData.keySet());
            for(Integer key2: keySet2){
                System.out.println(key2 + " = " + linkData.get(key2));
            }
        }
    }

    public List<String> getSortedKeys(Set<String> keySet){
        SortedSet<String> keys = new TreeSet<String>(keySet);

        List keys2 = new ArrayList(keySet);
        Collections.sort(keys2, new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                if (o1.length() == o2.length()){
                    return o1.compareTo(o2);
                }
                return o1.length() - o2.length();
            }
        });

        return keys2;
    }


    public void compareAndPrint(Map<String, Map<String, Integer>> data1, Map<String, Map<String, Integer>> data2){

        int countForDifferentData = 0;
        List<String> links = getSortedKeys(data1.keySet());

        for(String key: links){
            System.out.println("Link Name " + key);
            Map<String, Integer> linkData1 = data1.get(key);
            Map<String, Integer> linkData2 = data2.get(key);

            List<String> keySet2 = getSortedKeys(linkData1.keySet());

            for(String key2: keySet2){
                int count1 = linkData1.get(key2);
                int count2 = linkData2.get(key2);
                System.out.println(key2 + " -> (" + count1 + ", " + count2 + ")");
                if(count1 != count2)
                    countForDifferentData = countForDifferentData + 1;
            }

            System.out.println("FOUND " + countForDifferentData + " difference");
        }
    }


    public static void main(String[] args){

        CompareEventCSV compareEventCSV = new CompareEventCSV();

        String fileName1 = "C:\\ns\\output\\testfile_bin_1496875267302.csv";
        Map<String, Map<String, Integer>> dataForSimRun1 = compareEventCSV.readCSVFile(fileName1);

        String fileName2 = "C:\\ns\\output\\testfile_bin_1497906481020.csv";
        Map<String, Map<String, Integer>> dataForSimRun2 = compareEventCSV.readCSVFile(fileName2);

        String fileName3 = "C:\\ns\\output\\testfile_bin_1497907798783.csv";
        Map<String, Map<String, Integer>> dataForSimRun3 = compareEventCSV.readCSVFile(fileName3);

        /*compareEventCSV.printData(dataForSimRun1);
        compareEventCSV.printData(dataForSimRun2);*/
        //compareEventCSV.compareAndPrint(dataForSimRun1, dataForSimRun2);
        compareEventCSV.compareAndPrint(dataForSimRun1, dataForSimRun3);
    }
}
