package pieces.wall;

import javafx.geometry.Rectangle2D;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import utils.Config;

public class TileMap {
    private Rectangle2D[][] tileMapGrids;
    private int cols;
    private int rows;
    private int tileWidth;
    private int tileHeight;
    private Image tileMapImage;
    private int realCols;
    private int realRows;

    public TileMap(Image tileMapImage ,int rows , int cols , int tileWidth , int tileHeight){
        this.cols=cols;
        this.rows=rows;
        this.tileWidth=tileWidth;
        this.tileHeight=tileHeight;
        this.tileMapImage=tileMapImage;
        this.realCols = (int) (tileMapImage.getWidth()/tileWidth);
        this.realRows = (int) (tileMapImage.getHeight()/tileHeight);

        tileMapGrids = new Rectangle2D[realRows][realCols];
        for(int row = 0 ; row < realRows ; row++){
            for(int col = 0 ; col < realCols ; col++){
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
        tile.setFitWidth(Config.SQUARE_SIZE);
        tile.setFitHeight(Config.SQUARE_SIZE);
        tile.setPreserveRatio(true);
        tile.setViewport(tileMapGrids[row][col]);
        return tile;
    }

}
