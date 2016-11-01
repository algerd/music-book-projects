package mvc.genre.controller;

import db.driver.Entity;
import db.entity.Genre;
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
import core.ContextMenuManager;
import core.Main;
import core.Params;
import static core.ContextMenuManager.ItemType.ADD_GENRE;
import static core.ContextMenuManager.ItemType.DELETE_GENRE;
import static core.ContextMenuManager.ItemType.EDIT_GENRE;
import db.repository.RepositoryService;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TabPane;
import utils.ImageUtil;

public class GenrePaneController implements Initializable, Loadable {
    
    //@Inject
    private RepositoryService repositoryService; 
    
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
    private AnchorPane genrePane;
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
        //@Inject
        repositoryService = Main.getInstance().getRepositoryService();       
    }
    
    @Override
    public void show(Entity entity) {
        genre = (Genre) entity;  
        showDetails(); 
        genrePane.setVisible(true);
        initRepositoryListeners(); 
        includedArtistGenreTableController.bootstrap(this);
        includedAlbumGenreTableController.bootstrap(this);
        includedSongGenreTableController.bootstrap(this);
        includedMusicianGenreTableController.bootstrap(this);
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
            genrePane.setVisible(false);
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
        ContextMenuManager contextMenu = Main.getInstance().getContextMenu();
        contextMenu.clear();
		if (mouseEvent.getButton() == MouseButton.SECONDARY) {       
            contextMenu.add(ADD_GENRE, new Genre());
            contextMenu.add(EDIT_GENRE, genre);
            contextMenu.add(DELETE_GENRE, genre);
            contextMenu.show(genrePane, mouseEvent);
        }      
    }

    public Genre getGenre() {
        return genre;
    }

    public AnchorPane getGenrePane() {
        return genrePane;
    }   

}
