import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.*;

public class ReadCSV {
    String file = "/Users/siddharthgupta/All Stuff/Rutgers MSCS/Fall 2022/CS 512/Final Project/simplify/3.csv";
    public String[] readParticipantNamesFromCSV() {
        String delimiter = ",";
        String line;
        List<List<String>> lines = new ArrayList();
        String[] person;
        try (BufferedReader br =
                     new BufferedReader(new FileReader(file))) {
            while((line = br.readLine()) != null){
                List<String> values = Arrays.asList(line.split(delimiter));
                lines.add(values);
            }
            int j=0;
            person = new String[lines.get(0).size() - 5];
            for(int i=5;i<lines.get(0).size();i++){
                person[j] = lines.get(0).get(i);
                j++;
            }
        } catch (Exception e){
            person = null;
            System.out.println(e);
        }
        return person;
    }

    public void readTransactionsFromCSV(ArrayList<ArrayList<Double>> transac) {
        String delimiter = ",";
        String line;
        List<List<String>> lines = new ArrayList();
        try (BufferedReader br =
                     new BufferedReader(new FileReader(file))) {
            while((line = br.readLine()) != null){
                List<String> values = Arrays.asList(line.split(delimiter));
                lines.add(values);
            }
            for(int i=2;i<lines.size();i++){
                if(i==lines.size()-2 || i==lines.size()-1){
                    continue;
                }else {
                    ArrayList<Double> tr = new ArrayList<>();
                    for(int k=5;k<lines.get(i).size();k++){
                        tr.add(new Double(lines.get(i).get(k)));
                    }
                    transac.add(tr);
                }
            }
        } catch (Exception e){
            System.out.println(e);
        }
    }

}
