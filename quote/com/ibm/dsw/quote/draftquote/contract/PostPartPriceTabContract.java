package com.ibm.dsw.quote.draftquote.contract;

import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;

import com.ibm.dsw.quote.base.config.QuoteConstants;
import com.ibm.dsw.quote.base.util.LocaleHelperImpl;
import com.ibm.dsw.quote.common.config.PartPriceConstants;
import com.ibm.dsw.quote.common.domain.Quote;
import com.ibm.dsw.quote.common.domain.ServiceDateModType;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteActionKeys;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteParamKeys;
import com.ibm.dsw.quote.draftquote.config.DraftRQParamKeys;
import com.ibm.dsw.quote.draftquote.util.date.DateUtil;
import com.ibm.ead4j.jade.config.ActionHandlerKeys;
import com.ibm.ead4j.jade.config.ApplicationContextFactory;
import com.ibm.ead4j.jade.config.FrameworkKeys;
import com.ibm.ead4j.jade.session.JadeSession;
import com.ibm.ead4j.jade.util.Parameters;
import com.ibm.ead4j.opal.log.LogContextFactory;

/**
 * Copyright 2006 by IBM Corporation All rights reserved.
 *
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 *
 * The <code>PostPartPriceTabContract</code>
 *
 *
 * @author <a href="liuxinlx@cn.ibm.com">Andy Liu </a> <br/>
 *
 * Creation date: 2007-3-21
 */
public class PostPartPriceTabContract extends PostDraftQuoteBaseContract {

    public static final String ADD_DUPLICATE_ACTION_CODE = "ADD_DUP_LINE_ITEM";

    private static final String UN_SUPPORT_PARAM = "UN_SUPPORT_PARAM";

    //use GSA pricing where applicable
    private String useGSAPricing;

    //Quote discount percent
    private String quoteDiscountPercent;

    //YTY growth delegation for quote header
    private String quoteYty;

    private String prevOfferPrice;

    private int recaculateFlag;

    // only for renwal qutoe
    private boolean rqEditable;

    HashMap items = new HashMap();

    private String offerPrice;

    private Quote quote;

    private String backDatingComment ;

    private transient List reasonCodes = new ArrayList();

    private String partnerDiscountPercent;

    private boolean enableCmprssCvrage;

    private int offerAction;

    private boolean isAddDuplicatePart = false;

    private boolean isDeleteLineItem = false;

    private boolean isRemoveConfgrtn = false;

    private String deletePartNum;

    private String deletePartSeq;

    private String removeConfigrtnId;

    private boolean isExceedLimit = false;

    HashMap prvsningDaysMap = new HashMap();

    private String chargeAgreementNum;

	private Boolean isSaasMigration;

	private Boolean isSaasRenewal;

	private Boolean isMonthlyMigration;

	private Boolean isMonthlyRenewal;

    private String billingFrequencyChangeSelect = null;

    HashMap coTermsMap = new HashMap();

    HashMap serviceDateModTypeMap = new HashMap();

    HashMap serviceDateMap = new HashMap();

    HashMap termExtensionMap = new HashMap();

    HashMap renwlModeConfigrtnMap = new HashMap();
    private String setLineToRsvpSrpId;
    public static class LineItemParameter {

        public String key; // format: PARTNUM_SEQNUM

		public String manualSortOrder;

        public String quantity;

        public String overrideEntitledPrice;

        public String overridePrice;
        
        public String originalOverridePrice;

        public String overridePriceRadio;

        public String discountPercent;

        public String discountPriceRadio;

        public String maintainAddtionalYear;

        public boolean prorateFirstYearToAnni = false;

        public Date maintStartDate;

        public Date maintEndDate;

        public String ovrdStartDateFlag;

        public String ovrdEndDateFlag;

        public boolean isFTL = false;

        public boolean isOfferIncldFlag = false;

        public boolean pvuQtyManuallyEntered = false;

        public String additionalMaintSeqNum;

        public transient List subLineItems = new ArrayList();

        public String rqChangeReason;

        public String rqOvrdStartDay;

        public String rqOvrdStartMonth;

        public String rqOvrdStartYear;

        public String rqOvrdEndDay;

        public String rqOvrdEndMonth;

        public String rqOvrdEndYear;

        public String rqPrevQty;

        public String prevOvrrdPrice;

        public String prevDiscount;
        
        public String prevYty;

        public String rqPrevStartDate;

        public String rqPrevEndDate;

        public String bpOverrideDiscount;

        public String entitledExtPrice;

        public String bpChannelExtndPrice;

        //Compressed coverage
        public String cmprssCvrageMonth;
        public String cmprssCvrageDisc;

        // SaaS line items attribute
        public String cvrageTerm;
        public String billingFrequency;
        public String overrideType;
        public boolean saasRenwl; //refer to rtc#207982
        public boolean saasMgrtn;
		public Boolean isLineItemNewService;
		public boolean isLineDivHidden;
        public String renwlModeSubscrptn;


        //appliance Item
        public String applianceId;
        public String machineType;
        public String machineModel;
        public String machineSerialNumber;
        public String applncPocInd;
        public String applncPriorPoc;
        
        //appliance#145
        public String nonIBMSerialNumber;
        public String nonIBMModel;
        
        //appliance#99
        
        public Date custReqArrlDate;
        public String custQliReqstdArrivlDay;
        public String custQliReqstdArrivlMonth;
        public String custQliReqstdArrivlYear;
        public Boolean appSendtoMFG=false;
        public Date headerCRAD;
        public Date lineItemCRAD;

        //yty growth delegation for quote line item
        public String yty;
        public String ytyRadio;
        //appliance deployment model
        public int deployModelOption;
        public String deployModelId;
        public String deployModelIdSystem;
        public boolean deployModelValid;

        public Date getRQStartDate() {
            return DateUtil.parseDate(rqOvrdStartYear + "-" + rqOvrdStartMonth + "-" + rqOvrdStartDay, DateUtil.PATTERN);
        }

        public Date getRQEndDate() {
            return DateUtil.parseDate(rqOvrdEndYear + "-" + rqOvrdEndMonth + "-" + rqOvrdEndDay, DateUtil.PATTERN);
        }

        @Override
		public String toString() {
            StringBuffer sb = new StringBuffer();
            return sb.toString();
        }
    }

