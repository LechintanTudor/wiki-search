package ro.ubb.search.question;

import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;

public record Question(String category, String question, String answer) {
    public String toQuery() {
        return parseQuery(question);
    }

    private String parseQuery(String query) {
        try {
            QueryParser titleParser = new QueryParser("title", new EnglishAnalyzer());
            Query titleQuery = titleParser.parse(query);

            Query contentQuery = new QueryParser("content", new EnglishAnalyzer()).parse(query);

            BooleanQuery.Builder booleanQueryBuilder = new BooleanQuery.Builder();
            booleanQueryBuilder.add(new BooleanClause(titleQuery, BooleanClause.Occur.SHOULD));
            booleanQueryBuilder.add(new BooleanClause(contentQuery, BooleanClause.Occur.SHOULD));
            return booleanQueryBuilder.build().toString();
        } catch (org.apache.lucene.queryparser.classic.ParseException e) {
            e.printStackTrace();
            return "";
        }
    }
}
