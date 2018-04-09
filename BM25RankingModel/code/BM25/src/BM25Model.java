import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Implements BM25 retrieval model
 * Produces a ranked list of documents(using BM25 model) for a given query or a list of queries
 **/
public class BM25Model {

    private static String unigramIndexPath = "..\\..\\unigramInvertedIndex\\invertedIndex_for_1-grams.txt";
    private static String queryFilePath;
    // private static String queryFilePath = "..\\..\\query\\queries.txt";

    private static Map<String, List<Posting>> invertedIndex;
    private static Map<String, List<Posting>> invertedListsForQuery;
    private static Map<String, Double> docLengths;
    private static double avdl;

    public static void main(String[] args) throws IOException {

        BufferedReader br1 = new BufferedReader(new InputStreamReader(System.in));
        /**
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

         */

        System.out.println("Enter the FULL path for the query file: (e.g. /Usr/query.txt or c:\\temp\\query.txt)");
        queryFilePath = br1.readLine();

        final Path queryDir = Paths.get(queryFilePath);
        if (!Files.isReadable(queryDir)) {
            System.out.println("Document directory '" + queryDir.toAbsolutePath() + "' \ndoes not exist or is not readable, please check the path");
            System.exit(1);
        }
        if (!new File(queryFilePath).isFile() && (new File(queryFilePath).isDirectory()) ) {
            System.out.println("Query file not found at the specified path.");
            System.exit(1);
        }

        // ============================================================== //
        // Check if Queries in the Query File are as per required format
        // ============================================================== //

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
            avdl = DocLength.getAvgDocLength();
            docLengths = new HashMap<>();
            invertedIndex = UnigramInvertedIndex.populateIndex(unigramIndexPath);
            invertedListsForQuery = new HashMap<>();
            List<String> allQueryTerms = getAllQueryTerms(queryFilePath);



            // Display all queries - all terms:
            System.out.println("ALL QUERY TERMS: ");
            for(String s: allQueryTerms){
                System.out.println(s);
            }
            System.out.println("-----------------");


            invertedListsForQuery = UnigramInvertedIndex.addAllIndexLists(invertedIndex, allQueryTerms);
            writeInvertedLists(invertedListsForQuery);
            processQueryList(queryFilePath);

        }

