/**
 * @author Michael Cuison 2020.12.23
 */
package org.xersys.kumander.crypt;

import java.io.UnsupportedEncodingException;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.xersys.kumander.iface.XCrypt;

public class GCrypt implements XCrypt{
    int pnHexCrypt = 0;
    
    @Override
    public String Encrypt(String fsValue, String fsSalt) {
        if(fsValue == null || fsValue.trim().length() == 0 || fsSalt == null || fsSalt.trim().length() == 0) return null;
    
        try {
            Crypt loCrypt = new Crypt(fsSalt.getBytes("ISO-8859-1"));
            byte[] ret = loCrypt.encrypt(fsValue.getBytes("ISO-8859-1"));
         
            if(pnHexCrypt == 1){
                return Hex.encodeHexString(ret);
            } else{
                return new String(ret, "ISO-8859-1");
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public String Decrypt(String fsValue, String fsSalt) {
        if(fsValue == null || fsValue.trim().length() == 0 || fsSalt == null || fsSalt.trim().length() == 0) return null;

        byte[] hex;
        try {
            if(pnHexCrypt == 1){
                try {
                   hex = Hex.decodeHex(fsValue);
                } catch (DecoderException e1) {
                   return null;
                }
            } else{
                hex = fsValue.getBytes("ISO-8859-1");
            }
            
            Crypt loCrypt = new Crypt(fsSalt.getBytes("ISO-8859-1"));
            byte ret[] = loCrypt.decrypt(hex);

            return new String(ret, "ISO-8859-1");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    @Override
    public void setHexCrypt(int fnValue){
        pnHexCrypt = fnValue;
    }
}
