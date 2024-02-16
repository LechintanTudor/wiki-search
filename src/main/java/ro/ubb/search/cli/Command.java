package ro.ubb.search.cli;

import java.util.function.Consumer;

public record Command(Consumer<String[]> function, String description) {
    public void run(String[] args) {
        function.accept(args);
    }
}
