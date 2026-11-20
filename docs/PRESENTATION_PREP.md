# Presentation Prep

Each person explains their own code. Below: what you own, what to show, a ~2-minute outline, and questions the
lecturer might ask with short, honest answers. Line numbers match the current code; `CODE_GUIDE.md` explains
every class in more detail.

**Demo order for the whole group (≈1 minute, before the three talks):** open the jar → type a name → play one
Normal round → show the heatmap → go back, pick the most-missed letter → play it (bold letters) → result →
Leaderboard filtered on that letter.

---

## Person A — game rules, statistics, timer, game screen

**Owns:** `mode/GameMode.java`, `mode/ClassicMode.java`, `mode/LetterFocusMode.java`, `model/TypingStats.java`,
`engine/GameTimer.java`, `engine/TimerListener.java`, `ui/GamePanel.java`.
**Topics:** Inheritance, Abstraction, runtime Polymorphism, Multithreading + EDT.

**Show on screen:**
1. The class chain: `src/mode/GameMode.java:17` (abstract) → `src/mode/ClassicMode.java:16` → `src/mode/LetterFocusMode.java:18`.
2. `src/ui/GamePanel.java:41` `private GameMode mode;` and `src/ui/GamePanel.java:297` (calls `mode.getFocusLetter()`).
3. `src/engine/GameTimer.java:39` (the loop) and `src/ui/GamePanel.java:325` (`invokeLater`).

**Talk outline (~2 min):**
- `GameMode` is abstract: it lists what every mode must do (`getCurrentTarget`, `submit`, `isOver`, `calculateScore`).
- `ClassicMode` = Normal: 30 random words, score = WPM × accuracy / 100 × 10.
- `LetterFocusMode extends ClassicMode`: only the word choice, name and focus letter differ; typing, ending and score are inherited. Multilevel inheritance.
- `GamePanel` holds a `GameMode` variable → runtime polymorphism; the focus letter is found by an overridden method, not `instanceof`.
- `TypingStats`: private counters, changed only by `recordKeystroke`; accuracy and WPM formulas; the mistake `HashMap`.
- `GameTimer extends Thread`: ticks every 100 ms; the UI is only changed on the EDT through `invokeLater`; stopped with a `volatile` flag + `interrupt()`.

**Possible questions:**
1. **Why is `GameMode` an abstract class and not an interface?** Because the modes share real data and code: the fields `name`, `wordBank`, `stats`, the constructor, and normal methods like `getName()` and `getFocusLetter()`. An interface can't hold fields or a constructor (and we don't use default methods).
2. **Where is runtime polymorphism?** `GamePanel` has `private GameMode mode;` (line 41). When it calls `mode.getFocusLetter()` or `mode.calculateScore()`, Java runs the version of the real object (`ClassicMode` or `LetterFocusMode`). `MainFrame.createMode` returns `GameMode` but creates a child object (upcasting).
3. **Why is `LetterFocusMode` a subclass of `ClassicMode`?** It *is* a Classic round: same 30 words, same ending, same score. Only the choice of words differs. By extending Classic it reuses all of that and overrides only what is different.
4. **What happens if two threads change a `JLabel`?** Swing is not thread-safe. Two threads changing a component at the same time can give a wrong picture or random exceptions that are very hard to reproduce. That's why the timer never touches a label; it uses `SwingUtilities.invokeLater` so only the EDT changes the UI.
5. **Why `volatile`?** `running` is written by the EDT (`stopTimer`) and read by the timer thread. Without `volatile`, a thread may keep a cached old value and never see `false`. `volatile` makes every read see the latest value.
6. **How does the timer stop?** `stopTimer()` sets `running = false` and calls `interrupt()`. `sleep` throws `InterruptedException`, we `break` out of the loop, `run()` ends, and the thread is finished. We stop it on finish, on quit, and before every new round.
7. **Why a new `GameTimer` every round?** A `Thread` object can only be started once; calling `start()` again throws an exception.
8. **Why does `getMistakes()` return a copy?** Encapsulation. If we returned the real map, other classes could `put` or `clear` it and break our statistics. With a copy, the data only changes through `recordKeystroke`.
9. **How is WPM calculated?** Correct keystrokes ÷ 5 = "words", divided by elapsed minutes. Under 1 second it is 0, so it doesn't jump to a huge number at the start.
10. **Is Backspace counted as a mistake?** No. A keystroke is only counted when the text gets longer. Backspace makes it shorter, so nothing is recorded; but a wrong letter that was typed stays counted.

