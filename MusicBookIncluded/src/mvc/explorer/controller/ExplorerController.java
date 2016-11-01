package mvc.explorer.controller;

import db.entity.Album;
import db.entity.Artist;
import db.entity.Entity;
import db.entity.Song;
import db.entity.WrapUpdatedEntity;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.util.Callback;
import mvc.ContextMenuManager;
import static mvc.ContextMenuManager.ItemType.*;
import main.Main;

public class ExplorerController implements Initializable {
    
    private TreeItem rootNode;
    private TreeItem selectedItem;
    private boolean refresh = false;
    
    @FXML
    private AnchorPane explorer;
    @FXML
    private TreeView artistTree;

    @Override
    public void initialize(URL url, ResourceBundle rb) {  
        initTreeView();
    } 
    
    /**
     * Загрузить элементы в TreeView и задать CellFactory для требуемого отображения значений элементов.
     */
    private void initTreeView() {
        // Создать корневой элемент дерева сущностей	
        rootNode = new TreeItem();      
        // Передать корневой элемент дереву 
        artistTree.setRoot(rootNode);        
        // Сделать невидимым корневой элемент       
        artistTree.setShowRoot(false);
                   
        // рекурсивное добавление элементов в дерево
        fillTreeItems(Main.main.getLoader().getArtistTable().getEntities(), rootNode);
        
        // Задать CellFactory дереву
		artistTree.setCellFactory(new Callback<TreeView, TreeCell>() {
            @Override
            public TreeCell call(TreeView tv) {
                TreeCell<Entity> cell = new TreeCell<Entity>() {
                    @Override
                    public void updateItem(Entity item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            this.setText(null);
                            this.setGraphic(null);
                        } else {
                            this.setText(item.getName());
                        }
                    }
                };
                return cell;
            }
        });       
        // Добавить слушателей на выбор элемента в дереве артистов.        
        artistTree.getSelectionModel().selectedItemProperty().addListener(
            (observable, oldValue, newValue) -> {             
                selectedItem = (TreeItem) artistTree.getSelectionModel().getSelectedItem();
            }
        );           
        // Добавить слушателей на добавление или удалении сущности в массивы
        Main.main.getLoader().getArtistTable().getEntities().addListener(this::changeTreeItem);
        Main.main.getLoader().getAlbumTable().getEntities().addListener(this::changeTreeItem);
        Main.main.getLoader().getSongTable().getEntities().addListener(this::changeTreeItem);
        
