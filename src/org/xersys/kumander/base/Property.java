/**
 * @author Michael Cuison 2020.12.23
 */
package org.xersys.kumander.base;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.xersys.kumander.crypt.MySQLAES;
import org.xersys.kumander.iface.XProperty;

public class Property implements XProperty{
    private final String SIGNATURE = "07071991";
    
    private MySQLAES aes = null;
    private Properties prop = null;
    
    private String psProductID = "";
    private String psMessage = "";
    
    private String psDBSrvrMn = null;  // ip address of central server
    private String psDBSrvrNm = null;
    private String psDBNameXX = null;
    private String psDBPassWD = null;
    private String psDBUserNm = null;
    private String psDBPortNo = null;
    private String psDriverNm = null;
    private String psClientID = null;
    
    public Property() {
        prop = new Properties();
        aes = new MySQLAES();
    }
    
    public Property(String fsFile, String fsProdctID){
        prop = new Properties();
        aes = new MySQLAES();
      
        try {
            prop.load(new FileInputStream(fsFile));
            psProductID = fsProdctID;
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    public Property(InputStream foValue, String fsProdctID){
        prop = new Properties();
        aes = new MySQLAES();
      
        try {
            prop.load(foValue);
            psProductID = fsProdctID;
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    @Override
    public void setConfigDIR(String fsValue) {
        prop = new Properties();
      
        try {
            prop.load(new FileInputStream(fsValue));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    @Override
    public void setProductID(String fsValue) {
        psProductID = fsValue;
    }
    
    @Override
    public String getProductID() {
        return psProductID;
    }
    
    @Override
    public boolean loadConfig() {
        System.out.println("Loading database configuration.");
        
        try{                 
            psDBNameXX = aes.Decrypt(getConfig("Database"), SIGNATURE);
            psDBSrvrNm = aes.Decrypt(getConfig("ServerName"), SIGNATURE);
            psDBSrvrMn = aes.Decrypt(getConfig("MainServer"), SIGNATURE);         
            
            if(!getConfig("UserName").isEmpty()){
                psDBUserNm = aes.Decrypt(getConfig("UserName"), SIGNATURE);
                psDBPassWD = aes.Decrypt(getConfig("Password"), SIGNATURE);
            } else{
                psDBUserNm = "";
                psDBPassWD = "";
            }

            psDBPortNo = aes.Decrypt(getConfig("Port"), SIGNATURE);
            psDriverNm = getConfig("DBDriver");
            psClientID = getConfig("ClientID");         
        }catch(NumberFormatException ex){
            ex.printStackTrace();
            setMessage(ex.getMessage());
            return false;
        }
             
        if(psDBNameXX.equals("") ||
            psDBSrvrNm.equals("") ||
            psClientID.equals("")) {
            setMessage("Invalid configuration values!");
            return false;
        }

        if (psDBPortNo == null || psDBPortNo.equals("")){
            psDBPortNo = "3306";
        }

        return true;
    }
    
    @Override
    public String getConfig(String fsValue) {
        return prop.getProperty(psProductID + "-" + fsValue);
    }

    @Override
    public void setConfig(String fsConfig, String fsValue) {
        switch(fsConfig){
            case "ServerName":
                psDBSrvrNm = fsValue;
                break;
            case "MainServer":
                psDBSrvrMn = fsValue;
                break;
            case "Database":
                psDBNameXX = fsValue;
                break;
            case "UserName":
                psDBUserNm = fsValue;
                break;
            case "Password":
                psDBPassWD = fsValue;
                break;
            case "Port":
                psDBPortNo = fsValue;
                break;
            case "DBDriver":
                psDriverNm = fsValue;
                break;
            case "ClientID":
                psClientID = fsValue;
                break;
            default:
                return;
        }
        
        prop.setProperty(psProductID + "-" + fsConfig, fsValue);
    }

    @Override
    public void save(String fsFileName) {
        try {
            Properties loProp = prop;
            
            loProp.setProperty(psProductID + "-ServerName", aes.Encrypt(getDBHost(), SIGNATURE));
            loProp.setProperty(psProductID + "-MainServer", aes.Encrypt(getMainServer(), SIGNATURE));
            loProp.setProperty(psProductID + "-Database", aes.Encrypt(getDBName(), SIGNATURE));
            loProp.setProperty(psProductID + "-UserName", aes.Encrypt(getUser(), SIGNATURE));
            loProp.setProperty(psProductID + "-Password", aes.Encrypt(getPassword(), SIGNATURE));
            loProp.setProperty(psProductID + "-Port", aes.Encrypt(getPort(), SIGNATURE));
            
            loProp.store(new FileOutputStream(fsFileName), null);
            loProp = null;
        } catch (IOException ex) {
            ex.printStackTrace();
            Logger.getLogger(Property.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public String getClientID() {
        return psClientID;
    }
    
    @Override
    public String getDBDriver() {
        return psDriverNm;
    }
    
    @Override
    public String getDBHost() {
        return psDBSrvrNm;
    }

    @Override
    public String getMainServer() {
        return psDBSrvrMn;
    }    

    @Override
    public String getPort() {
        return psDBPortNo;
    }

    @Override
    public String getDBName() {
        return psDBNameXX;
    }

    @Override
    public String getUser() {
        return psDBUserNm;
    }

    @Override
    public String getPassword() {
        return psDBPassWD;
    }

    @Override
    public void setDBHost(String fsValue) {
        psDBSrvrNm = fsValue;
    }
    
    @Override
    public void setDBName(String fsValue) {
        psDBNameXX = fsValue;
    }

    @Override
    public void setPort(String fsValue) {
        psDBPortNo = fsValue;
    }

    @Override
    public String getMessage() {
        return psMessage;
    }

    //added methods
    private void setMessage(String fsValue){
        psMessage = fsValue;
    }
}