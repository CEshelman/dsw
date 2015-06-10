package com.ibm.dsw.quote.ps.process.jdbc;

import java.rmi.RemoteException;
import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.ibm.dsw.quote.appcache.domain.WwideProductCodeFactory;
import com.ibm.dsw.quote.appcache.domain.jdbc.WwideProductCodeFactoryOEM_jdbc;
import com.ibm.dsw.quote.base.config.DBConstants;
import com.ibm.dsw.quote.base.config.QuoteConstants;
import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.base.util.LogHelper;
import com.ibm.dsw.quote.base.util.TopazUtil;
import com.ibm.dsw.quote.common.config.CommonDBConstants;
import com.ibm.dsw.quote.common.domain.PartPriceConfigFactory;
import com.ibm.dsw.quote.ps.config.PartSearchParamKeys;
import com.ibm.dsw.quote.ps.domain.PartSearchResult;
import com.ibm.dsw.quote.ps.domain.PartSearchResultFactory;
import com.ibm.dsw.quote.ps.domain.PartSearchService;
import com.ibm.dsw.quote.ps.domain.PartSearchServiceResult;
import com.ibm.dsw.quote.ps.domain.PartSearchServiceResult_Impl;
import com.ibm.dsw.quote.ps.domain.jdbc.PartSearchResult_jdbc;
import com.ibm.dsw.quote.ps.process.PartSearchProcess_Impl;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;
import com.ibm.ead4j.topaz.common.jdbc.QueryContext;
import com.ibm.ead4j.topaz.exception.TopazException;

/**
 * Copyright 2006 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * The <code>oooooo.java</code> class is to oooooo
 * 
 * 
 * @author: chenzhh@cn.ibm.com
 * 
 * Creation date: Jan 26, 2007
 */
public class PartSearchProcess_jdbc extends PartSearchProcess_Impl {

    /**
     * inputs : piAudience, piLineOfBusCode, piCntryCode, piCurrncyCode, piPartNumList [32762]
     */
	public PartSearchResult searchPartsByNumber(String partNumbers, String creatorId, String lob, String acqrtnCode, String countryCode, String audience)
			throws RemoteException, QuoteException, TopazException {
		PartSearchResult psResult = null;
		try {
			//begin the transaction
			this.beginTransaction();

			psResult = PartSearchResultFactory.singleton().findByNum(partNumbers, creatorId, lob, acqrtnCode, countryCode, audience);
			psResult.setPortfolioDescriptions();
			
			//commit the transaction
			this.commitTransaction();
		} catch (TopazException tce) {
			throw new QuoteException(tce);
		} finally {
			rollbackTransaction();
		}

		if (isPartCountBeyondExpandLimit(psResult.getPartCount())) {
		    psResult.setExpandLevelCollapsed();
		} else {
		    psResult.setExpandLevelExpanded();
		}
		
		return psResult;
	}
	
    /**
     * inputs : piAudience, piLineOfBusCode, piCntryCode, piCurrncyCode, piPartNumList [32762]
     */
	public PartSearchResult searchPartsByDescription(String description, String creatorId, String lob)
			throws RemoteException, QuoteException, TopazException {
		PartSearchResult psResult = null;
		try {
			//begin the transaction
			this.beginTransaction();

			psResult = PartSearchResultFactory.singleton().findByDesc(description, creatorId, lob);
			psResult.setPortfolioDescriptions();

			//commit the transaction
			this.commitTransaction();
		} catch (TopazException tce) {
			throw new QuoteException(tce);
		} finally {
			rollbackTransaction();
		}

		if (isPartCountBeyondExpandLimit(psResult.getPartCount())) {
		    psResult.setExpandLevelCollapsed();
		} else {
		    psResult.setExpandLevelExpanded();
		}
		
		return psResult;
	}
	
	public PartSearchResult searchRelatedParts(String partNumber, String creatorId, String lob)
	throws RemoteException, QuoteException, TopazException {
		PartSearchResult psResult = null;
		try {
			//begin the transaction
			this.beginTransaction();
		
			psResult = PartSearchResultFactory.singleton().findRelatedParts(partNumber, creatorId, lob);
			psResult.setPortfolioDescriptions();
		
			//commit the transaction
			this.commitTransaction();
		} catch (TopazException tce) {
			throw new QuoteException(tce);
		} finally {
			rollbackTransaction();
		}
		
		if (isPartCountBeyondExpandLimit(psResult.getPartCount())) {
		    psResult.setExpandLevelCollapsed();
		} else {
		    psResult.setExpandLevelExpanded();
		}

		return psResult;
	}

