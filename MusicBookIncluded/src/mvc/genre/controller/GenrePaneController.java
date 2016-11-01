package mvc.genre.controller;

import db.entity.Artist;
import db.entity.Entity;
import db.entity.Genre;
import mvc.VisiblePane;
import utils.Helper;
import java.net.URL;
import java.util.Comparator;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import mvc.ContextMenuManager;
import static mvc.ContextMenuManager.ItemType.*;
import main.Main;

public class GenrePaneController implements Initializable, VisiblePane<Genre> {
    
    private Genre genre;
    private Artist selectedItem;
    
    @FXML
    private AnchorPane genrePane;
    /* ********** Details *********** */
    @FXML
    private Label nameLabel;    
    @FXML
    private Text commentText;
    /* ********** TableView *********** */
    @FXML
    private TableView<Artist> artistTableView;
    @FXML
    private TableColumn<Artist, String> artistNameColumn;
    @FXML
    private TableColumn<Artist, Integer> artistRatingColumn;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        initArtistTableView();
    }
    
    private void initArtistTableView() {
        artistNameColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        artistRatingColumn.setCellValueFactory(cellData -> cellData.getValue().ratingProperty().asObject());
        
        // Добавить слушателя на выбор элемента в таблице.
        artistTableView.getSelectionModel().selectedItemProperty().addListener(
            (observable, oldValue, newValue) -> {             
                selectedItem = artistTableView.getSelectionModel().getSelectedItem();
            }
        );
        // Добавить слушателя на изменения в бд артистов
        Main.main.getLoader().getArtistTable().getEntities().addListener(this::changeArtistTable);
    } 
    
    private void changeArtistTable(ListChangeListener.Change<? extends Entity> change) {
        while (change.next()) {
            if ((change.wasAdded() && ((Artist)change.getAddedSubList().get(0)).getGenre() == genre) ||
                (change.wasRemoved() && ((Artist)change.getRemoved().get(0)).getGenre() == genre)) {                 
                findArtists();
            } 
        }    
    }
    
    @Override
    public void show(Genre genre) {
        this.genre = genre;
        showGenreDetails(); 
        clearSelectionTable();
        findArtists();
        genrePane.setVisible(true);
    }
    
    /**
     * найти артистов с жанром this.genre
     */
    private void findArtists() {       
        ObservableList<Artist> genreArtists = FXCollections.observableArrayList();
        ObservableList<Artist> artists = Main.main.getLoader().getArtistTable().getEntities();
        for (Artist artist : artists) {
            if (artist.getGenre() == genre) {               
                genreArtists.add(artist);                
            }
        }
        // отсортировать по алфавиту 
        genreArtists.sort(Comparator.comparing(Artist::getName));       
        artistTableView.setItems(genreArtists);
        Helper.setHeightTable(artistTableView);
    }

    @Override
    public void hide() {
        clearSelectionTable();
        genrePane.setVisible(false);
    }
    
    private void showGenreDetails() {  
        nameLabel.textProperty().bind(genre.nameProperty()); 
        commentText.textProperty().bind(genre.descriptionProperty());
    }
     
    private void clearSelectionTable() {
        artistTableView.getSelectionModel().clearSelection();
        selectedItem = null;
    } 
    
    /**
     * ЛКМ - зызов окна выбранного артиста;
     * ПКМ - вызов контекстного меню для add, edit, delete.
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
     * При ПКМ по таблице артистов показать контекстное меню.
     */
    private void showTableContextMenu(MouseEvent mouseEvent) { 
        ContextMenuManager contextMenu = Main.main.getContextMenu();
        contextMenu.add(ADD_ARTIST, null);
        // запретить удаление и редактирование записи с id = 1 (Unknown album)
        if (selectedItem != null && selectedItem.getId() != 1) {
            contextMenu.add(EDIT_ARTIST, selectedItem);
            contextMenu.add(DELETE_ARTIST, selectedItem);                       
        }
        contextMenu.show(genrePane, mouseEvent);      
    }
    
    /**
     * При ПКМ по странице жанра показать контекстное меню.
     */
    @FXML
    private void showContextMenu(MouseEvent mouseEvent) {
        clearSelectionTable();
        ContextMenuManager contextMenu = Main.main.getContextMenu();
        contextMenu.clear();
		if (mouseEvent.getButton() == MouseButton.SECONDARY) {       
            contextMenu.add(ADD_GENRE, null);
            // запретить удаление и редактирование записи с id = 1 (Unknown genre)
            if (genre.getId() != 1) {
                contextMenu.add(EDIT_GENRE, genre);
                contextMenu.add(DELETE_GENRE, genre);
            }
            contextMenu.show(genrePane, mouseEvent);
        }      
    }
    
}
