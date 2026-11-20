# Cut and Simplified Features

Features that were cut or made smaller to keep the code simple, and why.

## Removed in the pivot (Phase 6B)

The project changed from "type fast in four modes" to "train the letters you are weak at" (brief section 12).
Removed code is still in the git history (commit before "Pivot: letter focus mode, remove extra modes").

| What | Why |
|---|---|
| **Time Attack**, **Survival** and **Code Mode** (`TimeAttackMode`, `SurvivalMode`, `CodeMode`) | The app now solves one clear problem: practising one letter. Classic with a letter focus covers that; the other modes did not. |
| `Timed` interface, per-word countdown, lives, "upcoming words", Space/Enter word and line submit in `GamePanel` | Only existed for the removed modes. |
| `code_snippets.txt`, `WordBank.loadSnippets` / `randomSnippet` | Only used by Code Mode. |
| `askingToQuit` flag in `GamePanel` | Only needed when the clock could end a round behind the "Keluar?" dialog (Time Attack/Survival). Now a round only ends by typing. |
| Heatmap line "Simbol lain" | Showed missed symbols such as `{ ;` from Code Mode. The words are only `a`–`z` now, so it could never appear. |
| HTML escaping of `<`, `>`, `&` and `&nbsp;` for repeated spaces | Only mattered for Code Mode text. Words have letters and single spaces only. |
| Leaderboard tabs (`JTabbedPane`) | Replaced by one table with a filter combo box ("Semua", "Normal", "A" … "Z"). |
| Old leaderboard rows (`Time Attack`, `Survival`, `Code`, `Classic`) | **Not deleted and not converted.** They stay in the CSV and only show under "Semua", because they match no other filter. This needs no extra code. |

## Earlier phases

Nothing was cut before the pivot. The optional "New personal best!" message was built (Phase 5),
because it only needed one small loop (`LeaderboardStore.getPersonalBest`) and one label.
The optional heatmap tooltips were built too (Phase 6): one `setToolTipText` line per key.

## Simplified in the pivot (Phase 6B)

| What | Simplification | Why |
|---|---|---|
| Letter focus words | The 24 focus words and 6 normal words are picked at random, so the same word can appear twice, and some of the 6 "normal" words may also contain the letter (so it can be more than 24). | Avoiding repeats would need extra checks. Normal mode already works this way. |

## Simplified (Phase 6)

| What | Simplification | Why |
|---|---|---|
| Heatmap position | The keyboard is **next to** the result numbers, not under them. | Under them it does not fit in a 650-pixel-high window. |
| Quit dialog | While "Keluar?" is open, the clock is **not paused**. | Pausing would need the timer to remember and subtract paused time. |
| Look-and-feel | Kept Java's default ("Metal"). | The Windows look-and-feel ignores our button colors. |

## Simplified (Phase 5)

| What | Simplification | Why |
|---|---|---|
| Leaderboard reading | `getTop(focus, 10)` reads the CSV file again every time the filter changes. | The file is tiny, and keeping `getTop(String, int)` self-contained is easier to explain than keeping a list around. |
| File encoding | `FileReader`/`FileWriter` use the computer's default character set. Java 8 and Java 18+ have different defaults, so a name with a special letter (like `é`) written by one Java version may look wrong when read by the other. Plain letters, digits and `_` are always fine. | Choosing a character set for `FileReader`/`FileWriter` needs Java 11+, and we compile for Java 8. The course slides use plain `FileReader`/`FileWriter`. |
