package org.xersys.commander.util;

import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang3.StringUtils;
import org.xersys.commander.iface.XNautilus;

public class CommonUtil {
    public static String getPCName(){
        try{
            return InetAddress.getLocalHost().getHostName();
        }catch (Exception ex){
            ex.printStackTrace();
            return null;
        }
    }
    
    public static String nameFormat(String fsLastNme, String fsFrstName, String fsMiddName, String fsSuffixNm){
        if (fsLastNme.isEmpty() || fsFrstName.isEmpty()){
            return "";
        }
        
        String lsValue = fsLastNme + ", " + fsFrstName;
        
        if (!fsSuffixNm.isEmpty()){
            lsValue += " " + fsSuffixNm;
        }
        
        if (!fsMiddName.isEmpty()){
            lsValue += " " + fsMiddName;
        }
        
        return lsValue;
    }
    
    public static String[] splitName(String fsName){
        String laNames[] = {"", "", ""};
        fsName = fsName.trim();

        if(fsName.length() > 0){
            String laNames1[] = fsName.split(",");
            laNames[0] = laNames1[0].trim();
            laNames[1] = laNames1[1].trim();
            
            if(laNames1.length > 1){
                String lsFrstName = laNames1[1].trim();
                
                if(lsFrstName.length() > 0){
                    laNames1 = lsFrstName.split("»");
                    laNames[1] = laNames1[0];
                    
                    if(laNames1.length > 1)
                        laNames[2] = laNames1[1];
                    }       
                }

            if(laNames[0].trim().length() == 0)
                laNames[0] = "%";
            if(laNames[1].trim().length() == 0)
                laNames[1] = "%";
            if(laNames[2].trim().length() == 0)
                laNames[2] = "%";
        }
        return laNames;
    }
    
    public static Object createInstance(String classname){
        Class<?> x;
        Object obj = null;
        
        try {
            x = Class.forName(classname);
            obj = x.newInstance();
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        } catch (InstantiationException ex) {
            ex.printStackTrace();
        } catch (IllegalAccessException ex) {
            ex.printStackTrace();
        }
        
        return obj;
    }
    
    public static int getRandom(int num){
        Random rand = new Random();
        return rand.nextInt(num) + 1;
    }
    
    public static int getRandom(int fnLow, int fnHigh){
        Random r = new Random();
        return r.nextInt(fnHigh - fnLow) + fnLow;
    }
    
    public static Date dateAdd(Date date, int toAdd){
        return dateAdd(date, Calendar.DATE, toAdd);
    }
   
    public static Date dateAdd(Date date, int field, int toAdd){
        Calendar c1 = Calendar.getInstance();
        c1.setTime(date);
        c1.add(field, toAdd);
        return c1.getTime();
    }
    
    public static String StringToHex(String str) {
        char[] chars = Hex.encodeHex(str.getBytes(StandardCharsets.UTF_8));

        return String.valueOf(chars);
    }
    
    public static String HexToString(String hex) {
        String result = "";
        try {
            byte[] bytes = Hex.decodeHex(hex);
            result = new String(bytes, StandardCharsets.UTF_8);
        } catch (DecoderException e) {
            throw new IllegalArgumentException("Invalid Hex format!");
        }
        return result;
    }
    
    public static String SerializeNumber(long value){
        return Dec2Radix(value, 36);
    }
    
    public static String SerializeNumber(long value, int number){
        return Dec2Radix(value, number);
    }
    
    public static long DeSerializeNumber(String value){
        return Radix2Dec(value, 36);
    }
    
    public static long DeSerializeNumber(String value, int number){
        return Radix2Dec(value, number);
    }
    
    private static String Dec2Radix(long value, int radix){
        return Long.toString(value, radix).toUpperCase();
    }
    
    private static long Radix2Dec(String value, int radix){
        return Long.parseLong(value, radix);
    }
    
    public static long dateDiff(Date date1, Date date2){
        return (date1.getTime() - date2.getTime()) / (1000 * 60 * 60 * 24);
    }
    
