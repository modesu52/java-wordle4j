package ru.yandex.practicum;

import java.util.List;
import java.util.Random;

/*
этот класс содержит в себе список слов List<String>
    его методы похожи на методы списка, но учитывают особенности игры
    также этот класс может содержать рутинные функции для сравнения слов, букв и т.д.
 */

public class WordleDictionary {

    public static final int WORD_LENGTH = 5; // длина необходимого слова

    private final List<String> words;
    private final Random random = new Random();

    public WordleDictionary(List<String> words) {
        this.words = words;
    }

    public boolean contains(String word) {
        return words.contains(word);
    }

    public String getRandomWord() {
        if (words.isEmpty()) {
            throw new EmptyDictionaryException("Словарь пуст, невозможно выбрать слово.");
        }
        return words.get(random.nextInt(words.size()));
    }

    public static String normalizeWord(String word) {
        return word.toLowerCase().trim().replace('\u0451', '\u0435')
                .replace('\u00EB', '\u0435');
    }

    public List<String> getAllWords() {
        return words;
    }

    public static boolean isValidLength(String word) {
        return word.length() == WORD_LENGTH;
    }

    public int size() {
        return words.size();
    }

    public static String analyzeWord(String secret, String guess) {
        StringBuilder result = new StringBuilder();
        boolean[] inGuessWord = new boolean[5];
        boolean[] inAnswerWord = new boolean[5];
        for (int i = 0; i < guess.length(); i++) {
            if (guess.charAt(i) == secret.charAt(i)) {
                result.append('+');
                inGuessWord[i] = true;
                inAnswerWord[i] = true;
            } else {
                result.append(' ');
            }
        }
        for (int i = 0; i < 5; i++) {
            if (inGuessWord[i]) continue;
            char letter = guess.charAt(i);
            boolean found = false;
            for (int j = 0; j < 5; j++) {
                if (!inAnswerWord[j] && secret.charAt(j) == letter) {
                    inAnswerWord[j] = true;
                    found = true;
                    break;
                }
            }
            if (found) {
                result.setCharAt(i, '^');
            } else {
                result.setCharAt(i, '-');
            }
        }
        return result.toString();
    }
}
