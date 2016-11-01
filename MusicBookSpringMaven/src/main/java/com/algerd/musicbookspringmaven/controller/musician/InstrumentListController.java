package com.algerd.musicbookspringmaven.controller.musician;

import com.algerd.musicbookspringmaven.controller.BaseIncludeController;
import java.net.URL;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;
import javafx.beans.value.ObservableValue;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import com.algerd.musicbookspringmaven.entity.Instrument;
import com.algerd.musicbookspringmaven.entity.MusicianInstrument;
import com.algerd.musicbookspringmaven.utils.Helper;

public class InstrumentListController extends BaseIncludeController<MusicianPaneController> {
       
    @FXML
    private AnchorPane instrumentList;
    @FXML
    private ListView<Instrument> instrumentListView;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {  
    }

    @Override
    public void bootstrap() {
        setListValue();
        initRepositoryListeners();
    }
    
    private void setListValue() {
        List<Instrument> instruments = new ArrayList<>();
        List<MusicianInstrument> musicianInstruments = repositoryService.getMusicianInstrumentRepository().selectJoinByMusician(paneController.getMusician());
        musicianInstruments.stream().forEach(musicianInstrument -> instruments.add(musicianInstrument.getInstrument()));     
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
    
    private void initRepositoryListeners() {   
        //clear listeners
        repositoryService.getMusicianInstrumentRepository().clearChangeListeners(this);                         
        repositoryService.getMusicianRepository().clearDeleteListeners(this);           
        repositoryService.getMusicianRepository().clearUpdateListeners(this);                
        repositoryService.getInstrumentRepository().clearDeleteListeners(this);           
        repositoryService.getInstrumentRepository().clearUpdateListeners(this);
        
        //add listeners
        repositoryService.getMusicianInstrumentRepository().addChangeListener(this::changed, this);                         
        repositoryService.getMusicianRepository().addDeleteListener(this::changed, this);           
        repositoryService.getMusicianRepository().addUpdateListener(this::changed, this);                
        repositoryService.getInstrumentRepository().addDeleteListener(this::changed, this);           
        repositoryService.getInstrumentRepository().addUpdateListener(this::changed, this);
    }
    
    private void changed(ObservableValue observable, Object oldVal, Object newVal) {
        setListValue();
    }
    
    private void sort() {
        instrumentListView.getSelectionModel().clearSelection();
        instrumentListView.getItems().sort(Comparator.comparing(Instrument::getName));
    }  
    
    @FXML
    private void onMouseClickGenreList(MouseEvent mouseEvent) {    
        contextMenuService.clear();   
        if (mouseEvent.getButton() == MouseButton.PRIMARY) {           
            Instrument selectedItem = instrumentListView.getSelectionModel().getSelectedItem();
            // если лкм выбрана запись - показать её
            if (selectedItem != null && selectedItem.getId() > 1) {
                // Дозагрузка
                Instrument instrument = repositoryService.getInstrumentRepository().selectById(selectedItem.getId());
                requestPageService.instrumentPane(instrument);
            }           
        }
    }
    
}
