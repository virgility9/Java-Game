package entity;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;
import main.GamePanel;
import main.KeyHandler;

public class Player extends Entity {

    GamePanel gp;
    KeyHandler keyH;

    public final int screenX;
    public final int screenY;
    
    // Invincibility frames
    public boolean invincible = false;
    public int invincibleCounter = 0;
    
    // Coin bonus tracking
    private int lastCoinBonus = 0;

    // PLAYER STATS
    public int maxLife = 100;
    public int life = 100;
    public int maxMana = 50;
    public int mana = 50;
    public int level = 1;
    public int exp = 0;
    public int expNeeded = 100;
    public int strength = 10;
    public int defense = 5;
    public int attackPower = 15;
    public int shieldDefense = 0;

    // INVENTORY
    public int hasKey = 0;
    public int hasPotion = 0;
    public int hasTent = 0;
    public int hasLantern = 0;
    public int hasPickaxe = 0;
    public int coins = 0;

    // EQUIPMENT
    public boolean hasSword = false;
    public boolean hasWoodShield = false;
    public boolean hasBlueShield = false;
    public boolean hasBoots = false;
    public boolean hasAxe = false;

    // Chosen weapon (free selection)
    public WeaponType equippedWeapon = WeaponType.NONE;

    // HEARTS & MANA
    public int maxHeartContainers = 3;
    public int maxManaCrystals = 3;
    public int currentManaCrystals = 3;

    // Attack / block cooldown
    public int attackCooldown = 0;
    public int blockCooldown = 0;

    // Weapon-specific animation frames
    public BufferedImage axeDown1, axeDown2, axeUp1, axeUp2, axeLeft1, axeLeft2, axeRight1, axeRight2;
    public BufferedImage pickaxeDown1, pickaxeDown2, pickaxeUp1, pickaxeUp2, pickaxeLeft1, pickaxeLeft2, pickaxeRight1, pickaxeRight2;

    public Player(GamePanel gp, KeyHandler keyH) {
        super(gp);
        this.gp = gp;
        this.keyH = keyH;
        screenX = gp.screenWidth / 2 - (gp.tileSize / 2);
        screenY = gp.screenHeight / 2 - (gp.tileSize / 2);
        // Hitbox is the body only, not the whole 48px sprite: 24px wide,
        // centred, and bottom-aligned so the sprite's edges stop clipping
        // trees and a monster only hurts you when your bodies overlap.
        solidArea = new Rectangle(12, 20, 24, 28);
        solidAreaDefaultX = solidArea.x;
        solidAreaDefaultY = solidArea.y;
        setDefaultValues();
        getPlayerImage();
        getAttackImages();
        getShieldImages();
    }

    public void setDefaultValues() {
        worldX = gp.tileSize * 23;
        worldY = gp.tileSize * 21;
        speed = 4;
        direction = "down";
    }

    private BufferedImage loadImage(String path) {
        try {
            return ImageIO.read(getClass().getResourceAsStream(path));
        } catch (IOException e) {
            System.out.println("Missing image: " + path);
            return null;
        }
    }

    public void getPlayerImage() {
        up1    = loadImage("/player/boy_up_1.png");
        up2    = loadImage("/player/boy_up_2.png");
        down1  = loadImage("/player/boy_down_1.png");
        down2  = loadImage("/player/boy_down_2.png");
        left1  = loadImage("/player/boy_left_1.png");
        left2  = loadImage("/player/boy_left_2.png");
        right1 = loadImage("/player/boy_right_1.png");
        right2 = loadImage("/player/boy_right_2.png");
    }

