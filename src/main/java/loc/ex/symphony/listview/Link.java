package loc.ex.symphony.listview;

import javafx.collections.ObservableList;
import loc.ex.symphony.indexdata.IndexStruct;

import java.util.List;


public class Link {

    private final List<IndexStruct> references;
    private String linkContent;

    private final String[] words;

    public Link(List<IndexStruct> references, ObservableList<Book> book, String... words) {
        this.references = references;

        try {
            linkContent =
                    book.get(references.get(0).getBookID()).name.get().replace("-", "\u00A0") + "\u00A0" +
                            (book.get(references.get(0).getBookID()).getChapters().get(references.get(0).getChapterID()).number.get()-1) + "\n" +
                            book.get(references.get(0).getBookID()).getChapters().get(references.get(0).getChapterID())
                                    .getFragments().get(references.get(0).getFragmentID());
        } catch (Exception exception) {
            linkContent = "none";
        }

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
