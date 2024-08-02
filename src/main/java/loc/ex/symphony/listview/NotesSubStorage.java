package loc.ex.symphony.listview;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import javafx.geometry.Insets;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import loc.ex.symphony.Symphony;
import org.fxmisc.richtext.InlineCssTextArea;
import org.fxmisc.richtext.StyleClassedTextArea;
import org.fxmisc.richtext.TextExt;
import org.fxmisc.richtext.model.SimpleEditableStyledDocument;
import org.fxmisc.richtext.model.StyleSpan;
import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyledDocument;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
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
        else return notes.get(0);
    }

    public void add(Note note) {
        notes.add(note);
    }

    public Note getFromPos(int pos) {
        for (Note note : notes) {
            if (note.getFrom() <= pos && note.getTo() >= pos) {
                return note;
            }
        }
        return new Note(0, 1, "error");
    }

    public void remove(int index) {
        for (int i = index; i < notes.size(); i++) {
            notes.get(i).setFrom(notes.get(i).getFrom()-1);
            notes.get(i).setTo(notes.get(i).getTo()-1);
        }
        notes.remove(index);
    }

    public void display(StyleClassedTextArea textArea) {

        double y = textArea.getEstimatedScrollY();

        for (Note note : notes) {
            textArea.setStyleClass(note.getFrom(), note.getTo(), "note");
        }

        if (y == 0d) textArea.moveTo(0);

    }


    public int size() {
        return notes.size();
    }

}
