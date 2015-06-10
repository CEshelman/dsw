/*
 * Created on 2007-5-17
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.ibm.dsw.quote.draftquote.util;

import java.util.HashMap;
import java.util.Map;

/**
 * @author helen
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class MimeTypeUtil {

    private static final Map map = new HashMap();
    
    static{
        map.put("lwp", "application/vnd.lotus-wordpro");
        map.put("sam", "application/vnd.lotus-wordpro");
        map.put("mwp", "application/vnd.lotus-wordpro");
        map.put("smm", "application/vnd.lotus-wordpro");
        map.put("prz", "application/vnd.lotus-freelance");
        map.put("pre", "application/vnd.lotus-freelance");
        map.put("123", "application/vnd.lotus-1-2-3");
        map.put("12m", "application/vnd.lotus-1-2-3");
        map.put("wd1", "application/vnd.lotus-1-2-3");
        map.put("wk1", "application/vnd.lotus-1-2-3");
        map.put("wk3", "application/vnd.lotus-1-2-3");
        map.put("wk4", "application/vnd.lotus-1-2-3");
        map.put("doc", "application/msword");
        map.put("dot", "application/msword");
        map.put("ppt", "application/vnd.ms-powerpoint");
        map.put("pps", "application/vnd.ms-powerpoint");
        map.put("ppa", "application/vnd.ms-powerpoint");
        map.put("pot", "application/vnd.ms-powerpoint");
        map.put("pwz", "application/vnd.ms-powerpoint");
        map.put("xls", "application/vnd.ms-excel");
        map.put("xlt", "application/vnd.ms-excel");
        map.put("xlm", "application/vnd.ms-excel");
        map.put("xld", "application/vnd.ms-excel");
        map.put("xla", "application/vnd.ms-excel");
        map.put("xlc", "application/vnd.ms-excel");
        map.put("xlw", "application/vnd.ms-excel");
        map.put("pdf", "application/pdf");
        map.put("txt", "text/plain");
        map.put("rtf", "application/rtf");
        map.put("xml", "text/xml");
    }
    
    public static String getMimeTypeBySuffix(String suffix){
        String mimeType = (String)map.get(suffix.toLowerCase());
        if(mimeType == null || "".equals(mimeType)){
            mimeType = "application/octet-stream";
        }
        return mimeType;
    }
}
