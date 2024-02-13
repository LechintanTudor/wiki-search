package ro.ubb.search.document;

import java.util.List;
import java.util.function.Consumer;

public record Document(String title, String content, List<ChapterData> chapterData) {
    public void forEachChapter(Consumer<Chapter> chapterConsumer) {
        for (var c : chapterData) {
            var chapterContent = content.substring(c.contentStart(), c.contentEnd());
            chapterConsumer.accept(new Chapter(c.title(), chapterContent));
        }
    }
}
