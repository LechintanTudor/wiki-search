package ro.ubb.search.cli;

import org.apache.commons.io.FileUtils;
import ro.ubb.search.index.IndexBuilder;
import ro.ubb.search.index.IndexSearcher;
import ro.ubb.search.index.SearchResult;
import ro.ubb.search.question.QuestionParser;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class CliApp {
    private static final String LUCENE_INPUT = "lucene_input";
    private static final String LUCENE_OUTPUT = "lucene_output";
    private static final String LUCENE_DOCUMENTS = "lucene_documents";
    private static final String QUESTIONS_FILE = "questions.txt";

    private boolean isRunning;
    private final Map<String, Command> commands;

    public CliApp() {
        this.isRunning = false;

        var commands = new HashMap<String, Command>();
        commands.put("exit", new Command(this::cmdExit, "Exit the program."));
        commands.put("help", new Command(this::cmdHelp, "Show available commands."));
        commands.put("index", new Command(this::cmdIndex, "Build the Lucene index."));
        commands.put("reset", new Command(this::cmdReset, "Reset the Lucene index."));
        commands.put("search", new Command(this::cmdSearch, "Search the Lucene index."));
        commands.put("answer", new Command(this::cmdAnswer, "Answer the questions in the questions file."));
        this.commands = commands;
    }

    public void run() {
        isRunning = true;

        System.out.println("Welcome to Wiki Search!");
        System.out.println("Type 'help' for a list of commands.");

        var scanner = new Scanner(System.in);

        while (isRunning) {
            System.out.print("\n>>> ");
            System.out.flush();

            var words = scanner.nextLine().trim().split("\\s+", 2);

            if (words.length == 0) {
                continue;
            }

            var commandName = words[0];
            var command = commands.get(commandName);

            if (command == null) {
                System.out.printf("'%s' is not a valid command.\n", commandName);
                continue;
            }

            var args = words.length == 2 ? words[1] : "";

            try {
                command.run(args);
            } catch (Exception error) {
                System.out.printf("[ERROR]: %s\n", error.getMessage());
            }
        }

        System.out.println("Goodbye.");
    }

    private void cmdExit(String args) {
        this.isRunning = false;
    }

    private void cmdHelp(String args) {
        for (var command : commands.entrySet()) {
            System.out.printf("- %s: %s\n", command.getKey(), command.getValue().description());
        }
    }

    private void cmdReset(String args) {
        try {
            FileUtils.deleteDirectory(new File(LUCENE_OUTPUT));
            FileUtils.deleteDirectory(new File(LUCENE_DOCUMENTS));
            System.out.println("Lucene index deleted successfully!");
        } catch (IOException error) {
            throw new RuntimeException(error);
        }
    }

    private void cmdIndex(String args) {
        try {
            IndexBuilder.buildIndex(LUCENE_INPUT, LUCENE_OUTPUT, LUCENE_DOCUMENTS);
            System.out.println("Lucene index built successfully!");
        } catch (Exception error) {
            throw new RuntimeException(error);
        }
    }

    private void cmdSearch(String args) {
        System.out.printf("Searching for '%s'.\n", args);

        List<SearchResult> results;

        try {
            var indexSearcher = new IndexSearcher(LUCENE_OUTPUT);
            results = indexSearcher.search(args, 10);
        } catch (Exception error) {
            throw new RuntimeException(error);
        }

        System.out.printf("Found %d results.\n", results.size());

        for (var result : results) {
            System.out.printf("- [%.4f] %s\n", result.score(), result.title());
        }
    }

    private void cmdAnswer(String args) {
        System.out.println("Answering questions.");

        try {
            var questions = QuestionParser.parseQuestions(QUESTIONS_FILE);
            var indexSearcher = new IndexSearcher(LUCENE_OUTPUT);
            var counter = 0;
            for (int i = 0; i < questions.size(); ++i) {
                var question = questions.get(i);
                var results = indexSearcher.search(question.toQuery(), 1);
                var result = results.isEmpty() ? "<not_found>" : results.get(0).title();

                System.out.printf("%d) %s\n", i + 1, question.question());
                System.out.printf("  FOUND: %s\n", result);
                System.out.printf("CORRECT: %s\n", question.answer());
                System.out.println();
                if (Objects.equals(trimAndLowerCase(question.answer()), trimAndLowerCase(result)) ||
                        Objects.equals(trimAndLowerCase(result), trimAndLowerCase(question.answer()))) {
                    counter += 1;
                }
            }
            System.out.printf("Number of correct answers: %d\n", counter);
        } catch (Exception error) {
            System.out.println("1");
            throw new RuntimeException(error);
        }
    }
    private String trimAndLowerCase(String input) {
        return input.trim().toLowerCase();
    }
}
