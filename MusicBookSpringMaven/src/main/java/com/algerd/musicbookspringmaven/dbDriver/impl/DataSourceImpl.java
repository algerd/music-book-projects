
package com.algerd.musicbookspringmaven.dbDriver.impl;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import com.algerd.musicbookspringmaven.dbDriver.DataSource;
import java.io.IOException;
import java.io.InputStream;

public class DataSourceImpl implements DataSource {
    
    private String driverClassName;
    private String url;
    private String user;
    private String password;
    private final Properties dbProperties = new Properties();
    private Connection connection;

    public DataSourceImpl() {     
    }
    
    public DataSourceImpl(String driverClassName, String url, String user, String password) {  
        this.driverClassName = driverClassName;
        this.url = url;
        this.user = user;
        this.password = password;
    }
    
    @Override
    public Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            registerDriver();
            if (!dbProperties.containsKey("user") && user != null) {
                dbProperties.setProperty("user", user);
            }
            if (!dbProperties.containsKey("password") && password != null) {
                dbProperties.setProperty("password", password);
            }
            connection = DriverManager.getConnection(url, dbProperties);           
            connection.setAutoCommit(false);
        }
        return connection;
    }
    
    @Override
    public Connection getConnection(String user, String password) throws SQLException {
        this.user = user;
        this.password = password;
        return getConnection();
    }
    
    @Override
    public boolean isConnection() throws SQLException {
        return connection != null && !connection.isClosed();
    }
    
    private void registerDriver() throws SQLException {
        try {
            Driver driver = (Driver) Class.forName(driverClassName).newInstance();
            DriverManager.registerDriver(driver); 
        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException ex) {
            ex.printStackTrace();
        }     
    }

    public void setDriverClassName(String driverClassName) {
        this.driverClassName = driverClassName;
    }

    public String getDriverClassName() {
        return driverClassName;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getUser() {
        return user;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    public void setDbProperties(Properties dbProperties) {
        this.dbProperties.putAll(dbProperties);
    }
    
    public void setDbProperties(String propertiesUrl) {              
        try {
            InputStream input = this.getClass().getClassLoader().getResourceAsStream(propertiesUrl);
            if (input == null) {
                throw new IOException("Sorry, unable to find " + propertiesUrl);
            }        
            Properties properties = new Properties();
            properties.load(input);
            setDbProperties(properties); 
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public Properties getDbProperties() {
        return dbProperties;
    }

    @Override
    public String toString() {
        return "DataSourceImpl{" + "driverClassName=" + driverClassName + ", url=" + url + ", user=" + user + ", password=" + password + ", connectionProperties=" + dbProperties + '}';
    }
   
}
