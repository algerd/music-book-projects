
package com.algerd.musicbookspringmaven.controller;

import com.algerd.musicbookspringmaven.spring.SpringFxmlLoader;
import java.io.IOException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class BasePaneController extends BaseAwareController implements PaneController {
    
    @FXML
    protected AnchorPane view;
    @Autowired
    private SpringFxmlLoader springFxmlLoader;
    
    @Override
    public void loadFxml() {      
        try {
            FXMLLoader loader = springFxmlLoader.loadController(getClass()); 
            view = loader.<AnchorPane>load();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
   
    @Override
    public AnchorPane getView() {
        if (view == null) {
            loadFxml();
        }       
        return view;
    }
   
}
