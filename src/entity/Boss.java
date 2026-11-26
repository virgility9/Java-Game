package entity;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;
import main.GamePanel;

public class Boss extends Monster {
    
    private int phase = 1;
    private int attackPatternCounter = 0;
    private int originalMaxLife;
    
    // Phase 1 animations
    public BufferedImage phase1_down1, phase1_down2;
    public BufferedImage phase1_up1, phase1_up2;
    public BufferedImage phase1_left1, phase1_left2;
    public BufferedImage phase1_right1, phase1_right2;
    
    // Phase 1 attack animations
    public BufferedImage phase1_attack_down1, phase1_attack_down2;
    public BufferedImage phase1_attack_up1, phase1_attack_up2;
    public BufferedImage phase1_attack_left1, phase1_attack_left2;
    public BufferedImage phase1_attack_right1, phase1_attack_right2;
    
    // Phase 2 animations
    public BufferedImage phase2_down1, phase2_down2;
    public BufferedImage phase2_up1, phase2_up2;
    public BufferedImage phase2_left1, phase2_left2;
    public BufferedImage phase2_right1, phase2_right2;
    
    // Phase 2 attack animations
    public BufferedImage phase2_attack_down1, phase2_attack_down2;
    public BufferedImage phase2_attack_up1, phase2_attack_up2;
    public BufferedImage phase2_attack_left1, phase2_attack_left2;
    public BufferedImage phase2_attack_right1, phase2_attack_right2;
    
    public Boss(GamePanel gp) {
        super(gp);
        name = "Skeleton Lord";
        maxLife = 250;
        life = 250;
        originalMaxLife = maxLife;
        attack = 28;
        defense = 12;
        expReward = 800;
        coinReward = 500;
        speed = 1;
        mapIndex = GamePanel.MAP_BOSS_ROOM;
        monsterType = "Boss";
        // Boss sprite is drawn 2x (96px) centred on its tile, but Monster's
        // 32px hitbox only covered its middle — sword swings that visually
        // hit the boss missed. 64px box centred on the sprite instead.
        solidArea = new Rectangle(-8, -8, 64, 64);
        solidAreaDefaultX = solidArea.x;
        solidAreaDefaultY = solidArea.y;
        getPhase1Images();
        getPhase2Images();
    }
    
    public void getPhase1Images() {
        try {
            // Phase 1 - Normal movement
            phase1_down1 = ImageIO.read(getClass().getResourceAsStream("/monsters/skeletonlord_down_1.png"));
            phase1_down2 = ImageIO.read(getClass().getResourceAsStream("/monsters/skeletonlord_down_2.png"));
            phase1_up1 = ImageIO.read(getClass().getResourceAsStream("/monsters/skeletonlord_up_1.png"));
            phase1_up2 = ImageIO.read(getClass().getResourceAsStream("/monsters/skeletonlord_up_2.png"));
            phase1_left1 = ImageIO.read(getClass().getResourceAsStream("/monsters/skeletonlord_left_1.png"));
            phase1_left2 = ImageIO.read(getClass().getResourceAsStream("/monsters/skeletonlord_left_2.png"));
            phase1_right1 = ImageIO.read(getClass().getResourceAsStream("/monsters/skeletonlord_right_1.png"));
            phase1_right2 = ImageIO.read(getClass().getResourceAsStream("/monsters/skeletonlord_right_2.png"));
            
            // Phase 1 - Attack animations
            phase1_attack_down1 = ImageIO.read(getClass().getResourceAsStream("/monsters/skeletonlord_attack_down_1.png"));
            phase1_attack_down2 = ImageIO.read(getClass().getResourceAsStream("/monsters/skeletonlord_attack_down_2.png"));
            phase1_attack_up1 = ImageIO.read(getClass().getResourceAsStream("/monsters/skeletonlord_attack_up_1.png"));
            phase1_attack_up2 = ImageIO.read(getClass().getResourceAsStream("/monsters/skeletonlord_attack_up_2.png"));
            phase1_attack_left1 = ImageIO.read(getClass().getResourceAsStream("/monsters/skeletonlord_attack_left_1.png"));
            phase1_attack_left2 = ImageIO.read(getClass().getResourceAsStream("/monsters/skeletonlord_attack_left_2.png"));
            phase1_attack_right1 = ImageIO.read(getClass().getResourceAsStream("/monsters/skeletonlord_attack_right_1.png"));
            phase1_attack_right2 = ImageIO.read(getClass().getResourceAsStream("/monsters/skeletonlord_attack_right_2.png"));
            
        } catch (IOException e) {
            System.out.println("Phase 1 Boss images not found!");
            e.printStackTrace();
        }
    }
    
