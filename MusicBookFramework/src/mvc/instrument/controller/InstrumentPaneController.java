
package mvc.instrument.controller;

import core.ContextMenuManager;
import static core.ContextMenuManager.ItemType.ADD_INSTRUMENT;
import static core.ContextMenuManager.ItemType.DELETE_INSTRUMENT;
import static core.ContextMenuManager.ItemType.EDIT_INSTRUMENT;
import core.Loadable;
import core.Main;
import core.Params;
import db.entity.Entity;
import db.entity.Instrument;
import db.entity.WrapChangedEntity;
import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TabPane;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import utils.ImageUtil;

public class InstrumentPaneController implements Initializable, Loadable {
    
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
    }  
    
    @Override
    public void show(Entity entity) {
        instrument = (Instrument) entity;  
        showDetails();
        instrumentPane.setVisible(true);
        initListeners();
        includedMusicianTableController.bootstrap(this);
        instrumentTabPane.getSelectionModel().selectFirst();
    } 

    private void initListeners() {
        // слушатель удаления сущности
        Main.getInstance().getDbLoader().getInstrumentTable().deletedProperty().addListener(
            (observable, oldVal, newVal) -> {  
                Instrument newEntity = ((WrapChangedEntity<Instrument>) newVal).getNew();
                if (newEntity.getId() == instrument.getId()) {
                    instrumentPane.setVisible(false);
                }
            }  
        );
        // слушатель изменения файла изображения инструмента при обновлении данных инструмента
        Main.getInstance().getDbLoader().getInstrumentTable().updatedProperty().addListener(
            (observable, oldVal, newVal) -> {                    
                if (file.lastModified() != imageLastModified) {
                    instrumentImageView.setImage(ImageUtil.readImage(file));
                    imageLastModified = file.lastModified();
                }
            }  
        );
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
        ContextMenuManager contextMenu = Main.getInstance().getContextMenu();
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
