package com.stchome.saf.messages.get._1_2;

public class GetForecastRequestProxy implements com.stchome.saf.messages.get._1_2.GetForecastRequest {
  private String _endpoint = null;
  private com.stchome.saf.messages.get._1_2.GetForecastRequest getForecastRequest = null;
  
  public GetForecastRequestProxy() {
    _initGetForecastRequestProxy();
  }
  
  public GetForecastRequestProxy(String endpoint) {
    _endpoint = endpoint;
    _initGetForecastRequestProxy();
  }
  
  private void _initGetForecastRequestProxy() {
    try {
      getForecastRequest = (new com.stchome.saf.messages.get._1_2.GetForecastRequestServiceLocator()).getGetForecastRequestSoap11();
      if (getForecastRequest != null) {
        if (_endpoint != null)
          ((javax.xml.rpc.Stub)getForecastRequest)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
        else
          _endpoint = (String)((javax.xml.rpc.Stub)getForecastRequest)._getProperty("javax.xml.rpc.service.endpoint.address");
      }
      
    }
    catch (javax.xml.rpc.ServiceException serviceException) {}
  }
  
  public String getEndpoint() {
    return _endpoint;
  }
  
  public void setEndpoint(String endpoint) {
    _endpoint = endpoint;
    if (getForecastRequest != null)
      ((javax.xml.rpc.Stub)getForecastRequest)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
    
  }
  
  public com.stchome.saf.messages.get._1_2.GetForecastRequest getGetForecastRequest() {
    if (getForecastRequest == null)
      _initGetForecastRequestProxy();
    return getForecastRequest;
  }
  
  public com.stchome.saf.messages.get._1_2.ResponseDetailType[] getForecast(com.stchome.saf.messages.get._1_2.PersonDetailsType[] getForecastRequest0) throws java.rmi.RemoteException{
    if (getForecastRequest == null)
      _initGetForecastRequestProxy();
    return getForecastRequest.getForecast(getForecastRequest0);
  }
  
  
}