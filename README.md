# 🔐 StegaCrypt — Hide Secrets in Plain Sight

A **Steganography Tool** that hides secret messages inside ordinary images using **LSB (Least Significant Bit) Encoding**. The image looks completely normal — but it carries a hidden message inside its pixels.

### 🌐 [Live Demo → stegacrypt.vercel.app](https://stegacrypt.vercel.app)

---

## 🤔 What is Steganography?

Steganography is the practice of hiding secret information inside ordinary-looking data. Unlike encryption (which scrambles data), steganography makes the message **invisible**. The encoded image looks identical to the original — the color changes by at most ±1 per pixel, which is completely undetectable to the human eye.

---

## 🎯 How It Works

| Step | What Happens |
|------|-------------|
| 1️⃣ | Your secret message is converted to **binary** (0s and 1s) |
| 2️⃣ | Each pixel's **RGB values** are read as 8-bit binary numbers |
| 3️⃣ | The **Least Significant Bit** of each color channel is replaced with a message bit |
| 4️⃣ | The modified image is saved — it looks **identical** but carries your secret |

```
Original pixel:  R = 11010110 (214)   G = 10110001 (177)   B = 01001110 (78)
Message bits:    0, 1, 0
Modified pixel:  R = 11010110 (214)   G = 10110001 (177)   B = 01001110 (78)
                         ↑ LSB                ↑ LSB                ↑ LSB
```

---

## 🛠️ Core Java Concepts Used

This project demonstrates **12 Core Java concepts**:

| Concept | Implementation |
|---------|---------------|
| **OOP** | Classes, Objects — `StegoImage`, `SecretMessage`, `LSBSteganography` |
| **Abstraction** | `SteganographyEngine` interface |
| **Polymorphism** | `SteganographyEngine engine = new LSBSteganography()` |
| **Encapsulation** | Private fields, public getters/setters |
| **Inheritance** | `StegoException extends Exception` |
| **Bit Manipulation** | AND (`&`), OR (`\|`), SHIFT (`>>`, `<<`) operators |
| **File I/O** | `ImageIO.read()`, `ImageIO.write()`, `BufferedImage` |
| **Collections** | `ArrayList`, `HashMap` for pixel data and stats |
| **Exception Handling** | Custom `StegoException` with error type `Enum` |
| **Multithreading** | `SwingWorker` for background encoding/decoding |
| **Swing GUI** | JFrame, JPanel, JTabbedPane, JFileChooser, event listeners |
| **Enums** | `ErrorType` enum for categorizing exceptions |

---

## 📁 Project Structure

```
StegaCrypt/
├── java/                              # Core Java PBL Code
│   └── src/stegacrypt/
│       ├── Main.java                  # Entry point
│       ├── model/
│       │   ├── StegoImage.java        # Image model
│       │   └── SecretMessage.java     # Message model
│       ├── core/
│       │   ├── SteganographyEngine.java  # Interface (Abstraction)
│       │   ├── LSBSteganography.java     # Algorithm (Polymorphism)
│       │   └── StegoException.java       # Custom Exception
│       ├── util/
│       │   ├── BitManipulator.java    # Bit operations
│       │   └── ImageProcessor.java    # File I/O
│       └── gui/
│           └── StegaCryptGUI.java     # Swing GUI
│
├── web/                               # Web Demo (deployed on Vercel)
│   ├── index.html
│   ├── style.css
│   └── app.js
│
└── README.md
```

---

## 🚀 How to Run

### Java (Desktop GUI)
```bash
cd java
javac -d out src/stegacrypt/util/*.java src/stegacrypt/core/*.java \
      src/stegacrypt/model/*.java src/stegacrypt/gui/*.java src/stegacrypt/Main.java
java -cp out stegacrypt.Main
```

### Web Demo (Local)
```bash
cd web
npx serve . -l 3000
# Open http://localhost:3000
```

---

## 📸 Screenshots

### Encode Mode
Upload any image → Type a secret message → Download the encoded image

### Decode Mode
Upload the encoded image → Reveal the hidden message

---

## 🧠 Key Algorithm — LSB Encoding

```java
// Set the Least Significant Bit
int modified = (value & 0xFE) | messageBit;
// 0xFE = 11111110 → clears the last bit
// Then OR with our message bit (0 or 1)
```

Each pixel stores **3 bits** of the message (one in R, G, B). A 1024×1024 image can hide **~390,000 characters** — an entire novel!

---

## 📄 License

This project is built for educational purposes as a Core Java PBL Project.
