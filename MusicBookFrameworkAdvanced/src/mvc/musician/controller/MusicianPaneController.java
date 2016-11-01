package mvc.musician.controller;

import db.driver.Entity;
import db.entity.Musician;
import db.entity.MusicianGroup;
import db.driver.impl.WrapChangedEntity;
import utils.ImageUtil;
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
import static core.ContextMenuManager.ItemType.*;
import core.Main;
import core.Params;
import db.repository.RepositoryService;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;

public class MusicianPaneController implements Initializable, Loadable {
    
    //@Inject
    private RepositoryService repositoryService;

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
    private AnchorPane musicianPane;
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
        //@Inject
        repositoryService = Main.getInstance().getRepositoryService(); 
    }
    
    @Override
    public void show(Entity entity) {
        musician = (Musician) entity; 
        showDetails();
        musicianPane.setVisible(true);
        initRepositoryListeners();
        includedArtistTableController.bootstrap(this);
        includedAlbumTableController.bootstrap(this);
        includedSongTableController.bootstrap(this);
        includedGenreListController.bootstrap(this); 
        includedInstrumentListController.bootstrap(this);
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
            musicianPane.setVisible(false);
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
        ContextMenuManager contextMenu = Main.getInstance().getContextMenu();
        contextMenu.clear();
		if (mouseEvent.getButton() == MouseButton.SECONDARY) {
            contextMenu.add(ADD_MUSICIAN, new Musician());
            contextMenu.add(EDIT_MUSICIAN, musician);
            contextMenu.add(DELETE_MUSICIAN, musician);
            contextMenu.add(SEPARATOR);  
            MusicianGroup newMusicianGroup = new MusicianGroup();
            newMusicianGroup.setId_musician(musician.getId());
            contextMenu.add(ADD_MUSICIAN_GROUP, newMusicianGroup);      
            contextMenu.show(musicianPane, mouseEvent);
        }      
    }

    public AnchorPane getMusicianPane() {
        return musicianPane;
    }

    public Musician getMusician() {
        return musician;
    }   
        
}

