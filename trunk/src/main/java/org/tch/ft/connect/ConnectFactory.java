package org.tch.ft.connect;

import java.util.List;

import org.tch.ft.model.ForecastItem;
import org.tch.ft.model.Service;
import org.tch.ft.model.Software;

public class ConnectFactory {
  
  public static ConnectorInterface createConnecter(Software software, List<ForecastItem> forecastItemList) throws Exception
  {
    if (software.getService() == Service.TCH)
    {
      return new TCHConnector(software, forecastItemList);
    }
    if (software.getService() == Service.WEB1)
    {
      
    }
    if (software.getService() == Service.SWP)
    {
      return new SWPConnector(software, forecastItemList);
    }
    if (software.getService() == Service.STC)
    {
      return new STCConnector(software, forecastItemList);
    }
    return null;
  }
}
