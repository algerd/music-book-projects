package mvc.artist.controller;

import db.entity.ArtistReference;
import db.entity.Entity;
import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Iterator;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import main.Main;
import mvc.ContextMenuManager;
import static mvc.ContextMenuManager.ItemType.ADD_ARTIST_REFERENCE;
import static mvc.ContextMenuManager.ItemType.DELETE_ARTIST_REFERENCE;
import static mvc.ContextMenuManager.ItemType.EDIT_ARTIST_REFERENCE;
import utils.Helper;

public class ArtistReferenceTableController implements Initializable {
    
    private ArtistPaneController artistPaneController;
    private ArtistReference artistReferenceSelectedItem;

    @FXML
    private AnchorPane artistReferenceTable;
    @FXML
    private TableView<ArtistReference> artistReferenceTableView;
    @FXML
    private TableColumn<ArtistReference, String> nameReferenceColumn;
    @FXML
    private TableColumn<ArtistReference, String> referenceColumn;   
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        nameReferenceColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        referenceColumn.setCellValueFactory(cellData -> cellData.getValue().referenceProperty());
        // Добавить слушателя на выбор элемента в таблице.
        artistReferenceTableView.getSelectionModel().selectedItemProperty().addListener(
            (observable, oldValue, newValue) -> {             
                artistReferenceSelectedItem = artistReferenceTableView.getSelectionModel().getSelectedItem();
            }
        );  
    }
    
    public void bootstrap(ArtistPaneController artistPaneController) {
        this.artistPaneController = artistPaneController;
        addArtistReferenceTableLitener();
        clearSelectionArtistReferenceTable();
        setArtistReferenceTableValues();
    }

    private void addArtistReferenceTableLitener() {
        // Добавить слущателей на удаление/добавление в ArtistReference
        Main.main.getLoader().getArtistReferenceTable().getEntities().addListener(
            (ListChangeListener.Change<? extends Entity> change) -> {           
                while (change.next()) {
                    if (change.wasRemoved() || change.wasAdded()) {
                        setArtistReferenceTableValues();
                    }
                }   
            }    
        );
    }
    
    private void setArtistReferenceTableValues() {
        ObservableList<ArtistReference> artistReferences = FXCollections.observableArrayList();
        Iterator<ArtistReference> iterArtistReference = Main.main.getLoader().getArtistReferenceTable().getEntities().iterator();
        while (iterArtistReference.hasNext()) {
            ArtistReference reference = iterArtistReference.next();
            if (reference.getIdArtist()== artistPaneController.getArtist().getId()) {
                artistReferences.add(reference);
            }
        }
        artistReferenceTableView.setItems(artistReferences);
        Helper.setHeightTable(artistReferenceTableView);
    }

    public void clearSelectionArtistReferenceTable() {
        artistReferenceTableView.getSelectionModel().clearSelection();
        artistReferenceSelectedItem = null;
    }
    
    /**
     * ЛКМ - зызов окна выбранного альбома;
     * ПКМ - вызов контекстного меню для add, edit, delete выбранного альбома.
     */
    @FXML
    private void onMouseClickArtistReferenceTable(MouseEvent mouseEvent) { 
        ContextMenuManager contextMenu = Main.main.getContextMenu();
        boolean isShowingContextMenu = contextMenu.getContextMenu().isShowing();       
        contextMenu.clear();   
        if (mouseEvent.getButton() == MouseButton.PRIMARY) {
            // если контекстное меню выбрано, то лкм сбрасывает контекстное меню и выбор в таблице
            if (isShowingContextMenu) {
                clearSelectionArtistReferenceTable();
            }
            // если лкм выбрана запись - показать веб-страницу ссылки
            if (artistReferenceSelectedItem != null) {
                String errorMessage = "";                
                Desktop desktop = Desktop.getDesktop();  
                if (desktop.isSupported(Desktop.Action.BROWSE)) {  
                    try {  
                         desktop.browse(new URI(artistReferenceSelectedItem.getReference()));  
                    } catch (URISyntaxException | IOException ex) {  
                         errorMessage += "Сбой при попытке открытия ссылки.";  
                    }  
                } else {  
                    errorMessage += "На вашей платформе просмотр ссылок не из приложения не поддерживается";
                }                
                if (!errorMessage.equals("")) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.initOwner(Main.main.getPrimaryStage());
                    alert.setTitle("ERROR");
                    alert.setContentText(errorMessage);           
                    alert.showAndWait();    
                }               
            }           
        }
        else if (mouseEvent.getButton() == MouseButton.SECONDARY) { 
            showArtistReferenceTableContextMenu(mouseEvent);      
        }
    }
    
    /**
     * При ПКМ по таблице альбомов артиста показать контекстное меню.
     */
    private void showArtistReferenceTableContextMenu(MouseEvent mouseEvent) { 
        ContextMenuManager contextMenu = Main.main.getContextMenu();
        contextMenu.add(ADD_ARTIST_REFERENCE, new ArtistReference(artistPaneController.getArtist()));    
        if (artistReferenceSelectedItem != null) {
            contextMenu.add(EDIT_ARTIST_REFERENCE, artistReferenceSelectedItem);
            contextMenu.add(DELETE_ARTIST_REFERENCE, artistReferenceSelectedItem);                           
        }
        contextMenu.show(artistPaneController.getArtistPane(), mouseEvent);      
    }
    
    public void setArtistPaneController(ArtistPaneController artistPaneController) {
        this.artistPaneController = artistPaneController;
    }       
    
}
