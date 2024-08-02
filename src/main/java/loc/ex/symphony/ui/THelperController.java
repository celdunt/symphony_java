package loc.ex.symphony.ui;

import javafx.scene.control.TextArea;
import loc.ex.symphony.listview.Note;
import loc.ex.symphony.listview.TranslateHelper;

public class THelperController {

    public TranslateHelper thelper;

    public TextArea textArea;

    public void initTextAreaHandler() {

        textArea.setText(thelper.getText());

        textArea.textProperty().addListener(action -> {
            thelper.setText(textArea.getText());
        });

    }

}
