package com.ibm.dsw.quote.configurator.preprocessor;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.ibm.dsw.quote.common.config.PartPriceConstants;
import com.ibm.dsw.quote.common.domain.ServiceDateModType;
import com.ibm.dsw.quote.customer.action.PrepareConfiguratorRedirectDataBaseAction;
import com.ibm.dsw.quote.customer.config.CustomerParamKeys;
import com.ibm.dsw.quote.customerlist.domain.ActiveService;
import com.ibm.dsw.quote.customerlist.domain.ExtensionActiveService;
import com.ibm.dsw.quote.customerlist.domain.RedirectConfiguratorDataBasePack;
import com.ibm.dsw.quote.draftquote.util.date.DateUtil;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;

public class FctToPAPreProcessor extends ConfigBasePreProcessor {
    protected static final LogContext logger = LogContextFactory.singleton().getLogContext();

    private static final int DURING_INITIAL_TERM = 0;
    private static final int DURING_RENEWAL_TERM = 1;
    private static final int DURING_LAST_MONTH_CURRENT_TERM = 2;
    private static final int DURING_CAENDDATE_BEFORE_TODAY=3;

	public FctToPAPreProcessor(){
	}

	public FctToPAPreProcessor(ConfigBasePreProcessor parentProcess){
		this.parentProcess = parentProcess;
	}

