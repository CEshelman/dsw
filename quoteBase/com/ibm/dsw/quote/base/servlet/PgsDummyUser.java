/**
 * Created on July 6, 2006
 */
package com.ibm.dsw.quote.base.servlet;


/**
 * A fugazi user to map from the jump page instead of web-auth
 * 
 * @author Jin Wang
 * @author Matt Givney
 */
public class PgsDummyUser extends DummyUser{
	
	public String getCurrentPortal(){
    	return "PAR";
    }
    
}
