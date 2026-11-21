# Course Style Notes

Findings from reading all lecture slides in `Full Materi PBO` (Week 1–14, by Afrizal Doewes), plus the
practicum GUI pages on ambatucode (checked by the team, see the last section).
Every later phase follows these notes. Format: **what the course shows → what KetikIn does**.

Where a feature is *not* in the slides, it is marked **(not in slides)**.

## GUI (Week 13)

- **Event listeners for buttons:** the slides use an anonymous inner class
  (`new ActionListener() { public void actionPerformed(ActionEvent e) { ... } }`), but the **practicum pages
  "Event Handling" and "Input Processing" use a lambda**: `btn.addActionListener(e -> { ... });`.
  → **KetikIn uses a lambda ONLY for `addActionListener`, and always in exactly that form**
  (`button.addActionListener(e -> { ... });`, with braces, even for one line).
  → **Everything else stays an anonymous inner class**: the `KeyListener`, the `Runnable` passed to
  `SwingUtilities.invokeLater`, and the `DefaultTableModel` override (`isCellEditable`).
  No other lambdas, no method references.
- `@Override` is taught in Week 4 (Inheritance). → KetikIn puts `@Override` on every overridden method,
  including the methods inside anonymous inner classes.
- Imports in the slides: `import javax.swing.*` style is implied. → KetikIn uses **explicit imports**
  (`import javax.swing.JButton;`) so you can see exactly which classes a file uses.
- Components shown: `JFrame`, `JLabel`, `JButton`, `ImageIcon`, `JOptionPane.showMessageDialog`.
- Layout managers in the slides: `FlowLayout`, `BorderLayout`, `GridLayout`, `BoxLayout` (and `setLayout(null)`
  once). The practicum "Layout Manager" page shows only **`FlowLayout`, `BorderLayout` and `GridLayout`**.
  Tip in the slides: nest `JPanel`s with different layouts. → KetikIn uses `BorderLayout` + `GridLayout` +
  `FlowLayout` + nested panels. **No `null` layout, no `BoxLayout`.**
- The slides build the `JFrame` directly inside `main`. → KetikIn uses `MainFrame extends JFrame` (inheritance,
  Week 4), which is the same idea organized as a class.
- **(not in slides or practicum)** `CardLayout`. Required by the brief (screen switching). → kept, and listed in
  `CODE_GUIDE.md` under "Things outside the course material". (`JTabbedPane` was used until the pivot; the
  leaderboard now uses one `JTable` with a `JComboBox` filter.)
- **JTable (practicum "JTable & Collections"):** `new DefaultTableModel(kolom, 0)` (column names + 0 rows),
  then filled row by row from an `ArrayList`. → `LeaderboardPanel` uses exactly this pattern
  (`addRow` in a plain `for` loop).
- **Key input:** the slides show neither `KeyListener` nor `DocumentListener`. The practicum "Event Handling"
  page only *names* `DocumentListener` ("mendeteksi perubahan teks pada komponen input"), with no example.
  `KeyListener` is not shown anywhere. → KetikIn keeps a **`KeyListener` written as an anonymous inner class**,
  reading the text in `keyReleased`. Reason: unlike `DocumentListener`, it lets us safely change the text field
  from inside the listener (cutting off letters typed past the end); a `DocumentListener` that changes its own document
  throws an exception. `keyTyped` and `keyPressed` stay empty. Listed in `CODE_GUIDE.md` under
  "Things outside the course material".
- **Input validation (practicum "Input Processing & Validation"):** `JOptionPane.showMessageDialog`, `trim()`,
  `isEmpty()`, `try/catch`. → the menu validates the name with `trim()` + `isEmpty()` + our own checks, catches
  `InvalidUsernameException`, and shows the message with `JOptionPane.showMessageDialog`.
- **(not in slides)** `SwingUtilities.invokeLater`. Required by the brief for thread safety (EDT rule).
  Listed in `CODE_GUIDE.md` under "Things outside the course material".

