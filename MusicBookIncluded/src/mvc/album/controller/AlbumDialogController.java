
package mvc.album.controller;

import helper.inputImageBox.DialogImageBoxController;
import utils.Helper;
import db.entity.Album;
import db.entity.Artist;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import main.Main;
import main.Params;

public class AlbumDialogController implements Initializable {
    
    private Stage dialogStage;
    private Album album;
    private boolean edit;
      
    private final IntegerProperty rating = new SimpleIntegerProperty();
    private final IntegerProperty year = new SimpleIntegerProperty();
    private final IntegerProperty minute = new SimpleIntegerProperty();
    private final IntegerProperty secund = new SimpleIntegerProperty();
      
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

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Helper.initEntityChoiceBox(artistField);
        Helper.initIntegerSpinner(ratingField, Params.MIN_RATING, Params.MAX_RATING, Params.MIN_RATING, true, rating);
        Helper.initIntegerSpinner(yearField, Params.MIN_YEAR, Params.MAX_YEAR, Params.MIN_YEAR, true, year);
        Helper.initIntegerSpinner(minuteSpinner, 0, 100, 0, true, minute);
        Helper.initIntegerSpinner(secundSpinner, 0, 59, 0, true, secund);
        artistField.getItems().addAll(Main.main.getLoader().getArtistTable().getEntities());
        // ограничить поле ввода указав вторым параметром предельное количество символов
        Helper.limitTextInput(nameField, 255);
        Helper.limitTextInput(commentTextArea, 1000);
        includedDialogImageBoxController.setStage(dialogStage);
    }
            
    /**
     * Добавить в список объектов и в бд.
     */
    @FXML
    private void handleOkButton() {
        if (isInputValid()) {
            Artist artist = artistField.getValue();
            
            if (edit && artist.getId() != album.getIdArtist()) {
                // удалить из прежнего артиста(parent) альбом(child)
                album.getParent().getChildren().remove(album);
                // добавить новому артисту(parent) альбом(child)
                artist.getChildren().add(album);  
                // установить флаг дереву артистов - обновить дерево
                Main.main.getMainController().getIncludedExplorerController().setRefresh(true);
            }           
            album.setParent(artist);
            album.setIdArtist(artist.getId());
            album.setName(nameField.getText().trim());             
            album.setTime(getMinute() + ":" + ((getSecund() < 10) ? "0" : "") + getSecund());                     
            album.setDescription(commentTextArea.getText().trim());             
            album.setYear(getYear());
            album.setRating(getRating());           
            if (!edit) {               
                artist.getChildren().add(album);
            }             
            Main.main.getLoader().getAlbumTable().save(album);          
            if (includedDialogImageBoxController.isChangedImage()) {
                includedDialogImageBoxController.saveImage();
                includedDialogImageBoxController.setChangedImage(false);                              
                album.setChangedImage(!album.isChangedImage());
            }
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
            errorMessage += "Введите название альбома!\n"; 
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
      
    public void setEditDialog(Album alb) {
        edit = true;
        setAddDialog(alb);
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
          
    public void setAddDialog(Album alb) {
        album = alb;
        Artist parent = album.getParent();
        if (parent == null) {
            // id = 1 "Unknown" артист
            parent = Main.main.getLoader().getArtistTable().getEntityWithId(1);
        }
        artistField.getSelectionModel().select(parent);
        includedDialogImageBoxController.setEntity(album);
    }
   
    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
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
