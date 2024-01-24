package ro.ubb.search.document;

public record Token(TokenKind kind, int level, String content) {
    public static final Token EOF = new Token(TokenKind.Eof, 0, "");
}