    public boolean hasOverrideDate() {
        Iterator iter = items.values().iterator();
        while (iter.hasNext()) {
            LineItemParameter li = (LineItemParameter) iter.next();
            if ((li.ovrdStartDateFlag != null) || (li.ovrdEndDateFlag != null)) {
                return true;
            }
        }
        return false;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.ibm.ead4j.jade.contract.ProcessContract#load(com.ibm.ead4j.jade.util.Parameters,
     *      com.ibm.ead4j.jade.session.JadeSession)
     */
@Override
public void load(Parameters parameters, JadeSession session) {
        super.load(parameters, session);
        if (offerPrice != null){
        	offerPrice = offerPrice.replaceAll(",", "");
        }
        Locale locale = (Locale) session.getAttribute(FrameworkKeys.JADE_LOCALE_KEY);
        if (locale == null) {
            locale = LocaleHelperImpl.getDefaultDSWLocale();
        }

        useGSAPricing = parameters.getParameterAsString(DraftQuoteParamKeys.USE_GSA_PRICING);

        if (StringUtils.isNotBlank(parameters.getParameterAsString(DraftQuoteParamKeys.QUOTE_DIS_PER_INPUT))) {
            quoteDiscountPercent = parameters.getParameterAsString(DraftQuoteParamKeys.QUOTE_DIS_PER_INPUT);
        }

        if (StringUtils.isNotBlank(parameters.getParameterAsString(DraftQuoteParamKeys.OVERALL_YTY_GROWTH))) {
            quoteYty = parameters.getParameterAsString(DraftQuoteParamKeys.OVERALL_YTY_GROWTH);
        }

        if (StringUtils.isNotBlank(parameters.getParameterAsString(DraftQuoteParamKeys.PREV_OFFER_PRICE))) {
            prevOfferPrice = parameters.getParameterAsString(DraftQuoteParamKeys.PREV_OFFER_PRICE);
        }

        if(StringUtils.isNotBlank(parameters.getParameterAsString(DraftQuoteParamKeys.BACK_DATEING_COMMENT))){
        	backDatingComment =  parameters.getParameterAsString(DraftQuoteParamKeys.BACK_DATEING_COMMENT);
        }

        if (StringUtils.isNotBlank(parameters.getParameterAsString(DraftQuoteParamKeys.PARTNER_DIS_PER_INPUT))) {
            partnerDiscountPercent = parameters.getParameterAsString(DraftQuoteParamKeys.PARTNER_DIS_PER_INPUT);
        }

        //If user checks the checkbox, it must has a non-blank value
        if(StringUtils.isNotBlank(parameters.getParameterAsString(DraftQuoteParamKeys.PARAM_CMPRSS_CVRAGE))){
            enableCmprssCvrage = true;
        }

        if (StringUtils.isNotBlank(parameters.getParameterAsString(DraftQuoteParamKeys.SERVICE_AGREEMENT))) {
            chargeAgreementNum = parameters.getParameterAsString(DraftQuoteParamKeys.SERVICE_AGREEMENT);
        }
        //get saasMigrationFlag
        String saasMigrationFlag=parameters
				.getParameterAsString(DraftQuoteParamKeys.MIGRATION_FLAG);
		if (StringUtils.isNotBlank(saasMigrationFlag)) {
			isSaasMigration = saasMigrationFlag.equals(
					DraftQuoteParamKeys.GSA_PRICING_YES) ? true : false;
		}
		//get saasRenewalFlag
        String saasRenewalFlag=parameters
				.getParameterAsString(DraftQuoteParamKeys.RENEWAL_FLAG);
		if (StringUtils.isNotBlank(saasRenewalFlag)) {
			isSaasRenewal = saasRenewalFlag.equals(
					DraftQuoteParamKeys.GSA_PRICING_YES) ? true : false;
		}
		//get mtMigrationFlag
        String mtMigrationFlag=parameters
				.getParameterAsString(DraftQuoteParamKeys.MONTHLY_MIGRATION_FLAG);
		if (StringUtils
			 	.isNotBlank(mtMigrationFlag)) {
			isMonthlyMigration = mtMigrationFlag.equals(
					DraftQuoteParamKeys.GSA_PRICING_YES) ? true : false;
		}
		//get mtRenewalFlag
        String mtRenewalFlag=parameters
				.getParameterAsString(DraftQuoteParamKeys.MONTHLY_RENEWAL_FLAG);
		if (StringUtils
				.isNotBlank(mtRenewalFlag)) {
			isMonthlyRenewal = mtRenewalFlag.equals(
					DraftQuoteParamKeys.GSA_PRICING_YES) ? true : false;
		}

        if(this.isRenwalQuote()){

            this.rqEditable = parameters.getParameterAsBoolean(DraftRQParamKeys.RQ_EDITABLE);
            LogContextFactory.singleton().getLogContext().debug(this,"RQ Editable = "+this.rqEditable);

        }

        String actionCode = parameters.getParameterAsString(ApplicationContextFactory.singleton().getApplicationContext().getConfigParameter(ActionHandlerKeys.JADE_ACTION_KEY));
        isAddDuplicatePart = ADD_DUPLICATE_ACTION_CODE.equals(StringUtils.trim(actionCode));
        isDeleteLineItem = DraftQuoteActionKeys.DELETE_LINE_ITEM.equals(StringUtils.trim(actionCode));
        if(isDeleteLineItem){
        	if(StringUtils.isNotBlank(parameters.getParameterAsString(DraftQuoteParamKeys.PART_NUM))){
        		deletePartNum = parameters.getParameterAsString(DraftQuoteParamKeys.PART_NUM);
            }
        	if(StringUtils.isNotBlank(parameters.getParameterAsString("partSeqNum"))){
        		deletePartSeq = parameters.getParameterAsString("partSeqNum");
            }
        }
        isRemoveConfgrtn = DraftQuoteActionKeys.REMOVE_CONFIGURATION.equals(StringUtils.trim(actionCode));
        if(isRemoveConfgrtn){
        	if(StringUtils.isNotBlank(parameters.getParameterAsString("configurationId"))){
        		removeConfigrtnId = parameters.getParameterAsString("configurationId");
            }
        }

        Iterator it = parameters.getParameterNames();


        while (it.hasNext()) {
            String name = (String) it.next();

            try {
                String value = null;
                try{
                    value = parameters.getParameterAsString(name);
                }catch(Throwable e){
                    LogContextFactory.singleton().getLogContext().debug(this,"param value of "+name+" is not a string ,just ignore it");
                    continue;
                }

                String key = getKey(name);
                LogContextFactory.singleton().getLogContext().debug(this,"param name="+name+"->key="+key);
                if (UN_SUPPORT_PARAM.equals(key)) {
                    continue;
                }


	           if(name.endsWith(DraftQuoteParamKeys.BACK_DATE_REASON_SUFFIX)){
	               if(!"".equals(value)){
	               		reasonCodes.add(value);
	               }
                }

                LineItemParameter item = getLineItem(key);

                if (name.endsWith(DraftQuoteParamKeys.partNumberSuffix)) {
                    item.key = value.trim();

                } else if (name.endsWith(DraftQuoteParamKeys.specifySortOrderSuffix)) {

                    item.manualSortOrder = value.trim();

                } else if (name.endsWith(DraftQuoteParamKeys.quantitySuffix)) {

                    item.quantity = value.trim();

                } else if (name.endsWith(DraftQuoteParamKeys.overridePriceSuffix)) {
                    item.overridePrice = value.trim();

                } else if (name.endsWith(DraftQuoteParamKeys.originalOverridePriceSuffix)) {
                    item.originalOverridePrice = value.trim();

                }
                else if (name.endsWith(DraftQuoteParamKeys.overridePriceRadio)) {
                    item.overridePriceRadio = value.trim();
                }
                else if (name.endsWith(DraftQuoteParamKeys.discountPriceSuffix)) {

                    item.discountPercent = value.trim();

                }
                else if (name.endsWith(DraftQuoteParamKeys.discountPriceRadio)) {
                    item.discountPriceRadio = value.trim();
                }

                else if (name.endsWith(DraftQuoteParamKeys.maintainAddtionalYearSuffix)) {

                    item.maintainAddtionalYear = value.trim();

                } else if (name.endsWith(DraftQuoteParamKeys.prorateFirstYearToAnniSuffix)) {

                    if (value.trim().equals(DraftQuoteParamKeys.GSA_PRICING_YES)){
                        item.prorateFirstYearToAnni = true;
                    }else{
                        item.prorateFirstYearToAnni = false;
                    }
//                    item.prorateFirstYearToAnni = true;

                } else if (name.endsWith(DraftQuoteParamKeys.prorateStartDateSuffix)) {

                    item.maintStartDate = DateUtil.parseDate(value.trim(), DateUtil.PATTERN1,locale);

                } else if (name.endsWith(DraftQuoteParamKeys.prorateEndDateSuffix)) {

                    item.maintEndDate = DateUtil.parseDate(value.trim(), DateUtil.PATTERN1,locale);

                }  else if (name.endsWith(DraftQuoteParamKeys.ovrdDtStartFlagSuffix)) {

                    item.ovrdStartDateFlag = value.trim();
                } else if (name.endsWith(DraftQuoteParamKeys.ovrdDtEndFlagSuffix)) {

                    item.ovrdEndDateFlag = value.trim();
                }else if (name.endsWith(DraftQuoteParamKeys.isFTLSuffix)) {
                    item.isFTL = this.parseBoolean(value.trim());
                }else if (name.endsWith(DraftQuoteParamKeys.offerIncldFlagSuffix)) {
                    item.isOfferIncldFlag = this.parseBoolean(value.trim());
                } else if (name.endsWith(DraftQuoteParamKeys.PVU_QTY_MANUALLY_ENTERED_SUFFIX)){

                    item.pvuQtyManuallyEntered = this.parseBoolean(value.trim());
                } else if (name.endsWith(DraftQuoteParamKeys.addiSeqNumSuffix)){
                    item.additionalMaintSeqNum = value.trim();
                }
                else if(name.endsWith(DraftQuoteParamKeys.PREV_OVRRD_PRICE_SUFFIX)){
                    item.prevOvrrdPrice = value.trim();
                }
                else if(name.endsWith(DraftQuoteParamKeys.PREV_DISCOUNT_SUFFIX)){
                    item.prevDiscount = value.trim();
                }
                else if(name.endsWith(DraftQuoteParamKeys.PREV_YTY_SUFFIX)){
                	item.prevYty = value.trim();
                }
                else if(name.endsWith(DraftQuoteParamKeys.bpOverrideDisSuffix)){
                    item.bpOverrideDiscount = value.trim();
                }
                else if(name.endsWith(DraftQuoteParamKeys.overrideEntitledPriceSuffix)){
                	item.overrideEntitledPrice = value.trim();
                }
                else if(name.endsWith(DraftQuoteParamKeys.PARAM_CMPRSS_CVRAGE_MONTH_SUFFIX)){
                    item.cmprssCvrageMonth = value.trim();
                }
                else if(name.endsWith(DraftQuoteParamKeys.PARAM_CMPRSS_CVRAGE_DISC_SUFFIX)){
                    item.cmprssCvrageDisc = value.trim();
                }
                // get SaaS line item attribute
                else if(name.endsWith(DraftQuoteParamKeys.CVRAGE_TERM)){
                    item.cvrageTerm = value.trim();
                }
                else if(name.endsWith(DraftQuoteParamKeys.BILLING_FREQUENCY)){
                    item.billingFrequency = value.trim();
                }
                else if(name.endsWith(DraftQuoteParamKeys.PARAM_OVERRIDE_TYPE)){
                    item.overrideType = value.trim();
                }
                else if (name.endsWith(DraftQuoteParamKeys.PROVISIONING_DAYS)) {
                    prvsningDaysMap.put(key, value.trim());
                }else if (name.endsWith(DraftQuoteParamKeys.CO_TERM_SUFFIX)) {
                	coTermsMap.put(key, value.trim());
                }else if (name.endsWith(DraftQuoteParamKeys.SAAS_RENWL_SUFFIX)) { //refer to rtc#207982
                    if (value.trim().equals(DraftQuoteParamKeys.GSA_PRICING_YES)){
                        item.saasRenwl = true;
                    }else{
                        item.saasRenwl = false;
                    }
                }else if (name.endsWith(DraftQuoteParamKeys.SAAS_MGRTN_SUFFIX)) { //refer to rtc#207982
                    if (DraftQuoteParamKeys.GSA_PRICING_YES.equals(value.trim())){
						item.saasMgrtn = true;
					} else {
						item.saasMgrtn = false;
					}
				} else if (name
						.endsWith(DraftQuoteParamKeys.LINE_NEW_SERVICE_SUFFIX)) {
					if (DraftQuoteParamKeys.GSA_PRICING_YES
							.equals(value.trim())) {
						item.isLineItemNewService = true;
                    }else{
						item.isLineItemNewService = false;
                    }
				} else if(name.endsWith(DraftQuoteParamKeys.LINE_DIV_HIDDEN)){
					if("none".equals(value.trim())){
						item.isLineDivHidden = true;
					}else{
						item.isLineDivHidden = false;
					}
				}
				else if (name
						.endsWith(DraftQuoteParamKeys.SERVICE_DATE_MODTYPE)) {
                	serviceDateModTypeMap.put(key, StringUtils.trim(value) == null ? null : ServiceDateModType.valueOf(StringUtils.trim(value)));
                } else if(name.endsWith(DraftQuoteParamKeys.SERVICE_DATE)){
                	serviceDateMap.put(key,DateUtil.parseDate(value.trim(), DateUtil.PATTERN4,locale));
                } else if(name.endsWith(DraftQuoteParamKeys.TERM_EXTENSION)){
                	termExtensionMap.put(key, this.parseBoolean(StringUtils.trim(value)) == true ? true : false);
                }

              //auto renewal model
    	        else if (name.endsWith(DraftQuoteParamKeys.PARAM_RENEWAL_MODEL_CONFIGURTN)){
    	        	renwlModeConfigrtnMap.put(key, value.trim());
    	        } else if (name.endsWith(DraftQuoteParamKeys.PARAM_RENEWAL_MODEL_SUBSCRPTN)){
    	        	item.renwlModeSubscrptn = value.trim();
    	        }

                //Appliance item
                else if(name.endsWith(DraftQuoteParamKeys.APPLNC_MTM_TYPE)){
    	        	item.machineType = value.trim();
    	        } else if(name.endsWith(DraftQuoteParamKeys.APPLNC_MTM_MODEL)){
    	        	item.machineModel = value.trim();
    	        } else if(name.endsWith(DraftQuoteParamKeys.APPLNC_MTM_SERIAL_NUMBER)){
    	        	item.machineSerialNumber = value.trim();
    	        } else if(name.endsWith(DraftQuoteParamKeys.APPLNC_POC_IND)){
    	        	item.applncPocInd = value.trim();
    	        } else if(name.endsWith(DraftQuoteParamKeys.APPLNC_PRIOR_POC)){
    	        	item.applncPriorPoc = value.trim();
    	        } else if (name.endsWith(DraftQuoteParamKeys.APPLNC_ID)){
    	        	item.applianceId = value.trim();
    	        } else if (name.endsWith(DraftQuoteParamKeys.NON_IBM_MACHINE_SERIAL_NUM)) {
    	        	item.nonIBMSerialNumber = value.trim();
    	        } else if(name.endsWith(DraftQuoteParamKeys.NON_IBM_MODEL)) {
    	        	item.nonIBMModel = value.trim();
    	        } else if (name.endsWith(DraftQuoteParamKeys.APPLNC_DEPLOY_ASSOCIATION)) {
    	        	item.deployModelOption = NumberUtils.stringToInt(value.trim());
    	        } else if(name.endsWith(DraftQuoteParamKeys.APPLNC_DEPLOY_ID)) {
    	        	item.deployModelId = value.trim();
    	        }else if (name.endsWith(DraftQuoteParamKeys.APPLNC_DEPLOY_ID_SYSTEM)) {
    	        	item.deployModelIdSystem = value.trim();
    	        } else if(name.endsWith(DraftQuoteParamKeys.APPLNC_DEPLOY_ID_VALID)) {
    	        	item.deployModelValid = PartPriceConstants.DEPLOYMENT_ID_INVALID.equalsIgnoreCase(value.trim());
    	        }
                
                //Appliance#99
    	        else if (name.endsWith(DraftQuoteParamKeys.PARAM_CUST_REQSTD_ARRIVAL_DATE_SUFFIX)){
    	        	LogContextFactory.singleton().getLogContext().debug(this,"lineItem cust req arrival date:"+value.trim());
    	        	item.custReqArrlDate = DateUtil.parseDate(value.trim(), DateUtil.PATTERN,locale);
    	        } else if (name.endsWith(DraftQuoteParamKeys.PARAM_LINEITEM_CUST_REQSTD_ARRIVL_DAY)){
    	        	LogContextFactory.singleton().getLogContext().debug(this,"lineItem cust req arrival day:"+value.trim());
    	        	item.custQliReqstdArrivlDay = value.trim();
    	        } else if (name.endsWith(DraftQuoteParamKeys.PARAM_LINEITEM_CUST_REQSTD_ARRIVL_MONTH)){
    	        	LogContextFactory.singleton().getLogContext().debug(this,"lineItem cust req arrival month:"+value.trim());
    	        	item.custQliReqstdArrivlMonth = value.trim();
    	        } else if (name.endsWith(DraftQuoteParamKeys.PARAM_LINEITEM_CUST_REQSTD_ARRIVL_YEAR)){
    	        	LogContextFactory.singleton().getLogContext().debug(this,"lineItem cust req arrival year:"+value.trim());
    	        	item.custQliReqstdArrivlYear = value.trim();
    	        } else if (name.endsWith(DraftQuoteParamKeys.PARAM_LINEITEM_APP_SEND_MFG_FLG)){
    	        	LogContextFactory.singleton().getLogContext().debug(this,"lineItem app send mfg flag:"+value.trim());
    	        	item.appSendtoMFG = Boolean.valueOf(value.trim());
    	        } else if (name.endsWith(DraftQuoteParamKeys.PARAM_ORIGINAL_HEADER_CRAD)){
    	        	LogContextFactory.singleton().getLogContext().debug(this,"head CRAD:"+value.trim());
    	        	item.headerCRAD = DateUtil.parseDate(value.trim(), DateUtil.PATTERN,locale);
    	        } else if (name.endsWith(DraftQuoteParamKeys.PARAM_CUST_REQSTD_ARRIVAL_DATE_ORG_SUFFIX)){
    	        	LogContextFactory.singleton().getLogContext().debug(this,"original lineItem  CRAD:"+value.trim());
    	        	item.lineItemCRAD = DateUtil.parseDate(value.trim(), DateUtil.PATTERN,locale);
    	        }

                //yty
    	        else if (name.endsWith(DraftQuoteParamKeys.YTY_GROWTH_SUFFIX)){
    	        	item.yty = value.trim();
    	        }

    	        else if (name.endsWith(DraftQuoteParamKeys.PRICE_DISCOUNT_RADIO)){
    	        	item.ytyRadio = value.trim();
    	        }


                //              start to handle RQ quote
                if (QuoteConstants.QUOTE_TYPE_RENEWAL.equals(super.getQuoteType())){
                    //LogContextFactory.singleton().getLogContext().debug(this,"rq name="+name+",value="+value);
	                if (name.endsWith(DraftRQParamKeys.RQ_REASON_FOR_ADD_SUFFIX)
	                        || name.endsWith(DraftRQParamKeys.RQ_REASON_FOR_DEL_SUFFIX)
	                        || name.endsWith(DraftRQParamKeys.RQ_REASON_FOR_CHG_SUFFIX)) {

	                    item.rqChangeReason = value.trim();
	                } else if (name.endsWith(DraftRQParamKeys.ovrdDtStartDaySuffix)) {
	                    LogContextFactory.singleton().getLogContext().debug(this,"rq ovrd start day:"+value.trim());
	                    item.rqOvrdStartDay = value.trim();

	                } else if (name.endsWith(DraftRQParamKeys.ovrdDtStartMonthSuffix)) {
	                    LogContextFactory.singleton().getLogContext().debug(this,"rq ovrd start month:"+value.trim());
	                    item.rqOvrdStartMonth = value.trim();

	                } else if (name.endsWith(DraftRQParamKeys.ovrdDtStartYearSuffix)) {
	                    LogContextFactory.singleton().getLogContext().debug(this,"rq ovrd start year:"+value.trim());
	                    item.rqOvrdStartYear = value.trim();

	                } else if (name.endsWith(DraftRQParamKeys.ovrdDtEndDaySuffix)) {
	                    LogContextFactory.singleton().getLogContext().debug(this,"rq ovrd end day:"+value.trim());
	                    item.rqOvrdEndDay = value.trim();

	                } else if (name.endsWith(DraftRQParamKeys.ovrdDtEndMonthSuffix)) {
	                    LogContextFactory.singleton().getLogContext().debug(this,"rq ovrd end month:"+value.trim());
	                    item.rqOvrdEndMonth = value.trim();

	                } else if (name.endsWith(DraftRQParamKeys.ovrdDtEndYearSuffix)) {
	                    LogContextFactory.singleton().getLogContext().debug(this,"rq ovrd start year:"+value.trim());
	                    item.rqOvrdEndYear = value.trim();
	                }
	                // get the previous data from UI hidden field
	                else if(name.endsWith(DraftRQParamKeys.RQ_PREV_QTY_SUFFIX)){
	                    item.rqPrevQty = value.trim();

	                }
	                else if(name.endsWith(DraftRQParamKeys.RQ_PREV_START_DATE_SUFFIX)){
	                    item.rqPrevStartDate = value.trim();
	                }
	                else if(name.endsWith(DraftRQParamKeys.RQ_PREV_END_DATE_SUFFIX)){
	                    item.rqPrevEndDate = value.trim();
	                }


	                //enforce the end day of month.
	                if(StringUtils.isBlank(item.rqOvrdEndDay)&&StringUtils.isNotBlank(item.rqOvrdEndMonth)&&StringUtils.isNotBlank(item.rqOvrdEndYear)){
	                    Date ovrdEndDate = DateUtil.parseDate(item.rqOvrdEndYear+"-"+item.rqOvrdEndMonth+"-01");
	                    Date endDay = DateUtil.moveToLastDayofMonth(ovrdEndDate);
	                    item.rqOvrdEndDay =DateUtil.formatDate(endDay,"dd");
	                }
	                LogContextFactory.singleton().getLogContext().debug(this,"After load all RQ paramer :item.rqOvrdStartYear="+item.rqOvrdStartYear);

                }

            } catch (Exception e) {

                LogContextFactory.singleton().getLogContext().fatal(this, "Load parameter error:" + e.getMessage());
            }
        }

        LogContextFactory.singleton().getLogContext().debug(this, "End of Load Parameters, total line items :"+items.values().size());

        Iterator iter = items.values().iterator();
        while(iter.hasNext()){
            LineItemParameter item = (LineItemParameter)iter.next();
            if(item.isLineDivHidden){
            	item.isLineItemNewService=null;
            }
            if (StringUtils.isNotEmpty(item.custQliReqstdArrivlDay)&&StringUtils.isNotEmpty(item.custQliReqstdArrivlMonth)&&
            		StringUtils.isNotEmpty(item.custQliReqstdArrivlYear)&&(item.custReqArrlDate==null)){
            	String tempDateStr = item.custQliReqstdArrivlYear + "-" + item.custQliReqstdArrivlMonth + "-" + item.custQliReqstdArrivlDay;
            	try {
            		item.custReqArrlDate = DateUtil.parseDate(tempDateStr.trim(), DateUtil.PATTERN,locale);
            	} catch (Exception e) {
                    LogContextFactory.singleton().getLogContext().fatal(this, "Load parameter error:" + e.getMessage());
                }
            	
            }
            LogContextFactory.singleton().getLogContext().debug(this,"key="+item.key);
            LogContextFactory.singleton().getLogContext().debug(this,"After load parameter: item.rqOvrdStartYear="+item.rqOvrdStartYear);
        }

    }

