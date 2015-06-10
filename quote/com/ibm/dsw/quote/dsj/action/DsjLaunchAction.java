package com.ibm.dsw.quote.dsj.action;

import java.util.ArrayList;
import java.util.List;

import com.ibm.dsw.quote.appcache.domain.AcqCodeDesc;
import com.ibm.dsw.quote.appcache.domain.CodeDescObj;
import com.ibm.dsw.quote.appcache.process.CacheProcessFactory;
import com.ibm.dsw.quote.base.config.ParamKeys;
import com.ibm.dsw.quote.base.config.QuoteConstants;
import com.ibm.dsw.quote.base.contract.QuoteBaseContract;
import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.common.domain.QuoteRightColumn;
import com.ibm.dsw.quote.common.process.QuoteProcess;
import com.ibm.dsw.quote.common.process.QuoteProcessFactory;
import com.ibm.dsw.quote.dsj.contract.DsjBaseContract;
import com.ibm.dsw.quote.dsj.contract.DsjLaunchContract;
import com.ibm.dsw.quote.dsj.util.DsjKeys;
import com.ibm.dsw.quote.newquote.config.NewQuoteParamKeys;
import com.ibm.ead4j.jade.bean.ResultBean;
import com.ibm.ead4j.jade.bean.ResultBeanException;
import com.ibm.ead4j.jade.bean.ResultHandler;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;

public class DsjLaunchAction extends DsjBaseAction {
    protected static final LogContext logContext = LogContextFactory.singleton().getLogContext();

	@Override
	public ResultBean executeBiz(DsjBaseContract contract, ResultHandler handler)
			throws QuoteException, ResultBeanException {
		DsjLaunchContract dlc = (DsjLaunchContract) contract;
		// TODO Auto-generated method stub
		logContext.debug(this, "getOpprtntyNum() is: "+contract.getOpprtntyNum());
		logContext.debug(this, "getOppOwnerEmailAddr() is: "+contract.getOppOwnerEmailAddr());
		logContext.debug(this, "getCustQueryName() is: "+contract.getCustQueryName());
		logContext.debug(this, "getResellerQueryName() is: "+contract.getResellerQueryName());
		
		

        List countries = CacheProcessFactory.singleton().create().getCountryList();
        List lobs = CacheProcessFactory.singleton().create().getLOBList();
        List acqstns = filterAcqstns(CacheProcessFactory.singleton().create().getAcquisitionList());
        
        List displayLobs = new ArrayList();
        if (lobs != null) {
            for (int i = 0; i < lobs.size(); i++) {
                CodeDescObj lob = (CodeDescObj) lobs.get(i);
                // remove PA & PAE
                if (QuoteConstants.LOB_PA.equalsIgnoreCase(lob.getCode())
                        || QuoteConstants.LOB_PAE.equalsIgnoreCase(lob.getCode()))
                    continue;
                displayLobs.add(lob);
            }

        }
        
        handler.addObject(NewQuoteParamKeys.PARAM_COUNTRY_LIST, countries);
        handler.addObject(NewQuoteParamKeys.PARAM_LOB_LIST, displayLobs);
        handler.addObject(NewQuoteParamKeys.PARAM_ACQUISITION_LIST, acqstns);

        handler.addObject(NewQuoteParamKeys.PARAM_DEFAULT_COUNTRY, dlc.getDefaultCountry());
        handler.addObject(NewQuoteParamKeys.PARAM_DEFAULT_LOB, dlc.getDefaultLOB());
        handler.addObject(NewQuoteParamKeys.PARAM_DEFAULT_ACQSTN, dlc.getDefaultAcquisition());
        
        

        //get creatorId from Contract
        QuoteBaseContract quoteBaseContract = (QuoteBaseContract) contract;
        String creatorId = quoteBaseContract.getUserId();
        
        // get session quote information for right column
        QuoteRightColumn sessionQuote = null;
        if (creatorId != null && !"".equals(creatorId)) {
	        QuoteProcess process = QuoteProcessFactory.singleton().create();
	        sessionQuote = process.getQuoteRightColumnInfo(creatorId);
        }
        
        handler.addObject(ParamKeys.PARAM_QUOTE_RIGHTCOLUMN, sessionQuote);
        
        handler.setState(DsjKeys.DSJ_LAUNCH_STATE);
        		
		return handler.getResultBean();
	}
	
	

    protected List filterAcqstns(List list)
    {
    	List retList = new ArrayList();
    	if ( list == null )
    	{
    		return retList;
    	}
    	for ( int i = 0; i < list.size(); i++ )
    	{
    		AcqCodeDesc acq = (AcqCodeDesc)list.get(i);
    		if ( acq.isEffFlag() )
    		{
    			retList.add(acq);
    		}
    	}
    	return retList;
    }
}
