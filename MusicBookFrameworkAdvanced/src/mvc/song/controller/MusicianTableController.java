package mvc.song.controller;

import core.ContextMenuManager;
import static core.ContextMenuManager.ItemType.ADD_MUSICIAN_SONG;
import static core.ContextMenuManager.ItemType.DELETE_MUSICIAN_SONG;
import static core.ContextMenuManager.ItemType.EDIT_MUSICIAN_SONG;
import core.Main;
import core.RequestPage;
import db.entity.Musician;
import db.entity.MusicianInstrument;
import db.entity.MusicianSong;
import db.repository.RepositoryService;
import java.net.URL;
import java.util.Comparator;
import java.util.List;
import java.util.ResourceBundle;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import utils.Helper;

public class MusicianTableController implements Initializable {
    
    //@Inject
    private RepositoryService repositoryService; 
    
    private MusicianSong selectedItem;   
    private SongPaneController songPaneController;
    
    @FXML
    private AnchorPane musicianTable;
    @FXML
    private TableView<MusicianSong> musicianTableView;
    @FXML
    private TableColumn<MusicianSong, String> musicianColumn;
    @FXML
    private TableColumn<MusicianSong, MusicianSong> instrumentColumn;   
    @FXML
    private TableColumn<MusicianSong, Integer> ratingColumn;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        //@Inject
        repositoryService = Main.getInstance().getRepositoryService();
        
        musicianColumn.setCellValueFactory(cellData -> cellData.getValue().getMusician().nameProperty());
        ratingColumn.setCellValueFactory(cellData -> cellData.getValue().getMusician().ratingProperty().asObject());
        
        instrumentColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue()));
        instrumentColumn.setCellFactory(col -> {
            TableCell<MusicianSong, MusicianSong> cell = new TableCell<MusicianSong, MusicianSong>() {
                @Override
                public void updateItem(MusicianSong item, boolean empty) {
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
    
    public void bootstrap(SongPaneController songPaneController) {
        this.songPaneController = songPaneController;      
        setTableValue();
        initListeners();
        initRepositoryListeners();
    }
    
    private void setTableValue() {
        ObservableList<MusicianSong> musicianSongs = 
            FXCollections.observableArrayList(repositoryService.getMusicianSongRepository().selectJoinBySong(songPaneController.getSong()));
        clearSelectionTable();
        musicianTableView.getItems().clear();
        musicianTableView.setItems(musicianSongs);
        sort();
        Helper.setHeightTable(musicianTableView, 10);
    }
    
    private void initListeners() {
        // Добавить слушателя на выбор элемента в таблице.
        musicianTableView.getSelectionModel().selectedItemProperty().addListener(
            (observable, oldValue, newValue) -> selectedItem = musicianTableView.getSelectionModel().getSelectedItem()
        );
    }
    
    private void initRepositoryListeners() {
        //clear listeners
        repositoryService.getMusicianSongRepository().clearChangeListeners(this);                          
        repositoryService.getMusicianRepository().clearDeleteListeners(this);           
        repositoryService.getMusicianRepository().clearUpdateListeners(this);       
        repositoryService.getSongRepository().clearDeleteListeners(this);           
        repositoryService.getSongRepository().clearUpdateListeners(this);       
        repositoryService.getAlbumRepository().clearDeleteListeners(this);
        repositoryService.getArtistRepository().clearDeleteListeners(this);
        
        //add listeners
        repositoryService.getMusicianSongRepository().addChangeListener(this::changed, this);                          
        repositoryService.getMusicianRepository().addDeleteListener(this::changed, this);           
        repositoryService.getMusicianRepository().addUpdateListener(this::changed, this);       
        repositoryService.getSongRepository().addDeleteListener(this::changed, this);           
        repositoryService.getSongRepository().addUpdateListener(this::changed, this);       
        repositoryService.getAlbumRepository().addDeleteListener(this::changed, this);
        repositoryService.getArtistRepository().addDeleteListener(this::changed, this);
    }
    
    private void changed(ObservableValue observable, Object oldVal, Object newVal) {
        setTableValue();
    }
    
    private void sort() {
        clearSelectionTable();
        musicianTableView.getItems().sort(Comparator.comparing((MusicianSong musicianSong) -> musicianSong.getMusician().getName()));
    }
    
    private void clearSelectionTable() {
        musicianTableView.getSelectionModel().clearSelection();
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
                Musician musician = repositoryService.getMusicianRepository().selectById(selectedItem.getMusician().getId());
                RequestPage.MUSICIAN_PANE.load(musician);
            }
        }
        else if (mouseEvent.getButton() == MouseButton.SECONDARY) { 
            MusicianSong newMusicianSong = new MusicianSong();
            newMusicianSong.setId_song(songPaneController.getSong().getId());
            contextMenu.add(ADD_MUSICIAN_SONG, newMusicianSong);                    
            if (selectedItem != null) {
                contextMenu.add(EDIT_MUSICIAN_SONG, selectedItem);
                contextMenu.add(DELETE_MUSICIAN_SONG, selectedItem);     
            }
            contextMenu.show(songPaneController.getSongPane(), mouseEvent);            
        }
    }  
    
}
