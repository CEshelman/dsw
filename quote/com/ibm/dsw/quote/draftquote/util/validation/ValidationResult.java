
package com.ibm.dsw.quote.draftquote.util.validation;
/**
 * Copyright 2006 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * The <code>ValidationResult</code> is to save the validation result for further processing
 * 
 * 
 * @author <a href="liuxinlx@cn.ibm.com">Andy Liu </a> <br/>
 * 
 * Creation date: Apr 18, 2007
 */

public class ValidationResult {        
    
    
    boolean quantityChanged = false;    
    String quantityChangeCode = "";    
    boolean partDeleted = false;
    boolean unitPriceOrDiscountChanged = false;
    // for renewal quote, the override unit price and discount is 0, if user input value 
    // to the field, the flag will be set
    boolean unitPriceOrDiscountSet = false;
    boolean prorateAnniversaryChanged = false;
    boolean additionalMaintYearsChanged = false;
    boolean dateOverrided = false; // user selected the new override date
    boolean gsaPricingChanged = false;
    boolean dateChanged = false;
    boolean bpOvrrdDiscountChanged=false;
    
    //Compressed coverage
    boolean cmprssCvrageMonthChanged = false;
    boolean cmprssCvrageDiscChanged = false;
    
    // SaaS part attribute
    boolean SaaSCvrageTermChanged = false;
    boolean SaaSBillingFrequencyChanged = false;
    boolean SaaSUnitPriceOrExtPriceChanged = false;
    
    
    boolean shouldRecalcuate(){
        
        return quantityChanged 
        || unitPriceOrDiscountChanged
        || prorateAnniversaryChanged
        || additionalMaintYearsChanged
        || dateOverrided
        || partDeleted
        || dateChanged
        || bpOvrrdDiscountChanged
        || cmprssCvrageMonthChanged
        || cmprssCvrageDiscChanged
        || SaaSCvrageTermChanged
        || SaaSBillingFrequencyChanged
        || SaaSUnitPriceOrExtPriceChanged;
    }
    
}
