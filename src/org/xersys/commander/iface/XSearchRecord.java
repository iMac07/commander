package org.xersys.commander.iface;

import org.json.simple.JSONObject;

public interface XSearchRecord {
    JSONObject Search(Enum foType, String fsValue, String fsKey, String fsFilter, int fnMaxRow, boolean fbExact);
    JSONObject SearchRecord(String fsValue, String fsKey, String fsFilter, int fnMaxRow, boolean fbExact);
}
