package loc.ex.symphony.ui;

import javafx.collections.FXCollections;
import javafx.scene.control.ListView;
import loc.ex.symphony.file.BookmarksSerializer;
import loc.ex.symphony.listview.Bookmark;
import javafx.collections.ObservableList;
import loc.ex.symphony.listview.RichCell;


public class BookmarksController {
    public ListView<Bookmark> bookmarksListView;
    public static ObservableList<Bookmark> bookmarks = FXCollections.observableArrayList();

    public static Bookmark selectedBookmark;
    public static boolean isOpenBookmark = false;

    public void initialize() {
        bookmarks = BookmarksSerializer.Deserialize();

        bookmarksListView.setCellFactory(param -> new RichCell<>(350));

        bookmarksListView.setItems(bookmarks);
    }

    public void OpenBookmark__Action() {
        selectedBookmark = bookmarksListView.getSelectionModel().getSelectedItem();
        isOpenBookmark = true;

        MainController.listener.set("132");
        if (!MainController.listener.get().isEmpty()) MainController.listener.set("");

        MainController.bookmarksWindow.GetStage().close();
    }

    public void DeleteBookmark__Action() {
        if (bookmarksListView.getSelectionModel().getSelectedIndex() > -1) {
            bookmarks.remove(bookmarksListView.getSelectionModel().getSelectedIndex());
            BookmarksSerializer.Serialize(bookmarks);
        }
    }

}
