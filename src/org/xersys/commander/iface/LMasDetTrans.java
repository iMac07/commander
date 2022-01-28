/**
 * @author Michael Cuison 2021.06.22
 */

package org.xersys.commander.iface;

public interface LMasDetTrans {
    void MasterRetreive(String fsFieldNm, Object foValue);
    void MasterRetreive(int fnIndex, Object foValue);
    void DetailRetreive(int fnRow, String fsFieldNm, Object foValue);
    void DetailRetreive(int fnRow, int fnIndex, Object foValue);
}
