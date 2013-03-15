/**
 * ForecastDetailsType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.stchome.saf.messages.get._1_2;

public class ForecastDetailsType  implements java.io.Serializable {
    private java.lang.String familyCode;

    private int doseNumber;

    private java.lang.String maxAllowableDate;

    private java.lang.String minAllowableDate;

    private java.lang.String recommendedDate;

    private java.lang.String pastDueDate;

    public ForecastDetailsType() {
    }

    public ForecastDetailsType(
           java.lang.String familyCode,
           int doseNumber,
           java.lang.String maxAllowableDate,
           java.lang.String minAllowableDate,
           java.lang.String recommendedDate,
           java.lang.String pastDueDate) {
           this.familyCode = familyCode;
           this.doseNumber = doseNumber;
           this.maxAllowableDate = maxAllowableDate;
           this.minAllowableDate = minAllowableDate;
           this.recommendedDate = recommendedDate;
           this.pastDueDate = pastDueDate;
    }


    /**
     * Gets the familyCode value for this ForecastDetailsType.
     * 
     * @return familyCode
     */
    public java.lang.String getFamilyCode() {
        return familyCode;
    }


    /**
     * Sets the familyCode value for this ForecastDetailsType.
     * 
     * @param familyCode
     */
    public void setFamilyCode(java.lang.String familyCode) {
        this.familyCode = familyCode;
    }


    /**
     * Gets the doseNumber value for this ForecastDetailsType.
     * 
     * @return doseNumber
     */
    public int getDoseNumber() {
        return doseNumber;
    }


    /**
     * Sets the doseNumber value for this ForecastDetailsType.
     * 
     * @param doseNumber
     */
    public void setDoseNumber(int doseNumber) {
        this.doseNumber = doseNumber;
    }


    /**
     * Gets the maxAllowableDate value for this ForecastDetailsType.
     * 
     * @return maxAllowableDate
     */
    public java.lang.String getMaxAllowableDate() {
        return maxAllowableDate;
    }


    /**
     * Sets the maxAllowableDate value for this ForecastDetailsType.
     * 
     * @param maxAllowableDate
     */
    public void setMaxAllowableDate(java.lang.String maxAllowableDate) {
        this.maxAllowableDate = maxAllowableDate;
    }


    /**
     * Gets the minAllowableDate value for this ForecastDetailsType.
     * 
     * @return minAllowableDate
     */
    public java.lang.String getMinAllowableDate() {
        return minAllowableDate;
    }


    /**
     * Sets the minAllowableDate value for this ForecastDetailsType.
     * 
     * @param minAllowableDate
     */
    public void setMinAllowableDate(java.lang.String minAllowableDate) {
        this.minAllowableDate = minAllowableDate;
    }


    /**
     * Gets the recommendedDate value for this ForecastDetailsType.
     * 
     * @return recommendedDate
     */
    public java.lang.String getRecommendedDate() {
        return recommendedDate;
    }


    /**
     * Sets the recommendedDate value for this ForecastDetailsType.
     * 
     * @param recommendedDate
     */
    public void setRecommendedDate(java.lang.String recommendedDate) {
        this.recommendedDate = recommendedDate;
    }


    /**
     * Gets the pastDueDate value for this ForecastDetailsType.
     * 
     * @return pastDueDate
     */
    public java.lang.String getPastDueDate() {
        return pastDueDate;
    }


    /**
     * Sets the pastDueDate value for this ForecastDetailsType.
     * 
     * @param pastDueDate
     */
    public void setPastDueDate(java.lang.String pastDueDate) {
        this.pastDueDate = pastDueDate;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof ForecastDetailsType)) return false;
        ForecastDetailsType other = (ForecastDetailsType) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.familyCode==null && other.getFamilyCode()==null) || 
             (this.familyCode!=null &&
              this.familyCode.equals(other.getFamilyCode()))) &&
            this.doseNumber == other.getDoseNumber() &&
            ((this.maxAllowableDate==null && other.getMaxAllowableDate()==null) || 
             (this.maxAllowableDate!=null &&
              this.maxAllowableDate.equals(other.getMaxAllowableDate()))) &&
            ((this.minAllowableDate==null && other.getMinAllowableDate()==null) || 
             (this.minAllowableDate!=null &&
              this.minAllowableDate.equals(other.getMinAllowableDate()))) &&
            ((this.recommendedDate==null && other.getRecommendedDate()==null) || 
             (this.recommendedDate!=null &&
              this.recommendedDate.equals(other.getRecommendedDate()))) &&
            ((this.pastDueDate==null && other.getPastDueDate()==null) || 
             (this.pastDueDate!=null &&
              this.pastDueDate.equals(other.getPastDueDate())));
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
        if (getFamilyCode() != null) {
            _hashCode += getFamilyCode().hashCode();
        }
        _hashCode += getDoseNumber();
        if (getMaxAllowableDate() != null) {
            _hashCode += getMaxAllowableDate().hashCode();
        }
        if (getMinAllowableDate() != null) {
            _hashCode += getMinAllowableDate().hashCode();
        }
        if (getRecommendedDate() != null) {
            _hashCode += getRecommendedDate().hashCode();
        }
        if (getPastDueDate() != null) {
            _hashCode += getPastDueDate().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(ForecastDetailsType.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://saf.stchome.com/messages/get/1.2", "ForecastDetailsType"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("familyCode");
        elemField.setXmlName(new javax.xml.namespace.QName("http://saf.stchome.com/messages/get/1.2", "familyCode"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("doseNumber");
        elemField.setXmlName(new javax.xml.namespace.QName("http://saf.stchome.com/messages/get/1.2", "doseNumber"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("maxAllowableDate");
        elemField.setXmlName(new javax.xml.namespace.QName("http://saf.stchome.com/messages/get/1.2", "maxAllowableDate"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("minAllowableDate");
        elemField.setXmlName(new javax.xml.namespace.QName("http://saf.stchome.com/messages/get/1.2", "minAllowableDate"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("recommendedDate");
        elemField.setXmlName(new javax.xml.namespace.QName("http://saf.stchome.com/messages/get/1.2", "recommendedDate"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("pastDueDate");
        elemField.setXmlName(new javax.xml.namespace.QName("http://saf.stchome.com/messages/get/1.2", "pastDueDate"));
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
