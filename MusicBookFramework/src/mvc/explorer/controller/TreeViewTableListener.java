
package mvc.explorer.controller;

import db.entity.Album;
import db.entity.Artist;
import db.entity.Entity;
import db.entity.Song;
import db.entity.WrapChangedEntity;
import java.util.Comparator;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TreeView;
import core.Main;

public class TreeViewTableListener {
    
    private final ArtistTreeItem rootTreeItem;
    private final TreeView artistTree;
    
    public TreeViewTableListener(TreeView artistTree) {
        this.artistTree = artistTree;
        this.rootTreeItem = (ArtistTreeItem) artistTree.getRoot();
        init();    
    }
    
    public void init() {
        // добавить слушателей на добавление/удаление элементов в таблицах
        Main.getInstance().getDbLoader().getArtistTable().deletedProperty().addListener(this::deleted);       
        Main.getInstance().getDbLoader().getAlbumTable().deletedProperty().addListener(this::deleted);
        Main.getInstance().getDbLoader().getSongTable().deletedProperty().addListener(this::deleted);
              
        Main.getInstance().getDbLoader().getArtistTable().addedProperty().addListener(this::addedArtist);
        Main.getInstance().getDbLoader().getAlbumTable().addedProperty().addListener(this::addedAlbum);
        Main.getInstance().getDbLoader().getSongTable().addedProperty().addListener(this::addedSong); 
   
        Main.getInstance().getDbLoader().getArtistTable().updatedProperty().addListener(this::updatedArtistName);
        Main.getInstance().getDbLoader().getAlbumTable().updatedProperty().addListener(this::updatedAlbum);
        Main.getInstance().getDbLoader().getSongTable().updatedProperty().addListener(this::updatedSong);
    }
    
    private void updatedArtistName(ObservableValue<? extends Object> observable, Object oldVal, Object newVal) {
        Artist newEntity = ((WrapChangedEntity<Artist>) newVal).getNew();
        Artist oldEntity = ((WrapChangedEntity<Artist>) newVal).getOld();
        if (!newEntity.getName().equals(oldEntity.getName())) {
            rootTreeItem.getChildren().sort(Comparator.comparing(
                e -> ((Artist) e.getValue()).getName()
            ));
        }
    }
   
    private void updatedAlbum(ObservableValue<? extends Object> observable, Object oldVal, Object newVal) {
        deleted(observable, oldVal, newVal);
        addedAlbum(observable, oldVal, newVal);      
    }
    
    private void updatedSong(ObservableValue<? extends Object> observable, Object oldVal, Object newVal) {
        deleted(observable, oldVal, newVal);
        addedSong(observable, oldVal, newVal);      
    }
      
    private void addedArtist(ObservableValue<? extends Object> observable, Object oldValue, Object newValue) {        
        Entity entity = ((WrapChangedEntity<Entity>) newValue).getNew();        
        if (entity != null && entity instanceof Artist) {
            artistTree.getSelectionModel().clearSelection();
            ArtistTreeItem artistItem = new ArtistTreeItem((Entity) entity);
            rootTreeItem.getChildren().add(artistItem);
            rootTreeItem.getChildren().sort(Comparator.comparing(
                e -> ((Artist) e.getValue()).getName()
            ));
        }    
    }
    
    private void addedAlbum(ObservableValue<? extends Object> observable, Object oldValue, Object newValue) { 
        Entity entity = ((WrapChangedEntity<Entity>) newValue).getNew();
        if (entity != null && entity instanceof Album) {
            artistTree.getSelectionModel().clearSelection();
            Entity parentEntity = Main.getInstance().getDbLoader().getArtistTable().select(((Album) entity).getId_artist());
            searchTreeItem(rootTreeItem, parentEntity).reset();
        }    
    }
    
    private void addedSong(ObservableValue<? extends Object> observable, Object oldValue, Object newValue) {      
        Entity entity = ((WrapChangedEntity<Entity>) newValue).getNew();
        if (entity != null && entity instanceof Song) {
            artistTree.getSelectionModel().clearSelection();
            Entity parentEntity = Main.getInstance().getDbLoader().getAlbumTable().select(((Song) entity).getId_album());
            searchTreeItem(rootTreeItem, parentEntity).reset();
        }    
    }
     
    //TODO: после удаления записи надо вызывать isLeaf для случаев, когда все записи у родителя удаляются, а значок не удаляется
    private void deleted(ObservableValue<? extends Object> observable, Object oldValue, Object newValue) {
        Entity entity = ((WrapChangedEntity<Entity>) newValue).getNew();
        if (entity != null && entity instanceof Entity) {
            artistTree.getSelectionModel().clearSelection();        
            ArtistTreeItem removedItem = searchTreeItem(rootTreeItem, (Entity) entity);
            if (removedItem != null) {
                ArtistTreeItem parentRemovedItem = (ArtistTreeItem) removedItem.getParent();
                parentRemovedItem.getChildren().remove(removedItem);
                parentRemovedItem.setLeafPropertyComputed(false);
                parentRemovedItem.isLeaf();
                if (parentRemovedItem.getChildren().isEmpty() && parentRemovedItem.isExpanded()) {
                    parentRemovedItem.setExpanded(false);
                }
            } 
        }    
    }
       
    /**
     * Рекурсивно найти TreeItem сущности searchEntity из дерева rootItem.
     */
    private ArtistTreeItem searchTreeItem(ArtistTreeItem rootItem, Entity searchEntity) {
        ArtistTreeItem treeItem = null;
        for ( Object objectItem : rootItem.getChildren()) {
            ArtistTreeItem childItem = (ArtistTreeItem) objectItem;
            Entity childEntity = (Entity) childItem.getValue();
            if (childEntity.equals(searchEntity)) {  
                treeItem = childItem;             
                break;
            }
            if (!childItem.isLeaf()) {
                ArtistTreeItem deepChildItem = searchTreeItem(childItem, searchEntity);
                if (deepChildItem != null) {
                    treeItem = deepChildItem;
                    break;
                }               
            }           
        }
        return treeItem;     
    }
    
}
