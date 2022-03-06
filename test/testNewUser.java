import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.xersys.commander.base.Nautilus;
import org.xersys.commander.base.Property;
import org.xersys.commander.base.SQLConnection;
import org.xersys.commander.base.SysUser;
import org.xersys.commander.crypt.CryptFactory;
import org.xersys.commander.iface.LRecordMas;
import org.xersys.commander.util.CommonUtil;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class testNewUser {
    static Nautilus _nautilus;
    static LRecordMas _listener;
    static SysUser _trans;
    
    public testNewUser(){}
    
    @BeforeClass
    public static void setUpClass() {        
        setupConnection();
        setupObject();
    }
    
    @AfterClass
    public static void tearDownClass() {        
        System.out.println(8 + 32 & 4);
        System.out.println(8 + 32 & 2);
        System.out.println(8 + 32 & 1);
    }
    
    @Test
    public void test01NewTransaction(){        
//        System.out.println("----------------------------------------");
//        System.out.println("test01NewTransaction() --> Start");
//        System.out.println("----------------------------------------");
//        try {
//            if (_trans.NewRecord()){
//                _trans.displayMasFields();
//                
//                _trans.setMaster("sProdctID", "icarus");
//                _trans.setMaster("sUsername", "aly");
//                _trans.setMaster("sPassword", "123456");
//                _trans.setMaster("nUserLevl", 2);
//                _trans.setMaster("nObjAcces", 1);
//                _trans.setMaster("cGloblAct", "1");
//                
//                if (!_trans.SaveRecord()) fail(_trans.getMessage());
//            } else {
//                fail(_trans.getMessage());
//            }
//        } catch (SQLException e) {
//            fail(e.getMessage());
//        }
//        System.out.println("----------------------------------------");
//        System.out.println("test01NewTransaction() --> End");
//        System.out.println("----------------------------------------");
    }
    
//    @Test
//    public void test02UpdateTransaction(){
//        System.out.println("----------------------------------------");
//        System.out.println("test02UpdateTransaction() --> Start");
//        System.out.println("----------------------------------------");
//        try {
//            if (_trans.OpenRecord("000100210002")){
//                _trans.displayMasFields();
//                
//                if (_trans.UpdateRecord()){
//                    _trans.setMaster("sProdctID", "daedalus");
//                    _trans.setMaster("sUsername", "mac");
//                    _trans.setMaster("sPassword", "michael07");
//                    _trans.setMaster("nUserLevl", 128);
//                    _trans.setMaster("cGloblAct", "0");
//
//                    _trans.IsModPurchasingUser(true);
//                    _trans.IsModSalesUser(false);
//                    _trans.IsModInventoryUser(false);
//                    _trans.IsModWarehouseUser(false);
//                    _trans.IsModAccountingUser(false);
//
//                    if (!_trans.SaveRecord()) fail(_trans.getMessage());
//                } else fail(_trans.getMessage());
//            } else {
//                fail(_trans.getMessage());
//            }
//        } catch (SQLException e) {
//            fail(e.getMessage());
//        }
//        System.out.println("----------------------------------------");
//        System.out.println("test02UpdateTransaction() --> End");
//        System.out.println("----------------------------------------");
//    }
    
//    @Test
//    public void test03UpdateStatus(){
//        System.out.println("----------------------------------------");
//        System.out.println("test03UpdateStatus() --> Start");
//        System.out.println("----------------------------------------");
//        if (_trans.OpenRecord("000100210002")){
//            if (!_trans.LockAccount()) fail(_trans.getMessage());
//            if (!_trans.SuspendAccount()) fail(_trans.getMessage());
//            if (!_trans.DeactivateAccount()) fail(_trans.getMessage());
//            if (!_trans.ActivateAccount()) fail(_trans.getMessage());
//        } else {
//            fail(_trans.getMessage());
//        }
//        System.out.println("----------------------------------------");
//        System.out.println("test03UpdateStatus() --> End");
//        System.out.println("----------------------------------------");
//    }
    
    
    private static void setupConnection(){
        String PRODUCTID = "Daedalus";
        
        //get database property
        Property loConfig = new Property("db-config.properties", PRODUCTID);
        if (!loConfig.loadConfig()){
            System.err.println(loConfig.getMessage());
            System.exit(1);
        } else System.out.println("Database configuration was successfully loaded.");
        
        //connect to database
        SQLConnection loConn = new SQLConnection();
        loConn.setProperty(loConfig);
        if (loConn.getConnection() == null){
            System.err.println(loConn.getMessage());
            System.exit(1);
        } else
            System.out.println("Connection was successfully initialized.");        
        
        //load application driver
        _nautilus = new Nautilus();
        
        _nautilus.setConnection(loConn);
        _nautilus.setEncryption(CryptFactory.make(CryptFactory.CrypType.AESCrypt));
        
        _nautilus.setUserID("000100210001");
        if (!_nautilus.load(PRODUCTID)){
            System.err.println(_nautilus.getMessage());
            System.exit(1);
        } else
            System.out.println("Application driver successfully initialized.");
    }
    
    private static void setupObject(){
        _listener = new LRecordMas() {          
            @Override
            public void MasterRetreive(int fnIndex, Object foValue) {
                System.out.println(fnIndex + " ->> " + foValue);
            }
            
            @Override
            public void MasterRetreive(String fsFieldNm, Object foValue) {
                System.out.println(fsFieldNm + " ->> " + foValue);
            }
        };
        
        _trans = new SysUser(_nautilus);
        _trans.setListener(_listener);
    }
}
