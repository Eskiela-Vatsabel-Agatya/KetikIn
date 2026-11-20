# Code Guide

_The full guide is written in Phase 7. For now this file tracks the pivot summary and the section below._

## What KetikIn is now (pivot, Phase 6B)

KetikIn trains the letters a player is weak at. There is one typing mode with two variants:

- **Normal** (`ClassicMode`): 30 random words, the timer counts up, live WPM/accuracy and green/red coloring.
- **Letter focus** (`LetterFocusMode extends ClassicMode`): the player picks a letter A–Z on the menu.
  24 of the 30 words (80 %) contain that letter somewhere in the word; it is shown **bold** in the target text.

Class chain: `GameMode` (abstract) ← `ClassicMode` ← `LetterFocusMode` (multi-level inheritance).
`LetterFocusMode` only changes how the words are chosen (`WordBank.randomWordsWithFocus`), its name and
description, and overrides `getFocusLetter()` / `getFocusLabel()`. Typing, ending and scoring are inherited
from `ClassicMode`. `GamePanel` calls `mode.getFocusLetter()` on its `GameMode` variable, so it needs no `instanceof`.

The leaderboard CSV keeps its 6 columns; the `mode` column now stores `Normal` or the letter (e.g. `J`).
`LeaderboardStore.getTop(focus, 10)` filters with one `if` ("Semua" keeps every row). Old rows from removed
modes (`Time Attack`, `Survival`, `Code`, `Classic`) are kept in the file and only show under "Semua".

## Things outside the course material

Java features we use that do **not** appear in the lecture slides (see `COURSE_STYLE_NOTES.md`).
Each is used because there is no simpler way, and each is commented in the code.

| Feature | Where | Why we need it |
|---|---|---|
| `SwingUtilities.invokeLater` | `app/Main.java`, `ui/GamePanel.java` (Phase 4) | Swing is not thread-safe: only the Event Dispatch Thread (EDT) may create or change components. `invokeLater` hands a `Runnable` to the EDT. |
| `CardLayout` | `ui/MainFrame.java` | Shows one screen at a time inside a single window. The slides and the practicum "Layout Manager" page only show `FlowLayout`, `BorderLayout` and `GridLayout`. |
| `JComboBox<String>` | `ui/LeaderboardPanel.java` (`createFilterPanel`) | The leaderboard filter ("Semua", "Normal", "A" … "Z"). Its `addActionListener(e -> { ... })` is the same style as our buttons. `setSelectedItem` also fires that listener, so choosing a filter from code reloads the table too. |
| `DefaultTableModel.setRowCount(0)` | `ui/LeaderboardPanel.java` (`refresh`) | Removes all old rows before the table is filled again for the new filter. |
| `JToggleButton` + `ButtonGroup` | `ui/MenuPanel.java` (`createLetterGrid`) | The letter picker: a button that stays pressed, and a group in which only one button can be pressed at a time. |
| `setContentAreaFilled(false)` + `setOpaque(true)` | `ui/MenuPanel.java` (`createLetterButton`) | Java's default look paints a selected toggle button in its own gray. These two lines make it use our `setBackground` color (accent = selected). |
| `KeyListener` (`keyReleased`) | `ui/GamePanel.java` (Phase 3) | Reads the input after every key. The practicum only names `DocumentListener`; we use `KeyListener` because a `DocumentListener` may not change its own text field (we cut off letters typed past the end of the target). |
| HTML text (`<span style='color:...'>`) | `ui/GamePanel.java` (`buildColoredHtml`) | Lets us color, underline and bold parts of the target text. We only build it with a plain loop + `StringBuilder`. The text is only `a`–`z` and single spaces, so nothing needs escaping, and a normal space is where a line may wrap. Lesson from testing: one tag **per character** makes Java 9+ on a scaled Windows display measure the line too short, so line 1 runs out of the box; that is why characters with the same style share **one** `<span>` (fix after Phase 4). The bold focus letter adds `font-weight:bold` to the style; checked on Java 8 and Java 25 (125 % scaling). |
| `JEditorPane` (`"text/html"`, not editable, not focusable) | `ui/GamePanel.java` (`createTargetPanel`) | Shows the colored target text. Before, a `JLabel` with `<body style='width:660px'>` was used, but that "px" width means a different screen size on different Java versions and Windows display scaling, so the text was clipped on Java 25. A `JEditorPane` wraps lines at its **real** width, so no guessed number is needed. `HONOR_DISPLAY_PROPERTIES` makes it use our `Theme` font and color. Verified on Java 8 and Java 25 (125 % display scaling). |
| `volatile` | `engine/GameTimer.java` | Makes sure the timer thread sees the `running = false` written by the UI thread. |
| `interrupt()` | `engine/GameTimer.java` (`stopTimer`) | Wakes the timer thread out of `Thread.sleep` right away (the slides only say that `sleep` "can throw `InterruptedException`"). |
| `Thread.currentThread()` | `ui/GamePanel.java` (`onTick`) | Tells us *which* timer thread sent a tick, so a late tick from an old, already stopped timer is ignored. |
| `setCaretPosition` | `ui/GamePanel.java` (`keyReleased`) | Moves the text cursor back to the end after every key, so arrow keys and Home cannot insert letters in the middle. **Known limit:** a mouse click in the middle of the text can still move the cursor until the next key is released; a letter typed at that exact moment is counted by position at the end. We accept this rare case to keep the code simple. |
| `InputStreamReader` + `getResourceAsStream` | `data/WordBank.java` (Phase 2) | Files packed *inside* the jar are not normal files, so `FileReader` cannot open them. |
| `System.getProperty("user.home")` | `data/LeaderboardStore.java` (constructor) | Gives the user's home folder (e.g. `C:\Users\Budi`). The leaderboard is saved there in `.ketikin\leaderboard.csv`, because a program cannot write into its own jar. |
| `DefaultTableCellRenderer` | `ui/LeaderboardPanel.java` (`styleTable`) | Centers the text in each table cell. We use Swing's ready-made renderer and only change its alignment (3 lines). |
| `getTableHeader()` styling | `ui/LeaderboardPanel.java` (`styleTable`) | Gives the column titles the `Theme` colors and stops columns from being dragged into another order. |
| `setPreferredSize(new Dimension(w, h))` | `ui/KeyboardHeatmapPanel.java` (`createKey`) | Gives every heatmap key the same square size, and the space bar a wide size. |
| `setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR))` | `ui/Theme.java` (`styleButton`) | Shows a "hand" mouse cursor over buttons. |
| `setToolTipText` | `ui/KeyboardHeatmapPanel.java` (`colorKey`) | Small text that appears when the mouse rests on a heatmap key ("'e' salah 4 kali"). |
| `FileWriter(file, true)` (append mode) | `test/SelfTest.java` only | Adds broken lines to the end of a test CSV file, to check that they are skipped. The app itself always rewrites the whole file. |
