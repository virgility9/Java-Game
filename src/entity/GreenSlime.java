package entity;

import java.io.IOException;
import javax.imageio.ImageIO;
import main.GamePanel;

public class GreenSlime extends Monster {
    public GreenSlime(GamePanel gp) {
        super(gp);
        name = "Green Slime";
        maxLife = 30;
        life = 30;
        attack = 8;
        defense = 2;
        expReward = 20;
        coinReward = 10;
        speed = 2;
        mapIndex = 0;
        getImage();
    }
    
    public void getImage() {
        try {
            // Down
            down1 = ImageIO.read(getClass().getResourceAsStream("/monsters/greenslime_down_1.png"));
            down2 = ImageIO.read(getClass().getResourceAsStream("/monsters/greenslime_down_2.png"));
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