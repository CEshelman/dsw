/*
 * Created on 2007-2-28
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.ibm.dsw.quote.base.cache;

import com.ibm.ead4j.quartz.common.Cache;
import com.ibm.ead4j.quartz.exception.InvalidGroupException;

/**
 * @author Nathan
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class QuoteCache extends Cache {
    public void loadGroup(String name, Object arg) throws InvalidGroupException{
        super.loadGroup(name, arg);
    }
}
