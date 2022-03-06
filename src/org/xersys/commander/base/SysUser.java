package org.xersys.commander.base;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Date;
import javax.sql.rowset.CachedRowSet;
import javax.sql.rowset.RowSetFactory;
import javax.sql.rowset.RowSetProvider;
import org.xersys.commander.contants.EditMode;
import org.xersys.commander.contants.Logical;
import org.xersys.commander.contants.TransactionStatus;
import org.xersys.commander.iface.LRecordMas;
import org.xersys.commander.iface.XNautilus;
import org.xersys.commander.util.CommonUtil;
import org.xersys.commander.util.MiscUtil;
import org.xersys.commander.util.SQLUtil;

public class SysUser{
    private final String MASTER_TABLE = "xxxSysUser";

    private final XNautilus p_oNautilus;
    private LRecordMas p_oListener;

    private String p_sMessage;
    private int p_nEditMode;

    private CachedRowSet p_oMaster;
    
    private String p_sSalesObj;
    private String p_sPurchase;
    private String p_sInventry;
    private String p_sWrehouse;
    private String p_sARAPObjx;

    public SysUser(XNautilus foNautilus){
        p_oNautilus = foNautilus;

        p_sMessage = "";
        p_nEditMode = EditMode.UNKNOWN;
    }

    public boolean NewRecord() {
        System.out.println(this.getClass().getSimpleName() + ".NewRecord()");
        p_sMessage = "";

        try {
            String lsSQL;
            ResultSet loRS;

            RowSetFactory factory = RowSetProvider.newFactory();

            //open master record
            lsSQL = MiscUtil.addCondition(getSQ_Master(), "0=1");
            loRS = p_oNautilus.executeQuery(lsSQL);
            p_oMaster = factory.createCachedRowSet();
            p_oMaster.populate(loRS);
            MiscUtil.close(loRS);

            p_oMaster.last();
            p_oMaster.moveToInsertRow();
            MiscUtil.initRowSet(p_oMaster);
            p_oMaster.updateObject("nUserLevl", 1);
            p_oMaster.updateObject("nObjAcces", 0);
            p_oMaster.updateObject("cGloblAct", "0");
            p_oMaster.updateObject("cUserStat", TransactionStatus.STATE_OPEN);
            p_oMaster.insertRow();
            p_oMaster.moveToCurrentRow();
            
            p_sSalesObj = Logical.NO;
            p_sPurchase = Logical.NO;
            p_sInventry = Logical.NO;
            p_sWrehouse = Logical.NO;
            p_sARAPObjx = Logical.NO;
        } catch (SQLException ex) {
            ex.printStackTrace();
            p_sMessage = ex.getMessage();
        }

        p_nEditMode  = EditMode.ADDNEW;
        return true;
    }

    public boolean SaveRecord() {
        try {
            if (p_nEditMode != EditMode.ADDNEW && p_nEditMode != EditMode.UPDATE){
                p_sMessage = "Invalid edit mode detected.";
                return false;
            }

            if (!isEntryOK()) return false;

            String lsSQL = "";

            if (p_nEditMode == EditMode.ADDNEW){
                setMaster("sUserIDxx", MiscUtil.getNextCode(MASTER_TABLE, "sUserIDxx", false, p_oNautilus.getConnection().getConnection(), (String) p_oNautilus.getBranchConfig("sBranchCd")));
                setMaster("sBranchCd", (String) p_oNautilus.getBranchConfig("sBranchCd"));

                lsSQL = MiscUtil.rowset2SQL(p_oMaster, MASTER_TABLE, "sClientNm");
            } else {
                lsSQL = MiscUtil.rowset2SQL(p_oMaster, MASTER_TABLE, "sClientNm", "sUserIDxx = " + SQLUtil.toSQL((String) getMaster("sUserIDxx")));
            }

            if (lsSQL.isEmpty()){
                p_sMessage = "No record to update.";
                return false;
            }

            p_oNautilus.beginTrans();

            if (p_oNautilus.executeUpdate(lsSQL, MASTER_TABLE, (String) getMaster("sBranchCd"), "") <= 0){
                p_sMessage = p_oNautilus.getMessage();
                p_oNautilus.rollbackTrans();
                return false;
            }

            p_oNautilus.commitTrans();
        } catch (SQLException e) {
            p_sMessage = e.getMessage();
            return false;
        }

        p_nEditMode = EditMode.READY;
        return true;
    }

    public boolean UpdateRecord() {
        if (p_nEditMode != EditMode.READY) {
            p_sMessage = "No record was loaded";
            return false;
        }

        p_nEditMode = EditMode.UPDATE;
        return true;
    }

