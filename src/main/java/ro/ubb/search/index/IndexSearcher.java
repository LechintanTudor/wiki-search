package ro.ubb.search.index;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.store.FSDirectory;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

public class IndexSearcher {
    public static List<SearchResult> searchIndex(String directory, String queryString) throws Exception {
        var analyzer = new StandardAnalyzer();
        var queryParser = new QueryParser("content", analyzer);
        var query = queryParser.parse(queryString);

        var indexDirectory = FSDirectory.open(Paths.get(directory));
        var indexReader = DirectoryReader.open(indexDirectory);
        var indexSearcher = new org.apache.lucene.search.IndexSearcher(indexReader);

        var storedFields = indexReader.storedFields();
        var hits = indexSearcher.search(query, 10).scoreDocs;

        return Arrays.stream(hits).map(hit -> {
            try {
                var document = storedFields.document(hit.doc);
                var title = document.get("title");
                var content = document.get("content");
                return new SearchResult(hit.score, title, content);
            } catch (IOException error) {
                throw new RuntimeException(error);
            }
        }).toList();
    }
}
