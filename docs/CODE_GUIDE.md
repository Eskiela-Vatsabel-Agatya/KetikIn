# KetikIn Code Guide

This guide is for a classmate who has never seen the code. Read it top to bottom once, then use it as a map.
Line numbers point to the current code (`src/...java:NN`). Code comments are in Indonesian; this guide is in English.

KetikIn is a typing trainer. The player types 30 words. In **Normal** mode the words are random. With a
**focus letter** (A–Z), 24 of the 30 words contain that letter and it is shown in **bold**. After the round,
the result screen shows WPM, accuracy and a keyboard "heatmap" of the letters that were missed.
(During development the app also had Time Attack, Survival and Code Mode; they were removed in the pivot.)

---

## 1. Big picture: from double-click to result screen

1. **Double-click `KetikIn.jar`.** Java starts `app.Main` (`build.bat` writes `app.Main` into the jar as the entry point).
2. **`Main.main`** asks the Swing thread (the EDT) to build the window: `src/app/Main.java:17` `SwingUtilities.invokeLater(new Runnable() {`.
3. **`MainFrame` constructor** loads the word list from inside the jar: `src/ui/MainFrame.java:71` `wordBank.loadWords(WordBank.WORDS_PATH);`.
   If that fails, an error dialog is shown and the program ends.
4. **`MainFrame.buildScreens`** creates the 4 screens (menu, game, result, leaderboard) and stacks them in a `CardLayout`.
5. **Menu (`MenuPanel`)**: the player types a name, picks "Normal" or a letter, and clicks **Mulai**.
   The name is checked by `Player` (`src/ui/MenuPanel.java:216` `player = new Player(nameField.getText());`).
6. **`MainFrame.startGame`** creates the right mode object (`ClassicMode` or `LetterFocusMode`) and opens the game screen:
   `src/ui/MainFrame.java:124` `GameMode mode = createMode(focusLetter);`.
7. **`GamePanel.startGame`** starts the clock in `TypingStats` and starts a new timer thread:
   `src/ui/GamePanel.java:172` `timer = new GameTimer(this);`.
8. **Every key** the player releases calls `handleInput` (section 4 below). The text is colored green/red again.
9. **Every 100 ms** the timer thread calls `onTick`, which updates the time and WPM labels (section 5 below).
10. **Last letter typed**: `GamePanel.finishGame` stops the timer and calls `src/ui/GamePanel.java:179` `mainFrame.showResult(mode);`.
11. **`MainFrame.showResult`** calculates the score, saves it to the CSV file, and fills the result screen with the
    numbers and the heatmap (section 6 below).
12. On the result screen: **Main lagi** (new round, same focus), **Menu**, or **Leaderboard** (opens filtered on the same focus).

---

## 2. Package map

| Package | What is in it | Uses Swing? |
|---|---|---|
| `app` | `Main`: the starting point | yes |
| `ui` | All screens (`MainFrame`, `MenuPanel`, `GamePanel`, `ResultPanel`, `LeaderboardPanel`, `KeyboardHeatmapPanel`) and `Theme` (colors/fonts) | yes |
| `mode` | The game rules: `GameMode` (abstract), `ClassicMode`, `LetterFocusMode` | **no** |
| `model` | Data objects: `Player`, `TypingStats`, `ScoreEntry` | **no** |
| `data` | Files: `WordBank` (word list in the jar), `LeaderboardStore` (CSV file), `DataStore<T>`, the two custom exceptions | **no** |
| `engine` | The timer thread: `GameTimer`, `TimerListener` | **no** |
| `resources` | `words_en.txt`: 2626 English words, packed into the jar | – |
| `test/` | `SelfTest`: checks the non-GUI classes without JUnit | no |

**The rule: logic never imports Swing.** Only `ui` and `app` import `javax.swing` / `java.awt`.

Why?
- The rules (score, WPM, which words) can be tested by `SelfTest` without opening a window.
- If one day we change the screens, the logic does not change.
- It is easier to explain: "`mode` decides, `ui` shows".

How does the timer (in `engine`) tell the screen something without importing Swing? Through the interface
`TimerListener`. `GameTimer` only knows "someone who has `onTick`". `GamePanel` is that someone.

---

## 3. One section per class

### app/Main.java (Hanif Rafa Ramidzar)
**Purpose:** starts the program. Builds `MainFrame` on the EDT and shows it.
**Important method:** `main`.

Lines worth understanding:
- `src/app/Main.java:17` `SwingUtilities.invokeLater(new Runnable() {` — Swing windows may only be built by the EDT, and `main` runs on another thread. So we hand the work to the EDT as a `Runnable` (anonymous inner class).
- `src/app/Main.java:20` `MainFrame frame = new MainFrame();` — creating the window also loads the word list. If loading failed, the frame is thrown away (`dispose`) and the program ends.

