package com.seeyon.v3x.system.util;

import java.io.Serializable;

public class AccountSymbol
  implements Serializable
{
  private static final long serialVersionUID = -643623578900581205L;
  private Long accountId;
  private String logoImagePath;
  private boolean isHiddenLogo;
  private String bannerImagePath;
  private boolean isTileBanner;
  private boolean isHiddenAccountName;
  private Boolean isHiddenGroupName = null;
  
  public Long getAccountId()
  {
    return this.accountId;
  }
  
  public void setAccountId(Long accountId)
  {
    this.accountId = accountId;
  }
  
  public String getBannerImagePath()
  {
    return this.bannerImagePath;
  }
  
  public void setBannerImagePath(String bannerImagePath)
  {
    this.bannerImagePath = bannerImagePath;
  }
  
  public boolean isHiddenAccountName()
  {
    return this.isHiddenAccountName;
  }
  
  public void setHiddenAccountName(boolean isHiddenAccountName)
  {
    this.isHiddenAccountName = isHiddenAccountName;
  }
  
  public boolean isHiddenLogo()
  {
    return this.isHiddenLogo;
  }
  
  public void setHiddenLogo(boolean isHiddenLogo)
  {
    this.isHiddenLogo = isHiddenLogo;
  }
  
  public boolean isTileBanner()
  {
    return this.isTileBanner;
  }
  
  public void setTileBanner(boolean isTileBanner)
  {
    this.isTileBanner = isTileBanner;
  }
  
  public String getLogoImagePath()
  {
    return this.logoImagePath;
  }
  
  public void setLogoImagePath(String logoImagePath)
  {
    this.logoImagePath = logoImagePath;
  }
  
  public Boolean isHiddenGroupName()
  {
    return this.isHiddenGroupName;
  }
  
  public void setHiddenGroupName(Boolean isHiddenGroupName)
  {
    this.isHiddenGroupName = isHiddenGroupName;
  }
}
