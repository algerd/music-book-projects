package mvc.genre.controller;

import db.entity.Artist;
import db.entity.ArtistGenre;
import java.net.URL;
import java.util.Comparator;
import java.util.List;
import java.util.ResourceBundle;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import core.Main;
import core.RequestPage;
import core.ContextMenuManager;
import static core.ContextMenuManager.ItemType.ADD_ARTIST;
import static core.ContextMenuManager.ItemType.DELETE_ARTIST;
import static core.ContextMenuManager.ItemType.EDIT_ARTIST;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.control.Label;
import utils.Helper;

public class ArtistGenreTableController implements Initializable {

    private ArtistGenre selectedItem;
    private GenrePaneController genrePaneController;
    
    @FXML
    private AnchorPane artistGenreTable;
    @FXML
    private Label titleLabel;
    @FXML
    private TableView<ArtistGenre> artistTableView;
    @FXML
    private TableColumn<ArtistGenre, Integer> rankColumn;
    @FXML
    private TableColumn<ArtistGenre, String> artistColumn;
    @FXML
    private TableColumn<ArtistGenre, Integer> ratingColumn;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        rankColumn.setCellValueFactory(
            cellData -> new SimpleIntegerProperty(artistTableView.getItems().indexOf(cellData.getValue()) + 1).asObject()
        );
        artistColumn.setCellValueFactory(cellData -> cellData.getValue().getArtist().nameProperty());
        ratingColumn.setCellValueFactory(cellData -> cellData.getValue().getArtist().ratingProperty().asObject());       
    }
    
    public void bootstrap(GenrePaneController genrePaneController) {
        this.genrePaneController = genrePaneController;       
        setTableValue();
        initListeners();
        titleLabel.setText("Артисты жанра " + genrePaneController.getGenre().getName());
    }
    
    private void setTableValue() {        
        clearSelectionTable();
        artistTableView.getItems().clear();
        List<ArtistGenre> artistGenres = Main.getInstance().getDbLoader().getArtistGenreTable().selectJoinByGenre(genrePaneController.getGenre());  
        artistTableView.setItems(FXCollections.observableArrayList(artistGenres));      
        sort();
        Helper.setHeightTable(artistTableView, 10);       
    }
    
    private void initListeners() {
        // Добавить слушателя на выбор элемента в таблице.
        artistTableView.getSelectionModel().selectedItemProperty().addListener(
            (observable, oldValue, newValue) -> {             
                selectedItem = artistTableView.getSelectionModel().getSelectedItem();
            }
        );
        Main.getInstance().getDbLoader().getArtistGenreTable().addedProperty().addListener(this::changed);      
        Main.getInstance().getDbLoader().getArtistGenreTable().deletedProperty().addListener(this::changed);           
        Main.getInstance().getDbLoader().getArtistGenreTable().updatedProperty().addListener(this::changed);
                 
        Main.getInstance().getDbLoader().getArtistTable().deletedProperty().addListener(this::changed);           
        Main.getInstance().getDbLoader().getArtistTable().updatedProperty().addListener(this::changed);
         
        Main.getInstance().getDbLoader().getGenreTable().deletedProperty().addListener(this::changed);           
        Main.getInstance().getDbLoader().getGenreTable().updatedProperty().addListener(this::changed);
    }
    
    // слушатель на изменение в таблицах
    private void changed(ObservableValue observable, Object oldVal, Object newVal) {
        setTableValue();
    }
    
    private void sort() {
        clearSelectionTable();
        artistTableView.getItems().sort(Comparator.comparingInt((ArtistGenre artistGenre) -> artistGenre.getArtist().getRating()).reversed());
    }
    
    public void clearSelectionTable() {
        artistTableView.getSelectionModel().clearSelection();
        selectedItem = null;
    }
    
    /**
     * ЛКМ - зызов окна выбранного артиста;
     * ПКМ - вызов контекстного меню для add, edit, delete.
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
                Artist artist = Main.getInstance().getDbLoader().getArtistTable().select(selectedItem.getArtist().getId());
                RequestPage.ARTIST_PANE.load(artist);
            }           
        }
        else if (mouseEvent.getButton() == MouseButton.SECONDARY) { 
            contextMenu.add(ADD_ARTIST, new Artist());
            // запретить удаление и редактирование записи с id = 1 (Unknown album)
            if (selectedItem != null && selectedItem.getId() != 1) {
                Artist artist = Main.getInstance().getDbLoader().getArtistTable().select(selectedItem.getArtist().getId());
                contextMenu.add(EDIT_ARTIST, artist);
                contextMenu.add(DELETE_ARTIST, artist);                       
            }
            contextMenu.show(genrePaneController.getGenrePane(), mouseEvent);          
        }
    }
    
}
