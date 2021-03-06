package mvc.artist.controller;

import db.entity.Artist;
import db.driver.Entity;
import db.driver.impl.WrapChangedEntity;
import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import core.Loadable;
import core.Main;
import core.Params;
import core.ContextMenuManager;
import static core.ContextMenuManager.ItemType.*;
import db.repository.RepositoryService;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import utils.ImageUtil;

public class ArtistPaneController implements Initializable, Loadable {
    
    //@Inject
    private RepositoryService repositoryService; 
    
    private Artist artist;
    private File file;
    private long imageLastModified;
    
    @FXML
    private AnchorPane artistReferenceTable;
    @FXML
    private ArtistReferenceTableController includedArtistReferenceTableController;
    @FXML
    private AnchorPane albumTable;
    @FXML
    private AlbumTableController includedAlbumTableController;
    @FXML
    private AnchorPane musicianTable;
    @FXML
    private MusicianTableController includedMusicianTableController;      
    @FXML
    private AnchorPane genreList;
    @FXML
    private GenreListController includedGenreListController;
           
    @FXML
    private AnchorPane artistPane;
    @FXML
    private TabPane artistTabPane;
    @FXML
    private Tab detailsTab;         
    @FXML
    private ImageView artistImageView;
    @FXML
    private Label nameLabel;       
    @FXML
    private Label ratingLabel;  
    @FXML
    private Text commentText;     
          
    @Override
    public void initialize(URL url, ResourceBundle rb) {  
        //@Inject
        repositoryService = Main.getInstance().getRepositoryService();
    } 
    
    @Override
    public void show(Entity entity) {
        this.artist = (Artist) entity;                          
        showDetails();
        artistPane.setVisible(true);
        initRepositoryListeners();
        includedArtistReferenceTableController.bootstrap(this);
        includedAlbumTableController.bootstrap(this);
        includedMusicianTableController.bootstrap(this); 
        includedGenreListController.bootstrap(this);
        artistTabPane.getSelectionModel().select(detailsTab);
    }
    
    private void initRepositoryListeners() {
        repositoryService.getArtistRepository().clearDeleteListeners(this);
        repositoryService.getArtistRepository().clearUpdateListeners(this);
       
        repositoryService.getArtistRepository().addDeleteListener(this::deletedArtist, this);
        repositoryService.getArtistRepository().addUpdateListener(this::updatedArtist, this);
    }
    
    private void deletedArtist(ObservableValue observable, Object oldVal, Object newVal) {
        Artist newEntity = ((WrapChangedEntity<Artist>) newVal).getNew();
        if (newEntity.getId() == artist.getId()) {
            artistPane.setVisible(false);
        }
    }
    
    private void updatedArtist(ObservableValue observable, Object oldVal, Object newVal) {
         if (file.lastModified() != imageLastModified) {
            artistImageView.setImage(ImageUtil.readImage(file));
            imageLastModified = file.lastModified();
        }
    }
      
    private void showDetails() {         
        nameLabel.textProperty().bind(artist.nameProperty());                                 
        ratingLabel.textProperty().bind(artist.ratingProperty().asString());
        commentText.textProperty().bind(artist.descriptionProperty());  
        
        // проверить и загрузить изображение
        file = new File(Params.DIR_IMAGE_ENTITY + "artist/" + artist.getId() + "." + Params.SAVED_IMAGE_FORMAT);
        artistImageView.setImage(ImageUtil.readImage(file));
        imageLastModified = file.lastModified();       
    } 
    
    /**
     * При ПКМ по странице артиста показать контекстное меню.
     */
    @FXML
    private void showContextMenu(MouseEvent mouseEvent) {
        ContextMenuManager contextMenu = Main.getInstance().getContextMenu();
        contextMenu.clear();
		if (mouseEvent.getButton() == MouseButton.SECONDARY) {       
            contextMenu.add(ADD_ARTIST, new Artist());
            // запретить удаление и редактирование записи с id = 1 (Unknown artist)
            if (artist.getId() != 1) {
                contextMenu.add(EDIT_ARTIST, artist);
                contextMenu.add(DELETE_ARTIST, artist);
                contextMenu.add(SEPARATOR);
            }
            contextMenu.show(artistPane, mouseEvent);
        }      
    }

    public Artist getArtist() {
        return artist;
    }

    public AnchorPane getArtistPane() {
        return artistPane;
    }   
              
}