    public void getAttackImages() {
        attackDown1  = loadImage("/player/boy_attack_down_1.png");
        attackDown2  = loadImage("/player/boy_attack_down_2.png");
        attackLeft1  = loadImage("/player/boy_attack_left_1.png");
        attackLeft2  = loadImage("/player/boy_attack_left_2.png");
        attackRight1 = loadImage("/player/boy_attack_right_1.png");
        attackRight2 = loadImage("/player/boy_attack_right_2.png");
        attackUp1    = loadImage("/player/boy_attack_up_1.png");
        attackUp2    = loadImage("/player/boy_attack_up_2.png");

        axeDown1  = loadImage("/player/boy_axe_down_1.png");
        axeDown2  = loadImage("/player/boy_axe_down_2.png");
        axeLeft1  = loadImage("/player/boy_axe_left_1.png");
        axeLeft2  = loadImage("/player/boy_axe_left_2.png");
        axeRight1 = loadImage("/player/boy_axe_right_1.png");
        axeRight2 = loadImage("/player/boy_axe_right_2.png");
        axeUp1    = loadImage("/player/boy_axe_up_1.png");
        axeUp2    = loadImage("/player/boy_axe_up_2.png");

        pickaxeDown1  = loadImage("/player/boy_pick_down_1.png");
        pickaxeDown2  = loadImage("/player/boy_pick_down_2.png");
        pickaxeLeft1  = loadImage("/player/boy_pick_left_1.png");
        pickaxeLeft2  = loadImage("/player/boy_pick_left_2.png");
        pickaxeRight1 = loadImage("/player/boy_pick_right_1.png");
        pickaxeRight2 = loadImage("/player/boy_pick_right_2.png");
        pickaxeUp1    = loadImage("/player/boy_pick_up_1.png");
        pickaxeUp2    = loadImage("/player/boy_pick_up_2.png");
    }

    public void getShieldImages() {
        blockDown  = loadImage("/player/boy_guard_down.png");
        blockLeft  = loadImage("/player/boy_guard_left.png");
        blockRight = loadImage("/player/boy_guard_right.png");
        blockUp    = loadImage("/player/boy_guard_up.png");
    }

    // ── WEAPON / SHIELD STATE ─────────────────────────────────────────────────

    public void updateWeaponType() {
        if (equippedWeapon == WeaponType.AXE && hasAxe) {
            currentWeapon = WeaponType.AXE;
            attackPower = 20;
        } else if (equippedWeapon == WeaponType.SWORD && hasSword) {
            currentWeapon = WeaponType.SWORD;
            attackPower = 15;
        } else if (equippedWeapon == WeaponType.PICKAXE && hasPickaxe > 0) {
            currentWeapon = WeaponType.PICKAXE;
            attackPower = 12;
        } else {
            if (hasAxe) {
                currentWeapon = WeaponType.AXE;
                attackPower = 20;
                equippedWeapon = WeaponType.AXE;
            } else if (hasSword) {
                currentWeapon = WeaponType.SWORD;
                attackPower = 15;
                equippedWeapon = WeaponType.SWORD;
            } else if (hasPickaxe > 0) {
                currentWeapon = WeaponType.PICKAXE;
                attackPower = 12;
                equippedWeapon = WeaponType.PICKAXE;
            } else {
                currentWeapon = WeaponType.NONE;
                attackPower = 5;
                equippedWeapon = WeaponType.NONE;
            }
        }
    }

    public void updateShieldDefense() {
        if (hasBlueShield) {
            shieldDefense = 8;
            defense = 8;
        } else if (hasWoodShield) {
            shieldDefense = 5;
            defense = 5;
        } else {
            shieldDefense = 0;
            defense = 0;
        }
    }

    // ── ATTACK FRAME SELECTOR ────────────────────────────────────────────────

    public BufferedImage getAttackFrame(int num) {
        return switch (currentWeapon) {
            case AXE -> switch (direction) {
                case "up"    -> (num == 1) ? axeUp1    : axeUp2;
                case "down"  -> (num == 1) ? axeDown1  : axeDown2;
                case "left"  -> (num == 1) ? axeLeft1  : axeLeft2;
                case "right" -> (num == 1) ? axeRight1 : axeRight2;
                default      -> (num == 1) ? axeDown1  : axeDown2;
            };
            case PICKAXE -> switch (direction) {
                case "up"    -> (num == 1) ? pickaxeUp1    : pickaxeUp2;
                case "down"  -> (num == 1) ? pickaxeDown1  : pickaxeDown2;
                case "left"  -> (num == 1) ? pickaxeLeft1  : pickaxeLeft2;
                case "right" -> (num == 1) ? pickaxeRight1 : pickaxeRight2;
                default      -> (num == 1) ? pickaxeDown1  : pickaxeDown2;
            };
            default -> switch (direction) {
                case "up"    -> (num == 1) ? attackUp1    : attackUp2;
                case "down"  -> (num == 1) ? attackDown1  : attackDown2;
                case "left"  -> (num == 1) ? attackLeft1  : attackLeft2;
                case "right" -> (num == 1) ? attackRight1 : attackRight2;
                default      -> (num == 1) ? attackDown1  : attackDown2;
            };
        };
    }

