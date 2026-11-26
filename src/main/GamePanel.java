package main;

import entity.Player;
import entity.Monster;

import javax.swing.JPanel;
import java.awt.*;
import java.util.ArrayList;
import object.OBJ_BlueHeart;

import object.SuperObject;
import tile.TileManager;

public class GamePanel extends JPanel implements Runnable {

    // ── MONSTERS ─────────────────────────────────────────────────────────────
    public ArrayList<Monster> monsters = new ArrayList<>();
    public MonsterManager monsterManager = new MonsterManager(this);

    // ── SCREEN SETTINGS ───────────────────────────────────────────────────────
    final int originalTileSize = 16;
    final int scale = 3;
    public final int tileSize     = originalTileSize * scale;
    public final int maxScreenCol = 19;
    public final int maxScreenRow = 15;
    public final int screenWidth  = tileSize * maxScreenCol;
    public final int screenHeight = tileSize * maxScreenRow;

    // ── WORLD SETTINGS ────────────────────────────────────────────────────────
    public int maxWorldCol;
    public int maxWorldRow;
    public int maxMap = 10;
    public int currentMap = 0;

    public static final int MAP_OUTDOOR = 0;
    public static final int MAP_INDOOR = 1;
    public static final int MAP_DUNGEON = 2;
    public static final int MAP_BOSS_ROOM = 3;

    // Boss room door control
    public boolean bossDoorClosed = false;
    public boolean bossDefeated = false;

    // ── PORTAL SYSTEM (HOUSE TRANSITIONS) ─────────────────────────────────────
    private static class Portal {
        int outdoorCol, outdoorRow;
        int indoorMapIndex;
        int spawnCol, spawnRow;
        int exitCol, exitRow;
        int returnOutdoorCol, returnOutdoorRow;
    }

    private final Portal[] portals = {
        new Portal() {{
            outdoorCol = 30;
            outdoorRow = 29;
            indoorMapIndex = 1;
            spawnCol = 12;
            spawnRow = 12;
            exitCol = 12;
            exitRow = 13;
            returnOutdoorCol = 30;
            returnOutdoorRow = 30;
        }},
        new Portal() {{
            outdoorCol = 10;
            outdoorRow = 39;
            indoorMapIndex = 2;
            spawnCol = 12;
            spawnRow = 12;
            exitCol = 12;
            exitRow = 13;
            returnOutdoorCol = 10;
            returnOutdoorRow = 40;
        }},
        // DUNGEON ENTRANCE
        new Portal() {{
            outdoorCol = 48;
            outdoorRow = 25;
            indoorMapIndex = 3;
            spawnCol = 25;
            spawnRow = 20;
            exitCol = 25;
            exitRow = 30;
            returnOutdoorCol = 48;
            returnOutdoorRow = 26;
        }}
    };

    private Portal activePortal = null;

    // ── TRANSITION / FADE ─────────────────────────────────────────────────────
    private int fadeAlpha = 0;
    private boolean fadingOut = false;
    private int pendingMap = -1;
    private int pendingSpawnCol = -1;
    private int pendingSpawnRow = -1;
    private static final int FADE_SPEED = 10;

    // ── SYSTEM ────────────────────────────────────────────────────────────────
    public TileManager tileM = new TileManager(this);
    public KeyHandler keyH = new KeyHandler();
    public MouseHandler mouseH = new MouseHandler();
    public Sound sound = new Sound();
    public CollisionChecker cChecker = new CollisionChecker(this);
    public AssetSetter aSetter = new AssetSetter(this);
    public Player player = new Player(this, keyH);
    public SuperObject[] obj = new SuperObject[20];
    public UI ui = new UI(this);
    public GameSaver gameSaver = new GameSaver(this);
    public MainMenu mainMenu;

    Thread gameThread;

    // ── GAME STATE ────────────────────────────────────────────────────────────
    public enum GameState { PLAYING, PAUSED, MENU, TRANSITION, MAIN_MENU, CUTSCENE }
    public GameState gameState = GameState.MAIN_MENU;

    // ── CONSTRUCTOR ───────────────────────────────────────────────────────────
    public GamePanel() {
        this.setPreferredSize(new Dimension(screenWidth, screenHeight));
        this.setBackground(Color.BLACK);
        this.setDoubleBuffered(true);
        this.addKeyListener(keyH);
        this.addMouseListener(mouseH);
        this.addMouseMotionListener(mouseH);
        this.addMouseWheelListener(mouseH);
        this.setFocusable(true);
        this.setFocusTraversalKeysEnabled(false);
        
        mainMenu = new MainMenu(this);
    }

    // ── SETUP ────────────────────────────────────────────────────────────────
    public void setupGame() {
        aSetter.setobject();
        monsterManager.setMonsters();
        playMusic(0);
        bossDoorClosed = false;
        bossDefeated = false;
    }
    
