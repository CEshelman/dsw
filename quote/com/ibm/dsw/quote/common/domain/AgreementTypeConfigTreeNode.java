package com.ibm.dsw.quote.common.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.commons.lang.StringUtils;
import org.jdom.Element;

/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * This <code>AgreementTypeConfigTreeNode<code> class.
 *    
 * @author: zhengmr@cn.ibm.com
 * 
 * Creation date: Aug 12, 2009
 */

public class AgreementTypeConfigTreeNode {
    
    public static final String DEFAULT = "DEFAULT";
    public static final String RESULT = "result";
    
    protected Element element = null;
    protected String name = null;
    protected String value = null;
    protected Map valueMap = null;
    
    protected List subNodes = new ArrayList();
    protected AgreementTypeConfigTreeNode defaultSubNode = null;

    public AgreementTypeConfigTreeNode(Element element) {
        this.element = element;
        name = element.getName();
        if (RESULT.equalsIgnoreCase(name))
            value = element.getTextTrim();
        else
            value = StringUtils.deleteWhitespace(element.getAttributeValue("value"));
        valueMap = parseValue(value);
        
        List children = element.getChildren();
        for (int i = 0; children != null && i < children.size(); i++) {
            Element child = (Element) children.get(i);
            AgreementTypeConfigTreeNode subNode = new AgreementTypeConfigTreeNode(child);
            if (DEFAULT.equals(subNode.getValue()))
                defaultSubNode = subNode;
            else
                subNodes.add(subNode);
        }
    }
    
    public String getName() {
        return name;
    }
    
    public String getValue() {
        return value;
    }
    
    public String getResult(Map paramValueMap) {
        
        if (RESULT.equalsIgnoreCase(name)) {
            return getValue();
        }
        else {
            for (int i = 0; i < subNodes.size(); i++) {
                AgreementTypeConfigTreeNode subNode = (AgreementTypeConfigTreeNode) subNodes.get(i);
                if (subNode.isMatched(paramValueMap))
                    return subNode.getResult(paramValueMap);
            }
            
            if (defaultSubNode != null)
                return defaultSubNode.getResult(paramValueMap);
            else
                return "";
        }
    }
    
    public boolean isMatched(Map paramValueMap) {
        Object obj = paramValueMap.get(name);
        
        if (RESULT.equalsIgnoreCase(name)) {
            // always return true for result node.
            return true;
        }
        else if (obj == null) {
            return false;
        }
        else if (obj instanceof String) {
            String str = (String) obj;
            return valueMap.containsKey(str);
        }
        else {
            return false;
        }
    }
    
    protected Map parseValue(String value) {
        HashMap valueMap = new HashMap();
        
        if (StringUtils.isNotBlank(value)) {
            StringTokenizer st = new StringTokenizer(value, ",");
            
            while (st.hasMoreTokens()) {
                String token = st.nextToken();
                if (StringUtils.isNotBlank(token))
                    valueMap.put(token.trim(), token.trim());
            }
        }
        
        return valueMap;
    }

}
