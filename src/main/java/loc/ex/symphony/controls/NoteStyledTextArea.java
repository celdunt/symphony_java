package loc.ex.symphony.controls;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.control.IndexRange;
import javafx.scene.control.ScrollBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.*;
import javafx.util.Duration;
import loc.ex.symphony.Symphony;
import loc.ex.symphony.listview.*;
import loc.ex.symphony.ui.MainController;
import org.fxmisc.flowless.Virtualized;
import org.fxmisc.flowless.VirtualizedScrollPane;
import org.fxmisc.richtext.CharacterHit;
import org.fxmisc.richtext.StyleClassedTextArea;
import org.fxmisc.richtext.model.StyleSpans;
import org.reactfx.value.Val;
import org.reactfx.value.Var;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public class NoteStyledTextArea extends Region implements Virtualized {

    private StyleClassedTextArea textArea = new StyleClassedTextArea();
    private StackPane pane = new StackPane();
    private GridPane container = new GridPane();
    InputStream urlStream = Symphony.class.getResourceAsStream("buttons/to-note.png");
    private MainController controller;
    private VirtualizedScrollPane spane;

    public NoteStyledTextArea(MainController controller) {

        pane.prefWidthProperty().bind(this.prefWidthProperty());
        pane.prefHeightProperty().bind(this.prefHeightProperty());
        container.prefWidthProperty().bind(pane.prefWidthProperty());
        container.prefHeightProperty().bind(pane.prefHeightProperty());
        textArea.prefWidthProperty().bind(container.prefWidthProperty());
        textArea.prefHeightProperty().bind(container.prefHeightProperty());

        ColumnConstraints column = new ColumnConstraints();
        column.setHgrow(Priority.ALWAYS);
        container.getColumnConstraints().add(column);

        RowConstraints row = new RowConstraints();
        row.setVgrow(Priority.ALWAYS);
        container.getRowConstraints().add(row);

        pane.getChildren().add(container);
        container.getChildren().add(textArea);
        this.getChildren().add(container);

        textArea.setStyle("-fx-font-size: 14px");
        textArea.setPadding(new Insets(0, 0, 0, 7));

        defineScrollBehavior();

        this.controller = controller;

    }

    public void setSPane(VirtualizedScrollPane pane) {
        spane = pane;
    }

    public void display(List<Note> notes) {

        double y = (double) spane.estimatedScrollYProperty().getValue();

        notes.sort(Comparator.comparing(Note::getFrom));
        reboot();

        for (Note note : notes) {
            textArea.insertText(note.getTo(), "\uD83D\uDCDD");
            setStyleClass(note.getFrom(), note.getTo(), "note");
        }

        try {
            controller.getParallelLinkForSelectedChapter().display(controller.currentTArea);
            controller.getTHelperForSelectedChapter().display(controller.currentTArea);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Platform.runLater(() -> {
            Timeline tm = new Timeline(new KeyFrame(
                    Duration.millis(100),
                    ae -> {
                        spane.scrollYToPixel(0d);
                    }
            ), new KeyFrame(
                    Duration.millis(150),
                    ae -> {
                        spane.scrollYToPixel(y);
                    }
            ), new KeyFrame(
                    Duration.millis(200),
                    ae -> {
                        spane.scrollYToPixel(y);
                    }
            ));
            tm.play();
        });

    }

    private void reboot() {

        controller.currentTArea.clear();
        controller.currentTArea.setStyleClass(0, 0, "jtext");
        controller.currentTArea.insertText(0, controller.selectedBook.getChapters().get(controller.chapterListView.getSelectionModel().getSelectedItem()).getEntireText());

    }

    private void defineScrollBehavior() {

        textArea.addEventFilter(ScrollEvent.SCROLL, event -> {
            if (event.isControlDown()) {
                String currentStyle = textArea.getStyle();
                double currentFontSize = extractFontSize(currentStyle);
                double newFontSize = currentFontSize + (event.getDeltaY() > 0 ? 1 : -1);

                String newStyle = updateFontSize(currentStyle, newFontSize);
                textArea.setStyle(newStyle);

                event.consume();
            }
        });

    }

    private String updateFontSize(String style, double newSize) {
        String[] styles = style.split(";");
        StringBuilder newStyle = new StringBuilder();
        for (String s : styles) {
            if (s.trim().startsWith("-fx-font-size")) {
                newStyle.append("-fx-font-size: ").append(newSize).append("px;");
            } else {
                newStyle.append(s).append(";");
            }
        }
        return newStyle.toString();
    }

    private double extractFontSize(String style) {
        String[] styles = style.split(";");
        for (String s : styles) {
            if (s.trim().startsWith("-fx-font-size")) {
                String size = s.split(":")[1].trim().replace("px", "");
                return Double.parseDouble(size);
            }
        }
        return 14.0;
    }

    @Override
    protected void layoutChildren() {
        super.layoutChildren();
        container.resizeRelocate(0, 0, getWidth(), getHeight());
    }

    public void setStyleClass(int from, int to, String styleClass) {
        this.textArea.setStyleClass(from, to, styleClass);
    }

    public String getText() {
        return this.textArea.getText();
    }

    public String getText(int from, int to) {
        return this.textArea.getText(from, to);
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

    public BooleanProperty editableProperty() {
        return this.textArea.editableProperty();
    }

    @Override
    public Val<Double> totalWidthEstimateProperty() {
        return this.textArea.totalWidthEstimateProperty();
    }

    @Override
    public Val<Double> totalHeightEstimateProperty() {
        return this.textArea.totalHeightEstimateProperty();
    }

    @Override
    public Var<Double> estimatedScrollXProperty() {
        return this.textArea.estimatedScrollXProperty();
    }

    @Override
    public Var<Double> estimatedScrollYProperty() {
        return this.textArea.estimatedScrollYProperty();
    }

    public double getEstimatedScrollY() {
        return this.textArea.getEstimatedScrollY();
    }

    @Override
    public void scrollXBy(double v) {
        this.textArea.scrollXBy(v);
    }

    @Override
    public void scrollYBy(double v) {
        this.textArea.scrollYBy(v);
    }

    @Override
    public void scrollXToPixel(double v) {
        this.textArea.scrollXToPixel(v);
    }

    @Override
    public void scrollYToPixel(double v) {
        this.textArea.scrollYToPixel(v);
    }

    public int getCaretPosition() {
        return this.textArea.getCaretPosition();
    }

    public void selectRange(int start, int end) {
        this.textArea.selectRange(start, end);
    }

    public void setWrapText(boolean wrapText) {
        this.textArea.setWrapText(wrapText);
    }

    public Collection<String> getStyleOfChar(int index) {
        return this.textArea.getStyleOfChar(index);
    }

    public StyleSpans<Collection<String>> getStyleSpans(int start, int end) {
        return this.textArea.getStyleSpans(start, end);
    }

    public int getLength() {
        return this.textArea.getLength();
    }

    public void deleteText(int from, int to) {
        this.textArea.deleteText(from, to);
    }

    public void setEditable(boolean editable) {
        this.textArea.setEditable(editable);
    }

}
