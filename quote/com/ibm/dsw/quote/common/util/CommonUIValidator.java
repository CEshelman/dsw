/*
 * Created on Nov 18, 2009
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.ibm.dsw.quote.common.util;

import org.apache.commons.lang.StringUtils;

import com.ibm.dsw.quote.base.config.QuoteConstants;
import com.ibm.dsw.quote.common.config.PartPriceConstants;

/**
 * @author xiaogy@cn.ibm.com
 */
public class CommonUIValidator {
	public static final int WEB_QUOTE_NUM_LENGTH = 10;
	public static final int CURRNCY_CODE_LENGTH = 3;
	public static final int PART_NUM_LENGTH = 7;
    
    
    public static boolean isLobValid(String lob){
        //add OEM support
        if(QuoteConstants.LOB_PA.equals(lob)
                || QuoteConstants.LOB_PAE.equals(lob)
                || QuoteConstants.LOB_PAUN.equals(lob)
                || QuoteConstants.LOB_FCT.equals(lob)
                || QuoteConstants.LOB_PPSS.equals(lob)
                || QuoteConstants.LOB_OEM.equalsIgnoreCase(lob)
                || QuoteConstants.LOB_SSP.equalsIgnoreCase(lob)){
            return true;
            
        } else {
            return false;
        }
    }
    
    public static boolean isQuoteNumValid(String quoteNum){
        if(StringUtils.isBlank(quoteNum)){
            return false;
        }
        
        int len = quoteNum.length();
        
        //ensure length is 10
        if(len != WEB_QUOTE_NUM_LENGTH){
            return false;
        }
        
        //ensure each char is quoteNum is number
        for(int i = 0; i < len; i++){
            if(!Character.isDigit(quoteNum.charAt(i))){
                return false;
            }
        }
        
        return true;
    }
    
    public static boolean isAudienceValid(String audience){
        if(StringUtils.isBlank(audience)){
            return false;
        }
        
        if(QuoteConstants.QUOTE_AUD_CODE.INTERNAL.equals(audience)
                || QuoteConstants.QUOTE_AUD_CODE.SALES.equals(audience)
                || QuoteConstants.QUOTE_AUD_CODE.PSPTRSEL.equals(audience)){
            return true;
        }
        
        return false;
    }
    
    public static boolean isCurrencyValid(String currencyCode){
        if(StringUtils.isBlank(currencyCode)){
            return false;
        }
        
        int len = currencyCode.length();
        if(len != CURRNCY_CODE_LENGTH){
            return false;
        }
        
//      ensure each char is currencyCode is character
        if(!StringUtils.isAlpha(currencyCode)){
            return false;
        }
        
        return true;
    }
    
    public static boolean isPartNumberValid(String partNum){
        if(StringUtils.isBlank(partNum)){
            return false;
        }
        
        int len = partNum.length();
        
        if(len != PART_NUM_LENGTH){
            return false;
        }
        
//      ensure each char is partNum is character or digit
        if(!StringUtils.isAlphanumeric(partNum)){
            return false;
        }
        
        return true;
    }
    
    public static boolean isPartSeqNumberValid(String seqNum){
        if(StringUtils.isBlank(seqNum)){
            return false;
        }
        
        //The part seq number should be valid integer number
        if(!StringUtils.isNumeric(seqNum)){
            return false;
        }
        
        return true;
    }
    
    public static boolean isPartBrandValid(String partBrand) {
        if (StringUtils.isBlank(partBrand)) {
            return false;
        }
/*
        if (partBrand.equals(PartPriceConstants.PartBrand.DB2)
                || partBrand.equals(PartPriceConstants.PartBrand.LOTUS)
                || partBrand.equals(PartPriceConstants.PartBrand.OTHER)
                || partBrand.equals(PartPriceConstants.PartBrand.RATIONAL)
                || partBrand.equals(PartPriceConstants.PartBrand.TIVOL)
                || partBrand.equals(PartPriceConstants.PartBrand.WEBSPHER)) {
            return true;
        }
*/
        return true;
    }
}
