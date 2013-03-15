/**
 * PersonDetailsType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.stchome.saf.messages.get._1_2;


/**
 * A person has a date of birth and possibly: gender, a list of vaccinations,
 * and contraindications
 *         Any information sent in the "forecast" part of a request for
 * forecast is ignored.
 */
public class PersonDetailsType  implements java.io.Serializable {
    private java.lang.String dateOfBirth;

    private com.stchome.saf.common.GenderType gender;

    private com.stchome.saf.messages.get._1_2.VaccinationType[] vaccination;

    private com.stchome.saf.messages.get._1_2.ContraindicationType[] contraindication;

    private java.lang.String personId;  // attribute

    public PersonDetailsType() {
    }

    public PersonDetailsType(
           java.lang.String dateOfBirth,
           com.stchome.saf.common.GenderType gender,
           com.stchome.saf.messages.get._1_2.VaccinationType[] vaccination,
           com.stchome.saf.messages.get._1_2.ContraindicationType[] contraindication,
           java.lang.String personId) {
           this.dateOfBirth = dateOfBirth;
           this.gender = gender;
           this.vaccination = vaccination;
           this.contraindication = contraindication;
           this.personId = personId;
    }


    /**
     * Gets the dateOfBirth value for this PersonDetailsType.
     * 
     * @return dateOfBirth
     */
    public java.lang.String getDateOfBirth() {
        return dateOfBirth;
    }


    /**
     * Sets the dateOfBirth value for this PersonDetailsType.
     * 
     * @param dateOfBirth
     */
    public void setDateOfBirth(java.lang.String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }


    /**
     * Gets the gender value for this PersonDetailsType.
     * 
     * @return gender
     */
    public com.stchome.saf.common.GenderType getGender() {
        return gender;
    }


    /**
     * Sets the gender value for this PersonDetailsType.
     * 
     * @param gender
     */
    public void setGender(com.stchome.saf.common.GenderType gender) {
        this.gender = gender;
    }


    /**
     * Gets the vaccination value for this PersonDetailsType.
     * 
     * @return vaccination
     */
    public com.stchome.saf.messages.get._1_2.VaccinationType[] getVaccination() {
        return vaccination;
    }


    /**
     * Sets the vaccination value for this PersonDetailsType.
     * 
     * @param vaccination
     */
    public void setVaccination(com.stchome.saf.messages.get._1_2.VaccinationType[] vaccination) {
        this.vaccination = vaccination;
    }

    public com.stchome.saf.messages.get._1_2.VaccinationType getVaccination(int i) {
        return this.vaccination[i];
    }

    public void setVaccination(int i, com.stchome.saf.messages.get._1_2.VaccinationType _value) {
        this.vaccination[i] = _value;
    }


    /**
     * Gets the contraindication value for this PersonDetailsType.
     * 
     * @return contraindication
     */
    public com.stchome.saf.messages.get._1_2.ContraindicationType[] getContraindication() {
        return contraindication;
    }


    /**
     * Sets the contraindication value for this PersonDetailsType.
     * 
     * @param contraindication
     */
    public void setContraindication(com.stchome.saf.messages.get._1_2.ContraindicationType[] contraindication) {
        this.contraindication = contraindication;
    }

    public com.stchome.saf.messages.get._1_2.ContraindicationType getContraindication(int i) {
        return this.contraindication[i];
    }

    public void setContraindication(int i, com.stchome.saf.messages.get._1_2.ContraindicationType _value) {
        this.contraindication[i] = _value;
    }


    /**
     * Gets the personId value for this PersonDetailsType.
     * 
     * @return personId
     */
    public java.lang.String getPersonId() {
        return personId;
    }


    /**
     * Sets the personId value for this PersonDetailsType.
     * 
     * @param personId
     */
    public void setPersonId(java.lang.String personId) {
        this.personId = personId;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof PersonDetailsType)) return false;
        PersonDetailsType other = (PersonDetailsType) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.dateOfBirth==null && other.getDateOfBirth()==null) || 
             (this.dateOfBirth!=null &&
              this.dateOfBirth.equals(other.getDateOfBirth()))) &&
            ((this.gender==null && other.getGender()==null) || 
             (this.gender!=null &&
              this.gender.equals(other.getGender()))) &&
            ((this.vaccination==null && other.getVaccination()==null) || 
             (this.vaccination!=null &&
              java.util.Arrays.equals(this.vaccination, other.getVaccination()))) &&
            ((this.contraindication==null && other.getContraindication()==null) || 
             (this.contraindication!=null &&
              java.util.Arrays.equals(this.contraindication, other.getContraindication()))) &&
            ((this.personId==null && other.getPersonId()==null) || 
             (this.personId!=null &&
              this.personId.equals(other.getPersonId())));
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
        if (getDateOfBirth() != null) {
            _hashCode += getDateOfBirth().hashCode();
        }
        if (getGender() != null) {
            _hashCode += getGender().hashCode();
        }
        if (getVaccination() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getVaccination());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getVaccination(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getContraindication() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getContraindication());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getContraindication(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getPersonId() != null) {
            _hashCode += getPersonId().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(PersonDetailsType.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://saf.stchome.com/messages/get/1.2", "PersonDetailsType"));
        org.apache.axis.description.AttributeDesc attrField = new org.apache.axis.description.AttributeDesc();
        attrField.setFieldName("personId");
        attrField.setXmlName(new javax.xml.namespace.QName("", "personId"));
        attrField.setXmlType(new javax.xml.namespace.QName("http://saf.stchome.com/common", "Len20StringType"));
        typeDesc.addFieldDesc(attrField);
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("dateOfBirth");
        elemField.setXmlName(new javax.xml.namespace.QName("http://saf.stchome.com/messages/get/1.2", "dateOfBirth"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("gender");
        elemField.setXmlName(new javax.xml.namespace.QName("http://saf.stchome.com/messages/get/1.2", "gender"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://saf.stchome.com/common", "GenderType"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("vaccination");
        elemField.setXmlName(new javax.xml.namespace.QName("http://saf.stchome.com/messages/get/1.2", "vaccination"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://saf.stchome.com/messages/get/1.2", "VaccinationType"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setMaxOccursUnbounded(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("contraindication");
        elemField.setXmlName(new javax.xml.namespace.QName("http://saf.stchome.com/messages/get/1.2", "contraindication"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://saf.stchome.com/messages/get/1.2", "ContraindicationType"));
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
