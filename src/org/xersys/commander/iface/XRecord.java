package org.xersys.commander.iface;

public interface XRecord {
    boolean NewRecord();
    boolean NewRecord(String fsTmpTrans);
    boolean SaveRecord(boolean fbConfirmed);
    boolean UpdateRecord();
    boolean OpenRecord(String fsTransNox);
    boolean DeleteRecord(String fsTransNox);
    boolean DeactivateRecord(String fsTransNox);
    boolean ActivateRecord(String fsTransNox);

    String getMessage();
   
    void setMaster(String fsFieldNm, Object foValue);
    Object getMaster(String fsFieldNm);

    void setMaster(int fnIndex, Object foValue);
    Object getMaster(int fnIndex);
   
    void setListener(Object foListener);
    void setSaveToDisk(boolean fbValue);
    
    Object TempTransactions();
}
