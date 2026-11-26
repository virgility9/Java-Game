package tile;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import main.GamePanel;

public class TileManager {

    GamePanel gp;
    public Tile[] tile;          // active tile set (points to outdoor or indoor)
    private Tile[] outdoorTiles;
    private Tile[][] indoorTiles;           // [indoorMapIndex] -> Tile[]
    public int[][][] mapTileNum; // [mapIndex][col][row]

    public TileManager(GamePanel gp) {
        this.gp = gp;

        // ----- outdoor -----
        outdoorTiles = loadTileSet("/maps/tiledata.txt", "/tiles/");
        loadWorldSize("/maps/worldmap.txt");

        if (gp.maxMap <= 0) gp.maxMap = 10;
        mapTileNum = new int[gp.maxMap][gp.maxWorldCol][gp.maxWorldRow];
        loadMap("/maps/worldmap.txt", 0);

        // ----- indoor maps -----
        int maxIndoorMaps = 10;
        indoorTiles = new Tile[maxIndoorMaps][];

        // House 1 (map index 1)
        indoorTiles[1] = loadTileSet("/maps/indoortd.txt", "/tiles/");
        loadMap("/maps/indoor1.txt", 1);

        // House 2 (map index 2)
        indoorTiles[2] = loadTileSet("/maps/indoortd2.txt", "/tiles/");
        loadMap("/maps/indoor2.txt", 2);

        // Start on outdoor
        tile = outdoorTiles;
    }

    // Load a tile set from a tiledata file
    private Tile[] loadTileSet(String tileDataPath, String imageBasePath) {
        ArrayList<String> fileNames = new ArrayList<>();
        ArrayList<String> collisions = new ArrayList<>();

        try (InputStream is = getClass().getResourceAsStream(tileDataPath);
             BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
            String line;
            while ((line = br.readLine()) != null) {
                String col = br.readLine();
                if (col == null) break;
                fileNames.add(line.trim());
                collisions.add(col.trim());
            }
        } catch (IOException e) {
            System.out.println("Could not load tile data: " + tileDataPath);
            e.printStackTrace();
        }

        Tile[] tiles = new Tile[fileNames.size()];
        for (int i = 0; i < fileNames.size(); i++) {
            tiles[i] = loadOneTile(imageBasePath + fileNames.get(i),
                                   collisions.get(i).equals("true"));
        }
        return tiles;
    }

    private Tile loadOneTile(String path, boolean collision) {
        try (InputStream stream = getClass().getResourceAsStream(path)) {
            if (stream == null) {
                System.out.println("Missing tile: " + path);
                return null;
            }
            BufferedImage raw = ImageIO.read(stream);
            BufferedImage optimized = new BufferedImage(
                raw.getWidth(), raw.getHeight(), BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2 = optimized.createGraphics();
            g2.drawImage(raw, 0, 0, null);
            g2.dispose();

            Tile t = new Tile();
            t.image = optimized;
            t.collision = collision;
            return t;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void loadWorldSize(String mapPath) {
        try (InputStream is = getClass().getResourceAsStream(mapPath);
             BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
            String line;
            int rows = 0, cols = 0;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
                String[] tokens = line.split("\\s+");
                if (cols == 0) cols = tokens.length;
                rows++;
            }
            gp.maxWorldCol = cols;
            gp.maxWorldRow = rows;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadMap(String filePath, int mapIndex) {
        try (InputStream is = getClass().getResourceAsStream(filePath);
             BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
            int row = 0;
            String line;
            while ((line = br.readLine()) != null && row < gp.maxWorldRow) {
                line = line.trim();
                if (line.isEmpty()) continue;
                String[] tokens = line.split("\\s+");
                for (int col = 0; col < gp.maxWorldCol; col++) {
                    mapTileNum[mapIndex][col][row] =
                        (col < tokens.length) ? Integer.parseInt(tokens[col]) : 0;
                }
                row++;
            }
        } catch (Exception e) {
            System.out.println("Could not load map: " + filePath);
            e.printStackTrace();
        }
    }

    // Call this when switching maps so the right tile images are used
    public void switchTileSet(int mapIndex) {
        if (mapIndex == 0) {
            tile = outdoorTiles;
        } else if (mapIndex > 0 && mapIndex < indoorTiles.length && indoorTiles[mapIndex] != null) {
            tile = indoorTiles[mapIndex];
        } else {
            tile = outdoorTiles;
        }
    }

    public void draw(Graphics2D g2) {
        if (mapTileNum == null || tile == null) return;

        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);

        int playerTileX      = gp.player.worldX / gp.tileSize;
        int playerTileY      = gp.player.worldY / gp.tileSize;
        int playerScreenTileX = gp.player.screenX / gp.tileSize;
        int playerScreenTileY = gp.player.screenY / gp.tileSize;

        int startCol = Math.max(0, playerTileX - playerScreenTileX - 1);
        int endCol   = Math.min(gp.maxWorldCol, playerTileX + (gp.maxScreenCol - playerScreenTileX) + 1);
        int startRow = Math.max(0, playerTileY - playerScreenTileY - 1);
        int endRow   = Math.min(gp.maxWorldRow, playerTileY + (gp.maxScreenRow - playerScreenTileY) + 1);

        for (int worldRow = startRow; worldRow < endRow; worldRow++) {
            for (int worldCol = startCol; worldCol < endCol; worldCol++) {
                int tileNum = mapTileNum[gp.currentMap][worldCol][worldRow];
                if (tileNum < 0 || tileNum >= tile.length || tile[tileNum] == null) continue;

                int screenX = (worldCol * gp.tileSize) - gp.player.worldX + gp.player.screenX;
                int screenY = (worldRow * gp.tileSize) - gp.player.worldY + gp.player.screenY;
                g2.drawImage(tile[tileNum].image, screenX, screenY, gp.tileSize, gp.tileSize, null);
            }
        }
    }
}