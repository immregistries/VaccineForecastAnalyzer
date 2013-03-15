/**
 * VaccinationType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.stchome.saf.messages.get._1_2;


/**
 * Vaccination messages are ignored by the SAF on incoming vaccination
 * lists. 
 *         On responses, messages may be included if the vaccination
 * was determined to have a warning
 *         or was invalid.
 */
public class VaccinationType  implements java.io.Serializable {
    private java.lang.String vaccCode;

    private java.lang.String vaccDate;

    private java.math.BigDecimal doseSize;

    private com.stchome.saf.common.VaccMessageType[] message;

    private com.stchome.saf.common.VaccineCodeType codeType;  // attribute

    public VaccinationType() {
    }

    public VaccinationType(
           java.lang.String vaccCode,
           java.lang.String vaccDate,
           java.math.BigDecimal doseSize,
           com.stchome.saf.common.VaccMessageType[] message,
           com.stchome.saf.common.VaccineCodeType codeType) {
           this.vaccCode = vaccCode;
           this.vaccDate = vaccDate;
           this.doseSize = doseSize;
           this.message = message;
           this.codeType = codeType;
    }


    /**
     * Gets the vaccCode value for this VaccinationType.
     * 
     * @return vaccCode
     */
    public java.lang.String getVaccCode() {
        return vaccCode;
    }


    /**
     * Sets the vaccCode value for this VaccinationType.
     * 
     * @param vaccCode
     */
    public void setVaccCode(java.lang.String vaccCode) {
        this.vaccCode = vaccCode;
    }


    /**
     * Gets the vaccDate value for this VaccinationType.
     * 
     * @return vaccDate
     */
    public java.lang.String getVaccDate() {
        return vaccDate;
    }


    /**
     * Sets the vaccDate value for this VaccinationType.
     * 
     * @param vaccDate
     */
    public void setVaccDate(java.lang.String vaccDate) {
        this.vaccDate = vaccDate;
    }


    /**
     * Gets the doseSize value for this VaccinationType.
     * 
     * @return doseSize
     */
    public java.math.BigDecimal getDoseSize() {
        return doseSize;
    }


    /**
     * Sets the doseSize value for this VaccinationType.
     * 
     * @param doseSize
     */
    public void setDoseSize(java.math.BigDecimal doseSize) {
        this.doseSize = doseSize;
    }


    /**
     * Gets the message value for this VaccinationType.
     * 
     * @return message
     */
    public com.stchome.saf.common.VaccMessageType[] getMessage() {
        return message;
    }


    /**
     * Sets the message value for this VaccinationType.
     * 
     * @param message
     */
    public void setMessage(com.stchome.saf.common.VaccMessageType[] message) {
        this.message = message;
    }

    public com.stchome.saf.common.VaccMessageType getMessage(int i) {
        return this.message[i];
    }

    public void setMessage(int i, com.stchome.saf.common.VaccMessageType _value) {
        this.message[i] = _value;
    }


    /**
     * Gets the codeType value for this VaccinationType.
     * 
     * @return codeType
     */
    public com.stchome.saf.common.VaccineCodeType getCodeType() {
        return codeType;
    }


    /**
     * Sets the codeType value for this VaccinationType.
     * 
     * @param codeType
     */
    public void setCodeType(com.stchome.saf.common.VaccineCodeType codeType) {
        this.codeType = codeType;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof VaccinationType)) return false;
        VaccinationType other = (VaccinationType) obj;
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
            ((this.vaccDate==null && other.getVaccDate()==null) || 
             (this.vaccDate!=null &&
              this.vaccDate.equals(other.getVaccDate()))) &&
            ((this.doseSize==null && other.getDoseSize()==null) || 
             (this.doseSize!=null &&
              this.doseSize.equals(other.getDoseSize()))) &&
            ((this.message==null && other.getMessage()==null) || 
             (this.message!=null &&
              java.util.Arrays.equals(this.message, other.getMessage()))) &&
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
        if (getVaccDate() != null) {
            _hashCode += getVaccDate().hashCode();
        }
        if (getDoseSize() != null) {
            _hashCode += getDoseSize().hashCode();
        }
        if (getMessage() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getMessage());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getMessage(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getCodeType() != null) {
            _hashCode += getCodeType().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(VaccinationType.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://saf.stchome.com/messages/get/1.2", "VaccinationType"));
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
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("vaccDate");
        elemField.setXmlName(new javax.xml.namespace.QName("http://saf.stchome.com/messages/get/1.2", "vaccDate"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("doseSize");
        elemField.setXmlName(new javax.xml.namespace.QName("http://saf.stchome.com/messages/get/1.2", "doseSize"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "decimal"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("message");
        elemField.setXmlName(new javax.xml.namespace.QName("http://saf.stchome.com/messages/get/1.2", "message"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://saf.stchome.com/common", "VaccMessageType"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setMaxOccursUnbounded(true);
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
