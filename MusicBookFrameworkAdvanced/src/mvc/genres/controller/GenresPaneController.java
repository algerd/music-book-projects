
package mvc.genres.controller;

import core.ContextMenuManager;
import static core.ContextMenuManager.ItemType.ADD_GENRE;
import static core.ContextMenuManager.ItemType.DELETE_GENRE;
import static core.ContextMenuManager.ItemType.EDIT_GENRE;
import core.Loadable;
import core.Main;
import core.RequestPage;
import db.entity.AlbumGenre;
import db.entity.ArtistGenre;
import db.driver.Entity;
import db.entity.Genre;
import db.entity.MusicianGenre;
import db.entity.SongGenre;
import db.repository.RepositoryService;
import java.net.URL;
import java.util.Comparator;
import java.util.List;
import java.util.ResourceBundle;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import utils.Helper;

public class GenresPaneController implements Initializable, Loadable {
    
    //@Inject
    private RepositoryService repositoryService; 
    
    private Genre selectedItem;
    private List<Genre> genres;
    private String searchString = "";
    
    @FXML
    private AnchorPane genresPane;
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
        //@Inject
        repositoryService = Main.getInstance().getRepositoryService(); 
        
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
        // Добавить слушателя на выбор элемента в таблице.
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
       repositoryService.getGenreRepository().clearDeleteListeners(this);     
        
        repositoryService.getArtistGenreRepository().addChangeListener(this::changed, this);                      
        repositoryService.getAlbumGenreRepository().addChangeListener(this::changed, this);                          
        repositoryService.getSongGenreRepository().addChangeListener(this::changed, this);                       
        repositoryService.getMusicianGenreRepository().addChangeListener(this::changed, this);                         
        repositoryService.getArtistRepository().addDeleteListener(this::changed, this);  
        repositoryService.getAlbumRepository().addDeleteListener(this::changed, this);  
        repositoryService.getSongRepository().addDeleteListener(this::changed, this);  
        repositoryService.getMusicianRepository().addDeleteListener(this::changed, this);                         
        repositoryService.getGenreRepository().addDeleteListener(this::changed, this);           
    }
    
    // слушатель на изменение в таблицах
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
        ContextMenuManager contextMenu = Main.getInstance().getContextMenu();
        boolean isShowingContextMenu = contextMenu.getContextMenu().isShowing();     
        contextMenu.clear();        
        if (mouseEvent.getButton() == MouseButton.PRIMARY) {
            // если контекстное меню выбрано, то лкм сбрасывает контекстное меню и выбор в таблице
            if (isShowingContextMenu) {
                clearSelectionTable();
            }
            // если лкм выбрана запись - показать её
            if (selectedItem != null) {
                Genre genre = repositoryService.getGenreRepository().selectById(selectedItem.getId());
                RequestPage.GENRE_PANE.load(genre);
            }           
        }
        else if (mouseEvent.getButton() == MouseButton.SECONDARY) { 
            contextMenu.add(ADD_GENRE, new Genre());
            if (selectedItem != null && selectedItem.getId() > 1) {
                Genre genre = repositoryService.getGenreRepository().selectById(selectedItem.getId());
                contextMenu.add(EDIT_GENRE, genre);
                contextMenu.add(DELETE_GENRE, genre);                       
            }
            contextMenu.show(genresPane, mouseEvent);       
        }
    }
      
    @FXML
    private void showContextMenu(MouseEvent mouseEvent) {
        clearSelectionTable();
        ContextMenuManager contextMenu = Main.getInstance().getContextMenu();
        contextMenu.clear();
		if (mouseEvent.getButton() == MouseButton.SECONDARY) {       
            contextMenu.add(ADD_GENRE, new Genre());
            contextMenu.show(genresPane, mouseEvent);
        }      
    }
      
}