    public boolean OpenRecord(String fsTransNox) {
        System.out.println(this.getClass().getSimpleName() + ".OpenRecord(String fsTransNox)");
        p_sMessage = "";

        try {
            String lsSQL;
            ResultSet loRS;

            RowSetFactory factory = RowSetProvider.newFactory();

            //open master record
            lsSQL = MiscUtil.addCondition(getSQ_Master(), "a.sUserIDxx = " + SQLUtil.toSQL(fsTransNox));
            loRS = p_oNautilus.executeQuery(lsSQL);

            if (MiscUtil.RecordCount(loRS) <= 0){
                p_sMessage = "No record loaded.";

                p_oMaster = null;
                return false;
            }

            p_oMaster = factory.createCachedRowSet();
            p_oMaster.populate(loRS);
            MiscUtil.close(loRS);
        } catch (SQLException ex) {
            ex.printStackTrace();
            p_sMessage = ex.getMessage();
        }

        p_nEditMode  = EditMode.READY;
        return true;
    }

    public boolean DeleteRecord() {
        p_sMessage = "Deletion of account is not supported.";
        return false;
    }

    public boolean DeactivateAccount() {
        if (p_nEditMode != EditMode.READY) {
            p_sMessage = "No record was loaded";
            return false;
        }

        if ("3".equals((String) getMaster("cUserStat"))) return true;

        String lsSQL = "UPDATE " + MASTER_TABLE + " SET" +
                            "  cUserStat = '3'" +
                        " WHERE sUserIDxx = " + SQLUtil.toSQL((String) getMaster("sUserIDxx"));
        
        if (p_oNautilus.executeUpdate(lsSQL, MASTER_TABLE, (String) getMaster("sBranchCd"), "") <= 0){
            p_sMessage = p_oNautilus.getMessage();
            return false;
        }
        
        return true;
    }

    public boolean ActivateAccount() {
        if (p_nEditMode != EditMode.READY) {
            p_sMessage = "No record was loaded";
            return false;
        }

        if ("1".equals((String) getMaster("cUserStat"))) return true;

        String lsSQL = "UPDATE " + MASTER_TABLE + " SET" +
                            "  cUserStat = '1'" +
                        " WHERE sUserIDxx = " + SQLUtil.toSQL((String) getMaster("sUserIDxx"));
        
        if (p_oNautilus.executeUpdate(lsSQL, MASTER_TABLE, (String) getMaster("sBranchCd"), "") <= 0){
            p_sMessage = p_oNautilus.getMessage();
            return false;
        }
        
        return true;
    }
    
    public boolean LockAccount() {
        if (p_nEditMode != EditMode.READY) {
            p_sMessage = "No record was loaded";
            return false;
        }

        if ("0".equals((String) getMaster("cUserStat"))) return true;

        String lsSQL = "UPDATE " + MASTER_TABLE + " SET" +
                            "  cUserStat = '0'" +
                        " WHERE sUserIDxx = " + SQLUtil.toSQL((String) getMaster("sUserIDxx"));
        
        if (p_oNautilus.executeUpdate(lsSQL, MASTER_TABLE, (String) getMaster("sBranchCd"), "") <= 0){
            p_sMessage = p_oNautilus.getMessage();
            return false;
        }
        
        return true;
    }
    
    public boolean SuspendAccount() {
        if (p_nEditMode != EditMode.READY) {
            p_sMessage = "No record was loaded";
            return false;
        }

        if ("2".equals((String) getMaster("cUserStat"))) return true;

        String lsSQL = "UPDATE " + MASTER_TABLE + " SET" +
                            "  cUserStat = '2'" +
                        " WHERE sUserIDxx = " + SQLUtil.toSQL((String) getMaster("sUserIDxx"));
        
        if (p_oNautilus.executeUpdate(lsSQL, MASTER_TABLE, (String) getMaster("sBranchCd"), "") <= 0){
            p_sMessage = p_oNautilus.getMessage();
            return false;
        }
        
        return true;
    }
    
