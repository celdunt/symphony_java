package loc.ex.symphony.file;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.collections.FXCollections;
import loc.ex.symphony.listview.Book;
import loc.ex.symphony.listview.PathsEnum;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class BookSerializer {

    public static void save(List<Book> books) throws IOException {

        Path path = getPath(books.getFirst().root);
        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(Files.newBufferedWriter(path), books);

    }

    public static List<Book> load(PathsEnum mode) throws IOException {

        Path path = getPath(mode);

        if (!Files.exists(path)) {
            return FXCollections.observableArrayList();
        }

        ObjectMapper mapper = new ObjectMapper();
        mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
        return mapper.readValue(Files.newBufferedReader(path), new TypeReference<>() {
        });

    }

    private static Path getPath(PathsEnum mode) throws IOException {
        Path path = Path.of("components");
        if (!Files.exists(path)) {
            Files.createDirectory(path);
        }
        return mode == PathsEnum.Bible? Path.of("components/bible.json")
                : mode == PathsEnum.EllenWhite? Path.of("components/ellen.json")
                : Path.of("components/other.json");
    }

}
