package main;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.util.ArrayList;
import java.util.Collections;

public class UI {

    GamePanel panel;
    Graphics2D g2;
    Font fontTitle, fontNormal, fontSmall, fontStat, fontDamage;

    // ── INVENTORY MENU (M key) ────────────────────────────────────────────────
    public int selectedSlot = 0;
    private final int INV_START_X_OFFSET = 25;
    private final int INV_START_Y_OFFSET = 60;
    private final int SLOT_SIZE = 64;
    private final int SLOT_SPACING = 74;
    private int invRightX, invRightY;

    public ArrayList<InventoryItem> inventory = new ArrayList<>();

    // ── PAUSE MENU (ESC key) ─────────────────────────────────────────────────
    public int pauseIndex = 0;
    public String[] pauseOptions = {"SAVE GAME", "LOAD GAME", "EQUIPMENT", "RESUME", "QUIT"};
    private int[] pauseOptionY;
    private int pauseOptionX;
    private int pauseOptionW = 240;
    private int pauseOptionH = 45;

    // Equipment selection
    public boolean inEquipment = false;
    public int equipIndex = 0;
    public String[] weaponOptions = {"Sword", "Axe", "Pickaxe"};

    // Dialogue popup and floating messages
    public String dialogueText = "";
    public int dialogueTimer = 0;
    public int messageWorldX = 0;
    public int messageWorldY = 0;

    // Images
    BufferedImage heartFull, heartHalf, heartBlank;
    BufferedImage manaFull, manaBlank;
    BufferedImage keyIcon, potionIcon, coinIcon, swordIcon, shieldIcon, axeIcon, bootsIcon, tentIcon, blueShieldIcon;

    public UI(GamePanel panel) {
        this.panel = panel;
        fontTitle  = new Font("Monospaced", Font.BOLD,  28);
        fontNormal = new Font("Monospaced", Font.PLAIN, 16);
        fontSmall  = new Font("Monospaced", Font.PLAIN, 11);
        fontStat   = new Font("Monospaced", Font.BOLD,  14);
        fontDamage = new Font("Monospaced", Font.BOLD,  20);
        pauseOptionY = new int[pauseOptions.length];
        loadImages();
        initInventory();
    }

    public void initInventory() { }

    public void addItem(String itemName, int amount) {
        for (InventoryItem item : inventory) {
            if (item.name.equals(itemName)) {
                item.count += amount;
                sortInventory();
                return;
            }
        }
        BufferedImage icon = getIconForItem(itemName);
        inventory.add(new InventoryItem(itemName, amount, icon));
        sortInventory();
    }

    public void removeItem(String itemName, int amount) {
        for (int i = 0; i < inventory.size(); i++) {
            InventoryItem item = inventory.get(i);
            if (item.name.equals(itemName)) {
                item.count -= amount;
                if (item.count <= 0) inventory.remove(i);
                sortInventory();
                return;
            }
        }
    }

    public BufferedImage getIconForItem(String itemName) {
        switch(itemName) {
            case "Key": return keyIcon;
            case "Potion": return potionIcon;
            case "Coin": return coinIcon;
            case "Sword": return swordIcon;
            case "Shield": return shieldIcon;
            case "Blue Shield": return blueShieldIcon;
            case "Axe": return axeIcon;
            case "Boots": return bootsIcon;
            case "Tent": return tentIcon;
            default: return null;
        }
    }

    private String getEquippedWeaponName() {
        switch (panel.player.equippedWeapon) {
            case SWORD:   return "Sword";
            case AXE:     return "Axe";
            case PICKAXE: return "Pickaxe";
            default:      return "";
        }
    }

    public void sortInventory() {
        Collections.sort(inventory, (a, b) -> a.name.compareTo(b.name));
    }

    public void updateInventoryFromPlayer() {
        inventory.clear();
        if (panel.player.hasKey > 0) addItem("Key", panel.player.hasKey);
        if (panel.player.hasPotion > 0) addItem("Potion", panel.player.hasPotion);
        if (panel.player.coins > 0) addItem("Coin", panel.player.coins);
        if (panel.player.hasSword) addItem("Sword", 1);
        if (panel.player.hasWoodShield) addItem("Shield", 1);
        if (panel.player.hasBlueShield) addItem("Blue Shield", 1);  // ADDED Blue Shield
        if (panel.player.hasAxe) addItem("Axe", 1);
        if (panel.player.hasBoots) addItem("Boots", 1);
        if (panel.player.hasTent > 0) addItem("Tent", panel.player.hasTent);
        sortInventory();
    }

