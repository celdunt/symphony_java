package loc.ex.symphony.indexdata;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;


@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class IndexStruct implements Serializable, Comparable<IndexStruct> {

    public int bookId;
    public int chapterId;
    public int fragmentId;
    public int position;
    public int wordKey;

    public final List<Integer> synonymsKeys = new ArrayList<>();

    @JsonCreator
    public IndexStruct(
            @JsonProperty("bookId") int bookId,
            @JsonProperty("chapterId") int chapterId,
            @JsonProperty("fragmentId") int fragmentId,
            @JsonProperty("position") int position,
            @JsonProperty("wordKey") int wordKey,
            @JsonProperty("synonymsKeys") List<Integer> synonymsKeys) {
        this.bookId = bookId;
        this.chapterId = chapterId;
        this.fragmentId = fragmentId;
        this.position = position;
        this.wordKey = wordKey;
        if (synonymsKeys != null) {
            this.synonymsKeys.addAll(synonymsKeys);
        }
    }

    public IndexStruct() {}

    public List<Integer> getSynonymsKeys() {
        return synonymsKeys;
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


    public int getWordKey() {
        return wordKey;
    }

    public void setWordKey(int wordKey) {
        this.wordKey = wordKey;
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

    public static class PositionComparator implements Comparator<loc.ex.symphony.indexdata.IndexStruct> {
        @Override
        public int compare(loc.ex.symphony.indexdata.IndexStruct o1, loc.ex.symphony.indexdata.IndexStruct o2) {
            return Integer.compare(o1.getPosition(), o2.getPosition());
        }
    }
}
