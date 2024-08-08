package loc.ex.symphony.listview;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import javafx.beans.property.SimpleStringProperty;

import java.util.ArrayList;
import java.util.List;

@JsonAutoDetect
public class Book {

    @JsonIgnore public SimpleStringProperty name;
    public String _name;
    public  PathsEnum root;
    public  List<Chapter> chapters;

    @JsonCreator
    public Book(
            @JsonProperty("_name") String _name,
            @JsonProperty("root") PathsEnum root,
            @JsonProperty("chapters") List<Chapter> chapters
    ) {
        this.name = new SimpleStringProperty(_name);
        this.root = root;
        this.chapters = chapters;
        this._name = _name;
    }

    public Book(String name, PathsEnum root) {
        chapters = new ArrayList<>();
        this.name = new SimpleStringProperty();
        this.name.set(name);
        _name = name;
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
