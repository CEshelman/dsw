package com.ibm.dsw.quote.configurator.action;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.ibm.dsw.quote.base.config.ParamKeys;
import com.ibm.dsw.quote.base.config.StateKeys;
import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.base.util.HtmlUtil;
import com.ibm.dsw.quote.configurator.config.ConfiguratorParamKeys;
import com.ibm.dsw.quote.configurator.config.ConfiguratorStateKeys;
import com.ibm.dsw.quote.configurator.contract.SearchRestrictedMonthlyPartsContract;
import com.ibm.dsw.quote.configurator.process.MonthlyConfiguratorProcess;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteActionKeys;
import com.ibm.dsw.quote.ps.process.PartSearchProcess;
import com.ibm.dsw.quote.ps.process.PartSearchProcessFactory;
import com.ibm.ead4j.jade.bean.ResultBean;
import com.ibm.ead4j.jade.bean.ResultBeanException;
import com.ibm.ead4j.jade.bean.ResultHandler;
import com.ibm.ead4j.jade.contract.ProcessContract;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;
import com.ibm.ead4j.topaz.exception.TopazException;
/**
 * @ClassName: SearchRestrictedMonthlyPartsAction
 * @author Crimson Lin
 * @Description: Search the input restricted part list from DB, if there are restricted monthly parts which 
 * also were not contained by current configuration, then insert them into both configuration table and quote
 * line item table, prepares to configurate them in configurator.
 * @date April 07, 2015
 *
 */

public class SearchRestrictedMonthlyPartsAction extends SubmittedMonthlyConfiguratorAction {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	protected String getState(ProcessContract contract) {
		return ConfiguratorStateKeys.STATE_SEARCH_RESTRICT_PARTS_GO;
	}

	@Override
	public ResultBean executeBiz(ProcessContract contract, ResultHandler handler)
			throws QuoteException, ResultBeanException {
		LogContext logger = LogContextFactory.singleton().getLogContext();

		// search input part list string
		SearchRestrictedMonthlyPartsContract searchRestrictedPartContract = (SearchRestrictedMonthlyPartsContract) contract;
		
		MonthlyConfiguratorProcess monthlyProcess = getMonthlySwProcess(searchRestrictedPartContract
				.getConfigrtnActionCode());
		
		Map<String, String> reuturnStringMap;
		
		String notFoundList="";
		String existedList=searchRestrictedPartContract.getExistingPartList();
		String needAddedList="";
		String redirectURL = HtmlUtil.getURLForAction(DraftQuoteActionKeys.BUILD_MONTHLY_SW_CONFIGUATOR);
		String inputSearchString = searchRestrictedPartContract.getSearchPartList();
		
		String searchString = "";//only use this parameter when need get related parts
		String retrievalType="-1"; //others
		
		String creatorId = searchRestrictedPartContract.getUserId();
		String webQuoteNum=searchRestrictedPartContract.getWebQuoteNum()==null?"":searchRestrictedPartContract.getWebQuoteNum();
		String caNum=searchRestrictedPartContract.getChrgAgrmtNum()==null?"":searchRestrictedPartContract.getChrgAgrmtNum();
		String actionCode=searchRestrictedPartContract.getConfigrtnActionCode();
		String orgConfigId=searchRestrictedPartContract.getOrgConfigId()==null?"":searchRestrictedPartContract.getOrgConfigId();
		
		try {
			//1. submit current changes
			monthlyProcess.addMonthlySwToQuote(searchRestrictedPartContract);
			//2. search input partNumList
			if (!StringUtils.isBlank(inputSearchString.replaceAll(",", ""))){
				reuturnStringMap = monthlyProcess.searchRestrictedMonthlyPart(searchRestrictedPartContract);	
				
				notFoundList = (String) reuturnStringMap.get(ConfiguratorParamKeys.notFoundRestrictedPartList);
				needAddedList = (String) reuturnStringMap.get(ConfiguratorParamKeys.neededProcessRestrictedPartList);
				
				//3. save part to db which need to be added.
				if(!StringUtils.isBlank(needAddedList)){
					PartSearchProcess process= PartSearchProcessFactory.singleton().create();
					
					String[] str = needAddedList.split(",");
					List<String> partNumList = new ArrayList<String>();
				
					for(int i=0;i<str.length;i++){
						partNumList.add(str[i]);
					}	
					process.saveSelectedParts(partNumList, creatorId, searchString, retrievalType,caNum,actionCode,orgConfigId );
				}
			}
			
		} catch (TopazException te) {
            logger.error(this, te);
            throw new QuoteException(te);
        } catch (RemoteException re) {
            logger.error(this, re);
            throw new QuoteException(re);
        }
		//4. save redirect parameter for buildMonthlyAction
		if (!StringUtils.isBlank(existedList)){
			redirectURL = HtmlUtil.addURLParam(new StringBuffer(redirectURL), ConfiguratorParamKeys.existedRestriectedPartList,existedList).toString();
		}
		if (!StringUtils.isBlank(notFoundList)){
			redirectURL = HtmlUtil.addURLParam(new StringBuffer(redirectURL), ConfiguratorParamKeys.notFoundRestrictedPartList,notFoundList).toString();
		}
		if (!StringUtils.isBlank(needAddedList)){
			redirectURL = HtmlUtil.addURLParam(new StringBuffer(redirectURL), ConfiguratorParamKeys.neededProcessRestrictedPartList,needAddedList).toString();
		}
		
		//userId can be fetched from session, no need put into request
		redirectURL=HtmlUtil.addURLParam(new StringBuffer(redirectURL),ConfiguratorParamKeys.webQuoteNum,webQuoteNum).toString();
		redirectURL=HtmlUtil.addURLParam(new StringBuffer(redirectURL),ConfiguratorParamKeys.configrtnActionCode,actionCode).toString();
		redirectURL=HtmlUtil.addURLParam(new StringBuffer(redirectURL),ConfiguratorParamKeys.chrgAgrmtNum,caNum).toString();
		redirectURL=HtmlUtil.addURLParam(new StringBuffer(redirectURL),ConfiguratorParamKeys.orgConfigId,orgConfigId).toString();
		
		
		handler.addObject(ParamKeys.PARAM_REDIRECT_URL, redirectURL);
		handler.addObject(ConfiguratorParamKeys.CANCEL_CONFIGRTN, false);
        handler.setState(StateKeys.STATE_REDIRECT_ACTION);
		
		return handler.getResultBean();
		
	}
	
	protected boolean validate(ProcessContract contract) {
		return super.validate(contract);

	}

}
