
package mvc.album.controller;

import mvc.ContextMenuManager;
import db.entity.Album;
import db.entity.Song;
import mvc.VisiblePane;
import utils.Helper;
import utils.ImageUtil;
import java.io.File;
import static mvc.ContextMenuManager.ItemType.*;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import main.Main;
import main.Params;

public class AlbumPaneController implements Initializable, VisiblePane<Album> {
    
    private Album album;
    private Song selectedItem;
    
    @FXML
    private AnchorPane albumPane;
    /* ********** Details *********** */
    @FXML
    private ImageView albumImageView;
    @FXML
    private Hyperlink artistLink; 
    @FXML
    private Label genreLabel;
    @FXML
    private Label nameLabel;
    @FXML
    private Label yearLabel;
    @FXML
    private Label timeLabel;
    @FXML
    private Label ratingLabel;
    @FXML
    private Text commentText;
    
    /* ********** Songs *********** */
    @FXML
    private TableView<Song> songTableView;
    @FXML
    private TableColumn<Song, String> songNameColumn;
    @FXML
    private TableColumn<Song, Integer> songTrackColumn;
    @FXML
    private TableColumn<Song, String> songTimeColumn;
    @FXML
    private TableColumn<Song, Integer> songRatingColumn;
    
    public AlbumPaneController() {
        super();
    }
       
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        initSongTableView();
    } 
    
    private void initSongTableView() {      
        songNameColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        songTrackColumn.setCellValueFactory(cellData -> cellData.getValue().trackProperty().asObject());
        songTimeColumn.setCellValueFactory(cellData -> cellData.getValue().timeProperty());
        songRatingColumn.setCellValueFactory(cellData -> cellData.getValue().ratingProperty().asObject());
        
        //Добавить слушателей на выбор элемента в дереве артистов.
        songTableView.getSelectionModel().selectedItemProperty().addListener(
            (observable, oldValue, newValue) -> {
                selectedItem = songTableView.getSelectionModel().getSelectedItem();
            }
        );
    }
           
    /**
     * Отобразить окно альбома с данными album.
     */
    public void show(Album album) {
        this.album = album; 
        // добавить слушателя на изменение артиста
        this.album.parentProperty().addListener((observable, oldValue, newValue) -> {
            artistLink.textProperty().bind(album.getParent().nameProperty());
            genreLabel.textProperty().bind(album.getParent().getGenre().nameProperty());
        });  
        // добавить слушателя на изменение жанра у артиста
        this.album.getParent().genreProperty().addListener((observable, oldValue, newValue) -> {
            genreLabel.textProperty().bind(album.getParent().getGenre().nameProperty());
        });      
        showAlbumDetails();       
        clearSelectionTable();
        songTableView.setItems(album.getChildren());
        Helper.setHeightTable(songTableView);
        albumPane.setVisible(true);       
    }
    
    public void hide() {
        clearSelectionTable();
        albumPane.setVisible(false);
    }
    
    private void showAlbumDetails() {
        artistLink.textProperty().bind(album.getParent().nameProperty());
        genreLabel.textProperty().bind(album.getParent().getGenre().nameProperty());
        nameLabel.textProperty().bind(album.nameProperty());
        yearLabel.textProperty().bind(album.yearProperty().asString());
        timeLabel.textProperty().bind(album.timeProperty());
        ratingLabel.textProperty().bind(album.ratingProperty().asString());
        commentText.textProperty().bind(album.descriptionProperty());
        
        // проверить и загрузить изображение
        File file = new File(Params.DIR_IMAGE_ENTITY + "album/" + album.getId() + "." + Params.SAVED_IMAGE_FORMAT);
        albumImageView.setImage(ImageUtil.readImage(file));
        album.changedImageProperty().addListener(
            (observable, newValue, oldValue) -> albumImageView.setImage(ImageUtil.readImage(file))     
        );        
    }
     
    /**
     * Сбросить выбор в таблице песен.
     */
    private void clearSelectionTable() {
        songTableView.getSelectionModel().clearSelection();
        selectedItem = null;
    }
             
    @FXML 
    private void onLinkArtist() {
        Main.main.getMainController().showTreeEntity(album.getParent());
    }
    
    /**
     * ЛКМ - зызов окна выбранного альбома selectedAlbum;
     * ПКМ - вызов контекстного меню для add, edit, delete выбранного selectedAlbum или нового альбома.
     */
    @FXML
    private void onMouseClickTable(MouseEvent mouseEvent) {
        ContextMenuManager contextMenu = Main.main.getContextMenu();
        boolean isShowingContextMenu = contextMenu.getContextMenu().isShowing();
        contextMenu.clear();  
        if (mouseEvent.getButton() == MouseButton.PRIMARY) {
            // если контекстное меню выбрано, то лкм сбрасывает контекстное меню и выбор в таблице
            if (isShowingContextMenu) {
                clearSelectionTable();
            }
            // если лкм выбрана запись - показать её
            if (selectedItem != null) { 
                Main.main.getMainController().showTreeEntity(selectedItem);
            }
        }
        else if (mouseEvent.getButton() == MouseButton.SECONDARY) { 
            showTableContextMenu(mouseEvent);
        }
    }
       
    /**
     * При ПКМ по странице альбома показать контекстное меню.
     */
    @FXML
    private void showContextMenu(MouseEvent mouseEvent) {
        clearSelectionTable();
        ContextMenuManager contextMenu = Main.main.getContextMenu();
        contextMenu.clear();
		if (mouseEvent.getButton() == MouseButton.SECONDARY) {
            contextMenu.add(ADD_ALBUM, new Album(album.getParent()));
            // запретить удаление и редактирование записи с id = 1 (Unknown album)
            if (album.getId() != 1) { 
                contextMenu.add(EDIT_ALBUM, album);
                contextMenu.add(DELETE_ALBUM, album);
                contextMenu.add(SEPARATOR);
            }
            contextMenu.add(ADD_SONG, new Song(album));      
            contextMenu.show(albumPane, mouseEvent);
        }      
    }
    
    /**
     * При ПКМ по таблице песен альбома показать контекстное меню.
     */
    private void showTableContextMenu(MouseEvent mouseEvent) { 
        ContextMenuManager contextMenu = Main.main.getContextMenu();
        contextMenu.add(ADD_SONG, new Song(album));                    
        if (selectedItem != null) {
            contextMenu.add(EDIT_SONG, selectedItem);
            contextMenu.add(DELETE_SONG, selectedItem);     
        }
        contextMenu.show(albumPane, mouseEvent);            
    }
           
}
