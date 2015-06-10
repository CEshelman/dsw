package com.ibm.dsw.quote.dlgtn.process;

import java.util.HashMap;

import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.bluepages.BluePageUser;
import com.ibm.dsw.quote.bluepages.BluePagesLookup;
import com.ibm.ead4j.topaz.process.TopazTransactionalProcess;

/**
 * Copyright 2006 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * The <code>QuoteDlgtnProcess_Impl</code> class is a empty implementation for
 * interface QuoteDlgtnProcess
 * 
 * @author <a href="liuxinlx@cn.ibm.com">Andy Liu </a> <br/>
 * 
 * Creation date: Mar 13, 2007
 */
public abstract class QuoteDlgtnProcess_Impl extends TopazTransactionalProcess implements QuoteDlgtnProcess {

    public void fillBluePageInfo(String userId, HashMap parms) throws QuoteException {

        BluePageUser user = BluePagesLookup.getBluePagesInfo(userId);
        if (null == user) {
            throw new QuoteException("Can't find Blue page info for :" + userId);
        }

        parms.put("piCntryCode", user.getCountryCode());
        parms.put("piNotesId", user.getNotesId());
        parms.put("piFullName", user.getFullName());
        parms.put("piFirstName", user.getFirstName());
        parms.put("piLastName ", user.getLastName());
        parms.put("piIntlPhoneNumFull", user.getPhoneNumber());
        parms.put("piIntlFaxNumFull", user.getFaxNumber());

    }
}