    // Floating message with world position (for damage numbers)
    public void showMessage(String msg, int worldX, int worldY) {
        dialogueText = msg;
        dialogueTimer = 60;
        messageWorldX = worldX;
        messageWorldY = worldY;
    }

    // Simple on-screen message
    public void showMessage(String msg) {
        dialogueText = msg;
        dialogueTimer = 120;
        messageWorldX = panel.player.worldX;
        messageWorldY = panel.player.worldY;
    }

    public void loadImages() {
        try {
            heartFull  = ImageIO.read(getClass().getResourceAsStream("/objects/heart_full.png"));
            heartHalf  = ImageIO.read(getClass().getResourceAsStream("/objects/heart_half.png"));
            heartBlank = ImageIO.read(getClass().getResourceAsStream("/objects/heart_blank.png"));
            manaFull   = ImageIO.read(getClass().getResourceAsStream("/objects/manacrystal_full.png"));
            manaBlank  = ImageIO.read(getClass().getResourceAsStream("/objects/manacrystal_blank.png"));
            keyIcon    = ImageIO.read(getClass().getResourceAsStream("/objects/key.png"));
            potionIcon = ImageIO.read(getClass().getResourceAsStream("/objects/potion_red.png"));
            coinIcon   = ImageIO.read(getClass().getResourceAsStream("/objects/coin_bronze.png"));
            swordIcon  = ImageIO.read(getClass().getResourceAsStream("/objects/sword_normal.png"));
            shieldIcon = ImageIO.read(getClass().getResourceAsStream("/objects/shield_wood.png"));
            blueShieldIcon = ImageIO.read(getClass().getResourceAsStream("/objects/shield_blue.png"));
            axeIcon    = ImageIO.read(getClass().getResourceAsStream("/objects/axe.png"));
            bootsIcon  = ImageIO.read(getClass().getResourceAsStream("/objects/boots.png"));
            tentIcon   = ImageIO.read(getClass().getResourceAsStream("/objects/tent.png"));
        } catch (Exception e) { e.printStackTrace(); }
    }

    public void draw(Graphics2D g2) {
        this.g2 = g2;
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        if (inEquipment) {
            drawEquipmentScreen();
            return;
        }

        switch (panel.gameState) {
            case PLAYING -> drawHUD();
            case MENU    -> drawInventoryMenu();
            case PAUSED  -> drawPauseMenu();
            // No UI overlay in these states: GamePanel draws the main menu
            // and cutscene itself, and TRANSITION is just the fade.
            case MAIN_MENU, CUTSCENE, TRANSITION -> {}
        }

        // Draw floating message at world position (damage numbers, etc.)
        if (dialogueTimer > 0) {
            int screenX = messageWorldX - panel.player.worldX + panel.player.screenX;
            int screenY = messageWorldY - panel.player.worldY + panel.player.screenY - 30;
            g2.setFont(fontDamage);
            int textWidth = g2.getFontMetrics().stringWidth(dialogueText);
            int textX = screenX - textWidth / 2;
            // Black outline first, so the number reads on grass, dirt and water.
            g2.setColor(Color.BLACK);
            for (int dx = -2; dx <= 2; dx++) {
                for (int dy = -2; dy <= 2; dy++) {
                    if (dx != 0 || dy != 0) g2.drawString(dialogueText, textX + dx, screenY + dy);
                }
            }
            // Damage in yellow, everything else in white.
            g2.setColor(dialogueText.startsWith("-") ? Color.YELLOW : Color.WHITE);
            g2.drawString(dialogueText, textX, screenY);
            dialogueTimer--;
        }
    }

