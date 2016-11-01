
package com.algerd.musicbookspringmaven.controller.explorer;

import java.util.Comparator;
import java.util.List;
import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;
import com.algerd.musicbookspringmaven.entity.Album;
import com.algerd.musicbookspringmaven.entity.Artist;
import com.algerd.musicbookspringmaven.entity.Song;
import com.algerd.musicbookspringmaven.service.RepositoryService;

public class ArtistTreeItem extends TreeItem {
    
    private RepositoryService repositoryService;
    
    private boolean childrenLoaded = false;
	private boolean leafPropertyComputed = false;
	private boolean leafNode = false;
    
    public ArtistTreeItem(Object obj, RepositoryService repositoryService) {
		super(obj);	
        this.repositoryService = repositoryService;
        //@Inject
        //repositoryService = Main.getInstance().getRepositoryService();
	}
    
    @Override 
	public ObservableList<TreeItem> getChildren() {
		if (!childrenLoaded) {
			childrenLoaded = true;
			populateChildren();
		}
		return super.getChildren();
	}

	@Override 
	public boolean isLeaf() {
		if (!leafPropertyComputed) {
			leafPropertyComputed = true;            
			Object obj = getValue();
                       
            if (obj instanceof Artist) {               
                leafNode = !repositoryService.getAlbumRepository().containsArtist((Artist) obj);
            }
            else if (obj instanceof Album) {
                leafNode = !repositoryService.getSongRepository().containsAlbum((Album) obj);          
            }
            else if (obj instanceof Song) {
                leafNode = true;
            }
            else {
                leafNode = false;
            }
		}
		return leafNode;
	}
    
    private void populateChildren() {
		getChildren().clear();
        Object obj = getValue();
        
        if (obj instanceof Artist) {
            List<Album> albums = repositoryService.getAlbumRepository().selectByArtist((Artist) obj);            
            albums.sort(Comparator.comparingInt(Album::getYear));
            for (Album album : albums) {
                getChildren().add(new ArtistTreeItem(album, repositoryService));
            }            
        }
        else if (obj instanceof Album) {
            List<Song> songs = repositoryService.getSongRepository().selectByAlbum((Album) obj);
            songs.sort(Comparator.comparingInt(Song::getTrack));
            for (Song song : songs) {
                getChildren().add(new ArtistTreeItem(song, repositoryService));
            }            
        }	
	}
    
    public void reset() {
        childrenLoaded = false;
        leafPropertyComputed = false;
        getChildren();
        isLeaf();
    }
   
    public void setLeafPropertyComputed(boolean leafPropertyComputed) {
        this.leafPropertyComputed = leafPropertyComputed;
    }

    public void setChildrenLoaded(boolean childrenLoaded) {
        this.childrenLoaded = childrenLoaded;
    }
    
}
