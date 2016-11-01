
package com.algerd.musicbookspringmaven.core;

import com.algerd.musicbookspringmaven.entity.Album;
import com.algerd.musicbookspringmaven.entity.Artist;
import com.algerd.musicbookspringmaven.entity.ArtistReference;
import com.algerd.musicbookspringmaven.entity.Genre;
import com.algerd.musicbookspringmaven.entity.Instrument;
import com.algerd.musicbookspringmaven.entity.Musician;
import com.algerd.musicbookspringmaven.entity.MusicianAlbum;
import com.algerd.musicbookspringmaven.entity.MusicianGroup;
import com.algerd.musicbookspringmaven.entity.MusicianSong;
import com.algerd.musicbookspringmaven.entity.Song;
import com.algerd.musicbookspringmaven.repository.RepositoryService;
import java.util.Optional;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

public class DeleteAlert {
    
    //@Inject
    private RepositoryService RepositoryService;
    
    public DeleteAlert() {
        //@Inject
        RepositoryService = Main.getInstance().getRepositoryService();
    }
    
    public void setRepositoryService(RepositoryService RepositoryService) {
        this.RepositoryService = RepositoryService;
    }
    
    public void show(Album album) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setContentText("Do you want to remove the album " + album.getName() + " ?");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK) {
            RepositoryService.getAlbumRepository().delete(album);
        }
    }
    
    public void show(Artist artist) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setContentText("Do you want to remove the artist " + artist.getName() + " ?");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK) {
           RepositoryService.getArtistRepository().delete(artist);
        }
    }
    
    public void show(Genre genre) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");       
        alert.setContentText("Do you want to remove the genre " + genre.getName() + " ?");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK) {
            RepositoryService.getGenreRepository().delete(genre);
        }
    }
    
    public void show(Instrument instrument) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");       
        alert.setContentText("Do you want to remove the instrument " + instrument.getName() + " ?");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK) {
            RepositoryService.getInstrumentRepository().delete(instrument);
        }
    }
    
    public void show(MusicianAlbum musicianAlbum) {
        Musician musician = RepositoryService.getMusicianRepository().selectById(musicianAlbum.getId_musician());
        Album album = RepositoryService.getAlbumRepository().selectById(musicianAlbum.getId_album());
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");        
        alert.setContentText("Do you want to remove the musician " + musician.getName() + " from " + album.getName() + " ?");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK) {
            RepositoryService.getMusicianAlbumRepository().delete(musicianAlbum);
        }
    }
    
    public void show(Musician musician) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");       
        alert.setContentText("Do you want to remove the musician " + musician.getName() + " ?");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK) {
            RepositoryService.getMusicianRepository().delete(musician);       
        }
    }
    
    public void show(MusicianSong musicianSong) {
        Musician musician = RepositoryService.getMusicianRepository().selectById(musicianSong.getId_musician());
        Song song = RepositoryService.getSongRepository().selectById(musicianSong.getId_song());
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");        
        alert.setContentText("Do you want to remove the musician " + musician.getName() + " from " + song.getName() + " ?");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK) {
            RepositoryService.getMusicianSongRepository().delete(musicianSong);
        }
    }
    
    public void show(Song song) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setContentText("Do you want to remove the song " + song.getName() + " ?");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK) {
            RepositoryService.getSongRepository().delete(song);
        }
    }
    
    public void show(MusicianGroup musicianGroup) {
        Musician musician = Main.getInstance().getRepositoryService().getMusicianRepository().selectById(musicianGroup.getId_musician());
        Artist artist = Main.getInstance().getRepositoryService().getArtistRepository().selectById(musicianGroup.getId_artist());
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");        
        alert.setContentText("Do you want to remove the musician " + musician.getName() + " from " + artist.getName() + " ?");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK) {
            RepositoryService.getMusicianGroupRepository().delete(musicianGroup);
        }
    }
    
    public void show(ArtistReference artistReference) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setContentText("Do you want to remove the artist reference: " + artistReference.getName() + " ?");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK) {
            RepositoryService.getArtistReferenceRepository().delete(artistReference);
        }
    }
     
}
