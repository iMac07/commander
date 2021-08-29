package org.xersys.commander.iface;

public interface XPayments {
    boolean NewTransaction();
    boolean SaveTransaction();
    boolean UpdateTransaction();
    boolean OpenTransaction(String fsTransNox);
    boolean CloseTransaction();
    boolean PrintTransaction();
    
    void setSourceCd(String fsValue);
    void setSourceNo(String fsValue);

    void setMaster(String fsFieldNm, Object foValue);
    Object getMaster(String fsFieldNm);
    
    XPaymentInfo getCreditCardInfo();
    void setCardInfo(XPaymentInfo foValue);
    
    XPaymentInfo getChequeInfo();
    void setChequeInfo(XPaymentInfo foValue);
    
    XPaymentInfo getGCInfo();
    void setGCInfo(XPaymentInfo foValue);
    
    void setListener(LRecordMas foValue);
    
    String getMessage();
}
