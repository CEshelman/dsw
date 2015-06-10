package com.ibm.dsw.quote.common.domain.jdbc;

import java.sql.CallableStatement;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import com.ibm.dsw.quote.base.util.LogHelper;
import com.ibm.dsw.quote.base.util.TopazUtil;
import com.ibm.dsw.quote.common.config.CommonDBConstants;
import com.ibm.dsw.quote.common.domain.CoTermConfiguration;
import com.ibm.dsw.quote.common.domain.PartsPricingConfiguration;
import com.ibm.dsw.quote.common.domain.PartsPricingConfigurationFactory;
import com.ibm.dsw.quote.common.domain.ServiceDateModType;
import com.ibm.dsw.quote.log.util.TimeTracer;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;
import com.ibm.ead4j.topaz.common.jdbc.QueryContext;
import com.ibm.ead4j.topaz.exception.TopazException;

public class PartsPricingConfigurationFactory_jdbc extends PartsPricingConfigurationFactory{
	private static final LogContext logger = LogContextFactory.singleton().getLogContext();

	public List findPartsPricingConfiguration(String webQuoteNum) throws TopazException {
		TimeTracer tracer = TimeTracer.newInstance();

        List result = new ArrayList();
        HashMap params = new HashMap();
        params.put("piWebQuoteNum", webQuoteNum);
        ResultSet rsCA = null;
        ResultSet rs = null;
        try{
        	QueryContext context = QueryContext.getInstance();
            String sqlQuery = context.getCompletedQuery(CommonDBConstants.DB2_S_QT_GET_WEB_QUOTE_CONFIGRTN, null);
            logger.debug(this, LogHelper.logSPCall(sqlQuery, params));
            CallableStatement ps = TopazUtil.getConnection().prepareCall(sqlQuery);
            context.completeStatement(ps, CommonDBConstants.DB2_S_QT_GET_WEB_QUOTE_CONFIGRTN, params);

            tracer.stmtTraceStart("call DB2_S_QT_GET_WEB_QUOTE_CONFIGRTN");
            boolean psResult = ps.execute();
            tracer.stmtTraceEnd("call DB2_S_QT_GET_WEB_QUOTE_CONFIGRTN");


            if (psResult) {
            	 //get cursor crsrConfig to get configuration informations
            	 rs = ps.getResultSet();
            	 while (rs.next()) {
            		 PartsPricingConfiguration_jdbc confgrtn = new PartsPricingConfiguration_jdbc();
            		 confgrtn.webQuoteNum = webQuoteNum;
            		 confgrtn.configrtnId = StringUtils.trim(rs.getString("CONFIGRTN_ID"));
            		 confgrtn.configrtrConfigrtnId = StringUtils.trim(rs.getString("CONFIGRTR_CONFIGRTN_ID"));
            		 confgrtn.provisngDays = rs.getObject("ESTD_NUM_PROVISNG_DAYS")==null ? null : new Integer(rs.getInt("ESTD_NUM_PROVISNG_DAYS"));
            		 confgrtn.configrtnActionCode = StringUtils.trim(rs.getString("CONFIGRTN_ACTION_CODE"));
            		 confgrtn.ibmProdId = StringUtils.trim(rs.getString("IBM_PROD_ID"));
            		 confgrtn.ibmProdIdDscr = StringUtils.trim(rs.getString("IBM_PROD_ID_DSCR"));
            		 confgrtn.endDate = rs.getDate("END_DATE");
            		 confgrtn.cotermConfigrtnId = StringUtils.trim(rs.getString("COTERM_CONFIGRTN_ID"));
            		 confgrtn.configrtnModDate = rs.getDate("CONFIGRTN_MOD_DATE");
            		 confgrtn.provisngDaysDefault = rs.getObject("ESTD_PROVISNG_DAYS_DFLT")==null ? null : new Integer(rs.getInt("ESTD_PROVISNG_DAYS_DFLT"));
            		 confgrtn.configrtnOvrrdn = rs.getInt("CONFIGRTN_OVRRDN_FLAG") == 1;
            		 confgrtn.allowOvrrd = rs.getInt("ALLOW_OVRRD_FLAG") == 1;
            		 confgrtn.configrtnErrCode = StringUtils.trim(rs.getString("CONFIGRTN_ERR_CODE"));
            		 confgrtn.provisioningId = StringUtils.trim(rs.getString("PROVISNG_ID"));
            		 confgrtn.isProvisioningCopied = rs.getInt("PROVISNG_ID_COPY_FLAG") == 1;
            		 confgrtn.prodBrandCode = StringUtils.trim(rs.getString("PROD_BRAND_CODE"));
            		 confgrtn.prodBrandCodeDscr = StringUtils.trim(rs.getString("PROD_BRAND_CODE_DSCR"));
            		 confgrtn.configEntireExtended = rs.getInt("EXT_ENTIRE_CONFIGRTN_FLAG") == 1;
            		 confgrtn.increaseBidTCV = rs.getObject("TCV_NET_CHANGE")==null ? null : new Double(rs.getDouble("TCV_NET_CHANGE"));
            		 confgrtn.unusedBidTCV = rs.getObject("TCV_UNUSED")==null ? null : new Double(rs.getDouble("TCV_UNUSED"));
            		 String serviceDateModType = StringUtils.trim(rs.getString("SERVICE_DATE_MOD_TYPE"));
            		 if(StringUtils.isNotBlank(serviceDateModType)){
            			 confgrtn.serviceDateModType = ServiceDateModType.valueOf(serviceDateModType);
            		 }
            		 confgrtn.serviceDate = rs.getDate("SERVICE_DATE");
            		 confgrtn.termExtension = "1".equals(StringUtils.trim(rs.getString("TERM_EXTENSION_FLAG")));
            		 result.add(confgrtn);
            	 }
            	 //get cursor crsrCA to get charge agreement NUMs
            	 List chargeAgreementList = new ArrayList();
            	//store the coterm configurations <ca num,coterm config list>
            	 Map<String,List> cotermMap = new HashMap();
            	 if (ps.getMoreResults()) {
            		 Set caSet = new HashSet();
            		 Set ctConfigSet = new HashSet();
            		 rsCA = ps.getResultSet();
            		 while (rsCA.next()) {
            			 String chargeAgreementNum = StringUtils.trim(rsCA.getString("CHARGE_AGREEMENT_NUM"));
            			 String cotermConfigrtnId = StringUtils.trim(rsCA.getString("COTERM_CONFIGRTN_ID"));
            			 String ibmProdId = StringUtils.trim(rsCA.getString("IBM_PROD_ID"));
            			 String ibmProdIdDscr = StringUtils.trim(rsCA.getString("IBM_PROD_ID_DSCR"));
            			 Date endDate = rsCA.getDate("END_DATE");
            			 CoTermConfiguration cotermConfigrtn = new CoTermConfiguration(chargeAgreementNum,cotermConfigrtnId,ibmProdId,ibmProdIdDscr,endDate);
            			 caSet.add(chargeAgreementNum);
            			 ctConfigSet.add(cotermConfigrtn);

                	 }
            		 chargeAgreementList.addAll(caSet);
            		 for (Iterator iterator = chargeAgreementList.iterator(); iterator.hasNext();) {
            			 String caNum = (String) iterator.next();
            			 List ctConfigList = new ArrayList();
            			 for (Iterator iterator2 = ctConfigSet.iterator(); iterator2.hasNext();) {
            				 CoTermConfiguration ctConfig = (CoTermConfiguration) iterator2.next();
							 if(caNum != null && caNum.equals(ctConfig.getChargeAgreementNum())){
								 ctConfigList.add(ctConfig);
							 }
						}
            			 cotermMap.put(caNum, ctConfigList);
            		 }
            	 }
            	 for (Iterator iterator = result.iterator(); iterator.hasNext();) {
            		 PartsPricingConfiguration confgrtn = (PartsPricingConfiguration) iterator.next();
            		 confgrtn.setChargeAgreementList(chargeAgreementList);
            		 confgrtn.setCotermMap(cotermMap);
				 }
            }

        } catch (Exception e) {
            logger.error("Failed to get the web quote configurations from the database!", e);
            throw new TopazException(e);
        } finally{
        	try {
				if (null != rs && !rs.isClosed())
				{
					rs.close();
				}
			} catch (SQLException e) {
				logger.error("Failed to close the resultset!", e);
			}
			try {
				if (null != rsCA && !rsCA.isClosed())
				{
					rsCA.close();
				}
			} catch (SQLException e) {
				logger.error("Failed to close the resultset!", e);
			}
        	tracer.dump();
        }

        return result;
	}

}