    public void getPhase2Images() {
        try {
            // Phase 2 - Normal movement
            phase2_down1 = ImageIO.read(getClass().getResourceAsStream("/monsters/skeletonlord_phase2_down_1.png"));
            phase2_down2 = ImageIO.read(getClass().getResourceAsStream("/monsters/skeletonlord_phase2_down_2.png"));
            phase2_up1 = ImageIO.read(getClass().getResourceAsStream("/monsters/skeletonlord_phase2_up_1.png"));
            phase2_up2 = ImageIO.read(getClass().getResourceAsStream("/monsters/skeletonlord_phase2_up_2.png"));
            phase2_left1 = ImageIO.read(getClass().getResourceAsStream("/monsters/skeletonlord_phase2_left_1.png"));
            phase2_left2 = ImageIO.read(getClass().getResourceAsStream("/monsters/skeletonlord_phase2_left_2.png"));
            phase2_right1 = ImageIO.read(getClass().getResourceAsStream("/monsters/skeletonlord_phase2_right_1.png"));
            phase2_right2 = ImageIO.read(getClass().getResourceAsStream("/monsters/skeletonlord_phase2_right_2.png"));
            
            // Phase 2 - Attack animations
            phase2_attack_down1 = ImageIO.read(getClass().getResourceAsStream("/monsters/skeletonlord_phase2_attack_down_1.png"));
            phase2_attack_down2 = ImageIO.read(getClass().getResourceAsStream("/monsters/skeletonlord_phase2_attack_down_2.png"));
            phase2_attack_up1 = ImageIO.read(getClass().getResourceAsStream("/monsters/skeletonlord_phase2_attack_up_1.png"));
            phase2_attack_up2 = ImageIO.read(getClass().getResourceAsStream("/monsters/skeletonlord_phase2_attack_up_2.png"));
            phase2_attack_left1 = ImageIO.read(getClass().getResourceAsStream("/monsters/skeletonlord_phase2_attack_left_1.png"));
            phase2_attack_left2 = ImageIO.read(getClass().getResourceAsStream("/monsters/skeletonlord_phase2_attack_left_2.png"));
            phase2_attack_right1 = ImageIO.read(getClass().getResourceAsStream("/monsters/skeletonlord_phase2_attack_right_1.png"));
            phase2_attack_right2 = ImageIO.read(getClass().getResourceAsStream("/monsters/skeletonlord_phase2_attack_right_2.png"));
            
        } catch (IOException e) {
            System.out.println("Phase 2 Boss images not found!");
            e.printStackTrace();
        }
    }
    
    @Override
    public void setAction() {
        actionLockCounter++;
        if (actionLockCounter >= ACTION_DELAY) {
            int distance = getDistanceToPlayer();
            
            if (distance < PLAYER_TRACKING_RANGE) {
                moveTowardPlayer();
            } else {
                randomMove();
            }
            actionLockCounter = 0;
        }
    }
    
