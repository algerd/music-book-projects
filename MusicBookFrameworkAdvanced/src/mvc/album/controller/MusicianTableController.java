package mvc.album.controller;

import core.ContextMenuManager;
import static core.ContextMenuManager.ItemType.ADD_MUSICIAN_ALBUM;
import static core.ContextMenuManager.ItemType.DELETE_MUSICIAN_ALBUM;
import static core.ContextMenuManager.ItemType.EDIT_MUSICIAN_ALBUM;
import core.Main;
import core.RequestPage;
import db.entity.Musician;
import db.entity.MusicianAlbum;
import db.entity.MusicianInstrument;
import db.repository.RepositoryService;
import java.net.URL;
import java.util.Comparator;
import java.util.List;
import java.util.ResourceBundle;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
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
    
    private MusicianAlbum selectedItem;   
    private AlbumPaneController albumPaneController;
    
    @FXML
    private AnchorPane musicianTable;
    @FXML
    private TableView<MusicianAlbum> musicianTableView;
    @FXML
    private TableColumn<MusicianAlbum, String> musicianColumn;
    @FXML
    private TableColumn<MusicianAlbum, MusicianAlbum> instrumentColumn;   
    @FXML
    private TableColumn<MusicianAlbum, Integer> ratingColumn;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        //@Inject
        repositoryService = Main.getInstance().getRepositoryService();
        
        musicianColumn.setCellValueFactory(cellData -> cellData.getValue().getMusician().nameProperty());
        ratingColumn.setCellValueFactory(cellData -> cellData.getValue().getMusician().ratingProperty().asObject());
        
        instrumentColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue()));
        instrumentColumn.setCellFactory(col -> {
            TableCell<MusicianAlbum, MusicianAlbum> cell = new TableCell<MusicianAlbum, MusicianAlbum>() {
                @Override
                public void updateItem(MusicianAlbum item, boolean empty) {
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
    
    public void bootstrap(AlbumPaneController albumPaneController) {
        this.albumPaneController = albumPaneController;      
        setTableValue();
        initListeners();
        initRepositoryListeners();
    }
    
    private void setTableValue() {
        clearSelectionTable();
        musicianTableView.getItems().clear();
        List<MusicianAlbum> musicianAlbums = repositoryService.getMusicianAlbumRepository().selectJoinByAlbum(albumPaneController.getAlbum());              
        musicianTableView.setItems(FXCollections.observableArrayList(musicianAlbums));
        sort();
        Helper.setHeightTable(musicianTableView, 10);
    }
    
    private void initRepositoryListeners() {
        repositoryService.getMusicianAlbumRepository().clearChangeListeners(this);                
        repositoryService.getMusicianRepository().clearDeleteListeners(this);           
        repositoryService.getMusicianRepository().clearUpdateListeners(this);        
        repositoryService.getAlbumRepository().clearDeleteListeners(this);           
        repositoryService.getAlbumRepository().clearUpdateListeners(this);              
        repositoryService.getArtistRepository().clearDeleteListeners(this);
      
        repositoryService.getMusicianAlbumRepository().addChangeListener(this::changed, this);                
        repositoryService.getMusicianRepository().addDeleteListener(this::changed, this);           
        repositoryService.getMusicianRepository().addUpdateListener(this::changed, this);        
        repositoryService.getAlbumRepository().addDeleteListener(this::changed, this);           
        repositoryService.getAlbumRepository().addUpdateListener(this::changed, this);              
        repositoryService.getArtistRepository().addDeleteListener(this::changed, this);
    }
    
    private void initListeners() {
        // Добавить слушателя на выбор элемента в таблице.
        musicianTableView.getSelectionModel().selectedItemProperty().addListener(
            (observable, oldValue, newValue) -> selectedItem = musicianTableView.getSelectionModel().getSelectedItem()
        );       
    }
    
    // слушатель на изменение в таблицах
    private void changed(ObservableValue observable, Object oldVal, Object newVal) {
        setTableValue();
    }
    
    private void sort() {
        clearSelectionTable();
        musicianTableView.getItems().sort(Comparator.comparing((MusicianAlbum musicianAlbum) -> musicianAlbum.getMusician().getName()));
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
            MusicianAlbum newMusicianAlbum = new MusicianAlbum();
            newMusicianAlbum.setId_album(albumPaneController.getAlbum().getId());
            contextMenu.add(ADD_MUSICIAN_ALBUM, newMusicianAlbum);                    
            if (selectedItem != null) {
                contextMenu.add(EDIT_MUSICIAN_ALBUM, selectedItem);
                contextMenu.add(DELETE_MUSICIAN_ALBUM, selectedItem);     
            }
            contextMenu.show(albumPaneController.getAlbumPane(), mouseEvent);            
        }
    }  
    
}
