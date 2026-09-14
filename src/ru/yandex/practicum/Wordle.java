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
        // PrintWriter лога создаём первым, он передаётся во все классы
        try (PrintWriter log = new PrintWriter(
                new OutputStreamWriter(
                        new FileOutputStream(LOG_PATH), StandardCharsets.UTF_8))) {

            log.println("=== НАЧАЛО ИГРЫ ===");

            // загружаем словарь
            WordleDictionaryLoader loader = new WordleDictionaryLoader(log);
            WordleDictionary dictionary = loader.loadDictionary(DICTIONARY_PATH);

            // создаем игру
            WordleGame game = new WordleGame(dictionary, log);

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
                        continue;
                    }

                    try {
                        String result = game.makeMove(input);
                        System.out.println(WordleDictionary.normalizeWord(input));
                        System.out.println(result);
                        System.out.println();
                    } catch (IllegalArgumentException e) {
                        System.out.println(e.getMessage());
                    }
                }
            }

            System.out.println();
            if (game.isWon()) {
                System.out.println("🎉 ПОБЕДА! Угадали за " + game.getSteps() + " попытки!");
            } else {
                System.out.println("💔 ПОРАЖЕНИЕ. Загаданное слово: " + game.getAnswer());
            }

            log.println("=== КОНЕЦ ИГРЫ. Слово: " + game.getAnswer()
                    + ", ходов: " + game.getSteps() + ", победа: " + game.isWon() + " ===");

        } catch (Exception e) {
            System.err.println("Критическая ошибка: " + e.getMessage());
            e.printStackTrace();
        }
    }
}