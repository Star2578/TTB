package logic.effect;

import javafx.animation.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.util.Duration;
import utils.Config;

public class PopupMaker {

    protected static final int POPUP_WIDTH = 50;
    protected static final int POPUP_HEIGHT = 20;
    protected static final int DURATION = 5000;
    public static final String DAMAGE_COLOR = "#f4f4f4";
    public static final String HEAL_COLOR = "#3ac718";
    public static final String BUFF_COLOR = "#19B3DA";

//    public static PopupManager instance;

    public PopupMaker(){
    }

//    public static PopupManager getInstance(){
//        if(instance == null){
//            instance = new PopupManager();
//        }
//        return instance;
//    }

    /*
        piece - popup display at this piece
        popupConfig - config of the popup
     */

    public static void createPopup(int row, int col , PopupConfig popupConfig){

        double posX = col * Config.SQUARE_SIZE + popupConfig.offsetX ;
        double posY = row * Config.SQUARE_SIZE - popupConfig.offsetY ;

        //new Popup object
        Popup newPopup = new Popup( POPUP_WIDTH * popupConfig.scale , POPUP_HEIGHT * popupConfig.scale );
        newPopup.setLayoutX( posX );
        newPopup.setLayoutY( posY );
        newPopup.setSpacing(4);

        //setup transition
        TranslateTransition movingUp = new TranslateTransition();
        movingUp.setNode( newPopup );
        movingUp.setByY( - popupConfig.distance );
        movingUp.setInterpolator( Interpolator.EASE_OUT );
        movingUp.setDuration( Duration.millis(popupConfig.duration) );

        FadeTransition fadeTransition = new FadeTransition();
        fadeTransition.setNode( newPopup );
        fadeTransition.setInterpolator(Interpolator.EASE_IN);
        fadeTransition.setDuration( Duration.millis(popupConfig.duration) );
        fadeTransition.setToValue(0);

        //setup text
        Text text = new Text("");
        //setup image
        StackPane iconContainer = new StackPane(); //contain image
        if( !(popupConfig.image == null) ){
            ImageView image = new ImageView(popupConfig.image);
            image.setPreserveRatio(true);
            image.setFitWidth(popupConfig.imageSize);
            iconContainer.getChildren().add(image);
        }

        newPopup.getChildren().addAll(text,iconContainer); //add text and image node to popup


        text.setText( popupConfig.text );
        text.setStyle(
                "-fx-font-family:ThaleahFat;" +
                        "-fx-fill:" + (popupConfig.colorHex) + ";" +
                        "-fx-font-size:" +(28 * popupConfig.scale)+ ";"
        );

        //display on effectPane
        EffectMaker.getInstance().effectPane.getChildren().add(newPopup);
        newPopup.toFront();
        movingUp.play();
        fadeTransition.play();

        //remove from game after it faded
        new Timeline(new KeyFrame(Duration.millis(Math.max(PopupMaker.DURATION, popupConfig.duration)) , event -> {
            EffectMaker.getInstance().effectPane.getChildren().remove(newPopup);
        })).play();
    }
}

class Popup extends HBox{

    /*
        Popup structure will be like:
            [ text | icon ]
     */

    public Popup(double width , double height){
        this.setPrefSize(width, height);
    }
}