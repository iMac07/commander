/**
 * @author Michael Cuison 2020.12.23
 */
package org.xersys.kumander.crypt;

import org.xersys.kumander.iface.XCrypt;

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
