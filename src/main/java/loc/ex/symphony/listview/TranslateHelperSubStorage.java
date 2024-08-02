package loc.ex.symphony.listview;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.fxmisc.richtext.InlineCssTextArea;
import org.fxmisc.richtext.StyleClassedTextArea;

import java.util.ArrayList;
import java.util.List;

@JsonAutoDetect
public class TranslateHelperSubStorage {

    @JsonCreator
    public TranslateHelperSubStorage(@JsonProperty("thelpers") List<TranslateHelper> thelpers) {
        this.thelpers = thelpers;
    }

    public TranslateHelperSubStorage() {}

    public List<TranslateHelper> thelpers = new ArrayList<>();

    public TranslateHelper get(int index) {
        if (index >= 0 && index < thelpers.size())
            return thelpers.get(index);
        else return thelpers.get(0);
    }

    public void add(TranslateHelper note) {
        thelpers.add(note);
    }

    public TranslateHelper getFromPos(int pos) {
        for (TranslateHelper thelper : thelpers) {
            if (thelper.getFrom() <= pos && thelper.getTo() >= pos) {
                return thelper;
            }
        }
        return new TranslateHelper(0, 1, "error");
    }

    public void remove(int index) {
        thelpers.remove(index);
    }

    public void display(StyleClassedTextArea textArea) {
        for (TranslateHelper thelper : thelpers) {
            textArea.setStyleClass(thelper.getFrom(), thelper.getTo(), "thelper");
        }
    }

    public int size() {
        return thelpers.size();
    }

}
