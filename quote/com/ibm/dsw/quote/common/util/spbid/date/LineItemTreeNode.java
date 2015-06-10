
package com.ibm.dsw.quote.common.util.spbid.date;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.ibm.dsw.quote.common.domain.QuoteLineItem;
/**
 * Copyright 2006 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * The <code>LineItemTreeNode</code>  
 * 
 * 
 * @author <a href="liuxinlx@cn.ibm.com">Andy Liu </a> <br/>
 * 
 * Creation date: Mar 10, 2008
 */

public class LineItemTreeNode {
    
    LineItemTreeNode parent;
    QuoteLineItem lineItem;
    List children = new ArrayList();
    
   
    public List getAllLeafs(LineItemTreeNode node){
        
        List result = new ArrayList();
        if(node.children.size() == 0){
            result.add(node);            
        }
        else{
            for(int i=0;i<node.children.size();i++){
                LineItemTreeNode child = (LineItemTreeNode)node.children.get(i);
                result.addAll(getAllLeafs(child));
            }
        }
        return result;
    }
    public List getAllNodes(){
        return this.getAllNodes(this);
    }
    private List getAllNodes(LineItemTreeNode node){
        List result = new ArrayList();
        result.add(node.lineItem);
        
        if(node.children.size() == 0){
            return result;        
        }
        else{
            for(int i=0;i<node.children.size();i++){
                LineItemTreeNode child = (LineItemTreeNode)node.children.get(i);
                result.addAll(getAllNodes(child));
            }
        }
        return result;
        
    }
    
    public List getDeepestPath(){
        
        List leafs = this.getAllLeafs(this);
        
        List paths = new ArrayList();
        
        for(int i=0;i<leafs.size();i++){
            LineItemTreeNode node = (LineItemTreeNode)leafs.get(i);
            List subPaths = new ArrayList();
            subPaths.add(node);
            while(node.parent != null){
                node = node.parent;
                subPaths.add(node);
            }
            
            paths.add(reverseList(subPaths));
        }
        Collections.sort(paths,new Comparator(){
            public int compare(Object o1, Object o2){
                List l1 = (List)o1;
                List l2 = (List)o2;
                return l2.size() - l1.size();
            }
       });
        
        return (List)paths.get(0);
    }
    
    public List getDeepestPathWithLineItems(){
    
        List paths = getDeepestPath();
        List result = new ArrayList();
        for(int i=0;i<paths.size();i++){
            LineItemTreeNode node = (LineItemTreeNode)paths.get(i);
            result.add(node.lineItem);
        }
        return result;
    }
    
    private List reverseList(List paths){
        
        List result = new ArrayList();
        for(int i=(paths.size()-1); i>=0; i--){
            result.add(paths.get(i));
        }
        
        return result;
    }
    
    
}
