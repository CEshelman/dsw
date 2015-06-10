package com.ibm.dsw.quote.newquote.spreadsheet;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import com.ibm.dsw.quote.common.domain.MonthlySwLineItem;
import com.ibm.dsw.quote.common.domain.QuoteLineItem;

/**
 * Adapt monthly licensing item to saas line item.
 * @author lirui
 *
 */
public class SpreadSheetMonthlyItemToSaasProxy implements InvocationHandler {
	private static final long serialVersionUID = 1L;
	private MonthlySwLineItem target;

	public static QuoteLineItem createProx(MonthlySwLineItem target){
		SpreadSheetMonthlyItemToSaasProxy handler = new SpreadSheetMonthlyItemToSaasProxy(target);
		QuoteLineItem proxy = (QuoteLineItem)
		Proxy.newProxyInstance(target.getClass().getClassLoader(), 
				target.getClass().getInterfaces(), handler);
		return proxy;
	}

	SpreadSheetMonthlyItemToSaasProxy(MonthlySwLineItem t){
		this.target = t;
	}

	public Object invoke(Object proxy, Method method, Object[] args)
	throws Throwable {
		Object result = null;
		//treat monthly software as saas when export
		if("isSaasPart".equals(method.getName())){
			result = true;
		}
		//treat monthly software as saasSubscrptnPart when export
		else if("isSaasSubscrptnPart".equals(method.getName())){
			result = target.isMonthlySoftwarePart() && target.isMonthlySwSubscrptnPart();
		}
		else if("isSaasDaily".equals(method.getName())) 
			result = target.isSaasDaily() || target.isMonthlySwDailyPart();
		else if("isSaasOnDemand".equals(method.getName())) 
			result = target.isSaasOnDemand()|| target.isMonthlySwOnDemandPart();
		else if("isSaasSubscrptnOvragePart".equals(method.getName())) 
			result = target.isSaasSubscrptnOvragePart() || target.isMonthlySwSubscrptnOvragePart();
		else{
			result = method.invoke(target, args);
		}
		System.out.println("Invoked:"+method.getName()+"="+result);
		return result;
	}
}
