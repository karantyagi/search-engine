package retriever;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.SimpleAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.*;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;


public class Searcher {

    private static Analyzer simpleAnalyzer;
    private static IndexReader reader;
    private static IndexSearcher searcher;
    private static String indexDir;
    private static String searchQuery;

    /**
     * Constructor
     *
     * @param indexDirPath path of index directory
     * @throws java.io.IOException when exception creating index.
     */
    public Searcher(String indexDirPath, String query) throws IOException {

        this.indexDir = indexDirPath;
        this.searchQuery = query;
        simpleAnalyzer = new SimpleAnalyzer(Version.LUCENE_47);
        reader = DirectoryReader.open(FSDirectory.open(new File(indexDir)));
        searcher = new IndexSearcher(reader);
    }

    public static void findDocs() throws IOException {

        TopScoreDocCollector collector = TopScoreDocCollector.create(100, true);
        Query q;
        ScoreDoc[] hits;

            try {

                // ===========================================================
                // TO DO: improve Query using parsed Query and analyze results
                // ===========================================================

                q = new QueryParser(Version.LUCENE_47, "contents", simpleAnalyzer).parse(searchQuery);
                searcher.search(q, collector);
                hits = collector.topDocs().scoreDocs;
                generateResults(searchQuery, hits);
            }catch (Exception e) {
                System.out.println("Error searching " + searchQuery + " : "+ e.getMessage());

        }
        reader.close();
    }


    private static void generateResults(String query, ScoreDoc[] hits) throws IOException {


        // ============================================================================================= //
        // Write top 100 results for a query to file and save in "retrievedResults" directory
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

        try {
            //System.out.println(resultDir+"\\"+query+".txt");
            FileWriter fw = new FileWriter(resultDir + "\\" + query + "_results.txt");
            BufferedWriter bw = new BufferedWriter(fw);

            // Display results
            System.out.println("\nQuery: " + query);
            System.out.println("Found " + hits.length + " hits.\n");
            for (int i = 0; i < hits.length; ++i) {
                int docId = hits[i].doc;
                Document d = searcher.doc(docId);
                String doc = d.get("path").toString();
                doc = doc.substring(doc.lastIndexOf('\\') + 1);
                bw.append(String.format("%-3d %-60s %1.7f \r\n", (i + 1), doc, hits[i].score));
                System.out.printf("%-3d  %-85s       | Score: %1.7f ", (i + 1), doc, hits[i].score);
                System.out.print("\n");
            }
            bw.close();
            fw.close();
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
        }

    }

}