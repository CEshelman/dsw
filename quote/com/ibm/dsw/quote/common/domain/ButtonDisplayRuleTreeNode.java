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
 * This <code>ButtonDisplayRuleNode<code> class.
 *    
 * @author: zhengmr@cn.ibm.com
 * 
 * Creation date: 2008-5-6
 */

public class ButtonDisplayRuleTreeNode {
    
    public static final String DEFAULT = "DEFAULT";
    public static final String DISPLAY = "display";
    
    public static final String EQ = "EQ";
    public static final String MT = "MT";
    public static final String LT = "LT";
    public static final String NMT = "NMT";
    public static final String NLT = "NLT";

    protected Element element = null;
    protected String name = null;
    protected String value = null;
    protected String oper = null;
    protected Map valueMap = null;
    
    protected List subNodes = new ArrayList();
    protected ButtonDisplayRuleTreeNode defaultSubNode = null;
    
    public ButtonDisplayRuleTreeNode(Element element) {
        this.element = element;
        name = element.getName();
        if (DISPLAY.equalsIgnoreCase(name))
            value = element.getTextTrim();
        else
            value = StringUtils.deleteWhitespace(element.getAttributeValue("value"));
        oper = StringUtils.deleteWhitespace(element.getAttributeValue("oper"));
        valueMap = parseValue(value);
        
        List children = element.getChildren();
        for (int i = 0; children != null && i < children.size(); i++) {
            Element child = (Element) children.get(i);
            ButtonDisplayRuleTreeNode subNode = new ButtonDisplayRuleTreeNode(child);
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
    
    public boolean isDisplay(Map paramValueMap) {
        
        if (DISPLAY.equalsIgnoreCase(name)) {
            return "1".equals(value);
        }
        else {
            for (int i = 0; i < subNodes.size(); i++) {
                ButtonDisplayRuleTreeNode subNode = (ButtonDisplayRuleTreeNode) subNodes.get(i);
                if (subNode.isMatched(paramValueMap))
                    return subNode.isDisplay(paramValueMap);
            }
            
            if (defaultSubNode != null)
                return defaultSubNode.isDisplay(paramValueMap);
            else
                return false;
        }
    }
    
    public boolean isMatched(Map paramValueMap) {
        Object obj = paramValueMap.get(name);
        
        if (DISPLAY.equalsIgnoreCase(name)) {
            // always return true for result node.
            return true;
        }
        else if (obj == null) {
            return false;
        }
        else if (obj instanceof String) {
            boolean matched = true;
            String str = (String) obj;
            
            if (StringUtils.isBlank(oper)) {
                matched = valueMap.containsKey(str);
            }
            else {
                try {
                    double d1 = Double.parseDouble(str);
                    double d2 = Double.parseDouble(value);
                    
                    if (EQ.equalsIgnoreCase(oper)) {
                        matched = (d1 == d2);
                    }
                    else if (MT.equalsIgnoreCase(oper)) {
                        matched = (d1 > d2);
                    }
                    else if (LT.equalsIgnoreCase(oper)) {
                        matched = (d1 < d2);
                    }
                    else if (NMT.equalsIgnoreCase(oper)) {
                        matched = (d1 <= d2);
                    }
                    else if (NLT.equalsIgnoreCase(oper)) {
                        matched = (d1 >= d2);
                    }
                    else {
                        matched = valueMap.containsKey(str);
                    }
                    
                } catch (NumberFormatException e) {
                    matched = false;
                }
            }
            
            return matched;
        }
        else if (obj instanceof List) {
            List list = (List) obj;
            boolean matched = true;
            
            for (int i = 0; i < list.size(); i++) {
                Object val = list.get(i);
                if (!valueMap.containsKey(val))
                    matched = false;
            }
            return matched;
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
