package com.seeyon.v3x.system.util;

import com.seeyon.ctp.common.AppContext;
import com.seeyon.ctp.common.config.SystemConfig;

public class PwdStrengthValidationUtil
{
  public static int getPwdStrengthValidationValue()
  {
    int pwdStrengthValidationValue = 0;
    SystemConfig systemConfig = (SystemConfig)AppContext.getBean("systemConfig");
    
    String PWD_STRENGTH_VALIDATION_ENABLE = systemConfig.get("pwd_strength_validation");
    pwdStrengthValidationValue = Integer.valueOf(PWD_STRENGTH_VALIDATION_ENABLE).intValue();
    
    return pwdStrengthValidationValue;
  }
  
  public static int getPwdNeedUpdate()
  {
    int pwdNeedUpdate = 0;
    SystemConfig systemConfig = (SystemConfig)AppContext.getBean("systemConfig");
    
    String pwdStrengthValidation = systemConfig.get("pwd_needupdate");
    if (pwdStrengthValidation != null) {
      if ("enable".equals(pwdStrengthValidation)) {
        pwdNeedUpdate = 1;
      } else if ("disable".equals(pwdStrengthValidation)) {
        pwdNeedUpdate = 0;
      } else {
        pwdNeedUpdate = 0;
      }
    }
    return pwdNeedUpdate;
  }
}
