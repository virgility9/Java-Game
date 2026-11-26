package entity;
 
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import main.GamePanel;  // MAKE SURE THIS IMPORT EXISTS

public class Entity {
    
    public GamePanel gp;  // ADD THIS FIELD
    
    public int worldX, worldY;
    public int speed; 
    
    public BufferedImage up1, up2, down1, down2, left1, left2, right1, right2;
    public String direction;
    
    public int spriteCounter = 0;
    public int spriteNum = 1;
    
    public Rectangle solidArea;
    public boolean collisionOn = false;
    
    public int solidAreaDefaultX = 0;
    public int solidAreaDefaultY = 0;
    
    // Attack animation
    public boolean attacking = false;
    public int attackCounter = 0;
    public BufferedImage attackUp1, attackUp2, attackDown1, attackDown2, attackLeft1, attackLeft2, attackRight1, attackRight2;
    
    // Shield block animation
    public boolean blocking = false;
    public int blockCounter = 0;
    public BufferedImage blockUp, blockDown, blockLeft, blockRight;
    
    // Weapon types
    public enum WeaponType { NONE, SWORD, AXE, PICKAXE }
    public WeaponType currentWeapon = WeaponType.NONE;
    
    // Invincibility frames
    public boolean invincible = false;
    public int invincibleCounter = 0;
    
    // ADD THIS CONSTRUCTOR
    public Entity(GamePanel gp) {
        this.gp = gp;
    }
}