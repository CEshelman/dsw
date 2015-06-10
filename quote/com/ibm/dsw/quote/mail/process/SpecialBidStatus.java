/**
 * SpecialBidStatus.java
 *
 * This file was auto-generated from WSDL
 * by the IBM Web services WSDL2Java emitter.
 * cf160638.12 v101006191000
 */

package com.ibm.dsw.quote.mail.process;

public class SpecialBidStatus  {
    private int _value_;
    private static java.util.HashMap _table_ = new java.util.HashMap();

    // Constructor
    protected SpecialBidStatus(int value) {
        _value_ = value;
        _table_.put(new java.lang.Integer(_value_),this);
    };

    public static final int _value1 = 1;
    public static final int _value2 = 2;
    public static final int _value3 = 3;
    public static final int _value4 = 4;
    public static final int _value5 = 5;
    public static final int _value6 = 7;
    public static final SpecialBidStatus value1 = new SpecialBidStatus(_value1);
    public static final SpecialBidStatus value2 = new SpecialBidStatus(_value2);
    public static final SpecialBidStatus value3 = new SpecialBidStatus(_value3);
    public static final SpecialBidStatus value4 = new SpecialBidStatus(_value4);
    public static final SpecialBidStatus value5 = new SpecialBidStatus(_value5);
    public static final SpecialBidStatus value6 = new SpecialBidStatus(_value6);
    public int getValue() { return _value_;}
    public static SpecialBidStatus fromValue(int value)
          throws java.lang.IllegalArgumentException {
        SpecialBidStatus enumeration = (SpecialBidStatus)
            _table_.get(new java.lang.Integer(value));
        if (enumeration==null) throw new java.lang.IllegalArgumentException();
        return enumeration;
    }
    public static SpecialBidStatus fromString(java.lang.String value)
          throws java.lang.IllegalArgumentException {
        try {
            return fromValue(java.lang.Integer.parseInt(value));
        } catch (Exception e) {
            throw new java.lang.IllegalArgumentException();
        }
    }
    public boolean equals(java.lang.Object obj) {return (obj == this);}
    public int hashCode() { return toString().hashCode();}
    public java.lang.String toString() { return java.lang.String.valueOf(_value_);}

}