    // ── HUD ──────────────────────────────────────────────────────────────────
    public void drawHUD() {
        drawHearts(10, 10);
        drawManaCrystals(10, 50);
        g2.setFont(fontSmall);
        g2.setColor(Color.WHITE);
        g2.drawString("Lv." + panel.player.level, 10, 95);

        g2.setColor(Color.DARK_GRAY);
        g2.fillRect(10, 105, 150, 8);
        g2.setColor(Color.CYAN);
        int expW = (int)((double) panel.player.exp / panel.player.expNeeded * 150);
        g2.fillRect(10, 105, expW, 8);

        int ix = panel.screenWidth - 100, iy = 10, is = 20;
        if (keyIcon != null && panel.player.hasKey > 0) g2.drawImage(keyIcon, ix, iy, is, is, null);
        if (panel.player.hasKey > 0) g2.drawString("x" + panel.player.hasKey, ix + 22, iy + 15);
        if (potionIcon != null && panel.player.hasPotion > 0) g2.drawImage(potionIcon, ix, iy + 25, is, is, null);
        if (panel.player.hasPotion > 0) g2.drawString("x" + panel.player.hasPotion, ix + 22, iy + 40);
        if (coinIcon != null && panel.player.coins > 0) g2.drawImage(coinIcon, ix, iy + 50, is, is, null);
        if (panel.player.coins > 0) g2.drawString("x" + panel.player.coins, ix + 22, iy + 65);

        g2.setFont(fontSmall);
        g2.setColor(Color.ORANGE);
        String weaponText = panel.player.hasAxe ? "Axe" : (panel.player.hasSword ? "Sword" : (panel.player.hasPickaxe > 0 ? "Pickaxe" : "Fists"));
        g2.drawString("Weapon: " + weaponText, panel.screenWidth - 100, 95);
        g2.setColor(Color.CYAN);
        String shieldText = panel.player.hasBlueShield ? "Blue Shield" : (panel.player.hasWoodShield ? "Wood Shield" : "None");
        g2.drawString("Shield: " + shieldText, panel.screenWidth - 100, 115);
    }

    // ── INVENTORY MENU (M key) ───────────────────────────────────────────────
    public void drawInventoryMenu() {
        updateInventoryFromPlayer();
        g2.setColor(new Color(0, 0, 0, 220));
        g2.fillRect(0, 0, panel.screenWidth, panel.screenHeight);
        g2.setColor(new Color(100, 100, 100));
        g2.drawRect(20, 20, panel.screenWidth - 40, panel.screenHeight - 40);
        g2.setFont(fontTitle);
        g2.setColor(Color.YELLOW);
        String title = "INVENTORY";
        int titleX = panel.screenWidth / 2 - g2.getFontMetrics().stringWidth(title) / 2;
        g2.drawString(title, titleX, 55);

        int leftX = 45, leftY = 80, panelW = 270;
        g2.setColor(new Color(50, 50, 50, 200));
        g2.fillRoundRect(leftX, leftY, panelW, 390, 15, 15);
        g2.setColor(Color.WHITE);
        g2.drawRoundRect(leftX, leftY, panelW, 390, 15, 15);
        g2.setFont(fontNormal);
        g2.setColor(Color.YELLOW);
        g2.drawString("STATUS", leftX + 95, leftY + 30);
        g2.setColor(Color.GRAY);
        g2.drawLine(leftX + 10, leftY + 38, leftX + panelW - 10, leftY + 38);

        int statsY = leftY + 65;
        int lineHeight = 30;
        drawStat("Level",   String.valueOf(panel.player.level),               Color.CYAN,   leftX, statsY);
        drawStat("Life",    panel.player.life + "/" + panel.player.maxLife,   Color.RED,    leftX, statsY + lineHeight);
        drawStat("Mana",    panel.player.mana + "/" + panel.player.maxMana,   Color.BLUE,   leftX, statsY + lineHeight*2);
        drawStat("Attack",  String.valueOf(panel.player.attackPower),         Color.ORANGE, leftX, statsY + lineHeight*3);
        drawStat("Defense", String.valueOf(panel.player.defense),             Color.ORANGE, leftX, statsY + lineHeight*4);
        drawStat("Exp",     panel.player.exp + "/" + panel.player.expNeeded,  Color.CYAN,   leftX, statsY + lineHeight*5);
        drawStat("Coins",   String.valueOf(panel.player.coins),               Color.YELLOW, leftX, statsY + lineHeight*6);
        drawStat("Weapon",  panel.player.hasSword ? "Sword" : "Fists",        Color.ORANGE, leftX, statsY + lineHeight*7);
        drawStat("Shield",  panel.player.hasWoodShield ? "Wood Shield" : (panel.player.hasBlueShield ? "Blue Shield" : "None"), Color.ORANGE, leftX, statsY + lineHeight*8);

        invRightX = panel.screenWidth - 340;
        invRightY = 80;
        int invW = 290;
        g2.setColor(new Color(50, 50, 50, 200));
        g2.fillRoundRect(invRightX, invRightY, invW, 390, 15, 15);
        g2.setColor(Color.WHITE);
        g2.drawRoundRect(invRightX, invRightY, invW, 390, 15, 15);
        g2.setFont(fontNormal);
        g2.setColor(Color.YELLOW);
        g2.drawString("ITEMS", invRightX + 110, invRightY + 30);
        g2.setColor(Color.GRAY);
        g2.drawLine(invRightX + 10, invRightY + 38, invRightX + invW - 10, invRightY + 38);

        String equippedName = getEquippedWeaponName();

        for (int i = 0; i < 9; i++) {
            int col = i % 3, row = i / 3;
            int sx = invRightX + INV_START_X_OFFSET + col * SLOT_SPACING;
            int sy = invRightY + INV_START_Y_OFFSET + row * SLOT_SPACING;
            if (selectedSlot == i) g2.setColor(new Color(120, 120, 220, 200));
            else g2.setColor(new Color(40, 40, 40, 180));
            g2.fillRect(sx, sy, SLOT_SIZE, SLOT_SIZE);
            g2.setColor(selectedSlot == i ? Color.CYAN : Color.WHITE);
            g2.drawRect(sx, sy, SLOT_SIZE, SLOT_SIZE);

            if (i < inventory.size()) {
                InventoryItem item = inventory.get(i);
                g2.setFont(fontSmall);
                g2.setColor(Color.YELLOW);
                String shortName = item.name.length() > 8 ? item.name.substring(0,6) : item.name;
                int nameX = sx + (SLOT_SIZE/2) - (g2.getFontMetrics().stringWidth(shortName)/2);
                g2.drawString(shortName, nameX, sy + 12);
                if (item.icon != null) {
                    int iconSize = 36;
                    int iconX = sx + (SLOT_SIZE - iconSize)/2;
                    int iconY = sy + 18;
                    g2.drawImage(item.icon, iconX, iconY, iconSize, iconSize, null);
                }
                if (item.count > 1) {
                    g2.drawString("x" + item.count, sx + SLOT_SIZE - 20, sy + SLOT_SIZE - 5);
                }

                // ★ Star on equipped weapon
                if (item.name.equals(equippedName)) {
                    g2.setColor(Color.YELLOW);
                    g2.setFont(fontSmall);
                    g2.drawString("★", sx + SLOT_SIZE - 15, sy + 15);
                }
            } else {
                g2.setFont(fontSmall);
                g2.setColor(Color.GRAY);
                g2.drawString("Empty", sx + 15, sy + 35);
            }
        }

        g2.setFont(fontSmall);
        g2.setColor(Color.GRAY);
        String hint = "[ESC/M] Close   [Enter/Click] Use   [Arrow Keys/Mouse] Navigate";
        int hintX = panel.screenWidth / 2 - g2.getFontMetrics().stringWidth(hint) / 2;
        g2.drawString(hint, hintX, panel.screenHeight - 20);
    }

