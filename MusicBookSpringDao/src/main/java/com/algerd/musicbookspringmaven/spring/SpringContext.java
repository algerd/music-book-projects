
package com.algerd.musicbookspringmaven.spring;

import java.util.Objects;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class SpringContext {

    private final AnnotationConfigApplicationContext applicationContext;
    private final Object contextRoot;
    private final Class<?>[] configClasses;
    
    public SpringContext(Object contextRoot, Class<?>... configClasses) {
        this.contextRoot = Objects.requireNonNull(contextRoot);
        this.configClasses = Objects.requireNonNull(configClasses);
        this.applicationContext = new AnnotationConfigApplicationContext();      
    }

    public AnnotationConfigApplicationContext getApplicationContext() {
        return applicationContext;
    }
   
    public void init() {
        applicationContext.register(configClasses);
        applicationContext.refresh();
        applicationContext.getAutowireCapableBeanFactory().autowireBean(contextRoot);
    }
    
    public void registerSingleton(String beanName, Object singletonObject) {
        applicationContext.getBeanFactory().registerSingleton(beanName, singletonObject);
    }
    
}
