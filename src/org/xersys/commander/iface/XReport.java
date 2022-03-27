package org.xersys.commander.iface;

public interface XReport {
    void setNautilus(XNautilus foValue);
    void hasPreview(boolean fbValue);
    void list();
    boolean getParam();
    boolean processReport();
}