### ui/MainFrame.java (Hanif Rafa Ramidzar)
**Purpose:** the only window. Holds the 4 screens in a `CardLayout` and the shared objects (`WordBank`, `LeaderboardStore`, the current `Player`). Panels call its methods to switch screens.
**Important fields:** `cardLayout`, `cardPanel`, the 4 panels, `wordBank`, `leaderboardStore`, `player`.
**Important methods:** `loadWordBank`, `buildScreens`, `showScreen`, `createMode`, `startGame(char)`, `showResult`, `showLeaderboard()` / `showLeaderboard(String)`, `askConfirmation`.

Lines worth understanding:
- `src/ui/MainFrame.java:22` `public class MainFrame extends JFrame {` — inheritance: our window *is a* `JFrame` with extra content.
- `src/ui/MainFrame.java:106` `cardLayout.show(cardPanel, name);` — `CardLayout` keeps all screens stacked like cards and shows only one.
- `src/ui/MainFrame.java:116` `if (focusLetter == GameMode.NO_FOCUS) {` — no letter picked → `new ClassicMode(...)`, otherwise `new LetterFocusMode(...)`. The return type is `GameMode` (upcasting).
- `src/ui/MainFrame.java:136` `int previousBest = leaderboardStore.getPersonalBest(player.getName(), focus);` — the old best is read *before* the new score is saved, so we can say "Rekor pribadi baru!".
- `src/ui/MainFrame.java:155` `public void showLeaderboard() {` and `src/ui/MainFrame.java:160` `public void showLeaderboard(String focus) {` — two `showLeaderboard` methods with the same name and different parameters (overloading): from the menu the filter is "Semua", from the result screen it is the focus that was just played.

### ui/MenuPanel.java (Hanif Rafa Ramidzar)
**Purpose:** first screen: name field, letter picker ("Normal" + A–Z), "Mulai", "Leaderboard", "Keluar".
**Important fields:** `nameField`, `letterButtons` (an `ArrayList<JToggleButton>`), `selectedLetter` (a `char`, or `GameMode.NO_FOCUS`), `focusLabel`.
**Important methods:** `createLetterGrid`, `createLetterButton`, `updateLetterButtons`, `startGame`, `confirmExit`.

Lines worth understanding:
- `src/ui/MenuPanel.java:122` `ButtonGroup group = new ButtonGroup();` — all 27 toggle buttons are in one group, so only one can be selected.
- `src/ui/MenuPanel.java:128` `for (char c = 'A'; c <= 'Z'; c++) {` — a `char` can be used as a loop counter: 'A', 'B', … 'Z'.
- `src/ui/MenuPanel.java:146` `selectedLetter = letter;` — inside the button's lambda: remember which letter was clicked, then recolor all buttons.
- `src/ui/MenuPanel.java:141` `button.setContentAreaFilled(false);` — Java's default look paints a selected toggle button gray; with this (and `setOpaque(true)`) our own color is used.
- `src/ui/MenuPanel.java:218` `JOptionPane.showMessageDialog(this, e.getMessage(), "Nama tidak valid", JOptionPane.WARNING_MESSAGE);` — the name was wrong: show the exception's message and stay on the menu.

### ui/GamePanel.java (Eskiela Vatsabel Agatya)
**Purpose:** the game screen: colored target text, input field, time, WPM, accuracy, "Keluar ke Menu".
**Important fields:** `mode` (type `GameMode`), `timer` (the current `GameTimer`), `previousText` (the input at the last check), `targetPane` (shows the colored HTML), `inputField`.
**Important methods:** `startGame`, `handleInput`, `recordNewKeystrokes`, `refreshDisplay`, `onTick`, `handleTick`, `finishGame`, `quitGame`, `buildColoredHtml`, `styleForChar`.

Lines worth understanding:
- `src/ui/GamePanel.java:33` `private GameMode mode;` — runtime polymorphism: the variable's type is the parent class; the object inside is `ClassicMode` or `LetterFocusMode`.
- `src/ui/GamePanel.java:124` `inputField.setTransferHandler(null);` — turns off paste, so the target text cannot be pasted in.
- `src/ui/GamePanel.java:242` `targetPane.setText(buildColoredHtml(mode.getCurrentTarget(), typed, mode.getFocusLetter()));` — `getFocusLetter()` is overridden in `LetterFocusMode`, so no `instanceof` is needed.
- `src/ui/GamePanel.java:263` `if (tickingTimer != timer || mode == null || mode.isOver()) {` — ignore a "late" tick from an old timer or a finished round.
- `src/ui/GamePanel.java:292` `if (!style.equals(previousStyle)) {` — letters next to each other with the same style share one `<span>`. Fewer tags = Java 9+ wraps the lines correctly.

