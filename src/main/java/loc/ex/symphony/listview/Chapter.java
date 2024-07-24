package loc.ex.symphony.listview;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import javafx.beans.property.SimpleIntegerProperty;

import java.util.ArrayList;
import java.util.List;


public class Chapter {

    public Chapter(int number) {
        this.number = new SimpleIntegerProperty(number);
        this.fragments = new ArrayList<>();
        this.num = number;
    }

    @JsonCreator
    public Chapter(
            @JsonProperty("num") int num,
            @JsonProperty("fragments") List<String> fragments
    ) {
        this.number = new SimpleIntegerProperty(num);
        this.fragments = fragments;
    }

    @JsonIgnore public SimpleIntegerProperty number;
    public int num;
    public final List<String> fragments;

    public List<String> getFragments() {
        return fragments;
    }

    @JsonIgnore public String getEntireText() {
        return String.join("", fragments);
    }
}
