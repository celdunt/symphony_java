package loc.ex.symphony.file;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import loc.ex.symphony.listview.Article;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class ArticleSerializer {

    public static void save(ObservableList<Article> articles) throws IOException {

        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(Files.newBufferedWriter(Path.of("articles.json")), articles.stream().toList());

    }

    public static List<Article> load() throws IOException {

        Path path = Path.of("articles.json");

        if (!Files.exists(path)) {
            return FXCollections.observableArrayList();
        }

        ObjectMapper mapper = new ObjectMapper();
        mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
        return mapper.readValue(Files.newBufferedReader(path), new TypeReference<List<Article>>() {});

    }

}
