/**
 * ContraindicationType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.stchome.saf.messages.get._1_2;

public class ContraindicationType  implements java.io.Serializable {
    private java.lang.String vaccCode;

    private com.stchome.saf.common.VaccineCodeType codeType;  // attribute

    public ContraindicationType() {
    }

    public ContraindicationType(
           java.lang.String vaccCode,
           com.stchome.saf.common.VaccineCodeType codeType) {
           this.vaccCode = vaccCode;
           this.codeType = codeType;
    }


    /**
     * Gets the vaccCode value for this ContraindicationType.
     * 
     * @return vaccCode
     */
    public java.lang.String getVaccCode() {
        return vaccCode;
    }


    /**
     * Sets the vaccCode value for this ContraindicationType.
     * 
     * @param vaccCode
     */
    public void setVaccCode(java.lang.String vaccCode) {
        this.vaccCode = vaccCode;
    }


    /**
     * Gets the codeType value for this ContraindicationType.
     * 
     * @return codeType
     */
    public com.stchome.saf.common.VaccineCodeType getCodeType() {
        return codeType;
    }


    /**
     * Sets the codeType value for this ContraindicationType.
     * 
     * @param codeType
     */
    public void setCodeType(com.stchome.saf.common.VaccineCodeType codeType) {
        this.codeType = codeType;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof ContraindicationType)) return false;
        ContraindicationType other = (ContraindicationType) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.vaccCode==null && other.getVaccCode()==null) || 
             (this.vaccCode!=null &&
              this.vaccCode.equals(other.getVaccCode()))) &&
            ((this.codeType==null && other.getCodeType()==null) || 
             (this.codeType!=null &&
              this.codeType.equals(other.getCodeType())));
        __equalsCalc = null;
        return _equals;
    }

    private boolean __hashCodeCalc = false;
    public synchronized int hashCode() {
        if (__hashCodeCalc) {
            return 0;
        }
        __hashCodeCalc = true;
        int _hashCode = 1;
        if (getVaccCode() != null) {
            _hashCode += getVaccCode().hashCode();
        }
        if (getCodeType() != null) {
            _hashCode += getCodeType().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(ContraindicationType.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://saf.stchome.com/messages/get/1.2", "ContraindicationType"));
        org.apache.axis.description.AttributeDesc attrField = new org.apache.axis.description.AttributeDesc();
        attrField.setFieldName("codeType");
        attrField.setXmlName(new javax.xml.namespace.QName("", "codeType"));
        attrField.setXmlType(new javax.xml.namespace.QName("http://saf.stchome.com/common", "VaccineCodeType"));
        typeDesc.addFieldDesc(attrField);
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("vaccCode");
        elemField.setXmlName(new javax.xml.namespace.QName("http://saf.stchome.com/messages/get/1.2", "vaccCode"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
    }

    /**
     * Return type metadata object
     */
    public static org.apache.axis.description.TypeDesc getTypeDesc() {
        return typeDesc;
    }

    /**
     * Get Custom Serializer
     */
    public static org.apache.axis.encoding.Serializer getSerializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanSerializer(
            _javaType, _xmlType, typeDesc);
    }

    /**
     * Get Custom Deserializer
     */
    public static org.apache.axis.encoding.Deserializer getDeserializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanDeserializer(
            _javaType, _xmlType, typeDesc);
    }

}
