package com.ibm.dsw.quote.scw.addon.process.jdbc;

import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import org.apache.commons.lang.StringUtils;

import com.ibm.dsw.quote.base.config.I18NBundleNames;
import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.base.util.LocaleHelperImpl;
import com.ibm.dsw.quote.base.util.LogHelper;
import com.ibm.dsw.quote.base.util.LogThrowableUtil;
import com.ibm.dsw.quote.base.util.TopazUtil;
import com.ibm.dsw.quote.common.config.CommonDBConstants;
import com.ibm.dsw.quote.common.exception.NoDataException;
import com.ibm.dsw.quote.common.exception.SPException;
import com.ibm.dsw.quote.configurator.config.ConfiguratorViewKeys;
import com.ibm.dsw.quote.configurator.contract.AddOrUpdateConfigurationContract;
import com.ibm.dsw.quote.configurator.process.SaasConfiguratorProcess;
import com.ibm.dsw.quote.configurator.process.SaasConfiguratorProcessFactory;
import com.ibm.dsw.quote.log.util.TimeTracer;
import com.ibm.dsw.quote.scw.addon.ScwAddOnTradeUpResult;
import com.ibm.dsw.quote.scw.addon.ScwAddonTradeUpErrorCode;
import com.ibm.dsw.quote.scw.addon.domain.AddOnTradeUpConfiguration;
import com.ibm.dsw.quote.scw.addon.domain.AddOnTradeUpInfo;
import com.ibm.dsw.quote.scw.addon.domain.AddOnTradeUpLineItem;
import com.ibm.dsw.quote.scw.addon.domain.RetrieveLineItem;
import com.ibm.dsw.quote.scw.addon.domain.RetrieveQuote;
import com.ibm.dsw.quote.scw.addon.domain.RetrieveQuoteHeader;
import com.ibm.dsw.quote.scw.addon.process.ScwAddonTradeupProcess_impl;
import com.ibm.dsw.quote.scw.addon.process.convert.ScwAddonTradeupInputParamConvertProcess;
import com.ibm.dsw.quote.scw.addon.process.convert.ScwAddonTradeupInputParamConvertProcess_Impl;
import com.ibm.dsw.quote.scw.addon.util.ScwAddonUtil;
import com.ibm.dsw.quote.scw.config.ScwDBConstants;
import com.ibm.ead4j.jade.config.ApplicationContextFactory;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;
import com.ibm.ead4j.topaz.common.jdbc.QueryContext;
import com.ibm.ead4j.topaz.exception.TopazException;

public class ScwAddonTradeupProcess_jdbc extends ScwAddonTradeupProcess_impl {

	private LogContext logger = LogContextFactory.singleton().getLogContext();

	@Override
	public ScwAddOnTradeUpResult doScwAddOnTradeUp(
			AddOnTradeUpInfo addOnTradeUpInfo) throws TopazException,
			QuoteException {		
		// 1. basic validate target to convert from configuration to contract
		// correctly
		validateAddOnTradeUpInfo(addOnTradeUpInfo);
		validateBasicInputParams(addOnTradeUpInfo);
		if (!scwAddOnTradeUpResult.isSuccessful()) {
			return scwAddOnTradeUpResult;
		}			

		// 2. convert from configuration to contract
		ScwAddonTradeupInputParamConvertProcess convertProcess = new ScwAddonTradeupInputParamConvertProcess_Impl();
		List<AddOrUpdateConfigurationContract> contracList = convertProcess
				.convert(addOnTradeUpInfo);
		// 3. validate contract
		for (AddOrUpdateConfigurationContract contract : contracList) {
			validateInputParams(contract);
		}
		// 4. add-on trade-up for every contract
		if (scwAddOnTradeUpResult.isSuccessful()) {
			for (AddOrUpdateConfigurationContract contract : contracList) {
				SaasConfiguratorProcess saasConfiguratorProcess = SaasConfiguratorProcessFactory
						.singleton().create(contract.getConfigrtnActionCode());
				saasConfiguratorProcess.addSaasPartsToQuote(contract);
			}
		}
		return scwAddOnTradeUpResult;
	}
 
