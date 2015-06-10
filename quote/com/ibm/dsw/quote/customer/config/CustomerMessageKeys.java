package com.ibm.dsw.quote.customer.config;

import com.ibm.dsw.quote.base.config.MessageKeys;

/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * This <code>CustomerMessageKeys<code> class.
 *    
 * @author: zhengmr@cn.ibm.com
 * 
 * Creation date: 2007-3-6
 */

public interface CustomerMessageKeys extends MessageKeys {
    
	public static final String NO_CUSTOMER_DATA_HINT = "no_customer_data_hint";
	public static final String SITE_NUM = "site_num";
	public static final String CITY = "city";
	public static final String POSTAL_CODE = "postal_code";
	public static final String AGREEMENT_NUM = "agreement_num";
	
	// Create Customer fields
	public static final String RSVPSRPPRICINGONLY = "Renewal RSVP/SRP pricing only";
	public static final String CURRENCY = "currency";
	public static final String COMPANY_NAME = "company_name";
	public static final String COMPANY_NAME1 = "company_name1";
	public static final String COMPANY_NAME2 = "company_name2";
	public static final String ADDR_LINE_1 = "cs_address1";
	public static final String ADDR_LINE_2 = "cs_address2";
	public static final String STATE = "state";
	public static final String VAT = "cs_vat";
//	public static final String VAT_FRA = "cs_vat_fra";
//	public static final String VAT_NLD = "cs_vat_nld";
	public static final String INDUSTRY = "cs_industry";
	public static final String COMP_SIZE = "company_size";
	public static final String FIRST_NAME = "first_name";
	public static final String LAST_NAME = "last_name";
	public static final String PHONE_NUM = "phone_number";
	public static final String FAX_NUM = "fax_number";
	public static final String EMAIL_ADDR = "email_addr";
	public static final String COMM_LANGUAGE = "comm_language";
	public static final String MEDIA_LANGUAGE = "media_language";
	public static final String OFFERING_TYPE = "offering_type";
	public static final String AGREEMENT_TYPE = "agrmnt_type";
	public static final String AGREEMENT_OPTION = "agrmnt_option";
	public static final String TRANS_SVP_LEVEL = "tran_svp_lvl";
	public static final String GOV_SITE_TYPE = "gov_site_type";
	public static final String NEW_CUST_AGRMNT_MSG = "new_cust_agrmnt_msg";
	public static final String ADDITIONAL_SITE_AGRMNT_MSG = "additional_site_agrmnt_msg";
	public static final String NEW_PA_CUST_STAND_MSG = "new_stand_pa_cust_msg";
	public static final String STATE_N_LOCAL = "state_n_local";
	public static final String FEDERAL = "federal";
	
	public static final String VIEW_ORGN_RQ_DETAIL = "view_original_rq_details";
	public static final String INVLD_CTRCT_ERR_MSG = "invld_ctrct_err_msg";
	public static final String INVLD_GOV_ERR_MSG = "invld_gov_err_msg";
	public static final String CTRCT_NOT_GOV_MSG = "ctrct_not_gov_msg";
	public static final String SIGNATURE_MSG = "signature_msg";
	public static final String NEWCUST_AGRMNT_SGNTUR_MSG = "newCust_agrmnt_sgntur_msg";
	public static final String NEW_CUST_AGRMNT_CSA_REL_MSG = "new_cust_agrmnt_csa_rel_msg";
	public static final String AGRMNT_EMPTY_MSG = "agreement_not_selected_msg";
	
	//for ToU use
	public static final String SAAS_TERM_OF_USE_LINK = "SaaS_terms_of_use_link";
}
