
package core;

import db.driver.Entity;
import java.io.IOException;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;

public enum RequestPage {
    
    ALBUM_PANE      ("mvc/album/view/AlbumPane.fxml"),
    ALBUMS_PANE     ("mvc/albums/view/AlbumsPane.fxml", true),  
    ARTIST_PANE     ("mvc/artist/view/ArtistPane.fxml"),
    ARTISTS_PANE    ("mvc/artists/view/ArtistsPane.fxml", true),
    GENRE_PANE      ("mvc/genre/view/GenrePane.fxml"),
    GENRES_PANE      ("mvc/genres/view/GenresPane.fxml", true),
    MUSICIAN_PANE   ("mvc/musician/view/MusicianPane.fxml"),
    MUSICIANS_PANE   ("mvc/musicians/view/MusiciansPane.fxml", true),
    SONG_PANE       ("mvc/song/view/SongPane.fxml"),
    SONGS_PANE      ("mvc/songs/view/SongsPane.fxml", true),
    INSTRUMENTS_PANE("mvc/instruments/view/InstrumentsPane.fxml", true),
    INSTRUMENT_PANE ("mvc/instrument/view/InstrumentPane.fxml");
 
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
            loader.setLocation(RequestPage.class.getClassLoader().getResource(path));
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
            loader.setLocation(RequestPage.class.getClassLoader().getResource(path));
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
