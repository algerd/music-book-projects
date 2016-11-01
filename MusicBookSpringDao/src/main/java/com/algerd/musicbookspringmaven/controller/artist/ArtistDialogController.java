
package com.algerd.musicbookspringmaven.controller.artist;

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
import javafx.scene.control.Spinner;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import com.algerd.musicbookspringmaven.utils.Helper;
import com.algerd.musicbookspringmaven.entity.Artist;
import com.algerd.musicbookspringmaven.entity.ArtistGenre;
import com.algerd.musicbookspringmaven.dbDriver.Entity;
import com.algerd.musicbookspringmaven.entity.Genre;
import com.algerd.musicbookspringmaven.controller.helper.inputImageBox.DialogImageBoxController;
import com.algerd.musicbookspringmaven.Params;
import com.algerd.musicbookspringmaven.controller.helper.choiceCheckBox.ChoiceCheckBoxController;

public class ArtistDialogController extends BaseDialogController {
    
    private Artist artist; 
    private final IntegerProperty rating = new SimpleIntegerProperty();
          
    @FXML
    private AnchorPane view;
    @FXML
    private TextField nameTextField;
    @FXML
    private Spinner<Integer> ratingSpinner;    
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
        Helper.initIntegerSpinner(ratingSpinner, Params.MIN_RATING, Params.MAX_RATING, Params.MIN_RATING, true, rating);      
        Helper.limitTextInput(nameTextField, 255);
        Helper.limitTextInput(commentTextArea, 1000);
        includedDialogImageBoxController.setStage(dialogStage); 
        
        includedChoiceCheckBoxController.setMainPane(view);
        includedChoiceCheckBoxController.getChoiceCheckBox().setPrefWidth(250.0);
    }
    
    private void initGenreChoiceCheckBox() {
        List<Genre> artistGenres = new ArrayList<>();
        if (edit) {        
            repositoryService.getArtistGenreRepository().selectJoinByArtist(artist).stream().forEach(artistGenre -> {
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
            artist.setName(nameTextField.getText().trim());
            artist.setRating(getRating());
            artist.setDescription(commentTextArea.getText().trim());           
            /*
            Если артист создаётся снуля, то сначала надо его сохранить,
            получить его id (в объекте artist) и потом сохранить связку artistGenre
            */
            if (!edit) {
                repositoryService.getArtistRepository().save(artist);
            } else {           
                // Cначала удалить все жанры из бд для артиста, а потом добавить
                repositoryService.getArtistGenreRepository().deleteByArtist(artist);
            }    
            // Извлечь жанры из списка и сохранить их в связке связанные с артистом             
            for (Genre genre : includedChoiceCheckBoxController.getItemMap().keySet()) {
                ObservableValue<Boolean> value = includedChoiceCheckBoxController.getItemMap().get(genre);    
                if (value.getValue()) {
                    ArtistGenre artistGenre = new ArtistGenre();
                    artistGenre.setId_artist(artist.getId());
                    artistGenre.setId_genre(genre.getId());                  
                    repositoryService.getArtistGenreRepository().save(artistGenre);
                }
            }                       
            if (includedDialogImageBoxController.isChangedImage()) {
                includedDialogImageBoxController.saveImage();
                includedDialogImageBoxController.setChangedImage(false);                              
            }
            if (edit) {
                repositoryService.getArtistRepository().save(artist);
            } else {
                requestPageService.artistPane(artist);
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
        
        if (nameTextField.getText() == null || nameTextField.getText().trim().equals("")) {
            errorMessage += "Введите имя артиста!\n"; 
        }       
        if (!edit && !repositoryService.getArtistRepository().isUniqueColumnValue("name", nameTextField.getText())) {
            errorMessage += "Такой артист уже есть!\n";
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
    protected void add() {
        includedDialogImageBoxController.setEntity(artist);
        initGenreChoiceCheckBox();
    }
       
    @Override
    protected void edit() { 
        edit = true;
        nameTextField.setText(artist.getName());
        ratingSpinner.getValueFactory().setValue(artist.getRating());
        commentTextArea.setText(artist.getDescription());
        add();
        includedDialogImageBoxController.setImage(includedDialogImageBoxController.getImageFile());
    }

    @Override
    public void setEntity(Entity entity) {
        artist = (Artist) entity;
        super.setEntity(entity);
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
    
    public AnchorPane getIncludedDialogImageBox() {
        return includedDialogImageBox;
    }
    public DialogImageBoxController getIncludedDialogImageBoxController() {
        return includedDialogImageBoxController;
    } 
          
}
