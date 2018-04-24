package com.seeyon.v3x.hr.manager;

import com.seeyon.v3x.common.exceptions.BusinessException;
import com.seeyon.v3x.hr.domain.Page;
import com.seeyon.v3x.hr.domain.PageLabel;
import com.seeyon.v3x.hr.domain.PageProperties;
import com.seeyon.v3x.hr.domain.PageProperty;
import com.seeyon.v3x.hr.domain.PropertyCategory;
import com.seeyon.v3x.hr.domain.PropertyLabel;
import com.seeyon.v3x.hr.domain.Repository;
import java.util.List;
import java.util.Map;

public abstract interface UserDefinedManager
{
  public abstract Page getPageById(Long paramLong);
  
  public abstract List getAllPropertyByPageId(Long paramLong);
  
  public abstract List getPropertyLabelsByPropertyId(Long paramLong);
  
  public abstract List<PropertyCategory> getCategorysByRemove(int paramInt);
  
  public abstract List<PropertyCategory> getCategorysByRemove(int paramInt, boolean paramBoolean);
  
  public abstract void addCategory(PropertyCategory paramPropertyCategory);
  
  public abstract List getAllCategory();
  
  public abstract PropertyCategory getCategoryById(Long paramLong);
  
  public abstract void deleteCategory(List<Long> paramList);
  
  public abstract void updateCategory(PropertyCategory paramPropertyCategory);
  
  public abstract void addProperties(PageProperties paramPageProperties);
  
  public abstract void updateProperty(PageProperties paramPageProperties);
  
  public abstract void addPageLabel(PageLabel paramPageLabel);
  
  public abstract void addPropertyLabel(PropertyLabel paramPropertyLabel);
  
  public abstract List getAllLanguage();
  
  public abstract void addPageProperty(PageProperty paramPageProperty);
  
  public abstract void updatePageProperty(PageProperty paramPageProperty);
  
  public abstract void deletePageProperty(List<Long> paramList);
  
  public abstract List getAllProperty();
  
  public abstract List getAllPropertyLabel();
  
  public abstract List<PropertyLabel> getPropertyLabelByPropertyId(Long paramLong);
  
  public abstract List<PropertyLabel> getPropertyLabelByPropertyIds(List<Long> paramList);
  
  public abstract PageProperty getPropertyById(Long paramLong);
  
  public abstract void updatePropertyLabel(PropertyLabel paramPropertyLabel);
  
  public abstract List getAllPage();
  
  public abstract List getPageLabelByPageId(Long paramLong);
  
  public abstract void addPage(Page paramPage);
  
  public abstract void deletePage(List<Long> paramList);
  
  public abstract void updatePage(Page paramPage);
  
  public abstract void updatePageLabel(PageLabel paramPageLabel);
  
  public abstract List<PageProperty> getPropertyByPageId(Long paramLong);
  
  public abstract List getPageByPropertyId(Long paramLong);
  
  public abstract void deletePageProperties(Long paramLong);
  
  public abstract void deletePageLabel(Long paramLong);
  
  public abstract void deletePropertyLabel(Long paramLong);
  
  public abstract List<Page> getPageByModelName(String paramString);
  
  public abstract List<Page> getPageByModelName(String paramString, boolean paramBoolean1, boolean paramBoolean2);
  
  public abstract List getPageByRemove(int paramInt);
  
  public abstract List<PageProperty> getPropertyByAccount(Long paramLong);
  
  public abstract List<PageProperty> getPropertyByRemove(int paramInt);
  
  public abstract List<PageProperty> getPropertyByRemove(int paramInt, boolean paramBoolean);
  
  public abstract List getPropertyByRemove(int paramInt1, int paramInt2, int paramInt3)
    throws BusinessException;
  
  public abstract List getPropertyByCategoryId(Long paramLong);
  
  public abstract List getPropertyByCategoryId(Long paramLong, int paramInt);
  
  public abstract void deleteCategoryBYId(Long paramLong);
  
  public abstract void deletePagePropertiesByPropertyId(Long paramLong);
  
  public abstract void deleteRespository(Long paramLong);
  
  public abstract void addRepository(Repository paramRepository);
  
  public abstract List getRepostoryByOperationId(Long paramLong);
  
  public abstract List getRepostoryByPageId(Long paramLong);
  
  public abstract void updateRepository(Repository paramRepository);
  
  public abstract Repository getRepositoryById(Long paramLong);
  
  public abstract void deleteRepositoryByOperationId(Long paramLong);
  
  public abstract void deleteRepositoryByOperationId(List<Long> paramList);
  
  public abstract List getRepositoryByMemberIdAndPropertyId(Long paramLong1, Long paramLong2);
  
  public abstract void deleteRepositoryByIds(List<Long> paramList);
  
  public abstract Map<Long, Repository> getRepositoryByMemberIdAndPropertyIdAndPageId(Long paramLong1, Long paramLong2, Long paramLong3);
  
  public abstract PropertyLabel getPropertyLabelByName(String paramString);
  
  public abstract Repository getRepositoryByMemberIdAndPropertyId(Long paramLong1, Long paramLong2, Long paramLong3);
  
  public abstract List<Repository> getRepositoryByMemberIdAndPropertyIds(Long paramLong, List<Long> paramList);
  
  public abstract List<Repository> getRepositoryPropertyId(List<Long> paramList);
  
  public abstract List<Repository> getSalaryAdminRepositoryPropertyId(List<Long> paramList);
  
  public abstract List<Repository> getSalaryAdminRepositoryPropertyId(List<Long> paramList1, List<Long> paramList2);
  
  public abstract void updatePageOrder(String[] paramArrayOfString)
    throws Exception;
  
  public abstract List<PageProperty> findUnUsePropertyByCategoryId(Long paramLong, int paramInt)
    throws Exception;
  
  public abstract void addAllProperties(List<PageProperties> paramList);
  
  public abstract void initHrData(Long paramLong);
  
  public abstract void addAllRepository(List<Repository> paramList)
    throws Exception;
  
  public abstract void updateAllRepository(List<Repository> paramList)
    throws Exception;
  
  public abstract void updateSalaryForTransfer(List<Long> paramList, String paramString)
    throws Exception;
}