    // ── COIN BONUS CHECK ─────────────────────────────────────────────────────
    
    public void checkCoinBonus() {
        int bonusThreshold = (lastCoinBonus / 100) * 100 + 100;
        if (coins >= bonusThreshold && coins > 0) {
            lastCoinBonus = coins;
            maxLife += 5;
            life += 5;
            attackPower += 2;
            defense += 1;
            gp.ui.showMessage("COIN BONUS! +5 HP, +2 ATK, +1 DEF", worldX, worldY);
            System.out.println("💰 COIN BONUS! New stats - HP: " + maxLife + ", ATK: " + attackPower + ", DEF: " + defense);
            gp.playSE(12);
        }
    }

    // ── MAIN UPDATE ───────────────────────────────────────────────────────────

    public void update() {
        updateWeaponType();
        updateShieldDefense();
        
        checkCoinBonus();

        if (attackCooldown > 0) {
            attackCooldown--;
            if (attackCooldown == 0) {
                attacking = false;
                checkAttackHit();
            }
        }

        if (blockCooldown > 0) {
            blockCooldown--;
            if (blockCooldown == 0) blocking = false;
        }

        if (gp.mouseH.leftClick && attackCooldown == 0 && !attacking
                && currentWeapon != WeaponType.NONE) {
            gp.mouseH.leftClick = false;
            attacking = true;
            attackCooldown = 15;
            attackCounter = 0;
        }

        if (gp.mouseH.rightClick && blockCooldown == 0 && !blocking
                && (hasWoodShield || hasBlueShield)) {
            gp.mouseH.rightClick = false;
            blocking = true;
            blockCooldown = 10;
        }

        boolean moving = !attacking && !blocking
                && (keyH.upPressed || keyH.downPressed || keyH.leftPressed || keyH.rightPressed);

        if (moving) {
            if      (keyH.upPressed)    direction = "up";
            else if (keyH.downPressed)  direction = "down";
            else if (keyH.leftPressed)  direction = "left";
            else if (keyH.rightPressed) direction = "right";

            collisionOn = false;
            gp.cChecker.checkTile(this);

            int objIndex = gp.cChecker.checkObject(this, true);
            pickUpObject(objIndex);

            if (!collisionOn) {
                switch (direction) {
                    case "up"    -> worldY -= speed;
                    case "down"  -> worldY += speed;
                    case "left"  -> worldX -= speed;
                    case "right" -> worldX += speed;
                }
            }

            spriteCounter++;
            if (spriteCounter > 12) {
                spriteNum = (spriteNum == 1) ? 2 : 1;
                spriteCounter = 0;
            }

        } else if (attacking) {
            attackCounter++;
            if (attackCounter > 6) {
                spriteNum = (spriteNum == 1) ? 2 : 1;
                attackCounter = 0;
            }
        }
        
        checkMonsterCollision();
        
        if (invincible) {
            invincibleCounter++;
            if (invincibleCounter > 60) {
                invincible = false;
                invincibleCounter = 0;
            }
        }
    }

    // ── OBJECT PICKUP & INTERACTION ───────────────────────────────────────────

