package com.ibm.dsw.automation.util;

import java.io.File;
import java.io.FilenameFilter;

import org.apache.commons.lang3.StringUtils;

public class FileNameFilter0 implements FilenameFilter{

	private String nameFilter;
	
	public FileNameFilter0(String nameFilter){
		this.nameFilter = nameFilter;
	}
	
	@Override
	public boolean accept(File dir, String name) {
		if (StringUtils.isBlank(name)) {
			return false;
		}
		if (StringUtils.isBlank(this.nameFilter)) {
			return false;
		}
		if (!nameFilter.contains(".properties")) {
			nameFilter += ".properties";
		}
		if (name.equalsIgnoreCase(nameFilter)) {
			return true;
		}else{
			return false;
		}
	}

}
