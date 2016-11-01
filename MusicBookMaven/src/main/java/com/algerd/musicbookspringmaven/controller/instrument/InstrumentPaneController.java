
package com.algerd.musicbookspringmaven.controller.instrument;

import com.algerd.musicbookspringmaven.controller.BaseController;
import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TabPane;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import com.algerd.musicbookspringmaven.utils.ImageUtil;
import com.algerd.musicbookspringmaven.core.Loadable;
import com.algerd.musicbookspringmaven.core.Main;
import com.algerd.musicbookspringmaven.core.Params;
import com.algerd.musicbookspringmaven.dbDriver.Entity;
import com.algerd.musicbookspringmaven.entity.Instrument;
import com.algerd.musicbookspringmaven.dbDriver.impl.WrapChangedEntity;
import static com.algerd.musicbookspringmaven.core.ContextMenuManager.ItemType.ADD_INSTRUMENT;
import static com.algerd.musicbookspringmaven.core.ContextMenuManager.ItemType.DELETE_INSTRUMENT;
import static com.algerd.musicbookspringmaven.core.ContextMenuManager.ItemType.EDIT_INSTRUMENT;

public class InstrumentPaneController extends BaseController implements Initializable, Loadable {

    private Instrument instrument;
    private File file;
    private long imageLastModified;
    
    @FXML
    private AnchorPane instrumentPane;
    @FXML
    private TabPane instrumentTabPane; 
    @FXML
    private ImageView instrumentImageView;
    @FXML
    private Label nameLabel;    
    @FXML
    private Text descriptionText;
    
    @FXML
    private AnchorPane musicianTable; 
    @FXML
    private MusicianTableController includedMusicianTableController;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        //@Inject
        repositoryService = Main.getInstance().getRepositoryService(); 
        //@Inject
        contextMenu = Main.getInstance().getContextMenu();
    }  
    
    @Override
    public void show(Entity entity) {
        instrument = (Instrument) entity;  
        showDetails(); 
        instrumentPane.setVisible(true);
        initRepositoryListeners();
        includedMusicianTableController.bootstrap(this);
        instrumentTabPane.getSelectionModel().selectFirst();
    } 

    private void initRepositoryListeners() {
        repositoryService.getInstrumentRepository().clearDeleteListeners(this);
        repositoryService.getInstrumentRepository().clearUpdateListeners(this);
        
        repositoryService.getInstrumentRepository().addDeleteListener(this::deletedInstrument, this);
        repositoryService.getInstrumentRepository().addUpdateListener(this::updatedInstrument, this);
    }
    
    private void deletedInstrument(ObservableValue observable, Object oldVal, Object newVal) {
        Instrument newEntity = ((WrapChangedEntity<Instrument>) newVal).getNew();
        if (newEntity.getId() == instrument.getId()) {
            instrumentPane.setVisible(false);
        }
    }
    
    private void updatedInstrument(ObservableValue observable, Object oldVal, Object newVal) {
        if (file.lastModified() != imageLastModified) {
            instrumentImageView.setImage(ImageUtil.readImage(file));
            imageLastModified = file.lastModified();
        }
    }
    
    private void showDetails() {  
        nameLabel.textProperty().bind(instrument.nameProperty()); 
        descriptionText.textProperty().bind(instrument.descriptionProperty());
        
        // проверить и загрузить изображение
        file = new File(Params.DIR_IMAGE_ENTITY + "instrument/" + instrument.getId() + "." + Params.SAVED_IMAGE_FORMAT);
        instrumentImageView.setImage(ImageUtil.readImage(file));
        imageLastModified = file.lastModified();       
    }
    
    /**
     * При ПКМ по странице инструмента показать контекстное меню.
     */
    @FXML
    private void showContextMenu(MouseEvent mouseEvent) {
        contextMenu.clear();
		if (mouseEvent.getButton() == MouseButton.SECONDARY) {       
            contextMenu.add(ADD_INSTRUMENT, new Instrument());
            contextMenu.add(EDIT_INSTRUMENT, instrument);
            contextMenu.add(DELETE_INSTRUMENT, instrument);
            contextMenu.show(instrumentPane, mouseEvent);
        }      
    }

    public Instrument getInstrument() {
        return instrument;
    }

    public AnchorPane getInstrumentPane() {
        return instrumentPane;
    }
    
}
