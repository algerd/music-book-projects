
package core;

import mvc.main.controller.MainController;
import java.io.IOException;
import java.net.URL;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class Main extends Application {
	public static void main(String[] args) {
		Application.launch(args);
	}
      
    private static Main instance;
    private Stage primaryStage;
    private DbLoader dbLoader;
    private MainController mainController; 
    private ContextMenuManager contextMenu;
              
    @Override
    public void init() {
        instance = this;
        contextMenu = new ContextMenuManager();
        dbLoader = new DbLoader();      
    }

	@Override
	public void start(Stage stage) throws IOException {
              
        URL fxmlUrl = this.getClass().getClassLoader().getResource("mvc/main/view/Main.fxml");
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
        primaryStage.getIcons().add(new Image("resources/images/icon_root_layout.png"));
        primaryStage.show(); 
        
    }

    public static Main getInstance() {
        return instance;
    }
    
    public DbLoader getDbLoader() {
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
