package main;


import object.OBJ_Door;
import object.OBJ_Key;
import object.OBJ_Sword;
import object.OBJ_Shield;

public class AssetSetter {
    GamePanel gp;

    public AssetSetter(GamePanel gp) {
        this.gp = gp;
    }

    public void setobject() {

        // ── OUTDOOR MAP (mapIndex = 0) ────────────────────────────────────────

        gp.obj[0] = new OBJ_Key();
        gp.obj[0].worldX = 40 * gp.tileSize;
        gp.obj[0].worldY = 8  * gp.tileSize;
        gp.obj[0].mapIndex = 0;

        gp.obj[1] = new OBJ_Door();
        gp.obj[1].worldX = 12 * gp.tileSize;
        gp.obj[1].worldY = 12 * gp.tileSize;
        gp.obj[1].mapIndex = 0;

        gp.obj[2] = new OBJ_Key();
        gp.obj[2].worldX = 20 * gp.tileSize;
        gp.obj[2].worldY = 20 * gp.tileSize;
        gp.obj[2].mapIndex = 0;

        gp.obj[4] = new OBJ_Door();
        gp.obj[4].worldX = 28 * gp.tileSize;
        gp.obj[4].worldY = 31 * gp.tileSize;
        gp.obj[4].mapIndex = 0;

        gp.obj[5] = new OBJ_Door();
        gp.obj[5].worldX = 11 * gp.tileSize;
        gp.obj[5].worldY = 41 * gp.tileSize;
        gp.obj[5].mapIndex = 0;

        // ── INDOOR MAP (mapIndex = 1) ─────────────────────────────────────────

        // Open indoor1.txt in your tile editor and find the floor tiles
        // where you want the sword and shield to appear.

        gp.obj[6] = new OBJ_Sword();
        gp.obj[6].worldX = 10 * gp.tileSize; // ← adjust column
        gp.obj[6].worldY = 7  * gp.tileSize; // ← adjust row
        gp.obj[6].mapIndex = 1;

        gp.obj[7] = new OBJ_Shield();
        gp.obj[7].worldX = 14 * gp.tileSize; // ← adjust column
        gp.obj[7].worldY = 12 * gp.tileSize; // ← adjust row
        gp.obj[7].mapIndex = 2;
    }
}