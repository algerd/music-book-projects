package mvc.musician.controller;

import core.ContextMenuManager;
import static core.ContextMenuManager.ItemType.ADD_MUSICIAN_GROUP;
import static core.ContextMenuManager.ItemType.DELETE_MUSICIAN_GROUP;
import static core.ContextMenuManager.ItemType.EDIT_MUSICIAN_GROUP;
import core.Main;
import core.RequestPage;
import db.entity.Artist;
import db.entity.MusicianGroup;
import java.net.URL;
import java.util.Comparator;
import java.util.List;
import java.util.ResourceBundle;
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


public class ArtistTableController implements Initializable {
    
    private MusicianGroup selectedItem;   
    private MusicianPaneController musicianPaneController;
    
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
    
    public void bootstrap(MusicianPaneController musicianPaneController) {
        this.musicianPaneController = musicianPaneController;
        setTableValue();
        initListeners();
        titleLabel.setText("Группы с участием музыканта " + musicianPaneController.getMusician().getName());
    }
    
    private void setTableValue() {
        List<MusicianGroup> musicianGroups = Main.getInstance().getDbLoader().getMusicianGroupTable().selectJoinByMusician(musicianPaneController.getMusician());
        clearSelectionTable();
        artistTableView.getItems().clear();
        artistTableView.setItems(FXCollections.observableArrayList(musicianGroups));
        sort();
        Helper.setHeightTable(artistTableView, 10);        
    }
    
    private void initListeners() {
        //Добавить слушателей на выбор элемента.
        artistTableView.getSelectionModel().selectedItemProperty().addListener(
            (observable, oldValue, newValue) -> {
                selectedItem = artistTableView.getSelectionModel().getSelectedItem();
            }
        );
        Main.getInstance().getDbLoader().getMusicianGroupTable().addedProperty().addListener(this::changed);          
        Main.getInstance().getDbLoader().getMusicianGroupTable().deletedProperty().addListener(this::changed);           
        Main.getInstance().getDbLoader().getMusicianGroupTable().updatedProperty().addListener(this::changed);
                 
        Main.getInstance().getDbLoader().getArtistTable().deletedProperty().addListener(this::changed);           
        Main.getInstance().getDbLoader().getArtistTable().updatedProperty().addListener(this::changed);
        
        Main.getInstance().getDbLoader().getMusicianTable().deletedProperty().addListener(this::changed);           
        Main.getInstance().getDbLoader().getMusicianTable().updatedProperty().addListener(this::changed);
    }
    
    // слушатель на изменение в таблицах
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
            MusicianGroup newMusicianGroup = new MusicianGroup();
            newMusicianGroup.setId_musician(musicianPaneController.getMusician().getId());
            contextMenu.add(ADD_MUSICIAN_GROUP, newMusicianGroup);                    
            if (selectedItem != null) {
                contextMenu.add(EDIT_MUSICIAN_GROUP, selectedItem);
                contextMenu.add(DELETE_MUSICIAN_GROUP, selectedItem);     
            }
            contextMenu.show(musicianPaneController.getMusicianPane(), mouseEvent);            
        }
    }  
    
}
