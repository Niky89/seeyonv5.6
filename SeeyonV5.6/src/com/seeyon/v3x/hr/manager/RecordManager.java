package com.seeyon.v3x.hr.manager;

import com.seeyon.v3x.hr.domain.Record;
import com.seeyon.v3x.hr.domain.RecordWorkingTime;
import java.util.Date;
import java.util.List;
import java.util.Map;

public abstract interface RecordManager
{
  public abstract void addRecords4NoCard();
  
  public abstract void addRecords4NoCardTillnow();
  
  public abstract void addRecord(Record paramRecord);
  
  public abstract void updateRecord(String paramString1, String paramString2)
    throws Exception;
  
  public abstract Record getRecord(Long paramLong, Date paramDate)
    throws Exception;
  
  public abstract String getBeginHour()
    throws Exception;
  
  public abstract String getBeginMinute()
    throws Exception;
  
  public abstract String getEndHour()
    throws Exception;
  
  public abstract String getEndMinute()
    throws Exception;
  
  public abstract List<Record> getAllRecord(Long paramLong, Date paramDate1, Date paramDate2)
    throws Exception;
  
  public abstract List<Record> getNoBeginCardStatisticById(Long paramLong, Date paramDate1, Date paramDate2)
    throws Exception;
  
  public abstract Map<Long, Integer> getNoBeginCardStatisticByIdGroupByMemberId(Date paramDate1, Date paramDate2)
    throws Exception;
  
  public abstract List<Record> getNoEndCardStatisticById(Long paramLong, Date paramDate1, Date paramDate2)
    throws Exception;
  
  public abstract Map<Long, Integer> getNoEndCardStatisticByIdGroupByMemberId(Date paramDate1, Date paramDate2)
    throws Exception;
  
  public abstract int getNoCardStatisticById(Long paramLong, Date paramDate1, Date paramDate2)
    throws Exception;
  
  public abstract Map<Long, Integer> getNoCardStatisticByIdGroupByMemberId(Date paramDate1, Date paramDate2)
    throws Exception;
  
  public abstract List<Record> getComeLateStatisticById(Long paramLong, Date paramDate1, Date paramDate2)
    throws Exception;
  
  public abstract Map<Long, Integer> getComeLateStatisticByIdGroupByMemberId(Date paramDate1, Date paramDate2)
    throws Exception;
  
  public abstract List<Record> getLeaveEarlyStatisticById(Long paramLong, Date paramDate1, Date paramDate2)
    throws Exception;
  
  public abstract Map<Long, Integer> getLeaveEarlyStatisticByIdGroupByMemberId(Date paramDate1, Date paramDate2)
    throws Exception;
  
  public abstract List<Record> getBothStatisticById(Long paramLong, Date paramDate1, Date paramDate2)
    throws Exception;
  
  public abstract Map<Long, Integer> getBothStatisticByIdGroupByMemberId(Date paramDate1, Date paramDate2)
    throws Exception;
  
  public abstract List<Record> getNormalStatisticById(Long paramLong, Date paramDate1, Date paramDate2)
    throws Exception;
  
  public abstract Map<Long, Integer> getNormalStatisticByIdGroupByMemberId(Date paramDate1, Date paramDate2)
    throws Exception;
  
  public abstract List<Record> getNoBeginCardLeaveEarlyStatisticById(Long paramLong, Date paramDate1, Date paramDate2)
    throws Exception;
  
  public abstract Map<Long, Integer> getNoBeginCardLeaveEarlyStatisticByIdGroupByMemberId(Date paramDate1, Date paramDate2)
    throws Exception;
  
  public abstract List<Record> getComeLateNoEndCardStatisticById(Long paramLong, Date paramDate1, Date paramDate2)
    throws Exception;
  
  public abstract Map<Long, Integer> getComeLateNoEndCardStatisticByIdGroupByMemberId(Date paramDate1, Date paramDate2)
    throws Exception;
  
  public abstract void setWorkingTime(RecordWorkingTime paramRecordWorkingTime)
    throws Exception;
  
  public abstract Record getRecordById(Long paramLong)
    throws Exception;
  
  public abstract List getAllStaffRecords(Date paramDate)
    throws Exception;
  
  public abstract List getAllStaffRecords(Date paramDate, int paramInt)
    throws Exception;
  
  public abstract List<Record> getAllStaffRecord(Date paramDate1, Date paramDate2)
    throws Exception;
  
  public abstract List<Record> getAllStaffRecordByPage(Date paramDate1, Date paramDate2)
    throws Exception;
  
  public abstract List<Record> getAllStaffRecord(Date paramDate1, Date paramDate2, int paramInt)
    throws Exception;
  
  public abstract List<Record> getAdvancedQuery(String paramString1, String paramString2, String paramString3, int paramInt, String paramString4)
    throws Exception;
  
  public abstract List<Record> getAll()
    throws Exception;
  
  public abstract List getRecordByState(int paramInt)
    throws Exception;
  
  public abstract void deleteAttendance(int paramInt);
  
  public abstract void deleteAttendance(int paramInt, List<Long> paramList);
  
  public abstract boolean isWorkDay(Date paramDate)
    throws Exception;
  
  public abstract List<Record> getAllStaffRecordsDept(Date paramDate1, Date paramDate2, List<Long> paramList)
    throws Exception;
  
  public abstract List<Record> getAllStaffRecordsDeptByPage(Boolean paramBoolean, Date paramDate1, Date paramDate2, List<Long> paramList)
    throws Exception;
}
