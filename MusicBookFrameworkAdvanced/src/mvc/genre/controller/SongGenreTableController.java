
package mvc.genre.controller;

import core.ContextMenuManager;
import static core.ContextMenuManager.ItemType.ADD_SONG;
import static core.ContextMenuManager.ItemType.DELETE_SONG;
import static core.ContextMenuManager.ItemType.EDIT_SONG;
import core.Main;
import core.RequestPage;
import db.entity.Song;
import db.entity.SongGenre;
import db.repository.RepositoryService;
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
    
    //@Inject
    private RepositoryService repositoryService; 
    
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
        //@Inject
        repositoryService = Main.getInstance().getRepositoryService();  
        
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
        initRepositoryListeners();
        titleLabel.setText("Песни жанра " + genrePaneController.getGenre().getName());
    }
    
    private void initRepositoryListeners() {
        repositoryService.getArtistRepository().clearDeleteListeners(this);                           
        repositoryService.getAlbumRepository().clearDeleteListeners(this);                         
        repositoryService.getSongRepository().clearDeleteListeners(this);           
        repositoryService.getSongRepository().clearUpdateListeners(this);                 
        repositoryService.getGenreRepository().clearDeleteListeners(this);           
        repositoryService.getGenreRepository().clearUpdateListeners(this);      
        repositoryService.getSongGenreRepository().clearChangeListeners(this); 
        
        repositoryService.getArtistRepository().addDeleteListener(this::changed, this);                           
        repositoryService.getAlbumRepository().addDeleteListener(this::changed, this);                         
        repositoryService.getSongRepository().addDeleteListener(this::changed, this);           
        repositoryService.getSongRepository().addUpdateListener(this::changed, this);                 
        repositoryService.getGenreRepository().addDeleteListener(this::changed, this);           
        repositoryService.getGenreRepository().addUpdateListener(this::changed, this);      
        repositoryService.getSongGenreRepository().addChangeListener(this::changed, this);      
    }
    
    private void setTableValue() {       
        clearSelectionTable();
        songTableView.getItems().clear();
        List<SongGenre> songs = repositoryService.getSongGenreRepository().selectJoinByGenre(genrePaneController.getGenre());
        songTableView.setItems(FXCollections.observableArrayList(songs));      
        sort();       
        Helper.setHeightTable(songTableView, 10);  
    }
    
    private void initListeners() {
        // Добавить слушателя на выбор элемента в таблице.
        songTableView.getSelectionModel().selectedItemProperty().addListener(
            (observable, oldValue, newValue) -> selectedItem = songTableView.getSelectionModel().getSelectedItem()
        );                
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
                Song song = repositoryService.getSongRepository().selectById(selectedItem.getSong().getId());
                RequestPage.SONG_PANE.load(song);
            }           
        }
        else if (mouseEvent.getButton() == MouseButton.SECONDARY) { 
            // id = 1 is "Unknown" albom of "Unknown" artist
            Song newSong = new Song();
            newSong.setId_album(1);          
            contextMenu.add(ADD_SONG, newSong);                
            if (selectedItem != null) {
                Song song = repositoryService.getSongRepository().selectById(selectedItem.getSong().getId());
                contextMenu.add(EDIT_SONG, song);
                contextMenu.add(DELETE_SONG, song);                       
            }
            contextMenu.show(genrePaneController.getGenrePane(), mouseEvent);           
        }
    }
    
}