    private String getKey(String value) {
        if (value != null && value.trim().length() != 0) {
            if (value.endsWith(DraftQuoteParamKeys.partNumberSuffix)) {
                int index = value.lastIndexOf(DraftQuoteParamKeys.partNumberSuffix);
                value = value.substring(0, index);
            } else if (value.endsWith(DraftQuoteParamKeys.specifySortOrderSuffix)) {
                int index = value.lastIndexOf(DraftQuoteParamKeys.specifySortOrderSuffix);
                value = value.substring(0, index);
            } else if (value.endsWith(DraftQuoteParamKeys.quantitySuffix)) {
                int index = value.lastIndexOf(DraftQuoteParamKeys.quantitySuffix);
                value = value.substring(0, index);
            } else if (value.endsWith(DraftQuoteParamKeys.overridePriceSuffix)) {
                int index = value.lastIndexOf(DraftQuoteParamKeys.overridePriceSuffix);
                value = value.substring(0, index);
            } else if (value.endsWith(DraftQuoteParamKeys.originalOverridePriceSuffix)) {
                int index = value.lastIndexOf(DraftQuoteParamKeys.originalOverridePriceSuffix);
                value = value.substring(0, index);
            } else if (value.endsWith(DraftQuoteParamKeys.discountPriceSuffix)) {
                int index = value.lastIndexOf(DraftQuoteParamKeys.discountPriceSuffix);
                value = value.substring(0, index);
            } else if (value.endsWith(DraftQuoteParamKeys.maintainAddtionalYearSuffix)) {
                int index = value.lastIndexOf(DraftQuoteParamKeys.maintainAddtionalYearSuffix);
                value = value.substring(0, index);
            } else if (value.endsWith(DraftQuoteParamKeys.prorateFirstYearToAnniSuffix)) {
                int index = value.lastIndexOf(DraftQuoteParamKeys.prorateFirstYearToAnniSuffix);
                value = value.substring(0, index);
            } else if (value.endsWith(DraftQuoteParamKeys.prorateStartDateSuffix)) {
                int index = value.lastIndexOf(DraftQuoteParamKeys.prorateStartDateSuffix);
                value = value.substring(0, index);
            } else if (value.endsWith(DraftQuoteParamKeys.prorateEndDateSuffix)) {
                int index = value.lastIndexOf(DraftQuoteParamKeys.prorateEndDateSuffix);
                value = value.substring(0, index);
            } else if (value.endsWith(DraftQuoteParamKeys.itemPointSuffix)) {
                int index = value.lastIndexOf(DraftQuoteParamKeys.itemPointSuffix);
                value = value.substring(0, index);
            } else if (value.endsWith(DraftQuoteParamKeys.itemPriceSuffix)) {
                int index = value.lastIndexOf(DraftQuoteParamKeys.itemPriceSuffix);
                value = value.substring(0, index);
            } else if (value.endsWith(DraftQuoteParamKeys.totalPointsSuffix)) {
                int index = value.lastIndexOf(DraftQuoteParamKeys.totalPointsSuffix);
                value = value.substring(0, index);
            } else if (value.endsWith(DraftQuoteParamKeys.totalPriceSuffix)) {
                int index = value.lastIndexOf(DraftQuoteParamKeys.totalPriceSuffix);
                value = value.substring(0, index);
            } else if (value.endsWith(DraftQuoteParamKeys.ovrdDtStartFlagSuffix)) {
                int index = value.lastIndexOf(DraftQuoteParamKeys.ovrdDtStartFlagSuffix);
                value = value.substring(0, index);
            } else if (value.endsWith(DraftQuoteParamKeys.ovrdDtEndFlagSuffix)) {
                int index = value.lastIndexOf(DraftQuoteParamKeys.ovrdDtEndFlagSuffix);
                value = value.substring(0, index);
            } else if (value.endsWith(DraftQuoteParamKeys.isFTLSuffix)) {
                int index = value.lastIndexOf(DraftQuoteParamKeys.isFTLSuffix);
                value = value.substring(0, index);
            } else if (value.endsWith(DraftQuoteParamKeys.offerIncldFlagSuffix)) {
                int index = value.lastIndexOf(DraftQuoteParamKeys.offerIncldFlagSuffix);
                value = value.substring(0, index);
            } else if(value.endsWith(DraftQuoteParamKeys.PVU_QTY_MANUALLY_ENTERED_SUFFIX)){
                int index = value.lastIndexOf(DraftQuoteParamKeys.PVU_QTY_MANUALLY_ENTERED_SUFFIX);
                value = value.substring(0,index);
            } else if(value.endsWith(DraftQuoteParamKeys.addiSeqNumSuffix)){
                int index = value.lastIndexOf(DraftQuoteParamKeys.addiSeqNumSuffix);
                value = value.substring(0,index);
            } else if(value.endsWith(DraftQuoteParamKeys.BACK_DATE_REASON_SUFFIX)){
            	//do nothing here, just prevent this parameter becomes UN_SUPPORT_PARAM
            } else if(value.endsWith(DraftQuoteParamKeys.overrideEntitledPriceSuffix)){
                int index = value.lastIndexOf(DraftQuoteParamKeys.overrideEntitledPriceSuffix);
                value = value.substring(0,index);
            } else if(value.endsWith(DraftQuoteParamKeys.PARAM_CMPRSS_CVRAGE_MONTH_SUFFIX)){
                int index = value.lastIndexOf(DraftQuoteParamKeys.PARAM_CMPRSS_CVRAGE_MONTH_SUFFIX);
                value = value.substring(0,index);
            } else if(value.endsWith(DraftQuoteParamKeys.PARAM_CMPRSS_CVRAGE_DISC_SUFFIX)){
                int index = value.lastIndexOf(DraftQuoteParamKeys.PARAM_CMPRSS_CVRAGE_DISC_SUFFIX);
                value = value.substring(0,index);
            } else if(value.endsWith(DraftQuoteParamKeys.PROVISIONING_DAYS)){
                int index = value.lastIndexOf(DraftQuoteParamKeys.PROVISIONING_DAYS);
                value = value.substring(0,index);
            } else if(value.endsWith(DraftQuoteParamKeys.CO_TERM_SUFFIX)){
                int index = value.lastIndexOf(DraftQuoteParamKeys.CO_TERM_SUFFIX);
                value = value.substring(0,index);
            } else if(value.endsWith(DraftQuoteParamKeys.SERVICE_DATE_MODTYPE)){
                int index = value.lastIndexOf(DraftQuoteParamKeys.SERVICE_DATE_MODTYPE);
                value = value.substring(0,index);
            } else if(value.endsWith(DraftQuoteParamKeys.SERVICE_DATE)){
                int index = value.lastIndexOf(DraftQuoteParamKeys.SERVICE_DATE);
                value = value.substring(0,index);
            } else if(value.endsWith(DraftQuoteParamKeys.TERM_EXTENSION)){
                int index = value.lastIndexOf(DraftQuoteParamKeys.TERM_EXTENSION);
                value = value.substring(0,index);
            }
            // for renewal quote
            else if (value.endsWith(DraftRQParamKeys.RQ_REASON_FOR_ADD_SUFFIX)) {
                int index = value.lastIndexOf(DraftRQParamKeys.RQ_REASON_FOR_ADD_SUFFIX);
                value = value.substring(0, index);
            } else if (value.endsWith(DraftRQParamKeys.RQ_REASON_FOR_CHG_SUFFIX)) {
                int index = value.lastIndexOf(DraftRQParamKeys.RQ_REASON_FOR_CHG_SUFFIX);
                value = value.substring(0, index);
            } else if (value.endsWith(DraftRQParamKeys.RQ_REASON_FOR_DEL_SUFFIX)) {
                int index = value.lastIndexOf(DraftRQParamKeys.RQ_REASON_FOR_DEL_SUFFIX);
                value = value.substring(0, index);
            } else if (value.endsWith(DraftRQParamKeys.ovrdDtStartDaySuffix)) {
                int index = value.lastIndexOf(DraftRQParamKeys.ovrdDtStartDaySuffix);
                value = value.substring(0, index);
            } else if (value.endsWith(DraftRQParamKeys.ovrdDtStartMonthSuffix)) {
                int index = value.lastIndexOf(DraftRQParamKeys.ovrdDtStartMonthSuffix);
                value = value.substring(0, index);
            } else if (value.endsWith(DraftRQParamKeys.ovrdDtStartYearSuffix)) {
                int index = value.lastIndexOf(DraftRQParamKeys.ovrdDtStartYearSuffix);
                value = value.substring(0, index);
            } else if (value.endsWith(DraftRQParamKeys.ovrdDtEndDaySuffix)) {
                int index = value.lastIndexOf(DraftRQParamKeys.ovrdDtEndDaySuffix);
                value = value.substring(0, index);
            } else if (value.endsWith(DraftRQParamKeys.ovrdDtEndMonthSuffix)) {
                int index = value.lastIndexOf(DraftRQParamKeys.ovrdDtEndMonthSuffix);
                value = value.substring(0, index);
            } else if (value.endsWith(DraftRQParamKeys.ovrdDtEndYearSuffix)) {
                int index = value.lastIndexOf(DraftRQParamKeys.ovrdDtEndYearSuffix);
                value = value.substring(0, index);
            } else if (value.endsWith(DraftRQParamKeys.RQ_PREV_QTY_SUFFIX)){
                int index = value.lastIndexOf(DraftRQParamKeys.RQ_PREV_QTY_SUFFIX);
                value = value.substring(0, index);
            } else if (value.endsWith(DraftQuoteParamKeys.PREV_OVRRD_PRICE_SUFFIX)){
                int index = value.lastIndexOf(DraftQuoteParamKeys.PREV_OVRRD_PRICE_SUFFIX);
                value = value.substring(0, index);
            }else if (value.endsWith(DraftQuoteParamKeys.PREV_DISCOUNT_SUFFIX)){
                int index = value.lastIndexOf(DraftQuoteParamKeys.PREV_DISCOUNT_SUFFIX);
                value = value.substring(0, index);
            }else if (value.endsWith(DraftQuoteParamKeys.PREV_YTY_SUFFIX)){
                int index = value.lastIndexOf(DraftQuoteParamKeys.PREV_YTY_SUFFIX);
                value = value.substring(0, index);
            }else if (value.endsWith(DraftRQParamKeys.RQ_PREV_START_DATE_SUFFIX)){
                int index = value.lastIndexOf(DraftRQParamKeys.RQ_PREV_START_DATE_SUFFIX);
                value = value.substring(0, index);
            }else if (value.endsWith(DraftRQParamKeys.RQ_PREV_END_DATE_SUFFIX)){
                int index = value.lastIndexOf(DraftRQParamKeys.RQ_PREV_END_DATE_SUFFIX);
                value = value.substring(0, index);
            }
	        else if (value.endsWith(DraftQuoteParamKeys.bpOverrideDisSuffix)){
	            int index = value.lastIndexOf(DraftQuoteParamKeys.bpOverrideDisSuffix);
	            value = value.substring(0, index);
	        }
	        else if (value.endsWith(DraftQuoteParamKeys.CVRAGE_TERM)){
	            int index = value.lastIndexOf(DraftQuoteParamKeys.CVRAGE_TERM);
	            value = value.substring(0, index);
	        }
	        else if (value.endsWith(DraftQuoteParamKeys.BILLING_FREQUENCY)){
	            int index = value.lastIndexOf(DraftQuoteParamKeys.BILLING_FREQUENCY);
	            value = value.substring(0, index);
	        }
	        else if (value.endsWith(DraftQuoteParamKeys.PARAM_OVERRIDE_TYPE)){
	            int index = value.lastIndexOf(DraftQuoteParamKeys.PARAM_OVERRIDE_TYPE);
	            value = value.substring(0, index);
	        }
	        else if(value.endsWith(DraftQuoteParamKeys.SAAS_RENWL_SUFFIX)){ //refer to rtc#207982
                int index = value.lastIndexOf(DraftQuoteParamKeys.SAAS_RENWL_SUFFIX);
                value = value.substring(0,index);
			} else if (value
					.endsWith(DraftQuoteParamKeys.LINE_NEW_SERVICE_SUFFIX)) {
				int index = value
						.lastIndexOf(DraftQuoteParamKeys.LINE_NEW_SERVICE_SUFFIX);
				value = value.substring(0, index);
            }else if(value.endsWith(DraftQuoteParamKeys.LINE_DIV_HIDDEN)){
            	int index = value.lastIndexOf(DraftQuoteParamKeys.LINE_DIV_HIDDEN);
            	value = value.substring(0,index);
            }
	        else if(value.endsWith(DraftQuoteParamKeys.SAAS_MGRTN_SUFFIX)){
                int index = value.lastIndexOf(DraftQuoteParamKeys.SAAS_MGRTN_SUFFIX);
                value = value.substring(0,index);
            }
            //for auto renewal model
	        else if (value.endsWith(DraftQuoteParamKeys.PARAM_RENEWAL_MODEL_SUBSCRPTN)){
	        	int index = value.lastIndexOf(DraftQuoteParamKeys.PARAM_RENEWAL_MODEL_SUBSCRPTN);
	        	value = value.substring(0,index);
	        } else if (value.endsWith(DraftQuoteParamKeys.PARAM_RENEWAL_MODEL_CONFIGURTN)){
	        	int index = value.lastIndexOf(DraftQuoteParamKeys.PARAM_RENEWAL_MODEL_CONFIGURTN);
	        	value = value.substring(0, index);
	        }
            //for appliance item
	        else if(value.endsWith(DraftQuoteParamKeys.APPLNC_MTM_TYPE)){
	        	int index = value.lastIndexOf(DraftQuoteParamKeys.APPLNC_MTM_TYPE);
	        	value = value.substring(0,index);
	        } else if(value.endsWith(DraftQuoteParamKeys.APPLNC_MTM_MODEL)){
	        	int index = value.lastIndexOf(DraftQuoteParamKeys.APPLNC_MTM_MODEL);
	        	value = value.substring(0,index);
	        } else if(value.endsWith(DraftQuoteParamKeys.APPLNC_MTM_SERIAL_NUMBER)){
	        	int index = value.lastIndexOf(DraftQuoteParamKeys.APPLNC_MTM_SERIAL_NUMBER);
	        	value = value.substring(0,index);
	        } else if(value.endsWith(DraftQuoteParamKeys.APPLNC_POC_IND)){
	        	int index = value.lastIndexOf(DraftQuoteParamKeys.APPLNC_POC_IND);
	        	value = value.substring(0,index);
	        } else if(value.endsWith(DraftQuoteParamKeys.APPLNC_PRIOR_POC)){
	        	int index = value.lastIndexOf(DraftQuoteParamKeys.APPLNC_PRIOR_POC);
	        	value = value.substring(0,index);
	        } else if(value.endsWith(DraftQuoteParamKeys.APPLNC_ID)){
	        	int index = value.lastIndexOf(DraftQuoteParamKeys.APPLNC_ID);
	        	value = value.substring(0,index);
	        } else if (value.endsWith(DraftQuoteParamKeys.NON_IBM_MACHINE_SERIAL_NUM)) {
	        	int index = value.lastIndexOf(DraftQuoteParamKeys.NON_IBM_MACHINE_SERIAL_NUM);
	        	value = value.substring(0,index);
	        } else if(value.endsWith(DraftQuoteParamKeys.NON_IBM_MODEL)) {
	        	int index = value.lastIndexOf(DraftQuoteParamKeys.NON_IBM_MODEL);
	        	value = value.substring(0,index);
	        }else if(value.endsWith(DraftQuoteParamKeys.APPLNC_DEPLOY_ASSOCIATION)) {
	        	int index = value.lastIndexOf(DraftQuoteParamKeys.APPLNC_DEPLOY_ASSOCIATION);
	        	value = value.substring(0,index);
	        }else if(value.endsWith(DraftQuoteParamKeys.APPLNC_DEPLOY_ID)) {
	        	int index = value.lastIndexOf(DraftQuoteParamKeys.APPLNC_DEPLOY_ID);
	        	value = value.substring(0,index);
	        }else if(value.endsWith(DraftQuoteParamKeys.APPLNC_DEPLOY_ID_SYSTEM)) {
	        	int index = value.lastIndexOf(DraftQuoteParamKeys.APPLNC_DEPLOY_ID_SYSTEM);
	        	value = value.substring(0,index);
	        }else if(value.endsWith(DraftQuoteParamKeys.APPLNC_DEPLOY_ID_VALID)) {
	        	int index = value.lastIndexOf(DraftQuoteParamKeys.APPLNC_DEPLOY_ID_VALID);
	        	value = value.substring(0,index);
	        }

            //for yty
	        else if(value.endsWith(DraftQuoteParamKeys.YTY_GROWTH_SUFFIX)){
	        	int index = value.lastIndexOf(DraftQuoteParamKeys.YTY_GROWTH_SUFFIX);
	        	value = value.substring(0,index);
	        }

	        else if (value.endsWith(DraftQuoteParamKeys.PRICE_DISCOUNT_RADIO)){
	        	int index = value.lastIndexOf(DraftQuoteParamKeys.PRICE_DISCOUNT_RADIO);
	        	value = value.substring(0,index);
	        }
            
            //Appliance#99
	        else if (value.endsWith(DraftQuoteParamKeys.PARAM_CUST_REQSTD_ARRIVAL_DATE_SUFFIX)){
	        	int index = value.lastIndexOf(DraftQuoteParamKeys.PARAM_CUST_REQSTD_ARRIVAL_DATE_SUFFIX);
	        	value = value.substring(0,index);
	        }
            //
	        else if (value.endsWith(DraftQuoteParamKeys.PARAM_LINEITEM_CUST_REQSTD_ARRIVL_DAY)){
	        	int index = value.lastIndexOf(DraftQuoteParamKeys.PARAM_LINEITEM_CUST_REQSTD_ARRIVL_DAY);
	        	value = value.substring(0,index);
	        }
	        else if (value.endsWith(DraftQuoteParamKeys.PARAM_LINEITEM_CUST_REQSTD_ARRIVL_MONTH)){
	        	int index = value.lastIndexOf(DraftQuoteParamKeys.PARAM_LINEITEM_CUST_REQSTD_ARRIVL_MONTH);
	        	value = value.substring(0,index);
	        }
	        else if (value.endsWith(DraftQuoteParamKeys.PARAM_LINEITEM_CUST_REQSTD_ARRIVL_YEAR)){
	        	int index = value.lastIndexOf(DraftQuoteParamKeys.PARAM_LINEITEM_CUST_REQSTD_ARRIVL_YEAR);
	        	value = value.substring(0,index);
	        }
	        else if (value.endsWith(DraftQuoteParamKeys.PARAM_LINEITEM_APP_SEND_MFG_FLG)){
	        	int index = value.lastIndexOf(DraftQuoteParamKeys.PARAM_LINEITEM_APP_SEND_MFG_FLG);
	        	value = value.substring(0,index);
	        }
	        else if (value.endsWith(DraftQuoteParamKeys.PARAM_ORIGINAL_HEADER_CRAD)){
	        	int index = value.lastIndexOf(DraftQuoteParamKeys.PARAM_ORIGINAL_HEADER_CRAD);
	        	value = value.substring(0,index);
	        }
	        else if (value.endsWith(DraftQuoteParamKeys.PARAM_CUST_REQSTD_ARRIVAL_DATE_ORG_SUFFIX)){
	        	int index = value.lastIndexOf(DraftQuoteParamKeys.PARAM_CUST_REQSTD_ARRIVAL_DATE_ORG_SUFFIX);
	        	value = value.substring(0,index);
	        }
            else {
                value = UN_SUPPORT_PARAM;
            }

        }
        return value;
    }

