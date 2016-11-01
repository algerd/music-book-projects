package mvc.musician.controller;

import db.entity.Musician;
import utils.Helper;
import java.net.URL;
import java.util.Comparator;
import java.util.ResourceBundle;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import mvc.ContextMenuManager;
import static mvc.ContextMenuManager.ItemType.*;
import main.Main;

public class MusicianOverviewController implements Initializable {

    private Musician selectedItem;
    private ObservableList<Musician> musicians;
    
    @FXML
    private AnchorPane musicianOverview;
    @FXML
    private TableView<Musician> musicianOverviewTable;
    @FXML
    private TableColumn<Musician, String> nameColumn;
    @FXML
    private TableColumn<Musician, String> typeColumn;
    @FXML
    private TableColumn<Musician, Integer> ratingColumn;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        initOverviewTable();
        musicians = Main.main.getLoader().getMusicianTable().getEntities();
        musicians.sort(Comparator.comparing(Musician::getName));
        musicianOverviewTable.setItems(musicians); 
        Helper.setHeightTable(musicianOverviewTable);
    } 
    
    private void initOverviewTable() {
        nameColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        typeColumn.setCellValueFactory(cellData -> cellData.getValue().typeProperty());
        ratingColumn.setCellValueFactory(cellData -> cellData.getValue().ratingProperty().asObject());
        
        // Добавить слушателя на выбор элемента в таблице.
        musicianOverviewTable.getSelectionModel().selectedItemProperty().addListener(
            (observable, oldValue, newValue) -> {             
                selectedItem = musicianOverviewTable.getSelectionModel().getSelectedItem();
            }
        );
    }
    
    public void show() {
        Main.main.getMainController().hideAllPanes();
        Main.main.getMainController().getIncludedExplorerController().clearSelectionTree();
        clearSelectionTable();
        musicianOverview.setVisible(true);
    }
    
    public void hide() {       
        musicianOverview.setVisible(false);
        clearSelectionTable();
    }
    
    private void clearSelectionTable() {
        musicianOverviewTable.getSelectionModel().clearSelection();
        selectedItem = null;
    }
    
    /**
     * ЛКМ - зызов окна выбранного жанра;
     * ПКМ - вызов контекстного меню для add, edit, delete.
     */
    @FXML
    private void onMouseClickTable(MouseEvent mouseEvent) { 
        ContextMenuManager contextMenu = Main.main.getContextMenu();
        boolean isShowingContextMenu = contextMenu.getContextMenu().isShowing();     
        contextMenu.clear();        
        if (mouseEvent.getButton() == MouseButton.PRIMARY) {
            // если контекстное меню выбрано, то лкм сбрасывает контекстное меню и выбор в таблице
            if (isShowingContextMenu) {
                clearSelectionTable();
            }
            // если лкм выбрана запись - показать её
            if (selectedItem != null) {
                Main.main.getMainController().showEntity(selectedItem);
            }           
        }
        else if (mouseEvent.getButton() == MouseButton.SECONDARY) { 
            showTableContextMenu(mouseEvent);      
        }
    }
    
    /**
     * При ПКМ по таблице альбомов артиста показать контекстное меню.
     */
    private void showTableContextMenu(MouseEvent mouseEvent) { 
        ContextMenuManager contextMenu = Main.main.getContextMenu();
        contextMenu.add(ADD_MUSICIAN, null);
        if (selectedItem != null) {
            contextMenu.add(EDIT_MUSICIAN, selectedItem);
            contextMenu.add(DELETE_MUSICIAN, selectedItem);                       
        }
        contextMenu.show(musicianOverview, mouseEvent);      
    } 
    
    /**
     * При ПКМ по странице артиста показать контекстное меню.
     */
    @FXML
    private void showContextMenu(MouseEvent mouseEvent) {
        clearSelectionTable();
        ContextMenuManager contextMenu = Main.main.getContextMenu();
        contextMenu.clear();
		if (mouseEvent.getButton() == MouseButton.SECONDARY) {       
            contextMenu.add(ADD_MUSICIAN, null);
            contextMenu.show(musicianOverview, mouseEvent);
        }      
    }
    
}