        br1.close();
    }

    private static void writeInvertedLists(Map<String, List<Posting>> invertedListsForQuery) throws IOException {

        String resultDir =  "..\\..\\retrievedResults";
        FileWriter fw;
        BufferedWriter bw;
        if (!new File(resultDir).isDirectory())
        {
            File dir = new File(resultDir);
            dir.mkdirs();
            System.out.println(resultDir+" created!");
        }

        // PUT IN INVERTED_INDEX_ LOOP
         String q = "query";
        fw = new FileWriter(resultDir + "\\" + q+"___.txt");
        bw = new BufferedWriter(fw);
        bw.append(" write");

        // PUT IN INVERTED INDEX LOOP


        bw.close();
        fw.close();

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
                    else {
                        return false;
                    }
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

    public static List<String> getAllQueryTerms(String queryFilePath) throws IOException{
        List<String> qTerms;
        List<String> allQueryTerms = new ArrayList<>();
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
                if(lineCount>1){
                    qTerms = getQueryTerms(line);
                    for(String s: qTerms){
                        allQueryTerms.add(s);
                    }
                }
            }
            //Close the input stream
            br.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return allQueryTerms;
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

        Query1 q = new Query1(getQueryID(queryLine),getQueryTerms(queryLine));
        System.out.printf("%s %-2s\t","QUERY_ID: ",getQueryID(queryLine));
        System.out.printf("%s %s  \n","QUERY:   ",q.toString());

        // ======================================================================= //
        // Add relevant documents for a query (after fetching all inverted lists
        // corresponding to terms in the query)
        // ======================================================================= //
        Set<String> relevantDocsForQuery = getRelevantDocs(q);
        q.setRelevantDocuments(relevantDocsForQuery);

        System.out.println("RELEVANT DOCS FOR QUERy : "+q.toString());
        List<String> relDocs = new ArrayList<>(q.getRelevantDocuments());
        Collections.sort(relDocs);
        for(String s: relDocs){
            System.out.printf("%70s %f\n",s,DocLength.getDocLength(s));

        }


        // ============================================================================ //
        // Calculate BM25 score for relevant documents for a query and populate results
        // ============================================================================ //
        Set<Result> BM25queryResults = getRetrievalResults(q);
        q.setQueryResults(BM25queryResults);

        // ============================================================================= //
        // 6- Sort and rank the documents by the BM25 scores and write them to directory
        // ============================================================================= //
        List<Result> BM25ResultList = new ArrayList<>(BM25queryResults);
        Collections.sort(BM25ResultList, (Result a, Result b) -> Double.compare(b.getBM25Score(), a.getBM25Score()));

        // ============================================================================================= //
        // Rank the sorted results and write top 100 results for a query to "retrievedResults" directory
        // ============================================================================================= //

        // Create 'retrievedResults' directory
        String resultDir =  "..\\..\\retrievedResults";
        if (!new File(resultDir).isDirectory())
            {
                File dir = new File(resultDir);
                dir.mkdirs();
                System.out.println(resultDir+" created!");
            }

        // Writing top 100 results
        System.out.println("---------------------------");
        System.out.println("      Top 100 results      ");
        System.out.println("---------------------------");
        FileWriter fw = new FileWriter(resultDir + "\\" + q.toString()+"_results.txt");
        BufferedWriter bw= new BufferedWriter(fw);

        Result r;
        for (int i = 0; i < BM25ResultList.size(); i++){
            r = BM25ResultList.get(i);
            r.setRank(i+1);
            if(i<100){

                bw.append(r.queryId()+" "+r.literal()+" "+r.documentID()+" "+r.getRank()+" "+r.getBM25Score()+" "+r.systemName()+"\r\n");
                System.out.printf("%-4d %4s %-80s  %-4d   %3.7f  %s \n",
                        r.queryId(),r.literal(),r.documentID(),r.getRank(),r.getBM25Score(),r.systemName());
                //System.out.println();

            }
        }
        bw.close();
        fw.close();
        System.out.println("Top 100 results written to file: "+q.toString()+"_results.txt");
        System.out.println("=======================================================================================================================");

    }

    private static Set<String> getRelevantDocs(Query q) throws IOException {

        //System.out.println("================= ADDING DOCS FOR : "+q.toString()+" ==============");
        //
        Set<String> rDocs = new HashSet<>();
        String relevantDocID;
        String term = null;
        String docID= null;
        int count = 0;
        List<Posting> postings;
        for (Map.Entry<String, List<Posting>> entry : invertedListsForQuery.entrySet()) {
            term = entry.getKey().toString();
            for(String s: q.queryTerms()){
                if(s.equals(term)) {
                    //System.out.println("------------------- FETCH FOR TERM: "+s+" -------------------");
                    postings = entry.getValue();
                    for (Posting p : postings){
                        docLengths.put(p.docID(),(double)DocLength.getDocLength(p.docID()));
                        rDocs.add(p.docID());
                        count++;
                        //System.out.printf("%-3d %s \n",count,p.docID());
                    }
                }

            }

        }
        //System.out.println(rDocs.size()+" relevant Docs Fetched!");
        System.out.printf("%-4d %s \n",rDocs.size()," relevant Docs Fetched!");
        //System.out.println("Relevant Doc List Size (original): "+rDocs.size());
        return rDocs;
    }

    private static Set<Result> getRetrievalResults(Query1 q) throws IOException {

        Set<Result> results = new HashSet<>();
        Result1 r;
        Set<String> docList = q.getRelevantDocuments();
        double count =1;
        //System.out.printf("\rRetrieving Results: %3.1f%% ",(count*100)/docList.size());

        for(String rd : q.getRelevantDocuments()){
            r = new Result1(q.queryID(),rd);
            r.setBM25Score(calculateDocBMScore(q,rd));
            results.add(r);
            count+=1;
            //System.out.printf("\rRetrieving Results: %3.1f %% ",(count*100)/docList.size());
        }
        //System.out.println();
        return results;
    }

    private static double calculateDocBMScore(Query1 q, String rdocID) throws IOException {
        double BMScore = 0.0;
        for(String queryTerm : q.queryTerms()){
            System.out.println("==============================");
            System.out.println(queryTerm);
            int qf = getQueryTermFreq(queryTerm,q.queryTerms());
            System.out.println("BMScore : "+BMScore);
            int tf = getTermFreq(queryTerm,rdocID);
            double ni = computeDocFreq(queryTerm);
            BMScore += calculateTermScore(qf,tf,ni,rdocID);
        }
        System.out.println();
        System.out.println("BMScore : "+BMScore);
        System.out.println("==============================");
        return BMScore;
    }

    private static int getTermFreq(String queryTerm, String docID) {
        String term;
        List<Posting> postings;
        for (Map.Entry<String, List<Posting>> entry : invertedListsForQuery.entrySet()) {
            term = entry.getKey().toString();
                if(term.equals(queryTerm)) {
                    postings = entry.getValue();
                    for (Posting p : postings){
                        if(p.docID().equals(docID)){
                            //System.out.println("TF(fi)  : "+p.termFreq());
                            return p.termFreq();
                        }
                    }
                }
        }
        //System.out.println(queryTerm+" - tf DOES NOT EXIST FOR "+docID+"document");
        return 0;
    }

    private static double computeDocFreq(String queryTerm) {
        String term;
        List<Posting> postings;
        for (Map.Entry<String, List<Posting>> entry : invertedListsForQuery.entrySet()) {
            term = entry.getKey().toString();
            if (term.equals(queryTerm)) {
                postings = entry.getValue();
                //System.out.println("Doc Freq of term:" + queryTerm + ": " + postings.size());
                return (double)(postings.size());
            }
        }
        //System.out.println(queryTerm+" DOES NOT EXIST IN INDEX");
        return 0.0;
    }

    private static int getQueryTermFreq(String queryTerm, List<String> queryTerms) {
        int freq = 0;
        for(String s: queryTerms){
            if(s.equals(queryTerm)){
                freq+=1;
            }
        }
        //System.out.println("QTF(qfi)  : "+freq);
        return freq;
    }

    private static double calculateTermScore(int queryTermFreq, int termFreq,
                                             double relevantDocFreq, String rdoc) throws IOException {

        // No relevance information
        double r = 0.0;
        double R = 0.0;

        double k1 = 1.2;
        double k2 = 100;
        double b = 0.75;
        double K = computeK(k1,b,rdoc);

        double f = termFreq; // term frequency of term in given doc
        double qf = queryTermFreq;
        System.out.println("TF(fi)  : "+f);
        System.out.println("QTF(qfi): "+qf);

        double ni = relevantDocFreq;
        double N = 1000; // Number of Documents
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
        System.out.println("secondN : " + secondN);
        System.out.println("secondD : " + secondD);
        System.out.println("thirdN  : " + thirdN);
        System.out.println("thirdD  : " + thirdD);
        System.out.println("Score   : " + score);

        //System.out.println((firstParameterNumerator / firstParameterDenominator) * secondParameter * thirdParameter);
        //System.out.println("final   : "+((Math.log((firstComponentN / firstComponentD))) * secondComponent * thirdComponent));
        return score;
    }

    // ================================= //
    // TEST getDOcLength function....
    // ================================= //
    private static double computeK(double k1, double b, String docID) throws IOException {
        //System.out.println(avdl);
        System.out.println("Doc ID  : "+docID);
        System.out.println("Doc Len : "+ docLengths.get(docID));
        double K = k1 * ((1 - b) + b*(docLengths.get(docID)/ avdl));
        System.out.println("K       : "+K);
        return K;
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
                System.out.println("TERM: "+t);
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
