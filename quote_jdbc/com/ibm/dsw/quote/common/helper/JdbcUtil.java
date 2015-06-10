/*
 * Created on Apr 13, 2007
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.ibm.dsw.quote.common.helper;

/**
 * @author tboulet
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class JdbcUtil {

    public static String getTrimString(String sValue) {
        if(sValue==null){
            sValue = "";
        } else {
            sValue = sValue.trim();
        }
        return sValue;
    }
    
}