    private LineItemParameter getLineItem(String key) {
        if (key != null && key.trim().length() != 0) {
            LineItemParameter item = (LineItemParameter) items.get(key);
            if (null == item) {
                item = new LineItemParameter();
                item.key = key;
            }
            items.put(key, item);
            return item;
        }
        return null;
    }

    public String getPartQty(String key) {
        LineItemParameter item = (LineItemParameter) items.get(key);
        return item.quantity;
    }
    public void setPartQty(String key, String qty){

        LineItemParameter item = (LineItemParameter) items.get(key);
        LogContextFactory.singleton().getLogContext().debug(this,"try to set quantity : "+qty + " for "+ key);
        item.quantity = qty;
    }

    public int getPartQtyInteger(String key) {
        String qty = getPartQty(key);
        return Integer.valueOf(qty).intValue();
    }

    public int getManualSortOrderInteger(String key) {
        String order = getManualSortOrder(key);
        return Integer.valueOf(order).intValue();
    }

    public String getOvrdStartDateFlag(String key) {
        LineItemParameter item = (LineItemParameter) items.get(key);
        return item.ovrdStartDateFlag;
    }

    public String getOvrdEndDateFlag(String key) {
        LineItemParameter item = (LineItemParameter) items.get(key);
        return item.ovrdEndDateFlag;
    }

