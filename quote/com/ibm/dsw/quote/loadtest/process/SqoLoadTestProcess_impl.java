package com.ibm.dsw.quote.loadtest.process;

import java.util.ArrayList;
import java.util.List;

import DswSalesLibrary.GetPricesInput;
import DswSalesLibrary.GetPricesOutput;
import DswSalesLibrary.HeaderIn;
import DswSalesLibrary.ItemIn;
import DswSalesLibrary.PricingService;

import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.base.util.LogThrowableUtil;
import com.ibm.dsw.quote.common.config.CommonServiceConstants;
import com.ibm.dsw.quote.loadtest.dao.SqoLoadTestDaoFactory;
import com.ibm.dsw.wpi.java.util.ServiceLocator;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;
import com.ibm.ead4j.topaz.exception.TopazException;
import com.ibm.ead4j.topaz.process.TopazTransactionalProcess;

/**
 * @author julia.liu
 * 
 */
public abstract class SqoLoadTestProcess_impl extends TopazTransactionalProcess
		implements SqoLoadTestProcess {

	protected LogContext logContext = LogContextFactory.singleton()
			.getLogContext();

	ServiceLocator serviceLocator;

	public GetPricesOutput callWebService() throws TopazException,
			QuoteException {
		GetPricesInput input = mockPricesInput();
		GetPricesOutput output = null;
		try {
			output = callWebService(input);
		} catch (Exception e) {
			e.printStackTrace();
			output = null; 
		}
		return output;
	}

	protected GetPricesOutput callWebService(GetPricesInput input)
			throws Exception {
		try {
			if (serviceLocator == null) {
				serviceLocator = new ServiceLocator();
			}
			PricingService pricingService = (PricingService) serviceLocator
					.getServicePort(CommonServiceConstants.PRICING_SERVICE_BINDING, PricingService.class);

			GetPricesOutput output = pricingService.execute(input);

			return output;
		} catch (Exception e) {
			logContext.error(this,
					"Call Pricing Service error:" + LogThrowableUtil.getStackTraceContent(e));
			throw new Exception(
					"An unexpected exception occurred while updating quote with pricing info. Cause: "
							+ e);
		}
	}

	public StringBuffer loadtest() throws TopazException, QuoteException {
		StringBuffer sb = new StringBuffer();
		try {
			beginTransaction();
			boolean psStatus = SqoLoadTestDaoFactory
					.singleton().create().getWebApplCodesByColName("LAST_RULE_UPDATE");
			if (psStatus) {
				sb.append("Call Dao successful! ");
			}
			GetPricesOutput output = callWebService();
			if (output != null) {
				sb.append("Call webService successful!");
			}
			commitTransaction();
		} catch (TopazException e) {
			rollbackTransaction();
			logContext.error(SqoLoadTestProcess_impl.class,
					LogThrowableUtil.getStackTraceContent(e));
			return null;
		} finally {
			rollbackTransaction();
		}

		return sb;
	}

	private ItemIn[] mockInputItems() {
		List<ItemIn> itemsInList = new ArrayList<ItemIn>();
		ItemIn itemIn = new ItemIn();
		itemIn.setItmNum(1);
		itemIn.setPartNum("E09NVLL");
		itemIn.setQty(2);
		itemIn.setStartDt("20141101");
		itemIn.setEndDt("20150731");
		itemIn.setRefDocNum("0026531691");
		itemIn.setRefDocItemNum(70);
		itemsInList.add(itemIn);

		itemIn.setItmNum(2);
		itemIn.setPartNum("E025QLL");
		itemIn.setQty(2000);
		itemIn.setStartDt("20140801");
		itemIn.setEndDt("20150731");
		itemIn.setRefDocNum("0026531691");
		itemIn.setRefDocItemNum(40);
		itemsInList.add(itemIn);

		ItemIn[] itemsIn = new ItemIn[itemsInList.size()];
		itemsInList.toArray(itemsIn);
		return itemsIn;
	}

	private GetPricesInput mockPricesInput() {
		GetPricesInput input = new GetPricesInput();
		HeaderIn headerIn = new HeaderIn();

		headerIn.setCountry("PAK");
		headerIn.setLOB("PA");
		headerIn.setCurrency("USD");
		headerIn.setSalesOrg("0483");
		headerIn.setContract("0000175045");
		headerIn.setPrcDate("20140919");
		headerIn.setSoldTo("0003150342");
		headerIn.setPayer("0007967937");
		headerIn.setReseller("0007903256");
		headerIn.setDistChnl("J");
		headerIn.setDocCat("Q");
		headerIn.setNoGsaFlag("X");
		headerIn.setPaymentTermDays(30);
		headerIn.setProposalValidityDays(34);

		input.setHeaderIn(headerIn);

		ItemIn[] itemsIn = mockInputItems();
		input.setItemsIn(itemsIn);

		return input;
	}

	void setServiceLocator(ServiceLocator serviceLocator) {
		this.serviceLocator = serviceLocator;
	}
}
