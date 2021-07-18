package org.xersys.kumander.util;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.xersys.kumander.iface.XEntity;

public class MiscUtil {
    /**
     * Creates a Connection object for a MySQL server.
     * 
     * @param fsURL      the IP/Hostname of the MySQL server.
     * @param fsDatabase the name of the database to be used in the connection
     * @param fsUserID   the MySQL user's name
     * @param fsPassword the MySQL user's password 
     * @return           instance of the connection class.
     * @throws SQLException
     * @throws ClassNotFoundException 
     */
    public static Connection getConnection(String fsURL, String fsDatabase, String fsUserID, String fsPassword) throws SQLException, ClassNotFoundException{
        return getConnection(fsURL, fsDatabase, fsUserID, fsPassword, "3306");
    }

    /**
     * Connect to the MySQL Data using custom set port
     * 
     * @param fsURL      the IP/Hostname of the MySQL server.
     * @param fsDatabase the name of the database to be used in the connection
     * @param fsUserID   the MySQL user's name
     * @param fsPassword the MySQL user's password 
     * @param fsPort     the MySQL server's listening port.
     * @return           instance of the connection class.
     * @throws SQLException
     * @throws ClassNotFoundException 
     */
    public static Connection getConnection(String fsURL, String fsDatabase, String fsUserID, String fsPassword, String fsPort) throws SQLException, ClassNotFoundException{
        Connection oCon = null;
        Class.forName("com.mysql.jdbc.Driver");
        oCon = DriverManager.getConnection("jdbc:mysql://" + fsURL + ":" + fsPort + "/" + fsDatabase + "?useUnicode=true&characterEncoding=ISO-8859-1", fsUserID, fsPassword);
        return oCon;
    }  
   
