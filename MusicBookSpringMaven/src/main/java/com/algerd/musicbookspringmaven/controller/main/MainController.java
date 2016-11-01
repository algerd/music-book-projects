package com.algerd.musicbookspringmaven.controller.main;

import com.algerd.musicbookspringmaven.controller.Controller;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;

public class MainController implements Controller {
       
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
