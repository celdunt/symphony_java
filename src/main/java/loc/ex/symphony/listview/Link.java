package loc.ex.symphony.listview;

import javafx.collections.ObservableList;
import loc.ex.symphony.indexdata.IndexStruct;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public class Link implements Serializable {

    private final List<IndexStruct> references = new ArrayList<>();

    public String getLinkContent() {
        return linkContent;
    }

    private final String linkContent;

    private final String[] words;

    public Link(List<IndexStruct> references, ObservableList<Book> book, String... words) {
        this.references.addAll(references);

        String bookName = book.get(references.getFirst().getBookID()).name.get();
        String chapterNumber = (book.get(references.getFirst().getBookID()).getChapters().get(references.getFirst().getChapterID()).number.get()-1) + "";
        String fragmentText = book.get(references.getFirst().getBookID()).getChapters().get(references.getFirst().getChapterID())
                .getFragments().get(references.getFirst().getFragmentID());
        linkContent = bookName + " : " + chapterNumber + "\n" + fragmentText;

        this.words = words;
    }

    public String[] getWords() {
        return words;
    }


    public List<IndexStruct> getReferences() {
        return references;
    }

    @Override
    public String toString() {
        return linkContent;
    }
}
