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

    private NotesSubStorage[] notesSubStorage;

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

    public static void update() throws IOException {
        NotesSerializer.save(bibleNoteStorage, "bible");
        NotesSerializer.save(ellenNoteStorage, "ellen");
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