    public int getMaintainAdditionalYearInteger(String key) {
        LineItemParameter item = (LineItemParameter) items.get(key);
        LogContextFactory.singleton().getLogContext().debug(this, "getMaintainAdditionalYearInteger:  " + item.maintainAddtionalYear);
        if(item.maintainAddtionalYear == null || "".equals(item.maintainAddtionalYear.trim())){
            return -1;
        }

        return Integer.valueOf(item.maintainAddtionalYear).intValue();
    }
    public boolean getPVUQtyManuallyEntered(String key){
        LineItemParameter item = (LineItemParameter) items.get(key);
        return item.pvuQtyManuallyEntered;

    }
    public String getManualSortOrder(String key) {
        LineItemParameter item = (LineItemParameter) items.get(key);
        return item.manualSortOrder;
    }

    public String getPartDiscount(String key) {
        LineItemParameter item = (LineItemParameter) items.get(key);
        return item.discountPercent;
    }

    public String getPartPrice(String key) {
        LineItemParameter item = (LineItemParameter) items.get(key);
        return item.overridePrice;
    }

    public String getOriginalPartPrice(String key) {
        LineItemParameter item = (LineItemParameter) items.get(key);
        return item.originalOverridePrice;
    }

    public boolean getPartProrateAnni(String key) {
        LineItemParameter item = (LineItemParameter) items.get(key);
        return item.prorateFirstYearToAnni;
    }

