package loc.ex.symphony.file;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import loc.ex.symphony.listview.NotesStorage;
import loc.ex.symphony.listview.ParallelsLinksStorage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class ParallelsLinksSerializer {

    public static void save(ParallelsLinksStorage[] storage, String name) throws IOException {

        String fileName = getFileName(name);
        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(Files.newBufferedWriter(Path.of(fileName)), storage);

    }

    public static ParallelsLinksStorage[] load(String name) throws IOException {

        String fileName = getFileName(name);
        Path path = Path.of(fileName);

        if (!Files.exists(path)) {
            return new ParallelsLinksStorage[0];
        }

        ObjectMapper mapper = new ObjectMapper();
        mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
        return mapper.readValue(Files.newBufferedReader(path), ParallelsLinksStorage[].class);

    }

    private static String getFileName(String name) {
        return String.format("%s-parallels-links-storage.json", name);
    }


}
