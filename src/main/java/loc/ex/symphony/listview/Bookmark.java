package loc.ex.symphony.listview;

import java.io.Serializable;
import java.util.Date;

public record Bookmark(String name, Link link, Date date) implements Serializable {

    @Override
    public String toString() {
        return date().getDay() + "." + (date().getMonth()+1) + "." + (date().getYear()-100) + "    " + name();
    }
}
