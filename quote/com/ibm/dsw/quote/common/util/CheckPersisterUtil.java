package com.ibm.dsw.quote.common.util;

import com.ibm.ead4j.topaz.exception.TopazException;
import com.ibm.ead4j.topaz.persistence.jdbc.Persister;

public class CheckPersisterUtil {
	public static void checkPersisterDirty(Object obj1, Object obj2, Persister p) throws TopazException{
		if(!EqualsCheckingUtils.isEqualed(obj1, obj2)){
			p.setDirty();
		}
	}
}
