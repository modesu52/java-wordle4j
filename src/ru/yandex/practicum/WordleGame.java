package ru.yandex.practicum;


import java.io.PrintWriter;
import java.util.*;

/*
в этом классе хранится словарь и состояние игры
    текущий шаг
    всё что пользователь вводил
    правильный ответ

в этом классе нужны методы, которые
    проанализируют совпадение слова с ответом
    предложат слово-подсказку с учётом всего, что вводил пользователь ранее

не забудьте про специальные типы исключений для игровых и неигровых ошибок
 */
public class WordleGame {

    public static final int MAX_STEPS = 6;

    private final WordleDictionary dictionary;
    private final PrintWriter log;
    private final String answer;

    private int steps = 0;
    private boolean isGameOver = false;
    private boolean isWon = false;

    private final Map<String, String> history = new LinkedHashMap<>();

    private final Set<String> usedHints = new HashSet<>();

    public WordleGame(WordleDictionary dictionary, PrintWriter log) {
        this.dictionary = dictionary;
        this.log = log;
        this.answer = dictionary.getRandomWord();
        log.println("Загадано слово: " + answer);
    }

    public String makeMove(String raw) {
        if (isGameOver) {
            throw new IllegalStateException("Игра уже завершена.");
        }

        String word = validate(raw);

        steps++;
        String result = WordleDictionary.analyzeWord(answer, word);
        history.put(word, result);

        if (word.equals(answer)) {
            isGameOver = true;
            isWon = true;
        } else if (steps >= MAX_STEPS) {
            isGameOver = true;
        }

        log.println("Ход " + steps + ": " + word + " -> " + result);
        return result;
    }

    public String getHint() {
        for (String candidate : dictionary.getAllWords()) {
            if (history.containsKey(candidate) || usedHints.contains(candidate)) {
                continue;
            }
            // Проверяем, подходит ли слово под все известные ограничения
            if (matchesAllConstraints(candidate)) {
                usedHints.add(candidate);
                log.println("Подсказка: " + candidate);
                return candidate;
            }
        }
        throw new IllegalStateException("Не удалось найти слово для подсказки.");
    }

    private boolean matchesAllConstraints(String candidate) {
        for (Map.Entry<String, String> entry : history.entrySet()) {
            String guess = entry.getKey();
            String expectedResult = entry.getValue();

            // Если кандидат дал бы другой результат для этого хода — он не подходит
            String actualResult = WordleDictionary.analyzeWord(candidate, guess);
            if (!actualResult.equals(expectedResult)) {
                return false;
            }
        }
        return true;
    }

    private String validate(String raw) {
        if (raw == null || raw.isBlank()) {
            throw new IllegalArgumentException("Введите слово.");
        }
        String word = WordleDictionary.normalizeWord(raw);

        if (word.length() != WordleDictionary.WORD_LENGTH) {
            throw new IllegalArgumentException(
                    "Слово должно состоять из " + WordleDictionary.WORD_LENGTH + " букв.");
        }
        for (char c : word.toCharArray()) {
            if (c < 'а' || c > 'я') {
                throw new IllegalArgumentException("Используйте только русские буквы.");
            }
        }
        if (!dictionary.contains(word)) {
            throw new IllegalArgumentException(
                    "Слово \"" + word + "\" не найдено в словаре.");
        }
        return word;
    }

    public boolean isGameOver() { return isGameOver; }
    public boolean isWon()      { return isWon; }
    public int getSteps()       { return steps; }
    public String getAnswer()   { return answer; }
}