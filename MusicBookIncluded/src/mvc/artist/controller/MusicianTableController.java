package mvc.artist.controller;

import db.entity.Entity;
import db.entity.MusicianGroup;
import java.net.URL;
import java.util.Comparator;
import java.util.Iterator;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import main.Main;
import mvc.ContextMenuManager;
import static mvc.ContextMenuManager.ItemType.ADD_MUSICIAN_GROUP;
import static mvc.ContextMenuManager.ItemType.DELETE_MUSICIAN_GROUP;
import static mvc.ContextMenuManager.ItemType.EDIT_MUSICIAN_GROUP;
import mvc.artist.model.MusicianWrapper;
import utils.Helper;

public class MusicianTableController implements Initializable {
    
    private MusicianWrapper musicianSelectedItem;
    private ArtistPaneController artistPaneController;
    
    @FXML
    private AnchorPane musicianTable;
    @FXML
    private TableView<MusicianWrapper> musicianTableView;
    @FXML
    private TableColumn<MusicianWrapper, String> musicianNameColumn;
    @FXML
    private TableColumn<MusicianWrapper, String> musicianTypeColumn;
    @FXML
    private TableColumn<MusicianWrapper, String> musicianStartDateColumn;
    @FXML
    private TableColumn<MusicianWrapper, String> musicianEndDateColumn;
    @FXML
    private TableColumn<MusicianWrapper, Integer> musicianRatingColumn;  
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        musicianNameColumn.setCellValueFactory(cellData -> cellData.getValue().getMusician().nameProperty());
        musicianTypeColumn.setCellValueFactory(cellData -> cellData.getValue().getMusician().typeProperty());
        musicianStartDateColumn.setCellValueFactory(cellData -> cellData.getValue().getMusicianGroup().startDateProperty());
        musicianEndDateColumn.setCellValueFactory(cellData -> cellData.getValue().getMusicianGroup().endDateProperty());
        musicianRatingColumn.setCellValueFactory(cellData -> cellData.getValue().getMusician().ratingProperty().asObject());
        // Добавить слушателя на выбор элемента в таблице.
        musicianTableView.getSelectionModel().selectedItemProperty().addListener(
            (observable, oldValue, newValue) -> {             
                musicianSelectedItem = musicianTableView.getSelectionModel().getSelectedItem();
            }
        );
    }
    
    public void bootstrap(ArtistPaneController artistPaneController) {
        this.artistPaneController = artistPaneController;
        addMusicianGroupTableListener();
        clearSelectionMusicianTable();
        setMusicianTableValues();
    }
    
    private void addMusicianGroupTableListener() {
        // Добавить слущателей на редактирование MusicianGroup - обновить таблицу музыкантов при редактировании MusicianGroup
        Main.main.getLoader().getMusicianGroupTable().updatedEntityProperty().addListener(
            (observable, oldValue, Object) -> setMusicianTableValues()                     
        );
        // Добавить слущателей на удаление/добавление в MusicianGroup
        Main.main.getLoader().getMusicianGroupTable().getEntities().addListener(
            (ListChangeListener.Change<? extends Entity> change) -> {           
                while (change.next()) {
                    if (change.wasRemoved() || change.wasAdded()) {
                        setMusicianTableValues();
                    }
                }   
            }    
        );
    }
    
    private void setMusicianTableValues() { 
        ObservableList<MusicianWrapper> musicianWrappers = FXCollections.observableArrayList();
        Iterator<MusicianGroup> iterMusicianGroup = Main.main.getLoader().getMusicianGroupTable().getEntities().iterator();
        while (iterMusicianGroup.hasNext()) {
            MusicianGroup musicianGroup = iterMusicianGroup.next();
            if (musicianGroup.getIdArtist() == artistPaneController.getArtist().getId()) {
                MusicianWrapper musicianWrapper = new MusicianWrapper();
                musicianWrapper.setMusicianGroup(musicianGroup);
                musicianWrapper.setMusician(Main.main.getLoader().getMusicianTable().getEntityWithId(musicianGroup.getIdMusician()));
                musicianWrappers.add(musicianWrapper);
            }         
        }
        musicianWrappers.sort(Comparator.comparing((mw) -> mw.getMusician().getName()));  
        musicianTableView.setItems(musicianWrappers);        
        Helper.setHeightTable(musicianTableView);
    }
    
    void clearSelectionMusicianTable() {
        musicianTableView.getSelectionModel().clearSelection();
        musicianSelectedItem = null;
    }
    
    /**
     * ЛКМ - зызов окна выбранного музыканта;
     * ПКМ - вызов контекстного меню для add, edit, delete выбранного музыканта.
     */
    @FXML
    private void onMouseClickMusicianTable(MouseEvent mouseEvent) { 
        ContextMenuManager contextMenu = Main.main.getContextMenu();
        boolean isShowingContextMenu = contextMenu.getContextMenu().isShowing();       
        contextMenu.clear();   
        if (mouseEvent.getButton() == MouseButton.PRIMARY) {
            // если контекстное меню выбрано, то лкм сбрасывает контекстное меню и выбор в таблице
            if (isShowingContextMenu) {
                clearSelectionMusicianTable();
            }
            // если лкм выбрана запись - показать её
            if (musicianSelectedItem != null) {
                Main.main.getMainController().showEntity(musicianSelectedItem.getMusician());
            }           
        }
        else if (mouseEvent.getButton() == MouseButton.SECONDARY) { 
            showMusicianTableContextMenu(mouseEvent);      
        }
    }
    
    /**
     * При ПКМ по таблице музыкантов показать контекстное меню.
     */
    private void showMusicianTableContextMenu(MouseEvent mouseEvent) { 
        ContextMenuManager contextMenu = Main.main.getContextMenu();
        MusicianGroup newMusicianGroup = new MusicianGroup();
        newMusicianGroup.setIdArtist(artistPaneController.getArtist().getId());       
        contextMenu.add(ADD_MUSICIAN_GROUP, newMusicianGroup);
        
        if (musicianSelectedItem != null) {
            contextMenu.add(EDIT_MUSICIAN_GROUP, musicianSelectedItem.getMusicianGroup());
            contextMenu.add(DELETE_MUSICIAN_GROUP, musicianSelectedItem.getMusicianGroup());                       
        }
        contextMenu.show(artistPaneController.getArtistPane(), mouseEvent);      
    } 

    void setArtistPaneController(ArtistPaneController artistPaneController) {
        this.artistPaneController = artistPaneController;
    }
        
}
