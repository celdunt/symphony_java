package loc.ex.symphony.indexdata;

import loc.ex.symphony.listview.PathsEnum;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class IndexStruct implements Serializable, Comparable<IndexStruct> {

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

    @Override
    public int compareTo(@NotNull IndexStruct other) {
        int firstStage = Integer.compare(this.getBookID(), other.getBookID());
        int secondStage = Integer.compare(this.getChapterID(), other.getChapterID());
        int thirdStage = Integer.compare(this.getFragmentID(), other.getFragmentID());

        if (firstStage == 0)
            if (secondStage == 0)
                return thirdStage;
            else return secondStage;
        else return firstStage;
    }
}
