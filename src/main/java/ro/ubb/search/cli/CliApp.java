package ro.ubb.search.cli;

import org.apache.commons.io.FileUtils;
import ro.ubb.search.index.IndexBuilder;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class CliApp {
    private static final String LUCENE_INPUT = "lucene_input";
    private static final String LUCENE_OUTPUT = "lucene_output";

    private boolean isRunning;
    private final Map<String, Command> commands;

    public CliApp() {
        this.isRunning = false;

        var commands = new HashMap<String, Command>();
        commands.put("exit", new Command(this::cmdExit, "Exit the program."));
        commands.put("help", new Command(this::cmdHelp, "Show available commands."));
        commands.put("index", new Command(this::cmdIndex, "Build the Lucene index."));
        commands.put("reset", new Command(this::cmdReset, "Reset the Lucene index."));
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

            var words = scanner.nextLine().trim().split("\\s+");

            if (words.length == 0) {
                continue;
            }

            var commandName = words[0];
            var command = commands.get(commandName);

            if (command == null) {
                System.out.printf("'%s' is not a valid command.\n", commandName);
                continue;
            }

            var args = Arrays.copyOfRange(words, 1, words.length);

            command.run(args);

//            try {
//                command.run(args);
//            } catch (Exception error) {
//                System.out.printf("[ERROR]: %s\n", error.getMessage());
//            }
        }

        System.out.println("Goodbye.");
    }

    private void cmdExit(String[] args) {
        this.isRunning = false;
    }

    private void cmdHelp(String[] args) {
        for (var command : commands.entrySet()) {
            System.out.printf("- %s: %s\n", command.getKey(), command.getValue().description());
        }
    }

    private void cmdReset(String[] args) {
        try {
            FileUtils.deleteDirectory(new File(LUCENE_OUTPUT));
            System.out.println("Lucene index deleted successfully!");
        } catch (IOException error) {
            throw new RuntimeException(error);
        }
    }

    private void cmdIndex(String[] args) {
        try {
            IndexBuilder.buildIndex(LUCENE_INPUT, LUCENE_OUTPUT);
            System.out.println("Lucene index built successfully!");
        } catch (Exception error) {
            throw new RuntimeException(error);
        }
    }
}
