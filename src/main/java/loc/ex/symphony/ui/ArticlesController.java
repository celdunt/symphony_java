package loc.ex.symphony.ui;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import loc.ex.symphony.file.ArticleSerializer;
import loc.ex.symphony.listview.Article;

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