    public int getInventorySlotAt(int mx, int my) {
        for (int i = 0; i < 9; i++) {
            int col = i % 3, row = i / 3;
            int sx = invRightX + INV_START_X_OFFSET + col * SLOT_SPACING;
            int sy = invRightY + INV_START_Y_OFFSET + row * SLOT_SPACING;
            if (mx >= sx && mx <= sx + SLOT_SIZE && my >= sy && my <= sy + SLOT_SIZE) return i;
        }
        return -1;
    }

    public void updateInventoryHover(int mx, int my) {
        int slot = getInventorySlotAt(mx, my);
        if (slot >= 0 && slot < inventory.size()) selectedSlot = slot;
    }

    // ── PAUSE MENU (ESC key) ─────────────────────────────────────────────────
    public void drawPauseMenu() {
        g2.setColor(new Color(0, 0, 0, 200));
        g2.fillRect(0, 0, panel.screenWidth, panel.screenHeight);
        int pw = 320, ph = 340;
        int px = panel.screenWidth / 2 - pw / 2;
        int py = panel.screenHeight / 2 - ph / 2;
        g2.setColor(new Color(40, 40, 50, 240));
        g2.fillRoundRect(px, py, pw, ph, 20, 20);
        g2.setColor(Color.WHITE);
        g2.drawRoundRect(px, py, pw, ph, 20, 20);
        g2.setFont(fontTitle);
        g2.setColor(Color.YELLOW);
        String title = "PAUSED";
        int titleX = panel.screenWidth / 2 - g2.getFontMetrics().stringWidth(title) / 2;
        g2.drawString(title, titleX, py + 55);
        g2.setColor(Color.GRAY);
        g2.drawLine(px + 20, py + 70, px + pw - 20, py + 70);

        pauseOptionX = px + (pw - pauseOptionW) / 2;
        g2.setFont(fontNormal);
        for (int i = 0; i < pauseOptions.length; i++) {
            int oy = py + 110 + i * 55;
            pauseOptionY[i] = oy - 20;
            if (pauseIndex == i) {
                g2.setColor(new Color(100, 100, 200, 220));
                g2.fillRoundRect(pauseOptionX, oy - 22, pauseOptionW, pauseOptionH, 10, 10);
                g2.setColor(Color.YELLOW);
            } else {
                g2.setColor(Color.LIGHT_GRAY);
            }
            int tx = panel.screenWidth / 2 - g2.getFontMetrics().stringWidth(pauseOptions[i]) / 2;
            g2.drawString(pauseOptions[i], tx, oy);
        }
        g2.setFont(fontSmall);
        g2.setColor(Color.GRAY);
        String hint = "";
        int hintX = panel.screenWidth / 2 - g2.getFontMetrics().stringWidth(hint) / 2;
        g2.drawString(hint, hintX, py + ph - 20);
    }

