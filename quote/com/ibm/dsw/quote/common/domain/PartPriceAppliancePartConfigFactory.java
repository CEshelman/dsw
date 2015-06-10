package com.ibm.dsw.quote.common.domain;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.jdom.Element;
import org.jdom.Text;

import com.ibm.dsw.quote.common.util.CommonServiceUtil;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;

/**
 * PartPriceSaaSPartConfigFactory.java
 *
 * <p>
 * Copyright 2011 by IBM Corporation All rights reserved.
 * </p>
 * 
 * @author <a href="jhma@cn.ibm.com">Edward</a> <br/>
 * Mar 31, 2011
 */
public class PartPriceAppliancePartConfigFactory{
	
	private static PartPriceAppliancePartConfigFactory singleton = null;

	private LogContext logContext = LogContextFactory.singleton()
			.getLogContext();
	
	private int totalLineItemLimit = 1;

	public PartPriceAppliancePartConfigFactory() {
		loadConfig();
	}
	
	private ApplianceDisplayUIConfig aduConfig = new ApplianceDisplayUIConfig();

	public static PartPriceAppliancePartConfigFactory singleton() {
		LogContext logCtx = LogContextFactory.singleton().getLogContext();

		if (PartPriceAppliancePartConfigFactory.singleton == null) {
			String factoryClassName = null;
			try {
				factoryClassName = PartPriceAppliancePartConfigFactory.class.getName();
				Class factoryClass = Class.forName(factoryClassName);
				PartPriceAppliancePartConfigFactory.singleton = (PartPriceAppliancePartConfigFactory) factoryClass
						.newInstance();
			} catch (IllegalAccessException iae) {
				logCtx.error(PartPriceAppliancePartConfigFactory.class, iae, iae
						.getMessage());
			} catch (ClassNotFoundException cnfe) {
				logCtx.error(PartPriceAppliancePartConfigFactory.class, cnfe, cnfe
						.getMessage());
			} catch (InstantiationException ie) {
				logCtx.error(PartPriceAppliancePartConfigFactory.class, ie, ie.getMessage());
			}
		}
		return singleton;
	}


	/**
	 * void
	 * lood the SaaS part config
	 */
	protected void loadConfig() {

		try {
			logContext.debug(this, "Loading part price Appliance part config ");
			Element rootElement = PartPriceConfigFactory.singleton().getAllRootElements();
			loadApplianceDisplayUiConfig(rootElement);
		} catch (Exception e) {
			e.printStackTrace();
			logContext.error(this, e,
					"Exception loading part price Appliance part config ");
		}
		logContext.debug(this, "Finished loading part price Appliance part config ");
	}

	protected void reset() {
		singleton = null;
	}
	/**
	 * Notes://D01DBL35/8525721300181EEE/477C010BD75EC87C85256A2D006A582E/F0A58B424A4A467E852579CF007E23AB
	 * @author majh
	 *
	 */
	private static class ApplianceDisplayUIConfig{
		public List <String> allowMtmInputPTLst = new ArrayList();
		public List <String> displayApplianceInfoDraftPTLst = new ArrayList();
		public List <String> displayApplianceAssociationInfoDraftPTLst = new ArrayList();
		public List <String> displayApplianceIdDropdownPTLst = new ArrayList();
		public List <String> displayApplianceInfoSubmittedPTLst = new ArrayList();
		public List <String> displayLineItemHoldPTLst = new ArrayList();
		public List <String> displayPocLst = new ArrayList();
		
		public List <String> allowAdditionalYearLst = new ArrayList();
		
		public List<String> getAllowMtmInputPTLst() {
			return allowMtmInputPTLst;
		}

		public void setAllowMtmInputPTLst(List<String> allowMtmInputPTLst) {
			this.allowMtmInputPTLst = allowMtmInputPTLst;
		}

		public List<String> getDisplayApplianceInfoDraftPTLst() {
			return displayApplianceInfoDraftPTLst;
		}

		public void setDisplayApplianceInfoDraftPTLst(
				List<String> displayApplianceInfoDraftPTLst) {
			this.displayApplianceInfoDraftPTLst = displayApplianceInfoDraftPTLst;
		}

		public List<String> getDisplayApplianceAssociationInfoDraftPTLst() {
			return displayApplianceAssociationInfoDraftPTLst;
		}

		public void setDisplayApplianceAssociationInfoDraftPTLst(
				List<String> displayApplianceAssociationInfoDraftPTLst) {
			this.displayApplianceAssociationInfoDraftPTLst = displayApplianceAssociationInfoDraftPTLst;
		}

		public List<String> getDisplayApplianceIdDropdownPTLst() {
			return displayApplianceIdDropdownPTLst;
		}

