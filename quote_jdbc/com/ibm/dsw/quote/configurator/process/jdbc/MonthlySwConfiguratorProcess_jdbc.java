/**
 * 
 */
package com.ibm.dsw.quote.configurator.process.jdbc;

import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.ibm.dsw.quote.base.util.LogHelper;
import com.ibm.dsw.quote.base.util.TopazUtil;
import com.ibm.dsw.quote.common.config.CommonDBConstants;
import com.ibm.dsw.quote.common.exception.SPException;
import com.ibm.dsw.quote.configurator.config.ConfiguratorParamKeys;
import com.ibm.dsw.quote.configurator.domain.MonthlySwActionConfiguratorPart;
import com.ibm.dsw.quote.configurator.domain.MonthlySwConfiguratorPart;
import com.ibm.dsw.quote.configurator.domain.MonthlySwDailyConfiguratorPart;
import com.ibm.dsw.quote.configurator.domain.MonthlySwOnDemandConfiguratorPart;
import com.ibm.dsw.quote.configurator.domain.MonthlySwOverageConfiguratorPart;
import com.ibm.dsw.quote.configurator.domain.MonthlySwRampUpSubscriptionConfiguratorPart;
import com.ibm.dsw.quote.configurator.domain.MonthlySwSubscrptnConfiguratorPart;
import com.ibm.dsw.quote.configurator.process.MonthlySwConfiguratorJDBC;
import com.ibm.dsw.quote.log.util.TimeTracer;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;
import com.ibm.ead4j.topaz.common.jdbc.QueryContext;
import com.ibm.ead4j.topaz.exception.TopazException;
import com.ibm.ead4j.topaz.process.TopazTransactionalProcess;

/**
 * @ClassName: MonthlySwConfiguratorProcess_jdbc
 * @author Frank
 * @Description: TODO
 * @date Dec 23, 2013 2:02:29 PM
 *
 */