### ui/ResultPanel.java (Satrio Ananda Widiyanto)
**Purpose:** shows the result of a round: title with the focus, score, WPM, accuracy, time, correct/wrong keystrokes and the heatmap. Buttons "Main lagi", "Menu", "Leaderboard".
**Important fields:** the value labels, `heatmapPanel`, `lastFocusLetter`, `lastFocusLabel`.
**Important method:** `showResult(GameMode mode, Player player, boolean newPersonalBest)`.

Lines worth understanding:
- `src/ui/ResultPanel.java:131` `lastFocusLetter = mode.getFocusLetter();` — remembered so "Main lagi" can start a round with the same focus.
- `src/ui/ResultPanel.java:135` `titleLabel.setText("Hasil " + player.getName() + " - " + mode.getName());` — the mode name is "Mode normal" or e.g. "Fokus huruf J", so the focus is visible.
- `src/ui/ResultPanel.java:143` `scoreLabel.setText(String.valueOf(mode.calculateScore()));` — runtime polymorphism: the score formula of the real mode object is used.
- `src/ui/ResultPanel.java:149` `heatmapPanel.showMistakes(stats.getMistakes());` — gives the mistake map to the heatmap.

### ui/LeaderboardPanel.java (Hanif Rafa Ramidzar)
**Purpose:** one `JTable` with the top 10 scores, and a `JComboBox` filter above it ("Semua", "Normal", "A" … "Z").
**Important fields:** `filterBox`, `tableModel`, `store`.
**Important methods:** `createFilterPanel`, `createTablePanel`, `showFilter`, `refresh`, `styleTable`.

Lines worth understanding:
- `src/ui/LeaderboardPanel.java:94` `tableModel = new DefaultTableModel(COLUMN_NAMES, 0) {` — the practicum pattern: column names + 0 rows. The `{ ... }` is an anonymous inner class that overrides `isCellEditable` to return `false`.
- `src/ui/LeaderboardPanel.java:82` `refresh();` — inside the combo box's lambda: whenever the filter changes, reload the table.
- `src/ui/LeaderboardPanel.java:141` `tableModel.setRowCount(0);` — delete all old rows first.
- `src/ui/LeaderboardPanel.java:142` `List<ScoreEntry> topEntries = store.getTop(focus, TOP_LIMIT);` — ask the store for the top 10 of this filter, then add them row by row with `addRow` in a `for` loop.
- `src/ui/LeaderboardPanel.java:134` `filterBox.setSelectedItem(focus);` — choosing a filter from code also fires the listener, so the table reloads by itself.

### ui/KeyboardHeatmapPanel.java (Satrio Ananda Widiyanto)
**Purpose:** draws a QWERTY keyboard out of `JLabel`s. A key gets redder the more often that letter was missed. Below it: the 3 most missed keys.
**Important fields:** `keyLabels` (`HashMap<Character, JLabel>`: letter → its key label), `topMissedLabel`.
**Important methods:** `createKeyboard`, `createRow`, `createKey`, `showMistakes`, `findMaxCount`, `colorKey`, `calculateColor`, `buildTopText`.

Lines worth understanding:
- `src/ui/KeyboardHeatmapPanel.java:39` `private HashMap<Character, JLabel> keyLabels;` — lets us find the label of letter 'e' directly with `keyLabels.get('e')`.
- `src/ui/KeyboardHeatmapPanel.java:108` `for (Map.Entry<Character, JLabel> entry : keyLabels.entrySet()) {` — loop over every (letter, label) pair.
- `src/ui/KeyboardHeatmapPanel.java:138` `ratio = (double) count / maxCount;` — 0.0 = never missed, 1.0 = the most missed key. `(double)` avoids integer division.
- `src/ui/KeyboardHeatmapPanel.java:155` `int red = (int) Math.round(start.getRed() + (end.getRed() - start.getRed()) * ratio);` — mix white and dark red: `start + (end - start) × ratio`, the same for green and blue.
- `src/ui/KeyboardHeatmapPanel.java:96` `label.setOpaque(true);` — a `JLabel` is transparent by default; without this the background color would not show.

### ui/Theme.java (Hanif Rafa Ramidzar)
**Purpose:** all colors, fonts and paddings in one place, plus two helpers (`createLabel`, `styleButton`). Everything is `static`, so no object is needed.
Lines worth understanding:
- `src/ui/Theme.java:18` `public static final Color BACKGROUND = new Color(30, 30, 46);     // #1E1E2E` — `static final` = a constant shared by everyone.
- `src/ui/Theme.java:58` `public static void styleButton(JButton button) {` — gives every button the same look so the panels don't repeat these lines.

