package loc.ex.symphony.ui;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import loc.ex.symphony.file.ArticleSerializer;
import loc.ex.symphony.file.BookmarksSerializer;
import loc.ex.symphony.listview.Article;
import loc.ex.symphony.listview.BookmarkStruct;

import java.io.IOException;

public class ArticlesController {


    public TableView<Article> articlesTableView;
    public TextField searchField;
    public ObservableList<Article> articleObservableList = FXCollections.observableArrayList();
    SortedList<Article> sortedList;

    public static ObjectProperty<Article> additionArticle = new SimpleObjectProperty<>();

    public void initialize() throws IOException {

        initColumnTableView();
        initArticleList();
        initLoadArticles();
        initAdditionArticle();
        initArticleListViewMouseReaction();

    }

    public void initAdditionArticle() {

        additionArticle.addListener(change -> {
            if (additionArticle.getValue() != null) {
                articleObservableList.add(additionArticle.getValue());
                try {
                    ArticleSerializer.save(articleObservableList);
                } catch (IOException e) {
                    System.err.println(e.getMessage());
                }
            }
        });

    }

    public void initColumnTableView() {

        TableColumn<Article, String> dateColumn = new TableColumn<>("Дата");
        dateColumn.setCellValueFactory(param -> param.getValue().dateProperty());
        dateColumn.setSortable(true);
        articlesTableView.getColumns().add(dateColumn);

        TableColumn<Article, String> linkColumn = new TableColumn<>("Имя");
        linkColumn.setCellValueFactory(param -> param.getValue().nameProperty());
        linkColumn.setSortable(true);
        articlesTableView.getColumns().add(linkColumn);

        articlesTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);

    }

    private void initArticleListViewMouseReaction() {

        ContextMenu menu = new ContextMenu();
        MenuItem delete = new MenuItem("Удалить тему");

        delete.onActionProperty().set(action -> {

            if (articlesTableView.getSelectionModel().getSelectedIndex() >= 0) {
                articleObservableList.remove(articlesTableView.getSelectionModel().getSelectedIndex());
                try {
                    ArticleSerializer.save(articleObservableList);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

        });

        menu.getItems().add(delete);

        articlesTableView.setRowFactory(rf -> {
            TableRow<Article> row = new TableRow<>();
            row.setOnMouseClicked(mouse -> {

                if (!row.isEmpty() && mouse.getButton() == MouseButton.PRIMARY
                        && mouse.getClickCount() == 2 && articlesTableView.getSelectionModel().getSelectedItem() != null) {
                    MainController.openingArticle.set(row.getItem());
                } else if (!row.isEmpty() && mouse.getButton() == MouseButton.SECONDARY
                        && articlesTableView.getSelectionModel().getSelectedItem() != null) {
                    menu.show(articlesTableView, mouse.getScreenX(), mouse.getScreenY());
                } else menu.hide();

            });
            return row;
        });

    }

    private void initLoadArticles() throws IOException {

        try {
            articleObservableList.addAll(ArticleSerializer.load());
        } catch (IOException exception) {
            System.err.println("failed loaded articles:");
            System.err.println(exception.getMessage());
        }

    }

    public void initArticleList() {

        sortedList = new SortedList<>(articleObservableList);
        sortedList.comparatorProperty().bind(articlesTableView.comparatorProperty());
        FilteredList<Article> filteredList = new FilteredList<>(sortedList);
        searchField.textProperty().addListener((obs, old, _new) -> {
            filteredList.setPredicate(data -> {

                if (_new == null || _new.isEmpty()) {
                    return true;
                }

                String[] finds = _new.toLowerCase().split(" ");
                if (checkContains(data.getDate(), finds)) return true;
                else return checkContains(data.getName(), finds);

            });
        });

        articlesTableView.setItems(filteredList);

    }

    private boolean checkContains(String str, String[] arr) {

        for (var text : arr) {
            if (!str.toLowerCase().contains(text.toLowerCase())) return false;
        }
        return true;

    }

}
