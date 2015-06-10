package com.ibm.dsw.quote.draftquote.viewbean.helper;

import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.apache.commons.lang.StringUtils;

import com.ibm.dsw.quote.base.config.ApplicationProperties;
import com.ibm.dsw.quote.common.config.PartPriceConstants;
import com.ibm.dsw.quote.common.domain.Quote;
import com.ibm.dsw.quote.common.domain.QuoteLineItem;
import com.ibm.dsw.quote.common.domain.QuoteLineItem.PartGroup;
import com.ibm.dsw.quote.common.util.QuoteCommonUtil;
import com.ibm.dsw.quote.draftquote.config.PartPriceViewKeys;
import com.ibm.dsw.quote.draftquote.util.date.DateUtil;

/**
 * Copyright 2006 by IBM Corporation All rights reserved.
 *
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 *
 * The <code>NoteRowViewBean</code>
 *
 *
 * @author: xiuliw@cn.ibm.com
 *
 * Creation date: 2007-4-3
 *
 * $Log: NoteRow.java,v $
 * Revision 1.35  2010/07/28 16:17:22  pbaskara
 * CLP part notes should be displayed for all LOBs - if SAP PWS returns true.  RTC work item : 3214,3212
 *
 * Revision 1.34  2010/07/28 02:43:30  wxiaoli
 * CLP : DSQ02:  Display current draft sales quote parts & pricing tab, RTC 3212, reviewed by Will
 * fix the PL of TAHN-87RQ5D : No CLP text for PA quote
 *
 * Revision 1.33  2010/06/25 16:56:30  dsmith
 * Refactor getGoupName() to getGroupName()
 *
 * Revision 1.32  2010/06/10 04:11:45  pbaskara
 * CLP part notes should be displayed for all LOBs - if SAP PWS returns true.  RTC work item : 3214
 *
 * Revision 1.31  2010/05/27 07:45:52  wxiaoli
 * contract level prcing flag for Display parts & pricing tab, RTC 3211,3212,3213,3214,3215,3216,3217, reviewed by Will
 *
 */
public class NoteRow extends PartPriceCommon {
    private Locale locale;

    private String eolPartEntitledPriceNote = "";

    public NoteRow(Quote quote)
    {
        super(quote);
    }

    public NoteRow(Quote quote,Locale locale)
    {
        super(quote);
        this.locale = locale;
    }

    /**
     *
     * @param lineItem
     * @return
     */
    public boolean showNoteRow(QuoteLineItem lineItem) {
    	
    	if (isPA() || isPAE() || isOEM() || isSSP()) {
            if (showPriceEndSoonNote(lineItem)
                  || showEndOfLifeDateNote(lineItem)
                  || showPartObsoleteNote(lineItem)
                  || showPartRestrictedNote(lineItem)
                  || showPartGroupNote(lineItem)
                  || showRenewalQuoteLineItemNotes(lineItem)
                  || showCtLvPrcAppPartNote(lineItem)
                  || showExportRestrictedNote(lineItem)
                  || showEndDateAnniversaryNote(lineItem)
                  ||lineItem.isDivestedPart()) {
            	return true;
            }
        }
        else if (isPPSS()) {
            if (showPriceEndSoonNote(lineItem)
                  || showEndOfLifeDateNote(lineItem)
                  || showPartObsoleteNote(lineItem)
                  || showCtLvPrcAppPartNote(lineItem)
                  || showExportRestrictedNote(lineItem)
                  ||lineItem.isDivestedPart()) {
               return true;
            }
        }
        /*
         * don't show part restricted/unpublished note for FCT
         */
        else if (isFCT()) {
            if (showPriceEndSoonNote(lineItem)
                  || showEndOfLifeDateNote(lineItem)
                  || showPartObsoleteNote(lineItem)
                  || showPartGroupNote(lineItem)
                  || showRenewalQuoteLineItemNotes(lineItem)
                  || showCtLvPrcAppPartNote(lineItem)
                  || showExportRestrictedNote(lineItem)
                  ||lineItem.isDivestedPart()) {
            	return true;
            }
        }

        return false;
    }

    /**
     * @param lineItem
     * @return
     */
    public boolean showCtLvPrcAppPartNote(QuoteLineItem lineItem) {
        if(lineItem.isLegacyBasePriceUsedFlag()){
            return true;
        }
        return false;
    }

