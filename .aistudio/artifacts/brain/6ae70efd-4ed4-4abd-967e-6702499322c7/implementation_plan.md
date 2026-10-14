# Nova Calculator Pro

Nova Calculator Pro is an advanced, ultra-responsive scientific calculator Android application crafted with a futuristic matte-dark aesthetic, glowing neon blue/purple accents, and instant mathematical evaluation.

## User Review & Critical Decisions

> [!IMPORTANT]
> The following architectural and UX decisions are established for Nova Calculator Pro based on your specifications and modern Android standards:

- **Scientific Mode Transition**: A smooth sliding multi-row scientific accessory panel sits directly above the core keypad, allowing seamless single-tap expansion into trigonometric, logarithmic, power, factorial, and constant functions while keeping number pads reachable.
- **Evaluation Engine**: Real-time expression evaluation engine with live preview beneath the input expression. Tapping `=` locks the evaluated result into history and promotes it as the primary result with smooth numerical morphing.
- **Scientific Constants & Memory**: Dedicated scientific constants (π, e, c, G, h, N_A) and memory registers (MC, MR, M+, M-) with tactile feedback and visual active memory badges.
- **Theme & Offline Persistence**: Default futuristic matte black & neon theme with support for custom light/dark switching. Calculation history is stored locally in Room database for instant offline access and recall.

---

## 1. Overview & Core Concept

- **What It Does**: Nova Calculator Pro provides standard and high-level scientific calculations with high numerical precision, parentheses nesting, angle unit switching (DEG/RAD), memory registers, expression editing with cursor positioning, calculation history with tap-to-reuse, and result clipboard copying.
- **Target Audience / Persona**: Students, engineers, scientists, and daily users who need a powerful, error-free calculator that looks and feels like a modern flagship Android tool.
- **Key Value**: Delivers the tactile precision of a physical scientific calculator combined with an expressive, modern interface, responsive haptic feedback, and offline reliability.

---

## 2. User Experience & Visual Design

### Key User Flows

1. **Quick Calculations**: Launch into clean expression view. Typing numbers and basic operations (`+`, `−`, `×`, `÷`) shows live ghost results beneath the formula. Pressing `=` commits the equation to history with an energetic neon pulse.
2. **Scientific Calculations**: Tap the scientific expander or quick-access drawer to reveal `sin`, `cos`, `tan`, `log`, `ln`, `√`, `x²`, `xʸ`, `1/x`, `n!`, `π`, `e`, parentheses, and the `DEG/RAD` toggle.
3. **History Exploration & Re-use**: Tap the History button in the top bar to slide open the calculation history sheet. Users can tap any previous equation or result to inject it back into the active expression, share/copy it, or clear history.
4. **Memory Registers**: Tap `M+` or `M−` to accumulate to memory; `MR` recalls memory with a clear glowing badge when memory is non-zero, and `MC` clears it.
5. **Theme & Customization**: Access settings to toggle between Deep Matte Cyberpunk (neon blue/purple) and Clean Minimalist Slate theme, toggle haptic vibration intensity, and adjust scientific notation precision.

### Visual Identity & Theme Tokens

- **Aesthetic Direction**: Futuristic Cyber-Chic & Precision Instrument. Matte dark surface, subtle glassmorphism elevation, and neon glow accents.
- **Color Palette**:
  - `Background`: `#0B0E14` (Deep obsidian matte black)
  - `Surface`: `#131823` (Subtle dark slate card surface)
  - `Surface Variant / Keypad`: `#192030` (Muted dark elevated keys)
  - `Primary / Neon Accent`: `#4D96FF` (Electric Cyan Blue)
  - `Secondary Accent`: `#9B51E0` (Neon Ultraviolet Purple)
  - `Equals Neon Gradient`: Linear gradient from `#7F00FF` to `#00F0FF`
  - `Operator Accent`: `#FF7597` (Vibrant Coral Pink for clear operational distinction)
  - `Text Primary`: `#F0F4FC` (Crisp readable high-contrast white)
  - `Text Secondary`: `#8C97B0` (Soft metallic slate for formula history)
- **Typography & Touch Targets**:
  - High-legibility monospaced & geometric numeral hierarchy with dynamic auto-scaling text to avoid clipping long formulas.
  - Generous 48dp+ rounded touch targets with subtle scale spring animations and localized tactile haptic feedback.

---

## 3. Key Product Decisions & Trade-Offs