	@Override
	public void validateBasicInputParams(AddOnTradeUpInfo addOnTradeUpInfo)
			throws TopazException {
		String caNum = addOnTradeUpInfo.getChargeAgreementNumber();
		if(StringUtils.isBlank(caNum)){
			return;
		}
		StringBuffer configIdList = new StringBuffer();
		StringBuffer partAndConfigList=new StringBuffer();
		String colonSep=":";
		String sep = "-";
		// compute configuration id needed validate in database , seperated with '-'
		for (AddOnTradeUpConfiguration config : addOnTradeUpInfo
				.getConfigurations()) {
			if (AddOnTradeUpConfiguration.CONFIGCODE_U.equals(config
					.getUpdateCAConfigCode())) {
				configIdList.append(config.getConfigId());
				configIdList.append(sep);
			}
		}
		// validate end date of coterm configuration cann't be null
		StringBuffer cotermConfigIdList=new StringBuffer();
		for (AddOnTradeUpConfiguration config : addOnTradeUpInfo
				.getConfigurations()) {
			if(1==config.getCoTermFlag()){
				cotermConfigIdList.append(config.getRefConfigId());
				cotermConfigIdList.append(sep);
			}			
		}
		//validate whether the Part is  related to PID
		for (AddOnTradeUpConfiguration config : addOnTradeUpInfo
				.getConfigurations()) {
				partAndConfigList.append(config.getConfigId());
				partAndConfigList.append(colonSep);
				for(AddOnTradeUpLineItem item:config.getLineItems()){
					partAndConfigList.append(item.getPartNumber());
					partAndConfigList.append(colonSep);
				}
				partAndConfigList.append(sep);
		}
		LogContext logContext = LogContextFactory.singleton().getLogContext();
		List<ScwAddonTradeUpErrorCode> result = new ArrayList<ScwAddonTradeUpErrorCode>();
		try {
			HashMap params = new HashMap();
			params.put("piCaNum", caNum);
			params.put("piConfigrtnIdList", configIdList.toString());
			params.put("cotermConfigIdList", cotermConfigIdList.toString());
			params.put("partNumList", partAndConfigList.toString());
			this.beginTransaction();
			QueryContext queryCtx = QueryContext.getInstance();
			String sqlQuery = queryCtx.getCompletedQuery(
					ScwDBConstants.S_QT_VALIDATE_SCW_ADDON, null);
			logContext.debug(this, LogHelper.logSPCall(sqlQuery, params));
			CallableStatement statement = TopazUtil.getConnection()
					.prepareCall(sqlQuery);
			queryCtx.completeStatement(statement,
					ScwDBConstants.S_QT_VALIDATE_SCW_ADDON, params);
			statement.execute();
			int poGenStatus = statement.getInt(5);			
			if (poGenStatus == CommonDBConstants.DB2_SP_RETURN_SIGN_NODATA) {
				throw new NoDataException();
			} else if (poGenStatus != CommonDBConstants.DB2_SP_RETURN_SIGN_SUCCESS) {
				throw new SPException(LogHelper.logSPCall(sqlQuery, params),
						poGenStatus);
			}
			String errorMsg = statement.getString(6);
			this.commitTransaction();
			logContext.debug(logContext, "errorMsg " + errorMsg);
			generateAddOnTradeUpError(errorMsg, result);
			if (result != null && result.size() > 0) {
				this.scwAddOnTradeUpResult.addNewErrorCodeList(result);
			}
		}catch (Exception tce) {
			logContext.error(this, LogThrowableUtil.getStackTraceContent(tce));
			throw new TopazException("Exception when validate scw addon param , chargeAgreementNum ["
					+ caNum + "] confId [" + configIdList + "]",tce);
		} finally {
			this.rollbackTransaction();			
		}
	}

