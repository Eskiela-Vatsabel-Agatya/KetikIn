# KetikIn

A typing trainer game written in Java Swing. Final project for Pemrograman Berorientasi Objek (PBO), class B, 2026.

KetikIn helps you train the letters you are weak at. Pick **Normal** (30 random words) or a **focus letter A–Z**:
then 24 of the 30 words contain that letter, and it is shown in bold. You get live WPM and accuracy, green/red
coloring, a mistake heatmap on a keyboard, and a leaderboard you can filter by focus.

> Work in progress. This README is completed in Phase 7.

## Requirements

- A JDK with `javac` that supports `--release 8` (JDK 9 or newer) to build.
- Java 8 or newer to run.

## Build and run (Windows)

From the project folder:

```bat
build.bat
run.bat
```

`build.bat` compiles everything into `out\` and creates `dist\KetikIn.jar`.
`run.bat` starts the game with assertions enabled (`java -ea -jar dist\KetikIn.jar`).
You can also double-click `dist\KetikIn.jar`.

`selftest.bat` builds and then runs `test\SelfTest.java`, which checks the non-GUI classes and prints
`PASS`/`FAIL` for each check (it should end with `0 FAIL`).

## Project layout

```
src/app       Main (entry point)
src/model     Player, TypingStats, ScoreEntry
src/mode      GameMode, ClassicMode (Normal), LetterFocusMode
src/data      word bank, leaderboard file, custom exceptions
src/engine    timer thread
src/ui        Swing screens and theme
src/resources word list (packed into the jar)
test          SelfTest (plain Java, no JUnit)
docs          notes and guides
```
