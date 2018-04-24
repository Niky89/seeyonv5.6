package com.seeyon.v3x.hr.util;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.math.NumberUtils;

import com.seeyon.v3x.common.i18n.LocaleContext;
import com.seeyon.v3x.hr.domain.Page;
import com.seeyon.v3x.hr.domain.PageProperty;
import com.seeyon.v3x.hr.domain.PropertyLabel;
import com.seeyon.v3x.hr.domain.Repository;
import com.seeyon.v3x.hr.domain.Salary;
import com.seeyon.v3x.hr.manager.UserDefinedManager;
import com.seeyon.v3x.util.CommonTools;
import com.seeyon.v3x.util.Datetimes;
import com.seeyon.v3x.util.Strings;

public class SalaryUserDefinedHelper
{
  public static Map<Long, List<PageProperty>> getPageProperties(UserDefinedManager userDefinedManager, List<Page> pages)
  {
    Map<Long, List<PageProperty>> pageProperties = new LinkedHashMap();
    if (CollectionUtils.isNotEmpty(pages)) {
      for (Page page : pages)
      {
        List<PageProperty> properties = userDefinedManager.getPropertyByPageId(page.getId());
        if (CollectionUtils.isNotEmpty(properties)) {
          pageProperties.put(page.getId(), properties);
        }
      }
    }
    return pageProperties;
  }
  
  public static List<PageProperty> getPageProperties(UserDefinedManager userDefinedManager)
  {
    List<Page> pages = userDefinedManager.getPageByModelName("salary");
    List<PageProperty> properties = new ArrayList();
    if (CollectionUtils.isNotEmpty(pages)) {
      for (Page page : pages)
      {
        List<PageProperty> pageProperties = userDefinedManager.getPropertyByPageId(page.getId());
        if (CollectionUtils.isNotEmpty(pageProperties)) {
          properties.addAll(pageProperties);
        }
      }
    }
    return properties;
  }
  
  public static Map<Long, String> getPropertyTypes(HttpServletRequest request, UserDefinedManager userDefinedManager, Map<Long, List<PageProperty>> pageProperties)
  {
    List<PageProperty> allProperties = getAllProperties(pageProperties);
    return getPropertyTypes(request, userDefinedManager, allProperties);
  }
  
  public static Map<Long, String> getPropertyTypes(HttpServletRequest request, UserDefinedManager userDefinedManager, List<PageProperty> properties)
  {
    Locale locale = LocaleContext.getLocale(request);
    Map<Long, String> propertyTypes = new HashMap();
    if (CollectionUtils.isNotEmpty(properties))
    {
      List<Long> property_ids = CommonTools.getIds(properties);
      List<PropertyLabel> propertyLabels = userDefinedManager.getPropertyLabelByPropertyIds(property_ids);
      for (PropertyLabel label : propertyLabels) {
        if (locale.equals(Locale.ENGLISH))
        {
          if ("en".equals(label.getLanguage())) {
            propertyTypes.put(label.getProperty_id(), label.getPropertyLabelValue());
          }
        }
        else if ("zh_CN".equals(label.getLanguage())) {
          propertyTypes.put(label.getProperty_id(), label.getPropertyLabelValue());
        }
      }
    }
    return propertyTypes;
  }
  
  public static Repository getRepository(Long memberId, Long pagePropertyId, Long salaryId, List<Repository> allRepository)
  {
    if (allRepository == null) {
      return null;
    }
    for (Repository repository : allRepository) {
      if ((repository.getOperation_id().equals(salaryId)) && (repository.getProperty_id().equals(pagePropertyId))) {
        return repository;
      }
    }
    return null;
  }
  
  public static Map<Long, Map<Long, String>> getSalaryAdminRepositoryPropertyId(List<Long> salaryIds, UserDefinedManager userDefinedManager, List<Salary> salarys, Map<Long, List<PageProperty>> pageProperties)
  {
    List<PageProperty> allProperties = getAllProperties(pageProperties);
    List<Repository> allRepository = userDefinedManager.getSalaryAdminRepositoryPropertyId(salaryIds, CommonTools.getIds(allProperties));
    return setRepositoryValue(salarys, allProperties, allRepository);
  }
  