	@Override
	public void preProcessImp(RedirectConfiguratorDataBasePack dataPack) {
		List<ActiveService> ass = dataPack.getActiveServices();
		if(ass == null || ass.size() < 1){
			return;
		}

		Date newxtRenwlDate = null;
		String renwlModelCode = "";
		int orgTerm = -1;
		int renwlTerm = -1;
		Date cotermEndDate = dataPack.getCotermEndDate();
		ServiceDateModType serviceDateModType = dataPack.getServiceModelType();
		Date migrationDate = null;
		boolean isTermExtension = dataPack.isTermExtension();
		for(ActiveService as: ass){
			if(!as.isSbscrptnFlag()){
				continue;
			}
			if(orgTerm<0 && as.getTerm()!=null && Integer.parseInt(as.getTerm())>0){
				orgTerm = Integer.parseInt(as.getTerm());
			}
			if(migrationDate == null && as.getOrdAddDate()!=null){
				migrationDate = as.getOrdAddDate();
			}
			if(renwlTerm < 0 && as.getRenwlTermMths()!= null && as.getRenwlTermMths().intValue() >= 0){
				renwlTerm = as.getRenwlTermMths().intValue();
			}
			if("".equals(renwlModelCode) && as.getRenwlModelCode() != null && !("".equals(as.getRenwlModelCode()))){
				renwlModelCode = as.getRenwlModelCode();
			}
			if(orgTerm>0 && renwlTerm>0){
				break;
			}

		}

		if(orgTerm<0 ){
			return;
		}

		int calcTerm = -1;
		int finalTerm = -1;

		if(cotermEndDate == null){
			finalTerm = orgTerm;
			calcTerm  = orgTerm;
		}else{
			// not extension term old logic
			if(!isTermExtension){
				for(ActiveService as: ass){
					if(!as.isSbscrptnFlag()){
						continue;
					}
					orgTerm = as.getTerm() == null ? 0 : Integer.parseInt(as.getTerm());
					migrationDate = as.getOrdAddDate();
					renwlTerm = as.getRenwlTermMths() == null ? 0 : as.getRenwlTermMths().intValue();
					renwlModelCode = as.getRenwlModelCode();
				
					if(PartPriceConstants.ConfigrtnActionCode.FCT_TO_PA_FNL.equals(dataPack.getConfigrtnActionCode())){
						if(dataPack.getProvisioningDate()!=null)
							calcTerm = DateUtil.calculateFullCalendarMonths(dataPack.getProvisioningDate(), cotermEndDate);
						else
							calcTerm = DateUtil.calculateFullCalendarMonths(migrationDate, cotermEndDate);
					}else if(PartPriceConstants.ConfigrtnActionCode.ADD_TRD.equals(dataPack.getConfigrtnActionCode())
							|| PartPriceConstants.ConfigrtnActionCode.NEW_CA_CT.equals(dataPack.getConfigrtnActionCode()))
						calcTerm = DateUtil.calculateFullMonths(dataPack.getProvisioningDate(), cotermEndDate);
	
	
					//Per Sharon's response, update to below codes.Notes://CAMDB10/85256B890058CBA6/5D3D446FEB23FC918525718E006EAFDE/2DA8320A839F661985257A7E004AADBA
					if(calcTerm>0){
						finalTerm = calcTerm;
					//I took out logic for if calc term == 0.  It can match the logic for when calc term is < 0
					}else{
						if(calcTerm == 0 && "C".equalsIgnoreCase(renwlModelCode)){
							finalTerm = calcTerm;
						}else{
							if( renwlTerm <= 0){//Notes://CAMDB10/85256B890058CBA6/221524D57388DE9185256C01007D10E6/86498DF94E74241C85257A72005ACD1C
								if(orgTerm <= 12)
									renwlTerm = orgTerm;
								else
									renwlTerm = 12;
							}
							finalTerm = renwlTerm + calcTerm;
							if(finalTerm <= 0){
								finalTerm = renwlTerm;
							}
						}
					}
					
					dataPack.setFctToPaCalcTerm(Integer.toString(calcTerm));	
					String finalTermStr = Integer.toString(finalTerm);
					if(as.getTerm()!=null){
						as.setTerm(finalTermStr);
					}
				}
			}else{
				//only select service end date need to re-calculate term
				if(serviceDateModType != null && "CE".equals(serviceDateModType.toString())){
					List<ExtensionActiveService> extensionActiveServices = processExtensionActiveServices(dataPack);

					for(ExtensionActiveService eas : extensionActiveServices){

						int remainingTerm = 0;
						int calculateRemaningTerm = 0;
						Date caLineItemStartDate = eas.getCaLineItemStartDate();
						Date caLineItemEndDate = eas.getCaLineItemEndDate();
						Date current = DateUtil.getCurrentDate();
						Date renewalEndDate = eas.getRenewalEndDate();
						renwlModelCode = eas.getRenwlModelCode();
						newxtRenwlDate = eas.getNewxtRenwlDate();
						renwlTerm = eas.getRenwlTermMths() == null ? 0 : eas.getRenwlTermMths().intValue();

						if("R".equals(renwlModelCode)){
							renwlTerm = 12;
						}
						if(caLineItemEndDate == null){
							if(newxtRenwlDate == null){
								calcTerm = DateUtil.calculateFullMonths(dataPack.getProvisioningDate(),cotermEndDate);
								finalTerm = calculateFinalTerm(calcTerm,renwlTerm,orgTerm);
							}else{
								if(cotermEndDate != null){
									if(cotermEndDate.after(newxtRenwlDate)){
										remainingTerm = DateUtil.calculateFullMonths(dataPack.getProvisioningDate(),newxtRenwlDate);
										calcTerm = DateUtil.calculateFullMonths(newxtRenwlDate,cotermEndDate);
									}else{
										remainingTerm = DateUtil.calculateFullMonths(dataPack.getProvisioningDate(),cotermEndDate);
										calcTerm = 0;
									}
								}
								finalTerm = remainingTerm + calcTerm;
							}
						}else{
							if(caLineItemStartDate == null){
								caLineItemStartDate = DateUtil.plusMonth(caLineItemEndDate,-orgTerm);
							}

							int period = calculateQuotePeriod(caLineItemStartDate,caLineItemEndDate,current,newxtRenwlDate);
							if(period == FctToPAPreProcessor.DURING_CAENDDATE_BEFORE_TODAY){

								if (renewalEndDate != null){
									remainingTerm = calculateRemainingTerm(period,renewalEndDate,cotermEndDate,renwlModelCode,dataPack.getProvisioningDate());
									calcTerm = calculateExtensionTerm(period,renewalEndDate,cotermEndDate,renwlModelCode,renwlTerm);
									finalTerm = calcTerm + remainingTerm;
									//Fix ExtensionTerm and finalTerm value due to 15.2 Billing Flexibility Requirement 
									int partialTerm = calculatePartialTerm(dataPack.getProvisioningDate(), cotermEndDate, finalTerm);
									calcTerm += partialTerm;
									finalTerm += partialTerm;
									
								} else {
									calcTerm = DateUtil.calculateFullMonths(dataPack.getProvisioningDate(),cotermEndDate);
									// use old logic when calTerm > 0 this is finalTerm
									// else -1 (two date are in the same month) because add constrain between dataPack.getProvisioningDate() and cotermEndDate.
									finalTerm = calculateFinalTerm(calcTerm,renwlTerm,orgTerm);
									remainingTerm = finalTerm - calcTerm;
								}

								logger.debug(this, "finalTerm1["+dataPack.getChrgAgrmtNum()+"] is: "+finalTerm);
							}else{
								remainingTerm = calculateRemainingTerm(period,caLineItemEndDate,cotermEndDate,renwlModelCode,dataPack.getProvisioningDate());
								calcTerm = calculateExtensionTerm(period,caLineItemEndDate,cotermEndDate,renwlModelCode,renwlTerm);
								finalTerm = calcTerm + remainingTerm;
								//Fix ExtensionTerm and finalTerm value due to 15.2 Billing Flexibility Requirement 
								int partialTerm = calculatePartialTerm(dataPack.getProvisioningDate(), cotermEndDate, finalTerm);
								calcTerm += partialTerm;
								finalTerm += partialTerm;

							}
							calculateRemaningTerm = remainingTerm;
							if(period == FctToPAPreProcessor.DURING_RENEWAL_TERM || period == FctToPAPreProcessor.DURING_CAENDDATE_BEFORE_TODAY){
								if(remainingTerm <= 0){
									if( renwlTerm <= 0){
										if(orgTerm <= 12)
											renwlTerm = orgTerm;
										else
											renwlTerm = 12;
									}
									remainingTerm = renwlTerm + remainingTerm;
	//								if(remainingTerm > finalTerm){
	//									finalTerm = remainingTerm;
	//								}
	//								calcTerm = finalTerm - remainingTerm;
								}
							}
						}
						// if is extension ,set remainingTerm to datapack for replace term.
						eas.setFinalTerm(finalTerm);
						eas.setRemaningTerm(remainingTerm);
						eas.setCalculateRemaningTerm(calculateRemaningTerm);
						dataPack.setFctToPaCalcTerm(Integer.toString(remainingTerm));

					}

				}

			}
		}
		logger.debug(this, "calcTerm["+dataPack.getChrgAgrmtNum()+"] is: "+calcTerm);
		logger.debug(this, "renwlTerm["+dataPack.getChrgAgrmtNum()+"] is: "+renwlTerm);
		logger.debug(this, "finalTerm["+dataPack.getChrgAgrmtNum()+"] is: "+finalTerm);
		
		dataPack.setFctToPaFinalTerm(Integer.toString(finalTerm));


		dataPack.getReturnedParams().put(CustomerParamKeys.PARAM_RETURN_TO_SQO_CALCTERM, calcTerm+"");
		dataPack.setRedirectURL(PrepareConfiguratorRedirectDataBaseAction.getFinalReturnedURL(dataPack));

	}

