package loc.ex.symphony.listview;

import javafx.beans.property.SimpleIntegerProperty;

import java.util.ArrayList;
import java.util.List;


public class Chapter {

    public Chapter(int number) {
        this.number.set(number);
    }

    public final SimpleIntegerProperty number = new SimpleIntegerProperty();
    private final List<String> fragments = new ArrayList<>();

    public List<String> getFragments() {
        return fragments;
    }

    public String getEntireText() {
        return String.join("", fragments);
    }
}