    public static String getNextReference(Connection foConnection, String fsTableNme, String fsFieldNme, String fsFilter){
        if (foConnection == null) return "";
        if (fsTableNme.isEmpty()) return "";
        if (fsFieldNme.isEmpty()) return "";
        
        String lsSQL = ""; 
        String lsPref = "";
        int lnNext = 0;
        
        Statement loStmt = null;
        ResultSet loRS = null;
        
        try {
            lsSQL = "SELECT " + fsFieldNme + 
                    " FROM " + fsTableNme +
                    " ORDER BY " + fsFieldNme + " DESC LIMIT 1"; 
            
            if (!fsFilter.isEmpty()) lsSQL = MiscUtil.addCondition(lsSQL, fsFilter);
            
            loStmt = foConnection.createStatement();
            loRS = loStmt.executeQuery(lsSQL);
            
            if (loRS.next()) lnNext = Integer.parseInt(loRS.getString(1).substring(lsPref.length()));
            
            lsSQL = lsPref + StringUtils.leftPad(String.valueOf(lnNext + 1), loRS.getMetaData().getPrecision(1) - lsPref.length() , "0");;
            
            
        } catch (SQLException ex) {
            System.err.print(ex.getMessage());
            lsSQL = "";
        } finally{
            MiscUtil.close(loRS);
            MiscUtil.close(loStmt);
        }
        
        return lsSQL;
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
                MiscUtil.close(loRS);
                MiscUtil.close(loStmt);
            }
        }
      
        lsSQL = "SELECT " + fsFieldNme
                + " FROM " + fsTableNme
                + " ORDER BY " + fsFieldNme + " DESC "
                + " LIMIT 1";

        if(!lsPref.isEmpty())
            lsSQL = MiscUtil.addCondition(lsSQL, fsFieldNme + " LIKE " + SQLUtil.toSQL(lsPref + "%"));
      
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
            MiscUtil.close(loRS);
            MiscUtil.close(loStmt);
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
            MiscUtil.close(loRS);
            MiscUtil.close(loStmt);
         }
      }

      lsSQL = "SELECT " + fsFieldNme
           + " FROM " + fsTableNme
           + " ORDER BY " + fsFieldNme + " DESC "
           + " LIMIT 1";

      if(!lsPref.isEmpty())
         lsSQL = MiscUtil.addCondition(lsSQL, fsFieldNme + " LIKE " + SQLUtil.toSQL(lsPref + "%"));
         
      lsSQL = MiscUtil.addCondition(lsSQL, fsFilter);
      
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
         MiscUtil.close(loRS);
         MiscUtil.close(loStmt);
      }

      return lsNextCde;
   }
    
    public static boolean saveTempOrder(XNautilus foNautilus, String fsSourceCd, String fsOrderNox, String fsPayloadx){
        if (foNautilus == null) return false;
        if (fsSourceCd == null) return false;
        
        String lsSQL = "INSERT INTO xxxTempTransactions SET" +
                            "  sSourceCd = " + SQLUtil.toSQL(fsSourceCd) +
                            ", sOrderNox = " + SQLUtil.toSQL(fsOrderNox) +
                            ", dCreatedx = " + SQLUtil.toSQL(foNautilus.getServerDate()) +
                            ", sPayloadx = '" + fsPayloadx + "'" +
                            ", cRecdStat = '1'";
        
        return foNautilus.executeUpdate(lsSQL) != 0;
    }
    
    public static boolean saveTempOrder(XNautilus foNautilus, String fsSourceCd, String fsOrderNox, String fsPayloadx, String fsRecdStat){
        if (foNautilus == null) return false;
        
        String lsSQL = "UPDATE xxxTempTransactions SET" +
                            "  sPayloadx = '" + fsPayloadx + "'" +
                            ", cRecdStat = " + SQLUtil.toSQL(fsRecdStat) +
                        " WHERE sSourceCd = " + SQLUtil.toSQL(fsSourceCd) +
                            " AND sOrderNox = " + SQLUtil.toSQL(fsOrderNox);
        
        return foNautilus.executeUpdate(lsSQL) != 0;
    }
    
    public static boolean saveTempOrder(XNautilus foNautilus, String fsSourceCd, String fsOrderNox, String fsPayloadx, String fsRecdStat, String fsTransNox){
        if (foNautilus == null) return false;
        
        String lsSQL = "UPDATE xxxTempTransactions SET" +
                            "  sPayloadx = '" + fsPayloadx + "'" +
                            ", cRecdStat = " + SQLUtil.toSQL(fsRecdStat) +
                            ", sTransNox = " + SQLUtil.toSQL(fsTransNox) +
                        " WHERE sSourceCd = " + SQLUtil.toSQL(fsSourceCd) +
                            " AND sOrderNox = " + SQLUtil.toSQL(fsOrderNox);
        
        return foNautilus.executeUpdate(lsSQL) != 0;
    }
    
    public static ResultSet getTempOrder(XNautilus foNautilus, String fsSourceCd, String fsOrderNox){
        if (foNautilus == null) return null;
        
        String lsSQL = "SELECT * FROM xxxTempTransactions" +
                        " WHERE sSourceCd = " + SQLUtil.toSQL(fsSourceCd);
        
        if (!fsOrderNox.isEmpty()) lsSQL = MiscUtil.addCondition(lsSQL, "sOrderNox = " + SQLUtil.toSQL(fsOrderNox));
        
        return foNautilus.executeQuery(lsSQL);
    }
    
    public static String getTempOrder(XNautilus foNautilus, String fsSourceCd){
        if (foNautilus == null) return null;
        
        String lsSQL = "";
        ResultSet loRS = null;
        
        try {
            lsSQL = "SELECT * FROM xxxTempTransactions" +
                        " WHERE sSourceCd = " + SQLUtil.toSQL(fsSourceCd) +
                            " AND cRecdStat = '1'" +
                        " ORDER BY dCreatedx" +
                        " LIMIT 1";
            
            loRS = foNautilus.executeQuery(lsSQL);
            
            if (loRS.next())
                lsSQL = loRS.getString("sOrderNox");
            else
                lsSQL = "";
        } catch (SQLException ex) {
            System.err.print(ex.getMessage());
            lsSQL = "";
        } finally{
            MiscUtil.close(loRS);
        }
        
        return lsSQL;
    }
    
    public static ArrayList<Temp_Transactions> loadTempTransactions(XNautilus foNautilus, String fsSourceCd){
        String lsSQL = "SELECT * FROM xxxTempTransactions" +
                        " WHERE cRecdStat = '1'" +
                            " AND sSourceCd = " + SQLUtil.toSQL(fsSourceCd);
        
        ResultSet loRS = foNautilus.executeQuery(lsSQL);
        
        Temp_Transactions loTemp;
        ArrayList<Temp_Transactions> p_oTemp = new ArrayList<>();
        
        try {
            while(loRS.next()){
                loTemp = new Temp_Transactions();
                loTemp.setSourceCode(loRS.getString("sSourceCd"));
                loTemp.setOrderNo(loRS.getString("sOrderNox"));
                loTemp.setDateCreated(SQLUtil.toDate(loRS.getString("dCreatedx"), SQLUtil.FORMAT_TIMESTAMP));
                loTemp.setPayload(loRS.getString("sPayloadx"));
                p_oTemp.add(loTemp);
            }
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        } finally {
            MiscUtil.close(loRS);
        }
        
        return p_oTemp;
    }
}
