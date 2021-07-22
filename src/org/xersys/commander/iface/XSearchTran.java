package org.xersys.commander.iface;

import org.json.simple.JSONObject;

public interface XSearchTran {
    //    JSONObject SearchMaster(String fsFieldNm, Object foValue);
    //    JSONObject SearchDetail(int fnRow, String fsFieldNm, Object foValue);
    JSONObject Search(Enum foType, String fsValue, String fsKey, String fsFilter, int fnMaxRow, boolean fbExact);
}
