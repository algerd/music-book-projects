
package mvc.genre.controller;

import core.ContextMenuManager;
import static core.ContextMenuManager.ItemType.ADD_SONG;
import static core.ContextMenuManager.ItemType.DELETE_SONG;
import static core.ContextMenuManager.ItemType.EDIT_SONG;
import core.Main;
import core.RequestPage;
import db.entity.Song;
import db.entity.SongGenre;
import java.net.URL;
import java.util.Comparator;
import java.util.List;
import java.util.ResourceBundle;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import utils.Helper;

public class SongGenreTableController implements Initializable {
    
    private SongGenre selectedItem;
    private GenrePaneController genrePaneController;
    
    @FXML
    private AnchorPane songGenreTable;
    @FXML
    private Label titleLabel;        
    @FXML
    private TableView<SongGenre> songTableView;
    @FXML
    private TableColumn<SongGenre, Integer> rankColumn;
    @FXML
    private TableColumn<SongGenre, String> songColumn;
    @FXML
    private TableColumn<SongGenre, String> artistColumn;
    @FXML
    private TableColumn<SongGenre, String> albumColumn;
    @FXML
    private TableColumn<SongGenre, Integer> yearColumn;            
    @FXML
    private TableColumn<SongGenre, Integer> ratingColumn;  

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        rankColumn.setCellValueFactory(
            cellData -> new SimpleIntegerProperty(songTableView.getItems().indexOf(cellData.getValue()) + 1).asObject()
        );
        songColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSong().getName()));
        artistColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSong().getAlbum().getArtist().getName()));
        albumColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSong().getAlbum().getName()));
        yearColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getSong().getAlbum().getYear()).asObject());
        ratingColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getSong().getRating()).asObject());                                
    } 
    
    public void bootstrap(GenrePaneController genrePaneController) {
        this.genrePaneController = genrePaneController;       
        setTableValue();
        initListeners();
        titleLabel.setText("Песни жанра " + genrePaneController.getGenre().getName());
    }
    
    private void setTableValue() {       
        clearSelectionTable();
        songTableView.getItems().clear();
        List<SongGenre> songs = Main.getInstance().getDbLoader().getSongGenreTable().selectJoinByGenre(genrePaneController.getGenre());
        songTableView.setItems(FXCollections.observableArrayList(songs));      
        sort();       
        Helper.setHeightTable(songTableView, 10);  
    }
    
    private void initListeners() {
        // Добавить слушателя на выбор элемента в таблице.
        songTableView.getSelectionModel().selectedItemProperty().addListener(
            (observable, oldValue, newValue) -> {             
                selectedItem = songTableView.getSelectionModel().getSelectedItem();
            }
        );                
        Main.getInstance().getDbLoader().getArtistTable().deletedProperty().addListener(this::changed);                           
        Main.getInstance().getDbLoader().getAlbumTable().deletedProperty().addListener(this::changed);           
               
        Main.getInstance().getDbLoader().getSongTable().deletedProperty().addListener(this::changed);           
        Main.getInstance().getDbLoader().getSongTable().updatedProperty().addListener(this::changed);
                  
        Main.getInstance().getDbLoader().getGenreTable().deletedProperty().addListener(this::changed);           
        Main.getInstance().getDbLoader().getGenreTable().updatedProperty().addListener(this::changed);
        
        Main.getInstance().getDbLoader().getSongGenreTable().addedProperty().addListener(this::changed);      
        Main.getInstance().getDbLoader().getSongGenreTable().deletedProperty().addListener(this::changed);           
        Main.getInstance().getDbLoader().getSongGenreTable().updatedProperty().addListener(this::changed);               
    }
    
    // слушатель на изменение в таблицах
    private void changed(ObservableValue observable, Object oldVal, Object newVal) {
        setTableValue();
    }
    
    private void sort() {
        clearSelectionTable();
        songTableView.getItems().sort(Comparator.comparingInt((SongGenre songGenre) -> songGenre.getSong().getRating()).reversed());
    }
    
    public void clearSelectionTable() {
        songTableView.getSelectionModel().clearSelection();
        selectedItem = null;
    }
    
    /**
     * ЛКМ - зызов окна выбранной песни;
     * ПКМ - вызов контекстного меню для add, edit, delete выбранной песни.
     */
    @FXML
    private void onMouseClickTable(MouseEvent mouseEvent) { 
        ContextMenuManager contextMenu = Main.getInstance().getContextMenu();
        boolean isShowingContextMenu = contextMenu.getContextMenu().isShowing();     
        contextMenu.clear();        
        if (mouseEvent.getButton() == MouseButton.PRIMARY) {
            // если контекстное меню выбрано, то лкм сбрасывает контекстное меню и выбор в таблице
            if (isShowingContextMenu) {
                clearSelectionTable();
            }
            // если лкм выбрана запись - показать её
            if (selectedItem != null) {
                Song song = Main.getInstance().getDbLoader().getSongTable().select(selectedItem.getSong().getId());
                RequestPage.SONG_PANE.load(song);
            }           
        }
        else if (mouseEvent.getButton() == MouseButton.SECONDARY) { 
            // id = 1 is "Unknown" albom of "Unknown" artist
            Song newSong = new Song();
            newSong.setId_album(1);          
            contextMenu.add(ADD_SONG, newSong);                
            if (selectedItem != null) {
                Song song = Main.getInstance().getDbLoader().getSongTable().select(selectedItem.getSong().getId());
                contextMenu.add(EDIT_SONG, song);
                contextMenu.add(DELETE_SONG, song);                       
            }
            contextMenu.show(genrePaneController.getGenrePane(), mouseEvent);           
        }
    }
    
}
