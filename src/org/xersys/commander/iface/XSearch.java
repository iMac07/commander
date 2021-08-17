package org.xersys.commander.iface;

import org.json.simple.JSONObject;

public interface XSearch {
    public void setKey(String fsValue);
    public void setFilter(String fsValue);
    public void setMax(int fnValue);
    public void setExact(boolean fbValue);
    public JSONObject Search(Enum foType, Object foValue);
}
