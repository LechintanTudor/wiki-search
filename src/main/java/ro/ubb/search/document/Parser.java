package ro.ubb.search.document;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class Parser {
    private final Lexer lexer;
    private final StringBuilder buffer;

    public Parser(Supplier<String> lineSupplier) {
        this.lexer = new Lexer(lineSupplier);
        this.buffer = new StringBuilder();
    }

    public Document nextDocument() {
        if (lexer.peekToken().kind() == TokenKind.Eof) {
            return null;
        }

        var titleToken = lexer.nextToken();
        if (titleToken.kind() != TokenKind.Title) {
            throw new ParserError(titleToken, List.of(TokenKind.Title));
        }

        var chapters = new ArrayList<Chapter>();

        if (lexer.peekToken().kind() == TokenKind.Content) {
            chapters.add(new Chapter(titleToken.content(), nextContent()));
        }

        var chapterHierarchy = new ArrayList<>();
        var chapterNameBuilder = new StringBuilder();

        while (lexer.peekToken().kind() == TokenKind.Chapter) {
            // Read chapter title
            var chapterToken = lexer.nextToken();
            if (chapterToken.kind() != TokenKind.Chapter) {
                throw new ParserError(chapterToken, List.of(TokenKind.Chapter));
            }

            // Update chapter hierarchy
            while (chapterHierarchy.size() != chapterToken.level()) {
                chapterHierarchy.remove(chapterHierarchy.size() - 1);
            }
            chapterHierarchy.add(chapterToken.content());

            // Read chapter content
            var content = nextContent();

            // Build full chapter title
            chapterNameBuilder.delete(0, chapterNameBuilder.length());
            chapterNameBuilder.append(chapterHierarchy.get(0));

            for (var i = 1; i < chapterHierarchy.size(); ++i) {
                chapterNameBuilder.append('/');
                chapterNameBuilder.append(chapterHierarchy.get(i));
            }

            var chapterName = chapterNameBuilder.toString();
            chapters.add(new Chapter(chapterName, content));
        }

        return new Document(titleToken.content(), chapters);
    }

    private String nextContent() {
        buffer.delete(0, buffer.length());

        while (lexer.peekToken().kind() == TokenKind.Content) {
            buffer.append(lexer.nextToken().content());
        }

        return buffer.toString();
    }
}
