package com.algerd.musicbookspringmaven.controller.genre;

import com.algerd.musicbookspringmaven.controller.BaseIncludeController;
import java.net.URL;
import java.util.Comparator;
import java.util.List;
import java.util.ResourceBundle;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.control.Label;
import com.algerd.musicbookspringmaven.entity.Artist;
import com.algerd.musicbookspringmaven.entity.ArtistGenre;
import com.algerd.musicbookspringmaven.utils.Helper;
import static com.algerd.musicbookspringmaven.service.impl.ContextMenuItemType.ADD_ARTIST;
import static com.algerd.musicbookspringmaven.service.impl.ContextMenuItemType.DELETE_ARTIST;
import static com.algerd.musicbookspringmaven.service.impl.ContextMenuItemType.EDIT_ARTIST;

public class ArtistGenreTableController extends BaseIncludeController<GenrePaneController> {

    private ArtistGenre selectedItem;
    
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
    
    @Override
    public void bootstrap() {     
        setTableValue();
        initListeners();
        initRepositoryListeners();
        titleLabel.setText("Артисты жанра " + paneController.getGenre().getName());
    }
    
    private void initRepositoryListeners() {
        repositoryService.getArtistGenreRepository().clearChangeListeners(this);                          
        repositoryService.getArtistRepository().clearDeleteListeners(this);           
        repositoryService.getArtistRepository().clearUpdateListeners(this);        
        repositoryService.getGenreRepository().clearDeleteListeners(this);           
        repositoryService.getGenreRepository().clearUpdateListeners(this);
        
        repositoryService.getArtistGenreRepository().addChangeListener(this::changed, this);                          
        repositoryService.getArtistRepository().addDeleteListener(this::changed, this);           
        repositoryService.getArtistRepository().addUpdateListener(this::changed, this);        
        repositoryService.getGenreRepository().addDeleteListener(this::changed, this);           
        repositoryService.getGenreRepository().addUpdateListener(this::changed, this);
    }
    
    private void setTableValue() {        
        clearSelectionTable();
        artistTableView.getItems().clear();
        List<ArtistGenre> artistGenres = repositoryService.getArtistGenreRepository().selectJoinByGenre(paneController.getGenre());  
        artistTableView.setItems(FXCollections.observableArrayList(artistGenres));      
        sort();
        Helper.setHeightTable(artistTableView, 10);       
    }
    
    private void initListeners() {
        // Добавить слушателя на выбор элемента в таблице.
        artistTableView.getSelectionModel().selectedItemProperty().addListener(
            (observable, oldValue, newValue) -> selectedItem = artistTableView.getSelectionModel().getSelectedItem()
        );
    }
    
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
        boolean isShowingContextMenu = contextMenuService.getContextMenu().isShowing();       
        contextMenuService.clear();   
        if (mouseEvent.getButton() == MouseButton.PRIMARY) {
            // если контекстное меню выбрано, то лкм сбрасывает контекстное меню и выбор в таблице
            if (isShowingContextMenu) {
                clearSelectionTable();
            }
            // если лкм выбрана запись - показать её
            if (selectedItem != null) {
                Artist artist = repositoryService.getArtistRepository().selectById(selectedItem.getArtist().getId());
                //RequestPage.ARTIST_PANE.load(artist);
                requestPageService.artistPane(artist);
            }           
        }
        else if (mouseEvent.getButton() == MouseButton.SECONDARY) { 
            contextMenuService.add(ADD_ARTIST, new Artist());
            // запретить удаление и редактирование записи с id = 1 (Unknown album)
            if (selectedItem != null && selectedItem.getId() != 1) {
                Artist artist = repositoryService.getArtistRepository().selectById(selectedItem.getArtist().getId());
                contextMenuService.add(EDIT_ARTIST, artist);
                contextMenuService.add(DELETE_ARTIST, artist);                       
            }
            contextMenuService.show(paneController.getView(), mouseEvent);          
        }
    }
    
}
