package lucene.retriever;

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

import java.io.File;
import java.io.IOException;


public class Searcher {

    private static Analyzer simpleAnalyzer;
    private static IndexReader reader;
    private static IndexSearcher searcher;
    private static String indexDir = "E:\\1st - Career\\NEU_start\\@@Technical\\2 - sem\\IR\\assign4\\test\\index";
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

                // =========================================================
                // TO DO: improve parsed Query
                // =========================================================

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

        // =========================================================
        // TO DO: Write Results to a file
        // =========================================================

        // Display results
        System.out.println("\nQuery: "+query);
        System.out.println("Found " + hits.length + " hits.\n");
        for (int i = 0; i < hits.length; ++i) {
            int docId = hits[i].doc;
            Document d = searcher.doc(docId);
            String doc = d.get("path").toString();
            doc = doc.substring(doc.lastIndexOf('\\')+1);
            System.out.printf("%-3d  %-85s       | Score: %1.7f ",(i + 1),doc,hits[i].score);
            System.out.print("\n");
        }

        // 5. term stats --> watch out for which "version" of the term
        // must be checked here instead!

        // =========================================================
        // TO DO: REDUNDANT PART - This part is not working, ask Savan
        // REFER : https://stackoverflow.com/questions/31327126/accessing-terms-statistics-in-lucene-4?utm_medium=organic&utm_source=google_rich_qa&utm_campaign=google_rich_qa
        // =========================================================

        Term termInstance = new Term("contents", "moon");
        //System.out.println(termInstance.toString());
        long termFreq = reader.totalTermFreq(termInstance);
        long docCount = reader.docFreq(termInstance);
        long maxDocs = reader.maxDoc();

        System.out.print("\n");
        System.out.println("Statistics");
        System.out.println();
        System.out.println("Term(unigram): "+termInstance.toString());
        System.out.println("Term Frequency: " + termFreq);
        System.out.println("Document Frequency: " + maxDocs);
        System.out.println("Max Docs in Index: " + maxDocs);
        System.out.println("-------------------------------------------------------------------------------------------" +
                "----------------------");



    }

}