	public PartSearchResult searchRelatedPartsLic(String partNumber, String creatorId, String lob)
	throws RemoteException, QuoteException, TopazException {
		PartSearchResult psResult = null;
		try {
			//begin the transaction
			this.beginTransaction();
		
			psResult = PartSearchResultFactory.singleton().findRelatedPartsLic(partNumber, creatorId, lob);
			psResult.setPortfolioDescriptions();
		
			//commit the transaction
			this.commitTransaction();
		} catch (TopazException tce) {
			throw new QuoteException(tce);
		} finally {
			rollbackTransaction();
		}
		
		if (isPartCountBeyondExpandLimit(psResult.getPartCount())) {
		    psResult.setExpandLevelCollapsed();
		} else {
		    psResult.setExpandLevelExpanded();
		}

		return psResult;
	}
	
	public PartSearchResult searchReplacementParts(String partNumber, String creatorId, String lob, String acqrtnCode, String countryCode, String audience)
	throws RemoteException, QuoteException, TopazException {
		PartSearchResult psResult = null;
		try {
			//begin the transaction
			this.beginTransaction();
		
			psResult = PartSearchResultFactory.singleton().findReplacementParts(partNumber, creatorId, lob, acqrtnCode, countryCode, audience);
			psResult.setPortfolioDescriptions();
		
			//commit the transaction
			this.commitTransaction();
		} catch (TopazException tce) {
			throw new QuoteException(tce);
		} finally {
			rollbackTransaction();
		}
		
		if (isPartCountBeyondExpandLimit(psResult.getPartCount())) {
		    psResult.setExpandLevelCollapsed();
		} else {
		    psResult.setExpandLevelExpanded();
		}

		return psResult;
	}

    /**
     * inputs : piAudience, piLineOfBusCode, piCntryCode, piCurrncyCode, piPartNumList [32762]
     */
	public PartSearchResult browseParts(String prodCodes, String brand, String lob, String creatorId)
			throws RemoteException, QuoteException, TopazException {
		PartSearchResult psResult = null;
		List prodCodesList = getProdCodesListFromString(prodCodes);
		
		if(isBrowseByProdCodes(lob)) {
			if(prodCodesList == null) {
			    //if(isProdCodesBeyondLimit(brand, lob)) {
				    psResult = createResultFromProdCodes(brand, lob);
				    psResult.setExpandLevelCollapsed();
//			    } else {
//				    psResult = browseByProdCodes(prodCodesList, creatorId, lob);
//				    //search done on less than 11 prod codes
//				    psResult.setExpandLevelExpanded();
//			    }
			} else {
			    psResult = browseByProdCodes(prodCodesList, creatorId, lob);
			    //search was done on 1 prod code
			    psResult.setExpandLevelExpanded();
			}
		} else {
		    psResult = browseByLob(creatorId, lob);
		    //will be less than 10 prod codes by definition
		    psResult.setExpandLevelExpanded();
		}

		//don't set this to fully expanded here - this is handled by the logic above
		//this is here to just override the fully expanded setting if there are too many parts
		if (isPartCountBeyondExpandLimit(psResult.getPartCount())) {
		    psResult.setExpandLevelCollapsed();
		}
		
		return psResult;
	}
	
	protected PartSearchResult browseByProdCodes(List prodCodes, String creatorId, String lob) 
	throws RemoteException, QuoteException, TopazException {
	    PartSearchResult psResult = null;

		try {
			this.beginTransaction();

			psResult = PartSearchResultFactory.singleton().findByProdCodes(prodCodes, creatorId, lob);
			psResult.setPortfolioDescriptions();

			//commit the transaction
			this.commitTransaction();
		} catch (TopazException tce) {
			throw new TopazException(tce);
		} finally {
			rollbackTransaction();
		}
	
	    return psResult;
	}

	protected PartSearchResult browseByLob(String creatorId, String lob) 
	throws RemoteException, QuoteException, TopazException {
	    PartSearchResult psResult = null;

		try {
			this.beginTransaction();

			psResult = PartSearchResultFactory.singleton().findByLob(creatorId, lob);
			psResult.setPortfolioDescriptions();

			//commit the transaction
			this.commitTransaction();
		} catch (TopazException tce) {
			throw new TopazException(tce);
		} finally {
			rollbackTransaction();
		}
		
	    return psResult;
	}
	
	protected PartSearchResult createResultFromProdCodes(String brand, String lob) throws QuoteException, TopazException {
	    PartSearchResult psResult = new PartSearchResult_jdbc();
		try {
			this.beginTransaction();
			List result = null;
			if(QuoteConstants.LOB_OEM.equalsIgnoreCase(lob.trim())){
			    result = WwideProductCodeFactoryOEM_jdbc.getInstance().findAllProductCodes(brand);
			}else{
			    result = WwideProductCodeFactory.singleton().findAllProductCodes(brand);
			}
			psResult.setPartsFromProdCodeList(brand, result);
			//commit the transaction
			this.commitTransaction();
		} catch (TopazException tce) {
			throw new TopazException(tce);
		} finally {
			rollbackTransaction();
		}
	    return psResult;
	}

