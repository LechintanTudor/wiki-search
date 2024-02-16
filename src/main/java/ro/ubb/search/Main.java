package ro.ubb.search;

import ro.ubb.search.cli.CliApp;

public class Main {
    private static final String SEARCH_DIRECTORY = "data";

    public static void main(String[] args) {
        var cliApp = new CliApp();
        cliApp.run();
    }
}