		public void setDisplayApplianceIdDropdownPTLst(
				List<String> displayApplianceIdDropdownPTLst) {
			this.displayApplianceIdDropdownPTLst = displayApplianceIdDropdownPTLst;
		}

		public List<String> getDisplayApplianceInfoSubmittedPTLst() {
			return displayApplianceInfoSubmittedPTLst;
		}

		public void setDisplayApplianceInfoSubmittedPTLst(
				List<String> displayApplianceInfoSubmittedPTLst) {
			this.displayApplianceInfoSubmittedPTLst = displayApplianceInfoSubmittedPTLst;
		}

		public List<String> getDisplayLineItemHoldPTLst() {
			return displayLineItemHoldPTLst;
		}

		public void setDisplayLineItemHoldPTLst(List<String> displayLineItemHoldPTLst) {
			this.displayLineItemHoldPTLst = displayLineItemHoldPTLst;
		}



		public List<String> getAllowAdditionalYearLst() {
			return allowAdditionalYearLst;
		}

		public void setAllowAdditionalYearLst(List<String> allowAdditionalYearLst) {
			this.allowAdditionalYearLst = allowAdditionalYearLst;
		}

		public List<String> getDisplayPocLst() {
			return displayPocLst;
		}

		public void setDisplayPocLst(List<String> displayPocLst) {
			this.displayPocLst = displayPocLst;
		}
		
		
		
		
	} //~end of class ApplianceDisplayUIConfig

	private void addStringToLstFromElementAttr(List lst, Element elem, String attrName){
		if(lst == null)
			return;
		String str = elem.getAttributeValue(attrName);//element attribute
		String[] strArray = null;
		if(StringUtils.isNotBlank(str))
			strArray = str.split(",");
		if(strArray !=null && strArray.length >0){
			for (int i = 0; i < strArray.length; i++)
				lst.add(strArray[i]);
		}
	}
	/**
	 * @param root
	 * void
	 * load the SaaS parts display UI configuration
	 */
	private void loadApplianceDisplayUiConfig(Element root) {
		Element aducElement = root.getChild("appliance-display-ui-config");//appliance-display-ui-config element
		List childrenElems = aducElement.getChildren();
		
		if(childrenElems != null && childrenElems.size() > 0){
			for (int ei = 0; ei < childrenElems.size(); ei++) {
				Element childElem = (Element) childrenElems.get(ei);
				if(childElem.getName().equals("allow_mtm_input")){//allow_mtm_input element
					List <String> tmpLst = new ArrayList(); 
					List <Element> ptElems = childElem.getChildren();//part-type element
					if (ptElems != null && ptElems.size() > 0) {
						for (int i = 0; i < ptElems.size(); i++) {
							Element ptElem = ptElems.get(i);
							addStringToLstFromElementAttr(tmpLst,ptElem, "value");
						}
						aduConfig.setAllowMtmInputPTLst(tmpLst);
					}
				}	
				if(childElem.getName().equals("display_appliance_info_draft")){//display_appliance_info_draft element
					List <String> tmpLst = new ArrayList(); 
					List <Element> ptElems = childElem.getChildren();//part-type element
					if (ptElems != null && ptElems.size() > 0) {
						for (int i = 0; i < ptElems.size(); i++) {
							Element ptElem = ptElems.get(i);
							addStringToLstFromElementAttr(tmpLst,ptElem, "value");
						}
						aduConfig.setDisplayApplianceInfoDraftPTLst(tmpLst);
					}
				}	
				if(childElem.getName().equals("display_appliance_association_info_draft")){//display_appliance_association_info_draft element
					List <String> tmpLst = new ArrayList(); 
					List <Element> ptElems = childElem.getChildren();//part-type element
					if (ptElems != null && ptElems.size() > 0) {
						for (int i = 0; i < ptElems.size(); i++) {
							Element ptElem = ptElems.get(i);
							addStringToLstFromElementAttr(tmpLst,ptElem, "value");
						}
						aduConfig.setDisplayApplianceAssociationInfoDraftPTLst(tmpLst);
					}
				}
				if(childElem.getName().equals("display_appliance_id_dropdown")){//display_appliance_id_dropdown element
					List <String> tmpLst = new ArrayList(); 
					List <Element> ptElems = childElem.getChildren();//part-type element
					if (ptElems != null && ptElems.size() > 0) {
						for (int i = 0; i < ptElems.size(); i++) {
							Element ptElem = ptElems.get(i);
							addStringToLstFromElementAttr(tmpLst,ptElem, "value");
						}
						aduConfig.setDisplayApplianceIdDropdownPTLst(tmpLst);
					}
				}				
				if(childElem.getName().equals("display_appliance_info_submitted")){//display_appliance_info_submitted element
					List <String> tmpLst = new ArrayList(); 
					List <Element> ptElems = childElem.getChildren();//part-type element
					if (ptElems != null && ptElems.size() > 0) {
						for (int i = 0; i < ptElems.size(); i++) {
							Element ptElem = ptElems.get(i);
							addStringToLstFromElementAttr(tmpLst,ptElem, "value");
						}
						aduConfig.setDisplayApplianceInfoSubmittedPTLst(tmpLst);
					}
				}	
				
				if(childElem.getName().equals("display_poc")){//display_poc
					List <String> tmpLst = new ArrayList(); 
					List <Element> ptElems = childElem.getChildren();//part-type element
					if (ptElems != null && ptElems.size() > 0) {
						for (int i = 0; i < ptElems.size(); i++) {
							Element ptElem = ptElems.get(i);
							addStringToLstFromElementAttr(tmpLst,ptElem, "value");
						}
						aduConfig.setDisplayPocLst(tmpLst);
					}
				}			
				if(childElem.getName().equals("display_line_item_hold")){//display_line_item_hold element
					List <String> tmpLst = new ArrayList(); 
					List <Element> ptElems = childElem.getChildren();//part-type element
					if (ptElems != null && ptElems.size() > 0) {
						for (int i = 0; i < ptElems.size(); i++) {
							Element ptElem = ptElems.get(i);
							addStringToLstFromElementAttr(tmpLst,ptElem, "value");
						}
						aduConfig.setDisplayLineItemHoldPTLst(tmpLst);
					}
				}
				if(childElem.getName().equals("total_line_item_limit")){//total_line_item_limit element
					List <Text> texts = childElem.getContent();//199
					if (texts != null && texts.size() > 0) {
						Text text = (Text)texts.get(0);
						totalLineItemLimit = Integer.valueOf(text.getTextTrim());
					}
				}
			}
		}
		
	}
	
