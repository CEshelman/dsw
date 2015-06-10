/*
 * Created on 2007-5-16
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.ibm.dsw.quote.submittedquote.servlet;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ibm.dsw.quote.submittedquote.viewbean.AttachmentDownloadViewBean;
import com.ibm.ead4j.jade.bean.ResultBean;
import com.ibm.ws.util.Base64;

/**
 * @author helen
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class RetrieveAttachment extends HttpServlet implements Servlet {

    private static final long serialVersionUID = -3844296659397044012L;

	/* (non-Java-doc)
	 * @see javax.servlet.http.HttpServlet#HttpServlet()
	 */
	public RetrieveAttachment() {
		super();
	}
	
    /* (non-Java-doc)
	 * @see javax.servlet.http.HttpServlet#doGet(HttpServletRequest arg0, HttpServletResponse arg1)
	 */
	protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
	    File content = null;
	    BufferedInputStream in = null;
	    try
	    {
			ResultBean rb = (ResultBean) req.getAttribute("JADE_CURRENT_RESULT_KEY");
			AttachmentDownloadViewBean vb = (AttachmentDownloadViewBean) rb.getViewBean();
			if(vb.getMimeType()!=null && !"".equals(vb.getMimeType())){
			    res.setContentType(vb.getMimeType());
			}
			res.setHeader("Cache-Control", "max-age=0");
			String fileName = getFileName(req, vb.getFileName());
			res.setHeader("Content-Disposition","attachment; filename=\""+ fileName + "\"");
			System.out.println("fileName=" + fileName);
			content = vb.getContent();
			in = new BufferedInputStream(new FileInputStream(content));
			OutputStream os = res.getOutputStream();
			int ch = -1;
			while ( (ch = in.read()) != -1 )
			{
			    os.write(ch);
			}
			os.flush();
			
	    }
	    finally
	    {
	        if ( content != null )
	        {
	            content.delete();
	        }
	        if(in != null){
	        	in.close();
	        }
	        
	    }
	}
	
	protected String getFileName(HttpServletRequest request, String fileName) throws UnsupportedEncodingException
	{
		String agent = request.getHeader("USER-AGENT");   
	     if (null != agent && -1 != agent.indexOf("MSIE")) {   
	        return URLEncoder.encode(fileName, "UTF8");   
	    }else if (null != agent && -1 != agent.indexOf("Mozilla")) {   
	        return "=?UTF-8?B?"+(new String(Base64.encode(fileName.getBytes("UTF-8"))))+"?=";   
	    } else {   
	        return fileName;      
	    }   
	}

	/* (non-Java-doc)
	 * @see javax.servlet.http.HttpServlet#doPost(HttpServletRequest arg0, HttpServletResponse arg1)
	 */
	protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		doGet(req,res);
	}
}
