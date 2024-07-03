package loc.ex.symphony.search;

import com.almasb.fxgl.core.collection.Array;

import java.util.*;

public class Cutser {
    Iterator<String> bibleCuts = new Array.ArrayIterator<>(new Array<>(new String[] {"Быт.", "Исх.", "Лев.", "Числ.", "Втор.", "Нав.",
            "Суд.", "Руф.", "1 Цар.", "2 Цар.", "3 Цар.", "4 Цар.", "1 Пар.",
            "2 Пар.", "Езд.", "Неем.", "Есф.", "Иов.", "Пс.", "Пр.", "Еккл.",
            "Песн.", "Ис.", "Иер.", "Плач.", "Иез.", "Дан.", "Ос.", "Иоил.",
            "Ам.", "Авд.", "Ион.", "Мих.", "Наум.", "Авв.", "Соф.", "Агг.",
            "Зах.", "Мал.", "Мф.", "Мк.", "Лк.", "Ин.", "Деян.", "Иак.",
            "1 Петр.", "2 Петр.", "1 Ин.", "2 Ин.", "3 Ин.", "Иуд.", "Рим.",
            "1 Кор.", "2 Кор.", "Гал.", "Еф.", "Флп.", "Кол.", "1 Фес.", "2 Фес.",
            "1 Тим.", "2 Тим.", "Тит.", "Флм.", "Евр.", "Откр."}));

    public String getBibleCut(int bookIndex) {
        if (bookIndex >= 0 && bookIndex < 66) {
            for (int i = 0; i < bookIndex; i++) {
                if (bibleCuts.hasNext()) {
                    bibleCuts.next();
                }
            }
            return bibleCuts.next();
        } else return "error";
    }

    public int getBibleIndex(String bibleCut) {
        int findIndex = -1;
        int errorIndex = 65;
        while (bibleCuts.hasNext()) {
            findIndex++;
            if (bibleCuts.next().equalsIgnoreCase(bibleCut)) return findIndex;
            else errorIndex--;
        }
        return errorIndex;
    }
}
