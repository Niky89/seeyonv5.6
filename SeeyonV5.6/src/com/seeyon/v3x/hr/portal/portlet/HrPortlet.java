package com.seeyon.v3x.hr.portal.portlet;

import java.util.ArrayList;
import java.util.List;

import com.seeyon.ctp.common.AppContext;
import com.seeyon.ctp.common.authenticate.domain.User;
import com.seeyon.ctp.common.config.SystemConfig;
import com.seeyon.ctp.portal.portlet.BasePortlet;
import com.seeyon.ctp.portal.portlet.ImageLayout;
import com.seeyon.ctp.portal.portlet.ImagePortletLayout;
import com.seeyon.ctp.portal.portlet.PortletConstants;

public class HrPortlet
  implements BasePortlet
{
  private SystemConfig systemConfig;
  
  public void setSystemConfig(SystemConfig systemConfig)
  {
    this.systemConfig = systemConfig;
  }
  
  public String getId()
  {
    return "HrPortlet";
  }
  
  public List<ImagePortletLayout> getData()
  {
    List<ImagePortletLayout> layouts = new ArrayList();
    
    ImagePortletLayout hrOrg = new ImagePortletLayout();
    
    hrOrg.setPortletId("hrOrg");
    hrOrg.setResourceCode("F03_hrOrgMgr");
    hrOrg.setPluginId("hr");
    hrOrg.setPortletName("组织机构设置");
    hrOrg.setDisplayName("system.menuname.OrgManagement");
    hrOrg.setCategory(PortletConstants.PortletCategory.hrmanage.name());
    hrOrg.setPortletUrl("/hrOrgMgr.do?method=orgMgrHome");
    hrOrg.setPortletUrlType(PortletConstants.UrlType.workspace.name());
    hrOrg.setSize(PortletConstants.PortletSize.middle.ordinal());
    hrOrg.setOrder(0);
    
    List<ImageLayout> hrOrgIms = new ArrayList();
    ImageLayout hrOrgImg = new ImageLayout();
    hrOrgImg.setImageTitle("system.menuname.OrgManagement");
    hrOrgImg.setImageUrl("d_orgmanage.png");
    hrOrgIms.add(hrOrgImg);
    
    hrOrg.setImageLayouts(hrOrgIms);
    
    layouts.add(hrOrg);
    
    ImagePortletLayout hrStaff = new ImagePortletLayout();
    
    hrStaff.setPortletId("hrStaff");
    hrStaff.setResourceCode("F03_hrStaff");
    hrStaff.setPluginId("hr");
    hrStaff.setPortletName("员工档案管理");
    hrStaff.setDisplayName("system.menuname.EmployeeArchives");
    hrStaff.setCategory(PortletConstants.PortletCategory.hrmanage.name());
    hrStaff.setPortletUrl("/hrStaff.do?method=initStaffListFrame");
    hrStaff.setPortletUrlType(PortletConstants.UrlType.workspace.name());
    hrStaff.setSize(PortletConstants.PortletSize.middle.ordinal());
    hrStaff.setOrder(1);
    
    List<ImageLayout> hrStaffIms = new ArrayList();
    ImageLayout hrStaffImg = new ImageLayout();
    hrStaffImg.setImageTitle("system.menuname.EmployeeArchives");
    hrStaffImg.setImageUrl("d_employeearchives.png");
    hrStaffIms.add(hrStaffImg);
    
    hrStaff.setImageLayouts(hrStaffIms);
    
    layouts.add(hrStaff);
    
    ImagePortletLayout hrSalary = new ImagePortletLayout();
    
    hrSalary.setPortletId("hrSalary");
    hrSalary.setResourceCode("F03_hrSalary");
    hrSalary.setPluginId("hr");
    hrSalary.setPortletName("工资奖金管理");
    hrSalary.setDisplayName("system.menuname.Salary");
    hrSalary.setCategory(PortletConstants.PortletCategory.hrmanage.name());
    hrSalary.setPortletUrl("/hrSalary.do?method=home");
    hrSalary.setPortletUrlType(PortletConstants.UrlType.workspace.name());
    hrSalary.setSize(PortletConstants.PortletSize.middle.ordinal());
    hrSalary.setOrder(2);
    
    List<ImageLayout> hrSalaryIms = new ArrayList();
    ImageLayout hrSalaryImg = new ImageLayout();
    hrSalaryImg.setImageTitle("system.menuname.Salary");
    hrSalaryImg.setImageUrl("d_salary.png");
    hrSalaryIms.add(hrSalaryImg);
    
    hrSalary.setImageLayouts(hrSalaryIms);
    
    layouts.add(hrSalary);
    
    String cardEnable = this.systemConfig.get("card_enable");
    boolean isCardEnable = (cardEnable != null) && ("enable".equals(cardEnable));
    if (isCardEnable)
    {
      ImagePortletLayout hrRecord = new ImagePortletLayout();
      
      hrRecord.setPortletId("hrRecord");
      hrRecord.setResourceCode("F03_hrRecord");
      hrRecord.setPluginId("hr");
      hrRecord.setPortletName("考勤管理");
      hrRecord.setDisplayName("system.menuname.AttendanceManagement");
      hrRecord.setCategory(PortletConstants.PortletCategory.hrmanage.name());
      hrRecord.setPortletUrl("/hrRecord.do?method=initRecordManager");
      hrRecord.setPortletUrlType(PortletConstants.UrlType.workspace.name());
      hrRecord.setSize(PortletConstants.PortletSize.middle.ordinal());
      hrRecord.setOrder(3);
      
      List<ImageLayout> hrRecordIms = new ArrayList();
      ImageLayout hrRecordImg = new ImageLayout();
      hrRecordImg.setImageTitle("system.menuname.AttendanceManagement");
      hrRecordImg.setImageUrl("d_deptrecord.png");
      hrRecordIms.add(hrRecordImg);
      
      hrRecord.setImageLayouts(hrRecordIms);
      
      layouts.add(hrRecord);
      
      User user = AppContext.getCurrentUser();
      if (user.isInternal())
      {
        ImagePortletLayout perRecord = new ImagePortletLayout();
        
        perRecord.setPortletId("perRecord");
        perRecord.setResourceCode("");
        perRecord.setPluginId("hr");
        perRecord.setPortletName("在线打卡");
        perRecord.setDisplayName("menu.hr.personal.attendance.manager");
        perRecord.setCategory(PortletConstants.PortletCategory.common.name());
        perRecord.setPortletUrl("/hrRecord.do?method=initRecord");
        perRecord.setPortletUrlType(PortletConstants.UrlType.workspace.name());
        perRecord.setSize(PortletConstants.PortletSize.middle.ordinal());
        perRecord.setOrder(30);
        
        List<ImageLayout> perRecordIms = new ArrayList();
        ImageLayout perRecordImg = new ImageLayout();
        perRecordImg.setImageTitle("menu.hr.personal.attendance.manager");
        perRecordImg.setImageUrl("d_onlineRecord.png");
        perRecordIms.add(perRecordImg);
        
        perRecord.setImageLayouts(perRecordIms);
        
        layouts.add(perRecord);
      }
    }
    ImagePortletLayout hrStc = new ImagePortletLayout();
    
    hrStc.setPortletId("hrStc");
    hrStc.setResourceCode("F04_hrStatistic");
    hrStc.setPluginId("hr");
    hrStc.setPortletName("统计分析");
    hrStc.setDisplayName("system.menuname.Statistics");
    hrStc.setCategory(PortletConstants.PortletCategory.hrmanage.name());
    hrStc.setPortletUrl("/hrStatistic.do?method=home");
    hrStc.setPortletUrlType(PortletConstants.UrlType.workspace.name());
    hrStc.setSize(PortletConstants.PortletSize.middle.ordinal());
    hrStc.setOrder(5);
    
    List<ImageLayout> hrStcIms = new ArrayList();
    ImageLayout hrStcImg = new ImageLayout();
    hrStcImg.setImageTitle("system.menuname.Statistics");
    hrStcImg.setImageUrl("d_statistics.png");
    hrStcIms.add(hrStcImg);
    
    hrStc.setImageLayouts(hrStcIms);
    
    layouts.add(hrStc);
    
    ImagePortletLayout hrWorktime = new ImagePortletLayout();
    
    hrWorktime.setPortletId("hrWorktime");
    hrWorktime.setResourceCode("F13_viewByCalendar");
    hrWorktime.setPluginId("hr");
    hrWorktime.setPortletName("工作时间设置");
    hrWorktime.setDisplayName("system.menuname.WorkdaySetting");
    hrWorktime.setCategory(PortletConstants.PortletCategory.hrmanage.name());
    hrWorktime.setPortletUrl("/workTimeSetController.do?method=toFrameHTML");
    hrWorktime.setPortletUrlType(PortletConstants.UrlType.workspace.name());
    hrWorktime.setSize(PortletConstants.PortletSize.middle.ordinal());
    hrWorktime.setOrder(6);
    
    List<ImageLayout> hrWorktimeIms = new ArrayList();
    ImageLayout hrWorktimeImg = new ImageLayout();
    hrWorktimeImg.setImageTitle("system.menuname.WorkdaySetting");
    hrWorktimeImg.setImageUrl("d_workdaysetting.png");
    hrWorktimeIms.add(hrWorktimeImg);
    
    hrWorktime.setImageLayouts(hrWorktimeIms);
    
    layouts.add(hrWorktime);
    
    ImagePortletLayout hrUserDefined = new ImagePortletLayout();
    
    hrUserDefined.setPortletId("hrUserDefined");
    hrUserDefined.setResourceCode("F05_hrUserDefined");
    hrUserDefined.setPluginId("hr");
    hrUserDefined.setPortletName("信息项设置");
    hrUserDefined.setDisplayName("system.menuname.ItemSetting");
    hrUserDefined.setCategory(PortletConstants.PortletCategory.hrmanage.name());
    hrUserDefined.setPortletUrl("/hrUserDefined.do?method=home");
    hrUserDefined.setPortletUrlType(PortletConstants.UrlType.workspace.name());
    hrUserDefined.setSize(PortletConstants.PortletSize.middle.ordinal());
    hrUserDefined.setOrder(7);
    
    List<ImageLayout> hrUserDefinedIms = new ArrayList();
    ImageLayout hrUserDefinedImg = new ImageLayout();
    hrUserDefinedImg.setImageTitle("system.menuname.ItemSetting");
    hrUserDefinedImg.setImageUrl("d_itemsetting.png");
    hrUserDefinedIms.add(hrUserDefinedImg);
    
    hrUserDefined.setImageLayouts(hrUserDefinedIms);
    
    layouts.add(hrUserDefined);
    
    return layouts;
  }
  
  public ImagePortletLayout getPortlet(String portletId)
  {
    List<ImagePortletLayout> layouts = getData();
    if (layouts != null) {
      for (ImagePortletLayout layout : layouts) {
        if (portletId.equals(layout.getPortletId())) {
          return layout;
        }
      }
    }
    return null;
  }
  
  public int getDataCount(String portletId)
  {
    return -1;
  }
  
  public boolean isAllowDataUsed(String portletId)
  {
    if (("hrRecord".equals(portletId)) || ("perRecord".equals(portletId)))
    {
      String cardEnable = this.systemConfig.get("card_enable");
      boolean isEnable = (cardEnable != null) && ("enable".equals(cardEnable));
      if ("hrRecord".equals(portletId)) {
        return isEnable;
      }
      User user = AppContext.getCurrentUser();
      if (user.isInternal()) {
        return isEnable;
      }
      return false;
    }
    return true;
  }
  
  public boolean isAllowUsed()
  {
    return true;
  }
}
