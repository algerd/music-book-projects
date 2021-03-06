
package mvc.explorer.controller;

import db.entity.Album;
import db.entity.Artist;
import db.entity.Song;
import java.util.Comparator;
import java.util.List;
import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;
import core.Main;

public class ArtistTreeItem extends TreeItem {
    
    private boolean childrenLoaded = false;
	private boolean leafPropertyComputed = false;
	private boolean leafNode = false;
    
    public ArtistTreeItem(Object obj) {
		super(obj);	
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
                leafNode = !Main.getInstance().getDbLoader().getAlbumTable().containsArtist((Artist) obj);
            }
            else if (obj instanceof Album) {
                leafNode = !Main.getInstance().getDbLoader().getSongTable().containsAlbum((Album) obj);          
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
            List<Album> albums = Main.getInstance().getDbLoader().getAlbumTable().selectByArtist((Artist) obj);            
            albums.sort(Comparator.comparingInt(Album::getYear));
            for (Album album : albums) {
                getChildren().add(new ArtistTreeItem(album));
            }            
        }
        else if (obj instanceof Album) {
            List<Song> songs = Main.getInstance().getDbLoader().getSongTable().selectByAlbum((Album) obj);
            songs.sort(Comparator.comparingInt(Song::getTrack));
            for (Song song : songs) {
                getChildren().add(new ArtistTreeItem(song));
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
