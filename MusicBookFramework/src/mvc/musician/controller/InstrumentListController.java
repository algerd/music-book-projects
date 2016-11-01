package mvc.musician.controller;

import java.net.URL;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;
import core.Main;
import core.RequestPage;
import db.entity.Instrument;
import db.entity.MusicianInstrument;
import javafx.beans.value.ObservableValue;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import utils.Helper;

public class InstrumentListController implements Initializable {

    private MusicianPaneController musicianPaneController;
    
    @FXML
    private AnchorPane instrumentList;
    @FXML
    private ListView<Instrument> instrumentListView;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {             
    }

    public void bootstrap(MusicianPaneController controller) {
        this.musicianPaneController = controller;
        setListValue();
        initListeners();
    }
    
    private void setListValue() {
        List<Instrument> instruments = new ArrayList<>();
        List<MusicianInstrument> musicianInstruments = Main.getInstance().getDbLoader().getMusicianInstrumentTable().selectJoinByMusician(musicianPaneController.getMusician());
        for (MusicianInstrument musicianInstrument : musicianInstruments) {
            instruments.add(musicianInstrument.getInstrument());
        }     
        instrumentListView.getItems().clear();       
        if (!instruments.isEmpty()) {
            instrumentListView.getItems().addAll(instruments);
            sort();
        } else {
            Instrument instrument = new Instrument();
            instrument.setName("Unknown");
            instrumentListView.getItems().add(instrument);
        }                     
        Helper.setHeightList(instrumentListView, 6);        
    }
    
    private void initListeners() {           
        Main.getInstance().getDbLoader().getMusicianInstrumentTable().addedProperty().addListener(this::changed);          
        Main.getInstance().getDbLoader().getMusicianInstrumentTable().deletedProperty().addListener(this::changed);           
        Main.getInstance().getDbLoader().getMusicianInstrumentTable().updatedProperty().addListener(this::changed);
                  
        Main.getInstance().getDbLoader().getMusicianTable().deletedProperty().addListener(this::changed);           
        Main.getInstance().getDbLoader().getMusicianTable().updatedProperty().addListener(this::changed);
                 
        Main.getInstance().getDbLoader().getInstrumentTable().deletedProperty().addListener(this::changed);           
        Main.getInstance().getDbLoader().getInstrumentTable().updatedProperty().addListener(this::changed);
    }
    
    // слушатель на изменение в таблицах
    private void changed(ObservableValue observable, Object oldVal, Object newVal) {
        setListValue();
    }
    
    private void sort() {
        instrumentListView.getSelectionModel().clearSelection();
        instrumentListView.getItems().sort(Comparator.comparing(Instrument::getName));
    }  
    
    @FXML
    private void onMouseClickGenreList(MouseEvent mouseEvent) {    
        Main.getInstance().getContextMenu().clear();   
        if (mouseEvent.getButton() == MouseButton.PRIMARY) {           
            Instrument selectedItem = instrumentListView.getSelectionModel().getSelectedItem();
            // если лкм выбрана запись - показать её
            if (selectedItem != null && selectedItem.getId() > 1) {
                // Дозагрузка
                Instrument instrument = Main.getInstance().getDbLoader().getInstrumentTable().select(selectedItem.getId());
                RequestPage.INSTRUMENT_PANE.load(instrument);
            }           
        }
    }
    
}
