package com.ibm.dsw.quote.common.domain;

import is.domainx.User;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;


public class UserWrapper implements is.domainx.User{

	/**
	 * Proxy of the {@link is.domainx.User User}
	 */
	private static final long serialVersionUID = 1L;

	private User user;
	
	private String email = "";

	private Hashtable accessLevels = new Hashtable(4);
	// e.g: this.accessLevels.put(QuoteConstants.APP_CODE_SQO, QuoteConstants.ACCESS_LEVEL_SUBMITTER);
	
	public UserWrapper(User user){
		this.user = user;
		if (null != user) {
			this.email = user.getEmail();
		}
	}
	
	@Override
	public String getContractNumber() {
		if (null != user){
			return user.getContractNumber();
		}else{
			return null;
		}
	}

	@Override
	public String getUniqueID() {
		if (null != user){
			return user.getUniqueID();
		}else{
			return null;
		}
	}

	@Override
	public String getCookieDTime() {
		if (null != user){
			return user.getCookieDTime();
		}else{
			return null;
		}
	}

	@Override
	public String getDominoID() {
		if (null != user){
			return user.getDominoID();
		}else{
			return null;
		}
	}

	@Override
	public String getFirstName() {
		if (null != user){
			return user.getFirstName();
		}else{
			return null;
		}
	}

	@Override
	public String getLastName() {
		if (null != user){
			return user.getLastName();
		}else{
			return null;
		}
	}

	@Override
	public String getSAPNumber() {
		if (null != user){
			return user.getSAPNumber();
		}else{
			return null;
		}
	}

	@Override
	public String getWIID() {
		if (null != user){
			return user.getWIID();
		}else{
			return null;
		}
	}

	@Override
	public boolean isOkToEnterPortal(String[] paramArrayOfString) {
		if (null != user){
			return user.isOkToEnterPortal(paramArrayOfString);
		}else{
			return false;
		}
	}

	@Override
	public int getAccessLevel(String paramString) {
		if (null != user) {
			int origAccessLevel = user.getAccessLevel(paramString);
			
			if (origAccessLevel != -1) {
				return origAccessLevel;
			}
		}
		
		Integer accessLevel = (Integer) this.accessLevels.get(paramString);
		if (null != accessLevel) {
			int localAccessLevel = accessLevel.intValue();
			return localAccessLevel;
		}
		return -1;
	}

	@Override
	public int getAccessLevel(Object paramObject) {
		return this.getAccessLevel((String)paramObject);
	}

	@Override
	public String getRole() {
		if (null != user){
			return user.getRole();
		}else{
			return null;
		}
	}

	@Override
	public boolean isEntitledToApplication(String paramString) {
		if (null != user){
			return user.isEntitledToApplication(paramString);
		}else{
			return false;
		}
	}

	@Override
	public String getEmail() {
		return this.email;
	}

	@Override
	public Enumeration getApplicationCodes() {
		if (null != user){
			return user.getApplicationCodes();
		}else{
			return null;
		}
	}

	@Override
	public boolean isPartiallyEntitled() {
		if (null != user){
			return user.isPartiallyEntitled();
		}else{
			return false;
		}
	}

	@Override
	public boolean isFullyEntitled() {
		if (null != user){
			return user.isFullyEntitled();
		}else{
			return false;
		}
	}

	@Override
	public String getCntryCode() {
		if (null != user){
			return user.getCntryCode();
		}else{
			return null;
		}
	}

	@Override
	public String getCurrentPortal() {
		if (null != user){
			return user.getCurrentPortal();
		}else{
			return null;
		}
	}

	@Override
	public String getModDate() {
		if (null != user){
			return user.getModDate();
		}else{
			return null;
		}
	}

	@Override
	public String getStatCode() {
		if (null != user){
			return user.getStatCode();
		}else{
			return null;
		}
	}

	@Override
	public boolean isTelesalesUser() {
		if (null != user){
			return user.isTelesalesUser();
		}else{
			return false;
		}
	}

	@Override
	public boolean hasEntitledPortal(String paramString) {
		if (null != user){
			return user.hasEntitledPortal(paramString);
		}else{
			return false;
		}
	}

	@Override
	public Vector getEntitledPortals() {
		if (null != user){
			return user.getEntitledPortals();
		}else{
			return null;
		}
	}

	@Override
	public Hashtable getUIApplicationAccess() {
		if (null != user){
			return user.getUIApplicationAccess();
		}else{
			return null;
		}
	}

	@Override
	public boolean isReseller() {
		if (null != user){
			return user.isReseller();
		}else{
			return false;
		}
	}

	@Override
	public boolean isCustomer() {
		if (null != user){
			return user.isCustomer();
		}else{
			return false;
		}
	}

	@Override
	public int getUIApplicationAccessLevel(String paramString) {
		if (null != user){
			return user.getUIApplicationAccessLevel(paramString);
		}else{
			return -1;
		}
	}

	@Override
	public boolean isLinkedUser() {
		if (null != user){
			return user.isLinkedUser();
		}else{
			return false;
		}
	}

	@Override
	public String getCCMSNumber() {
		if (null != user){
			return user.getCCMSNumber();
		}else{
			return null;
		}
	}

	@Override
	public boolean isBHDeployed() {
		if (null != user){
			return user.isBHDeployed();
		}else{
			return false;
		}
	}

	@Override
	public boolean hasCCMSSiteNumber() {
		if (null != user){
			return user.hasCCMSSiteNumber();
		}else{
			return false;
		}
	}

	@Override
	public String getEntitledRenewalQuoteNumber() {
		if (null != user){
			return user.getEntitledRenewalQuoteNumber();
		}else{
			return null;
		}
	}

	@Override
	public void setEntitledRenewalQuoteNumber(String paramString) {
		if (null != user){
			user.setEntitledRenewalQuoteNumber(paramString);
		}
	}

	@Override
	public boolean isBasicInfoEquals(User paramUser) {
		if (null != user){
			return user.isBasicInfoEquals(paramUser);
		}else{
			return false;
		}
	}

	@Override
	public boolean isWICallNeeded() {
		if (null != user){
			return user.isWICallNeeded();
		}else{
			return false;
		}
	}

	@Override
	public boolean isRenewalUser() {
		if (null != user){
			return user.isRenewalUser();
		}else{
			return false;
		}
	}

	public Hashtable getAccessLevels() {
		return this.accessLevels;
	}

	public void setEmail(String email) {
		this.email = email;
	}

}
