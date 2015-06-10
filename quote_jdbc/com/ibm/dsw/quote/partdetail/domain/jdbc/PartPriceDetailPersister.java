/*
 * Created on 2007-4-5
 *
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.ibm.dsw.quote.partdetail.domain.jdbc;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.ibm.dsw.quote.base.util.LogHelper;
import com.ibm.dsw.quote.base.util.TopazUtil;
import com.ibm.dsw.quote.common.config.CommonDBConstants;
import com.ibm.dsw.quote.common.config.PartPriceConstants;
import com.ibm.dsw.quote.partdetail.domain.PartPriceDetail;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;
import com.ibm.ead4j.topaz.common.jdbc.QueryContext;
import com.ibm.ead4j.topaz.exception.TopazException;
import com.ibm.ead4j.topaz.persistence.jdbc.Persister;

/**
 * @author Administrator
 *
 * Preferences - Java - Code Style - Code Templates
 */
public class PartPriceDetailPersister extends Persister {

    private PartPriceDetail partPriceDetail;

    public PartPriceDetailPersister(PartPriceDetail partPriceDetail) {
        super();
        this.partPriceDetail = partPriceDetail;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.ibm.ead4j.topaz.persistence.jdbc.AbstractPersister#update(java.sql.Connection)
     */
    public void update(Connection connection) throws TopazException {
    }

    /*
     * (non-Javadoc)
     *
     * @see com.ibm.ead4j.topaz.persistence.jdbc.AbstractPersister#delete(java.sql.Connection)
     */
    public void delete(Connection connection) throws TopazException {
    }

    /*
     * (non-Javadoc)
     *
     * @see com.ibm.ead4j.topaz.persistence.jdbc.AbstractPersister#hydrate(java.sql.Connection)
     */
    public void hydrate(Connection connection) throws TopazException {
        PartPriceDetail_jdbc detail = (PartPriceDetail_jdbc)partPriceDetail;
        if(detail.isLoadCoPrerequsite()){
            loadCoPrerequisite();
        }else{
            loadWithoutCoPrerequisite();
        }
    }

    private void loadWithoutCoPrerequisite() throws TopazException {
        LogContext logContext = LogContextFactory.singleton().getLogContext();
        if (partPriceDetail == null) {
            return;
        }
        HashMap params = new HashMap();
        params.put("piPartNumber", partPriceDetail.getPartNumber());
        params.put("piWebQNumber", partPriceDetail.getWebQNumber());
        params.put("piSapDistribtnChnlCode", partPriceDetail.getPriceType());
        ResultSet chargeUnitRs = null;
        ResultSet platformRs = null;
        ResultSet pidRs = null;
        ResultSet languageRs = null;
        ResultSet partGroupRs = null;
        ResultSet mediaRs = null;
        ResultSet priceDetailRs = null;
        ResultSet dsPriceRs = null;
        try {
            QueryContext queryCtx = QueryContext.getInstance();
            String sqlQuery = queryCtx.getCompletedQuery(CommonDBConstants.DB2_S_QT_PART_PRC_ALL, null);
            logContext.debug(this, LogHelper.logSPCall(sqlQuery, params));
            CallableStatement ps = TopazUtil.getConnection().prepareCall(sqlQuery);
            queryCtx.completeStatement(ps, CommonDBConstants.DB2_S_QT_PART_PRC_ALL, params);

            boolean retCode = ps.execute();
            if (!retCode) {
                throw new TopazException("SQL statement execution returns: " + retCode);
            }
            int poGenStatus = ps.getInt(1);
            if (poGenStatus != 0) {
                throw new TopazException("SP call returns error code: " + poGenStatus);
            }
            //deal with outputs.
            partPriceDetail.setPartDescription(ps.getString(4));
            partPriceDetail.setBrandName(ps.getString(5));
            partPriceDetail.setProductDistributionChannel(ps.getString(6));
            partPriceDetail.setProductGroup(ps.getString(7));
            partPriceDetail.setProductTradeName(ps.getString(8));
            partPriceDetail.setProductName(ps.getString(9));
            partPriceDetail.setEncryptionLevelCode(ps.getString(10));
            partPriceDetail.setPartVersion(ps.getString(11));
            partPriceDetail.setPartType(ps.getString(12));
            partPriceDetail.setCountry(ps.getString(13));
            partPriceDetail.setCurrency(ps.getString(14));
            partPriceDetail.setPriceEffectiveDateFrom(ps.getDate(15));
            partPriceDetail.setPriceEffectiveDateTo(ps.getDate(16));
            partPriceDetail.setPoints(ps.getBigDecimal(17)==null?0:ps.getBigDecimal(17).doubleValue());
            //skip the poSapMatl5Code ps.getString(18)
            partPriceDetail.setProdSubGroupDesc(ps.getString(19));
            partPriceDetail.setIsPartCtrlld(ps.getInt(20) == 1);
            partPriceDetail.setCtrlldProgCode(StringUtils.trim(ps.getString(21)));
            partPriceDetail.setCtrlldProgDesc(StringUtils.trim(ps.getString(22)));

            partPriceDetail.setCountryCode(StringUtils.trim(ps.getString(23)));
            partPriceDetail.setCurrencyCode(StringUtils.trim(ps.getString(24)));
            partPriceDetail.setSubmitted(ps.getInt(25) == 1);
            partPriceDetail.setRenewal(ps.getInt(26) == 1);
            partPriceDetail.setRevnStrmCodeDscr(StringUtils.trimToEmpty(ps.getString(27)));

            partPriceDetail.setPartCntrlDistDesc(StringUtils.trimToEmpty(ps.getString(28)));
            partPriceDetail.setGbtBrandCodeDscr(StringUtils.trimToEmpty(ps.getString(29)));

            partPriceDetail.setPricngTierMdl(StringUtils.trimToEmpty(ps.getString(30)));

            partPriceDetail.setSaasPartTypeCodeDscr(StringUtils.trimToEmpty(ps.getString(31))); //-- SaaS part type

            partPriceDetail.setPublshdPriceDurtnCodeDscr(StringUtils.trimToEmpty(ps.getString(32))); //-- Price Coverage

			partPriceDetail.setPricngTierQtyMesurDscr(StringUtils.trimToEmpty(ps.getString(33)));      // -- Minimum Order Quantity
			partPriceDetail.setBillgUpfrntFlag       (StringUtils.trimToEmpty(ps.getString(34)));
			partPriceDetail.setBillgMthlyFlag        (StringUtils.trimToEmpty(ps.getString(35)));
			partPriceDetail.setBillgQtrlyFlag        (StringUtils.trimToEmpty(ps.getString(36)));
			partPriceDetail.setBillgAnlFlag          (StringUtils.trimToEmpty(ps.getString(37)));
            partPriceDetail.setBillgEventFlag(StringUtils.trimToEmpty(ps.getString(38)));
            partPriceDetail.setSaasRenwlMdlCodeDscr(StringUtils.trimToEmpty(ps.getString(39))); // -- Renewal Model
            partPriceDetail.setSwSbscrptnIdDscr(StringUtils.trimToEmpty(ps.getString(40))); // -- Subscription ID
            partPriceDetail.setSaasDaily(PartPriceConstants.SAAS_PART_FLAG_YES.equals(ps.getString(41))); // SaaS daily
                                                                                                          // flag
            partPriceDetail.setPriceType(StringUtils.trimToEmpty(ps.getString(42)));
			partPriceDetail.setSaasPartFlag(PartPriceConstants.FLAG_YES.equals(ps.getString(43)));
			partPriceDetail.setMonthlyPartFlag(PartPriceConstants.FLAG_YES.equals(ps.getString(44)));


            //deal with resultSets
            //deal with charge units.
            chargeUnitRs = ps.getResultSet();
            if (chargeUnitRs != null) {
                List chargeUnits = null;
                //System.err.println("Charge Unit RS is available!");
                while (chargeUnitRs.next()) {
                    if (chargeUnits == null) {
                        chargeUnits = new ArrayList();
                    }
                    String cu = chargeUnitRs.getString("CODE_DSCR");
                    if (cu != null) {
                        chargeUnits.add(cu);
                    }
                }
                partPriceDetail.setChargeUnits(chargeUnits);
            }
            //deal with platforms
            if (ps.getMoreResults()) {
                platformRs = ps.getResultSet();
                if (platformRs != null) {
                    //System.err.println("Platform RS is available!");
                    List platforms = null;
                    while (platformRs.next()) {
                        if (platforms == null) {
                            platforms = new ArrayList();
                        }
                        String pf = platformRs.getString("CODE_DSCR");
                        if (pf != null) {
                            platforms.add(pf);
                        }
                    }
                    partPriceDetail.setProductPlatforms(platforms);
                }
            }
            //deal with languages
            if (ps.getMoreResults()) {
                languageRs = ps.getResultSet();
                if (languageRs != null) {
                    //System.err.println("Language RS is available!");
                    List languages = null;
                    while (languageRs.next()) {
                        if (languages == null) {
                            languages = new ArrayList();
                        }
                        String lang = languageRs.getString("CODE_DSCR");
                        if (lang != null) {
                            languages.add(lang);
                        }
                    }
                    partPriceDetail.setPartLanguages(languages);
                }
            }
            //deal with pids
            if (ps.getMoreResults()) {
                pidRs = ps.getResultSet();
                if (pidRs != null) {
                    //System.err.println("PID RS is available!");
                    Map pids = null;
                    while (pidRs.next()) {
                        if (pids == null) {
                            pids = new LinkedHashMap();
                        }
                        String p_id = pidRs.getString("IBM_PROD_ID");
                        String p_desc = pidRs.getString("IBM_PROD_ID_DSCR");
                        if (p_id != null && p_desc != null) {
                            pids.put(p_id, p_desc);
                        }
                    }
                    partPriceDetail.setPids(pids);
                }
            }
            //deal with media types
            if (ps.getMoreResults()) {
                mediaRs = ps.getResultSet();
                if (mediaRs != null) {
                    //System.err.println("Media RS is available!");
                    List mediaTypes = null;
                    while (mediaRs.next()) {
                        if (mediaTypes == null) {
                            mediaTypes = new ArrayList();
                        }
                        String mediaType = mediaRs.getString("CODE_DSCR");
                        if (mediaType != null) {
                            mediaTypes.add(mediaType);
                        }
                    }
                    partPriceDetail.setMediaTypes(mediaTypes);
                }
            }
            //deal with part groups
            if (ps.getMoreResults()) {
                partGroupRs = ps.getResultSet();
                if (partGroupRs != null) {
                    //System.err.println("Part groups RS is available!");
                    List partGroups = null;
                    while (partGroupRs.next()) {
                        if (partGroups == null) {
                            partGroups = new ArrayList();
                        }
                        String partGroup = partGroupRs.getString("SPECL_BID_PART_GRP_NAME");
                        if (partGroup != null) {
                            partGroups.add(partGroup);
                        }
                    }
                    partPriceDetail.setPartGroups(partGroups);
                }
            }

            //deal with part price detail
            if (ps.getMoreResults()) {
            	priceDetailRs = ps.getResultSet();
                if (priceDetailRs != null) {
                    Map priceDetailMap = new HashMap();
                    while (priceDetailRs.next()) {
                    	List priceDetailList = new ArrayList();
                    	priceDetailList.add(priceDetailRs.getBigDecimal("SRP_PRICE") == null ? 0 : priceDetailRs.getBigDecimal("SRP_PRICE").doubleValue());
                    	priceDetailList.add(priceDetailRs.getBigDecimal("SVP_LEVEL_B") == null ? 0 : priceDetailRs.getBigDecimal("SVP_LEVEL_B").doubleValue());
                    	priceDetailList.add(priceDetailRs.getBigDecimal("SVP_LEVEL_D") == null ? 0 : priceDetailRs.getBigDecimal("SVP_LEVEL_D").doubleValue());
                    	priceDetailList.add(priceDetailRs.getBigDecimal("SVP_LEVEL_E") == null ? 0 : priceDetailRs.getBigDecimal("SVP_LEVEL_E").doubleValue());
                    	priceDetailList.add(priceDetailRs.getBigDecimal("SVP_LEVEL_F") == null ? 0 : priceDetailRs.getBigDecimal("SVP_LEVEL_F").doubleValue());
                    	priceDetailList.add(priceDetailRs.getBigDecimal("SVP_LEVEL_G") == null ? 0 : priceDetailRs.getBigDecimal("SVP_LEVEL_G").doubleValue());
                    	priceDetailList.add(priceDetailRs.getBigDecimal("SVP_LEVEL_H") == null ? 0 : priceDetailRs.getBigDecimal("SVP_LEVEL_H").doubleValue());
                    	priceDetailList.add(priceDetailRs.getBigDecimal("SVP_LEVEL_I") == null ? 0 : priceDetailRs.getBigDecimal("SVP_LEVEL_I").doubleValue());
                    	priceDetailList.add(priceDetailRs.getBigDecimal("SVP_LEVEL_J") == null ? 0 : priceDetailRs.getBigDecimal("SVP_LEVEL_J").doubleValue());
                    	priceDetailList.add(priceDetailRs.getBigDecimal("SVP_LEVEL_GV") == null ? 0 : priceDetailRs.getBigDecimal("SVP_LEVEL_GV").doubleValue());
                    	priceDetailList.add(priceDetailRs.getBigDecimal("SVP_LEVEL_ED") == null ? 0 : priceDetailRs.getBigDecimal("SVP_LEVEL_ED").doubleValue());
                    	priceDetailMap.put(priceDetailRs.getBigDecimal("TIERD_SCALE_QTY"),priceDetailList);
                    }
                    partPriceDetail.setPriceDetailMap(priceDetailMap);
                }
            }

            if (ps.getMoreResults()) {
            	dsPriceRs = ps.getResultSet();
                if (dsPriceRs != null) {
                    Map dsPriceMap = new HashMap();
                    while (dsPriceRs.next()) {
                    	List dsPriceList = new ArrayList();
                    	dsPriceList.add(dsPriceRs.getBigDecimal("SRP_PRICE") == null ? 0 : dsPriceRs.getBigDecimal("SRP_PRICE").doubleValue());
                    	dsPriceList.add(dsPriceRs.getBigDecimal("SVP_LEVEL_B") == null ? 0 : dsPriceRs.getBigDecimal("SVP_LEVEL_B").doubleValue());
                    	dsPriceList.add(dsPriceRs.getBigDecimal("SVP_LEVEL_D") == null ? 0 : dsPriceRs.getBigDecimal("SVP_LEVEL_D").doubleValue());
                    	dsPriceList.add(dsPriceRs.getBigDecimal("SVP_LEVEL_E") == null ? 0 : dsPriceRs.getBigDecimal("SVP_LEVEL_E").doubleValue());
                    	dsPriceList.add(dsPriceRs.getBigDecimal("SVP_LEVEL_F") == null ? 0 : dsPriceRs.getBigDecimal("SVP_LEVEL_F").doubleValue());
                    	dsPriceList.add(dsPriceRs.getBigDecimal("SVP_LEVEL_G") == null ? 0 : dsPriceRs.getBigDecimal("SVP_LEVEL_G").doubleValue());
                    	dsPriceList.add(dsPriceRs.getBigDecimal("SVP_LEVEL_H") == null ? 0 : dsPriceRs.getBigDecimal("SVP_LEVEL_H").doubleValue());
                    	dsPriceList.add(dsPriceRs.getBigDecimal("SVP_LEVEL_I") == null ? 0 : dsPriceRs.getBigDecimal("SVP_LEVEL_I").doubleValue());
                    	dsPriceList.add(dsPriceRs.getBigDecimal("SVP_LEVEL_J") == null ? 0 : dsPriceRs.getBigDecimal("SVP_LEVEL_J").doubleValue());
                    	dsPriceList.add(dsPriceRs.getBigDecimal("SVP_LEVEL_GV") == null ? 0 : dsPriceRs.getBigDecimal("SVP_LEVEL_GV").doubleValue());
                    	dsPriceList.add(dsPriceRs.getBigDecimal("SVP_LEVEL_ED") == null ? 0 : dsPriceRs.getBigDecimal("SVP_LEVEL_ED").doubleValue());
                    	dsPriceMap.put(dsPriceRs.getBigDecimal("TIERD_SCALE_QTY"),dsPriceList);
                    }
                    partPriceDetail.setDsPriceMap(dsPriceMap);
                }
            }
            
            isDeleted(false);
            isNew(false);
        } catch (Exception e) {
            logContext.error(this, e);
            throw new TopazException(e);
        }finally{
        	try {
				if (null != chargeUnitRs && !chargeUnitRs.isClosed())
				{
					chargeUnitRs.close();
				}
			} catch (SQLException e) {
				logContext.error("Failed to close the resultset!", e);
			}
        	try {
				if (null != platformRs && !platformRs.isClosed())
				{
					platformRs.close();
				}
			} catch (SQLException e) {
				logContext.error("Failed to close the resultset!", e);
			}
        	try {
				if (null != languageRs && !languageRs.isClosed())
				{
					languageRs.close();
				}
			} catch (SQLException e) {
				logContext.error("Failed to close the resultset!", e);
			}
        	try {
				if (null != pidRs && !pidRs.isClosed())
				{
					pidRs.close();
				}
			} catch (SQLException e) {
				logContext.error("Failed to close the resultset!", e);
			}
        	try {
				if (null != mediaRs && !mediaRs.isClosed())
				{
					mediaRs.close();
				}
			} catch (SQLException e) {
				logContext.error("Failed to close the resultset!", e);
			}
        	try {
				if (null != partGroupRs && !partGroupRs.isClosed())
				{
					partGroupRs.close();
				}
			} catch (SQLException e) {
				logContext.error("Failed to close the resultset!", e);
			}
            try {
				if (null != priceDetailRs && !priceDetailRs.isClosed())
				{
					priceDetailRs.close();
				}
			} catch (SQLException e) {
				logContext.error("Failed to close the resultset!", e);
			}
        }
    }

    private void loadCoPrerequisite() throws TopazException {
        LogContext logContext = LogContextFactory.singleton().getLogContext();
        if (partPriceDetail == null) {
            return;
        }
        HashMap params = new HashMap();
        params.put("piPartNum", partPriceDetail.getPartNumber());
        params.put("piWebQuoteNum", partPriceDetail.getWebQNumber());
        ResultSet coPrerequsiteRs = null;
        try {
            QueryContext queryCtx = QueryContext.getInstance();
            String sqlQuery = queryCtx.getCompletedQuery(CommonDBConstants.DB2_S_QT_PART_PREREQS, null);
            logContext.debug(this, LogHelper.logSPCall(sqlQuery, params));
            CallableStatement ps = TopazUtil.getConnection().prepareCall(sqlQuery);
            queryCtx.completeStatement(ps, CommonDBConstants.DB2_S_QT_PART_PREREQS, params);

            boolean retCode = ps.execute();
            if (!retCode) {
                throw new TopazException("SQL statement execution returns: " + retCode);
            }
            int poGenStatus = ps.getInt(1);
            if (poGenStatus != 0) {
                throw new TopazException("SP call returns error code: " + poGenStatus);
            }

            partPriceDetail.setCountryCode(StringUtils.trim(ps.getString(4)));
            partPriceDetail.setCurrencyCode(StringUtils.trim(ps.getString(5)));
            partPriceDetail.setSubmitted(ps.getInt(6) == 1);
            partPriceDetail.setRenewal(ps.getInt(7) == 1);

			partPriceDetail.setSaasPartFlag(PartPriceConstants.FLAG_YES.equals(ps.getString(8)));
			partPriceDetail.setMonthlyPartFlag(PartPriceConstants.FLAG_YES.equals(ps.getString(9)));

            coPrerequsiteRs = ps.getResultSet();
            if (coPrerequsiteRs != null) {
                Map coPrerequsites = null;
                while (coPrerequsiteRs.next()) {
                    if (coPrerequsites == null) {
                        coPrerequsites = new LinkedHashMap();
                    }
                    String p_id_desc = coPrerequsiteRs.getString("IBM_PROD_ID_DSCR");
                    String p_coprerequsite = coPrerequsiteRs.getString("KEY_PREREQUISITES");
                    if (p_id_desc != null && p_coprerequsite != null) {
                        coPrerequsites.put(p_id_desc, p_coprerequsite);
                    }
                }
                partPriceDetail.setCoprerequisites(coPrerequsites);
                coPrerequsiteRs.close();
            }
            isDeleted(false);
            isNew(false);
        } catch (Exception e) {
            logContext.error(this, e);
            throw new TopazException(e);
        }finally{
        	try {
				if (null != coPrerequsiteRs && !coPrerequsiteRs.isClosed())
				{
					coPrerequsiteRs.close();
				}
			} catch (SQLException e) {
				logContext.error("Failed to close the resultset!", e);
			}
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see com.ibm.ead4j.topaz.persistence.jdbc.AbstractPersister#insert(java.sql.Connection)
     */
    public void insert(Connection connection) throws TopazException {
        //middle of nowhere.
    }

}
