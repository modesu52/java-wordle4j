package ru.yandex.practicum;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/*
этот класс содержит в себе всю рутину по работе с файлами словарей и с кодировками
    ему нужны методы по загрузке списка слов из файла по имени файла
    на выходе должен быть класс WordleDictionary
 */

public class WordleDictionaryLoader {

    private final PrintWriter log;

    public WordleDictionaryLoader(PrintWriter log) {
        this.log = log;
    }

    public WordleDictionary loadDictionary(String filename) throws IOException {
        List<String> result = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(filename), StandardCharsets.UTF_8))) {

            String line;
            while ((line = reader.readLine()) != null) {
                String word = WordleDictionary.normalizeWord(line);
                if (isValid(word)) {
                    result.add(word);
                }
            }
        }

        if (result.isEmpty()) {
            throw new IOException("Подходящих  слов из пяти букв не найдено в файле: " + filename);
        }

        log.println("Загружено слов для игры: " + result.size());
        return new WordleDictionary(result);
    }

    private static boolean isValid(String word) {
        if (word == null || word.length() != WordleDictionary.WORD_LENGTH) return false;
        for (char c : word.toCharArray()) {
            if (c < 'а' || c > 'я') return false;
        }
        return true;
    }
}