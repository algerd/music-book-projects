
package com.algerd.musicbookspringmaven.core;

import com.algerd.musicbookspringmaven.dbDriver.Entity;
import java.io.IOException;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;
import static com.algerd.musicbookspringmaven.core.Params.DIR_FXML;

public enum RequestPage {
    
    ALBUM_PANE      ("album/AlbumPane.fxml"),
    ALBUMS_PANE     ("albums/AlbumsPane.fxml", true),  
    ARTIST_PANE     ("artist/ArtistPane.fxml"),
    ARTISTS_PANE    ("artists/ArtistsPane.fxml", true),
    GENRE_PANE      ("genre/GenrePane.fxml"),
    GENRES_PANE     ("genres/GenresPane.fxml", true),
    INSTRUMENT_PANE ("instrument/InstrumentPane.fxml"),
    INSTRUMENTS_PANE("instruments/InstrumentsPane.fxml", true),
    MUSICIAN_PANE   ("musician/MusicianPane.fxml"),
    MUSICIANS_PANE  ("musicians/MusiciansPane.fxml", true),
    SONG_PANE       ("song/SongPane.fxml"),
    SONGS_PANE      ("songs/SongsPane.fxml", true);

    private final String path;
    private AnchorPane page;
    private Loadable controller;
    private boolean isLazy = false;

    private RequestPage(String path) {
        this.path = path;
        initLoad();
    }
    
    private RequestPage(String path, boolean isLazy) {
        this.path = path;
        this.isLazy = isLazy;
        if (!isLazy) {
            initLoad();
        }
    }
    
    private void initLoad() {
         try {    
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(RequestPage.class.getResource(DIR_FXML + path));
            page = loader.<AnchorPane>load();
            controller = loader.getController();
        } 
        catch (IOException e) {
            e.printStackTrace();
        } 
    }
    
    /**
     * Загрузить страницу и контроллер и отобразить её в окне данных
     * @param entity 
     */
    public void load(Entity entity) {
        if (isLazy && page == null || controller == null) {
            initLoad();   
        }
        controller.show(entity);
        Main.getInstance().getMainController().show(page);
    }
    
    public void load() {
        RequestPage.this.load(null);
    }
    
    public void newLoad(Entity entity) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(RequestPage.class.getResource(DIR_FXML + path));
            AnchorPane page = loader.<AnchorPane>load();
            Loadable controller = loader.getController();
            controller.show(entity);
            Main.getInstance().getMainController().show(page);
        } 
        catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void newLoad() {
        newLoad(null);
    }  
       
    @Override
    public String toString() {
        return path;
    }
    
}
