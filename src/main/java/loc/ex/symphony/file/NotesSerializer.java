package loc.ex.symphony.file;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import loc.ex.symphony.listview.Article;
import loc.ex.symphony.listview.NotesStorage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class NotesSerializer {

    public static void save(NotesStorage[] storage, String name) throws IOException {

        String fileName = getFileName(name);
        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(Files.newBufferedWriter(Path.of(fileName)), storage);

    }

    public static NotesStorage[] load(String name) throws IOException {

        String fileName = getFileName(name);
        Path path = Path.of(fileName);

        if (!Files.exists(path)) {
            return new NotesStorage[0];
        }

        ObjectMapper mapper = new ObjectMapper();
        mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
        return mapper.readValue(Files.newBufferedReader(path), NotesStorage[].class);

    }

    private static String getFileName(String name) {
        return String.format("%s-note-storage.json", name);
    }

}
