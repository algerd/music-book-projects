
package com.algerd.musicbookspringmaven.service;

import com.algerd.musicbookspringmaven.controller.DialogController;
import com.algerd.musicbookspringmaven.dbDriver.Entity;

public interface RequestDialogService {
    
    void load(Class<? extends DialogController> type, Entity entity, double width, double heigth);
    void load(Class<? extends DialogController> type, Entity entity);
    
    void albumDialog(Entity entity, double width, double heigth);
    void albumDialog(Entity entity);
    
    void artistDialog(Entity entity, double width, double heigth);
    void artistDialog(Entity entity);
    
    void artistReferenceDialog(Entity entity, double width, double heigth);
    void artistReferenceDialog(Entity entity);
    
    void genreDialog(Entity entity, double width, double heigth);
    void genreDialog(Entity entity);
    
    void instrumentDialog(Entity entity, double width, double heigth);
    void instrumentDialog(Entity entity);
    
    void musicianDialog(Entity entity, double width, double heigth);
    void musicianDialog(Entity entity);
    
    void musicianAlbumDialog(Entity entity, double width, double heigth);
    void musicianAlbumDialog(Entity entity);
    
    void musicianGroupDialog(Entity entity, double width, double heigth);
    void musicianGroupDialog(Entity entity);
    
    void musicianSongDialog(Entity entity, double width, double heigth);
    void musicianSongDialog(Entity entity);
    
    void songDialog(Entity entity, double width, double heigth);
    void songDialog(Entity entity);
}
