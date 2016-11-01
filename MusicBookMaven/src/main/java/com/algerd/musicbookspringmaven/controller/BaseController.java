
package com.algerd.musicbookspringmaven.controller;

import com.algerd.musicbookspringmaven.core.ContextMenuManager;
import com.algerd.musicbookspringmaven.repository.RepositoryService;

public abstract class BaseController implements ControllerContextMenu, ControllerRepositoryService {
    
    //@Inject
    protected RepositoryService repositoryService;
    //@Inject
    protected ContextMenuManager contextMenu;
    
    @Override
    public void setRepositoryService(RepositoryService repositoryService) {
        this.repositoryService = repositoryService;
    }
    
    @Override
    public void  setContextMenu(ContextMenuManager contextMenu) {
        this.contextMenu = contextMenu;
    }
    
}
