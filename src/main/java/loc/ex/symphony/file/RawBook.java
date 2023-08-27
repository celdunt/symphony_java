package loc.ex.symphony.file;

import javafx.beans.property.SimpleStringProperty;

public class RawBook {

    public final SimpleStringProperty name = new SimpleStringProperty();
    public final SimpleStringProperty text = new SimpleStringProperty();

    public RawBook(String name, String text) {

        this.name.set(name);
        this.text.set(text);

    }

}
