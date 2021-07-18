/**
 * @author Michael Cuison 2021.06.22
 */

package org.xersys.kumander.iface;

public interface LMasDetTrans {
    void MasterRetreive(String fsFieldNm, Object foValue);
    void DetailRetreive(int fnRow, String fsFieldNm, Object foValue);
}
