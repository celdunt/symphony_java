package loc.ex.symphony.ui;

import javafx.scene.control.TextArea;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;

public class RichTextArea extends StackPane {

    //3 слоя, задник -- ректангл, затем ректангл, затем текст ареа

    private final TextArea textArea = new TextArea();
    private final Rectangle highlight = new Rectangle();

    public RichTextArea() {
        textArea.setWrapText(true);
        textArea.setPrefSize(200, 200);

        this.getChildren().add(textArea);
    }

    public void setText(String text) {
        textArea.setText(text);
    }

    public String getText() {
        return textArea.getText();
    }


    public void selectRange(int positionCarret, int positionCarret1) {
        textArea.selectRange(positionCarret, positionCarret1);
    }
}
