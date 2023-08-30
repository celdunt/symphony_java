package loc.ex.symphony.listview;

import javafx.collections.ObservableList;
import loc.ex.symphony.indexdata.IndexStruct;


public class Link {

    private final IndexStruct linkData;
    private final String linkContent;

    public Link(IndexStruct linkData, ObservableList<Book> book) {
        this.linkData = linkData;
        linkContent =
                book.get(linkData.getBookId()).name.get().replace("-", "\u00A0") + "\u00A0" +
                book.get(linkData.getBookId()).getChapters().get(linkData.getChapterId()).number.get() + "\n" +
                book.get(linkData.getBookId()).getChapters().get(linkData.getChapterId())
                        .getFragments().get(linkData.getFragmentId());
    }

    public IndexStruct getLinkData() {
        return linkData;
    }

    @Override
    public String toString() {
        return linkContent;
    }
}
