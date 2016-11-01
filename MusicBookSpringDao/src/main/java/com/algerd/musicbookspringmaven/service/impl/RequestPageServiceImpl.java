
package com.algerd.musicbookspringmaven.service.impl;

import com.algerd.musicbookspringmaven.service.RequestPageService;
import com.algerd.musicbookspringmaven.controller.main.MainController;
import com.algerd.musicbookspringmaven.dbDriver.Entity;
import javafx.scene.layout.AnchorPane;
import org.springframework.beans.factory.annotation.Autowired;
import com.algerd.musicbookspringmaven.controller.PaneController;

public class RequestPageServiceImpl implements RequestPageService {
    
    @Autowired
    private MainController mainController;   
    @Autowired
    private PaneController albumPaneController;
    @Autowired
    private PaneController albumsPaneController;
    @Autowired
    private PaneController artistPaneController;
    @Autowired
    private PaneController artistsPaneController;
    @Autowired
    private PaneController genrePaneController;
    @Autowired
    private PaneController genresPaneController;
    @Autowired
    private PaneController instrumentPaneController;
    @Autowired
    private PaneController instrumentsPaneController;
    @Autowired
    private PaneController musicianPaneController;
    @Autowired
    private PaneController musiciansPaneController; 
    @Autowired
    private PaneController songPaneController;
    @Autowired
    private PaneController songsPaneController;   
       
    @Override
    public void init() {
        artistPaneController.loadFxml();
        albumPaneController.loadFxml();       
        genrePaneController.loadFxml();
        instrumentPaneController.loadFxml();
        musicianPaneController.loadFxml();
        songPaneController.loadFxml();       
    }
   
    private void show(PaneController controller, Entity entity) {
        AnchorPane view = controller.getView();
        controller.show(entity);
        mainController.show(view);
    }
    
    @Override
    public void albumPane(Entity entity) {
        show(albumPaneController, entity);
    }
    
    @Override
    public void albumsPane() {
        show(albumsPaneController, null);
    }
    
    @Override
    public void artistPane(Entity entity) {
        show(artistPaneController, entity);
    }
   
    @Override
    public void artistsPane() {
        show(artistsPaneController, null);
    }
    
    @Override
    public void genrePane(Entity entity) {
        show(genrePaneController, entity);
    }
    
    @Override
    public void genresPane() {
        show(genresPaneController, null);
    }
    
    @Override
    public void instrumentPane(Entity entity) {
        show(instrumentPaneController, entity);
    }
    
    @Override
    public void instrumentsPane() {
        show(instrumentsPaneController, null);
    }
    
    @Override
    public void musicianPane(Entity entity) {
        show(musicianPaneController, entity);
    }
    
    @Override
    public void musiciansPane() {
        show(musiciansPaneController, null);
    }
    
    @Override
    public void songPane(Entity entity) {
        show(songPaneController, entity);
    }
    
    @Override
    public void songsPane() {
        show(songsPaneController, null);
    }
    
    /*
    @Override
    public void showPane(Class<Loadable> type, Entity entity) {
        Loadable controller = SpringFxmlLoader.APPLICATION_CONTEXT.getBean(type);
        if (controller.getView() == null) {
            controller.loadFxml();
        }
        controller.show(entity);
        Main.getInstance().getMainController().show(controller.getView()); 
    }
    */  
}
