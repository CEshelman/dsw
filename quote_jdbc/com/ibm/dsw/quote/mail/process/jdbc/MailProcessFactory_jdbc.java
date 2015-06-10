package com.ibm.dsw.quote.mail.process.jdbc;

import com.ibm.dsw.quote.mail.process.MailProcess;
import com.ibm.dsw.quote.mail.process.MailProcessFactory;

public class MailProcessFactory_jdbc extends MailProcessFactory {

	public MailProcess create() {
		return new MailProcess_jdbc();
	}

}
