
package mvc.song.controller;

import mvc.ContextMenuManager;
import db.entity.Song;
import mvc.VisiblePane;
import static mvc.ContextMenuManager.ItemType.*;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import main.Main;

public class SongPaneController implements Initializable, VisiblePane<Song> {
    
    private Song song;   
    
    @FXML
    private AnchorPane songPane;   
    @FXML
    private Hyperlink artistLink; 
    @FXML
    private Label genreLabel;      
    @FXML
    private Hyperlink albumLink; 
    @FXML
    private Label yearLabel;       
    @FXML
    private Label nameLabel;   
    @FXML
    private Label trackLabel;    
    @FXML
    private Label timeLabel;   
    @FXML
    private Label ratingLabel;   
    @FXML
    private Text lyricText;
    @FXML
    private Text commentText;
        
    @Override
    public void initialize(URL url, ResourceBundle rb) {   
    }
    
    /**
     * Отобразить окно артиста с данными song.
     */
    public void show(Song song) {
        this.song = song;      
        // добавить слушателя на изменение альбома
        song.parentProperty().addListener((observable, oldValue, newValue) -> {
            artistLink.textProperty().bind(song.getParent().getParent().nameProperty());
            genreLabel.textProperty().bind(song.getParent().getParent().getGenre().nameProperty());
            albumLink.textProperty().bind(song.getParent().nameProperty()); 
            yearLabel.textProperty().bind(song.getParent().yearProperty().asString()); 
        });
        // добавить слушателя на изменение жанра у артиста
        song.getParent().getParent().genreProperty().addListener((observable, oldValue, newValue) -> {
            genreLabel.textProperty().bind(song.getParent().getParent().getGenre().nameProperty());
        });       
        showSongDetails();
        songPane.setVisible(true);       
    }
    
    public void hide() {
        songPane.setVisible(false);
    }
    
    /**
     * Заполнить данными вид песен.
     */
    private void showSongDetails() {
        artistLink.textProperty().bind(song.getParent().getParent().nameProperty());
        genreLabel.textProperty().bind(song.getParent().getParent().getGenre().nameProperty());
        albumLink.textProperty().bind(song.getParent().nameProperty()); 
        yearLabel.textProperty().bind(song.getParent().yearProperty().asString());                
        nameLabel.textProperty().bind(song.nameProperty());
        trackLabel.textProperty().bind(song.trackProperty().asString());
        timeLabel.textProperty().bind(song.timeProperty());
        ratingLabel.textProperty().bind(song.ratingProperty().asString());
        lyricText.textProperty().bind(song.lyricProperty());
        commentText.textProperty().bind(song.descriptionProperty());
    } 
       
    @FXML 
    private void onLinkArtist() {
       Main.main.getMainController().showTreeEntity(song.getParent().getParent());
    }
    
    @FXML 
    private void onLinkAlbum() {
        Main.main.getMainController().showTreeEntity(song.getParent());
    }
    
    /**
     * При ПКМ по странице песни показать контекстное меню.
     */
    @FXML
    private void showContextMenu(MouseEvent mouseEvent) {
        ContextMenuManager contextMenu = Main.main.getContextMenu();
        contextMenu.clear();
		if (mouseEvent.getButton() == MouseButton.SECONDARY) {
            contextMenu.add(ADD_SONG, new Song(song.getParent()));
            contextMenu.add(EDIT_SONG, song);
            contextMenu.add(DELETE_SONG, song);        
            contextMenu.show(songPane, mouseEvent);
        }      
    }
}
