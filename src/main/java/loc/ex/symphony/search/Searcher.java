package loc.ex.symphony.search;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import loc.ex.symphony.indexdata.IndexSaver;
import loc.ex.symphony.indexdata.IndexStruct;
import loc.ex.symphony.listview.Book;
import loc.ex.symphony.listview.Link;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class Searcher {

    private ObservableList<Book> resource;
    private final ConcurrentHashMap<String, List<IndexStruct>> indexData;

    public Searcher() {
        indexData = IndexSaver.load();
    }

    public void setResource(ObservableList<Book> resource) {
        this.resource = resource;
    }

    public ObservableList<Link> search(String prompt) {
        List<IndexStruct> linkData = indexData.get(prompt);
        if (linkData != null)
            return FXCollections.observableArrayList(linkData.stream().map(x -> new Link(x, resource)).toList());
        else return FXCollections.observableArrayList();
    }
}
