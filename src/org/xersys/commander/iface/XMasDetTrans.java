/**
 * @author Michael Cuison 2021.06.22
 */

package org.xersys.commander.iface;

public interface XMasDetTrans {
    void setListener(LMasDetTrans foValue);
    void setSaveToDisk(boolean fbValue);
    
    void setMaster(String fsFieldNm, Object foValue);
    Object getMaster(String fsFieldNm);
    
    void setMaster(int fnIndex, Object foValue);
    Object getMaster(int fnIndex);
    
    void setDetail(int fnRow, String fsFieldNm, Object foValue);
    Object getDetail(int fnRow, String fsFieldNm);
    
    void setDetail(int fnRow, int fnIndex, Object foValue);
    Object getDetail(int fnRow, int fnIndex);
    
    String getMessage();
    
    int getEditMode();
    int getItemCount();
    
    boolean addDetail();
    boolean delDetail(int fnRow);
    
    boolean NewTransaction();
    boolean NewTransaction(String fsOrderNox);
    boolean SaveTransaction(boolean fbConfirmed);
    boolean SearchTransaction();
    boolean OpenTransaction(String fsTransNox);
    boolean UpdateTransaction();
    boolean CloseTransaction();
    boolean CancelTransaction();
    boolean DeleteTransaction(String fsTransNox);
    boolean PostTransaction();
    
    Object TempTransactions();
}
