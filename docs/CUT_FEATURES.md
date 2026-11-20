# Cut and Simplified Features

Features that were cut or made smaller to keep the code simple, and why.

Nothing has been cut so far. The optional "New personal best!" message was built (Phase 5),
because it only needed one small loop (`LeaderboardStore.getPersonalBest`) and one label.
The optional heatmap tooltips were built too (Phase 6): one `setToolTipText` line per key.

## Simplified (Phase 6)

| What | Simplification | Why |
|---|---|---|
| Heatmap "Simbol lain" | Shows at most the **5** most missed symbols that are not on the drawn keyboard. | A long list would make the panel wider than the window. |
| Heatmap position | The keyboard is **next to** the result numbers, not under them. | Under them it does not fit in a 650-pixel-high window. |
| Quit dialog | While "Keluar?" is open, timer ticks are ignored, but the clock is **not paused**. | Pausing would need the timer to remember and subtract paused time. Ignoring ticks needs one `boolean`. |
| Look-and-feel | Kept Java's default ("Metal"). | The Windows look-and-feel ignores our button colors. |

## Simplified (Phase 5)

| What | Simplification | Why |
|---|---|---|
| Code Mode lines | A round uses the **first 5 lines** of a random snippet. Snippets with 6–8 lines are cut after line 5, so a round can end before the closing `}`. | Picking "a nice block of 5 lines" would need extra logic. Typing practice does not need a complete program. |
| Code Mode wrong line | A wrong line is **not retried**. Enter sends it, the mistakes count in the accuracy, and the next line appears (same as Classic, which also ends even with mistakes). | Retrying would need extra state and could make a player stuck on one line. |
| Code Mode Enter | Enter is **ignored until the typed line is as long as the target**. | Stops an accidental Enter from skipping a line. One `if`, no extra state. |
| Leaderboard reading | `getTop(mode, 10)` reads the CSV file again for each of the 4 tabs, so a broken line prints its warning 4 times in the console. | The file is tiny, and keeping `getTop(String, int)` self-contained is easier to explain than passing a list around. |
| File encoding | `FileReader`/`FileWriter` use the computer's default character set. Java 8 and Java 18+ have different defaults, so a name with a special letter (like `é`) written by one Java version may look wrong when read by the other. Plain letters, digits and `_` are always fine. | Choosing a character set for `FileReader`/`FileWriter` needs Java 11+, and we compile for Java 8. The course slides use plain `FileReader`/`FileWriter`. |
