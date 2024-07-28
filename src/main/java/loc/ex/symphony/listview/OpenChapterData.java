package loc.ex.symphony.listview;

public class OpenChapterData {

    public int getTab() {
        return tab;
    }

    public void setTab(int tab) {
        this.tab = tab;
    }

    public int getBook() {
        return book;
    }

    public void setBook(int book) {
        this.book = book;
    }

    public int getChapter() {
        return chapter;
    }

    public void setChapter(int chapter) {
        this.chapter = chapter;
    }

    private int tab;
    private int book;
    private int chapter;

    public OpenChapterData() {

    }

    public void copy(OpenChapterData other) {
        this.tab = other.tab;
        this.book = other.book;
        this.chapter = other.chapter;
    }

}
