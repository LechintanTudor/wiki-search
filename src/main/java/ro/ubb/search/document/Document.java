package ro.ubb.search.document;

import java.util.ArrayList;
import java.util.List;

public record Document(String title, List<Chapter> chapters) {
    public static Document withTitle(String title) {
        return new Document(title, new ArrayList<>());
    }
}
