/**
 * @author Michael Cuison 2020.12.23
 */
package org.xersys.kumander.base;

import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import org.xersys.kumander.iface.XConnection;
import org.xersys.kumander.iface.XCrypt;
import org.xersys.kumander.iface.XNautilus;
import org.xersys.kumander.util.MiscUtil;

public class Nautilus implements XNautilus{
    private final String SIGNATURE = "07071991";
    
    XConnection poConn;
    XCrypt poCrypt;
    
    Date pdSysDate;    
    
    String psUserIDxx;
    String psMessagex;
    
    boolean pbLoaded;
    
    public Nautilus(){
        pbLoaded = false;
        psUserIDxx = "";
        psMessagex = "";
    }
    
    
    @Override
    public void setConnection(XConnection foValue) {
        poConn = foValue;
    }

    @Override
    public XConnection getConnection() {
        return poConn;
    }
    
    @Override
    public void setEncryption(XCrypt foValue) {
        poCrypt = foValue;
    }

    @Override
    public XCrypt getEncryption() {
        return poCrypt;
    }
    
    @Override
    public Connection doConnect() {
        return poConn.doConnect();
    }
    
    @Override
    public void setUserID(String fsValue) {
        psUserIDxx = fsValue;
    }
    
    @Override
    public String Encrypt(String fsValue) {
        return poCrypt.Encrypt(fsValue, SIGNATURE);
    }

    @Override
    public String Decrypt(String fsValue) {
        return poCrypt.Decrypt(fsValue, SIGNATURE);
    }

    @Override
    public void SystemDate(Date fdValue) {
        pdSysDate = fdValue;
    }

    @Override
    public Date SystemDate() {
        return pdSysDate;
    }
    
    @Override
    public Object getUserInfo(String fsValue) {
        return null;
    }

    @Override
    public Object getAppConfig(String fsValue) {
        return null;
    }

    @Override
    public Object getBranchConfig(String fsValue) {
        return null;
    }

    @Override
    public Object getSysConfig(String fsValue) {
        switch (fsValue){
            case "sBranchCd":
                return "X001";
            default:
                return null;
        }
    }

    @Override
    public Timestamp getServerDate() {
        Timestamp loTimeStamp = null;
        ResultSet loRS = null;
        String lsSQL = "";
        
        if (!pbLoaded){
            setMessage("Application driver is not initialized.");
            return loTimeStamp;
        }
        
        setMessage("");

        try{
            Connection loConn = poConn.getConnection();
            
            if(loConn == null){
                setMessage("Connection is not set.");
                return loTimeStamp;
            }

            if(loConn.getMetaData().getDriverName().equalsIgnoreCase("SQLite JDBC")){
                lsSQL = "SELECT DATETIME('now','localtime')";
                
                loRS = loConn.createStatement()
                     .executeQuery(lsSQL);
                //position record pointer to the first record
                loRS.next();
                //assigned timestamp

                loTimeStamp = Timestamp.valueOf(loRS.getString(1));
            }else{
                //assume that default database is MySQL ODBC
                lsSQL = "SELECT SYSDATE()";
                
                loRS = loConn.createStatement()
                    .executeQuery(lsSQL);
                //position record pointer to the first record
                loRS.next();
                //assigned timestamp
                loTimeStamp = loRS.getTimestamp(1);
            }            
        }
        catch(SQLException ex){
            ex.printStackTrace();
            setMessage(ex.getSQLState());
        } finally{
            MiscUtil.close(loRS);
        }
        return loTimeStamp;
    }

    @Override
    public boolean lockUser() {
        return true;
    }

    @Override
    public boolean load(String fsProdctID) {
        System.out.println("Initializing application driver.");
        pbLoaded = false;
        
        setMessage("");
        
        if (fsProdctID.isEmpty()){
            setMessage("Product ID is not set.");
            return false;
        }
        
        if (!psUserIDxx.isEmpty()){
            return loginUser(fsProdctID, psUserIDxx);
        } else {
            if (!poConn.getProperty().getProductID().equals(fsProdctID)){
                poConn.getProperty().setProductID(fsProdctID);

                if (poConn.doConnect() == null){
                    System.err.println(poConn.getMessage());
                    return false;
                }

                System.out.println("Connection was successfully initialized.");
            }
        }
        
        pbLoaded = true;
        return true;
    }

    @Override
    public boolean loginUser(String fsProdctID, String fsUserIDxx) {
        System.out.println("Initializing application driver.");
        pbLoaded = false;
        
        setMessage("");
        
        if (fsProdctID.isEmpty()){
            setMessage("Product ID is not set.");
            return false;
        }
        
        if (fsUserIDxx.isEmpty()){
            setMessage("User ID is not set.");
            return false;
        }
        
        if (!poConn.getProperty().getProductID().equals(fsProdctID)){
            poConn.getProperty().setProductID(fsProdctID);

            if (poConn.doConnect() == null){
                System.err.println(poConn.getMessage());
                return false;
            }

            System.out.println("Connection was successfully initialized.");
        }
    
        //TODO:
        //  load user information here
        
        pbLoaded = true;
        return true;
    }

    @Override
    public boolean logoutUser() {
        if (!pbLoaded){
            setMessage("Application driver is not initialized.");
            return false;
        }
        
        //TODO:
        //  add logout user procedure here
        
        return true;
    }

    @Override
    public boolean unlockUser() {
        if (!pbLoaded){
            setMessage("Application driver is not initialized.");
            return false;
        }
        
        //TODO:
        //  add unlock user procedure here
        
        return true;
    }

    @Override
    public boolean beginTrans() {
        if (!pbLoaded){
            setMessage("Application driver is not initialized.");
            return false;
        }
        
        return poConn.beginTrans();
    }

    @Override
    public boolean commitTrans() {
        if (!pbLoaded){
            setMessage("Application driver is not initialized.");
            return false;
        }
        
        return poConn.commitTrans();
    }

    @Override
    public boolean rollbackTrans() {
        if (!pbLoaded){
            setMessage("Application driver is not initialized.");
            return false;
        }
        
        return poConn.rollbackTrans();
    }

    @Override
    public ResultSet executeQuery(String fsValue) {
        if (!pbLoaded){
            setMessage("Application driver is not initialized.");
            return null;
        }
        
        return poConn.executeQuery(fsValue);
    }

    @Override
    public long executeUpdate(String fsValue, String fsTableNme, String fsBranchCd, String fsDestinat) {
        long lnRow = -1;
        
        if (!pbLoaded){
            setMessage("Application driver is not initialized.");
            return lnRow;
        }
        
        //TODO:
        //  add replication management here
        
        return executeUpdate(fsValue);
    }
    
    @Override
    public long executeUpdate(String fsValue) {        
        long lnRow = -1;
        
        if (!pbLoaded){
            setMessage("Application driver is not initialized.");
            return lnRow;
        }
        
        lnRow =  poConn.executeUpdate(fsValue);
        
        if (lnRow <= 0) setMessage(poConn.getMessage());
        
        return lnRow;
    }
    
    @Override
    public String getMessage() {
        return psMessagex;
    }

    //added methods
    private void setMessage(String fsValue) {
        psMessagex = fsValue;
    }
}
