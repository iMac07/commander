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
   
    void setListener(Object foListener);
    void setSaveToDisk(boolean fbValue);
    
    int getEditMode();
    
    Object TempTransactions();
}
