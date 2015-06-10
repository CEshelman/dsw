package com.ibm.dsw.quote.base.util;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.StringTokenizer;

import org.apache.commons.lang.StringUtils;

import com.ibm.dsw.quote.base.config.ParamKeys;
import com.ibm.ead4j.jade.config.ActionHandlerKeys;
import com.ibm.ead4j.jade.config.ApplicationContext;
import com.ibm.ead4j.jade.config.ApplicationContextFactory;
import com.ibm.ead4j.jade.util.ParameterNotFoundException;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;

/**
 * Copyright 2006 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * The <code>ParametersImpl.java</code> class is the sqo implementation of
 * EAD4J's parameter class This implementation added the function to enable
 * image button be submitted to a specific action.
 * 
 * 
 * @author: chenzhh@cn.ibm.com
 * 
 * Creation date: Jan 26, 2007
 */
public class ParametersImpl extends com.ibm.ead4j.jade.util.ParametersImpl {
    private static final long serialVersionUID = 5416227387533607718L;
	protected static final LogContext logContext = LogContextFactory.singleton().getLogContext();

    /**
     * ParametersImpl constructor comment.
     */
    public ParametersImpl() {
        super();
    }

    /**
     * ParametersImpl constructor comment.
     * 
     * @param arg1
     *            javax.servlet.http.HttpServletRequest
     * @exception com.ibm.ead4j.jade.util.ParameterNotFoundException
     *                The exception description.
     */
    public ParametersImpl(javax.servlet.http.HttpServletRequest arg1)
            throws com.ibm.ead4j.jade.util.ParameterNotFoundException {
        super(arg1);
        
    }


    /**
     * ParametersImpl constructor comment.
     * 
     * @param arg1
     *            javax.servlet.ServletRequest
     * @exception com.ibm.ead4j.jade.util.ParameterNotFoundException
     *                The exception description.
     */
    public ParametersImpl(javax.servlet.ServletRequest arg1) throws com.ibm.ead4j.jade.util.ParameterNotFoundException {
        super(arg1);
    }

    public void initialize(javax.servlet.http.HttpServletRequest req) throws ParameterNotFoundException {
        super.initialize(req);
        processSubmitButton();
        
        //if req coms from a forward action,change jade action parameter to target action manually, avoid endless loop
        if (hasParameter(ParamKeys.PARAM_COME_FROM_FORWARD) && getParameterAsBoolean(ParamKeys.PARAM_COME_FROM_FORWARD)){
            ApplicationContext context = ApplicationContextFactory.singleton().getApplicationContext();
            String actionKey = context.getConfigParameter(ActionHandlerKeys.JADE_ACTION_KEY);
            
            String forwardAction;
            String redirectURL = req.getQueryString();
            if (redirectURL.indexOf("&") > 0){
                forwardAction = StringUtils.substringBetween(redirectURL, actionKey + "=", "&");
            }else{
                forwardAction = StringUtils.substringAfter(redirectURL, actionKey + "=");
            }
            addParameter(actionKey, forwardAction);
        }        
    }

    public static Hashtable parseQueryString(String s) {
        String[] valArray = null;

        if (s == null) {
            throw new IllegalArgumentException();
        }

        Hashtable ht = new Hashtable();
        StringBuffer sb = new StringBuffer();
        String key;

        for (StringTokenizer st = new StringTokenizer(s, ","); st.hasMoreTokens(); ht.put(key, valArray)) {
            String pair = st.nextToken();
            int pos = pair.indexOf('=');

            if (pos == -1) {
                throw new IllegalArgumentException();
            }

            key = parseName(pair.substring(0, pos), sb);

            String val = parseName(pair.substring(pos + 1, pair.length()), sb);

            if (ht.containsKey(key)) {
                String[] oldVals = (String[]) ht.get(key);
                valArray = new String[oldVals.length + 1];

                for (int i = 0; i < oldVals.length; i++) {
                    valArray[i] = oldVals[i];
                }

                valArray[oldVals.length] = val;
            } else {
                valArray = new String[1];
                valArray[0] = val;
            }
        }

        return ht;
    }
    

    /*
     *  override default implementation to support action forward
     * @see com.ibm.ead4j.common.util.Parameters#getParameterAsString(java.lang.String)
     */
	public String getParameterAsString(String name) {
        Object stringParameter = (Object) getParametersTable().get(name);
        if (stringParameter == null) {
            return null;
        }
        if (stringParameter instanceof String) {
            return (String) stringParameter;
        } else {
            String[] ps = (String[]) stringParameter;
            return ps.length == 0 ? null : ps[0];
        }
    }    

    protected void processSubmitButton() {
        try {
//            String actionCode = null;
            String actionKey = null;
            ApplicationContext context = null;

            context = ApplicationContextFactory.singleton().getApplicationContext();
            actionKey = context.getConfigParameter(ActionHandlerKeys.JADE_ACTION_KEY);

            //if there is an action code in params, do nothing
//            actionCode = this.getParameterAsString(actionKey);

            if (true) {
                Iterator paramNames = this.getParameterNames();

                //check if an image button is submitted
                while (paramNames.hasNext()) {
                    String paramName = (String) paramNames.next();

                    if (paramName.startsWith(actionKey)) {
                        int idx = 0;

                        if (paramName.endsWith(".x")) {
                            idx = paramName.indexOf(".x");
                        } else if (paramName.endsWith(".y")) {
                            idx = paramName.indexOf(".y");
                        }
                        else
                        {
                        	idx = paramName.length();
                        }

                        if (idx > (actionKey.length() + 1)) {
                            if (logContext.isDebug()) {
                                logContext.debug(this, "Parsing submit button name: " + paramName);
                            }

                            //parse submit button name into param names and
                            // values
                            String paramString = paramName.substring(0, idx);
                            Hashtable ht = ParametersImpl.parseQueryString(paramString);

                            if ((ht != null) && (ht.size() > 0)) {
                                if (logContext.isDebug()) {
                                    logContext.debug(this, "Parsed parameters table from submit button name: "
                                            + ht.toString());
                                }

                                Enumeration names = ht.keys();

                                while (names.hasMoreElements()) {
                                    String pKey = (String) names.nextElement();
                                    String[] pValues = (String[]) ht.get(pKey);

                                    if (pValues.length > 1) {
                                        this.addParameter(pKey, pValues);
                                    } else {
                                        this.addParameter(pKey, pValues[0]);
                                    }
                                }

                                break;
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            logContext.error(this, e, "Exception caught in processSubmitButtons().");
        }
    }

    private static String parseName(String s, StringBuffer sb) {
        sb.setLength(0);

        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);

            switch (c) {
            case 43: // '+'
                sb.append(' ');

                break;

            case 37: // '%'

                try {
                    sb.append((char) Integer.parseInt(s.substring(i + 1, i + 3), 16));
                    i += 2;

                    break;
                } catch (NumberFormatException numberformatexception) {
                    throw new IllegalArgumentException();
                } catch (StringIndexOutOfBoundsException stringindexoutofboundsexception) {
                    String rest = s.substring(i);
                    sb.append(rest);

                    if (rest.length() == 2) {
                        i++;
                    }
                }

                break;

            default:
                sb.append(c);

                break;
            }
        }

        return sb.toString();
    }
}
