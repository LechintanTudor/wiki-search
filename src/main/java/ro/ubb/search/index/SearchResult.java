package ro.ubb.search.index;

public record SearchResult(float score, String title, String content) {
    // Empty
}
