
package mvc.instrument.alert;

import java.util.Optional;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import core.Main;
import db.entity.Instrument;

public class InstrumentDeleteAlert {
    
    private InstrumentDeleteAlert() {}
    
     public static void show(Instrument instrument) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");       
        alert.setContentText("Do you want to remove the instrument " + instrument.getName() + " ?");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK) {
            Main.getInstance().getDbLoader().getInstrumentTable().delete(instrument);
        }
    }

}
