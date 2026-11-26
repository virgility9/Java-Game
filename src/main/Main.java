package main;

import javax.swing.JFrame;

public class Main {
    public static void main(String[] args) {
        // Must be set before any AWT/Swing initialization
        System.setProperty("sun.java2d.opengl", "true");

        JFrame window = new JFrame();
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setResizable(false);
        window.setTitle("BlueBoy Adventure Game");

        GamePanel gamePanel = new GamePanel();
        window.add(gamePanel);
        window.pack();
        window.setLocationRelativeTo(null);
        window.setVisible(true);

        gamePanel.requestFocusInWindow();
        gamePanel.setupGame();
        gamePanel.startGameThread();
    }
}