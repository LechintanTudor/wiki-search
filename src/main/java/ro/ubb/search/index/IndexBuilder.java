package ro.ubb.search.index;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.FSDirectory;
import ro.ubb.search.document.Document;
import ro.ubb.search.document.Parser;

import java.io.*;
import java.nio.file.Paths;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class IndexBuilder {
    public static void buildIndex(String inputDirectory, String outputDirectory) throws Exception {
        var indexPath = Paths.get(outputDirectory);
        var analyzer = new StandardAnalyzer();

        var directory = FSDirectory.open(indexPath);

        var config = new IndexWriterConfig(analyzer);
        config.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);

        var indexWriter = new IndexWriter(directory, config);

        processDocuments(inputDirectory, document -> {
            // Create a Lucene Document
            org.apache.lucene.document.Document luceneDocument = new org.apache.lucene.document.Document();
            luceneDocument.add(new TextField("title", document.title(), TextField.Store.YES));
            luceneDocument.add(new TextField("content", document.content(), TextField.Store.YES));

            // Add the Lucene Document to the index
            try {
                System.out.printf("Indexing '%s'.\n", document.title());
                indexWriter.addDocument(luceneDocument);
            } catch (IOException error) {
                System.out.printf("[ERROR]: %s\n", error.getMessage());
            }
        });

        indexWriter.commit();
        indexWriter.close();
    }

    private static void processDocuments(String directory, Consumer<Document> documentConsumer) {
        for (var fileName : getDocumentFileNames(directory)) {
            BufferedReader reader;

            try {
                reader = new BufferedReader(new FileReader(fileName));
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }

            var parser = new Parser(() -> {
                try {
                    return reader.readLine();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });

            while (true) {
                var document = parser.nextDocument();

                if (document == null) {
                    break;
                }

                documentConsumer.accept(document);
            }

            try {
                reader.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private static List<String> getDocumentFileNames(String directory) {
        var files = new File(directory).listFiles();
        if (files == null) {
            throw new RuntimeException(String.format("'%s' is not a directory", directory));
        }

        return Stream.of(files)
                .filter(File::isFile)
                .map(file -> String.format("%s/%s", directory, file.getName()))
                .filter(name -> name.endsWith(".txt"))
                .toList();
    }
}
