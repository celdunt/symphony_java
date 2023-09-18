package loc.ex.symphony.listview;

import javafx.collections.ObservableList;
import loc.ex.symphony.indexdata.IndexStruct;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;


public class Link {

    private final List<IndexStruct> references;
    private final String linkContent;

    private final String[] words;

    public Link(List<IndexStruct> references, ObservableList<Book> book, String... words) {
        this.references = references;
        linkContent =
                book.get(references.get(0).getBookID()).name.get().replace("-", "\u00A0") + "\u00A0" +
                        (book.get(references.get(0).getBookID()).getChapters().get(references.get(0).getChapterID()).number.get()-1) + "\n" +
                        book.get(references.get(0).getBookID()).getChapters().get(references.get(0).getChapterID())
                        .getFragments().get(references.get(0).getFragmentID());
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
