/**
 * @author Michael Cuison 2020.12.23
 */
package org.xersys.commander.iface;

public interface XCrypt {
    public String Encrypt(String fsValue, String fsSalt);
    public String Decrypt(String fsValue, String fsSalt);
    public void setHexCrypt(int fnValue);
}
