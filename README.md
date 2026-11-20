# KetikIn

A typing trainer that helps you practise the letters you keep missing. Written in Java Swing as the final project
for Pemrograman Berorientasi Objek (PBO), class B, 2026.

Repository: _(add the GitHub link here)_

## Features

- **Normal mode:** type 30 random English words. A timer counts up; WPM and accuracy update live.
- **Letter focus (A–Z):** pick a letter on the menu. 24 of the 30 words contain that letter, and it is shown in **bold**.
- **Live coloring:** correct letters turn green, wrong letters red and underlined, the current position is underlined.
- **Mistake heatmap:** after each round, a keyboard shows which letters you missed (white → dark red) and the top 3.
- **Leaderboard:** top 10 scores, filterable by "Semua", "Normal" or a letter. Saved in `%USERPROFILE%\.ketikin\leaderboard.csv`.
- **Name check:** 3–15 characters, starts with a letter, only letters/digits/`_`.
- Works offline. No external libraries, no database.

## Requirements

- **To run:** Java 8 or newer.
- **To build:** a JDK whose `javac` supports `--release 8` (JDK 9 or newer).

## Build and run (Windows)

From the project folder:

```bat
build.bat
run.bat
```

- `build.bat` compiles everything into `out\` and creates `dist\KetikIn.jar`.
- `run.bat` starts the game with assertions enabled (`java -ea -jar dist\KetikIn.jar`).
- You can also **double-click `dist\KetikIn.jar`**. The jar works from any folder (the word list is inside it).

## Self-test

```bat
selftest.bat
```

Builds the project and runs `test\SelfTest.java`, which checks the non-GUI classes (name rules, statistics,
CSV lines, word list, both modes, leaderboard file) and prints `PASS`/`FAIL` for each check. It should end with
`62 PASS, 0 FAIL`. It uses a temporary file, never your real leaderboard.

## Folder structure

```
src/app        Main (entry point)
src/ui         Swing screens: MainFrame, MenuPanel, GamePanel, ResultPanel,
               LeaderboardPanel, KeyboardHeatmapPanel, Theme
src/mode       GameMode (abstract), ClassicMode (Normal), LetterFocusMode
src/model      Player, TypingStats, ScoreEntry
src/data       WordBank, LeaderboardStore, DataStore<T>, custom exceptions
src/engine     GameTimer (thread), TimerListener
src/resources  words_en.txt (2626 words, packed into the jar)
test           SelfTest (plain Java, no JUnit)
docs           CODE_GUIDE, MATERIALS_MAP, PRESENTATION_PREP, PROJECT_SUMMARY,
               CUT_FEATURES, TEST_CHECKLIST, COURSE_STYLE_NOTES
build.bat, run.bat, selftest.bat
```

Start reading at `docs/CODE_GUIDE.md`.

## Team

| Person | Owns | Topics |
|---|---|---|
| Person A | `mode/*`, `model/TypingStats`, `engine/*`, `ui/GamePanel` | Inheritance, abstraction, runtime polymorphism, multithreading + EDT |
| Person B | `data/*`, `resources/*`, `ui/ResultPanel`, `ui/KeyboardHeatmapPanel` | Generics, collections, Java IO, exceptions & assertions |
| Person C | `model/Player`, `model/ScoreEntry`, `ui/MainFrame`, `ui/MenuPanel`, `ui/LeaderboardPanel`, `ui/Theme`, `app/Main` | Encapsulation, String validation, GUI/CardLayout/events, JTable + collections, compile-time polymorphism |

_(Replace "Person A/B/C" with real names.)_
