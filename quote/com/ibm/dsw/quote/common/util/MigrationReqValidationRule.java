package com.ibm.dsw.quote.common.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ibm.dsw.quote.base.config.QuoteConstants;
import com.ibm.dsw.quote.base.domain.QuoteUserSession;
import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.common.domain.Customer;
import com.ibm.dsw.quote.common.domain.MigratePart;
import com.ibm.dsw.quote.common.domain.MigrateRequest;
import com.ibm.dsw.quote.common.domain.Partner;
import com.ibm.dsw.quote.common.process.QuoteCapabilityProcess;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;

public class MigrationReqValidationRule {

    protected LogContext logContext = LogContextFactory.singleton().getLogContext();

    protected Map validateResult = new HashMap();

    protected QuoteUserSession user;

    protected MigrateRequest migrateRequest;
    
    public MigrationReqValidationRule(MigrateRequest migrateRequest, QuoteUserSession user) {
        this.user = user;
        this.migrateRequest = migrateRequest;
    }
    
    /**
     *  validate FCT to PA migration request when submit 
     */
    @SuppressWarnings("unchecked")
	public Map validateFCTToPAMigrationRequest()  throws QuoteException {
        // Validate customer is selected
    	Customer customer = migrateRequest.getCustomer();
        if (customer == null) {
            validateResult.put(QuoteCapabilityProcess.MAGRATION_CUSTOMER_NOT_SELECTED, Boolean.TRUE);
        }
        
    	// Validate migration parts should not be null
        List<MigratePart> migrateParts = migrateRequest.getParts();
        if (migrateParts == null || (migrateParts != null && migrateParts.size() == 0)){
        	 validateResult.put(QuoteCapabilityProcess.MAGRATION_LINE_ITEM_NOT_SELECT, Boolean.TRUE);
        } 

        // Validate fulfillment source should be setted
        if (!isFulfillmentSrcSetForMigration()) {
            validateResult.put(QuoteCapabilityProcess.MIGRATION_FULFILLMENT_SRC_NOT_SET, Boolean.TRUE);
        }
        
    	// Validate reseller and distributor per fulfillment source
        validatePartnersForFCTToPAMigration(validateResult);
        
    	return validateResult;
    }
    
    protected boolean isFulfillmentSrcSetForMigration(){
    	if(migrateRequest == null){
    		return false;
    	}
        boolean isFulfillmentSrc = false;
        String fulfillmentSrc = migrateRequest.getFulfillmentSrc();
        if(fulfillmentSrc != null){
            if(QuoteConstants.FULFILLMENT_CHANNEL.equalsIgnoreCase(fulfillmentSrc.trim()) || 
            		QuoteConstants.FULFILLMENT_DIRECT.equalsIgnoreCase(fulfillmentSrc.trim())){
            	isFulfillmentSrc = true;
            }        	
        }

        return isFulfillmentSrc;
    }
    
    @SuppressWarnings("unchecked")
	protected void validatePartnersForFCTToPAMigration(Map result) {
    	if(migrateRequest == null){
    		return ;
    	}
        boolean isChannel = false;
        String fulfillmentSrc = migrateRequest.getFulfillmentSrc();
        if(fulfillmentSrc != null){
            if(QuoteConstants.FULFILLMENT_CHANNEL.equalsIgnoreCase(fulfillmentSrc.trim())){
            	isChannel = true;
            }         	
        }

        if (isChannel) {
            Partner reseller = migrateRequest.getReseller();
            Partner distributor = migrateRequest.getPayer();

            //If the fulfillment source is channel, a reseller must be selected
            if (reseller == null) {
                result.put(QuoteCapabilityProcess.MAGRATION_RESELLER_NOT_SELECT, Boolean.TRUE);
            }

            // If the fulfillment source is channel, a distributor must be selected.
            // If the reseller is a tier 1 reseller, the distributor should be set to the same value.

            if (distributor == null) {
                result.put(QuoteCapabilityProcess.MAGRATION_DISTRIBUTOR_NOT_SELECT, Boolean.TRUE);
            }
   	 }
   }
   
}
