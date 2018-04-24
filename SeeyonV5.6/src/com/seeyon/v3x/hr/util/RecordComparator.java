package com.seeyon.v3x.hr.util;

import com.seeyon.ctp.util.DateUtil;
import com.seeyon.ctp.util.Strings;
import com.seeyon.v3x.hr.domain.Record;
import com.seeyon.v3x.hr.domain.RecordState;
import java.text.ParseException;
import java.util.Comparator;
import java.util.Date;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class RecordComparator
  implements Comparator<Record>
{
  private static final Log log = LogFactory.getLog(RecordComparator.class);
  
  public int compare(Record arg0, Record arg1)
  {
    Date date1 = arg0.getBegin_work_time() != null ? arg0.getBegin_work_time() : arg0.getEnd_work_time();
    Date date2 = arg1.getBegin_work_time() != null ? arg1.getBegin_work_time() : arg1.getEnd_work_time();
    if ((arg0.getState() != null) && (arg0.getState().getId() == 3))
    {
      String format = "";
      if (arg0.getBegin_work_time() != null) {
        format = DateUtil.format(arg0.getBegin_work_time(), "yyyy-MM-dd");
      } else if (arg0.getEnd_work_time() != null) {
        format = DateUtil.format(arg0.getEnd_work_time(), "yyyy-MM-dd");
      }
      try
      {
        if (Strings.isNotBlank(format)) {
          date1 = DateUtil.parse(format + " 00:00:00", "yyyy-MM-dd HH:mm:ss");
        }
      }
      catch (ParseException e)
      {
        log.error("", e);
      }
    }
    if ((arg1.getState() != null) && (arg1.getState().getId() == 3))
    {
      String format = "";
      if (arg1.getBegin_work_time() != null) {
        format = DateUtil.format(arg1.getBegin_work_time(), "yyyy-MM-dd");
      } else if (arg1.getEnd_work_time() != null) {
        format = DateUtil.format(arg1.getEnd_work_time(), "yyyy-MM-dd");
      }
      try
      {
        if (Strings.isNotBlank(format)) {
          date2 = DateUtil.parse(format + " 00:00:00", "yyyy-MM-dd HH:mm:ss");
        }
      }
      catch (ParseException e)
      {
        log.error("", e);
      }
    }
    if (date1.before(date2)) {
      return 1;
    }
    return -1;
  }
}
