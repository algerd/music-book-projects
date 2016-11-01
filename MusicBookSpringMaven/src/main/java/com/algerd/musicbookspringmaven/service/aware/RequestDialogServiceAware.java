
package com.algerd.musicbookspringmaven.service.aware;

import com.algerd.musicbookspringmaven.service.impl.RequestDialogServiceImpl;


public interface RequestDialogServiceAware {
    
    void setRequestDialogService(RequestDialogServiceImpl requestDialogService);
}
