package logic;

import javafx.geometry.Rectangle2D;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class TileMap {
    private Rectangle2D[][] tileMapGrids;
    private int cols;
    private int rows;
    private int tileWidth;
    private int tileHeight;
    private Image tileMapImage;

    public TileMap(Image tileMapImage ,int rows , int cols , int tileWidth , int tileHeight){
        this.cols=cols;
        this.rows=rows;
        this.tileWidth=tileWidth;
        this.tileHeight=tileHeight;
        this.tileMapImage=tileMapImage;

        tileMapGrids = new Rectangle2D[rows][cols];
        for(int row = 0 ; row < rows ; row++){
            for(int col = 0 ; col < cols ; col++){
                tileMapGrids[row][col] = new Rectangle2D(col*tileWidth , row*tileHeight , tileWidth , tileHeight);
            }
        }
    }

    //choose tile by specify row , col , (scale)
    public ImageView getTileAt(int row , int col , double scale){
        ImageView tile = getTileAt(row , col);
        tile.setFitWidth(tileWidth*scale);
        return tile;
    }

    public ImageView getTileAt(int row , int col){
        ImageView tile = new ImageView(tileMapImage);
        tile.setPreserveRatio(true);
        tile.setViewport(tileMapGrids[row][col]);
        return tile;
    }

}
