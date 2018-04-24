package com.seeyon.v3x.hr.taglibs;

import com.seeyon.v3x.common.taglibs.functions.Functions;
import com.seeyon.v3x.hr.domain.Repository;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.BodyTagSupport;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

public class StaffDisplayTaglib
  extends BodyTagSupport
{
  private static final long serialVersionUID = -7117351481563303609L;
  public static final String TAG_NAME = "staffDisplayTag";
  private List<Map<Long, Repository>> valueList;
  private List<Integer> typeList;
  
  public void setValueList(List<Map<Long, Repository>> valueList)
  {
    this.valueList = valueList;
  }
  
  public void setTypeList(List<Integer> typeList)
  {
    this.typeList = typeList;
  }
  
  public void init()
  {
    this.valueList = null;
  }
  
  public void release()
  {
    super.release();
  }
  
  public int doStartTag()
    throws JspException
  {
    return super.doStartTag();
  }
  
  public int doEndTag()
    throws JspException
  {
    if (CollectionUtils.isNotEmpty(this.valueList)) {
      try
      {
        printHtml();
      }
      catch (IOException e)
      {
        throw new JspTagException(e.toString(), e);
      }
    }
    init();
    return super.doEndTag();
  }
  
  private void printHtml()
    throws IOException
  {
    JspWriter out = this.pageContext.getOut();
    if (out == null) {
      return;
    }
    Map<Long, Repository> l = (Map)this.valueList.get(0);
    if (l == null) {
      return;
    }
    for (Map.Entry<Long, Repository> entry : l.entrySet()) {
      printHtml(out, (Long)entry.getKey());
    }
  }
  
  private void printHtml(JspWriter out, Long key)
    throws IOException
  {
    String ids = doSetIds(key);
    
    out.println("<tr>");
    out.println("<td width=\"3%\"><input type=\"checkbox\" name=\"id\" id=\"id\" value=\"" + ids + "\"></td>");
    for (int i = 0; i < this.valueList.size(); i++)
    {
      Map<Long, Repository> l = (Map)this.valueList.get(i);
      if (l != null)
      {
        out.print("<td class=\"cursor-hand new-column\" width=\"16%\" nowrap=\"nowrap\" onclick=\"viewUserDefined('" + ids + "')\">");
        int type = catchType(i);
        
        Repository r = (Repository)l.get(key);
        if (r != null)
        {
          if (type == 1)
          {
            out.print((r.getF1() != null ? r.getF1() : "") + "&nbsp;");
          }
          else if (type == 2)
          {
            out.print((r.getF2() != null ? r.getF2() : "") + "&nbsp;");
          }
          else if (type == 3)
          {
            out.print((r.getF3() != null ? r.getF3() : "") + "&nbsp;");
          }
          else if (type == 4)
          {
            String str = r.getF4();
            if (str != null)
            {
              if (str.length() > 40) {
                str = str.substring(0, 39) + "...";
              }
              out.print(Functions.toHTML(str) + "&nbsp;");
            }
            else
            {
              out.print("&nbsp;");
            }
          }
          else if (type == 5)
          {
            String str = r.getF5();
            if (str != null)
            {
              if (str.length() > 40) {
                str = str.substring(0, 39) + "...";
              }
              out.print(Functions.toHTML(str) + "&nbsp;");
            }
            else
            {
              out.print("&nbsp;");
            }
          }
          out.println("</td>");
        }
      }
    }
    out.println("</tr>");
  }
  
  private int catchType(int index)
  {
    if ((this.typeList == null) || (index > this.typeList.size()) || (this.typeList.get(index) == null)) {
      return 1;
    }
    return ((Integer)this.typeList.get(index)).intValue();
  }
  
  private String doSetIds(Long key)
  {
    if (CollectionUtils.isEmpty(this.valueList)) {
      return "";
    }
    StringBuffer sb = new StringBuffer();
    for (Map<Long, Repository> l : this.valueList) {
      if (l != null)
      {
        Repository r = (Repository)l.get(key);
        if (r != null) {
          sb.append(r.getId() + ",");
        }
      }
    }
    return StringUtils.isNotBlank(sb.toString()) ? sb.substring(0, sb.length() - 1) : sb.toString();
  }
}
