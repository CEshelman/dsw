package com.ibm.dsw.quote.draftquote.viewbean;

import com.ibm.dsw.quote.base.viewbean.BaseViewBean;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteParamKeys;
import com.ibm.ead4j.jade.bean.ViewBeanException;
import com.ibm.ead4j.jade.util.Parameters;

/**
 * 
 * @author jason
 * 
 */
public class SubmitLppFormViewBean extends BaseViewBean {
	private String flag;
	private String errorMsgOrLpp;
	private double ytyGrowth;
	private String gdPartFlag;

	public String getFlag() {
		return flag;
	}

	public void setFlag(String flag) {
		this.flag = flag;
	}

	public String getErrorMsgOrLpp() {
		return errorMsgOrLpp;
	}

	public void setErrorMsgOrLpp(String errorMsgOrLpp) {
		this.errorMsgOrLpp = errorMsgOrLpp;
	}

	public void collectResults(Parameters params) throws ViewBeanException {
		if (params
				.getParameterAsString(DraftQuoteParamKeys.PARAM_WEB_QUOTE_YTY_FLAG) != null) {
			this.setFlag(params
					.getParameterAsString(DraftQuoteParamKeys.PARAM_WEB_QUOTE_YTY_FLAG));
		}
		if (params
				.getParameterAsString(DraftQuoteParamKeys.PARAM_WEB_QUOTE_YTY_ERROR_MSG_OR_LPP) != null) {
			this.setErrorMsgOrLpp(params
					.getParameterAsString(DraftQuoteParamKeys.PARAM_WEB_QUOTE_YTY_ERROR_MSG_OR_LPP));
		}
		
		if (params.getParameterAsString(DraftQuoteParamKeys.PARAM_YTY_GROWTH) != null && !"".equals(params.getParameterAsString(DraftQuoteParamKeys.PARAM_YTY_GROWTH))){
			this.setYtyGrowth(Double.parseDouble(params.getParameterAsString(DraftQuoteParamKeys.PARAM_YTY_GROWTH)) );
		}
		
		if (params.getParameterAsString(DraftQuoteParamKeys.PARAM_GROWTH_YTY_PART_FLAG) != null && !"".equals(params.getParameterAsString(DraftQuoteParamKeys.PARAM_GROWTH_YTY_PART_FLAG))){
			this.setGdPartFlag(params.getParameterAsString(DraftQuoteParamKeys.PARAM_GROWTH_YTY_PART_FLAG));
		}

	}

	public double getYtyGrowth() {
		return ytyGrowth;
	}

	public void setYtyGrowth(double ytyGrowth) {
		this.ytyGrowth = ytyGrowth;
	}

	public String getGdPartFlag() {
		return gdPartFlag;
	}

	public void setGdPartFlag(String gdPartFlag) {
		this.gdPartFlag = gdPartFlag;
	}

	
	
	
}
