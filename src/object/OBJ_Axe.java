package object;

import java.io.IOException;
import javax.imageio.ImageIO;

public class OBJ_Axe extends SuperObject {
    
    public OBJ_Axe() {
        name = "Axe";
        collision = true;
        solidArea = new java.awt.Rectangle(0, 0, 48, 48);
        solidAreaDefaultX = solidArea.x;
        solidAreaDefaultY = solidArea.y;
        try {
            image = ImageIO.read(getClass().getResourceAsStream("/objects/axe.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}