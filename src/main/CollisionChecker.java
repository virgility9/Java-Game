package main;

import entity.Entity;
import entity.Monster;
import java.awt.Rectangle;

public class CollisionChecker {

    GamePanel gp;

    public CollisionChecker(GamePanel gp) {
        this.gp = gp;
    }

    // ── TILE COLLISION ────────────────────────────────────────────────────────

    public void checkTile(Entity entity) {
        int leftX   = entity.worldX + entity.solidArea.x;
        int rightX  = entity.worldX + entity.solidArea.x + entity.solidArea.width;
        int topY    = entity.worldY + entity.solidArea.y;
        int bottomY = entity.worldY + entity.solidArea.y + entity.solidArea.height;

        int leftCol   = leftX   / gp.tileSize;
        int rightCol  = rightX  / gp.tileSize;
        int topRow    = topY    / gp.tileSize;
        int bottomRow = bottomY / gp.tileSize;

        int t1, t2;

        switch (entity.direction) {
            case "up" -> {
                topRow = (topY - entity.speed) / gp.tileSize;
                t1 = safeGetTile(leftCol, topRow);
                t2 = safeGetTile(rightCol, topRow);
                if (isSolid(t1) || isSolid(t2)) entity.collisionOn = true;
            }
            case "down" -> {
                bottomRow = (bottomY + entity.speed) / gp.tileSize;
                t1 = safeGetTile(leftCol, bottomRow);
                t2 = safeGetTile(rightCol, bottomRow);
                if (isSolid(t1) || isSolid(t2)) entity.collisionOn = true;
            }
            case "left" -> {
                leftCol = (leftX - entity.speed) / gp.tileSize;
                t1 = safeGetTile(leftCol, topRow);
                t2 = safeGetTile(leftCol, bottomRow);
                if (isSolid(t1) || isSolid(t2)) entity.collisionOn = true;
            }
            case "right" -> {
                rightCol = (rightX + entity.speed) / gp.tileSize;
                t1 = safeGetTile(rightCol, topRow);
                t2 = safeGetTile(rightCol, bottomRow);
                if (isSolid(t1) || isSolid(t2)) entity.collisionOn = true;
            }
        }
    }

    // Safe tile lookup — returns -1 if out of bounds
    private int safeGetTile(int col, int row) {
        if (col < 0 || col >= gp.maxWorldCol || row < 0 || row >= gp.maxWorldRow) return -1;
        return gp.tileM.mapTileNum[gp.currentMap][col][row];
    }

    private boolean isSolid(int tileNum) {
        if (tileNum < 0 || tileNum >= gp.tileM.tile.length || gp.tileM.tile[tileNum] == null)
            return false;
        return gp.tileM.tile[tileNum].collision;
    }

    // ── OBJECT COLLISION ─────────────────────────────────────────────────────

    public int checkObject(Entity entity, boolean playerIs) {
        int index = -1;

        for (int i = 0; i < gp.obj.length; i++) {
            if (gp.obj[i] == null) continue;
            if (gp.obj[i].mapIndex != gp.currentMap) continue;

            Rectangle entityRect = new Rectangle(
                entity.worldX + entity.solidArea.x,
                entity.worldY + entity.solidArea.y,
                entity.solidArea.width,
                entity.solidArea.height
            );
            Rectangle objRect = new Rectangle(
                gp.obj[i].worldX + gp.obj[i].solidArea.x,
                gp.obj[i].worldY + gp.obj[i].solidArea.y,
                gp.obj[i].solidArea.width,
                gp.obj[i].solidArea.height
            );

            // Project entity forward by speed
            switch (entity.direction) {
                case "up"    -> entityRect.y -= entity.speed;
                case "down"  -> entityRect.y += entity.speed;
                case "left"  -> entityRect.x -= entity.speed;
                case "right" -> entityRect.x += entity.speed;
            }

            if (entityRect.intersects(objRect)) {
                if (gp.obj[i].collision && isMovingToward(entity, gp.obj[i])) {
                    entity.collisionOn = true;
                }
                index = i;
            }
        }
        return index;
    }

    private boolean isMovingToward(Entity entity, object.SuperObject obj) {
        int eCX = entity.worldX + entity.solidArea.x + entity.solidArea.width  / 2;
        int eCY = entity.worldY + entity.solidArea.y + entity.solidArea.height / 2;
        int oCX = obj.worldX    + obj.solidArea.x    + obj.solidArea.width     / 2;
        int oCY = obj.worldY    + obj.solidArea.y    + obj.solidArea.height    / 2;

        return switch (entity.direction) {
            case "up"    -> eCY > oCY;
            case "down"  -> eCY < oCY;
            case "left"  -> eCX > oCX;
            case "right" -> eCX < oCX;
            default -> false;
        };
    }
    
    // ── MONSTER COLLISION (ADDED - prevents walking through monsters) ─────────
    
    public boolean checkMonsterCollision(Monster monster) {
        Rectangle monsterRect = new Rectangle(
            monster.worldX + monster.solidArea.x,
            monster.worldY + monster.solidArea.y,
            monster.solidArea.width,
            monster.solidArea.height
        );

        Rectangle playerRect = new Rectangle(
            gp.player.worldX + gp.player.solidArea.x,
            gp.player.worldY + gp.player.solidArea.y,
            gp.player.solidArea.width,
            gp.player.solidArea.height
        );

        return monsterRect.intersects(playerRect);
    }
    
     
    // ── MONSTER COLLISION FOR PLAYER MOVEMENT (BLOCKS WALKING THROUGH) ────────
public void checkMonsterBlock(Entity entity) {
    for (int i = 0; i < gp.monsters.size(); i++) {
        Monster monster = gp.monsters.get(i);
        if (monster.mapIndex != gp.currentMap) continue;
        
        Rectangle entityRect = new Rectangle(
            entity.worldX + entity.solidArea.x,
            entity.worldY + entity.solidArea.y,
            entity.solidArea.width,
            entity.solidArea.height
        );
        
        // Project entity forward
        switch (entity.direction) {
            case "up"    -> entityRect.y -= entity.speed;
            case "down"  -> entityRect.y += entity.speed;
            case "left"  -> entityRect.x -= entity.speed;
            case "right" -> entityRect.x += entity.speed;
        }
        
        Rectangle monsterRect = new Rectangle(
            monster.worldX + monster.solidArea.x,
            monster.worldY + monster.solidArea.y,
            monster.solidArea.width,
            monster.solidArea.height
        );
        
        if (entityRect.intersects(monsterRect)) {
            entity.collisionOn = true;
        }
    }
}
}