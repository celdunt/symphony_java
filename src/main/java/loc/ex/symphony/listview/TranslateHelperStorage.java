package loc.ex.symphony.listview;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import loc.ex.symphony.file.TranslateHelperSerializer;

import java.io.IOException;

@JsonAutoDetect
public class TranslateHelperStorage {

    @JsonIgnore
    private static TranslateHelperStorage[] bibleTHelperStorage;
    @JsonIgnore private static TranslateHelperStorage[] ellenTHelperStorage;
    @JsonIgnore private static TranslateHelperStorage[] otherTHelperStorage;

    public TranslateHelperSubStorage[] thelperSubStorage;

    public static TranslateHelperStorage getBible(int index, int chapters) throws IOException {
        if (bibleTHelperStorage == null) {
            bibleTHelperStorage = TranslateHelperSerializer.load("bible");
        }
        if (bibleTHelperStorage == null || bibleTHelperStorage.length == 0)
            bibleTHelperStorage = new TranslateHelperStorage[66];
        if (index >= 0 && index < bibleTHelperStorage.length){
            if (bibleTHelperStorage[index] == null)
                bibleTHelperStorage[index] = new TranslateHelperStorage(chapters);
            return bibleTHelperStorage[index];
        }
        else return new TranslateHelperStorage();
    }

    public static TranslateHelperStorage getEllen(int index, int chapters) throws IOException {
        if (ellenTHelperStorage == null) {
            ellenTHelperStorage = TranslateHelperSerializer.load("ellen");
        }
        if (ellenTHelperStorage == null || ellenTHelperStorage.length == 0)
            ellenTHelperStorage = new TranslateHelperStorage[55];
        if (index >= 0 && index < ellenTHelperStorage.length){
            if (ellenTHelperStorage[index] == null)
                ellenTHelperStorage[index] = new TranslateHelperStorage(chapters);
            return ellenTHelperStorage[index];
        }
        else return new TranslateHelperStorage();
    }

    public static TranslateHelperStorage getOther(int index, int chapters) throws IOException {
        if (otherTHelperStorage == null) {
            otherTHelperStorage = TranslateHelperSerializer.load("other");
        }
        if (otherTHelperStorage == null || otherTHelperStorage.length == 0)
            otherTHelperStorage = new TranslateHelperStorage[55];
        if (index >= 0 && index < otherTHelperStorage.length){
            if (otherTHelperStorage[index] == null)
                otherTHelperStorage[index] = new TranslateHelperStorage(chapters);
            return otherTHelperStorage[index];
        }
        else return new TranslateHelperStorage();
    }

    public static void update() throws IOException {
        TranslateHelperSerializer.save(bibleTHelperStorage, "bible");
        TranslateHelperSerializer.save(ellenTHelperStorage, "ellen");
        TranslateHelperSerializer.save(otherTHelperStorage, "other");
    }

    @JsonCreator
    public TranslateHelperStorage(@JsonProperty("thelperSubStorage") TranslateHelperSubStorage[] thelperSubStorage) {
        this.thelperSubStorage = thelperSubStorage;
    }

    public TranslateHelperStorage(int chapters) {
        thelperSubStorage = new TranslateHelperSubStorage[chapters];
    }

    public TranslateHelperStorage() {}

    public TranslateHelperSubStorage get(int index) {
        if (thelperSubStorage != null && index >= 0 && index < thelperSubStorage.length) {
            if (thelperSubStorage[index] == null)
                thelperSubStorage[index] = new TranslateHelperSubStorage();
            return thelperSubStorage[index];
        }
        else return new TranslateHelperSubStorage();
    }

}
