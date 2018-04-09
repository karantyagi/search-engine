package retriever;

import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;


public class ResultsGenerator {

    private static String queryDir;
    private static String indexDir;

    public static void main(String[] args)throws IOException, ParseException {

        // ============================================================== //
        // Get Queries (from query text file)
        // ============================================================== //

        BufferedReader br1 = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("Enter the FULL path for the query file: (e.g. /Usr/query.txt or c:\\temp\\query.txt)");
        //String queryFilePath = "..\\..\\query\\queries.txt";
        queryDir = br1.readLine();

        final Path queryFilePath = Paths.get(queryDir);
        if (!Files.isReadable(queryFilePath)) {
            System.out.println("Document directory '" + queryFilePath.toAbsolutePath() + "does not exist or is not readable, please check the path");
            System.exit(1);
        }
        if (!new File(queryDir).isFile() && (new File(queryDir).isDirectory()) ) {
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

        // =======================================
        // Add queries from file to a List
        // =======================================

        List<String> queryList = new ArrayList<>();
        addQueries(queryList);
        System.out.println("\n"+queryList.size()+" queries added successfully!");

        // ============================================================== //
        // Load Lucene Index
        // ============================================================== //

        System.out.println("Enter the FULL path for the lucene index (e.g. /Usr/luceneIndex or c:\\temp\\luceneIndex)");
        // indexDir  = "..\\..\\luceneIndex"
        indexDir = br1.readLine();

        final Path indexDirPath = Paths.get(indexDir);
        if (!Files.isReadable(indexDirPath)) {
            System.out.println("Document directory '" + indexDirPath.toAbsolutePath() + "does not exist or is not readable, please check the path");
            System.exit(1);
        }
        for (String query : queryList) {

            // ============================================================== //
            // Search for query and display results
            // ============================================================== //

            Searcher searchQuery = new Searcher(indexDir,query);
            searchQuery.findDocs();
        }
    }

    private static void addQueries(List<String> queryList) throws IOException {

        // =======================================
        // Add queries from file to a List
        // =======================================

        // Open the file
        FileInputStream fstream = null;
        try {
            fstream = new FileInputStream(queryDir);
            BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
            String line = null;
            String words[];
            String query;
            int wordPosition = 0;
            int lineCount = 0;

            //Read File Line By Line
            while ((line = br.readLine())!= null)   {
                query = "";
                wordPosition = 0;
                lineCount++;
                if(lineCount>1){
                    line = line.trim().replace("\t", " ");
                    line = line.replaceAll(" +", " ");
                    words = line.split(" ");
                    for(String s : words){
                        wordPosition++;
                        if(wordPosition>1){
                            query = query + s + " ";
                        }
                    }
                    queryList.add(query.trim());
                    //System.out.println(query.trim());

                }

            }
            //Close the input stream
            br.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }

    static boolean checkQueryFileFormat() throws IOException {

        // Open the file
        FileInputStream fstream = null;
        try {
            fstream = new FileInputStream(queryDir);
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

}

/**

        // ==========================================================================================================
        // CHECK if directory contains lucene index or not
        // ==========================================================================================================

        final Path indexDir = Paths.get(indexDirPath);
        if (Files.isDirectory(indexDir)) {
            // CHECK WHETHER IT HAS LUCENE INDEX OR NOT ...
        }


        // ==========================================================================================================
        // SOMETHING  LIKE THIS NEEDS TO BE DONE - THIS WAS FOR CREATION -
        // ==========================================================================================================

        try {

            FSDirectory dir = FSDirectory.open(new File(indexDir));
            IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_47, simpleAnalyzer);
            // reader = new IndexReader(dir, config);
        } catch (Exception ex) {
            //System.out.println("Cannot create index.");
            System.out.println("Cannot locate index inside the given directory.");
            System.out.println(ex.getMessage());
            System.out.println("Enter correct path for index directory.");
            System.exit(-1);

     */

