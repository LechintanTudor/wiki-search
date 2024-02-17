package ro.ubb.search.index;

import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.StoredFields;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.store.FSDirectory;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

public class IndexSearcher {
    private final QueryParser queryParser;
    private final org.apache.lucene.search.IndexSearcher indexSearcher;
    private final StoredFields storedFields;

    public IndexSearcher(String directory) throws Exception {
        var analyzer = new EnglishAnalyzer();

        this.queryParser = new QueryParser("content", analyzer);

        var indexDirectory = FSDirectory.open(Paths.get(directory));
        var indexReader = DirectoryReader.open(indexDirectory);

        this.indexSearcher = new org.apache.lucene.search.IndexSearcher(indexReader);
        this.storedFields = indexReader.storedFields();
    }

    public List<SearchResult> search(String queryString, int count) throws Exception {
        var query = queryParser.parse(queryString);
        var hits = indexSearcher.search(query, count).scoreDocs;

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
