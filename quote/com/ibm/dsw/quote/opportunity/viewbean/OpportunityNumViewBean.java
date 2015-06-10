package com.ibm.dsw.quote.opportunity.viewbean;

import java.util.Iterator;
import java.util.List;

import com.ibm.dsw.quote.base.viewbean.BaseViewBean;
import com.ibm.dsw.quote.newquote.config.NewQuoteParamKeys;
import com.ibm.dsw.quote.opportunity.domain.Opportunity;
import com.ibm.ead4j.jade.bean.ViewBeanException;
import com.ibm.ead4j.jade.util.Parameters;
import com.ibm.dsw.quote.draftquote.util.StringUtils;

public class OpportunityNumViewBean extends BaseViewBean{

	private static final long serialVersionUID = 3851631125632265931L;
	protected String returnCode = "";
    protected transient List<Opportunity> oppNumList;

	@SuppressWarnings("unchecked")

	public void collectResults(Parameters parameters) throws ViewBeanException {
        super.collectResults(parameters);
        returnCode = parameters.getParameterAsString(NewQuoteParamKeys.PARAM_RETURN_OPPNUM_CODE);
        oppNumList = (List<Opportunity>)parameters.getParameter(NewQuoteParamKeys.PARAM_OPPORTUNIRY_NUMS);
    }

	public String getReturnCode() {
		return returnCode;
	}

	public List<Opportunity> getOppNumList() {
		return oppNumList;
	}

	public String getOppNumListJson(){
		StringBuilder itBuf = new StringBuilder("oppNumList:[");
		if(null != oppNumList && oppNumList.size()>0){
			Iterator<Opportunity> it = oppNumList.iterator();

			while(it.hasNext()){
				Opportunity opp = (Opportunity)it.next();
				if(null != opp){
					itBuf.append("{name:\"").append(StringUtils.jsonStringEncoding(opp.getOpptName()))
						.append("\",value:\"").append(opp.getOpptNum()).append("\"},");
				}
			}

			itBuf.deleteCharAt(itBuf.length()-1);
		}
		itBuf.append("]");
		return itBuf.toString();
	}
}
