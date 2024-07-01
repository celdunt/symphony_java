package loc.ex.symphony.listview;

import javafx.scene.control.ListCell;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

public class RichCell<T> extends ListCell<T> {

    private final int width;

    public RichCell(int width) {
        this.width = width;
    }

    public RichCell() {
        this.width = 145;
    }

    @Override
    protected void updateItem(T item, boolean empty) {
        super.updateItem(item, empty);

        if (empty || item == null) {
            setText(null);
            setGraphic(null);
        } else {
            Text text = new Text(item.toString());
            int cellWidth = width;

            setMinWidth(cellWidth);
            setMaxWidth(cellWidth);
            setPrefWidth(cellWidth);

            text.setTextAlignment(TextAlignment.JUSTIFY);
            text.setWrappingWidth(cellWidth);

            setGraphic(text);
        }
    }

}
