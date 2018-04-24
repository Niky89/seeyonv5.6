package com.seeyon.v3x.hr.util;

import com.seeyon.ctp.util.Strings;
import com.seeyon.v3x.util.XMLCoder;

public class OperationLogHelper
{
  private static String omitHeader(String xml)
  {
    if (Strings.isNotBlank(xml)) {
      return xml.substring(xml.indexOf("<object-array>") + "<object-array>".length(), xml.lastIndexOf("</object-array>")).trim();
    }
    return null;
  }
  
  public static Object decoder(String xml)
  {
    return XMLCoder.decoder(omitHeader(xml));
  }
}
