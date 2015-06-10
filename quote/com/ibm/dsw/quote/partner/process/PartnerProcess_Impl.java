package com.ibm.dsw.quote.partner.process;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.ibm.dsw.quote.appcache.domain.CodeDescObj;
import com.ibm.dsw.quote.appcache.domain.Country;
import com.ibm.dsw.quote.appcache.process.CacheProcess;
import com.ibm.dsw.quote.appcache.process.CacheProcessFactory;
import com.ibm.dsw.quote.base.config.CustomerConstants;
import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.base.util.LogThrowableUtil;
import com.ibm.dsw.quote.common.domain.PartnerFactory;
import com.ibm.dsw.quote.common.domain.SearchResultList;
import com.ibm.dsw.quote.common.exception.NoDataException;
import com.ibm.dsw.quote.partner.exception.PartnerNotFoundException;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;
import com.ibm.ead4j.topaz.exception.TopazException;
import com.ibm.ead4j.topaz.process.TopazTransactionalProcess;

/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * The <code>PartnerProcess_Impl</code> class is the abstract
 * implementation of Partner process.
 * 
 * @author cuixg@cn.ibm.com
 * 
 * Created on Mar 5, 2007
 */
public abstract class PartnerProcess_Impl extends TopazTransactionalProcess implements PartnerProcess {
    protected static final LogContext logContext = LogContextFactory.singleton().getLogContext();
    
    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.dsw.quote.draftquote.process.PartnerProcess#findResellers(java.lang.String,
     *      java.lang.String, java.lang.String, java.lang.String,
     *      java.lang.String, int, int)
     */
    public SearchResultList findResellers(String lobCode, String custCnt, String custName, String county, String state,
            int tierType, int pageIndex, String webQuoteNum, int FCT2PAMigrtnFlag) throws QuoteException {
        SearchResultList resultList = null;
        try {
            this.beginTransaction();
            resultList = PartnerFactory.singleton().findResellersByAttr(lobCode, custCnt, custName, county, state,
                    tierType, pageIndex, webQuoteNum, FCT2PAMigrtnFlag);
            this.commitTransaction();
        } catch (NoDataException noe) {
            throw new PartnerNotFoundException("Partner is not found", noe);
        } catch (TopazException tce) {
            logContext.error(this, LogThrowableUtil.getStackTraceContent(tce));
            throw new QuoteException(tce);
        } finally {
            rollbackTransaction();
        }
        return resultList;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.dsw.quote.draftquote.process.PartnerSelectionProcess#findResellers(java.lang.String,
     *      java.lang.String, java.lang.String, int, int)
     */
    public SearchResultList findResellers(String lobCode, String custCnt, String custNum, int tierType,
            int pageIndex, String webQuoteNum, String audCode, int FCT2PAMigrtnFlag) throws QuoteException {
        SearchResultList resultList = null;
        try {
            this.beginTransaction();
            resultList = PartnerFactory.singleton().findResellersByNum(lobCode, custCnt, custNum, tierType, pageIndex,
                    webQuoteNum, audCode, FCT2PAMigrtnFlag);
            this.commitTransaction();
        } catch (NoDataException noe) {
            throw new PartnerNotFoundException("Partner is not found", noe);
        } catch (TopazException tce) {
            logContext.error(this, LogThrowableUtil.getStackTraceContent(tce));
            throw new QuoteException(tce);
        } finally {
            rollbackTransaction();
        }
        return resultList;

    }
    
    public SearchResultList findResellersByPortfolio(String portfolios, String lob, String custCntry, int pageIndex, int multipleProdFlag)
            throws QuoteException {
        
        SearchResultList resultList = null;
        try {
            this.beginTransaction();
            resultList = PartnerFactory.singleton().findResellersByPortfolio(portfolios, lob, custCntry, pageIndex, multipleProdFlag);
            this.commitTransaction();
            
        } catch (NoDataException noe) {
            throw new PartnerNotFoundException("Partner is not found", noe);
        } catch (TopazException tce) {
            logContext.error(this, LogThrowableUtil.getStackTraceContent(tce));
            throw new QuoteException(tce);
        } finally {
            rollbackTransaction();
        }
        return resultList;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.dsw.quote.draftquote.process.PartnerSelectionProcess#findDistributors(java.lang.String,
     *      java.lang.String, java.lang.String, int)
     */
    public SearchResultList findDistributors(String lobCode, String custCnt, String custNum, int pageIndex,
            String webQuoteNum, String audCode, int FCT2PAMigrtnFlag) throws QuoteException {
        SearchResultList resultList = null;
        try {
            this.beginTransaction();
            resultList = PartnerFactory.singleton().findDistributorsByNum(lobCode, custCnt, custNum, pageIndex,
                    webQuoteNum, audCode, FCT2PAMigrtnFlag);
            this.commitTransaction();
        } catch (NoDataException noe) {
            throw new PartnerNotFoundException("Partner is not found", noe);
        } catch (TopazException tce) {
            logContext.error(this, LogThrowableUtil.getStackTraceContent(tce));
            throw new QuoteException(tce);
        } finally {
            rollbackTransaction();
        }
        return resultList;

    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.dsw.quote.draftquote.process.PartnerSelectionProcess#findDistributors(java.lang.String,
     *      java.lang.String, java.lang.String, java.lang.String,
     *      java.lang.String, int)
     */
    public SearchResultList findDistributors(String lobCode, String custCnt, String custName, String county,
            String state, int pageIndex, String webQuoteNum, int FCT2PAMigrtnFlag) throws QuoteException {
        SearchResultList resultList = null;
        try {
            this.beginTransaction();
            resultList = PartnerFactory.singleton().findDistributorsByAttr(lobCode, custCnt, custName, county, state,
                    pageIndex, webQuoteNum, FCT2PAMigrtnFlag);
            this.commitTransaction();
        } catch (NoDataException noe) {
            throw new PartnerNotFoundException("Partner is not found", noe);
        } catch (TopazException tce) {
            logContext.error(this, LogThrowableUtil.getStackTraceContent(tce));
            throw new QuoteException(tce);
        } finally {
            rollbackTransaction();
        }
        return resultList;

    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.dsw.quote.draftquote.process.PartnerSelectionProcess#findValidCountryList(java.lang.String,
     *      java.lang.String)
     */
    public List findValidCountryList(String countryCode, String lobCode) throws QuoteException {
        final String WW_AMERICAS = "AMERICAS";
        final String WW_EMEA = "EMEA";
        final String WW_APAC = "APAC";

        CacheProcess cp = CacheProcessFactory.singleton().create();
        Country custCountry = cp.getCountryByCode3(countryCode);
        if (WW_AMERICAS.equals(custCountry.getWWRegion()) || WW_EMEA.equals(custCountry.getWWRegion())
        		||WW_APAC.equals(custCountry.getWWRegion())) {
            String salesOrgCode = findSaleOrgCode(lobCode, custCountry);
            if (salesOrgCode == null) {
                logContext.warning(this, "Can't find valid country list, return all");
                return cp.getCountryList();
            } else {
                List result = new ArrayList();
                // for every country, if this country's salse org list contains
                // above sales org , then the country should be return
                for (Iterator iter = cp.getCountryList().iterator(); iter.hasNext();) {
                    Country c = (Country) iter.next();
                    for (Iterator iter2 = c.getSalesOrgList().iterator(); iter2.hasNext();) {
                        CodeDescObj cdo = (CodeDescObj) iter2.next();
                        if (salesOrgCode.equalsIgnoreCase(cdo.getCodeDesc())) {
                            result.add(c);
                            break;
                        }
                    }

                }
                return result;
            }
        } else {
            // for JP
            List result = new ArrayList();
            result.add(custCountry);
            return result;
        }
    }

    protected String findSaleOrgCode(String lobCode, Country custCountry) {
        final String SALES_MODEL_LANDED = "L";
        List salesOrgList = custCountry.getSalesOrgList();
        String salesOrgCode = null;

        //Only PPSS uses landed model.
        for (Iterator iter = salesOrgList.iterator(); iter.hasNext();) {
            CodeDescObj cdo = (CodeDescObj) iter.next();
            if (lobCode.equals(CustomerConstants.LOB_PPSS)) {
                if (SALES_MODEL_LANDED.equalsIgnoreCase(cdo.getCode())) {
                    salesOrgCode = cdo.getCodeDesc();
                    break;
                }
            }
            else {
                
                if (StringUtils.isBlank(cdo.getCode())) {
                    salesOrgCode = cdo.getCodeDesc();
                    break;
                }
            }
        }
        
        // If no sales org fetched for PPSS, return any sales org
        if (StringUtils.isBlank(salesOrgCode)
                && (lobCode.equals(CustomerConstants.LOB_PPSS))) {
            for (Iterator iter = salesOrgList.iterator(); iter.hasNext();) {
                CodeDescObj cdo = (CodeDescObj) iter.next();
                salesOrgCode = cdo.getCodeDesc();
                break;
            }
        }
        
        return salesOrgCode;
    }

}