## Multithreading (Week 14)

- Both styles are shown: **`extends Thread`** (shown first) and `implements Runnable`.
  → **`GameTimer extends Thread`**, overriding `run()`, started with `start()`.
- `Thread.sleep(ms)` is shown, and that it "can throw `InterruptedException`". → the timer loop uses
  `Thread.sleep(100)` inside `try/catch (InterruptedException e)`.
- `join()` and `synchronized` are shown. → KetikIn does not need them (only one value is shared: the stop flag).
- **(not in slides)** `volatile`. Required by the brief for the `running` flag. Listed in `CODE_GUIDE.md`.
- `Runnable` interface is in the slides → `Main` passes an anonymous `Runnable` to `SwingUtilities.invokeLater`,
  which is the same pattern as `new Thread(task)`.

## Java IO (Week 12)

- Shown: `FileReader`, `FileWriter`, **`BufferedReader`** (with the `while ((line = br.readLine()) != null)` loop),
  **`BufferedWriter`** (with `write` + `newLine()`), `FileInputStream/FileOutputStream`, `File`
  (`exists()`, `createNewFile()`, `delete()`, `length()`), serialization, `RandomAccessFile`.
- **Not shown:** `Scanner`, `PrintWriter`, `InputStreamReader`, `getResourceAsStream`.
  → Leaderboard: **`BufferedReader(new FileReader(...))` to read and `BufferedWriter(new FileWriter(...))`
  to write** (exactly the slide classes). No `PrintWriter`, no `Scanner`.
  → Word files inside the jar: `BufferedReader(new InputStreamReader(getClass().getResourceAsStream(...), "UTF-8"))`.
  `InputStreamReader` + `getResourceAsStream` are needed because a file *inside a jar* is not a normal file
  (`FileReader` cannot open it). Listed in `CODE_GUIDE.md`.
- The slides close streams with `close()`; Week 11 then teaches try-with-resources as the better way.
  → KetikIn uses **try-with-resources** for every reader/writer.
- `File` class → used for the `.ketikin` folder and leaderboard file (`exists()`, plus `mkdirs()`).

## Exceptions & Assertions (Week 11)

- **Try-with-resources is shown** (`try (FileReader file = new FileReader("data.txt")) { ... }`). → used.
- try / catch / multiple catch / finally, `throw` vs `throws`, checked vs unchecked → all used where natural.
- Custom exception example: `class MyCustomException extends Exception { MyCustomException(String message) { super(message); } }`
  → `WordFileException` and `InvalidUsernameException` look exactly like this (checked, `extends Exception`).
- `IllegalArgumentException` is thrown in the slide example (`throw new IllegalArgumentException("Invalid age")`)
  → `ScoreEntry.fromCsvLine` throws it for a broken CSV line.
- `assert condition : "message";` and `java -ea` are shown. Rule from the slides: **assert = internal assumptions,
  never user-input validation.** → assertions only on invariants (accuracy 0–100, word list not empty), names are validated
  with exceptions. `run.bat` uses `-ea`.

## Generics & Collections (Week 10)

- Shown: `List<String> list = new ArrayList<>()` (diamond operator), `Set/HashSet`, `Queue/LinkedList`,
  `Map<String, Integer> = new HashMap<>()`, iterating with `for (Map.Entry<K, V> e : map.entrySet())`,
  `Collections.sort/max/min/shuffle`, `Arrays.asList`.
  → KetikIn uses `ArrayList`, `HashMap<Character, Integer>`, the `Map.Entry` loop (for the top-3 missed keys),
  `Collections.sort`, and the **diamond operator**.
- The slides say generics apply to "classes, interfaces, and methods" but **do not show writing your own generic
  type**. → `interface DataStore<T>` is our own generic interface (required by the brief). It is the simplest
  possible form (one type parameter, no wildcards, no bounds).