### mode/GameMode.java (Eskiela Vatsabel Agatya)
**Purpose:** the abstract parent of all modes. Holds what every mode has (`name`, `wordBank`, `stats`) and lists the methods every mode must write.
**Important fields:** `protected String name`, `protected WordBank wordBank`, `protected TypingStats stats`; constants `NO_FOCUS = ' '` and `FOCUS_NORMAL = "Normal"`.
**Important methods:** abstract `getDescription`, `getCurrentTarget`, `submit`, `isOver`, `calculateScore`; normal `getName`, `getStats`; overridable `getFocusLetter`, `getFocusLabel`.

Lines worth understanding:
- `src/mode/GameMode.java:10` `public abstract class GameMode {` — you cannot write `new GameMode(...)`. Only real modes can be created.
- `src/mode/GameMode.java:29` `public abstract String getCurrentTarget();` — no body: each child class must write its own.
- `src/mode/GameMode.java:49` `return NO_FOCUS;` — the default "no focus letter". `LetterFocusMode` overrides it.

### mode/ClassicMode.java (Eskiela Vatsabel Agatya)
**Purpose:** Normal mode: 30 random words; the round ends when the typed text is as long as the target. Score = `round(WPM × accuracy / 100 × 10)`.
**Important fields:** `protected String targetText`, `protected boolean finished`.
**Important methods:** two constructors (public and protected), `getDescription`, `getCurrentTarget`, `submit`, `isOver`, `calculateScore`.

Lines worth understanding:
- `src/mode/ClassicMode.java:19` `super(NAME, wordBank);` — first run the parent constructor (fills `name`, `wordBank`, `stats`).
- `src/mode/ClassicMode.java:26` `protected ClassicMode(String modeName, WordBank wordBank) {` — a second constructor only for child classes: it lets `LetterFocusMode` choose its own name and words.
- `src/mode/ClassicMode.java:57` `double rawScore = stats.getWpm() * stats.getAccuracy() / 100 * SCORE_FACTOR;` — the score formula.

### mode/LetterFocusMode.java (Eskiela Vatsabel Agatya)
**Purpose:** letter practice. Same as Normal, but 24 of the 30 words contain the chosen letter.
**Important field:** `private char focusLetter` (always lowercase).
**Important methods:** constructor, `getDescription`, `getFocusLetter`, `getFocusLabel` (all `@Override`).

Lines worth understanding:
- `src/mode/LetterFocusMode.java:10` `public class LetterFocusMode extends ClassicMode {` — multi-level inheritance: `GameMode` ← `ClassicMode` ← `LetterFocusMode`. It inherits `submit`, `isOver`, `calculateScore` without rewriting them.
- `src/mode/LetterFocusMode.java:18` `super("Fokus huruf " + Character.toUpperCase(letter), wordBank);` — uses Classic's protected constructor, e.g. name "Fokus huruf J".
- `src/mode/LetterFocusMode.java:20` `targetText = wordBank.randomWordsWithFocus(focusLetter, WORD_COUNT, FOCUS_WORD_COUNT);` — the only real difference from Classic: how the 30 words are chosen.
- `src/mode/LetterFocusMode.java:36` `return String.valueOf(Character.toUpperCase(focusLetter));` — the value stored in the CSV, e.g. "J".

### model/TypingStats.java (Eskiela Vatsabel Agatya)
**Purpose:** counts keystrokes, calculates accuracy and WPM, and remembers which letters were missed.
**Important fields (all private, no setters):** `totalKeystrokes`, `correctKeystrokes`, `startTimeMillis`, `endTimeMillis`, `mistakes` (`HashMap<Character, Integer>`).
**Important methods:** `start`, `stop`, `recordKeystroke`, `getAccuracy`, `getWpm()` / `getWpm(long)`, `getMistakes`.

Formulas: accuracy = correct ÷ total × 100 (100 when total is 0). WPM = (correct ÷ 5) ÷ minutes (0 under 1 second).

Lines worth understanding:
- `src/model/TypingStats.java:51` `public void recordKeystroke(char typed, char expected) {` — the **only** way to change the counters from outside (encapsulation).
- `src/model/TypingStats.java:59` `Integer oldCount = mistakes.get(key);` — `null` if the letter was never missed; then we treat it as 0 and `put(key, oldCount + 1)`.
- `src/model/TypingStats.java:75` `assert accuracy >= 0 && accuracy <= 100 : "Akurasi harus di antara 0 dan 100: " + accuracy;` — an internal check (only active with `java -ea`).
- `src/model/TypingStats.java:85` `public double getWpm(long elapsedMillis) {` — overload of `getWpm()`: same name, different parameter.
- `src/model/TypingStats.java:96` `return new HashMap<>(mistakes);` — returns a **copy**, so other classes cannot change our data.

