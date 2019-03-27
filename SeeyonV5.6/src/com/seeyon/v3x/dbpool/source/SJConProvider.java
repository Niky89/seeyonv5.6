package com.seeyon.v3x.dbpool.source;

import com.seeyon.v3x.dbpool.util.LogUtil;
import com.seeyon.v3x.dbpool.util.PwdEncoder;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.util.Vector;
import javax.management.ObjectName;
import org.apache.commons.modeler.Registry;

public class SJConProvider
  extends SJConProviderConfig
{
  private long deadConnectionTimeout = 10800000L;
  
  public void setDeadConnectionTimeout(long time)
  {
    this.deadConnectionTimeout = time;
  }
  
  public long getDeadConnectionTimeout()
  {
    return this.deadConnectionTimeout;
  }
  
  public SJConProvider()
  {
    Properties fpp = readDefineProperties("com/seeyon/v3x/dbpool/source/SJConProvider.properties");
    if (fpp != null) {
      initPName(fpp);
    } else {
      initDefaultPName();
    }
  }
  
  private volatile boolean finited = false;
  
  public final boolean inited()
  {
    return this.finited;
  }
  
  public String getSQLdriver()
  {
    return this.SQLdriver;
  }
  
  public String getSQLUrl()
  {
    return this.SQLUrl;
  }
  
  public String getUserName()
  {
    return this.userName;
  }
  
  public String getPassWord()
  {
    return this.passWord;
  }
  
  public int getMinCount()
  {
    return this.minCount;
  }
  
  public int getMaxCount()
  {
    return this.maxCount;
  }
  
  public int getDoReconnectTime()
  {
    this.doReconnectTime += 1;
    if (this.doReconnectTime >= 180) {
      this.doReconnectTime = 0;
    }
    return this.doReconnectTime;
  }
  
  private final void putDefaultConfig()
  {
    this.minCount = 0;
    this.maxCount = 2000;
  }
  
  private final void readConfig(Properties props)
  {
    putDefaultConfig();
    if (props == null) {
      return;
    }
    this.SQLdriver = props.getProperty(this.driverPName);
    this.SQLUrl = props.getProperty(this.SQLUrlPName);
    this.userName = props.getProperty(this.userNamePName);
    this.passWord = PwdEncoder.decode(props.getProperty(this.passWordPName));
    
    String ftemp = props.getProperty(this.minCountPName);
    if (ftemp != null) {
      this.minCount = Integer.parseInt(ftemp);
    }
    ftemp = props.getProperty(this.maxCountPName);
    if (ftemp != null) {
      this.maxCount = Integer.parseInt(ftemp);
    }
    ftemp = props.getProperty(this.tryCountPName);
    if (ftemp != null) {
      this.tryCount = Integer.parseInt(ftemp);
    }
    ftemp = props.getProperty(this.tryWaitPName);
    if (ftemp != null) {
      this.tryWait = Integer.parseInt(ftemp);
    }
    LogUtil.debug("配置：tryCount:" + this.tryCount + "; tryWait:" + this.tryWait);
  }
  
  private final boolean checkConfig()
  {
    try
    {
      Class.forName(this.SQLdriver).newInstance();
    }
    catch (Exception e)
    {
      e.printStackTrace();
      return false;
    }
//    if (checkWeakPwd())
//    {
//      String msg = "\r\n*****************************************************\r\n*\r\n*  数据库密码存在弱口令风险，系统无法启动\r\n*  密码要求：长度6位以上，数字字母混合组成\r\n*\r\n*  连接池名称：" + this.name + "\r\n" + "*  数据库驱动：" + getSQLdriver() + "\r\n" + "*  数据库URL： " + getSQLUrl() + "\r\n" + "*  数据库账号：" + getUserName() + "\r\n" + "*\r\n" + "*****************************************************";
//      
//      LogUtil.error(msg);
//      return false;
//    }
    int max = 10;
    for (int i = 0; i < max; i++) {
      try
      {
        testLink(getUserName(), getPassWord());
        return true;
      }
      catch (Throwable e1)
      {
        if (i == max - 1)
        {
          LogUtil.error("第" + (i + 1) + "次尝试连接失败,无法连接数据库,系统无法启动.");
          e1.printStackTrace();
          return false;
        }
        LogUtil.warn("第" + (i + 1) + "次尝试连接失败,等待10s后重试(还有" + (max - i - 1) + "次重试机会)...");
        try
        {
          Thread.sleep(10000L);
        }
        catch (Throwable localThrowable1) {}
      }
    }
    return false;
  }
  
  static Set<String> WeakPwds = new HashSet();
  private LinkedList<ConInfo> fNoUseList;
  private Vector<ConInfo> finUseList;
  
  static
  {
    WeakPwds.add("123456");
    WeakPwds.add("111111");
    WeakPwds.add("222222");
    WeakPwds.add("333333");
    WeakPwds.add("444444");
    WeakPwds.add("555555");
    WeakPwds.add("666666");
    WeakPwds.add("777777");
    WeakPwds.add("888888");
    WeakPwds.add("999999");
    WeakPwds.add("000000");
    WeakPwds.add("123123");
    WeakPwds.add("abc123");
  }
  
  private boolean checkWeakPwd()
  {
    String pwd = getPassWord();
    if ((pwd == null) || (pwd.length() < 6) || (WeakPwds.contains(pwd))) {
      return true;
    }
    return false;
  }
  
  public synchronized void init(String name, Properties props)
    throws SQLException
  {
    if (this.finited) {
      return;
    }
    this.name = name;
    readConfig(props);
    if (checkConfig())
    {
      initPool();
    }
    else
    {
      LogUtil.error("无法初始化数据库连接池，系统无法启动");
      try
      {
        Thread.sleep(10000L);
      }
      catch (Throwable localThrowable) {}
      System.exit(-1);
    }
    this.finited = true;
  }
  
  public synchronized void clearExceedConnInfos()
  {
    synchronized (this.mutex)
    {
      if (this.fNoUseList.size() > getMinCount())
      {
        int unintLength = this.fNoUseList.size() - getMinCount();
        for (int i = 0; i < unintLength; i++) {
          try
          {
            ConInfo ftemp = (ConInfo)this.fNoUseList.removeFirst();
            ftemp.uninit();
          }
          catch (Throwable localThrowable) {}
        }
      }
    }
    LogUtil.info("清理超支连接");
  }
  
  public synchronized void clearSlowConnInfos()
  {
    int c = 0;
    synchronized (this.mutex)
    {
      int unintLength = this.finUseList.size();
      for (int i = unintLength; i >= 0; i--) {
        try
        {
          ConInfo ftemp = (ConInfo)this.finUseList.get(i);
          long t = System.currentTimeMillis() - ftemp.getOpenConnectionTimestamp();
          if (t > getDeadConnectionTimeout())
          {
            ftemp.releaseConnection(ftemp.getCon());
            c++;
            LogUtil.info("强制终止执行了" + t / 1000L / 60L + "分的连接：" + ftemp.printStackTrace());
          }
        }
        catch (Throwable localThrowable) {}
      }
    }
    LogUtil.info("连接池自检：清理占用超过" + this.deadConnectionTimeout / 1000L / 60L + "分钟的连接: " + c + "个");
  }
  
  public synchronized int getNoUseCount()
  {
    return this.fNoUseList.size();
  }
  
  public List getUseList()
  {
    return this.finUseList;
  }
  
  protected int peakConnectionCount = 0;
  protected long peakConnectionTimestamp = System.currentTimeMillis();
  
  private synchronized void initPool()
    throws SQLException
  {
    if ((this.finUseList != null) || (this.fNoUseList != null)) {
      clearPool();
    }
    this.finUseList = new Vector(this.maxCount);
    this.fNoUseList = new LinkedList();
    int fcount = 1;
    for (int i = 0; i < getMinCount(); i++)
    {
      ConInfo ftemp = new ConInfo(this);
      ftemp.init();
      this.fNoUseList.add(ftemp);
      if (fcount % 10 == 0) {
        if (fcount == 10) {
          LogUtil.info("" + fcount);
        } else {
          LogUtil.info("," + fcount);
        }
      }
      fcount++;
    }
    LogUtil.info("连接池初始化完毕!");
  }
  
  private synchronized void clearPool()
  {
    LogUtil.info("关闭已建立的数据连接....");
    int flen = 0;
    if (this.finUseList != null) {
      flen = this.finUseList.size();
    }
    if (this.fNoUseList != null) {
      flen += this.fNoUseList.size();
    }
    int fcount = flen;
    if (this.finUseList != null)
    {
      for (int i = 0; i < this.finUseList.size(); i++)
      {
        ((ConInfo)this.finUseList.get(i)).uninit();
        if (fcount == flen) {
          LogUtil.info("" + fcount);
        } else {
          LogUtil.info("," + fcount);
        }
        fcount--;
      }
      this.finUseList.clear();
    }
    this.finUseList = null;
    if (this.fNoUseList != null)
    {
      for (int i = 0; i < this.fNoUseList.size(); i++)
      {
        ((ConInfo)this.fNoUseList.get(i)).uninit();
        if (fcount == flen) {
          LogUtil.info("" + fcount);
        } else {
          LogUtil.info("," + fcount);
        }
        fcount--;
      }
      this.fNoUseList.clear();
    }
    this.fNoUseList = null;
    LogUtil.info("");
    LogUtil.info("数据连接已关闭!");
  }
  
  private void testLink(String aUserName, String aPassword)
    throws Throwable
  {
    Connection ftemp = null;
    try
    {
      ftemp = DriverManager.getConnection(this.SQLUrl, aUserName, aPassword); return;
    }
    catch (Throwable ex)
    {
      throw ex;
    }
    finally
    {
      if (ftemp != null) {
        try
        {
          ftemp.close();
        }
        catch (Exception localException1) {}
      }
    }
  }
  
  SimpleDateFormat fformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
  
  public void printInfo(PrintWriter aWriter)
  {
    StringBuffer result = new StringBuffer();
    result.append("DriverName: ").append(getSQLdriver()).append("\r\n");
    result.append("ConnectURL: ").append(getSQLUrl()).append("\r\n");
    result.append("UserName: ").append(getUserName()).append("\r\n");
    result.append("Initialize connection count: ").append(getMinCount()).append("; Max connection count: ").append(getMaxCount()).append("\r\n");
    result.append("Current busy connections: ").append(this.finUseList.size());
    result.append("; Current spare connections: ").append(this.fNoUseList.size());
    result.append("; Peak connections: ").append(this.peakConnectionCount).append(" (").append(this.fformat.format(new Date(this.peakConnectionTimestamp))).append(")");
    result.append("\r\n");
    if (aWriter != null) {
      aWriter.write(result.toString());
    }
  }
  
  private void printInUseList()
  {
    for (int i = 0; i < this.finUseList.size(); i++)
    {
      ConInfo ftemp = (ConInfo)this.finUseList.get(i);
      if (ftemp.getCallPos() != null)
      {
        String tName = ftemp.getThreadName();
        LogUtil.debug("\"" + tName + "\" " + this.fformat.format(new Date(ftemp.getOpenConnectionTimestamp())), ftemp.getCallPos());
      }
    }
  }
  
  public void printInUseList(PrintWriter aWriter)
  {
    Map<String, Integer> threadCount = new HashMap();
    for (int i = 0; i < this.finUseList.size(); i++)
    {
      ConInfo ftemp = (ConInfo)this.finUseList.get(i);
      if (ftemp.getCallPos() != null)
      {
        String tName = ftemp.getThreadName();
        if (aWriter == null)
        {
          System.err.print("\"" + tName + "\" ");
          ftemp.getCallPos().printStackTrace();
        }
        else
        {
          aWriter.print("\"" + tName + "\" ");
          ftemp.getCallPos().printStackTrace(aWriter);
        }
        Integer c = (Integer)threadCount.get(tName);
        Integer localInteger1;
        if (c == null)
        {
          c = Integer.valueOf(1);
        }
        else
        {
          localInteger1 = c;Integer localInteger2 = c = Integer.valueOf(c.intValue() + 1);
        }
        threadCount.put(tName, c);
      }
    }
    for (Map.Entry<String, Integer> entry : threadCount.entrySet()) {
      if (aWriter == null) {
        System.err.println("\"" + (String)entry.getKey() + "\" : " + entry.getValue());
      } else {
        aWriter.println("\"" + (String)entry.getKey() + "\" : " + entry.getValue());
      }
    }
  }
  
  private Object mutex = new Object();
  
  public Connection getConnection()
    throws SQLException
  {
    ConInfo ftemp = null;
    int _tryCount = 0;
    do
    {
      synchronized (this.mutex)
      {
        if (this.fNoUseList.size() > 0)
        {
          ftemp = (ConInfo)this.fNoUseList.removeFirst();
          this.finUseList.add(ftemp);
        }
      }
      if (ftemp == null) {
        try
        {
          Thread.sleep(this.tryWait);
        }
        catch (Throwable localThrowable) {}
      }
    } while ((ftemp == null) && (_tryCount++ < this.tryCount));
    if (ftemp == null)
    {
      if (this.finUseList.size() + this.fNoUseList.size() >= this.maxCount) {
        throw new RuntimeException("数据库连接紧张，超过最大连接数，请稍后重试!");
      }
      ftemp = new ConInfo(this);
      ftemp.init();
      synchronized (this.mutex)
      {
        this.finUseList.add(ftemp);
      }
    }
    else
    {
      ftemp.checkReConnect(true);
    }
    ftemp.lock();
    synchronized (this.mutex)
    {
      int finUseSize = this.finUseList.size();
      if (this.peakConnectionCount < finUseSize)
      {
        this.peakConnectionCount = finUseSize;
        this.peakConnectionTimestamp = System.currentTimeMillis();
        if ((LogUtil.isDebugEnabled()) && (finUseSize >= this.connectionThreshold)) {
          printInUseList();
        }
      }
    }
    ProxyConnection c = ftemp.getCon();
    if (c == null)
    {
      releaseConnection(ftemp);
      throw new RuntimeException("不能获取连接");
    }
    c.setAutoCommit(true);
    
    return c;
  }
  
  public void releaseConnection(ConInfo aCon)
  {
    aCon.unLock();
    synchronized (this.mutex)
    {
      boolean index = this.finUseList.remove(aCon);
      if (index) {
        this.fNoUseList.addLast(aCon);
      }
    }
  }
  
  public void uninit()
  {
    try
    {
      clearPool();
      this.finited = false;
    }
    catch (Exception localException) {}
  }
  
  private int bianhao = 0;
  private Object bianbaoLock = new Object();
  
  public int getBianhao()
  {
    synchronized (this.bianbaoLock)
    {
      this.bianhao += 1;
      return this.bianhao;
    }
  }
  
  public int getPeakConnectionCount()
  {
    return this.peakConnectionCount;
  }
  
  public int getBusyConnectionCount()
  {
    return this.finUseList.size();
  }
  
  public void monitor()
  {
    Registry registry = Registry.getRegistry(null, null);
    String[] beans = registry.findManagedBeans("com.seeyon.ctp.dbpool:type=Connections,name=*");
    for (int i = 0; i < beans.length; i++)
    {
      System.out.println(beans[i]);
      registry.unregisterComponent(beans[i]);
    }
    for (ConInfo conInfo : this.finUseList) {
      try
      {
        registry.registerComponent(new ConInfoMBean(conInfo), new ObjectName("com.seeyon.ctp.dbpool:type=Connections,name=" + conInfo
          .getId()), null);
      }
      catch (Throwable e)
      {
        LogUtil.error(e.getLocalizedMessage(), e);
      }
    }
  }
}
