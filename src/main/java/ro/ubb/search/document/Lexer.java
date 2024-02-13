package ro.ubb.search.document;

import java.util.function.Supplier;

public class Lexer {
    private final Supplier<String> lineSupplier;
    private Token peekedToken;

    public Lexer(Supplier<String> lineSupplier) {
        this.lineSupplier = lineSupplier;
        this.peekedToken = null;
    }

    public Token nextToken() {
        if (peekedToken != null) {
            var currentToken = peekedToken;
            peekedToken = null;
            return currentToken;
        }

        var currentLine = lineSupplier.get();

        if (currentLine == null) {
            return Token.EOF;
        } else if (currentLine.startsWith("[[") && currentLine.endsWith("]]")) {
            return nextTitleToken(currentLine);
        } else if (currentLine.startsWith("==") && currentLine.endsWith("==")) {
            return nextChapterToken(currentLine);
        } else {
            return new Token(TokenKind.Content, 0, currentLine);
        }
    }

    public Token peekToken() {
        if (peekedToken == null) {
            peekedToken = nextToken();
        }

        return peekedToken;
    }

    private Token nextTitleToken(String currentLine) {
        var title = currentLine.substring(2, currentLine.length() - 2);
        return new Token(TokenKind.Title, 0, title);
    }

    private Token nextChapterToken(String currentLine) {
        var halfLength = currentLine.length() / 2;
        var i = 2;

        for (; i < halfLength; ++i) {
            var j = currentLine.length() - i;

            if (!(currentLine.charAt(i) == '=' && currentLine.charAt(j) == '=')) {
                break;
            }
        }

        var title = currentLine.substring(i, currentLine.length() - i);
        return new Token(TokenKind.Chapter, i - 1, title);
    }
}
