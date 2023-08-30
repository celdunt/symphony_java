package loc.ex.symphony.listview;

import javafx.scene.control.ListCell;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

public class LinkCell<T> extends ListCell<T> {

    @Override
    protected void updateItem(T item, boolean empty) {
        super.updateItem(item, empty);

        if (empty || item == null) {
            setText(null);
            setGraphic(null);
        } else {
            Text text = new Text(item.toString());
            int cellWidth = 145;

            setMinWidth(cellWidth);
            setMaxWidth(cellWidth);
            setPrefWidth(cellWidth);

            text.setTextAlignment(TextAlignment.RIGHT);
            text.setWrappingWidth(cellWidth);

            setGraphic(text);
        }
    }

}
