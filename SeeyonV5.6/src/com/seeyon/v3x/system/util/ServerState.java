package com.seeyon.v3x.system.util;

import com.seeyon.ctp.cluster.notification.NotificationFactory;
import com.seeyon.ctp.cluster.notification.NotificationManager;
import com.seeyon.ctp.cluster.notification.NotificationType;
import com.seeyon.ctp.common.AppContext;
import com.seeyon.ctp.common.constants.SystemProperties;
import com.seeyon.ctp.login.online.OnlineManager;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ServerState
{
  private static final Log log = LogFactory.getLog(ServerState.class);
  private static final long delay = 15000L;
  private static final long period = 15000L;
  private static final int minMinute = 1;
  private static int autoExitdelay = 30;
  private static ServerState instance;
  private static OnlineManager onLineManager;
  
  public static enum State
  {
    normalization,  shutdown;
  }
  
  private State currentState = State.normalization;
  private long currentTime = 0L;
  private long second = 0L;
  private String comment;
  private long stopwatch = 0L;
  private boolean autoExit;
  private Timer timer;
  private Map<Long, Boolean> hasWarnUsers = new HashMap();
  
  public static ServerState getInstance()
  {
    if (instance == null)
    {
      instance = new ServerState();
      onLineManager = (OnlineManager)AppContext.getBean("onlineManager");
    }
    return instance;
  }
  
  public void setStateShutdown(int minute, String comment, boolean _autoExit)
  {
    NotificationManager.getInstance().send(NotificationType.ServerStateShutdown, new Object[] { Integer.valueOf(minute), comment, Boolean.valueOf(_autoExit) });
    this.autoExit = _autoExit;
    minute = Math.max(minute, 1);
    this.second = (this.stopwatch = minute * 60 * 1000);
    
    this.currentState = State.shutdown;
    this.comment = comment;
    this.currentTime = System.currentTimeMillis();
    
    autoExitdelay = Integer.parseInt(SystemProperties.getInstance().getProperty("message.interval.second")) * 2 + 15;
    
    TimerTask task = new TimerTask()
    {
      public void run()
      {
        if (ServerState.this.stopwatch > 0L)
        {
          System.out.println("[A8提醒] : 停止服务倒计时 " + ServerState.this.stopwatch / 1000L + " 秒. 当前 " + ServerState.onLineManager.getOnlineNumber() + " 人在线");
          ServerState.this.stopwatch -= 15000L;
        }
        else
        {
          ServerState.this.timer.cancel();
          if (ServerState.this.autoExit)
          {
            System.out.println("[A8提醒] : 服务停止预留时间到，服务器在 " + ServerState.autoExitdelay + " 秒钟后自动关闭.");
            for (int i = ServerState.autoExitdelay; i > 0; i--) {
              try
              {
                Thread.sleep(1000L);
                System.out.print(i - 1 + "  ");
              }
              catch (InterruptedException e)
              {
                ServerState.log.error(e.getMessage(), e);
              }
            }
            ServerState.this.toXiaoDiqiu();
            
            System.exit(0);
          }
          else
          {
            System.out.println("[A8提醒] : 服务停止预留时间到");
          }
        }
      }
    };
    this.stopwatch -= 15000L;
    
    this.timer = new Timer(true);
    this.timer.schedule(task, 15000L, 15000L);
    
    log.info("将服务器状态设置为“停止”[" + minute + ", " + comment + ", " + _autoExit + "]");
  }
  
  /* Error */
  private void toXiaoDiqiu()
  {
    // Byte code:
    //   0: new 171	java/lang/StringBuilder
    //   3: dup
    //   4: invokestatic 207	com/seeyon/ctp/common/SystemEnvironment:getApplicationFolder	()Ljava/lang/String;
    //   7: invokestatic 212	java/lang/String:valueOf	(Ljava/lang/Object;)Ljava/lang/String;
    //   10: invokespecial 175	java/lang/StringBuilder:<init>	(Ljava/lang/String;)V
    //   13: ldc -39
    //   15: invokevirtual 184	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   18: invokevirtual 192	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   21: astore_1
    //   22: aload_1
    //   23: invokestatic 219	com/seeyon/ctp/util/Strings:getCanonicalPath	(Ljava/lang/String;)Ljava/lang/String;
    //   26: astore_1
    //   27: aconst_null
    //   28: astore_2
    //   29: new 224	java/io/File
    //   32: dup
    //   33: aload_1
    //   34: invokespecial 226	java/io/File:<init>	(Ljava/lang/String;)V
    //   37: astore_3
    //   38: aload_3
    //   39: invokevirtual 227	java/io/File:exists	()Z
    //   42: ifne +16 -> 58
    //   45: aload_2
    //   46: ifnull +11 -> 57
    //   49: aload_2
    //   50: invokevirtual 231	java/io/FileOutputStream:close	()V
    //   53: goto +4 -> 57
    //   56: pop
    //   57: return
    //   58: new 232	java/io/FileOutputStream
    //   61: dup
    //   62: aload_3
    //   63: iconst_0
    //   64: invokespecial 236	java/io/FileOutputStream:<init>	(Ljava/io/File;Z)V
    //   67: astore_2
    //   68: aload_2
    //   69: new 213	java/lang/String
    //   72: dup
    //   73: ldc -17
    //   75: invokespecial 241	java/lang/String:<init>	(Ljava/lang/String;)V
    //   78: invokevirtual 242	java/lang/String:getBytes	()[B
    //   81: invokevirtual 246	java/io/FileOutputStream:write	([B)V
    //   84: aload_2
    //   85: invokevirtual 250	java/io/FileOutputStream:flush	()V
    //   88: goto +74 -> 162
    //   91: astore_3
    //   92: getstatic 45	com/seeyon/v3x/system/util/ServerState:log	Lorg/apache/commons/logging/Log;
    //   95: ldc -3
    //   97: aload_3
    //   98: invokeinterface 255 3 0
    //   103: aload_2
    //   104: ifnull +70 -> 174
    //   107: aload_2
    //   108: invokevirtual 231	java/io/FileOutputStream:close	()V
    //   111: goto +63 -> 174
    //   114: pop
    //   115: goto +59 -> 174
    //   118: astore_3
    //   119: getstatic 45	com/seeyon/v3x/system/util/ServerState:log	Lorg/apache/commons/logging/Log;
    //   122: ldc -3
    //   124: aload_3
    //   125: invokeinterface 255 3 0
    //   130: aload_2
    //   131: ifnull +43 -> 174
    //   134: aload_2
    //   135: invokevirtual 231	java/io/FileOutputStream:close	()V
    //   138: goto +36 -> 174
    //   141: pop
    //   142: goto +32 -> 174
    //   145: astore 4
    //   147: aload_2
    //   148: ifnull +11 -> 159
    //   151: aload_2
    //   152: invokevirtual 231	java/io/FileOutputStream:close	()V
    //   155: goto +4 -> 159
    //   158: pop
    //   159: aload 4
    //   161: athrow
    //   162: aload_2
    //   163: ifnull +11 -> 174
    //   166: aload_2
    //   167: invokevirtual 231	java/io/FileOutputStream:close	()V
    //   170: goto +4 -> 174
    //   173: pop
    //   174: return
    // Line number table:
    //   Java source line #152	-> byte code offset #0
    //   Java source line #153	-> byte code offset #22
    //   Java source line #155	-> byte code offset #27
    //   Java source line #157	-> byte code offset #29
    //   Java source line #158	-> byte code offset #38
    //   Java source line #170	-> byte code offset #45
    //   Java source line #172	-> byte code offset #49
    //   Java source line #174	-> byte code offset #56
    //   Java source line #158	-> byte code offset #57
    //   Java source line #159	-> byte code offset #58
    //   Java source line #160	-> byte code offset #68
    //   Java source line #161	-> byte code offset #84
    //   Java source line #163	-> byte code offset #91
    //   Java source line #164	-> byte code offset #92
    //   Java source line #170	-> byte code offset #103
    //   Java source line #172	-> byte code offset #107
    //   Java source line #174	-> byte code offset #114
    //   Java source line #166	-> byte code offset #118
    //   Java source line #167	-> byte code offset #119
    //   Java source line #170	-> byte code offset #130
    //   Java source line #172	-> byte code offset #134
    //   Java source line #174	-> byte code offset #141
    //   Java source line #169	-> byte code offset #145
    //   Java source line #170	-> byte code offset #147
    //   Java source line #172	-> byte code offset #151
    //   Java source line #174	-> byte code offset #158
    //   Java source line #177	-> byte code offset #159
    //   Java source line #170	-> byte code offset #162
    //   Java source line #172	-> byte code offset #166
    //   Java source line #174	-> byte code offset #173
    //   Java source line #178	-> byte code offset #174
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	175	0	this	ServerState
    //   21	13	1	path	String
    //   28	139	2	out	java.io.FileOutputStream
    //   37	26	3	file	java.io.File
    //   91	7	3	e	java.io.FileNotFoundException
    //   118	7	3	e	Exception
    //   145	15	4	localObject	Object
    //   56	1	7	localException1	Exception
    //   114	1	8	localException2	Exception
    //   141	1	9	localException3	Exception
    //   158	1	10	localException4	Exception
    //   173	1	11	localException5	Exception
    // Exception table:
    //   from	to	target	type
    //   49	53	56	java/lang/Exception
    //   29	45	91	java/io/FileNotFoundException
    //   58	88	91	java/io/FileNotFoundException
    //   107	111	114	java/lang/Exception
    //   29	45	118	java/lang/Exception
    //   58	88	118	java/lang/Exception
    //   134	138	141	java/lang/Exception
    //   29	45	145	finally
    //   58	103	145	finally
    //   118	130	145	finally
    //   151	155	158	java/lang/Exception
    //   166	170	173	java/lang/Exception
  }
  
  public int getShutdownTime()
  {
    return (int)((this.second - (System.currentTimeMillis() - this.currentTime)) / 1000L);
  }
  
  public boolean isShutdown()
  {
    return State.shutdown.equals(this.currentState);
  }
  
  public boolean isShutdownWarn(Long userId)
  {
    if ((isShutdown()) && (!this.hasWarnUsers.containsKey(userId)))
    {
      this.hasWarnUsers.put(userId, Boolean.TRUE);
      return true;
    }
    return false;
  }
  
  public boolean isForceLogout()
  {
    return (isShutdown()) && (System.currentTimeMillis() - this.currentTime > this.second);
  }
  
  public String getComment()
  {
    return this.comment;
  }
  
  public int getMinute()
  {
    return new Long(this.second / 60000L).intValue();
  }
  
  public boolean isAutoExit()
  {
    return this.autoExit;
  }
}
