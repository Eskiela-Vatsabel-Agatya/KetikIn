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
| `volatile` | `engine/GameTimer.java` | Makes sure the timer thread sees the `running = false` written by the UI thread. |
| `InputStreamReader` + `getResourceAsStream` | `data/WordBank.java` (Phase 2) | Files packed *inside* the jar are not normal files, so `FileReader` cannot open them. |
