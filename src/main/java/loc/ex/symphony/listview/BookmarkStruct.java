package loc.ex.symphony.listview;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

@JsonDeserialize(builder = BookmarkStruct.Builder.class)
public class BookmarkStruct implements Comparable {
    @JsonIgnore private final StringProperty date;
    @JsonIgnore private final StringProperty link;
    @JsonIgnore private final StringProperty content;
    @JsonProperty("_date") private final String _date;
    @JsonProperty("_link") private final String _link;
    @JsonProperty("_content") private final String _content;
    @JsonProperty("root") private final PathsEnum root;
    @JsonProperty("bookId") private final int bookId;
    @JsonProperty("chapterId") private final int chapterId;
    @JsonProperty("position") private final int position;
    @JsonProperty("text") private final String text;


    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    public BookmarkStruct(
            @JsonProperty("_date")String _date,
            @JsonProperty("_link")String _link,
            @JsonProperty("_content")String _content,
            @JsonProperty("root")PathsEnum root,
            @JsonProperty("bookId")int bookId,
            @JsonProperty("chapterId")int chapterId,
            @JsonProperty("position")int position,
            @JsonProperty("text")String text) {
        this._date = _date;
        this._link = _link;
        this._content = _content;
        this.date = new SimpleStringProperty(_date);
        this.link = new SimpleStringProperty(_link);
        this.content = new SimpleStringProperty(_content);
        this.root = root;
        this.bookId = bookId;
        this.chapterId = chapterId;
        this.position = position;
        this.text = text;
    }

    public StringProperty dateProperty() {
        return date;
    }

    public StringProperty linkProperty() {
        return link;
    }

    public StringProperty contentProperty() {
        return content;
    }

    public String getDate() {
        return date.getValue();
    }

    public String getLink() {
        return link.getValue();
    }

    public String getContent() {
        return content.getValue();
    }

    public String get_date() {
        return _date;
    }

    public String get_link() {
        return _link;
    }

    public String get_content() {
        return _content;
    }

    public PathsEnum getRoot() {
        return root;
    }

    public int getBookId() {
        return bookId;
    }

    public int getChapterId() {
        return chapterId;
    }

    public int getPosition() {
        return position;
    }

    public String getText() {
        return text;
    }

    @Override
    public int compareTo(@NotNull Object o) {
        return _date.compareTo(((BookmarkStruct)o)._date);
    }


    @JsonPOJOBuilder
    public static class Builder {
        String _date;
        String _content;
        String _link;
        PathsEnum root;
        int bookId;
        int chapterId;
        int position;
        String text;

        public Builder with_date(String _date) {
            this._date = _date;
            return this;
        }

        public Builder with_content(String _content) {
            this._content = _content;
            return this;
        }

        public Builder with_link(String _link) {
            this._link = _link;
            return this;
        }

        public Builder withRoot(PathsEnum root) {
            this.root = root;
            return this;
        }

        public Builder withBookId(int bookId) {
            this.bookId = bookId;
            return this;
        }

        public Builder withChapterId(int chapterId) {
            this.chapterId = chapterId;
            return this;
        }

        public Builder withPosition(int position) {
            this.position = position;
            return this;
        }

        public Builder withText(String text) {
            this.text = text;
            return this;
        }

        public BookmarkStruct build() {
            if (_date == null) _date = LocalDateTime.now().format(DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL));
            return new BookmarkStruct(
                    _date,
                    _link,
                    _content,
                    root,
                    bookId,
                    chapterId,
                    position,
                    text);
        }
    }

}
