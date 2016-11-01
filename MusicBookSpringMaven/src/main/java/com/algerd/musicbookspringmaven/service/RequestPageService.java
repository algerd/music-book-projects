
package com.algerd.musicbookspringmaven.service;

import com.algerd.musicbookspringmaven.dbDriver.Entity;

public interface RequestPageService {
    
    void init();
    
    void albumPane(Entity entity);
    void albumsPane();
    void artistPane(Entity entity);
    void artistsPane();
    void genrePane(Entity entity);
    void genresPane();
    void instrumentPane(Entity entity);
    void instrumentsPane();
    void musicianPane(Entity entity);
    void musiciansPane();
    void songPane(Entity entity);
    void songsPane();
}