    /**
    * Connect to the SQLite database.
    * 
    * @param fsURL      the location of the SQLite file
    * @param fsDatabase the name of the SQLite file
    * @return           the BasicDataSource instance.
    * @throws SQLException
    * @throws ClassNotFoundException 
    */
    public static Connection getConnection(String fsPackageN, String fsDatabase) throws SQLException, ClassNotFoundException{
        try {
            DriverManager.registerDriver((Driver) Class.forName("org.sqldroid.SQLDroidDriver").newInstance());
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Failed to register SQLDroidDriver");
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Failed to register SQLDroidDriver");
        } catch (InstantiationException e) {
            throw new RuntimeException("Failed to register SQLDroidDriver");
        } catch (SQLException e) {
            throw new RuntimeException("Failed to register SQLDroidDriver");
        }
        
        
        try {
            String jdbcUrl = "jdbc:sqldroid:" + "/data/data/" + fsPackageN + "/databases/" + fsDatabase;
            Connection loConn = DriverManager.getConnection(jdbcUrl);
            
            if (loConn == null){
                jdbcUrl = "jdbc:sqldroid:" + "/data/user/0/" + fsPackageN + "/databases/" + fsDatabase;
                return DriverManager.getConnection(jdbcUrl);
            }
            return loConn;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }    
    
    /**
     * Closes the ResultSet object.
     * 
     * @param foRs   The ResultSet to close.
     */
    public static void close(java.sql.ResultSet foRs) {
        if ( foRs == null) {
            return;
        }
        
        try {
            foRs.close();
        } catch(Exception ex) {
            ex.printStackTrace();
            //Ignore the error
        }
    }

    /**
     * Closes the Statement object.
     * 
     * @param foStmt The statement object to close.
     */
    public static void close(java.sql.Statement foStmt) {
        if (foStmt == null) {
            return;
        }
      
        try {
            foStmt.close();
        } catch(Exception ex) {
            ex.printStackTrace();
            //Ignore the error
        } finally{
            foStmt = null;
        }
    }

    /**
     * Closes the PreparedStatement object.
     * 
     * @param foPstmt The PreparedStatement to close. 
     */
    public static void close(java.sql.PreparedStatement foPstmt) {
        if (foPstmt == null) {
            return;
        }
      
        try {
            foPstmt.close();
        } catch(Exception ex) {
            ex.printStackTrace();
            //Ignore the error
        }
        finally{
            foPstmt = null;
        }
    }

    /**
     * Closes the Connection object.
     * 
     * @param foConn The connection object to close.
     */
    public static void close(java.sql.Connection foConn) {
        System.out.println("close:" + foConn.toString());
        if (foConn == null) {
            return;
        }
      
        try {
            foConn.close();
        } catch(Exception ex) {
            ex.printStackTrace();
            //Ignore the error
        } finally{
            foConn = null;
        }
    }
    
    /**
    * Count the number of rows within the ResultSet.
    * 
    * @param rs   The ResultSet to count.
    * @return     The number of rows within the ResultSet.
    */ 
    public static long RecordCount(java.sql.ResultSet rs) {
        long pos;
        long ctr;
        boolean frst = false;
        boolean last = false;
      
        try {
            frst = rs.isBeforeFirst();
            last = rs.isAfterLast();
            pos = rs.getRow();

            rs.beforeFirst();

            ctr = 0;
            while(rs.next())
                ctr++;

            if(pos > 0) {
                rs.absolute((int) pos);
            } else if(frst){
                rs.beforeFirst();
            } else{
                rs.afterLast();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            ctr=0;
        }
    	
        return ctr;
    }

    /**
    * Creates a SQL INSERT statement from a POJO instance.
    * 
    * @param foNewEntity   The POJO to convert into SQL INSERT statement.
    * @return              The SQL INSERT equivalent of GEntity.
    */ 
    public static String makeSQL(XEntity foNewEntity){
        StringBuilder lsSQL = new StringBuilder();
        StringBuilder lsNme = new StringBuilder();
        for(int lnCol = 1; !foNewEntity.getColumn(lnCol).equals("");lnCol++){
            lsSQL.append(", " + SQLUtil.toSQL(foNewEntity.getValue(lnCol)));
            lsNme.append(", " + foNewEntity.getColumn(lnCol));
        }

        return "INSERT INTO " + foNewEntity.getTable() + "(" + lsNme.toString().substring(1) + ") VALUES (" +  
            lsSQL.toString().substring(1) + ")";
    }
    
    public static String makeSQL(XEntity foNewEntity, XEntity foPrevEntity, String fsCondition){
        StringBuilder lsSQL = new StringBuilder();
        int lnCol1 = 0;
        int lnCol2 = 0;

        for(int lnCol = 1; !foNewEntity.getColumn(lnCol).equals("");lnCol++){
            if(lnCol1 == 0 || lnCol2 == 0){
                if(foNewEntity.getColumn(lnCol).equalsIgnoreCase("smodified"))
                    lnCol1 = lnCol;
                else if(foNewEntity.getColumn(lnCol).equalsIgnoreCase("dmodified"))
                    lnCol2 = lnCol;
                else{
                    if(!SQLUtil.equalValue(foNewEntity.getValue(lnCol), foPrevEntity.getValue(lnCol))) 
                        lsSQL.append( ", " + foNewEntity.getColumn(lnCol) + " = " + SQLUtil.toSQL(foNewEntity.getValue(lnCol)));
            }
         } else{
            if(!SQLUtil.equalValue(foNewEntity.getValue(lnCol), foPrevEntity.getValue(lnCol))) 
                lsSQL.append( ", " + foNewEntity.getColumn(lnCol) + " = " + SQLUtil.toSQL(foNewEntity.getValue(lnCol)));
            }
        }

        //If no update was detected return an empty string
        if(lsSQL.toString().equals(""))
            return "";

        //Add the value of smodified if the field is available
        if(lnCol1 > 0)
            lsSQL.append( ", sModified = " + SQLUtil.toSQL(foNewEntity.getValue(lnCol1)));
        //Add the value of dmodified if the field is available
        if(lnCol2 > 0)
            lsSQL.append( ", dModified = " + SQLUtil.toSQL(foNewEntity.getValue(lnCol2)));

//        System.out.println("UPDATE " + foNewEntity.getTable() + " SET" +
//                            lsSQL.toString().substring(1) +
//                            " WHERE " + fsCondition);

        return "UPDATE " + foNewEntity.getTable() + " SET" +
                lsSQL.toString().substring(1) +
                " WHERE " + fsCondition;
    }    

    public static String addCondition(String SQL, String condition){
        int lnIndex;
        StringBuffer lsSQL = new StringBuffer(SQL);
      
        if(lsSQL.indexOf("WHERE") > 0){
            //inside
            if(lsSQL.indexOf("GROUP BY") > 0){
                lnIndex = lsSQL.indexOf("GROUP BY");
                lsSQL.insert(lnIndex, "AND (" + condition + ") ");
            } else if(lsSQL.indexOf("HAVING") > 0){
                lnIndex = lsSQL.indexOf("HAVING");
                lsSQL.insert(lnIndex, "AND (" + condition + ") ");
            } else if(lsSQL.indexOf("ORDER BY") > 0){
                lnIndex = lsSQL.indexOf("ORDER BY");
                lsSQL.insert(lnIndex, "AND (" + condition + ") ");
            } else if(lsSQL.indexOf("LIMIT") > 0){
                lnIndex = lsSQL.indexOf("LIMIT");
                lsSQL.insert(lnIndex, "AND (" + condition + ") ");
            } else
                lsSQL.append(" AND (" + condition + ")");
            //inside
        } else if(lsSQL.indexOf("GROUP BY") > 0){
            lnIndex = lsSQL.indexOf("GROUP BY");
            lsSQL.insert(lnIndex, "WHERE " + condition + " ");
        } else if(lsSQL.indexOf("HAVING") > 0){
            lnIndex = lsSQL.indexOf("HAVING");
            lsSQL.insert(lnIndex, "WHERE " + condition + " ");
        } else if(lsSQL.indexOf("ORDER BY") > 0){
            lnIndex = lsSQL.indexOf("ORDER BY");
            lsSQL.insert(lnIndex, "WHERE " + condition + " ");
        } else if(lsSQL.indexOf("LIMIT") > 0){
            lnIndex = lsSQL.indexOf("LIMIT");
            lsSQL.insert(lnIndex, "WHERE " + condition + " ");
        } else{
            lsSQL.append(" WHERE " + condition);
        }   
        
        return lsSQL.toString();
    }

    public static String getNextCode(
        String fsTableNme,
        String fsFieldNme,
        boolean fbYearFormat,
        java.sql.Connection foCon,
        String fsBranchCd,
        int fnFieldLen,
        boolean fbSeries){
        
        String lsNextCde = "";
        int lnNext;
        String lsPref = fsBranchCd;

        String lsSQL = null;
        Statement loStmt = null;
        ResultSet loRS = null;

        if(fbYearFormat){
            try {
                if(foCon.getMetaData().getDriverName().equalsIgnoreCase("SQLiteJDBC") ||
                        foCon.getMetaData().getDriverName().equalsIgnoreCase("SQLDroid")){
                    lsSQL = "SELECT STRFTIME('%Y', DATETIME('now','localtime'))";
                }else{
                    //assume that default database is MySQL ODBC
                    lsSQL = "SELECT YEAR(CURRENT_TIMESTAMP)";
                }          
            
                loStmt = foCon.createStatement();
                loRS = loStmt.executeQuery(lsSQL);
                loRS.next();
                System.out.println(loRS.getString(1));
                lsPref = lsPref + loRS.getString(1).substring(2);
                System.out.println(lsPref);
            } catch (SQLException ex) {
                Logger.getLogger(MiscUtil.class.getName()).log(Level.SEVERE, null, ex);
                return "";
            } finally{
                close(loRS);
                close(loStmt);
            }
        }
        
        try {
            if (!fbSeries){
                lsSQL = "SELECT " + fsFieldNme
                    + " FROM " + fsTableNme
                    + " ORDER BY " + fsFieldNme + " DESC "
                    + " LIMIT 1";

                if(!lsPref.isEmpty())
                    lsSQL = addCondition(lsSQL, fsFieldNme + " LIKE " + SQLUtil.toSQL(lsPref + "%"));
            } else {
                if(foCon.getMetaData().getDriverName().equalsIgnoreCase("SQLiteJDBC") ||
                        foCon.getMetaData().getDriverName().equalsIgnoreCase("SQLDroid")){
                    lsSQL = "SELECT SUBSTR(" + fsFieldNme + ", " + lsPref.length() + ", " + (fnFieldLen - lsPref.length()) + ")"
                            + " FROM " + fsTableNme
                            + " ORDER BY " + fsFieldNme + " DESC "
                            + " LIMIT 1";
                }else{
                    lsSQL = "SELECT RIGHT(" + fsFieldNme + ", " + (fnFieldLen - lsPref.length()) + ")"
                            + " FROM " + fsTableNme
                            + " ORDER BY " + fsFieldNme + " DESC "
                            + " LIMIT 1";
                }        
            }
            
            loStmt = foCon.createStatement();
            loRS = loStmt.executeQuery(lsSQL);
            if(loRS.next()){
                lnNext = Integer.parseInt(loRS.getString(1).substring(lsPref.length()));
            } else
                lnNext = 0;
            lsNextCde = lsPref + StringUtils.leftPad(String.valueOf(lnNext + 1), fnFieldLen - lsPref.length(), "0");
        } catch (SQLException ex) {
            Logger.getLogger(MiscUtil.class.getName()).log(Level.SEVERE, null, ex);
            lsNextCde = "";
        } finally{
            close(loRS);
            close(loStmt);
        }

        return lsNextCde;
    }
    
    public static String getNextCode(
        String fsTableNme,
        String fsFieldNme,
        boolean fbYearFormat,
        java.sql.Connection foCon,
        String fsBranchCd){
        String lsNextCde="";
        int lnNext;
        String lsPref = fsBranchCd;

        String lsSQL = null;
        Statement loStmt = null;
        ResultSet loRS = null;

        if(fbYearFormat){
            try {
                if(foCon.getMetaData().getDriverName().equalsIgnoreCase("SQLiteJDBC")){
                    lsSQL = "SELECT STRFTIME('%Y', DATETIME('now','localtime'))";
                }else{
                    //assume that default database is MySQL ODBC
                    lsSQL = "SELECT YEAR(CURRENT_TIMESTAMP)";
                }          
            
                loStmt = foCon.createStatement();
                loRS = loStmt.executeQuery(lsSQL);
                loRS.next();
                System.out.println(loRS.getString(1));
                lsPref = lsPref + loRS.getString(1).substring(2);
                System.out.println(lsPref);
            } 
            catch (SQLException ex) {
                ex.printStackTrace();
                Logger.getLogger(MiscUtil.class.getName()).log(Level.SEVERE, null, ex);
                return "";
            }
            finally{
                close(loRS);
                close(loStmt);
            }
        }
      
        lsSQL = "SELECT " + fsFieldNme
                + " FROM " + fsTableNme
                + " ORDER BY " + fsFieldNme + " DESC "
                + " LIMIT 1";

        if(!lsPref.isEmpty())
            lsSQL = addCondition(lsSQL, fsFieldNme + " LIKE " + SQLUtil.toSQL(lsPref + "%"));
      
        try {
            loStmt = foCon.createStatement();
            loRS = loStmt.executeQuery(lsSQL);
            if(loRS.next()){
               lnNext = Integer.parseInt(loRS.getString(1).substring(lsPref.length()));
            }
            else
               lnNext = 0;

            lsNextCde = lsPref + StringUtils.leftPad(String.valueOf(lnNext + 1), loRS.getMetaData().getPrecision(1) - lsPref.length() , "0");

        } 
        catch (SQLException ex) {
            ex.printStackTrace();
            Logger.getLogger(MiscUtil.class.getName()).log(Level.SEVERE, null, ex);
            lsNextCde = "";
        }
        finally{
            close(loRS);
            close(loStmt);
        }

        return lsNextCde;
    }

    public static String getNextCode(
      String fsTableNme,
      String fsFieldNme,
      boolean fbYearFormat,
      java.sql.Connection foCon,
      String fsBranchCd,
      String fsFilter){
      String lsNextCde="";
      int lnNext;
      String lsPref = fsBranchCd;

      String lsSQL = null;
      Statement loStmt = null;
      ResultSet loRS = null;

      if(fbYearFormat){
         try {
            if(foCon.getMetaData().getDriverName().equalsIgnoreCase("SQLiteJDBC")){
               lsSQL = "SELECT STRFTIME('%Y', DATETIME('now','localtime'))";
            }else{
               //assume that default database is MySQL ODBC
               lsSQL = "SELECT YEAR(CURRENT_TIMESTAMP)";
            }          
            loStmt = foCon.createStatement();
            loRS = loStmt.executeQuery(lsSQL);
            loRS.next();
            lsPref = lsPref + loRS.getString(1).substring(2);
         } 
         catch (SQLException ex) {
            ex.printStackTrace();
            Logger.getLogger(MiscUtil.class.getName()).log(Level.SEVERE, null, ex);
            return "";
         }
         finally{
            close(loRS);
            close(loStmt);
         }
      }

      lsSQL = "SELECT " + fsFieldNme
           + " FROM " + fsTableNme
           + " ORDER BY " + fsFieldNme + " DESC "
           + " LIMIT 1";

      if(!lsPref.isEmpty())
         lsSQL = addCondition(lsSQL, fsFieldNme + " LIKE " + SQLUtil.toSQL(lsPref + "%"));
         
      lsSQL = addCondition(lsSQL, fsFilter);
      
      try {
         loStmt = foCon.createStatement();
         loRS = loStmt.executeQuery(lsSQL);
         if(loRS.next()){
            lnNext = Integer.parseInt(loRS.getString(1).substring(lsPref.length()));
         }
         else
            lnNext = 0;


         lsNextCde = lsPref + StringUtils.leftPad(String.valueOf(lnNext + 1), loRS.getMetaData().getPrecision(1) - lsPref.length() , "0");

      } 
      catch (SQLException ex) {
         ex.printStackTrace();
         Logger.getLogger(MiscUtil.class.getName()).log(Level.SEVERE, null, ex);
         lsNextCde = "";
      }
      finally{
         close(loRS);
         close(loStmt);
      }

      return lsNextCde;
   }

    public static String makeSelect(XEntity foObject) {
        StringBuilder lsSQL = new StringBuilder();
        lsSQL.append("SELECT ");
        lsSQL.append(foObject.getColumn(1));

        for(int lnCol=2; lnCol<=foObject.getColumnCount(); lnCol++){
            lsSQL.append(", " + foObject.getColumn(lnCol));
        }

        lsSQL.append( " FROM " + foObject.getTable());
        return lsSQL.toString();
    }
   
    public static Map row2Map(ResultSet rs){
        Map map = new HashMap();
            
        try {
            if(rs.isAfterLast() || rs.isBeforeFirst()) return null;
            
            ResultSetMetaData rsmd = rs.getMetaData();
            int count = rsmd.getColumnCount();
            
            for (int i = 1; i <= count; i++) {
                String key = rsmd.getColumnName(i);
                
                switch(rsmd.getColumnType(i)){
                case java.sql.Types.ARRAY:
                    map.put(key, rs.getArray(i));
                    break;
                case java.sql.Types.BIGINT:
                    map.put(key, rs.getLong(i));
                    break;
                case java.sql.Types.REAL:
                    map.put(key, rs.getFloat(i));
                    break;
                case java.sql.Types.BOOLEAN:
                case java.sql.Types.BIT:    
                    map.put(key, rs.getBoolean(i));
                    break;
                case java.sql.Types.BLOB:
                    map.put(key, rs.getBlob(i));
                    break;
                case java.sql.Types.DOUBLE:
                case java.sql.Types.FLOAT:
                    map.put(key, rs.getDouble(i));
                    break;
                case java.sql.Types.INTEGER:
                    map.put(key, rs.getInt(i));
                    break;
                case java.sql.Types.NVARCHAR:
                    map.put(key, rs.getNString(i));
                    break;
                case java.sql.Types.VARCHAR:
                case java.sql.Types.CHAR:
                case java.sql.Types.LONGVARCHAR:
                    map.put(key, rs.getString(i));
                    break;
                case java.sql.Types.NCHAR:
                case java.sql.Types.LONGNVARCHAR:
                    map.put(key, rs.getNString(i));
                    break;
                case java.sql.Types.TINYINT:
                    map.put(key, rs.getByte(i));
                    break;
                case java.sql.Types.SMALLINT:
                    map.put(key, rs.getShort(i));
                    break;
                case java.sql.Types.DATE:
                    map.put(key, rs.getDate(i));
                    break;
                case java.sql.Types.TIME:
                    map.put(key, rs.getTime(i));
                    break;
                case java.sql.Types.TIMESTAMP:
                    map.put(key, rs.getTimestamp(i));
                    break;
                case java.sql.Types.BINARY:
                case java.sql.Types.VARBINARY:
                    map.put(key, rs.getBytes(i));
                    break;
                case java.sql.Types.LONGVARBINARY:
                    map.put(key, rs.getBinaryStream(i));
                    break;
                case java.sql.Types.CLOB:
                    map.put(key, rs.getClob(i));
                    break;
                case java.sql.Types.NUMERIC:
                case java.sql.Types.DECIMAL:
                    map.put(key, rs.getBigDecimal(i));
                    break;
                case java.sql.Types.DATALINK:
                    map.put(key, rs.getURL(i));
                    break;
                case java.sql.Types.REF:
                    map.put(key, rs.getRef(i));
                    break;
                case java.sql.Types.STRUCT:
                case java.sql.Types.DISTINCT:
                case java.sql.Types.JAVA_OBJECT:
                    map.put(key, rs.getObject(i));
                    break;
                default:
                    map.put(key, rs.getString(i));
                }
            }
        } catch (SQLException ex) {
            map = null;
        }        
        
        return map;
    } 

    public static List rows2Map(ResultSet rs){
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

        try {
            while(rs.next()){
                Map map = row2Map(rs);
                if(map != null)
                    list.add(map);
            }
        } catch (SQLException ex) {
            list = null;
        }

        return list;
    }

    public static String makeSQL(ResultSetMetaData rsmd, Map foMap, String fsTable, String fsExclude){
        StringBuilder lsSQL = new StringBuilder();
        StringBuilder lsNme = new StringBuilder();
        String column;
        
        try {
            int count = rsmd.getColumnCount();
            
            for(int i=1; i <= count; i++){
                column = rsmd.getColumnName(i);
                if(!fsExclude.contains(column)){
                    lsNme.append(", " + column);
                    lsSQL.append(", " + SQLUtil.toSQL(foMap.get(column)));
                }
            }
        } catch (SQLException ex) {
            lsSQL = new StringBuilder();
        }

        if(lsSQL.toString().isEmpty())
            return "";
        else
            return "INSERT INTO " 
                    + fsTable + " (" + lsNme.toString().substring(1) + ")" 
                    + " VALUES (" + lsSQL.toString().substring(1) + ")";
    }

    public static String makeSQL(ResultSetMetaData rsmd, Map foNewMap, Map foOldMap, String fsTable, String fsWhere, String fsExclude){
        StringBuilder lsSQL = new StringBuilder();
        String column;
        
        try {
            int count = rsmd.getColumnCount();
            
            for(int i=1; i <= count; i++){
                column = rsmd.getColumnName(i);
                
                if(!fsExclude.contains(column)){
                    if(!SQLUtil.equalValue(foNewMap.get(column), foOldMap.get(column)))
                        lsSQL.append(", " + column + " = " + SQLUtil.toSQL(foNewMap.get(column)));
                }
            }
        } catch (SQLException ex) {
            lsSQL = new StringBuilder();
        }

        if(lsSQL.toString().isEmpty())
            return "";
        else
            return "UPDATE " + fsTable + " SET" + lsSQL.toString().substring(1) + " WHERE " + fsWhere;
    }
    
    public static JSONArray RS2JSON(ResultSet foSource){        
        JSONArray loArray = new JSONArray();
        JSONObject loJSON;
        
        try {
            while (foSource.next()){
                loJSON = new JSONObject();
                
                for (int lnCtr = 1; lnCtr <= foSource.getMetaData().getColumnCount(); lnCtr++){
                    loJSON.put(foSource.getMetaData().getColumnLabel(lnCtr), foSource.getObject(lnCtr));
                }
                loArray.add(loJSON);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        
        return loArray;
    }
    
    public static JSONArray RS2JSON(ResultSet foSource, String fsFields){   
        if (fsFields.isEmpty()) return RS2JSON(foSource);
        
        JSONArray loArray = new JSONArray();
        JSONObject loJSON;
        String lasFields [] = fsFields.split("»");
        int lnCtr;
        
        try {
            while (foSource.next()){
                loJSON = new JSONObject();
                
                for(lnCtr = 0; lnCtr <= lasFields.length - 1; lnCtr++){
                    loJSON.put(lasFields[lnCtr], foSource.getObject(lasFields[lnCtr]));
                }                
                
                loArray.add(loJSON);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        
        return loArray;
    }
}