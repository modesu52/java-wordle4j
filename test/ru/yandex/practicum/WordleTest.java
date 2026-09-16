package ru.yandex.practicum;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class WordleTest {

    private WordleDictionary dictionary;
    private WordleGame game;
    private WordleDictionaryLoader loader;
    private PrintWriter log;

    @BeforeEach
    void setUp() {
        List<String> words = Arrays.asList(
                "герой", "город", "гонец", "моряк", "слово",
                "весна", "земля", "кепка", "женка", "алмаз"
        );
        dictionary = new WordleDictionary(words);
        log = new PrintWriter(System.out);
        game = new WordleGame(dictionary, log);
        loader = new WordleDictionaryLoader(log);
    }

    @Test
    void normalizeWord_shouldHandleAllCases() {
        assertEquals("город", WordleDictionary.normalizeWord("ГОРОД"));
        assertEquals("ежик", WordleDictionary.normalizeWord("Ёжик"));
        assertEquals("еж", WordleDictionary.normalizeWord("Ёж"));
        assertEquals("море", WordleDictionary.normalizeWord("  МОРЕ  "));
        assertEquals("елка", WordleDictionary.normalizeWord("Ёлка"));
        assertEquals("ежик", WordleDictionary.normalizeWord("ëжик")); // латинская ë
    }

    @Test
    void isValidLength_shouldReturnTrueFor5Letters() {
        assertTrue(WordleDictionary.isValidLength("город"));
        assertFalse(WordleDictionary.isValidLength("дом"));
        assertFalse(WordleDictionary.isValidLength("городок"));
    }

    @Test
    void containsAndSize_shouldWorkCorrectly() {
        assertTrue(dictionary.contains("герой"));
        assertTrue(dictionary.contains("город"));
        assertFalse(dictionary.contains("привет"));
        assertEquals(10, dictionary.size());
    }

    @Test
    void getRandomWord_shouldReturnWordFromDictionary() {
        for (int i = 0; i < 20; i++) {
            String word = dictionary.getRandomWord();
            assertNotNull(word);
            assertEquals(5, word.length());
            assertTrue(dictionary.contains(word));
        }
    }

    @Test
    void getRandomWord_shouldThrowForEmptyDictionary() {
        WordleDictionary empty = new WordleDictionary(List.of());
        assertThrows(RuntimeException.class, empty::getRandomWord);
    }

    @Test
    void analyzeWord_shouldReturnAllPlusesForExactMatch() {
        assertEquals("+++++", WordleDictionary.analyzeWord("герой", "герой"));
    }

    @Test
    void analyzeWord_shouldReturnCorrectMarksForExample() {
        assertEquals("+^-^-", WordleDictionary.analyzeWord("герой", "гонец"));
    }

    @Test
    void analyzeWord_shouldReturnAllMinusesWhenNoMatches() {
        assertEquals("-----", WordleDictionary.analyzeWord("герой", "алмаз"));
    }

    @Test
    void makeMove_shouldReturnValidResult() throws InvalidInputException, WordNotFoundException {
        String result = game.makeMove("город");
        assertNotNull(result);
        assertEquals(5, result.length());
        assertTrue(result.matches("[+^\\-]{5}"));
    }

    @Test
    void makeMove_shouldThrowForUnknownWord() {
        assertThrows(IllegalArgumentException.class, () -> game.makeMove("привет"));
        assertEquals(0, game.getSteps());
    }

    @Test
    void makeMove_shouldThrowForWrongLength() {
        assertThrows(IllegalArgumentException.class, () -> game.makeMove("дом"));
        assertThrows(IllegalArgumentException.class, () -> game.makeMove("городок"));
    }

    @Test
    void makeMove_shouldThrowForEmptyOrNull() {
        assertThrows(IllegalArgumentException.class, () -> game.makeMove(""));
        assertThrows(IllegalArgumentException.class, () -> game.makeMove("   "));
        assertThrows(IllegalArgumentException.class, () -> game.makeMove(null));
    }

    @Test
    void getHint_shouldReturnValidWord() throws InvalidInputException, WordNotFoundException {
        game.makeMove("город");
        String hint = game.getHint();
        assertNotNull(hint);
        assertEquals(5, hint.length());
        assertTrue(dictionary.contains(hint));
    }

    @Test
    void loadDictionary_shouldFilterInvalidWords(@TempDir Path tempDir) throws IOException {
        Path file = tempDir.resolve("words.txt");
        Files.writeString(file,
                "город\n" +
                        "дом\n" +
                        "городок\n" +
                        "word\n" +
                        "12345\n" +
                        "моряк\n",
                StandardCharsets.UTF_8);

        WordleDictionary dict = loader.loadDictionary(file.toString());

        assertEquals(2, dict.size());
        assertTrue(dict.contains("город"));
        assertTrue(dict.contains("моряк"));
        assertFalse(dict.contains("дом"));
    }

    @Test
    void loadDictionary_shouldNormalizeWords(@TempDir Path tempDir) throws IOException {
        Path file = tempDir.resolve("words.txt");
        Files.writeString(file, "ЁЖИК\nГОРОД\nМОРЯК\n", StandardCharsets.UTF_8);
        WordleDictionary dict = loader.loadDictionary(file.toString());
        assertEquals(2, dict.size());
        assertTrue(dict.contains("город"));
        assertTrue(dict.contains("моряк"));
        assertFalse(dict.contains("ежик"));
    }

    @Test
    void loadDictionary_shouldThrowForMissingFile() {
        assertThrows(IOException.class, () -> loader.loadDictionary("no_such_file.txt"));
    }
}