### model/Player.java (Hanif Rafa Ramidzar)
**Purpose:** holds the player name. The name can only be set through `setName`, which always checks the rules first.
Rules (after `trim()`): 3–15 characters, starts with a letter, only letters/digits/`_`, not "admin", "null" or "guest".

Lines worth understanding:
- `src/model/Player.java:17` `private String name;` — private: no one can put a wrong name in directly.
- `src/model/Player.java:29` `this.name = validate(name);` — the setter checks first, then stores.
- `src/model/Player.java:60` `if (!Character.isLetterOrDigit(c) && c != '_') {` — checked one character at a time in a loop, no regex. This also keeps commas out, so the CSV is safe.

### model/ScoreEntry.java (Hanif Rafa Ramidzar)
**Purpose:** one leaderboard row: name, focus (stored in the field/column named `mode`), score, WPM, accuracy, date. Converts to and from a CSV line and can be sorted.

Lines worth understanding:
- `src/model/ScoreEntry.java:27` `public ScoreEntry(String name, String mode, int score, double wpm, double accuracy, String date) {` and `src/model/ScoreEntry.java:37` `public ScoreEntry(String playerName, String modeName, int score, TypingStats stats) {` — constructor overloading: one for reading from the file, one for a new score.
- `src/model/ScoreEntry.java:50` `public int compareTo(ScoreEntry other) {` — negative = `this` comes first. Higher score first; same score → higher WPM first.
- `src/model/ScoreEntry.java:68` `return String.format(Locale.US, "%s,%s,%d,%.2f,%.2f,%s", name, mode, score, wpm, accuracy, date);` — `Locale.US` forces a dot (64.30), because an Indonesian setting would write a comma and break the CSV.
- `src/model/ScoreEntry.java:78` `String[] parts = line.split(",");` — split one CSV line into its 6 columns. A broken line throws `IllegalArgumentException`.

### data/WordBank.java (Satrio Ananda Widiyanto)
**Purpose:** loads the word list from inside the jar and gives random words: normal (`randomWords`) or with a focus letter (`randomWordsWithFocus`).
**Important fields:** `words` (`ArrayList<String>`), `random`.

Lines worth understanding:
- `src/data/WordBank.java:47` `InputStream stream = getClass().getResourceAsStream(resourcePath);` — a file *inside* the jar is not a normal file, so we open it as a stream. `null` = not found → `WordFileException`.
- `src/data/WordBank.java:91` `if (word.indexOf(letter) >= 0) {` — `indexOf` gives the position of the letter, or -1. So `>= 0` means "the word contains the letter".
- `src/data/WordBank.java:105` `chosen.add(randomFrom(focusWords));` — step 1: 24 random words that contain the letter. Step 2 adds 6 normal random words.
- `src/data/WordBank.java:114` `Collections.shuffle(chosen);` — step 3: mix them, so the 6 normal words are not always at the end. Step 4 joins them with a `StringBuilder`.

### data/LeaderboardStore.java (Satrio Ananda Widiyanto)
**Purpose:** reads and writes `<user home>\.ketikin\leaderboard.csv`; gives the top 10 for a filter and a player's personal best.
**Important fields:** `file`, `lastErrorMessage`; constants `CSV_HEADER`, `FILTER_ALL = "Semua"`.
**Important methods:** `loadAll`, `saveAll`, `addScore`, `getTop`, `getPersonalBest`.

Lines worth understanding:
- `src/data/LeaderboardStore.java:19` `public class LeaderboardStore implements DataStore<ScoreEntry> {` — our generic interface with `T = ScoreEntry`.
- `src/data/LeaderboardStore.java:33` `File folder = new File(System.getProperty("user.home"), FOLDER_NAME);` — saved in the user's home folder, because a program cannot write into its own jar.
- `src/data/LeaderboardStore.java:79` `entries.add(ScoreEntry.fromCsvLine(line));` — inside a try/catch *per line*: one broken line is skipped with a warning, the rest are still read.
- `src/data/LeaderboardStore.java:113` `if (lastErrorMessage != null) {` — if the old file could not be read, we do **not** save, or all old scores would be overwritten.
- `src/data/LeaderboardStore.java:128` `if (focus.equals(FILTER_ALL) || entry.getMode().equals(focus)) {` — the whole filter: "Semua" keeps everything, otherwise the focus must match.

### data/DataStore.java (Satrio Ananda Widiyanto)
**Purpose:** our own generic interface: `List<T> loadAll()` and `boolean saveAll(List<T> items)`. `T` is "the type of data", chosen by the class that implements it.
- `src/data/DataStore.java:11` `List<T> loadAll();`

