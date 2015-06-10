/*
 * Created on 2009-6-11
 */
package com.ibm.dsw.quote.submittedquote.viewbean.helper;

import java.io.Serializable;

import org.apache.commons.lang.StringUtils;

import com.ibm.dsw.quote.common.domain.Quote;
import com.ibm.dsw.quote.common.domain.QuoteLineItem;

/**
 * @author Xiao Guo Yi
 * 
 * $Log: SubmittedNoteRow.java,v $
 * Revision 1.7  2010/07/28 16:17:22  pbaskara
 * CLP part notes should be displayed for all LOBs - if SAP PWS returns true.  RTC work item : 3214,3212
 *
 * Revision 1.6  2010/07/28 02:43:31  wxiaoli
 * CLP : DSQ02:  Display current draft sales quote parts & pricing tab, RTC 3212, reviewed by Will
 * fix the PL of TAHN-87RQ5D : No CLP text for PA quote
 *
 * Revision 1.5  2010/06/10 04:11:45  pbaskara
 * CLP part notes should be displayed for all LOBs - if SAP PWS returns true.  RTC work item : 3214
 *
 * Revision 1.4  2010/05/27 07:45:52  wxiaoli
 * contract level prcing flag for Display parts & pricing tab, RTC 3211,3212,3213,3214,3215,3216,3217, reviewed by Will
 *
 */
public class SubmittedNoteRow implements Serializable{
    private Quote quote;
    
    public SubmittedNoteRow(Quote quote){
        this.quote = quote;
    }
    
    public boolean showNoteRow(QuoteLineItem qli){
        if(quote.getQuoteHeader().isSalesQuote()){
            return (showEOLNote(qli) || StringUtils.isNotBlank(qli.getRenewalQuoteNum()) || showCtLvPrcAppPartNote(qli)||qli.isDivestedPart());
        }
        
        if(quote.getQuoteHeader().isRenewalQuote()){
            return (showEOLNote(qli) || showCtLvPrcAppPartNote(qli)||qli.isDivestedPart());
        }
        
        return false;
    }
    
    public boolean showEOLNote(QuoteLineItem qli){
    	return (showHasEOLPriceNote(qli) || showEntitledPriceOverriden(qli));
    }
    
    public boolean showPartGroups(QuoteLineItem qli){
        return (qli.getPartGroups() != null && qli.getPartGroups().size() > 0);
    }
    
    public boolean showHasEOLPriceNote(QuoteLineItem qli){
    	return (qli.isObsoletePart() && qli.canPartBeReactivated() && qli.isHasEolPrice());
    }
    
    public boolean showEntitledPriceOverriden(QuoteLineItem qli){
        return (qli.isObsoletePart()
                && qli.canPartBeReactivated()
                && (qli.getManualProratedLclUnitPriceFlag() == 1));
    }
    
    /**
     * @param lineItem
     * @return
     */
    public boolean showCtLvPrcAppPartNote(QuoteLineItem lineItem) {
        if (lineItem.isLegacyBasePriceUsedFlag()) {
            return true;
        }
        return false;
    }
    
    public boolean showExportRestrictedNote(QuoteLineItem qli){
    	return qli.isExportRestricted();
    }
}
