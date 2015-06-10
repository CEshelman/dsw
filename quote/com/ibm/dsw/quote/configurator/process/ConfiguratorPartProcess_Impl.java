package com.ibm.dsw.quote.configurator.process;

import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.ibm.dsw.quote.base.config.CustomerConstants;
import com.ibm.dsw.quote.base.config.QuoteConstants;
import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.common.config.PartPriceConstants;
import com.ibm.dsw.quote.common.config.PartPriceConstants.PartRelNumAndTypeDefault;
import com.ibm.dsw.quote.common.domain.PartPriceConfigFactory;
import com.ibm.dsw.quote.common.domain.PartPriceSaaSPartConfigFactory;
import com.ibm.dsw.quote.common.domain.PartsPricingConfiguration;
import com.ibm.dsw.quote.common.domain.PartsPricingConfigurationFactory;
import com.ibm.dsw.quote.common.domain.QuoteHeader;
import com.ibm.dsw.quote.common.domain.QuoteHeaderFactory;
import com.ibm.dsw.quote.common.domain.QuoteLineItem;
import com.ibm.dsw.quote.common.domain.QuoteLineItemFactory;
import com.ibm.dsw.quote.common.domain.SaasPart;
import com.ibm.dsw.quote.configurator.config.ConfiguratorConstants;
import com.ibm.dsw.quote.configurator.contract.AddOrUpdateConfigurationContract;
import com.ibm.dsw.quote.configurator.contract.ConfigureHostedServiceContract;
import com.ibm.dsw.quote.configurator.dao.SaasConfiguratorDaoFactory;
import com.ibm.dsw.quote.configurator.domain.ConfiguratorHeader;
import com.ibm.dsw.quote.configurator.domain.ConfiguratorPart;
import com.ibm.dsw.quote.configurator.domain.ConfiguratorPartGroup;
import com.ibm.dsw.quote.configurator.domain.ConfiguratorProduct;
import com.ibm.dsw.quote.configurator.domain.CotermParameter;
import com.ibm.dsw.quote.configurator.domain.DomainAdapter;
import com.ibm.dsw.quote.configurator.helper.BillingOptionHelper;
import com.ibm.dsw.quote.configurator.helper.ConfigurationRetrievalService;
import com.ibm.dsw.quote.configurator.helper.ConfiguratorUtil;
import com.ibm.dsw.quote.configurator.viewbean.RestrictedPartSearchUIParams;
import com.ibm.dsw.quote.customerlist.domain.RedirectConfiguratorDataBasePack;
import com.ibm.dsw.quote.draftquote.util.PartPriceHelper;
import com.ibm.dsw.quote.draftquote.util.date.DateUtil;
import com.ibm.dsw.quote.log.util.QuoteLogContextLog4JImpl;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;
import com.ibm.ead4j.topaz.exception.TopazException;
import com.ibm.ead4j.topaz.persistence.TransactionContextManager;
import com.ibm.ead4j.topaz.process.TopazTransactionalProcess;