    public Object getMaster(String fsIndex) {
        try {
            return getMaster(MiscUtil.getColumnIndex(p_oMaster, fsIndex));
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public Object getMaster(int fnIndex) {
        try {
            if (p_nEditMode != EditMode.READY &&
                p_nEditMode != EditMode.ADDNEW && 
                p_nEditMode != EditMode.UPDATE) return null;

            p_oMaster.first();

            switch (fnIndex){
                case 4: //sUsername
                case 5: //sPassword
                    return p_oNautilus.Decrypt(p_oMaster.getString(fnIndex));
                default:
                    return p_oMaster.getObject(fnIndex);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public void setMaster(String fsIndex, Object foValue) {
        try {
            setMaster(MiscUtil.getColumnIndex(p_oMaster, fsIndex), foValue);
        } catch (SQLException e) {
            e.printStackTrace();;
        }
    }

    public void setMaster(int fnIndex, Object foValue) {
        try {
            if (p_nEditMode != EditMode.ADDNEW && p_nEditMode != EditMode.UPDATE) return;

            p_oMaster.first();
            switch(fnIndex){
                case 1: //sUserIDxx
                case 2: //sBranchCd
                case 3: //sProdctID
                case 8: //cGloblAct
                case 10: //cUserStat
                    p_oMaster.updateObject(fnIndex, foValue);
                    p_oMaster.updateRow();
                    break;
                case 4: //sUsername
                case 5: //sPassword
                    p_oMaster.updateObject(fnIndex, p_oNautilus.Encrypt((String) foValue));
                    p_oMaster.updateRow();
                    break;
                case 6: //sClientID
                    break;
                case 7: //nUserLevl
                case 12: //nObjAcces
                    if (foValue instanceof Integer){
                        p_oMaster.updateObject(fnIndex, foValue);
                        p_oMaster.updateRow();
                    }
                    break;
                case 9: //dLastLogx
                    if (foValue instanceof Date){
                        p_oMaster.updateObject(fnIndex, foValue);
                        p_oMaster.updateRow();
                    }
                    break;
            }

            if (p_oListener != null) p_oListener.MasterRetreive(fnIndex, getMaster(fnIndex));
        } catch (SQLException e) {
        }
    }

    public void setListener(Object foListener) {
        p_oListener = (LRecordMas) foListener;
    }

    public int getEditMode() {
        return p_nEditMode;
    }

    public String getMessage() {
        return p_sMessage;
    }

    public void displayMasFields() throws SQLException{
        if (p_nEditMode != EditMode.ADDNEW && p_nEditMode != EditMode.UPDATE) return;

        int lnRow = p_oMaster.getMetaData().getColumnCount();

        System.out.println("----------------------------------------");
        System.out.println("MASTER TABLE INFO");
        System.out.println("----------------------------------------");
        System.out.println("Total number of columns: " + lnRow);
        System.out.println("----------------------------------------");

        for (int lnCtr = 1; lnCtr <= lnRow; lnCtr++){
            System.out.println("Column index: " + (lnCtr) + " --> Label: " + p_oMaster.getMetaData().getColumnLabel(lnCtr));
            if (p_oMaster.getMetaData().getColumnType(lnCtr) == Types.CHAR ||
                p_oMaster.getMetaData().getColumnType(lnCtr) == Types.VARCHAR){

                System.out.println("Column index: " + (lnCtr) + " --> Size: " + p_oMaster.getMetaData().getColumnDisplaySize(lnCtr));
            }
        }

        System.out.println("----------------------------------------");
        System.out.println("END: MASTER TABLE INFO");
        System.out.println("----------------------------------------");
    }

    private boolean isEntryOK() throws SQLException{
        p_oMaster.first();

        if (p_oMaster.getString("sProdctID").trim().isEmpty()){
            p_sMessage = "Invalid product detected.";
            return false;
        }

        if (p_oMaster.getString("sUsername").trim().isEmpty()){
            p_sMessage = "Username is not set.";
            return false;
        }

        if (p_oMaster.getString("sPassword").trim().isEmpty()){
            p_sMessage = "Password is not set.";
            return false;
        }

        return true;
    }
    
    private void encodeObjAccess() throws SQLException{
        String lsValue = p_sARAPObjx + p_sWrehouse + p_sInventry + p_sPurchase + p_sSalesObj;
        
        p_oMaster.updateObject("sObjAcces", CommonUtil.DeSerializeNumber(lsValue, 2));
        p_oMaster.updateRow();
    }
    
    private void decideObjAccess() throws SQLException{
        String lsValue = CommonUtil.SerializeNumber((long) p_oMaster.getObject("sObjAcces"), 2);

        p_sSalesObj = Logical.NO;
        p_sPurchase = Logical.NO;
        p_sInventry = Logical.NO;
        p_sWrehouse = Logical.NO;
        p_sARAPObjx = Logical.NO;
    }

    private String getSQ_Master(){
        return "SELECT" +
                    "  a.sUserIDxx" +
                    ", a.sBranchCd" +
                    ", a.sProdctID" +
                    ", a.sUsername" +
                    ", a.sPassword" +
                    ", a.sClientID" +
                    ", a.nUserLevl" +
                    ", a.cGloblAct" +
                    ", a.dLastLogx" +
                    ", a.cUserStat" +
                    ", b.sClientNm" +
                    ", a.nObjAcces" +
                " FROM " + MASTER_TABLE + " a" +
                    " LEFT JOIN Client_Master b ON a.sClientID = b.sClientID";
    }
}
