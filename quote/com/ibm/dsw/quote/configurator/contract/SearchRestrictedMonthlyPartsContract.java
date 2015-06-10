package com.ibm.dsw.quote.configurator.contract;

import java.util.Iterator;

import com.ibm.dsw.quote.configurator.config.ConfiguratorParamKeys;
import com.ibm.dsw.quote.configurator.domain.MonthlySwConfiguratorPart;
import com.ibm.ead4j.jade.session.JadeSession;
import com.ibm.ead4j.jade.util.Parameters;

/**
 * @ClassName: SearchRestrictedMonthlyPartsContract
 * @author Crimson Lin
 * @Description:
 * @date April 07, 2015
 * 
 */

public class SearchRestrictedMonthlyPartsContract extends
		SubmittedMonthlySwConfiguratorContract {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String searchPartList;
	private String existingPartList;
	
	public void load(Parameters parameters, JadeSession session) {
        super.load(parameters, session);
        
        searchPartList=(parameters.getParameterAsString(ConfiguratorParamKeys.neededProcessRestrictedPartList)==null?
        		"":parameters.getParameterAsString(ConfiguratorParamKeys.neededProcessRestrictedPartList)).toUpperCase();
        
        Iterator<MonthlySwConfiguratorPart> ite = configuratorPartsList.iterator();
        String existedTempList="";
        String searchTempList=searchPartList+",";
        
		while(ite.hasNext()) { 
			MonthlySwConfiguratorPart configPart = (MonthlySwConfiguratorPart) ite.next();
			
			//if searched part num is existed in page configuration part list, check its quantity, 
			//if quantity >0 means the part is active, so add this part to existed list and delete the part number from processList
			//if quantity=0 or is null means the part is not configured(inactive), so still keep it in processList.
			if(searchPartList.contains(configPart.getPartNum())){
				if(configPart.getPartQty() !=null && configPart.getPartQty()>0){
					existedTempList="".equals(existedTempList)?configPart.getPartNum():existedTempList+","+configPart.getPartNum();
					searchTempList=searchTempList.replace(configPart.getPartNum()+",", "");
				}	
			}
		}
		setSearchPartList(searchTempList);
		setExistingPartList(existedTempList);
		
    }

	public String getSearchPartList() {
		return searchPartList;
	}

	public void setSearchPartList(String searchPartList) {
		this.searchPartList = searchPartList;
	}

	public String getExistingPartList() {
		return existingPartList;
	}

	public void setExistingPartList(String existingPartList) {
		this.existingPartList = existingPartList;
	}

}
