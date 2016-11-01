package com.algerd.musicbookspringmaven.controller.artist;

import java.net.URL;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ListView;
import javafx.scene.control.cell.CheckBoxListCell;
import javafx.scene.layout.AnchorPane;
import javafx.util.Callback;
import com.algerd.musicbookspringmaven.utils.Helper;

/**
 * TODO: Надо сортировать map по строке 
 */
public class CheckBoxListViewController<T> implements Initializable {
    
    private int countVisibleRaw = 10;
    private ChoiceBox choiceBox;
    private final Map<T, ObservableValue<Boolean>> map = new HashMap<>();   
    private final BooleanProperty showing = new SimpleBooleanProperty(false);   
    
    @FXML
    private AnchorPane checkBoxListView;
    @FXML
    private ListView<T> listView;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {         
		Callback<T, ObservableValue<Boolean>> itemToBoolean = item -> map.get(item);		
		listView.setCellFactory(CheckBoxListCell.forListView(itemToBoolean));
        Helper.setHeightList(listView, countVisibleRaw); 
        
        listView.visibleProperty().bind(showing);      
        showing.addListener((observable, oldValue, newValue) -> {
            Helper.placeBoundingNode(choiceBox, checkBoxListView);
            if (isShowing()) checkBoxListView.toFront();               
            else checkBoxListView.toBack();
        });
        // при выборе элемента в списке активируется CheckBox
        listView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            ((SimpleBooleanProperty) map.get(newValue)).set(true);
        });
    } 
    
    public void addListenerToCheckBox(ChangeListener<? super Boolean> listener) {
        for (T item : map.keySet()) {
            map.get(item).addListener(listener);
        }
    }
    
    public void addItems(Map<T, ObservableValue<Boolean>> map) {
        this.map.putAll(map);
        listView.getItems().addAll(map.keySet());
        sort();
    }
      
    public void addItems(T... elements) {
        for (T item  : elements) {
            map.put(item, new SimpleBooleanProperty(false));
        }
        listView.getItems().addAll(map.keySet());
        sort();
    }
    
    private void sort() {
        listView.getItems().sort(Comparator.comparing((T key) -> key.toString()));
    }
    
    public void setChoiceBox(ChoiceBox choiceBox) {
        this.choiceBox = choiceBox;
        showingProperty().bind(choiceBox.showingProperty());
        listView.prefWidthProperty().bind(choiceBox.prefWidthProperty());
        checkBoxListView.toBack();
    }

    public void setCountVisibleRaw(int countVisibleRaw) {
        this.countVisibleRaw = countVisibleRaw;
        Helper.setHeightList(listView, countVisibleRaw);
    }
   
    public boolean isShowing() {
        return showing.get();
    }
    public void setShowing(boolean value) {
        showing.set(value);
    }
    public BooleanProperty showingProperty() {
        return showing;
    }

    public Map<T, ObservableValue<Boolean>> getMap() {
        return map;
    }

    public AnchorPane getCheckBoxListView() {
        return checkBoxListView;
    }
    
}
