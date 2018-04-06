import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Implements BM25 retrieval model
 * Produces a ranked list of documents(using BM25 model) for a given query or a list of queries
 **/
public class BM25Model {

// private static HashMap<String, Integer> documentWordTotal;
    //private static HashMap<String, List<Posting>> invertedIndex;
    //private static List<RelevanceInfo> relevantDocuments;
    //private static Query queryObj;

    private static String unigramIndexPath = "E:\\1st - Career\\NEU_start\\@@Technical\\2 - sem\\IR\\assign4\\test\\invertedIndex\\invertedIndex_for_1-grams.txt";
    private static String queryFilePath = "E:\\1st - Career\\NEU_start\\@@Technical\\2 - sem\\IR\\assign4\\test\\queries1.txt";

    // === sample INDEX FILE PATH ====
    // E:\1st - Career\NEU_start\@@Technical\2 - sem\IR\assign4\test\invertedIndex\invertedIndex_for_1-grams.txt
    // === sample QUERY FILE PATH ====
    // E:\1st - Career\NEU_start\@@Technical\2 - sem\IR\assign4\test\queries1.txt

    public static void main(String[] args) throws IOException {

        /**
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("Enter the FULL path for the unigram inverted index: (e.g. /Usr/unigram_index.txt or c:\\temp\\unigram_index.txt)");
        String unigramIndexPath = br.readLine();

        final Path indexDir = Paths.get(unigramIndexPath);
        if (!Files.isReadable(indexDir)) {
            System.out.println("Document directory '" + indexDir.toAbsolutePath() + "' does not exist or is not readable, please check the path");
            System.exit(1);
        }
        if (!new File(unigramIndexPath).isFile() && (new File(unigramIndexPath).isDirectory()) ){
            System.out.println("Index file not found at the specified path.");
            System.exit(1);
        }

        System.out.println("Enter the FULL path for the query file: (e.g. /Usr/query.txt or c:\\temp\\query.txt)");
        String queryFilePath = br.readLine();

        final Path queryDir = Paths.get(queryFilePath);
        if (!Files.isReadable(queryDir)) {
            System.out.println("Document directory '" + queryDir.toAbsolutePath() + "' \ndoes not exist or is not readable, please check the path");
            System.exit(1);
        }
        if (!new File(queryFilePath).isFile() && (new File(queryFilePath).isDirectory()) ) {
            System.out.println("Query file not found at the specified path.");
            System.exit(1);
        }

        */

        //if (!checkQueryFileFormat()){
        if (!checkQueryFileFormat()){
            System.out.println();
            System.out.println("Query file is not in proper format.");
            System.out.println("Query file format: Use stopped, case folded queries");
            System.out.println("------------------------------");
            System.out.println("query_id    query_text");
            System.out.println("1           dark eclipse moon");
            System.out.println("2           forecast models");
            System.out.println("------------------------------");
            System.out.println("NOTE: Use stopped, case folded queries");
            System.exit(1);
        }

        else{
            processQueryList("E:\\1st - Career\\NEU_start\\@@Technical\\2 - sem\\IR\\assign4\\test\\queries1.txt");
            //processQueryList(queryFilePath);
        }
    }


