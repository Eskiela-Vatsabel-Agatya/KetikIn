# Project Summary

Factual input for the paper (makalah) and the slides, which will be written in Indonesian.
Course: Pemrograman Berorientasi Objek (PBO), class B, 2026. Group of 3 (Eskiela Vatsabel Agatya, Satrio Ananda Widiyanto, Hanif Rafa Ramidzar).
For course-topic locations with file and line numbers, see `MATERIALS_MAP.md`.

## 1. Background / problem

- Typing is a daily skill for students and programmers: assignments, reports, chat, and above all writing code.
  Slow or inaccurate typing costs time and breaks concentration.
- Most people do not miss *all* letters equally. They keep missing the **same few letters** (for example
  `j`, `q`, `x`, or letters typed with the weak little finger).
- Typical typing trainers give **random text**. A weak letter shows up only now and then, so you cannot practise it
  on purpose.
- Many typing trainers are **online-only** (need internet) and often full of ads.

## 2. Solution

**KetikIn** is an offline desktop typing trainer (Java Swing). The idea in three steps:

1. **Type normally** (Normal mode: 30 random words). Live green/red coloring, WPM and accuracy.
2. **See your weak letters**: the result screen shows a keyboard **heatmap** (the more mistakes on a letter, the redder
   the key) and the 3 most-missed letters.
3. **Practise one letter**: pick that letter (A–Z) on the menu. Now 24 of the 30 words (80 %) contain it, and it is
   shown in **bold** in the text, so you know where to concentrate.

Scores are saved on the computer and shown in a leaderboard that can be filtered by focus.

## 3. Main features

**Normal typing round.** The player types 30 random English words (from a list of 2626 words). A timer counts up.
WPM (correct characters ÷ 5 per minute) and accuracy (correct ÷ all keystrokes) update live. Backspace is allowed;
paste is blocked. Score = WPM × accuracy / 100 × 10.

**Letter focus A–Z.** On the menu, a grid of 27 buttons ("Normal" + A–Z). With a letter selected, 24 of the 30 words
contain that letter anywhere in the word (for G: "garden", "big", "bloggers"); the other 6 are normal words, and the
order is shuffled. Every letter a–z appears in at least 57 words of the list, so even Q, X and Z work well.

**Live coloring.** Already typed and correct letters turn green, wrong letters turn red and underlined, the current
position is underlined, the rest is gray. The focus letter is bold everywhere in the text.

**Result screen with mistake heatmap.** Shows score, WPM, accuracy, time, correct and wrong keystrokes, and a QWERTY
keyboard where each key is colored from white (no mistakes) to dark red (most mistakes), plus the 3 most-missed
letters. Buttons: "Main lagi" (new round, same focus), "Menu", "Leaderboard". A message "Rekor pribadi baru!" appears
when the player beats their own best score for that focus.

**Leaderboard with filter.** One table (Peringkat, Nama, Fokus, Skor, WPM, Akurasi, Tanggal) with the top 10, and a
drop-down filter: "Semua", "Normal", or one letter A–Z. Scores are stored in a CSV file in the user's home folder
(`.ketikin\leaderboard.csv`).

**Player name check.** 3–15 characters, starts with a letter, only letters/digits/`_`, not "admin", "null" or
"guest". A clear message in a dialog says which rule failed.

## 4. Technology

- **Java**, compiled for **Java 8** (`javac --release 8`), so the jar runs on Java 8 and every newer version.
  Tested on Java 8 and Java 25.
- **Swing** for the GUI (standard components only, no custom painting).
- **Plain files:** the word list is a text file packed inside the jar; scores are a CSV file. No database.
- **No external libraries** (JDK only; even the tests are a plain Java program, `SelfTest`, with 62 checks).
- One extra thread (`GameTimer`) for the live clock and WPM.
- Structure: 21 classes/interfaces in 6 packages (`app`, `ui`, `mode`, `model`, `data`, `engine`).
  The logic packages never import Swing, so they can be tested without a window.

## 5. Challenges we met

- **Threads and the Event Dispatch Thread.** The timer thread may not touch Swing components directly, so every
  screen update goes through `SwingUtilities.invokeLater`. We also had to ignore "late" ticks from an old timer
  (after quitting or restarting quickly), stop the thread safely with a `volatile` flag + `interrupt()`, and start a
  new thread every round (a thread can only be started once). Testing found a bug where a timed round could end
  behind the "Quit?" dialog; it was fixed (that code later disappeared with the removed timed modes).
- **Live coloring of the text.** We build HTML text after every key. Two problems came up: (1) on Java 9+ with Windows
  display scaling, one HTML tag per letter made Swing measure lines wrongly and cut off text, so letters with the same
  color now share one tag; (2) a fixed width in a `JLabel` was clipped on Java 25, so we switched to a `JEditorPane`
  that wraps at its real width. Checked on Java 8 and Java 25.
- **Files inside the jar vs. normal files.** The word list is inside the jar and must be read with
  `getResourceAsStream`; the leaderboard cannot be written into the jar, so it lives in the user's home folder.
- **Decimal commas.** On Indonesian settings, numbers are written as `64,30`, which breaks a CSV file. We force
  `Locale.US` when writing.
- **Robust file handling.** A broken CSV line is skipped; an unreadable file is never overwritten, so old scores
  are never lost.
- **The pivot.** The first version had four modes (Classic, Time Attack, Survival, Code Mode). Before the final phase
  we removed three of them to focus on one clear problem: practising weak letters. Removing code cleanly (and
  keeping the four OOP pillars strong) was part of the work; `LetterFocusMode extends ClassicMode` now shows
  multilevel inheritance.
- **Enough words for rare letters.** With only ~400 words, letters like J, Q, X, Z appeared in very few words. We
  rewrote the list to 2626 common words and added a self-test that checks every letter appears in at least 40 words
  (the rarest, Q, is in 57).
- **Keeping it simple.** Features were cut or simplified when they could not be written simply (see
  `CUT_FEATURES.md`), e.g. the clock does not pause during the quit dialog, and words may repeat in a round.

## 6. Opportunities (future work)

- **Suggest the focus letter automatically** from the heatmap of the last round (or of all rounds).
- **Letter pairs**, e.g. "th", "ng", "ck", which are often harder than single letters.
- **Indonesian word list** (and a language choice).
- **Online leaderboard** so players on different computers can compare.
- Track progress per letter over time (a small chart of accuracy for one letter).

## 7. Screenshots to take (for the paper and slides)

Run `run.bat` (or double-click the jar), window size is fixed at 1000×650.

1. **Menu** right after start ("Normal" selected, name filled in).
2. **Menu with a letter selected** (e.g. J highlighted, "Fokus: huruf J").
3. **Name error dialog** (e.g. name "ab" → "Nama minimal 3 karakter.").
4. **Game screen, Normal mode, halfway** (green, red and gray letters visible, some mistakes).
5. **Game screen, focus J, halfway** (bold j's visible).
6. **Result screen** with a colorful heatmap and "Rekor pribadi baru!".
7. **Leaderboard, filter "Semua"** with several rows.
8. **Leaderboard, filter on one letter** (e.g. "J").
9. **Quit dialog** "Keluar dari permainan?".
10. **SelfTest output** in the terminal ending with "62 PASS, 0 FAIL".
11. *(Optional, for the code part)* the class chain `GameMode` → `ClassicMode` → `LetterFocusMode` in the editor,
    and `GameTimer.run()` next to `GamePanel.onTick()`.
