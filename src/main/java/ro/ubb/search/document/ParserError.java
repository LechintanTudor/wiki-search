package ro.ubb.search.document;

import java.util.List;

public class ParserError extends RuntimeException {
    public final Token found;
    public final List<TokenKind> expected;

    public ParserError(Token found, List<TokenKind> expected) {
        super(String.format("Unexpected token: (%s: %s)", found.kind(), found.content()));
        this.found = found;
        this.expected = expected;
    }
}