- **Decision 1: Mathematical Evaluation Engine (Recursive Descent / Shunting-Yard Parser with BigDecimal Precision)**
  - *Chosen Approach*: Implement a robust custom mathematical expression parser supporting operator precedence, implicit multiplication (e.g. `2π`, `3(4+5)`), trigonometric functions in DEG and RAD, logarithms, powers, factorials, and balanced parentheses.
  - *Why*: Eliminates arbitrary limitations of naive single-operation state machines, guarantees error handling without crashing on division by zero or negative square roots, and supports dynamic cursor editing.
  - *Alternatives Considered*: Standard Java `ScriptEngine` (unavailable on Android JVM without heavy extra runtimes).
- **Decision 2: Local Persistence via Room Database**
  - *Chosen Approach*: Persist all calculation history locally with timestamp, expression, result, and optional favorite bookmarking using Android Room and Kotlin Flow.
  - *Why*: Completely offline, fast instant loading, zero login barriers, and safe lifecycle management.
- **Decision 3: Dual Mode Responsive Keypad with Ergonomic Bottom Reach**
  - *Chosen Approach*: Numbers and standard operators are anchored to the bottom thumb zone; scientific functions are housed in an ergonomic secondary strip / expandable sheet with quick-access badges.
  - *Why*: Enables rapid one-handed calculations while keeping complex scientific tools one tap away.

---

## 4. Technical Architecture & Data Strategy

```
┌─────────────────────────────────────────────────────────────┐
│                       MainActivity                          │
│        (Edge-to-Edge Insets, System Bars, Theme Host)       │
└──────────────────────────────┬──────────────────────────────┘
                               │
                               ▼
┌─────────────────────────────────────────────────────────────┐
│                    CalculatorViewModel                      │
│  - Expression state (String & Cursor)                       │
│  - Live evaluation preview                                  │
│  - Angle mode (DEG / RAD)                                   │
│  - Memory register (Double)                                 │
│  - Theme selection & Haptics toggle                         │
│  - History observation (Flow<List<HistoryItem>>)            │
└──────────────┬───────────────────────────────┬──────────────┘
               │                               │
               ▼                               ▼
┌───────────────────────────────┐ ┌───────────────────────────┐
│       CalculationEngine       │ │      HistoryRepository    │
│  - Tokenizer & Lexer          │ │  - Room DB (AppDatabase)  │
│  - Shunting-Yard Parser       │ │  - HistoryDao (Flow)      │
│  - Scientific Math Functions  │ │  - Insert / Delete / Clear│
└───────────────────────────────┘ └───────────────────────────┘
               │
               ▼
┌─────────────────────────────────────────────────────────────┐
│                          UI Layer                           │
│  ├── TopBar (App Title, Angle Mode, History & Settings)    │
│  ├── DisplayScreen (Formula, Live Preview, Copy & Cursor)   │
│  ├── ScientificTray (sin, cos, tan, ln, log, √, ^, !, etc.) │
│  ├── NumericKeypad (0-9, Operators, Clear, Delete, Equals)  │
│  ├── HistoryBottomSheet / Modal (List, Re-use, Delete)      │
│  └── SettingsDialog (Themes, Haptic, Sound, Precision)      │
└─────────────────────────────────────────────────────────────┘
```

### Data Models & State

- **`HistoryItem`**: `@Entity(tableName = "calculation_history")` containing `id: Long`, `expression: String`, `result: String`, `timestamp: Long`, `angleMode: String`.
- **`CalculatorUiState`**: Holds `expression: String`, `cursorPosition: Int`, `liveResult: String?`, `finalResult: String?`, `angleMode: AngleMode` (DEG/RAD), `memoryValue: Double`, `isScientificExpanded: Boolean`, `isDarkTheme: Boolean`, `hapticEnabled: Boolean`.

### Interactive Component & State Mapping

- **Number & Dot keys**: Append digits, smart validation against multiple decimals within the same token.
- **Operator keys**: Prevent duplicate adjacent operators; replace previous trailing operator cleanly.
- **Scientific functions**: Insert function prefix with open parenthesis (e.g. `sin(`, `√(`, `ln(`).
- **Parentheses**: Smart matching auto-inserts `(` or `)` based on balance count.
- **Delete (⌫)**: Backspaces character or removes whole multi-character function tags (`sin(`, `log(`). Long press clears the whole expression.
- **Equals (=)**: Computes final result, saves record into Room DB, highlights result with neon glow pulse.
- **History item tap**: Inserts past expression or result directly into current input.
- **Copy Button**: Copies formatted result to Android clipboard with a transient confirmation snackbar/pill.
