package com.ibm.dsw.quote.draftquote.action;

import com.ibm.dsw.quote.base.action.BaseContractActionHandler;
import com.ibm.dsw.quote.base.config.ParamKeys;
import com.ibm.dsw.quote.base.config.StateKeys;
import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.base.util.HtmlUtil;
import com.ibm.dsw.quote.common.process.QuoteProcess;
import com.ibm.dsw.quote.common.process.QuoteProcessFactory;
import com.ibm.dsw.quote.draftquote.contract.AddMigratePartListContract;
import com.ibm.ead4j.jade.bean.ResultBean;
import com.ibm.ead4j.jade.bean.ResultBeanException;
import com.ibm.ead4j.jade.bean.ResultHandler;
import com.ibm.ead4j.jade.contract.ProcessContract;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;

public class AddMigratePartListAction extends BaseContractActionHandler {

	private static final long serialVersionUID = 69753584973415268L;
	static final LogContext logContext = LogContextFactory.singleton()
			.getLogContext();

	@Override
	public ResultBean executeBiz(ProcessContract contract, ResultHandler handler)
			throws QuoteException, ResultBeanException {
		// TODO Auto-generated method stub
		AddMigratePartListContract pars = (AddMigratePartListContract) contract;

		String caNum = pars.getCaNum();
		String partNums = pars.getPartNums();
		String lineNums = pars.getLineNums();
		String migrtnReqstNum = pars.getMigrtnReqstNum();
		String billingFreq = pars.getBillingFreq();
		String coverageTerm = pars.getCoverageTerm();
		String userId = pars.getUserId();
		logContext.debug(this, "caNum := " + caNum);
		logContext.debug(this, "partNums := " + partNums);
		logContext.debug(this, "lineNums := " + lineNums);
		logContext.debug(this, "migrtnReqstNum := " + migrtnReqstNum);
		logContext.debug(this, "billingFreq := " + billingFreq);
		logContext.debug(this, "coverageTerm := " + coverageTerm);
		logContext.debug(this, "userId := " + userId);
		QuoteProcess process = QuoteProcessFactory.singleton().create();
		String requestNum = process.addMigrateParts(caNum, migrtnReqstNum,
				partNums, lineNums, billingFreq, coverageTerm, userId);

		handler.setState(StateKeys.STATE_REDIRECT_ACTION);

		StringBuffer buff = new StringBuffer();
		buff.append(HtmlUtil.getURLForAction("DISPLAY_FCT2PA_CUST_PARTNER"));
		buff.append("&").append(ParamKeys.PARAM_MIGRATION_REQSTD_NUM).append("=").append(requestNum);
		handler.addObject(ParamKeys.PARAM_REDIRECT_URL, buff.toString());
		return handler.getResultBean();
	}
}
