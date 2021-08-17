package org.xersys.commander.iface;

public interface XPaymentInfo {
    boolean NewTransaction();
    boolean SaveTransaction();
    boolean OpenTransaction();
    boolean UpdateTransaction();
    boolean CloseTransaction();
    boolean PostTransaction();
    boolean CancelTransaction();    
    
    boolean addDetail();
    boolean delDetail(int fnRow);
    
    Object getDetail(int fnRow, String fsFieldNm);
    void setDetail(int fnRow, String fsFieldNm, Object foValue);    
    
    void setSourceCd(String fsValue);
    void setSourceNo(String fsValue);
    
    void setListener(LRecordMas foValue);
    
    int getItemCount();
    double getPaymentTotal();
    
    String getMessage();
}
