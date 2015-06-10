package com.ibm.dsw.quote.draftquote.contract;

import org.apache.commons.lang.StringUtils;

import com.ibm.dsw.quote.base.config.ParamKeys;
import com.ibm.dsw.quote.base.contract.QuoteBaseContract;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteParamKeys;
import com.ibm.ead4j.jade.session.JadeSession;
import com.ibm.ead4j.jade.util.Parameters;

public class DisplayMigratePartListContract extends QuoteBaseContract{

	private static final long serialVersionUID = 8713355147236641662L;
	
	protected String caNum;
	protected String migrtnReqstNum;

	public String getMigrtnReqstNum() {
		return migrtnReqstNum;
	}

	public void load(Parameters parameters, JadeSession session) 
	{
		super.load(parameters, session);
		caNum = parameters.getParameterAsString(DraftQuoteParamKeys.CA_NUM);
		migrtnReqstNum = parameters.getParameterAsString(ParamKeys.PARAM_MIGRATION_REQSTD_NUM);
		if ( StringUtils.isEmpty(migrtnReqstNum) )
		{
			migrtnReqstNum = "0";
		}
	}

	public String getCaNum() {
		return caNum;
	}
}
