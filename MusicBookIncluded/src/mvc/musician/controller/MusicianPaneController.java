package mvc.musician.controller;

import db.entity.Artist;
import db.entity.Entity;
import db.entity.Musician;
import db.entity.MusicianGroup;
import mvc.VisiblePane;
import utils.Helper;
import utils.ImageUtil;
import java.io.File;
import java.net.URL;
import java.util.Comparator;
import java.util.Iterator;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import mvc.ContextMenuManager;
import static mvc.ContextMenuManager.ItemType.*;
import main.Main;
import main.Params;

public class MusicianPaneController implements Initializable, VisiblePane<Musician> {

    private Musician musician;
    private ArtistInfo selectedItem;   
    
    @FXML
    private AnchorPane musicianPane;
    /* ********** Details *********** */
    @FXML
    private ImageView musicianImageView;
    @FXML
    private Label nameLabel; 
    @FXML
    private Label typeLabel; 
    @FXML
    private Label dobLabel; 
    @FXML
    private Label dodLabel; 
    @FXML
    private Label countryLabel; 
    @FXML
    private Label ratingLabel; 
    @FXML
    private Text commentText;
    /* ********** Artists *********** */
    @FXML
    private TableView<ArtistInfo> artistTableView;
    @FXML
    private TableColumn<ArtistInfo, String> artistColumn;
    @FXML
    private TableColumn<ArtistInfo, String> startDateColumn;
    @FXML
    private TableColumn<ArtistInfo, String> endDateColumn;
    
    private class ArtistInfo {
        private Artist artist;
        private MusicianGroup musicianGroup;        
    }
       
    @Override
    public void initialize(URL url, ResourceBundle rb) {        
        initArtistTableView();
    }
    
    private void initArtistTableView() {           
        artistColumn.setCellValueFactory(cellData -> cellData.getValue().artist.nameProperty());
        startDateColumn.setCellValueFactory(cellData -> cellData.getValue().musicianGroup.startDateProperty());
        endDateColumn.setCellValueFactory(cellData -> cellData.getValue().musicianGroup.endDateProperty());
        
        //Добавить слушателей на выбор элемента.
        artistTableView.getSelectionModel().selectedItemProperty().addListener(
            (observable, oldValue, newValue) -> {
                selectedItem = artistTableView.getSelectionModel().getSelectedItem();
            }
        );
    }
    
    private void addMusicianGroupTableListeners() {
        // Добавить слущателей на редактирование MusicianGroup - обновить таблицу артистов при редактировании MusicianGroup
        Main.main.getLoader().getMusicianGroupTable().updatedEntityProperty().addListener(
            (observable, oldValue, Object) -> setTableValues()
        );
        // Добавить слущателей на удаление/добавление в MusicianGroup
        Main.main.getLoader().getMusicianGroupTable().getEntities().addListener(
            (ListChangeListener.Change<? extends Entity> change) -> {              
                while (change.next()) {
                    if (change.wasRemoved() || change.wasAdded()) {
                        setTableValues();
                    }
                }           
            }    
        );  
    }
    
    @Override
    public void show(Musician mus) {
        musician = mus; 
        addMusicianGroupTableListeners();
        showMusicianDetails();
        clearSelectionTable();
        setTableValues();
        musicianPane.setVisible(true);
    }
    
    @Override
    public void hide() {
        clearSelectionTable();
        musicianPane.setVisible(false);
    }
       
    private void setTableValues() {
        ObservableList<ArtistInfo> artistInfos = FXCollections.observableArrayList();
        Iterator<MusicianGroup> iterMusicianGroup = Main.main.getLoader().getMusicianGroupTable().getEntities().iterator();
        while (iterMusicianGroup.hasNext()) {
            MusicianGroup musicianGroup = iterMusicianGroup.next();
            if (musicianGroup.getIdMusician() == musician.getId()) {
                ArtistInfo artistInfo = new ArtistInfo();
                artistInfo.musicianGroup = musicianGroup;
                artistInfo.artist = Main.main.getLoader().getArtistTable().getEntityWithId(musicianGroup.getIdArtist());
                artistInfos.add(artistInfo);
            }          
        }
        artistInfos.sort(Comparator.comparing((ai) -> ai.artist.getName()));      
        artistTableView.setItems(artistInfos);
        Helper.setHeightTable(artistTableView);
    }
    
    private void clearSelectionTable() {
        artistTableView.getSelectionModel().clearSelection();
        selectedItem = null;
    }
    
    private void showMusicianDetails() {
        nameLabel.textProperty().bind(musician.nameProperty());
        typeLabel.textProperty().bind(musician.typeProperty()); 
        dobLabel.textProperty().bind(musician.dateOfBirthProperty());  
        dodLabel.textProperty().bind(musician.dateOfDeathProperty()); 
        countryLabel.textProperty().bind(musician.countryProperty());
        ratingLabel.textProperty().bind(musician.ratingProperty().asString());
        commentText.textProperty().bind(musician.descriptionProperty());
        // проверить и загрузить изображение
        File file = new File(Params.DIR_IMAGE_ENTITY + "musician/" + musician.getId() + "." + Params.SAVED_IMAGE_FORMAT);
        musicianImageView.setImage(ImageUtil.readImage(file));
        musician.changedImageProperty().addListener(
            (observable, newValue, oldValue) -> musicianImageView.setImage(ImageUtil.readImage(file))     
        );
    }
    
    /**
     * ЛКМ - зызов окна выбранного элемента;
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
                Main.main.getMainController().showTreeEntity(selectedItem.artist);
            }
        }
        else if (mouseEvent.getButton() == MouseButton.SECONDARY) { 
            showTableContextMenu(mouseEvent);
        }
    }
    
    /**
     * При ПКМ по таблице показать контекстное меню.
     */
    private void showTableContextMenu(MouseEvent mouseEvent) { 
        ContextMenuManager contextMenu = Main.main.getContextMenu();
        MusicianGroup newMusicianGroup = new MusicianGroup();
        newMusicianGroup.setIdMusician(musician.getId());
        contextMenu.add(ADD_MUSICIAN_GROUP, newMusicianGroup);                    
        if (selectedItem != null) {
            contextMenu.add(EDIT_MUSICIAN_GROUP, selectedItem.musicianGroup);
            contextMenu.add(DELETE_MUSICIAN_GROUP, selectedItem.musicianGroup);     
        }
        contextMenu.show(musicianPane, mouseEvent);            
    }
    
    /**
     * При ПКМ по странице показать контекстное меню.
     */
    @FXML
    private void showContextMenu(MouseEvent mouseEvent) {
        clearSelectionTable();
        ContextMenuManager contextMenu = Main.main.getContextMenu();
        contextMenu.clear();
		if (mouseEvent.getButton() == MouseButton.SECONDARY) {
            contextMenu.add(ADD_MUSICIAN, null);
            contextMenu.add(EDIT_MUSICIAN, musician);
            contextMenu.add(DELETE_MUSICIAN, musician);
            contextMenu.add(SEPARATOR);  
            MusicianGroup newMusicianGroup = new MusicianGroup();
            newMusicianGroup.setIdMusician(musician.getId());
            contextMenu.add(ADD_MUSICIAN_GROUP, newMusicianGroup);      
            contextMenu.show(musicianPane, mouseEvent);
        }      
    }
        
}

