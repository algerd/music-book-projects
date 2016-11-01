
package com.algerd.musicbookspringmaven.controller;

public interface IncludeController<T extends PaneController> {
    
    void setPaneController(T paneController);
}
