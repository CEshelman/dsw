package com.ibm.dsw.automation.vo;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.regex.Pattern;

import org.testng.log4testng.Logger;

import com.ibm.dsw.automation.util.DBTools;

public class Part
{
    /**
     * retrieve Country.properties
     */
    private static Properties prop;

    private Logger logger = Logger.getLogger(this.getClass());
    /**
     * Database connection
     */
    private Connection conn;
    public Part(String connKey) {
    	try{
    		this.conn = DBTools.getInstance().getConn(connKey);
    	}catch(Exception e){
    		logger.fatal("Failed to get db connection.",e);
    	}
    }
    

	

    static
    {
        prop = new Properties();
        InputStream in = Object.class.getResourceAsStream("/com/ibm/dsw/automation/vo/Part.properties");
        try
        {
            prop.load(in);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public Part(Connection conn)
    {
        super();
        this.conn = conn;
    }

    public void setConn(Connection conn)
    {
        this.conn = conn;
    }

    /**
     * Query for hidden parts together the acquisition name: you may need to change the parameter values for EBIZ1.F_QT_P_RSTRCT
     * 
     * @param internal default 'INTERNAL'
     * @param country default 'USA'
     * @param state default 'AMERICAS'
     * @param namerica default 'NAMERICA'
     * @return a list that is a json object of name/value pairs.
     * The field 'PART_NUM' retrieve PART_NUM of SQL 'partAcquisition',
     * The field 'PART_DSCR_LONG' retrieve PART_DSCR_LONG of SQL' partAcquisition',
     * The field 'MIGRTN_CODE' retrieve MIGRTN_CODE of 'partAcquisition'
     */
    public List<String> getPartTogethorAcquisition(String internal, String country, String state, String namerica)
    {
        List<String> parts = new ArrayList<String>();

        internal = null == internal ? "INTERNAL" : internal;
        country = null == country ? "USA" : country;
        state = null == state ? "AMERICAS" : state;
        namerica = null == namerica ? "NAMERICA" : namerica;

        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String sql = prop.getProperty("partAcquisition");
        try
        {
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, internal);
            pstmt.setString(2, country);
            pstmt.setString(3, state);
            pstmt.setString(4, namerica);
            rs = pstmt.executeQuery();
            StringBuilder dataInfo = null;
            while (rs.next())
            {
                dataInfo = new StringBuilder("{");
                dataInfo.append("'PART_NUM':'").append(rs.getString(1)).append("',");
                dataInfo.append("'PART_DSCR_LONG':'").append(rs.getString(2)).append("',");
                dataInfo.append("'MIGRTN_CODE':'").append(rs.getString(3)).append("'");
                dataInfo.append("}");
                parts.add(dataInfo.toString());
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        finally
        {
            try
            {
                if (rs != null)
                {
                    rs.close();
                }
                if (pstmt != null)
                {
                    pstmt.close();
                }
            }
            catch (SQLException e)
            {
                e.printStackTrace();
            }

        }
        return parts;
    }

    /**
     * Query for FCT EOL parts
     * 
     * @param state default 'AMERICAS'
     * @return a list that is a json object of name/value pairs.
     * The field 'MIGRTN_CODE' retrieve migrtn_code of SQL 'fctEOLparts'
     * The field 'PART_NUM' retrieve part_num of SQL 'fctEOLparts',
     * The field 'PART_EOL_DT' retrieve part_eol_dt of SQL 'fctEOLparts',
     * The field 'PART_DSCR_LONG' retrieve part_dscr_long of SQL 'fctEOLparts',
     */
    public List<String> getFctEOLparts(String state)
    {
        List<String> parts = new ArrayList<String>();
        state = null == state ? "AMERICAS" : state;

        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String sql = prop.getProperty("fctEOLparts");
        try
        {
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, state);
            pstmt.setString(2, state);
            pstmt.setString(3, state);
            pstmt.setString(4, state);
            rs = pstmt.executeQuery();
            StringBuilder dataInfo = null;
            while (rs.next())
            {
                dataInfo = new StringBuilder("{");
                dataInfo.append("'MIGRTN_CODE':'").append(rs.getString(1)).append("',");
                dataInfo.append("'PART_NUM':'").append(rs.getString(2)).append("',");
                dataInfo.append("'PART_EOL_DT':'").append(rs.getString(3)).append("',");
                dataInfo.append("'PART_DSCR_LONG':'").append(rs.getString(4)).append("'");
                dataInfo.append("}");
                parts.add(dataInfo.toString());
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        finally
        {
            try
            {
                if (rs != null)
                {
                    rs.close();
                }
                if (pstmt != null)
                {
                    pstmt.close();
                }
            }
            catch (SQLException e)
            {
                e.printStackTrace();
            }

        }
        return parts;
    }
    
    
    /**
     * Query for PA/PAE  parts
     * 
     * @param state default 'AMERICAS'
     * @return a list that is a json object of name/value pairs.
     * The field 'SW_PROD_BRAND_CODE' retrieve SW_PROD_BRAND_CODE of SQL 'papaeEOLparts',
     * The field 'SW_PROD_BRAND_CODE_DSCR' retrieve SW_PROD_BRAND_CODE_DSCR of SQL 'papaeEOLparts',
     * The field 'WWIDE_PROD_CODE' retrieve WWIDE_PROD_CODE of SQL 'papaeEOLparts',
     * The field 'WWIDE_PROD_CODE_DSCR' retrieve WWIDE_PROD_CODE_DSCR of SQL 'papaeEOLparts',
     * The field 'REV_STRM_OR_PROD_PACK' retrieve REV_STRM_OR_PROD_PACK of SQL 'papaeEOLparts',
     * The field 'REV_STRM_OR_PROD_PACK_DSCR' retrieve REV_STRM_OR_PROD_PACK_DSCR of SQL 'papaeEOLparts',
     * The field 'PART_NUM' retrieve PART_NUM of SQL 'papaeEOLparts',
     * The field 'PART_DSCR_LONG' retrieve PART_DSCR_LONG of SQL 'papaeEOLparts',
     * The field 'PROD_EOL_DATE' retrieve PROD_EOL_DATE of SQL 'papaeEOLparts',
     * The field 'PART_OBSLTE_FLAG' retrieve PART_OBSLTE_FLAG of SQL 'papaeEOLparts',
     * The field 'SAP_MATL_GRP_5_COND_CODE' retrieve SAP_MATL_GRP_5_COND_CODE of SQL 'papaeEOLparts',
     * The field 'SAP_SALES_STAT_CODE' retrieve SAP_SALES_STAT_CODE of SQL 'papaeEOLparts'
     */
    public List<String> getPAPAEParts(String state)
    {
        List<String> parts = new ArrayList<String>();
        state = null == state ? "AMERICAS" : state;

        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String sql = prop.getProperty("papaeParts");
        try
        {
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, state);

            rs = pstmt.executeQuery();
            while (rs.next())
            {
                parts.add( rs.getString(1));
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        finally
        {
            try
            {
                if (rs != null)
                {
                    rs.close();
                }
                if (pstmt != null)
                {
                    pstmt.close();
                }
            }
            catch (SQLException e)
            {
                e.printStackTrace();
            }

        }
        return parts;
    }

    /**
     * Query for PA/PAE EOL parts
     * 
     * @param state default 'AMERICAS'
     * @return a list that is a json object of name/value pairs.
     * The field 'SW_PROD_BRAND_CODE' retrieve SW_PROD_BRAND_CODE of SQL 'papaeEOLparts',
     * The field 'SW_PROD_BRAND_CODE_DSCR' retrieve SW_PROD_BRAND_CODE_DSCR of SQL 'papaeEOLparts',
     * The field 'WWIDE_PROD_CODE' retrieve WWIDE_PROD_CODE of SQL 'papaeEOLparts',
     * The field 'WWIDE_PROD_CODE_DSCR' retrieve WWIDE_PROD_CODE_DSCR of SQL 'papaeEOLparts',
     * The field 'REV_STRM_OR_PROD_PACK' retrieve REV_STRM_OR_PROD_PACK of SQL 'papaeEOLparts',
     * The field 'REV_STRM_OR_PROD_PACK_DSCR' retrieve REV_STRM_OR_PROD_PACK_DSCR of SQL 'papaeEOLparts',
     * The field 'PART_NUM' retrieve PART_NUM of SQL 'papaeEOLparts',
     * The field 'PART_DSCR_LONG' retrieve PART_DSCR_LONG of SQL 'papaeEOLparts',
     * The field 'PROD_EOL_DATE' retrieve PROD_EOL_DATE of SQL 'papaeEOLparts',
     * The field 'PART_OBSLTE_FLAG' retrieve PART_OBSLTE_FLAG of SQL 'papaeEOLparts',
     * The field 'SAP_MATL_GRP_5_COND_CODE' retrieve SAP_MATL_GRP_5_COND_CODE of SQL 'papaeEOLparts',
     * The field 'SAP_SALES_STAT_CODE' retrieve SAP_SALES_STAT_CODE of SQL 'papaeEOLparts'
     */
    public List<String> getPAPAEEOLparts(String state)
    {
        List<String> parts = new ArrayList<String>();
        state = null == state ? "AMERICAS" : state;

        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String sql = prop.getProperty("papaeEOLparts");
        try
        {
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, state);
            pstmt.setString(2, state);
            pstmt.setString(3, state);
            pstmt.setString(4, state);
            rs = pstmt.executeQuery();
            StringBuilder dataInfo = null;
            while (rs.next())
            {
                dataInfo = new StringBuilder("{");
                dataInfo.append("'SW_PROD_BRAND_CODE':'").append(rs.getString(1)).append("',");
                dataInfo.append("'SW_PROD_BRAND_CODE_DSCR':'").append(rs.getString(2)).append("',");
                dataInfo.append("'WWIDE_PROD_CODE':'").append(rs.getString(3)).append("',");
                dataInfo.append("'WWIDE_PROD_CODE_DSCR':'").append(rs.getString(4)).append("',");
                dataInfo.append("'REV_STRM_OR_PROD_PACK':'").append(rs.getString(5)).append("',");
                dataInfo.append("'REV_STRM_OR_PROD_PACK_DSCR':'").append(rs.getString(6)).append("',");
                dataInfo.append("'PART_NUM':'").append(rs.getString(7)).append("',");
                dataInfo.append("'PART_DSCR_LONG':'").append(rs.getString(8)).append("',");
                dataInfo.append("'PROD_EOL_DATE':'").append(rs.getString(9)).append("',");
                dataInfo.append("'PART_OBSLTE_FLAG':'").append(rs.getString(10)).append("',");
                dataInfo.append("'SAP_MATL_GRP_5_COND_CODE':'").append(rs.getString(11)).append("',");
                dataInfo.append("'SAP_SALES_STAT_CODE':'").append(rs.getString(12)).append("'");
                dataInfo.append("}");
                parts.add(dataInfo.toString());
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        finally
        {
            try
            {
                if (rs != null)
                {
                    rs.close();
                }
                if (pstmt != null)
                {
                    pstmt.close();
                }
            }
            catch (SQLException e)
            {
                e.printStackTrace();
            }

        }
        return parts;
    }

    /**
     * Query for restrict parts
     * 
     * @param internal default 'INTERNAL'
     * @param country default 'USA'
     * @param state default 'AMERICAS'
     * @param namerica default 'NAMERICA'
     * @return a list contain The field 'PART_NUM' retrieve part_num of SQL 'partAcquisition',
     */
    public List<String> getRestrictParts(String internal, String country, String state, String namerica)
    {
        List<String> parts = new ArrayList<String>();
        internal = null == internal ? "INTERNAL" : internal;
        country = null == country ? "USA" : country;
        state = null == state ? "AMERICAS" : state;
        namerica = null == namerica ? "NAMERICA" : namerica;

        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String sql = prop.getProperty("restrictParts");
        try
        {
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, internal);
            pstmt.setString(2, country);
            pstmt.setString(3, state);
            pstmt.setString(4, namerica);
            rs = pstmt.executeQuery();
            while (rs.next())
            {
                parts.add(rs.getString(1));
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        finally
        {
            try
            {
                if (rs != null)
                {
                    rs.close();
                }
                if (pstmt != null)
                {
                    pstmt.close();
                }
            }
            catch (SQLException e)
            {
                e.printStackTrace();
            }

        }
        return parts;
    }

    /**
     * Query FCT parts
     * 
     * @param piCtrctProgCode default 'FCTTOT'
     * @param piRegion default 'AMERICAS'
     * @param piAcqrtnCode default 'MMSE'
     * @return FCT parts
     */
    public List<String> getPartNum(String piCtrctProgCode, String piRegion, String piAcqrtnCode)
    {
        List<String> partNums = new ArrayList<String>();
        piCtrctProgCode = null == piCtrctProgCode ? "FCTTOT" : piCtrctProgCode;
        piRegion = null == piRegion ? "AMERICAS" : piRegion;
        piAcqrtnCode = null == piAcqrtnCode ? "MMSE" : piAcqrtnCode;

        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String sql = prop.getProperty("queryPartNum");
        try
        {
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, piCtrctProgCode);
            pstmt.setString(2, piRegion);
            pstmt.setString(3, piAcqrtnCode);
            rs = pstmt.executeQuery();
            while (rs.next())
            {
                partNums.add(rs.getString(1));
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        finally
        {
            try
            {
                if (rs != null)
                {
                    rs.close();
                }
                if (pstmt != null)
                {
                    pstmt.close();
                }
            }
            catch (SQLException e)
            {
                e.printStackTrace();
            }

        }
        return partNums;
    }

    /**
     * Query some replacement parts for some EOL part (FCT)
     * 
     * @param partNum default 'AMERICAS'
     * @return a list that is a json object of name/value pairs.
     * The field 'WWIDE_PROD_CODE' retrieve WWIDE_PROD_CODE of SQL 'someEOLPart',
     * The field 'WWIDE_PROD_CODE_DSCR' retrieve WWIDE_PROD_CODE_DSCR of SQL 'someEOLPart',
     * The field 'PART_NUM' retrieve PART_NUM of SQL 'someEOLPart',
     * The field 'PART_DSCR_LONG' retrieve PART_DSCR_LONG of SQL 'someEOLPart',
     * The field 'PRICE_LEVEL_A' retrieve PRICE_LEVEL_A of SQL 'someEOLPart',
     * The field 'PRICE_END_DATE' retrieve PRICE_END_DATE of SQL 'someEOLPart',
     * The field 'PROD_EOL_DATE' retrieve PROD_EOL_DATE of SQL 'someEOLPart',
     * The field 'PART_RSTRCT_FLAG' retrieve PART_RSTRCT_FLAG of SQL 'someEOLPart',
     * The field 'SW_PROD_BRAND_CODE' retrieve SW_PROD_BRAND_CODE of SQL 'someEOLPart',
     * The field 'SAP_MATL_GRP_5_COND_CODE' retrieve SAP_MATL_GRP_5_COND_CODE of SQL 'someEOLPart',
     * The field 'SAAS_DLY_FLAG' retrieve SAAS_DLY_FLAG of SQL 'someEOLPart'
     */
    public List<String> getSomeEOLPart(String partNum)
    {
        List<String> parts = new ArrayList<String>();
        partNum = null == partNum ? "AMERICAS" : partNum;

        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String sql = prop.getProperty("someEOLPart");
        try
        {
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, partNum);
            rs = pstmt.executeQuery();
            StringBuilder dataInfo = null;
            while (rs.next())
            {
                dataInfo = new StringBuilder("{");
                dataInfo.append("'WWIDE_PROD_CODE':'").append(rs.getString(1)).append("',");
                dataInfo.append("'WWIDE_PROD_CODE_DSCR':'").append(rs.getString(2)).append("',");
                dataInfo.append("'PART_NUM':'").append(rs.getString(3)).append("',");
                dataInfo.append("'PART_DSCR_LONG':'").append(rs.getString(4)).append("',");
                dataInfo.append("'PRICE_LEVEL_A':'").append(rs.getString(5)).append("',");
                dataInfo.append("'PRICE_END_DATE':'").append(rs.getString(6)).append("',");
                dataInfo.append("'PROD_EOL_DATE':'").append(rs.getString(7)).append("',");
                dataInfo.append("'PART_RSTRCT_FLAG':'").append(rs.getString(8)).append("',");
                dataInfo.append("'SW_PROD_BRAND_CODE':'").append(rs.getString(9)).append("',");
                dataInfo.append("'SAP_MATL_GRP_5_COND_CODE':'").append(rs.getString(10)).append("',");
                dataInfo.append("'SAAS_DLY_FLAG':'").append(rs.getString(11)).append("'");
                dataInfo.append("}");
                parts.add(dataInfo.toString());
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        finally
        {
            try
            {
                if (rs != null)
                {
                    rs.close();
                }
                if (pstmt != null)
                {
                    pstmt.close();
                }
            }
            catch (SQLException e)
            {
                e.printStackTrace();
            }

        }
        return parts;
    }

    /**
     * Query OEM parts
     * 
     * @return OEM part number
     */
    public List<String> getOEMPartNum()
    {
        List<String> partNums = new ArrayList<String>();

        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String sql = prop.getProperty("queryOEMPart");
        try
        {
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();
            while (rs.next())
            {
                partNums.add(rs.getString(1));
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        finally
        {
            try
            {
                if (rs != null)
                {
                    rs.close();
                }
                if (pstmt != null)
                {
                    pstmt.close();
                }
            }
            catch (SQLException e)
            {
                e.printStackTrace();
            }

        }
        return partNums;
    }

    /**
     * Query PPSS parts
     * 
     * @return PPSS part number
     */
    public List<String> getPPSSPartNum()
    {
        List<String> partNums = new ArrayList<String>();

        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String sql = prop.getProperty("queryPPSSPart");
        try
        {
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();
            while (rs.next())
            {
                partNums.add(rs.getString(1));
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        finally
        {
            try
            {
                if (rs != null)
                {
                    rs.close();
                }
                if (pstmt != null)
                {
                    pstmt.close();
                }
            }
            catch (SQLException e)
            {
                e.printStackTrace();
            }

        }
        return partNums;
    }

    /**
     * Query SAAS parts that show quantity input box
     * 
     * @return SAAS part number
     */
    public List<String> getSAASPartNum()
    {
        List<String> partNums = new ArrayList<String>();

        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String sql = prop.getProperty("querySAASPart");
        try
        {
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();
            while (rs.next())
            {
                partNums.add(rs.getString(1));
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        finally
        {
            try
            {
                if (rs != null)
                {
                    rs.close();
                }
                if (pstmt != null)
                {
                    pstmt.close();
                }
            }
            catch (SQLException e)
            {
                e.printStackTrace();
            }

        }
        return partNums;
    }

    /**
     * Query SAAS parts that show up to drop down or show quantity should
     * multiple of xxx
     * 
     * @param mdl ['VALU', 'GRAD']
     * @return part number
     */
    public List<String> getSAASPartNum(String mdl)
    {
        List<String> partNums = new ArrayList<String>();

        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String sql = prop.getProperty("querySAASPartBymdl");
        try
        {
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, mdl);
            rs = pstmt.executeQuery();
            while (rs.next())
            {
                partNums.add(rs.getString(1));
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        finally
        {
            try
            {
                if (rs != null)
                {
                    rs.close();
                }
                if (pstmt != null)
                {
                    pstmt.close();
                }
            }
            catch (SQLException e)
            {
                e.printStackTrace();
            }

        }
        return partNums;
    }

    /**
     * Query SAAS parts that have no quantity
     * 
     * @return SAAS part number
     */
    public List<String> getSAASPartNumWithNoQuantity()
    {
        List<String> partNums = new ArrayList<String>();

        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String sql = prop.getProperty("querySAASPartNoQuantity");
        try
        {
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();
            while (rs.next())
            {
                partNums.add(rs.getString(1));
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        finally
        {
            try
            {
                if (rs != null)
                {
                    rs.close();
                }
                if (pstmt != null)
                {
                    pstmt.close();
                }
            }
            catch (SQLException e)
            {
                e.printStackTrace();
            }

        }
        return partNums;
    }

    /**
     * Query SAAS parts that show committed term
     * 
     * @return SAAS part number
     */
    public List<String> getSAASPartCommittedTerm()
    {
        List<String> partNums = new ArrayList<String>();

        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String sql = prop.getProperty("querySAASPartCommittedTerm");
        try
        {
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();
            while (rs.next())
            {
                partNums.add(rs.getString(1));
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        finally
        {
            try
            {
                if (rs != null)
                {
                    rs.close();
                }
                if (pstmt != null)
                {
                    pstmt.close();
                }
            }
            catch (SQLException e)
            {
                e.printStackTrace();
            }

        }
        return partNums;
    }

    /**
     * DSW 10.5 valid PID
     * 
     * @return a list that is a json object of name/value pairs.
     * The field 'IBM_PROD_ID' retrieve IBM_PROD_ID of SQL 'queryValidPId',
     * The field 'IBM_PROD_ID_DSCR' retrieve IBM_PROD_ID_DSCR of SQL 'queryValidPId'
     */
    public List<String> getValidPId()
    {
        List<String> parts = new ArrayList<String>();

        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String sql = prop.getProperty("queryValidPId");
        try
        {
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();
            StringBuilder dataInfo = null;
            while (rs.next())
            {
                dataInfo = new StringBuilder("{");
                dataInfo.append("'IBM_PROD_ID':'").append(rs.getString(1)).append("',");
                dataInfo.append("'IBM_PROD_ID_DSCR':'").append(rs.getString(2)).append("'");
                dataInfo.append("}");
                parts.add(dataInfo.toString());
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        finally
        {
            try
            {
                if (rs != null)
                {
                    rs.close();
                }
                if (pstmt != null)
                {
                    pstmt.close();
                }
            }
            catch (SQLException e)
            {
                e.printStackTrace();
            }

        }
        return parts;
    }

    /**
     * Parts have pvu
     * 
     * @return a list that is a json object of name/value pairs.
     * The field 'PART_CFGS' retrieve PART_CFGS of SQL 'queryPartWithPvu',
     * The field 'PART_NUM' retrieve PART_NUM of SQL 'queryPartWithPvu',
     * The field 'QUOTE_LINE_ITEM_SEQ_NUM' retrieve QUOTE_LINE_ITEM_SEQ_NUM of SQL 'queryPartWithPvu',
     * The field 'PROCR_CODE' retrieve PROCR_CODE of SQL 'queryPartWithPvu',
     * The field 'PROCR_VEND_CODE' retrieve PROCR_VEND_CODE of SQL 'queryPartWithPvu',
     * The field 'PROCRVENDCODEDSCR' retrieve PROCRVENDCODEDSCR of SQL 'queryPartWithPvu',
     * The field 'PROCR_BRAND_CODE' retrieve PROCR_BRAND_CODE of SQL 'queryPartWithPvu',
     * The field 'PROCRBRANDCODEDSCR' retrieve PROCRBRANDCODEDSCR of SQL 'queryPartWithPvu',
     * The field 'PROCR_TYPE_CODE' retrieve PROCR_TYPE_CODE of SQL 'queryPartWithPvu',
     * The field 'PROCRTYPECODEDSCR' retrieve PROCRTYPECODEDSCR of SQL 'queryPartWithPvu',
     * The field 'CORE_VAL_UNIT' retrieve CORE_VAL_UNIT of SQL 'queryPartWithPvu',
     * The field 'PROCR_TYPE_QTY' retrieve PROCR_TYPE_QTY of SQL 'queryPartWithPvu',
     * The field 'EXTND_DVU' retrieve EXTND_DVU of SQL 'queryPartWithPvu'
     */
    public List<String> getPartWithPvu()
    {
        List<String> parts = new ArrayList<String>();

        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String sql = prop.getProperty("queryPartWithPvu");
        try
        {
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();
            StringBuilder dataInfo = null;
            while (rs.next())
            {
                dataInfo = new StringBuilder("{");
                dataInfo.append("'PART_CFGS':'").append(rs.getString(1)).append("',");
                dataInfo.append("'PART_NUM':'").append(rs.getString(2)).append("',");
                dataInfo.append("'QUOTE_LINE_ITEM_SEQ_NUM':'").append(rs.getString(3)).append("',");
                dataInfo.append("'PROCR_CODE':'").append(rs.getString(4)).append("',");
                dataInfo.append("'PROCR_VEND_CODE':'").append(rs.getString(5)).append("',");
                dataInfo.append("'PROCRVENDCODEDSCR':'").append(rs.getString(6)).append("',");
                dataInfo.append("'PROCR_BRAND_CODE':'").append(rs.getString(7)).append("',");
                dataInfo.append("'PROCRBRANDCODEDSCR':'").append(rs.getString(8)).append("',");
                dataInfo.append("'PROCR_TYPE_CODE':'").append(rs.getString(9)).append("',");
                dataInfo.append("'PROCRTYPECODEDSCR':'").append(rs.getString(10)).append("',");
                dataInfo.append("'CORE_VAL_UNIT':'").append(rs.getString(11)).append("',");
                dataInfo.append("'PROCR_TYPE_QTY':'").append(rs.getString(12)).append("',");
                dataInfo.append("'EXTND_DVU':'").append(rs.getString(13)).append("'");
                dataInfo.append("}");
                parts.add(dataInfo.toString());
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        finally
        {
            try
            {
                if (rs != null)
                {
                    rs.close();
                }
                if (pstmt != null)
                {
                    pstmt.close();
                }
            }
            catch (SQLException e)
            {
                e.printStackTrace();
            }

        }
        return parts;
    }

    /**
     * Search parts within a PID
     * 
     * @param pid PID
     * @return a list that is a json object of name/value pairs.
     * The field 'PART_NUM' retrieve PART_NUM of SQL 'queryPartWithPid',
     * The field 'SAAS_FLAG' retrieve ebiz1.f_web_is_saas_part(efpi.PART_NUM) of SQL 'queryPartWithPid'
     */
    public List<String> getPartWithPid(String pid)
    {
        List<String> parts = new ArrayList<String>();

        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String sql = prop.getProperty("queryPartWithPid");
        try
        {
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, pid);
            rs = pstmt.executeQuery();
            StringBuilder dataInfo = null;
            while (rs.next())
            {
                dataInfo = new StringBuilder("{");
                dataInfo.append("'PART_NUM':'").append(rs.getString(1)).append("',");
                dataInfo.append("'SAAS_FLAG':'").append(rs.getString(2)).append("'");
                dataInfo.append("}");
                parts.add(dataInfo.toString());
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        finally
        {
            try
            {
                if (rs != null)
                {
                    rs.close();
                }
                if (pstmt != null)
                {
                    pstmt.close();
                }
            }
            catch (SQLException e)
            {
                e.printStackTrace();
            }

        }
        return parts;
    }

    /**
     * Get parts by PID --- main sqls in SP - EBIZ1.S_QT_PART_BY_PID
     * 
     * @return part number
     */
    public List<String> getPartWithPidInSP(String pid)
    {
        List<String> partNums = new ArrayList<String>();

        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String sql = prop.getProperty("queryPartWithPidInSP");
        try
        {
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, pid);
            rs = pstmt.executeQuery();
            while (rs.next())
            {
                partNums.add(rs.getString(1));
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        finally
        {
            try
            {
                if (rs != null)
                {
                    rs.close();
                }
                if (pstmt != null)
                {
                    pstmt.close();
                }
            }
            catch (SQLException e)
            {
                e.printStackTrace();
            }

        }
        return partNums;
    }

    /**
     * Find parts for South Sudan
     * 
     * @return part number
     */
    public List<String> getPartForSouthSudan()
    {
        List<String> partNums = new ArrayList<String>();

        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String sql = prop.getProperty("queryPartForSouthSudan");
        try
        {
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();
            while (rs.next())
            {
                partNums.add(rs.getString(1));
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        finally
        {
            try
            {
                if (rs != null)
                {
                    rs.close();
                }
                if (pstmt != null)
                {
                    pstmt.close();
                }
            }
            catch (SQLException e)
            {
                e.printStackTrace();
            }

        }
        return partNums;
    }

    /**
     * SQL to find PID redirect to GST
     * Please use below SQO to query PIDs which can be redirected to GST.
     * AUD_CODE 'INTERNAL' means in SQO, 'PSPTRSEL' means in PGS.
     * 
     * @return a list that is a json object of name/value pairs.
     * The field 'IBM_PROD_ID' retrieve IBM_PROD_ID of SQL 'queryProductId',
     * The field 'AUD_CODE' retrieve AUD_CODE of SQL 'queryProductId'
     */
    public List<String> getProductId()
    {
        List<String> productIds = new ArrayList<String>();

        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String sql = prop.getProperty("queryProductId");
        try
        {
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();
            StringBuilder dataInfo = null;
            while (rs.next())
            {
                dataInfo = new StringBuilder("{");
                dataInfo.append("'IBM_PROD_ID':'").append(rs.getString(1)).append("',");
                dataInfo.append("'AUD_CODE':'").append(rs.getString(2)).append("'");
                dataInfo.append("}");
                productIds.add(dataInfo.toString());
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        finally
        {
            try
            {
                if (rs != null)
                {
                    rs.close();
                }
                if (pstmt != null)
                {
                    pstmt.close();
                }
            }
            catch (SQLException e)
            {
                e.printStackTrace();
            }

        }
        return productIds;
    }

    /**
     * SQL to find appliance parts
     * 
     * @return a list that is a json object of name/value pairs.
     * The field 'PART_NUM' retrieve PART_NUM of SQL 'queryAppliancePart',
     * The field 'WWIDE_PROD_CODE' retrieve WWIDE_PROD_CODE of SQL 'queryAppliancePart'
     * The field 'CNTRIBTN_UNIT_PTS' retrieve CNTRIBTN_UNIT_PTS of SQL 'queryAppliancePart'
     * The field 'PROD_PACK_TYPE_CODE' retrieve PROD_PACK_TYPE_CODE of SQL 'queryAppliancePart'
     * The field 'PROD_DISTRIBTN_CODE' retrieve PROD_DISTRIBTN_CODE of SQL 'queryAppliancePart'
     * The field 'REVN_STREAM_CODE' retrieve REVN_STREAM_CODE of SQL 'queryAppliancePart'
     * The field 'CTRCT_PROG_CODE' retrieve CTRCT_PROG_CODE of SQL 'queryAppliancePart'
     */
    public List<String> getAppliancePart()
    {
        List<String> productIds = new ArrayList<String>();

        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String sql = prop.getProperty("queryAppliancePart");
        try
        {
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();
            StringBuilder dataInfo = null;
            while (rs.next())
            {
                dataInfo = new StringBuilder("{");
                dataInfo.append("'PART_NUM':'").append(rs.getString(1)).append("',");
                dataInfo.append("'WWIDE_PROD_CODE':'").append(rs.getString(2)).append("',");
                dataInfo.append("'CNTRIBTN_UNIT_PTS':'").append(rs.getDouble(3)).append("',");
                dataInfo.append("'PROD_PACK_TYPE_CODE':'").append(rs.getString(6)).append("',");
                dataInfo.append("'PROD_DISTRIBTN_CODE':'").append(rs.getString(7)).append("',");
                dataInfo.append("'REVN_STREAM_CODE':'").append(rs.getString(8)).append("',");
                dataInfo.append("'CTRCT_PROG_CODE':'").append(rs.getString(9)).append("'");
                dataInfo.append("}");
                productIds.add(dataInfo.toString());
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        finally
        {
            try
            {
                if (rs != null)
                {
                    rs.close();
                }
                if (pstmt != null)
                {
                    pstmt.close();
                }
            }
            catch (SQLException e)
            {
                e.printStackTrace();
            }

        }
        return productIds;
    }

    /**
     * Find FCT parts by ID
     * 
     * @return a list that contain key-value.
     * The field 'PART_NUM' retrieve PART_NUM of SQL 'queryAppliancePart',
     * The field 'WWIDE_PROD_CODE' retrieve WWIDE_PROD_CODE of SQL 'queryAppliancePart'
     * The field 'CNTRIBTN_UNIT_PTS' retrieve CNTRIBTN_UNIT_PTS of SQL 'queryAppliancePart'
     * The field 'PROD_PACK_TYPE_CODE' retrieve PROD_PACK_TYPE_CODE of SQL 'queryAppliancePart'
     * The field 'PROD_DISTRIBTN_CODE' retrieve PROD_DISTRIBTN_CODE of SQL 'queryAppliancePart'
     * The field 'REVN_STREAM_CODE' retrieve REVN_STREAM_CODE of SQL 'queryAppliancePart'
     * The field 'CTRCT_PROG_CODE' retrieve CTRCT_PROG_CODE of SQL 'queryAppliancePart'
     */
    public List<String> getFctPartById()
    {
        List<String> partIds = new ArrayList<String>();

        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String sql = prop.getProperty("queryFctPartById");
        try
        {
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();
            StringBuilder dataInfo = null;
            while (rs.next())
            {
                dataInfo = new StringBuilder("{");
                dataInfo.append("'WWIDE_PROD_CODE':'").append(rs.getString(1)).append("',");
                dataInfo.append("'WWIDE_PROD_CODE_DSCR':'").append(rs.getString(2)).append("',");
                dataInfo.append("'PART_NUM':'").append(rs.getString(3)).append("',");
                dataInfo.append("'PART_DSCR_LONG':'").append(rs.getString(4)).append("',");
                dataInfo.append("'PRICE_LEVEL_A':'").append(rs.getString(5)).append("',");
                dataInfo.append("'PRICE_END_DATE':'").append(rs.getString(6)).append("',");
                dataInfo.append("'PROD_EOL_DATE':'").append(rs.getString(7)).append("',");
                dataInfo.append("'PART_RSTRCT_FLAG':'").append(rs.getString(8)).append("',");
                dataInfo.append("'SW_PROD_BRAND_CODE':'").append(rs.getString(9)).append("',");
                dataInfo.append("'SAP_MATL_GRP_5_COND_CODE':'").append(rs.getString(10)).append("',");
                dataInfo.append("'PART_OBSLTE_FLAG':'").append(rs.getString(11)).append("',");
                dataInfo.append("'SAP_SALES_STAT_CODE':'").append(rs.getString(12)).append("'");
                dataInfo.append("}");
                partIds.add(dataInfo.toString());
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        finally
        {
            try
            {
                if (rs != null)
                {
                    rs.close();
                }
                if (pstmt != null)
                {
                    pstmt.close();
                }
            }
            catch (SQLException e)
            {
                e.printStackTrace();
            }

        }
        return partIds;
    }
    
    
    /**
     * Query for EC parts
     * 
     * @param quoteNum
     * @return a list that is a json object of name/value pairs.
     */
    public StringBuffer getECpartList(String quoteNum)
    {
        StringBuffer dataInfo = new StringBuffer();
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String sql = prop.getProperty("queryECPart");
        try
        {
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, quoteNum);

            rs = pstmt.executeQuery();
       
            while (rs.next())
            {
            	if(Pattern.compile("^[A-Za-z].*[[A-Za-z]]{1,3}$").matcher(rs.getString(1).trim()).find())
            	dataInfo=dataInfo.append(rs.getString(1).trim()).append(",");
            	//dataInfo=dataInfo.append(rs.getString(2)).append(",");
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        finally
        {
            try
            {
                if (rs != null)
                {
                    rs.close();
                }
                if (pstmt != null)
                {
                    pstmt.close();
                }
            }
            catch (SQLException e)
            {
                e.printStackTrace();
            }

        }
        return dataInfo;
    }
    
}