    public void newGame() {
        // Reset player stats
        player.maxLife = 100;
        player.life = 100;
        player.maxMana = 50;
        player.mana = 50;
        player.level = 1;
        player.exp = 0;
        player.expNeeded = 100;
        player.strength = 10;
        player.defense = 5;
        player.attackPower = 15;
        player.coins = 0;
        player.hasKey = 0;
        player.hasPotion = 0;
        player.hasSword = false;
        player.hasAxe = false;
        player.hasWoodShield = false;
        player.hasBlueShield = false;
        player.maxHeartContainers = 3;
        player.maxManaCrystals = 3;
        
        // Reset position
        player.worldX = 23 * tileSize;
        player.worldY = 21 * tileSize;
        currentMap = MAP_OUTDOOR;
        
        // Reset monsters
        monsterManager.setMonsters();
        
        // Reset objects
        aSetter.setobject();
        
        gameState = GameState.PLAYING;
    }

    public void startGameThread() {
        gameThread = new Thread(this);
        gameThread.start();
    }

    // ── GAME LOOP ─────────────────────────────────────────────────────────────
    @Override
    public void run() {
        final double drawInterval = 1_000_000_000.0 / 60;
        double delta = 0;
        long lastTime = System.nanoTime();

        while (gameThread != null) {
            long currentTime = System.nanoTime();
            delta += (currentTime - lastTime) / drawInterval;
            lastTime = currentTime;

            if (delta >= 1) {
                update();
                repaint();
                Toolkit.getDefaultToolkit().sync();
                delta--;
            }
        }
    }

    // ── UPDATE ────────────────────────────────────────────────────────────────
    public void update() {
        if (gameState == GameState.MAIN_MENU) {
            mainMenu.update();
            return;
        }
        
        if (gameState == GameState.CUTSCENE) {
            // Cutscene logic - simple timer to return to menu
            if (keyH.enterPressed || keyH.escPressed) {
                keyH.enterPressed = false;
                gameState = GameState.MAIN_MENU;
            }
            return;
        }
        
        if (keyH.escPressed) {
            keyH.escPressed = false;
            if (gameState == GameState.PLAYING) {
                gameState = GameState.PAUSED;
            } else if (gameState == GameState.PAUSED) {
                gameState = GameState.PLAYING;
            } else if (gameState == GameState.MENU) {
                gameState = GameState.PLAYING;
            }
        }

        if (keyH.mPressed) {
            keyH.mPressed = false;
            if (gameState == GameState.PLAYING) {
                gameState = GameState.MENU;
            } else if (gameState == GameState.MENU) {
                gameState = GameState.PLAYING;
            }
        }

        if (gameState == GameState.PLAYING) {
            player.update();

            if (currentMap == MAP_OUTDOOR) {
                for (int i = 0; i < monsters.size(); i++) {
                    if (monsters.get(i) != null) {
                        monsters.get(i).update();
                    }
                }
                monsterManager.updateRespawn();
            } else if (currentMap == MAP_BOSS_ROOM) {
                // Update boss only
                for (int i = 0; i < monsters.size(); i++) {
                    if (monsters.get(i) != null) {
                        monsters.get(i).update();
                    }
                }
                // Check if boss is defeated
                if (!bossDefeated && monsters.isEmpty()) {
                    bossDefeated = true;
                    bossDoorClosed = false;
                    ui.showMessage("DOORS OPENED! You found a BLUE HEART!", player.worldX, player.worldY);
                    // Spawn blue heart gem
                    for (int i = 0; i < obj.length; i++) {
                        if (obj[i] == null) {
                            OBJ_BlueHeart blueHeart = new OBJ_BlueHeart();
                            blueHeart.worldX = 25 * tileSize;
                            blueHeart.worldY = 23 * tileSize;
                            blueHeart.mapIndex = MAP_BOSS_ROOM;
                            obj[i] = blueHeart;
                            break;
                        }
                    }
                }
            }

            checkPortal();
            
        } else if (gameState == GameState.PAUSED) {
            handlePauseNavigation();
        } else if (gameState == GameState.MENU) {
            handleInventoryNavigation();
        } else if (gameState == GameState.TRANSITION) {
            updateTransition();
        }
        
        mouseH.resetMouseWheel();
    }

