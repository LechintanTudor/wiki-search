package ro.ubb.search.question;

public record Question(String category, String question, String answer) {
    public String toQuery() {
        return question.replaceAll("[^\\sa-zA-Z0-9]", "");
    }
}
