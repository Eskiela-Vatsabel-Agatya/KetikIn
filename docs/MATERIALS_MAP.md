# Materials Map

Where each course topic (PBO, class B, 2026) appears in KetikIn. Topic names follow the lecture slides
(Week 1–14) and the practicum modules. Every place is also marked in the code with a comment
`// [MATERI: ...]`, so it can be found with a text search.

Format: `file:line` `code on that line` — one-sentence explanation.

## 1. Table: course topic → code

| Course topic | File | Method / line | Explanation |
|---|---|---|---|
| Class, Object & Constructor | `model/Player.java` | `src/model/Player.java:20` `public Player(String name) throws InvalidUsernameException {` | A constructor (same name as the class, no return type) that checks the name through `setName`. |
| Class, Object & Constructor | `ui/MainFrame.java` | `src/ui/MainFrame.java:124` `GameMode mode = createMode(focusLetter);` | A new mode object is created with `new` for every round. |
| Constructor overloading | `model/ScoreEntry.java` | `src/model/ScoreEntry.java:27` `public ScoreEntry(String name, String mode, int score, double wpm, double accuracy, String date) {` | Constructor 1: all values given (used when reading the CSV file). |
| Constructor overloading | `model/ScoreEntry.java` | `src/model/ScoreEntry.java:37` `public ScoreEntry(String playerName, String modeName, int score, TypingStats stats) {` | Constructor 2: WPM, accuracy and date are taken from the finished round. |
| Variable & Data Types (static final, modifiers) | `ui/Theme.java` | `src/ui/Theme.java:18` `public static final Color BACKGROUND = new Color(30, 30, 46);     // #1E1E2E` | All colors, fonts and paddings are `public static final` constants in one class. |
| Variable & Data Types (wrapper, autoboxing) | `model/TypingStats.java` | `src/model/TypingStats.java:59` `Integer oldCount = mistakes.get(key);` | `char` is boxed to `Character` as map key; the count is an `Integer` that can be `null`. |
| Variable & Data Types (access modifiers) | `mode/GameMode.java` | `src/mode/GameMode.java:16` `protected String name;` | `protected` so the child classes can use it; other fields in the project are `private` with getters. |
| Inheritance | `mode/ClassicMode.java` | `src/mode/ClassicMode.java:19` `super(NAME, wordBank);` | `ClassicMode extends GameMode` and calls the parent constructor with `super(...)`. |
| Inheritance (multilevel) | `mode/LetterFocusMode.java` | `src/mode/LetterFocusMode.java:10` `public class LetterFocusMode extends ClassicMode {` | `GameMode` ← `ClassicMode` ← `LetterFocusMode`: it inherits typing, ending and scoring from Classic. |
| Inheritance (from Java classes) | `ui/MainFrame.java`, `engine/GameTimer.java` | `src/ui/MainFrame.java:22` `public class MainFrame extends JFrame {` | Our window extends `JFrame`; our timer extends `Thread`; every screen extends `JPanel`. |
| Encapsulation | `model/Player.java` | `src/model/Player.java:29` `this.name = validate(name);` | `name` is private and can only be set through a setter that checks the rules first. |
| Encapsulation | `model/TypingStats.java` | `src/model/TypingStats.java:51` `public void recordKeystroke(char typed, char expected) {` | Counters are private, there are no setters; this method is the only way to change them. |
| Encapsulation | `model/TypingStats.java` | `src/model/TypingStats.java:96` `return new HashMap<>(mistakes);` | The getter returns a copy, so outside code cannot change the original map. |
| Polymorphism – runtime (overriding + upcasting) | `ui/GamePanel.java` | `src/ui/GamePanel.java:33` `private GameMode mode;` | The variable has the parent type; the real object is `ClassicMode` or `LetterFocusMode`. |
| Polymorphism – runtime | `ui/GamePanel.java` | `src/ui/GamePanel.java:242` `targetPane.setText(buildColoredHtml(mode.getCurrentTarget(), typed, mode.getFocusLetter()));` | `getFocusLetter()` is overridden in `LetterFocusMode`; Java picks the right version, no `instanceof` needed. |
| Polymorphism – runtime | `ui/MainFrame.java` | `src/ui/MainFrame.java:119` `return new LetterFocusMode(wordBank, focusLetter);` | Upcasting: the method returns `GameMode`, the object is a `LetterFocusMode`. |
| Polymorphism – compile-time (overloading) | `model/TypingStats.java` | `src/model/TypingStats.java:85` `public double getWpm(long elapsedMillis) {` | Same name as `getWpm()`, different parameter; the compiler chooses by the arguments. |
| Polymorphism – compile-time (overloading) | `ui/MainFrame.java` | `src/ui/MainFrame.java:160` `public void showLeaderboard(String focus) {` | `showLeaderboard()` (from the menu) and `showLeaderboard(String)` (from the result screen). |
| Abstraction – abstract class | `mode/GameMode.java` | `src/mode/GameMode.java:10` `public abstract class GameMode {` | Cannot be created with `new`; has abstract methods (no body) and normal methods. |
| Abstraction – abstract method | `mode/GameMode.java` | `src/mode/GameMode.java:36` `public abstract int calculateScore();` | Every mode must write its own version. |
| Abstraction – interface | `engine/TimerListener.java` | `src/engine/TimerListener.java:10` `void onTick(long elapsedMillis);` | The timer only knows this interface, so it does not need Swing. `GamePanel` implements it. |
| Generics – own generic interface | `data/DataStore.java` | `src/data/DataStore.java:11` `List<T> loadAll();` | `T` is a type parameter, filled in by the implementing class. |
| Generics – type argument | `data/LeaderboardStore.java` | `src/data/LeaderboardStore.java:19` `public class LeaderboardStore implements DataStore<ScoreEntry> {` | Here `T = ScoreEntry`, so `loadAll()` returns `List<ScoreEntry>`. |
| Generics – Comparable | `model/ScoreEntry.java` | `src/model/ScoreEntry.java:50` `public int compareTo(ScoreEntry other) {` | `ScoreEntry implements Comparable<ScoreEntry>`: higher score first, then higher WPM. |
| Collections – ArrayList | `data/WordBank.java` | `src/data/WordBank.java:91` `if (word.indexOf(letter) >= 0) {` | Inside a loop over `ArrayList<String> words`; matching words go into a new `ArrayList`. |
| Collections – HashMap | `model/TypingStats.java` | `src/model/TypingStats.java:24` `private HashMap<Character, Integer> mistakes;` | Key = letter that should have been typed, value = how many times it was missed. |
| Collections – Map.Entry loop | `ui/KeyboardHeatmapPanel.java` | `src/ui/KeyboardHeatmapPanel.java:108` `for (Map.Entry<Character, JLabel> entry : keyLabels.entrySet()) {` | Loops over all (letter, key label) pairs to color each key. |
| Collections – sort | `data/LeaderboardStore.java` | `src/data/LeaderboardStore.java:134` `Collections.sort(matchingEntries);` | Sorts the scores using `ScoreEntry.compareTo`. |
| Collections – shuffle | `data/WordBank.java` | `src/data/WordBank.java:114` `Collections.shuffle(chosen);` | Mixes the 24 focus words and 6 normal words into a random order. |
| String – indexOf | `data/WordBank.java` | `src/data/WordBank.java:91` `if (word.indexOf(letter) >= 0) {` | `indexOf` returns -1 if the letter is not in the word, so `>= 0` = "contains the letter". |
| String – charAt / comparison | `ui/GamePanel.java` | `src/ui/GamePanel.java:313` `if (typed.charAt(index) == target.charAt(index)) {` | Compares the typed letter with the target letter at the same position. |
| String – StringBuilder | `ui/GamePanel.java` | `src/ui/GamePanel.java:300` `html.append(target.charAt(i));` | The colored HTML text is built with a `StringBuilder` in a loop, rebuilt after every key. |
| String – StringBuilder (joining) | `data/WordBank.java` | `src/data/WordBank.java:124` `builder.append(" ");` | Joins the 30 words with one space between them. |
| String – split | `model/ScoreEntry.java` | `src/model/ScoreEntry.java:78` `String[] parts = line.split(",");` | Splits one CSV line into 6 columns. |
| String – format | `model/ScoreEntry.java` | `src/model/ScoreEntry.java:68` `return String.format(Locale.US, "%s,%s,%d,%.2f,%.2f,%s", name, mode, score, wpm, accuracy, date);` | Builds one CSV line with 2 decimals. |
| String – validation (Username Validator practicum) | `model/Player.java` | `src/model/Player.java:60` `if (!Character.isLetterOrDigit(c) && c != '_') {` | Checks the name letter by letter with a loop, no regex. Also uses `trim`, `length`, `equalsIgnoreCase`. |
| Exception – custom (checked) | `data/WordFileException.java` | `src/data/WordFileException.java:8` `public class WordFileException extends Exception {` | Thrown when the word file is missing or empty; the caller must catch it. |
| Exception – custom (checked) | `data/InvalidUsernameException.java` | `src/data/InvalidUsernameException.java:8` `public class InvalidUsernameException extends Exception {` | Thrown by `Player.validate`; the message names the broken rule. |
| Exception – throw | `model/Player.java` | `src/model/Player.java:54` `throw new InvalidUsernameException("Nama harus diawali huruf.");` | `throw` creates the exception when a rule is broken. |
| Exception – try/catch + dialog | `ui/MenuPanel.java` | `src/ui/MenuPanel.java:218` `JOptionPane.showMessageDialog(this, e.getMessage(), "Nama tidak valid", JOptionPane.WARNING_MESSAGE);` | The menu catches the exception and shows its message. |
| Exception – try-with-resources | `data/LeaderboardStore.java` | `src/data/LeaderboardStore.java:57` `try (BufferedReader reader = new BufferedReader(new FileReader(file))) {` | The reader is closed automatically, even when an error happens. |
| Exception – try/catch per line | `data/LeaderboardStore.java` | `src/data/LeaderboardStore.java:79` `entries.add(ScoreEntry.fromCsvLine(line));` | One broken CSV line is skipped with a warning; the rest of the file is still read. |
| Exception – unchecked (IllegalArgumentException) | `model/ScoreEntry.java` | `src/model/ScoreEntry.java:96` `throw new IllegalArgumentException("Angka tidak valid: " + line);` | A broken CSV line is reported with a clear message. |
| Assertion | `model/TypingStats.java` | `src/model/TypingStats.java:75` `assert accuracy >= 0 && accuracy <= 100 : "Akurasi harus di antara 0 dan 100: " + accuracy;` | Checks an internal assumption (not user input). Active with `java -ea` (`run.bat`). |
| Assertion | `data/WordBank.java` | `src/data/WordBank.java:68` `assert !list.isEmpty() : "Daftar kata kosong";` | The word list must never be empty when we pick a random word. |
| Java IO – BufferedReader (file) | `data/LeaderboardStore.java` | `src/data/LeaderboardStore.java:60` `while ((line = reader.readLine()) != null) {` | Reads the CSV line by line, the same loop as in the slides. |
| Java IO – BufferedWriter | `data/LeaderboardStore.java` | `src/data/LeaderboardStore.java:99` `writer.write(entry.toCsvLine());` | Writes one score per line (with `newLine()`). |
| Java IO – File | `data/LeaderboardStore.java` | `src/data/LeaderboardStore.java:92` `folder.mkdirs();` | Creates the `.ketikin` folder in the user's home if it does not exist (`exists()`, `mkdirs()`). |
| Java IO – resource in the jar | `data/WordBank.java` | `src/data/WordBank.java:47` `InputStream stream = getClass().getResourceAsStream(resourcePath);` | Reads `words_en.txt` from inside the jar (a jar file is read-only). |
| GUI – JFrame + CardLayout | `ui/MainFrame.java` | `src/ui/MainFrame.java:106` `cardLayout.show(cardPanel, name);` | One window, four screens; `CardLayout` shows one at a time. |
| GUI – layout managers | `ui/MenuPanel.java` | `src/ui/MenuPanel.java:122` `ButtonGroup group = new ButtonGroup();` | The menu nests `BorderLayout`, `FlowLayout` and a `GridLayout(3, 9)` of letter buttons. |
| GUI – event handling (lambda) | `ui/MenuPanel.java` | `src/ui/MenuPanel.java:146` `selectedLetter = letter;` | Inside `button.addActionListener(e -> { ... })`, the practicum style. |
| GUI – event handling (KeyListener) | `ui/GamePanel.java` | `src/ui/GamePanel.java:127` `inputField.addKeyListener(new KeyListener() {` | Anonymous inner class; `keyReleased` checks the input after every key. |
| GUI – input validation + JOptionPane | `ui/MainFrame.java` | `src/ui/MainFrame.java:169` `int choice = JOptionPane.showOptionDialog(this, message, title,` | Yes/No dialog ("Ya"/"Tidak") before quitting a round or the app. |
| GUI – JTable & Collections | `ui/LeaderboardPanel.java` | `src/ui/LeaderboardPanel.java:94` `tableModel = new DefaultTableModel(COLUMN_NAMES, 0) {` | Practicum pattern: columns + 0 rows, filled from a `List` with `addRow` in a loop. |
| GUI – JComboBox | `ui/LeaderboardPanel.java` | `src/ui/LeaderboardPanel.java:73` `filterBox = new JComboBox<>();` | The leaderboard filter: "Semua", "Normal", "A" … "Z". |
| GUI – integrating GUI with OOP | `ui/GamePanel.java` | `src/ui/GamePanel.java:25` `public class GamePanel extends JPanel implements TimerListener {` | A screen is a class that extends `JPanel` and implements our own interface. |
| Multithreading – extends Thread | `engine/GameTimer.java` | `src/engine/GameTimer.java:27` `while (running) {` | `GameTimer extends Thread`; `run()` loops: `Thread.sleep(100)`, then `onTick`. |
| Multithreading – start | `ui/GamePanel.java` | `src/ui/GamePanel.java:173` `timer.start();` | Starts the new thread; a new `GameTimer` is made every round. |
| Multithreading – volatile stop flag | `engine/GameTimer.java` | `src/engine/GameTimer.java:14` `private volatile boolean running = true;` | Set to `false` by the EDT; the timer thread sees it and stops. |
| Multithreading – InterruptedException | `engine/GameTimer.java` | `src/engine/GameTimer.java:30` `} catch (InterruptedException e) {` | `sleep` can be interrupted; we use it as the signal to stop. |
| Multithreading – EDT / invokeLater | `ui/GamePanel.java` | `src/ui/GamePanel.java:259` `SwingUtilities.invokeLater(new Runnable() {` | Only the EDT may change Swing components, so the timer hands its UI work to the EDT. |
| Multithreading – Runnable | `app/Main.java` | `src/app/Main.java:17` `SwingUtilities.invokeLater(new Runnable() {` | The window is created on the EDT, passed as a `Runnable`. |

