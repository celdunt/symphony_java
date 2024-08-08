package loc.ex.symphony.file;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.collections.FXCollections;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@JsonAutoDetect
public class StartupParameters {

    public int getTabId() {
        return tabId;
    }

    public int getBookId() {
        return bookId;
    }

    public int getChapterId() {
        return chapterId;
    }

    public int tabId;
    public int bookId;
    public int chapterId;

    @JsonCreator
    public StartupParameters(@JsonProperty("tabId") int tabId,
                             @JsonProperty("bookId") int bookId,
                             @JsonProperty("chapterId") int chapterId) {
        this.tabId = tabId;
        this.bookId = bookId;
        this.chapterId = chapterId;
    }

    public StartupParameters() {

    }

    public void save() throws IOException {
        Path path = Path.of("startup-parameters.json");
        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(Files.newBufferedWriter(path), this);
    }

    @JsonIgnore public StartupParameters load() throws IOException {
        Path path = Path.of("startup-parameters.json");

        if (!Files.exists(path)) {
            return new StartupParameters(0, 0, 0);
        }

        ObjectMapper mapper = new ObjectMapper();
        mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
        return mapper.readValue(Files.newBufferedReader(path), new TypeReference<>() {
        });
    }

}
