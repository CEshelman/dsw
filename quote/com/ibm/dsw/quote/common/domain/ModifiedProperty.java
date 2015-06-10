package com.ibm.dsw.quote.common.domain;

import java.sql.Date;

public class ModifiedProperty {
	protected boolean crad;
	protected boolean mtm;
	protected boolean deployId;
	private String oldDeployId;
	private boolean changedDeployId;
	private Date oldCrad;
	private boolean changedLineitemCrad;
	private String oldMachModelValue;
	private boolean changedMachModelValue;
	private String oldMachSerialNumberValue;
	private boolean changedSerialNumberValue;
	private String oldMachTypeValue;
	private boolean changedMachTypeValue;
	public String getOldMachModelValue() {
		return oldMachModelValue;
	}
	public void setOldMachModelValue(String oldMachModelValue) {
		this.oldMachModelValue = oldMachModelValue;
		this.changedMachModelValue = true;
	}
	public String getOldMachSerialNumberValue() {
		return oldMachSerialNumberValue;
	}
	public void setOldMachSerialNumberValue(String oldMachSerialNumberValue) {
		this.oldMachSerialNumberValue = oldMachSerialNumberValue;
		this.changedSerialNumberValue = true;
	}
	public String getOldMachTypeValue() {
		return oldMachTypeValue;
	}
	public void setOldMachTypeValue(String oldMachTypeValue) {
		this.oldMachTypeValue = oldMachTypeValue;
		this.changedMachTypeValue = true;
	}
	public String getOldDeployId() {
		return oldDeployId;
	}
	public void setOldDeployId(String oldDeployId) {
		this.oldDeployId = oldDeployId;
		this.changedDeployId = true;
	}
	public boolean isChangedDeployId() {
		return changedDeployId;
	}
	public boolean isChangedLineitemCrad() {
		return changedLineitemCrad;
	}

	public boolean isChangedMachModelValue() {
		return changedMachModelValue;
	}

	public boolean isChangedSerialNumberValue() {
		return changedSerialNumberValue;
	}

	public boolean isChangedMachTypeValue() {
		return changedMachTypeValue;
	}

	public Date getOldCrad() {
		return oldCrad;
	}
	public void setOldCrad(Date oldCrad) {
		this.oldCrad = oldCrad;
		this.changedLineitemCrad = true;
	}
	public boolean isCrad() {
		return crad;
	}
	public void setCrad(boolean crad) {
		this.crad = crad;
	}
	public boolean isMtm() {
		return mtm;
	}
	public void setMtm(boolean mtm) {
		this.mtm = mtm;
	}
	public boolean isDeployId() {
		return deployId;
	}
	public void setDeployId(boolean deployId) {
		this.deployId = deployId;
	}
}