---

## Person B — files, word list, result screen, heatmap

**Owns:** `data/WordBank.java`, `data/LeaderboardStore.java`, `data/DataStore.java`, `data/WordFileException.java`,
`data/InvalidUsernameException.java`, `resources/words_en.txt`, `ui/ResultPanel.java`, `ui/KeyboardHeatmapPanel.java`.
**Topics:** Generics, Collections (Map), Java IO, Exceptions & Assertions.

**Show on screen:**
1. `src/data/WordBank.java:118` `randomWordsWithFocus` (the 4 steps) and `src/data/WordBank.java:108` (`indexOf`).
2. `src/data/LeaderboardStore.java:28` (`implements DataStore<ScoreEntry>`) and `src/data/LeaderboardStore.java:70` (try-with-resources).
3. `src/ui/KeyboardHeatmapPanel.java:130` (the `Map.Entry` loop) and the color formula at line 190.

**Talk outline (~2 min):**
- The word list (2626 words) is inside the jar; it is read with `getResourceAsStream` + `BufferedReader`. Every letter is in at least 57 words, so even Q, X, Z work.
- Letter focus: `wordsContaining` (loop + `indexOf`), then 24 focus words + 6 normal words, `Collections.shuffle`, join with `StringBuilder`.
- Scores are saved in a CSV file in the user's home folder; `BufferedReader` / `BufferedWriter`, try-with-resources.
- `DataStore<T>` is our own generic interface; `LeaderboardStore` fills in `T = ScoreEntry`.
- Errors: custom checked exceptions; a broken CSV line is skipped, an unreadable file never overwrites old scores. Assertions check internal assumptions.
- Heatmap: `HashMap<Character, Integer>` from `TypingStats` → each key's color is mixed between white and dark red.

**Possible questions:**
1. **Why is the leaderboard outside the jar?** A jar is like a read-only zip; a program cannot write into its own jar. The home folder (`System.getProperty("user.home")`) is always writable, so the file is `<home>\.ketikin\leaderboard.csv`.
2. **Why checked exceptions (`extends Exception`)?** So the compiler forces the caller to handle them. Nobody can forget that the word file can be missing or that a name can be wrong.
3. **What is `T` in `DataStore<T>`?** A type parameter: a placeholder for "the type of data". `LeaderboardStore implements DataStore<ScoreEntry>`, so there `loadAll()` returns `List<ScoreEntry>`.
4. **Why try-with-resources?** The reader/writer is closed automatically when the block ends, even if an exception happens. No `finally` with `close()` needed.
5. **What happens with a broken line in the CSV?** `fromCsvLine` throws `IllegalArgumentException`; we catch it *per line*, print a warning with `System.err`, and keep reading the other lines.
6. **What if the whole file can't be read?** `loadAll` returns an empty list and stores an error message; the leaderboard shows a warning dialog. `addScore` then refuses to save, so old scores are not overwritten.
7. **Why `getResourceAsStream` and not `FileReader` for the words?** A file inside the jar is not a normal file on disk. `getResourceAsStream` opens it from the jar; `InputStreamReader` turns the bytes into characters for `BufferedReader`.
8. **How does the 80 % focus work?** `randomWordsWithFocus(letter, 30, 24)`: 24 random words from `wordsContaining(letter)`, 6 normal random words, shuffle, join. The 6 normal words may also contain the letter by chance.
9. **Assertion vs exception?** An assertion checks something that should *never* be false if our code is correct (e.g. accuracy between 0 and 100). Wrong user input (a bad name) is handled with an exception, because it can really happen. Assertions only run with `java -ea`.
10. **How is the heatmap color calculated?** `ratio = count ÷ maxCount` (0 to 1). For red, green and blue: `start + (end − start) × ratio`, from white (255, 255, 255) to dark red (170, 20, 40).

