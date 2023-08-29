package loc.ex.symphony.indexdata;

import loc.ex.symphony.listview.PathsEnum;

import java.io.Serializable;

public class IndexStruct implements Serializable {

    public PathsEnum root;
    private int bookId;
    private int chapterId;
    private int fragmentId;
    private int position;
    private int wordLength;

    public int getBookId() {
        return bookId;
    }

    public void setBookId(int bookId) {
        this.bookId = bookId;
    }

    public int getChapterId() {
        return chapterId;
    }

    public void setChapterId(int chapterId) {
        this.chapterId = chapterId;
    }

    public int getFragmentId() {
        return fragmentId;
    }

    public void setFragmentId(int fragmentId) {
        this.fragmentId = fragmentId;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public int getWordLength() {
        return wordLength;
    }

    public void setWordLength(int wordLength) {
        this.wordLength = wordLength;
    }
}
