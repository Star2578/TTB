package logic.effect;

import javafx.scene.image.Image;

public class PopupConfig {

    String text = "";
    String colorHex = "#f5f5f5";
    double distance = 30;
    double duration = 600;
    double scale = 1;
    double offsetX = 25;
    double offsetY = 25;
    Image image;
    int imageSize = 1;


    //determine position
    public PopupConfig(String text , String colorHex , double distance, double duration , double scale, double offsetX, double offsetY , Image image , int imageSize ) {
        this.text = text;
        this.colorHex = colorHex;
        this.distance = distance;
        this.duration = duration;
        this.scale = scale;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.image = image;
        this.imageSize = imageSize;
    }

    //determine how long it will last, how far it'll float
    public PopupConfig(String text , String colorHex, double distance, double duration , double scale, Image image , int imageSize ) {
        this.text = text;
        this.colorHex = colorHex;
        this.distance = distance;
        this.duration = duration;
        this.scale = scale;
        this.image = image;
        this.imageSize = imageSize;
    }

    //default popup
    public PopupConfig(String text , String colorHex , Image image , int imageSize ) {
        this.text = text;
        this.colorHex = colorHex;
        this.image = image;
        this.imageSize = imageSize;
    }

}
