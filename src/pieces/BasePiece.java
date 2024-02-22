package pieces;

import javafx.scene.image.ImageView;

public class BasePiece {
    private String type;
    private ImageView texture;
    private int row;
    private int col;

    protected BasePiece(String type, ImageView texture, int row, int col) {
        this.type = type;
        this.texture = texture;
        this.row = row;
        this.col = col;
    }

    // Getters and setters
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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
