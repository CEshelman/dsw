package com.ibm.dsw.quote.common.domain;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;

/**
 * Copyright 2010 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * The <code>CompressedQuoteAttachment</code> class is to hold compressed attachement info
 * tab of a draft quote
 * 
 * @author: Fred (qinfengc@cn.ibm.com)
 * 
 * Creation date: Aug. 2, 2010
 */
public class CompressedQuoteAttachment extends QuoteAttachment{
    protected transient File compressedFile;
    protected long compressedSize;
    
    public File getCompressedFile() {
        return compressedFile;
    }
    public void setCompressedFile(File file) {
        compressedFile = file;
    }
    public long getCompressedSize() {
        return compressedSize;
    }
    public void setCompressedSize(long size) {
    	compressedSize = size;
    }
    public void compress() throws QuoteException
    {
    	LogContext logger = LogContextFactory.singleton().getLogContext();
    	ZipOutputStream out = null;
    	InputStream in = null;
        try
        {
	        String fileName = this.getAttchmt().getAbsolutePath();
	        compressedFile = new File(fileName + ".zip");
	        out = new ZipOutputStream(new FileOutputStream(compressedFile));
	        out.putNextEntry(new ZipEntry(this.getFileName()));
	        in = new BufferedInputStream(new FileInputStream(this.getAttchmt()));
	        int ch = -1;
	        while ( true )
	        {
	            ch = in.read();
	            if ( ch == -1 )
	            {
	                break;
	            }
	            out.write(ch);
	        }
	        out.flush();
	        compressedSize = compressedFile.length();
	        this.setCmprssdFileFlag(true);
        }
        catch ( IOException e )
        {
            throw new QuoteException(e);
        }
        finally
        {
        	if (null != in)
        	{
        		try {
					in.close();
				} catch (IOException e) {
					logger.error(this, e);
				}
        	}
        	if (null != out)
        	{
        		try {
					out.close();
				} catch (IOException e) {
					logger.error(this, e);
				}
        	}
        }
    }
}
