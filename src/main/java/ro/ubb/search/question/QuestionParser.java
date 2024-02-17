package ro.ubb.search.question;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class QuestionParser {
    public static List<Question> parseQuestions(String questionsPath) throws Exception {
        var content = FileUtils.readFileToString(new File(questionsPath), "UTF-8");
        var lines = content.split("\n");

        var questions = new ArrayList<Question>();

        for (var i = 0; i < lines.length;) {
            if (lines[i].isEmpty()) {
                i += 1;
                continue;
            }

            questions.add(new Question(lines[i], lines[i + 1], lines[i + 2]));
            i += 3;
        }

        return questions;
    }
}