	public boolean allowMtmInput(QuoteLineItem qli){
		String apt = CommonServiceUtil.getAppliancePartType(qli);
		if(StringUtils.isNotBlank(apt)
			&& aduConfig.getAllowMtmInputPTLst().contains(apt)){
			return true;
		}
		return false;
	}
	public boolean displayApplianceInfoDraft(QuoteLineItem qli){
		String apt = CommonServiceUtil.getAppliancePartType(qli);
		if(StringUtils.isNotBlank(apt)
			&& aduConfig.getDisplayApplianceInfoDraftPTLst().contains(apt)){
			return true;
		}
		return false;
	}
	public boolean displayApplianceAssociationInfoDraft(QuoteLineItem qli){
		String apt = CommonServiceUtil.getAppliancePartType(qli);
		if(StringUtils.isNotBlank(apt)
			&& aduConfig.getDisplayApplianceAssociationInfoDraftPTLst().contains(apt)){
			return true;
		}
		return false;
	}
	
	public boolean displayAllowMtmInputInfoDraft(QuoteLineItem qli){
		String apt = CommonServiceUtil.getAppliancePartType(qli);
		if(StringUtils.isNotBlank(apt) && aduConfig.getAllowMtmInputPTLst().contains(apt))
			return true;
		return false;
	}
	
	
	public boolean displayApplianceIdDropdown(QuoteLineItem qli){
		String apt = CommonServiceUtil.getAppliancePartType(qli);
		if(StringUtils.isNotBlank(apt)
			&& aduConfig.getDisplayApplianceIdDropdownPTLst().contains(apt)){
			return true;
		}
		return false;
	}
	public boolean displayApplianceInfoSubmitted(QuoteLineItem qli){
		String apt = CommonServiceUtil.getAppliancePartType(qli);
		if(StringUtils.isNotBlank(apt)
			&& aduConfig.getDisplayApplianceInfoSubmittedPTLst().contains(apt)){
			return true;
		}
		return false;
	}
	public boolean displayLineItemHold(QuoteLineItem qli){
		String apt = CommonServiceUtil.getAppliancePartType(qli);
		if(StringUtils.isNotBlank(apt)
			&& aduConfig.getDisplayLineItemHoldPTLst().contains(apt)){
			return true;
		}
		return false;
	}
	
	public boolean displayPoc(QuoteLineItem qli){
		// For Ownership transfered parts
    	if (qli.isOwerTransferPart()) {
			return false;
		}
		String apt = CommonServiceUtil.getAppliancePartType(qli);
		if(StringUtils.isNotBlank(apt) && aduConfig.getDisplayPocLst().contains(apt))
			return true;
		return false;
	}
	
	public int getTotalLineItemLimit() {
		return totalLineItemLimit;
	}
}
