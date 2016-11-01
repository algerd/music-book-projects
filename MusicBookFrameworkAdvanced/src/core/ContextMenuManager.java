
package core;

import db.entity.Album;
import db.entity.Artist;
import db.entity.ArtistReference;
import db.driver.Entity;
import db.entity.Genre;
import db.entity.Musician;
import db.entity.MusicianGroup;
import db.entity.Song;
import java.util.HashMap;
import java.util.Map;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import static core.RequestDialog.*;
import db.entity.Instrument;
import db.entity.MusicianAlbum;
import db.entity.MusicianSong;
import mvc.album.alert.AlbumDeleteAlert;
import mvc.artist.alert.ArtistDeleteAlert;
import mvc.artist.alert.ArtistReferenceDeleteAlert;
import mvc.genre.alert.GenreDeleteAlert;
import mvc.instrument.alert.InstrumentDeleteAlert;
import mvc.musician.alert.MusicianAlbumDeleteAlert;
import mvc.musician.alert.MusicianDeleteAlert;
import mvc.musician.alert.MusicianGroupDeleteAlert;
import mvc.musician.alert.MusicianSongDeleteAlert;
import mvc.song.alert.SongDeleteAlert;


public class ContextMenuManager {     
    public enum ItemType {
        ADD_ARTIST(new MenuItem("Add Artist")),
        EDIT_ARTIST(new MenuItem("Edit Artist")),
        DELETE_ARTIST(new MenuItem("Delete Artist")),
        
        ADD_ALBUM(new MenuItem("Add Album")),
        EDIT_ALBUM(new MenuItem("Edit Album")),
        DELETE_ALBUM(new MenuItem("Delete Album")),
        
        ADD_SONG(new MenuItem("Add Song")),
        EDIT_SONG(new MenuItem("Edit Song")),
        DELETE_SONG(new MenuItem("Delete Song")),
        
        ADD_GENRE(new MenuItem("Add Genre")),
        EDIT_GENRE(new MenuItem("Edit Genre")),
        DELETE_GENRE(new MenuItem("Delete Genre")),
        
        ADD_MUSICIAN(new MenuItem("Add Musician")),
        EDIT_MUSICIAN(new MenuItem("Edit Musician")),
        DELETE_MUSICIAN(new MenuItem("Delete Musician")),
        
        ADD_MUSICIAN_ALBUM(new MenuItem("Add Musician To Album")),
        EDIT_MUSICIAN_ALBUM(new MenuItem("Edit Musician In Album")),
        DELETE_MUSICIAN_ALBUM(new MenuItem("Delete Musician From Album")),
        
        ADD_MUSICIAN_GROUP(new MenuItem("Add Musician To Group")),
        EDIT_MUSICIAN_GROUP(new MenuItem("Edit Musician In Group")),
        DELETE_MUSICIAN_GROUP(new MenuItem("Delete Musician From Group")),
        
        ADD_MUSICIAN_SONG(new MenuItem("Add Musician To Song")),
        EDIT_MUSICIAN_SONG(new MenuItem("Edit Musician In Song")),
        DELETE_MUSICIAN_SONG(new MenuItem("Delete Musician From Song")),
        
        ADD_ARTIST_REFERENCE(new MenuItem("Add Artist Reference")),
        EDIT_ARTIST_REFERENCE(new MenuItem("Edit Artist Reference")),
        DELETE_ARTIST_REFERENCE(new MenuItem("Delete Artist Reference")),
        
        ADD_INSTRUMENT(new MenuItem("Add Instrument")),
        EDIT_INSTRUMENT(new MenuItem("Edit Instrument")),
        DELETE_INSTRUMENT(new MenuItem("Delete Instrument")),
        
        SEPARATOR(new SeparatorMenuItem());
        
        private final MenuItem item;
        
        private ItemType(MenuItem item) {
            this.item = item;
        }      
        public MenuItem get() {
            return item;
        }       
    }
    
    private final ContextMenu contextMenu = new ContextMenu();
    private final Map<ItemType, EventHandler<ActionEvent>> menuMap = new HashMap<>();
    private Map<ItemType, Entity> valueMap = new HashMap<>();
        
