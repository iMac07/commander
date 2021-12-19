/**
 * @author Michael Cuison 2021.06.22
 */

package org.xersys.commander.iface;

public interface LOthTrans {
    void OthersRetreive(int fnRow, String fsFieldNm, Object foValue);
    void OthersRetreive(int fnRow, int fnIndex, Object foValue);    
}
