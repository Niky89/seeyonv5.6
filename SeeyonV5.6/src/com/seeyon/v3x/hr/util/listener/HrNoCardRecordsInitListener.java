package com.seeyon.v3x.hr.util.listener;

import com.seeyon.ctp.common.AbstractSystemInitializer;
import com.seeyon.ctp.common.AppContext;
import com.seeyon.ctp.common.config.SystemConfig;
import com.seeyon.ctp.common.quartz.QuartzListener;
import com.seeyon.v3x.hr.util.HrNoCardRecordsJob;
import java.text.ParseException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.CronExpression;
import org.quartz.CronTrigger;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;

public class HrNoCardRecordsInitListener
  extends AbstractSystemInitializer
{
  private static Log logger = LogFactory.getLog(HrNoCardRecordsInitListener.class);
  private static final String TRIGGER_HRCARD = "trigger_hrcard";
  private static final String TRIGGER_GROUP_HRCARD = "trigger_group_hrcard";
  private static final String JOB_HRCARD = "job_hrcard";
  private static final String JOB_GROUP_HRCARD = "job_group_hrcard";
  
  public void destroy() {}
  
  public void initialize()
  {
    try
    {
      SystemConfig systemConfig = (SystemConfig)AppContext.getBean("systemConfig");
      String ci = systemConfig.get("card_enable");
      boolean cardEnabled = (ci != null) && ("enable".equals(ci));
      if (!cardEnabled) {
        return;
      }
      Scheduler sched = QuartzListener.getScheduler();
      
      String[] triggerGroups = sched.getTriggerGroupNames();
      Boolean isRepeat = Boolean.valueOf(false);
      for (int i = 0; i < triggerGroups.length; i++) {
        if ("trigger_group_hrcard".equals(triggerGroups[i]))
        {
          String[] triggers = sched.getTriggerNames(triggerGroups[i]);
          for (int j = 0; j < triggers.length; j++)
          {
            Trigger tg = sched.getTrigger(triggers[j], triggerGroups[i]);
            if (((tg instanceof CronTrigger)) && (tg.getFullName().equals("trigger_group_hrcard.trigger_hrcard")))
            {
              sched.rescheduleJob(triggers[j], triggerGroups[i], tg);
              isRepeat = Boolean.valueOf(true);
            }
          }
        }
      }
      if (!isRepeat.booleanValue())
      {
        CronTrigger trigger = new CronTrigger("trigger_hrcard", "trigger_group_hrcard");
        
        CronExpression cexp = new CronExpression("0 0 3 ? * SUN-SAT");
        trigger.setCronExpression(cexp);
        
        JobDetail job = new JobDetail("job_hrcard", "job_group_hrcard", HrNoCardRecordsJob.class);
        job.setJobDataMap(new JobDataMap());
        sched.scheduleJob(job, trigger);
      }
      if (logger.isDebugEnabled()) {
        logger.debug("为未进行签到、签退操作的员工，插入未打卡考勤记录的定时任务已经启动...");
      }
    }
    catch (SchedulerException e)
    {
      logger.error("设置任务调度时出现异常：", e);
    }
    catch (ParseException e)
    {
      logger.error("解析定时任务调度时间格式时出现异常：", e);
    }
    catch (Exception e)
    {
      logger.error("为未进行签到、签退操作的员工，插入未打卡考勤记录过程中出现异常：", e);
    }
  }
}
