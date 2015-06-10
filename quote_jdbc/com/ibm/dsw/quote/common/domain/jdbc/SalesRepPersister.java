package com.ibm.dsw.quote.common.domain.jdbc;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;

import org.apache.commons.lang.StringUtils;

import com.ibm.dsw.quote.base.util.LogHelper;
import com.ibm.dsw.quote.base.util.TopazUtil;
import com.ibm.dsw.quote.common.config.CommonDBConstants;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;
import com.ibm.ead4j.topaz.common.jdbc.QueryContext;
import com.ibm.ead4j.topaz.exception.TopazException;
import com.ibm.ead4j.topaz.persistence.jdbc.Persister;

/**
 * <p>Copyright 2006 by IBM Corporation All rights reserved.</p>
 *
 * <p>This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information"). </p>
 *
 * @author <a href="mailto:mgivney@us.ibm.com">Matt Givney</a><br/>
 *
 */
public class SalesRepPersister extends Persister {
    /** the <code>SalesRep</code> */
    private SalesRep_jdbc salesRep_jdbc;

    protected LogContext logger = LogContextFactory.singleton().getLogContext();

    protected static final String PROCEDURE_NAME = "IU_QT_CREATR_DTL";
    protected static final String PARAM_USEREMAILADR = "piUserEmailAdr";
    protected static final String PARAM_CNTRYCODE = "piCntryCode";
    protected static final String PARAM_USERFULLNAME = "piUserFullName";
    protected static final String PARAM_USERFIRSTNAME  = "piUserFirstName";
    protected static final String PARAM_USERLASTNAME = "piUserLastName";
    protected static final String PARAM_INTLPHONENUMFULL = "piIntlPhoneNumFull";
    protected static final String PARAM_INTLFAXNUMFULL = "piIntlFaxNumFull";
    protected static final String PARAM_NOTESID = "piNotesId";

    /**
     * Constructor
     * @param salesRep_jdbc the <code>SalesRep</code> implementation
     */
    public SalesRepPersister(SalesRep_jdbc salesRep_jdbc) {
        super();
        this.salesRep_jdbc = salesRep_jdbc;
    }

    /**
     * @param connection
     * @throws com.ibm.ead4j.topaz.exception.TopazException
     * @see com.ibm.ead4j.topaz.persistence.jdbc.AbstractPersister#update(java.sql.Connection)
     */
    public void update(Connection connection) throws TopazException {
    }

    /**
     * @param connection
     * @throws com.ibm.ead4j.topaz.exception.TopazException
     * @see com.ibm.ead4j.topaz.persistence.jdbc.AbstractPersister#delete(java.sql.Connection)
     */
    public void delete(Connection connection) throws TopazException {
    }

    /**
     * @param connection
     * @throws com.ibm.ead4j.topaz.exception.TopazException
     * @see com.ibm.ead4j.topaz.persistence.jdbc.AbstractPersister#hydrate(java.sql.Connection)
     */
    public void hydrate(Connection connection) throws TopazException {
        HashMap parms = new HashMap();
        String userId = salesRep_jdbc.getEmailAddress();
        parms.put("piUserId", userId == null ? "" : userId);

        try {
            QueryContext queryCtx = QueryContext.getInstance();
            String sqlQuery = queryCtx.getCompletedQuery(CommonDBConstants.DB2_S_QT_GET_USERDTL, null);
            CallableStatement ps = TopazUtil.getConnection().prepareCall(sqlQuery);
            queryCtx.completeStatement(ps, CommonDBConstants.DB2_S_QT_GET_USERDTL, parms);
            logger.debug(this, LogHelper.logSPCall(sqlQuery, parms));

            boolean retCode = ps.execute();
            int poGenStatus = ps.getInt(1);

            if (poGenStatus == 0) {
                this.salesRep_jdbc.setEmailAddress(StringUtils.trimToEmpty(ps.getString(3)));
                this.salesRep_jdbc.setCountryCode(StringUtils.trimToEmpty(ps.getString(4)));
                this.salesRep_jdbc.setFirstName(StringUtils.trimToEmpty(ps.getString(5)));
                this.salesRep_jdbc.setLastName(StringUtils.trimToEmpty(ps.getString(6)));
                this.salesRep_jdbc.setPhoneNumber(StringUtils.trimToEmpty(ps.getString(7)));
                this.salesRep_jdbc.setFullName(StringUtils.trimToEmpty(ps.getString(8)));
                this.salesRep_jdbc.setCompany(StringUtils.trimToEmpty(ps.getString(9)));
                this.salesRep_jdbc.setEvaluator(ps.getInt(10) ==1);
            }
            this.isNew(false);
            this.isDeleted(false);
        } catch (SQLException e) {
            logger.error("Failed to retrieve user's detail from database!", e );
            throw new TopazException(e);
        }
    }

    /**
     * @param connection
     * @throws com.ibm.ead4j.topaz.exception.TopazException
     * @see com.ibm.ead4j.topaz.persistence.jdbc.AbstractPersister#insert(java.sql.Connection)
     */
    public void insert(Connection connection) throws TopazException {
        HashMap params = new HashMap();
        params.put( PARAM_USEREMAILADR , 			StringUtils.lowerCase(salesRep_jdbc.getEmailAddress() ));
        params.put( PARAM_CNTRYCODE, 					salesRep_jdbc.getCountryCode() );
        params.put( PARAM_USERFULLNAME, 			salesRep_jdbc.getFullName() );
        params.put( PARAM_USERFIRSTNAME, 			salesRep_jdbc.getFirstName() );
        params.put( PARAM_USERLASTNAME, 			salesRep_jdbc.getLastName() );
        params.put( PARAM_INTLPHONENUMFULL,	salesRep_jdbc.getPhoneNumber() );
        params.put( PARAM_INTLFAXNUMFULL,			salesRep_jdbc.getFaxNumber() );
        params.put( PARAM_NOTESID, 							salesRep_jdbc.getNotesId() );
        int retCode = -1;
        try{
            QueryContext context = QueryContext.getInstance();
            String sqlQuery = context.getCompletedQuery( PROCEDURE_NAME, null);
            logger.debug(this, LogHelper.logSPCall( sqlQuery, params ));
            CallableStatement ps = connection.prepareCall(sqlQuery);
            context.completeStatement(ps, PROCEDURE_NAME, params);
            ps.execute();
            retCode = ps.getInt(1);
            salesRep_jdbc.countryCode3 = ps.getString(10);
            if(retCode != 0){
                throw new TopazException("SP call returns error code: "+ retCode);
            }
            this.isNew(true);
            this.isDeleted(false);
        }catch( Exception e ){
            logger.error(  "Failed to log the SalesRep to the database!", e );
            throw new TopazException( e );
        }
    }
}
