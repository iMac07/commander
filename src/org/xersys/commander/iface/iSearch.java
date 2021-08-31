package org.xersys.commander.iface;

import java.util.ArrayList;
import org.json.simple.JSONObject;

public interface iSearch {
    void setKey(String fsValue);
    void setValue(Object foValue);
    void setMaxResult(int fnValue);
    void setExact(boolean fbValue);
    
    int getMaxResult();
    
    Object getValue();
    
    ArrayList<String> getFilterListDescription();
    
    ArrayList<String> getColumns();
    ArrayList<String> getColumnNames();
    
    ArrayList<String> getFilter();
    int addFilter(String fsField, Object foValue);
    Object getFilterValue(String fsField);
    boolean removeFilter(String fsField); 
    boolean removeFilter();
    
    String getMessage();
    
    JSONObject Search();
}
