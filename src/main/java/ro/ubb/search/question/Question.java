package ro.ubb.search.question;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;

import java.text.ParseException;

public record Question(String category, String question, String answer) {
    public String toQuery() {
        return parseQuery(question);
    }

    private String parseQuery(String query) {
        try {
            QueryParser titleParser = new QueryParser("title", new StandardAnalyzer());
            Query titleQuery = titleParser.parse(query);

            Query contentQuery = new QueryParser("content", new StandardAnalyzer()).parse(query);

            BooleanQuery.Builder booleanQueryBuilder = new BooleanQuery.Builder();
            booleanQueryBuilder.add(new BooleanClause(titleQuery, BooleanClause.Occur.SHOULD));
            booleanQueryBuilder.add(new BooleanClause(contentQuery, BooleanClause.Occur.SHOULD));
            return booleanQueryBuilder.build().toString();
        } catch (org.apache.lucene.queryparser.classic.ParseException e) {
            e.printStackTrace();
            System.out.println("here");
            return "";
        }
    }
}
