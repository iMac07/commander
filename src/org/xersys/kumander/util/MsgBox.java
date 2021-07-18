package org.xersys.kumander.util;

import java.awt.Component;
import javax.swing.JOptionPane;

public class MsgBox {
   public static int showYesNo(String theMessage, String theTitle){
      int result = JOptionPane.showConfirmDialog((Component)
              null, theMessage, theTitle, JOptionPane.YES_NO_OPTION);
      return result;
   }

   public static int showYesNoCancel(String theMessage, String theTitle){
      int result = JOptionPane.showConfirmDialog((Component)
              null, theMessage, theTitle, JOptionPane.YES_NO_CANCEL_OPTION);
      return result;
   }

   public static int showOkCancel(String theMessage, String theTitle){
      int result = JOptionPane.showConfirmDialog((Component)
              null, theMessage, theTitle, JOptionPane.OK_CANCEL_OPTION);
      return result;
   }

   public static int showOk(String theMessage, String theTitle){
      int result = JOptionPane.showConfirmDialog((Component)
              null, theMessage, theTitle, JOptionPane.DEFAULT_OPTION);
      return result;
   }

   public static final int RESP_WINDOW_CLOSED = -1;
   public static final int RESP_YES_OK = 0;
   public static final int RESP_NO = 1;
   public static final int RESP_CANCEL = 2;
}
