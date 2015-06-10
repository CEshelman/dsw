package com.ibm.dsw.quote.common.domain;
import java.util.List;
/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * This <code>FCTSpecialBidTypesDesc<code> class.
 *    
 * @author: qinfengc@cn.ibm.com
 * 
 * Creation date: 2008-5-6
 */

public class FCTSpecialBidTypesDesc {
    
    /**
     * @return Returns the lobs.
     */
    public String getLobs() {
        return lobs;
    }
    /**
     * @param lobs The lobs to set.
     */
    public void setLobs(String lobs) {
        this.lobs = lobs;
    }
    /**
     * @return Returns the types.
     */
    public String getTypes() {
        return types;
    }
    /**
     * @param types The types to set.
     */
    public void setTypes(String types) {
        this.types = types;
    }
    private String migrations;
    private String inputTypes;
    private String value;
    private List descItemList;
    private String lobs;
    private String types;
    
    public boolean contain(String lob, String type, String migration, String inputType)
    {
        return this.containLob(lob) && this.containType(type) 
        	&& this.containMigration(migration) && this.containInputType(inputType);
    }
    
    protected boolean containInputType(String inputType)
    {
        if ( inputTypes == null )
        {
            return false;
        }
        return inputTypes.indexOf(inputType) != -1;
    }
    
    protected boolean containLob(String lob)
    {
        if ( lobs == null || lobs.equals("") )
        {
            return false;
        }
        String[] arr = lobs.split(",");
        for ( int i = 0; i < arr.length; i++ )
        {
            if ( arr[i].trim().equalsIgnoreCase(lob) )
            {
                return true;
            }
        }
        return false;
    }
    
    protected boolean containType(String type)
    {
        if ( types == null )
        {
            return false;
        }
        return types.indexOf(type) != -1;
    }
    
    protected boolean containMigration(String migration)
    {
        if ( migrations == null )
        {
            return false;
        }
        return migrations.indexOf(migration) != -1;
    }
    
    /**
     * @return Returns the descItemList.
     */
    public List getDescItemList() {
        return descItemList;
    }
    /**
     * @param descItemList The descItemList to set.
     */
    public void setDescItemList(List descItemList) {
        this.descItemList = descItemList;
    }
    /**
     * @return Returns the inputType.
     */
    public String getInputTypes() {
        return inputTypes;
    }
    /**
     * @param inputType The inputType to set.
     */
    public void setInputTypes(String inputTypes) {
        this.inputTypes = inputTypes;
    }
   
    /**
     * @return Returns the migration.
     */
    public String getMigrations() {
        return migrations;
    }
    /**
     * @param migration The migration to set.
     */
    public void setMigrations(String migrations) {
        this.migrations = migrations;
    }
    
    /**
     * @return Returns the value.
     */
    public String getValue() {
        return value;
    }
    /**
     * @param value The value to set.
     */
    public void setValue(String value) {
        this.value = value;
    }
    
    public String toString()
    {
        return lobs + ":" + types + ":" + migrations + ":" + inputTypes + ":" + value;
    }
}
