
package com.algerd.musicbookspringmaven.spring;

import com.algerd.musicbookspringmaven.controller.Controller;
import java.net.URL;
import javafx.fxml.FXMLLoader;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

public class SpringFxmlLoader {
    
    @Autowired
    private ApplicationContext appContext;
    
    public static final String DIR_FXML = "/fxml/";
             
    public FXMLLoader getLoader(String url) {
        URL fxmlUrl = SpringContext.class.getResource(url);
        FXMLLoader loader = new FXMLLoader(); 
        loader.setLocation(fxmlUrl);
        loader.setControllerFactory(type -> {
            Object instance = null;          
            try {
                instance = appContext.getBean(type);
                //System.out.println("bean: " + instance);
            } catch (NoSuchBeanDefinitionException ex) {
                try {
                    instance = type.newInstance();
                    appContext.getAutowireCapableBeanFactory().autowireBean(instance);
                    //System.out.println("autowireBean: " + instance);
                } catch (InstantiationException | IllegalAccessException e) {
                    e.printStackTrace();
                }
            } 
            if (instance == null) {
                throw new NullPointerException("Bean is not exist");
            }
            return instance;
        });             
        return loader;
    }
    
    public FXMLLoader loadController(Class<? extends Controller> type) {       
        String[] arrName = type.getName().split("\\.");
        String className = arrName[arrName.length - 1].split("Controller")[0];
        String packageName = arrName[arrName.length - 2];
        if (packageName.equals("controller")) {
            packageName = "";
        }          
        String urlString = DIR_FXML + packageName + "/" + className + ".fxml";          
        return getLoader(urlString); 
    }

}
