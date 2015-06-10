package com.ibm.dsw.quote.pvu.process.jdbc;

import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.common.config.PartPriceConstants;
import com.ibm.dsw.quote.common.domain.QuoteHeader;
import com.ibm.dsw.quote.common.domain.QuoteLineItem;
import com.ibm.dsw.quote.common.domain.QuoteLineItemConfig;
import com.ibm.dsw.quote.common.domain.QuoteLineItemConfigFactory;
import com.ibm.dsw.quote.common.domain.QuoteLineItemFactory;
import com.ibm.dsw.quote.common.domain.SearchResultList;
import com.ibm.dsw.quote.common.process.QuoteProcess;
import com.ibm.dsw.quote.common.process.QuoteProcessFactory;
import com.ibm.dsw.quote.pvu.config.VUDBConstants;
import com.ibm.dsw.quote.pvu.domain.VUConfig;
import com.ibm.dsw.quote.pvu.domain.VUConfigFactory;
import com.ibm.dsw.quote.pvu.process.VUConfigProcess_Impl;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;
import com.ibm.ead4j.topaz.exception.TopazException;

/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * This <code>VUConfigProcess_jdbc</code> class.
 * 
 * @author: cgang@cn.ibm.com
 * 
 * Creation date: 2007-3-20
 */