    // ── PORTAL / MAP TRANSITION ───────────────────────────────────────────────
    private void checkPortal() {
        int playerCol = player.worldX / tileSize;
        int playerRow = player.worldY / tileSize;

        if (currentMap == MAP_OUTDOOR) {
            for (Portal p : portals) {
                if (playerCol == p.outdoorCol && playerRow == p.outdoorRow) {
                    activePortal = p;
                    startTransition(p.indoorMapIndex, p.spawnCol, p.spawnRow);
                    return;
                }
            }
        } else if (currentMap != MAP_OUTDOOR && activePortal != null) {
            if (playerCol == activePortal.exitCol && playerRow == activePortal.exitRow) {
                if (currentMap == MAP_BOSS_ROOM && bossDoorClosed && !bossDefeated) {
                    ui.showMessage("The door is locked! Defeat the boss first!");
                    return;
                }
                startTransition(MAP_OUTDOOR, activePortal.returnOutdoorCol, activePortal.returnOutdoorRow);
                activePortal = null;
            }
        }
    }

    private void startTransition(int targetMap, int spawnCol, int spawnRow) {
        if (gameState == GameState.TRANSITION) return;
        pendingMap = targetMap;
        pendingSpawnCol = spawnCol;
        pendingSpawnRow = spawnRow;
        fadeAlpha = 0;
        fadingOut = true;
        gameState = GameState.TRANSITION;
    }

    private void updateTransition() {
        if (fadingOut) {
            fadeAlpha += FADE_SPEED;
            if (fadeAlpha >= 255) {
                fadeAlpha = 255;
                doMapSwitch();
                fadingOut = false;
            }
        } else {
            fadeAlpha -= FADE_SPEED;
            if (fadeAlpha <= 0) {
                fadeAlpha = 0;
                gameState = GameState.PLAYING;
            }
        }
    }

    private void doMapSwitch() {
        currentMap = pendingMap;
        tileM.switchTileSet(currentMap);

        player.worldX = pendingSpawnCol * tileSize;
        player.worldY = pendingSpawnRow * tileSize;
        player.direction = (currentMap == MAP_INDOOR) ? "up" : "down";

        // Boss room logic
        if (currentMap == MAP_BOSS_ROOM) {
            bossDoorClosed = true;
            bossDefeated = false;
            // Clear existing monsters and spawn boss
            monsters.removeIf(m -> m.mapIndex == MAP_BOSS_ROOM);
            monsterManager.spawnBoss();
            playMusic(17); // Boss battle music
            ui.showMessage("The doors close behind you! Defeat the DRAGON!", player.worldX, player.worldY);
        } else {
            stopMusic();
            if (currentMap == MAP_INDOOR) {
                playMusic(1);
            } else {
                playMusic(0);
            }
        }
    }

    // ── PAUSE MENU (ESC) ──────────────────────────────────────────────────────
    private void handlePauseNavigation() {
        if (keyH.upPressed) {
            keyH.upPressed = false;
            ui.pauseIndex = (ui.pauseIndex - 1 + ui.pauseOptions.length) % ui.pauseOptions.length;
        }
        if (keyH.downPressed) {
            keyH.downPressed = false;
            ui.pauseIndex = (ui.pauseIndex + 1) % ui.pauseOptions.length;
        }
        if (keyH.enterPressed) {
            keyH.enterPressed = false;
            handlePauseSelection(ui.pauseIndex);
        }
        if (keyH.escPressed) {
            keyH.escPressed = false;
            gameState = GameState.PLAYING;
        }
        ui.updatePauseHover(mouseH.mouseX, mouseH.mouseY);
        if (mouseH.leftClick) {
            mouseH.leftClick = false;
            int clicked = ui.getPauseOptionAt(mouseH.mouseX, mouseH.mouseY);
            if (clicked >= 0) handlePauseSelection(clicked);
        }
    }

    private void handlePauseSelection(int index) {
        switch (index) {
            case 0 -> gameSaver.saveGame();
            case 1 -> gameSaver.loadGame();
            case 2 -> gameState = GameState.PLAYING;
            case 3 -> gameState = GameState.MAIN_MENU;
        }
    }

    // ── INVENTORY MENU (M) ───────────────────────────────────────────────────
    private void handleInventoryNavigation() {
        if (keyH.upPressed) {
            keyH.upPressed = false;
            if (ui.selectedSlot >= 3) ui.selectedSlot -= 3;
        }
        if (keyH.downPressed) {
            keyH.downPressed = false;
            if (ui.selectedSlot + 3 < 9) ui.selectedSlot += 3;
        }
        if (keyH.leftPressed) {
            keyH.leftPressed = false;
            if (ui.selectedSlot > 0) ui.selectedSlot--;
        }
        if (keyH.rightPressed) {
            keyH.rightPressed = false;
            if (ui.selectedSlot < 8) ui.selectedSlot++;
        }
        if (keyH.enterPressed) {
            keyH.enterPressed = false;
            useInventoryItem(ui.selectedSlot);
        }
        if (keyH.escPressed || keyH.mPressed) {
            keyH.escPressed = false;
            keyH.mPressed = false;
            gameState = GameState.PLAYING;
        }
        ui.updateInventoryHover(mouseH.mouseX, mouseH.mouseY);
        if (mouseH.leftClick) {
            mouseH.leftClick = false;
            int clicked = ui.getInventorySlotAt(mouseH.mouseX, mouseH.mouseY);
            if (clicked >= 0) {
                ui.selectedSlot = clicked;
                useInventoryItem(clicked);
            }
        }
    }

