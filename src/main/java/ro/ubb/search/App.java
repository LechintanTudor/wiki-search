package ro.ubb.search;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import ro.ubb.search.document.Document;
import ro.ubb.search.document.Parser;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class App {
    private static final String SEARCH_DIRECTORY = "data";

    public static void main(String[] args) {
        Path indexPath = Paths.get("lucene_index");
        Analyzer analyzer = new StandardAnalyzer();

        try {
            Directory directory = FSDirectory.open(indexPath);

            IndexWriterConfig config = new IndexWriterConfig(analyzer);

            config.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);

            IndexWriter indexWriter = new IndexWriter(directory, config);

            processDocuments(document -> {
                document.forEachChapter(chapter -> {
                    // Create a Lucene Document
                    org.apache.lucene.document.Document luceneDocument = new org.apache.lucene.document.Document();
                    luceneDocument.add(new TextField("chapterTitle", chapter.title(), TextField.Store.YES));
                    luceneDocument.add(new TextField("content", chapter.content(), TextField.Store.YES));
                    // Add the Lucene Document to the index
                    try {
                        indexWriter.addDocument(luceneDocument);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
            });

            indexWriter.commit();
            indexWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private static void processDocuments(Consumer<Document> documentConsumer) {
        for (var fileName : getDocumentFileNames(SEARCH_DIRECTORY)) {
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
