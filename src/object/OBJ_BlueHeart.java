package object;

import java.io.IOException;
import javax.imageio.ImageIO;

public class OBJ_BlueHeart extends SuperObject {
    
    public OBJ_BlueHeart() {
        name = "Blue Heart";
        collision = false;
        solidArea = new java.awt.Rectangle(0, 0, 48, 48);
        solidAreaDefaultX = solidArea.x;
        solidAreaDefaultY = solidArea.y;
        try {
            image = ImageIO.read(getClass().getResourceAsStream("/objects/blueheart.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}