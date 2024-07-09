package loc.ex.symphony.listview;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import javafx.beans.property.SimpleIntegerProperty;

import java.util.ArrayList;
import java.util.List;


public class Chapter {

    public Chapter(int number) {
        this.number = new SimpleIntegerProperty();
        this.fragments = new ArrayList<>();
        this.number.set(number);
    }

    @JsonCreator
    public Chapter(
            @JsonProperty("number") SimpleIntegerProperty number,
            @JsonProperty("fragments") List<String> fragments
    ) {
        this.number = number;
        this.fragments = fragments;
    }

    public final SimpleIntegerProperty number;
    public final List<String> fragments;

    public List<String> getFragments() {
        return fragments;
    }

    public String getEntireText() {
        return String.join("", fragments);
    }
}
