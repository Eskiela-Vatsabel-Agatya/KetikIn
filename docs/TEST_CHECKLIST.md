# Test Checklist

Manual robustness tests. Each item was run on **Java 8** (`run.bat`) and **Java 25** (the VS Code one, with
125 % Windows display scaling). The tests were driven by a small throw-away test program that clicks buttons
and sends keys to the real app window (with a temporary leaderboard folder); screenshots were checked by eye.
`selftest.bat` covers the non-GUI classes separately (62 PASS, 0 FAIL after the pivot).

"Re-run" = checked again after the pivot (Phase 6B). "Phase 6" = checked in Phase 6; the code involved did not change.

## Letter focus and leaderboard filter (Phase 6B)

| # | Test | Result | Note |
|---|---|---|---|
| 1 | App opens with "Normal" selected | ✅ Re-run | "Normal" is accent-colored, the line under the grid says "Mode normal". |
| 2 | Normal round plays like Classic before | ✅ Re-run | 30 words, timer counts up, round ends at the last letter, result screen "Hasil ... - Mode normal". |
| 3 | Pick J, press "Mulai" | ✅ Re-run | Title "Fokus huruf J"; at least 24 of 30 words contain `j`, every `j` is bold. Coloring and line wrapping still correct on both Java versions. |
| 4 | Every letter A–Z | ✅ Re-run | SelfTest builds a round for each letter and checks ≥ 24 words contain it. In the GUI: Q, X and Z rounds start with no error. |
| 5 | "Main lagi" keeps the focus | ✅ Re-run | After a Z round, "Main lagi" starts "Fokus huruf Z"; after a Normal round, "Mode normal". |
| 6 | Leaderboard filter | ✅ Re-run | From the result screen the filter opens on the played focus (e.g. "J"). "Semua" shows all rows, "Normal" only Normal rows, a letter only that letter. |
| 7 | Old CSV rows (`Time Attack`, `Classic`, `Code`) | ✅ Re-run | No crash. They only show under "Semua". |
| 8 | "Rekor pribadi baru!" per focus | ✅ Re-run | First J round and first Normal round each show it (compared only within the same focus). SelfTest checks `getPersonalBest` per focus. |
| 9 | Heatmap after the cleanup | ✅ Re-run | Wrong letters color their keys; "Paling sering salah: 'j' (4), 'a' (3), 'e' (3)". With no mistakes: "Tidak ada kesalahan. Mantap!". |
| 10 | Quit in the middle, then start another round | ✅ Re-run | After each quit: back at the menu, **0** timer threads alive. During a round: exactly 1. |
| 11 | Nothing cut off at 1000×650 | ✅ Re-run | Menu (letter grid), game, result and leaderboard checked on both Java versions. |

## General (Phase 6)

| # | Test | Result | Note |
|---|---|---|---|
| 12 | Empty name | ✅ Phase 6 | Dialog "Nama tidak boleh kosong." The game does not start. |
| 13 | Name with a space (`budi sari`) | ✅ Phase 6 | Dialog "Nama hanya boleh berisi huruf, angka, dan garis bawah (_)." |
| 14 | Too long name (17 letters) | ✅ Phase 6 | Dialog "Nama maksimal 15 karakter." |
| 15 | `Admin` | ✅ Phase 6 | Dialog "Nama "Admin" tidak boleh dipakai." (checked ignoring case) |
| 16 | "Main lagi" ×5 quickly | ✅ Phase 6 | Every round starts clean. Never more than 1 timer thread alive. |
| 17 | Type far past the end | ✅ Phase 6 | Extra letters are cut off; the round ends exactly at the target length. |
| 18 | Spam Backspace on an empty field | ✅ Phase 6 | Nothing happens; no keystrokes are counted. |
| 19 | Paste (Ctrl+V, right-click) | ✅ Phase 6 | `setTransferHandler(null)` blocks paste. A `JTextField` has no right-click menu by default. |
| 20 | Missing word file (`words_en.txt` removed from a **copy** of `out\`) | ✅ Phase 6 | Error dialog "File kata tidak bisa dimuat ... KetikIn akan ditutup."; after OK the program ends. |
| 21 | Corrupt leaderboard line | ✅ Phase 6 | The line is skipped with a console warning; all other scores still show. (Also in SelfTest.) |
| 22 | Missing leaderboard file | ✅ Phase 6 | Empty leaderboard, no dialog, no crash. The file is created at the first saved score. |
| 23 | Unreadable leaderboard file | ✅ Phase 6 | Warning dialog; old file is not overwritten; result screen still shows. (Also in SelfTest.) |
| 24 | Run the jar from a different folder | ✅ Phase 6 | Words load from inside the jar. **To do by hand:** copy the jar to the Desktop and double-click it once. |
