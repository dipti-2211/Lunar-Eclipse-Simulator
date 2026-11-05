# Interactive Lunar Eclipse Simulator

An interactive educational simulator demonstrating the celestial mechanics, alignment, and shadow geometry (umbra and penumbra) of a lunar eclipse.

---

## 🚀 How to Run

### Method 1: Instant Browser Mode (Recommended — No Setup Required)
Open `LunarEclipse.html` directly in any modern web browser (Chrome, Safari, Firefox, Edge):
- **On macOS:**
  ```bash
  open LunarEclipse.html
  ```
- Or double-click `LunarEclipse.html` in your file manager / Finder.
- Runs offline via HTML5 Canvas with identical geometry, rendering, physics, and interactive controls.

---

### Method 2: Desktop Java Application (JDK 8, 11, 17, 21+)
A native desktop runner (`main` entry point) has been integrated into `LunarEclipseApplet.java` while preserving 100% Applet API compatibility.

1. **One-command launcher:**
   ```bash
   ./run.sh
   ```

2. **Or compile & run manually:**
   ```bash
   javac -d . src/LunarEclipseApplet.java
   java LunarEclipseApplet
   ```

---

### Method 3: Legacy Appletviewer (JDK 8 only)
If you are running in an environment with legacy JDK 8 installed:
```bash
javac -source 1.8 -target 1.8 -d . src/LunarEclipseApplet.java
appletviewer LunarEclipse.html
```

---

## 🎮 Interactive Controls
- **Start**: Begin the Moon's orbital animation.
- **Pause**: Halt the animation to inspect the current stage.
- **Reset**: Return the Moon to the beginning of the trajectory.
- **Eclipse type**: Select between **Total**, **Partial**, or **Penumbral** eclipse geometries.
- **Animation speed**: Adjust orbital traversal speed (1 to 10).

---

## 🔭 Model Concepts & Viva Explanation
- **Syzygy Alignment**: Demonstrates Sun, Earth, and Moon aligned along the ecliptic plane.
- **Umbra**: The dark central cone of Earth's shadow where direct sunlight is completely obstructed.
- **Penumbra**: The outer shadow cone where sunlight is only partially blocked.
- **Rayleigh Scattering ("Blood Moon")**: During totality in a total lunar eclipse, shorter blue wavelengths are scattered by Earth's atmosphere, and refracted longer red wavelengths illuminate the Moon with a copper-red glow.
- **Viva explanation**: The applet extends `java.applet.Applet` and implements `Runnable`, `ActionListener`, `ItemListener`, and `AdjustmentListener`. AWT components provide user interaction, `Graphics2D` handles custom double-buffered rendering, and a dedicated thread updates the Moon position and coordinates stage classification.

---

## 📁 Project Structure
- `LunarEclipse.html` — Interactive web simulator & host page
- `src/LunarEclipseApplet.java` — Complete Java source code (dual Applet & standalone Application)
- `run.sh` — Automated compile & run script for desktop Java
- `Technical_Report.pdf` — Project technical report
