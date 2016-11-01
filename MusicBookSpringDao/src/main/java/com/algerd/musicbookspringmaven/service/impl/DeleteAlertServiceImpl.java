
package com.algerd.musicbookspringmaven.service.impl;

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
import com.algerd.musicbookspringmaven.service.RepositoryService;
import com.algerd.musicbookspringmaven.service.DeleteAlertService;
import java.util.Optional;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import org.springframework.beans.factory.annotation.Autowired;

public class DeleteAlertServiceImpl implements DeleteAlertService {
    
    @Autowired
    private RepositoryService repositoryService;
    
    public DeleteAlertServiceImpl() {     
    }
    
    public void setRepositoryService(RepositoryService repositoryService) {
        this.repositoryService = repositoryService;
    }
    
    @Override
    public void show(Album album) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setContentText("Do you want to remove the album " + album.getName() + " ?");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK) {
            repositoryService.getAlbumRepository().delete(album);
        }
    }
    
    @Override
    public void show(Artist artist) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setContentText("Do you want to remove the artist " + artist.getName() + " ?");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK) {
           repositoryService.getArtistRepository().delete(artist);
        }
    }
    
    @Override
    public void show(Genre genre) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");       
        alert.setContentText("Do you want to remove the genre " + genre.getName() + " ?");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK) {
            repositoryService.getGenreRepository().delete(genre);
        }
    }
    
    @Override
    public void show(Instrument instrument) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");       
        alert.setContentText("Do you want to remove the instrument " + instrument.getName() + " ?");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK) {
            repositoryService.getInstrumentRepository().delete(instrument);
        }
    }
    
    @Override
    public void show(MusicianAlbum musicianAlbum) {
        Musician musician = repositoryService.getMusicianRepository().selectById(musicianAlbum.getId_musician());
        Album album = repositoryService.getAlbumRepository().selectById(musicianAlbum.getId_album());
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");        
        alert.setContentText("Do you want to remove the musician " + musician.getName() + " from " + album.getName() + " ?");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK) {
            repositoryService.getMusicianAlbumRepository().delete(musicianAlbum);
        }
    }
    
    @Override
    public void show(Musician musician) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");       
        alert.setContentText("Do you want to remove the musician " + musician.getName() + " ?");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK) {
            repositoryService.getMusicianRepository().delete(musician);       
        }
    }
    
    @Override
    public void show(MusicianSong musicianSong) {
        Musician musician = repositoryService.getMusicianRepository().selectById(musicianSong.getId_musician());
        Song song = repositoryService.getSongRepository().selectById(musicianSong.getId_song());
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");        
        alert.setContentText("Do you want to remove the musician " + musician.getName() + " from " + song.getName() + " ?");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK) {
            repositoryService.getMusicianSongRepository().delete(musicianSong);
        }
    }
    
    @Override
    public void show(Song song) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setContentText("Do you want to remove the song " + song.getName() + " ?");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK) {
            repositoryService.getSongRepository().delete(song);
        }
    }
    
    @Override
    public void show(MusicianGroup musicianGroup) {
        Musician musician = repositoryService.getMusicianRepository().selectById(musicianGroup.getId_musician());
        Artist artist = repositoryService.getArtistRepository().selectById(musicianGroup.getId_artist());
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");        
        alert.setContentText("Do you want to remove the musician " + musician.getName() + " from " + artist.getName() + " ?");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK) {
            repositoryService.getMusicianGroupRepository().delete(musicianGroup);
        }
    }
    
    @Override
    public void show(ArtistReference artistReference) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setContentText("Do you want to remove the artist reference: " + artistReference.getName() + " ?");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK) {
            repositoryService.getArtistReferenceRepository().delete(artistReference);
        }
    }
     
}