### data/WordFileException.java and data/InvalidUsernameException.java (Satrio Ananda Widiyanto)
**Purpose:** our own checked exceptions (`extends Exception`), with a message. The compiler forces callers to handle them with try/catch.
- `src/data/WordFileException.java:8` `public class WordFileException extends Exception {`
- `src/data/InvalidUsernameException.java:8` `public class InvalidUsernameException extends Exception {`

### engine/GameTimer.java (Eskiela Vatsabel Agatya)
**Purpose:** a separate thread that "ticks" every 100 ms and tells its listener how much time has passed. One `GameTimer` per round.

Lines worth understanding:
- `src/engine/GameTimer.java:14` `private volatile boolean running = true;` — written by the EDT, read by the timer thread. `volatile` makes sure the timer sees the new value.
- `src/engine/GameTimer.java:27` `while (running) {` — loop: sleep 100 ms, then `listener.onTick(elapsed)`.
- `src/engine/GameTimer.java:46` `interrupt();` — wakes the thread out of `sleep` right away, so it stops without waiting.

### engine/TimerListener.java (Eskiela Vatsabel Agatya)
**Purpose:** the interface between the timer and the screen: one method.
- `src/engine/TimerListener.java:10` `void onTick(long elapsedMillis);`

### test/SelfTest.java (all three)
**Purpose:** a small `main` program that tests `Player`, `TypingStats`, `ScoreEntry`, `WordBank`, both modes and `LeaderboardStore`, and prints PASS/FAIL. No JUnit (no external libraries allowed). It uses a temporary CSV file, never the real leaderboard. Run with `selftest.bat`; the result should end with `0 FAIL`.

---

## 4. The journey of one keystroke

The player presses and releases the key **j**:

1. **Listener.** The `KeyListener` on the input field gets `keyReleased` and calls `handleInput()`:
   `src/ui/GamePanel.java:138` `handleInput();`
2. **Read the whole input.** `handleInput` reads the full text of the field and the target. Text longer than the target is cut off:
   `src/ui/GamePanel.java:211` `if (typed.length() > target.length()) {`
3. **Only new letters count.** `recordNewKeystrokes` compares the length with `previousText`. Shorter (Backspace) or equal = nothing to count:
   `src/ui/GamePanel.java:231` `if (typed.length() <= previousText.length()) {`
4. **Record it.** For each new letter, the typed char and the expected char go to `TypingStats`:
   `src/ui/GamePanel.java:236` `stats.recordKeystroke(typed.charAt(i), target.charAt(i));`
   Inside, total +1; correct +1 if they match; otherwise the expected letter gets +1 in the `mistakes` map.
5. **Rebuild the colored text.** `refreshDisplay` builds new HTML from scratch:
   `src/ui/GamePanel.java:242` `targetPane.setText(buildColoredHtml(mode.getCurrentTarget(), typed, mode.getFocusLetter()));`
   For every target letter, `styleForChar` picks green (correct), red + underline (wrong), underline (current position) or gray (not typed yet). The focus letter also gets bold:
   `src/ui/GamePanel.java:288` `style = style + "; font-weight:bold";`
6. **Labels updated.** WPM and accuracy are shown again:
   `src/ui/GamePanel.java:246` `accuracyLabel.setText(String.format("Akurasi: %.1f%%", stats.getAccuracy()));`
7. **Round finished?** If the typed text is as long as the target, `mode.submit(typed)` ends the round and `finishGame()` goes to the result screen:
   `src/ui/GamePanel.java:220` `if (typed.length() == target.length()) {`

---

## 5. Threads made simple

- **Two threads matter.** The **EDT** (Event Dispatch Thread) is Swing's own thread: it handles clicks and keys and draws the screen. The **timer thread** is our `GameTimer`.
- **What the timer thread does:** sleep 100 ms, compute the elapsed time, call `listener.onTick(elapsed)`, repeat while `running` is true (`src/engine/GameTimer.java:27` `while (running) {`).
- **The EDT rule:** Swing is not thread-safe; only the EDT may touch components. If two threads change a `JLabel` at the same time, the screen can show garbage or Swing can throw an exception, and such bugs are random and hard to find.
- **Why `invokeLater`:** `onTick` runs on the timer thread, so it does *not* change labels itself. It packs the work in a `Runnable` and hands it to the EDT:
  `src/ui/GamePanel.java:259` `SwingUtilities.invokeLater(new Runnable() {`
  The EDT runs it a moment later and updates the time and WPM label.
