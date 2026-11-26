package main;

import java.io.*;

public class GameSaver {
    
    GamePanel gp;
    
    public GameSaver(GamePanel gp) {
        this.gp = gp;
    }
    
    public void saveGame() {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter("savegame.txt"));
            
            // Player stats
            writer.write(gp.player.maxLife + "\n");
            writer.write(gp.player.life + "\n");
            writer.write(gp.player.maxMana + "\n");
            writer.write(gp.player.mana + "\n");
            writer.write(gp.player.level + "\n");
            writer.write(gp.player.exp + "\n");
            writer.write(gp.player.expNeeded + "\n");
            writer.write(gp.player.strength + "\n");
            writer.write(gp.player.defense + "\n");
            writer.write(gp.player.attackPower + "\n");
            writer.write(gp.player.coins + "\n");
            writer.write(gp.player.hasKey + "\n");
            writer.write(gp.player.hasPotion + "\n");
            writer.write(gp.player.hasTent + "\n");
            writer.write(gp.player.hasPickaxe + "\n");
            
            // Equipment flags
            writer.write(gp.player.hasSword + "\n");
            writer.write(gp.player.hasWoodShield + "\n");
            writer.write(gp.player.hasBlueShield + "\n");
            writer.write(gp.player.hasBoots + "\n");
            writer.write(gp.player.hasAxe + "\n");
            
            // Hearts and mana
            writer.write(gp.player.maxHeartContainers + "\n");
            writer.write(gp.player.maxManaCrystals + "\n");
            writer.write(gp.player.currentManaCrystals + "\n");
            
            // Position
            writer.write(gp.player.worldX + "\n");
            writer.write(gp.player.worldY + "\n");
            writer.write(gp.currentMap + "\n");
            
            writer.close();
            gp.ui.showMessage("Game Saved!");
            System.out.println("Game saved successfully!");
        } catch (IOException e) {
            System.out.println("Save failed!");
            e.printStackTrace();
        }
    }
    
    public void loadGame() {
        try {
            BufferedReader reader = new BufferedReader(new FileReader("savegame.txt"));
            
            gp.player.maxLife = Integer.parseInt(reader.readLine());
            gp.player.life = Integer.parseInt(reader.readLine());
            gp.player.maxMana = Integer.parseInt(reader.readLine());
            gp.player.mana = Integer.parseInt(reader.readLine());
            gp.player.level = Integer.parseInt(reader.readLine());
            gp.player.exp = Integer.parseInt(reader.readLine());
            gp.player.expNeeded = Integer.parseInt(reader.readLine());
            gp.player.strength = Integer.parseInt(reader.readLine());
            gp.player.defense = Integer.parseInt(reader.readLine());
            gp.player.attackPower = Integer.parseInt(reader.readLine());
            gp.player.coins = Integer.parseInt(reader.readLine());
            gp.player.hasKey = Integer.parseInt(reader.readLine());
            gp.player.hasPotion = Integer.parseInt(reader.readLine());
            gp.player.hasTent = Integer.parseInt(reader.readLine());
            gp.player.hasPickaxe = Integer.parseInt(reader.readLine());
            
            gp.player.hasSword = Boolean.parseBoolean(reader.readLine());
            gp.player.hasWoodShield = Boolean.parseBoolean(reader.readLine());
            gp.player.hasBlueShield = Boolean.parseBoolean(reader.readLine());
            gp.player.hasBoots = Boolean.parseBoolean(reader.readLine());
            gp.player.hasAxe = Boolean.parseBoolean(reader.readLine());
            
            gp.player.maxHeartContainers = Integer.parseInt(reader.readLine());
            gp.player.maxManaCrystals = Integer.parseInt(reader.readLine());
            gp.player.currentManaCrystals = Integer.parseInt(reader.readLine());
            
            gp.player.worldX = Integer.parseInt(reader.readLine());
            gp.player.worldY = Integer.parseInt(reader.readLine());
            gp.currentMap = Integer.parseInt(reader.readLine());
            
            reader.close();
            // Rebuild the bonus components from the loaded level/coins and
            // recalculate attack/defense from them, so the loaded totals are
            // consistent with the stat formula instead of going stale.
            gp.player.restoreDerivedStats();
            gp.ui.updateInventoryFromPlayer();
            gp.ui.showMessage("Game Loaded!");
            gp.gameState = GamePanel.GameState.PLAYING;
            System.out.println("Game loaded successfully!");
        } catch (IOException e) {
            gp.ui.showMessage("No save file found!");
            System.out.println("Load failed - no save file!");
        }
    }
}