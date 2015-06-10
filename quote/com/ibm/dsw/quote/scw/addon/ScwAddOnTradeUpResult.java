package com.ibm.dsw.quote.scw.addon;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ScwAddOnTradeUpResult {

	/**
	 * @uml.annotations for <code>scwAddonTradeUpErrorCodeList</code>
	 *                  collection_type=
	 *                  "com.ibm.dsw.quote.scw.addon.ScwAddonTradeUpErrorCode"
	 */
	private List<ScwAddonTradeUpErrorCode> scwAddonTradeUpErrorCodeList = new ArrayList<ScwAddonTradeUpErrorCode>();
	public boolean isSuccessful() {
		return (scwAddonTradeUpErrorCodeList == null || scwAddonTradeUpErrorCodeList.size() == 0);
	}


	public void addNewErrorCode(ScwAddonTradeUpErrorCode errorCode) {
		scwAddonTradeUpErrorCodeList.add(errorCode);
	}

	public void addNewErrorCodeList(List<ScwAddonTradeUpErrorCode> errorCodeList) {
		if (errorCodeList != null) {
			scwAddonTradeUpErrorCodeList.addAll(errorCodeList);
		}
	}

	public List<ScwAddonTradeUpErrorCode> getScwAddonTradeUpErrorCodeList() {
		return scwAddonTradeUpErrorCodeList;
	}

	@Override
	public String toString() {
		StringBuilder str = new StringBuilder("");
		for (Iterator iterator = scwAddonTradeUpErrorCodeList.iterator(); iterator.hasNext();) {
			ScwAddonTradeUpErrorCode errorCodeBean = (ScwAddonTradeUpErrorCode) iterator.next();
			str.append(errorCodeBean.toString()).append("\n");
		}
		return str.toString();
	}


}
