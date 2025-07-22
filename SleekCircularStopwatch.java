import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.time.Duration;
import java.time.Instant;

public class SleekCircularStopwatch extends JPanel implements ActionListener {
    private Timer timer;
    private Instant startTime;
    private boolean running = false;

    private JButton startButton, stopButton, resetButton;
    private JLabel timeLabel;

    public SleekCircularStopwatch() {
        setPreferredSize(new Dimension(400, 500));
        setBackground(new Color(30, 30, 30));

        timeLabel = new JLabel("00:00:00", SwingConstants.CENTER);
        timeLabel.setFont(new Font("JetBrains Mono", Font.BOLD, 26));
        timeLabel.setForeground(new Color(220, 220, 220));
        timeLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

        startButton = createButton("Start");
        stopButton = createButton("Stop");
        resetButton = createButton("Reset");

        timer = new Timer(100, this); // refresh rate

        JPanel controls = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        controls.setBackground(new Color(30, 30, 30));
        controls.add(startButton);
        controls.add(stopButton);
        controls.add(resetButton);

        setLayout(new BorderLayout());
        add(timeLabel, BorderLayout.NORTH);
        add(controls, BorderLayout.SOUTH);
    }

    private JButton createButton(String text) {
        JButton button = new JButton(text);
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(50, 50, 50));
        button.setFocusPainted(false);
        button.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        button.setPreferredSize(new Dimension(80, 40));
        button.addActionListener(e -> {
            switch (text) {
                case "Start" -> start();
                case "Stop" -> stop();
                case "Reset" -> reset();
            }
        });
        return button;
    }

    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int radius = 160;
        int cx = getWidth() / 2;
        int cy = getHeight() / 2;

        // Draw shadow glow effect
        g2.setColor(new Color(60, 60, 60));
        g2.fillOval(cx - radius - 10, cy - radius - 10, radius * 2 + 20, radius * 2 + 20);

        // Draw circle outline
        g2.setColor(new Color(120, 120, 120));
        g2.setStroke(new BasicStroke(3));
        g2.drawOval(cx - radius, cy - radius, radius * 2, radius * 2);

        // Draw hand
        if (startTime != null) {
            long elapsed = Duration.between(startTime, Instant.now()).toMillis();
            double angle = Math.toRadians((elapsed % 60000) / 60000.0 * 360);

            int handLength = radius - 30;
            int x2 = (int) (cx + handLength * Math.sin(angle));
            int y2 = (int) (cy - handLength * Math.cos(angle));

            g2.setColor(Color.RED);
            g2.setStroke(new BasicStroke(2));
            g2.drawLine(cx, cy, x2, y2);
        }
        g2.dispose();
    }

    private void start() {
        if (!running) {
            startTime = Instant.now().minusMillis(elapsed());
            running = true;
            timer.start();
        }
    }

    private void stop() {
        running = false;
        timer.stop();
    }

    private void reset() {
        running = false;
        timer.stop();
        startTime = null;
        timeLabel.setText("00:00:00");
        repaint();
    }

    private long elapsed() {
        return startTime == null ? 0 : Duration.between(startTime, Instant.now()).toMillis();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        long ms = elapsed();
        long sec = ms / 1000;
        long min = sec / 60;
        long hr = min / 60;
        timeLabel.setText(String.format("%02d:%02d:%02d", hr, min % 60, sec % 60));
        repaint();
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Sleek Dark Circular Stopwatch");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setContentPane(new SleekCircularStopwatch());
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}