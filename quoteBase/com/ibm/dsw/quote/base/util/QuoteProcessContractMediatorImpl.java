/*
 * Created on Feb 15, 2007
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.ibm.dsw.quote.base.util;

import java.util.Map;

import com.ibm.ead4j.jade.contract.ProcessContract;
import com.ibm.ead4j.jade.contract.ProcessContractMediatorDefaultImpl;
import com.ibm.ead4j.jade.util.Parameters;

/**
 * @author minhuiy
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class QuoteProcessContractMediatorImpl extends ProcessContractMediatorDefaultImpl {
    public void load(ProcessContract contract, Parameters parms, Map hashMap) {
        Class hClass = contract.getClass();
        String name = null;

        // note: has not been implemented/validated for radio/check buttons or
        // lists (especially
        //	multi-select lists.
        // find the methods (only need the setters)
        java.lang.reflect.Method[] hMethods = hClass.getMethods();
        int count = 0;
        for (int i = 0; i < hMethods.length; i++) {
            java.lang.reflect.Method hMethod = hMethods[i];

            // is this a set method?
            name = hMethod.getName();
            if (name.startsWith("set")) {
                // Here is the difference from the default implementation: we
                // don't change the name
                //				String attributeName = AttributeNameConverter
                //						.convertJavaToHtml(name);
                String attributeName = getAttributeName(name);
                Object value = parms.getParameter(attributeName);
                hashMap.put(attributeName, value);
                // keep track if validation is used
                if (value != null) {
                    // we have a value for the attribute
                    Class[] parmTypes = hMethod.getParameterTypes();
                    String parm = parmTypes[0].getName();
                    parm = parm.substring(parm.lastIndexOf(".") + 1);
                    // get rid of the java.lang stuff

                    // the set method, for auto-loading, must accept a
                    // compatible parameter and
                    //	is responsible for performing the conversion to the
                    // appropriate type.
                    invokeMethod(contract, hMethod, parm, value);
                }
            }
        }
    }

    protected String getAttributeName(String methodName) {
        // This method should be called only when methodName starts with set
        String attributeName = methodName.substring(3);

        if (attributeName.length() > 0) {
            char firstChar = attributeName.charAt(0);
            if (Character.isUpperCase(firstChar)) {
                firstChar = Character.toLowerCase(firstChar);
                attributeName = attributeName.substring(1);
                char[] data = { firstChar };
                attributeName = new String(data) + attributeName;
            }
        }
        return attributeName;
    }

}
