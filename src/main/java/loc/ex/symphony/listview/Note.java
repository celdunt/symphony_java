package loc.ex.symphony.listview;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
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

    private int from;
    private int to;
    private String text;

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