	protected void validateAddOnTradeUpInfo(AddOnTradeUpInfo addOnInfo)  {
		String chargeAgreementNumber = addOnInfo.getChargeAgreementNumber();
		if (StringUtils.isBlank(chargeAgreementNumber)) {
			scwAddOnTradeUpResult
					.addNewErrorCode(ScwAddonUtil
							.populateScwErrorBean(ConfiguratorViewKeys.CA_NUMBER_NOT_NULL));
		}
		List<AddOnTradeUpConfiguration> configList = addOnInfo
				.getConfigurations();
		if (configList == null || configList.size() == 0) {
			scwAddOnTradeUpResult
					.addNewErrorCode(ScwAddonUtil
							.populateScwErrorBean(ConfiguratorViewKeys.CONFIG_LIST_NOT_NULL));
		} else {
			for (AddOnTradeUpConfiguration config : configList) {
				validateConfig(config);
			}
		}
		
	}

	private void validateConfig(AddOnTradeUpConfiguration config) {
		String configId = config.getConfigId();
		if (StringUtils.isBlank(configId)) {
			scwAddOnTradeUpResult
					.addNewErrorCode(ScwAddonUtil
							.populateScwErrorBean(ConfiguratorViewKeys.CONFIG_ID_NOT_NULL));
		}
		String term = config.getTermLength();
		if (StringUtils.isBlank(term)) {
			scwAddOnTradeUpResult.addNewErrorCode(ScwAddonUtil
					.populateScwErrorBean(ConfiguratorViewKeys.TERM_NOT_NULL,
							configId));
		} else {
			if (!term.matches("\\d*")) {
				scwAddOnTradeUpResult.addNewErrorCode(ScwAddonUtil
						.populateScwErrorBean(
								ConfiguratorViewKeys.TERM_MUST_BE_NUMBER,
								configId));
			}
		}
		String configCode = config.getUpdateCAConfigCode();
		if (!(AddOnTradeUpConfiguration.CONFIGCODE_A.equals(configCode) || AddOnTradeUpConfiguration.CONFIGCODE_U
				.equals(configCode))) {
			scwAddOnTradeUpResult.addNewErrorCode(ScwAddonUtil
					.populateScwErrorBean(
							ConfiguratorViewKeys.UPDATE_CONFIG_CODE_ERROR,
							configId));
		}
		List<AddOnTradeUpLineItem> items = config.getLineItems();
		if (items == null || items.size() == 0) {
			scwAddOnTradeUpResult.addNewErrorCode(ScwAddonUtil
					.populateScwErrorBean(ConfiguratorViewKeys.ITEMS_NOT_NULL,
							configId));
		} else {
			for (AddOnTradeUpLineItem item : items)
				validateLineItem(item);
		}
	}

	private void validateLineItem(AddOnTradeUpLineItem item) {

	}

