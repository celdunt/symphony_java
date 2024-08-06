package loc.ex.symphony.listview;

import com.fasterxml.jackson.annotation.JsonIgnore;

public abstract class TextMarkObservable {

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

    @JsonIgnore private final int markSize = 2;
    protected int from;
    protected int to;

    public void notifySub(TextMarkObservable comparable) {
        if (comparable != null && comparable.to < from) {
            from += markSize;
            to += markSize;
        }
    }

    public void notifyUnsub(TextMarkObservable comparable) {
        if (comparable != null && comparable.to < from) {
            from -= markSize;
            to -= markSize;
        }
    }

}
