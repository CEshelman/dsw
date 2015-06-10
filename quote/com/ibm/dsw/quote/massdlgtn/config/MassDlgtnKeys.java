package com.ibm.dsw.quote.massdlgtn.config;

/**
 * Copyright 2006 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * The <code>MassDlgtnKeys</code> class is to hold constant for
 * Action/State/Params
 * 
 * 
 * @author <a href="liuxinlx@cn.ibm.com">Andy Liu </a> <br/>
 * 
 * Creation date: 2007-3-8
 */
public interface MassDlgtnKeys {

    public static interface Action {
        public final static String MASS_DLGTN_ADD_DELEGATE = "MASS_DLGTN_ADD_DELEGATE";

        public final static String MASS_DLGTN_REMOVE_DELEGATE = "MASS_DLGTN_REMOVE_DELEGATE";

        public final static String MASS_DLGTN_SELECT_SALES_REP = "MASS_DLGTN_SELECT_SALES_REP";

        public final static String MASS_DLGTN_SHOW_SALES_SELECTION = "MASS_DLGTN_SHOW_SALES_SELECTION";

    }

    public static interface State {
        public final static String STATE_DISPLAY_MASS_DLGTN = "STATE_DISPLAY_MASS_DLGTN";

        public final static String STATE_SELECT_SALES_REP = "STATE_SELECT_SALES_REP";

        public final static String STATE_DISPLAY_SALES_REP_SELECTION = "STATE_DISPLAY_SALES_REP_SELECTION";

    }

    public static interface Params {

        public final static String SALES_USER_ID = "salesUserID";

        public final static String DELEGATE_USER_ID = "delegateUserID";

        public final static String DELEGATES_LIST = "delegatesList";

        public final static String CURRENT_USER_ID = "currentUserID";

        public final static String IS_SALES_MANAGER = "isSalesManager";

        public final static String SALES_USER_FULL_NAME = "salesUserFullName";

    }

    public static interface Forms {
        public static final String SALES_REPRESENTITIVE_MAIL = "sales_repre_mail";

        public static final String MASS_DLGTN_MAIL = "mass_dlgtn_mail";

        public final static String SALESREP_MAIL_BLANK_MSG = "salesrep_mail_blank_msg";

        public final static String SALESREP_MAIL_INVALID_MSG = "salesrep_mail_invalid_msg";

        public final static String EDITOR_MAIL_BLANK_MSG = "editor_mail_blank_msg";

        public final static String EDITOR_MAIL_INVALID_MSG = "editor_mail_invalid_msg";

    }
    
    public static interface Messages {
        public static final String APPRVR_NOT_DLGT_MSG = "apprvr_not_dlgt_msg";
    }

}
