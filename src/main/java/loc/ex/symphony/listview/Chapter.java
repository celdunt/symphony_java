package loc.ex.symphony.listview;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import javafx.beans.property.SimpleIntegerProperty;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Chapter {

    public Chapter(int number) {
        this.number = new SimpleIntegerProperty(number);
        this.fragments = new ArrayList<>();
        this.num = number;
    }

    @JsonCreator
    public Chapter(
            @JsonProperty("num") int num,
            @JsonProperty("fragments") List<String> fragments
    ) {
        this.number = new SimpleIntegerProperty(num);
        this.fragments = fragments;
        this.num = num;
    }

    @JsonIgnore public SimpleIntegerProperty number;
    public int num;
    public final List<String> fragments;

    public List<String> getFragments() {
        return fragments;
    }

    public void acceptBibleEdit(String text) {
        text = text.replaceAll("\uD83D\uDCDD", "");
        Pattern splitByFragmentPattern = Pattern.compile("(\\d+)\\s+(.*?)\\n");
        Matcher matcher = splitByFragmentPattern.matcher(text);

        fragments.clear();

        while (matcher.find()) {
            String part1 = matcher.group(1);
            String part2 = "";

            if (matcher.groupCount() > 1)
                part2 = matcher.group(2);

            fragments.add(String.format("%s %s\n", part1, part2));
        }
    }

    public void acceptOtherEdit(String text) {
        text = text.replaceAll("\uD83D\uDCDD", "");
        String[] rawFragments = text.split("\\.");
        List<String> splitedFragmets = new ArrayList<>();
        int minLength = 25;
        StringBuilder buffer = new StringBuilder();

        for (String part : rawFragments) {
            part = part.trim();

            if (buffer.isEmpty()) {
                buffer.append(part);
            } else {
                if (buffer.length() + part.length() + 1 < minLength) {
                    buffer.append(".").append(part);
                } else {
                    splitedFragmets.add(buffer.append(".").toString());
                    buffer.setLength(0);
                    buffer.append(part);
                }
            }
        }
        if (!buffer.isEmpty())
            splitedFragmets.add(buffer.append(".").toString());

        fragments.clear();
        splitedFragmets = splitedFragmets.stream()
                .map(fr -> fr.replaceAll("^\\s*\\n+", ""))
                .toList();
        fragments.addAll(splitedFragmets);
    }

    @JsonIgnore public String getEntireText() {
        return String.join("", fragments);
    }
}
