package entity;

import java.io.IOException;
import javax.imageio.ImageIO;
import main.GamePanel;

public class Orc extends Monster {
    public Orc(GamePanel gp) {
        super(gp);
        name = "Orc";
        maxLife = 80;
        life = 80;
        attack = 18;
        defense = 8;
        expReward = 70;
        coinReward = 50;
        speed = 2;
        mapIndex = 0;
        getImage();
    }
    
    public void getImage() {
        try {
            down1 = ImageIO.read(getClass().getResourceAsStream("/monsters/orc_down_1.png"));
            down2 = ImageIO.read(getClass().getResourceAsStream("/monsters/orc_down_2.png"));
            up1 = ImageIO.read(getClass().getResourceAsStream("/monsters/orc_up_1.png"));
            up2 = ImageIO.read(getClass().getResourceAsStream("/monsters/orc_up_2.png"));
            left1 = ImageIO.read(getClass().getResourceAsStream("/monsters/orc_left_1.png"));
            left2 = ImageIO.read(getClass().getResourceAsStream("/monsters/orc_left_2.png"));
            right1 = ImageIO.read(getClass().getResourceAsStream("/monsters/orc_right_1.png"));
            right2 = ImageIO.read(getClass().getResourceAsStream("/monsters/orc_right_2.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}