package loc.ex.symphony.listview;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import loc.ex.symphony.file.NotesSerializer;

import java.io.IOException;

@JsonAutoDetect
public class NotesStorage {

    @JsonIgnore private static NotesStorage[] bibleNoteStorage;
    @JsonIgnore private static NotesStorage[] ellenNoteStorage;
    @JsonIgnore private static NotesStorage[] otherNoteStorage;

    public NotesSubStorage[] notesSubStorage;

    public static NotesStorage getBible(int index, int chapters) throws IOException {
        if (bibleNoteStorage == null) {
            bibleNoteStorage = NotesSerializer.load("bible");
        }
        if (bibleNoteStorage == null || bibleNoteStorage.length == 0)
            bibleNoteStorage = new NotesStorage[66];
        if (index >= 0 && index < bibleNoteStorage.length){
            if (bibleNoteStorage[index] == null)
                bibleNoteStorage[index] = new NotesStorage(chapters);
            return bibleNoteStorage[index];
        }
        else return new NotesStorage();
    }

    public static NotesStorage getEllen(int index, int chapters) throws IOException {
        if (ellenNoteStorage == null) {
            ellenNoteStorage = NotesSerializer.load("ellen");
        }
        if (ellenNoteStorage == null || ellenNoteStorage.length == 0)
            ellenNoteStorage = new NotesStorage[55];
        if (index >= 0 && index < ellenNoteStorage.length){
            if (ellenNoteStorage[index] == null)
                ellenNoteStorage[index] = new NotesStorage(chapters);
            return ellenNoteStorage[index];
        }
        else return new NotesStorage();
    }

    public static NotesStorage getOther(int index, int chapters) throws IOException {
        if (otherNoteStorage == null) {
            otherNoteStorage = NotesSerializer.load("other");
        }
        if (otherNoteStorage == null || otherNoteStorage.length == 0)
            otherNoteStorage = new NotesStorage[55];
        if (index >= 0 && index < otherNoteStorage.length){
            if (otherNoteStorage[index] == null)
                otherNoteStorage[index] = new NotesStorage(chapters);
            return otherNoteStorage[index];
        }
        else return new NotesStorage();
    }

    public static void update() throws IOException {
        NotesSerializer.save(bibleNoteStorage, "bible");
        NotesSerializer.save(ellenNoteStorage, "ellen");
        NotesSerializer.save(otherNoteStorage, "other");
    }

    @JsonCreator
    public NotesStorage(@JsonProperty("subNotesStorage") NotesSubStorage[] notesSubStorage) {
        this.notesSubStorage = notesSubStorage;
    }

    public NotesStorage(int chapters) {
        notesSubStorage = new NotesSubStorage[chapters];
    }

    public NotesStorage() {}

    public NotesSubStorage get(int index) {
        if (notesSubStorage != null && index >= 0 && index < notesSubStorage.length) {
            if (notesSubStorage[index] == null)
                notesSubStorage[index] = new NotesSubStorage();
            return notesSubStorage[index];
        }
        else return new NotesSubStorage();
    }

}