  public static Map<Long, Map<Long, String>> getPropertyValues(Long userId, UserDefinedManager userDefinedManager, List<Salary> salarys, Map<Long, List<PageProperty>> pageProperties)
  {
    List<PageProperty> allProperties = getAllProperties(pageProperties);
    List<Repository> allRepository = userDefinedManager.getRepositoryByMemberIdAndPropertyIds(userId, CommonTools.getIds(allProperties));
    return setRepositoryValue(salarys, allProperties, allRepository);
  }
  
  public static Map<Long, Map<Long, String>> setRepositoryValue(List<Salary> salarys, List<PageProperty> allProperties, List<Repository> allRepository)
  {
    Map<Long, Map<Long, String>> propertyValues = new HashMap();
    for (Salary salary : salarys) {
      if (salary != null)
      {
        Map<Long, String> salaryPropertyList = new HashMap();
        for (PageProperty property : allProperties)
        {
          Repository repository = getRepository(salary.getStaffId(), property.getId(), salary.getId(), allRepository);
          String value = "";
          if (repository != null) {
            if ((property.getType().longValue() == 1L) && (repository.getF1() != null))
            {
              BigDecimal res = EncryptUtil.decrypt(repository.getMemberId(), repository.getF1().longValue(), 0);
              value = String.valueOf(res);
            }
            else if ((property.getType().longValue() == 2L) && (repository.getF2() != null))
            {
              value = String.valueOf(EncryptUtil.decrypt(repository.getMemberId(), repository.getF2().doubleValue(), 2));
            }
            else if ((property.getType().longValue() == 3L) && (repository.getF3() != null))
            {
              value = String.valueOf(repository.getF3());
            }
            else if ((property.getType().longValue() == 4L) && (repository.getF4() != null))
            {
              value = String.valueOf(repository.getF4());
            }
            else if ((property.getType().longValue() == 5L) && (repository.getF5() != null))
            {
              value = String.valueOf(repository.getF5());
            }
          }
          if ((value == null) || ("null".equals(value))) {
            value = "";
          }
          salaryPropertyList.put(property.getId(), value);
        }
        propertyValues.put(salary.getId(), salaryPropertyList);
      }
    }
    return propertyValues;
  }
  
  public static List<PageProperty> getAllProperties(Map<Long, List<PageProperty>> pageProperties)
  {
    List<PageProperty> allProperties = new ArrayList();
    if (pageProperties != null) {
      for (List<PageProperty> properties : pageProperties.values()) {
        allProperties.addAll(properties);
      }
    }
    return allProperties;
  }
  
  public static void addRepository(int propertyType, String propertyValue, Repository repository)
  {
    boolean isNotBlank = Strings.isNotBlank(propertyValue);
    if (!isNotBlank) {
      return;
    }
    if (propertyType == 1)
    {
      Double f1 = Double.valueOf(isNotBlank ? NumberUtils.toDouble(propertyValue.replaceAll(",", "")) : 0.0D);
      double result = EncryptUtil.encrypt(repository.getMemberId(), f1.doubleValue());
      repository.setF1(Long.valueOf(String.valueOf(result)));
      repository.setF2(Double.valueOf(0.0D));
    }
    else if (propertyType == 2)
    {
      Double f2 = Double.valueOf(isNotBlank ? NumberUtils.toDouble(propertyValue.replaceAll(",", "")) : 0.0D);
      double result = EncryptUtil.encrypt(repository.getMemberId(), f2.doubleValue());
      repository.setF1(Long.valueOf(0L));
      repository.setF2(Double.valueOf(result));
    }
    else if (propertyType == 3)
    {
      Date f3 = isNotBlank ? Datetimes.parse(propertyValue, "yyyy-MM-dd") : new Date();
      repository.setF1(Long.valueOf(0L));
      repository.setF2(Double.valueOf(0.0D));
      repository.setF3(f3);
    }
    else if (propertyType == 4)
    {
      String f4 = isNotBlank ? propertyValue : "";
      repository.setF1(Long.valueOf(0L));
      repository.setF2(Double.valueOf(0.0D));
      repository.setF4(f4);
    }
    else
    {
      String f5 = isNotBlank ? propertyValue : " ";
      repository.setF1(Long.valueOf(0L));
      repository.setF2(Double.valueOf(0.0D));
      repository.setF5(f5);
    }
  }
  
