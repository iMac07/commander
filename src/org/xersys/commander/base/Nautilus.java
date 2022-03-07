/**
 * @author Michael Cuison 2020.12.23
 */
package org.xersys.commander.base;

import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import javax.sql.rowset.CachedRowSet;
import javax.sql.rowset.RowSetFactory;
import javax.sql.rowset.RowSetProvider;
import org.xersys.commander.iface.XConnection;
import org.xersys.commander.iface.XCrypt;
import org.xersys.commander.iface.XNautilus;
import org.xersys.commander.util.MiscUtil;
import org.xersys.commander.util.SQLUtil;

public class Nautilus implements XNautilus{
    private final String SIGNATURE = "07071991";
    
    private XConnection poConn;
    private XCrypt poCrypt;
    
    private Date pdSysDate;    
    
    private String psUserIDxx;
    private String psMessagex;
    
    private boolean pbLoaded;
    
    private CachedRowSet poClient;
    private CachedRowSet poUser;
    
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
        try {
            poUser.first();
            return poUser.getObject(fsValue);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Object getAppConfig(String fsValue) {
        return null;
    }

    @Override
    public Object getBranchConfig(String fsValue) {
        try {
            poClient.first();
            return poClient.getObject(fsValue);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Object getSysConfig(String fsValue) {
        Object loValue = null;
        
        if (poConn == null) return loValue;
        if (fsValue.isEmpty()) return loValue;
        
        String lsSQL = "SELECT sConfigVl FROM xxxSysConfig WHERE sConfigCd = " + SQLUtil.toSQL(fsValue);
        
        ResultSet loRS = poConn.executeQuery(lsSQL);
        
        try {
            if (loRS.next()) loValue = loRS.getObject("sConfigVl");
            
            MiscUtil.close(loRS);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        
        return loValue;
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
    
        if (!loadClient()) return false;
        if (!loadUser(fsProdctID)) return false;
        
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
        
        //execute statement
        System.out.println(fsValue);
        lnRow = executeUpdate(fsValue);
        
        //execute statement to replicaton
        if (!fsTableNme.isEmpty()){
            Timestamp tme = getServerDate();

            StringBuilder lsSQL = new StringBuilder();
            StringBuilder lsNme = new StringBuilder();

            //set fieldnames
            lsNme.append("(sTransNox");
            lsNme.append(", sBranchCd");
            lsNme.append(", sStatemnt");
            lsNme.append(", sTableNme");
            lsNme.append(", sDestinat");
            lsNme.append(", sModified");
            lsNme.append(", dEntryDte");
            lsNme.append(", dModified)");

            //set values
            lsSQL.append("(" + SQLUtil.toSQL(MiscUtil.getNextCode("xxxReplicationLog", "sTransNox", true, poConn.getConnection(), fsBranchCd)));
            lsSQL.append(", " + SQLUtil.toSQL(fsBranchCd));
            lsSQL.append(", " + SQLUtil.toSQL(fsValue));
            lsSQL.append(", " + SQLUtil.toSQL(fsTableNme));
            lsSQL.append(", " + SQLUtil.toSQL(fsDestinat));
            lsSQL.append(", " + SQLUtil.toSQL((psUserIDxx == null ? "" : psUserIDxx)));
            lsSQL.append(", " + SQLUtil.toSQL(tme));
            lsSQL.append(", " + SQLUtil.toSQL(tme) + ")");
            
            executeUpdate("INSERT INTO xxxReplicationLog" + lsNme.toString() + " VALUES" + lsSQL.toString());
            
            tme = null;
            lsSQL = null;
            lsNme = null;
        }
        
        return lnRow;
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
    
    private boolean loadUser(String fsProdctID){
        try {
            RowSetFactory factory = RowSetProvider.newFactory();

            String lsSQL = MiscUtil.addCondition(getSQ_User(), "a.sUserIDxx = " + SQLUtil.toSQL(psUserIDxx));
            ResultSet loRS = poConn.executeQuery(lsSQL);

            poUser = factory.createCachedRowSet();
            poUser.populate(loRS);
            MiscUtil.close(loRS);

            if (!poUser.next()){
                setMessage("Unable to load user information.");
                return false;
            }
            
            switch (poUser.getString("cUserStat")){
                case "0":
                    setMessage("Account status is LOCKED. Unable to login.");
                    return false;
                case "2":
                    setMessage("Account status is SUSPENDED. Unable to login.");
                    return false;
                case "3":
                    setMessage("Account status is INACTIVE. Unable to login.");
                    return false;
            }
            
            if (!fsProdctID.equalsIgnoreCase(poUser.getString("sProdctID"))){
                if (!poUser.getString("cGloblAct").equals("1")){
                    setMessage("Account is NOT A MEMBER of this application. Unable to login.");
                    return false;
                }
            }
            
            return true;
        } catch (SQLException e) {
            setMessage(e.getMessage());
        }
        
        return false;
    }
    
    private boolean loadClient(){
        try {
            RowSetFactory factory = RowSetProvider.newFactory();
        
            String lsSQL = poConn.getProperty().getClientID();
            lsSQL = "sClientID = " + SQLUtil.toSQL(lsSQL);

            ResultSet loRS = poConn.executeQuery(MiscUtil.addCondition(getSQ_Client(), lsSQL));

            poClient = factory.createCachedRowSet();
            poClient.populate(loRS);
            MiscUtil.close(loRS);
            
            if (!poClient.next()){
                setMessage("Unable to load system client information.");
                return false;
            }

            return true;
        } catch (SQLException e) {
            setMessage(e.getMessage());
        }
        
        return false;
    }
    
    private String getSQ_User(){
        return "SELECT" +
                    "  a.sUserIDxx" +	
                    ", a.sBranchCd" +
                    ", a.sProdctID" +	
                    ", a.sUsername" +	
                    ", a.sPassword" +	
                    ", a.sClientID" +	
                    ", a.nUserLevl" +
                    ", a.nObjAcces" +
                    ", a.cGloblAct" +
                    ", a.dLastLogx" +
                    ", a.cUserStat" +
                    ", IFNULL(b.sClientNm, 'UNKNOWN USER') xClientNm" +
                " FROM xxxSysUser a" +
                    ", Client_Master b" +
                " WHERE a.sClientiD = b.sClientID" +
                    " AND b.cEmployee = '1'";
                     
    }
    
    private String getSQ_Client(){
        return "SELECT" +
                    "  a.sClientID" +
                    ", a.sBranchCd" +
                    ", a.sCompnyNm" +
                    ", a.sAddressx" +
                    ", a.sTownIDxx" +
                    ", a.sTINIDxxx" +
                    ", a.sTelNoxxx" +
                    ", a.sFaxNoxxx" +
                    ", a.sContactx" +
                    ", a.sManagrID" +
                    ", a.cMainOffc" +
                    ", a.cWarehous" +
                    ", a.dCutOffxx" +
                    ", a.cRecdStat" +
                    ", IFNULL(b.sTownName, '') xTownName" +
                    ", '' xManagerID" +
                " FROM xxxSysClient a" +
                    " LEFT JOIN TownCity b ON a.sTownIDxx = b.sTownIDxx";
    }
}