    static boolean checkQueryFileFormat() throws IOException {

        // Open the file
        FileInputStream fstream = null;
        try {
            fstream = new FileInputStream(queryFilePath);
            BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
            String line = null;
            String words[];
            int lineCount =0;

            //Read File Line By Line
            while ((line = br.readLine())!= null)   {
                lineCount++;
                // Print the content on the console
                line = line.trim().replace("\t", " ");
                line = line.replaceAll(" +", " ");
                words = line.split(" ");

                if(lineCount==1){
                    if(line.toLowerCase().contains("query_id") && line.toLowerCase().contains("query_text"))
                        continue;
                    else
                        return false;
                }

                else{
                    if (words.length>1 && words[0].matches("[0-9]+") && words[1].matches("[a-zA-Z0-9]+")){
                        //System.out.println(words[0]+" "+words[1]);
                        continue;
                    }
                    else if(words.length == 1 && words[0].equals("")){
                        //System.out.println("Blank line "+" len : "+words.length);
                        return false;
                    }

                    else {
                        //System.out.println(words[0]+" len : "+words.length);
                        return false;
                    }
                }
            }
            //Close the input stream
            br.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return true;
    }



    static void processQueryList(String queryFilePath) throws IOException {

        // Open the file
        FileInputStream fstream = null;
        try {
            fstream = new FileInputStream(queryFilePath);
            BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
            String line = null;
            String words[];
            int lineCount =0;

            //Read File Line By Line
            while ((line = br.readLine())!= null)   {
                lineCount++;
                if(lineCount>1)
                    processQuery(line);
            }
            //Close the input stream
            br.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    static void processQuery(String queryLine) throws IOException {



        // ============================================================== //
        // Process Each Query
        // ============================================================== //

        // 1- Fetch all inverted lists corresponding to terms in the query
        // 2- Compute BM25 score for documents in the lists. Make a score list for documents in the	inverted lists.
        // 3- Accumulate	scores	for	each	term	in	a	query	on	the	score	list.
        // 4- Assume	that	no	relevance	information	is	available.
        // 5- For	parameters,	use	k1=1.2,	b=0.75,	k2=100.
        // 6- Sort	the	documents	by	the	BM25	scores

        //List<String> queryTerms = getQueryTerms(queryLine);

        Query1 q = new Query1(getQueryID(queryLine),getQueryTerms(queryLine));
        System.out.print(getQueryID(queryLine)+ "   ");
        System.out.println(q.toString());

        // ============================================================== //
        // Add relevant documents for a query
        // ============================================================== //

        List<RelevantDoc> relevantDocsForQuery = getRelevantDocs(q);
        q.setRelevantDocuments(relevantDocsForQuery);

        for(RelevantDoc r: q.getRelevantDocuments()){
            System.out.println(r.toString());
        }

        // ================================================================ //
        // Calculate r relevant documents for a query and populate results
        // ================================================================ //

        List<Result> BM25Results = getRetrievalResults(q);
        q.setQueryResults(BM25Results);

        // ============================================================================= //
        // 6- Sort and rank the documents by the BM25 scores and write them to directory
        // ============================================================================= //

        Collections.sort(BM25Results, (Result a, Result b) -> Double.compare(b.getBM25Score(), a.getBM25Score()));

        // Now add Rank and print

        for (int i = 0; i < BM25Results.size(); i++){
            BM25Results.get(i).setRank(i+1);
            if(i<100){
                System.out.println(BM25Results.get(i).toString());
            }
        }


        // ============================================================================= //
        // Write results for a query to "queryResults" directory
        // ============================================================================= //




    }

    private static List<Result> getRetrievalResults(Query1 q) {

        List<Result> results = new ArrayList<>();
        Result1 r = null;
        List<RelevantDoc> docList = q.getRelevantDocuments();

        for(RelevantDoc rd : docList){
            r = new Result1(q.queryID(),rd.documentID());
        }

        return results;
    }

    private static List<RelevantDoc> getRelevantDocs(Query q) throws IOException {

        List<RelevantDoc> rDocs = new ArrayList<>();
        RelevantDoc d;
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
                    for(String s: q.queryTerms()){
                        if(s.equals(term)){
                            // System.out.println("======= " +term + "================");
                            //System.out.println(invertedList);
                           Matcher m = Pattern.compile("\\[([^]]+)\\]").matcher(invertedList);
                            while(m.find()){
                               //System.out.println(m.group());
                               docID = m.group().split(":")[0].replace("["," ").trim();
                               tf = m.group().split(":")[1].replace("]"," ").trim();
                               //System.out.println(docID+" "+tf);
                               d = new RelevantDoc1(s,docID,Integer.parseInt(tf));
                               rDocs.add(d);
                            }


                        }
                    }
                }

            }

            //Close the input stream
            br.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        // Remove duplicated relevant docs
        Set<RelevantDoc> hs = new HashSet<>();
        hs.addAll( rDocs);
        rDocs.clear();
        rDocs.addAll(hs);
        return rDocs;
    }


    static List<String> getQueryTerms(String query) {

        List<String> queryTerms = new ArrayList<>();
        String terms[];
        query = query.trim().replace("\t", " ");
        query = query.replaceAll(" +", " ");
        terms = query.split(" ");
        int position = 1;
        for(String t : terms){
            if(position!=1) {
                queryTerms.add(t);
            }
            position++;
        }
        return queryTerms;
    }

    static int getQueryID(String query) {

        String wordInLine[];
        query = query.trim().replace("\t", " ");
        query = query.replaceAll(" +", " ");
        wordInLine= query.split(" ");
        return Integer.parseInt(wordInLine[0]);
    }


}