    @Override
    public void update() {
        // Phase change at 50% HP
        if (life <= originalMaxLife / 2 && phase == 1) {
            phase = 2;
            attack += 12;
            speed = 2;
            System.out.println("💀 SKELETON LORD ENRAGED! Phase 2! 💀");
            gp.ui.showMessage("SKELETON LORD ENRAGED!", worldX, worldY);
        }
        
        // Special attack pattern every 120 frames in phase 2
        attackPatternCounter++;
        if (attackPatternCounter >= 120 && phase == 2) {
            if (gp.cChecker.checkMonsterCollision(this)) {
                int damage = attack + 20;
                if (!gp.player.invincible) {
                    gp.player.life -= damage;
                    gp.player.invincible = true;
                    gp.player.invincibleCounter = 0;
                    gp.ui.showMessage("💀 " + damage + " DAMAGE! 💀", worldX, worldY);
                    System.out.println("Skeleton Lord used special attack! Dealt " + damage + " damage!");
                }
            }
            attackPatternCounter = 0;
        }
        
        setAction();
        
        // Movement
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
                    case "up": gp.player.worldY += 40; break;
                    case "down": gp.player.worldY -= 40; break;
                    case "left": gp.player.worldX += 40; break;
                    case "right": gp.player.worldX -= 40; break;
                }
            }
        }
        
        spriteCounter++;
        if (spriteCounter > 12) {
            spriteNum = (spriteNum == 1) ? 2 : 1;
            spriteCounter = 0;
        }
    }
    
    @Override
    public void draw(Graphics2D g2) {
        if (direction == null) direction = "down";
        BufferedImage image = null;
        int screenX = worldX - gp.player.worldX + gp.player.screenX;
        int screenY = worldY - gp.player.worldY + gp.player.screenY;

        if (worldX + gp.tileSize > gp.player.worldX - gp.player.screenX &&
            worldX - gp.tileSize < gp.player.worldX + gp.player.screenX &&
            worldY + gp.tileSize > gp.player.worldY - gp.player.screenY &&
            worldY - gp.tileSize < gp.player.worldY + gp.player.screenY) {

            // Choose which animation set to use based on phase
            if (phase == 1) {
                // Phase 1 animations
                if (attacking) {
                    image = switch(direction) {
                        case "up" -> (spriteNum == 1) ? phase1_attack_up1 : phase1_attack_up2;
                        case "down" -> (spriteNum == 1) ? phase1_attack_down1 : phase1_attack_down2;
                        case "left" -> (spriteNum == 1) ? phase1_attack_left1 : phase1_attack_left2;
                        case "right" -> (spriteNum == 1) ? phase1_attack_right1 : phase1_attack_right2;
                        default -> (spriteNum == 1) ? phase1_attack_down1 : phase1_attack_down2;
                    };
                } else {
                    image = switch(direction) {
                        case "up" -> (spriteNum == 1) ? phase1_up1 : phase1_up2;
                        case "down" -> (spriteNum == 1) ? phase1_down1 : phase1_down2;
                        case "left" -> (spriteNum == 1) ? phase1_left1 : phase1_left2;
                        case "right" -> (spriteNum == 1) ? phase1_right1 : phase1_right2;
                        default -> (spriteNum == 1) ? phase1_down1 : phase1_down2;
                    };
                }
            } else {
                // Phase 2 animations
                if (attacking) {
                    image = switch(direction) {
                        case "up" -> (spriteNum == 1) ? phase2_attack_up1 : phase2_attack_up2;
                        case "down" -> (spriteNum == 1) ? phase2_attack_down1 : phase2_attack_down2;
                        case "left" -> (spriteNum == 1) ? phase2_attack_left1 : phase2_attack_left2;
                        case "right" -> (spriteNum == 1) ? phase2_attack_right1 : phase2_attack_right2;
                        default -> (spriteNum == 1) ? phase2_attack_down1 : phase2_attack_down2;
                    };
                } else {
                    image = switch(direction) {
                        case "up" -> (spriteNum == 1) ? phase2_up1 : phase2_up2;
                        case "down" -> (spriteNum == 1) ? phase2_down1 : phase2_down2;
                        case "left" -> (spriteNum == 1) ? phase2_left1 : phase2_left2;
                        case "right" -> (spriteNum == 1) ? phase2_right1 : phase2_right2;
                        default -> (spriteNum == 1) ? phase2_down1 : phase2_down2;
                    };
                }
            }

            if (image != null) {
                // Boss is larger - draw at 2x size
                g2.drawImage(image, screenX - gp.tileSize/2, screenY - gp.tileSize/2, 
                            gp.tileSize * 2, gp.tileSize * 2, null);
                drawHealthBar(g2, screenX - gp.tileSize / 2, screenY - gp.tileSize / 2 - 12,
                        gp.tileSize * 2, 8);
            }
        }
    }
    
    @Override
    public void damageMonster(int damage) {
        life -= damage;
        gp.ui.showMessage("-" + damage, worldX, worldY);
        System.out.println(name + " took " + damage + " damage! HP: " + life + "/" + maxLife);
        if (life <= 0) {
            die();
        }
    }
    
    @Override
    public void die() {
        gp.player.exp += expReward;
        gp.player.coins += coinReward;
        System.out.println("👑 DEFEATED THE SKELETON LORD! 👑");
        System.out.println("+" + expReward + " EXP, +" + coinReward + " Coins");
        gp.ui.showMessage("SKELETON LORD DEFEATED!", worldX, worldY);
        
        // Give a bonus level up
        gp.player.levelUp();
        
        // Remove boss from game
    }
}