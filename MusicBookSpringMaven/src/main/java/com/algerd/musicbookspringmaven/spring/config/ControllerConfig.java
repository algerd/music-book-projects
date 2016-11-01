
package com.algerd.musicbookspringmaven.spring.config;

import com.algerd.musicbookspringmaven.controller.album.AlbumPaneController;
import com.algerd.musicbookspringmaven.controller.albums.AlbumsPaneController;
import com.algerd.musicbookspringmaven.controller.artist.ArtistPaneController;
import com.algerd.musicbookspringmaven.controller.artists.ArtistsPaneController;
import com.algerd.musicbookspringmaven.controller.genre.GenrePaneController;
import com.algerd.musicbookspringmaven.controller.genres.GenresPaneController;
import com.algerd.musicbookspringmaven.controller.instrument.InstrumentPaneController;
import com.algerd.musicbookspringmaven.controller.instruments.InstrumentsPaneController;
import com.algerd.musicbookspringmaven.controller.main.MainController;
import com.algerd.musicbookspringmaven.controller.musician.MusicianPaneController;
import com.algerd.musicbookspringmaven.controller.musicians.MusiciansPaneController;
import com.algerd.musicbookspringmaven.controller.song.SongPaneController;
import com.algerd.musicbookspringmaven.controller.songs.SongsPaneController;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.algerd.musicbookspringmaven.controller.PaneController;

@Configuration
public class ControllerConfig {
    
    @Bean
    public MainController mainController() {        
        MainController MainController = new MainController();
        return MainController;
    }
    
    @Bean
    public PaneController albumPaneController() {
        PaneController albumPaneController = new AlbumPaneController();
        return albumPaneController;
    }
    
    @Bean
    public PaneController albumsPaneController() {
        PaneController albumsPaneController = new AlbumsPaneController();
        return albumsPaneController;
    }
    
    @Bean
    public PaneController artistPaneController() {
        PaneController artistPaneController = new ArtistPaneController();
        return artistPaneController;
    }
    
    @Bean
    public PaneController artistsPaneController() {
        PaneController artistsPaneController = new ArtistsPaneController();
        return artistsPaneController;
    }
    
    @Bean
    public PaneController genrePaneController() {
        PaneController genrePaneController = new GenrePaneController();
        return genrePaneController;
    }
    
    @Bean
    public PaneController genresPaneController() {
        PaneController genresPaneController = new GenresPaneController();
        return genresPaneController;
    }
    
    @Bean
    public PaneController instrumentPaneController() {
        PaneController instrumentPaneController = new InstrumentPaneController();
        return instrumentPaneController;
    }
    
    @Bean
    public PaneController instrumentsPaneController() {
        PaneController instrumentsPaneController = new InstrumentsPaneController();
        return instrumentsPaneController;
    }
    
    @Bean
    public PaneController musicianPaneController() {
        PaneController musicianPaneController = new MusicianPaneController();
        return musicianPaneController;
    }
    
    @Bean
    public PaneController musiciansPaneController() {
        PaneController musiciansPaneController = new MusiciansPaneController();
        return musiciansPaneController;
    }
    
    @Bean
    public PaneController songPaneController() {
        PaneController songPaneController = new SongPaneController();
        return songPaneController;
    }
    
    @Bean
    public PaneController songsPaneController() {
        PaneController songsPaneController = new SongsPaneController();
        return songsPaneController;
    }

}
