package loc.ex.symphony.listview;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.util.List;

@JsonAutoDetect
public class Article {

    @JsonCreator
    public Article(
                @JsonProperty("date") String date,
                @JsonProperty("name") String name,
                @JsonProperty("bLinks") List<Link> bLinks,
                @JsonProperty("eLinks") List<Link> eLinks,
                @JsonProperty("oLinks") List<Link> oLinks) {
        this.date = new SimpleStringProperty(date);
        this.name = new SimpleStringProperty(name);
        this.bLinks = bLinks;
        this.eLinks = eLinks;
        this.oLinks = oLinks;
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

    public List<Link> getbLinks() {
        return bLinks;
    }

    public void setbLinks(List<Link> bLinks) {
        this.bLinks = bLinks;
    }

    public List<Link> geteLinks() {
        return eLinks;
    }

    public void seteLinks(List<Link> eLinks) {
        this.eLinks = eLinks;
    }

    public SimpleStringProperty date;
    public SimpleStringProperty name;
    public List<Link> bLinks;
    public List<Link> eLinks;

    public List<Link> getoLinks() {
        return oLinks;
    }

    public void setoLinks(List<Link> oLinks) {
        this.oLinks = oLinks;
    }

    public List<Link> oLinks;

}
