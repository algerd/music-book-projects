
package utils;

import db.entity.Entity;
import java.time.DayOfWeek;
import java.time.LocalDate;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.binding.NumberBinding;
import javafx.beans.binding.When;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TableView;
import javafx.scene.control.TextInputControl;
import javafx.scene.paint.Color;
import javafx.util.Callback;
import javafx.util.StringConverter;

public class Helper {

    /**
     * Задать диапазон спиннера, его начальное значение, возможность редактирования и отловить случаи неправильного ввода в поле.
     */
    public static void initIntegerSpinner(Spinner<Integer> spinner, int minValue, int maxValue, int initValue, boolean editable, IntegerProperty value) {  
        spinner.setValueFactory(
            (SpinnerValueFactory<Integer>)new SpinnerValueFactory.IntegerSpinnerValueFactory(minValue, maxValue, initValue)
        );
        spinner.setEditable(editable);
        StringConverter<Integer> sci = spinner.getValueFactory().getConverter();
        StringConverter<Integer> sci2 = new StringConverter<Integer>() {
            @Override
            public Integer fromString(String value) {
                try {
                   return sci.fromString(value);
                }
                catch (NumberFormatException nfe) {
                   return minValue;
                }
            }
            @Override
            public String toString(Integer value) {
               return sci.toString(value);
            }
        };      
        spinner.getValueFactory().setConverter(sci2);   
        
        // force the field to be numeric only
        spinner.getEditor().textProperty().addListener(
            (observable, oldValue, newValue) -> {
                if (newValue.equals("")) {
                    // установить в поле значение 0
                    ((StringProperty)observable).setValue("" + 0);                   
                    value.set(0);
                    return;
                }
                try {
                    // если newValue не парсится к int - выбросит NumberFormatException
                    int val = Integer.valueOf(newValue);
                    // избавится от первого нуля перед вводимым числом
                    ((StringProperty)observable).setValue("" + val);
                    // ограничить вводимое число
                    if (val > maxValue) {
                        ((StringProperty)observable).setValue(oldValue);
                    } 
                    else { 
                        // задать значение спиннера свойству value
                        value.set((val < minValue) ? minValue : val);
                    }                    
                } 
                catch (NumberFormatException e) {
                  ((StringProperty)observable).setValue(oldValue);
                }                  
            }              
        );        
    }
    
    public static void limitTextInput(TextInputControl textInput, int limit) {
        textInput.textProperty().addListener((observable, oldValue, newValue) -> {  
            // обрезать newValue
            if (newValue.length() > limit) {
                ((StringProperty)observable).setValue(newValue.substring(0, limit));
            }
            
        });
    }   
    
    public static void initEntityChoiceBox(ChoiceBox choiceBox) {
        choiceBox.setConverter(new StringConverter<Entity>() {
            @Override
            public String toString(Entity object) {
                return object == null? null : object.getName();
            }
            @Override
            public Entity fromString(String string) {
                return null;
            }
        });
    }
    
    public static void initDatePicker(DatePicker datePicker, boolean editable) {
        datePicker.setEditable(editable);
        
        LocalDateStringConverter converter = new LocalDateStringConverter();
		datePicker.setConverter(converter);
		datePicker.setPromptText(converter.getPattern().toLowerCase());
        
        Callback<DatePicker, DateCell> dayCellFactory = dp -> new DateCell() {
            @Override
            public void updateItem(LocalDate item, boolean empty) {
                super.updateItem(item, empty);              
                // Disable all future date cells
                if (item.isAfter(LocalDate.now())) {
                    this.setDisable(true);
                }               
                // Show Weekends in blue color
                DayOfWeek day = DayOfWeek.from(item);
                if (day == DayOfWeek.SATURDAY || day == DayOfWeek.SUNDAY) {
                    this.setTextFill(Color.BLUE);
                }
            }
        };
        // Set a day cell factory to disable all future day cells and show weekends in blue
		datePicker.setDayCellFactory(dayCellFactory);     
    }
    
    public static void setHeightTable(TableView tableView) {
        /*
        Обернуть таблицу контейнером. Контейнер заанкорить к низу анкор-контейнера.
        Определять(связать) высоту контейнера таблицы и по ней определить сколько строк в него влезет.
        Полученное число строк передать в maxCountVisibleRows(связать).
        */       
        int maxCountVisibleRows = 10;        
        tableView.setFixedCellSize(25);
                         
        NumberBinding maxFactor =   
            new When(Bindings.size(tableView.getItems()).greaterThan(maxCountVisibleRows)).
                then(maxCountVisibleRows).
                otherwise(Bindings.size(tableView.getItems()));
           
        DoubleBinding factor = 
            new When(Bindings.size(tableView.getItems()).isEqualTo(0)).
                then(2.01).
                otherwise(1.01);       
        
        tableView.prefHeightProperty().bind(tableView.fixedCellSizeProperty().multiply(maxFactor.add(factor)));
        
        tableView.minHeightProperty().bind(tableView.prefHeightProperty());
        tableView.maxHeightProperty().bind(tableView.prefHeightProperty());
    }
          
}