        // Добавить слущателей на обновление сущности в массиве
        Main.main.getLoader().getArtistTable().updatedEntityProperty().addListener(this::updateTreeItem);
        Main.main.getLoader().getAlbumTable().updatedEntityProperty().addListener(this::updateTreeItem);
        Main.main.getLoader().getSongTable().updatedEntityProperty().addListener(this::updateTreeItem);       
    }
    
    /**
     * Рекурсивно добавить элементы entities в parentItem.
     */
    private void fillTreeItems(List<? extends Entity> entities, TreeItem parentItem) {
        for (Entity entity : entities) {
            TreeItem childItem = new TreeItem(entity);
            parentItem.getChildren().add(childItem);           
            fillTreeItems(entity.getChildren(), childItem);          
        }     
    }
    
    /**
     * Обновить дерево при добавлении/удалении элемента.
     */
    private void changeTreeItem(ListChangeListener.Change<? extends Entity> change) {
        // Добавление элемента в дерево.
        while (change.next()) {
            //Удаление элемента из дерева
            if (change.wasRemoved() && change.getRemovedSize() == 1) {                  
                // Определить удалённую сущность
                Entity removedEntity = change.getRemoved().get(0);

                // Найти TreeItem удалённого элемента
                TreeItem removedItem = searchTreeItem(rootNode, removedEntity);
                TreeItem parentRemovedItem = removedItem.getParent();

                // Удалить TreeItem удалённого элемента у родителя
                artistTree.getSelectionModel().clearSelection();
                parentRemovedItem.getChildren().remove(removedItem);
            }            
            else if (change.wasAdded() && change.getAddedSize() == 1) {
                // Определить родителя добавленного элемента
                Entity addedEntity = change.getAddedSubList().get(0);
                Entity parentAddedEntity = (Entity) addedEntity.getParent();

                // Найти TreeItem родителя добавленного элемента
                TreeItem parentItem = (parentAddedEntity == null) ? rootNode : searchTreeItem(rootNode, parentAddedEntity);

                // Добавить TreeItem элемента в TreeItem родителя
                TreeItem addedItem = new TreeItem(addedEntity);
                artistTree.getSelectionModel().clearSelection();
                parentItem.getChildren().add(addedItem); 
         
                // Раскрыть дерево родителя
                if (!parentItem.isExpanded()) {
                    parentItem.setExpanded(true);
                } 
                artistTree.scrollTo(artistTree.getRow(addedItem));
            }            
            // Перестроить всё дерево
            else if (!change.wasPermutated()) {
                rootNode.getChildren().clear();
                fillTreeItems(Main.main.getLoader().getArtistTable().getEntities(), rootNode);
            }
        }      
    }
    
    /**
     * Рекурсивно найти TreeItem сущности searchEntity из дерева rootItem.
     */
    private TreeItem searchTreeItem(TreeItem rootItem, Entity searchEntity) {
        TreeItem treeItem = null;
             
        for ( Object objectItem : rootItem.getChildren()) {
            TreeItem childItem = (TreeItem) objectItem;
            Entity childEntity = (Entity) childItem.getValue();
            if (childEntity == searchEntity) {                            
                treeItem = childItem;             
                break;
            }
            if (!childItem.isLeaf()) {
                TreeItem deepChildItem = searchTreeItem(childItem, searchEntity);
                if (deepChildItem != null) {
                    treeItem = deepChildItem;
                    break;
                }               
            }           
        }
        return treeItem;     
    }
    
    /**
     * Обновить дерево при апдете данных. 
     */
    private void updateTreeItem(ObservableValue<? extends Object> observable, Object oldValue, Object newValue) {       
        // перестроить всё дерево
        if (refresh) {          
            rootNode.getChildren().clear();
            fillTreeItems(Main.main.getLoader().getArtistTable().getEntities(), rootNode);
        }
        // получить обновленную сущность и её TreeItem
        Entity entity = ((WrapUpdatedEntity<Entity>) newValue).getNewEntity();               
        TreeItem updateItem = searchTreeItem(rootNode, entity);
        TreeItem parentItem = updateItem.getParent(); 
        
        // Заменить item на самого себя, чтобы вызвать событие - изменение айтема
        if (!refresh) {
            int index = parentItem.getChildren().indexOf(updateItem);       
            artistTree.getSelectionModel().clearSelection();
            parentItem.getChildren().set(index, updateItem);           
        }
        // выбрать айтем сущности в дереве и раскрыть его
        artistTree.getSelectionModel().select(updateItem);
        artistTree.scrollTo(artistTree.getSelectionModel().getSelectedIndex());    
        
        // Раскрыть дерево родителя и прокрутить дерево к выбранной обновленной записи
        if (!parentItem.isExpanded()) {
            parentItem.setExpanded(true);
        } 
        artistTree.scrollTo(artistTree.getRow(updateItem));             
        refresh = false;
    }
    
    /**
     * Сбросить выбор в дереве.
     * Необходимо производить при переходах на страницы, не связанные с какой-то конкретной сущностью дерева,
     * напр. на ArtistOvervew, AlbumOverview и SongOverview
     */
    public void clearSelectionTree() {
        artistTree.getSelectionModel().clearSelection();
        selectedItem = null;
    }
    
    /**
     * Выбрать (сделать selected) в дереве TreeItem сущности entity.
     */
    public void selectEntity(Entity entity) {
        artistTree.getSelectionModel().clearSelection();
        TreeItem showedItem = searchTreeItem(rootNode, entity);       
        artistTree.getSelectionModel().select(showedItem);
        // Раскрыть дерево родителя
        TreeItem parentItem = showedItem.getParent();  
        if (!parentItem.isExpanded()) {
            parentItem.setExpanded(true);
        } 
        artistTree.scrollTo(artistTree.getSelectionModel().getSelectedIndex());  
    }
    
    /**
     * ЛКМ - зызов окна выбранной сущности
     * ПКМ - вызов контекстного меню.
     */
    @FXML
    private void onMouseClickTreeView(MouseEvent mouseEvent) {
        ContextMenuManager contextMenu = Main.main.getContextMenu();
        boolean isShowingContextMenu = contextMenu.getContextMenu().isShowing();       
        contextMenu.clear();
        if (mouseEvent.getButton() == MouseButton.PRIMARY) {
            // если контекстное меню выбрано, то лкм сбрасывает контекстное меню и выбор в дереве
            if (isShowingContextMenu) {
                clearSelectionTree();
            }
            // если лкм выбрана запись - показать её
            if (selectedItem != null) {
                Entity entity = (Entity) selectedItem.getValue();
                Main.main.getMainController().showEntity(entity);
            }            
        }
        else if (mouseEvent.getButton() == MouseButton.SECONDARY) { 
            showTreeContextMenu(mouseEvent);      
        }      
    }
    
    /**
     * При ПКМ по дереву показать контекстное меню.
     */
    private void showTreeContextMenu(MouseEvent mouseEvent) { 
        ContextMenuManager contextMenu = Main.main.getContextMenu();       
        if (selectedItem != null) { 
            Entity entity = (Entity) selectedItem.getValue();

            if (entity instanceof Artist) {
                Artist artist = (Artist) entity;
                contextMenu.add(ADD_ARTIST, null);
                // запретить удаление и редактирование записи с id = 1 (Unknown artist)
                if (artist.getId() != 1) {
                    contextMenu.add(EDIT_ARTIST, artist);
                    contextMenu.add(DELETE_ARTIST, artist);
                    contextMenu.add(SEPARATOR);
                }
                contextMenu.add(ADD_ALBUM, new Album(artist));           
            }
            else if(entity instanceof Album) {   
                Album album = (Album) entity;
                contextMenu.add(ADD_ALBUM, new Album(album.getParent()));
                // запретить удаление и редактирование записи с id = 1 (Unknown album)
                if (album.getId() != 1) {
                    contextMenu.add(EDIT_ALBUM, album);
                    contextMenu.add(DELETE_ALBUM, album);
                    contextMenu.add(SEPARATOR);
                }
                contextMenu.add(ADD_SONG, new Song(album));
            } 
            else if(entity instanceof Song) {
                Song song = (Song) entity;
                contextMenu.add(ADD_SONG, new Song(song.getParent()));
                contextMenu.add(EDIT_SONG, song);
                contextMenu.add(DELETE_SONG, song);
            }                         
        }
        //Если не выбран элемент в дереве - предоставить меню: add artist
        else {
            artistTree.getSelectionModel().clearSelection();
            contextMenu.add(ADD_ARTIST, null);
        }
        contextMenu.show(explorer, mouseEvent); 
    } 

    public boolean isRefresh() {
        return refresh;
    }
    public void setRefresh(boolean refresh) {
        this.refresh = refresh;
    }
               
}
