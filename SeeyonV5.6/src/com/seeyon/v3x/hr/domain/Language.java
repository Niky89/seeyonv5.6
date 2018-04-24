package com.seeyon.v3x.hr.domain;

import com.seeyon.v3x.common.domain.BaseModel;
import java.io.Serializable;

public class Language
  extends BaseModel
  implements Serializable
{
  private static final long serialVersionUID = 411185004755098325L;
  private String languageName;
  private String languageShortName;
  private String languageLabel;
  
  public String getLanguageLabel()
  {
    return this.languageLabel;
  }
  
  public void setLanguageLabel(String languageLabel)
  {
    this.languageLabel = languageLabel;
  }
  
  public String getLanguageName()
  {
    return this.languageName;
  }
  
  public void setLanguageName(String languageName)
  {
    this.languageName = languageName;
  }
  
  public String getLanguageShortName()
  {
    return this.languageShortName;
  }
  
  public void setLanguageShortName(String languageShortName)
  {
    this.languageShortName = languageShortName;
  }
}
