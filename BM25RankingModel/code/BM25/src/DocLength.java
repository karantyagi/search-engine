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
           System.out.println("Total docs: "+totalDocs);

                    //Close the input stream
                    br.close();
                    } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    }

       System.out.println("Avg doc length: "+ (allDocLength/totalDocs));
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
            //Close the input stream
            br.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return 0.0;
    }
/**
    //TEST Doc Length

    public static void main(String[] args) throws IOException {

        double d = getAvgDocLength();
        d = getDocLength("Russia");

        double r = 0.0;
        double R = 0.0;

        double k1 = 1.2;
        double k2 = 100;
        double b = 0.75;
        double K = computeK(k1,b,"Russia");

        double f = 15; // term frequency of term in given doc
        double qf = 1;
        System.out.println("TF(fi)  : "+f);
        System.out.println("QTF(qfi): "+qf);

        double ni = 40000;
        double N = 500000; // Number of Documents
        System.out.println("Doc Freq:" +ni);

        double firstN = (r+0.5) / (R- r+0.5);
        double firstD = (ni-r +0.5) / (N-ni-R+r+0.5);

        double secondN = (k1 + 1.0)*f;
        double secondD = K+f;

        double thirdN = (k2+1)*qf;
        double thirdD = k2+qf;

        double score = (Math.log(firstN/firstD))*(secondN/secondD)*(thirdN/thirdD);

        System.out.println("first N : " + firstN);
        System.out.println("first D : " + firstD);
        System.out.println("first   : " + Math.log(firstN/firstD));
        System.out.println("secondN : " + secondN);
        System.out.println("secondD : " + secondD);
        System.out.println("thirdN  : " + thirdN);
        System.out.println("thirdD  : " + thirdD);
        System.out.println("Score   : " + score);


    }
    private static double computeK(double k1, double b, String docID) throws IOException {
        //System.out.println(avdl);
        System.out.println("Doc ID  : "+docID);
        System.out.println("Doc Len : "+ getDocLength(docID));
        double K = k1 * ((1 - b) + b*(0.9));
        System.out.println("K       : "+K);
        return K;
    }
*/

}
