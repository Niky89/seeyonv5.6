package com.seeyon.v3x.hr.util;

import com.seeyon.v3x.common.i18n.ResourceBundleUtil;
import com.seeyon.v3x.hr.domain.Salary;
import com.seeyon.v3x.hr.webmodel.WebProperty;
import java.util.List;

public class SalaryTableHelper
{
  public static String generateSalarySubject(Salary salary)
  {
    String prefix = ResourceBundleUtil.getString("com.seeyon.v3x.hr.resource.i18n.HRResources", "hr.salary.list.label", new Object[0]);
    return prefix + ": " + salary.getName() + "(" + salary.getYear() + "/" + salary.getMonth() + ")";
  }
  
  public static String generateSalaryTable(Salary salary, List<WebProperty> properties)
  {
    StringBuffer table = new StringBuffer();
    StringBuffer th = new StringBuffer();
    StringBuffer tr = new StringBuffer();
    
    genertateTableTD(th, "姓名");
    genertateTableTD(tr, salary.getName());
    genertateTableTD(th, "工资年月份");
    genertateTableTD(tr, salary.getYear() + "-" + salary.getMonth());
    genertateTableTD(th, "基本工资");
    genertateTableTD(tr, salary.getSalaryBasic());
    genertateTableTD(th, "职位工资");
    genertateTableTD(tr, salary.getSalaryBusiness());
    genertateTableTD(th, "公基金");
    genertateTableTD(tr, salary.getFund());
    genertateTableTD(th, "保险金");
    genertateTableTD(tr, salary.getInsurance());
    genertateTableTD(th, "奖金");
    genertateTableTD(tr, salary.getBonus());
    genertateTableTD(th, "个人所得税");
    genertateTableTD(tr, salary.getIncomeTax());
    genertateTableTD(th, "应发金额");
    genertateTableTD(tr, salary.getSalaryOriginally());
    genertateTableTD(th, "实发金额");
    genertateTableTD(tr, salary.getSalaryActually());
    if ((properties != null) && (!properties.isEmpty())) {
      for (WebProperty property : properties)
      {
        genertateTableTD(th, property.getLabelName_zh());
        switch (property.getPropertyType())
        {
        case 1: 
          genertateTableTD(tr, property.getF1());
          break;
        case 2: 
          genertateTableTD(tr, property.getF2());
          break;
        case 3: 
          genertateTableTD(tr, property.getF3());
          break;
        case 4: 
          genertateTableTD(tr, property.getF4());
          break;
        default: 
          genertateTableTD(tr, property.getF5());
        }
      }
    }
    genertateTableHeader(table);
    genertateTableTR(table, th);
    genertateTableTR(table, tr);
    genertateTableFoot(table);
    return table.toString();
  }
  
  private static void genertateTableHeader(StringBuffer table)
  {
    table.append("<table>");
  }
  
  private static void genertateTableTR(StringBuffer table, StringBuffer tr)
  {
    table.append("<tr>").append(tr).append("</tr>");
  }
  
  private static void genertateTableTD(StringBuffer tr, Object td)
  {
    tr.append("<td>").append(td).append("</td>");
  }
  
  private static void genertateTableFoot(StringBuffer table)
  {
    table.append("</table>");
  }
}
