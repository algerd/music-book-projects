package mvc.musician.controller;

import helper.inputImageBox.DialogImageBoxController;
import utils.Helper;
import db.entity.Musician;
import java.net.URL;
import java.time.format.DateTimeParseException;
import java.util.ResourceBundle;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import main.Main;
import main.Params;

public class MusicianDialogController implements Initializable {
    
    private Stage dialogStage;
    private Musician musician;
    private boolean edit;
    
    private final IntegerProperty rating = new SimpleIntegerProperty();
             
    @FXML
    private TextField nameTextField;
    @FXML
    private ChoiceBox<String> typeChoiceBox;
    @FXML
    private DatePicker dobDatePicker;
    @FXML
    private DatePicker dodDatePicker;
    @FXML
    private TextField countryTextField;
    @FXML
    private Spinner<Integer> ratingSpinner;
    @FXML
    private TextArea commentTextArea;
    
    @FXML
    private AnchorPane includedDialogImageBox;
    @FXML
    private DialogImageBoxController includedDialogImageBoxController;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Helper.initIntegerSpinner(ratingSpinner, Params.MIN_RATING, Params.MAX_RATING, Params.MIN_RATING, true, rating);
        Helper.initDatePicker(dobDatePicker, true);
        Helper.initDatePicker(dodDatePicker, true);  
        // ограничить поле ввода указав вторым параметром предельное количество символов
        Helper.limitTextInput(nameTextField, 255);
        Helper.limitTextInput(countryTextField, 255);
        Helper.limitTextInput(commentTextArea, 1000);
        includedDialogImageBoxController.setStage(dialogStage);
    } 
     
    @FXML
    private void handleOkButton() {      
        if (isInputValid()) {
            musician.setName(nameTextField.getText().trim());
            musician.setType(typeChoiceBox.getValue());
            musician.setDateOfBirth(dobDatePicker.getEditor().getText());
            musician.setDateOfDeath(dodDatePicker.getEditor().getText());
            musician.setCountry(countryTextField.getText().trim());
            musician.setRating(getRating());
            musician.setDescription(commentTextArea.getText());
            
            Main.main.getLoader().getMusicianTable().save(musician);
            if (includedDialogImageBoxController.isChangedImage()) {
                includedDialogImageBoxController.saveImage();
                includedDialogImageBoxController.setChangedImage(false);                              
                musician.setChangedImage(!musician.isChangedImage());
            }
            dialogStage.close();
            edit = false;
        }
    }
    
    @FXML
    private void handleCancelButton() {
        dialogStage.close();
    }
    
    public void setAddDialog() {
        musician = new Musician();
        includedDialogImageBoxController.setEntity(musician);
    }
       
    public void setEditDialog(Musician mus) {
        edit = true;
        musician = mus;
        nameTextField.setText(musician.getName());
        typeChoiceBox.getSelectionModel().select(musician.getType());
        dobDatePicker.setValue(dobDatePicker.getConverter().fromString(musician.getDateOfBirth()));
        dodDatePicker.setValue(dodDatePicker.getConverter().fromString(musician.getDateOfDeath()));
        countryTextField.setText(musician.getCountry());
        ratingSpinner.getValueFactory().setValue(musician.getRating());
        commentTextArea.setText(musician.getDescription());
              
        includedDialogImageBoxController.setEntity(musician);
        includedDialogImageBoxController.setImage(includedDialogImageBoxController.getImageFile());      
    }
    
    private boolean isInputValid() {
        String errorMessage = "";       
        if (nameTextField.getText() == null || nameTextField.getText().trim().equals("")) {
            errorMessage += "Введите название музыканта!\n"; 
        }
        if (!edit && !Main.main.getLoader().getMusicianTable().isUniqueName(nameTextField.getText())) {
            errorMessage += "Такой музыкант уже есть!\n";
        }
        try { 
            dobDatePicker.getConverter().fromString(dobDatePicker.getEditor().getText());
        } catch (DateTimeParseException e) {
            errorMessage += "Неправильно введён формат Date Of Birth " + dobDatePicker.getEditor().getText() +". Надо mm.dd.yyyy \n";
        }
        
        try { 
            dodDatePicker.getConverter().fromString(dodDatePicker.getEditor().getText());
        } catch (DateTimeParseException e) {
            errorMessage += "Неправильно введён формат Date Of Death " + dodDatePicker.getEditor().getText() +". Надо mm.dd.yyyy \n";
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
    
    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
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
