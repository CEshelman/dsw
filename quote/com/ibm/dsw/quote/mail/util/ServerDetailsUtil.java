/*
 * Created on May 30, 2007
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.ibm.dsw.quote.mail.util;

import java.net.InetAddress;

import com.ibm.dsw.quote.mail.config.MailConstants;
import com.ibm.ead4j.jade.config.ApplicationContext;
import com.ibm.ead4j.jade.config.ApplicationContextFactory;
import com.ibm.websphere.management.AdminServiceFactory;

/**
 * @author dhintze
 * 
 * To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class ServerDetailsUtil {

    /**
     *  
     */
	private static ApplicationContext appCtx = ApplicationContextFactory.singleton().getApplicationContext();
	
    public ServerDetailsUtil() {
        super();
        // Auto-generated constructor stub
    }

    public static String getServerDetailAsString() {

    	String mailAppName = appCtx.getConfigParameter(MailConstants.MAIL_APP);
    	String mailEnvName = appCtx.getConfigParameter(MailConstants.MAIL_ENV);
        String serverHostName = "";
        try {
            serverHostName = InetAddress.getLocalHost().getCanonicalHostName();
        } catch (Exception e) {
            serverHostName = "UNKNOWN";
        }

        String nodeName = "";
        try {
            nodeName = AdminServiceFactory.getAdminService().getProcessName() + "/" + AdminServiceFactory.getAdminService().getNodeName();
        } catch (Exception e) {
            nodeName = "UNKNOWN";
        }

        return "Svr:[" + serverHostName + "]-Node:[" + nodeName + "]" + "-App:[" + mailAppName + "]-Env:[" + mailEnvName + "]";

    }

}
