package loc.ex.symphony.listview;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import loc.ex.symphony.controls.NoteStyledTextArea;
import org.fxmisc.richtext.InlineCssTextArea;
import org.fxmisc.richtext.StyleClassedTextArea;

import java.util.ArrayList;
import java.util.List;

public class ParallelsLinksSubStorage {

    public List<ParallelLink> parallelsLinks = new ArrayList<>();

    @JsonCreator
    public ParallelsLinksSubStorage(@JsonProperty("parallelsLinks") List<ParallelLink> parallelsLinks) {
        this.parallelsLinks = parallelsLinks;
    }

    public ParallelsLinksSubStorage() {}

    public ParallelLink get(int index) {
        if (index >= 0 && index < parallelsLinks.size()) {
            return parallelsLinks.get(index);
        } else return parallelsLinks.get(0);
    }

    public void add(ParallelLink link) {
        parallelsLinks.add(link);
    }

    public void remove(int index) {
        parallelsLinks.remove(index);
    }

    public void display(NoteStyledTextArea textArea) {
        for (ParallelLink link : parallelsLinks) {
            textArea.setStyleClass(link.getFrom(), link.getTo(), "parallel-link");
        }
    }

    public int size() {
        return parallelsLinks.size();
    }

}
