
package com.algerd.musicbookspringmaven.controller.genres;

import java.net.URL;
import java.util.Comparator;
import java.util.List;
import java.util.ResourceBundle;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import com.algerd.musicbookspringmaven.utils.Helper;
import com.algerd.musicbookspringmaven.controller.BasePaneController;
import com.algerd.musicbookspringmaven.entity.AlbumGenre;
import com.algerd.musicbookspringmaven.entity.ArtistGenre;
import com.algerd.musicbookspringmaven.dbDriver.Entity;
import com.algerd.musicbookspringmaven.entity.Genre;
import com.algerd.musicbookspringmaven.entity.MusicianGenre;
import com.algerd.musicbookspringmaven.entity.SongGenre;
import static com.algerd.musicbookspringmaven.service.impl.ContextMenuItemType.ADD_GENRE;
import static com.algerd.musicbookspringmaven.service.impl.ContextMenuItemType.DELETE_GENRE;
import static com.algerd.musicbookspringmaven.service.impl.ContextMenuItemType.EDIT_GENRE;

public class GenresPaneController extends BasePaneController {
    
    private Genre selectedItem;
    private List<Genre> genres;
    private String searchString = "";
    
    @FXML
    private TextField searchField;
    @FXML
    private Label resetSearchLabel; 
    //table
    @FXML
    private TableView<Genre> genresTable;
    @FXML
    private TableColumn<Genre, String> genreColumn;
    @FXML
    private TableColumn<Genre, Genre> artistsAmountColumn;
    @FXML
    private TableColumn<Genre, Genre> artistsAvRatingColumn;
    @FXML
    private TableColumn<Genre, Genre> albumsAmountColumn;
    @FXML
    private TableColumn<Genre, Genre> albumsAvRatingColumn;
    @FXML
    private TableColumn<Genre, Genre> songsAmountColumn;
    @FXML
    private TableColumn<Genre, Genre> songsAvRatingColumn;
    @FXML
    private TableColumn<Genre, Genre> musiciansAmountColumn;
    @FXML
    private TableColumn<Genre, Genre> musiciansAvRatingColumn;    

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        initGenresTable();
        setTableValue();
        initTableListeners();
        initRepositoryListeners();
        initFilterListeners();
    } 
    
    @Override
    public void show(Entity entity) {
        resetSearchLabel();
        clearSelectionTable();
    } 
    
    private void initGenresTable() { 
        genreColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        
        artistsAmountColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue()));
        artistsAmountColumn.setCellFactory(col -> {
            TableCell<Genre, Genre> cell = new TableCell<Genre, Genre>() {
                @Override
                public void updateItem(Genre item, boolean empty) {
                    super.updateItem(item, empty);
                    this.setText(null);
                    if (!empty) {                        
                        this.setText("" + repositoryService.getArtistGenreRepository().countArtistsByGenre(item));                   
                    }
                }
            };
			return cell;
        });
        artistsAvRatingColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue()));
        artistsAvRatingColumn.setCellFactory(col -> {
            TableCell<Genre, Genre> cell = new TableCell<Genre, Genre>() {
                @Override
                public void updateItem(Genre item, boolean empty) {
                    super.updateItem(item, empty);
                    this.setText(null);
                    if (!empty) {                        
                        List<ArtistGenre> artistGenres = repositoryService.getArtistGenreRepository().selectJoinByGenre(item);
                        int averageRating = 0;
                        for (ArtistGenre artistGenre : artistGenres) {
                            averageRating += artistGenre.getArtist().getRating();
                        }
                        int count = artistGenres.size();
                        this.setText("" + ((count != 0) ? ((int) (100.0 * averageRating / artistGenres.size() + 0.5))/ 100.0 : ""));
                    }
                }
            };
			return cell;
        }); 
        albumsAmountColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue()));
        albumsAmountColumn.setCellFactory(col -> {
            TableCell<Genre, Genre> cell = new TableCell<Genre, Genre>() {
                @Override
                public void updateItem(Genre item, boolean empty) {
                    super.updateItem(item, empty);
                    this.setText(null);
                    if (!empty) {                        
                        this.setText("" + repositoryService.getAlbumGenreRepository().countAlbumsByGenre(item));                   
                    }
                }
            };
			return cell;
        });
        albumsAvRatingColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue()));
        albumsAvRatingColumn.setCellFactory(col -> {
            TableCell<Genre, Genre> cell = new TableCell<Genre, Genre>() {
                @Override
                public void updateItem(Genre item, boolean empty) {
                    super.updateItem(item, empty);
                    this.setText(null);
                    if (!empty) {                        
                        List<AlbumGenre> albumGenres = repositoryService.getAlbumGenreRepository().selectJoinByGenre(item);
                        int averageRating = 0;
                        for (AlbumGenre albumGenre : albumGenres) {
                            averageRating += albumGenre.getAlbum().getRating();
                        }
                        int count = albumGenres.size();
                        this.setText("" + ((count != 0) ? ((int) (100.0 * averageRating / albumGenres.size() + 0.5))/ 100.0 : ""));
                    }
                }
            };
			return cell;
        });
        songsAmountColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue()));
        songsAmountColumn.setCellFactory(col -> {
            TableCell<Genre, Genre> cell = new TableCell<Genre, Genre>() {
                @Override
                public void updateItem(Genre item, boolean empty) {
                    super.updateItem(item, empty);
                    this.setText(null);
                    if (!empty) {                        
                        this.setText("" + repositoryService.getSongGenreRepository().countSongsByGenre(item));                   
                    }
                }
            };
			return cell;
        });
        songsAvRatingColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue()));
        songsAvRatingColumn.setCellFactory(col -> {
            TableCell<Genre, Genre> cell = new TableCell<Genre, Genre>() {
                @Override
                public void updateItem(Genre item, boolean empty) {
                    super.updateItem(item, empty);
                    this.setText(null);
                    if (!empty) {                        
                        List<SongGenre> songGenres = repositoryService.getSongGenreRepository().selectJoinByGenre(item);
                        int averageRating = 0;
                        for (SongGenre songGenre : songGenres) {
                            averageRating += songGenre.getSong().getRating();
                        }
                        int count = songGenres.size();
                        this.setText("" + ((count != 0) ? ((int) (100.0 * averageRating / songGenres.size() + 0.5))/ 100.0 : ""));
                    }
                }
            };
			return cell;
        });
        musiciansAmountColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue()));
        musiciansAmountColumn.setCellFactory(col -> {
            TableCell<Genre, Genre> cell = new TableCell<Genre, Genre>() {
                @Override
                public void updateItem(Genre item, boolean empty) {
                    super.updateItem(item, empty);
                    this.setText(null);
                    if (!empty) {                        
                        this.setText("" + repositoryService.getMusicianGenreRepository().countMusiciansByGenre(item));                   
                    }
                }
            };
			return cell;
        });
        musiciansAvRatingColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue()));
        musiciansAvRatingColumn.setCellFactory(col -> {
            TableCell<Genre, Genre> cell = new TableCell<Genre, Genre>() {
                @Override
                public void updateItem(Genre item, boolean empty) {
                    super.updateItem(item, empty);
                    this.setText(null);
                    if (!empty) {                        
                        List<MusicianGenre> musicianGenres = repositoryService.getMusicianGenreRepository().selectJoinByGenre(item);
                        int averageRating = 0;
                        for (MusicianGenre musicianGenre : musicianGenres) {
                            averageRating += musicianGenre.getMusician().getRating();
                        }
                        int count = musicianGenres.size();
                        this.setText("" + ((count != 0) ? ((int) (100.0 * averageRating / musicianGenres.size() + 0.5))/ 100.0 : ""));
                    }
                }
            };
			return cell;
        });       
    }
    
    private void setTableValue() {
        genres = repositoryService.getGenreRepository().selectAll();    
        genresTable.setItems(FXCollections.observableArrayList(genres));      
        sort();
        Helper.setHeightTable(genresTable, 10, 2);  
    }
    
    private void initTableListeners() {      
        genresTable.getSelectionModel().selectedItemProperty().addListener(
            (observable, oldValue, newValue) -> selectedItem = genresTable.getSelectionModel().getSelectedItem()
        );  
    }
    
    private void initRepositoryListeners() {
       repositoryService.getArtistGenreRepository().clearChangeListeners(this);                      
       repositoryService.getAlbumGenreRepository().clearChangeListeners(this);                          
       repositoryService.getSongGenreRepository().clearChangeListeners(this);                       
       repositoryService.getMusicianGenreRepository().clearChangeListeners(this);                         
       repositoryService.getArtistRepository().clearDeleteListeners(this);  
       repositoryService.getAlbumRepository().clearDeleteListeners(this);  
       repositoryService.getSongRepository().clearDeleteListeners(this);  
       repositoryService.getMusicianRepository().clearDeleteListeners(this);                         
       repositoryService.getGenreRepository().clearChangeListeners(this);     
        
        repositoryService.getArtistGenreRepository().addChangeListener(this::changed, this);                      
        repositoryService.getAlbumGenreRepository().addChangeListener(this::changed, this);                          
        repositoryService.getSongGenreRepository().addChangeListener(this::changed, this);                       
        repositoryService.getMusicianGenreRepository().addChangeListener(this::changed, this);                         
        repositoryService.getArtistRepository().addDeleteListener(this::changed, this);  
        repositoryService.getAlbumRepository().addDeleteListener(this::changed, this);  
        repositoryService.getSongRepository().addDeleteListener(this::changed, this);  
        repositoryService.getMusicianRepository().addDeleteListener(this::changed, this);                         
        repositoryService.getGenreRepository().addChangeListener(this::changed, this);           
    }
    
    private void changed(ObservableValue observable, Object oldVal, Object newVal) {
        genresTable.getItems().clear();
        setTableValue();
        repeatFilter();
    }
    
    private void initFilterListeners() {            
        searchField.textProperty().addListener((ObservableValue, oldValue, newValue)-> {
            resetSearchLabel.setVisible(newValue.length() > 0);
            searchString = newValue.trim();                     
            filter();   
        });
    } 
    
    private void repeatFilter() {
        if (!searchString.equals("")) {
            filter();
        }
    }
    
    private void filter() {
        ObservableList<Genre> filteredList = FXCollections.observableArrayList();
        int lengthSearch = searchString.length();
        
        for (Genre genre : genres) {
            if ((searchString.equals("") || genre.getName().regionMatches(true, 0, searchString, 0, lengthSearch))) {                                      
                filteredList.add(genre);                
            }
        }
        genresTable.setItems((filteredList.size() < genres.size()) ? filteredList : FXCollections.observableArrayList(genres));
        sort();
        Helper.setHeightTable(genresTable, 10, 2);       
    }
    
    private void sort() {
        genresTable.getItems().sort(Comparator.comparing(Genre::getName));
    }
    
    private void clearSelectionTable() {
        genresTable.getSelectionModel().clearSelection();
        selectedItem = null;
    }
    
    @FXML
    private void resetSearchLabel() {
        searchField.textProperty().setValue("");
        resetSearchLabel.setVisible(false);
    }
    
    @FXML
    private void onMouseClickTable(MouseEvent mouseEvent) { 
        boolean isShowingContextMenu = contextMenuService.getContextMenu().isShowing();     
        contextMenuService.clear();        
        if (mouseEvent.getButton() == MouseButton.PRIMARY) {
            // если контекстное меню выбрано, то лкм сбрасывает контекстное меню и выбор в таблице
            if (isShowingContextMenu) {
                clearSelectionTable();
            }
            // если лкм выбрана запись - показать её
            if (selectedItem != null) {
                Genre genre = repositoryService.getGenreRepository().selectById(selectedItem.getId());
                requestPageService.genrePane(genre);
            }           
        }
        else if (mouseEvent.getButton() == MouseButton.SECONDARY) { 
            contextMenuService.add(ADD_GENRE, new Genre());
            if (selectedItem != null && selectedItem.getId() > 1) {
                Genre genre = repositoryService.getGenreRepository().selectById(selectedItem.getId());
                contextMenuService.add(EDIT_GENRE, genre);
                contextMenuService.add(DELETE_GENRE, genre);                       
            }
            contextMenuService.show(view, mouseEvent);       
        }
    }
      
    @FXML
    private void showContextMenu(MouseEvent mouseEvent) {
        clearSelectionTable();
        contextMenuService.clear();
		if (mouseEvent.getButton() == MouseButton.SECONDARY) {       
            contextMenuService.add(ADD_GENRE, new Genre());
            contextMenuService.show(view, mouseEvent);
        }      
    }
      
}
