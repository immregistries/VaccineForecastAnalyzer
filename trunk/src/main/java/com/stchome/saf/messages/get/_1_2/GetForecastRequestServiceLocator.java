/**
 * GetForecastRequestServiceLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.stchome.saf.messages.get._1_2;

public class GetForecastRequestServiceLocator extends org.apache.axis.client.Service implements com.stchome.saf.messages.get._1_2.GetForecastRequestService {

    public GetForecastRequestServiceLocator() {
    }


    public GetForecastRequestServiceLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public GetForecastRequestServiceLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for GetForecastRequestSoap11
    private java.lang.String GetForecastRequestSoap11_address = "http://10.0.5.70:8084/safdemo/soa/forecast";

    public java.lang.String getGetForecastRequestSoap11Address() {
        return GetForecastRequestSoap11_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String GetForecastRequestSoap11WSDDServiceName = "GetForecastRequestSoap11";

    public java.lang.String getGetForecastRequestSoap11WSDDServiceName() {
        return GetForecastRequestSoap11WSDDServiceName;
    }

    public void setGetForecastRequestSoap11WSDDServiceName(java.lang.String name) {
        GetForecastRequestSoap11WSDDServiceName = name;
    }

    public com.stchome.saf.messages.get._1_2.GetForecastRequest getGetForecastRequestSoap11() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(GetForecastRequestSoap11_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getGetForecastRequestSoap11(endpoint);
    }

    public com.stchome.saf.messages.get._1_2.GetForecastRequest getGetForecastRequestSoap11(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            com.stchome.saf.messages.get._1_2.GetForecastRequestSoap11Stub _stub = new com.stchome.saf.messages.get._1_2.GetForecastRequestSoap11Stub(portAddress, this);
            _stub.setPortName(getGetForecastRequestSoap11WSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setGetForecastRequestSoap11EndpointAddress(java.lang.String address) {
        GetForecastRequestSoap11_address = address;
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        try {
            if (com.stchome.saf.messages.get._1_2.GetForecastRequest.class.isAssignableFrom(serviceEndpointInterface)) {
                com.stchome.saf.messages.get._1_2.GetForecastRequestSoap11Stub _stub = new com.stchome.saf.messages.get._1_2.GetForecastRequestSoap11Stub(new java.net.URL(GetForecastRequestSoap11_address), this);
                _stub.setPortName(getGetForecastRequestSoap11WSDDServiceName());
                return _stub;
            }
        }
        catch (java.lang.Throwable t) {
            throw new javax.xml.rpc.ServiceException(t);
        }
        throw new javax.xml.rpc.ServiceException("There is no stub implementation for the interface:  " + (serviceEndpointInterface == null ? "null" : serviceEndpointInterface.getName()));
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(javax.xml.namespace.QName portName, Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        if (portName == null) {
            return getPort(serviceEndpointInterface);
        }
        java.lang.String inputPortName = portName.getLocalPart();
        if ("GetForecastRequestSoap11".equals(inputPortName)) {
            return getGetForecastRequestSoap11();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("http://saf.stchome.com/messages/get/1.2", "GetForecastRequestService");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("http://saf.stchome.com/messages/get/1.2", "GetForecastRequestSoap11"));
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(java.lang.String portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        
if ("GetForecastRequestSoap11".equals(portName)) {
            setGetForecastRequestSoap11EndpointAddress(address);
        }
        else 
{ // Unknown Port Name
            throw new javax.xml.rpc.ServiceException(" Cannot set Endpoint Address for Unknown Port" + portName);
        }
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(javax.xml.namespace.QName portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        setEndpointAddress(portName.getLocalPart(), address);
    }

}
