package entity;

import java.io.IOException;
import javax.imageio.ImageIO;
import main.GamePanel;

public class Bat extends Monster {
    public Bat(GamePanel gp) {
        super(gp);
        name = "Bat";
        maxLife = 25;
        life = 25;
        attack = 10;
        defense = 1;
        expReward = 15;
        coinReward = 8;
        speed = 4;
        mapIndex = 0;  // FIXED - added mapIndex
        getImage();
    }
    
    public void getImage() {
        try {
            down1 = ImageIO.read(getClass().getResourceAsStream("/monsters/bat_down_1.png"));
            down2 = ImageIO.read(getClass().getResourceAsStream("/monsters/bat_down_2.png"));
            up1 = down1;
            up2 = down2;
            left1 = down1;
            left2 = down2;
            right1 = down1;
            right2 = down2;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}