    public Date getPartEndDate(String key) {
        LineItemParameter item = (LineItemParameter) items.get(key);
        return item.maintEndDate;
    }

    public Date getPartStartDate(String key) {
        LineItemParameter item = (LineItemParameter) items.get(key);
        return item.maintStartDate;
    }

    public String getRQComment(String key) {
        LineItemParameter item = (LineItemParameter) items.get(key);
        return item.rqChangeReason;
    }

    public Date getRQStartDate(String key) {
        LineItemParameter item = (LineItemParameter) items.get(key);
        return item.getRQStartDate();
    }

    public Date getRQEndDate(String key) {
        LineItemParameter item = (LineItemParameter) items.get(key);
        return item.getRQEndDate();
    }

    public boolean isFTL(String key) {
        LineItemParameter item = (LineItemParameter) items.get(key);
        return item.isFTL;
    }

    public boolean parseBoolean(String value) {
        try {
            return Boolean.valueOf(value).booleanValue();
        } catch (Throwable e) {
            LogContextFactory.singleton().getLogContext().debug(this, "parse boolean value failed : value = " + value);
            return false;
        }
    }

    /**
     * @return Returns the items.
     */
    public HashMap getItems() {
        return items;
    }

    /**
     * @return Returns the quoteDiscountPercent.
     */
    public String getQuoteDiscountPercent() {
        return quoteDiscountPercent;
    }

