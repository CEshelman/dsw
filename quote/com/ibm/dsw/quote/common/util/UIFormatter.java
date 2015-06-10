package com.ibm.dsw.quote.common.util;

import java.io.Serializable;

import com.ibm.dsw.quote.appcache.domain.Country;
import com.ibm.dsw.quote.appcache.process.CacheProcess;
import com.ibm.dsw.quote.appcache.process.CacheProcessFactory;
import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.base.util.DecimalUtil;
import com.ibm.dsw.quote.common.domain.Partner;
import com.ibm.dsw.quote.common.domain.Quote;
import com.ibm.ead4j.opal.log.LogContextFactory;

/**
 * Copyright 2006 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * @author <a href="liuxinlx@cn.ibm.com">Andy Liu </a> <br/>
 * 
 * Creation date: Jun 6, 2007
 */

public class UIFormatter implements Serializable {


    private Quote quote;

    private int endCustomerRoundingFactor = DecimalUtil.DEFAULT_SCALE;

    private int channelRoundingFactor = DecimalUtil.DEFAULT_SCALE;

    private static Country cntryUSA = null;

    private static Country cntryFRA = null;

    private final static String COUNTRY_USA = "USA";

    private final static String COUNTRY_FRA = "FRA";

    static {
        try {
            CacheProcess process = CacheProcessFactory.singleton().create();
            cntryUSA = process.getCountryByCode3(COUNTRY_USA);
            cntryFRA = process.getCountryByCode3(COUNTRY_FRA);

        } catch (Throwable e) {

            LogContextFactory.singleton().getLogContext().error(UIFormatter.class, "Can't get Country object for USA/FRA");
        }
    }

    public UIFormatter(Quote quote) {

        this.quote = quote;

        try {

            this.endCustomerRoundingFactor = getEndCustomerRoundingFactor();

        } catch (Throwable e) {
            LogContextFactory.singleton().getLogContext().error(this, "Get end customer rounding factor Error " + e.getMessage());
        }

        try {
            this.channelRoundingFactor = getChannelRoundingFactor();
        } catch (Throwable e) {
            LogContextFactory.singleton().getLogContext().error(this, "Get end channel rounding factor Error " + e.getMessage());
        }

    }

    /**
     * try to get the customer's country , if no customer selected, use the country in quote.
     * then get the rounding factor in the country object.  
     * if rounding factor is less than 0, use default rounding factor 2. 
     * @param quote
     * @return
     */
    private int getEndCustomerRoundingFactor() {
        Country country = quote.getQuoteHeader().getPriceCountry();

        int roundingFactor = DecimalUtil.DEFAULT_SCALE;

        /*
         * Andy : 2008-2-3: we don't need to get country from Customer object, 
         * because after the ebiz ticket "BJJK-7B4LN4  allow cross-border for FCT" 
         * the PriceCountry always reflect the end customer's country or payer's country (FCT)         * 
         * 
         * Customer customer = quote.getCustomer();

        if (customer != null) {

            String customerCountryCode = customer.getCountryCode();

            try {
                CacheProcess process = CacheProcessFactory.singleton().create();
                country = process.getCountryByCode3(customerCountryCode);
            } catch (Throwable e) {
                LogContextFactory.singleton().getLogContext().error(DecimalUtil.class, "Can't find the country for " + customerCountryCode);
            }
        }*/
        if (country != null) {
            roundingFactor = country.endCustPrckRoundingFactor();
        }

        return roundingFactor;
    }

    private int getChannelRoundingFactor() {

        Country country = quote.getQuoteHeader().getPriceCountry();

        int roundingFactor = DecimalUtil.DEFAULT_SCALE;

        Partner partner = quote.getPayer();

        if (partner != null) {

            String countryCode = partner.getCountry();

            try {
                CacheProcess process = CacheProcessFactory.singleton().create();
                country = process.getCountryByCode3(countryCode);
            } catch (Throwable e) {
                LogContextFactory.singleton().getLogContext().error(DecimalUtil.class, "Can't find the country for " + countryCode);
            }
        }
        if (country != null) {
            roundingFactor = country.tier1PrckRoundingFactor();
        }
        if (roundingFactor < 0) {
            roundingFactor = DecimalUtil.DEFAULT_SCALE;
        }
        return roundingFactor;
    }

    public String formatEndCustomerPrice(double value) {
        return DecimalUtil.format(value, endCustomerRoundingFactor);
    }
    
    public double roundEndCustomerPrice(double value){
        return DecimalUtil.roundAsDouble(value, endCustomerRoundingFactor);
    }

    public String formatPoint(double value) {
        return DecimalUtil.format(value);
    }

    public String formatChannelprice(double value) {
        return DecimalUtil.format(value, channelRoundingFactor);
    }

    public static String formatUSDPrice(double value) {
        int roundingFactor = DecimalUtil.DEFAULT_SCALE;
        if (cntryUSA != null) {
            roundingFactor = cntryUSA.endCustPrckRoundingFactor();
        }
        return DecimalUtil.format(value, roundingFactor);

    }

    public static String formatEURPrice(double value) {
        int roundingFactor = DecimalUtil.DEFAULT_SCALE;
        if (cntryFRA != null) {
            roundingFactor = cntryFRA.endCustPrckRoundingFactor();
        }
        return DecimalUtil.format(value, roundingFactor);
    }

    public static String formatUSDChannelPrice(double value) {
        int roundingFactor = DecimalUtil.DEFAULT_SCALE;
        if (cntryUSA != null) {
            roundingFactor = cntryUSA.tier1PrckRoundingFactor();
        }
        return DecimalUtil.format(value, roundingFactor);

    }

    public static String formatEURChannelPrice(double value) {
        int roundingFactor = DecimalUtil.DEFAULT_SCALE;
        if (cntryFRA != null) {
            roundingFactor = cntryFRA.tier1PrckRoundingFactor();
        }
        return DecimalUtil.format(value, roundingFactor);
    }

    public static String formatPrice(double value, String countryCode) {

        int roundingFactor = DecimalUtil.DEFAULT_SCALE;

        try {

            CacheProcess process = CacheProcessFactory.singleton().create();

            Country country = process.getCountryByCode3(countryCode);
            if (null != country) {
                roundingFactor = country.endCustPrckRoundingFactor();
            }

        } catch (QuoteException e) {

            LogContextFactory.singleton().getLogContext().error(UIFormatter.class, "Get country error :" + e.getMessage());

        }

        return DecimalUtil.format(value, roundingFactor);
    }
}
