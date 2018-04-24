package com.seeyon.v3x.hr.util;

import com.seeyon.ctp.common.SystemEnvironment;

public class PersonHrMenuChecker
{
  public boolean check(long memberId, long loginAccountId)
  {
    return SystemEnvironment.hasPlugin("hr");
  }
}
