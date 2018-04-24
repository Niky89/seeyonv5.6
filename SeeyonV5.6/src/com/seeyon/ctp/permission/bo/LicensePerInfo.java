package com.seeyon.ctp.permission.bo;

import com.seeyon.apps.m1.authorization.mobileAuth.service.MobileAuthService;
import com.seeyon.ctp.common.AppContext;
import com.seeyon.ctp.common.exceptions.BusinessException;
import com.seeyon.ctp.common.init.MclclzUtil;
import com.seeyon.ctp.login.online.OnlineRecorder;
import com.seeyon.ctp.organization.OrgConstants;
import com.seeyon.ctp.organization.manager.OrgManager;
import com.seeyon.ctp.permission.dao.LicensePerMissionCache;
import com.seeyon.ctp.permission.manager.LicensePermissionManager;
import com.seeyon.ctp.permission.po.PrivPermission;
import com.seeyon.ctp.util.ObjectToXMLBase;
import java.io.Serializable;

public final class LicensePerInfo
  extends ObjectToXMLBase
  implements Serializable
{
  private static final long serialVersionUID = 116238264079364320L;
  protected OrgManager orgManager;
  protected static LicensePermissionManager licensePermissionManager;
  protected LicensePerMissionCache licensePerMissionCache;
  protected MobileAuthService mobileAuthService;
  private String serverversion;
  
  protected OrgManager getOrgManager()
  {
    if (this.orgManager == null) {
      this.orgManager = ((OrgManager)AppContext.getBean("orgManager"));
    }
    return this.orgManager;
  }
  
  protected static LicensePermissionManager getLicensePermissionManager()
  {
    if (licensePermissionManager == null) {
      licensePermissionManager = (LicensePermissionManager)AppContext.getBean("licensePermissionManager");
    }
    return licensePermissionManager;
  }
  
  protected LicensePerMissionCache getLicensePerMissionCache()
  {
    if (this.licensePerMissionCache == null) {
      this.licensePerMissionCache = ((LicensePerMissionCache)AppContext.getBean("licensePerMissionCache"));
    }
    return this.licensePerMissionCache;
  }
  
  protected MobileAuthService getMobileAuthService()
  {
    if (this.mobileAuthService == null) {
      this.mobileAuthService = ((MobileAuthService)AppContext.getBean("mobileAuthService"));
    }
    return this.mobileAuthService;
  }
  
  private Long totalservernum = Long.valueOf(0L);
  private Long useservernum = Long.valueOf(0L);
  private Long unuseservernum = Long.valueOf(0L);
  private Long totalm1num = Long.valueOf(0L);
  private Long usem1num = Long.valueOf(0L);
  private Long unusem1num = Long.valueOf(0L);
  private String accId;
  private Integer servertype;
  private static final Class<?> c1 = MclclzUtil.ioiekc("com.seeyon.ctp.product.ProductInfo");
  public static final int PERMISSION_TYPE_SERVER = 1;
  public static final int PERMISSION_TYPE_M1 = 2;
  public static final int PERMISSION_TYPE_RES = 1;
  public static final int PERMISSION_TYPE_ONLINE = 2;
  int maxOnline = 0;
  int maxRes = 0;
  int maxCompany = 0;
  
  public LicensePerInfo(String accId)
  {
    this.maxOnline = ((Integer)MclclzUtil.invoke(c1, "getMaxOnline")).intValue();
    this.maxRes = ((Integer)MclclzUtil.invoke(c1, "getMaxRegisterSize")).intValue();
    this.maxCompany = ((Integer)MclclzUtil.invoke(c1, "getMaxCompanySize")).intValue();
    
    String serverVersion = (String)MclclzUtil.invoke(c1, "getVersion");
    
    setServerversion(serverVersion);
    if (this.maxOnline > 0) {
      setTotalservernum(Long.valueOf(this.maxOnline));
    } else {
      setTotalservernum(Long.valueOf(this.maxRes));
    }
    setAccId(accId);
  }
  
  public int getMaxCompany()
  {
    return this.maxCompany;
  }
  
  public static LicensePerInfo getInstance(String accountId)
  {
    return new LicensePerInfo(accountId);
  }
  
  public String getAccId()
  {
    return this.accId;
  }
  
  public void setAccId(String accId)
  {
    this.accId = accId;
  }
  
  public Integer getserverType()
  {
    if (this.servertype == null) {
      if (this.maxOnline > 0) {
        this.servertype = Integer.valueOf(2);
      } else {
        this.servertype = Integer.valueOf(1);
      }
    }
    return this.servertype;
  }
  
  public Long getServerOnLineNum()
  {
    if (("".equals(this.accId)) || (OrgConstants.GROUPID.equals(Long.valueOf(this.accId)))) {
      return new Long(OnlineRecorder.getOnlineUserNumber4Server());
    }
    return new Long(OnlineRecorder.getOnlineUserNumber4ServerInAccount(Long.valueOf(this.accId)));
  }
  
  public Integer getm1Type()
  {
    int maxM1Online = ((Integer)MclclzUtil.invoke(c1, "getM1MaxOnline")).intValue();
    int maxM1Res = ((Integer)MclclzUtil.invoke(c1, "getM1MaxRegisterSize")).intValue();
    if (maxM1Online > 0) {
      return Integer.valueOf(2);
    }
    return Integer.valueOf(1);
  }
  
  public void setType(Integer type)
  {
    this.servertype = type;
  }
  
  public String getServerversion()
  {
    return this.serverversion;
  }
  
  public void setServerversion(String serverversion)
  {
    this.serverversion = serverversion;
  }
  
  public Long getTotalservernum()
  {
    if ((!this.accId.equals("")) && (!OrgConstants.GROUPID.toString().equals(this.accId))) {
      this.totalservernum = getLicensePerMissionCache().getPerMissionPO(Long.valueOf(this.accId), 
        Integer.valueOf(1)).getDistributionnum();
    }
    if (this.totalservernum == null) {
      this.totalservernum = Long.valueOf(0L);
    }
    return this.totalservernum;
  }
  
  public void setTotalservernum(Long totalservernum)
  {
    this.totalservernum = totalservernum;
  }
  
  public Long getUseservernum()
    throws NumberFormatException, BusinessException
  {
    if (getAccId().equals("")) {
      setAccId(OrgConstants.GROUPID.toString());
    }
    if (getserverType().intValue() == 1) {
      setUseservernum(Long.valueOf(getOrgManager().getAllMembersNumsWithOutConcurrent(Long.valueOf(getAccId())).intValue()));
    } else {
      setUseservernum(getServerOnLineNum());
    }
    return this.useservernum;
  }
  
  public void setUseservernum(Long useservernum)
  {
    this.useservernum = useservernum;
  }
  
  public Long getUnuseservernum()
    throws NumberFormatException, BusinessException
  {
    setUnuseservernum(Long.valueOf(getTotalservernum().longValue() - getUseservernum().longValue()));
    return this.unuseservernum;
  }
  
  public void setUnuseservernum(Long unuseservernum)
  {
    this.unuseservernum = unuseservernum;
  }
  
  public String getM1version()
  {
    return (String)MclclzUtil.invoke(c1, "getM1Version");
  }
  
  public Long getTotalm1num()
  {
    if ((!this.accId.equals("")) && (!OrgConstants.GROUPID.toString().equals(this.accId)))
    {
      this.totalm1num = getLicensePerMissionCache().getPerMissionPO(Long.valueOf(this.accId), 
        Integer.valueOf(2)).getDistributionnum();
    }
    else
    {
      int maxM1Online = ((Integer)MclclzUtil.invoke(c1, "getM1MaxOnline")).intValue();
      int maxM1Res = ((Integer)MclclzUtil.invoke(c1, "getM1MaxRegisterSize")).intValue();
      if (maxM1Online > 0) {
        this.totalm1num = Long.valueOf(maxM1Online);
      } else {
        this.totalm1num = Long.valueOf(maxM1Res);
      }
    }
    if (this.totalm1num == null) {
      this.totalm1num = Long.valueOf(0L);
    }
    return this.totalm1num;
  }
  
  public void setTotalm1num(Long totalm1num)
  {
    this.totalm1num = totalm1num;
  }
  
  public Long getUsem1num()
  {
    if (getAccId().equals("")) {
      setAccId(OrgConstants.GROUPID.toString());
    }
    if (getm1Type().intValue() == 1) {
      setUsem1num(getM1Resnum());
    } else {
      setUsem1num(getM1OnLinenum());
    }
    return this.usem1num;
  }
  
  public Long getM1OnLinenum()
  {
    if (("".equals(this.accId)) || (OrgConstants.GROUPID.equals(Long.valueOf(this.accId)))) {
      return new Long(OnlineRecorder.getOnlineUserNumber4M1());
    }
    return new Long(OnlineRecorder.getOnlineUserNumber4M1InAccount(Long.valueOf(this.accId)));
  }
  
  public Long getM1Resnum()
  {
    if (("".equals(this.accId)) || (OrgConstants.GROUPID.equals(Long.valueOf(this.accId))))
    {
      if (getMobileAuthService() != null) {
        return Long.valueOf(this.mobileAuthService.getAllUsedRegisterUserCount());
      }
      return Long.valueOf(Long.MAX_VALUE);
    }
    if (getMobileAuthService() != null) {
      return Long.valueOf(this.mobileAuthService.getUsedRegisterUserCount(Long.valueOf(this.accId).longValue()));
    }
    return Long.valueOf(Long.MAX_VALUE);
  }
  
  public void setUsem1num(Long usem1num)
  {
    this.usem1num = usem1num;
  }
  
  public Long getUnusem1num()
  {
    setUnusem1num(Long.valueOf(getTotalm1num().longValue() - getUsem1num().longValue()));
    return this.unusem1num;
  }
  
  public void setUnusem1num(Long unusem1num)
  {
    this.unusem1num = unusem1num;
  }
  
  public static boolean licensePerServerValid()
    throws NumberFormatException, BusinessException
  {
    LicensePerInfo licinfo = new LicensePerInfo("");
    if (licinfo.getserverType().equals(Integer.valueOf(1)))
    {
      if (licinfo.getTotalservernum().intValue() < licinfo.getUseservernum().intValue()) {
        return false;
      }
      return true;
    }
    return true;
  }
  
  public static boolean licensePerM1Valid()
    throws NumberFormatException, BusinessException
  {
    LicensePerInfo licinfo = new LicensePerInfo("");
    if (licinfo.getm1Type().equals(Integer.valueOf(1)))
    {
      if (licinfo.getTotalservernum().intValue() < licinfo.getUseservernum().intValue()) {
        return false;
      }
      return true;
    }
    return true;
  }
  
  public static Long getSysTotalservernum()
  {
    LicensePerInfo licinfo = new LicensePerInfo("");
    return licinfo.getTotalservernum();
  }
  
  public static Long getSysTotalm1num()
  {
    LicensePerInfo licinfo = new LicensePerInfo("");
    return licinfo.getTotalm1num();
  }
  
  public static String getServerPermissionType()
    throws BusinessException
  {
    return getLicensePermissionManager().getServerPermissionType();
  }
  
  public static String getM1PermissionType()
    throws BusinessException
  {
    return getLicensePermissionManager().getM1PermissionType();
  }
}
