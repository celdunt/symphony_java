package loc.ex.symphony.listview;

import javafx.beans.property.SimpleStringProperty;

import java.util.ArrayList;
import java.util.List;

public class Book {

    public final SimpleStringProperty name = new SimpleStringProperty();
    public final PathsEnum root;
    private final List<Chapter> chapters = new ArrayList<>();

    public Book(String name, PathsEnum root) {
        this.name.set(name);
        this.root = root;
    }

    public List<Chapter> getChapters() {
        return chapters;
    }

    @Override
    public String toString() {
        return name.get();
    }
}
