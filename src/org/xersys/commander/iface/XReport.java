package org.xersys.commander.iface;

import net.sf.jasperreports.engine.JasperPrint;

public interface XReport {
    void setNautilus(XNautilus foValue);
    void hasPreview(boolean fbValue);
    void list();
    boolean getParam();
    JasperPrint processReport();
    String getFilterID();
    
    //boolean processReport();
}
