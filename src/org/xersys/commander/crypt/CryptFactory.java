/**
 * @author Michael Cuison 2020.12.23
 */
package org.xersys.commander.crypt;

import org.xersys.commander.iface.XCrypt;

public class CryptFactory {
    public enum CrypType{
        AESCrypt,
        XCrypt
    }
    
    public static XCrypt make(CryptFactory.CrypType foType){
        switch (foType){
            case AESCrypt:
                return new MySQLAES();
            case XCrypt:
                return new GCrypt();
            default:
                return null;
        }
    }
}