	/**
	 * compute ScwAddonTradeUpErrorCode List from error message
	 * 
	 * @param msgs
	 *        <pre>
	 *            format:ERR0001-ERR0002,configId1-ERR0023,productId2-ERR0002,configId2-ERR0023,productId2-ERR0024,productId3
	 *            only ERR0002 ERR0023 ERR0024 have	id info
	 *         </pre>
	 * @param errorList 
	 */
	private void generateAddOnTradeUpError(String msgs,
			List<ScwAddonTradeUpErrorCode> errorList) {
		String splitMsg = "-", errorCode, id;
		String[] oneError = new String[2];
		String errorMsg;
		Locale locale = LocaleHelperImpl.getDefaultDSWLocale();
		ScwAddonTradeUpErrorCode errorBean;
		// msgs : include all error message
		String msgsArray[] = StringUtils.split(msgs, splitMsg);
		// errorCodeAndInfo : one error info 
		for (String errorCodeAndInfo : msgsArray) {
			if (StringUtils.isNotBlank(errorCodeAndInfo)) {
				oneError = StringUtils.split(errorCodeAndInfo, ",");
				errorCode = StringUtils.trim(oneError[0]);				
				// get errorMsg from error code
				errorMsg = ApplicationContextFactory
						.singleton()
						.getApplicationContext()
						.getI18nValueAsString(
								I18NBundleNames.SCW_ADDON_MESSAGE, locale,
								errorCode);
				//id: maybe configuration id or product id 
				id = "";
				// get  id
				if (oneError.length > 1) {
					//get product id and configuration id and add it to error description
					id = StringUtils.trim(oneError[1]);
					errorMsg = id + splitMsg + errorMsg;
				}
				if (StringUtils.isNotBlank(errorMsg)) {
					errorBean = new ScwAddonTradeUpErrorCode();
					errorBean.setErrorCode(errorCode);
					errorBean.setErrorCodeDesc(errorMsg);
					errorList.add(errorBean);
				}
			}
		}
	}
	@Override
	public RetrieveQuote enhanceRetrieveQuote(String chargeAgreementNum,
			String webQuoteNum, RetrieveQuote retrieveQuote)
			throws TopazException {
		HashMap params = new HashMap();
		params.put("piCaNum", chargeAgreementNum);
		params.put("piWebQuoteNum", webQuoteNum);
		TimeTracer tracer = TimeTracer.newInstance();
		ResultSet rs = null;
		ResultSet rsItem = null;
		try {
			QueryContext context = QueryContext.getInstance();
			String sqlQuery = context.getCompletedQuery(
					ScwDBConstants.S_QT_GET_SCW_QUOTE, null);
			logger.debug(this, LogHelper.logSPCall(sqlQuery, params));
			CallableStatement ps = TopazUtil.getConnection().prepareCall(
					sqlQuery);
			context.completeStatement(ps, ScwDBConstants.S_QT_GET_SCW_QUOTE,
					params);

			tracer.stmtTraceStart("call S_QT_GET_SCW_QUOTE");
			boolean psResult = ps.execute();
			tracer.stmtTraceEnd("call S_QT_GET_SCW_QUOTE");
			if (psResult) {
				RetrieveQuoteHeader quoteHeader = retrieveQuote.getHeader();
				rs = ps.getResultSet();
				while (rs.next()) {
					quoteHeader.setOrderDate(rs.getString("ORDER_DATE"));
//					quoteHeader.setReferenceDocNum(rs.getString("REF_DOC_NUM"));
				}
				if (ps.getMoreResults()) {
					List<RetrieveLineItem> retrieveLineItems = retrieveQuote
							.getLineItems();
					rsItem = ps.getResultSet();
					while (rsItem.next()) {
						for (RetrieveLineItem lineItem : retrieveLineItems) {
							if (lineItem.getLineItemNum().equals(
									rsItem.getString("DEST_OBJCT_LINE_ITM_SEQ_NUM").trim())) {
								lineItem.setDoNotOrderFlag(rsItem
										.getString("DO_NOT_ORDER_FLAG"));
								lineItem.setRenewType(rsItem
										.getString("RENEW_TYPE"));
								lineItem.setTouUrlName(StringUtils.trim(rsItem.getString("TOU_URL_NAME")));
							}
						}
					}
				}
			}

		} catch (SQLException e) {
			logger.error(this, LogThrowableUtil.getStackTraceContent(e));
			throw new TopazException(
					"Exception when validate scw addon param , chargeAgreementNum ["
							+ chargeAgreementNum + "] webQuoteNum ["
							+ webQuoteNum + "]", e);
		} catch (TopazException tce) {
			logger.error(this, LogThrowableUtil.getStackTraceContent(tce));
			throw new TopazException(tce);
		} finally {
			try {
				if (null != rsItem && !rsItem.isClosed()) {
					rsItem.close();
				}
			} catch (SQLException e) {
				logger.error("Failed to close the resultset!", e);
			}
			try {
				if (null != rs && !rs.isClosed()) {
					rs.close();
				}
			} catch (SQLException e) {
				logger.error("Failed to close the resultset!", e);
			}
			tracer.dump();
		}
		return retrieveQuote;
	}

//  @Override
//  protected void persiserAddOnTradeUpLineItem(AddOnTradeUpInfo addOnTradeUpInfo) throws TopazException {
//      QuoteLineItem_jdbc qli = null;
//      int i = 1;
//      for (AddOnTradeUpConfiguration configuration: addOnTradeUpInfo.getConfigurations()){
//      	if (null == configuration || null == configuration.getLineItems() || configuration.getLineItems().isEmpty()){
//              continue;
//          }
//      	for( AddOnTradeUpLineItem addOnTradeUpLineItem : configuration.getLineItems()){
//      		if (null == addOnTradeUpLineItem || StringUtils.isBlank(addOnTradeUpLineItem.getPartNumber())){
//      			continue;
//      		}
//      		
//      		qli = new QuoteLineItem_jdbc(addOnTradeUpInfo.getWebQuoteNum(), addOnTradeUpLineItem.getPartNumber());
//      		qli.iCvrageTerm       = stringToInteger(configuration.getTermLength(),null);
//      		qli.billgFrqncyCode   = addOnTradeUpLineItem.getBillFrequency();
//      		qli.iPartQty          = addOnTradeUpLineItem.getQuantity();
//      		qli.configrtnId       = configuration.getConfigId();
//      		qli.originatingItemNum= stringToInteger(addOnTradeUpLineItem.getItemNumber(),null);
//      		qli.setUserID(addOnTradeUpInfo.getUserId());
//      		// use the default value
//      		qli.iQuoteSectnSeqNum = i++;
//      		qli.iVuConfigrtnNum   = 0;
//      		qli.bStartDtOvrrdFlg  = false;
//      		qli.bEndDtOvrrdFlg    = false;
//      		qli.bProrationFlag    = false;
//      		qli.bHasAssocdLicPart = false;
//      		qli.dLineDiscPct      = 0.0;
//      		qli.bBackDatingFlag   = false;
//      		qli.dChnlStdDiscPct   = 0.0;
//      		qli.dTotDiscPct       = 0.0;
//      		
//      		qli.isDeleted(false);
//      		qli.isNew(true);
//      	}
//      }
//  }

