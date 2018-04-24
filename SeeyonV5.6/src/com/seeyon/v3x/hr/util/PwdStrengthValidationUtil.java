package com.seeyon.v3x.hr.util;

import com.seeyon.ctp.common.AppContext;
import com.seeyon.ctp.common.config.SystemConfig;

public class PwdStrengthValidationUtil
{
  public static int getPwdStrengthValidationValue()
  {
    int pwdStrengthValidationValue = 0;
    SystemConfig systemConfig = (SystemConfig)AppContext.getBean("systemConfig");
    
    String pwdStrengthValidation = systemConfig.get("pwd_strength_validation_enable");
    if (pwdStrengthValidation != null) {
      if ("enable".equals(pwdStrengthValidation)) {
        pwdStrengthValidationValue = 1;
      } else if ("disable".equals(pwdStrengthValidation)) {
        pwdStrengthValidationValue = 0;
      } else {
        pwdStrengthValidationValue = 0;
      }
    }
    return pwdStrengthValidationValue;
  }
}
