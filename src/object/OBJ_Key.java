package object;

import java.io.IOException;
import javax.imageio.ImageIO;

public class OBJ_Key extends SuperObject {
    
    public OBJ_Key() {
        name = "Key";
        collision = true;
        solidArea = new java.awt.Rectangle(0, 0, 48, 48);
        solidAreaDefaultX = solidArea.x;
        solidAreaDefaultY = solidArea.y;
        try {
            image = ImageIO.read(getClass().getResourceAsStream("/objects/key.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}