## 2. The 4 OOP pillars in KetikIn

### Inheritance
`ClassicMode` extends the abstract `GameMode`, and `LetterFocusMode` extends `ClassicMode` (multilevel).
`LetterFocusMode` only changes how the 30 words are chosen; everything else is inherited.
Also: `MainFrame extends JFrame`, `GameTimer extends Thread`, every screen `extends JPanel`.

```java
// src/mode/LetterFocusMode.java:10, :29, :32
public class LetterFocusMode extends ClassicMode {
    super("Fokus huruf " + Character.toUpperCase(letter), wordBank);
    targetText = wordBank.randomWordsWithFocus(focusLetter, WORD_COUNT, FOCUS_WORD_COUNT);
```

### Encapsulation
Data is `private` and only changes through methods that keep it correct. `Player` checks the name in its setter;
`TypingStats` has no setters at all and returns a copy of its mistake map.

```java
// src/model/Player.java:17, :35
private String name;
this.name = validate(name);   // inside setName: check first, then store
```

### Polymorphism
**Runtime:** `GamePanel` keeps a `GameMode` variable and calls methods on it; the overridden version of the real
object runs (`getFocusLetter`, `getDescription`, `calculateScore`). **Compile-time:** overloaded methods and constructors
(`getWpm()` / `getWpm(long)`, two `ScoreEntry` constructors, two `showLeaderboard` methods).

```java
// src/ui/GamePanel.java:33 and src/mode/LetterFocusMode.java:31
private GameMode mode;                       // ClassicMode or LetterFocusMode inside
return focusLetter;                          // LetterFocusMode's override of getFocusLetter()
```

### Abstraction
`GameMode` is an abstract class: it says *what* every mode must do (`getCurrentTarget`, `submit`, `isOver`,
`calculateScore`) but not *how*. Interfaces `TimerListener` and `DataStore<T>` describe a contract without code.

```java
// src/mode/GameMode.java:10, :49
public abstract class GameMode {
    public abstract int calculateScore();
```

## 3. Numbers for the paper

- 21 classes/interfaces in `src` (6 packages) + 1 test class. About 3100 lines of Java including comments.
- `words_en.txt`: 2626 English words; every letter a–z appears in at least 57 words (q is the rarest; j 61, x 81, z 67).
- `SelfTest`: 62 checks, all PASS.
- Runs on Java 8 and newer (compiled with `--release 8`), no external libraries.
