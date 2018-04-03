package lucene.indexer;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Arrays;

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

/** E:\1st - Career\NEU_start\@@Technical\2 - sem\IR\assign4\test\rawDocSub */


/**
 * Creates an index to be used by Lucene's retrieval model
 * ??????????????????????????????????????????????????????????????   and add files into this index based
 * on the input of the user.
 */
public class Indexer {

    private static Analyzer simpleAnalyzer;
    private static IndexWriter writer;
    private static ArrayList<File> queue;
    private static float fileCounter;
    private static int totalDocs;


    /**
     * Constructor
     *
     * @param indexDir the name of the folder in which the index should be created
     * @throws java.io.IOException when exception creating index.
     */
    Indexer(String indexDir) throws IOException {

        simpleAnalyzer = new SimpleAnalyzer(Version.LUCENE_47);
        FSDirectory dir = FSDirectory.open(new File(indexDir));
        IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_47, simpleAnalyzer);
        writer = new IndexWriter(dir, config);
        queue = new ArrayList<>();
        fileCounter = 0.0f;
        totalDocs = 0;
    }

    public static void main(String[] args) throws IOException {

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("Enter the FULL path where the index will be created: (e.g. /Usr/index or c:\\temp\\index)");
        String indexDirPath = br.readLine();
        Indexer indexer = null;

        // ==========================================================================================================
        // If directory exists, delete all file in the directory.
        // ==========================================================================================================

        final Path indexDir = Paths.get(indexDirPath);
        if(Files.isDirectory(indexDir)) {
            // DELETE ALL CONTENTS OF THE DIRECTORY

            Arrays.stream(new File(indexDirPath).listFiles()).forEach(File::delete);
        }


        // ==========================================================================================================
        // What is this block doing ??
        // ==========================================================================================================

        try {

            indexer = new Indexer(indexDirPath);
        } catch (Exception ex) {
            System.out.println("Cannot create index.");
            System.out.println(ex.getMessage());
            System.exit(-1);
        }


        System.out.println("Enter the FULL path for raw docs (e.g. /Usr/index or c:\\temp\\index)");
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



        // ==========================================================================================================
        // after adding files , close the index
        // ==========================================================================================================
        closeIndex();
        System.out.println("\nLucene index created!");

    }

    /**
     * Close the index.
     *
     * @throws java.io.IOException
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

/**
 *
 * Indexes a file or directory
 *
 * @param fileName
 *            the name of a text file or a folder we wish to add to the
 *            index
 * @throws java.io.IOException
 *             when exception

public void indexFileOrDirectory(String fileName) throws IOException {
    // ===================================================
    // gets the list of files in a folder (if user has submitted
    // the name of a folder) or gets a single file name (is user
    // has submitted only the file name)
    // ===================================================
    addFiles(new File(fileName));

    int originalNumDocs = writer.numDocs();
    for (File f : queue) {
        FileReader fr = null;
        try {
            Document doc = new Document();

            // ===================================================
            // add contents of file
            // ===================================================
            fr = new FileReader(f);
            doc.add(new TextField("contents", fr));
            doc.add(new StringField("path", f.getPath(), Field.Store.YES));
            doc.add(new StringField("filename", f.getName(),
                    Field.Store.YES));

            writer.addDocument(doc);
            System.out.println("Added: " + f);
        } catch (Exception e) {
            System.out.println("Could not add: " + f);
        } finally {
            fr.close();
        }
    }

    int newNumDocs = writer.numDocs();
    System.out.println("");
    System.out.println("************************");
    System.out
            .println((newNumDocs - originalNumDocs) + " documents added.");
    System.out.println("************************");

    queue.clear();
}

    private void addFiles(File file) {

        if (!file.exists()) {
            System.out.println(file + " does not exist.");
        }
        if (file.isDirectory()) {
            for (File f : file.listFiles()) {
                addFiles(f);
            }
        } else {
            String filename = file.getName().toLowerCase();
            // ===================================================
            // Only index text files
            // ===================================================
            if (filename.endsWith(".htm") || filename.endsWith(".html")
                    || filename.endsWith(".xml") || filename.endsWith(".txt")) {
                queue.add(file);
            } else {
                System.out.println("Skipped " + filename);
            }
        }
    }
 */

