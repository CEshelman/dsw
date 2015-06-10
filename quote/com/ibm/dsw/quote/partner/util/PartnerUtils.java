package com.ibm.dsw.quote.partner.util;

import org.apache.commons.lang.StringUtils;

import com.ibm.dsw.quote.appcache.process.CacheProcessFactory;
import com.ibm.dsw.quote.base.config.CustomerConstants;
import com.ibm.dsw.quote.base.exception.QuoteException;

/**
 * 
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * The <code>PartnerUtils</code> class implements some common util method in
 * partner selection
 * 
 * @author cuixg@cn.ibm.com
 * 
 * Created on Mar 19, 2007
 */
public class PartnerUtils {
    private static final String COUNTRY_USA = "USA";

    private static final String COUNTRY_CAN = "CAN";

    public static boolean isValidLob(String lob) {
        //according to use case. partner selection is not available for PPSS
        // and FCT
        return (CustomerConstants.LOB_PA.equals(lob) || CustomerConstants.LOB_PAE.equals(lob)
                || CustomerConstants.LOB_FCT.equals(lob) || CustomerConstants.LOB_PAUN.equals(lob)
                || CustomerConstants.LOB_OEM.equals(lob));
    }

    public static boolean isNorthAmerica(String code3) {
        return COUNTRY_USA.equals(code3) || COUNTRY_CAN.equals(code3);
    }

    /**
     * check whether code3 rep a country
     * 
     * @param code3
     * @return
     * @throws QuoteException
     */
    public static boolean isValidCountry(String code3) throws QuoteException {
        if (StringUtils.isBlank(code3)) {
            return false;
        } else {
            return (CacheProcessFactory.singleton().create().getCountryByCode3(code3) != null);
        }
    }

}
