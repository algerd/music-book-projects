
package com.algerd.musicbookspringmaven.controller.helper.choiceCheckBox;

import com.sun.javafx.scene.control.skin.LabeledText;
import java.net.URL;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.binding.NumberBinding;
import javafx.beans.binding.When;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.cell.CheckBoxListCell;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.util.Callback;

public class ChoiceCheckBoxController<T> implements Initializable {
    
    private int countVisibleRaw = 10;
    private final Map<T, ObservableValue<Boolean>> itemMap = new HashMap<>();     
    private AnchorPane checkBoxListPane;
    private ListView<T> listView;
    private Pane mainPane;
        
    @FXML
    AnchorPane choiceCheckBox;
    @FXML
    ChoiceBox<T> choiceBox;
    @FXML
    Label label;
   
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        initCheckBoxList();
        initChoiceBox();
    } 
    
    private void initCheckBoxList() {
        checkBoxListPane = new AnchorPane();
        listView = new ListView<>();
        AnchorPane.setTopAnchor(listView, 0.0);
        AnchorPane.setBottomAnchor(listView, 0.0);
		AnchorPane.setLeftAnchor(listView, 0.0);
        AnchorPane.setRightAnchor(listView, 0.0);       
        checkBoxListPane.getChildren().add(listView);
        checkBoxListPane.minWidthProperty().bind(choiceCheckBox.minWidthProperty());
        checkBoxListPane.prefWidthProperty().bind(choiceCheckBox.prefWidthProperty());
        checkBoxListPane.maxWidthProperty().bind(choiceCheckBox.maxWidthProperty());
        
        Callback<T, ObservableValue<Boolean>> itemToBoolean = item -> itemMap.get(item);		
		listView.setCellFactory(CheckBoxListCell.forListView(itemToBoolean));
        setHeightList(listView, countVisibleRaw); 
       
        // при выборе элемента в списке активируется CheckBox
        listView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            ((SimpleBooleanProperty) itemMap.get(newValue)).set(true);
        });
    }
        
    private void initChoiceBox() {
        choiceBox.showingProperty().addListener((observable, oldVal, newVal) -> {
            ObservableList<Node> children = mainPane.getChildren();
            if (newVal && !children.contains(checkBoxListPane)) {
                children.add(checkBoxListPane);
                placeBoundingNode(choiceBox, checkBoxListPane);
            }  
            else if (!newVal && children.contains(checkBoxListPane)) {
                children.remove(checkBoxListPane);
            }
        });
             
        label.addEventHandler(MouseEvent.MOUSE_PRESSED, mouseEvent -> { 
            if (choiceBox.isShowing()) { 
                choiceBox.hide();           
            } else {
                choiceBox.show();               
            }
        });         
    }
    
    private void setTextToLabel() {     
        List<T> items = new ArrayList<>(); 
        for (T item : itemMap.keySet()) {
            if (itemMap.get(item).getValue() == true) {
                items.add(item);
            }
        }
        items.sort(Comparator.comparing(T::toString));
        
        String str = "";
        for (T item : items) {
            if (!str.equals("")) {
               str +=  ", ";
            }
            str += item;
        }        
        label.setText(str);          
    }
    
    private void addListenerToCheckBox() {
        addListenerToCheckBox((observable, oldVal, newVal) -> {
            setTextToLabel();
        });
    };   
    
    public void addListenerToCheckBox(ChangeListener<? super Boolean> listener) {
        for (T item : itemMap.keySet()) {
            itemMap.get(item).addListener(listener);
        }
    }
    
    public void addItems(Map<T, ObservableValue<Boolean>> map) {
        this.itemMap.putAll(map);
        listView.getItems().addAll(map.keySet());
        sort();
        setTextToLabel();
        addListenerToCheckBox();
    }
      
    public void addItems(T... elements) {
        Map<T, ObservableValue<Boolean>> map = new HashMap<>();
        for (T item  : elements) {
            map.put(item, new SimpleBooleanProperty(false));
        }
        addItems(map);
    }
    
    private void sort() {
        listView.getItems().sort(Comparator.comparing((T key) -> key.toString()));
    }
    
    /*
    Расположить узел boundingNode так, чтобы его левая верхняя координата
    совпадала с левой нижней координатой узла focusNode.
    */
    public void placeBoundingNode(Node focusNode, Node boundingNode) {
        // It gets the (x, y) coordinates of the top left corner of the
        // bounding box of the node in focus in the local coordinate space:
		double nodeMinX = focusNode.getLayoutBounds().getMinX();
		double nodeMaxY = focusNode.getLayoutBounds().getMaxY();
        
        // It transforms the coordinates of the top left corner of the node from the local coordinate space to the
        //coordinate space of the scene:
		Point2D nodeInScene = focusNode.localToScene(nodeMinX, nodeMaxY);
        
        // Now the coordinates of the top left corner of the node are transformed from the coordinate space of the
        // scene to the coordinate space of the searchList:
		Point2D nodeInListViewLocal = boundingNode.sceneToLocal(nodeInScene);
        
        // Finally, the coordinate of the top left corner of the node is transformed to the coordinate space of the
        // parent of the searchList:
		Point2D nodeInListViewParent = boundingNode.localToParent(nodeInListViewLocal);
        
        // Position the circle approperiately
		boundingNode.relocate(
            nodeInListViewParent.getX() + boundingNode.getLayoutBounds().getMinX(),
			nodeInListViewParent.getY() + boundingNode.getLayoutBounds().getMinY()
        );     
    }

    public void setMainPane(Pane mainPane) {
        this.mainPane = mainPane;       
        this.mainPane.addEventFilter(MouseEvent.MOUSE_PRESSED, mouseEvent -> {
            //System.out.println(mouseEvent.getTarget().getClass().getSimpleName());         
            if (choiceBox.isShowing()
                && !(mouseEvent.getTarget() instanceof Label) 
                && !(mouseEvent.getTarget() instanceof CheckBox)
                && !(mouseEvent.getTarget() instanceof LabeledText)
                && !(mouseEvent.getTarget() instanceof StackPane)
                && !(mouseEvent.getTarget() instanceof CheckBoxListCell)) 
            {
                choiceBox.hide();
            }          
        });
    }
    
    public void setCountVisibleRaw(int countVisibleRaw) {
        this.countVisibleRaw = countVisibleRaw;
        setHeightList(listView, countVisibleRaw);
    }
    
    private void setHeightList(ListView listView, int maxCountVisibleRows) {      
        listView.setFixedCellSize(25);
        
        NumberBinding maxFactor =   
            new When(Bindings.size(listView.getItems()).greaterThan(maxCountVisibleRows)).
                then(maxCountVisibleRows).
                otherwise(Bindings.size(listView.getItems())); 
        
        DoubleBinding factor = 
            new When(Bindings.size(listView.getItems()).isEqualTo(0)).
                then(1.05).
                otherwise(0.05);
        
        listView.prefHeightProperty().bind(listView.fixedCellSizeProperty().multiply(maxFactor.add(factor)));
        
        listView.minHeightProperty().bind(listView.prefHeightProperty());
        listView.maxHeightProperty().bind(listView.prefHeightProperty());       
    }
    
    
    public int getCountVisibleRaw() {
        return countVisibleRaw;
    }
    
    public Map<T, ObservableValue<Boolean>> getItemMap() {
        return itemMap;
    }
       
    public AnchorPane getChoiceCheckBox() {
        return choiceCheckBox;
    }
    
    public AnchorPane getCheckBoxListPane() {
        return checkBoxListPane;
    }

    public ListView<T> getListView() {
        return listView;
    }

    public ChoiceBox<T> getChoiceBox() {
        return choiceBox;
    }

    public Label getLabel() {
        return label;
    }
    
}