	@Override
	public String persiserAddOnTradeUpQuote(AddOnTradeUpInfo addOnTradeUpInfo)
			throws TopazException, QuoteException {
		LogContext logger = LogContextFactory.singleton().getLogContext();

		String sqlQuery = null;
		String quoteNum = null;
		HashMap params = new HashMap();
		params.put("piUserId", addOnTradeUpInfo.getUserId());
		params.put("piChrgArgMent", addOnTradeUpInfo.getChargeAgreementNumber());
		try {
			logger.debug(this, "validate add-on/trade-up quote  webQuoteNum: "
					+ addOnTradeUpInfo.getChargeAgreementNumber());

			QueryContext queryCtx = QueryContext.getInstance();
			sqlQuery = queryCtx.getCompletedQuery(
					ScwDBConstants.SP_CREATE_ADDON_TRADEUP_QUOTE, null);
			logger.debug(this, LogHelper.logSPCall(sqlQuery, params));

			CallableStatement ps = TopazUtil.getConnection().prepareCall(
					sqlQuery);
			queryCtx.completeStatement(ps,
					ScwDBConstants.SP_CREATE_ADDON_TRADEUP_QUOTE, params);
			ps.execute();
			int poGenStatus = ps.getInt(1);
			if ( poGenStatus == CommonDBConstants.DB2_SP_RETURN_SIGN_NODATA )
			{
				logger.error(this, "save add on trade up quote return 5000, ca not exists, delegate the validate to validate logic");
				return null;
			}
			if (poGenStatus != CommonDBConstants.DB2_SP_RETURN_SIGN_SUCCESS)
				throw new SPException(LogHelper.logSPCall(sqlQuery, params),
						poGenStatus);
			quoteNum = ps.getString(2);
		} catch (Exception e) {
			logger.error(this, LogHelper.logSPCall(sqlQuery, params));
			throw new QuoteException(e);
		} 
		return quoteNum;
	}

	private Integer stringToInteger(String str, Integer defaultValue) {
		try {
			return Integer.valueOf(str);
		} catch (NumberFormatException nfe) {
			return defaultValue;
		}
	}
}
