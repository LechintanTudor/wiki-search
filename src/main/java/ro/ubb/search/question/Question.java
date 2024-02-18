package ro.ubb.search.question;

import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;

public record Question(String category, String question, String answer) {
    public String toQuery() {
        try {
            var queryParser = new QueryParser("content", new EnglishAnalyzer());
            var contentQuery = queryParser.parse(question);
            var categoryQuery = queryParser.parse(category);

            var booleanQueryBuilder = new BooleanQuery.Builder();
            booleanQueryBuilder.add(new BooleanClause(contentQuery, BooleanClause.Occur.SHOULD));
            booleanQueryBuilder.add(new BooleanClause(categoryQuery, BooleanClause.Occur.SHOULD));
            return booleanQueryBuilder.build().toString();
        } catch (org.apache.lucene.queryparser.classic.ParseException error) {
            System.out.printf("[ERROR]: %s\n", error);
            return "";
        }
    }
}