    public int getPauseOptionAt(int mx, int my) {
        for (int i = 0; i < pauseOptions.length; i++) {
            if (mx >= pauseOptionX && mx <= pauseOptionX + pauseOptionW &&
                my >= pauseOptionY[i] && my <= pauseOptionY[i] + pauseOptionH)
                return i;
        }
        return -1;
    }

    public void updatePauseHover(int mx, int my) {
        int idx = getPauseOptionAt(mx, my);
        if (idx >= 0) pauseIndex = idx;
    }

    public void drawEquipmentScreen() {
        g2.setColor(new Color(0,0,0,220));
        g2.fillRect(0,0,panel.screenWidth, panel.screenHeight);
        g2.setFont(fontTitle);
        g2.setColor(Color.YELLOW);
        String title = "EQUIP WEAPON";
        int x = panel.screenWidth/2 - g2.getFontMetrics().stringWidth(title)/2;
        g2.drawString(title, x, 80);

        g2.setFont(fontNormal);
        for (int i = 0; i < weaponOptions.length; i++) {
            int y = 180 + i * 50;
            if (i == equipIndex) {
                g2.setColor(Color.YELLOW);
                g2.drawString("> " + weaponOptions[i] + " <", panel.screenWidth/2 - 60, y);
            } else {
                g2.setColor(Color.WHITE);
                g2.drawString(weaponOptions[i], panel.screenWidth/2 - 30, y);
            }
        }
        g2.setFont(fontSmall);
        g2.setColor(Color.GRAY);
        String hint = "UP/DOWN: Navigate   ENTER: Equip   ESC: Cancel";
        int hintX = panel.screenWidth/2 - g2.getFontMetrics().stringWidth(hint)/2;
        g2.drawString(hint, hintX, panel.screenHeight - 50);
    }

    // ── HELPERS ──────────────────────────────────────────────────────────────
    private void drawStat(String label, String value, Color valueColor, int x, int y) {
        g2.setFont(fontStat);
        g2.setColor(Color.WHITE);
        g2.drawString(label, x + 20, y);
        g2.setColor(valueColor);
        g2.drawString(value, x + 175, y);
    }

    public void drawHearts(int x, int y) {
        int size = 24, spacing = 28;
        for (int i = 0; i < panel.player.maxHeartContainers; i++)
            g2.drawImage(heartBlank, x + i * spacing, y, size, size, null);
        int full = panel.player.life / 20;
        boolean half = (panel.player.life % 20) >= 10;
        for (int i = 0; i < full; i++)
            g2.drawImage(heartFull, x + i * spacing, y, size, size, null);
        if (half && full < panel.player.maxHeartContainers)
            g2.drawImage(heartHalf, x + full * spacing, y, size, size, null);
    }

    public void drawManaCrystals(int x, int y) {
        int size = 24, spacing = 28;
        for (int i = 0; i < panel.player.maxManaCrystals; i++)
            g2.drawImage(manaBlank, x + i * spacing, y, size, size, null);
        for (int i = 0; i < panel.player.currentManaCrystals; i++)
            g2.drawImage(manaFull, x + i * spacing, y, size, size, null);
    }

    class InventoryItem {
        String name;
        int count;
        BufferedImage icon;
        InventoryItem(String name, int count, BufferedImage icon) {
            this.name = name; this.count = count; this.icon = icon;
        }
    }
}