---

## Person C — window, menu, player name, scores table

**Owns:** `model/Player.java`, `model/ScoreEntry.java`, `ui/MainFrame.java`, `ui/MenuPanel.java`,
`ui/LeaderboardPanel.java`, `ui/Theme.java`, `app/Main.java`.
**Topics:** Encapsulation, String (validation), GUI / CardLayout / events, JTable + Collections, compile-time Polymorphism.

**Show on screen:**
1. `src/model/Player.java:43` `validate` (the loop at line 71) and the setter at line 35.
2. `src/ui/MenuPanel.java:132` `createLetterGrid` (27 toggle buttons, one `ButtonGroup`).
3. `src/ui/LeaderboardPanel.java:156` `refresh` and `src/model/ScoreEntry.java:63` `compareTo`.

**Talk outline (~2 min):**
- `Main` builds the window on the EDT; `MainFrame extends JFrame` holds 4 screens in a `CardLayout`.
- Menu: name field, letter picker (`JToggleButton` + `ButtonGroup` in a `GridLayout`), buttons with `addActionListener(e -> { ... })`.
- `Player`: private name, setter that validates with a loop + `Character.isLetterOrDigit`, no regex; throws `InvalidUsernameException`, shown in a `JOptionPane`.
- `ScoreEntry`: two constructors (overloading), `toCsvLine` / `fromCsvLine` with `String.format` and `split`, `Comparable` for sorting.
- Leaderboard: one `JTable` from a `DefaultTableModel`, filled with a loop from a `List`; a `JComboBox` filter reloads it.

**Possible questions:**
1. **What does `compareTo` return?** A negative number if `this` should come before `other`, positive if after, 0 if equal. We return -1 when our score is higher, so the highest score comes first; equal scores are ordered by WPM.
2. **Where is encapsulation in `Player`?** `name` is private and only set through `setName`, which calls `validate` first. Even the constructor uses `setName`, so a `Player` object can never hold a wrong name.
3. **Why no regex for the name?** Regex was not in the course. A loop with `charAt` and `Character.isLetterOrDigit` is easy to read and explain, like the "Username Validator" practicum.
4. **Where is compile-time polymorphism?** Two `ScoreEntry` constructors (all values vs. from `TypingStats`), `showLeaderboard()` / `showLeaderboard(String)` in `MainFrame`, and `getWpm()` / `getWpm(long)` in `TypingStats`. The compiler picks the version by the arguments.
5. **How does the screen change?** All 4 panels are added to one panel with a `CardLayout`; `cardLayout.show(cardPanel, "GAME")` shows one of them.
6. **Why `SwingUtilities.invokeLater` in `Main`?** `main` runs on the "main" thread, but Swing components must be created on the EDT. `invokeLater` hands the work to the EDT.
7. **How does the filter work?** The combo box's lambda calls `refresh()`, which clears the table (`setRowCount(0)`), calls `store.getTop(focus, 10)` and adds the rows in a `for` loop. `getTop` keeps a row if the filter is "Semua" or the focus matches.
8. **Why is the table not editable?** We override `isCellEditable` to return `false` in an anonymous subclass of `DefaultTableModel`; otherwise a double-click would let you change scores.
9. **Why `Locale.US` in `toCsvLine`?** On a computer with Indonesian settings, `String.format` writes `64,30` with a comma, and the comma would break the CSV columns. `Locale.US` always writes `64.30`.
10. **Why `ButtonGroup`?** So only one letter can be selected at a time; selecting one automatically unselects the others.

---

## Honest limits (if asked "what doesn't it do?")

- The clock keeps running while the "Keluar?" dialog is open (a quit round is never saved anyway).
- Words can repeat within one round; the focus letter is suggested by the heatmap, but the player picks it by hand.
- English words only. The full list of cut/simplified features is in `CUT_FEATURES.md`.