	private List<ExtensionActiveService> processExtensionActiveServices(
			RedirectConfiguratorDataBasePack dataPack) {
		List<ActiveService> ass = dataPack.getActiveServices();
		List<ExtensionActiveService> extensionActiveServices = new ArrayList<ExtensionActiveService>();
		ExtensionActiveService eas = null;
		boolean allEndDateIsNull = true;
		for(ActiveService as : ass){
			if(as.getEndDate()!= null && as.isSbscrptnFlag()){
				allEndDateIsNull = false;
				eas = new ExtensionActiveService();
				eas.setPartNumber(as.getPartNumber());
				eas.setLineItemSeqNumber(as.getLineItemSeqNumber());
				eas.setCaLineItemStartDate(DateUtil.parseDate(as.getStartDate()));
				eas.setCaLineItemEndDate(DateUtil.parseDate(as.getEndDate()));
				eas.setNewxtRenwlDate(as.getNewxtRenwlDate());
				eas.setRenewalEndDate(as.getRenewalEndDate());
				eas.setRenwlModelCode(as.getRenwlModelCode());
				eas.setRenwlTermMths(as.getRenwlTermMths());
				extensionActiveServices.add(eas);
			}
		}
		dataPack.setExtensionActiveServices(extensionActiveServices);
		

		if(allEndDateIsNull){
			Date caLineItemStartDate = null;
			Date caLineItemEndDate = null;
			String renwlModelCode = "";
			Date newxtRenwlDate = null;
			int renwlTerm = -1;
			for(ActiveService as : ass){

				if(as.isSbscrptnFlag()){
					if(caLineItemStartDate == null && as.getStartDate() != null){
						caLineItemStartDate = DateUtil.parseDate(as.getStartDate());
					}
					if(newxtRenwlDate == null && as.getNewxtRenwlDate() != null){
						newxtRenwlDate = as.getNewxtRenwlDate();
					}
					if("".equals(renwlModelCode) && as.getRenwlModelCode() != null && !("".equals(as.getRenwlModelCode()))){
						renwlModelCode = as.getRenwlModelCode();
					}

					if(renwlTerm < 0 && as.getRenwlTermMths()!= null && as.getRenwlTermMths().intValue() >= 0){
						renwlTerm = as.getRenwlTermMths().intValue();
					}

					if(caLineItemStartDate != null && newxtRenwlDate != null
							&& StringUtils.isNotEmpty(renwlModelCode) && renwlTerm > 0){
						break;
					}
				}
			}
			eas = new ExtensionActiveService();
			eas.setCaLineItemEndDate(null);
			eas.setCaLineItemStartDate(caLineItemStartDate);
			eas.setNewxtRenwlDate(newxtRenwlDate);
			eas.setRenwlModelCode(renwlModelCode);
			eas.setAllEndDateIsNull(true);
			eas.setRenwlTermMths(Integer.valueOf(renwlTerm));

			dataPack.getExtensionActiveServices().add(eas);
		}
		return dataPack.getExtensionActiveServices();
	}

