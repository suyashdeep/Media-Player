/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */



import java.io.File;  
  
import javafx.application.Application;  
import javafx.scene.Group;  
import javafx.scene.media.Media;  
import javafx.scene.media.MediaPlayer;  
import javafx.scene.media.MediaView;  
import javafx.stage.Stage;  
/**
 *
 * @author Suyash
 */
public class JavaFXApplication1 extends Application {

    @Override
    public void start(Stage primaryStage) {   
        String path = "F:\\my phone\\music\\Cocktail.mp3";  
        Media media = new Media(new File(path).toURI().toString());  
        MediaPlayer mediaPlayer = new MediaPlayer(media);  
        mediaPlayer.setAutoPlay(true);  
        primaryStage.setTitle("Playing Audio");  
        primaryStage.show();  
                }
   
    public static void main(String[] args) {
        launch(args);
    }

}
