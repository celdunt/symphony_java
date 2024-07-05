package loc.ex.symphony.search;

import com.almasb.fxgl.core.collection.Array;
import loc.ex.symphony.listview.PathsEnum;

import java.util.*;

public class Cutser {
     String[] bibleCuts = new String[]{
            "Быт.", "Исх.", "Лев.", "Числ.", "Втор.", "Нав.",
            "Суд.", "Руф.", "1 Цар.", "2 Цар.", "3 Цар.", "4 Цар.", "1 Пар.",
            "2 Пар.", "Езд.", "Неем.", "Есф.", "Иов.", "Пс.", "Пр.", "Еккл.",
            "Песн.", "Ис.", "Иер.", "Плач.", "Иез.", "Дан.", "Ос.", "Иоил.",
            "Ам.", "Авд.", "Ион.", "Мих.", "Наум.", "Авв.", "Соф.", "Агг.",
            "Зах.", "Мал.", "Мф.", "Мк.", "Лк.", "Ин.", "Деян.", "Иак.",
            "1 Петр.", "2 Петр.", "1 Ин.", "2 Ин.", "3 Ин.", "Иуд.", "Рим.",
            "1 Кор.", "2 Кор.", "Гал.", "Еф.", "Флп.", "Кол.", "1 Фес.", "2 Фес.",
            "1 Тим.", "2 Тим.", "Тит.", "Флм.", "Евр.", "Откр."};

    String[] ellenCuts = new String[]{
            "пп.", "пц.", "жв.", "да.", "вб.", "спп.", "вд.", "хд.", "хвбг.", "си.", "вдм.", "рп.",
            "пх.", "ss.", "нух.", "нпх.", "хс.", "сур.", "в.", "се.", "pm.", "ив1.", "ив2.", "ив3.",
            "т1.", "т2.", "т3.", "т4.", "т5.", "т6.", "т7.", "т8.", "т9.", "сп.", "ле.", "ссш.", "ом.",
            "мтрп.", "зз.", "сз.", "СЖ.", "hl.", "пг.", "пмв.", "соиос.", "пбл.", "жке.", "ппк.", "цг.",
            "сх.", "кмс.", "одпд.", "рхл.", "чну.", "грп."
    };

    public String getCutByRoot(int index, PathsEnum root) {

        if (root == PathsEnum.Bible)
            return getBibleCut(index);
        else return getEllenCut(index);

    }

    public String getEllenCut(int ellenIndex) {

        if (ellenIndex >= 0 && ellenIndex < 55) {
           return ellenCuts[ellenIndex].toUpperCase();
        } else return "error";

    }

    public int getEllenIndex(String ellenCut) {

        int findIndex = -1;
        for (var cut : ellenCuts) {
            findIndex++;
            if (cut.equalsIgnoreCase(ellenCut)) return findIndex;
        }
        return -1;

    }

    public String getBibleCut(int bookIndex) {
        if (bookIndex >= 0 && bookIndex < 66) {
            return bibleCuts[bookIndex];
        } else return "error";
    }

    public int getBibleIndex(String bibleCut) {
        int findIndex = -1;
        for (var cut : bibleCuts) {
            findIndex++;
            if (cut.equalsIgnoreCase(bibleCut)) return findIndex;
        }
        return -1;
    }
}
