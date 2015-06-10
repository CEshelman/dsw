package com.ibm.dsw.quote.base.config;


/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 *
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 *
 * This <code>NoUndoStateKeys<code> class.
 *
 * @author: zhaoxw@cn.ibm.com
 *
 * Creation date: Apr 10, 2007
 */

public class NoUndoStateKeys {

    public static final String[] noUndoStates = new String[] {
            StateKeys.STATE_REDIRECT_ACTION,
            "STATE_DISPLAY_STATUS_TRACKER_SETTINGS",//FindQuoteStateKeys.STATE_DISPLAY_STATUS_TRACKER_SETTINGS
            "STATE_DISPLAY_STATUS_TRACKER",//FindQuoteStateKeys.STATE_DISPLAY_STATUS_TRACKER
            "STATE_DISPLAY_PART_DETAILS",//PartDetailsStateKeys.STATE_DISPLAY_PART_DETAILS
            "STATE_DISPLAY_PRICE_DETAILS",//PartDetailsStateKeys.STATE_DISPLAY_PRICE_DETAILS
            "STATE_DISPLAY_PART_COPREREQUISITES",//PartDetailsStateKeys.STATE_DISPLAY_PART_COPREREQUISITES
            "STATE_DISPLAY_OVERRIDE_DATE_POPUP" //DraftQuoteStateKeys.STATE_DISPLAY_OVERRIDE_DATE_POPUP
            ,"STATE_SET_USER_COOKIE" //DraftQuoteStateKeys.STATE_SET_USER_COOKIE
            ,"STATE_QUOTE_RTF_DOWNLOAD" //ExportQuoteStateKeys.STATE_QUOTE_RTF_DOWNLOAD
            ,"STATE_EXPORT_QUOTE" //ExportQuoteStateKeys.STATE_EXPORT_QUOTE
            ,"STATE_EXPORT_QUOTE_NATIVE_EXCEL" //ExportQuoteStateKeys.STATE_EXPORT_QUOTE_NATIVE_EXCEL
            ,StateKeys.STATE_DISPLAY_ATTACH_FILES_TO_QUOTE
            ,StateKeys.STATE_DISPLAY_UPLOAD_PROGRESS_REPORT
            ,StateKeys.STATE_DISPLAY_ATTACH_FILES_INPUT
            ,StateKeys.STATE_DISPLAY_ATTACH_FILES_COMPLETE
            ,"STATE_DISPLAY_HOME"
			,"STATE_RETRIEVE_ATTACHMENT"
			,"STATE_DISPLAY_ADD_ATTACHS_FOR_SALESINFO"
			,"STATE_DISPLAY_ADD_ATTACHS_INPUT_FOR_SALESINFO"
			,"STATE_DISPLAY_ADD_ATTACHS_COMPLETE_FOR_SALESINFO"
			,"STATE_TREE_CONTROLLER"
			,"STATE_DISPLAY_STATUS_DETAIL_EXPLANATION" //SubmittedQuoteStateKeys.STATE_DISPLAY_STATUS_DETAIL_EXPLANATION
			,"DISPLAY_EXEC_SUMMARY_PP"//SubmittedQuoteStateKeys.DISPLAY_EXEC_SUMMARY_PP
			,"STATE_ALTER_DISPLAY_FIND_BY_COUNTRY"
			,"STATE_ALTER_DISPLAY_FIND_BY_APPVLATTR"
			,"STATE_SUBMITTED_SQ_VIEW_TXT_HISTORY"
			,StateKeys.STATE_DISPLAY_AJAX_OPER
			,"OPPORTUNITY_NUM_JSON"
			,"GET_RELATED_BIDS" // related bids table display
			,"STATE_REDIRECT_TO_SUBMIT_LPP_FORM" //
			,"STATE_REDIRECT_TO_VIEW_UPDATE_LPP_FORM"
			,"STATE_REDIRECT_TO_ADD_LPP_FORM"
			,"STATE_DISPLAY_PRIOR_SS_PRICE"
			,"STATE_DISPLAY_DRAFT_TOU_LIST"
			,StateKeys.STATE_LINE_ITEM_ADDRESSES_JSON
			,"STATE_DISPLAY_AJAX_OPER"
			,"STATE_GENERAL_ERROR"
            };

}