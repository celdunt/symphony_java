package loc.ex.symphony.listview;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonAutoDetect
public class TranslateHelper extends TextMarkObservable {

    @JsonCreator
    public TranslateHelper(
            @JsonProperty("from") int from,
            @JsonProperty("to") int to,
            @JsonProperty("text") String text
    ) {
        this.from = from;
        this.to = to;
        this.text = text;
    }

    public String text;
    @JsonIgnore
    boolean isOpen = false;

    public TranslateHelper open() {
        isOpen = true;
        return this;
    }

    public void close() {
        isOpen = false;
    }

    @JsonIgnore public boolean isOpened() {
        return isOpen;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

}