public abstract class ConfiguratorPartProcess_Impl extends TopazTransactionalProcess
		implements ConfiguratorPartProcess {

    protected LogContext logContext = LogContextFactory.singleton().getLogContext();

	private String configrtnErrCode;

	private List<String> missingPartLst = null;//refer to rtc#215704
	private List<ConfiguratorPart> chrgAgrmPartList = new ArrayList<ConfiguratorPart>();
	private void createQuoteLineItem(String configrtnId, AddOrUpdateConfigurationContract ct
			                             , List<ConfiguratorPart> lineItemList
			                             , CotermParameter parameter,List SaaSLineItems)
	                                                      throws TopazException{
		Date coTermEndDate = null;
		String coTermConfigrtnId = null;
		if(PartPriceConstants.ConfigrtnActionCode.FCT_TO_PA_FNL.equals(ct.getConfigrtnActionCode())){
			coTermEndDate = DateUtil.parseDate(ct.getCaEndDate());
		}else if(parameter != null){
			coTermEndDate = parameter.getEndDate();
			coTermConfigrtnId = parameter.getConfigrtnId();
		}
		//overrideFlag logic is changed, refer to rtc #200014
		String overrideFlag = ct.getOverrideFlag();
		if(StringUtils.isBlank(overrideFlag))
			overrideFlag = ConfiguratorConstants.OVERRIDE_FLAG_NO;
		if(StringUtils.isNotBlank(ct.getOverridePilotFlag()) && ct.getOverridePilotFlag().equals(ConfiguratorConstants.OVERRIDE_PILOT_FLAG_YES)){
			overrideFlag = ConfiguratorConstants.OVERRIDE_FLAG_YES;
		}else if(StringUtils.isNotBlank(ct.getOverrideRstrctFlag())&& ct.getOverrideRstrctFlag().equals(ConfiguratorConstants.OVERRIDE_RSTRCT_FLAG_YES)){
			overrideFlag = ConfiguratorConstants.OVERRIDE_FLAG_YES;
		}

		//set TermExtention
		setTermExtensionToContract(ct);
		// update EXT_ENTIRE_CONFIGRTN_FLAG
		int extEntireConfigFlag = 0;

		if (PartPriceConstants.ConfigrtnActionCode.FCT_TO_PA_FNL.equals(ct.getConfigrtnActionCode())||
				PartPriceConstants.ConfigrtnActionCode.ADD_TRD.equals(ct.getConfigrtnActionCode())){
			Map<String, ConfiguratorPart> origPartMap = toMap(chrgAgrmPartList);
			if("1".equals(ct.getTermExtensionFlag())){
				extEntireConfigFlag = getExtEntireConfigFlagAccordingToParts(origPartMap,lineItemList);
			}
		}

		int destObjSeqNum = addOrUpdateConfigrtn(ct.getWebQuoteNum(), configrtnId,
				      ct.getCpqConfigurationID(), ct.getUserId(), ct.getChrgAgrmtNum(),
				      configrtnErrCode, ct.getConfigrtnActionCode(), coTermEndDate, coTermConfigrtnId,
				      overrideFlag,"0",ct.getTermExtensionFlag(), DateUtil.parseDate(ct.getSeviceDate()),
				      ct.getServiceDateModType(),"",extEntireConfigFlag);
		
		//sort all configurator parts, let original existing parts sort in front
		new ConfiguratorUtil().sortConfiguratorParts(lineItemList, SaaSLineItems);

		for(ConfiguratorPart part : lineItemList){
			createQli(destObjSeqNum, configrtnId, ct.getWebQuoteNum(), ct.getUserId(), part,SaaSLineItems);
		}
		

		if(logContext instanceof QuoteLogContextLog4JImpl){
			if(((QuoteLogContextLog4JImpl)logContext).isDebug(this)){
				StringBuilder sb = new StringBuilder("\n");
				for(ConfiguratorPart part : lineItemList){
					sb.append(part.toString()).append("\n");
				}

				logContext.debug(this, "parts added to session quote: " + sb.toString());
			}
		}
	}

	private List<ConfiguratorPart> processForRelatedParts(List<ConfiguratorPart> lineItemList){
		List<ConfiguratorPart> fullLineItemList = associateParts(lineItemList);

		List<ConfiguratorPart> setUpPart = new ArrayList<ConfiguratorPart>();
		List<ConfiguratorPart> subscrptnPart = new ArrayList<ConfiguratorPart>();
		List<ConfiguratorPart> others = new ArrayList<ConfiguratorPart>();

		for(ConfiguratorPart part : fullLineItemList){
			if(part.isSetUp()){
				setUpPart.add(part);

			} else if(part.isSubscrptn()){
				List<ConfiguratorPart> rampUpList = part.getRampUpLineItems();
				if(rampUpList != null && rampUpList.size() > 0){
					for(int i = 0; i < rampUpList.size(); i++){
						ConfiguratorPart rampUpPart = rampUpList.get(i);

						//associate daily part with the first ramp up period
						if(i == 0 && part.getAssociatedDailyPart() != null){
							rampUpPart.setAssociatedDailyPart(part.getAssociatedDailyPart());
							part.setAssociatedDailyPart(null);
						}

						if(part.getAssociatedOveragePart() != null){
							rampUpPart.setAssociatedOveragePart(part.getAssociatedOveragePart().clone());
						}
					}
				}
				subscrptnPart.add(part);

			} else {
				others.add(part);
			}
		}

		fullLineItemList.clear();

		int destObjSeqNum = 0;
		for(ConfiguratorPart part : subscrptnPart){

			int lastRampSeqNum = destObjSeqNum;
			boolean hasRampUp = false;

			List<ConfiguratorPart> rampUpList = part.getRampUpLineItems();
			if(rampUpList != null && rampUpList.size() > 0){
				hasRampUp = true;

				for(int k = 0; k < rampUpList.size(); k++){
					ConfiguratorPart rampUpPart = rampUpList.get(k);
					rampUpPart.setRampUpFlag(ConfiguratorConstants.RAMP_FLAG_YES);

					fullLineItemList.add(rampUpPart);
					int j = destObjSeqNum;
					rampUpPart.setDestObjSeqNum(destObjSeqNum);
					destObjSeqNum++;

					//First ramp up will get related seq num as null
					if(k != 0){
						rampUpPart.setRelatedSeqNum(lastRampSeqNum);
					}
					lastRampSeqNum = j;

					//Process for overage and daily parts
					ConfiguratorPart dailyPart = rampUpPart.getAssociatedDailyPart();
					ConfiguratorPart overagePart = rampUpPart.getAssociatedOveragePart();

					if(dailyPart != null){
						fullLineItemList.add(dailyPart);
						dailyPart.setDestObjSeqNum(destObjSeqNum);
						destObjSeqNum++;
						dailyPart.setRelatedSeqNum(rampUpPart.getDestObjSeqNum());
					}
					if(overagePart != null){
						fullLineItemList.add(overagePart);
						overagePart.setDestObjSeqNum(destObjSeqNum);
						destObjSeqNum++;
						overagePart.setRelatedSeqNum(rampUpPart.getDestObjSeqNum());
					}
				}
			}

			fullLineItemList.add(part);
			part.setDestObjSeqNum(destObjSeqNum);
			destObjSeqNum++;
			if(hasRampUp){
				part.setRelatedSeqNum(lastRampSeqNum);
			}

			//Process for overage and daily parts
			ConfiguratorPart dailyPart = part.getAssociatedDailyPart();
			ConfiguratorPart overagePart = part.getAssociatedOveragePart();

			if(dailyPart != null){
				fullLineItemList.add(dailyPart);
				dailyPart.setDestObjSeqNum(destObjSeqNum);
				destObjSeqNum++;
				dailyPart.setRelatedSeqNum(part.getDestObjSeqNum());
			}
			if(overagePart != null){
				fullLineItemList.add(overagePart);
				overagePart.setDestObjSeqNum(destObjSeqNum);
				destObjSeqNum++;
				overagePart.setRelatedSeqNum(part.getDestObjSeqNum());
			}
		}

		for(ConfiguratorPart part : setUpPart){
			fullLineItemList.add(part);

			part.setDestObjSeqNum(destObjSeqNum);
			destObjSeqNum++;
			
			ConfiguratorPart subsumedPart = part.getAssociatedSubsumedSubscrptnPart();	
			
			if (subsumedPart != null){
				fullLineItemList.add(subsumedPart);
				subsumedPart.setDestObjSeqNum(destObjSeqNum);
				destObjSeqNum++;
				part.setRelatedSeqNum(subsumedPart.getDestObjSeqNum());
				subsumedPart.setRelatedSeqNum(part.getDestObjSeqNum());
			} else {
				ConfiguratorPart subPart = part.getAssociatedSubscrptnPart();
				if(subPart != null){
					part.setRelatedSeqNum(subPart.getDestObjSeqNum());
				} 
			}

		}

		for(ConfiguratorPart part : others){
			fullLineItemList.add(part);
			part.setDestObjSeqNum(destObjSeqNum);
			destObjSeqNum++;
		}

		for(ConfiguratorPart part : fullLineItemList){
			if(part.isPartReplaced()){
				//This is to set related seq number for related parts
				part.setReplacementRelation();
			}
            // call setRelatedSeqNum for Subsumed Subscription Part and Set Up part. call setPartQtyStr and
            // setPartQty
            // for Subsumed Subscription Part
            if (part.isSubsumedSubscrptn()) {
                ConfiguratorPart relatedSetUpPart = getSetUpPart(fullLineItemList, part);
                part.setPartQty(relatedSetUpPart.getPartQty());
                part.setPartQtyStr(relatedSetUpPart.getPartQty() + "");
                part.setBillgUpfrntFlag("1");
                part.setBillingFrequencyCode(ConfiguratorConstants.BILLING_FREQUENCY_UPFRONT);
            }

		}

		return fullLineItemList;
	}

	private boolean shouldCoTerm(String configrtnActionCode){
		return (PartPriceConstants.ConfigrtnActionCode.ADD_TRD.equals(configrtnActionCode)
				|| PartPriceConstants.ConfigrtnActionCode.NEW_CA_CT.equals(configrtnActionCode)
				|| PartPriceConstants.ConfigrtnActionCode.FCT_TO_PA_FNL.equals(configrtnActionCode));
	}

	private boolean shouldSetProvDays(String configrtnActionCode){
		return shouldCoTerm(configrtnActionCode);
	}

	private boolean coTermToConfigrtn(String configrtnActionCode){
		//Notes://CAMDB10/85256B890058CBA6/39528B5E1014EA3E85256D24005FCB4D/098ED5DE9EA82DEC85257A59005CBBAC
		return (PartPriceConstants.ConfigrtnActionCode.ADD_TRD.equals(configrtnActionCode)
				|| PartPriceConstants.ConfigrtnActionCode.FCT_TO_PA_FNL.equals(configrtnActionCode));
	}


	private CotermParameter calculateCoTerm(List<ConfiguratorPart> list, String actionCode){
		Date latestEndDate = null;
		ConfiguratorPart latestEndDatePart = null;

		for(ConfiguratorPart part : list){
			if(!part.isSubscrptn()&&!part.isSubsumedSubscrptn()){
				continue;
			}

			//FCT to PA
			if(PartPriceConstants.ConfigrtnActionCode.FCT_TO_PA_FNL.equals(actionCode)){
				if (part.getCotermEndDate() != null){
					latestEndDate = part.getCotermEndDate();
					latestEndDatePart = part;
					break;
				} else {
					continue;
				}
			}

			if(part.getEndDate() == null || DateUtil.isDateBeforeToday(part.getEndDate())){

				if(part.getRenewalEndDate() == null || DateUtil.isDateBeforeToday(part.getRenewalEndDate())){
					if(ConfiguratorConstants.BILLING_FREQUENCY_MONTHLY.equals(part.getSapBillgFrqncyOptCode())){
						if(part.getNextRenwlDate() != null){
							latestEndDate = DateUtil.plusMonth(part.getNextRenwlDate(), part.getRenwlTermMths());
							latestEndDatePart = part;
						}
					}
				} else {
					if(latestEndDate == null){
						latestEndDate = part.getRenewalEndDate();
						latestEndDatePart = part;
					} else {
						if(part.getRenewalEndDate().after(latestEndDate)){
							latestEndDate = part.getRenewalEndDate();
							latestEndDatePart = part;
						}
					}
				}
			} else {
				if(latestEndDate == null){
					latestEndDate = part.getEndDate();
					latestEndDatePart = part;
				} else {
					if(part.getEndDate().after(latestEndDate)){
						latestEndDate = part.getEndDate();
						latestEndDatePart = part;
					}
				}
			}
		}

		if(latestEndDatePart != null){
			return new CotermParameter(latestEndDate, latestEndDatePart.getRefDocLineNum(), latestEndDatePart.getConfigrtnId());
		}

		return null;
	}

	public CotermParameter getCotermToPartInfo(String chrgAgrmtNum, String configrtnId, List<DomainAdapter> replacedParts,String actionCode) throws QuoteException{
		List<ConfiguratorPart> chrgAgrmLineItemList = getSubPartsFromChrgAgrm(chrgAgrmtNum, configrtnId);

		List<ConfiguratorPart> replacedChrgAgrmParts = new ArrayList<ConfiguratorPart>();
		for(Iterator<ConfiguratorPart> chrgIt = chrgAgrmLineItemList.iterator(); chrgIt.hasNext();){
			ConfiguratorPart part = chrgIt.next();

			if(!part.isSubscrptn()&&!part.isSubsumedSubscrptn()){
				chrgIt.remove();
				continue;
			}

			for(Iterator<DomainAdapter> it = replacedParts.iterator(); it.hasNext(); ){
				DomainAdapter adapter = it.next();

				if(adapter.getPartNum().equals(part.getPartNum())
						&& (adapter.getRefDocLineItemNum() == part.getRefDocLineNum())){
					chrgIt.remove();
					it.remove();

					replacedChrgAgrmParts.add(part);
					break;
				}
			}
		}

		CotermParameter param = null;

		if(replacedChrgAgrmParts.size() > 0){
			param = calculateCoTerm(replacedChrgAgrmParts,actionCode);

			if(param != null){
				logContext.debug(this, "Coterm parameter derived from replaced parts: " + param.toString());
				return param;
			} else {
				logContext.debug(this, "Unable to derive coterm parameter from replaced parts");
			}
		}

		param = calculateCoTerm(chrgAgrmLineItemList,actionCode);

		if(param != null){
			logContext.debug(this, "Coterm parameter derived from not replaced CA parts: " + param.toString());
			return param;
		} else {
			logContext.debug(this, "Unable to derive coterm parameter from un-replaced parts");
			return null;
		}
	}

	protected abstract List<ConfiguratorPart> getSubPartsFromChrgAgrm(String chrgAgrmtNum, String configId)throws QuoteException;

	private CotermParameter processForCoterm(List<ConfiguratorPart> lineItemList
			                      , String chrgAgrmtNum, String configrtnId
			                      , Map<String, ConfiguratorPart> map,String actionCode)
	                                                  throws QuoteException{
		List<DomainAdapter> replacedParts = new ArrayList<DomainAdapter>();

		for(ConfiguratorPart part : lineItemList){
			if(part.isPartReplaced()){
				replacedParts.add(DomainAdapter.create(part));
			}
		}

		CotermParameter ctParam = getCotermToPartInfo(chrgAgrmtNum, configrtnId, replacedParts,actionCode);
		if(ctParam == null){
			return null;
		}

		for(ConfiguratorPart part : lineItemList){
			if((part.isSubscrptn() && !part.isPartReplaced()) || part.isSetUp() || part.isSubsumedSubscrptn()){
				part.setRelatedCotermLineItmNum(ctParam.getRefDocLineItemSeqNum());
			}
		}

		return ctParam;
	}


	private Integer getRelatedSeqNum(Integer base, int increase){
		if(base == null){
			return PartRelNumAndTypeDefault.RELATED_LINE_ITM_NUM_DEFAULT;
		}

		return base + increase;
	}

	private void createQli(int baseDestObjNum, String configrtnId, String webQuoteNum, String userId,
			                             ConfiguratorPart part,List SaaSLineItems)throws TopazException{

		QuoteHeader quoteHeader = QuoteHeaderFactory.singleton().findByWebQuoteNum(webQuoteNum);

		QuoteLineItem qli = QuoteLineItemFactory.singleton()
		                              .createQuoteLineItem(webQuoteNum, new SaasPart(part.getPartNum()), userId);

		for (Object obj : SaaSLineItems) {
			QuoteLineItem lineItem = (QuoteLineItem) obj;
			if (lineItem.isReplacedPart()) continue;
			if (lineItem.getPartNum().equals(part.getPartNum())) {
				qli.setOverrideUnitPrc(lineItem.getOverrideUnitPrc());
				qli.setOvrrdExtPrice(lineItem.getOvrrdExtPrice());
				qli.setLocalExtProratedDiscPrc(lineItem.getOvrrdExtPrice());
				qli.setLineDiscPct(lineItem.getLineDiscPct());
				qli.setManualSortSeqNum(lineItem.getManualSortSeqNum());
				qli.setSwSubId(lineItem.getSwSubId());
				qli.setRevnStrmCodeDesc(lineItem.getRevnStrmCodeDesc());
				qli.setSeqNum(lineItem.getSeqNum());
				qli.setChnlOvrrdDiscPct(lineItem.getChnlOvrrdDiscPct());
				break;
			}
		}

		qli.setSwProdBrandCode(part.getSwProdBrandCode());
		qli.setProrateFlag(false);
		qli.setAssocdLicPartFlag(false);
		qli.setConfigrtnId(configrtnId);

		//Every part will get a dest seq num
		qli.setDestSeqNum(part.getDestObjSeqNum() + baseDestObjNum);
		qli.setSeqNum(qli.getDestSeqNum());
		qli.setIRelatedLineItmNum(getRelatedSeqNum(part.getRelatedSeqNum(), baseDestObjNum));
		qli.setPartQty(part.getPartQty());
		qli.setBillgFrqncyCode(part.getBillingFrequencyCode());
		qli.setCumCvrageTerm(part.getTotalTerm());
		qli.setICvrageTerm(part.getTerm());
		
		
		//qli.setRefDocLineNum(part.getRefDocLineNum());
		Integer refDocLineNum = part.getRefDocLineNum() == null || part.getRefDocLineNum().intValue() == 0 ? part
				.getRelatedCotermLineItmNum() : part.getRefDocLineNum();
		qli.setRefDocLineNum(refDocLineNum);
		
		
		qli.setRelatedCotermLineItmNum(part.getRelatedCotermLineItmNum());
		qli.setRampUp(part.isRampUp());
		qli.setReplacedPart(part.isPartReplaced());


		//FCT TO PA Finalization
		qli.setOrignlSalesOrdRefNum(part.getOrignlSalesOrdRefNum());
		qli.setOrignlConfigrtnId(part.getOrignlConfigrtnId());

		if(part.isPartReplaced()){
			if(quoteHeader.isChannelQuote()){
				if(part.isOvrage() || part.isAddiSetUp() || part.isOnDemand()){
		        	qli.setLocalUnitProratedPrc(null);
		        	qli.setLocalUnitProratedDiscPrc(part.getLocalSaasOvrageAmt());
		        	qli.setChannelUnitPrice(part.getLocalSaasOvrageAmt());
		        }
		        if(part.isSubscrptn()){
		        	qli.setLocalExtPrc(null);
		        	qli.setLocalExtProratedPrc(null);
		        	qli.setLocalExtProratedDiscPrc(part.getLocalExtndPrice());
		        	qli.setSaasBidTCV(part.getSaasTotCmmtmtVal());
		        	qli.setChannelExtndPrice(part.getLocalExtndPrice());
		        	qli.setSaasBpTCV(part.getSaasTotCmmtmtVal());
		        }
			}else{
				if(part.isOvrage() || part.isAddiSetUp() || part.isOnDemand()){
		        	qli.setLocalUnitProratedPrc(null);
		        	qli.setLocalUnitProratedDiscPrc(part.getLocalSaasOvrageAmt());
		        }
		        if(part.isSubscrptn()){
		        	qli.setLocalExtPrc(null);
		        	qli.setLocalExtProratedPrc(null);
		        	qli.setLocalExtProratedDiscPrc(part.getLocalExtndPrice());
		        	qli.setSaasBidTCV(part.getSaasTotCmmtmtVal());
		        	//PL: 421951,fixed the total price missed.  The detail infomation Notes://CAMDB10/85256B890058CBA6/8278F0CE794010B985256D24005FCB4F/FBF6BC9B291B7A9485257AD2005C4E3A
		        	qli.setChannelExtndPrice(part.getLocalExtndPrice());
                    qli.setSaasBpTCV(part.getSaasTotCmmtmtVal());
		        }
			}
		}
	}

	private List<ConfiguratorPart> associateParts(List<ConfiguratorPart> list){
		List<ConfiguratorPart> lineItemList = new ArrayList<ConfiguratorPart>();
		Map<String, SubscrptnPartGroup> map = new HashMap<String, SubscrptnPartGroup>();
		Map<String,SetUpPartGroup> setUpMap =new HashMap<String,SetUpPartGroup>();

		for(ConfiguratorPart part : list){
			//For add-on/trade-up, there could be duplicate
			//subscription parts with same part number, just ignore replaced parts
			if(part.isPartReplaced()){
				lineItemList.add(part);
				continue;
			}

			if(part.isSubscrptn() || part.isDaily() || part.isOvrage()){
				SubscrptnPartGroup grp = map.get(part.getSubId());

				if(grp == null){
					grp = new SubscrptnPartGroup();
					map.put(part.getSubId(), grp);
				}
				grp.addPart(part);

            } 
            
            //set AssociateSubscrptnPart for setup Part by  wwideProdCode
            if (part.isSetUp()){
                for(ConfiguratorPart partInner : list){
                    if(partInner.isSubscrptn() && partInner.getWwideProdCode().equals(part.getWwideProdCode())){
                        part.setAssociatedSubscrptnPart(partInner);
                        break;
                    }
                }
            }
            
            //set AssociateSubscrptnPart group for setup Part by subId
            if (part.isSubscrptn() || part.isSetUp() || part.isSubsumedSubscrptn()){
                
                SetUpPartGroup setUpGroup = setUpMap.get(part.getSubId());
                if(setUpGroup == null){
                    setUpGroup = new SetUpPartGroup();
                    setUpMap.put(part.getSubId(), setUpGroup);
                }
                setUpGroup.addPart(part);
                
            } else if (!part.isOvrage()&& !part.isDaily()){
                lineItemList.add(part);
            }
        }

        for(SubscrptnPartGroup grp : map.values()){
            if(grp.hasSubscriptionPart()){
                lineItemList.add(grp.getSubscrptnPart());
            }
        }
        
        for (SetUpPartGroup setUpGrp : setUpMap.values()){
            if (setUpGrp.hasSetUpPart()){
                lineItemList.add(setUpGrp.getSetUpPart());
            }
        }

        return lineItemList;
    }

	class SubscrptnPartGroup{
		void addPart(ConfiguratorPart part){
			if(part.isSubscrptn()){
				this.subscrptnPart = part;
			}

			if(part.isDaily()){
				this.dailyPart = part;
			}

			if(part.isOvrage()){
				this.overagePart = part;
			}
		}

		boolean hasSubscriptionPart(){
			return subscrptnPart != null;
		}

		ConfiguratorPart getSubscrptnPart(){
			subscrptnPart.setAssociatedDailyPart(dailyPart);
			subscrptnPart.setAssociatedOveragePart(overagePart);

			return subscrptnPart;
		}

		ConfiguratorPart subscrptnPart;
		ConfiguratorPart dailyPart;
		ConfiguratorPart overagePart;
	}
	
	class SetUpPartGroup{
		void addPart(ConfiguratorPart part){
			if(part.isSubscrptn()){
				this.subscrptnPart = part;
			}

			if(part.isSetUp()){
				this.setUpPart = part;
			}

			if(part.isSubsumedSubscrptn()){
				this.subsumedScrptnPart = part;
			}
		}
		
		boolean hasSetUpPart(){
			return setUpPart != null;
		}

		ConfiguratorPart getSetUpPart(){
			setUpPart.setAssociatedSubsumedSubscrptnPart(subsumedScrptnPart);
			
			setUpPart.setAssociatedSubscrptnPart(subscrptnPart);

			return setUpPart;
		}
		
		ConfiguratorPart subscrptnPart;
		ConfiguratorPart setUpPart;
		ConfiguratorPart subsumedScrptnPart;
	}

	protected abstract List<ConfiguratorPart> getPartsFromChrgAgrm(AddOrUpdateConfigurationContract ct, Map<String, ConfiguratorPart> map)  throws QuoteException;

	private List<ConfiguratorPart> getAddOnTradeUpParts(AddOrUpdateConfigurationContract ct,
			Map<String, ConfiguratorPart> map)throws QuoteException, TopazException{
		List<ConfiguratorPart> lineItemList = new ArrayList<ConfiguratorPart>();

		List<ConfiguratorPart> configuratorPartList = associateParts(getConfiguratorParts(ct, map));

		 chrgAgrmPartList = associateParts(getPartsFromChrgAgrm(ct, map));
		Map<String, ConfiguratorPart> origPartMap = toMap(chrgAgrmPartList);

		List<ConfiguratorPart> newlyAddedSubscrptnPartList = new ArrayList<ConfiguratorPart>();
		List<ConfiguratorPart> removedSubscrptnPartList = new ArrayList<ConfiguratorPart>();

		for(ConfiguratorPart newPart : configuratorPartList){
			if(newPart.isSubscrptn()){
				ConfiguratorPart origPart = origPartMap.remove(newPart.getPartNum());

				if(origPart != null){
					int origQty = origPart.getPartQty().intValue();
					int newQty = newPart.getPartQty().intValue();

					if(newQty != origQty){
						lineItemList.add(newPart);
						origPart.markAsReplaced(newPart);
						lineItemList.add(origPart);

						ConfiguratorPart newOveragePart = newPart.getAssociatedOveragePart();
						if(newOveragePart != null){
							lineItemList.add(newOveragePart);
						}

						ConfiguratorPart origOveragePart = origPart.getAssociatedOveragePart();
						if(origOveragePart != null){
							origOveragePart.markAsReplaced(newOveragePart);
							lineItemList.add(origOveragePart);
						}

						if(newPart.getAssociatedDailyPart() != null){
							lineItemList.add(newPart.getAssociatedDailyPart());//Add new daily parts as new line items
						}
					} else {
						//Quantity not touched, do nothing here
					}
				} else {
					//No original part with same part#, this is a new part
					lineItemList.add(newPart);

					if(newPart.getAssociatedDailyPart() != null){
						lineItemList.add(newPart.getAssociatedDailyPart());
					}

					if(newPart.getAssociatedOveragePart() != null){
						lineItemList.add(newPart.getAssociatedOveragePart());
					}

					logContext.debug(this, "new part added from configurator : " + newPart.toString());
					newlyAddedSubscrptnPartList.add(newPart);
				}

			} else if(newPart.isSetUp()){
				//Update on 2012-1-12 per PL : Notes://CAMDB10/85256B890058CBA6/5D3D446FEB23FC918525718E006EAFDE/7446F20C4604F57985257982005B3409
				if(ct.isFromCPQConfigurator()){	//from GST
					ConfiguratorPart origPart = origPartMap.remove(newPart.getPartNum());
					if(origPart != null){	//in CA.
						int origQty = origPart.getPartQty().intValue();
						int newQty = newPart.getPartQty().intValue();
						if(newQty > origQty){
							int delta = newQty - origQty;
							newPart.setPartQty(delta);
							lineItemList.add(newPart);
						} else {
							//Do nothing here
						}
					} else {	//not in CA
						lineItemList.add(newPart);
					}
				}else { //from Basic
                    lineItemList.add(newPart);
                    if (newPart.getAssociatedSubsumedSubscrptnPart()!=null){
                        lineItemList.add(newPart.getAssociatedSubsumedSubscrptnPart());
                    }
                }

			} else if(newPart.isAddiSetUp()){
				ConfiguratorPart origPart = origPartMap.remove(newPart.getPartNum());

				if(origPart != null){
					if(setUpPartWithSameSubIdReturned(newPart.getSubId(), configuratorPartList)){
						lineItemList.add(newPart);

						origPart.markAsReplaced();
						lineItemList.add(origPart);
					} else {
						//Do nothing here
					}
				} else {
					lineItemList.add(newPart);
				}

			} else if(newPart.isOnDemand()){
				ConfiguratorPart origPart = origPartMap.remove(newPart.getPartNum());
				if(origPart != null){
					//Do nothing here
				} else {
					lineItemList.add(newPart);
				}
			} else if(newPart.isHumanSrvs()){
				lineItemList.add(newPart);
			}
		}

		Map<String, ConfiguratorPart> configuratorPartMap = toMap(configuratorPartList);
		for(ConfiguratorPart origPart : origPartMap.values()){
			if(origPart.isSubscrptn()){
				ConfiguratorPart newPart = configuratorPartMap.get(origPart.getPartNum());
				boolean isMissingPart = false;//refer to rtc#215704. If currnet ca part is a missing part, the part will be added into replaced list.
				if(missingPartLst != null && missingPartLst.contains(origPart.getPartNum()))
					isMissingPart = true;
				if(newPart == null && !isMissingPart){
					origPart.markAsReplaced();
					lineItemList.add(origPart);

					ConfiguratorPart overagePart = origPart.getAssociatedOveragePart();
					if(overagePart != null){
						overagePart.markAsReplaced();
						lineItemList.add(overagePart);
					}

					logContext.debug(this, "original part removed from configurator: " + origPart.toString());
					removedSubscrptnPartList.add(origPart);
				}
			}
		}

		processForTradeUp(newlyAddedSubscrptnPartList, removedSubscrptnPartList);

		for(ConfiguratorPart part : lineItemList){
			//Clear daily/overage parts for replaced subscription parts
			if(part.isPartReplaced() && part.isSubscrptn()){
				part.setAssociatedDailyPart(null);
				part.setAssociatedOveragePart(null);
			}
		}

		return lineItemList;
	}

	private void processForTradeUp(List<ConfiguratorPart> newPartList, List<ConfiguratorPart> removedPartList){
		if(newPartList.size() == 0 || removedPartList.size() == 0){
			return;
		}

		ConfiguratorPart minRefDocLineItemNumPart = null;
		int minRefDocLineItemNum = Integer.MAX_VALUE;

		//removedPartList is from CA, so every part has refDocLineItemNum
		for(ConfiguratorPart part : removedPartList){
			if(part.getRefDocLineNum() < minRefDocLineItemNum){
				minRefDocLineItemNum = part.getRefDocLineNum();
				minRefDocLineItemNumPart = part;
			}
		}

		ConfiguratorPart newPart = newPartList.get(0);
		//#todo remove set trade up part ref doc number
		//Notes://D01dbm17/8525788A006C6448//DCBF686CDD314599872578920047B86E
		//minRefDocLineItemNumPart.markAsReplaced(newPart);
		minRefDocLineItemNumPart.markAsReplaced();
	}

	private boolean setUpPartWithSameSubIdReturned(String subId, List<ConfiguratorPart> list){
		for(ConfiguratorPart part : list){
			if(part.isSetUp() && part.getSubId().equals(subId)){
				return true;
			}
		}

		return false;
	}

	private Map<String, ConfiguratorPart> toMap(List<ConfiguratorPart> partList){
		Map<String, ConfiguratorPart> map = new HashMap<String, ConfiguratorPart>();

		for(ConfiguratorPart part : partList){
			map.put(part.getPartNum(), part);
		}
		return map;
	}

    private boolean setUpWithSameSubIdRemoved(String subId
    		           , List<ConfiguratorPart> list
    		           , Map<String, ConfiguratorPart> allPartsMap){

    	boolean hasSameSubIdSetUp = false;
    	for(ConfiguratorPart part : allPartsMap.values()){
    		if(part.isSetUp() && part.getSubId().equals(subId)){
    			hasSameSubIdSetUp = true;
    			break;
    		}
    	}

    	//No existing set up with same sub id
    	if(!hasSameSubIdSetUp){
    		logContext.debug(this, "no set up part for SubId=" + subId);
    		return false;

    	} else {
    		logContext.debug(this, "find set up part for SubId=" + subId);

    		//Check that same sub id set up is not deleted from configurator
        	for(ConfiguratorPart part : list){
        		if(part.isSetUp() && part.getSubId().equals(subId)){
        			logContext.debug(this, "set up part returned from configurator for SubId=" + subId);
        			return false;
        		}
        	}

        	return true;
    	}
    }
    
    private boolean hasSetUpPartWithSameSubId(String subId, List<ConfiguratorPart> list){
    	for (ConfiguratorPart part : list){
    		if (part.isSetUp() && part.getSubId().equals(subId)){
    			return true;
    		}
    	}
    	return false;
    }

    private boolean hasSubscriptionPartWithSameSubId(String subId, List<ConfiguratorPart> list){
    	for(ConfiguratorPart part : list){
    		if(part.isSubscrptn() && part.getSubId().equals(subId)){
    			return true;
    		}
    	}

    	return false;
    }

	private List<ConfiguratorPart> getConfiguratorParts(AddOrUpdateConfigurationContract ct
			                                       , Map<String, ConfiguratorPart> map) throws TopazException, QuoteException{
		List<ConfiguratorPart> list = new ArrayList<ConfiguratorPart>();

		if(ct.isFromCPQConfigurator()){

			//Call web service here to get parts from CPQ
			try{
				StringBuffer bf = new StringBuffer();
				missingPartLst = new ArrayList<String>();//refer to rtc#215704
				list = ConfigurationRetrievalService
				         .getInstance().callWebService(ct.getCpqConfigurationID(), ct.getWebQuoteNum(),
				        		   bf, PartPriceSaaSPartConfigFactory.singleton().shouldUseTokenCache(), missingPartLst);
				configrtnErrCode = bf.toString();

				setSaaSPartAttribute(list, map, ct.getPid());
			}
			catch(Exception e){
				logContext.error(this, "error to retrieve parts from CPQ web service");
				throw new QuoteException(e);
			}

		} else {
			List<ConfiguratorPart> tmp = ct.getParts();
			setSaaSPartAttribute(tmp, map, ct.getPid());

			for(ConfiguratorPart part : tmp){
				if(part.isAddiSetUp() && setUpWithSameSubIdRemoved(part.getSubId(), tmp, map)){
					logContext.debug(this, "additional set up part " + part.toString()
							            + " removed from list as same SubId set up is not returned from configurator");
					continue;
				}

				if((part.isOvrage() || part.isDaily())
						&& !hasSubscriptionPartWithSameSubId(part.getSubId(), tmp)){
					continue;
				}
				
				if (part.isSubsumedSubscrptn()
						&& !hasSetUpPartWithSameSubId(part.getSubId(), tmp)) {
					continue;
				}

				//Handle term, clear data for non-subscription parts
				if(!part.isSubscrptn() && !part.isSetUp() && !part.isSubsumedSubscrptn()){
		    		part.setTotalTerm(null);
		    		part.setTerm(null);
				}

				list.add(part);
			}
		}

		return list;
	}

	// public void setSaaSPartAttribute(String webQuoteNum, String pid,
	// List<ConfiguratorPart> list) throws QuoteException{
	//
	// try{
	// TransactionContextManager.singleton().begin();
	//
	// List<ConfiguratorPart> allParts =
	// findConfiguratorPartsByWebQuoteNumPID(webQuoteNum, pid);
	// Map<String, ConfiguratorPart> map = toMap(allParts);
	// setSaaSPartAttribute(list, map, pid);
	//
	// TransactionContextManager.singleton().commit();
	// } catch(TopazException e){
	// try{
	// TransactionContextManager.singleton().rollback();
	//
	// } catch(TopazException ignore){
	// logContext.error(this, ignore);
	// }
	//
	// logContext.error(this, e);
	// throw new QuoteException(e);
	// }
	// }


	public void setSaaSPartAttribute(List<ConfiguratorPart> list
			              , Map<String, ConfiguratorPart> map
			              , String pId){
		for(Iterator<ConfiguratorPart> it = list.iterator(); it.hasNext(); ){
			ConfiguratorPart part = it.next();

			logContext.debug(this, "set saas part attribute for part : " + part.getPartNum());
			ConfiguratorPart pidPart = map.get(part.getPartNum());
			if(pidPart == null){
				logContext.debug(this, "unable to find a matching part from S_QT_PART_BY_ID result of pId -> "
						                + pId + ", part removed " + part.getPartNum());
				it.remove();
				continue;
			}

			part.setSwProdBrandCode(pidPart.getSwProdBrandCode());
			part.setSubId(pidPart.getSubId());
			part.setSapMatlTypeCode(pidPart.getSapMatlTypeCode());
			part.setWwideProdCode(pidPart.getWwideProdCode());
			part.setTierQtyMeasre(pidPart.getTierQtyMeasre());
			part.setPricingTierModel(pidPart.getPricingTierModel());
            part.setSubsumedSubscrptn(pidPart.isSubsumedSubscrptn());

			if(part.getRampUpLineItems() != null){

				for(ConfiguratorPart rampUpPart : part.getRampUpLineItems()){
					rampUpPart.setSwProdBrandCode(pidPart.getSwProdBrandCode());
					rampUpPart.setSubId(pidPart.getSubId());
					rampUpPart.setSapMatlTypeCode(pidPart.getSapMatlTypeCode());
					rampUpPart.setWwideProdCode(pidPart.getWwideProdCode());
					rampUpPart.setTierQtyMeasre(pidPart.getTierQtyMeasre());
					rampUpPart.setPricingTierModel(pidPart.getPricingTierModel());
				}
			}
		}
	}



    /*
     * build configurator header for all scenario
     * detailed design document can refer to :
     * Notes://D01dbm17/8525788A006C6448/867962C7D68029F085256603006ADD59/3A0F1C0EE52DBBAA872578A90051700A
     * @see com.ibm.dsw.quote.configurator.process.ConfiguratorPartProcess#buildConfiguratorHeader(com.ibm.dsw.quote.configurator.contract.ConfigureHostedServiceContract, com.ibm.dsw.quote.configurator.domain.ConfiguratorHeader)
     */
	public void buildConfiguratorHeader(ConfigureHostedServiceContract chsContract, ConfiguratorHeader header) throws TopazException{
		String webQuoteNum = chsContract.getWebQuoteNum();
		String pid = chsContract.getPid();
    	String[] origPartNumbers = chsContract.getOrigPartNumbers();	//for setting active on agreement , include new add-on and re-editing add-on.
    	String[] origQtys = chsContract.getOrigQtys();	//for setting original quantity , include new add-on and re-editing add-on.
    	List<String> origPartNumberLst = null;
    	Map<String, Integer> origQtyMap = null;
    	boolean isWhenAddOn = false;
    	if(origPartNumbers != null && origPartNumbers.length > 0)
    		isWhenAddOn = true;

    	if(isWhenAddOn){	// when Add-on, put original part number transfered from CA in a map.
    		origPartNumberLst = new ArrayList();
    		origQtyMap = new HashMap();
    		int i = 0;
    		for(String origPartNumber : origPartNumbers){
    			origPartNumberLst.add(origPartNumber);
    			if(origQtys != null && origQtys.length > 0)
    				if(i < origQtys.length && StringUtils.isNotBlank(origQtys[i]))
    					origQtyMap.put(origPartNumber, new Integer(origQtys[i]));
    			i++;
    		}
    	}

		//1. get cps by pid from db.
		List<ConfiguratorPart> cpFromPIDs = SaasConfiguratorDaoFactory.singleton().create()
		.findPartsByWebQuoteNumPID(webQuoteNum, pid);
		Map<String,ConfiguratorPart> cpFromPIDMap = new HashMap();
		Map<String,List> boMap = BillingOptionHelper.singleton().getFinalBillingFrequencyOptions(chsContract.getAvaliableBillingFrequencyOptions());	//billing options map.
		//2. set the properties for cpFromPIDs via CA values & billing options.
		for(ConfiguratorPart cpFromPID:cpFromPIDs){
			cpFromPID.setAvailableBillingOptions(boMap.get(cpFromPID.getPartNum()));	//set billing options which is from parameters.
			if(isWhenAddOn){	//when Add-on, set active flag and checked property.
				if(origPartNumberLst.contains(cpFromPID.getPartNum())){	//when part number in original part number list, means the part is active on agreement.
					cpFromPID.setActiveOnAgreement(true);
					cpFromPID.setChecked(true);	//set 'include' check-box.
				}
				else{
					cpFromPID.setActiveOnAgreement(false);
					cpFromPID.setChecked(false);
				}
			}
			if(origQtyMap != null){		//when original quantity map is not null, means the part original quantity can be got.
				Integer origQty = origQtyMap.get(cpFromPID.getPartNum());
				if(origQty != null){
					cpFromPID.setOrigQty(origQty);
					if(isWhenAddOn
						&& cpFromPID.isSubscrptn()){
						if((PartPriceConstants.ConfigrtnActionCode.FCT_TO_PA_FNL.equals(chsContract.getConfigrtnActionCode())
								&& !CustomerConstants.CONFIGURATOR_SOURCE_TYPE_REEDIT.equals(chsContract.getSourceType()))
								||!PartPriceConstants.ConfigrtnActionCode.FCT_TO_PA_FNL.equals(chsContract.getConfigrtnActionCode()))
							//When AddOn, if the part is a subscription part, need to set part quantity firstly.
							//Since if a subscription part's inputed quantity is same as the quantity from CA,
							//the part will not be added into draft quote.
							//But when re-edit the draft quote, the part quantity will need to be as the quantity from CA.
							//Otherwise, adding part function will have issue.
							cpFromPID.setPartQty(origQty);
					}


				}
			}

			cpFromPIDMap.put(cpFromPID.getPartNum(), cpFromPID);
		}
		List<ConfiguratorPart> cpsWithContractVal = new ArrayList();
		//3. set the properties of cps with contract value ,including: quantity,billing frequency code,ramp up period, ramp up line items...
		if(chsContract.getConfiguratorParts()!=null && chsContract.getConfiguratorParts().size()>0){	//include re-edit new configuration, copy configuration, new Add-on, re-edit Add-on
			List<ConfiguratorPart> cpFromContracts = chsContract.getConfiguratorParts();
			boolean hasSetTerm = false;
			for(ConfiguratorPart cpFromContract:cpFromContracts){
				ConfiguratorPart cpFromPID = cpFromPIDMap.get(cpFromContract.getPartNum());
				if(cpFromPID == null){	//When some part in CA can not be found in PID searching result list, throw exception.
                    continue;
				}
				if(StringUtils.isNotBlank(chsContract.getSourceType())
						&& CustomerConstants.CONFIGURATOR_SOURCE_TYPE_ADDONTRADEUP.equals(chsContract.getSourceType())){
				//above: CustomerConstants.CONFIGURATOR_SOURCE_TYPE_ADDONTRADEUP means new AddOn.
					if(isWhenAddOn && (cpFromPID.isSetUp()||cpFromPID.isSubsumedSubscrptn()) )	//if a part is set up part, the quantity should be null.
						cpFromPID.setPartQty(null);
				}else
					cpFromPID.setPartQty(cpFromContract.getPartQty());

				if(isWhenAddOn){	//when Add-on, need to notice active flag.
					if(cpFromPID.isActiveOnAgreement())	//if the part is active on agreement, set checked property with true.
						cpFromPID.setChecked(true);
					else
						cpFromPID.setChecked(cpFromContract.isChecked());	// else set checked property with contract value. maybe is true , or maybe is false.
				}else	//when re-edit & copy configuration scenario.
					cpFromPID.setChecked(cpFromContract.isChecked());	// just set checked property with contract value.

				cpFromPID.setBillingFrequencyCode(cpFromContract.getBillingFrequencyCode());

				if(cpFromContract.getRampUpLineItems() != null)	//set ramp up period
					cpFromPID.setRampUpPeriod(cpFromContract.getRampUpLineItems().size());
				else
					cpFromPID.setRampUpPeriod(0);

				//set tierd scale quantity options for ramp up with master's options.
				if(cpFromContract.getRampUpLineItems() != null && cpFromContract.getRampUpLineItems().size() > 0){
					for(ConfiguratorPart cpRampUp : cpFromContract.getRampUpLineItems())
						if(cpFromPID.getTierdScalQtyList() != null && cpFromPID.getTierdScalQtyList().size() > 0)
							cpRampUp.setTierdScalQtyList(cpFromPID.getTierdScalQtyList());
				}
				cpFromPID.setRampUpLineItems(cpFromContract.getRampUpLineItems());
				if(cpFromContract.getTerm() != null && cpFromContract.getRampUpFlag().equals(ConfiguratorConstants.RAMP_FLAG_NO)){
					if(cpFromContract.getTerm().intValue() > 0 && !hasSetTerm){
						header.setTerm(cpFromContract.getTerm());
						hasSetTerm = true;
					}
				}
			}
		}
		//3. In order to keep the sequence, need to add cp via cps.
		for(ConfiguratorPart cp: cpFromPIDs){
			if(cpFromPIDMap.get(cp.getPartNum())!=null)
				cpsWithContractVal.add(cpFromPIDMap.get(cp.getPartNum()));
		}
		cpFromPIDs = cpsWithContractVal;
		
		processRestrictedPart(cpFromPIDs, header);
		setUpHeaderInfo(chsContract,header);
		buildConfiguratorProduct(cpFromPIDs, header); // build product, as the same
												// time build group of this
												// product.
	}
	/**
	 * Reset the needShow flag based on QTY, and also will set needShow Flag  based on parts relationship
	 * And get restricted main parts list and put it into view bean.
	 * @param cpFromPIDs
	 * @param header
	 */
	private void processRestrictedPart(List<ConfiguratorPart> cpFromPIDs, ConfiguratorHeader header){
		
		// Can not set it within one loop, firstly need to make sure all main part's need show flag is correct
		RestrictedPartSearchUIParams uiParams = new RestrictedPartSearchUIParams();
		for (ConfiguratorPart cp : cpFromPIDs) {
			// Set the partCategoryId, which is coded by subId and part group 
			cp.setPartCategoryId(cp.getSubId() + RestrictedPartSearchUIParams.SEPERATOR_CONJUNCTION + cp.getSapMatlTypeCodeGroupCode());
			if(cp.isRestrictedPart()){
				if ((cp.getPartQty() != null && cp.getPartQty() != 0)
						|| (cp.getOrigQty() != null && cp.getOrigQty() != 0)) {
					cp.setNeedShow(true);
				}
			}
		}
		List <ConfiguratorPart> needRemovePartsList = new ArrayList<ConfiguratorPart>();
		for (ConfiguratorPart cp : cpFromPIDs) {
			if(cp.isRestrictedPart()){
				// Here can not get subscription part by calling cp.getAssociatedSubscrptnPart
				// So compose a new function to get its subscription part
				// Set the overage or daily part's need show flag as its subscription part
				if(cp.isOvrage()){
					ConfiguratorPart subscrptnPart = getAssociatedSubscrptnPart(cp, cpFromPIDs);
					if(subscrptnPart != null){
						cp.setNeedShow(subscrptnPart.isNeedShow());
						// if its subscription part is not restricted part, directly remove it.
						if(!subscrptnPart.isRestrictedPart()){
							needRemovePartsList.add(cp);
						}
					}					
				}
				// Daily part will be obsoleted from 15.2, so directly remove it.
				if(cp.isDaily()){
					needRemovePartsList.add(cp);
				}
				// Set the additional set up part's need show flag as its set up part
				// Actually additional set up part is the overage part of set up part
				if(cp.isAddiSetUp()){
					ConfiguratorPart setUpPart = getAssociatedSetUpPart(cp, cpFromPIDs);
					if(setUpPart != null){
						cp.setNeedShow(setUpPart.isNeedShow());
						// if its setup part is not restricted part, directly remove it.
						if(!setUpPart.isRestrictedPart()){
							needRemovePartsList.add(cp);
						}
					}										
				}
				// Subscription parts, set up parts, on demand parts, and human service parts are main parts
				if(cp.isSubscrptn() || cp.isSetUp() || cp.isOnDemand() || cp.isHumanSrvs()){
					uiParams.addRestrictedMainPart(cp);
					if(cp.isNeedShow()){
						uiParams.addRestrictedMainPartAlreadyShow(cp);
					}
				}
			}	
		}
		
		cpFromPIDs.removeAll(needRemovePartsList);
		header.setRstrctPartSearchUIParams(uiParams);
	}
	
	private ConfiguratorPart getAssociatedSubscrptnPart(ConfiguratorPart cp, List<ConfiguratorPart> cpFromPIDs){
		for (ConfiguratorPart part : cpFromPIDs) {
			if(part.getSubId().equals(cp.getSubId()) && part.isSubscrptn()){
				return part;
			}
		}
		return null;
	} 
	
	private ConfiguratorPart getAssociatedSetUpPart(ConfiguratorPart cp, List<ConfiguratorPart> cpFromPIDs){
		for (ConfiguratorPart part : cpFromPIDs) {
			if(part.getSubId().equals(cp.getSubId()) && part.isSetUp()){
				return part;
			}
		}
		return null;
	} 

	private void buildConfiguratorProduct(List<ConfiguratorPart> cps,
			ConfiguratorHeader header) {
		Map<String, ConfiguratorProduct> cprodMap = new HashMap();
		ConfiguratorProduct cprod = null;
		if (cps != null) {
			int i = 0;
			for (ConfiguratorPart cp : cps) {
				if (i == 0) {
					header.setPidDscr(cp.getPidDscr());
					i++;
				}
				Double pricedb = getCPPrice(cp, header); // set up price
				if (pricedb != null)
					cp.setPrice(pricedb);
				String wwideProdCode = cp.getWwideProdCode();
				if (cprodMap.get(wwideProdCode) == null) { // if the product with the wwide prod code not exists, will add a new one.
					cprod = new ConfiguratorProduct();
					cprod.setWwideProdCode(cp.getWwideProdCode());
					cprod.setWwideProdCodeDscr(cp.getWwideProdCodeDscr());
					cprodMap.put(wwideProdCode, cprod);
					header.addConfiguratorProduct(cprod);

				} else {
					cprod = cprodMap.get(cp.getWwideProdCode());
				}
				addConfiguratorPartIntoProduct(cp, cprod); // add configurator part into product.
			}
			if(header.getProducts()!=null)
				logContext.debug(this, "products size:"
						+ header.getProducts().size());
		}
	}

    /**
     * DOC get the corresponding set up part of the subsumed subscription part
     * 
     * @param cps
     * @param subsumedSubscriptionPart
     * @return
     */
    private ConfiguratorPart getSetUpPart(List<ConfiguratorPart> cps, ConfiguratorPart subsumedSubscriptionPart) {
        for (ConfiguratorPart cp : cps) {
            if (cp.isSetUp() && cp.getSubId().equals(subsumedSubscriptionPart.getSubId())) {
                return cp;
            }
        }
        return null;
    }

    private void addConfiguratorPartIntoProduct(ConfiguratorPart cp,
			ConfiguratorProduct cprod) {
		String cpGroupCode = cp.getSapMatlTypeCodeGroupCode();
		ConfiguratorPartGroup cpg = cprod.getPartGroup(cpGroupCode);
		if (cpg == null) { // if the group with the group code not exists, will add a new one.
			cpg = new ConfiguratorPartGroup();
			cpg.setSapMatlTypeCodeGroupCode(cp.getSapMatlTypeCodeGroupCode());
			cpg.setSapMatlTypeCodeGroupDscr(cp.getSapMatlTypeCodeGroupDscr());
			cprod.addPartGroup(cpg);
		}
		cpg.addConfiguratorPart(cp); // add part into group.
	}

	/**
	 * get price per lob & band level.
	 *
	 * @param cp
	 * @param lob
	 * @param bandLevel
	 * @return
	 */
	private Double getCPPrice(ConfiguratorPart cp, ConfiguratorHeader header) {
		String price = null;
		Double pricedb = null;
//		logContext.debug(this, "log_bandLevel:" + header.getLob() + "_" + header.getBandLevel());
		if (header.getLob().equalsIgnoreCase("PA") || header.getLob().equalsIgnoreCase("PAE")
				|| header.getLob().equalsIgnoreCase("PAUN") || QuoteConstants.LOB_SSP.equalsIgnoreCase(header.getLob())) {
			if (header.getBandLevel() == null || header.getBandLevel().trim().length() == 0) {// no
																		// customer
																		// is
																		// selected.
				price = cp.getSvpLevelB();
			} else {
				if (header.getBandLevel().equalsIgnoreCase("A"))
					price = cp.getSvpLevelA();
				else if (header.getBandLevel().equalsIgnoreCase("B"))
					price = cp.getSvpLevelB();
				else if (header.getBandLevel().equalsIgnoreCase("D"))
					price = cp.getSvpLevelD();
				else if (header.getBandLevel().equalsIgnoreCase("E"))
					price = cp.getSvpLevelE();
				else if (header.getBandLevel().equalsIgnoreCase("F"))
					price = cp.getSvpLevelF();
				else if (header.getBandLevel().equalsIgnoreCase("G"))
					price = cp.getSvpLevelG();
				else if (header.getBandLevel().equalsIgnoreCase("H"))
					price = cp.getSvpLevelH();
				else if (header.getBandLevel().equalsIgnoreCase("I"))
					price = cp.getSvpLevelI();
				else if (header.getBandLevel().equalsIgnoreCase("J"))
					price = cp.getSvpLevelJ();
				else if (header.getBandLevel().equalsIgnoreCase("GV"))
					price = cp.getSvpLevelGV();
				else if (header.getBandLevel().equalsIgnoreCase("ED"))
					price = cp.getSvpLevelED();
			}
		} else if (header.getLob().equalsIgnoreCase("FCT"))
			price = cp.getSvpLevelA();
		if (StringUtils.isNotBlank(price))
			pricedb = new Double(price);
		return pricedb;
	}

	public void changeRampUpPeriods(ConfigureHostedServiceContract chsContract,ConfiguratorHeader header) throws TopazException{

		//1.change ramp up periods
		List<ConfiguratorPart> cps = chsContract.getConfiguratorParts();
		if(cps == null || cps.size()==0){
			buildConfiguratorHeader(chsContract,header);
			return;
		}else{
			//check every part's period.
			for(ConfiguratorPart cp:cps){
				int period = cp.getRampUpPeriod();
				if(period == 0){ // If 0
					cp.setRampUpLineItems(null);
				}else if(period >0){
					if(cp.getRampUpLineItems() == null){
						//indicate there is no items, so add number of period ramp up line items.
						addRampUpLineItemsWithPeriod(cp);
					}else if(period > cp.getRampUpLineItems().size()){
						//indicate there is a need to add items with the difference value.Notice need to add behind the tailor.
						addRampUpLineItemsWithDiff(cp);
					}else if(period < cp.getRampUpLineItems().size()){	//ie.2<3
						logContext.debug(this, "cp.getRampUpLineItems().size():"+cp.getRampUpLineItems().size());
						//indicate there is a need to delete items with the difference value.Notice need to delete from the tailor to head.
						delRampUpLineItemsViaDiff(cp);
					}
				}
			}
			//2.build header, then replace with contract values, including quantity....
			buildConfiguratorHeader(chsContract,header);
		}
	}

	private void setUpHeaderInfo(ConfigureHostedServiceContract chsContract, ConfiguratorHeader header) throws TopazException {
		header.setWebQuoteNum(chsContract.getWebQuoteNum());
		header.setCntryCode(chsContract.getCntryCode());
		header.setCurrencyCode(chsContract.getCurrencyCode());
		header.setPid(chsContract.getPid());
		header.setBandLevel(chsContract.getBandLevel());
		header.setLob(chsContract.getLob());
		header.setCTFlag(chsContract.getCTFlag());
		header.setConfigId(chsContract.getConfigId());
		header.setOrgConfigId(chsContract.getOrgConfigId());
		header.setChrgAgrmtNum(chsContract.getChrgAgrmtNum());
		header.setConfigrtnActionCode(chsContract.getConfigrtnActionCode());
		//Notes://CAMDB10/85256B890058CBA6/8278F0CE794010B985256D24005FCB4F/BBD5DF9ECAD70B4C85257A7F0068E9BA
		if(StringUtils.isNotBlank(chsContract.getCaEndDate()))
			header.setEndDate(DateUtil.parseDate(chsContract.getCaEndDate()));
		else{
			Date caEndDate = null;
			if(chsContract.getWebQuoteNum() != null && StringUtils.isNotBlank(chsContract.getConfigId())){
				List confgrtnList = findConfiguratorsByWebQuoteNum(chsContract.getWebQuoteNum());
		        if(confgrtnList != null && confgrtnList.size() > 0){
		        	for(int i = 0; i< confgrtnList.size(); i++){
		        		PartsPricingConfiguration ppc = (PartsPricingConfiguration)confgrtnList.get(i);
		        		if(chsContract.getConfigId().equals(ppc.getConfigrtnId())){
		        			caEndDate = ppc.getEndDate();
		        		}
		        	}
		        }
			}
			if(caEndDate != null)
				header.setEndDate(caEndDate);
		}
		if(header.getTerm() == null || header.getTerm().intValue() == 0 )
			header.setTerm(chsContract.getTerm());
		header.setAvaliableBillingFrequencyOptions(chsContract.getAvaliableBillingFrequencyOptions());
		if (StringUtils.isNotBlank(chsContract.getAddOnTradeUpFlag())) {
			if (chsContract.getAddOnTradeUpFlag().equals("0"))
				header.setAddOnOrTradeUp(false);
			else if (chsContract.getAddOnTradeUpFlag().equals("1"))
				header.setAddOnOrTradeUp(true);
		}
		//Update for FCT TO PA Finalization. Currently, Finalization UI rule is same as Add on.
		if (StringUtils.isNotBlank(chsContract.getConfigrtnActionCode())) {
			if (chsContract.getConfigrtnActionCode().equalsIgnoreCase(PartPriceConstants.ConfigrtnActionCode.FCT_TO_PA_FNL)
					|| chsContract.getConfigrtnActionCode().equalsIgnoreCase(PartPriceConstants.ConfigrtnActionCode.ADD_TRD))
				header.setAddOnOrTradeUp(true);
			else
				header.setAddOnOrTradeUp(false);
		}

		if (StringUtils.isNotBlank(chsContract.getCTFlag())) {
			if (chsContract.getCTFlag().equals("0"))
				header.setCoTermed(false);
			else if (chsContract.getCTFlag().equals("1"))
				header.setCoTermed(true);
		}
		header.setOverrideFlag(chsContract.getOverrideFlag());
		header.setOverridePilotFlag(chsContract.getOverridePilotFlag());
		header.setOverrideRstrctFlag(chsContract.getOverrideRstrctFlag());

		//FCT TO PA Finalization, calculate term
		if(PartPriceConstants.ConfigrtnActionCode.FCT_TO_PA_FNL.equals(header.getConfigrtnActionCode())
				&& header.getEndDate() == null ){ //co-term end date is null.
			String configrtnId = header.getConfigId();
			String orgConfigrtnId = header.getOrgConfigId();
			//if original configrtnId is null or configrtnId is equal to orgConfigrtnId, it means first time to Finalization.
			if(StringUtils.isBlank(orgConfigrtnId) || configrtnId.equalsIgnoreCase(orgConfigrtnId)){
				orgConfigrtnId = configrtnId;
			}
			try {
				RedirectConfiguratorDataBasePack dataPack = PartPriceHelper.calculateAddOnFinalizationTerm(
						header.getChrgAgrmtNum(), orgConfigrtnId, null, null, header.getConfigrtnActionCode(),false,null);
				if(dataPack != null){
					if(StringUtils.isNotBlank(dataPack.getFctToPaFinalTerm()))
						header.setTermForFCTToPAFinalization(dataPack.getFctToPaFinalTerm());
				}
			} catch (QuoteException e) {
				throw new TopazException(e);
			}
		}
	}

	private void addRampUpLineItemsWithPeriod(ConfiguratorPart cp){
		int period = cp.getRampUpPeriod();
		for(int i = 0;i<period;i++){
			ConfiguratorPart ruli = createRULI(cp);
			cp.addRampUpLineItem(ruli, i);
		}
	}

	private void addRampUpLineItemsWithDiff(ConfiguratorPart cp){
		int period = cp.getRampUpPeriod();	//ie 3
		int size = cp.getRampUpLineItems().size();	//ie 1
		int diff = period - size;	//ie 2
		for(int i = 0;i<diff;i++){
			ConfiguratorPart ruli = createRULI(cp);
//			logContext.debug(this, "size + i:"+size + i);
			cp.addRampUpLineItem(ruli, size + i);
		}
	}

	private void delRampUpLineItemsViaDiff(ConfiguratorPart cp){
		int period = cp.getRampUpPeriod();	//ie 2
		int size = cp.getRampUpLineItems().size();	//ie 3
		int diff = size - period;	//ie 1
		for (int i = 0;i<diff;i++){
			cp.delRampUpLineItem( size-i-1);
		}
	}

	private ConfiguratorPart createRULI(ConfiguratorPart cp){
		ConfiguratorPart ruli = new ConfiguratorPart();
		ruli.setPartNum(cp.getPartNum());
		ruli.setRampUpDuration(new Integer(1));
		return ruli;
	}
	/**
	 * This method is for FCT to PA finalization.
	 * Notes://D01DBL35/8525721300181EEE/477C010BD75EC87C85256A2D006A582E/FBB1A208FDC5AAAE852579E900572C27
	 * Please note - For line items with...
	 * @param ct
	 * @param map
	 * @return
	 * @throws QuoteException
	 * @throws TopazException
	 */
	private List<ConfiguratorPart> getFCTToPAFNLParts(AddOrUpdateConfigurationContract ct,
			Map<String, ConfiguratorPart> map)throws QuoteException, TopazException{
		List<ConfiguratorPart> lineItemList = new ArrayList<ConfiguratorPart>();

		List<ConfiguratorPart> configuratorPartList = associateParts(getConfiguratorParts(ct, map));

		List<ConfiguratorPart> partsFromChrgAgrmList = getPartsFromChrgAgrm(ct, map);
		//get original charge agreement number and configuration id.
	    String orignlSalesOrdRefNum = null;
	    String orignlConfigrtnId = null;

		if(partsFromChrgAgrmList != null){
			for(ConfiguratorPart partChrAgrm : partsFromChrgAgrmList){
				//For Finalization, each original sales order ref num in CA should be same.
				orignlSalesOrdRefNum = partChrAgrm.getOrignlSalesOrdRefNum();
				orignlConfigrtnId = partChrAgrm.getOrignlConfigrtnId();
				break;
			}
		}

		chrgAgrmPartList = associateParts(partsFromChrgAgrmList);
		Map<String, ConfiguratorPart> origPartMap = toMap(chrgAgrmPartList);

		List<ConfiguratorPart> newlyAddedSubscrptnPartList = new ArrayList<ConfiguratorPart>();
		List<ConfiguratorPart> removedSubscrptnPartList = new ArrayList<ConfiguratorPart>();

		for(ConfiguratorPart newPart : configuratorPartList){
			//Only Sub and On Demand parts are in CA, per Finalization rules.
			if(newPart.isSubscrptn()){
				ConfiguratorPart origPart = origPartMap.remove(newPart.getPartNum());

				if(origPart != null){
					//Per Finalization rules, all parts in CA will be marked replaced
					//and correspond new line items will be added.
					lineItemList.add(newPart);

					origPart.markAsReplaced(newPart);
					lineItemList.add(origPart);

					ConfiguratorPart newOveragePart = newPart.getAssociatedOveragePart();
					if(newOveragePart != null){
						lineItemList.add(newOveragePart);
					}

					ConfiguratorPart origOveragePart = origPart.getAssociatedOveragePart();
					if(origOveragePart != null){
						origOveragePart.markAsReplaced(newOveragePart);
						lineItemList.add(origOveragePart);
					}

					if(newPart.getAssociatedDailyPart() != null){
						lineItemList.add(newPart.getAssociatedDailyPart());//Add new daily parts as new line items
					}

				} else {
					//No original part with same part#, this is a new part
					lineItemList.add(newPart);

					if(newPart.getAssociatedDailyPart() != null){
						lineItemList.add(newPart.getAssociatedDailyPart());
					}

					if(newPart.getAssociatedOveragePart() != null){
						lineItemList.add(newPart.getAssociatedOveragePart());
					}

					logContext.debug(this, "new part added from configurator : " + newPart.toString());
					newlyAddedSubscrptnPartList.add(newPart);
				}

			} else if(newPart.isSetUp()){
				//Update on 2012-1-12 per PL : Notes://CAMDB10/85256B890058CBA6/5D3D446FEB23FC918525718E006EAFDE/7446F20C4604F57985257982005B3409
				if(ct.isFromCPQConfigurator()){	//from GST
					ConfiguratorPart origPart = origPartMap.remove(newPart.getPartNum());
					if(origPart != null){	//in CA.
						int origQty = origPart.getPartQty().intValue();
						int newQty = newPart.getPartQty().intValue();
						if(newQty > origQty){
							int delta = newQty - origQty;
							newPart.setPartQty(delta);
							lineItemList.add(newPart);
						} else {
							//Do nothing here
						}
					} else {	//not in CA
						lineItemList.add(newPart);
					}
				}else {	//from Basic
					lineItemList.add(newPart);
					if (newPart.getAssociatedSubsumedSubscrptnPart() != null){
						lineItemList.add(newPart.getAssociatedSubsumedSubscrptnPart());
					}
				}

			} else if(newPart.isAddiSetUp()){
				ConfiguratorPart origPart = origPartMap.remove(newPart.getPartNum());

				if(origPart != null){
					//keep the old codes, actually, for Finalization, there are only Sub and On Demand in CA.
					if(setUpPartWithSameSubIdReturned(newPart.getSubId(), configuratorPartList)){
						lineItemList.add(newPart);
						origPart.markAsReplaced();
						lineItemList.add(origPart);
					} else {
						//Do nothing here
					}
				} else {
					//No original part with same part#, this is a new part
					lineItemList.add(newPart);
				}

			} else if(newPart.isOnDemand()){

				ConfiguratorPart origPart = origPartMap.remove(newPart.getPartNum());
				if(origPart != null){
					//Per Finalization rules, all parts in CA will be marked replaced
					//and correspond new line items will be added.
					lineItemList.add(newPart);
					origPart.markAsReplaced(newPart);
					lineItemList.add(origPart);

				} else {
					lineItemList.add(newPart);
				}
			} else if(newPart.isHumanSrvs()){
				lineItemList.add(newPart);
			}
		}

		Map<String, ConfiguratorPart> configuratorPartMap = toMap(configuratorPartList);
		for(ConfiguratorPart origPart : origPartMap.values()){
			if(origPart.isSubscrptn()){
				ConfiguratorPart newPart = configuratorPartMap.get(origPart.getPartNum());
				boolean isMissingPart = false;//refer to rtc#215704. If currnet ca part is a missing part, the part will be added into replaced list.
				if(missingPartLst != null && missingPartLst.contains(origPart.getPartNum()))
					isMissingPart = true;
				if(newPart == null && !isMissingPart){
					origPart.markAsReplaced();
					lineItemList.add(origPart);

					ConfiguratorPart overagePart = origPart.getAssociatedOveragePart();
					if(overagePart != null){
						overagePart.markAsReplaced();
						lineItemList.add(overagePart);
					}

					logContext.debug(this, "original part removed from configurator: " + origPart.toString());
					removedSubscrptnPartList.add(origPart);
				}
			}
		}

		processForTradeUp(newlyAddedSubscrptnPartList, removedSubscrptnPartList);

		for(ConfiguratorPart part : lineItemList){
			//FCT TO PA Finalization
			part.setOrignlSalesOrdRefNum(orignlSalesOrdRefNum);
			part.setOrignlConfigrtnId(orignlConfigrtnId);
			//Clear daily/overage parts for replaced subscription parts
			if(part.isPartReplaced() && part.isSubscrptn()){
				part.setAssociatedDailyPart(null);
				part.setAssociatedOveragePart(null);
			}
		}

		return lineItemList;
	}

	private boolean checkPartLimitExceedLimit(String configrtnId ,List lineItems,List configrnParts){
		int partCount = configrnParts == null ?0 : configrnParts.size();
		int totalQli = 0;
		if (lineItems != null && lineItems.size() >0){
			for (Object obj : lineItems){
				QuoteLineItem qli = (QuoteLineItem)obj;
				if (configrtnId.equals(qli.getConfigrtnId())){
					continue;
				}
				totalQli++;
			}
		}
		int limit = PartPriceConfigFactory.singleton().getElaLimits();

		if (totalQli+partCount > limit){
			return false;
		}
		return true;
	}

	/**
	 * when add on/tradeup or FCT to PA , need update EXT_ENTIRE_CONFIGRTN_FLAG
	 * Case 1.CA has not  parts
	 * Case 2.CA has  parts,current quote no parts
	 * Case 3.CA has parts ,current quote also has parts
	 * @param origPartMap  All CA parts
	 * @param lineItemList Current All parts
	 * @return
	 */
	public int getExtEntireConfigFlagAccordingToParts(Map<String, ConfiguratorPart> origPartMap,
			List<ConfiguratorPart> lineItemList){
		int extEntireConfigFlag = 1;

		if(origPartMap == null || origPartMap.size() < 1)
			return extEntireConfigFlag ;

		if (origPartMap !=null && origPartMap.size()>0
				&&(lineItemList == null || lineItemList.size()<1)){
			return extEntireConfigFlag = 0;
		}

		for (ConfiguratorPart lineItem : lineItemList){
				origPartMap.remove(lineItem.getPartNum());
		}


		if (origPartMap.size() > 0){
			extEntireConfigFlag = 0;
		}

		return extEntireConfigFlag;
	}

	private List findConfiguratorsByWebQuoteNum(String webQuoteNum)throws TopazException{
		List list = null;

		try {
			TransactionContextManager.singleton().begin();
			list = PartsPricingConfigurationFactory.singleton()
					.findPartsPricingConfiguration(webQuoteNum);
			TransactionContextManager.singleton().commit();
		} catch (TopazException e) {
			logContext.error(this, e);
			TransactionContextManager.singleton().rollback();
			throw e;
		}
		return list;
	}



	private void setTermExtensionToContract(AddOrUpdateConfigurationContract ct)
			throws TopazException {
		if (StringUtils.isEmpty(ct.getConfigId()))
			return;

		List<PartsPricingConfiguration> configurators = findConfiguratorsByWebQuoteNum(ct
				.getWebQuoteNum());

		if (configurators != null) {
			for (PartsPricingConfiguration ppc : configurators) {
				if (ppc.getConfigrtnId().equals(ct.getConfigId())) {
					ct.setTermExtensionFlag(ppc.isTermExtension() ? "1" : "0");
					ct.setSeviceDate(DateUtil.formatDate(ppc.getServiceDate()));
					ct.setServiceDateModType(ppc.getServiceDateModType() != null ? ppc
							.getServiceDateModType().toString() : null);
					break;
				}
			}
		}

	}

}
