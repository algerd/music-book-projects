
package com.algerd.musicbookspringmaven.controller.album;

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
import com.algerd.musicbookspringmaven.controller.helper.inputImageBox.DialogImageBoxController;
import com.algerd.musicbookspringmaven.utils.Helper;
import com.algerd.musicbookspringmaven.entity.Album;
import com.algerd.musicbookspringmaven.entity.AlbumGenre;
import com.algerd.musicbookspringmaven.entity.Artist;
import com.algerd.musicbookspringmaven.dbDriver.Entity;
import com.algerd.musicbookspringmaven.entity.Genre;
import com.algerd.musicbookspringmaven.Params;
import com.algerd.musicbookspringmaven.controller.helper.choiceCheckBox.ChoiceCheckBoxController;

public class AlbumDialogController extends BaseDialogController {
        
    private Album album;
      
    private final IntegerProperty rating = new SimpleIntegerProperty();
    private final IntegerProperty year = new SimpleIntegerProperty();
    private final IntegerProperty minute = new SimpleIntegerProperty();
    private final IntegerProperty secund = new SimpleIntegerProperty();
    
    @FXML
    private AnchorPane view;
    @FXML
    private ChoiceBox<Artist> artistField;  
    @FXML
    private TextField nameField;
    @FXML
    private Spinner<Integer> yearField;
    @FXML
    private Spinner<Integer> minuteSpinner;
    @FXML
    private Spinner<Integer> secundSpinner;
    @FXML
    private Spinner<Integer> ratingField;
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
        Helper.initIntegerSpinner(ratingField, Params.MIN_RATING, Params.MAX_RATING, Params.MIN_RATING, true, rating);
        Helper.initIntegerSpinner(yearField, Params.MIN_YEAR, Params.MAX_YEAR, Params.MIN_YEAR, true, year);
        Helper.initIntegerSpinner(minuteSpinner, 0, 100, 0, true, minute);
        Helper.initIntegerSpinner(secundSpinner, 0, 59, 0, true, secund);     
        // ограничить поле ввода указав вторым параметром предельное количество символов
        Helper.limitTextInput(nameField, 255);
        Helper.limitTextInput(commentTextArea, 1000);
        
        artistField.getItems().addAll(repositoryService.getArtistRepository().selectAll());
        includedDialogImageBoxController.setStage(dialogStage);
        
        includedChoiceCheckBoxController.setMainPane(view);
        includedChoiceCheckBoxController.getChoiceCheckBox().setPrefWidth(250.0);
    }
    
    private void initGenreChoiceCheckBox() {
        List<Genre> artistGenres = new ArrayList<>();
        if (edit) {        
            repositoryService.getAlbumGenreRepository().selectJoinByAlbum(album).stream().forEach(artistGenre -> {
                artistGenres.add(artistGenre.getGenre());
            });
        }
        Map<Genre, ObservableValue<Boolean>> map = new HashMap<>();
        repositoryService.getGenreRepository().selectAll().stream().forEach(genre -> {                     
            map.put(genre, new SimpleBooleanProperty(artistGenres.contains(genre)));
        });
        includedChoiceCheckBoxController.addItems(map);       
    }  
            
    @FXML
    private void handleOkButton() {
        if (isInputValid()) {
            Artist artist = artistField.getValue();                         
            album.setId_artist(artist.getId());
            album.setName(nameField.getText().trim());             
            album.setTime(getMinute() + ":" + ((getSecund() < 10) ? "0" : "") + getSecund());                     
            album.setDescription(commentTextArea.getText().trim());             
            album.setYear(getYear());
            album.setRating(getRating());  
            /*
            Если альбом создаётся снуля, то сначала надо его сохранить,
            получить его id (в объекте album) и потом сохранить связку albumGenre
            */
            if (!edit) {
                repositoryService.getAlbumRepository().save(album);   
            } else {           
                // Cначала удалить все жанры из бд для артиста, а потом добавить
                repositoryService.getAlbumGenreRepository().deleteByAlbum(album);
            } 
            // Извлечь жанры из списка и сохранить их в связке связанные с альбомом
            for (Genre genre : includedChoiceCheckBoxController.getItemMap().keySet()) {
                ObservableValue<Boolean> value = includedChoiceCheckBoxController.getItemMap().get(genre);
                if (value.getValue()) {
                    AlbumGenre albumGenre = new AlbumGenre();
                    albumGenre.setId_album(album.getId());
                    albumGenre.setId_genre(genre.getId());
                    repositoryService.getAlbumGenreRepository().save(albumGenre);
                }
            }
            if (includedDialogImageBoxController.isChangedImage()) {
                includedDialogImageBoxController.saveImage();
                includedDialogImageBoxController.setChangedImage(false);                              
            }
            if (edit) {
                repositoryService.getAlbumRepository().save(album);
            } else {
                requestPageService.albumPane(album);
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
            errorMessage += "Введите название альбома!\n"; 
        }
        if (!edit && repositoryService.getAlbumRepository().containsAlbum(nameField.getText(), artistField.getValue())) {
            errorMessage += "Такой альбом уже есть у артиста!\n";
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
        nameField.setText(album.getName());
        yearField.getValueFactory().setValue(album.getYear());
        ratingField.getValueFactory().setValue(album.getRating());
        commentTextArea.setText(album.getDescription());
        
        String timeString = album.getTime();       
        if (timeString.equals("")) {
            minuteSpinner.getValueFactory().setValue(0);
            secundSpinner.getValueFactory().setValue(0);
        }
        else {
            String[] timeArray = timeString.split(":");
            minuteSpinner.getValueFactory().setValue(Integer.parseInt(timeArray[0]));
            secundSpinner.getValueFactory().setValue(Integer.parseInt(timeArray[1]));
        }       
        includedDialogImageBoxController.setEntity(album);
        includedDialogImageBoxController.setImage(includedDialogImageBoxController.getImageFile());        
    }
          
    @Override
    protected void add() {   
        Artist artist = repositoryService.getArtistRepository().selectById(album.getId_artist());         
        artistField.getSelectionModel().select(artist);
        includedDialogImageBoxController.setEntity(album);
        initGenreChoiceCheckBox();
    }
   
    @Override
    public void setEntity(Entity entity) {
        album = (Album) entity;
        super.setEntity(entity);
    }
       
    public int getYear() {
        return year.get();
    }
    public void setYear(int value) {
        year.set(value);
    }
    public IntegerProperty yearProperty() {
        return year;
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
