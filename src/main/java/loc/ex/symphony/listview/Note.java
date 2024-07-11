package loc.ex.symphony.listview;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonAutoDetect
public class Note {

    @JsonCreator
    public Note(
        @JsonProperty("from") int from,
        @JsonProperty("to") int to,
        @JsonProperty("text") String text
    ) {
        this.from = from;
        this.to = to;
        this.text = text;
    }

    public int from;
    public int to;
    public String text;
    @JsonIgnore boolean isOpen = false;

    public Note open() {
        isOpen = true;
        return this;
    }

    public void close() {
        isOpen = false;
    }

    @JsonIgnore public boolean isOpened() {
        return isOpen;
    }

    public int getFrom() {
        return from;
    }

    public void setFrom(int from) {
        this.from = from;
    }

    public int getTo() {
        return to;
    }

    public void setTo(int to) {
        this.to = to;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
