package com.ibm.dsw.quote.base.util;

import java.io.Serializable;
import java.net.InetAddress;

/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * This <code>UUIDHelper<code> class.
 *    
 * @author: wnan@cn.ibm.com
 * 
 * Creation date: May 11, 2007
 */

public class UUIDHelper {

    public UUIDHelper() {
        sep = "";
    }

    protected int getJVM() {
        return JVM;
    }

    protected short getCount() {
        synchronized (UUIDHelper.class) {
            if (counter < 0)
                counter = 0;
            return counter++;
        }
    }

    protected int getIP() {
        return IP;
    }

    protected short getHiTime() {
        return (short) (int) (System.currentTimeMillis() >>> 32);
    }

    protected int getLoTime() {
        return (int) System.currentTimeMillis();
    }

    protected String format(int intval) {
        String formatted = Integer.toHexString(intval);
        StringBuffer buf = new StringBuffer("00000000");
        buf.replace(8 - formatted.length(), 8, formatted);
        return buf.toString();
    }

    protected String format(short shortval) {
        String formatted = Integer.toHexString(shortval);
        StringBuffer buf = new StringBuffer("0000");
        buf.replace(4 - formatted.length(), 4, formatted);
        return buf.toString();
    }

    public Serializable generate() {
        return (new StringBuffer(36)).append(format(getIP())).append(sep).append(format(getJVM())).append(sep).append(
                format(getHiTime())).append(sep).append(format(getLoTime())).append(sep).append(format(getCount()))
                .toString();
    }

    public static void main(String args[]) {
        System.out.println((new UUIDHelper()).generate());
    }

    private static final int IP;

    private static short counter = 0;

    private static final int JVM = (int) (System.currentTimeMillis() >>> 8);

    private String sep;

    private static int toInt(byte[] bytes) {
        int result = 0;
        for (int i = 0; i < 4; i++) {
            result = (result << 8) - Byte.MIN_VALUE + (int) bytes[i];
        }
        return result;
    }

    static {
        int ipadd;
        try {
            ipadd = toInt(InetAddress.getLocalHost().getAddress());
        } catch (Exception e) {
            ipadd = 0;
        }
        IP = ipadd;
    }
    
    public static Serializable getUUID(){
        return new UUIDHelper().generate();
    }
}