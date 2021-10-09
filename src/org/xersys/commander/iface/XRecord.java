package org.xersys.commander.iface;

public interface XRecord {
    boolean NewRecord();
    boolean SaveRecord();
    boolean UpdateRecord();
    boolean OpenRecord(String fsTransNox);
    boolean DeleteRecord(String fsTransNox);
    boolean DeactivateRecord(String fsTransNox);
    boolean ActivateRecord(String fsTransNox);
    
    Object getMaster(String fsFieldNm);
    void setMaster(String fsFieldNm, Object foValue);
    
    void setListener(Object foListener);

    int getEditMode();
    String getMessage();
   
    
    
    
}
