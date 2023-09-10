package loc.ex.symphony.listview;

import javafx.collections.ObservableList;
import loc.ex.symphony.indexdata.IndexStruct;

import java.util.List;


public class Link {

    private final IndexStruct linkData;
    private final String linkContent;

    private final String[] words;

    public Link(IndexStruct linkData, ObservableList<Book> book, String... words) {
        this.linkData = linkData;
        linkContent =
                book.get(linkData.getBookId()).name.get().replace("-", "\u00A0") + "\u00A0" +
                        (book.get(linkData.getBookId()).getChapters().get(linkData.getChapterId()).number.get()-1) + "\n" +
                        book.get(linkData.getBookId()).getChapters().get(linkData.getChapterId())
                        .getFragments().get(linkData.getFragmentId());
        this.words = words;
    }

    public String[] getWords() {
        return words;
    }


    public IndexStruct getLinkData() {
        return linkData;
    }

    @Override
    public String toString() {
        return linkContent;
    }
}
