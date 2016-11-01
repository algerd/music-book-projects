package com.algerd.musicbookspringmaven.controller.explorer;

import com.algerd.musicbookspringmaven.controller.BaseAwareController;
import com.algerd.musicbookspringmaven.service.RequestPageService;
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
import static com.algerd.musicbookspringmaven.service.impl.ContextMenuItemType.*;
import org.springframework.beans.factory.annotation.Autowired;

public class ExplorerController extends BaseAwareController implements Initializable {
    
    @Autowired
    private RequestPageService requestPageService;
            
    @FXML
    private AnchorPane explorer;
    @FXML
    private TreeView artistTree;

    @Override
    public void initialize(URL url, ResourceBundle rb) {                
        initTreeView();
    } 
    
    /**
     * Загрузить элементы в TreeView и задать CellFactory для требуемого отображения значений элементов.
     */
    private void initTreeView() {
        // Передать корневой элемент дереву 
        artistTree.setRoot(new ArtistTreeItem(null, repositoryService));        
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
            TreeItem artistItem = new ArtistTreeItem(artist, repositoryService);
            artistTree.getRoot().getChildren().add(artistItem); 
        }
    }
       
    /**
     * ЛКМ - зызов окна выбранной сущности
     * ПКМ - вызов контекстного меню.
     */
    @FXML
    private void onMouseClickTreeView(MouseEvent mouseEvent) {
        boolean isShowingContextMenu = contextMenuService.getContextMenu().isShowing();       
        contextMenuService.clear();
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
                    requestPageService.artistPane(entity);
                }
                else if(entity instanceof Album) {
                    requestPageService.albumPane(entity);
                }
                else if (entity instanceof Song) {
                    requestPageService.songPane(entity);
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
                contextMenuService.add(ADD_ARTIST, new Artist());
                // запретить удаление и редактирование записи с id = 1 (Unknown artist)
                if (artist.getId() != 1) {
                    contextMenuService.add(EDIT_ARTIST, artist);
                    contextMenuService.add(DELETE_ARTIST, artist);
                    contextMenuService.add(SEPARATOR);
                }
                Album newAlbum = new Album();
                newAlbum.setId_artist(artist.getId());  
                contextMenuService.add(ADD_ALBUM, newAlbum);           
            }
            else if(entity instanceof Album) {   
                Album album = (Album) entity;
                Album newAlbum = new Album();
                newAlbum.setId_artist(album.getId_artist());
                contextMenuService.add(ADD_ALBUM, newAlbum);
                // запретить удаление и редактирование записи с id = 1 (Unknown album)
                if (album.getId() != 1) {
                    contextMenuService.add(EDIT_ALBUM, album);
                    contextMenuService.add(DELETE_ALBUM, album);
                    contextMenuService.add(SEPARATOR);
                }
                Song song = new Song();
                song.setId_album(album.getId());
                contextMenuService.add(ADD_SONG, song);
            } 
            else if(entity instanceof Song) {
                Song song = (Song) entity;
                Song newSong = new Song();
                newSong.setId_album(song.getId_album());
                contextMenuService.add(ADD_SONG, newSong);
                contextMenuService.add(EDIT_SONG, song);
                contextMenuService.add(DELETE_SONG, song);
            }                         
        }
        //Если не выбран элемент в дереве - предоставить меню: add artist
        else {
            artistTree.getSelectionModel().clearSelection();
            contextMenuService.add(ADD_ARTIST, new Artist());
        }
        contextMenuService.show(explorer, mouseEvent); 
    } 
            
}
