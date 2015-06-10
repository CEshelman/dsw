package com.ibm.dsw.quote.common.domain.jdbc;

import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.ibm.dsw.quote.base.util.LogHelper;
import com.ibm.dsw.quote.base.util.TopazUtil;
import com.ibm.dsw.quote.common.config.CommonDBConstants;
import com.ibm.dsw.quote.common.domain.MonthlySoftwareConfiguration;
import com.ibm.dsw.quote.common.domain.MonthlySoftwareConfigurationFactory;
import com.ibm.dsw.quote.common.domain.MonthlySwBrand;
import com.ibm.dsw.quote.common.domain.ServiceDateModType;
import com.ibm.dsw.quote.log.util.TimeTracer;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;
import com.ibm.ead4j.topaz.common.jdbc.QueryContext;
import com.ibm.ead4j.topaz.exception.TopazException;

public class MonthlySoftwareConfigurationFactory_jdbc extends MonthlySoftwareConfigurationFactory {

	private static final LogContext logger = LogContextFactory.singleton().getLogContext();
	
	@Override
	public List<MonthlySoftwareConfiguration> findMonthlySwConfiguration(String webQuoteNum)
			throws TopazException {
		TimeTracer tracer = TimeTracer.newInstance();

        List<MonthlySoftwareConfiguration> result = new ArrayList();
        Map<String,MonthlySoftwareConfiguration> monthlySwConfgrtnMap = new HashMap();
        HashMap params = new HashMap();
        params.put("piWebQuoteNum", webQuoteNum);
        ResultSet rs = null;
        try{
        	QueryContext context = QueryContext.getInstance();
            String sqlQuery = context.getCompletedQuery(CommonDBConstants.DB2_S_QT_GET_MONTHLY_SW_CONFGRTN, null);
            logger.debug(this, LogHelper.logSPCall(sqlQuery, params));
            CallableStatement ps = TopazUtil.getConnection().prepareCall(sqlQuery);
            context.completeStatement(ps, CommonDBConstants.DB2_S_QT_GET_MONTHLY_SW_CONFGRTN, params);

            tracer.stmtTraceStart("call DB2_S_QT_GET_MONTHLY_SW_CONFGRTN");
            boolean psResult = ps.execute();
            tracer.stmtTraceEnd("call DB2_S_QT_GET_MONTHLY_SW_CONFGRTN");


            if (psResult) {
            	 //get cursor crsrConfig to get configuration informations
            	 rs = ps.getResultSet();
            	 while (rs.next()) {
            		 MonthlySoftwareConfiguration_jdbc confgrtn = new MonthlySoftwareConfiguration_jdbc();
            		 confgrtn.webQuoteNum = webQuoteNum;
            		 confgrtn.configrtnId = StringUtils.trim(rs.getString("CONFIGRTN_ID"));
            		 confgrtn.configrtnActionCode = StringUtils.trim(rs.getString("CONFIGRTN_ACTION_CODE"));
            		 confgrtn.endDate = rs.getDate("END_DATE");
            		 confgrtn.cotermConfigrtnId = StringUtils.trim(rs.getString("COTERM_CONFIGRTN_ID"));
            		 String serviceDateModType = StringUtils.trim(rs.getString("SERVICE_DATE_MOD_TYPE"));
            		 if(StringUtils.isNotBlank(serviceDateModType)){
            			 confgrtn.serviceDateModType = ServiceDateModType.valueOf(serviceDateModType);
            		 }
            		 confgrtn.serviceDate = rs.getDate("SERVICE_DATE");
            		 confgrtn.termExtension = "1".equals(StringUtils.trim(rs.getString("TERM_EXTENSION_FLAG")));
            		 confgrtn.configEntireExtended = rs.getInt("EXT_ENTIRE_CONFIGRTN_FLAG") == 1;
            		 confgrtn.globalTerm = rs.getObject("GLOBAL_TERM") == null ? null : new Integer(rs.getInt("GLOBAL_TERM"));
            		 confgrtn.needReconfigFlag = "1".equals(StringUtils.trim(rs.getString("NEED_RECONFIG_FLAG")));
            		 confgrtn.globalBillingFrequencyCode = StringUtils.trim(rs.getString("GLOBAL_BILLG_FRQNCY_CODE"));
            		 confgrtn.addNewMonthlySWFlag = "1".equals(StringUtils.trim(rs.getString("IS_ADD_NEW_MONTHLY_SW_FLAG")));
            		 confgrtn.chrgAgrmtNum =StringUtils.trim(rs.getString("CA_NUM"));
					confgrtn.configrtnIdFromCa = StringUtils.trim(rs.getString("CONFIGRTN_ID_FRM_CA"));
            		 result.add(confgrtn);
            		 monthlySwConfgrtnMap.put(confgrtn.configrtnId, confgrtn);
            	 }
            	 
            	 
            	// below two cursor are conditionally opened
                 while(ps.getMoreResults()){
                     rs = ps.getResultSet();

                     //we can't access resultset again when all rows are consumed
                     if(rs.next()){
                         String cursorName = rs.getString(1);
                         if ("SW_CONFIGRTN_BRAND_CRSR".equalsIgnoreCase(cursorName)){
                        	 processMonthlySwBrands(rs,monthlySwConfgrtnMap);
                         }
                     }
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
				if (null != rs && !rs.isClosed())
				{
					rs.close();
				}
			} catch (SQLException e) {
				logger.error("Failed to close the resultset!", e);
			}
        	tracer.dump();
        }

        return result;
	}
	
    private void processMonthlySwBrands(ResultSet rs,Map monthlySwConfgrtnMap) throws TopazException{
        LogContext logger = LogContextFactory.singleton().getLogContext();
        try{
	        do {
	            String confgrtnId = StringUtils.trim(rs.getString("CONFIGRTN_ID"));
	            MonthlySoftwareConfiguration_jdbc confgrtn = (MonthlySoftwareConfiguration_jdbc) monthlySwConfgrtnMap.get(confgrtnId);
	            if (null != confgrtn) {
	            	MonthlySwBrand swBrand = new MonthlySwBrand();
	            	swBrand.setBrandCode(StringUtils.trim(rs.getString("PROD_BRAND_CODE")));
	            	swBrand.setBrandCodeDesc(StringUtils.trim(rs.getString("PROD_BRAND_CODE_DSCR")));
	            	confgrtn.getBrandsList().add(swBrand);
	            }
	        } while(rs.next());
        }catch(Exception e){
            logger.error("Failed to get renewal quote infomation",e);
            throw new TopazException(e);
        }
    }

	/* (non-Javadoc)
	 * @see com.ibm.dsw.quote.common.domain.MonthlySoftwareConfigurationFactory#createMonthlyConfiguration(java.lang.String)
	 */
	@Override
	public MonthlySoftwareConfiguration createMonthlyConfiguration(String userId)
			throws TopazException {
		
		MonthlySoftwareConfiguration_jdbc monthlySwConfigratorJdbc = new MonthlySoftwareConfiguration_jdbc();
		monthlySwConfigratorJdbc.isNew(true);
		monthlySwConfigratorJdbc.setUserID(userId);
		return monthlySwConfigratorJdbc;
	}
    

}
