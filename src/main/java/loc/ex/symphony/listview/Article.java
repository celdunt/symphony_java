package loc.ex.symphony.listview;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import javafx.beans.property.StringProperty;

import java.util.List;

@JsonAutoDetect
public class Article {

    @JsonCreator
    public Article(
                @JsonProperty("date") StringProperty date,
                @JsonProperty("name") StringProperty name,
                @JsonProperty("links") List<Link> links) {
        this.date = date;
        this.name = name;
        this.links = links;
    }

    public String getDate() {
        return date.get();
    }

    public StringProperty dateProperty() {
        return date;
    }

    public void setDate(String date) {
        this.date.set(date);
    }

    public String getName() {
        return name.get();
    }

    public StringProperty nameProperty() {
        return name;
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public List<Link> getLinks() {
        return links;
    }

    public void setLinks(List<Link> links) {
        this.links = links;
    }

    public StringProperty date;
    public StringProperty name;
    public List<Link> links;



}
