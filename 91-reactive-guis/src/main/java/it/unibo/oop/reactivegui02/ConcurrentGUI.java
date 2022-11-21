package it.unibo.oop.reactivegui02;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.lang.reflect.InvocationTargetException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

/**
 * Second example of reactive GUI.
 */
public final class ConcurrentGUI extends JFrame {

    private static final long serialVersionUID = 2L;
    private static final double WIDTH_PERCENT = 0.2;
    private static final double HEIGHT_PERCENT = 0.13;
    private final JLabel display = new JLabel();
    private final JButton up = new JButton("Up");
    private final JButton down = new JButton("Down");
    private final JButton stop = new JButton("Stop");

    public ConcurrentGUI() {
        super();
        final Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        this.setSize((int) (screen.getWidth() * WIDTH_PERCENT), (int) (screen.getHeight() * HEIGHT_PERCENT));
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        final JPanel panel = new JPanel();
        panel.add(display);
        panel.add(up);
        panel.add(down);
        panel.add(stop);
        this.getContentPane().add(panel);
        this.setVisible(true);

        // Creating a new thread for the counter.
        final Agent agent = new Agent();
        new Thread(agent).start();

        // Event handlers.
        up.addActionListener((e) -> agent.setUpCounting());
        down.addActionListener((e) -> agent.setDownCounting());
        stop.addActionListener((e) -> {
            agent.stopCounting();
            stop.setEnabled(false);
            up.setEnabled(false);
            down.setEnabled(false);
        });
    }

    private class Agent implements Runnable {

        private int counter;
        private volatile boolean stop;
        private volatile boolean up = true;

        @Override
        public void run() {
            while (!this.stop) {
                try {
                    final var nextIntegerText = Integer.toString(this.counter);
                    SwingUtilities.invokeAndWait(() -> ConcurrentGUI.this.display.setText(nextIntegerText));
                    countOperation();
                    Thread.sleep(100);
                } catch (final InvocationTargetException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        public void stopCounting() {
            this.stop = true;
        }

        public void setUpCounting() {
            this.up = true;
        }

        public void setDownCounting() {
            this.up = false;
        }

        private void countOperation() {
            if (this.up) {
                this.counter++;
            } else {
                this.counter--;
            }
        }

    }
}
