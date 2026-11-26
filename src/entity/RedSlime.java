package entity;

import java.io.IOException;
import javax.imageio.ImageIO;
import main.GamePanel;

public class RedSlime extends Monster {
    public RedSlime(GamePanel gp) {
        super(gp);
        name = "Red Slime";
        maxLife = 50;
        life = 50;
        attack = 12;
        defense = 4;
        expReward = 40;
        coinReward = 25;
        speed = 3;
        mapIndex = 0;
        getImage();
    }
    
    public void getImage() {
        try {
            // Down
            down1 = ImageIO.read(getClass().getResourceAsStream("/monsters/redslime_down_1.png"));
            down2 = ImageIO.read(getClass().getResourceAsStream("/monsters/redslime_down_2.png"));
            // Up (using down sprites since slime is same from all angles)
            up1 = down1;
            up2 = down2;
            // Left
            left1 = down1;
            left2 = down2;
            // Right
            right1 = down1;
            right2 = down2;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}