    public boolean showEndOfLifeDateNote(QuoteLineItem lineItem) {
        Date expDate = lineItem.getEOLDate();
        Date currDate = DateUtil.getCurrentDate();
        if(expDate == null)
        {
            return false;
        }
        if (currDate.before(expDate)) {
            return true;
        }
        return false;
    }

    public boolean showFindReplaceMentPartLink(QuoteLineItem lineItem) {
            String cutOffDays = ApplicationProperties.getInstance().getCutOffDays();
            java.sql.Date expDate = lineItem.getEOLDate();
	        Date currDate = DateUtil.getCurrentDate();
	        Date cutOffDate = DateUtil.minusDays(expDate, Integer.parseInt(cutOffDays));
	        if (currDate.after(cutOffDate) && currDate.before(expDate)) {
	            return true;
	        }
        return false;
    }

    public String getEOLDate(QuoteLineItem qli) {
        return DateUtil.formatDate(qli.getEOLDate(),DateUtil.PATTERN1,locale);
    }

    public boolean showPartRestrictedNote(QuoteLineItem lineItem) {
        return ((isPA() || isPAE() || isOEM() || isSSP()) && lineItem.isPartRestrct());
    }

    public boolean showPartObsoleteNote(QuoteLineItem lineItem) {
        return lineItem.isObsoletePart();
    }

    public boolean showEOLPartHasEntitledPriceNote(QuoteLineItem lineItem){
    	return (lineItem.isObsoletePart() && lineItem.isHasEolPrice());
    }

    public boolean showEOLPartRequireEntitledPriceNote(QuoteLineItem lineItem){
    	return (lineItem.isObsoletePart()
    			&& !lineItem.isHasEolPrice()
				&& (lineItem.getManualProratedLclUnitPriceFlag() == 0));
    }

    public boolean showEOLPartNotReactivable(QuoteLineItem lineItem){
    	return (lineItem.isObsoletePart() && !lineItem.canPartBeReactivated());
    }

    public boolean showRenewalQuoteLineItemNotes(QuoteLineItem lineItem) {
        //if line item references a renewal quote, and a discrepancy was found
        // when the saved draft quote was
        //loaded or the spreadsheet was uploaded. See use cases USP92: Upload
        // sales quote spreadsheet and
        //DSP02: Load saved draft sales quote for details. Note that these
        // messages appear in SQO today;
        //functionality is being duplicated
        return quote.getQuoteHeader().isSalesQuote() && (isPA() || isPAE() || isFCT() || isOEM() || isSSP())
               && lineItem.getPartDispAttr().getRenwlChgCode() != null;
        /*if (lineItem.getRenewalQuoteNum() != null &&
                lineItem.getRenewalQuoteNum().length() != 0) return true;
        return false;*/
    }

    public boolean showPartGroupNote(QuoteLineItem lineItem) {
        //if the part belongs to any part group, display the following note:
        //	"Part is a member of the followin group: [group name]."
        //	If the part belongs to more than one group (a possible but
        //	unlikely occurance), list the groups with a comma between each one.

        return ((isPA() || isPAE() || isFCT() || isOEM() || isSSP()) && lineItem.getPartGroups().size()>0);
        /*QuoteLineItem.PartGroup group = lineItem.getPartGroup();
        if(null == group)
        {
            return false;
        }
        return group.getGoupName() != null;*/
    }
    public String getGroupNameList(QuoteLineItem item){
        List groups = item.getPartGroups();
        StringBuffer buffer = new StringBuffer();
        for(int i=0;i<groups.size();i++){
            PartGroup group = (PartGroup)groups.get(i);
            if(i >0 ){
                buffer.append(",");

            }
            buffer.append(group.getGroupName());

        }
        return buffer.toString();
    }

    public boolean showPriceEndSoonNote(QuoteLineItem qli){
        return qli.isPartPrcEndSoon();
    }

