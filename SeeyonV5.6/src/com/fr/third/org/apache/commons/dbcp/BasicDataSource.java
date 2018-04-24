package com.fr.third.org.apache.commons.dbcp;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;

import javax.sql.DataSource;

import org.freehep.graphicsio.swf.SWFAction.TypeOf;

import com.fr.third.org.apache.commons.pool.KeyedObjectPoolFactory;
import com.fr.third.org.apache.commons.pool.impl.GenericKeyedObjectPoolFactory;
import com.fr.third.org.apache.commons.pool.impl.GenericObjectPool;

public class BasicDataSource
  implements DataSource
{
  static
  {
    DriverManager.getDrivers();
  }
  
  protected volatile boolean defaultAutoCommit = true;
  
  public boolean getDefaultAutoCommit()
  {
    return this.defaultAutoCommit;
  }
  
  public void setDefaultAutoCommit(boolean defaultAutoCommit)
  {
    this.defaultAutoCommit = defaultAutoCommit;
    this.restartNeeded = true;
  }
  
  protected transient Boolean defaultReadOnly = null;
  
  public boolean getDefaultReadOnly()
  {
    Boolean val = this.defaultReadOnly;
    if (val != null) {
      return val.booleanValue();
    }
    return false;
  }
  
  public void setDefaultReadOnly(boolean defaultReadOnly)
  {
    this.defaultReadOnly = (defaultReadOnly ? Boolean.TRUE : Boolean.FALSE);
    this.restartNeeded = true;
  }
  
  protected volatile int defaultTransactionIsolation = -1;
  
  public int getDefaultTransactionIsolation()
  {
    return this.defaultTransactionIsolation;
  }
  
  public void setDefaultTransactionIsolation(int defaultTransactionIsolation)
  {
    this.defaultTransactionIsolation = defaultTransactionIsolation;
    this.restartNeeded = true;
  }
  
  protected volatile String defaultCatalog = null;
  
  public String getDefaultCatalog()
  {
    return this.defaultCatalog;
  }
  
  public void setDefaultCatalog(String defaultCatalog)
  {
    if ((defaultCatalog != null) && (defaultCatalog.trim().length() > 0)) {
      this.defaultCatalog = defaultCatalog;
    } else {
      this.defaultCatalog = null;
    }
    this.restartNeeded = true;
  }
  
  protected String driverClassName = null;
  
  public synchronized String getDriverClassName()
  {
    return this.driverClassName;
  }
  
  public synchronized void setDriverClassName(String driverClassName)
  {
    if ((driverClassName != null) && (driverClassName.trim().length() > 0)) {
      this.driverClassName = driverClassName;
    } else {
      this.driverClassName = null;
    }
    this.restartNeeded = true;
  }
  
  protected ClassLoader driverClassLoader = null;
  
  public synchronized ClassLoader getDriverClassLoader()
  {
    return this.driverClassLoader;
  }
  
  public synchronized void setDriverClassLoader(ClassLoader driverClassLoader)
  {
    this.driverClassLoader = driverClassLoader;
    this.restartNeeded = true;
  }
  
  protected int maxActive = 8;
  
  public synchronized int getMaxActive()
  {
    return this.maxActive;
  }
  
  public synchronized void setMaxActive(int maxActive)
  {
    this.maxActive = maxActive;
    if (this.connectionPool != null) {
      this.connectionPool.setMaxActive(maxActive);
    }
  }
  
  protected int maxIdle = 8;
  
  public synchronized int getMaxIdle()
  {
    return this.maxIdle;
  }
  
  public synchronized void setMaxIdle(int maxIdle)
  {
    this.maxIdle = maxIdle;
    if (this.connectionPool != null) {
      this.connectionPool.setMaxIdle(maxIdle);
    }
  }
  
  protected int minIdle = 0;
  
  public synchronized int getMinIdle()
  {
    return this.minIdle;
  }
  
  public synchronized void setMinIdle(int minIdle)
  {
    this.minIdle = minIdle;
    if (this.connectionPool != null) {
      this.connectionPool.setMinIdle(minIdle);
    }
  }
  
  protected int initialSize = 0;
  
  public synchronized int getInitialSize()
  {
    return this.initialSize;
  }
  
  public synchronized void setInitialSize(int initialSize)
  {
    this.initialSize = initialSize;
    this.restartNeeded = true;
  }
  
  protected long maxWait = -1L;
  
  public synchronized long getMaxWait()
  {
    return this.maxWait;
  }
  
  public synchronized void setMaxWait(long maxWait)
  {
    this.maxWait = maxWait;
    if (this.connectionPool != null) {
      this.connectionPool.setMaxWait(maxWait);
    }
  }
  
  protected boolean poolPreparedStatements = false;
  
  public synchronized boolean isPoolPreparedStatements()
  {
    return this.poolPreparedStatements;
  }
  
  public synchronized void setPoolPreparedStatements(boolean poolingStatements)
  {
    this.poolPreparedStatements = poolingStatements;
    this.restartNeeded = true;
  }
  
  protected int maxOpenPreparedStatements = -1;
  
  public synchronized int getMaxOpenPreparedStatements()
  {
    return this.maxOpenPreparedStatements;
  }
  
  public synchronized void setMaxOpenPreparedStatements(int maxOpenStatements)
  {
    this.maxOpenPreparedStatements = maxOpenStatements;
    this.restartNeeded = true;
  }
  
  protected boolean testOnBorrow = true;
  
  public synchronized boolean getTestOnBorrow()
  {
    return this.testOnBorrow;
  }
  
  public synchronized void setTestOnBorrow(boolean testOnBorrow)
  {
    this.testOnBorrow = testOnBorrow;
    if (this.connectionPool != null) {
      this.connectionPool.setTestOnBorrow(testOnBorrow);
    }
  }
  
  protected boolean testOnReturn = false;
  
  public synchronized boolean getTestOnReturn()
  {
    return this.testOnReturn;
  }
  
  public synchronized void setTestOnReturn(boolean testOnReturn)
  {
    this.testOnReturn = testOnReturn;
    if (this.connectionPool != null) {
      this.connectionPool.setTestOnReturn(testOnReturn);
    }
  }
  
  protected long timeBetweenEvictionRunsMillis = -1L;
  
  public synchronized long getTimeBetweenEvictionRunsMillis()
  {
    return this.timeBetweenEvictionRunsMillis;
  }
  
  public synchronized void setTimeBetweenEvictionRunsMillis(long timeBetweenEvictionRunsMillis)
  {
    this.timeBetweenEvictionRunsMillis = timeBetweenEvictionRunsMillis;
    if (this.connectionPool != null) {
      this.connectionPool.setTimeBetweenEvictionRunsMillis(timeBetweenEvictionRunsMillis);
    }
  }
  
  protected int numTestsPerEvictionRun = 3;
  
  public synchronized int getNumTestsPerEvictionRun()
  {
    return this.numTestsPerEvictionRun;
  }
  
  public synchronized void setNumTestsPerEvictionRun(int numTestsPerEvictionRun)
  {
    this.numTestsPerEvictionRun = numTestsPerEvictionRun;
    if (this.connectionPool != null) {
      this.connectionPool.setNumTestsPerEvictionRun(numTestsPerEvictionRun);
    }
  }
  
  protected long minEvictableIdleTimeMillis = 1800000L;
  
  public synchronized long getMinEvictableIdleTimeMillis()
  {
    return this.minEvictableIdleTimeMillis;
  }
  
  public synchronized void setMinEvictableIdleTimeMillis(long minEvictableIdleTimeMillis)
  {
    this.minEvictableIdleTimeMillis = minEvictableIdleTimeMillis;
    if (this.connectionPool != null) {
      this.connectionPool.setMinEvictableIdleTimeMillis(minEvictableIdleTimeMillis);
    }
  }
  
  protected boolean testWhileIdle = false;
  
  public synchronized boolean getTestWhileIdle()
  {
    return this.testWhileIdle;
  }
  
  public synchronized void setTestWhileIdle(boolean testWhileIdle)
  {
    this.testWhileIdle = testWhileIdle;
    if (this.connectionPool != null) {
      this.connectionPool.setTestWhileIdle(testWhileIdle);
    }
  }
  
  public synchronized int getNumActive()
  {
    if (this.connectionPool != null) {
      return this.connectionPool.getNumActive();
    }
    return 0;
  }
  
  public synchronized int getNumIdle()
  {
    if (this.connectionPool != null) {
      return this.connectionPool.getNumIdle();
    }
    return 0;
  }
  
  protected volatile String password = null;
  
  public String getPassword()
  {
    return this.password;
  }
  
  public void setPassword(String password)
  {
    this.password = password;
    this.restartNeeded = true;
  }
  
  protected String url = null;
  
  public synchronized String getUrl()
  {
    return this.url;
  }
  
  public synchronized void setUrl(String url)
  {
    this.url = url;
    this.restartNeeded = true;
  }
  
  protected String username = null;
  
  public String getUsername()
  {
    return this.username;
  }
  
  public void setUsername(String username)
  {
    this.username = username;
    this.restartNeeded = true;
  }
  
  protected volatile String validationQuery = null;
  
  public String getValidationQuery()
  {
    return this.validationQuery;
  }
  
  public void setValidationQuery(String validationQuery)
  {
    if ((validationQuery != null) && (validationQuery.trim().length() > 0)) {
      this.validationQuery = validationQuery;
    } else {
      this.validationQuery = null;
    }
    this.restartNeeded = true;
  }
  
  protected volatile int validationQueryTimeout = -1;
  protected volatile List connectionInitSqls;
  
  public int getValidationQueryTimeout()
  {
    return this.validationQueryTimeout;
  }
  
  public void setValidationQueryTimeout(int timeout)
  {
    this.validationQueryTimeout = timeout;
    this.restartNeeded = true;
  }
  
  public Collection getConnectionInitSqls()
  {
    Collection result = this.connectionInitSqls;
    if (result == null) {
      return Collections.EMPTY_LIST;
    }
    return result;
  }
  
  public void setConnectionInitSqls(Collection connectionInitSqls)
  {
    if ((connectionInitSqls != null) && (connectionInitSqls.size() > 0))
    {
      ArrayList newVal = null;
      Iterator iterator = connectionInitSqls.iterator();
      while (iterator.hasNext())
      {
        Object o = iterator.next();
        if (o != null)
        {
          String s = o.toString();
          if (s.trim().length() > 0)
          {
            if (newVal == null) {
              newVal = new ArrayList();
            }
            newVal.add(s);
          }
        }
      }
      this.connectionInitSqls = newVal;
    }
    else
    {
      this.connectionInitSqls = null;
    }
    this.restartNeeded = true;
  }
  
  private boolean accessToUnderlyingConnectionAllowed = false;
  
  public synchronized boolean isAccessToUnderlyingConnectionAllowed()
  {
    return this.accessToUnderlyingConnectionAllowed;
  }
  
  public synchronized void setAccessToUnderlyingConnectionAllowed(boolean allow)
  {
    this.accessToUnderlyingConnectionAllowed = allow;
    this.restartNeeded = true;
  }
  
  private volatile boolean restartNeeded = false;
  
  private boolean isRestartNeeded()
  {
    return this.restartNeeded;
  }
  
  protected volatile GenericObjectPool connectionPool = null;
  protected Properties connectionProperties = new Properties();
  protected volatile DataSource dataSource = null;
  protected PrintWriter logWriter = new PrintWriter(System.out);
  private AbandonedConfig abandonedConfig;
  protected boolean closed;
  
  public Connection getConnection()
    throws SQLException
  {
    return createDataSource().getConnection();
  }
  
  public Connection getConnection(String user, String pass)
    throws SQLException
  {
    throw new UnsupportedOperationException("Not supported by BasicDataSource");
  }
  
  public int getLoginTimeout()
    throws SQLException
  {
    throw new UnsupportedOperationException("Not supported by BasicDataSource");
  }
  
  public PrintWriter getLogWriter()
    throws SQLException
  {
    return createDataSource().getLogWriter();
  }
  
  public void setLoginTimeout(int loginTimeout)
    throws SQLException
  {
    throw new UnsupportedOperationException("Not supported by BasicDataSource");
  }
  
  public void setLogWriter(PrintWriter logWriter)
    throws SQLException
  {
    createDataSource().setLogWriter(logWriter);
    this.logWriter = logWriter;
  }
  
  public boolean getRemoveAbandoned()
  {
    if (this.abandonedConfig != null) {
      return this.abandonedConfig.getRemoveAbandoned();
    }
    return false;
  }
  
  public void setRemoveAbandoned(boolean removeAbandoned)
  {
    if (this.abandonedConfig == null) {
      this.abandonedConfig = new AbandonedConfig();
    }
    this.abandonedConfig.setRemoveAbandoned(removeAbandoned);
    this.restartNeeded = true;
  }
  
  public int getRemoveAbandonedTimeout()
  {
    if (this.abandonedConfig != null) {
      return this.abandonedConfig.getRemoveAbandonedTimeout();
    }
    return 300;
  }
  
  public void setRemoveAbandonedTimeout(int removeAbandonedTimeout)
  {
    if (this.abandonedConfig == null) {
      this.abandonedConfig = new AbandonedConfig();
    }
    this.abandonedConfig.setRemoveAbandonedTimeout(removeAbandonedTimeout);
    this.restartNeeded = true;
  }
  
  public boolean getLogAbandoned()
  {
    if (this.abandonedConfig != null) {
      return this.abandonedConfig.getLogAbandoned();
    }
    return false;
  }
  
  public void setLogAbandoned(boolean logAbandoned)
  {
    if (this.abandonedConfig == null) {
      this.abandonedConfig = new AbandonedConfig();
    }
    this.abandonedConfig.setLogAbandoned(logAbandoned);
    this.restartNeeded = true;
  }
  
  public void addConnectionProperty(String name, String value)
  {
    this.connectionProperties.put(name, value);
    this.restartNeeded = true;
  }
  
  public void removeConnectionProperty(String name)
  {
    this.connectionProperties.remove(name);
    this.restartNeeded = true;
  }
  
  public void setConnectionProperties(String connectionProperties)
  {
    if (connectionProperties == null) {
      throw new NullPointerException("connectionProperties is null");
    }
    String[] entries = connectionProperties.split(";");
    Properties properties = new Properties();
    for (int i = 0; i < entries.length; i++)
    {
      String entry = entries[i];
      if (entry.length() > 0)
      {
        int index = entry.indexOf('=');
        if (index > 0)
        {
          String name = entry.substring(0, index);
          String value = entry.substring(index + 1);
          properties.setProperty(name, value);
        }
        else
        {
          properties.setProperty(entry, "");
        }
      }
    }
    this.connectionProperties = properties;
    this.restartNeeded = true;
  }
  
  public synchronized void close()
    throws SQLException
  {
    this.closed = true;
    GenericObjectPool oldpool = this.connectionPool;
    this.connectionPool = null;
    this.dataSource = null;
    try
    {
      if (oldpool != null) {
        oldpool.close();
      }
    }
    catch (SQLException e)
    {
      throw e;
    }
    catch (RuntimeException e)
    {
      throw e;
    }
    catch (Exception e)
    {
      throw new SQLNestedException("Cannot close connection pool", e);
    }
  }
  
  public synchronized boolean isClosed()
  {
    return this.closed;
  }
  
  protected synchronized DataSource createDataSource()
    throws SQLException
  {
    if (this.closed) {
      throw new SQLException("Data source is closed");
    }
    if (this.dataSource != null) {
      return this.dataSource;
    }
    ConnectionFactory driverConnectionFactory = createConnectionFactory();
    
    createConnectionPool();
    
    GenericKeyedObjectPoolFactory statementPoolFactory = null;
    if (isPoolPreparedStatements()) {
      statementPoolFactory = new GenericKeyedObjectPoolFactory(null, -1, (byte)0, 0L, 1, this.maxOpenPreparedStatements);
    }
    createPoolableConnectionFactory(driverConnectionFactory, statementPoolFactory, this.abandonedConfig);
    
    createDataSourceInstance();
    try
    {
      for (int i = 0; i < this.initialSize; i++) {
        this.connectionPool.addObject();
      }
    }
    catch (Exception e)
    {
      throw new SQLNestedException("Error preloading the connection pool", e);
    }
    return this.dataSource;
  }
  
  protected ConnectionFactory createConnectionFactory()
    throws SQLException
  {
    Class driverFromCCL = null;
    if (this.driverClassName != null) {
      try
      {
        try
        {
          if (this.driverClassLoader == null) {
            Class.forName(this.driverClassName);
          } else {
            Class.forName(this.driverClassName, true, this.driverClassLoader);
          }
        }
        catch (ClassNotFoundException cnfe)
        {
          driverFromCCL = Thread.currentThread().getContextClassLoader().loadClass(this.driverClassName);
        }
      }
      catch (Throwable t)
      {
        String message = "Cannot load JDBC driver class '" + this.driverClassName + "'";
        
        this.logWriter.println(message);
        t.printStackTrace(this.logWriter);
        throw new SQLNestedException(message, t);
      }
    }
    Driver driver = null;
    try
    {
      if (driverFromCCL == null)
      {
        driver = DriverManager.getDriver(this.url);
      }
      else
      {
        driver = (Driver)driverFromCCL.newInstance();
        if (!driver.acceptsURL(this.url)) {
          throw new SQLException("No suitable driver", "08001");
        }
      }
    }
    catch (Throwable t)
    {
      String message = "Cannot create JDBC driver of class '" + (this.driverClassName != null ? this.driverClassName : "") + "' for connect URL '" + this.url + "'";
      
      this.logWriter.println(message);
      t.printStackTrace(this.logWriter);
      throw new SQLNestedException(message, t);
    }
    if (this.validationQuery == null)
    {
      setTestOnBorrow(false);
      setTestOnReturn(false);
      setTestWhileIdle(false);
    }
    String user = this.username;
    if (user != null) {
      this.connectionProperties.put("user", user);
    } else {
      log("DBCP DataSource configured without a 'username'");
    }
    String pwd = this.password;
    if (pwd != null) {
      this.connectionProperties.put("password", pwd);
    } else {
      log("DBCP DataSource configured without a 'password'");
    }
    ConnectionFactory driverConnectionFactory = new DriverConnectionFactory(driver, this.url, this.connectionProperties);
    return driverConnectionFactory;
  }
  
  protected void createConnectionPool()
  {
    GenericObjectPool gop;
    if ((this.abandonedConfig != null) && (this.abandonedConfig.getRemoveAbandoned())) {
      gop = new AbandonedObjectPool(null, this.abandonedConfig);
    } else {
      gop = new GenericObjectPool();
    }
    gop.setMaxActive(this.maxActive);
    gop.setMaxIdle(this.maxIdle);
    gop.setMinIdle(this.minIdle);
    gop.setMaxWait(this.maxWait);
    gop.setTestOnBorrow(this.testOnBorrow);
    gop.setTestOnReturn(this.testOnReturn);
    gop.setTimeBetweenEvictionRunsMillis(this.timeBetweenEvictionRunsMillis);
    gop.setNumTestsPerEvictionRun(this.numTestsPerEvictionRun);
    gop.setMinEvictableIdleTimeMillis(this.minEvictableIdleTimeMillis);
    gop.setTestWhileIdle(this.testWhileIdle);
    this.connectionPool = gop;
  }
  
  protected void createDataSourceInstance()
    throws SQLException
  {
    PoolingDataSource pds = new PoolingDataSource(this.connectionPool);
    pds.setAccessToUnderlyingConnectionAllowed(isAccessToUnderlyingConnectionAllowed());
    pds.setLogWriter(this.logWriter);
    this.dataSource = pds;
  }
  
  protected void createPoolableConnectionFactory(ConnectionFactory driverConnectionFactory, KeyedObjectPoolFactory statementPoolFactory, AbandonedConfig configuration)
    throws SQLException
  {
    PoolableConnectionFactory connectionFactory = null;
    try
    {
      connectionFactory = new PoolableConnectionFactory(driverConnectionFactory, this.connectionPool, statementPoolFactory, this.validationQuery, this.validationQueryTimeout, this.connectionInitSqls, this.defaultReadOnly, this.defaultAutoCommit, this.defaultTransactionIsolation, this.defaultCatalog, configuration);
      
      validateConnectionFactory(connectionFactory);
    }
    catch (RuntimeException e)
    {
      throw e;
    }
    catch (Exception e)
    {
      throw new SQLNestedException("Cannot create PoolableConnectionFactory (" + e.getMessage() + ")", e);
    }
  }
  
  protected static void validateConnectionFactory(PoolableConnectionFactory connectionFactory)
    throws Exception
  {
    Connection conn = null;
    try
    {
      conn = (Connection)connectionFactory.makeObject();
      connectionFactory.activateObject(conn);
      connectionFactory.validateConnection(conn);
      connectionFactory.passivateObject(conn);
    }
    finally
    {
      connectionFactory.destroyObject(conn);
    }
  }
  
  private void restart()
  {
    try
    {
      close();
    }
    catch (SQLException e)
    {
      log("Could not restart DataSource, cause: " + e.getMessage());
    }
  }
  
  protected void log(String message)
  {
    if (this.logWriter != null) {
      this.logWriter.println(message);
    }
  }

@Override
public Logger getParentLogger() throws SQLFeatureNotSupportedException {
	// TODO Auto-generated method stub
	return null;
}

@Override
public boolean isWrapperFor(Class<?> iface) throws SQLException {
	// TODO Auto-generated method stub
	return false;
}

@Override
public <T> T unwrap(Class<T> iface) throws SQLException {
	// TODO Auto-generated method stub
	return null;
}
}
