# Code Guide

_The full guide is written in Phase 7. For now this file only tracks the section below._

## Things outside the course material

Java features we use that do **not** appear in the lecture slides (see `COURSE_STYLE_NOTES.md`).
Each is used because there is no simpler way, and each is commented in the code.

| Feature | Where | Why we need it |
|---|---|---|
| `SwingUtilities.invokeLater` | `app/Main.java`, `ui/GamePanel.java` (Phase 4) | Swing is not thread-safe: only the Event Dispatch Thread (EDT) may create or change components. `invokeLater` hands a `Runnable` to the EDT. |
| `CardLayout` | `ui/MainFrame.java` | Shows one screen at a time inside a single window. The slides and the practicum "Layout Manager" page only show `FlowLayout`, `BorderLayout` and `GridLayout`. |
| `JTabbedPane` | `ui/LeaderboardPanel.java` (Phase 5) | One leaderboard tab per mode. Not shown in the slides or the practicum. |
| `KeyListener` (`keyReleased`) | `ui/GamePanel.java` (Phase 3) | Reads the input after every key. The practicum only names `DocumentListener`; we use `KeyListener` because a `DocumentListener` may not change its own text field (we clear the field after Space/Enter). |
| HTML text (`<span style='color:...'>`) | `ui/GamePanel.java` (`buildColoredHtml`), `ui/MenuPanel.java` (short `<html>` labels) | Lets us color and underline parts of the target text. We only build it with a plain loop + `StringBuilder`. Two lessons from testing: (1) a space written as `&nbsp;` can never be a line break, so a paragraph of only `&nbsp;` spaces gets cut mid-word; that is why only *repeated* spaces use `&nbsp;` (Phase 3). (2) One tag **per character** makes Java 9+ on a scaled Windows display measure the line too short, so line 1 runs out of the box; that is why characters with the same color share **one** `<span>` (fix after Phase 4). |
| `JEditorPane` (`"text/html"`, not editable, not focusable) | `ui/GamePanel.java` (`createTargetPanel`) | Shows the colored target text. Before, a `JLabel` with `<body style='width:660px'>` was used, but that "px" width means a different screen size on different Java versions and Windows display scaling, so the text was clipped on Java 25. A `JEditorPane` wraps lines at its **real** width, so no guessed number is needed. `HONOR_DISPLAY_PROPERTIES` makes it use our `Theme` font and color. Verified on Java 8 and Java 25 (125 % display scaling). |
| `volatile` | `engine/GameTimer.java` | Makes sure the timer thread sees the `running = false` written by the UI thread. |
| `interrupt()` | `engine/GameTimer.java` (`stopTimer`) | Wakes the timer thread out of `Thread.sleep` right away (the slides only say that `sleep` "can throw `InterruptedException`"). |
| `Thread.currentThread()` | `ui/GamePanel.java` (`onTick`) | Tells us *which* timer thread sent a tick, so a late tick from an old, already stopped timer is ignored. |
| `instanceof` + cast | `ui/GamePanel.java` (`updateTimeLabel`, `handleTick`) | Checks whether the current mode implements `Timed`, then treats it as a `Timed` to call `getTimeLeftMillis` / `onTimeUp`. |
| `setCaretPosition` | `ui/GamePanel.java` (`keyReleased`) | Moves the text cursor back to the end after every key, so arrow keys and Home cannot insert letters in the middle. **Known limit:** a mouse click in the middle of the text can still move the cursor until the next key is released; a letter typed at that exact moment is counted by position at the end. We accept this rare case to keep the code simple. |
| `InputStreamReader` + `getResourceAsStream` | `data/WordBank.java` (Phase 2) | Files packed *inside* the jar are not normal files, so `FileReader` cannot open them. |
| `System.getProperty("user.home")` | `data/LeaderboardStore.java` (constructor) | Gives the user's home folder (e.g. `C:\Users\Budi`). The leaderboard is saved there in `.ketikin\leaderboard.csv`, because a program cannot write into its own jar. |
| `DefaultTableCellRenderer` | `ui/LeaderboardPanel.java` (`styleTable`) | Centers the text in each table cell. We use Swing's ready-made renderer and only change its alignment (3 lines). |
| `getTableHeader()` styling | `ui/LeaderboardPanel.java` (`styleTable`) | Gives the column titles the `Theme` colors and stops columns from being dragged into another order. |
| `FileWriter(file, true)` (append mode) | `test/SelfTest.java` only | Adds broken lines to the end of a test CSV file, to check that they are skipped. The app itself always rewrites the whole file. |
