import java.awt.Menu;
import javafx.application.Application;
import javafx.beans.value.ObservableValue;
import javafx.scene.Scene;
import javafx.scene.media.Media;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.util.Duration;

import java.io.File;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.event.EventHandler;
import javafx.scene.control.Menu.*;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;

import javafx.scene.control.Slider;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;

/** Example of playing all audio files in a given directory. */
public class mediaplayer extends Application {
  final Label currentlyPlaying = new Label();
  private MediaView mediaView;
  private Duration duration;
    private Slider timeSlider;
    private Label playTime;
    private Slider volumeSlider;
    private HBox mediaBar,bar;
  public static void main(String[] args) throws Exception { launch(args); }

  @Override
  public void start(final Stage stage) throws Exception {
    stage.setTitle("Media Player");

    // determine the source directory for the playlist (either the first parameter to the program or a 
    final List<String> params = getParameters().getRaw();
    final File dir = (params.size() > 0)
      ? new File(params.get(0))
      : new File("F:\\my phone\\mp");
    if (!dir.exists() && dir.isDirectory()) {
      System.out.println("Cannot find audio source directory: " + dir);
    }

    // create some media players.
    final List<MediaPlayer> players = new ArrayList<>();
    for (String file : dir.list((File dir1, String name) -> name.endsWith(".mp3"))) players.add(createPlayer("file:///" + (dir + "\\" + file).replace("\\", "/").replaceAll(" ", "%20")));
    if (players.isEmpty()) {
      System.out.println("No audio found in " + dir);
      return;
    }
    
    // create a view to show the mediaplayers.
     mediaView = new MediaView(players.get(0));
    final Button skip = new Button("Skip");
    final Button play = new Button("Pause");

    // play each audio file in turn.
    for (int i = 0; i < players.size(); i++) {
      final MediaPlayer player     = players.get(i);
      final MediaPlayer nextPlayer = players.get((i + 1) % players.size());
      player.setOnEndOfMedia(() -> {
          mediaView.setMediaPlayer(nextPlayer);
          nextPlayer.play();
      });
    }
    
    // allow the user to skip a track.
    skip.setOnAction((ActionEvent actionEvent) -> {
        final MediaPlayer curPlayer = mediaView.getMediaPlayer();
        MediaPlayer nextPlayer = players.get((players.indexOf(curPlayer) + 1) % players.size());
        mediaView.setMediaPlayer(nextPlayer);
        curPlayer.stop();
        nextPlayer.play();
    });

    // allow the user to play or pause a track.
    play.setOnAction((ActionEvent actionEvent) -> {
        if ("Pause".equals(play.getText())) {
            mediaView.getMediaPlayer().pause();
            play.setText("Play");
        } else {
            mediaView.getMediaPlayer().play();
            play.setText("Pause");
        }
    });
 

    // display the name of the currently playing track.
    mediaView.mediaPlayerProperty().addListener((ObservableValue<? extends MediaPlayer> observableValue, MediaPlayer oldPlayer, MediaPlayer newPlayer) -> {
        setCurrentlyPlaying(newPlayer);
    });
    
    // start playing the first track.
    mediaView.setMediaPlayer(players.get(0));
    mediaView.getMediaPlayer().play();
    setCurrentlyPlaying(mediaView.getMediaPlayer());

    // silly invisible button used as a template to get the actual preferred size of the Pause button.
    Button invisiblePause = new Button("Pause");
    invisiblePause.setVisible(false);
    play.prefHeightProperty().bind(invisiblePause.heightProperty());
    play.prefWidthProperty().bind(invisiblePause.widthProperty());
    
    
         FileChooser filechooser = new FileChooser();
        
             
    
    // layout the scene.
    BorderPane bp=new BorderPane();
     mediaBar = new HBox();
    bar = new HBox();
        mediaBar.setAlignment(Pos.CENTER);
        bar.setAlignment(Pos.CENTER);
        //mediaBar.setPadding(new Insets(5, 10, 5, 10));
       mediaBar.getChildren().add(invisiblePause);
        mediaBar.getChildren().add(play);
        bar.getChildren().add(currentlyPlaying);
        
        //menu items
         MenuItem open = new MenuItem("Open");
        javafx.scene.control.Menu File = new javafx.scene.control.Menu("File");
        javafx.scene.control.Menu Playlist = new javafx.scene.control.Menu("Playlist");
        MenuItem addPlaylist = new MenuItem("Add to Playlist");
        final MenuBar menu = new MenuBar();
        
        File.getItems().add(open);
        menu.getMenus().add(File);
        Playlist.getItems().add(addPlaylist);
        menu.getMenus().add(Playlist);
        
        
        // Add spacer
        Label spacer = new Label("   ");
        mediaBar.getChildren().add(spacer);
        mediaBar.getChildren().add(skip);
         Label spacer1 = new Label("   ");
        mediaBar.getChildren().add(spacer1);
        Label timeLabel = new Label("Time: ");
        mediaBar.getChildren().add(timeLabel);

        // Add time slider
        timeSlider = new Slider();
        HBox.setHgrow(timeSlider, Priority.ALWAYS);
        timeSlider.setMinWidth(50);
        timeSlider.setMaxWidth(Double.MAX_VALUE);
        InvalidationListener sliderChangeListener = o-> {
    Duration seekTo = Duration.seconds(timeSlider.getValue());
    mediaView.getMediaPlayer().seek(seekTo);
};
timeSlider.valueProperty().addListener(sliderChangeListener);

// Link the player's time to the slider
        mediaView.getMediaPlayer().currentTimeProperty().addListener(l-> {
    // Temporarily remove the listener on the slider, so it doesn't respond to the change in playback time
    timeSlider.valueProperty().removeListener(sliderChangeListener);

    // Keep timeText's text up to date with the slider position.
    Duration currentTime = mediaView.getMediaPlayer().getCurrentTime();
    int value = (int) currentTime.toSeconds();
    timeSlider.setValue(value);    

    // Re-add the slider listener
    timeSlider.valueProperty().addListener(sliderChangeListener);
});
        mediaBar.getChildren().add(timeSlider);

        // Add Play label
        playTime = new Label();
        playTime.setPrefWidth(130);
        playTime.setMinWidth(50);
        mediaBar.getChildren().add(playTime);

        // Add the volume label
        Label volumeLabel = new Label("Vol: ");
        mediaBar.getChildren().add(volumeLabel);

        // Add Volume slider
        volumeSlider = new Slider();
        volumeSlider.setPrefWidth(70);
        volumeSlider.setMaxWidth(Region.USE_PREF_SIZE);
        volumeSlider.setMinWidth(30);
        volumeSlider.valueProperty().addListener((Observable ov) -> {
            if (volumeSlider.isValueChanging()) {
                mediaView.getMediaPlayer().setVolume(volumeSlider.getValue() / 100.0);
            }
    });
        mediaBar.getChildren().add(volumeSlider);
        mediaBar.setStyle("-fx-background-color: #336699;");
        bp.setBottom(mediaBar);
        bp.setCenter(bar);
        bp.setTop(menu);
    Scene scene = new Scene(bp, 600, 120);
    stage.setScene(scene);
    stage.show();

  }
 
