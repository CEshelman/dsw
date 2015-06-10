package com.ibm.dsw.quote.draftquote.viewbean;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.ibm.dsw.quote.appcache.domain.BillingOption;
import com.ibm.dsw.quote.appcache.domain.BillingOptionFactory;
import com.ibm.dsw.quote.base.util.HtmlUtil;
import com.ibm.dsw.quote.configurator.config.ConfiguratorParamKeys;
import com.ibm.dsw.quote.configurator.domain.ConfiguratorHeader;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteActionKeys;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteParamKeys;
import com.ibm.ead4j.jade.bean.ViewBeanException;
import com.ibm.ead4j.jade.taglib.html.SelectOption;
import com.ibm.ead4j.jade.taglib.html.SelectOptionImpl;
import com.ibm.ead4j.jade.util.Parameters;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;
import com.ibm.ead4j.topaz.exception.TopazException;

public class MigratePartListViewBean extends MigrationReqBaseViewBean {
	private static final long serialVersionUID = 6867850041126256307L;
	protected static final LogContext logContext = LogContextFactory.singleton().getLogContext();
	transient List partList = null;

	private ConfiguratorHeader header;
	public String getCaNum() {
		return caNum;
	}



	public void collectResults(Parameters params) throws ViewBeanException {
		super.collectResults(params);
		partList = (List) params.getParameter(DraftQuoteParamKeys.MIGRATE_PART_LIST);

        header = (ConfiguratorHeader)params.getParameter(ConfiguratorParamKeys.CONFIGURATOR_HEADER);
	}


	public List getPartList() {
		return partList;
	}
	
	public Collection generateBillingOptions(){
		List list = new ArrayList();
		try {
			Map<String, BillingOption> billingOptionMap = BillingOptionFactory.singleton().getBillingOptionMap();
			Iterator<BillingOption> iter = billingOptionMap.values().iterator();
			while ( iter.hasNext() )
			{
				list.add(iter.next());
			}
			Collections.sort(list, new BillingCompare());
			for ( int i = 0; i < list.size(); i++ )
			{
				BillingOption b = (BillingOption)list.get(i);
				if(billingFreq != null){
					billingFreq = billingFreq.trim();
				}
				SelectOption s = new SelectOptionImpl(b.getCodeDesc(), b.getCode(),b.getCode().equals(billingFreq));
				list.set(i, s);
			}
		} catch (TopazException e) {
			logContext.error(this, e);
		}
		return list;
	}	
	
	public Collection getTermList() {
		Collection options = new ArrayList();
		int defaultTerm = this.getCoverageTerm();
		if(defaultTerm == 0 || defaultTerm == -1){
			defaultTerm = 12;
		}
		int totalMonths = 60;

	   for (int i = 1; i <= totalMonths; i++) {
		   if(i == defaultTerm){
			   options.add(new SelectOptionImpl(String.valueOf(i), String.valueOf(i), true));
		   } else {
			   options.add(new SelectOptionImpl(String.valueOf(i), String.valueOf(i), false));
		   }
        }

	   return options;
	}
	
	class BillingCompare implements Comparator
	{

		@Override
		public int compare(Object object1, Object object2) {
			BillingOption obj1 = (BillingOption)object1;
			BillingOption obj2 = (BillingOption)object2;
			return obj1.getMonths() - obj2.getMonths();
		}
		
	}
	
	public String getAddPartURL()
	{
		String actionURL = HtmlUtil.getURLForAction(DraftQuoteActionKeys.ADD_MIGRATE_PART);
		return actionURL;
	}
}
