package com.algerd.musicbookspringmaven.controller.musician;

import com.algerd.musicbookspringmaven.controller.BaseIncludeController;
import java.net.URL;
import java.util.Comparator;
import java.util.List;
import java.util.ResourceBundle;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import com.algerd.musicbookspringmaven.entity.Artist;
import com.algerd.musicbookspringmaven.entity.MusicianGroup;
import com.algerd.musicbookspringmaven.utils.Helper;
import static com.algerd.musicbookspringmaven.service.impl.ContextMenuItemType.ADD_MUSICIAN_GROUP;
import static com.algerd.musicbookspringmaven.service.impl.ContextMenuItemType.DELETE_MUSICIAN_GROUP;
import static com.algerd.musicbookspringmaven.service.impl.ContextMenuItemType.EDIT_MUSICIAN_GROUP;

public class ArtistTableController extends BaseIncludeController<MusicianPaneController> {
        
    private MusicianGroup selectedItem;   
    
    @FXML
    private AnchorPane artistTable;
    @FXML
    private Label titleLabel;
    @FXML
    private TableView<MusicianGroup> artistTableView;
    @FXML
    private TableColumn<MusicianGroup, String> artistColumn;
    @FXML
    private TableColumn<MusicianGroup, String> startDateColumn;
    @FXML
    private TableColumn<MusicianGroup, String> endDateColumn;
    @FXML
    private TableColumn<MusicianGroup, Integer> ratingColumn;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        artistColumn.setCellValueFactory(cellData -> cellData.getValue().getArtist().nameProperty());
        startDateColumn.setCellValueFactory(cellData -> cellData.getValue().start_dateProperty());
        endDateColumn.setCellValueFactory(cellData -> cellData.getValue().end_dateProperty());                
        ratingColumn.setCellValueFactory(cellData -> cellData.getValue().getArtist().ratingProperty().asObject());
    }
    
    @Override
    public void bootstrap() {
        setTableValue();
        initListeners();
        initRepositoryListeners();
        titleLabel.setText("Группы с участием музыканта " + paneController.getMusician().getName());
    }
    
    private void setTableValue() {
        List<MusicianGroup> musicianGroups = repositoryService.getMusicianGroupRepository().selectJoinByMusician(paneController.getMusician());
        clearSelectionTable();
        artistTableView.getItems().clear();
        artistTableView.setItems(FXCollections.observableArrayList(musicianGroups));
        sort();
        Helper.setHeightTable(artistTableView, 10);        
    }
    
    private void initListeners() {
        artistTableView.getSelectionModel().selectedItemProperty().addListener(
            (observable, oldValue, newValue) -> selectedItem = artistTableView.getSelectionModel().getSelectedItem()
        );     
    }
    
    private void initRepositoryListeners() {
        //clear listeners
        repositoryService.getMusicianGroupRepository().clearChangeListeners(this);                         
        repositoryService.getArtistRepository().clearDeleteListeners(this);           
        repositoryService.getArtistRepository().clearUpdateListeners(this);      
        repositoryService.getMusicianRepository().clearDeleteListeners(this);           
        repositoryService.getMusicianRepository().clearUpdateListeners(this);
        
        //add listeners
        repositoryService.getMusicianGroupRepository().addChangeListener(this::changed, this);                         
        repositoryService.getArtistRepository().addDeleteListener(this::changed, this);           
        repositoryService.getArtistRepository().addUpdateListener(this::changed, this);      
        repositoryService.getMusicianRepository().addDeleteListener(this::changed, this);           
        repositoryService.getMusicianRepository().addUpdateListener(this::changed, this);
    }
    
    private void changed(ObservableValue observable, Object oldVal, Object newVal) {
        setTableValue();
    }
    
    private void sort() {
        clearSelectionTable();
        artistTableView.getItems().sort(Comparator.comparing(musicianGroup -> musicianGroup.getArtist().getName()));
    }
    
    private void clearSelectionTable() {
        artistTableView.getSelectionModel().clearSelection();
        selectedItem = null;
    }
    
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
                requestPageService.artistPane(artist);
            }
        }
        else if (mouseEvent.getButton() == MouseButton.SECONDARY) { 
            MusicianGroup newMusicianGroup = new MusicianGroup();
            newMusicianGroup.setId_musician(paneController.getMusician().getId());
            contextMenuService.add(ADD_MUSICIAN_GROUP, newMusicianGroup);                    
            if (selectedItem != null) {
                contextMenuService.add(EDIT_MUSICIAN_GROUP, selectedItem);
                contextMenuService.add(DELETE_MUSICIAN_GROUP, selectedItem);     
            }
            contextMenuService.show(paneController.getView(), mouseEvent);            
        }
    }  
    
}
