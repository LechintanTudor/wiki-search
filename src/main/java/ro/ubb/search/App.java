package ro.ubb.search;

import ro.ubb.search.document.Document;
import ro.ubb.search.document.Parser;

import java.io.*;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class App {
    private static final String SEARCH_DIRECTORY = "data";

    public static void main(String[] args) {
        processDocuments(document -> {
            System.out.printf("===== %s =====\n", document.title());

            for (var chapter : document.chapters()) {
                System.out.printf("* %s\n", chapter.title());
            }

            System.out.println();
        });
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
