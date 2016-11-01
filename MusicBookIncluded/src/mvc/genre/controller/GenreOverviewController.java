
package mvc.genre.controller;

import db.entity.Artist;
import db.entity.Genre;
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

public class GenreOverviewController implements Initializable {
    
    private Genre selectedItem;
    private ObservableList<Genre> genres;   
    
    @FXML
    private AnchorPane genreOverview;
    @FXML
    private TableView<Genre> genreOverviewTable;
    @FXML
    private TableColumn<Genre, String> nameColumn;
    @FXML
    private TableColumn<Genre, String> descriptionColumn;

    @Override
    public void initialize(URL url, ResourceBundle rb) { 
        initOverviewTable();
        genres = Main.main.getLoader().getGenreTable().getEntities();
        genres.sort(Comparator.comparing(Genre::getName));
        genreOverviewTable.setItems(genres); 
        Helper.setHeightTable(genreOverviewTable);
    }  
    
    private void initOverviewTable() {   
        nameColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());       
        descriptionColumn.setCellValueFactory(cellData -> cellData.getValue().descriptionProperty());      
    
        // Добавить слушателя на выбор элемента в таблице.
        genreOverviewTable.getSelectionModel().selectedItemProperty().addListener(
            (observable, oldValue, newValue) -> {             
                selectedItem = genreOverviewTable.getSelectionModel().getSelectedItem();
            }
        );
    }
    
    public void show() {
        Main.main.getMainController().hideAllPanes();
        Main.main.getMainController().getIncludedExplorerController().clearSelectionTree();
        clearSelectionTable();
        genreOverview.setVisible(true);
    }
    
    public void hide() {       
        genreOverview.setVisible(false);
        clearSelectionTable(); 
    } 
    
    private void clearSelectionTable() {
        genreOverviewTable.getSelectionModel().clearSelection();
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
        contextMenu.add(ADD_GENRE, null);
        // запретить удаление и редактирование записи с id = 1 (Unknown genre)
        if (selectedItem != null && selectedItem.getId() != 1) {
            contextMenu.add(EDIT_GENRE, selectedItem);
            contextMenu.add(DELETE_GENRE, selectedItem);                       
        }
        contextMenu.show(genreOverview, mouseEvent);      
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
            contextMenu.add(ADD_GENRE, null);
            contextMenu.show(genreOverview, mouseEvent);
        }      
    }
    
}