    /**
     * inputs : piAudience, piLineOfBusCode, piCntryCode, piCurrncyCode, piPartNumList [32762]
     */
	public int saveSelectedParts(List selectedParts, String creatorId, String searchString, String dataRetrievalType, String chrgAgrmtNum, String configrtnActionCode, String orgConfigId)
			throws RemoteException, QuoteException, TopazException {
		PartSearchResult psResult = null;
		try {
			//begin the transaction
			this.beginTransaction();

			psResult = PartSearchResultFactory.singleton().saveSelectedParts(selectedParts, creatorId, searchString, dataRetrievalType, chrgAgrmtNum, configrtnActionCode, orgConfigId);

			//commit the transaction
			this.commitTransaction();
			
			return psResult.getExceedCode();
			
		}catch (TopazException tce) {
		    LogContextFactory.singleton().getLogContext().error(this, tce);
			throw new TopazException(tce);
		} finally {
			rollbackTransaction();
		}
		
		
	}
	
	private boolean isProdCodesBeyondLimit(String brand, String lob) throws QuoteException, TopazException {
	    boolean result = false;
	    int prodCodeNum = 0;
		try {
			this.beginTransaction();
			if(QuoteConstants.LOB_OEM.equalsIgnoreCase(lob.trim())){
			    prodCodeNum = WwideProductCodeFactoryOEM_jdbc.getInstance().getProductCount(brand);
			}else{
			    prodCodeNum = WwideProductCodeFactory.singleton().getProductCount(brand);
			}
			//commit the transaction
			this.commitTransaction();
		} catch (TopazException tce) {
			throw new TopazException(tce);
		} finally {
			rollbackTransaction();
		}
	    if(prodCodeNum > PS_MAX_PRODUCTS_EXPANDED) {
	        result = true;
	    }
	    return result;
	}
	
    public List getProdCodesListFromString(String prodCodes) {
        List prodCodesList = new ArrayList();
        if(prodCodes==null || prodCodes.equals("")) {
            prodCodesList = null;
        } else {
	        String[] tempString = prodCodes.split(",");
	        for(int i = 0;i < tempString.length; i++) {
	            prodCodesList.add(i, tempString[i]);
	        }
        }
        
        return prodCodesList;
    }  
	
    public int addReplForObsParts(String webQuoteNumber,int seqNumber,List parts) throws QuoteException{
        LogContext logger = LogContextFactory.singleton().getLogContext();
        HashMap parms = new HashMap();
        String sqlQuery = "";
        parms.put("piWebQuoteNumber", webQuoteNumber);
        parms.put("piSeqNumber", new Integer(seqNumber));
        parms.put("piPartNumList", getSelectedPartsString(parts));
        parms.put("piPartNumLimit", new Integer(PartPriceConfigFactory.singleton().getElaLimits()));

        int retCode = -1;
        try {
            this.beginTransaction();
            QueryContext queryCtx = QueryContext.getInstance();
            String searchType = PartSearchParamKeys.ADD_REPL_FOR_OBS_PARTS;
            sqlQuery = queryCtx.getCompletedQuery(searchType, null);

            logger.debug(this, LogHelper.logSPCall(sqlQuery, parms));
            CallableStatement ps = TopazUtil.getConnection().prepareCall(sqlQuery);
            queryCtx.completeStatement(ps, searchType, parms);
            ps.execute();
            retCode = ps.getInt(1);
            
            if (retCode != 0 && (retCode != DBConstants.DB2_SP_EXCEED_MAX) && (retCode != DBConstants.DB2_SP_ALREADY_IS_MAX)) {
            	if(CommonDBConstants.DB2_SP_RETURN_PART_INPUT_INVALID == retCode){
            		logger.info(this, "retStatus = " + retCode+ ", Invalid part number:"+getSelectedPartsString(parts));
            	}else{
            		throw new QuoteException("Execute failed on " + sqlQuery + " with retCode " + retCode);
            	}
            }
            
            this.commitTransaction();
        } catch (Exception e) {
            logger.error("Failed replace the expired parts to db2", e);
            throw new QuoteException("Execute failed on " + sqlQuery + " with retCode " + retCode);
        }finally {
        	this.rollbackTransaction();
        }
        
        return retCode;
    }
    
    public String getSelectedPartsString(List list) {
        StringBuffer sb = new StringBuffer();
        boolean flag = false;
        Iterator i = list.iterator();
        while(i.hasNext()) {
            if(flag) {
                sb.append(',' + ((String)i.next()).trim());
            } else {
                sb.append(((String)i.next()).trim());
                flag = true;
            }
        }
        
        return sb.toString();
    }
    
