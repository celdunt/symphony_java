package loc.ex.symphony.file;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Stream;

public class FileLoader {

    Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    public List<RawBook> getBible() {
        Path biblePath = Paths.get("bible");
        List<RawBook> books = new ArrayList<>();

        if (!Files.isReadable(biblePath)) return books;

        try(Stream<Path> paths = Files.walk(biblePath)) {
            for (Path path : paths.filter(Files::isRegularFile).toList()) {
                books.add(new RawBook(path.getFileName().toString()
                        .replace(".txt", "")
                        .substring(3),
                        String.join("\n", Files.readAllLines(path, Charset.forName("windows-1251")))));
            }
        } catch (IOException exception) {
            logger.info(exception.getMessage());
        }

        return books;
    }

    public List<RawBook> getEllen() {
        Path ellenPath = Paths.get("ellen");
        List<RawBook> books = new ArrayList<>();

        if (!Files.isReadable(ellenPath)) return books;

        try(Stream<Path> paths = Files.walk(ellenPath)) {
            for (Path path : paths.filter(Files::isRegularFile).toList()) {
                books.add(new RawBook(path.getFileName().toString()
                        .replace(".txt", "")
                        .substring(3),
                        String.join("\n", Files.readAllLines(path, StandardCharsets.UTF_8))));
            }
        } catch (IOException exception) {
            logger.info(exception.getMessage());
        }

        return books;
    }

}
