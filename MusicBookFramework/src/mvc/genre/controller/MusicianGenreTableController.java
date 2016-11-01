package mvc.genre.controller;

import core.ContextMenuManager;
import static core.ContextMenuManager.ItemType.ADD_MUSICIAN;
import static core.ContextMenuManager.ItemType.DELETE_MUSICIAN;
import static core.ContextMenuManager.ItemType.EDIT_MUSICIAN;
import core.Main;
import core.RequestPage;
import db.entity.Musician;
import db.entity.MusicianGenre;
import db.entity.MusicianInstrument;
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
import utils.Helper;

public class MusicianGenreTableController implements Initializable {
    
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
                            Main.getInstance().getDbLoader().getMusicianInstrumentTable().selectJoinByMusician(item.getMusician());
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
        titleLabel.setText("Музыканты жанра " + genrePaneController.getGenre().getName());
    }
    
    private void setTableValue() {
        clearSelectionTable();
        musicianTableView.getItems().clear();
        List<MusicianGenre> musicianGenres = Main.getInstance().getDbLoader().getMusicianGenreTable().selectJoinByGenre(genrePaneController.getGenre());
        musicianTableView.setItems(FXCollections.observableArrayList(musicianGenres));      
        sort();       
        Helper.setHeightTable(musicianTableView, 10);  
    }
    
    private void initListeners() {
        // Добавить слушателя на выбор элемента в таблице.
        musicianTableView.getSelectionModel().selectedItemProperty().addListener(
            (observable, oldValue, newValue) -> {             
                selectedItem = musicianTableView.getSelectionModel().getSelectedItem();
            }
        );             
        Main.getInstance().getDbLoader().getMusicianTable().deletedProperty().addListener(this::changed);           
        Main.getInstance().getDbLoader().getMusicianTable().updatedProperty().addListener(this::changed);
            
        Main.getInstance().getDbLoader().getGenreTable().deletedProperty().addListener(this::changed);           
        Main.getInstance().getDbLoader().getGenreTable().updatedProperty().addListener(this::changed);
        
        Main.getInstance().getDbLoader().getMusicianGenreTable().addedProperty().addListener(this::changed);      
        Main.getInstance().getDbLoader().getMusicianGenreTable().deletedProperty().addListener(this::changed);           
        Main.getInstance().getDbLoader().getMusicianGenreTable().updatedProperty().addListener(this::changed);
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
                Musician musician = Main.getInstance().getDbLoader().getMusicianTable().select(selectedItem.getMusician().getId());
                RequestPage.MUSICIAN_PANE.load(musician);
            }
        }
        else if (mouseEvent.getButton() == MouseButton.SECONDARY) { 
            contextMenu.add(ADD_MUSICIAN, new Musician());                    
            if (selectedItem != null) {
                Musician musician = Main.getInstance().getDbLoader().getMusicianTable().select(selectedItem.getMusician().getId());
                contextMenu.add(EDIT_MUSICIAN, musician);
                contextMenu.add(DELETE_MUSICIAN, musician);     
            }
            contextMenu.show(genrePaneController.getGenrePane(), mouseEvent);    
        }
    }
    
}