public class VUConfigProcess_jdbc extends VUConfigProcess_Impl {
    private static final String OVERRIDE_FLAG ="0";

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.dsw.quote.pvu.process.VUConfigProcess#findByConfigNum(java.lang.String,
     *      java.lang.Integer)
     */
    public SearchResultList findVUConfigByConfigNum(String configNum, String noDescFlag) throws QuoteException {
        SearchResultList resultList = null;
        try {
            this.beginTransaction();
            resultList = VUConfigFactory.singleton().findByConfigNum(configNum, noDescFlag);
            this.commitTransaction();
        } catch (TopazException tce) {
            throw new QuoteException(tce);
        } finally {
            this.rollbackTransaction();
        }
        return resultList;

    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.dsw.quote.pvu.process.VUConfigProcess#updateQuoteLineItemConfig(java.util.List,
     *      java.lang.String, java.lang.String)
     */
    public void updateQuoteLineItemConfig(List vuConfigList, String lineItemSeqNum, String creatorId, String configNum)
            throws QuoteException {
        LogContext logger = LogContextFactory.singleton().getLogContext();

        try {
        	this.beginTransaction();
            QuoteProcess qProcess = QuoteProcessFactory.singleton().create();
            QuoteHeader qHeader = qProcess.getQuoteHdrInfo(creatorId);
            if (qHeader == null)
                throw new QuoteException("Can not return QuoteHeader with creatorId: " + creatorId);
            
            boolean isRenewal = VUDBConstants.RENEWAL_QUOTE.equals(qHeader.getQuoteTypeCode().trim()); 
            		
            String webQuoteNum = qHeader.getWebQuoteNum();
            logger.debug(this, "updating quote line item config.... webQuoteNum: " + webQuoteNum);
            float qty = 0;
            for (Iterator vuConfigIt = vuConfigList.iterator(); vuConfigIt.hasNext();) {
                VUConfig vuc = (VUConfig) vuConfigIt.next();
                float coreValUnit = vuc.getCoreValUnit() == null ? 0 : Float.parseFloat(vuc.getCoreValUnit());
                qty += coreValUnit * vuc.getProcrTypeQTY();

            }
            //if LINE_ITEM is not null then for each processor core in the pvu
            // config
            if (!StringUtils.isBlank(lineItemSeqNum)) {
                logger.debug(this, "updating quote line item config.... lineItem seqNum: " + lineItemSeqNum);
                
                for (Iterator vuConfigIt = vuConfigList.iterator(); vuConfigIt.hasNext();) {
                    VUConfig vuc = (VUConfig) vuConfigIt.next();
                    insertOrUpdate(vuc, webQuoteNum, lineItemSeqNum, creatorId);
                }
                QuoteLineItemFactory.singleton().updateLineItemQty(webQuoteNum, Integer.parseInt(lineItemSeqNum), qty, OVERRIDE_FLAG,
                        Integer.parseInt(configNum), creatorId);                
                logger.debug(this, "quote line item config has been updated, lineItem seqNum: " + lineItemSeqNum + " qty: "+qty + " configNum: " + configNum);

                // for each VU lineItem in the SESSION quote. for each processor core
            } else {
                List lineItemList = QuoteLineItemFactory.singleton().findLineItemsByWebQuoteNum(webQuoteNum);
                logger.debug(this, "updating quote line item config.... lineItemList size: " + lineItemList.size());

                for (Iterator lineItemIt = lineItemList.iterator(); lineItemIt.hasNext();) {
                    QuoteLineItem lineItem = (QuoteLineItem) lineItemIt.next();
                    int seqNum = lineItem.getSeqNum();
                    logger.debug(this, "updating quote line item config.... lineItem seqNum: " + seqNum);

                    for (Iterator vuConfigIt = vuConfigList.iterator(); vuConfigIt.hasNext();) {
                        VUConfig vuc = (VUConfig) vuConfigIt.next();
                        insertOrUpdate(vuc, webQuoteNum, String.valueOf(seqNum),creatorId);
                    }
                    QuoteLineItemFactory.singleton().updateLineItemQty(webQuoteNum, seqNum, qty, OVERRIDE_FLAG,
                            Integer.parseInt(configNum), creatorId);                    

                    logger.debug(this, "quote line item config has been updated, lineItem seqNum: " + seqNum);
                }
            }
            
            logger.debug(this, "Set CHG_TYPE for PVU RQ Line item " + lineItemSeqNum);
            List lineItemList = QuoteLineItemFactory.singleton().findLineItemsByWebQuoteNum(webQuoteNum);

            for (Iterator lineItemIt = lineItemList.iterator(); lineItemIt.hasNext();) {
                QuoteLineItem lineItem = (QuoteLineItem) lineItemIt.next();
                if(StringUtils.isBlank(lineItemSeqNum) 
                		|| lineItemSeqNum.equals(String.valueOf(lineItem.getSeqNum()))) {
                	
                	String chgType = lineItem.getChgType();
                	
                	 /* Ignore chg_type setting when
                	  * 1. Part is not PVU part OR
                	  * 2. Part is added via part search func OR
                	  * 3. Part is regarding as deleted one
                	  **/
                	if (isRenewal 
                			&& lineItem.getSeqNum() < PartPriceConstants.RQ_MANUALLY_ADDED_PART_SEQ 
							&& lineItem.isPvuPart()
							&& !PartPriceConstants.PartChangeType.PART_DELETED.equals(chgType)) {
                	    //if change type has contained CU. don't append more CU to it
                	    if (!StringUtils.contains(chgType, PartPriceConstants.PartChangeType.PART_PVU_CHANGED)){
	                		if(StringUtils.isBlank(chgType)) {
	                			chgType = PartPriceConstants.PartChangeType.PART_NO_CHANGES;
	                		}
	                		
	                		final String PU = "_" + PartPriceConstants.PartChangeType.PART_PRICE_UPDATED;
	                		if (StringUtils.contains(chgType, PU)){
	                		    lineItem.setChgType(chgType.replaceAll(PU, PartPriceConstants.PartChangeType.PART_PVU_CHANGED + PU));
	                		}else{
	                		    lineItem.setChgType(chgType + PartPriceConstants.PartChangeType.PART_PVU_CHANGED);
	                		}
	                		logger.debug(this, " manually added RQ line item : " + 
	                				" part # " + lineItem.getPartNum()
									+ " seq num " + lineItem.getSeqNum());
                	    }
                	}
                }
            }
            this.commitTransaction();
        } catch (TopazException e) {
            logger.error(logger, e.getMessage());
            throw new QuoteException(e);
        } finally {
        	this.rollbackTransaction();
        }
    }

    /**
     * @param vuc
     * @param webQuoteNum
     * @param lineItemSeqNum
     * @throws QuoteException
     */
    protected void insertOrUpdate(VUConfig vuc, String webQuoteNum, String lineItemSeqNum, String userID) throws QuoteException {
        LogContext logger = LogContextFactory.singleton().getLogContext();
        QuoteLineItemConfig lic = null;
        try {
            this.beginTransaction();
            lic = QuoteLineItemConfigFactory.singleton().create(webQuoteNum, Integer.parseInt(lineItemSeqNum), userID);
            lic.setExtndDVU(vuc.getExtndDVU());
            lic.setProcrCode(vuc.getProcrCode());
            lic.setProcrTypeQty(vuc.getProcrTypeQTY());
            lic.setUnitDVU(Float.valueOf(vuc.getCoreValUnit()).intValue());
            this.commitTransaction();
        } catch (TopazException e) {
            String message = "Failed in saving QuoteLineItem  " + "webQuoteNum: " + webQuoteNum + " lineItemSeqNum: "
                    + lineItemSeqNum + "\n" + lic;
            logger.error(logger, message);
            throw new QuoteException(message, e);
        } finally {
            this.rollbackTransaction();
        }
    }

}
