package mvc.main.controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;

public class MainController implements Initializable {
    
    @FXML
    private StackPane mainWindow;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }

    public void show(AnchorPane pane) {
        mainWindow.getChildren().clear();
        mainWindow.getChildren().add(pane);
    }
         
}
