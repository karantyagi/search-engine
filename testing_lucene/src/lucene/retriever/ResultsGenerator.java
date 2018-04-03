package lucene.retriever;

import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;


public class ResultsGenerator {

    public static void main(String[] args)throws IOException, ParseException {

        // =======================================
        // Testing Manual Query List
        // =======================================

        // =======================================
        // TO DO : READ QUERY from file : xyz.txt ...
        // =======================================

        String indexDir = "E:\\1st - Career\\NEU_start\\@@Technical\\2 - sem\\IR\\assign4\\test\\index";
        List<String> queryList = new ArrayList<>();
        queryList.add("dark eclipse moon");             // Query 1
        queryList.add("forecast models");               // Query 2
        queryList.add("total eclipse solar");           // Query 3
        queryList.add("japan continental airline");     // Query 4
        queryList.add("japan continental airlines");    // Query 5
        queryList.add("solar eclipse fiction");         // Query 6
        queryList.add("2017 solar eclipse");            // Query 7
        queryList.add("total eclipse lyrics");          // Query 8
        queryList.add("nordic marine animals");         // Query 9
        queryList.add("volcanic eruptions tornadoes eruption tornado");  // Query 10

        // =========================================================
        // TO DO:
        // Enter path to Query file (.txt) or Enter Query[lowercase] (exmaple: Why so serious?)
        // https://stackoverflow.com/questions/17527741/what-is-the-default-list-of-stopwords-used-in-lucenes-stopfilter?utm_medium=organic&utm_source=google_rich_qa&utm_campaign=google_rich_qa
        // READ from QUERY FILE OF A SPECIFIED FORMAT (.txt), string in diff lines

        // String usage = "Usage:\tjava org.apache.lucene.demo.ResultGenerator [-index dir] [-field f] [-repeat n] [-queries file] [-query string]

        // =========================================================
        for (String query : queryList) {

            Searcher searchQuery = new Searcher(indexDir,query);
            searchQuery.findDocs();
        }
        }

        // ===========================
        // Write your Query processing Module - lowercase, remove stopwords:
        // https://stackoverflow.com/questions/17527741/what-is-the-default-list-of-stopwords-used-in-lucenes-stopfilter?utm_medium=organic&utm_source=google_rich_qa&utm_campaign=google_rich_qa
        // only alphabets and digits remain

        // GOAL is to learn : JAVA REGEX here.....
        // ==========================

    /**
     *
     * @param file_content
     * @return Cleans the given query
     *
    List<String> getProcessedQueryList(String file_content){
        List<String> query_list = new ArrayList<>();
        String[] splitByDoc = file_content.split("</DOC>");

        for (String s : splitByDoc) {
            String query = s.split("</DOCNO>")[1].replace("</DOC>", "");
            query_list.add(query.replaceAll("(?=[]\\[+&|!(){}^\"~*?:\\\\-])", "\\\\").replaceAll("/", "\\\\/"));
        }
        return query_list;
    }
     */


}

/**


 public static void main(String[] args) throws IOException {

        // =========================================================
        // Now search
        // =========================================================

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("Enter the FULL path where the index will be created: (e.g. /Usr/index or c:\\temp\\index)");
        String indexDirPath = br.readLine();

        // ==========================================================================================================
        // If directory exists, delete all file in the directory.
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

            // ==========================================================================================================
            // TO DO : Put the above in  a WHILE LOOP - Press 'q' to Quit ..... ??????????????????
            // ==========================================================================================================


        }
    }
     */

