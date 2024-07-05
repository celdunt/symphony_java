package loc.ex.symphony.listview;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import javafx.collections.ObservableList;
import loc.ex.symphony.indexdata.IndexStruct;
import loc.ex.symphony.search.Cutser;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class Link implements Serializable {

    private final List<IndexStruct> references = new ArrayList<>();

    public String getLinkContent() {
        return linkContent;
    }

    private final String linkContent;

    private final String[] words;

    @JsonCreator
    public Link(
            @JsonProperty("references")List<IndexStruct> references,
            @JsonProperty("book")ObservableList<Book> book,
            PathsEnum pathsEnum,
            @JsonProperty("words")String... words) {
        this.references.addAll(references);

        String bookName = new Cutser().getCutByRoot(references.getFirst().getBookID(), pathsEnum); //book.get(references.getFirst().getBookID()).name.get();
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