    public ContextMenuManager() {
        
        menuMap.put(ItemType.ADD_ARTIST, e -> ARTIST_DIALOG.load(valueMap.get(ItemType.ADD_ARTIST)));
        menuMap.put(ItemType.EDIT_ARTIST, e -> ARTIST_DIALOG.load(valueMap.get(ItemType.EDIT_ARTIST)));            
        menuMap.put(ItemType.DELETE_ARTIST, e -> ArtistDeleteAlert.show((Artist) valueMap.get(ItemType.DELETE_ARTIST)));      
        
        menuMap.put(ItemType.ADD_ALBUM, e -> ALBUM_DIALOG.load(valueMap.get(ItemType.ADD_ALBUM)));
        menuMap.put(ItemType.EDIT_ALBUM, e -> ALBUM_DIALOG.load(valueMap.get(ItemType.EDIT_ALBUM)));
        menuMap.put(ItemType.DELETE_ALBUM, e -> AlbumDeleteAlert.show((Album) valueMap.get(ItemType.DELETE_ALBUM)));             
        
        menuMap.put(ItemType.ADD_SONG, e -> SONG_DIALOG.load(valueMap.get(ItemType.ADD_SONG)));       
        menuMap.put(ItemType.EDIT_SONG, e -> SONG_DIALOG.load(valueMap.get(ItemType.EDIT_SONG)));        
        menuMap.put(ItemType.DELETE_SONG, e -> SongDeleteAlert.show((Song) valueMap.get(ItemType.DELETE_SONG)));       
        
        menuMap.put(ItemType.ADD_GENRE, e -> GENRE_DIALOG.load(valueMap.get(ItemType.ADD_GENRE)));
        menuMap.put(ItemType.EDIT_GENRE, e -> GENRE_DIALOG.load(valueMap.get(ItemType.EDIT_GENRE)));
        menuMap.put(ItemType.DELETE_GENRE, e -> GenreDeleteAlert.show((Genre) valueMap.get(ItemType.DELETE_GENRE)));     
        
        menuMap.put(ItemType.ADD_MUSICIAN, e -> MUSICIAN_DIALOG.load(valueMap.get(ItemType.ADD_MUSICIAN)));
        menuMap.put(ItemType.EDIT_MUSICIAN, e -> MUSICIAN_DIALOG.load(valueMap.get(ItemType.EDIT_MUSICIAN)));
        menuMap.put(ItemType.DELETE_MUSICIAN, e -> MusicianDeleteAlert.show((Musician) valueMap.get(ItemType.DELETE_MUSICIAN)));       
       
        menuMap.put(ItemType.ADD_MUSICIAN_ALBUM, e -> MUSICIAN_ALBUM_DIALOG.load(valueMap.get(ItemType.ADD_MUSICIAN_ALBUM)));
        menuMap.put(ItemType.EDIT_MUSICIAN_ALBUM, e -> MUSICIAN_ALBUM_DIALOG.load(valueMap.get(ItemType.EDIT_MUSICIAN_ALBUM)));
        menuMap.put(ItemType.DELETE_MUSICIAN_ALBUM, e -> MusicianAlbumDeleteAlert.show((MusicianAlbum) valueMap.get(ItemType.DELETE_MUSICIAN_ALBUM))); 
        
        menuMap.put(ItemType.ADD_MUSICIAN_GROUP, e -> MUSICIAN_GROUP_DIALOG.load(valueMap.get(ItemType.ADD_MUSICIAN_GROUP)));
        menuMap.put(ItemType.EDIT_MUSICIAN_GROUP, e -> MUSICIAN_GROUP_DIALOG.load(valueMap.get(ItemType.EDIT_MUSICIAN_GROUP)));
        menuMap.put(ItemType.DELETE_MUSICIAN_GROUP, e -> MusicianGroupDeleteAlert.show((MusicianGroup) valueMap.get(ItemType.DELETE_MUSICIAN_GROUP))); 
        
        menuMap.put(ItemType.ADD_MUSICIAN_SONG, e -> MUSICIAN_SONG_DIALOG.load(valueMap.get(ItemType.ADD_MUSICIAN_SONG)));
        menuMap.put(ItemType.EDIT_MUSICIAN_SONG, e -> MUSICIAN_SONG_DIALOG.load(valueMap.get(ItemType.EDIT_MUSICIAN_SONG)));
        menuMap.put(ItemType.DELETE_MUSICIAN_SONG, e -> MusicianSongDeleteAlert.show((MusicianSong) valueMap.get(ItemType.DELETE_MUSICIAN_SONG))); 
        
        menuMap.put(ItemType.ADD_ARTIST_REFERENCE, e -> ARTIST_REFERENCE_DIALOG.load(valueMap.get(ItemType.ADD_ARTIST_REFERENCE)));
        menuMap.put(ItemType.EDIT_ARTIST_REFERENCE, e -> ARTIST_REFERENCE_DIALOG.load(valueMap.get(ItemType.EDIT_ARTIST_REFERENCE)));
        menuMap.put(ItemType.DELETE_ARTIST_REFERENCE, e -> ArtistReferenceDeleteAlert.show((ArtistReference) valueMap.get(ItemType.DELETE_ARTIST_REFERENCE)));
    
        menuMap.put(ItemType.ADD_INSTRUMENT, e -> INSTRUMENT_DIALOG.load(valueMap.get(ItemType.ADD_INSTRUMENT)));
        menuMap.put(ItemType.EDIT_INSTRUMENT, e -> INSTRUMENT_DIALOG.load(valueMap.get(ItemType.EDIT_INSTRUMENT)));
        menuMap.put(ItemType.DELETE_INSTRUMENT, e -> InstrumentDeleteAlert.show((Instrument) valueMap.get(ItemType.DELETE_INSTRUMENT)));
    }
    
    /**
     * Добавить элемент меню и задать ему значение сущности entity
     * @param itemType Тип элемента меню.
     * @param entity Значение для элемента меню.
     */
    public void add(ItemType itemType, Entity entity) {
        // сохранить в карте переменных entity для элемента меню itemType
        valueMap.put(itemType, entity);
        // получить элемент меню
        MenuItem item = itemType.get();   
        // задать экшен элементу меню
        item.setOnAction(menuMap.get(itemType));
        // добавить в меню элемент
        contextMenu.getItems().add(item);   
    }
    
    public void add(ItemType itemType) {
        contextMenu.getItems().add(itemType.get());
    }
        
    public void show(Pane pane, MouseEvent mouseEvent) {
        contextMenu.show(pane, mouseEvent.getScreenX(), mouseEvent.getScreenY());
    }
      
    public void clear() {
        contextMenu.hide();
        contextMenu.getItems().clear();
        valueMap.clear();
    }

    public ContextMenu getContextMenu() {
        return contextMenu;
    }
    
}
