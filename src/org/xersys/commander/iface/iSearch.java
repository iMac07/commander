package org.xersys.commander.iface;

import java.util.ArrayList;
import org.json.simple.JSONObject;

public interface iSearch {
    //void setType(Object foValue);
    void setKey(String fsValue);
    void setValue(Object foValue);
    void setMaxResult(int fnValue);
    void setExact(boolean fbValue);
    
    String getFilterList();
    int getFilterListCount();
    String getFilterListDescription(int fnRow);
    
    ArrayList<String> getColumns();
    ArrayList<String> getColumnNames();
    
    String getFilter();
    int addFilter(String fsField, Object foValue);
    boolean removeFilter(int fnRow); 
    
    String getMessage();
    
    JSONObject Search();
}