- **Late ticks:** a tick can be queued just before the round ends. The check `src/ui/GamePanel.java:263` `if (tickingTimer != timer || mode == null || mode.isOver()) {` drops ticks from an old timer or a finished round.
- **How the thread stops:** `GamePanel.stopTimer` (on finish, on quit, and before every new round) calls `timer.stopTimer()`, which sets `running = false` and calls `interrupt()`. `sleep` wakes up with an `InterruptedException`, the loop ends, `run()` returns, and the thread is finished.
- **A new thread per round**, because a `Thread` object can only be `start()`ed once (`src/ui/GamePanel.java:172` `timer = new GameTimer(this);`).

---

## 6. How a score gets saved and loaded

**Saving** (after a finished round; a quit round is never saved):
1. `MainFrame.showResult` computes the score: `src/ui/MainFrame.java:132` `int score = mode.calculateScore();`
2. Gets the focus label ("Normal" or e.g. "J"): `src/ui/MainFrame.java:133` `String focus = mode.getFocusLabel();`
3. Reads the old personal best for this player and this focus (before saving).
4. Creates the row: `src/ui/MainFrame.java:138` `ScoreEntry entry = new ScoreEntry(player.getName(), focus, score, mode.getStats());`
5. `LeaderboardStore.addScore` loads all rows, adds the new one, and writes the whole file again:
   `src/data/LeaderboardStore.java:118` `return saveAll(entries);`
6. `saveAll` creates the `.ketikin` folder if needed, then writes the header and one `toCsvLine()` per row with a `BufferedWriter`.
   Example file:
   ```
   name,mode,score,wpm,accuracy,date
   budi,J,512,64.30,97.50,2026-11-20 15:30
   sari,Normal,430,52.10,95.00,2026-11-20 15:41
   ```
7. If saving failed, the result screen still shows and a warning dialog explains why.

**Loading** (each time the leaderboard filter is chosen):
1. `LeaderboardPanel.refresh` reads the chosen filter and calls `store.getTop(focus, 10)`.
2. `loadAll` reads the file line by line with a `BufferedReader` (`src/data/LeaderboardStore.java:60` `while ((line = reader.readLine()) != null) {`). No file yet = empty list. A broken line is skipped with a warning. An unreadable file = empty list + a warning dialog.
3. `getTop` keeps the rows that match the filter, sorts them with `Collections.sort` (which uses `ScoreEntry.compareTo`), and keeps the first 10.
4. The panel clears the table and adds one row per entry.

Rows from the removed modes (`Time Attack`, `Survival`, `Code`, `Classic`) may still be in an old file. They match no filter except "Semua".

---

## 7. Things outside the course material

Java features we use that do **not** appear in the lecture slides (see `COURSE_STYLE_NOTES.md`).
Each is used because there is no simpler way, and each is commented in the code.

