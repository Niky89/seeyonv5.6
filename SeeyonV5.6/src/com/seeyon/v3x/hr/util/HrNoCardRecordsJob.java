package com.seeyon.v3x.hr.util;

import com.seeyon.ctp.common.AppContext;
import com.seeyon.v3x.hr.manager.RecordManager;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class HrNoCardRecordsJob
  implements Job
{
  private static final Log log = LogFactory.getLog(HrNoCardRecordsJob.class);
  
  public void execute(JobExecutionContext context)
    throws JobExecutionException
  {
    try
    {
      RecordManager recordManager = (RecordManager)AppContext.getBean("recordManager");
      if (recordManager == null)
      {
        log.warn("无法从Spring IOC容器中获取RecordManager，未打卡记录可能会因此无法插入!");
        return;
      }
      recordManager.addRecords4NoCard();
    }
    catch (Exception e)
    {
      log.error("为未进行签到、签退操作的员工，插入未打卡考勤记录过程中出现异常：", e);
    }
  }
}
