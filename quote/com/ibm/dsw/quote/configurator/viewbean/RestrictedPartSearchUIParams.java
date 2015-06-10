package com.ibm.dsw.quote.configurator.viewbean;
import com.ibm.dsw.quote.configurator.domain.ConfiguratorPart;
import java.util.List;
import java.util.ArrayList;

public class RestrictedPartSearchUIParams {
	public static final String SEPERATOR_COMMA = ",";
	public static final String SEPERATOR_SEMICOLON = ":";
	public static final String SEPERATOR_CONJUNCTION = "-";

	private List<ConfiguratorPart> restrictedMainPartsList = new ArrayList<ConfiguratorPart>();
	private List<ConfiguratorPart> restrictedMainPartsAlreadyShowList= new ArrayList<ConfiguratorPart>();
	public List<ConfiguratorPart> getRestrictedMainPartsList() {
		return restrictedMainPartsList;
	}
	public void setRestrictedMainPartsList(
			List<ConfiguratorPart> restrictedMainPartsList) {
		this.restrictedMainPartsList = restrictedMainPartsList;
	}
	public List<ConfiguratorPart> getRestrictedMainPartsAlreadyShowList() {
		return restrictedMainPartsAlreadyShowList;
	}
	public void setRestrictedMainPartsAlreadyShowList(
			List<ConfiguratorPart> restrictedMainPartsAlreadyShowList) {
		this.restrictedMainPartsAlreadyShowList = restrictedMainPartsAlreadyShowList;
	}
	
	public void addRestrictedMainPart(ConfiguratorPart cp){
		if(restrictedMainPartsList == null){
			restrictedMainPartsList = new ArrayList<ConfiguratorPart>();
		}
		restrictedMainPartsList.add(cp);
	}
	
	public void addRestrictedMainPartAlreadyShow(ConfiguratorPart cp){
		if(restrictedMainPartsAlreadyShowList == null){
			restrictedMainPartsAlreadyShowList = new ArrayList<ConfiguratorPart>();
		}
		restrictedMainPartsAlreadyShowList.add(cp);
	}
}
