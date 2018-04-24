package com.seeyon.v3x.hr.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.servlet.ModelAndView;

import com.seeyon.ctp.common.AppContext;
import com.seeyon.ctp.common.excel.DataRecord;
import com.seeyon.ctp.common.excel.DataRow;
import com.seeyon.ctp.common.excel.FileToExcelManager;
import com.seeyon.ctp.common.filemanager.manager.FileManager;
import com.seeyon.ctp.common.operationlog.manager.OperationlogManager;
import com.seeyon.ctp.common.po.operationlog.OperationLog;
import com.seeyon.ctp.organization.bo.V3xOrgMember;
import com.seeyon.ctp.organization.manager.OrgManager;
import com.seeyon.ctp.util.annotation.CheckRoleAccess;
import com.seeyon.v3x.common.i18n.LocaleContext;
import com.seeyon.v3x.common.i18n.ResourceBundleUtil;
import com.seeyon.v3x.common.search.manager.SearchManager;
import com.seeyon.v3x.common.web.BaseController;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.hr.log.StaffTransferLog;
import com.seeyon.v3x.hr.util.Constants;
import com.seeyon.v3x.hr.util.HqlSearchHelper;
import com.seeyon.v3x.hr.util.OperationLogHelper;
import com.seeyon.v3x.hr.webmodel.WebOperationLog;
import com.seeyon.v3x.util.Datetimes;

