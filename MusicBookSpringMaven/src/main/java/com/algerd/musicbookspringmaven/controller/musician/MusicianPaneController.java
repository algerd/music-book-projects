package com.algerd.musicbookspringmaven.controller.musician;

import static com.algerd.musicbookspringmaven.service.impl.ContextMenuItemType.ADD_MUSICIAN;
import static com.algerd.musicbookspringmaven.service.impl.ContextMenuItemType.ADD_MUSICIAN_GROUP;
import static com.algerd.musicbookspringmaven.service.impl.ContextMenuItemType.DELETE_MUSICIAN;
import static com.algerd.musicbookspringmaven.service.impl.ContextMenuItemType.EDIT_MUSICIAN;
import static com.algerd.musicbookspringmaven.service.impl.ContextMenuItemType.SEPARATOR;
import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import com.algerd.musicbookspringmaven.dbDriver.Entity;
import com.algerd.musicbookspringmaven.entity.Musician;
import com.algerd.musicbookspringmaven.entity.MusicianGroup;
import com.algerd.musicbookspringmaven.dbDriver.impl.WrapChangedEntity;
import com.algerd.musicbookspringmaven.utils.ImageUtil;
import com.algerd.musicbookspringmaven.controller.BasePaneController;
import com.algerd.musicbookspringmaven.Params;

public class MusicianPaneController extends BasePaneController {

    private Musician musician; 
    private File file;
    private long imageLastModified;
       
    @FXML
    private AnchorPane artistTable;
    @FXML
    private ArtistTableController includedArtistTableController;
    @FXML
    private AnchorPane albumTable;
    @FXML
    private AlbumTableController includedAlbumTableController;
    @FXML
    private AnchorPane songTable;
    @FXML
    private SongTableController includedSongTableController;
    
    @FXML
    private AnchorPane genreList;
    @FXML
    private GenreListController includedGenreListController;
    @FXML
    private AnchorPane instrumentList;
    @FXML
    private InstrumentListController includedInstrumentListController;
    
    @FXML
    private TabPane musicianTabPane;
    @FXML
    private Tab detailsTab;   
    @FXML
    private ImageView musicianImageView;
    @FXML
    private Label nameLabel; 
    @FXML
    private Label dobLabel; 
    @FXML
    private Label dodLabel; 
    @FXML
    private Label countryLabel; 
    @FXML
    private Label ratingLabel; 
    @FXML
    private Text commentText;
       
    @Override
    public void initialize(URL url, ResourceBundle rb) { 
    }
    
    @Override
    public void show(Entity entity) {
        musician = (Musician) entity; 
        showDetails();
        view.setVisible(true);
        initRepositoryListeners();
        includedArtistTableController.setPaneController(this);
        includedAlbumTableController.setPaneController(this);
        includedSongTableController.setPaneController(this);
        includedGenreListController.setPaneController(this); 
        includedInstrumentListController.setPaneController(this);
        musicianTabPane.getSelectionModel().select(detailsTab);
    } 
    
    private void initRepositoryListeners() {
        //clear listeners
        repositoryService.getMusicianRepository().clearDeleteListeners(this);
        repositoryService.getMusicianRepository().clearUpdateListeners(this);

        //add listeners
        repositoryService.getMusicianRepository().addDeleteListener(this::deletedMusician, this);
        repositoryService.getMusicianRepository().addUpdateListener(this::updatedMusician, this);
    }
    
    private void deletedMusician(ObservableValue observable, Object oldVal, Object newVal) {
        Musician newEntity = ((WrapChangedEntity<Musician>) newVal).getNew();
        if (newEntity.getId() == musician.getId()) {
            view.setVisible(false);
        }
    }
    
    private void updatedMusician(ObservableValue observable, Object oldVal, Object newVal) {
        if (file.lastModified() != imageLastModified) {
            musicianImageView.setImage(ImageUtil.readImage(file));
            imageLastModified = file.lastModified();
        }
    }
       
    private void showDetails() {
        nameLabel.textProperty().bind(musician.nameProperty());       
        dobLabel.textProperty().bind(musician.date_of_birthProperty());  
        dodLabel.textProperty().bind(musician.date_of_deathProperty()); 
        countryLabel.textProperty().bind(musician.countryProperty());
        ratingLabel.textProperty().bind(musician.ratingProperty().asString());
        commentText.textProperty().bind(musician.descriptionProperty());
        
        // проверить и загрузить изображение
        file = new File(Params.DIR_IMAGE_ENTITY + "musician/" + musician.getId() + "." + Params.SAVED_IMAGE_FORMAT);
        musicianImageView.setImage(ImageUtil.readImage(file));
        imageLastModified = file.lastModified();       
    }
    
    /**
     * При ПКМ по странице показать контекстное меню.
     */
    @FXML
    private void showContextMenu(MouseEvent mouseEvent) {
        contextMenuService.clear();
		if (mouseEvent.getButton() == MouseButton.SECONDARY) {
            contextMenuService.add(ADD_MUSICIAN, new Musician());
            contextMenuService.add(EDIT_MUSICIAN, musician);
            contextMenuService.add(DELETE_MUSICIAN, musician);
            contextMenuService.add(SEPARATOR);  
            MusicianGroup newMusicianGroup = new MusicianGroup();
            newMusicianGroup.setId_musician(musician.getId());
            contextMenuService.add(ADD_MUSICIAN_GROUP, newMusicianGroup);      
            contextMenuService.show(view, mouseEvent);
        }      
    }

    public Musician getMusician() {
        return musician;
    }   
        
}