    public void pickUpObject(int index) {
        if (index == -1 || gp.obj[index] == null) return;

        switch (gp.obj[index].name) {

            case "Key" -> {
                hasKey++;
                gp.obj[index] = null;
                gp.playSE(11);
                System.out.println("Picked up a key! Keys: " + hasKey);
            }

            case "Door" -> {
                if (hasKey > 0) {
                    hasKey--;
                    gp.obj[index] = null;
                    gp.playSE(14);
                    System.out.println("Door unlocked! Keys remaining: " + hasKey);
                } else {
                    collisionOn = true;
                    System.out.println("You need a key to open this door!");
                }
            }

            case "Potion" -> {
                hasPotion++;
                gp.obj[index] = null;
                gp.playSE(12);
                System.out.println("Picked up a potion! Potions: " + hasPotion);
            }

            case "Sword" -> {
                if (!hasSword) {
                    hasSword = true;
                    gp.ui.addItem("Sword", 1);
                    gp.ui.updateInventoryFromPlayer();
                    gp.playSE(12);
                    System.out.println("Picked up a sword!");
                }
                gp.obj[index] = null;
            }

            case "Axe" -> {
                if (!hasAxe) {
                    hasAxe = true;
                    gp.ui.addItem("Axe", 1);
                    gp.ui.updateInventoryFromPlayer();
                    gp.playSE(12);
                    System.out.println("Picked up an axe!");
                }
                gp.obj[index] = null;
            }

            case "Pickaxe" -> {
                hasPickaxe++;
                gp.obj[index] = null;
                gp.playSE(12);
                System.out.println("Picked up a pickaxe! Pickaxes: " + hasPickaxe);
            }

            case "Blue Shield" -> {
                if (!hasBlueShield) {
                    hasBlueShield = true;
                    hasWoodShield = false;
                    gp.ui.addItem("Blue Shield", 1);
                    gp.playSE(12);
                    System.out.println("Picked up a blue shield!");
                }
                gp.obj[index] = null;
            }
            
            case "Shield" -> {
                if (!hasWoodShield && !hasBlueShield) {
                    hasWoodShield = true;
                    gp.ui.addItem("Shield", 1);
                    gp.playSE(12);
                    System.out.println("Picked up a wood shield!");
                }
                gp.obj[index] = null;
            }

            case "Tent" -> {
                hasTent++;
                gp.obj[index] = null;
                gp.playSE(12);
                System.out.println("Picked up a tent! Tents: " + hasTent);
            }

            case "Boots" -> {
                if (!hasBoots) {
                    hasBoots = true;
                    speed = 5;
                    gp.playSE(12);
                    System.out.println("Picked up boots! Speed increased.");
                }
                gp.obj[index] = null;
            }

            case "Coin" -> {
                coins++;
                gp.obj[index] = null;
                gp.playSE(10);
                System.out.println("Picked up a coin! Coins: " + coins);
            }
            
            case "Blue Heart" -> {
                coins += 1000000;
                gp.obj[index] = null;
                gp.ui.showMessage("+1,000,000 COINS!", worldX, worldY);
                gp.playSE(10);
                System.out.println("Picked up Blue Heart! +1,000,000 coins! Total: " + coins);
            }
        }
    }
    
    // ── MONSTER COLLISION ─────────────────────────────────────────────────────
    
    public void checkMonsterCollision() {
        for (int i = 0; i < gp.monsters.size(); i++) {
            Monster monster = gp.monsters.get(i);
            if (monster.mapIndex != gp.currentMap) continue;

            Rectangle playerRect = new Rectangle(
                worldX + solidArea.x,
                worldY + solidArea.y,
                solidArea.width,
                solidArea.height
            );
            Rectangle monsterRect = new Rectangle(
                monster.worldX + monster.solidArea.x,
                monster.worldY + monster.solidArea.y,
                monster.solidArea.width,
                monster.solidArea.height
            );

            if (playerRect.intersects(monsterRect) && !invincible) {
                int damage = monster.attack - defense;
                if (damage < 1) damage = 1;
                life -= damage;
                invincible = true;
                invincibleCounter = 0;
                gp.ui.showMessage("-" + damage, worldX, worldY);
                System.out.println("Took " + damage + " damage! HP: " + life);
                
                switch(direction) {
                    case "up": worldY += 30; break;
                    case "down": worldY -= 30; break;
                    case "left": worldX += 30; break;
                    case "right": worldX -= 30; break;
                }
            }
        }
    }

    // ── ATTACK HIT CHECK ─────────────────────────────────────────────────────