	private int calculateFinalTerm(int calcTerm,int renwlTerm,int orgTerm){
		int finalTerm = -1;
		if(calcTerm>0){
			finalTerm = calcTerm;
			//I took out logic for if calc term == 0.  It can match the logic for when calc term is < 0
		}else{
			if( renwlTerm <= 0){//Notes://CAMDB10/85256B890058CBA6/221524D57388DE9185256C01007D10E6/86498DF94E74241C85257A72005ACD1C
				if(orgTerm <= 12)
					renwlTerm = orgTerm;
				else
					renwlTerm = 12;
			}
			finalTerm = renwlTerm + calcTerm;
			if(finalTerm <= 0){
				finalTerm = renwlTerm;
			}
		}
		return finalTerm;
	}

	private int calculateRemainingTerm(int period, Date caLineItemEndDate,Date cotermEndDate,
			String renwlModelCode,Date provisioningDate) {
		int remainingTerm = 0;
		boolean compareFlag = caLineItemEndDate.before(cotermEndDate);
		if(compareFlag){
			remainingTerm = DateUtil.calculateFullMonths(provisioningDate,caLineItemEndDate);
		}else{
			remainingTerm = DateUtil.calculateFullMonths(provisioningDate,cotermEndDate);
		}
//		if(FctToPAPreProcessor.DURING_INITIAL_TERM == period){
//			// If cotermEndDate < caLineItemEndDate --> remainingTerm = cotermEndDate - current
//			//                                      --> extension = 0;
//			// else remainingTerm = cotermEndDate - current
//			//      extension = cotermEndDate - caLineItemEndDate
//			// Need to make sure there is possible the extension end date < ca line item end date.
//			if(compareFlag){
//				remainingTerm = DateUtil.calculateFullCalendarMonths(provisioningDate,caLineItemEndDate);
//			}else{
//				remainingTerm = DateUtil.calculateFullCalendarMonths(provisioningDate,cotermEndDate);
//			}
//		}else if(FctToPAPreProcessor.DURING_RENEWAL_TERM == period){
//			//remainingTerm = DateUtil.calculateFullCalendarMonths(current,caLineItemEndDate);
////				if(cotermEndDate.after(renewalEndDate)){
////					remainingTerm = DateUtil.calculateFullCalendarMonths(provisioningDate,renewalEndDate);
////				}else{
////					remainingTerm = DateUtil.calculateFullCalendarMonths(provisioningDate,cotermEndDate);
////				}
//				if(compareFlag){
//					remainingTerm = DateUtil.calculateFullCalendarMonths(provisioningDate,caLineItemEndDate);
//				}else{
//					remainingTerm = DateUtil.calculateFullCalendarMonths(provisioningDate,cotermEndDate);
//				}
//		}else if(FctToPAPreProcessor.DURING_LAST_MONTH_CURRENT_TERM == period){
//			if("R".equals(renwlModelCode)){
//				remainingTerm = 12;
//			}else{
//				//remainingTerm = DateUtil.calculateFullCalendarMonths(current,caLineItemEndDate);
//				if(compareFlag){
//					remainingTerm = DateUtil.calculateFullCalendarMonths(provisioningDate,caLineItemEndDate);
//				}else{
//					remainingTerm = DateUtil.calculateFullCalendarMonths(provisioningDate,cotermEndDate);
//				}
//			}
//		}
		return remainingTerm;
	}

