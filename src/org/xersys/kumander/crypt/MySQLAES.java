/**
 * @author Michael Cuison 2020.12.23
 */
package org.xersys.kumander.crypt;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.xersys.kumander.iface.XCrypt;

public class MySQLAES implements XCrypt{
    @Override
    public String Encrypt(String fsValue, String fsSalt) {
        try {
            Cipher encryptCipher = Cipher.getInstance("AES");
            encryptCipher.init(Cipher.ENCRYPT_MODE, generateMySQLAESKey(fsSalt, "UTF-8"));
            String ret = new String(Hex.encodeHex(encryptCipher.doFinal(fsValue.getBytes("UTF-8"))));
            return ret.toUpperCase();
        } catch (NoSuchAlgorithmException ex) {
            ex.printStackTrace();
        } catch (NoSuchPaddingException ex) {
            ex.printStackTrace();
        } catch (InvalidKeyException ex) {
            ex.printStackTrace();
        } catch (UnsupportedEncodingException ex) {
            ex.printStackTrace();
        } catch (IllegalBlockSizeException ex) {
            ex.printStackTrace();
        } catch (BadPaddingException ex) {
            ex.printStackTrace();
        }
        
        return null;
    }

    @Override
    public String Decrypt(String fsValue, String fsSalt) {
        try {
            Cipher decryptCipher = Cipher.getInstance("AES");
            decryptCipher.init(Cipher.DECRYPT_MODE, generateMySQLAESKey(fsSalt, "UTF-8"));
            return new String(decryptCipher.doFinal(Hex.decodeHex(fsValue.toCharArray())));
        } catch (NoSuchAlgorithmException ex) {
            ex.printStackTrace();
        } catch (NoSuchPaddingException ex) {
            ex.printStackTrace();
        } catch (InvalidKeyException ex) {
            ex.printStackTrace();
        } catch (IllegalBlockSizeException ex) {
            ex.printStackTrace();
        } catch (BadPaddingException ex) {
            ex.printStackTrace();
        } catch (DecoderException ex) {
            ex.printStackTrace();
        }
        
        return null;
    }
    
    //added methods
    private SecretKeySpec generateMySQLAESKey(final String key, final String encoding) {
        try {
            final byte[] finalKey = new byte[16];
            int i = 0;
            for(byte b : key.getBytes(encoding))
                finalKey[i++%16] ^= b;			
            return new SecretKeySpec(finalKey, "AES");
        } catch(UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void setHexCrypt(int fnValue) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
