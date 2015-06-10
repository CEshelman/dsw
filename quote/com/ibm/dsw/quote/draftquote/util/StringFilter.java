package com.ibm.dsw.quote.draftquote.util;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.regex.*;

import com.ibm.ead4j.opal.log.LogContextFactory;
public class StringFilter {
	public static String filter(String src)
	{
		try
		{
			if ( src == null || "".equals(src) )
			{
				return src;
			}
			//remove script and style
			String patternStr = "(<script)|(</script(\\s)*>)|(<style)|(</style(\\s)*>)|(<img)|(</img(\\s)*)";
			
			Pattern pattern = Pattern.compile(patternStr, Pattern.CASE_INSENSITIVE);
			String temp = filter(src, pattern);
			return temp;
		}
		catch ( Throwable t )
		{
			LogContextFactory.singleton().getLogContext().error(StringFilter.class, t.getMessage());
			LogContextFactory.singleton().getLogContext().error(StringFilter.class, src);
			return src;
		}
	}
	
	public static String filter(String src, Pattern pattern)
	{
		StringBuffer temp = new StringBuffer(src);
		Matcher m = pattern.matcher(src);
		int pos = 0;
		int pos2 = 0;
		int num = 0;
		while ( m.find(pos) )
		{
			pos = m.start();
			temp.deleteCharAt(pos + num * 3);
			temp.insert(pos + num * 3, "&lt;");
			num++;
			pos2 = temp.indexOf(">", pos + num * 3);
			if ( pos2 != -1 )
			{
				temp.deleteCharAt(pos2);
				temp.insert(pos2, "&gt;");
				num++;
			}
			pos = pos + 1;
		}
		return temp.toString();
	}
	
	public static String urlEncode(String str) {
        String encodedStr = str;
        if(str == null || str.length() ==0){
        	return "";
        }
        try {
            encodedStr = URLEncoder.encode(str, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return encodedStr;
    }
}
