package org.xersys.commander.util;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class StringUtil {

    /**
     * Trims space from the start of a string.
     *
     * @param str string to trim space characters from
     *
     * @return a new string without any leading space characters
     */
    public static String trimLeft(String str) {
        char[] val = str.toCharArray();
        int st = 0;
        while ((st < val.length) && (val[st] <= ' ')) {
            st++;
        }
        return str.substring(st);
    }

    /**
     * Trims space from the end of a string.
     *
     * @param str string to trim space characters from
     *
     * @return a new string without any trailing space characters
     */
    public static String trimRight(String str) {
        char[] val = str.toCharArray();
        int    end = val.length;
        while ((end > 0) && (val[end - 1] <= ' ')) {
            end--;
        }
        return str.substring(0, end);
    }

    /**
    * Replicates a string.
    * 
    * @param str     The string to replicate.
    * @param ctr     The number of times the string will be replicated.
    * @return        The replicated string value.
    */ 
    public static String replicate(String str, int ctr){
        StringBuilder s = new StringBuilder();
        if(ctr < 1)
            return "";

        for(int ln = 1;ln<=ctr;ln++)
            s.append(str);

        return s.toString();
    }

    /**
     * Checks whether string is convertible to number.
     * 
     * @param str     The string value to check.
     * @return        true if the string value is convertible, otherwise false.
     */
    public static boolean isNumeric(String str){
        return str.matches("-?\\d+(\\.\\d+)?");  //match a number with optional '-' and decimal.
    }   
   
    //mac
    public static String NumberFormat(double fnValue, String fsPattern){
        DecimalFormat myFormatter = new DecimalFormat(fsPattern);
        return myFormatter.format(fnValue);
    }
    
    public static String NumberFormat(BigDecimal fnValue, String fsPattern){
        DecimalFormat myFormatter = new DecimalFormat(fsPattern);
        return myFormatter.format(fnValue);
    }
    
    public static String NumberFormat(Number fnValue, String fsPattern){
        DecimalFormat myFormatter = new DecimalFormat(fsPattern);
        return myFormatter.format(fnValue);
    }
    
    public static boolean isDate(String fsValue, String fsPattern){
        SimpleDateFormat dateFormat;
        
        dateFormat = new SimpleDateFormat(fsPattern);
        dateFormat.setLenient(false);
        
        try {
            dateFormat.parse(fsValue.trim());
        } catch (ParseException e) {
            return false;
        }
        
        return true;
    }
    
    public static boolean isValidMobile(String fsValue){
        Pattern p = Pattern.compile("(0|91)?[7-9][0-9]{9}");
        Matcher m = p.matcher(fsValue);
        return (m.find() && m.group().equals(fsValue));
    }
}

