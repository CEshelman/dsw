package com.ibm.dsw.quote.draftquote.contract;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.ibm.dsw.quote.base.config.ParamKeys;
import com.ibm.dsw.quote.common.domain.MigratePart;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteParamKeys;
import com.ibm.dsw.quote.draftquote.config.SpecialBidViewKeys;
import com.ibm.ead4j.jade.session.JadeSession;
import com.ibm.ead4j.jade.util.Parameters;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;

public class AddMigratePartListContract extends DisplayMigratePartListContract {

	private static final long serialVersionUID = -4444313307220462791L;
	protected static final LogContext logContext = LogContextFactory.singleton().getLogContext();

	public void load(Parameters parameters, JadeSession session) {
		super.load(parameters, session);
		String temp = parameters.getParameterAsString(DraftQuoteParamKeys.PART_TOTAL_NUM);
		int total = 0;
		try
		{
			total = Integer.parseInt(temp);
		}
		catch ( Throwable t )
		{
			logContext.error(this, t.getMessage());
		}
		partNums = new StringBuffer();
		lineNums = new StringBuffer();
		for ( int i = 0; i < total; i++ )
		{
			String partNum = parameters.getParameterAsString(DraftQuoteParamKeys.PART_NUM + "_" + i);
			if ( !SpecialBidViewKeys.NO_OPTION.equals(partNum) )
			{
				String seqNum = parameters.getParameterAsString(DraftQuoteParamKeys.LINE_SEQ_NUM + "_" + i);
				partNums.append(partNum.trim()).append(",");
				lineNums.append(seqNum).append(",");
			}
		}
		migrtnReqstNum = parameters.getParameterAsString(ParamKeys.PARAM_MIGRATION_REQSTD_NUM);
		if ( StringUtils.isBlank(migrtnReqstNum) )
		{
			migrtnReqstNum = "0";
		}
		billingFreq = parameters.getParameterAsString(DraftQuoteParamKeys.BILLING_FREQ);
		displayTerm = parameters.getParameterAsString(DraftQuoteParamKeys.DISPLAY_TERM);
		if("0".equals(displayTerm)){
			coverageTerm = "0";
		}else{			
			coverageTerm = parameters.getParameterAsString(DraftQuoteParamKeys.COVERAGE_TERM);
		}		
	}


	protected StringBuffer partNums;
	protected StringBuffer lineNums;
	protected String migrtnReqstNum;
	protected String billingFreq;
	protected String coverageTerm;
	protected transient List<MigratePart> partList = new ArrayList<MigratePart>();
	protected String displayTerm;
	
	public String getDisplayTerm() {
		return displayTerm;
	}
	public String getBillingFreq() {
		return billingFreq;
	}
	public String getCoverageTerm() {
		return coverageTerm;
	}

	public String getCaNum() {
		return caNum;
	}
	public String getPartNums() {
		return partNums.toString();
	}
	public String getLineNums() {
		return lineNums.toString();
	}

	public String getMigrtnReqstNum() {
		return migrtnReqstNum;
	}

	
}
