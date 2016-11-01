
package com.algerd.musicbookspringmaven.controller;

import com.algerd.musicbookspringmaven.service.aware.ContextMenuServiceAware;
import com.algerd.musicbookspringmaven.service.aware.RequestPageServiceAware;
import com.algerd.musicbookspringmaven.service.aware.RepositoryServiceAware;
import com.algerd.musicbookspringmaven.service.RequestPageService;
import com.algerd.musicbookspringmaven.service.ContextMenuService;
import com.algerd.musicbookspringmaven.service.RepositoryService;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class BaseAwareController implements 
        Controller,
        ContextMenuServiceAware, 
        RepositoryServiceAware, 
        RequestPageServiceAware {
    
    @Autowired
    protected RequestPageService requestPageService;
    @Autowired
    protected RepositoryService repositoryService;
    @Autowired
    protected ContextMenuService contextMenuService;
      
    @Override
    public void setRepositoryService(RepositoryService repositoryService) {
        this.repositoryService = repositoryService;
    }
    
    @Override
    public void  setContextMenuService(ContextMenuService contextMenuService) {
        this.contextMenuService = contextMenuService;
    }

    @Override
    public void setRequestPageService(RequestPageService requestPageService) {
        this.requestPageService = requestPageService;
    }      

}
