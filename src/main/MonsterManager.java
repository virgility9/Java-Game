package main;

import entity.GreenSlime;
import entity.RedSlime;
import entity.Bat;
import entity.Boss;
import entity.Monster;
import entity.Orc;
import java.util.ArrayList;

public class MonsterManager {
    
    GamePanel gp;
    
    // Store original spawn positions for respawning
    private ArrayList<String> spawnTypes = new ArrayList<>();
    private ArrayList<int[]> spawnPositions = new ArrayList<>();
    private ArrayList<Integer> respawnTimers = new ArrayList<>();
    
    public MonsterManager(GamePanel gp) {
        this.gp = gp;
    }
    
    public void setMonsters() {
        // Clear existing lists
        spawnTypes.clear();
        spawnPositions.clear();
        respawnTimers.clear();
        gp.monsters.clear();
        
        // Green Slime 1
        addMonsterSpawn("GreenSlime", 26, 16);
        spawnMonsterAt("GreenSlime", 26, 16);
        
        // Green Slime 2
        addMonsterSpawn("GreenSlime", 15, 30);
        spawnMonsterAt("GreenSlime", 15, 30);
        
        // Red Slime 1
        addMonsterSpawn("RedSlime", 37, 10);
        spawnMonsterAt("RedSlime", 37, 10);
        
        // Red Slime 2
        addMonsterSpawn("RedSlime", 38, 18);
        spawnMonsterAt("RedSlime", 38, 18);
        
        // Orc 1
        addMonsterSpawn("Orc", 33, 21);
        spawnMonsterAt("Orc", 33, 21);
        
        // Orc 2
        addMonsterSpawn("Orc", 36, 26);
        spawnMonsterAt("Orc", 36, 26);
        
        // Orc 3
        addMonsterSpawn("Orc", 20, 37);
        spawnMonsterAt("Orc", 20, 37);
        
        System.out.println("Spawned " + gp.monsters.size() + " monsters");
    }
    
    private void addMonsterSpawn(String type, int col, int row) {
        spawnTypes.add(type);
        spawnPositions.add(new int[]{col, row});
        respawnTimers.add(0);
    }
    
    private void spawnMonsterAt(String type, int col, int row) {
        int worldX = col * gp.tileSize;
        int worldY = row * gp.tileSize;
        
        Monster monster = null;
        
        switch(type) {
            case "GreenSlime":
                GreenSlime slime = new GreenSlime(gp);
                slime.worldX = worldX;
                slime.worldY = worldY;
                slime.mapIndex = 0;
                slime.originalWorldX = worldX;
                slime.originalWorldY = worldY;
                slime.monsterType = "GreenSlime";
                monster = slime;
                break;
            case "RedSlime":
                RedSlime redSlime = new RedSlime(gp);
                redSlime.worldX = worldX;
                redSlime.worldY = worldY;
                redSlime.mapIndex = 0;
                redSlime.originalWorldX = worldX;
                redSlime.originalWorldY = worldY;
                redSlime.monsterType = "RedSlime";
                monster = redSlime;
                break;
            case "Bat":
                Bat bat = new Bat(gp);
                bat.worldX = worldX;
                bat.worldY = worldY;
                bat.mapIndex = 0;
                bat.originalWorldX = worldX;
                bat.originalWorldY = worldY;
                bat.monsterType = "Bat";
                monster = bat;
                break;
            case "Orc":
                Orc orc = new Orc(gp);
                orc.worldX = worldX;
                orc.worldY = worldY;
                orc.mapIndex = 0;
                orc.originalWorldX = worldX;
                orc.originalWorldY = worldY;
                orc.monsterType = "Orc";
                monster = orc;
                break;
        }
        
        if (monster != null) {
            gp.monsters.add(monster);
        }
    }
    
    public void updateRespawn() {
        for (int i = 0; i < respawnTimers.size(); i++) {
            int timer = respawnTimers.get(i);
            if (timer > 0) {
                respawnTimers.set(i, timer - 1);
                if (timer - 1 == 0) {
                    // Respawn monster after 3 seconds
                    String type = spawnTypes.get(i);
                    int[] pos = spawnPositions.get(i);
                    spawnMonsterAt(type, pos[0], pos[1]);
                    System.out.println("🔄 Respawned: " + type);
                }
            }
        }
    }
    
    public void markMonsterForRespawn(String type, int col, int row) {
        for (int i = 0; i < spawnTypes.size(); i++) {
            if (spawnTypes.get(i).equals(type) && 
                spawnPositions.get(i)[0] == col && 
                spawnPositions.get(i)[1] == row) {
                respawnTimers.set(i, 180); // 3 seconds at 60 FPS (60 * 3 = 180)
                System.out.println("⏰ " + type + " will respawn in 3 seconds");
                break;
            }
        }
    }
    
    public void spawnBoss() {
    Boss boss = new Boss(gp);
    boss.worldX = 25 * gp.tileSize;
    boss.worldY = 20 * gp.tileSize;
    boss.mapIndex = GamePanel.MAP_BOSS_ROOM;
    boss.originalWorldX = boss.worldX;
    boss.originalWorldY = boss.worldY;
    boss.monsterType = "Boss";
    gp.monsters.add(boss);
    System.out.println("🐉 DRAGON BOSS SPAWNED! 🐉");
}
    
}