    public PartSearchServiceResult getPartSearchServiceResults(String userId) throws QuoteException{
    	  LogContext logger = LogContextFactory.singleton().getLogContext();
          HashMap parms = new HashMap();
          String sqlQuery = "";
          parms.put("piUserId", userId);
          PartSearchServiceResult partSearchServiceResult = new PartSearchServiceResult_Impl();
          
          int retCode = -1;
          try {
              this.beginTransaction();
              QueryContext queryCtx = QueryContext.getInstance();
              String searchType = PartSearchParamKeys.BROWSE_SERVICES;
              sqlQuery = queryCtx.getCompletedQuery(searchType, null);

              logger.debug(this, LogHelper.logSPCall(sqlQuery, parms));
              CallableStatement ps = TopazUtil.getConnection().prepareCall(sqlQuery);
              queryCtx.completeStatement(ps, searchType, parms);
              ps.execute();
              retCode = ps.getInt(1);
              
              if (retCode != 0 ) {
                  throw new QuoteException("Execute failed on " + sqlQuery + " with retCode " + retCode);
              }

              partSearchServiceResult.setHasConfigrtn(ps.getInt(3) == 1);
              
              do{
            	  ResultSet rs = ps.getResultSet();
            	  String firstColumnName = rs.getMetaData().getColumnName(1);
            	  if("PROD_BRAND_CODE".equalsIgnoreCase(firstColumnName)){
            		  List prodBrandsList =new ArrayList();
                      List prodBrandsCodeList =new ArrayList();
                      List serviceList = new ArrayList();
                      PartSearchService service = null;
                      while (rs.next()) {
                    	  service = new PartSearchService();
                      	  service.setProdBrandCode(StringUtils.trimToEmpty(rs.getString("PROD_BRAND_CODE")));
                          service.setProdBrandCodeDscr(StringUtils.trimToEmpty(rs.getString("PROD_BRAND_CODE_DSCR")));
                          service.setProdGrpCode(StringUtils.trimToEmpty(rs.getString("PROD_GRP_CODE")));
                          service.setProdGrpCodeDscr(StringUtils.trimToEmpty(rs.getString("PROD_GRP_CODE_DSCR")));
                          service.setProdSetCode(StringUtils.trimToEmpty(rs.getString("PROD_SET_CODE")));
                          service.setProdSetCodeDscr(StringUtils.trimToEmpty(rs.getString("PROD_SET_CODE_DSCR")));
                          service.setProdId(StringUtils.trimToEmpty(rs.getString("IBM_PROD_ID")));
                          service.setProdIdDscr(StringUtils.trimToEmpty(rs.getString("IBM_PROD_ID_DSCR")));
                          serviceList.add(service);
                           
                          if(!prodBrandsCodeList.contains(service.getProdBrandCode())){
                        	  prodBrandsCodeList.add(service.getProdBrandCode());
                          	  prodBrandsList.add(new String[]{service.getProdBrandCode(),service.getProdBrandCodeDscr()});
                           }
                        }
                      rs.close();
                      partSearchServiceResult.setServices(serviceList);
                      partSearchServiceResult.setProdBrandsList(prodBrandsList);
            	  }else if("SAP_SALES_ORD_NUM".equalsIgnoreCase(firstColumnName)){
            		  List agreements = new ArrayList();
                      Object[] params = null;
                      while (rs.next()) {
						params = new Object[6];
						params[0] = StringUtils.trim(rs.getString("SAP_SALES_ORD_NUM"));
						params[1] = StringUtils.trim(rs.getString("IBM_PROD_ID"));
						params[2] = StringUtils.trim(rs.getString("IBM_PROD_ID_DSCR"));
						params[3] = StringUtils.trim(rs.getString("CONFIGRTN_ID"));
						params[4] = rs.getDate("END_DATE");
						params[5] = StringUtils.trim(rs.getString("WITH_SUBSCRIPTION"));
						agreements.add(params);
                      }
                      rs.close();
                      partSearchServiceResult.setAgreements(agreements);
            	  }else if("IBM_PROD_ID".equalsIgnoreCase(firstColumnName)){
            		  List configuredPids = new ArrayList();
                      while (rs.next()) {
                    	  configuredPids.add(StringUtils.trim(rs.getString("IBM_PROD_ID")));
                      }
                      rs.close();
                      partSearchServiceResult.setConfiguredPids(configuredPids);
                      
            	  }
              }while (ps.getMoreResults()); 
              this.commitTransaction();
              ps.close();
              return partSearchServiceResult;
          } catch (Exception e) {
              logger.error("Failed replace the expired parts to db2", e);
              throw new QuoteException("Execute failed on " + sqlQuery + " with retCode " + retCode);
          }finally {
          	this.rollbackTransaction();
          }    
      }

}
