package com.ibm.dsw.quote.ps.domain.jdbc;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;

import com.ibm.dsw.quote.base.config.DBConstants;
import com.ibm.dsw.quote.base.config.QuoteConstants;
import com.ibm.dsw.quote.base.util.LogHelper;
import com.ibm.dsw.quote.common.config.CommonDBConstants;
import com.ibm.dsw.quote.common.domain.PartPriceConfigFactory;
import com.ibm.dsw.quote.common.helper.JdbcUtil;
import com.ibm.dsw.quote.ps.config.PartSearchParamKeys;
import com.ibm.dsw.quote.ps.domain.PartSearchPart;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;
import com.ibm.ead4j.topaz.common.jdbc.QueryContext;
import com.ibm.ead4j.topaz.exception.TopazException;
import com.ibm.ead4j.topaz.persistence.jdbc.Persister;

/**
 * Copyright 2006 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * The <code>CustomerListPersister.java</code> class does not do anything.
 * CustomerList object is created and populated in the factory class
 * 
 * 
 * @author: chenzhh@cn.ibm.com
 * 
 * Creation date: Jan 26, 2007
 */
public class PartSearchResultPersister extends Persister {
    
    public static final String SW_PROD_BRAND_CODE  = "SW_PROD_BRAND_CODE";
    public static final String SW_PROD_BRAND_CODE_DSCR = "SW_PROD_BRAND_CODE_DSCR";
    public static final String WWIDE_PROD_CODE = "WWIDE_PROD_CODE";
    public static final String WWIDE_PROD_CODE_DSCR = "WWIDE_PROD_CODE_DSCR";
    public static final String REV_STRM_OR_PROD_PACK = "REV_STRM_OR_PROD_PACK";
    public static final String REV_STRM_OR_PROD_PACK_DSCR = "REV_STRM_OR_PROD_PACK_DSCR";
    public static final String PART_NUM = "PART_NUM";
    public static final String PART_DSCR_LONG = "PART_DSCR_LONG";
    public static final String SVP_LEVEL_A = "SVP_LEVEL_A";
    public static final String SVP_LEVEL_B = "SVP_LEVEL_B";
    public static final String SVP_LEVEL_D = "SVP_LEVEL_D";
    public static final String SVP_LEVEL_E = "SVP_LEVEL_E";
    public static final String SVP_LEVEL_F = "SVP_LEVEL_F";
    public static final String SVP_LEVEL_G = "SVP_LEVEL_G";
    public static final String SVP_LEVEL_H = "SVP_LEVEL_H";
    public static final String SVP_LEVEL_I = "SVP_LEVEL_I";
    public static final String SVP_LEVEL_J = "SVP_LEVEL_J";
    public static final String SVP_LEVEL_GV = "SVP_LEVEL_GV";
    public static final String SVP_LEVEL_ED = "SVP_LEVEL_ED";
    public static final String PRICE_END_DATE = "PRICE_END_DATE";
    public static final String PROD_EOL_DATE = "PROD_EOL_DATE";
    public static final String PART_RSTRCT_FLAG = "PART_RSTRCT_FLAG";
    public static final String PART_OBSLTE_FLAG = "PART_OBSLTE_FLAG";
    public static final String IBM_PROG_CODE = "IBM_PROG_CODE";
    public static final String IBM_PROG_CODE_DSCR = "IBM_PROG_CODE_DSCR";
    public static final String SAP_MATL_GRP_5_COND_CODE = "SAP_MATL_GRP_5_COND_CODE";
    public static final String SAP_SALES_STAT_CODE = "SAP_SALES_STAT_CODE";
    public static final String EXPORT_REGLTN_CODE = "ENCRYPTN_IND";
    
    
    
    
    public static final String APPLIANCE_FLAG = "APPLIANCE_FLAG";
    public static final String APPLNC_QTY_RESTRCTN_FLAG = "APPLNC_QTY_RESTRCTN_FLAG";
    
    public static final String SRP_PRICE = "SRP_PRICE";
    public static final String PRICE_LEVEL_A = "PRICE_LEVEL_A";

