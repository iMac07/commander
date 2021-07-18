/**
 * @author Michael Cuison 2020.12.21
 */

package org.xersys.kumander.iface;

public interface XEntity {
    public Object getValue(int fnColumn);
    public Object getValue(String fsColumn);
    
    public String getColumn(int fnCol);
    public int getColumn(String fsCol);
    
    public void setValue(int fnColumn, Object foValue);
    public void setValue(String fsColumn, Object foValue);
    
    public int getColumnCount();
    
    public String getTable();
    public String toJSONString();
    
    public void list();
}