    public void useInventoryItem(int slot) {
        if (slot >= ui.inventory.size()) return;
        
        UI.InventoryItem item = ui.inventory.get(slot);
        
        switch(item.name) {
            case "Key" -> System.out.println("Key - opens doors");
            case "Potion" -> {
                if (player.life < player.maxLife) {
                    player.life = Math.min(player.life + 50, player.maxLife);
                    player.hasPotion--;
                    ui.removeItem("Potion", 1);
                    System.out.println("Used potion! HP: " + player.life);
                } else {
                    System.out.println("HP is already full!");
                }
            }
            case "Coin" -> System.out.println("Coins: " + player.coins);
            case "Sword" -> {
                if (!player.hasSword) {
                    player.hasSword = true;
                    player.attackPower += 5;
                    ui.removeItem("Sword", 1);
                    System.out.println("Equipped sword! ATK: " + player.attackPower);
                } else {
                    System.out.println("Sword already equipped!");
                }
            }
            case "Shield" -> {
                if (!player.hasWoodShield) {
                    player.hasWoodShield = true;
                    player.defense += 3;
                    ui.removeItem("Shield", 1);
                    System.out.println("Equipped shield! DEF: " + player.defense);
                } else {
                    System.out.println("Shield already equipped!");
                }
            }
            case "Axe" -> System.out.println("Axe - can cut trees");
            case "Boots" -> System.out.println("Boots - increases speed");
            case "Tent" -> {
                if (player.hasTent > 0) {
                    player.hasTent--;
                    player.life = player.maxLife;
                    player.mana = player.maxMana;
                    ui.removeItem("Tent", 1);
                    System.out.println("Used tent! HP and MP restored!");
                }
            }
        }
        ui.updateInventoryFromPlayer();
    }

    // ── DRAW ──────────────────────────────────────────────────────────────────
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        if (gameState == GameState.MAIN_MENU) {
            mainMenu.draw(g2);
            g2.dispose();
            return;
        }
        
        if (gameState == GameState.CUTSCENE) {
            drawCutscene(g2);
            g2.dispose();
            return;
        }

        tileM.draw(g2);

        for (SuperObject o : obj) {
            if (o != null && o.mapIndex == currentMap) {
                o.draw(g2, this);
            }
        }

        if (currentMap == MAP_OUTDOOR || currentMap == MAP_BOSS_ROOM) {
            for (Monster monster : monsters) {
                if (monster != null && monster.mapIndex == currentMap) {
                    monster.draw(g2);
                }
            }
        }

        player.draw(g2);
        ui.draw(g2);

        if (gameState == GameState.TRANSITION && fadeAlpha > 0) {
            g2.setColor(new Color(0, 0, 0, Math.min(fadeAlpha, 255)));
            g2.fillRect(0, 0, screenWidth, screenHeight);
        }

        g2.dispose();
    }
    
    private void drawCutscene(Graphics2D g2) {
        g2.setColor(Color.BLACK);
        g2.fillRect(0, 0, screenWidth, screenHeight);
        g2.setFont(new Font("Monospaced", Font.BOLD, 36));
        g2.setColor(Color.YELLOW);
        String text = "THANK YOU FOR PLAYING!";
        int x = screenWidth / 2 - g2.getFontMetrics().stringWidth(text) / 2;
        g2.drawString(text, x, screenHeight / 2 - 50);
        g2.setFont(new Font("Monospaced", Font.PLAIN, 24));
        g2.setColor(Color.WHITE);
        String text2 = "Little Man Adventures";
        x = screenWidth / 2 - g2.getFontMetrics().stringWidth(text2) / 2;
        g2.drawString(text2, x, screenHeight / 2);
        String text3 = "Press ENTER or ESC to return to menu";
        x = screenWidth / 2 - g2.getFontMetrics().stringWidth(text3) / 2;
        g2.drawString(text3, x, screenHeight / 2 + 100);
    }

    // ── SOUND ─────────────────────────────────────────────────────────────────
    public void playMusic(int i) {
        // soundURL lives on Sound (and Sound's constructor already loads
        // index 17 = FinalBattle.wav), so there is nothing to set here.
        sound.setMusicFile(i);
        sound.play();
        sound.loop();
    }

    public void stopMusic() { sound.stop(); }
    public void playSE(int i) { sound.setSEFile(i); sound.playSE(); }
}