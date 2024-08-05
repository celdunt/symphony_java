package loc.ex.symphony.listview;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import loc.ex.symphony.file.ParallelsLinksSerializer;

import java.io.IOException;

@JsonAutoDetect
public class ParallelsLinksStorage {

    @JsonIgnore private static ParallelsLinksStorage[] bibleParallelsLinksStorage;
    @JsonIgnore private static ParallelsLinksStorage[] ellenParallelsLinksStorage;
    @JsonIgnore private static ParallelsLinksStorage[] otherParallelsLinksStorage;

    public ParallelsLinksSubStorage[] subStorage;

    @JsonCreator
    public ParallelsLinksStorage(@JsonProperty("subStorage") ParallelsLinksSubStorage[] subStorage) {
        this.subStorage = subStorage;
    }

    public ParallelsLinksStorage(int chapters) {
        this.subStorage = new ParallelsLinksSubStorage[chapters];
    }

    public ParallelsLinksStorage() {}

    public static ParallelsLinksStorage getBible(int index, int chapters) throws IOException {
        if (bibleParallelsLinksStorage == null) {
            bibleParallelsLinksStorage = ParallelsLinksSerializer.load("bible");
        }
        if (bibleParallelsLinksStorage == null || bibleParallelsLinksStorage.length == 0)
            bibleParallelsLinksStorage = new ParallelsLinksStorage[66];
        if (index >= 0 && index < bibleParallelsLinksStorage.length) {
            if (bibleParallelsLinksStorage[index] == null)
                bibleParallelsLinksStorage[index] = new ParallelsLinksStorage(chapters);
            return bibleParallelsLinksStorage[index];
        } else return new ParallelsLinksStorage();
    }

    public static ParallelsLinksStorage getEllen(int index, int chapters) throws IOException {
        if (ellenParallelsLinksStorage == null) {
            ellenParallelsLinksStorage = ParallelsLinksSerializer.load("ellen");
        }
        if (ellenParallelsLinksStorage == null || ellenParallelsLinksStorage.length == 0)
            ellenParallelsLinksStorage = new ParallelsLinksStorage[55];
        if (index >= 0 && index < ellenParallelsLinksStorage.length) {
            if (ellenParallelsLinksStorage[index] == null)
                ellenParallelsLinksStorage[index] = new ParallelsLinksStorage(chapters);
            return ellenParallelsLinksStorage[index];
        } else return new ParallelsLinksStorage();
    }

    public static ParallelsLinksStorage getOther(int index, int chapters) throws IOException {
        if (otherParallelsLinksStorage == null) {
            otherParallelsLinksStorage = ParallelsLinksSerializer.load("other");
        }
        if (otherParallelsLinksStorage == null || otherParallelsLinksStorage.length == 0)
            otherParallelsLinksStorage = new ParallelsLinksStorage[31];
        if (index >= 0 && index < otherParallelsLinksStorage.length) {
            if (otherParallelsLinksStorage[index] == null)
                otherParallelsLinksStorage[index] = new ParallelsLinksStorage(chapters);
            return otherParallelsLinksStorage[index];
        } else return new ParallelsLinksStorage();
    }

    public static void reset() {
        bibleParallelsLinksStorage = null;
        ellenParallelsLinksStorage = null;
        otherParallelsLinksStorage = null;
    }

    public ParallelsLinksSubStorage get(int index) {
        if (subStorage != null && index >= 0 && index < subStorage.length) {
            if (subStorage[index] == null)
                subStorage[index] = new ParallelsLinksSubStorage();
            return subStorage[index];
        } else return new ParallelsLinksSubStorage();
    }

    public static void update() throws IOException {
        ParallelsLinksSerializer.save(bibleParallelsLinksStorage, "bible");
        ParallelsLinksSerializer.save(ellenParallelsLinksStorage, "ellen");
        ParallelsLinksSerializer.save(otherParallelsLinksStorage, "other");
    }

}
