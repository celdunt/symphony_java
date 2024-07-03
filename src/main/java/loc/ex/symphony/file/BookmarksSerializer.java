package loc.ex.symphony.file;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import loc.ex.symphony.listview.BookmarkStruct;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;


public class BookmarksSerializer {

    public static void save(ObservableList<BookmarkStruct> bookmarks) throws IOException {

        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(Files.newBufferedWriter(Path.of("bookmarks.json")), bookmarks.stream().toList());

    }

    public static List<BookmarkStruct> load() throws IOException {

        Path path = Path.of("bookmarks.json");

        if (!Files.exists(path)) {
            return FXCollections.observableArrayList();
        }

        ObjectMapper mapper = new ObjectMapper();
        mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
        return mapper.readValue(Files.newBufferedReader(path), new TypeReference<List<BookmarkStruct>>() {});

    }

}
