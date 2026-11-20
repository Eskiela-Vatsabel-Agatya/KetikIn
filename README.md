# KetikIn

A typing trainer game written in Java Swing. Final project for Pemrograman Berorientasi Objek (PBO), class B, 2026.

Modes: Classic, Time Attack, Survival and Code Mode, with live WPM and accuracy, a mistake heatmap and a leaderboard.

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

## Project layout

```
src/app       Main (entry point)
src/model     Player, TypingStats, ScoreEntry
src/mode      GameMode and the four modes
src/data      word bank, leaderboard file, custom exceptions
src/engine    timer thread
src/ui        Swing screens and theme
src/resources word list and code snippets (packed into the jar)
test          SelfTest (plain Java, no JUnit)
docs          notes and guides
```
