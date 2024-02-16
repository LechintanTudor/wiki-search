package ro.ubb.search.document;

import java.util.ArrayList;
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
            titleToken = new Token(TokenKind.Title, 0, "<unknown>");
        }

        var chapterData = new ArrayList<ChapterData>();
        var contentBuilder = new StringBuilder();

        if (lexer.peekToken().kind() == TokenKind.Content) {
            var contentStart = contentBuilder.length();
            contentBuilder.append(nextContent());
            var contentEnd = contentBuilder.length();

            chapterData.add(new ChapterData(titleToken.content(), contentStart, contentEnd));
        }

        var chapterHierarchy = new ArrayList<>();
        chapterHierarchy.add(titleToken.content());

        var chapterNameBuilder = new StringBuilder();

        while (lexer.peekToken().kind() == TokenKind.Chapter) {
            // Read chapter title
            var chapterToken = lexer.nextToken();
            if (chapterToken.kind() != TokenKind.Chapter) {
                continue;
            }

            // Update chapter hierarchy
            while (chapterHierarchy.size() > 1 && chapterHierarchy.size() != chapterToken.level()) {
                chapterHierarchy.remove(chapterHierarchy.size() - 1);
            }
            chapterHierarchy.add(chapterToken.content());

            // Read chapter content
            var contentStart = contentBuilder.length();
            contentBuilder.append(nextContent());
            var contentEnd = contentBuilder.length();

            // Build full chapter title
            chapterNameBuilder.delete(0, chapterNameBuilder.length());
            chapterNameBuilder.append(chapterHierarchy.get(0));

            for (var i = 1; i < chapterHierarchy.size(); ++i) {
                chapterNameBuilder.append(" / ");
                chapterNameBuilder.append(chapterHierarchy.get(i));
            }

            var chapterName = chapterNameBuilder.toString();
            chapterData.add(new ChapterData(chapterName, contentStart, contentEnd));
        }

        return new Document(titleToken.content(), contentBuilder.toString(), chapterData);
    }

    private String nextContent() {
        buffer.delete(0, buffer.length());

        while (lexer.peekToken().kind() == TokenKind.Content) {
            buffer.append(lexer.nextToken().content());
        }

        return buffer.toString();
    }
}
