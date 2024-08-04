package loc.ex.symphony.controls;

import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.scene.control.IndexRange;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import org.fxmisc.richtext.CharacterHit;
import org.fxmisc.richtext.StyleClassedTextArea;

import java.util.Optional;

public class NoteStyledTextArea extends Region {

    private StyleClassedTextArea textArea = new StyleClassedTextArea();
    private Pane container = new Pane();

    public NoteStyledTextArea() {
        container.getChildren().add(textArea);
        this.getChildren().add(container);
    }

    public void setStyleClass(int from, int to,  String styleClass) {
        this.textArea.setStyleClass(from, to, styleClass);
    }

    public String getText() {
        return this.textArea.getText();
    }

    public void moveTo(int pos) {
        this.textArea.moveTo(pos);
    }

    public void requestFocus() {
        this.textArea.requestFocus();
    }

    public boolean hasFocused() {
        return this.textArea.isFocused();
    }

    public String getSelectedText() {
        return this.textArea.getSelectedText();
    }

    public IndexRange getSelection() {
        return this.textArea.getSelection();
    }

    public CharacterHit hit(double x, double y) {
        return this.textArea.hit(x, y);
    }

    public ObservableValue<IndexRange> selectionProperty() {
        return this.textArea.selectionProperty();
    }

    public Optional<Bounds> getCharacterBoundsOnScreen(int s, int e) {
        return this.textArea.getCharacterBoundsOnScreen(s, e);
    }

    public void setOnMousePressedAction(EventHandler<? super MouseEvent> event) {
        this.textArea.setOnMousePressed(event);
    }

    public void clear() {
        this.textArea.clear();
    }

    public void insertText(int position, String text) {
        this.textArea.insertText(position, text);
    }

    public void requestFollowCaret() {
        this.textArea.requestFollowCaret();
    }

    public void append(String text, String styleClass) {
        this.textArea.append(text, styleClass);
    }

}
