package indexer;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.SimpleAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Arrays;

/**
 * Creates an index to be used by Lucene based retrieval model
 * and add files into this index based on the input of the user.
 */
public class Indexer {

    private static Analyzer simpleAnalyzer;
    private static IndexWriter writer;
    private static float fileCounter;
    private static int totalDocs;


    /**
     * Constructor
     *
     * @param indexDir the name of the folder in which the index should be created
     * @throws IOException when exception creating index.
     */
    Indexer(String indexDir) throws IOException {

        simpleAnalyzer = new SimpleAnalyzer(Version.LUCENE_47);
        FSDirectory dir = FSDirectory.open(new File(indexDir));
        IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_47, simpleAnalyzer);
        writer = new IndexWriter(dir, config);
        fileCounter = 0.0f;
        totalDocs = 0;
    }

    public static void main(String[] args) throws IOException {

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("Enter the FULL path where the index will be created: (e.g. /Usr/index or c:\\temp\\index)");
        // "..\\..\\luceneIndex";
        String indexDirPath = br.readLine();
        Indexer indexer = null;

        // ==========================================================================================================
        // If directory does not exist, create it, otherwise delete all previous files in the directory.
        // ==========================================================================================================
        final Path indexDir = Paths.get(indexDirPath);
        if(Files.isDirectory(indexDir)) {
            // DELETE ALL CONTENTS OF THE DIRECTORY
            Arrays.stream(new File(indexDirPath).listFiles()).forEach(File::delete);
        }
        else{
            File dir = new File(indexDirPath);
            dir.mkdirs();
            System.out.println(Paths.get(indexDirPath).toAbsolutePath()+" created!");

        }

        try {

            indexer = new Indexer(indexDirPath);
        } catch (Exception ex) {
            System.out.println("Cannot create index.");
            System.out.println(ex.getMessage());
            System.exit(-1);
        }


        System.out.println("Enter the FULL path for raw docs (e.g. /Usr/index or c:\\temp\\index)");
        // "..\\..\\rawDocs"
        String rawDocsPath  = br.readLine();
        final Path docDir = Paths.get(rawDocsPath);
        if (!Files.isReadable(docDir)) {
            System.out.println("Document directory '" + docDir.toAbsolutePath() + "' does not exist or is not readable, please check the path");
            System.exit(1);
        }

        totalDocs = getTotalDocs(docDir);

        // ==========================================================================================================
        // Adding raw docs to the index
        // ==========================================================================================================

        indexDocs(writer, docDir);
        br.close();


        // ==========================================================================================================
        // after adding files , close the index
        // ==========================================================================================================
        closeIndex();
        System.out.println("\nLucene index created!");

    }

    /**
     * Close the index.
     *
     * @throws IOException
     *             when exception closing
     */
    static void closeIndex() throws IOException {
        writer.close();
    }


    private static int getTotalDocs(Path path) throws IOException {

            if(Files.isDirectory(path)) {
                Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
                    @Override
                    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                        //System.out.println("Found File    : " + file);
                        totalDocs+=1;
                        return FileVisitResult.CONTINUE;
                    }
                });
            }

        System.out.println("Total raw docs in directory : "+totalDocs);
            return totalDocs;
    }

    /**
     * @param writer
     * @param path
     * @throws IOException
     * Loops through each document in a directory to create its index
     */
    static void indexDocs(final IndexWriter writer, Path path) throws IOException {

        if(Files.isDirectory(path)) {


            Files.walkFileTree(path, new SimpleFileVisitor<Path>() {

                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {

                    try {
                        fileCounter+=1;
                        //System.out.println(fileCounter+" Adding:                     " + file);
                        //System.out.printf("\r%-3.0f Adding: %-120s | Progress: %3.1f ",fileCounter,file,((fileCounter*100)/totalDocs));
                        System.out.printf("\rFiles Added: %-3.0f                   | Progress: %3.1f%% ",fileCounter,((fileCounter*100)/totalDocs));
                        indexDoc(writer, file);
                    } catch(IOException io) {

                    }
                    return FileVisitResult.CONTINUE;
                }
            });
        }
    }

    /**
     * @param writer
     * @param file
     * @throws IOException
     * creates index of the current doc
     */
    static void indexDoc(IndexWriter writer, Path file) throws IOException {

        try(InputStream stream = Files.newInputStream(file)) {

            Document doc = new Document();

            Field pathField = new StringField("path", file.toString(), Field.Store.YES);
            doc.add(pathField);

            doc.add(new TextField("contents", new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8))));
            writer.addDocument(doc);
        }
    }


}