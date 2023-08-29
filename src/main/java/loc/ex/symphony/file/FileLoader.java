package loc.ex.symphony.file;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class FileLoader {

    private final Path biblePath = Paths.get("bible");
    private final Path ellenPath = Paths.get("ellen");

    public List<RawBook> getBible() throws IOException {
        if (!Files.isReadable(biblePath)) return null;
        List<RawBook> books = new ArrayList<>();

        Stream<Path> paths = Files.walk(biblePath);
        for (Path path : paths.filter(Files::isRegularFile).toList()) {
            books.add(new RawBook(path.getFileName().toString().replace(".txt", ""),
                    String.join("\n", Files.readAllLines(path))));
        }

        return books;
    }

    public List<RawBook> getEllen() throws IOException {
        if (!Files.isReadable(ellenPath)) return null;
        List<RawBook> books = new ArrayList<>();

        Stream<Path> paths = Files.walk(ellenPath);
        for (Path path : paths.filter(Files::isRegularFile).toList()) {
            books.add(new RawBook(path.getFileName().toString().replace(".txt", ""),
                    String.join("\n", Files.readAllLines(path))));
        }

        return books;
    }

}
