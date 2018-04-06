import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DocLength {

    private static String docLengthFile = "..\\..\\unigramInvertedIndex\\DocLengths_for_1-grams.txt";
    static double getAvgDocLength() throws IOException {

        double allDocLength = 0;
        int totalDocs = 0;

        // Read inverted index for terms
        // Open the file
        FileInputStream fstream = null;
        try {
            fstream = new FileInputStream(docLengthFile);
            BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
            String line = null;
            int lineCount = 0;

            //Read File Line By Line
            while ((line = br.readLine())!= null) {
                lineCount+=1;
                if(lineCount>2){

                    Matcher m = Pattern.compile(" [0-9]+").matcher(line);
                    while(m.find()){
                        //System.out.println(allDocLength);
                        //System.out.println(m.group().trim().toString());
                        allDocLength+= Double.parseDouble(m.group().trim().toString());
                    }

                }

            }
            totalDocs = lineCount-3;
           // System.out.println("Total docs: "+totalDocs);

                    //Close the input stream
                    br.close();
                    } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    }

       //System.out.println("Avg doc length: "+ (allDocLength/totalDocs));
        return ((double)(allDocLength))/totalDocs;
    }


    static double getDocLength(String docID) throws IOException {

        double allDocLength = 0;
        int totalDocs = 0;
        // Read inverted index for terms
        // Open the file
        FileInputStream fstream = null;
        try {
            fstream = new FileInputStream(docLengthFile);
            BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
            String line = null;
            int lineCount = 0;

            //Read File Line By Line
            while ((line = br.readLine())!= null) {
                lineCount += 1;
                if (lineCount > 2) {
                    //System.out.println(line.split(" ")[0].trim());
                    if (line.split(" ")[0].trim().toLowerCase().equals(docID.toLowerCase())) {
                        Matcher m = Pattern.compile(" [0-9]+").matcher(line);
                        while (m.find()) {
                            //System.out.println(docID+" "+m.group().trim().toString());
                            return Double.parseDouble(m.group().trim().toString());
                        }
                    }
                }
            }
            //System.out.println("Total docs: "+totalDocs);
            //Close the input stream
            br.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return 0.0;
    }

/**
    // TEST Doc Length

    public static void main(String[] args) throws IOException {

        double d = getAvgDocLength();
        d = getDocLength("human");
    }
*/

}
