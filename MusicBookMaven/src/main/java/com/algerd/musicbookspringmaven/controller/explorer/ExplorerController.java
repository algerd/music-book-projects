package com.algerd.musicbookspringmaven.controller.explorer;

import com.algerd.musicbookspringmaven.controller.BaseController;
import java.net.URL;
import java.util.Comparator;
import java.util.List;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.util.Callback;
import com.algerd.musicbookspringmaven.entity.Album;
import com.algerd.musicbookspringmaven.entity.Artist;
import com.algerd.musicbookspringmaven.dbDriver.Entity;
import com.algerd.musicbookspringmaven.entity.Song;
import com.algerd.musicbookspringmaven.core.Main;
import com.algerd.musicbookspringmaven.core.RequestPage;
import static com.algerd.musicbookspringmaven.core.ContextMenuManager.ItemType.*;

public class ExplorerController extends BaseController implements Initializable {
     
    @FXML
    private AnchorPane explorer;
    @FXML
    private TreeView artistTree;

    @Override
    public void initialize(URL url, ResourceBundle rb) {        
        //@Inject
        repositoryService = Main.getInstance().getRepositoryService();  
        //@Inject
        contextMenu = Main.getInstance().getContextMenu();
        
        initTreeView();
    } 
    
    /**
     * Загрузить элементы в TreeView и задать CellFactory для требуемого отображения значений элементов.
     */
    private void initTreeView() {
        // Передать корневой элемент дереву 
        artistTree.setRoot(new ArtistTreeItem(null));        
        // Сделать невидимым корневой элемент       
        artistTree.setShowRoot(false);      
        //добавление элементов в дерево
        fillTreeItems();
        
        artistTree.setCellFactory(new Callback<TreeView, ArtistTreeCell>() {
            @Override
            public ArtistTreeCell call(TreeView tv) {
                return new ArtistTreeCell();
            }
        });               
        //  инициализировать слушателей таблиц
        new TreeViewTableListener(artistTree, repositoryService);
    }
       
    private void fillTreeItems() {      
        List<Artist> artists = repositoryService.getArtistRepository().selectAll();        
        artists.sort(Comparator.comparing(Artist::getName));       
        for (Artist artist : artists) {
            TreeItem artistItem = new ArtistTreeItem(artist);
            artistTree.getRoot().getChildren().add(artistItem); 
        }
    }
       
    /**
     * ЛКМ - зызов окна выбранной сущности
     * ПКМ - вызов контекстного меню.
     */
    @FXML
    private void onMouseClickTreeView(MouseEvent mouseEvent) {
        boolean isShowingContextMenu = contextMenu.getContextMenu().isShowing();       
        contextMenu.clear();
        if (mouseEvent.getButton() == MouseButton.PRIMARY) {
            // если контекстное меню выбрано, то лкм сбрасывает контекстное меню и выбор в дереве
            if (isShowingContextMenu) {
                artistTree.getSelectionModel().clearSelection();
            }
            // если лкм выбрана запись - показать её
            ArtistTreeItem selectedItem = (ArtistTreeItem) artistTree.getSelectionModel().getSelectedItem();
            if (selectedItem != null) {
                Entity entity = (Entity) selectedItem.getValue();
                if (entity instanceof Artist) {
                    RequestPage.ARTIST_PANE.load(entity);
                }
                else if(entity instanceof Album) {
                    RequestPage.ALBUM_PANE.load(entity);
                }
                else if (entity instanceof Song) {
                    RequestPage.SONG_PANE.load(entity);
                }               
            }            
        }
        else if (mouseEvent.getButton() == MouseButton.SECONDARY) { 
            showTreeContextMenu(mouseEvent);      
        }    
    }
    
    /**
     * При ПКМ по дереву показать контекстное меню.
     */
    private void showTreeContextMenu(MouseEvent mouseEvent) { 
        ArtistTreeItem selectedItem = (ArtistTreeItem) artistTree.getSelectionModel().getSelectedItem();
        if (selectedItem != null) { 
            Entity entity = (Entity) selectedItem.getValue();

            if (entity instanceof Artist) {
                Artist artist = (Artist) entity;
                contextMenu.add(ADD_ARTIST, new Artist());
                // запретить удаление и редактирование записи с id = 1 (Unknown artist)
                if (artist.getId() != 1) {
                    contextMenu.add(EDIT_ARTIST, artist);
                    contextMenu.add(DELETE_ARTIST, artist);
                    contextMenu.add(SEPARATOR);
                }
                Album newAlbum = new Album();
                newAlbum.setId_artist(artist.getId());  
                contextMenu.add(ADD_ALBUM, newAlbum);           
            }
            else if(entity instanceof Album) {   
                Album album = (Album) entity;
                Album newAlbum = new Album();
                newAlbum.setId_artist(album.getId_artist());
                contextMenu.add(ADD_ALBUM, newAlbum);
                // запретить удаление и редактирование записи с id = 1 (Unknown album)
                if (album.getId() != 1) {
                    contextMenu.add(EDIT_ALBUM, album);
                    contextMenu.add(DELETE_ALBUM, album);
                    contextMenu.add(SEPARATOR);
                }
                Song song = new Song();
                song.setId_album(album.getId());
                contextMenu.add(ADD_SONG, song);
            } 
            else if(entity instanceof Song) {
                Song song = (Song) entity;
                Song newSong = new Song();
                newSong.setId_album(song.getId_album());
                contextMenu.add(ADD_SONG, newSong);
                contextMenu.add(EDIT_SONG, song);
                contextMenu.add(DELETE_SONG, song);
            }                         
        }
        //Если не выбран элемент в дереве - предоставить меню: add artist
        else {
            artistTree.getSelectionModel().clearSelection();
            contextMenu.add(ADD_ARTIST, new Artist());
        }
        contextMenu.show(explorer, mouseEvent); 
    } 
            
}
