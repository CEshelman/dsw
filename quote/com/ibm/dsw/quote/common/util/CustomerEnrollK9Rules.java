package com.ibm.dsw.quote.common.util;

import java.util.HashMap;

import org.apache.regexp.RE;

import com.ibm.ead4j.opal.log.LogContextFactory;

/**
 * Insert the type's description here.
 * Creation date: (9/17/2003 9:41:25 AM)
 * @author: Administrator
 */
public class CustomerEnrollK9Rules {
	private static CustomerEnrollK9Rules singleton = null;

/**
 * CustomerEnrollK9Rules constructor comment.
 */
private CustomerEnrollK9Rules() {
	super();
}
public String checkPostalCode(String countryCode, String postCode ,HashMap errMsgs)
{
	String message = "";
    String rexp= null;
    RE re= null;
    boolean testResult= false;
    boolean testResult2= false;
    try
        {
        //perform K-9 checks

        // USA format is NNNNN-NNNN or NNNNN
        if (countryCode.equalsIgnoreCase("USA"))
            {
            if (postCode.length() != 10 && postCode.length() != 5)
                {
                testResult2= false;
            }
            else
                {
            	if(postCode.length() == 10){
                    rexp= "\\d\\d\\d\\d\\d\\-\\d\\d\\d\\d";
                    re= new RE(rexp);
                    testResult= re.match(postCode);
            	}
                if (postCode.length() == 5){
                    rexp= "\\d\\d\\d\\d\\d";
                    re= new RE(rexp);
                    testResult2= re.match(postCode);
                }
            }
            if (!testResult && !testResult2)
                {
                message = (String)errMsgs.get("USA");
            }

        }
        else if (countryCode.equalsIgnoreCase("CAN"))
            {
            rexp= "\\D\\d\\D\\s\\d\\D\\d";
            re= new RE(rexp);
            testResult= re.match(postCode);
            if (postCode.length() != 7) testResult = false; 
            if (!testResult)
                message= (String)errMsgs.get("CAN");
        }
        else if (countryCode.equalsIgnoreCase("CZE"))
            {
            rexp= "\\d\\d\\d\\s\\d\\d";
            re= new RE(rexp);
            testResult= re.match(postCode);
            if (postCode.length() != 6) testResult = false;  
            if (!testResult)
                message= (String)errMsgs.get("CZE");
        }
        else if (countryCode.equalsIgnoreCase("NLD"))
            {
            rexp= "\\d\\d\\d\\d\\s\\D\\D";
            re= new RE(rexp);
            testResult= re.match(postCode);
            if (postCode.length() != 7) testResult = false;              
            if (!testResult)
                message= (String)errMsgs.get("NLD");
        }
        else if (countryCode.equalsIgnoreCase("POL"))
            {
            rexp= "\\d\\d\\-\\d\\d\\d";
            re= new RE(rexp);
            testResult= re.match(postCode);
            if (postCode.length() != 6) testResult = false;              
            if (!testResult)
                message= (String)errMsgs.get("POL");
        }
        else if (countryCode.equalsIgnoreCase("KOR"))
            {
            rexp= "\\d\\d\\d-\\d\\d\\d";
            re= new RE(rexp);
            testResult= re.match(postCode);
            if (postCode.length() != 7) testResult = false;  
            if (!testResult)
                message= (String)errMsgs.get("KOR");
        }
        else if (countryCode.equalsIgnoreCase("SWE"))
            {
            rexp= "\\d\\d\\d\\s\\d\\d";
            re= new RE(rexp);
            testResult= re.match(postCode);
            if (postCode.length() != 6) testResult = false;              
            if (!testResult)
                message= (String)errMsgs.get("SWE");
        }
        else if (countryCode.equalsIgnoreCase("PRT"))
            {
            if (postCode.length() != 8 && postCode.length() != 4)
                {
                testResult2= false;
            }
            else
                {
                rexp= "\\d\\d\\d\\d-\\d\\d\\d";
                re= new RE(rexp);
                testResult= re.match(postCode);
                if (!testResult)
                    {
                    rexp= "\\d\\d\\d\\d";
                    re= new RE(rexp);
                    testResult2= re.match(postCode);
                }
            }
            if (!testResult && !testResult2)
                message= (String)errMsgs.get("PRT");

        }
        else if (countryCode.equalsIgnoreCase(""))
            {
                 message= "You must enter a country code to continue";

        }            
    }
    catch (org.apache.regexp.RESyntaxException res)
        {
        LogContextFactory.singleton().getLogContext().error(this, res, "RESyntaxException caught when processing regular expressions");
    }

    return message;
}
/**
 * Insert the method's description here.
 * Creation date: (9/17/2003 9:42:07 AM)
 * @return is.eorder.enrollments.xmlconfig.CustomerEnrollK9Rules
 */
public static CustomerEnrollK9Rules singleton() {

	if (singleton == null)
	{
		singleton = new CustomerEnrollK9Rules();
	}
	return singleton;
}
}