	private int calculateExtensionTerm(int period, Date caLineItemEndDate,
			Date cotermEndDate, String renwlModelCode, int renwlTerm) {
		boolean compareFlag = caLineItemEndDate.before(cotermEndDate);
		int extensionTerm = 0;
		if(compareFlag){
			extensionTerm = DateUtil.calculateFullMonths(caLineItemEndDate,cotermEndDate);
		}else{
			extensionTerm = 0;
		}
//		if(FctToPAPreProcessor.DURING_INITIAL_TERM == period){
//			if(compareFlag){
//				extensionTerm = DateUtil.calculateFullCalendarMonths(caLineItemEndDate,cotermEndDate);
//			}else{
//				extensionTerm = 0;
//			}
//		}else if(FctToPAPreProcessor.DURING_RENEWAL_TERM == period){
////			if("R".equals(renwlModelCode)){
////				extensionTerm = 12;
////			}else{
////				extensionTerm = renwlTerm;
////			}
////			if(cotermEndDate != null && renewalEndDate != null){
////				if(cotermEndDate.after(renewalEndDate)){
////					extensionTerm = DateUtil.calculateFullCalendarMonths(renewalEndDate,cotermEndDate);
////				}else{
////					// #todo
////					//extensionTerm = DateUtil.calculateFullCalendarMonths(cotermEndDate,renewalEndDate);
////					extensionTerm = 0;
////				}
////			}
//			if(compareFlag){
//				extensionTerm = DateUtil.calculateFullCalendarMonths(caLineItemEndDate,cotermEndDate);
//			}else{
//				extensionTerm = 0;
//			}
//		}else if(FctToPAPreProcessor.DURING_LAST_MONTH_CURRENT_TERM == period){
//			if("R".equals(renwlModelCode)){
//				extensionTerm = 12;
//			}else{
//				//extensionTerm = DateUtil.calculateFullCalendarMonths(caLineItemEndDate,cotermEndDate);
//				if(compareFlag){
//					extensionTerm = DateUtil.calculateFullCalendarMonths(caLineItemEndDate,cotermEndDate);
//				}else{
//					extensionTerm = 0;
//				}
//			}
//		}
		return extensionTerm;
	}

	private int calculateQuotePeriod(Date caLineItemStartDate,
			Date caLineItemEndDate, Date current, Date newxtRenwlDate) {
		if(DateUtil.isDateBeforeToday(caLineItemEndDate) && newxtRenwlDate != null){
			return FctToPAPreProcessor.DURING_CAENDDATE_BEFORE_TODAY;
		}

		if(( DateUtil.isDateBeforeToday(caLineItemStartDate)||DateUtil.isEqual(caLineItemStartDate, current) )
				&& ( DateUtil.isDateAfterToday(caLineItemEndDate) ||DateUtil.isEqual(caLineItemEndDate, current) )
				&& newxtRenwlDate == null){
			// $(CA Line Item End Date) - $(Quote Date) < 30 days
			Date firstDayOfMonth = DateUtil.moveToNextFirstDayOfMonth(DateUtil.plusMonth(caLineItemEndDate,-1));
			Date lastDayOfMonth = DateUtil.moveToLastDayofMonth(caLineItemEndDate);
			if((DateUtil.isDateBeforeToday(firstDayOfMonth) || DateUtil.isEqual(firstDayOfMonth,current))
					&& DateUtil.isDateAfterToday(lastDayOfMonth) || DateUtil.isEqual(lastDayOfMonth,current)){
				return FctToPAPreProcessor.DURING_LAST_MONTH_CURRENT_TERM;
			}else{
				return FctToPAPreProcessor.DURING_INITIAL_TERM;
			}
		}
		// #todo
//		if(( DateUtil.isDateBeforeToday(caLineItemStartDate)||DateUtil.isEqual(caLineItemStartDate, current) )
//				&& ( DateUtil.isDateAfterToday(caLineItemEndDate) ||DateUtil.isEqual(caLineItemEndDate, current) )
//				&& DateUtil.isDateAfterToday(newxtRenwlDate)){
//			return FctToPAPreProcessor.DURING_RENEWAL_TERM;
//		}
		if(newxtRenwlDate != null){
			return FctToPAPreProcessor.DURING_RENEWAL_TERM;
		}


		return -1;
	}

	/**
	 * Used to Fix term Extension and Renewal Scenarios Term Calculate twice lead finalTerm with wrong value
	 * @param startDate : Provisioning started Date
	 * @param endDate : Flexibility Service End Date Choose On Page
	 * @param unfixFinalTerm
	 * @return The finalTerm Fix value, as default we look the DateUtil.calculateFullMonths(startDate, endDate) as correct term value
	 * @return 1, 0, -1
	 */
	private int calculatePartialTerm(Date startDate, Date endDate, int unfixFinalTerm)
	{
		return DateUtil.calculateFullMonths(startDate, endDate) - unfixFinalTerm;
	}
}
