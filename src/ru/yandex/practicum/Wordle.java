package ru.yandex.practicum;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

/*
в главном классе нам нужно:
    создать лог-файл (он должен передаваться во все классы)
    создать загрузчик словарей WordleDictionaryLoader
    загрузить словарь WordleDictionary с помощью класса WordleDictionaryLoader
    затем создать игру WordleGame и передать ей словарь
    вызвать игровой метод в котором в цикле опрашивать пользователя и передавать информацию в игру
    вывести состояние игры и конечный результат
 */

public class Wordle {

    private static final String DICTIONARY_PATH = "words_ru.txt";
    private static final String LOG_PATH        = "wordle.log";

    public static void main(String[] args) {
        PrintWriter logWriter = null;
        try {
                logWriter = new PrintWriter(
                new OutputStreamWriter(
                        new FileOutputStream(LOG_PATH),
                        StandardCharsets.UTF_8
                )
        );
            logWriter.println("=== НАЧАЛО ИГРЫ ===");

            // загружаем словарь
            WordleDictionaryLoader loader = new WordleDictionaryLoader(logWriter);
            WordleDictionary dictionary = loader.loadDictionary(DICTIONARY_PATH);

            // создаем игру
            WordleGame game = new WordleGame(dictionary, logWriter);

            System.out.println("=== ИГРА WORDLE ===");
            System.out.println("Угадайте слово из 5 букв. У вас " + WordleGame.MAX_STEPS + " попыток.");
            System.out.println("Нажмите Enter без ввода, чтобы получить подсказку.");
            System.out.println();

            try (Scanner scanner = new Scanner(System.in, StandardCharsets.UTF_8)) {
                while (!game.isGameOver()) {
                    System.out.println("Попытка " + (game.getSteps() + 1) + "/" + WordleGame.MAX_STEPS);
                    System.out.print("Введите слово: ");
                    String input = scanner.nextLine().trim();

                    if (input.isEmpty()) {
                        String hint = game.getHint();
                        System.out.println("Подсказка: " + hint);
                        logWriter.println("Запрошена подсказка: " + hint); // обязательно в лог
                        continue;
                    }

                    try {
                        String result = game.makeMove(input);
                        System.out.println(WordleDictionary.normalizeWord(input));
                        System.out.println(result);
                        System.out.println();
                    } catch (WordNotFoundException e) {
                        System.out.println(e.getMessage());
                        logWriter.println("Слово не найдено: " + input);
                    } catch (InvalidInputException e) {
                        System.out.println(e.getMessage());
                        logWriter.println("Некорректный ввод: " + input);
                    }
                }
            }

            System.out.println();
            if (game.isWon()) {
                System.out.println("🎉 ПОБЕДА! Угадали за " + game.getSteps() + " попытки!");
            } else {
                System.out.println("💔 ПОРАЖЕНИЕ. Загаданное слово: " + game.getAnswer());
            }

            logWriter.println("=== КОНЕЦ ИГРЫ. Слово: " + game.getAnswer()
                    + ", ходов: " + game.getSteps() + ", победа: " + game.isWon() + " ===");
        } catch (Exception e) {
            // теперь logWriter доступен здесь
            if (logWriter != null) {
                logWriter.println("Критическая ошибка: " + e.getMessage());
                e.printStackTrace(logWriter); // пишем в лог, не в консоль
            }
            System.out.println("Произошла ошибка. Подробности в wordle.log");
        } finally {
            if (logWriter != null) {
                logWriter.close();
            }
        }
    }
}