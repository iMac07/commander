package org.xersys.commander.iface;

import org.json.simple.JSONObject;

public interface XNeoSearch {
    public void setSearchType(Object foValue);
    public void setKey(String fsValue);
    public void setFilter(String fsValue);
    public void setMax(int fnValue);
    public void setExact(boolean fbValue);
    public JSONObject Search(Object foValue);
}
