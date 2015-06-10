package com.ibm.dsw.quote.ps.action;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;

import com.ibm.ead4j.jade.util.Parameters;
import com.ibm.reuse.tree.controller.ITreeController;
import com.ibm.reuse.tree.controller.TreeControllerDefaultImpl;
/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * The <code>TreeControllerAdapter.java</code>
 * 
 * @author: chenzhh@cn.ibm.com
 * 
 * Created on: Mar 12, 2007
 */

public class TreeControllerAdapter extends TreeControllerDefaultImpl{

	protected Parameters parameters;
   
	@Override
	public String execute() {
		// TODO Auto-generated method stub
		return super.execute();
	}
    /**
     * 
     * @param parameters
     */
    public TreeControllerAdapter(Parameters parameters) {
        this(null, parameters);
    }
    /**
     * @param treeController
     */
    public TreeControllerAdapter(ITreeController treeController, Parameters parameters) {
        super(treeController);
        this.parameters = parameters;
    }

    /* (non-Javadoc)
     * @see com.ibm.reuse.tree.controller.ITreeController#setTreeStatus(java.lang.String, java.lang.String)
     */
    public void setTreeStatus(String treeName, String treeStatus) {
    }

    /* (non-Javadoc)
     * @see com.ibm.reuse.tree.controller.ITreeController#getTreeStatus(java.lang.String)
     */
    public String getTreeStatus(String treeName) {
        return "";
    }

    
    @Override
	public Object getParameter(String parameter) {
    	 return parameters.getParameter(parameter);
	}
	/* (non-Javadoc)
     * @see com.ibm.reuse.tree.controller.ITreeController#getParameterNames()
     */
    public Enumeration getParameterNames() {        
        Iterator it = parameters.getParameterNames();
        Hashtable paraNames = new Hashtable();
        while(it.hasNext()) {
            paraNames.put(it.next(), "");
        }
        return paraNames.keys();
    }
    
}
