package com.algerd.musicbookspringmaven.controller.genre;

import com.algerd.musicbookspringmaven.controller.BaseController;
import java.net.URL;
import java.util.Comparator;
import java.util.List;
import java.util.ResourceBundle;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import com.algerd.musicbookspringmaven.utils.Helper;
import com.algerd.musicbookspringmaven.core.Main;
import com.algerd.musicbookspringmaven.core.RequestPage;
import com.algerd.musicbookspringmaven.entity.Musician;
import com.algerd.musicbookspringmaven.entity.MusicianGenre;
import com.algerd.musicbookspringmaven.entity.MusicianInstrument;
import static com.algerd.musicbookspringmaven.core.ContextMenuManager.ItemType.ADD_MUSICIAN;
import static com.algerd.musicbookspringmaven.core.ContextMenuManager.ItemType.DELETE_MUSICIAN;
import static com.algerd.musicbookspringmaven.core.ContextMenuManager.ItemType.EDIT_MUSICIAN;

public class MusicianGenreTableController extends BaseController implements Initializable {
       
    private MusicianGenre selectedItem;
    private GenrePaneController genrePaneController;
    
    @FXML
    private AnchorPane musicianGenreTable;
    @FXML
    private Label titleLabel;        
    @FXML
    private TableView<MusicianGenre> musicianTableView;
    @FXML
    private TableColumn<MusicianGenre, Integer> rankColumn;
    @FXML
    private TableColumn<MusicianGenre, String> musicianColumn;
    @FXML
    private TableColumn<MusicianGenre, MusicianGenre> instrumentColumn;
    @FXML
    private TableColumn<MusicianGenre, Integer> ratingColumn;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        //@Inject
        repositoryService = Main.getInstance().getRepositoryService(); 
        //@Inject
        contextMenu = Main.getInstance().getContextMenu();
        
        rankColumn.setCellValueFactory(
            cellData -> new SimpleIntegerProperty(musicianTableView.getItems().indexOf(cellData.getValue()) + 1).asObject()
        );
        musicianColumn.setCellValueFactory(cellData ->cellData.getValue().getMusician().nameProperty());
        ratingColumn.setCellValueFactory(cellData ->cellData.getValue().getMusician().ratingProperty().asObject());
        
        instrumentColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue()));
        instrumentColumn.setCellFactory(col -> {
            TableCell<MusicianGenre, MusicianGenre> cell = new TableCell<MusicianGenre, MusicianGenre>() {
                @Override
                public void updateItem(MusicianGenre item, boolean empty) {
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
    
    public void bootstrap(GenrePaneController genrePaneController) {
        this.genrePaneController = genrePaneController;       
        setTableValue();
        initListeners();
        //??
        initRepositoryListeners(); 
        titleLabel.setText("Музыканты жанра " + genrePaneController.getGenre().getName());
    }
    
    private void initRepositoryListeners() {       
        repositoryService.getMusicianRepository().clearDeleteListeners(this);           
        repositoryService.getMusicianRepository().clearUpdateListeners(this);          
        repositoryService.getGenreRepository().clearDeleteListeners(this);           
        repositoryService.getGenreRepository().clearUpdateListeners(this);      
        repositoryService.getMusicianGenreRepository().clearChangeListeners(this);
        
        repositoryService.getMusicianRepository().addDeleteListener(this::changed, this);           
        repositoryService.getMusicianRepository().addUpdateListener(this::changed, this);          
        repositoryService.getGenreRepository().addDeleteListener(this::changed, this);           
        repositoryService.getGenreRepository().addUpdateListener(this::changed, this);      
        repositoryService.getMusicianGenreRepository().addChangeListener(this::changed, this);      
    }
    
    private void setTableValue() {
        clearSelectionTable();
        musicianTableView.getItems().clear();
        List<MusicianGenre> musicianGenres = repositoryService.getMusicianGenreRepository().selectJoinByGenre(genrePaneController.getGenre());
        musicianTableView.setItems(FXCollections.observableArrayList(musicianGenres));      
        sort();       
        Helper.setHeightTable(musicianTableView, 10);  
    }
    
    private void initListeners() {
        // Добавить слушателя на выбор элемента в таблице.
        musicianTableView.getSelectionModel().selectedItemProperty().addListener(
            (observable, oldValue, newValue) -> selectedItem = musicianTableView.getSelectionModel().getSelectedItem()
        );             
    }
    
    public void clearSelectionTable() {
        musicianTableView.getSelectionModel().clearSelection();
        selectedItem = null;
    }
    
    private void sort() {
        clearSelectionTable();
        musicianTableView.getItems().sort(Comparator.comparingInt((MusicianGenre musicianGenre) -> musicianGenre.getMusician().getRating()).reversed());
    }
    
    // слушатель на изменение в таблицах
    private void changed(ObservableValue observable, Object oldVal, Object newVal) {
        setTableValue();
    }
    
    /**
     * ЛКМ - зызов окна выбранного элемента;
     * ПКМ - вызов контекстного меню для add, edit, delete.
     */
    @FXML
    private void onMouseClickTable(MouseEvent mouseEvent) {
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
            contextMenu.add(ADD_MUSICIAN, new Musician());                    
            if (selectedItem != null) {
                Musician musician = repositoryService.getMusicianRepository().selectById(selectedItem.getMusician().getId());
                contextMenu.add(EDIT_MUSICIAN, musician);
                contextMenu.add(DELETE_MUSICIAN, musician);     
            }
            contextMenu.show(genrePaneController.getGenrePane(), mouseEvent);    
        }
    }
    
}