    private PartSearchResult_jdbc partList_jdbc;

    protected LogContext logger = LogContextFactory.singleton().getLogContext();

    /**
     * @param PartListPersister
     */
    public PartSearchResultPersister(PartSearchResult_jdbc pj) {
        super();
        partList_jdbc = pj;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.ead4j.topaz.persistence.jdbc.AbstractPersister#update(java.sql.Connection)
     */
    public void update(Connection arg0) throws TopazException {
        // do nothing

    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.ead4j.topaz.persistence.jdbc.AbstractPersister#delete(java.sql.Connection)
     */
    public void delete(Connection arg0) throws TopazException {
        // do nothing

    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.ead4j.topaz.persistence.jdbc.AbstractPersister#hydrate(java.sql.Connection)
     */
    public void hydrate(Connection con) throws TopazException {
        int genStatus = 0;
        HashMap parms = new HashMap();

        if (partList_jdbc.getSearchType().equals(PartSearchParamKeys.SEARCH_PARTS_BY_NUMBERS)) {
            //parms.put("poGenStatus", "");
            parms.put("piCreatorId", partList_jdbc.getCreatorId()==null?"":partList_jdbc.getCreatorId());
            
            parms.put("piPartNumList", partList_jdbc.getSearchPartNumbers());
            parms.put("piLineOfBusCode",partList_jdbc.getLob()==null?"":partList_jdbc.getLob());
            parms.put("piAcqrtnCode",partList_jdbc.getAcqrtnCode()==null?"":partList_jdbc.getAcqrtnCode());
            parms.put("piCountryCode",partList_jdbc.getCountryCode()==null?"":partList_jdbc.getCountryCode());
            parms.put("piAudience",partList_jdbc.getAudience()==null?"":partList_jdbc.getAudience());
            // partList_jdbc.getCreatorId() + ", search nums: " +
            // partList_jdbc.getSearchPartNumbers());
        } else if (partList_jdbc.getSearchType().equals(PartSearchParamKeys.SEARCH_PARTS_BY_DESCRIPTION)) {
            //parms.put("poGenStatus", "");
            parms.put("piCreatorId", partList_jdbc.getCreatorId());
            //System.err.println("Description is:"+partList_jdbc.getSearchDescription());
            StringBuffer sb = new StringBuffer(partList_jdbc.getSearchDescription());
            /*
            int start_index = 0;
            int index = -1;
            while((index = sb.indexOf("\"", start_index)) >= 0){
                sb.replace(index, index+1, "\\\"");
                start_index = index + 2;
            }
            index = -1;
            start_index = 0;
            while((index = sb.indexOf("'", start_index)) >= 0){
                sb.replace(index, index+1, "''");
                start_index = index + 2;
            }
            */
            //System.err.println("Description is:"+sb.toString());
            parms.put("piPartDscr", sb.toString());
        } else if (partList_jdbc.getSearchType().equals((PartSearchParamKeys.SEARCH_PARTS_BY_PROD_CODE))) {
            //parms.put("poGenStatus", "");
            parms.put("piCreatorId", partList_jdbc.getCreatorId());
            Iterator iterProdCodes = partList_jdbc.getSearchProdCodes().iterator();
            int count = 0;
            String parmValue;
            while (count < 10) {
                if (iterProdCodes.hasNext()) {
                    parmValue = (String) iterProdCodes.next();
                } else {
                    parmValue = "";
                }
                parms.put("piWwpc" + ++count, parmValue);
            }
        } else if (partList_jdbc.getSearchType().equals((PartSearchParamKeys.SEARCH_PARTS_BY_LOB))) {
            //parms.put("poGenStatus", "");
            parms.put("piCreatorId", partList_jdbc.getCreatorId());
        } else if (partList_jdbc.getSearchType().equals((PartSearchParamKeys.GET_RELATED_PARTS))) {
            //parms.put("poGenStatus", "");
            parms.put("piCreatorId", partList_jdbc.getCreatorId());
            //will only be 1 part number, the parent part string
            parms.put("piParentPart", partList_jdbc.getSearchPartNumbers());
            parms.put("piCallType", "R");
        } else if (partList_jdbc.getSearchType().equals((PartSearchParamKeys.GET_RELATED_PARTS_LIC))) {
            //parms.put("poGenStatus", "");
            parms.put("piCreatorId", partList_jdbc.getCreatorId());
            //will only be 1 part number, the parent part string
            parms.put("piParentPart", partList_jdbc.getSearchPartNumbers());
            parms.put("piCallType", "L");
        } else if (partList_jdbc.getSearchType().equals((PartSearchParamKeys.GET_REPLACEMENT_PARTS))) {
            //parms.put("poGenStatus", "");
            parms.put("piCreatorId", partList_jdbc.getCreatorId()==null?"":partList_jdbc.getCreatorId());
            //will only be 1 part number, the parent part string
            parms.put("piParentPart", partList_jdbc.getSearchPartNumbers());
            parms.put("piLineOfBusCode",partList_jdbc.getLob()==null?"":partList_jdbc.getLob());
            parms.put("piAcqrtnCode",partList_jdbc.getAcqrtnCode()==null?"":partList_jdbc.getAcqrtnCode());
            parms.put("piCountryCode",partList_jdbc.getCountryCode()==null?"":partList_jdbc.getCountryCode());
            parms.put("piAudience",partList_jdbc.getAudience()==null?"":partList_jdbc.getAudience());
        }
        int rowCount = 0;
        ResultSet rs = null;
        try {
            QueryContext queryCtx = QueryContext.getInstance();
            String sqlQuery = null;
            if (partList_jdbc.getSearchType().equals((PartSearchParamKeys.GET_RELATED_PARTS_LIC)))
            	sqlQuery = queryCtx.getCompletedQuery(PartSearchParamKeys.GET_RELATED_PARTS, null);
            else
            	sqlQuery = queryCtx.getCompletedQuery(partList_jdbc.getSearchType(), null);
            logger.debug(this, LogHelper.logSPCall(sqlQuery, parms));
            CallableStatement ps = con.prepareCall(sqlQuery);
            if (partList_jdbc.getSearchType().equals((PartSearchParamKeys.GET_RELATED_PARTS_LIC)))            
            	queryCtx.completeStatement(ps, PartSearchParamKeys.GET_RELATED_PARTS, parms);
            else
            	queryCtx.completeStatement(ps, partList_jdbc.getSearchType(), parms);
            ps.execute();
            genStatus = ps.getInt(1);
            rs = ps.getResultSet();
            if (genStatus != 0) {
                throw new TopazException("Execute failed on " + sqlQuery + " ; result code = " + genStatus);
            }
            if (rs != null) {
                while (rs.next()) {
                    PartSearchPart part = processResultSet(rs, partList_jdbc.getSearchType(), partList_jdbc.getLob());
                    boolean isPartValid = !part.isPartObsoleteFlag();
                    boolean isPartPriceValid = true;
                    if(part.getPriceLvlA() == -1){// has not been named a price yet
                        isPartPriceValid = false;
                    }
                    if(isPartValid && !isPartPriceValid){
                        continue;
                    }
                    rowCount++;
                    partList_jdbc.addPart(part);
                }
                rs.close();
            }
        } catch (Exception e) {
            logger.error("Failed getting search parts from db2", e);
            throw new TopazException(e);
        }finally{
        	try {
				if (null != rs && !rs.isClosed())
				{
					rs.close();
				}
			} catch (SQLException e) {
				logger.error("Failed to close the resultset!", e);
			}
        }
    }

    private PartSearchPart processResultSet(ResultSet rs, String searchType, String lob) throws TopazException,
            SQLException {
        PartSearchPart part = new PartSearchPart();
        //add OEM support
        if (QuoteConstants.LOB_PA.equals(lob.trim()) || QuoteConstants.LOB_PAE.equals(lob.trim()) || QuoteConstants.LOB_PAUN.equals(lob.trim())
                || QuoteConstants.LOB_OEM.equalsIgnoreCase(lob.trim())) {
            String type = "";
            if(PartSearchParamKeys.SEARCH_PARTS_BY_DESCRIPTION.equalsIgnoreCase(searchType.trim())){
                type = rs.getString(1);
            }
            
            if(PartSearchParamKeys.SEARCH_PARTS_BY_DESCRIPTION.equalsIgnoreCase(searchType.trim()) && "FullCursor".equalsIgnoreCase(type)){
                part.setBrandID(rs.getString(SW_PROD_BRAND_CODE).trim());
                part.setBrandDescription(JdbcUtil.getTrimString(rs.getString(SW_PROD_BRAND_CODE_DSCR)));
                part.setProductID(JdbcUtil.getTrimString(rs.getString(WWIDE_PROD_CODE)));
                part.setProductDescription(JdbcUtil.getTrimString(rs.getString(WWIDE_PROD_CODE_DSCR)));
                part.setProdTypeID(JdbcUtil.getTrimString(rs.getString(REV_STRM_OR_PROD_PACK)));
                part.setProdTypeDescription(JdbcUtil.getTrimString(rs.getString(REV_STRM_OR_PROD_PACK_DSCR)));
                part.setPartID(JdbcUtil.getTrimString(rs.getString(PART_NUM)));
                part.setPartDescription(JdbcUtil.getTrimString(rs.getString(PART_DSCR_LONG)));
                part.setPriceLvlA(rs.getBigDecimal(SVP_LEVEL_A)==null?-1:rs.getBigDecimal(SVP_LEVEL_A).doubleValue());
                part.setPriceLvlBL(rs.getBigDecimal(SVP_LEVEL_B)==null?-1:rs.getBigDecimal(SVP_LEVEL_B).doubleValue());
                part.setPriceLvlD(rs.getBigDecimal(SVP_LEVEL_D)==null?-1:rs.getBigDecimal(SVP_LEVEL_D).doubleValue());
                part.setPriceLvlE(rs.getBigDecimal(SVP_LEVEL_E)==null?-1:rs.getBigDecimal(SVP_LEVEL_E).doubleValue());
                part.setPriceLvlF(rs.getBigDecimal(SVP_LEVEL_F)==null?-1:rs.getBigDecimal(SVP_LEVEL_F).doubleValue());
                part.setPriceLvlG(rs.getBigDecimal(SVP_LEVEL_G)==null?-1:rs.getBigDecimal(SVP_LEVEL_G).doubleValue());
                part.setPriceLvlH(rs.getBigDecimal(SVP_LEVEL_H)==null?-1:rs.getBigDecimal(SVP_LEVEL_H).doubleValue());
                part.setPriceLvlI(rs.getBigDecimal(SVP_LEVEL_I)==null?-1:rs.getBigDecimal(SVP_LEVEL_I).doubleValue());
                part.setPriceLvlJ(rs.getBigDecimal(SVP_LEVEL_J)==null?-1:rs.getBigDecimal(SVP_LEVEL_J).doubleValue());
                part.setPriceLvlGV(rs.getBigDecimal(SVP_LEVEL_GV)==null?-1:rs.getBigDecimal(SVP_LEVEL_GV).doubleValue());
                part.setPriceLvlED(rs.getBigDecimal(SVP_LEVEL_ED)==null?-1:rs.getBigDecimal(SVP_LEVEL_ED).doubleValue());
                part.setPriceEndSoonFlag(rs.getDate(PRICE_END_DATE));
                part.setPartEolDate(rs.getDate(PROD_EOL_DATE));
                part.setPartRestrictedFlag(rs.getBoolean(PART_RSTRCT_FLAG));
                part.setPartObsoleteFlag(rs.getBoolean(PART_OBSLTE_FLAG)); 
                part.setIbmProgCode(JdbcUtil.getTrimString(rs.getString(IBM_PROG_CODE)));
                part.setIbmProgDscr(JdbcUtil.getTrimString(rs.getString(IBM_PROG_CODE_DSCR)));
                part.setPortfolioCode(JdbcUtil.getTrimString(rs.getString(SAP_MATL_GRP_5_COND_CODE)));
                part.setExportRestrctnFlag(JdbcUtil.getTrimString(rs.getString(EXPORT_REGLTN_CODE)).equalsIgnoreCase("1"));
                part.setSapSalesStatCode(rs.getString(SAP_SALES_STAT_CODE));
               
            } else{
                part.setBrandID(rs.getString(SW_PROD_BRAND_CODE).trim());
                part.setBrandDescription(JdbcUtil.getTrimString(rs.getString(SW_PROD_BRAND_CODE_DSCR)));
                part.setProductID(JdbcUtil.getTrimString(rs.getString(WWIDE_PROD_CODE)));
                part.setProductDescription(JdbcUtil.getTrimString(rs.getString(WWIDE_PROD_CODE_DSCR)));
                part.setProdTypeID(JdbcUtil.getTrimString(rs.getString(REV_STRM_OR_PROD_PACK)));
                part.setProdTypeDescription(JdbcUtil.getTrimString(rs.getString(REV_STRM_OR_PROD_PACK_DSCR)));
                part.setPartID(JdbcUtil.getTrimString(rs.getString(PART_NUM)));
                part.setPartDescription(JdbcUtil.getTrimString(rs.getString(PART_DSCR_LONG)));
                part.setPriceLvlA(rs.getBigDecimal(SVP_LEVEL_A)==null?-1:rs.getBigDecimal(SVP_LEVEL_A).doubleValue());                
                part.setPriceLvlBL(rs.getBigDecimal(SVP_LEVEL_B)==null?-1:rs.getBigDecimal(SVP_LEVEL_B).doubleValue());
                part.setPriceLvlD(rs.getBigDecimal(SVP_LEVEL_D)==null?-1:rs.getBigDecimal(SVP_LEVEL_D).doubleValue());
                part.setPriceLvlE(rs.getBigDecimal(SVP_LEVEL_E)==null?-1:rs.getBigDecimal(SVP_LEVEL_E).doubleValue());
                part.setPriceLvlF(rs.getBigDecimal(SVP_LEVEL_F)==null?-1:rs.getBigDecimal(SVP_LEVEL_F).doubleValue());
                part.setPriceLvlG(rs.getBigDecimal(SVP_LEVEL_G)==null?-1:rs.getBigDecimal(SVP_LEVEL_G).doubleValue());
                part.setPriceLvlH(rs.getBigDecimal(SVP_LEVEL_H)==null?-1:rs.getBigDecimal(SVP_LEVEL_H).doubleValue());
                part.setPriceLvlI(rs.getBigDecimal(SVP_LEVEL_I)==null?-1:rs.getBigDecimal(SVP_LEVEL_I).doubleValue());
                part.setPriceLvlJ(rs.getBigDecimal(SVP_LEVEL_J)==null?-1:rs.getBigDecimal(SVP_LEVEL_J).doubleValue());
                part.setPriceLvlGV(rs.getBigDecimal(SVP_LEVEL_GV)==null?-1:rs.getBigDecimal(SVP_LEVEL_GV).doubleValue());
                part.setPriceLvlED(rs.getBigDecimal(SVP_LEVEL_ED)==null?-1:rs.getBigDecimal(SVP_LEVEL_ED).doubleValue());
                part.setPriceEndSoonFlag(rs.getDate(PRICE_END_DATE));
                part.setPartEolDate(rs.getDate(PROD_EOL_DATE));
                part.setPartRestrictedFlag(rs.getBoolean(PART_RSTRCT_FLAG));
                part.setPartObsoleteFlag(rs.getBoolean(PART_OBSLTE_FLAG));
                part.setIbmProgCode(JdbcUtil.getTrimString(rs.getString(IBM_PROG_CODE)));
                part.setIbmProgDscr(JdbcUtil.getTrimString(rs.getString(IBM_PROG_CODE_DSCR)));
                part.setPortfolioCode(JdbcUtil.getTrimString(rs.getString(SAP_MATL_GRP_5_COND_CODE)));
                if (partList_jdbc.getSearchType().equals(PartSearchParamKeys.SEARCH_PARTS_BY_NUMBERS)
                 || partList_jdbc.getSearchType().equals(PartSearchParamKeys.SEARCH_PARTS_BY_PROD_CODE)) {
                    part.setSapSalesStatCode(rs.getString(SAP_SALES_STAT_CODE));
                }
                part.setExportRestrctnFlag(JdbcUtil.getTrimString(rs.getString(EXPORT_REGLTN_CODE)).equalsIgnoreCase("1"));
                
                for(int i= 1 ; i <= rs.getMetaData().getColumnCount();i++){
                	
                	String columnName = rs.getMetaData().getColumnName(i);
                	
                	if(columnName.equals(APPLIANCE_FLAG) && rs.getString(APPLIANCE_FLAG) != null)
                        	part.setApplianceFlag(rs.getString(APPLIANCE_FLAG).equals("1") ? true : false);
                	
                	if(columnName.equals(APPLNC_QTY_RESTRCTN_FLAG) && rs.getString(APPLNC_QTY_RESTRCTN_FLAG) != null)
                			part.setApplianceQtyRestrctnFlag(rs.getString(APPLNC_QTY_RESTRCTN_FLAG).equals("1") ? true : false);
                }
                	
                
            }
            
        } else if (lob.trim().equals(QuoteConstants.LOB_PPSS)) {
            part.setProductID(JdbcUtil.getTrimString(rs.getString(WWIDE_PROD_CODE)));
            part.setProductDescription(JdbcUtil.getTrimString(rs.getString(WWIDE_PROD_CODE_DSCR)));
            part.setPartID(JdbcUtil.getTrimString(rs.getString(PART_NUM)));
            part.setPartDescription(JdbcUtil.getTrimString(rs.getString(PART_DSCR_LONG)));
            part.setPriceLvlA(rs.getBigDecimal(SRP_PRICE)==null?-1:rs.getBigDecimal(SRP_PRICE).doubleValue());
            part.setPriceEndSoonFlag(rs.getDate(PRICE_END_DATE));
            part.setPartEolDate(rs.getDate(PROD_EOL_DATE));
            part.setPartRestrictedFlag(rs.getBoolean(PART_RSTRCT_FLAG));
            part.setPortfolioCode(JdbcUtil.getTrimString(rs.getString(SAP_MATL_GRP_5_COND_CODE)));
            part.setExportRestrctnFlag(JdbcUtil.getTrimString(rs.getString(EXPORT_REGLTN_CODE)).equalsIgnoreCase("1"));
            if (partList_jdbc.getSearchType().equals(PartSearchParamKeys.SEARCH_PARTS_BY_NUMBERS)
             || partList_jdbc.getSearchType().equals(PartSearchParamKeys.SEARCH_PARTS_BY_PROD_CODE)) {
                part.setSapSalesStatCode(rs.getString(SAP_SALES_STAT_CODE));
            }            
        } 
        else if (lob.trim().equals(QuoteConstants.LOB_FCT)) {
            part.setProductID(JdbcUtil.getTrimString(rs.getString(WWIDE_PROD_CODE)));
            part.setProductDescription(JdbcUtil.getTrimString(rs.getString(WWIDE_PROD_CODE_DSCR)));
            part.setPartID(JdbcUtil.getTrimString(rs.getString(PART_NUM)));
            part.setPartDescription(JdbcUtil.getTrimString(rs.getString(PART_DSCR_LONG)));
            part.setPriceLvlA(rs.getBigDecimal(PRICE_LEVEL_A)==null?-1:rs.getBigDecimal(PRICE_LEVEL_A).doubleValue());
            part.setPriceEndSoonFlag(rs.getDate(PRICE_END_DATE));
            part.setPartEolDate(rs.getDate(PROD_EOL_DATE));
            part.setPartRestrictedFlag(rs.getBoolean(PART_RSTRCT_FLAG));
            part.setPortfolioCode(JdbcUtil.getTrimString(rs.getString(SAP_MATL_GRP_5_COND_CODE)));
            //Re-use sproc EBIZ1.S_QT_PART_BY_ID for upload use case, get sw_brand_code returned
            if (partList_jdbc.getSearchType().equals(PartSearchParamKeys.SEARCH_PARTS_BY_NUMBERS)
             || partList_jdbc.getSearchType().equals(PartSearchParamKeys.SEARCH_PARTS_BY_PROD_CODE)) {
                part.setBrandID(rs.getString(SW_PROD_BRAND_CODE).trim());
                part.setPartObsoleteFlag(rs.getBoolean(PART_OBSLTE_FLAG));
                part.setSapSalesStatCode(rs.getString(SAP_SALES_STAT_CODE));
            }
            part.setExportRestrctnFlag(JdbcUtil.getTrimString(rs.getString(EXPORT_REGLTN_CODE)).equalsIgnoreCase("1"));
        }

        return part;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.ead4j.topaz.persistence.jdbc.AbstractPersister#insert(java.sql.Connection)
     */
    public void insert(Connection con) throws TopazException {
        HashMap parms = new HashMap();
        String sqlQuery = "";
        parms.put("poGenStatus", "");
        parms.put("piCreatorID", partList_jdbc.getCreatorId());
        parms.put("piPartNumList", partList_jdbc.getSelectedPartsString());
        parms.put("piPartLimit", String.valueOf(PartPriceConfigFactory.singleton().getElaLimits()));
        parms.put("piSearchString", partList_jdbc.getSearchString());
        parms.put("piSearchType", getSearchType(partList_jdbc.getDataRetrievalType()));
        parms.put("piChrgAgrmtNum", partList_jdbc.getChrgAgrmtNum());
        parms.put("piConfigrtnActionCode", partList_jdbc.getConfigrtnActionCode());
        parms.put("piOrgConfigId", partList_jdbc.getOrgConfigId());
        //System.out.println("Setting piCreatorID to " +
        // partList_jdbc.getCreatorId() + " and piPartNumList to " +
        // partList_jdbc.getSelectedPartNumbers());

        int retCode = -1;
        try {
            QueryContext queryCtx = QueryContext.getInstance();
            sqlQuery = queryCtx.getCompletedQuery(partList_jdbc.getSearchType(), null);
            logger.debug(this, LogHelper.logSPCall(sqlQuery, parms));
            CallableStatement ps = con.prepareCall(sqlQuery);
            queryCtx.completeStatement(ps, partList_jdbc.getSearchType(), parms);
            ps.execute();
            retCode = ps.getInt(1);
        
        } catch (Exception e) {
            logger.error("Failed saving the saved parts to db2", e);
            throw new TopazException("Execute failed on " + sqlQuery + " with retCode " + retCode);
        }
        
        if ((retCode == DBConstants.DB2_SP_ALREADY_IS_MAX) || (retCode == DBConstants.DB2_SP_EXCEED_MAX)){
            //throw new SPException("Execute failed on " + sqlQuery + " with retCode " + retCode, retCode);
            logger.debug(this, "retCode==="+retCode );
            this.partList_jdbc.setExceedCode(retCode);
        }else if (retCode != 0){
        	if(CommonDBConstants.DB2_SP_RETURN_PART_INPUT_INVALID == retCode){
        		logger.info(this, "retStatus = " + retCode+ ", Invalid part number:"+partList_jdbc.getSelectedPartsString());
        	}else{
        		throw new TopazException("Execute failed on " + sqlQuery + " with retCode " + retCode);
        	}
        }
    }
    
    private Integer getSearchType(String dataRetrievalType){
        int intSearchType = -1;
        if(PartSearchParamKeys.SEARCH_PARTS_BY_NUMBERS.equals(dataRetrievalType)){
            intSearchType = 1;
        }else if(PartSearchParamKeys.SEARCH_PARTS_BY_DESCRIPTION.equals(dataRetrievalType)){
            intSearchType = 2;
        }else if(PartSearchParamKeys.SEARCH_PARTS_BROWSE.equals(dataRetrievalType)){
            intSearchType = 3;
        }else if(PartSearchParamKeys.GET_RELATED_PARTS.equals(dataRetrievalType)){
            intSearchType = 4;
        }
        return new Integer(intSearchType);
    }
}
