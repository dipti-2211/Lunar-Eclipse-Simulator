import java.applet.Applet;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;

/**
 * Interactive Lunar Eclipse Simulator
 * Target runtime: JDK 8 (Applet API; legacy academic demonstration).
 *
 * The model is intentionally illustrative and not to scale. It demonstrates
 * the alignment of Sun, Earth and Moon, and the umbra/penumbra shadow regions.
 */
public class LunarEclipseApplet extends Applet
        implements Runnable, ActionListener, ItemListener, AdjustmentListener {

    private SceneCanvas scene;
    private Button startButton, pauseButton, resetButton;
    private Choice eclipseChoice;
    private Scrollbar speedBar;
    private Label statusLabel, speedLabel;

    private Thread animator;
    private volatile boolean running = false;
    private volatile boolean paused = true;
    private volatile int moonX = 565;
    private volatile int speed = 3;

    private final int sceneHeight = 470;
    private String eclipseType = "Total";
    private String stage = "Outside shadow";
    private int direction = 1;

    public void init() {
        setLayout(new BorderLayout(6, 6));
        setBackground(new Color(7, 12, 27));

        scene = new SceneCanvas();
        add(scene, BorderLayout.CENTER);

        Panel controls = new Panel(new FlowLayout(FlowLayout.CENTER, 10, 8));
        controls.setBackground(new Color(18, 27, 48));

        startButton = new Button("Start");
        pauseButton = new Button("Pause");
        resetButton = new Button("Reset");
        eclipseChoice = new Choice();
        eclipseChoice.add("Total");
        eclipseChoice.add("Partial");
        eclipseChoice.add("Penumbral");
        speedBar = new Scrollbar(Scrollbar.HORIZONTAL, 3, 1, 1, 10);

        startButton.addActionListener(this);
        pauseButton.addActionListener(this);
        resetButton.addActionListener(this);
        eclipseChoice.addItemListener(this);
        speedBar.addAdjustmentListener(this);

        statusLabel = new Label("Ready - select an eclipse type and press Start");
        statusLabel.setForeground(Color.WHITE);
        speedLabel = new Label("Speed: 3");
        speedLabel.setForeground(Color.WHITE);

        controls.add(startButton);
        controls.add(pauseButton);
        controls.add(resetButton);
        Label eclipseTypeLabel = new Label("Eclipse type:");
        eclipseTypeLabel.setForeground(Color.WHITE);
        controls.add(eclipseTypeLabel);
        controls.add(eclipseChoice);
        Label speedTextLabel = new Label("Animation speed:");
        speedTextLabel.setForeground(Color.WHITE);
        controls.add(speedTextLabel);
        controls.add(speedBar);
        controls.add(speedLabel);
        add(controls, BorderLayout.SOUTH);

        setSize(1000, 600);
    }

    public void start() {
        if (animator == null) {
            running = true;
            paused = true;
            animator = new Thread(this, "LunarEclipseAnimator");
            animator.start();
        }
    }

    public void stop() {
        running = false;
        if (animator != null) {
            animator.interrupt();
            animator = null;
        }
    }

    public void destroy() {
        stop();
    }

    public void run() {
        while (running) {
            if (!paused) {
                updateMoonPosition();
                scene.repaint();
            }
            try {
                Thread.sleep(45);
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    private void updateMoonPosition() {
        int minX = 565;
        int maxX = 855;
        moonX += direction * speed;

        if (moonX >= maxX) {
            moonX = maxX;
            direction = -1;
        } else if (moonX <= minX) {
            moonX = minX;
            direction = 1;
        }

        updateStage();
    }

    private void updateStage() {
        int shadowCenter = (scene.getWidth() / 2 - 35) + 280;
        int relative = moonX - shadowCenter;
        if ("Total".equals(eclipseType)) {
            if (Math.abs(relative) < 43) stage = "Total lunar eclipse";
            else if (Math.abs(relative) < 88) stage = "Partial lunar eclipse";
            else if (Math.abs(relative) < 130) stage = "Penumbral eclipse";
            else stage = "Outside shadow";
        } else if ("Partial".equals(eclipseType)) {
            if (Math.abs(relative) < 65) stage = "Partial lunar eclipse";
            else if (Math.abs(relative) < 130) stage = "Penumbral eclipse";
            else stage = "Outside shadow";
        } else {
            if (Math.abs(relative) < 130) stage = "Penumbral eclipse";
            else stage = "Outside shadow";
        }
        statusLabel.setText("Current stage: " + stage);
    }

    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
        if (source == startButton) {
            paused = false;
            statusLabel.setText("Animation running");
        } else if (source == pauseButton) {
            paused = true;
            statusLabel.setText("Animation paused - " + stage);
        } else if (source == resetButton) {
            paused = true;
            moonX = 565;
            direction = 1;
            stage = "Outside shadow";
            statusLabel.setText("Simulation reset");
            scene.repaint();
        }
        scene.repaint();
    }

    public void itemStateChanged(ItemEvent e) {
        eclipseType = eclipseChoice.getSelectedItem();
        moonX = 565;
        direction = 1;
        paused = true;
        stage = "Outside shadow";
        statusLabel.setText(eclipseType + " eclipse selected - press Start");
        scene.repaint();
    }

    public void adjustmentValueChanged(AdjustmentEvent e) {
        speed = speedBar.getValue();
        speedLabel.setText("Speed: " + speed);
    }

    private class SceneCanvas extends Canvas {
        private BufferedImage buffer;

        SceneCanvas() {
            setBackground(new Color(7, 12, 27));
        }

        public void update(Graphics g) {
            paint(g);
        }

        public void paint(Graphics g) {
            int w = getWidth();
            int h = getHeight();
            if (w <= 0 || h <= 0) return;

            if (buffer == null || buffer.getWidth() != w || buffer.getHeight() != h) {
                buffer = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
            }

            Graphics2D g2 = buffer.createGraphics();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                RenderingHints.VALUE_ANTIALIAS_ON);
            drawBackground(g2, w, h);
            drawScene(g2, w, h);
            g2.dispose();
            g.drawImage(buffer, 0, 0, this);
        }

        private void drawBackground(Graphics2D g, int w, int h) {
            g.setPaint(new GradientPaint(0, 0, new Color(8, 15, 35),
                                         0, h, new Color(20, 27, 52)));
            g.fillRect(0, 0, w, h);
            g.setColor(new Color(255, 255, 255, 145));
            for (int i = 0; i < 95; i++) {
                int x = (i * 97 + 31) % Math.max(1, w);
                int y = (i * 53 + 19) % Math.max(1, h);
                g.fillOval(x, y, (i % 5 == 0) ? 3 : 1, (i % 5 == 0) ? 3 : 1);
            }
        }

        private void drawScene(Graphics2D g, int w, int h) {
            int cy = h / 2 - 15;
            int sunX = 105;
            int earthX = w / 2 - 35;
            int earthR = 43;
            int moonR = 24;

            // Heading and scale disclaimer
            g.setFont(new Font("SansSerif", Font.BOLD, 22));
            g.setColor(Color.WHITE);
            g.drawString("LUNAR ECLIPSE SIMULATOR", 28, 34);
            g.setFont(new Font("SansSerif", Font.PLAIN, 11));
            g.setColor(new Color(185, 199, 222));
            g.drawString("ILLUSTRATIVE GEOMETRY  |  NOT TO SCALE", 30, 54);

            // Sun glow and body
            g.setColor(new Color(255, 185, 40, 28));
            g.fillOval(sunX - 53, cy - 53, 106, 106);
            g.setColor(new Color(255, 194, 61, 70));
            g.fillOval(sunX - 43, cy - 43, 86, 86);
            g.setPaint(new RadialGradientPaint(new Point(sunX, cy), 32,
                    new float[]{0f, 1f},
                    new Color[]{new Color(255, 248, 180), new Color(255, 143, 28)}));
            g.fillOval(sunX - 32, cy - 32, 64, 64);
            label(g, "SUN", sunX - 16, cy + 57);

            // Sunlight rays
            g.setColor(new Color(255, 221, 120, 90));
            g.setStroke(new BasicStroke(1.2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND,
                                        1f, new float[]{6f, 7f}, 0f));
            g.drawLine(sunX + 35, cy - 25, earthX - 35, cy - 25);
            g.drawLine(sunX + 35, cy + 25, earthX - 35, cy + 25);
            g.drawLine(sunX + 35, cy, earthX - 35, cy);

            // Shadow regions behind Earth/Moon
            int shadowStart = earthX + earthR - 2;
            int farX = Math.min(w - 18, earthX + 440);
            Polygon penumbra = new Polygon();
            penumbra.addPoint(shadowStart, cy - 34);
            penumbra.addPoint(farX, cy - 125);
            penumbra.addPoint(farX, cy + 125);
            penumbra.addPoint(shadowStart, cy + 34);
            g.setColor(new Color(90, 111, 157, 45));
            g.fillPolygon(penumbra);

            Polygon umbra = new Polygon();
            umbra.addPoint(shadowStart, cy - 23);
            umbra.addPoint(Math.min(farX, earthX + 360), cy - 58);
            umbra.addPoint(Math.min(farX, earthX + 360), cy + 58);
            umbra.addPoint(shadowStart, cy + 23);
            g.setColor(new Color(3, 5, 13, 205));
            g.fillPolygon(umbra);

            // Earth
            g.setColor(new Color(30, 105, 190));
            g.fillOval(earthX - earthR, cy - earthR, earthR * 2, earthR * 2);
            g.setColor(new Color(55, 165, 110));
            g.fillOval(earthX - 27, cy - 22, 22, 15);
            g.fillOval(earthX + 3, cy + 2, 24, 18);
            g.fillOval(earthX - 10, cy + 20, 13, 9);
            g.setColor(new Color(120, 190, 255, 180));
            g.setStroke(new BasicStroke(2f));
            g.drawOval(earthX - earthR, cy - earthR, earthR * 2, earthR * 2);
            label(g, "EARTH", earthX - 22, cy + 62);

            // Moon orbit guide
            g.setColor(new Color(190, 202, 225, 100));
            g.setStroke(new BasicStroke(1f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND,
                                        1f, new float[]{3f, 5f}, 0f));
            g.drawLine(earthX + 48, cy, w - 25, cy);

            // Moon appearance based on selected geometry and position
            int mx = moonX;
            int my = cy;
            if ("Penumbral".equals(eclipseType)) my = cy - 84;
            if ("Partial".equals(eclipseType)) my = cy - 28;

            g.setColor(new Color(205, 211, 220));
            g.fillOval(mx - moonR, my - moonR, moonR * 2, moonR * 2);
            g.setColor(new Color(150, 158, 171, 130));
            g.fillOval(mx - 12, my - 9, 9, 7);
            g.fillOval(mx + 4, my + 3, 8, 6);
            g.fillOval(mx - 4, my + 11, 6, 5);

            // Eclipse shading overlay, simplified to communicate the stage.
            int rel = Math.abs(mx - (earthX + 280));
            if (rel < 135) {
                float darkness = rel < 45 ? 0.82f : (rel < 90 ? 0.52f : 0.22f);
                if ("Penumbral".equals(eclipseType)) darkness = 0.22f;
                if ("Partial".equals(eclipseType) && rel < 45) darkness = 0.56f;
                g.setColor(new Color(10, 12, 22, (int)(darkness * 220)));
                g.fillOval(mx - moonR, my - moonR, moonR * 2, moonR * 2);
                if ("Total".equals(eclipseType) && rel < 45) {
                    g.setColor(new Color(175, 58, 45, 105));
                    g.fillOval(mx - moonR, my - moonR, moonR * 2, moonR * 2);
                }
            }
            g.setColor(new Color(245, 245, 255, 180));
            g.drawOval(mx - moonR, my - moonR, moonR * 2, moonR * 2);
            label(g, "MOON", mx - 18, my + 40);

            // Legend
            int ly = h - 54;
            g.setColor(new Color(175, 188, 215));
            g.setFont(new Font("SansSerif", Font.PLAIN, 12));
            g.drawString("SHADOW KEY:", 28, ly);
            g.setColor(new Color(3, 5, 13, 220));
            g.fillRect(120, ly - 11, 22, 12);
            g.setColor(Color.WHITE);
            g.drawString("Umbra (dark inner shadow)", 149, ly);
            g.setColor(new Color(90, 111, 157, 100));
            g.fillRect(330, ly - 11, 22, 12);
            g.setColor(Color.WHITE);
            g.drawString("Penumbra (lighter outer shadow)", 359, ly);

            g.setFont(new Font("SansSerif", Font.PLAIN, 11));
            g.setColor(new Color(190, 202, 225));
            g.drawString("Selected mode: " + eclipseType + " eclipse", 28, h - 25);
            g.drawString("Stage: " + stage, Math.max(260, w - 250), h - 25);
        }

        private void label(Graphics2D g, String text, int x, int y) {
            g.setFont(new Font("SansSerif", Font.BOLD, 12));
            g.setColor(Color.WHITE);
            g.drawString(text, x, y);
        }
    }

    /**
     * Standalone application runner allowing this project to run directly
     * on modern Java (JDK 8 through JDK 17/21+) without appletviewer.
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                Frame frame = new Frame("Interactive Lunar Eclipse Simulator");
                LunarEclipseApplet applet = new LunarEclipseApplet();
                frame.setLayout(new BorderLayout());
                frame.add(applet, BorderLayout.CENTER);
                applet.init();

                frame.setSize(1000, 620);
                frame.setLocationRelativeTo(null);
                frame.addWindowListener(new WindowAdapter() {
                    @Override
                    public void windowClosing(WindowEvent e) {
                        applet.stop();
                        applet.destroy();
                        frame.dispose();
                        System.exit(0);
                    }
                });
                frame.setVisible(true);
                applet.start();
            }
        });
    }
}
