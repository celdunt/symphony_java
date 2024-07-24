package loc.ex.symphony.indexdata;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import loc.ex.symphony.listview.PathsEnum;

import java.io.*;
import java.lang.reflect.Type;
import javafx.concurrent.Task;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;

public class IndexSaverSingleThreaded {

    public static void saveUniqueWords(HashMap<Integer, String> uniqueWords, PathsEnum mode) throws IOException {
        String name = mode == PathsEnum.Bible ? "b_uniqueWords.json" : mode == PathsEnum.EllenWhite? "e_uniqueWords.json"
                : "o_uniqueWords.json";
        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(Files.newBufferedWriter(Path.of(name)), uniqueWords);
    }

    public static void saveUniqueWordsHelp(HashMap<String, Integer> uniqueWords, PathsEnum mode) throws IOException {
        String name = mode == PathsEnum.Bible ? "b_uniqueWordsH.json" : mode == PathsEnum.EllenWhite? "e_uniqueWordsH.json"
                : "o_uniqueWordsH.json";
        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(Files.newBufferedWriter(Path.of(name)), uniqueWords);
    }

    public static HashMap<Integer, String> loadUniqueWords(PathsEnum mode) throws IOException {
        String name = mode == PathsEnum.Bible ? "b_uniqueWords.json" : mode == PathsEnum.EllenWhite? "e_uniqueWords.json"
                : "o_uniqueWords.json";
        Path path = Path.of(name);

        if (!Files.exists(path)) {
            return new HashMap<>();
        }

        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(Files.newBufferedReader(path), new TypeReference<HashMap<Integer, String>>() {});
    }

    public static HashMap<String, Integer> loadUniqueWordsHelp(PathsEnum mode) throws IOException {
        String name = mode == PathsEnum.Bible ? "b_uniqueWordsH.json" : mode == PathsEnum.EllenWhite? "e_uniqueWordsH.json"
                : "o_uniqueWordsH.json";
        Path path = Path.of(name);

        if (!Files.exists(path)) {
            return new HashMap<>();
        }

        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(Files.newBufferedReader(path), new TypeReference<HashMap<String, Integer>>() {});
    }


    public static void save(HashMap<String, List<IndexStruct>> index, PathsEnum mode) throws IOException {
        String name_ = mode == PathsEnum.Bible? "bible.json" : mode == PathsEnum.EllenWhite? "ellen.json" : "other.json";
        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(Files.newBufferedWriter(Path.of(name_)), index);
    }

    public static Task<HashMap<String, List<IndexStruct>>> loadTask(PathsEnum mode) {
        return new Task<HashMap<String, List<IndexStruct>>>() {
            @Override
            protected HashMap<String, List<IndexStruct>> call() throws Exception {
                return load(mode);
            }
        };
    }

    public static HashMap<String, List<IndexStruct>> load(PathsEnum mode) throws IOException {
        String name = mode == PathsEnum.Bible? "bible.json" : mode == PathsEnum.EllenWhite? "ellen.json" : "other.json";

        Path path = Path.of(name);

        if (!Files.exists(path)) {
            return new HashMap<>();
        }

        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(Files.newBufferedReader(path), new TypeReference<HashMap<String, List<IndexStruct>>>() {});
    }
}
