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

    public List<IndexStruct> references = new ArrayList<>();

    public String linkContent;

    public String[] words;


    public Link(
            List<IndexStruct> references,
            ObservableList<Book> book,
            PathsEnum pathsEnum,
            String... words) {
        this.references.addAll(references);



        String bookName = new Cutser().getCutByRoot(references.get(0).getBookID(), pathsEnum); //book.get(references.getFirst().getBookID()).name.get();
        String chapterNumber = (book.get(references.get(0).getBookID()).getChapters().get(references.get(0).getChapterID()).number.get()-1) + "";
        String fragmentText = book.get(references.get(0).getBookID()).getChapters().get(references.get(0).getChapterID())
                .getFragments().get(references.get(0).getFragmentID());
        linkContent = bookName + " : " + chapterNumber + "\n" + fragmentText;

        this.words = words;
    }

    @JsonCreator
    public Link(
        @JsonProperty("references") List<IndexStruct> references,
        @JsonProperty("linkContent") String linkContent,
        @JsonProperty("words") String[] words
    ) {
        this.references = references;
        this.linkContent = linkContent;
        this.words = words;
    }

    public String getLinkContent() {
        return linkContent;
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
