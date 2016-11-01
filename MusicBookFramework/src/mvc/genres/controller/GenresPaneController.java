
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
import db.entity.Entity;
import db.entity.Genre;
import db.entity.MusicianGenre;
import db.entity.SongGenre;
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
        initGenresTable();
        setTableValue();
        initTableListeners();
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
                        this.setText("" + Main.getInstance().getDbLoader().getArtistGenreTable().countArtistsByGenre(item));                   
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
                        List<ArtistGenre> artistGenres = Main.getInstance().getDbLoader().getArtistGenreTable().selectJoinByGenre(item);
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
                        this.setText("" + Main.getInstance().getDbLoader().getAlbumGenreTable().countAlbumsByGenre(item));                   
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
                        List<AlbumGenre> albumGenres = Main.getInstance().getDbLoader().getAlbumGenreTable().selectJoinByGenre(item);
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
                        this.setText("" + Main.getInstance().getDbLoader().getSongGenreTable().countSongsByGenre(item));                   
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
                        List<SongGenre> songGenres = Main.getInstance().getDbLoader().getSongGenreTable().selectJoinByGenre(item);
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
                        this.setText("" + Main.getInstance().getDbLoader().getMusicianGenreTable().countMusiciansByGenre(item));                   
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
                        List<MusicianGenre> musicianGenres = Main.getInstance().getDbLoader().getMusicianGenreTable().selectJoinByGenre(item);
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
        genres = Main.getInstance().getDbLoader().getGenreTable().select();    
        genresTable.setItems(FXCollections.observableArrayList(genres));      
        sort();
        Helper.setHeightTable(genresTable, 10, 2);  
    }
    
    private void initTableListeners() {      
        // Добавить слушателя на выбор элемента в таблице.
        genresTable.getSelectionModel().selectedItemProperty().addListener(
            (observable, oldValue, newValue) -> {             
                selectedItem = genresTable.getSelectionModel().getSelectedItem();
            }
        );  
        Main.getInstance().getDbLoader().getArtistGenreTable().addedProperty().addListener(this::changed);          
        Main.getInstance().getDbLoader().getArtistGenreTable().deletedProperty().addListener(this::changed);           
        Main.getInstance().getDbLoader().getArtistGenreTable().updatedProperty().addListener(this::changed);
        
        Main.getInstance().getDbLoader().getAlbumGenreTable().addedProperty().addListener(this::changed);          
        Main.getInstance().getDbLoader().getAlbumGenreTable().deletedProperty().addListener(this::changed);           
        Main.getInstance().getDbLoader().getAlbumGenreTable().updatedProperty().addListener(this::changed);
        
        Main.getInstance().getDbLoader().getSongGenreTable().addedProperty().addListener(this::changed);          
        Main.getInstance().getDbLoader().getSongGenreTable().deletedProperty().addListener(this::changed);           
        Main.getInstance().getDbLoader().getSongGenreTable().updatedProperty().addListener(this::changed);
        
        Main.getInstance().getDbLoader().getMusicianGenreTable().addedProperty().addListener(this::changed);          
        Main.getInstance().getDbLoader().getMusicianGenreTable().deletedProperty().addListener(this::changed);           
        Main.getInstance().getDbLoader().getMusicianGenreTable().updatedProperty().addListener(this::changed);
        
        Main.getInstance().getDbLoader().getArtistTable().deletedProperty().addListener(this::changed);  
        Main.getInstance().getDbLoader().getAlbumTable().deletedProperty().addListener(this::changed);  
        Main.getInstance().getDbLoader().getSongTable().deletedProperty().addListener(this::changed);  
        Main.getInstance().getDbLoader().getMusicianTable().deletedProperty().addListener(this::changed);                         
        Main.getInstance().getDbLoader().getGenreTable().deletedProperty().addListener(this::changed);           
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
                Genre genre = Main.getInstance().getDbLoader().getGenreTable().select(selectedItem.getId());
                RequestPage.GENRE_PANE.load(genre);
            }           
        }
        else if (mouseEvent.getButton() == MouseButton.SECONDARY) { 
            contextMenu.add(ADD_GENRE, new Genre());
            if (selectedItem != null && selectedItem.getId() > 1) {
                Genre genre = Main.getInstance().getDbLoader().getGenreTable().select(selectedItem.getId());
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
