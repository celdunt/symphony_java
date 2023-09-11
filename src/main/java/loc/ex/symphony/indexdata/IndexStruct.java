package loc.ex.symphony.indexdata;

import loc.ex.symphony.listview.PathsEnum;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class IndexStruct implements Serializable {

    public PathsEnum root;
    private int bookId;
    private int chapterId;
    private int fragmentId;
    private int position;
    private int wordLength;

    private final List<String> synonyms = new ArrayList<>();


    public List<String> getSynonyms() {
        return synonyms;
    }

    public int getBookID() {
        return bookId;
    }

    public void setBookID(int bookId) {
        this.bookId = bookId;
    }

    public int getChapterID() {
        return chapterId;
    }

    public void setChapterID(int chapterId) {
        this.chapterId = chapterId;
    }

    public int getFragmentID() {
        return fragmentId;
    }

    public void setFragmentID(int fragmentId) {
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
