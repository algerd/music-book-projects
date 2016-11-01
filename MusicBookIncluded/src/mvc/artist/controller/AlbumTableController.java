package mvc.artist.controller;

import db.entity.Album;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import main.Main;
import mvc.ContextMenuManager;
import static mvc.ContextMenuManager.ItemType.ADD_ALBUM;
import static mvc.ContextMenuManager.ItemType.DELETE_ALBUM;
import static mvc.ContextMenuManager.ItemType.EDIT_ALBUM;
import utils.Helper;

public class AlbumTableController implements Initializable {

    private Album albumSelectedItem;
    private ArtistPaneController artistPaneController;
    
    @FXML
    private AnchorPane albumTable;
    @FXML
    private TableView<Album> albumTableView;
    @FXML
    private TableColumn<Album, String> albumNameColumn;
    @FXML
    private TableColumn<Album, Integer> albumYearColumn;
    @FXML
    private TableColumn<Album, String> albumTimeColumn;
    @FXML
    private TableColumn<Album, Integer> albumRatingColumn;  
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        albumNameColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        albumYearColumn.setCellValueFactory(cellData -> cellData.getValue().yearProperty().asObject());
        albumTimeColumn.setCellValueFactory(cellData -> cellData.getValue().timeProperty());
        albumRatingColumn.setCellValueFactory(cellData -> cellData.getValue().ratingProperty().asObject());         
        // Добавить слушателя на выбор элемента в таблице.
        albumTableView.getSelectionModel().selectedItemProperty().addListener(
            (observable, oldValue, newValue) -> {             
                albumSelectedItem = albumTableView.getSelectionModel().getSelectedItem();
            }
        );
    }
    
    public void bootstrap(ArtistPaneController artistPaneController) {
        this.artistPaneController = artistPaneController;
        clearSelectionAlbumTable();
        setAlbumTableValues();
    }
    
    private void setAlbumTableValues() {
        albumTableView.setItems(artistPaneController.getArtist().getChildren());
        Helper.setHeightTable(albumTableView);
    }
    
    public void clearSelectionAlbumTable() {
        albumTableView.getSelectionModel().clearSelection();
        albumSelectedItem = null;
    }
    
    /**
     * ЛКМ - зызов окна выбранного альбома;
     * ПКМ - вызов контекстного меню для add, edit, delete выбранного альбома.
     */
    @FXML
    private void onMouseClickAlbumTable(MouseEvent mouseEvent) { 
        ContextMenuManager contextMenu = Main.main.getContextMenu();
        boolean isShowingContextMenu = contextMenu.getContextMenu().isShowing();       
        contextMenu.clear();   
        if (mouseEvent.getButton() == MouseButton.PRIMARY) {
            // если контекстное меню выбрано, то лкм сбрасывает контекстное меню и выбор в таблице
            if (isShowingContextMenu) {
                clearSelectionAlbumTable();
            }
            // если лкм выбрана запись - показать её
            if (albumSelectedItem != null) {
                Main.main.getMainController().showTreeEntity(albumSelectedItem);
            }           
        }
        else if (mouseEvent.getButton() == MouseButton.SECONDARY) { 
            showAlbumTableContextMenu(mouseEvent);      
        }
    }
    
    /**
     * При ПКМ по таблице альбомов артиста показать контекстное меню.
     */
    private void showAlbumTableContextMenu(MouseEvent mouseEvent) { 
        ContextMenuManager contextMenu = Main.main.getContextMenu();
        contextMenu.add(ADD_ALBUM, new Album(artistPaneController.getArtist()));
        // запретить удаление и редактирование записи с id = 1 (Unknown album)
        if (albumSelectedItem != null && albumSelectedItem.getId() != 1) {
            contextMenu.add(EDIT_ALBUM, albumSelectedItem);
            contextMenu.add(DELETE_ALBUM, albumSelectedItem);                       
        }
        contextMenu.show(artistPaneController.getArtistPane(), mouseEvent);      
    } 
  
    public void setArtistPaneController(ArtistPaneController artistPaneController) {
        this.artistPaneController = artistPaneController;
    }
      
}
