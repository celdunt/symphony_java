package loc.ex.symphony.listview;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.fxmisc.richtext.StyleClassedTextArea;

import java.util.ArrayList;
import java.util.List;

@JsonAutoDetect
public class NotesSubStorage {

    @JsonCreator
    public NotesSubStorage(@JsonProperty("notes") List<Note> notes) {
        this.notes = notes;
    }

    public NotesSubStorage() {}

    public List<Note> notes = new ArrayList<>();

    public Note get(int index) {
        if (index >= 0 && index < notes.size())
            return notes.get(index);
        else return notes.getFirst();
    }

    public void add(Note note) {
        notes.add(note);
    }

    public void remove(int index) {
        notes.remove(index);
    }

    public void display(StyleClassedTextArea textArea) {
        for (Note note : notes) {
            textArea.setStyleClass(note.getFrom(), note.getTo(), "note");
        }
    }

    public int size() {
        return notes.size();
    }

}
