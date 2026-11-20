# Course Style Notes

Findings from reading all lecture slides in `Full Materi PBO` (Week 1–14, by Afrizal Doewes).
Every later phase follows these notes. Format: **what the course shows → what KetikIn does**.

Where a feature is *not* in the slides, it is marked **(not in slides)**. Some of those were probably
covered in the practicum (ambatucode), which I could not open.

## GUI (Week 13)

- **Event listeners:** the slides use an **anonymous inner class**:
  `button.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent e) { ... } });`
  No lambdas anywhere in the slides. → **KetikIn uses anonymous inner classes for every listener. No lambdas.**
- The slides write `public void actionPerformed(...)` inside the anonymous class **without** `@Override`.
  But `@Override` *is* taught in Week 4 (Inheritance). → KetikIn puts `@Override` on overridden methods,
  including inside anonymous listeners, because it catches typos and was taught.
- Imports in the slides: `import javax.swing.*` style is implied. → KetikIn uses **explicit imports**
  (`import javax.swing.JButton;`) so you can see exactly which classes a file uses.
- Components shown: `JFrame`, `JLabel`, `JButton`, `ImageIcon`, `JOptionPane.showMessageDialog`.
- Layout managers shown: `FlowLayout`, `BorderLayout`, `GridLayout`, `BoxLayout` (and `setLayout(null)` once).
  Tip in the slides: nest `JPanel`s with different layouts. → KetikIn uses `BorderLayout` + `GridLayout` +
  `FlowLayout` + nested panels. **No `null` layout.**
- The slides build the `JFrame` directly inside `main`. → KetikIn uses `MainFrame extends JFrame` (inheritance,
  Week 4), which is the same idea organized as a class.
- **(not in slides)** `CardLayout`, `JTextField`, `JTable`, `JTabbedPane`, `DefaultTableModel`, `JScrollPane`.
  These match the practicum sections "Containers & Panels", "Basic Components" and "JTable & Collections",
  and the brief requires them.
- **(not in slides)** `KeyListener` and `DocumentListener`: neither appears. → KetikIn uses a **`KeyListener`
  written as an anonymous inner class** (same shape as the `ActionListener` example: implement the interface,
  fill in the methods), reading the text in `keyReleased`. Reason: it is the closest to the slide style, and
  unlike `DocumentListener` it lets us safely change the text field from inside the listener (clearing it after
  Space/Enter). `keyTyped` and `keyPressed` stay empty with a comment.
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
  never user-input validation.** → assertions only on invariants (accuracy 0–100, lives ≥ 0), names are validated
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
  **multilevel** / hierarchical inheritance. → `GameMode` ← `ClassicMode` ← `CodeMode` (multilevel),
  `GameMode` ← Classic/TimeAttack/Survival (hierarchical).
- Encapsulation (Week 5): `private` fields + public getters/setters, the four access modifiers including
  `protected` and default. → `Player`, `TypingStats`; `protected` fields in `GameMode` for subclasses.
- Polymorphism (Week 6): runtime = overriding + **upcasting** (`Animal myDog = new Dog();`);
  compile-time = **method overloading**. → `GameMode mode = new ClassicMode(...)`; overloaded `ScoreEntry`
  constructors and `getWpm()` / `getWpm(long)`.
- Abstraction (Week 7): `abstract class` with abstract + concrete methods and constructors; `interface` with
  `implements`. Slides mention "Java 8+ allows default methods". → `abstract class GameMode`, `interface Timed`,
  `interface TimerListener`, `interface DataStore<T>`. **No default methods** (brief forbids them).
- Variables (Week 3): local / instance / **static** variables, wrapper classes, **autoboxing/unboxing**.
  → `public static final` constants (e.g. in `Theme`); `HashMap<Character, Integer>` relies on autoboxing
  (comment it where it happens).
- Class & Object (Week 2): constructor = same name as class, no return type; `new`; heap vs stack.

## Things that do NOT appear anywhere in the slides

- **`enum`**: not in any slide. → **No enums.** Mode names are `public static final String` constants.
- **Lambdas / method references / streams**: not in any slide. → never used.
- `var`, records, switch expressions, text blocks: not in any slide. → never used.

## Practicum website

I could not open https://ambatucode.stevan.id/modules/ppbo-2026 (login needed). The decisions above that are
marked **(not in slides)** would be good to double-check against these practicum pages, if you can paste them:
- GUI → "Event Handling" (does it use `KeyListener`, `KeyAdapter` or `DocumentListener`?)
- GUI → "Containers & Panels" (is `CardLayout` shown?)
- GUI → "JTable & Collections" (how is the table model built?)