- **(not in slides)** `Comparable<T>` / `compareTo` as an interface we implement. `Integer.compareTo` is mentioned
  in Week 3, and `Collections.sort` is shown. → `ScoreEntry implements Comparable<ScoreEntry>` with plain `if`s.

## String (Week 9)

- Methods table in the slides: `length`, `charAt`, `substring`, `indexOf`, `toLowerCase`, `toUpperCase`, `trim`,
  `replace(char, char)`, `equals`, `equalsIgnoreCase`. Also `concat`, `+`, `==` vs `equals`, string pool,
  immutability, **`StringBuilder`** (`append`, `insert`, `delete`, `reverse`) and `StringBuffer`.
  → KetikIn uses these methods, and **`StringBuilder` for building the colored HTML and joined word lists**
  (the slides explain why: `+` in a loop creates many objects).
- **(not in slides)** `split` and `String.format`. The practicum list mentions "splitting/joining/formatting",
  so they are course material there. → used for CSV lines and number formatting.
- `Character.isLetter / isLetterOrDigit` are not in the slides, but the "Username Validator" practicum is exactly
  this kind of check. → used for name validation, with a plain loop, no regex.

## OOP pillars (Week 1–7)

- Inheritance (Week 4): `extends`, `@Override`, `super.method()`, `super(...)` in constructors, single /
  **multilevel** / hierarchical inheritance. → `GameMode` ← `ClassicMode` ← `LetterFocusMode` (multilevel).
  (Hierarchical inheritance ended with the pivot, when Time Attack and Survival were removed.)
- Encapsulation (Week 5): `private` fields + public getters/setters, the four access modifiers including
  `protected` and default. → `Player`, `TypingStats`; `protected` fields in `GameMode` for subclasses.
- Polymorphism (Week 6): runtime = overriding + **upcasting** (`Animal myDog = new Dog();`);
  compile-time = **method overloading**. → `GameMode mode = new ClassicMode(...)`; overloaded `ScoreEntry`
  constructors and `getWpm()` / `getWpm(long)`.
- Abstraction (Week 7): `abstract class` with abstract + concrete methods and constructors; `interface` with
  `implements`. Slides mention "Java 8+ allows default methods". → `abstract class GameMode`,
  `interface TimerListener`, `interface DataStore<T>`. **No default methods** (brief forbids them).
- Variables (Week 3): local / instance / **static** variables, wrapper classes, **autoboxing/unboxing**.
  → `public static final` constants (e.g. in `Theme`); `HashMap<Character, Integer>` relies on autoboxing
  (comment it where it happens).
- Class & Object (Week 2): constructor = same name as class, no return type; `new`; heap vs stack.

## Things that do NOT appear anywhere in the slides

- **`enum`**: not in any slide. → **No enums.** Mode names are `public static final String` constants.
- **Lambdas**: not in any slide, but used by the practicum for buttons. → only `addActionListener(e -> { ... });`.
- **Method references / streams**: not in the slides or the practicum. → never used.
- `var`, records, switch expressions, text blocks: not in any slide. → never used.

## Practicum website

https://ambatucode.stevan.id/modules/ppbo-2026 needs a login. The team checked these GUI pages and reported
(already folded into the sections above):
- **Event Handling / Input Processing:** buttons use lambdas `btn.addActionListener(e -> { ... });`.
  `DocumentListener` is only named; `KeyListener` is not shown.
- **JTable & Collections:** `new DefaultTableModel(kolom, 0)` filled from an `ArrayList`.
- **Input Processing & Validation:** `JOptionPane.showMessageDialog`, `trim()`, `isEmpty()`, `try/catch`.
- **Layout Manager:** only `FlowLayout`, `BorderLayout`, `GridLayout`. No `CardLayout`, no `JTabbedPane`.

Note: the Phase 1 placeholder buttons used anonymous `ActionListener`s. Since Phase 5 all of them are replaced,
and every button uses `addActionListener(e -> { ... });`.