public abstract class MonthlySwConfiguratorProcess_jdbc extends TopazTransactionalProcess implements 
 MonthlySwConfiguratorJDBC{
	
	//protected abstract 
	protected static LogContext logger = LogContextFactory.singleton()
			.getLogContext();
	

	/**
	 * 
	 * @param rs
	 * @param monthlyPart
	 * @throws SQLException 
	 */
	protected MonthlySwConfiguratorPart setParentAttuibute(ResultSet rs,
			MonthlySwConfiguratorPart monthlyPart) throws SQLException {
		
		monthlyPart.setPartNum(StringUtils.trimToEmpty(rs.getString("part_num")));
		monthlyPart.setPartDscr(StringUtils.trimToEmpty(rs.getString("PART_DSCR_LONG")));
		monthlyPart.setSeqNum(StringUtils.trimToEmpty(rs.getString("QUOTE_LINE_ITEM_SEQ_NUM")));
		monthlyPart.setPartQty(rs.getObject("PART_QTY") == null ? null : new Integer(rs.getInt("PART_QTY")));
		
		monthlyPart.setPricingTierModel(StringUtils.trimToEmpty(rs.getString("PRICNG_TIER_MDL")));
		
		String ptqm = StringUtils.trimToEmpty(rs.getString("PRICNG_TIER_QTY_MESUR"));
		if(StringUtils.isNotBlank(ptqm))
			monthlyPart.setTierQtyMeasre(StringUtils.isNumeric(ptqm)?  new Integer(ptqm) : null);
		else
			monthlyPart.setTierQtyMeasre(null);
		
		//subId
		//monthlyPart.setSubId(rs.getString(""));
		//monthlyPart.setSubIdDscr(StringUtils.trimToEmpty(rs.getString("SW_SBSCRPTN_ID_DSCR")));
		
		//product
		monthlyPart.setPid(StringUtils.trimToEmpty(rs.getString("ibm_prod_id")));
		monthlyPart.setPidDscr(StringUtils.trimToEmpty(rs.getString("IBM_PROD_ID_DSCR")));
		
		//brand
		monthlyPart.setBrandId(StringUtils.trimToEmpty(rs.getString("sw_prod_brand_code")));
		monthlyPart.setBrandDscr(StringUtils.trimToEmpty(rs.getString("SW_PROD_BRAND_CODE_DSCR")));
		
		//wwideProdCodeDscr
		monthlyPart.setWwideProdCodeDscr(StringUtils.trimToEmpty(rs.getString("WWIDE_PROD_CODE_DSCR")));
		
		//get all price , then in action will get real price per lob.
		monthlyPart.setPrice(rs.getObject("SRP_PRICE")==null ? null: new Double(rs.getDouble("SRP_PRICE")));
		monthlyPart.setSvpLevelA(rs.getObject("SVP_LEVEL_A")==null ? null: new Double(rs.getDouble("SVP_LEVEL_A")));
		monthlyPart.setSvpLevelB(rs.getObject("SVP_LEVEL_B")==null ? null: new Double(rs.getDouble("SVP_LEVEL_B")));
		monthlyPart.setSvpLevelD(rs.getObject("SVP_LEVEL_D")==null ? null: new Double(rs.getDouble("SVP_LEVEL_D")));
		monthlyPart.setSvpLevelE(rs.getObject("SVP_LEVEL_E")==null ? null: new Double(rs.getDouble("SVP_LEVEL_E")));
		monthlyPart.setSvpLevelF(rs.getObject("SVP_LEVEL_F")==null ? null: new Double(rs.getDouble("SVP_LEVEL_F")));
		monthlyPart.setSvpLevelG(rs.getObject("SVP_LEVEL_G")==null ? null: new Double(rs.getDouble("SVP_LEVEL_G")));
		monthlyPart.setSvpLevelH(rs.getObject("SVP_LEVEL_H")==null ? null: new Double(rs.getDouble("SVP_LEVEL_H")));
		monthlyPart.setSvpLevelI(rs.getObject("SVP_LEVEL_I")==null ? null: new Double(rs.getDouble("SVP_LEVEL_I")));
		monthlyPart.setSvpLevelJ(rs.getObject("SVP_LEVEL_J")==null ? null: new Double(rs.getDouble("SVP_LEVEL_J")));
		monthlyPart.setSvpLevelED(rs.getObject("SVP_LEVEL_ED")==null ? null: new Double(rs.getDouble("SVP_LEVEL_ED")));
		monthlyPart.setSvpLevelGV(rs.getObject("SVP_LEVEL_GV")==null ? null: new Double(rs.getDouble("SVP_LEVEL_GV")));
		
		monthlyPart.setTerm(rs.getObject("CVRAGE_TERM") == null ? null : new Integer(rs.getInt("CVRAGE_TERM")));
		
		return monthlyPart;
		
	}
	
	
	
	
	/**
	 * 
	 * @param rs
	 * @return
	 * @throws SQLException
	 */
	protected MonthlySwConfiguratorPart processSubscrptnPart(ResultSet rs,
			Map<String, MonthlySwConfiguratorPart> configuratorPartMap,
			String subId) throws SQLException {
		MonthlySwSubscrptnConfiguratorPart subscrptnPart = new MonthlySwSubscrptnConfiguratorPart();

		subscrptnPart.setPublshdPriceDurtnCode(StringUtils.trimToEmpty(rs
				.getString("PUBLSHD_PRICE_DURTN_CODE")));
		subscrptnPart.setPublshdPriceDurtnCodeDscr(StringUtils.trimToEmpty(rs
				.getString("PUBLSHD_PRICE_DURTN_CODE_DSCR")));
		subscrptnPart.setBillingFrequencyCode(StringUtils.trimToEmpty(rs
				.getString("BILLG_FRQNCY_CODE")));

		// subscrptnPart.setTerm(rs.getInt(" "));

		// set related overage part and daily part
		MonthlySwConfiguratorPart configuratorPart = configuratorPartMap
				.get(subId);

		if (configuratorPart != null) {

			if (configuratorPart instanceof MonthlySwOverageConfiguratorPart) {

				MonthlySwOverageConfiguratorPart overagePart = (MonthlySwOverageConfiguratorPart) configuratorPart;
				// set ovearage part
				subscrptnPart.setOveargePart(overagePart);

				// set configurator part
				overagePart.setSubscrptnPart(subscrptnPart);

				// set daily part
				if (overagePart.getDailyPart() != null) {
					overagePart.getDailyPart().setSubscrptnPart(subscrptnPart);
					subscrptnPart.setDailyPart(overagePart.getDailyPart());
				}

			} else if (configuratorPart instanceof MonthlySwDailyConfiguratorPart) {

				MonthlySwDailyConfiguratorPart dailyPart = (MonthlySwDailyConfiguratorPart) configuratorPart;

				// set daily part
				subscrptnPart.setDailyPart(dailyPart);

				// set configurator part
				dailyPart.setSubscrptnPart(subscrptnPart);

				// set oveage part
				if (dailyPart.getOveargePart() != null) {
					dailyPart.getOveargePart().setSubscrptnPart(subscrptnPart);
					subscrptnPart.setOveargePart(dailyPart.getOveargePart());
				}

			}

		}

		return subscrptnPart;
	}

	/**
	 * 
	 * @param rs
	 * @return
	 * @throws SQLException
	 */
	protected MonthlySwConfiguratorPart processOveragePart(ResultSet rs,
			Map<String, MonthlySwConfiguratorPart> configuratorPartMap,
			String subId) throws SQLException {

		MonthlySwOverageConfiguratorPart overagepart = new MonthlySwOverageConfiguratorPart();

		// set attribute

		// set related overage part and daily part
		MonthlySwConfiguratorPart configuratorPart = configuratorPartMap
				.get(subId);

		if (configuratorPart != null) {

			if (configuratorPart instanceof MonthlySwSubscrptnConfiguratorPart) {

				MonthlySwSubscrptnConfiguratorPart subscrptnPart = (MonthlySwSubscrptnConfiguratorPart) configuratorPart;

				// set configurator part
				overagepart.setSubscrptnPart(subscrptnPart);

				// set ovearage part
				subscrptnPart.setOveargePart(overagepart);

				// set daily part
				if (subscrptnPart.getDailyPart() != null) {
					overagepart.setDailyPart(subscrptnPart.getDailyPart());
					subscrptnPart.getDailyPart().setOveargePart(overagepart);
				}

			} else if (configuratorPart instanceof MonthlySwDailyConfiguratorPart) {

				MonthlySwDailyConfiguratorPart dailyPart = (MonthlySwDailyConfiguratorPart) configuratorPart;

				// set daily part
				overagepart.setDailyPart(dailyPart);

				// set ovearege part
				dailyPart.setOveargePart(overagepart);

				// set subscription part
				if (dailyPart.getSubscrptnPart() != null) {
					overagepart.setSubscrptnPart(dailyPart.getSubscrptnPart());
					dailyPart.getSubscrptnPart().setOveargePart(overagepart);
				}

			}

		}

		return overagepart;

	}
		
	/**
	 * 
	 * @param rs
	 * @return
	 * @throws SQLException
	 */
	protected MonthlySwConfiguratorPart processDailyPart(ResultSet rs,
			Map<String, MonthlySwConfiguratorPart> configuratorPartMap,

			String subId) throws SQLException {

		MonthlySwDailyConfiguratorPart dailyPart = new MonthlySwDailyConfiguratorPart();

		// set attribute

		// set related overage part and daily part
		MonthlySwConfiguratorPart configuratorPart = configuratorPartMap
				.get(subId);

		if (configuratorPart != null) {

			if (configuratorPart instanceof MonthlySwSubscrptnConfiguratorPart) {
				MonthlySwSubscrptnConfiguratorPart subscrptnPart = (MonthlySwSubscrptnConfiguratorPart) configuratorPart;
				// set configurator part
				dailyPart.setSubscrptnPart(subscrptnPart);

				// set daily part
				subscrptnPart.setDailyPart(dailyPart);

				// set overage part
				if (subscrptnPart.getOveargePart() != null) {
					dailyPart.setOveargePart(subscrptnPart.getOveargePart());
					subscrptnPart.getOveargePart().setDailyPart(dailyPart);
				}

			} else if (configuratorPart instanceof MonthlySwOverageConfiguratorPart) {
				MonthlySwOverageConfiguratorPart oveargePart = (MonthlySwOverageConfiguratorPart) configuratorPart;
				// set daily part
				oveargePart.setDailyPart(dailyPart);

				// set ovearege part
				dailyPart.setOveargePart(oveargePart);

				// set subscription part
				if (oveargePart.getSubscrptnPart() != null) {
					dailyPart.setSubscrptnPart(oveargePart.getSubscrptnPart());
					oveargePart.getSubscrptnPart().setDailyPart(dailyPart);
				}

			}

		}

		return dailyPart;

	}
		
	/**
	 * 
	 * @param rs
	 * @return
	 * @throws SQLException
	 */
	protected MonthlySwConfiguratorPart processOnDemandPart(ResultSet rs)
			throws SQLException {

		MonthlySwOnDemandConfiguratorPart onDemandPart = new MonthlySwOnDemandConfiguratorPart();
		// set attribute
		return onDemandPart;
	}
	
	
	protected abstract void setMonthlySwActionPart(ResultSet rs ,MonthlySwConfiguratorPart monthlyPart) throws SQLException ;
	
		
	protected void setMonthlySwActionPartAttribute(ResultSet rs ,MonthlySwActionConfiguratorPart actionconfiguratorPart) throws SQLException{
		actionconfiguratorPart.setBillgAnlFlag(StringUtils.trimToEmpty(rs.getString("BILLG_ANL_FLAG")));
		actionconfiguratorPart.setBillgMthlyFlag(StringUtils.trimToEmpty(rs.getString("BILLG_MTHLY_FLAG")));
		actionconfiguratorPart.setBillgQtrlyFlag(StringUtils.trimToEmpty(rs.getString("BILLG_QTRLY_FLAG")));
		actionconfiguratorPart.setBillgUpfrntFlag(StringUtils.trimToEmpty(rs.getString("BILLG_UPFRNT_FLAG")));
		actionconfiguratorPart.setBillgEvtFlag(StringUtils.trimToEmpty(rs.getString("BILLG_EVENT_FLAG"))); 
	}

    /**
     * DOC Comment method "processRampUpPart".
     * 
     * @param rs
     * @param configuratorPartMap
     * @param subId
     * @return
     */
    protected MonthlySwConfiguratorPart processRampUpPart(ResultSet rs,
            Map<String, MonthlySwConfiguratorPart> configuratorPartMap, String subId) {
        MonthlySwRampUpSubscriptionConfiguratorPart rampUpConfiguratorPart = new MonthlySwRampUpSubscriptionConfiguratorPart();
        // set related overage part and daily part
        MonthlySwConfiguratorPart configuratorPart = configuratorPartMap.get(subId);

        if (configuratorPart != null) {
            if (configuratorPart instanceof MonthlySwSubscrptnConfiguratorPart) {
                MonthlySwSubscrptnConfiguratorPart subscrptnPart = (MonthlySwSubscrptnConfiguratorPart) configuratorPart;
                rampUpConfiguratorPart.setSubscriptnPart(subscrptnPart);
                subscrptnPart.getRampUpParts().add(rampUpConfiguratorPart);
            } else if (configuratorPart instanceof MonthlySwRampUpSubscriptionConfiguratorPart) {
                MonthlySwRampUpSubscriptionConfiguratorPart previousRampUpConfiguratorPart = (MonthlySwRampUpSubscriptionConfiguratorPart) configuratorPart;
                MonthlySwSubscrptnConfiguratorPart subscrptnPart = previousRampUpConfiguratorPart.getSubscriptnPart();
                rampUpConfiguratorPart.setSubscriptnPart(subscrptnPart);
                subscrptnPart.getRampUpParts().add(rampUpConfiguratorPart);
            }
        }
        return rampUpConfiguratorPart;
    }

    /**
     * DOC Comment method "processPartsBaseOnType".
     * 
     * @param rs
     * @param masterMonthlySwParts
     * @param configuratorPartMap
     * @param subId
     * @param isSubscrtnPart
     * @param isOveragePart
     * @param isDailyPart
     * @param isOnDemand
     * @param isRampUp
     * @param monthlyPart
     * @throws SQLException
     */
    protected void processPartsBaseOnType(ResultSet rs, List<MonthlySwConfiguratorPart> masterMonthlySwParts,
            Map<String, MonthlySwConfiguratorPart> configuratorPartMap, String subId, boolean isSubscrtnPart,
            boolean isOveragePart, boolean isDailyPart, boolean isOnDemand, boolean isRampUp,
            MonthlySwConfiguratorPart monthlyPart) throws SQLException {
        // process subscription part
        if (isSubscrtnPart && !isRampUp) {
            monthlyPart = this.processSubscrptnPart(rs, configuratorPartMap, subId);
        } else if (isOveragePart && !isRampUp) {
            monthlyPart = this.processOveragePart(rs, configuratorPartMap, subId);
        } else if (isDailyPart) {
            monthlyPart = this.processDailyPart(rs, configuratorPartMap, subId);
        } else if (isOnDemand) {
            monthlyPart = this.processOnDemandPart(rs);
        } else if (isSubscrtnPart && isRampUp) {
            monthlyPart = this.processRampUpPart(rs, configuratorPartMap, subId);
        } else {
            monthlyPart = new MonthlySwConfiguratorPart();
        }

        if ((isSubscrtnPart || isOveragePart || isDailyPart) && !isRampUp) {
            configuratorPartMap.put(subId, monthlyPart);
        }

        // set parent attribute
        monthlyPart = setParentAttuibute(rs, monthlyPart);

        // set configurator action part
        this.setMonthlySwActionPart(rs, monthlyPart);

        // set master parts to list
        if ((isSubscrtnPart || isOnDemand) && !isRampUp) {
            masterMonthlySwParts.add(monthlyPart);
        }
    }
    
    public Map<String,String> searchRestrictedMonthlyParts(String searchString,String webQuoteNum) throws TopazException {
    	 TimeTracer tracer = TimeTracer.newInstance();
         // call sp
         HashMap<String, String> params = new HashMap<String, String>();
         params.put("piWebQuoteNum", webQuoteNum);
         params.put("piPartNumList", searchString);
         
         LogContext logger = LogContextFactory.singleton().getLogContext();
         ResultSet rs = null;
         
         try {
        	 QueryContext context = QueryContext.getInstance();
        	 
        	 String sqlQuery = context.getCompletedQuery(CommonDBConstants.DB2_S_QT_SEARCH_MONTHLY_RTRCT_PARTS, null);
             logger.debug(this, LogHelper.logSPCall(sqlQuery, params));
             CallableStatement ps = TopazUtil.getConnection().prepareCall(sqlQuery);
             context.completeStatement(ps, CommonDBConstants.DB2_S_QT_SEARCH_MONTHLY_RTRCT_PARTS, params);
             
             tracer.stmtTraceStart("call S_QT_SEARCH_MONTHLY_RTRCT_PARTS");           
             boolean psResult = ps.execute();
             tracer.stmtTraceEnd("call S_QT_SEARCH_MONTHLY_RTRCT_PARTS");
             
             int returnCode = ps.getInt(1);
             logger.debug(this, "the return code of calling " + sqlQuery + " is: " + returnCode);
             if (returnCode != CommonDBConstants.DB2_SP_RETURN_SIGN_SUCCESS) {
 				throw new SPException(returnCode);
 			 }
             
             String notFoundString = "";
             String searchOutString = "";
             
             rs = ps.getResultSet();
             if (rs!=null){
            	 while (rs.next()){
            		 String currentPartNum=rs.getString("part_num").trim();
            		 if(StringUtils.contains(searchString, currentPartNum)){
            			 searchOutString="".equals(searchOutString)?currentPartNum:searchOutString+","+currentPartNum;
            		 } 
            	 }
             }
             
             String[] strArry=searchString.split(",");
             for(int i=0;i<strArry.length;i++){
            	 if(!StringUtils.contains(searchOutString, strArry[i])){
            		 notFoundString="".equals(notFoundString)? strArry[i]:notFoundString+","+ strArry[i];
            	 }
             }
             
             Map<String, String> map =new HashMap<String, String>();
             map.put(ConfiguratorParamKeys.notFoundRestrictedPartList, notFoundString);
             map.put(ConfiguratorParamKeys.neededProcessRestrictedPartList, searchOutString);
   
             return map;    
         } catch (SQLException sqle) {
             logger.error("Failed to get the configurator parts from the database!", sqle);
             throw new TopazException(sqle);
         } finally {
             try {
                 if (null != rs && !rs.isClosed()) {
                     rs.close();
                 }
             } catch (SQLException e) {
                 logger.error("Failed to close the resultset!", e);
             }

             tracer.dump();
         }
    }
	
}
