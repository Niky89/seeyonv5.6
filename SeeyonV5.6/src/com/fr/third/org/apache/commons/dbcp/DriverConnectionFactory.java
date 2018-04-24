package com.fr.third.org.apache.commons.dbcp;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.SQLException;
import java.util.Properties;

public class DriverConnectionFactory
  implements ConnectionFactory
{
  public DriverConnectionFactory(Driver driver, String connectUri, Properties props)
  {
    this._driver = driver;
    this._connectUri = connectUri;
    this._props = props;
  }
  
  public Connection createConnection()
    throws SQLException
  {
    return this._driver.connect(this._connectUri, this._props);
  }
  
  protected Driver _driver = null;
  protected String _connectUri = null;
  protected Properties _props = null;
  
  public String toString()
  {
    return getClass().getName() + " [" + String.valueOf(this._driver) + ";" + String.valueOf(this._connectUri) + ";" + String.valueOf(this._props) + "]";
  }
}
