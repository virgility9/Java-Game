package main;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

public class MainMenu {
    
    GamePanel gp;
    public int menuIndex = 0;
    public String[] options = {"NEW GAME", "LOAD GAME", "QUIT"};
    
    public MainMenu(GamePanel gp) {
        this.gp = gp;
    }
    
    public void update() {
        if (gp.keyH.upPressed) {
            gp.keyH.upPressed = false;
            menuIndex--;
            if (menuIndex < 0) menuIndex = options.length - 1;
        }
        if (gp.keyH.downPressed) {
            gp.keyH.downPressed = false;
            menuIndex++;
            if (menuIndex >= options.length) menuIndex = 0;
        }
        if (gp.keyH.enterPressed) {
            gp.keyH.enterPressed = false;
            selectOption();
        }
    }
    
    public void selectOption() {
        switch(menuIndex) {
            case 0:
                gp.newGame();
                break;
            case 1:
                gp.gameSaver.loadGame();
                break;
            case 2:
                System.exit(0);
                break;
        }
    }
    
    public void draw(Graphics2D g2) {
        // Background
        g2.setColor(new Color(20, 20, 40));
        g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);
        
        // Title
        g2.setFont(new Font("Monospaced", Font.BOLD, 48));
        g2.setColor(Color.YELLOW);
        String title = "LITTLE MAN ADVENTURES";
        int x = gp.screenWidth / 2 - g2.getFontMetrics().stringWidth(title) / 2;
        g2.drawString(title, x, 120);
        
        // Menu options
        g2.setFont(new Font("Monospaced", Font.PLAIN, 32));
        for (int i = 0; i < options.length; i++) {
            if (i == menuIndex) {
                g2.setColor(Color.YELLOW);
                g2.drawString("> " + options[i] + " <", gp.screenWidth / 2 - 100, 300 + i * 60);
            } else {
                g2.setColor(Color.WHITE);
                g2.drawString(options[i], gp.screenWidth / 2 - 80, 300 + i * 60);
            }
        }
        
        // Instructions
        g2.setFont(new Font("Monospaced", Font.PLAIN, 16));
        g2.setColor(Color.GRAY);
        g2.drawString("UP/DOWN: Navigate   ENTER: Select", gp.screenWidth / 2 - 180, gp.screenHeight - 80);
    }
}