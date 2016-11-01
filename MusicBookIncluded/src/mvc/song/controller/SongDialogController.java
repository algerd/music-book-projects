
package mvc.song.controller;

import utils.Helper;
import db.entity.Album;
import db.entity.Artist;
import db.entity.Song;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.web.HTMLEditor;
import javafx.stage.Stage;
import main.Main;
import main.Params;

public class SongDialogController implements Initializable {
    
    private Stage dialogStage;
    private Song song;
    private boolean edit;
       
    private final IntegerProperty rating = new SimpleIntegerProperty();
    private final IntegerProperty track = new SimpleIntegerProperty();
    private final IntegerProperty minute = new SimpleIntegerProperty();
    private final IntegerProperty secund = new SimpleIntegerProperty();
  
    @FXML
    private ChoiceBox<Artist> artistField;
    @FXML
    private ChoiceBox<Album> albumField;
    @FXML
    private TextField nameField;
    @FXML
    private Spinner<Integer> trackField;
    @FXML
    private Spinner<Integer> minuteSpinner;
    @FXML
    private Spinner<Integer> secundSpinner;
    @FXML
    private Spinner<Integer> ratingField;
    @FXML
    private TextArea lyricField;
    @FXML
    private TextArea commentTextArea;
                             
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Helper.initEntityChoiceBox(artistField);
        // При выборе другого артиста - обновить список альбомов в ChoiceBox
        artistField.getSelectionModel().selectedItemProperty().addListener(this::changedArtistChoiceBox);    
        
        Helper.initEntityChoiceBox(albumField); 
        Helper.initIntegerSpinner(ratingField, Params.MIN_RATING, Params.MAX_RATING, Params.MIN_RATING, true, rating);
        Helper.initIntegerSpinner(trackField, Params.MIN_TRACK, Params.MAX_TRACK, Params.MIN_TRACK, true, track);
        Helper.initIntegerSpinner(minuteSpinner, 0, 100, 0, true, minute);
        Helper.initIntegerSpinner(secundSpinner, 0, 59, 0, true, secund);
        // ограничить поле ввода указав вторым параметром предельное количество символов
        Helper.limitTextInput(nameField, 255);
        Helper.limitTextInput(lyricField, 10000); //???
        Helper.limitTextInput(commentTextArea, 10000);
             
        artistField.getItems().addAll(Main.main.getLoader().getArtistTable().getEntities());
    }
          
    /**
     * При выборе другого артиста - обновить список альбомов в ChoiceBox albumField.
     */
    private void changedArtistChoiceBox(ObservableValue<? extends Object> observable, Artist oldValue, Artist newValue) {          
        albumField.getItems().clear();
        albumField.getItems().addAll(artistField.getSelectionModel().getSelectedItem().getChildren());
        albumField.getSelectionModel().select(0);
    }
             
    /**
     * Добавить в список объектов и в бд.
     */
    @FXML
    private void handleOkButton() {
        if (isInputValid()) {
            Album album = albumField.getValue();
           
            if (album == null) {
                Artist artist = artistField.getValue();
                // проверить есть ли у артиста альбом с названием "Unknown"               
                // если есть - получить в переменную album ссылку альбома с названием Unknown
                ObservableList<Album> albums = artist.getChildren();
                for (Album alb : albums) {
                    if (alb.getName().equals("Unknown")) {
                        album = alb;
                        break;
                    }
                }
                // если нет - создать альбом с названием "Unknown"
                if (album == null) {
                    album = new Album(artist);
                    album.setName("Unknown");               
                    Main.main.getLoader().getAlbumTable().save(album);
                    System.out.println("" + album.getIdArtist());
                    artist.getChildren().add(album);              
                }
            } 
            
            if (edit && album.getId() != song.getIdAlbum()) {
                // удалить из прежнего альбома(parent) песню(child)
                song.getParent().getChildren().remove(song);
                // добавить новому альбому(parent) песню(child)
                album.getChildren().add(song);  
                // установить флаг дереву артистов - обновить дерево
                Main.main.getMainController().getIncludedExplorerController().setRefresh(true);
            }
                      
            song.setParent(album);
            song.setIdAlbum(album.getId());
            song.setName(nameField.getText());
            song.setTime(getMinute() + ":" + ((getSecund() < 10) ? "0" : "") + getSecund());
            song.setDescription(commentTextArea.getText()); 
            song.setLyric(lyricField.getText());
            song.setTrack(getTrack());
            song.setRating(getRating());
            
            if (!edit) {
                album.getChildren().add(song);
            }  
            Main.main.getLoader().getSongTable().save(song);
            dialogStage.close();
            edit = false;
        }
    }
    
    @FXML
    private void handleCancelButton() {
        dialogStage.close();
    }
    
    private boolean isInputValid() {
        String errorMessage = "";
        if (nameField.getText() == null || nameField.getText().trim().equals("")) {
            errorMessage += "Введите название песни!\n"; 
        }
        if (errorMessage.equals("")) {
            return true;
        } 
        else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.initOwner(dialogStage);
            alert.setTitle("Invalid Fields");
            alert.setHeaderText("Please correct invalid fields");
            alert.setContentText(errorMessage);           
            alert.showAndWait();           
            return false;
        }
    }
        
    public void setEditDialog(Song s) {
        edit = true;
        setAddDialog(s);
             
        nameField.setText(song.getName());
        trackField.getValueFactory().setValue(song.getTrack());
        ratingField.getValueFactory().setValue(song.getRating());
        commentTextArea.setText(song.getDescription());
        lyricField.setText(song.getLyric()); 
        
        String timeString = song.getTime();       
        if (timeString.equals("")) {
            minuteSpinner.getValueFactory().setValue(0);
            secundSpinner.getValueFactory().setValue(0);
        }
        else {
            String[] timeArray = timeString.split(":");
            minuteSpinner.getValueFactory().setValue(Integer.parseInt(timeArray[0]));
            secundSpinner.getValueFactory().setValue(Integer.parseInt(timeArray[1]));
        }      
    } 
    
    public void setAddDialog(Song s) {
        song = s;
        Album album = song.getParent();
        if (album == null) {
            // id = 1 "Unknown" альбом "Unknown" артиста (id = 1)
            album = Main.main.getLoader().getAlbumTable().getEntityWithId(1);
        }
        Artist artist = album.getParent();
        artistField.getSelectionModel().select(artist);
        albumField.getSelectionModel().select(album);
    }
    
    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }
    
    public int getTrack() {
        return track.get();
    }
    public void setTrack(int value) {
        track.set(value);
    }
    public IntegerProperty trackProperty() {
        return track;
    }
       
    public int getRating() {
        return rating.get();
    }
    public void setRating(int value) {
        rating.set(value);
    }
    public IntegerProperty ratingProperty() {
        return rating;
    }
    
    public int getMinute() {
        return minute.get();
    }
    public void setMinute(int value) {
        minute.set(value);
    }
    public IntegerProperty minuteProperty() {
        return minute;
    }
    
    public int getSecund() {
        return secund.get();
    }
    public void setSecund(int value) {
        secund.set(value);
    }
    public IntegerProperty secundProperty() {
        return secund;
    }
         
}
