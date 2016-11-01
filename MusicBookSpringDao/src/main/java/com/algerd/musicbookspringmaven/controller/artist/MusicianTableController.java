package com.algerd.musicbookspringmaven.controller.artist;

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
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.TableCell;
import com.algerd.musicbookspringmaven.entity.Musician;
import com.algerd.musicbookspringmaven.entity.MusicianGroup;
import com.algerd.musicbookspringmaven.entity.MusicianInstrument;
import com.algerd.musicbookspringmaven.utils.Helper;
import static com.algerd.musicbookspringmaven.service.impl.ContextMenuItemType.ADD_MUSICIAN_GROUP;
import static com.algerd.musicbookspringmaven.service.impl.ContextMenuItemType.DELETE_MUSICIAN_GROUP;
import static com.algerd.musicbookspringmaven.service.impl.ContextMenuItemType.EDIT_MUSICIAN_GROUP;

public class MusicianTableController extends BaseIncludeController<ArtistPaneController> {
      
    private MusicianGroup selectedItem;
    
    @FXML
    private AnchorPane musicianTable;
    @FXML
    private TableView<MusicianGroup> musicianTableView;
    @FXML
    private TableColumn<MusicianGroup, String> musicianNameColumn;
    @FXML
    private TableColumn<MusicianGroup, MusicianGroup> musicianInstrumentColumn;
    @FXML
    private TableColumn<MusicianGroup, String> musicianStartDateColumn;
    @FXML
    private TableColumn<MusicianGroup, String> musicianEndDateColumn;
    @FXML
    private TableColumn<MusicianGroup, Integer> musicianRatingColumn; 
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        musicianNameColumn.setCellValueFactory(cellData -> cellData.getValue().getMusician().nameProperty());       
        musicianStartDateColumn.setCellValueFactory(cellData -> cellData.getValue().start_dateProperty());
        musicianEndDateColumn.setCellValueFactory(cellData -> cellData.getValue().end_dateProperty());
        musicianRatingColumn.setCellValueFactory(cellData -> cellData.getValue().getMusician().ratingProperty().asObject());
        
        musicianInstrumentColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue()));
        musicianInstrumentColumn.setCellFactory(col -> {
            TableCell<MusicianGroup, MusicianGroup> cell = new TableCell<MusicianGroup, MusicianGroup>() {
                @Override
                public void updateItem(MusicianGroup item, boolean empty) {
                    super.updateItem(item, empty);
                    this.setText(null);
                    if (!empty) {
                        List<MusicianInstrument> musicianInstruments = 
                            repositoryService.getMusicianInstrumentRepository().selectJoinByMusician(item.getMusician());
                        String str = "";
                        for (MusicianInstrument musicianInstrument :  musicianInstruments){
                            str += (!str.equals("")) ? ", " : "";
                            str += musicianInstrument.getInstrument().getName();
                        }
                        this.setText(str); 
                    }
                }
            };
			return cell;
        });
    }
    
    @Override
    public void bootstrap() {
        setTableValue();
        initListeners();
        initRepositoryListeners();    
    } 
    
    private void initRepositoryListeners() {
        repositoryService.getMusicianGroupRepository().clearChangeListeners(this);                
        repositoryService.getMusicianRepository().clearDeleteListeners(this);           
        repositoryService.getMusicianRepository().clearUpdateListeners(this);   
        repositoryService.getMusicianInstrumentRepository().clearChangeListeners(this);          
        
        repositoryService.getMusicianGroupRepository().addChangeListener(this::changed, this);                
        repositoryService.getMusicianRepository().addDeleteListener(this::changed, this);           
        repositoryService.getMusicianRepository().addUpdateListener(this::changed, this);   
        repositoryService.getMusicianInstrumentRepository().addChangeListener(this::changed, this);          
    }
    
    private void setTableValue() { 
        List<MusicianGroup> musicianGroups = repositoryService.getMusicianGroupRepository().selectJoinByArtist(paneController.getArtist());
        clearSelectionTable();
        musicianTableView.getItems().clear();
        musicianTableView.setItems(FXCollections.observableArrayList(musicianGroups));  
        sort();
        Helper.setHeightTable(musicianTableView, 10);
    }
    
    private void initListeners() {
        // Добавить слушателя на выбор элемента в таблице.
        musicianTableView.getSelectionModel().selectedItemProperty().addListener(
            (observable, oldValue, newValue) -> selectedItem = musicianTableView.getSelectionModel().getSelectedItem()
        );       
    }
 
    private void changed(ObservableValue observable, Object oldVal, Object newVal) {
        setTableValue();
    }
     
    private void sort() {
        clearSelectionTable();
        musicianTableView.getItems().sort(Comparator.comparing(musicianGroup -> musicianGroup.getMusician().getName()));
    } 
    
    void clearSelectionTable() {
        musicianTableView.getSelectionModel().clearSelection();
        selectedItem = null;
    }
    
    /**
     * ЛКМ - зызов окна выбранного музыканта;
     * ПКМ - вызов контекстного меню для add, edit, delete выбранного музыканта.
     */
    @FXML
    private void onMouseClickMusicianTable(MouseEvent mouseEvent) { 
        boolean isShowingContextMenu = contextMenuService.getContextMenu().isShowing();       
        contextMenuService.clear();   
        if (mouseEvent.getButton() == MouseButton.PRIMARY) {
            // если контекстное меню выбрано, то лкм сбрасывает контекстное меню и выбор в таблице
            if (isShowingContextMenu) {
                clearSelectionTable();
            }
            // если лкм выбрана запись - показать её
            if (selectedItem != null) {
                Musician musician = repositoryService.getMusicianRepository().selectById(selectedItem.getMusician().getId());
                requestPageService.musicianPane(musician);
            }           
        }
        else if (mouseEvent.getButton() == MouseButton.SECONDARY) { 
            MusicianGroup newMusicianGroup = new MusicianGroup();
            newMusicianGroup.setId_artist(paneController.getArtist().getId());       
            contextMenuService.add(ADD_MUSICIAN_GROUP, newMusicianGroup);

            if (selectedItem != null) {
                contextMenuService.add(EDIT_MUSICIAN_GROUP, selectedItem);
                contextMenuService.add(DELETE_MUSICIAN_GROUP, selectedItem);                       
            }
            contextMenuService.show(paneController.getView(), mouseEvent);   
        }
    }
    
}