@CheckRoleAccess(roleTypes={com.seeyon.ctp.organization.OrgConstants.Role_NAME.HrAdmin})
public class HrLogController
  extends BaseController
{
  private static final transient Log LOG = LogFactory.getLog(HrLogController.class);
  private OperationlogManager operationlogManager;
  private OrgManager orgManager;
  private SearchManager searchManager;
  private FileToExcelManager fileToExcelManager;
  private FileManager fileManager;

  public FileManager getFileManager()
  {
    return this.fileManager;
  }
  
  public void setFileManager(FileManager fileManager)
  {
    this.fileManager = fileManager;
  }
  
  public FileToExcelManager getFileToExcelManager()
  {
    return this.fileToExcelManager;
  }
  
  public void setFileToExcelManager(FileToExcelManager fileToExcelManager)
  {
    this.fileToExcelManager = fileToExcelManager;
  }
  
  public OperationlogManager getOperationlogManager()
  {
    return this.operationlogManager;
  }
  
  public void setOperationlogManager(OperationlogManager operationlogManager)
  {
    this.operationlogManager = operationlogManager;
  }
  
  public ModelAndView index(HttpServletRequest request, HttpServletResponse response)
    throws Exception
  {
    return null;
  }
  
  public ModelAndView initLog(HttpServletRequest request, HttpServletResponse response)
    throws Exception
  {
    ModelAndView mav = new ModelAndView("hr/log");
    String ids = request.getParameter("ids");
    String model = request.getParameter("model");
    mav.addObject("model", model);
    mav.addObject("ids", ids);
    return mav;
  }
  
  public ModelAndView viewLog(HttpServletRequest request, HttpServletResponse response)
    throws Exception
  {
    if (!AppContext.hasResourceCode("F03_hrStaff")) {
      return null;
    }
    ModelAndView mav = new ModelAndView("hr/log");
    List<OperationLog> operationLogs = new ArrayList();
    List<WebOperationLog> webOperationLogs = new ArrayList();
    Long accountId = CurrentUser.get().getLoginAccount();
    Locale local = LocaleContext.getLocale(request);
    String resource = "com.seeyon.v3x.hr.resource.i18n.HRResources";
    String actionNamedef = ResourceBundleUtil.getString(resource, local, "hr.default.people.value", new Object[0]);
    String model = request.getParameter("model");
    String ids = request.getParameter("ids");
    String condition = request.getParameter("condition");
    String textfield = request.getParameter("textfield");
    String textfield1 = request.getParameter("textfield1");
    if ((textfield1 != null) && 
      ("".equals(textfield)) && (actionNamedef.equals(textfield1)))
    {
      condition = null;
      textfield = null;
      textfield1 = null;
    }
    if ((condition != null) && (
      (condition.equals("actionTime")) || (condition.equals("actionName")) || (condition.equals("actionType"))))
    {
      if (model.equals("transfer")) {
        operationLogs = HqlSearchHelper.searchLog(condition, textfield, textfield1, Constants.MODULE_TRANSFER, 
          this.searchManager);
      } else if (model.equals("staff")) {
        if ((StringUtils.isNotBlank(textfield)) || (StringUtils.isNotBlank(textfield1))) {
          operationLogs = HqlSearchHelper.searchLog(condition, textfield, textfield1, Constants.MODULE_STAFF, 
            this.searchManager);
        } else {
          operationLogs = this.operationlogManager.queryBySubObjectIdAndObjectId(accountId, 
            Constants.MODULE_STAFF, true);
        }
      }
      mav.addObject("isLoad", "unLoad");
    }
    else
    {
      if ((ids != null) && (!ids.equals("")))
      {
        List<Long> objectIds = toLongList(ids);
        operationLogs = this.operationlogManager.getAllOperationLog(objectIds, true);
      }
      else if (model.equals("transfer"))
      {
        operationLogs = this.operationlogManager.queryBySubObjectIdAndObjectId(accountId, 
          Constants.MODULE_TRANSFER, true);
      }
      else if (model.equals("staff"))
      {
        operationLogs = this.operationlogManager.queryBySubObjectIdAndObjectId(accountId, 
          Constants.MODULE_STAFF, true);
      }
      mav.addObject("isLoad", "load");
    }
    webOperationLogs = toWebOperationLogList(operationLogs, model);
    int size = webOperationLogs.size();
    
    mav.addObject("size", Integer.valueOf(size));
    mav.addObject("webOperationLogs", webOperationLogs);
    mav.addObject("model", model);
    mav.addObject("ids", ids);
    return mav;
  }
  
  public ModelAndView searchLog(HttpServletRequest request, HttpServletResponse response)
    throws Exception
  {
    ModelAndView mav = new ModelAndView("hr/log");
    List<OperationLog> operationLogs = new ArrayList();
    String model = request.getParameter("model");
    String condition = request.getParameter("condition");
    String textfield = request.getParameter("textfield");
    String textfield1 = request.getParameter("textfield1");
    if (model.equals("transfer")) {
      operationLogs = HqlSearchHelper.searchLog(condition, textfield, textfield1, Constants.MODULE_TRANSFER, 
        this.searchManager);
    } else if (model.equals("staff")) {
      operationLogs = HqlSearchHelper.searchLog(condition, textfield, textfield1, Constants.MODULE_STAFF, 
        this.searchManager);
    }
    mav.addObject("webOperationLogs", toWebOperationLogList(operationLogs, model));
    mav.addObject("model", model);
    mav.addObject("size", Integer.valueOf(operationLogs.size()));
    mav.addObject("condition", condition);
    mav.addObject("textfield", textfield);
    mav.addObject("isLoad", "unLoad");
    return mav;
  }
  
  private List<WebOperationLog> toWebOperationLogList(List<OperationLog> operationLogs, String model)
    throws com.seeyon.v3x.common.exceptions.BusinessException, com.seeyon.ctp.common.exceptions.BusinessException
  {
    List<WebOperationLog> webOperationLogs = new ArrayList();
    if (operationLogs.size() != 0) {
      for (OperationLog operationLog : operationLogs)
      {
        WebOperationLog webOperationLog = new WebOperationLog();
        String staffName = "";
        V3xOrgMember orgMember = this.orgManager.getMemberById(operationLog.getMemberId());
        if (orgMember != null)
        {
          staffName = orgMember.getName();
          if (model.equals("transfer"))
          {
            StaffTransferLog staffTransferLog = (StaffTransferLog)OperationLogHelper.decoder(operationLog
              .getContentParameters());
            webOperationLog.setStaffTransferLog(staffTransferLog);
          }
          else if (model.equals("staff"))
          {
            String operation = (String)OperationLogHelper.decoder(operationLog.getContentParameters());
            webOperationLog.setOperation(operation);
          }
          webOperationLog.setStaffName(staffName);
          webOperationLog.setOperationLog(operationLog);
          webOperationLogs.add(webOperationLog);
        }
        else
        {
          orgMember = this.orgManager.getMemberById(operationLog.getMemberId());
          if (orgMember != null)
          {
            staffName = orgMember.getName();
            if (model.equals("transfer"))
            {
              StaffTransferLog staffTransferLog = (StaffTransferLog)
                OperationLogHelper.decoder(operationLog.getContentParameters());
              webOperationLog.setStaffTransferLog(staffTransferLog);
            }
            else if (model.equals("staff"))
            {
              String operation = (String)OperationLogHelper.decoder(operationLog.getContentParameters());
              webOperationLog.setOperation(operation);
            }
            webOperationLog.setStaffName(staffName);
            webOperationLog.setOperationLog(operationLog);
            webOperationLogs.add(webOperationLog);
          }
        }
      }
    }
    return webOperationLogs;
  }
  
  public SearchManager getSearchManager()
  {
    return this.searchManager;
  }
  
  public OrgManager getOrgManager()
  {
    return this.orgManager;
  }
  
  public void setOrgManager(OrgManager orgManager)
  {
    this.orgManager = orgManager;
  }
  
  public void setSearchManager(SearchManager searchManager)
  {
    this.searchManager = searchManager;
  }
  
  private List<Long> toLongList(String sIdStr)
    throws com.seeyon.v3x.common.exceptions.BusinessException
  {
    List<Long> sIds = new ArrayList();
    if ((sIdStr != null) && (!sIdStr.equals("")))
    {
      String[] salaryIds = sIdStr.split(",");
      String[] arrayOfString1;
      int j = (arrayOfString1 = salaryIds).length;
      for (int i = 0; i < j; i++)
      {
        String strId = arrayOfString1[i];
        if ((strId != null) && (!"".equals(strId)))
        {
          Long id = Long.valueOf(Long.parseLong(strId));
          LOG.debug("mIdStr: " + id);
          sIds.add(id);
        }
      }
    }
    return sIds;
  }
  
  public ModelAndView exportExcel(HttpServletRequest request, HttpServletResponse response)
    throws Exception
  {
    List<OperationLog> operationLogs = new ArrayList();
    Long accountId = CurrentUser.get().getLoginAccount();
    String condition = "";
    String textfield = "";
    String textfield1 = "";
    String model = request.getParameter("model");
    String isLoad = request.getParameter("isLoad");
    if (model.equals("transfer"))
    {
      if (isLoad.equals("load"))
      {
        String ids = request.getParameter("ids");
        if ((ids != null) && (!ids.equals(""))) {
          operationLogs = this.operationlogManager.getAllOperationLog(toLongList(ids), false);
        } else {
          operationLogs = this.operationlogManager.queryBySubObjectId(Constants.MODULE_TRANSFER, false);
        }
      }
      else if (isLoad.equals("unLoad"))
      {
        condition = request.getParameter("condition");
        textfield = request.getParameter("textfield");
        String ids = request.getParameter("ids");
        textfield1 = request.getParameter("textfield1");
        if ((ids != null) && (!ids.equals(""))) {
          operationLogs = this.operationlogManager.getAllOperationLog(toLongList(ids), false);
        } else {
          operationLogs = HqlSearchHelper.searchLog(condition, textfield, textfield1, 
            Constants.MODULE_TRANSFER, this.searchManager);
        }
      }
    }
    else if (model.equals("staff")) {
      if (isLoad.equals("load"))
      {
        operationLogs = this.operationlogManager.queryBySubObjectIdAndObjectId(accountId, 
          Constants.MODULE_STAFF, false);
      }
      else if (isLoad.equals("unLoad"))
      {
        condition = request.getParameter("condition");
        textfield = request.getParameter("textfield");
        textfield1 = request.getParameter("textfield1");
        operationLogs = HqlSearchHelper.searchLog(condition, textfield, textfield1, Constants.MODULE_STAFF, 
          this.searchManager);
      }
    }
    Locale local = LocaleContext.getLocale(request);
    String resource = "com.seeyon.v3x.hr.resource.i18n.HRResources";
    String form = ResourceBundleUtil.getString(resource, local, "hr.log.form.label", new Object[0]);
    String userName = ResourceBundleUtil.getString(resource, local, "hr.log.userName.label", new Object[0]);
    String type11 = ResourceBundleUtil.getString(resource, local, "hr.log.operation.type.label", new Object[0]);
    String operationTime = ResourceBundleUtil.getString(resource, local, "hr.log.operationTime.label", new Object[0]);
    String ip = ResourceBundleUtil.getString(resource, local, "hr.log.ip.label", new Object[0]);
    String note = ResourceBundleUtil.getString(resource, local, "hr.log.operation.note.label", new Object[0]);
    List<WebOperationLog> webOperationLogs = toWebOperationLogList(operationLogs, model);
    DataRecord record = new DataRecord();
    record.setSheetName(form);
    record.setTitle(form);
    String[] columnNames = { userName, type11, operationTime, ip, note };
    record.setColumnName(columnNames);
    for (WebOperationLog webOperationLog : webOperationLogs)
    {
      DataRow row = new DataRow();
      row.addDataCell(webOperationLog.getStaffName(), 1);
      row.addDataCell(
        ResourceBundleUtil.getString(resource, local, webOperationLog.getOperationLog().getActionType(), new Object[0]), 
        1);
      row.addDataCell(Datetimes.formatDatetime(webOperationLog.getOperationLog().getActionTime()), 
        5);
      row.addDataCell(webOperationLog.getOperationLog().getRemoteIp(), 1);
      if (model.equals("transfer")) {
        row.addDataCell(
        
          ResourceBundleUtil.getString(resource, local, webOperationLog.getStaffTransferLog().getStaffName(), new Object[0]) + 
          "  " + 
          ResourceBundleUtil.getString(resource, local, webOperationLog.getStaffTransferLog()
          .getStaffTransferType().getType_name(), new Object[0]), 1);
      } else if (model.equals("staff")) {
        row.addDataCell(
        
          ResourceBundleUtil.getString(resource, local, webOperationLog.getOperationLog().getActionType(), new Object[0]) + 
          "  " + 
          ResourceBundleUtil.getString(resource, local, webOperationLog.getOperation(), new Object[0]), 
          1);
      }
      record.addDataRow(new DataRow[] { row });
    }
    if (model.equals("transfer")) {
      this.fileToExcelManager.save(response, "transferLog", new DataRecord[] { record });
    } else if (model.equals("staff")) {
      this.fileToExcelManager.save(response, "staffLog", new DataRecord[] { record });
    }
    return null;
  }
}
