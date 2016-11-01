
package com.algerd.musicbookspringmaven.controller.explorer;

import java.util.Comparator;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TreeView;
import com.algerd.musicbookspringmaven.entity.Album;
import com.algerd.musicbookspringmaven.entity.Artist;
import com.algerd.musicbookspringmaven.dbDriver.Entity;
import com.algerd.musicbookspringmaven.entity.Song;
import com.algerd.musicbookspringmaven.dbDriver.impl.WrapChangedEntity;
import com.algerd.musicbookspringmaven.service.RepositoryService;

public class TreeViewTableListener {
    
    private final RepositoryService repositoryService;
    private final ArtistTreeItem rootTreeItem;
    private final TreeView artistTree;
    
    public TreeViewTableListener(TreeView artistTree, RepositoryService repositoryService) {
        this.artistTree = artistTree;
        this.rootTreeItem = (ArtistTreeItem) artistTree.getRoot();
        this.repositoryService = repositoryService;
        init();    
    }
    
    private void init() {
        //remove
        repositoryService.getArtistRepository().clearDeleteListeners(this);       
        repositoryService.getAlbumRepository().clearDeleteListeners(this);
        repositoryService.getSongRepository().clearDeleteListeners(this);
              
        repositoryService.getArtistRepository().clearInsertListeners(this);
        repositoryService.getAlbumRepository().clearInsertListeners(this);
        repositoryService.getSongRepository().clearInsertListeners(this); 
   
        repositoryService.getArtistRepository().clearUpdateListeners(this);
        repositoryService.getAlbumRepository().clearUpdateListeners(this);
        repositoryService.getSongRepository().clearUpdateListeners(this);
        
        //add
        repositoryService.getArtistRepository().addDeleteListener(this::deleted, this);       
        repositoryService.getAlbumRepository().addDeleteListener(this::deleted, this);
        repositoryService.getSongRepository().addDeleteListener(this::deleted, this);
              
        repositoryService.getArtistRepository().addInsertListener(this::addedArtist, this);
        repositoryService.getAlbumRepository().addInsertListener(this::addedAlbum, this);
        repositoryService.getSongRepository().addInsertListener(this::addedSong, this); 
   
        repositoryService.getArtistRepository().addUpdateListener(this::updatedArtistName, this);
        repositoryService.getAlbumRepository().addUpdateListener(this::updatedAlbum, this);
        repositoryService.getSongRepository().addUpdateListener(this::updatedSong, this);
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
            ArtistTreeItem artistItem = new ArtistTreeItem((Entity) entity, repositoryService);
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
            Entity parentEntity = repositoryService.getArtistRepository().selectById(((Album) entity).getId_artist());
            searchTreeItem(rootTreeItem, parentEntity).reset();
        }    
    }
    
    private void addedSong(ObservableValue<? extends Object> observable, Object oldValue, Object newValue) {      
        Entity entity = ((WrapChangedEntity<Entity>) newValue).getNew();
        if (entity != null && entity instanceof Song) {
            artistTree.getSelectionModel().clearSelection();
            Entity parentEntity = repositoryService.getAlbumRepository().selectById(((Song) entity).getId_album());
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