| Feature | Where | Why we need it |
|---|---|---|
| `SwingUtilities.invokeLater` | `app/Main.java`, `ui/GamePanel.java` (`onTick`) | Swing is not thread-safe: only the EDT may create or change components. `invokeLater` hands a `Runnable` to the EDT. |
| `CardLayout` | `ui/MainFrame.java` | Shows one screen at a time inside a single window. The slides and the practicum only show `FlowLayout`, `BorderLayout` and `GridLayout`. |
| `JComboBox<String>` | `ui/LeaderboardPanel.java` (`createFilterPanel`) | The leaderboard filter. Its `addActionListener(e -> { ... })` is the same style as our buttons. `setSelectedItem` also fires that listener, so choosing a filter from code reloads the table too. |
| `DefaultTableModel.setRowCount(0)` | `ui/LeaderboardPanel.java` (`refresh`) | Removes all old rows before the table is filled again for the new filter. |
| `JToggleButton` + `ButtonGroup` | `ui/MenuPanel.java` (`createLetterGrid`) | The letter picker: a button that stays pressed, and a group in which only one button can be pressed at a time. |
| `setContentAreaFilled(false)` + `setOpaque(true)` | `ui/MenuPanel.java` (`createLetterButton`) | Java's default look paints a selected toggle button in its own gray. These two lines make it use our `setBackground` color (accent = selected). |
| `KeyListener` (`keyReleased`) | `ui/GamePanel.java` (`createInputPanel`) | Reads the input after every key. The practicum only names `DocumentListener`; we use `KeyListener` because a `DocumentListener` may not change its own text field (we cut off letters typed past the end of the target). |
| HTML text (`<span style='color:...'>`) | `ui/GamePanel.java` (`buildColoredHtml`) | Colors, underlines and bolds parts of the target text, built with a plain loop + `StringBuilder`. The text is only `a`–`z` and single spaces, so nothing needs escaping. Lesson from testing: one tag **per character** makes Java 9+ on a scaled Windows display measure lines too short, so letters with the same style share **one** `<span>`. |
| `JEditorPane` (`"text/html"`, not editable, not focusable) | `ui/GamePanel.java` (`createTargetPanel`) | Shows the colored target text. A `JLabel` with a fixed HTML width was clipped on Java 25 with display scaling; a `JEditorPane` wraps lines at its real width. `HONOR_DISPLAY_PROPERTIES` makes it use our `Theme` font and color. |
| `volatile` | `engine/GameTimer.java` | Makes sure the timer thread sees the `running = false` written by the EDT. |
| `interrupt()` | `engine/GameTimer.java` (`stopTimer`) | Wakes the timer thread out of `Thread.sleep` right away (the slides only say that `sleep` "can throw `InterruptedException`"). |
| `Thread.currentThread()` | `ui/GamePanel.java` (`onTick`) | Tells us *which* timer thread sent a tick, so a late tick from an old, stopped timer is ignored. |
| `setCaretPosition` | `ui/GamePanel.java` (`keyReleased`) | Moves the text cursor back to the end after every key, so arrow keys and Home cannot insert letters in the middle. Known limit: a mouse click can still move the cursor until the next key is released. |
| `setTransferHandler(null)` | `ui/GamePanel.java` (`createInputPanel`) | Blocks paste into the input field (one line). |
| `InputStreamReader` + `getResourceAsStream` | `data/WordBank.java` (`readLines`) | Files packed *inside* the jar are not normal files, so `FileReader` cannot open them. |
| `System.getProperty("user.home")` | `data/LeaderboardStore.java` (constructor) | Gives the user's home folder. The leaderboard is saved there, because a program cannot write into its own jar. |
| `String.format(Locale.US, ...)` | `model/ScoreEntry.java` (`toCsvLine`) | Always writes decimals with a dot, so the CSV commas are not confused with decimal commas. |
| `SimpleDateFormat` + `Date` | `model/ScoreEntry.java` (second constructor) | Writes the date of a new score as `yyyy-MM-dd HH:mm`. |
| `Random` | `data/WordBank.java` (`randomFrom`) | Picks a random index in the word list (`nextInt`). |
| `DefaultTableCellRenderer` | `ui/LeaderboardPanel.java` (`styleTable`) | Centers the text in each table cell. We use Swing's ready-made renderer and only change its alignment. |
| `getTableHeader()` styling | `ui/LeaderboardPanel.java` (`styleTable`) | Gives the column titles the `Theme` colors and stops columns from being dragged into another order. |
| `setPreferredSize(new Dimension(w, h))` | `ui/KeyboardHeatmapPanel.java` (`createKey`) | Gives every heatmap key the same square size, and the space bar a wide size. |
| `setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR))` | `ui/Theme.java`, `ui/MenuPanel.java` | Shows a "hand" mouse cursor over buttons. |
| `setToolTipText` | `ui/KeyboardHeatmapPanel.java` (`colorKey`) | Small text when the mouse rests on a heatmap key ("'e' salah 4 kali"). |
| `FileWriter(file, true)` (append mode) | `test/SelfTest.java` only | Adds broken lines to the end of a test CSV file, to check that they are skipped. The app itself always rewrites the whole file. |

---

## 8. Small design decisions (the code comments are short, the reasons are here)

- **Look-and-feel is not changed** (`app/Main.java`). The Windows look-and-feel draws buttons its own way and ignores `setBackground`, so our blue `Theme` buttons would turn gray. We keep Java's default ("Metal").
- **Plain spaces in the target HTML, never `&nbsp;`** (`ui/GamePanel.java`, `buildColoredHtml`). `JEditorPane` may only wrap a line at a normal space. With `&nbsp;` the 30 words would become one long unbreakable line. The words are only `a`–`z`, so no character needs escaping.
- **One `GameTimer` object per round.** A `Thread` can only be `start()`ed once, so `startGame` always creates a new one.
- **The timer keeps ticking while the "Keluar ke Menu" dialog is open.** That is fine: a round only ends by typing, never by time.
- **Scores are saved directly on the EDT, without an extra thread** (`MainFrame.showResult`). The CSV file is only a few lines, so writing it is instant and the screen never freezes.
- **`ScoreEntry` has getters but no setters:** a score that was recorded should never change.
- **`Player.validate` is `static`:** it only uses its parameter, not the object's fields.
- **Checked custom exceptions** (`extends Exception`): the compiler forces every caller to handle a wrong name or a missing word file, so nobody can forget it.
- **`FRAME_WIDTH` / `FRAME_HEIGHT`, not `WIDTH` / `HEIGHT`:** `JFrame` already has constants with those names.
- **How `SelfTest` knows if `-ea` is on:** `assert assertionsOn = true;` is an assignment, not a comparison. It only runs when assertions are enabled, so afterwards `assertionsOn` tells us whether `-ea` was used.
