package it.unibo.oop.reactivegui03;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

/**
 * Third experiment with reactive gui.
 */
public final class AnotherConcurrentGUI extends JFrame {

    private static final long serialVersionUID = 2L;
    private static final double WIDTH_PERCENT = 0.2;
    private static final double HEIGHT_PERCENT = 0.13;
    private final JLabel display = new JLabel();
    private final JButton up = new JButton("Up");
    private final JButton down = new JButton("Down");
    private final JButton stop = new JButton("Stop");

    public AnotherConcurrentGUI() {
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

        /*
         * Creating a new threads for the counter and
         * the auto-disabling of the three buttons.
        */
        final CounterAgent counterAgent = new CounterAgent();
        new Thread(counterAgent).start();

        // Event handlers.
        up.addActionListener((e) -> counterAgent.setUpCounting());
        down.addActionListener((e) -> counterAgent.setDownCounting());
        stop.addActionListener((e) -> {
            counterAgent.stopCounting();
            stop.setEnabled(false);
            up.setEnabled(false);
            down.setEnabled(false);
        });
    }

    private class CounterAgent implements Runnable {

        private int counter;
        private volatile boolean stop;
        private volatile boolean up = true;

        @Override
        public void run() {
            while (!this.stop) {
                try {
                    // Executes the body after 10 seconds.
                    CompletableFuture.delayedExecutor(10, TimeUnit.SECONDS).execute(() -> {
                        stopCounting();
                        
                        // Updating the GUI.
                        try {
                            SwingUtilities.invokeAndWait(() -> AnotherConcurrentGUI.this.stop.setEnabled(false));
                            SwingUtilities.invokeAndWait(() -> AnotherConcurrentGUI.this.up.setEnabled(false));
                            SwingUtilities.invokeAndWait(() -> AnotherConcurrentGUI.this.down.setEnabled(false));
                        } catch (final InvocationTargetException | InterruptedException e) {
                            e.printStackTrace();
                        }
                    });

                    final var nextIntegerText = Integer.toString(this.counter);
                    SwingUtilities.invokeAndWait(() -> AnotherConcurrentGUI.this.display.setText(nextIntegerText));
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
