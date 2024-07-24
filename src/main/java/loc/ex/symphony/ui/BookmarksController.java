package loc.ex.symphony.ui;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import loc.ex.symphony.file.BookmarksSerializer;
import loc.ex.symphony.listview.BookmarkStruct;

import java.io.IOException;

public class BookmarksController {


    public TableView<BookmarkStruct> bookmarksTableView;
    public ObservableList<BookmarkStruct> observableBookmarksList = FXCollections.observableArrayList();
    public TextField searchField;
    public static ObjectProperty<BookmarkStruct> additionBookmark = new SimpleObjectProperty<>();

    SortedList<BookmarkStruct> sortedList;

    public void initialize() throws IOException {

        initColumnBookmarksTableView();
        initBookmarksList();
        initAdditionBookmark();
        initLoadBookmarks();
        initBookmarkTableViewMouseReaction();

    }

    private void initLoadBookmarks() throws IOException {

        try {
            observableBookmarksList.addAll(BookmarksSerializer.load());
        } catch (IOException exception) {
            System.err.println("failed loaded bookmarks:");
            System.err.println(exception.getMessage());
        }

    }

    private void initBookmarkTableViewMouseReaction() {

        ContextMenu menu = new ContextMenu();
        MenuItem delete = new MenuItem("Удалить закладку");

        delete.onActionProperty().set(action -> {

            if (bookmarksTableView.getSelectionModel().getSelectedIndex() >= 0) {
                observableBookmarksList.remove(bookmarksTableView.getSelectionModel().getSelectedIndex());
                try {
                    BookmarksSerializer.save(observableBookmarksList);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

        });

        menu.getItems().add(delete);

        bookmarksTableView.setRowFactory(rf -> {
            TableRow<BookmarkStruct> row = new TableRow<>();
            row.setOnMouseClicked(mouse -> {

                if (!row.isEmpty() && mouse.getButton() == MouseButton.PRIMARY
                        && mouse.getClickCount() == 2 && bookmarksTableView.getSelectionModel().getSelectedItem() != null) {
                    MainController.openingBookmark.set(row.getItem());
                } else if (!row.isEmpty() && mouse.getButton() == MouseButton.SECONDARY
                        && bookmarksTableView.getSelectionModel().getSelectedItem() != null) {
                    menu.show(bookmarksTableView, mouse.getScreenX(), mouse.getScreenY());
                } else menu.hide();

            });
            return row;
        });

    }

    public void initAdditionBookmark() {

        additionBookmark.addListener(change -> {
            if (additionBookmark.getValue() != null) {
                observableBookmarksList.add(additionBookmark.getValue());
                try {
                    BookmarksSerializer.save(observableBookmarksList);
                } catch (IOException e) {
                    System.err.println(e.getMessage());
                }
            }
        });

    }

    public void initColumnBookmarksTableView() {

        TableColumn<BookmarkStruct, String> dateColumn = new TableColumn<>("Дата");
        dateColumn.setCellValueFactory(param -> param.getValue().dateProperty());
        dateColumn.setSortable(true);
        dateColumn.setMinWidth(80);
        bookmarksTableView.getColumns().add(dateColumn);

        TableColumn<BookmarkStruct, String> linkColumn = new TableColumn<>("Ссылка");
        linkColumn.setCellValueFactory(param -> param.getValue().linkProperty());
        linkColumn.setSortable(true);
        linkColumn.setMinWidth(80);
        bookmarksTableView.getColumns().add(linkColumn);

        TableColumn<BookmarkStruct, String> contentColumn = new TableColumn<>("Текст");
        contentColumn.setCellValueFactory(param -> param.getValue().contentProperty());
        contentColumn.setSortable(true);
        contentColumn.setMinWidth(80);
        bookmarksTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_SUBSEQUENT_COLUMNS);
        bookmarksTableView.getColumns().add(contentColumn);

    }

    public void initBookmarksList() {

        sortedList = new SortedList<>(observableBookmarksList);
        sortedList.comparatorProperty().bind(bookmarksTableView.comparatorProperty());
        FilteredList<BookmarkStruct> filteredList = new FilteredList<>(sortedList);
        searchField.textProperty().addListener((obs, old, _new) -> {
            filteredList.setPredicate(data -> {

                if (_new == null || _new.isEmpty()) {
                    return true;
                }

                String[] finds = _new.toLowerCase().split(" ");
                if (checkContains(data.get_date(), finds)) return true;
                else if (checkContains(data.get_link(), finds)) return true;
                else return checkContains(data.get_content(), finds);

            });
        });

        bookmarksTableView.setItems(filteredList);

    }

    private boolean checkContains(String str, String[] arr) {

        for (var text : arr) {
            if (!str.toLowerCase().contains(text.toLowerCase())) return false;
        }
        return true;

    }
}