    public int getNoteRowColSpan(){
        int span = 0;
        if(isPPSS()){
            return 3;
        }

        if(quote.getQuoteHeader().isSalesQuote()){
            if(isPA() || isPAE() || isOEM() || isSSP()){
                if(isPA() || isPAE() || isSSP()){
                    span = 6;
                }

                if(isOEM()){
                    span = 5;
                }
                if(showChannelMarginCol()){
                	if(quote.getSaaSLineItems() != null && quote.getSaaSLineItems().size() > 0){
                		span += 4;
                	}
                	else if(quote.getMonthlySwQuoteDomain().getMonthlySoftwares()!=null&&quote.getMonthlySwQuoteDomain().getMonthlySoftwares().size()>0){
                		span += 4;
                	}
                	else{
                		span += 3;
                	}
                }
            }else if(isFCT()){
                span = 5;
            }

        } else {
            if(isPA() || isPAE() || isOEM() || isSSP()){
                if(isPA() || isPAE() || isSSP()){
                    span = 6;
                }

                if(isOEM()){
                    span = 4;
                }

                if(showChannelMarginCol()){
                    span += 2;
                }
            }

            if(isFCT()){
                span = 4;
            }
        }

        return span;
    }

    public boolean showEOLPartEntitledPriceNote(QuoteLineItem qli){
        boolean isSalesQuote = quote.getQuoteHeader().isSalesQuote();

        if(qli.isHasEolPrice()){
            if(isSalesQuote){
                eolPartEntitledPriceNote = PartPriceViewKeys.SQ_EOL_PART_HAS_HIS_PRICE;
                return true;
            } else {
                eolPartEntitledPriceNote = PartPriceViewKeys.RQ_EOL_PART_HAS_HIS_PRICE;
                return true;
            }
        } else {
            if(isSalesQuote){
                if(qli.getManualProratedLclUnitPriceFlag() == 0){
                    eolPartEntitledPriceNote = PartPriceViewKeys.SQ_EOL_PART_NO_ENTITLED_PRICE;
                    return true;
                }
            } else {
                eolPartEntitledPriceNote = PartPriceViewKeys.RQ_EOL_PART_NO_ENTITLED_PRICE;
                return true;
            }
        }

        return false;
    }

    public String getEOLPartEntitledPriceNote(){
        return eolPartEntitledPriceNote;
    }

    public String getRnwlQuoteNote(QuoteLineItem qli){
        String renwlChgCode = qli.getPartDispAttr().getRenwlChgCode();

        if(PartPriceConstants.RenwlChgCode.RQCLOSED.equalsIgnoreCase(renwlChgCode)){
            return PartPriceViewKeys.RQ_CLOSED_NOTE;
        } else if (PartPriceConstants.RenwlChgCode.RQCHGD.equalsIgnoreCase(renwlChgCode)){
            return PartPriceViewKeys.RQ_CHGD_NOTE;
        } else if(PartPriceConstants.RenwlChgCode.RQLIREMD.equalsIgnoreCase(renwlChgCode)){
            return PartPriceViewKeys.RQ_LI_REMD_NOTE;
        }

        return StringUtils.EMPTY;
    }
    
    public boolean showExportRestrictedNote(QuoteLineItem qli){
    	return qli.isExportRestricted();
    }
    
    // Add by Mike 2013-06-26
	public boolean showLineUp2AnniversaryNote(QuoteLineItem item) {
		return QuoteCommonUtil.checkLineUp2Anniversary(quote, item);
	}
	
	//if it returns true, the NOTE column will show 
	public boolean showEndDateAnniversaryNote(QuoteLineItem item){
		boolean flag = false;
		if((!showLineUp2AnniversaryNote(item))
				&& quote.getQuoteHeader().isPAQuote()
				&& isRenewalPartOrAddFromRenewlaQuote(item)
				&& !item.isApplncPart()){
			flag = true;
		}
		return flag;
	}
	
	//If it is renewal part or renewal quote , it returns true. 
	public boolean isRenewalPartOrAddFromRenewlaQuote(QuoteLineItem item){
		boolean isRenewalPartOrAddFromRenewlaQuote = false;
		boolean isAddFromRenewalQuote = (new PartPriceCommon(quote).isAddedFromRenewalQuote(item));
		boolean isRenewalPart = item.isRenewalPart();
		if(isAddFromRenewalQuote||isRenewalPart){
			isRenewalPartOrAddFromRenewlaQuote = true;
		}
		return isRenewalPartOrAddFromRenewlaQuote;
	}
	
	public boolean isShowObsoleteMessageForPart(QuoteLineItem qli){
		boolean isShow = false;
		if(qli.isObsoletePart()&&!qli.isDivestedPart()){
			isShow = true;
		}
		return isShow;
	}
}