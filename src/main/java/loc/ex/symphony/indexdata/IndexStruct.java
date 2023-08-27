package loc.ex.symphony.indexdata;

import javafx.beans.property.SimpleIntegerProperty;
import loc.ex.symphony.listview.PathsEnum;

public class IndexStruct {

    public PathsEnum root;
    public SimpleIntegerProperty bookId = new SimpleIntegerProperty();
    public SimpleIntegerProperty chapterId = new SimpleIntegerProperty();
    public SimpleIntegerProperty fragmentId = new SimpleIntegerProperty();
    public SimpleIntegerProperty position = new SimpleIntegerProperty();
    public SimpleIntegerProperty wordLength = new SimpleIntegerProperty();
}
