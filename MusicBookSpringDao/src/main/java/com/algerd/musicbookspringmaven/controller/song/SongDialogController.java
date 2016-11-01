
package com.algerd.musicbookspringmaven.controller.song;

import com.algerd.musicbookspringmaven.controller.BaseDialogController;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import com.algerd.musicbookspringmaven.Params;
import com.algerd.musicbookspringmaven.controller.helper.choiceCheckBox.ChoiceCheckBoxController;
import com.algerd.musicbookspringmaven.utils.Helper;
import com.algerd.musicbookspringmaven.entity.Album;
import com.algerd.musicbookspringmaven.entity.Artist;
import com.algerd.musicbookspringmaven.dbDriver.Entity;
import com.algerd.musicbookspringmaven.entity.Genre;
import com.algerd.musicbookspringmaven.entity.Song;
import com.algerd.musicbookspringmaven.entity.SongGenre;
import com.algerd.musicbookspringmaven.controller.helper.inputImageBox.DialogImageBoxController;

public class SongDialogController extends BaseDialogController {
   
    private Song song;
       
    private final IntegerProperty rating = new SimpleIntegerProperty();
    private final IntegerProperty track = new SimpleIntegerProperty();
    private final IntegerProperty minute = new SimpleIntegerProperty();
    private final IntegerProperty secund = new SimpleIntegerProperty();
  
    @FXML
    private AnchorPane view;
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
    
    @FXML
    private AnchorPane includedDialogImageBox;
    @FXML
    private DialogImageBoxController includedDialogImageBoxController;
    @FXML   
    private AnchorPane includedChoiceCheckBox;
    @FXML
    private ChoiceCheckBoxController<Genre> includedChoiceCheckBoxController;    
                             
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
             
        artistField.getItems().addAll(repositoryService.getArtistRepository().selectAll());
        includedDialogImageBoxController.setStage(dialogStage);
        
        includedChoiceCheckBoxController.setMainPane(view);
        includedChoiceCheckBoxController.getChoiceCheckBox().setMaxWidth(250.0);
        includedChoiceCheckBoxController.getChoiceCheckBox().setPrefWidth(250.0);
    }
          
    /**
     * При выборе другого артиста - обновить список альбомов в ChoiceBox albumField.
     */
    private void changedArtistChoiceBox(ObservableValue<? extends Object> observable, Artist oldValue, Artist newValue) {          
        albumField.getItems().clear();
        Artist artist = artistField.getSelectionModel().getSelectedItem();
        albumField.getItems().addAll(repositoryService.getAlbumRepository().selectByArtist(artist));
        albumField.getSelectionModel().select(0);
    }
    
    private void initGenreChoiceCheckBox() {
        List<Genre> artistGenres = new ArrayList<>();
        if (edit) {        
            repositoryService.getSongGenreRepository().selectJoinBySong(song).stream().forEach(artistGenre -> {
                artistGenres.add(artistGenre.getGenre());
            });
        }
        Map<Genre, ObservableValue<Boolean>> map = new HashMap<>();
        repositoryService.getGenreRepository().selectAll().stream().forEach(genre -> {                     
            map.put(genre, new SimpleBooleanProperty(artistGenres.contains(genre)));
        });
        includedChoiceCheckBoxController.addItems(map);       
    } 
             
    /**
     * Добавить в список объектов и в бд.
     */
    @FXML
    private void handleOkButton() {
        if (isInputValid()) {
            Album album = albumField.getValue();             
            
            song.setId_album(album.getId());
            song.setName(nameField.getText());
            song.setTime(getMinute() + ":" + ((getSecund() < 10) ? "0" : "") + getSecund());
            song.setDescription(commentTextArea.getText()); 
            song.setLyric(lyricField.getText());
            song.setTrack(getTrack());
            song.setRating(getRating());        
            /*
            Если песня создаётся снуля, то сначала надо её сохранить,
            получить её id (в объекте song) и потом сохранить связку songGenre
            */
            if (!edit) {
                repositoryService.getSongRepository().save(song);   
            } else {           
                // Cначала удалить все жанры из бд для песни, а потом добавить
                repositoryService.getSongGenreRepository().deleteBySong(song);
            } 
            // Извлечь жанры из списка и сохранить их в связке связанные с песней
            for (Genre genre : includedChoiceCheckBoxController.getItemMap().keySet()) {
                ObservableValue<Boolean> value = includedChoiceCheckBoxController.getItemMap().get(genre);
                if (value.getValue()) {
                    SongGenre songGenre = new SongGenre();
                    songGenre.setId_song(song.getId());
                    songGenre.setId_genre(genre.getId());
                    repositoryService.getSongGenreRepository().save(songGenre);
                }
            }
            if (includedDialogImageBoxController.isChangedImage()) {
                includedDialogImageBoxController.saveImage();
                includedDialogImageBoxController.setChangedImage(false);                              
            }
            if (edit) {
                repositoryService.getSongRepository().save(song);
            } else {
                requestPageService.songPane(song);
            } 
            dialogStage.close();
            edit = false;
        }
    }
    
    @FXML
    private void handleCancelButton() {
        dialogStage.close();
    }
    
    @Override
    protected boolean isInputValid() {
        String errorMessage = "";
        if (nameField.getText() == null || nameField.getText().trim().equals("")) {
            errorMessage += "Введите название песни!\n"; 
        }
        if (!edit && repositoryService.getSongRepository().containsSong(nameField.getText(), albumField.getValue())) {
            errorMessage += "Такая песня уже есть у альбома!\n";
        } 
        if (errorMessage.equals("")) {
            return true;
        } 
        else {
            errorMessage(errorMessage);
            return false;
        }
    }
        
    @Override
    protected void edit() {
        edit = true;
        add();             
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
        
        includedDialogImageBoxController.setEntity(song);
        includedDialogImageBoxController.setImage(includedDialogImageBoxController.getImageFile());  
    } 
    
    @Override
    protected void add() {    
        Album album = repositoryService.getAlbumRepository().selectById(song.getId_album());                          
        Artist artist = repositoryService.getArtistRepository().selectById(album.getId_artist());
        artistField.getSelectionModel().select(artist);
        albumField.getSelectionModel().select(album);
        includedDialogImageBoxController.setEntity(song);
        initGenreChoiceCheckBox();
    }
       
    @Override
    public void setEntity(Entity entity) {
        song = (Song) entity;
        super.setEntity(entity);
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
    
    public AnchorPane getIncludedDialogImageBox() {
        return includedDialogImageBox;
    }
    public DialogImageBoxController getIncludedDialogImageBoxController() {
        return includedDialogImageBoxController;
    } 
         
}
