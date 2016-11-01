package com.algerd.musicbookspringmaven.controller.genre;

import com.algerd.musicbookspringmaven.dbDriver.Entity;
import com.algerd.musicbookspringmaven.entity.Genre;
import com.algerd.musicbookspringmaven.dbDriver.impl.WrapChangedEntity;
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
import com.algerd.musicbookspringmaven.controller.BasePaneController;
import com.algerd.musicbookspringmaven.Params;
import static com.algerd.musicbookspringmaven.service.impl.ContextMenuItemType.ADD_GENRE;
import static com.algerd.musicbookspringmaven.service.impl.ContextMenuItemType.DELETE_GENRE;
import static com.algerd.musicbookspringmaven.service.impl.ContextMenuItemType.EDIT_GENRE;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TabPane;
import com.algerd.musicbookspringmaven.utils.ImageUtil;

public class GenrePaneController extends BasePaneController {
       
    private Genre genre;
    private File file;
    private long imageLastModified;
    
    @FXML
    private AnchorPane artistGenreTable;         
    @FXML
    private ArtistGenreTableController includedArtistGenreTableController;
    @FXML
    private AnchorPane albumGenreTable;         
    @FXML
    private AlbumGenreTableController includedAlbumGenreTableController;
    @FXML
    private AnchorPane songGenreTable;         
    @FXML
    private SongGenreTableController includedSongGenreTableController;
    @FXML
    private AnchorPane musicianGenreTable; 
    @FXML
    private MusicianGenreTableController includedMusicianGenreTableController;
    
    @FXML
    private TabPane genreTabPane; 
    @FXML
    private ImageView genreImageView;
    @FXML
    private Label nameLabel;    
    @FXML
    private Text commentText;
   
    @Override
    public void initialize(URL url, ResourceBundle rb) {      
    }
    
    @Override
    public void show(Entity entity) {
        genre = (Genre) entity;  
        showDetails();
        view.setVisible(true);
        initRepositoryListeners(); 
        includedArtistGenreTableController.setPaneController(this);
        includedAlbumGenreTableController.setPaneController(this);
        includedSongGenreTableController.setPaneController(this);
        includedMusicianGenreTableController.setPaneController(this);
        genreTabPane.getSelectionModel().selectFirst();
    }
    
    private void initRepositoryListeners() {
        repositoryService.getGenreRepository().clearDeleteListeners(this);
        repositoryService.getGenreRepository().clearUpdateListeners(this);
        
        repositoryService.getGenreRepository().addDeleteListener(this::deletedGenre, this);
        repositoryService.getGenreRepository().addUpdateListener(this::updatedGenre, this);
    }
    
    private void deletedGenre(ObservableValue observable, Object oldVal, Object newVal) {
        Genre newEntity = ((WrapChangedEntity<Genre>) newVal).getNew();
        if (newEntity.getId() == genre.getId()) {
            view.setVisible(false);
        }
    }
    
    private void updatedGenre(ObservableValue observable, Object oldVal, Object newVal) {
        if (file.lastModified() != imageLastModified) {
            genreImageView.setImage(ImageUtil.readImage(file));
            imageLastModified = file.lastModified();
        }
    }   
    
    private void showDetails() {  
        nameLabel.textProperty().bind(genre.nameProperty()); 
        commentText.textProperty().bind(genre.descriptionProperty());
        
        // проверить и загрузить изображение
        file = new File(Params.DIR_IMAGE_ENTITY + "genre/" + genre.getId() + "." + Params.SAVED_IMAGE_FORMAT);
        genreImageView.setImage(ImageUtil.readImage(file));
        imageLastModified = file.lastModified();       
    }
    
    /**
     * При ПКМ по странице жанра показать контекстное меню.
     */
    @FXML
    private void showContextMenu(MouseEvent mouseEvent) {
        contextMenuService.clear();
		if (mouseEvent.getButton() == MouseButton.SECONDARY) {       
            contextMenuService.add(ADD_GENRE, new Genre());
            contextMenuService.add(EDIT_GENRE, genre);
            contextMenuService.add(DELETE_GENRE, genre);
            contextMenuService.show(view, mouseEvent);
        }      
    }

    public Genre getGenre() {
        return genre;
    }
 
}