  public static void addRepositorys(int propertyType, String propertyValue, Repository repository)
  {
    boolean isNotBlank = Strings.isNotBlank(propertyValue);
    if (propertyType == 1)
    {
      Double f1 = Double.valueOf(isNotBlank ? NumberUtils.toDouble(propertyValue.replaceAll(",", "")) : 0.0D);
      double result = f1.doubleValue();
      repository.setF1(Long.valueOf(String.valueOf(result)));
      repository.setF2(Double.valueOf(0.0D));
    }
    else if (propertyType == 2)
    {
      Double f2 = Double.valueOf(isNotBlank ? NumberUtils.toDouble(propertyValue.replaceAll(",", "")) : 0.0D);
      double result = f2.doubleValue();
      repository.setF1(Long.valueOf(0L));
      repository.setF2(Double.valueOf(result));
    }
    else if (propertyType == 3)
    {
      Date f3 = isNotBlank ? Datetimes.parse(propertyValue, "yyyy-MM-dd") : new Date();
      repository.setF1(Long.valueOf(0L));
      repository.setF2(Double.valueOf(0.0D));
      repository.setF3(f3);
    }
    else if (propertyType == 4)
    {
      String f4 = isNotBlank ? propertyValue : "";
      repository.setF1(Long.valueOf(0L));
      repository.setF2(Double.valueOf(0.0D));
      repository.setF4(f4);
    }
    else
    {
      String f5 = isNotBlank ? propertyValue : " ";
      repository.setF1(Long.valueOf(0L));
      repository.setF2(Double.valueOf(0.0D));
      repository.setF5(f5);
    }
  }
  
  public static void updateRepository(int propertyType, String propertyValue, Repository repository)
  {
    boolean fNotBlank = Strings.isNotBlank(propertyValue);
    if (propertyType == 1)
    {
      Long f1 = Long.valueOf(fNotBlank ? NumberUtils.toLong(propertyValue) : 0L);
      double result = EncryptUtil.encrypt(repository.getMemberId(), f1.longValue());
      repository.setF1(Long.valueOf(String.valueOf(result)));
    }
    else if (propertyType == 2)
    {
      double f2 = fNotBlank ? NumberUtils.toDouble(propertyValue) : 0.0D;
      double res = EncryptUtil.encrypt(repository.getMemberId(), f2);
      repository.setF2(Double.valueOf(res));
    }
    else if (propertyType == 3)
    {
      Date f3 = fNotBlank ? Datetimes.parse(propertyValue, "yyyy-MM-dd") : new Date();
      repository.setF3(f3);
    }
    else if (propertyType == 4)
    {
      String f4 = fNotBlank ? propertyValue : "";
      repository.setF4(f4);
    }
    else
    {
      String f5 = fNotBlank ? propertyValue : " ";
      repository.setF5(f5);
    }
  }
  
  public static void updateRepositorys(int propertyType, String propertyValue, Repository repository)
  {
    boolean fNotBlank = Strings.isNotBlank(propertyValue);
    if (propertyType == 1)
    {
      Long f1 = Long.valueOf(fNotBlank ? NumberUtils.toLong(propertyValue) : 0L);
      double result = f1.longValue();
      repository.setF1(Long.valueOf(String.valueOf(result)));
    }
    else if (propertyType == 2)
    {
      double f2 = fNotBlank ? NumberUtils.toDouble(propertyValue) : 0.0D;
      double res = f2;
      repository.setF2(Double.valueOf(res));
    }
    else if (propertyType == 3)
    {
      Date f3 = fNotBlank ? Datetimes.parse(propertyValue, "yyyy-MM-dd") : new Date();
      repository.setF3(f3);
    }
    else if (propertyType == 4)
    {
      String f4 = fNotBlank ? propertyValue : "";
      repository.setF4(f4);
    }
    else
    {
      String f5 = fNotBlank ? propertyValue : " ";
      repository.setF5(f5);
    }
  }
}
