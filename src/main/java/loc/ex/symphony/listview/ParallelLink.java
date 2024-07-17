package loc.ex.symphony.listview;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

@JsonAutoDetect
public class ParallelLink {

    public List<Link> getParallelLink() {
        return parallelLink;
    }

    public void setParallelLink(List<Link> parallelLink) {
        this.parallelLink = parallelLink;
    }

    public void addLink(Link link) {
        this.parallelLink.add(link);
    }

    public int getTo() {
        return to;
    }

    public void setTo(int to) {
        this.to = to;
    }

    public int getFrom() {
        return from;
    }

    public void setFrom(int from) {
        this.from = from;
    }

    public int from;
    public int to;

    @JsonIgnore boolean isOpen = false;

    public List<Link> parallelLink = new ArrayList<>();

    @JsonCreator
    public ParallelLink(
            @JsonProperty("from") int from,
            @JsonProperty("to") int to,
            @JsonProperty("parallelLink") List<Link> parallelLink
    ) {
        this.from = from;
        this.to = to;
        this.parallelLink = parallelLink;
    }

    public ParallelLink open() {
        isOpen = true;
        return this;
    }

    public void close() {
        isOpen = false;
    }

    @JsonIgnore public boolean isOpened() {
        return isOpen;
    }

}
