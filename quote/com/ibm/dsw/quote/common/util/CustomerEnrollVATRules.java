package com.ibm.dsw.quote.common.util;
/**
 * @author achowdha
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class CustomerEnrollVATRules
{
	private static CustomerEnrollVATRules singleton = null;
	
	public static CustomerEnrollVATRules singleton()
	{
		if (singleton == null)
		{
			singleton = new CustomerEnrollVATRules();
		}
		return singleton;
	}
	
	
	private CustomerEnrollVATRules()
	{
		super();
	}
	
	public int checkVatNumber(String vatNumber, String twoLetterCntryCode, int minLen, int maxLen, boolean cntryCodePrefixFlag)
	{
		int retval = 0;
		if (minLen == maxLen && vatNumber.length() != maxLen)
		{
			retval = 5; // fixed length not met	
		}
		else if (vatNumber.length() < minLen)
		{
			retval = 1;		
		}
		else if (vatNumber.length() > maxLen)
		{
			retval = 2;
		}
		else // check the number
		{   		
			if (vatNumber.indexOf(" ") != -1)
			{
				retval = 3;	
			}
   		    else if (cntryCodePrefixFlag)
			{
   				String firstTwo = vatNumber.substring(0,2);
   				// SAP does not use the ISO two letter country code for Greece - need to change this to EL
   				if (twoLetterCntryCode.equalsIgnoreCase("GR"))
   				{
   					twoLetterCntryCode = "EL";
   				}	
   		    	if(!firstTwo.equalsIgnoreCase(twoLetterCntryCode))
   		    	{				
				      retval = 4;	
   		    	}
			}			
		}				
		return retval;	
	}
	
}
