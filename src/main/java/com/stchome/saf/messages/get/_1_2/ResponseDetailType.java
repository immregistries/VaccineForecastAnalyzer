/**
 * ResponseDetailType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.stchome.saf.messages.get._1_2;


/**
 * If there was any error in processing the person detail, the problem
 * or description will be
 *         embedded in the information element.
 */
public class ResponseDetailType  implements java.io.Serializable {
    private com.stchome.saf.messages.get._1_2.PersonDetailsType personDetails;

    private com.stchome.saf.messages.get._1_2.ForecastDetailsType[] forecastDetails;

    private java.lang.String information;

    public ResponseDetailType() {
    }

    public ResponseDetailType(
           com.stchome.saf.messages.get._1_2.PersonDetailsType personDetails,
           com.stchome.saf.messages.get._1_2.ForecastDetailsType[] forecastDetails,
           java.lang.String information) {
           this.personDetails = personDetails;
           this.forecastDetails = forecastDetails;
           this.information = information;
    }


    /**
     * Gets the personDetails value for this ResponseDetailType.
     * 
     * @return personDetails
     */
    public com.stchome.saf.messages.get._1_2.PersonDetailsType getPersonDetails() {
        return personDetails;
    }


    /**
     * Sets the personDetails value for this ResponseDetailType.
     * 
     * @param personDetails
     */
    public void setPersonDetails(com.stchome.saf.messages.get._1_2.PersonDetailsType personDetails) {
        this.personDetails = personDetails;
    }


    /**
     * Gets the forecastDetails value for this ResponseDetailType.
     * 
     * @return forecastDetails
     */
    public com.stchome.saf.messages.get._1_2.ForecastDetailsType[] getForecastDetails() {
        return forecastDetails;
    }


    /**
     * Sets the forecastDetails value for this ResponseDetailType.
     * 
     * @param forecastDetails
     */
    public void setForecastDetails(com.stchome.saf.messages.get._1_2.ForecastDetailsType[] forecastDetails) {
        this.forecastDetails = forecastDetails;
    }

    public com.stchome.saf.messages.get._1_2.ForecastDetailsType getForecastDetails(int i) {
        return this.forecastDetails[i];
    }

    public void setForecastDetails(int i, com.stchome.saf.messages.get._1_2.ForecastDetailsType _value) {
        this.forecastDetails[i] = _value;
    }


    /**
     * Gets the information value for this ResponseDetailType.
     * 
     * @return information
     */
    public java.lang.String getInformation() {
        return information;
    }


    /**
     * Sets the information value for this ResponseDetailType.
     * 
     * @param information
     */
    public void setInformation(java.lang.String information) {
        this.information = information;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof ResponseDetailType)) return false;
        ResponseDetailType other = (ResponseDetailType) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.personDetails==null && other.getPersonDetails()==null) || 
             (this.personDetails!=null &&
              this.personDetails.equals(other.getPersonDetails()))) &&
            ((this.forecastDetails==null && other.getForecastDetails()==null) || 
             (this.forecastDetails!=null &&
              java.util.Arrays.equals(this.forecastDetails, other.getForecastDetails()))) &&
            ((this.information==null && other.getInformation()==null) || 
             (this.information!=null &&
              this.information.equals(other.getInformation())));
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
        if (getPersonDetails() != null) {
            _hashCode += getPersonDetails().hashCode();
        }
        if (getForecastDetails() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getForecastDetails());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getForecastDetails(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getInformation() != null) {
            _hashCode += getInformation().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(ResponseDetailType.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://saf.stchome.com/messages/get/1.2", "ResponseDetailType"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("personDetails");
        elemField.setXmlName(new javax.xml.namespace.QName("http://saf.stchome.com/messages/get/1.2", "personDetails"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://saf.stchome.com/messages/get/1.2", "PersonDetailsType"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("forecastDetails");
        elemField.setXmlName(new javax.xml.namespace.QName("http://saf.stchome.com/messages/get/1.2", "forecastDetails"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://saf.stchome.com/messages/get/1.2", "ForecastDetailsType"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setMaxOccursUnbounded(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("information");
        elemField.setXmlName(new javax.xml.namespace.QName("http://saf.stchome.com/messages/get/1.2", "information"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
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