    /**
     * @return Returns the prevOfferPrice.
     */
    public String getPrevOfferPrice() {
        return prevOfferPrice;
    }

    /**
     * @return Returns the PartnerDiscountPercent.
     */
    public String getPartnerDiscountPercent() {
        return partnerDiscountPercent;
    }

    public boolean isEnableCmprssCvrage(){
        return this.enableCmprssCvrage;
    }

    /**
     * @return Returns the recaculateFlag.
     */
    public int getRecaculateFlag() {
        return recaculateFlag;
    }

    /**
     * @return Returns the useGSAPricing.
     */
    public String getUseGSAPricing() {
        return useGSAPricing;
    }

    public boolean isRenwalQuote(){
        return QuoteConstants.QUOTE_TYPE_RENEWAL.equals(getQuoteType());
    }
    public boolean isSalesQuote(){
        return QuoteConstants.QUOTE_TYPE_SALES.equals(getQuoteType());
    }
    /**
     * @return Returns the rqEditable.
     */
    public boolean isRqEditable() {
        return rqEditable;
    }
    /*public boolean isOverridePriceSpecified(String key){

        String overridePrice = getPartPrice(key);
        try{
            Double.valueOf(overridePrice);
            return true;
        }
        catch(Throwable e){
            LogContextFactory.singleton().getLogContext().error(this,"parse overridePrice error:"+overridePrice );

        }
        return false;
    }*/
    /*public boolean isDiscountSpecified(String key){

        String discountPercent = this.getPartDiscount(key);
        try{
            // if user input a value (not 0.0), we think discount is specificed
            if(DecimalUtil.isNotEqual(0.0,Double.valueOf(discountPercent).doubleValue())){
                return true;
            }
        }
        catch(Throwable e){
            LogContextFactory.singleton().getLogContext().error(this,"parse discount error :"+discountPercent);

        }
        return false;
    }*/

    /**
     * @param key
     * @return
     */
    public int getAdditionalMaintSeqNum(String key) {
        try{
            LineItemParameter item = (LineItemParameter) items.get(key);
            return Integer.valueOf(item.additionalMaintSeqNum).intValue();
        }catch(Throwable e ){
            LogContextFactory.singleton().getLogContext().debug(this,"parse AdditionalMaintSeqNum error :");
            return -1;
        }

    }
    /**
     * @return Returns the offerPrice.
     */
    public String getOfferPrice() {
        return offerPrice;
    }
    /**
     * @param offerPrice The offerPrice to set.
     */
    public void setOfferPrice(String offerPrice) {
        this.offerPrice = offerPrice;
    }

    public Quote getQuote() {
        return quote;
    }

    public void setQuote(Quote quote){
        this.quote = quote;
    }
	/**
	 * @return Returns the retroactiveCvrgReason.
	 */
	public String getBackDatingComment() {
		return backDatingComment;
	}

	public List getReasonCodes(){
		return reasonCodes;
	}

    /**
     * @param key
     * @return
     */
    public String getBpOverrideDiscount(String key) {
        LineItemParameter item = (LineItemParameter) items.get(key);
        return item.bpOverrideDiscount;
    }

    /**
     * @param key
     * @return
     */
    public String getEntitledExtPrice(String key) {
        LineItemParameter item = (LineItemParameter) items.get(key);
        return item.entitledExtPrice;
    }

    /**
     * @param key
     * @return
     */
    public String getBpChannelExtndPrice(String key) {
        LineItemParameter item = (LineItemParameter) items.get(key);
        return item.bpChannelExtndPrice;
    }

    public String getEntitledUnitPrice(String key){
    	LineItemParameter item = (LineItemParameter) items.get(key);
    	return item.overrideEntitledPrice;
    }

    public String getCmprssCvrageMonth(String key){
    	LineItemParameter item = (LineItemParameter) items.get(key);
    	return item.cmprssCvrageMonth;
    }

    public String getCmprssCvrageDisc(String key){
    	LineItemParameter item = (LineItemParameter) items.get(key);
    	return item.cmprssCvrageDisc;
    }

    public boolean isAddDuplicatePart(){
        return this.isAddDuplicatePart;
    }

    public boolean isDeleteLineItem(){
        return this.isDeleteLineItem;
    }

    /**
     * @return Returns the offerAction.
     */
    public int getOfferAction() {
        return offerAction;
    }
    /**
     * @param offerAction The offerAction to set.
     */
    public void setOfferAction(int offerAction) {
        this.offerAction = offerAction;
    }
    /**
     * @return Returns the isExceedLimit.
     */
    public boolean isExceedLimit() {
        return isExceedLimit;
    }
    /**
     * @param isExceedLimit The isExceedLimit to set.
     */
    public void setExceedLimit(boolean isExceedLimit) {
        this.isExceedLimit = isExceedLimit;
    }

    /**
     * @param key
     * @return
     * String
     *  get coverage term
     */
    public String getCvrageTerm(String key){
    	LineItemParameter item = (LineItemParameter) items.get(key);
    	return item.cvrageTerm;
    }

