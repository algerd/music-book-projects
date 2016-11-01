package mvc.artist.controller;

import mvc.VisiblePane;
import db.entity.Album;
import db.entity.Artist;
import db.entity.MusicianGroup;
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
import mvc.ContextMenuManager;
import static mvc.ContextMenuManager.ItemType.*;
import main.Main;
import main.Params;

public class ArtistPaneController implements Initializable, VisiblePane<Artist> {
    
    private Artist artist;
    
    @FXML
    private AnchorPane musicianTable;
    @FXML
    private MusicianTableController includedMusicianTableController;
    @FXML
    private AnchorPane albumTable;
    @FXML
    private AlbumTableController includedAlbumTableController;
    @FXML
    private AnchorPane artistReferenceTable;
    @FXML
    private ArtistReferenceTableController includedArtistReferenceTableController;
           
    @FXML
    private AnchorPane artistPane;
    @FXML
    private ImageView artistImageView;
    @FXML
    private Label nameLabel;    
    @FXML
    private Label genreLabel;    
    @FXML
    private Label ratingLabel;  
    @FXML
    private Text commentText;
          
    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }          
    
    /**
     * Отобразить окно артиста с данными artist.
     */
    public void show(Artist artist) {
        this.artist = artist;               
        // Добавить слушателя на редактирование жанра у артиста
        this.artist.genreProperty().addListener((observable, oldValue, newValue) -> 
            genreLabel.textProperty().bind(artist.getGenre().nameProperty())
        );     
        showArtistDetails();
        
        includedMusicianTableController.bootstrap(this);
        includedAlbumTableController.bootstrap(this);
        includedArtistReferenceTableController.bootstrap(this);
        artistPane.setVisible(true);
    }
    
    @Override
    public void hide() {
        includedMusicianTableController.clearSelectionMusicianTable();
        includedAlbumTableController.clearSelectionAlbumTable();
        includedArtistReferenceTableController.clearSelectionArtistReferenceTable();
        artistPane.setVisible(false);
    }
    
    /**
     * Заполнить данными вид артиста.
     */
    private void showArtistDetails() {  
        nameLabel.textProperty().bind(artist.nameProperty());                   
        genreLabel.textProperty().bind(artist.getGenre().nameProperty());               
        ratingLabel.textProperty().bind(artist.ratingProperty().asString());
        commentText.textProperty().bind(artist.descriptionProperty());
        
        // проверить и загрузить изображение
        File file = new File(Params.DIR_IMAGE_ENTITY + "artist/" + artist.getId() + "." + Params.SAVED_IMAGE_FORMAT);
        artistImageView.setImage(ImageUtil.readImage(file));
        artist.changedImageProperty().addListener(
            (observable, newValue, oldValue) -> artistImageView.setImage(ImageUtil.readImage(file))     
        );                                
    }         
            
    /**
     * При ПКМ по странице артиста показать контекстное меню.
     */
    @FXML
    private void showContextMenu(MouseEvent mouseEvent) {
        ContextMenuManager contextMenu = Main.main.getContextMenu();
        contextMenu.clear();
		if (mouseEvent.getButton() == MouseButton.SECONDARY) {       
            contextMenu.add(ADD_ARTIST, null);
            // запретить удаление и редактирование записи с id = 1 (Unknown artist)
            if (artist.getId() != 1) {
                contextMenu.add(EDIT_ARTIST, artist);
                contextMenu.add(DELETE_ARTIST, artist);
                contextMenu.add(SEPARATOR);
            }
            MusicianGroup newMusicianGroup = new MusicianGroup();
            newMusicianGroup.setIdArtist(artist.getId());       
            contextMenu.add(ADD_MUSICIAN_GROUP, newMusicianGroup);
            contextMenu.add(ADD_ALBUM, new Album(artist));
            contextMenu.show(artistPane, mouseEvent);
        }      
    }

    public AnchorPane getArtistPane() {
        return artistPane;
    }

    public Artist getArtist() {
        return artist;
    }
                
}
