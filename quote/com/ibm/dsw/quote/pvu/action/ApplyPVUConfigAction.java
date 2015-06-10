package com.ibm.dsw.quote.pvu.action;

import org.apache.commons.lang.StringUtils;

import com.ibm.dsw.quote.base.config.ParamKeys;
import com.ibm.dsw.quote.base.config.StateKeys;
import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.base.util.HtmlUtil;
import com.ibm.dsw.quote.common.domain.SearchResultList;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteActionKeys;
import com.ibm.dsw.quote.pvu.config.VUDBConstants;
import com.ibm.dsw.quote.pvu.config.VUStateKeys;
import com.ibm.dsw.quote.pvu.contract.ApplyPVUConfigContract;
import com.ibm.dsw.quote.pvu.process.VUConfigProcess;
import com.ibm.dsw.quote.pvu.process.VUConfigProcessFactory;
import com.ibm.ead4j.jade.bean.ResultBean;
import com.ibm.ead4j.jade.bean.ResultBeanException;
import com.ibm.ead4j.jade.bean.ResultHandler;
import com.ibm.ead4j.jade.contract.ProcessContract;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;

/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * This <code>ApplyPVUConfigAction.java</code> class.
 * 
 * @author: cgang@cn.ibm.com
 * 
 * Creation date: 2007-3-20
 */
public class ApplyPVUConfigAction extends com.ibm.dsw.quote.base.action.BaseContractActionHandler {

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.dsw.quote.base.action.BaseContractActionHandler#executeBiz(com.ibm.ead4j.jade.contract.ProcessContract,
     *      com.ibm.ead4j.jade.bean.ResultHandler)
     */
    public ResultBean executeBiz(ProcessContract contract, ResultHandler handler) throws QuoteException,
            ResultBeanException {
        LogContext logger = LogContextFactory.singleton().getLogContext();

        ApplyPVUConfigContract pvuContract = (ApplyPVUConfigContract) contract;
        String configNum = pvuContract.getConfigNum();
        String lineItemNum = pvuContract.getLineItemNum();
        String creatorId = pvuContract.getUserId();

        try {
            if (StringUtils.isNotBlank(configNum)) {
                VUConfigProcess vuProcess = VUConfigProcessFactory.sigleton().create();

                /*
                 * Read pvu config by calling EBIZ1.S_VU_CONFIG(CONFIG_NUM,1)
                 * <SP owned by VU Wizard>
                 */
                SearchResultList vuConfigResult = vuProcess.findVUConfigByConfigNum(configNum, VUDBConstants.NO_DSCR_FLAG);
                //Save the config to WEB_QUOTE_LINEITEM_CONFIG by calling
                // EBIZ1.IU_QT_LIN_ITEM_CFG
                logger.debug(this, "Get " + vuConfigResult.getResultList().size() + " VUConfig returned");
                vuProcess.updateQuoteLineItemConfig(vuConfigResult.getResultList(), lineItemNum, creatorId, configNum);

            } //endif
        } catch (QuoteException qe) {
            throw qe;
        }

        handler.addObject(ParamKeys.PARAM_REDIRECT_URL, HtmlUtil
                .getURLForAction(DraftQuoteActionKeys.DISPLAY_PARTS_PRICE_TAB));
        handler.setState(StateKeys.STATE_REDIRECT_ACTION);
        return handler.getResultBean();
    }

    public String getState() {
        return VUStateKeys.STATE_APPLY_PVU_CONFIG;
    }

}