    public void checkAttackHit() {
        // Sword swing: 40px reach in the facing direction, 32px wide,
        // centred on the player's body — not a whole 48px tile, so a
        // monster standing diagonally beside you no longer gets hit.
        int hitX = worldX, hitY = worldY, hitW = 32, hitH = 32;
        switch (direction) {
            case "up"    -> { hitX = worldX + 8;  hitY = worldY - 40; hitW = 32; hitH = 40; }
            case "down"  -> { hitX = worldX + 8;  hitY = worldY + solidArea.y + solidArea.height; hitW = 32; hitH = 40; }
            case "left"  -> { hitX = worldX - 40; hitY = worldY + 16; hitW = 40; hitH = 32; }
            case "right" -> { hitX = worldX + solidArea.x + solidArea.width; hitY = worldY + 16; hitW = 40; hitH = 32; }
        }

        Rectangle attackArea = new Rectangle(hitX, hitY, hitW, hitH);

        for (int i = 0; i < gp.monsters.size(); i++) {
            Monster monster = gp.monsters.get(i);
            if (monster.mapIndex != gp.currentMap) continue;

            Rectangle monsterRect = new Rectangle(
                monster.worldX + monster.solidArea.x,
                monster.worldY + monster.solidArea.y,
                monster.solidArea.width,
                monster.solidArea.height
            );

            if (attackArea.intersects(monsterRect)) {
                int damage = attackPower - monster.defense;
                if (damage < 1) damage = 1;
                monster.damageMonster(damage);
                System.out.println("Hit " + monster.name + " for " + damage + " damage!");

                if (monster.life <= 0) {
                    gp.monsters.remove(i);
                    i--;
                }
                return;
            }
        }

        for (int i = 0; i < gp.obj.length; i++) {
            if (gp.obj[i] == null || !gp.obj[i].collision) continue;
            Rectangle objRect = new Rectangle(
                gp.obj[i].worldX, gp.obj[i].worldY, gp.tileSize, gp.tileSize);
            if (attackArea.intersects(objRect)) {
                System.out.println("Attack hit: " + gp.obj[i].name);
                gp.obj[i] = null;
                return;
            }
        }
        System.out.println("Attack missed.");
    }

    // ── DRAW ─────────────────────────────────────────────────────────────────

    public void draw(Graphics2D g2) {
        BufferedImage image;
        int drawX = screenX, drawY = screenY;
        int drawW = gp.tileSize, drawH = gp.tileSize;

        if (attacking) {
            image = getAttackFrame(spriteNum);
            drawH = gp.tileSize * 2;
            switch (direction) {
                case "up"    -> drawY = screenY - gp.tileSize;
                case "left"  -> { drawW = gp.tileSize * 2; drawH = gp.tileSize; drawX = screenX - gp.tileSize; }
                case "right" -> { drawW = gp.tileSize * 2; drawH = gp.tileSize; }
            }
        } else if (blocking) {
            image = switch (direction) {
                case "up"    -> blockUp;
                case "down"  -> blockDown;
                case "left"  -> blockLeft;
                case "right" -> blockRight;
                default      -> blockDown;
            };
        } else {
            image = switch (direction) {
                case "up"    -> (spriteNum == 1) ? up1    : up2;
                case "down"  -> (spriteNum == 1) ? down1  : down2;
                case "left"  -> (spriteNum == 1) ? left1  : left2;
                case "right" -> (spriteNum == 1) ? right1 : right2;
                default      -> down1;
            };
        }

        if (image != null) g2.drawImage(image, drawX, drawY, drawW, drawH, null);
    }
    
    // ── LEVEL UP ──────────────────────────────────────────────────────────────
    
    public void levelUp() {
        exp -= expNeeded;
        level++;
        expNeeded = (int)(expNeeded * 1.2);
        
        // Add 1 heart container (20 HP)
        maxLife += 20;
        life = maxLife;
        maxHeartContainers++;
        
        maxMana += 10;
        mana = maxMana;
        maxManaCrystals++;
        
        strength += 5;
        attackPower += 3;
        defense += 2;
        
        System.out.println("🎉 LEVEL UP! Now level " + level + "!");
        System.out.println("❤️ Heart container increased! Max HP: " + maxLife);
        gp.ui.showMessage("LEVEL UP! +1 Heart!", worldX, worldY);
        gp.playSE(12);
        
        // Refresh UI stat window
        gp.ui.updateInventoryFromPlayer();
    }
}