  protected void updateValues() {
  if (playTime != null && timeSlider != null && volumeSlider != null) {
     
         Duration currentTime = mediaView.getMediaPlayer().getCurrentTime();
         timeSlider.setDisable(duration.isUnknown());
         if (!timeSlider.isDisabled()
                 && duration.greaterThan(Duration.ZERO)
                 && !timeSlider.isValueChanging()) {
             timeSlider.setValue(currentTime.divide(duration).toMillis()
                     * 100.0);
         }
         if (!volumeSlider.isValueChanging()) {
             volumeSlider.setValue((int)Math.round(mediaView.getMediaPlayer().getVolume()
                     * 100));
         }
  }

  
            
        
    } 

  /** sets the currently playing label to the label of the new media player and updates the progress monitor. */
  private void setCurrentlyPlaying(final MediaPlayer newPlayer) {
    
    newPlayer.currentTimeProperty();

    String source = newPlayer.getMedia().getSource();
    source = source.substring(0, source.length() - ".mp3".length());
    source = source.substring(source.lastIndexOf("/") + 1).replaceAll("%20", " ");
    currentlyPlaying.setText("Now Playing: " + source);
  }

  /** @return a MediaPlayer for the given source which will report any errors it encounters */
  private MediaPlayer createPlayer(String aMediaSrc) {
    final MediaPlayer player = new MediaPlayer(new Media(aMediaSrc));
    player.setOnError(new Runnable() {
      @Override public void run() {
        System.out.println("Media error occurred: " + player.getError());
      }
    });
    return player;
  }
}
