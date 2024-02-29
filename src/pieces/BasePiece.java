package pieces;

import javafx.scene.image.ImageView;
import logic.SpriteAnimation;
import utils.Config;

public class BasePiece {
    protected Config.ENTITY_TYPE type;
    protected ImageView texture;
    protected int row;
    protected int col;

    protected BasePiece(Config.ENTITY_TYPE type, ImageView texture, int row, int col) {
        this.type = type;
        this.texture = texture;
        this.row = row;
        this.col = col;
    }

    // Getters and setters
    public Config.ENTITY_TYPE getType() {
        return type;
    }

    public ImageView getTexture() {
        return texture;
    }

    public void setTexture(ImageView texture) {
        this.texture = texture;
    }

    public void setTextureByPath(String path) {
        this.texture = new ImageView(path);
    }

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public int getCol() {
        return col;
    }

    public void setCol(int col) {
        this.col = col;
    }
}