    /**
     * @param key
     * @return
     * String
     *  get the billing frequency
     */
    public String getBillingFrequency(String key){
    	LineItemParameter item = (LineItemParameter) items.get(key);
    	return item.billingFrequency;
    }

    /**
     * @param key
     * @return
     * String
     *  get the billing frequency
     */
    public String getOverrideType(String key){
    	LineItemParameter item = (LineItemParameter) items.get(key);
    	return item.overrideType;
    }
    /**
     * Refer to rtc#207982
     * @param key
     * @return
     * String
     *  get coverage term
     */
    public boolean getSaasRenwl(String key){
    	LineItemParameter item = (LineItemParameter) items.get(key);
    	return item.saasRenwl;
    }

	public Boolean isLineItemNewService(String key) {
		LineItemParameter item = (LineItemParameter) items.get(key);
		return item.isLineItemNewService;
	}

    public boolean getSaasMgrtn(String key){
    	LineItemParameter item = (LineItemParameter) items.get(key);
    	return item.saasMgrtn;
    }

    public String getRenwlModeConfigutn(String configurationId){
    	String configRenwlModel = "";
    	if (configurationId != null && configurationId.trim().length() != 0){
    		Object obj = renwlModeConfigrtnMap.get(configurationId);
    		if (obj != null){
    			configRenwlModel = (String)renwlModeConfigrtnMap.get(configurationId);
    		}
    	}
    	return configRenwlModel;
    }

	public String getDeletePartNum() {
		return deletePartNum;
	}

	public String getDeletePartSeq() {
		return deletePartSeq;
	}

    public String getPrvsningDays(String configurationId) {
        if (configurationId != null && configurationId.trim().length() != 0) {
            return (String)prvsningDaysMap.get(configurationId);
        }
        return null;
    }

    public Boolean isTermExtension(String configurationId){
		Boolean termFlag = false;
        if (configurationId != null && configurationId.trim().length() != 0) {

			Object flag = termExtensionMap.get(configurationId);
			if (flag != null) {
				termFlag = (Boolean) flag;
			}
        }
		return termFlag;
    }

    public ServiceDateModType getServiceDateModType(String configurationId){
        if (configurationId != null && configurationId.trim().length() != 0) {
            return (ServiceDateModType)serviceDateModTypeMap.get(configurationId);
        }
        return null;
    }

    public Date getServiceDate(String configurationId){
        if (configurationId != null && configurationId.trim().length() != 0) {
            return (Date)serviceDateMap.get(configurationId);
        }
        return null;
    }

	public String getChargeAgreementNum() {
		return chargeAgreementNum;
	}
	public void setChargeAgreementNum(String chargeAgreementNum) {
		this.chargeAgreementNum = chargeAgreementNum;
	}

	public Boolean getIsSaasMigration() {
		return isSaasMigration;
	}

	public void setIsSaasMigration(Boolean isSaasMigration) {
		this.isSaasMigration = isSaasMigration;
	}

	public Boolean getIsSaasRenewal() {
		return isSaasRenewal;
	}

	public void setIsSaasRenewal(Boolean isSaasRenewal) {
		this.isSaasRenewal = isSaasRenewal;
	}

	public Boolean getIsMonthlyMigration() {
		return isMonthlyMigration;
	}

	public void setIsMonthlyMigration(Boolean isMonthlyMigration) {
		this.isMonthlyMigration = isMonthlyMigration;
	}

	public Boolean getIsMonthlyRenewal() {
		return isMonthlyRenewal;
	}

	public void setIsMonthlyRenewal(Boolean isMonthlyRenewal) {
		this.isMonthlyRenewal = isMonthlyRenewal;
	}

	public HashMap getCoTermsMap() {
		return coTermsMap;
	}

	public HashMap getTermExtensionMap(){
		return termExtensionMap;
	}

	public boolean isRemoveConfgrtn() {
		return isRemoveConfgrtn;
	}

	public String getRemoveConfigrtnId() {
		return removeConfigrtnId;
	}

	//appliance parts
	public String getApplianceId(String key){
		LineItemParameter item = (LineItemParameter) items.get(key);
		return item.applianceId != null ? item.applianceId.trim():"";
	}

	public String getMachineType(String key){
		LineItemParameter item = (LineItemParameter) items.get(key);
		return item.machineType != null ? item.machineType.trim():"" ;
	}

	public String getMachineModel(String key){
		LineItemParameter item = (LineItemParameter) items.get(key);
		return item.machineModel != null ? item.machineModel.trim():"";
	}

	public String getMachineSerialNumber(String key){
		LineItemParameter item = (LineItemParameter) items.get(key);
		return item.machineSerialNumber != null ? item.machineSerialNumber.trim():"";
	}

	public String getApplncPocInd(String key){
		LineItemParameter item = (LineItemParameter) items.get(key);
		return item.applncPocInd != null ? item.applncPocInd.trim():DraftQuoteParamKeys.PARAM_NO;
	}

	public String getApplncPriorPoc(String key){
		LineItemParameter item = (LineItemParameter) items.get(key);
		return item.applncPriorPoc != null ? item.applncPriorPoc.trim():DraftQuoteParamKeys.PARAM_NO;
	}
	
	//Appliance#145
	public String getNonIBMSerialNum(String key) {
		LineItemParameter item = (LineItemParameter) items.get(key);
		return item.nonIBMSerialNumber != null ? item.nonIBMSerialNumber.trim():DraftQuoteParamKeys.PARAM_NO;
	}
	
	public String getNonIBMModel(String key){
		LineItemParameter item = (LineItemParameter) items.get(key);
		return item.nonIBMModel != null ? item.nonIBMModel.trim():DraftQuoteParamKeys.PARAM_NO;
	}

	//Appliance#99
	
	public boolean getAppSendMFGFlg(String key){
		LineItemParameter item = (LineItemParameter) items.get(key);
		return item.appSendtoMFG;
	}
	
	public Date getLineItemCRAD(String key){
		LineItemParameter item = (LineItemParameter) items.get(key);
		return item.custReqArrlDate != null ? item.custReqArrlDate:null;
	}
	
	public Date getHeaderCRAD(String key){
		LineItemParameter item = (LineItemParameter) items.get(key);
		return item.headerCRAD != null ? item.headerCRAD:null;
	}
	
	public Date getOrgLineItemCRAD(String key){
		LineItemParameter item = (LineItemParameter) items.get(key);
		return item.lineItemCRAD != null ? item.lineItemCRAD:null;
	}
	
	
	public String getYty(String key){
		LineItemParameter item = (LineItemParameter) items.get(key);
		return item != null?item.yty:null;
	}

	public String getYtyRadio(String key){
		LineItemParameter item = (LineItemParameter) items.get(key);
		return item!=null?item.ytyRadio:null;
	}

	public String getQuoteYty(){
		return quoteYty;
	}



	public String getRenwlModeSubscrptn(String key){
		String renwlModeSubscrptn = "";
		LineItemParameter item = (LineItemParameter) items.get(key);
		if (item != null){
			renwlModeSubscrptn = item.renwlModeSubscrptn;
		}
		return renwlModeSubscrptn != null ? renwlModeSubscrptn.trim():"";
	}

	public String getBillingFrequencyChangeSelect() {
		return billingFrequencyChangeSelect;
	}

	public void setBillingFrequencyChangeSelect(String	billingFrequencyChangeSelect) {
		this.billingFrequencyChangeSelect =	billingFrequencyChangeSelect;
	}

	public String getSetLineToRsvpSrpId() {
		return setLineToRsvpSrpId;
	}

	public void setSetLineToRsvpSrpId(String setLineToRsvpSrpId) {
		this.setLineToRsvpSrpId = setLineToRsvpSrpId;
	}
    //appliance deployment model
    public int getDeployModelOption(String key) {
    	LineItemParameter item = (LineItemParameter) items.get(key);
		return item.deployModelOption;
    }
    
    public String getDeployModelId(String key) {
		LineItemParameter item = (LineItemParameter) items.get(key);
		if (PartPriceConstants.DEPLOYMENT_NOT_ON_QUOTE.equals(getDeployModelOption(key))){
			return StringUtils.trimToEmpty(item.deployModelId);
		}else if(PartPriceConstants.DEPLOYMENT_SELECT_DEFAULT.equals(getDeployModelOption(key))){
			return null;
		}
		return StringUtils.trimToEmpty(item.deployModelIdSystem);
    }

    public boolean isDeployModelValid(String key) {
        LineItemParameter item = (LineItemParameter) items.get(key);
        return item.deployModelValid;
    }

}