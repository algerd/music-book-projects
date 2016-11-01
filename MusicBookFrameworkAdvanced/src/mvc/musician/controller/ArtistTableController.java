package mvc.musician.controller;

import core.ContextMenuManager;
import static core.ContextMenuManager.ItemType.ADD_MUSICIAN_GROUP;
import static core.ContextMenuManager.ItemType.DELETE_MUSICIAN_GROUP;
import static core.ContextMenuManager.ItemType.EDIT_MUSICIAN_GROUP;
import core.Main;
import core.RequestPage;
import db.entity.Artist;
import db.entity.MusicianGroup;
import db.repository.RepositoryService;
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
    
    //@Inject
    private RepositoryService repositoryService;
    
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
        //@Inject
        repositoryService = Main.getInstance().getRepositoryService();  
        
        artistColumn.setCellValueFactory(cellData -> cellData.getValue().getArtist().nameProperty());
        startDateColumn.setCellValueFactory(cellData -> cellData.getValue().start_dateProperty());
        endDateColumn.setCellValueFactory(cellData -> cellData.getValue().end_dateProperty());                
        ratingColumn.setCellValueFactory(cellData -> cellData.getValue().getArtist().ratingProperty().asObject());
    }
    
    public void bootstrap(MusicianPaneController musicianPaneController) {
        this.musicianPaneController = musicianPaneController;
        setTableValue();
        initListeners();
        initRepositoryListeners();
        titleLabel.setText("Группы с участием музыканта " + musicianPaneController.getMusician().getName());
    }
    
    private void setTableValue() {
        List<MusicianGroup> musicianGroups = repositoryService.getMusicianGroupRepository().selectJoinByMusician(musicianPaneController.getMusician());
        clearSelectionTable();
        artistTableView.getItems().clear();
        artistTableView.setItems(FXCollections.observableArrayList(musicianGroups));
        sort();
        Helper.setHeightTable(artistTableView, 10);        
    }
    
    private void initListeners() {
        //Добавить слушателей на выбор элемента.
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
                Artist artist = Main.getInstance().getRepositoryService().getArtistRepository().selectById(selectedItem.getArtist().getId());
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
