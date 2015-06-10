package com.ibm.dsw.quote.ps.domain;

import java.rmi.RemoteException;

import com.ibm.dsw.quote.base.config.QuoteConstants;
import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.ps.config.PartSearchParamKeys;
import com.ibm.dsw.quote.ps.process.PartSearchProcess;
import com.ibm.dsw.quote.ps.process.PartSearchProcessFactory;
import com.ibm.dsw.quote.ps.process.PartSearchProcess_Impl;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;
import com.ibm.ead4j.topaz.exception.TopazException;

public class PartSearchDataFactory {

    //tree level types integers initially correspond to the number of levels, but that is not necessary into the future if more display level types are added
    public static final String PART_TREE_LEVEL_TYPE = "PartTree";	//not used, here just for completeness
    public static final String PROD_PART_TREE_LEVEL_TYPE = "ProdPartTree";
    public static final String PROD_TYPE_PART_TREE_LEVEL_TYPE = "ProdTypePartTree";
    public static final String BRAND_PROD_TYPE_PART_TREE_LEVEL_TYPE = "BrandProdTypePartTree";
    


    public PartSearchResult getSearchData(
            String searchData, 
            String creatorId,
            String searchType, 
            String lob,
            String acqrtnCode,
            String countryCode,
            String audience) {
        PartSearchResult psResult = getData(
                						searchData, 
                			            creatorId,
                						"", 
                						searchType,
                						lob,
                						acqrtnCode,
                						countryCode,
                						audience);    
        return psResult;
    }
    
    public PartSearchResult getBrowseData(
            String prodCodes, 
            String creatorId,
            String brand, 
            String lob) {
        PartSearchResult psResult = getData(
                						prodCodes, 
                			            creatorId,
                						brand, 
                						PartSearchParamKeys.SEARCH_PARTS_BROWSE,
                						lob);
        
        return psResult;
    }
    
    /**
     * @param prodCodes
     * @param creatorId
     * @param brand
     * @param search_parts_browse
     * @param lob
     * @return
     */
    private PartSearchResult getData(String data, String creatorId, String brand, String searchType, String lob) {
        return getData(
				data, 
	            creatorId,
				brand, 
				searchType,
				lob,
				"",
				"",
				"");
    }

    private PartSearchResult getData(
            String data,
            String creatorId,
            String brand, 
            String searchType,
            String lob,
            String acqrtnCode,
            String countryCode,
            String audience) {
        PartSearchProcess process;
        PartSearchResult psResult = null;
        LogContext logger = LogContextFactory.singleton().getLogContext();
        
        try {
	        process = PartSearchProcessFactory.singleton().create();
            if(searchType.equals(PartSearchParamKeys.SEARCH_PARTS_BY_NUMBERS)) {
		        psResult = process.searchPartsByNumber(data, creatorId, lob, acqrtnCode, countryCode, audience);
            } else if(searchType.equals(PartSearchParamKeys.SEARCH_PARTS_BY_DESCRIPTION)) {
		        psResult = process.searchPartsByDescription(data, creatorId, lob);
            } else if(searchType.equals(PartSearchParamKeys.SEARCH_PARTS_BROWSE)) {
		        psResult = process.browseParts(data, brand, lob, creatorId );
            } else if(searchType.equals(PartSearchParamKeys.GET_RELATED_PARTS)) {
		        psResult = process.searchRelatedParts(data, creatorId, lob);
            } else if(searchType.equals(PartSearchParamKeys.GET_RELATED_PARTS_LIC)) {
		        psResult = process.searchRelatedPartsLic(data, creatorId, lob);
            } else if(searchType.equals(PartSearchParamKeys.GET_REPLACEMENT_PARTS)) {
		        psResult = process.searchReplacementParts(data, creatorId, lob, acqrtnCode, countryCode, audience);
	        }
        } catch (TopazException te) {
            logger.error(this, te);
        } catch (QuoteException qe) {
            logger.error(this, qe);
        } catch (RemoteException re) {
            logger.error(this, re);
        }
			    
	    return psResult;
    }

    //these should probably be moved to some utility class, but here for now
    public static String getTreeLevelType(String lob, String searchType) {
        String levelType = "";
        //add OEM support
        if(PartSearchProcess_Impl.isPA(lob) || QuoteConstants.LOB_OEM.equalsIgnoreCase(lob.trim())) {
            if(searchType.equals(PartSearchParamKeys.SEARCH_PARTS_BY_NUMBERS) 
                    || searchType.equals(PartSearchParamKeys.SEARCH_PARTS_BY_DESCRIPTION)
                    || searchType.equals(PartSearchParamKeys.GET_RELATED_PARTS)
                    || searchType.equals(PartSearchParamKeys.GET_RELATED_PARTS_LIC)
                    || searchType.equals(PartSearchParamKeys.GET_REPLACEMENT_PARTS)
                ) {
                levelType = BRAND_PROD_TYPE_PART_TREE_LEVEL_TYPE;
            } else if(searchType.equals(PartSearchParamKeys.SEARCH_PARTS_BROWSE)) {
                levelType = PROD_TYPE_PART_TREE_LEVEL_TYPE;
            }
        } else {
            levelType = PROD_PART_TREE_LEVEL_TYPE;
        }
        return levelType;
    } 
}
