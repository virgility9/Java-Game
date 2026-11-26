package entity;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import main.GamePanel;

public class Monster extends Entity {
    
    public int mapIndex = 0;
    public String name;
    public int maxLife;
    public int life;
    public int attack;
    public int defense;
    public int expReward;
    public int coinReward;
    public int speed = 2;
    
    // For respawn
    public int originalWorldX;
    public int originalWorldY;
    public String monsterType;
    
    protected int actionLockCounter = 0;
    protected int moveCounter = 0;
    protected static final int ACTION_DELAY = 60;
    // Boss (and any chasing monster) tracks the player inside this range, in pixels.
    // 240px = 5 tiles at 48px per tile.
    protected static final int PLAYER_TRACKING_RANGE = 240;
    
    public Monster(GamePanel gp) {
        super(gp);
        direction = "down";
        solidArea = new Rectangle(8, 16, 32, 32);
        solidAreaDefaultX = solidArea.x;
        solidAreaDefaultY = solidArea.y;
    }
    
    public void setAction() {
        actionLockCounter++;
        if (actionLockCounter >= ACTION_DELAY) {
            int rand = (int)(Math.random() * 100);
            if (rand <= 25) direction = "up";
            else if (rand <= 50) direction = "down";
            else if (rand <= 75) direction = "left";
            else direction = "right";
            actionLockCounter = 0;
        }
    }

    protected int getDistanceToPlayer() {
        int dx = gp.player.worldX - worldX;
        int dy = gp.player.worldY - worldY;
        return (int) Math.sqrt(dx * dx + dy * dy);
    }

    protected void moveTowardPlayer() {
        int dx = gp.player.worldX - worldX;
        int dy = gp.player.worldY - worldY;
        if (Math.abs(dx) > Math.abs(dy)) {
            direction = dx > 0 ? "right" : "left";
        } else {
            direction = dy > 0 ? "down" : "up";
        }
    }

    protected void randomMove() {
        int rand = (int) (Math.random() * 100);
        if (rand <= 25) direction = "up";
        else if (rand <= 50) direction = "down";
        else if (rand <= 75) direction = "left";
        else direction = "right";
    }
    
    public void update() {
        setAction();
        
        // Use move counter for smoother movement
        moveCounter++;
        if (moveCounter >= 3) {
            collisionOn = false;
            gp.cChecker.checkTile(this);
            gp.cChecker.checkObject(this, false);
            
            if (!collisionOn) {
                switch(direction) {
                    case "up": worldY -= speed; break;
                    case "down": worldY += speed; break;
                    case "left": worldX -= speed; break;
                    case "right": worldX += speed; break;
                }
            }
            moveCounter = 0;
        }
        
        // Check monster-player collision for damage
        if (gp.cChecker.checkMonsterCollision(this)) {
            if (!gp.player.invincible) {
                int damage = attack - gp.player.defense;
                if (damage < 1) damage = 1;
                gp.player.life -= damage;
                gp.player.invincible = true;
                gp.player.invincibleCounter = 0;
                gp.ui.showMessage("-" + damage, worldX, worldY);
                System.out.println(name + " hit! Dealt " + damage + " damage!");
                
                // Knockback
                switch(direction) {
                    case "up": gp.player.worldY += 30; break;
                    case "down": gp.player.worldY -= 30; break;
                    case "left": gp.player.worldX += 30; break;
                    case "right": gp.player.worldX -= 30; break;
                }
            }
        }
        
        spriteCounter++;
        if (spriteCounter > 12) {
            spriteNum = (spriteNum == 1) ? 2 : 1;
            spriteCounter = 0;
        }
    }
    
    public void draw(Graphics2D g2) {
        if (direction == null) direction = "down";
        BufferedImage image = null;
        int screenX = worldX - gp.player.worldX + gp.player.screenX;
        int screenY = worldY - gp.player.worldY + gp.player.screenY;

        if (worldX + gp.tileSize > gp.player.worldX - gp.player.screenX &&
            worldX - gp.tileSize < gp.player.worldX + gp.player.screenX &&
            worldY + gp.tileSize > gp.player.worldY - gp.player.screenY &&
            worldY - gp.tileSize < gp.player.worldY + gp.player.screenY) {

            switch(direction) {
                case "up": image = (spriteNum == 1) ? up1 : up2; break;
                case "down": image = (spriteNum == 1) ? down1 : down2; break;
                case "left": image = (spriteNum == 1) ? left1 : left2; break;
                case "right": image = (spriteNum == 1) ? right1 : right2; break;
                default: image = down1;
            }

            if (image != null) {
                g2.drawImage(image, screenX, screenY, gp.tileSize, gp.tileSize, null);
            }
        }
    }
    
    public void damageMonster(int damage) {
        life -= damage;
        gp.ui.showMessage("-" + damage, worldX, worldY);
        if (life <= 0) {
            die();
        }
    }
    
    public void die() {
        gp.player.exp += expReward;
        gp.player.coins += coinReward;
        System.out.println("Defeated " + name + "! +" + expReward + " EXP, +" + coinReward + " coins");
        if (gp.player.exp >= gp.player.expNeeded) {
            gp.player.levelUp();
        }
        
        // Mark for respawn
        int col = originalWorldX / gp.tileSize;
        int row = originalWorldY / gp.tileSize;
        gp.monsterManager.markMonsterForRespawn(monsterType, col, row);
    }
}