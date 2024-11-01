package loc.ex.symphony.listview;

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.ListCell;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import loc.ex.symphony.indexdata.IndexStruct;
import loc.ex.symphony.ui.MainController;
import org.fxmisc.richtext.StyleClassedTextArea;

import java.nio.file.Path;
import java.util.Comparator;
import java.util.HashMap;

public class LinkCell<T> extends ListCell<T> {

    StyleClassedTextArea tarea = new StyleClassedTextArea();
    private final int width;

    private MainController controller;

    public LinkCell(int width, MainController controller) {
        this.width = width-33;
        this.controller = controller;

        defineTArea();
    }

    public LinkCell(MainController controller) {
        this.width = 145;
        this.controller = controller;

        defineTArea();
    }


    private void defineTArea() {
        tarea.setStyle("""
                    -fx-background-color: white;
                    """);
        tarea.setMouseTransparent(true);
        tarea.setEditable(false);
        tarea.setWrapText(true);
        tarea.setMaxWidth(width);
        tarea.setPrefWidth(width);
        tarea.setMinWidth(width);
        tarea.setAutoHeight(true);
        tarea.setPadding(new Insets(0, 0, 0, 3));
    }

    @Override
    public void updateSelected(boolean b) {
        super.updateSelected(b);

        if (b) {
            tarea.setStyle("""
                    -fx-background-color: #ffdcdc;
                    """);
        } else {
            tarea.setStyle("""
                    -fx-background-color: white;
                    """);
        }
    }

    @Override
    protected void updateItem(T item, boolean empty) {
        super.updateItem(item, empty);

        if (empty || item == null) {
            setText(null);
            setGraphic(null);
        } else {
            tarea.clear();
            tarea.append(item.toString(), "");

            if (item instanceof Link) {
                highlight((Link) item, tarea);
            }

            setGraphic(tarea);
        }
    }

    private void highlight(Link item, StyleClassedTextArea tarea) {
        HashMap<Integer, String> uniqueWords = item.root == PathsEnum.Bible ? controller.b_uniqueWord
                : item.root == PathsEnum.EllenWhite ? controller.e_uniqueWord : controller.o_uniqueWord;

        String mainText = item.toString().toLowerCase();

        item.references.sort(Comparator.comparingInt(IndexStruct::getPosition));

        for (int i = 0; i < item.references.size(); i++) {

            int start = item.references.get(i).position;
            String word = uniqueWords.get(item.references.get(i).getWordKey()).toLowerCase();
            int end = start + word.length();

            while (start < mainText.length()-1 && !mainText.substring(start, end).equals(word)) {
                start++;
                end++;
            }

            tarea.setStyleClass(start, end, "fill-text");

        }

    }

}
