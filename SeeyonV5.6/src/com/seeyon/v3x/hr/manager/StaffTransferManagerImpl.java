package com.seeyon.v3x.hr.manager;

import com.seeyon.ctp.common.operationlog.manager.OperationlogManager;
import com.seeyon.ctp.organization.bo.V3xOrgMember;
import com.seeyon.ctp.organization.manager.OrgManager;
import com.seeyon.ctp.organization.manager.OrgManagerDirect;
import com.seeyon.v3x.hr.dao.StaffInfoDao;
import com.seeyon.v3x.hr.dao.StaffTransferDao;
import com.seeyon.v3x.hr.domain.StaffTransfer;
import com.seeyon.v3x.hr.domain.StaffTransferType;
import com.seeyon.v3x.hr.log.StaffTransferLog;
import java.util.List;

public class StaffTransferManagerImpl
  implements StaffTransferManager
{
  private StaffTransferDao staffTransferDao;
  private StaffInfoDao staffInfoDao;
  private OperationlogManager operationlogManager;
  private OrgManagerDirect orgManagerDirect;
  private OrgManager orgManager;
  
  public OrgManager getOrgManager()
  {
    return this.orgManager;
  }
  
  public void setOrgManager(OrgManager orgManager)
  {
    this.orgManager = orgManager;
  }
  
  public StaffInfoDao getStaffInfoDao()
  {
    return this.staffInfoDao;
  }
  
  public void setStaffInfoDao(StaffInfoDao staffInfoDao)
  {
    this.staffInfoDao = staffInfoDao;
  }
  
  public StaffTransferDao getStaffTransferDao()
  {
    return this.staffTransferDao;
  }
  
  public void setStaffTransferDao(StaffTransferDao staffTransferDao)
  {
    this.staffTransferDao = staffTransferDao;
  }
  
  public OrgManagerDirect getOrgManagerDirect()
  {
    return this.orgManagerDirect;
  }
  
  public void setOrgManagerDirect(OrgManagerDirect orgManagerDirect)
  {
    this.orgManagerDirect = orgManagerDirect;
  }
  
  public OperationlogManager getOperationlogManager()
  {
    return this.operationlogManager;
  }
  
  public void setOperationlogManager(OperationlogManager operationlogManager)
  {
    this.operationlogManager = operationlogManager;
  }
  
  public List<StaffTransfer> getStaffTransfer()
    throws Exception
  {
    return this.staffTransferDao.getStaffTransfer();
  }
  
  public void addTransfer(StaffTransfer staffTransfer)
    throws Exception
  {
    staffTransfer.setIdIfNew();
    this.staffTransferDao.save(staffTransfer);
    
    insertTransferLog(staffTransfer, "hr.staffTransfer.transfer.add.desc");
  }
  
  public void updateTransfer(StaffTransfer staffTransfer)
    throws Exception
  {
    insertTransferLog(staffTransfer, "hr.staffTransfer.transfer.update.desc");
    
    this.staffTransferDao.update(staffTransfer);
  }
  
  private void insertTransferLog(StaffTransfer staffTransfer, String bundleName)
    throws Exception
  {
    StaffTransferType staffTransferType = new StaffTransferType(this.staffTransferDao.getStaffTransferType(staffTransfer
      .getType().getId()));
    
    StaffTransferLog log = new StaffTransferLog();
    log.setStaffName(this.orgManager.getMemberById(staffTransfer.getMember_id()).getName());
    log.setStaffTransferType(staffTransferType);
  }
  
  public StaffTransfer getStaffTransferById(Long id)
    throws Exception
  {
    return (StaffTransfer)this.staffTransferDao.get(Long.valueOf(id.longValue()));
  }
  
  public List<StaffTransfer> getTransferTypeStaffTransfer()
    throws Exception
  {
    return this.staffTransferDao.getTransferTypeStaffTransfer();
  }
  
  public List<StaffTransfer> getDimissionTypeStaffTransfer()
    throws Exception
  {
    return this.staffTransferDao.getDimissionTypeStaffTransfer();
  }
  
  public void deleteTransfer(Long id)
    throws Exception
  {
    StaffTransfer staffTransfer = (StaffTransfer)this.staffTransferDao.get(Long.valueOf(id.longValue()));
    insertTransferLog(staffTransfer, "hr.staffTransfer.transfer.delete.desc");
    this.staffTransferDao.deleteTransfer(id.longValue());
  }
  
  public StaffTransferType getStaffTransferTypeById(int id)
    throws Exception
  {
    return this.staffTransferDao.getStaffTransferType(id);
  }
}
