package com.dyf.i18n.util;

public class BOMUtils {
    public static final String UTF8_BOM = "\uFEFF";

    public static String removeUTF8BOM(String s) {
        if (s.startsWith(UTF8_BOM)) {
            s = s.substring(1);
        } else {
//	    	System.out.printf("This String start:\t%x %x!!!!!\n",(int)s.charAt(0),(int)s.charAt(1));
//	    	System.out.printf("BOM symbol start:\t%x!!!!!\n",(int)UTF8_BOM.charAt(0));
        }
        return s;
    }
}
