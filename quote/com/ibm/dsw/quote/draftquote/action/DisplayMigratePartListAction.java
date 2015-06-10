package com.ibm.dsw.quote.draftquote.action;

import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.ibm.dsw.quote.base.action.BaseContractActionHandler;
import com.ibm.dsw.quote.base.config.ParamKeys;
import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.common.domain.MigratePart;
import com.ibm.dsw.quote.common.domain.MigrateRequest;
import com.ibm.dsw.quote.common.process.QuoteProcess;
import com.ibm.dsw.quote.common.process.QuoteProcessFactory;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteParamKeys;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteStateKeys;
import com.ibm.dsw.quote.draftquote.contract.DisplayMigratePartListContract;
import com.ibm.dsw.quote.draftquote.process.MigrationRequestProcessFactory;
import com.ibm.ead4j.jade.bean.ResultBean;
import com.ibm.ead4j.jade.bean.ResultBeanException;
import com.ibm.ead4j.jade.bean.ResultHandler;
import com.ibm.ead4j.jade.contract.ProcessContract;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;

public class DisplayMigratePartListAction extends BaseContractActionHandler {

	private static final long serialVersionUID = 69753584973415268L;
	static final LogContext logContext = LogContextFactory.singleton()
			.getLogContext();

	@Override
	public ResultBean executeBiz(ProcessContract contract, ResultHandler handler)
			throws QuoteException, ResultBeanException {
		DisplayMigratePartListContract parm = (DisplayMigratePartListContract) contract;
		String migrtnReqstNum = parm.getMigrtnReqstNum();
		QuoteProcess process = QuoteProcessFactory.singleton().create();
		//Call migrate request first, because the caNum is nullable when from cp tab
		MigrateRequest migrateRequest = MigrationRequestProcessFactory.singleton().create().getMigrtnReq(migrtnReqstNum);
		String caNum = parm.getCaNum();
		logContext.debug(this, "caNum=" + caNum);
		if ( StringUtils.isBlank(parm.getCaNum()) )
		{
			caNum = migrateRequest.getOrginalCANum();
		}
		logContext.debug(this, "caNum from request=" + caNum);
		List list = process.getMigrateParts(caNum);
		List migrationList = migrateRequest.getParts();
		
		// if the two table have no data with migrtnReqstNum as primary key then all acquire data is default
		// else make sure the status is the data begin to data.
		if( migrationList != null && migrationList.size() != 0 ){
			for(int i= 0 ; i < list.size(); i++){
				MigratePart caPart = (MigratePart) list.get(i);
				caPart.setMigration(false);
				for(int j = 0; j < migrationList.size(); j++){
					MigratePart reqPart = (MigratePart) migrationList.get(j);
					if(reqPart.getSeqNum() == caPart.getSeqNum() ){
						caPart.setMigration(true);
						break;
					}
				}
			}
		}	

		handler.addObject(DraftQuoteParamKeys.MIGRATE_PART_LIST, list);
		handler.addObject(DraftQuoteParamKeys.BILLING_FREQUENCY, migrateRequest.getBillingFreq());
    	handler.addObject(DraftQuoteParamKeys.COVERAGE_TERM, migrateRequest.getCoverageTerm()+"");
    	handler.addObject(DraftQuoteParamKeys.CA_NUM, caNum);
    	handler.addObject(ParamKeys.PARAM_MIGRATION_REQSTD_NUM, migrtnReqstNum); 
		
		handler.setState(DraftQuoteStateKeys.STATE_DISPLAY_MIGRATE_PART_LIST);
		return handler.getResultBean();
	}

}
