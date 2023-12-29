package loc.ex.symphony.listview;

public class Bookmark {
    private final String name;
    private final Link link;

    public Bookmark(String name, Link link) {
        this.name = name;
        this.link = link;
    }

    public String getName() {
        return name;
    }

    public Link getLink() {
        return link;
    }
}
