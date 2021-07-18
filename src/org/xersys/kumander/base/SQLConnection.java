/**
 * @author Michael Cuison 2020.12.21
 */
package org.xersys.kumander.base;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import org.xersys.kumander.iface.XConnection;
import org.xersys.kumander.iface.XProperty;
import org.xersys.kumander.util.MiscUtil;

public class SQLConnection implements XConnection{   
    private XProperty poProp = null;
    
    private boolean pbIsOnline = false;
    private Connection poConn;
    private String psMessage;
    
    @Override
    public void setProperty(XProperty foValue) {
        poProp = foValue;
    }
    
    @Override
    public XProperty getProperty() {
        return poProp;
    }   
    
    @Override
    public void IsOnline(boolean fbValue) {
        pbIsOnline = fbValue;
    }

    @Override
    public boolean IsOnline() {
        return pbIsOnline;
    }
    
    @Override
    public Timestamp getServerDate(){
        Connection loCon = null;
        
        if(poConn == null)
           loCon = doConnect();
        else
           loCon = poConn;
        
        ResultSet loRS = null;
        Timestamp loTimeStamp = null;
        String lsSQL = "";

        try{
            if(loCon == null){
                return loTimeStamp;
            }

            if(loCon.getMetaData().getDriverName().equalsIgnoreCase("SQLiteJDBC") ||
                    loCon.getMetaData().getDriverName().equalsIgnoreCase("SQLDroid")){
                lsSQL = "SELECT DATETIME('now','localtime')";
            }else{
                //assume that default database is MySQL ODBC
                lsSQL = "SELECT SYSDATE()";
            }            

            loRS = loCon.createStatement()
                     .executeQuery(lsSQL);
            //position record pointer to the first record
            loRS.next();
            //assigned timestamp
            loTimeStamp = loRS.getTimestamp(1);            
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
    public boolean beginTrans() {
        try {
            poConn.setAutoCommit(false);
            return true;
        } catch (SQLException ex) {
            setMessage(ex.getMessage());
            return false;
        }
    }

    @Override
    public boolean commitTrans() {
        boolean lbSuccess;
        try {
            poConn.commit();
            poConn.setAutoCommit(true);
            lbSuccess = true;
        } catch (SQLException ex) {
            ex.printStackTrace();            
            setMessage(ex.getMessage());
            lbSuccess = false;
        }
      
        return lbSuccess;
    }

    @Override
    public boolean rollbackTrans() {
        boolean lbSuccess;
        try {
            poConn.rollback();
            poConn.setAutoCommit(true);
            lbSuccess = true;
        } catch (SQLException ex) {
            ex.printStackTrace();            
            setMessage(ex.getMessage());
            lbSuccess = false;
        }
        return lbSuccess;
    }

    @Override
    public Connection getConnection() {
        if(poConn == null){
            System.out.println("Reset Connection");
            poConn = doConnect();
        }

        return poConn;
    }

    @Override
    public ResultSet executeQuery(String fsValue) {
        Statement loSQL = null;
        ResultSet oRS = null;
        try {
            loSQL = poConn.createStatement();
            oRS = loSQL.executeQuery(fsValue);
        } catch (SQLException ex) {
            setMessage(ex.getMessage());
            ex.printStackTrace();
            oRS = null;
        }
        
        return oRS;
    }

    @Override
    public long executeUpdate(String fsValue) {
        Statement loSQL = null;
        Statement loLog = null;
        long lnRecord = 0;
        
        if (poConn == null){
            setMessage("Connection object is null.");
            return lnRecord;
        }
        
        try {
            loSQL = poConn.createStatement();
            lnRecord = loSQL.executeUpdate(fsValue);
        } catch (SQLException ex) {
            setMessage(ex.getMessage());
            lnRecord = 0;
        }finally{
            MiscUtil.close(loSQL);
        }

        return lnRecord;
    }

    @Override
    public String getMessage() {
        return psMessage;
    }

    //added methods    
    private void setMessage(String fsValue) {
        psMessage = fsValue;
    }
    
    @Override
    public Connection doConnect(){
        if (poProp == null){
            setMessage("UNSET server configuration.");
            return null;
        }
        
        System.out.println("Initializing connection.");
      
        String lsDBSrvrNm = poProp.getDBHost();
        String lsDBNameXX = poProp.getDBName();
        
        String lsDBUserNm = "";
        String lsPassword = "";
        String lsDBSrvrMn = "";
        String lsDBPortNo = "";
        
        if (!(poProp.getUser() == null || poProp.getUser().isEmpty()) &&
            !(poProp.getPassword() == null || poProp.getPassword().isEmpty())){
            lsDBUserNm = poProp.getUser();
            lsPassword = poProp.getPassword();
            lsDBPortNo = poProp.getPort();
            lsDBSrvrMn = poProp.getMainServer();
        }
        
        Connection loCon = null;        
        
        try {
            if(lsPassword.isEmpty()){
                loCon = MiscUtil.getConnection(lsDBSrvrNm, lsDBNameXX);
            } else{
                if (pbIsOnline){
                    System.out.println("Connecting to " + lsDBSrvrMn + ".");
                    loCon = MiscUtil.getConnection(lsDBSrvrMn, lsDBNameXX, lsDBUserNm, lsPassword, lsDBPortNo);
                } else{
                    System.out.println("Connecting to " + lsDBSrvrNm + ".");            
                    loCon = MiscUtil.getConnection(lsDBSrvrNm, lsDBNameXX, lsDBUserNm, lsPassword, lsDBPortNo);
                }
            }
        } catch (SQLException | ClassNotFoundException ex) {
            ex.printStackTrace();
            setMessage(ex.getMessage());
            return null;
        }
        
        return loCon;
    }
}
