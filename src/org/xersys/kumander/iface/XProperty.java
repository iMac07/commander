/**
 * @author Michael Cuison 2020.12.23
 */
package org.xersys.kumander.iface;

public interface XProperty {
    public void setProductID(String fsValue);
    public String getProductID();
    
    public void setConfigDIR(String fsValue);
    
    public boolean loadConfig();
    public String getConfig(String fsValue);
    public void setConfig(String fsConfig, String fsValue);
    public void save(String fsFileName);
        
    public String getDBDriver();
    public String getDBName();
    public String getUser();
    public String getPassword();
    public String getPort();
    public String getDBHost();
    public String getMainServer();
    public String getClientID();
    
    public void setDBHost(String fsValue);
    public void setDBName(String fsValue);
    public void setPort(String fsValue);
    
    public String getMessage();
}
