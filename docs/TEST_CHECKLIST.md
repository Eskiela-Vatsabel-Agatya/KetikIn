# Test Checklist

Manual robustness tests from Phase 6. Each item was run on **Java 8** (`run.bat`) and **Java 25**
(the VS Code one, with 125 % Windows display scaling). The tests were driven by a small throw-away
test program that clicks buttons and sends keys to the real app window; screenshots were checked by eye.
`selftest.bat` covers the non-GUI classes separately (72 PASS, 0 FAIL).

| # | Test | Result | Note |
|---|---|---|---|
| 1 | Empty name | ✅ | Dialog "Nama tidak boleh kosong." The game does not start. |
| 2 | Name with a space (`budi sari`) | ✅ | Dialog "Nama hanya boleh berisi huruf, angka, dan garis bawah (_)." |
| 3 | Too long name (17 letters) | ✅ | Dialog "Nama maksimal 15 karakter." |
| 4 | `Admin` | ✅ | Dialog "Nama "Admin" tidak boleh dipakai." (checked ignoring case) |
| 5 | Quit in the middle of every mode, then start another mode right away | ✅ | After each quit: back at the menu, **0** timer threads still alive. While a new round runs: exactly 1. |
| 6 | Quit dialog left open in Time Attack / Survival | ✅ (fixed) | **Bug found:** the timer kept running behind the "Keluar?" dialog, so the round could end and its score was saved while the player was quitting. Fixed with an `askingToQuit` flag in `GamePanel`: ticks are ignored while the dialog is open. The clock itself does not pause, so after "Tidak" an expired Survival word still costs one life. |
| 7 | "Main lagi" ×5 quickly (and twice in a row) | ✅ | Every round starts clean. Never more than 1 timer thread alive. |
| 8 | Type far past the end (Classic, Code) | ✅ | Extra letters are cut off; the round ends exactly at the target length. |
| 9 | Very long "word" without Space (Time Attack) | ✅ | The text field scrolls; every extra letter counts as a mistake. No crash. |
| 10 | Spam Backspace on an empty field | ✅ | Nothing happens; no keystrokes are counted. |
| 11 | Paste (Ctrl+V, right-click) | ✅ | `setTransferHandler(null)` blocks paste. A `JTextField` has no right-click menu by default. |
| 12 | Missing word file (`words_en.txt` removed from a **copy** of `out\`) | ✅ | Error dialog "File kata tidak bisa dimuat ... KetikIn akan ditutup."; after OK no window is left, so the program ends. |
| 13 | Corrupt leaderboard line | ✅ | The line is skipped with a console warning; all other scores still show. |
| 14 | Missing leaderboard file | ✅ | Empty leaderboard, no dialog, no crash. The file is created at the first saved score. |
| 15 | Unreadable leaderboard file (Phase 5 test) | ✅ | Warning dialog; old file is not overwritten; result screen still shows. |
| 16 | Run the jar from a different folder | ✅ | Copied `dist\KetikIn.jar` to another folder and started it from there: words and snippets load from inside the jar. Double-click on this PC uses Java 8 `javaw`. **To do by hand:** copy the jar to the Desktop and double-click it once. |
| 17 | Heatmap matches mistakes | ✅ | Typed every `e` wrong (14×) and two `a` wrong: `E` is deep red, `A` light pink, the rest white. Text: "Paling sering salah: 'e' (14), 'a' (2)". Code Mode symbols show under "Simbol lain". |
| 18 | Nothing cut off at 1000×650 | ✅ | Menu, all four game screens, result (with heatmap) and leaderboard checked on both Java versions. |
