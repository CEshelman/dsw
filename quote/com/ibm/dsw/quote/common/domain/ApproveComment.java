package com.ibm.dsw.quote.common.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ApproveComment implements Serializable{
	protected int level;
	protected String type;
	protected List<String> cmts = new ArrayList<String>();
	public int getLevel() {
		return level;
	}
	public void setLevel(int level) {
		this.level = level;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public List<String> getCmts() {
		return cmts;
	}
}
