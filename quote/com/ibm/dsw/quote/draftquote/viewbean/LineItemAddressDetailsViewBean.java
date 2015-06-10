package com.ibm.dsw.quote.draftquote.viewbean;

import static com.ibm.dsw.quote.base.config.ParamKeys.LINE_ITEM_ADDRESSES;

import java.util.List;

import com.ibm.dsw.common.base.util.CommonUtils;
import com.ibm.dsw.quote.base.viewbean.BaseViewBean;
import com.ibm.dsw.quote.common.domain.LineItemAddress;
import com.ibm.ead4j.jade.bean.ViewBeanException;
import com.ibm.ead4j.jade.util.Parameters;

public class LineItemAddressDetailsViewBean extends BaseViewBean {

	private static final long serialVersionUID = 3647864093196809891L;
	
	public final int SUCCESS = 0;
	List<LineItemAddress> liDetails;

	@SuppressWarnings("unchecked")
	@Override
	public void collectResults(Parameters params) throws ViewBeanException {
		super.collectResults(params);
		liDetails = (List<LineItemAddress>) params.getParameter(LINE_ITEM_ADDRESSES);
	}
	
	/** Map LineItemAddress as a JSON object using get/is methods 
	 * as JSON object names.
	 * @return JSONObject list of LineItemAddress */
	public String getResponse()  {
		StringBuffer sb = new StringBuffer("[");
		for (LineItemAddress l : liDetails) {
			sb.append("{");
			
			sb.append("\"partNumber\":\""+l.getPartNumber()+"\",");
			sb.append("\"crad\":\""+l.getCrad()+"\",");
			sb.append("\"partDescription\":\""+CommonUtils.filter(l.getPartDescription())+"\",");
			sb.append("\"shipToAddress\":\""+CommonUtils.filter(l.getShipToAddress())+"\",");
			sb.append("\"installToAddress\":\""+CommonUtils.filter(l.getInstallToAddress())+"\"");
			
			sb.append("},");
		}
		String str = sb.length()>1? sb.substring(0, sb.length()-1):sb.toString();
		return str + "]";
	}

	public int getReturnCode() { return SUCCESS; }
	public boolean error() { return !success(); }
	public boolean success() { return getReturnCode() == SUCCESS; }
}
