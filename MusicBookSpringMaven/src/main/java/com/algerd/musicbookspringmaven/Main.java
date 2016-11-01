
package com.algerd.musicbookspringmaven;

import com.algerd.musicbookspringmaven.spring.SpringFxmlLoader;
import com.algerd.musicbookspringmaven.spring.SpringContext;
import com.algerd.musicbookspringmaven.service.RequestPageService;
import com.algerd.musicbookspringmaven.controller.main.MainController;
import java.io.IOException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import static com.algerd.musicbookspringmaven.Params.DIR_IMAGES;
import com.algerd.musicbookspringmaven.spring.config.AppConfig;
import org.springframework.beans.factory.annotation.Autowired;

public class Main extends Application {
	public static void main(String[] args) {
		Application.launch(args);
	}
    
    private final SpringContext context = new SpringContext(this, AppConfig.class);
    
    @Autowired
    private SpringFxmlLoader springFxmlLoader;
    @Autowired
    private RequestPageService requestPageService;
      
	@Override
	public void start(Stage stage) throws IOException {       
        context.registerSingleton("primaryStage", stage);      
        context.init();
        
        requestPageService.init();  
             
        FXMLLoader loader = springFxmlLoader.loadController(MainController.class);
        BorderPane root = loader.<BorderPane>load();
         
        // первоначальная загрузка панели обзорного списка артистов
        requestPageService.artistsPane();
     
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("MusicBook");
        stage.getIcons().add(new Image(DIR_IMAGES + "icon_root_layout.png"));
        stage.show();         
    }

}
