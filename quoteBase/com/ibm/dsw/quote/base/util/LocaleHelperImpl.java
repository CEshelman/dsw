package com.ibm.dsw.quote.base.util;

import is.util.PassportLocaleCookie;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Vector;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import com.ibm.ead4j.jade.session.JadeSession;
import com.ibm.ead4j.jade.session.JadeSessionFactory;
import com.ibm.ead4j.jade.util.LocaleHelper;
import com.ibm.ead4j.jade.util.LocaleHelperAbstractImpl;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;

/**
 * Copyright 2006 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * The <code>LocaleHelperImpl.java</code> class is sqo's implementation of the
 * locale helper
 * 
 * 
 * @author: chenzhh@cn.ibm.com
 * 
 * Creation date: Jan 26, 2007
 */
public class LocaleHelperImpl extends LocaleHelperAbstractImpl implements LocaleHelper {

    /**
     * LocaleHelperSwitchImpl constructor
     *  
     */
    public LocaleHelperImpl() {
        super();
    }

    /**
     * This method will returns the <code>Locale</code> set for the
     * application
     * 
     * @param req
     *            javax.servlet.http.HttpServletRequest
     * @return java.util.Locale
     * 
     * @since EAD4J 3.1.0
     */
    public Locale getLocale(HttpServletRequest req) {
        return getDefaultDSWLocale();
    }
//    public Locale getLocale(HttpServletRequest req) {
//        //try to get the locale from the request
//        Locale locale = null;
//
//        //check request
//        locale = checkRequest(req);
//
//        //check cookie
//        if (locale == null) {
//            locale = checkCookie(req);
//        }
//
//        //get the session
//        JadeSession session = JadeSessionFactory.getSession(req);
//
//        //check session
//        if (locale == null) {
//            locale = checkSession(req);
//        }
//
//        if (locale != null) {
//            locale = getDSWLocale(locale);
//        } else {
//            locale = getDefaultDSWLocale();
//        }
//
//        setLocale(locale, session);
//
//        return locale;
//    }

    private Locale checkCookie(HttpServletRequest request) {
        Locale locale = null;

        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (int x = 0; x < cookies.length; x++) {
                Cookie cookie = cookies[x];
                if (PassportLocaleCookie.hasSameName(cookie)) {
                    locale = PassportLocaleCookie.getLocaleFromValue(cookie.getValue());
                    break;
                }
            }
        }
        return locale;
    }

    /**
     * This method will returns an enumeration of valid <code>Locale</code>
     * objects that can be used for the application
     * 
     * @param req
     *            javax.servlet.http.HttpServletRequest
     * @return java.util.Enumeration - an enumeration of <code>Locale</code>
     *         objects
     * 
     * @since EAD4J 2.0.0
     */
    public Enumeration getLocales(HttpServletRequest req) {
        Vector locales = new Vector();
        Locale locale = null;
        //get the session
        JadeSession session = JadeSessionFactory.getSession(req);

        //first check request
        locale = checkRequest(req);

        if (locale != null) {
            locales.add(locale);
            setLocale(locale, session);
            return locales.elements();
        }

        //next check session
        locale = checkSession(req);

        if (locale != null) {
            locales.add(locale);
            setLocale(locale, session);
            return locales.elements();
        }

        //else check headers

        String acceptLanguage = req.getHeader("Accept-Language");
        // Short circuit with an empty enumeration if null header
        if (acceptLanguage == null) {
            locale = Locale.getDefault();
            Vector v = new Vector();
            v.addElement(locale);
            setLocale(locale, session);
            return v.elements();
        }

        Map languages = new HashMap();
        Vector quality = new Vector();
        processAcceptLanguage(acceptLanguage, languages, quality);

        if (languages.size() == 0) {
            locale = Locale.getDefault();
            Vector v = new Vector();
            v.addElement(locale);
            setLocale(locale, session);
            return v.elements();
        }

        List list = extractLocales(languages, quality);
        locale = (Locale) list.get(0);
        setLocale(locale, session);
        return ((Vector) list).elements();
    }

    /**
     * This method is used to intialize the <code>LocaleHelper</code>
     * 
     * @since EAD4J 2.0.0
     */
    public void initialize() {
        //nothing to initialize
    }

    public static Locale getDSWLocale(Locale locale) {
    	LogContext logCtx = LogContextFactory.singleton().getLogContext();
        try {
            String language = locale.getLanguage();
            String sLocale = locale.toString();
            
            if (sLocale.toLowerCase().equals("en_us") || language.toLowerCase().equals("en")) {
                return new Locale("en", "us");
            } else if (sLocale.toLowerCase().equals("es_mx") || language.toLowerCase().equals("mx")) {
                return new Locale("es", "mx");
            } else if (sLocale.toLowerCase().equals("fr_fr") || language.toLowerCase().equals("fr")) {
                return new Locale("fr", "fr");
            } else if (sLocale.toLowerCase().equals("de_de") || language.toLowerCase().equals("de")) {
                return new Locale("de", "de");
            } else if (sLocale.toLowerCase().equals("es_es") || language.toLowerCase().equals("es")) {
                return new Locale("es", "es");
            } else if (sLocale.toLowerCase().equals("it_it") || language.toLowerCase().equals("it")) {
                return new Locale("it", "it");
            } else if (sLocale.toLowerCase().equals("ja_jp") || language.toLowerCase().equals("ja")) {
                return new Locale("ja", "jp");
            } else if (sLocale.toLowerCase().equals("nl_nl") || language.toLowerCase().equals("nl")) {
                return new Locale("nl", "nl");
            } else if (sLocale.toLowerCase().equals("pt_br") || language.toLowerCase().equals("pt")) {
                return new Locale("pt", "br");
            } else if (sLocale.toLowerCase().equals("zh_cn") || language.toLowerCase().equals("zh")) {
                return new Locale("zh", "cn");
            }
        } catch (Throwable e) {
        	logCtx.error(LocaleHelperImpl.class, e.getMessage());
        }
        return getDefaultDSWLocale();
    }

    public static Locale getDefaultDSWLocale() {
        return Locale.US;
    }

}