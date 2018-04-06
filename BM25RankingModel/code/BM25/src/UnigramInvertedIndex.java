import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UnigramInvertedIndex {

    public static Map<String, List<Posting>> populateIndex(String unigramIndexPath) throws IOException {


        Map<String, List<Posting>> invertedIndex = new HashMap<>();
        List<RelevantDoc> rDocs = new ArrayList<>();

        final Path indexDir = Paths.get(unigramIndexPath);
        if (!Files.isReadable(indexDir)) {
            System.out.println("Document directory '" + indexDir.toAbsolutePath() + "' does not exist or is not readable, please check the path");
            System.exit(1);
        }
        if (!new File(unigramIndexPath).isFile() && (new File(unigramIndexPath).isDirectory()) ){
            System.out.println("Index file not found at the specified path.");
            System.exit(1);
        }

        List<Posting>  pList = new ArrayList<>();
        String term = null;
        String docID= null;
        String tf = null;

        // Read inverted index for terms
        // Open the file
        FileInputStream fstream = null;
        try {
            fstream = new FileInputStream(unigramIndexPath);
            BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
            String line = null;
            String invertedList = null;
            int lineCount =0;

            //Read File Line By Line
            while ((line = br.readLine())!= null) {
                lineCount++;
                invertedList = line.substring(line.indexOf("[")+1);
                // Print the content on the console
                if (lineCount > 2) {

                    term = line.split("\\[")[0].trim();
                    invertedIndex.put(term,new ArrayList<>());
                    Matcher m = Pattern.compile("\\[([^]]+)\\]").matcher(invertedList);
                    while(m.find()){
                        //System.out.println(m.group());
                        docID = m.group().split(":")[0].replace("["," ").trim();
                        tf = m.group().split(":")[1].replace("]"," ").trim();
                        //System.out.println(docID+" "+tf);
                        invertedIndex.get(term).add(new Posting1(docID, Integer.parseInt(tf)));
                    }
                }
            }

            //Close the input stream
            br.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        System.out.println("Unigram inverted index loaded successfully!");
        return invertedIndex;
    }

    public static Map<String, List<Posting>> addAllIndexLists(Map<String, List<Posting>> index, List<String> terms) throws IOException {

        Map<String, List<Posting>> invertedIndexForQuery = new HashMap<>();
        String term;
        for (Map.Entry<String, List<Posting>> entry : index.entrySet()) {
            term = entry.getKey().toString();
            for (String t : terms) {
                if (t.equals(term)) {
                    invertedIndexForQuery.put(term, entry.getValue());
                }
            }
        }

        System.out.println("All inverted lists(for all terms in each query) loaded successfully!");

        /**

        // Display -  invertedIndexForQuery
        System.out.println("-------- Display -  invertedIndexForQuery -------");
        for (Map.Entry<String, List<Posting>> entry : invertedIndexForQuery.entrySet()) {
            term = entry.getKey().toString();
            System.out.printf("%-30s  %s",term, entry.getValue().toString());
            System.out.println();
        }

        */
        return invertedIndexForQuery;
    }

/**
    public static void main(String[] args) throws IOException {

        System.out.println("TEST INDEX LOADING");
        String unigramIndexPath = "..\\..\\unigramInvertedIndex\\invertedIndex_for_1-grams.txt";
        Map<String, List<Posting>> testIndex= populateIndex(unigramIndexPath);

        Map<String, List<Posting>> treeMap = new TreeMap<>(testIndex);
        // TEST PRINT INDEX

        String term;
        List<Posting> postings;
        for (Map.Entry<String, List<Posting>> entry : treeMap.entrySet()) {
            term = entry.getKey().toString();
            System.out.printf("%-50s",term);
            postings = entry.getValue();
            for (Posting p : postings){
                System.out.print(p.toString()+" ");
            }
            System.out.println();
            }


    }
*/

}
