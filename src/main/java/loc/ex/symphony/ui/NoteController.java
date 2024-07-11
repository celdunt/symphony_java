package loc.ex.symphony.ui;

import javafx.scene.control.TextArea;
import loc.ex.symphony.listview.Note;

public class NoteController {

    public Note note;

    public TextArea textArea;

    public void initTextAreaHandler() {

        textArea.setText(note.getText());

        textArea.textProperty().addListener(action -> {
            note.setText(textArea.getText());
        });

    }


}
