package com.ibm.dsw.automation.util;

import com.ibm.dsw.automation.common.FunctionIdProvider;

import junit.framework.TestCase;


public class EncryptManagerTest extends TestCase {


	public void testMain_01() {
		/*String userId = "zhoujun@cn.ibm.com";
		String password = "secret,aha";
		String[] args = {userId, password};
		EncryptManager.main(args);*/
		/*String res = EncryptManager.encrypt("id10spring");
		System.out.println(res);
		System.out.println(DecryptManager.decrypt(res));*/
		
		System.out.println(FunctionIdProvider.getPWDForFuncId("dswweb10@us.ibm.com"));
	}


}
