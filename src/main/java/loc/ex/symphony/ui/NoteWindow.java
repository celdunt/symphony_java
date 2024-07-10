package loc.ex.symphony.ui;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import loc.ex.symphony.Symphony;
import loc.ex.symphony.listview.Note;

import java.io.IOException;

public class NoteWindow {

    private final Stage window = new Stage();

    public NoteWindow(String title, Note note) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Symphony.class.getResource("windows/note-design.fxml"));

        Scene scene = new Scene(fxmlLoader.load(), 300, 300);

        ((NoteController)fxmlLoader.getController()).note = note;
        ((NoteController)fxmlLoader.getController()).initTextAreaHandler();

        window.setTitle(title);
        window.setScene(scene);
    }


    public Stage stage() {
        return window;
    }

}
