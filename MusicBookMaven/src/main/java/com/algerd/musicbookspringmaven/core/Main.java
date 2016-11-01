
package com.algerd.musicbookspringmaven.core;

import com.algerd.musicbookspringmaven.repository.RepositoryServiceImpl;
import com.algerd.musicbookspringmaven.controller.main.MainController;
import java.io.IOException;
import java.net.URL;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import static com.algerd.musicbookspringmaven.core.Params.DIR_FXML;
import static com.algerd.musicbookspringmaven.core.Params.DIR_IMAGES;

public class Main extends Application {
	public static void main(String[] args) {
		Application.launch(args);
	}
      
    private static Main instance;
    private Stage primaryStage;
    private RepositoryServiceImpl dbLoader;
    private MainController mainController; 
    private ContextMenuManager contextMenu;
              
    @Override
    public void init() {
        instance = this;
        dbLoader = new RepositoryServiceImpl();
        contextMenu = new ContextMenuManager();             
    }

	@Override
	public void start(Stage stage) throws IOException {
              
        URL fxmlUrl = this.getClass().getResource(DIR_FXML + "main/Main.fxml");
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(fxmlUrl);
        BorderPane root = loader.<BorderPane>load();
        mainController = loader.getController();
        // первоначальная загрузка панели обзорного списка артистов 
        RequestPage.ARTISTS_PANE.load();
        
        Scene scene = new Scene(root);
        primaryStage = stage;
        primaryStage.setScene(scene);
        primaryStage.setTitle("MusicBook");
        primaryStage.getIcons().add(new Image(DIR_IMAGES + "icon_root_layout.png"));
        primaryStage.show(); 
        
    }

    public static Main getInstance() {
        return instance;
    }
    
    public RepositoryServiceImpl getRepositoryService() {
        return dbLoader;
    }
    
    public Stage getPrimaryStage() {
        return primaryStage;
    }

    public MainController getMainController() {
        return mainController;
    }

    public ContextMenuManager getContextMenu() {
        return contextMenu;
    }
             
}
