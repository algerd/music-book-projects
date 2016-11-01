
package com.algerd.musicbookspringmaven.spring.config;

import com.algerd.musicbookspringmaven.service.RequestDialogService;
import com.algerd.musicbookspringmaven.service.impl.RequestDialogServiceImpl;
import com.algerd.musicbookspringmaven.service.RequestPageService;
import com.algerd.musicbookspringmaven.service.impl.RequestPageServiceImpl;
import com.algerd.musicbookspringmaven.service.ContextMenuService;
import com.algerd.musicbookspringmaven.service.impl.ContextMenuServiceImpl;
import com.algerd.musicbookspringmaven.service.DeleteAlertService;
import com.algerd.musicbookspringmaven.service.impl.DeleteAlertServiceImpl;
import com.algerd.musicbookspringmaven.spring.SpringFxmlLoader;
import com.algerd.musicbookspringmaven.service.RepositoryService;
import com.algerd.musicbookspringmaven.service.impl.RepositoryServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Import;

@Configuration
@ComponentScan("com.algerd.musicbookspringmaven")
@Import({
    RepositoryConfig.class, 
    ControllerConfig.class
})
public class AppConfig {
        
    @Bean
    public SpringFxmlLoader springFxmlLoader() {
        return new SpringFxmlLoader();
    }
    
    @Bean
    public RepositoryService repositoryService() {
        return new RepositoryServiceImpl();
    }
          
    @Bean
    @DependsOn(value="repositoryService")
    public DeleteAlertService deleteAlertService() {
        return new DeleteAlertServiceImpl();
    }
    
    @Bean
    @DependsOn(value="deleteAlertService")
    public ContextMenuService contextMenuService() {
        return new ContextMenuServiceImpl();
    }
    
    @Bean
    public RequestPageService RequestPageService() {
        return new RequestPageServiceImpl();
    }
    @Bean
    public RequestDialogService requestDialogService() {
        return new RequestDialogServiceImpl();
    }
          
}
