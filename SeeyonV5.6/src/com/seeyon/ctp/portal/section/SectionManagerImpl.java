package com.seeyon.ctp.portal.section;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.seeyon.ctp.common.AppContext;
import com.seeyon.ctp.common.authenticate.domain.User;
import com.seeyon.ctp.common.exceptions.BusinessException;
import com.seeyon.ctp.common.i18n.ResourceUtil;
import com.seeyon.ctp.common.taglibs.functions.Functions;
import com.seeyon.ctp.portal.po.PortalSpaceFix;
import com.seeyon.ctp.portal.section.bo.SectionTreeNode;
import com.seeyon.ctp.portal.section.manager.BaseSectionSelector;
import com.seeyon.ctp.portal.section.manager.BaseSelectorFactory;
import com.seeyon.ctp.portal.space.manager.PageManager;
import com.seeyon.ctp.portal.space.manager.SpaceManager;
import com.seeyon.ctp.portal.util.Constants;
import com.seeyon.ctp.portal.util.PortletPropertyContants;
import com.seeyon.ctp.portal.util.SpaceFixUtil;
import com.seeyon.ctp.util.Strings;
import com.seeyon.ctp.util.UUIDLong;

import edu.emory.mathcs.backport.java.util.Collections;

public class SectionManagerImpl
  implements SectionManager
{
  private static final Log log = LogFactory.getLog(SectionManagerImpl.class);
  private BaseSelectorFactory selectorFactory;
  private PageManager pageManager;
  private SpaceManager spaceManager;
  
  public void setSpaceManager(SpaceManager spaceManager)
  {
    this.spaceManager = spaceManager;
  }
  
  public void setPageManager(PageManager pageManager)
  {
    this.pageManager = pageManager;
  }
  
  public void setSelectorFactory(BaseSelectorFactory selectorFactory)
  {
    this.selectorFactory = selectorFactory;
  }
  
  public Map<String, Object> doProjection(Map params)
  {
    String sectionBeanId = (String)params.get("sectionBeanId");
    String entityId = (String)params.get("entityId");
    String ordinal = (String)params.get("ordinal");
    String spaceType = (String)params.get("spaceType");
    String spaceId = (String)params.get("spaceId");
    String ownerId = (String)params.get("ownerId");
    String x = (String)params.get("x");
    String y = (String)params.get("y");
    String width = (String)params.get("width");
    String panelId = (String)params.get("panelId");
    String sprint = (String)params.get("sprint");
    List<String> paramKeys = (LinkedList)params.get("paramKeys");
    String[] keys = (String[])null;
    if (CollectionUtils.isEmpty(paramKeys)) {
      paramKeys = new ArrayList();
    }
    paramKeys.add("spaceId");
    if (Strings.isNotBlank(sprint)) {
      paramKeys.add("sprint");
    }
    keys = new String[paramKeys.size()];
    for (int i = 0; i < paramKeys.size(); i++) {
      keys[i] = ((String)paramKeys.get(i));
    }
    List<String> paramValues = (LinkedList)params.get("paramValues");
    String[] values = (String[])null;
    if (CollectionUtils.isEmpty(paramValues)) {
      paramValues = new ArrayList();
    }
    paramValues.add(spaceId);
    if (Strings.isNotBlank(sprint)) {
      paramValues.add(sprint);
    }
    values = new String[paramValues.size()];
    for (int i = 0; i < paramValues.size(); i++) {
      values[i] = ((String)paramValues.get(i));
    }
    if (Strings.isBlank(sectionBeanId)) {
      return null;
    }
    BaseSection baseSection = (BaseSection)AppContext.getBean(sectionBeanId);
    return baseSection.doProjection(entityId, ordinal, spaceType, ownerId, x, y, width, panelId, keys, values);
  }
  
  public List<SectionTreeNode> selectSectionTreeNode(Map params)
    throws BusinessException
  {
    String sectionType = (String)params.get("sectionType");
    String spaceType = (String)params.get("spaceType");
    String spaceId = (String)params.get("spaceId");
    String isMulti = (String)params.get("isMulti");
    String searchWord = (String)params.get("searchWord");
    ArrayList<BaseSectionSelector> selectors = this.selectorFactory.getSectionsBySelector(sectionType);
    List<SectionTreeNode> trees = new ArrayList();
    SectionTreeNode treeNode;
    if (CollectionUtils.isNotEmpty(selectors)) {
      for (BaseSectionSelector selector : selectors)
      {
        List<SectionTreeNode> tree = new ArrayList();
        if (Strings.isNotBlank(searchWord)) {
          tree = selector.selectSectionTreeDataSearch(spaceType, spaceId, searchWord);
        } else {
          tree = selector.selectSectionTreeData(spaceType, spaceId);
        }
        if (CollectionUtils.isNotEmpty(tree)) {
          for (Iterator localIterator2 = tree.iterator(); localIterator2.hasNext();)
          {
            treeNode = (SectionTreeNode)localIterator2.next();
            String sectionName = treeNode.getSectionName();
            String sectionId = treeNode.getId();
            if (Strings.isBlank(sectionName)) {
              log.error("栏目" + sectionId + "名称为空，被移除。");
            } else if ((!"true".equals(isMulti)) || ((!"banner".equals(treeNode.getSectionBeanId())) && 
              (!"weatherSection".equals(treeNode.getSectionBeanId())) && 
              (!"collaborationRemindSection".equals(treeNode.getSectionBeanId())) && 
              (!"shortCutSection".equals(treeNode.getSectionBeanId())) && 
              (!"projectTaskOverviewSection".equals(treeNode.getSectionBeanId())))) {
              trees.add(treeNode);
            }
          }
        }
      }
    }
    boolean project_notShow = Boolean.valueOf(Functions.getSysFlag("project_notShow").toString()).booleanValue();
    Object tempNode = new ArrayList();
    if (project_notShow) {
      for (SectionTreeNode temp : trees) {
        if (!"projectRelevanceSetion".equals(temp.getSectionBeanId())) {
          ((List)tempNode).add(temp);
        }
      }
    } else {
      ((List)tempNode).addAll(trees);
    }
    return (List<SectionTreeNode>)tempNode;
  }
  
  public List<SectionTreeNode> selectedSectionTreeNode(Map params)
    throws BusinessException
  {
    String pagePath = (String)params.get("pagePath");
    String entityId = (String)params.get("entityId");
    String editKeyId = (String)params.get("editKeyId");
    String ownerId = (String)params.get("ownerId");
    String spaceId = (String)params.get("spaceId");
    List<SectionTreeNode> trees = new ArrayList();
    int i;
    if (Strings.isNotBlank(pagePath))
    {
      Long userId = Long.valueOf(AppContext.currentUserId());
      List<Map<String, String>> portletProperties = new ArrayList();
      if (Strings.isNotBlank(editKeyId)) {
        portletProperties = this.pageManager.selectPortletPropertyByPagePath(pagePath, Long.valueOf(editKeyId), userId);
      } else {
        portletProperties = this.pageManager.selectPortletPropertyByPagePath(pagePath, Long.valueOf(UUIDLong.longUUID()), userId);
      }
      if (CollectionUtils.isNotEmpty(portletProperties)) {
        for (Map<String, String> portletProperty : portletProperties)
        {
          String sectionBeanId = (String)portletProperty.get(PortletPropertyContants.PropertyName.sections.name());
          if (!Strings.isBlank(sectionBeanId))
          {
            entityId = (String)portletProperty.get(PortletPropertyContants.PropertyName.entityId.name());
            String[] sectionBeanIds = sectionBeanId.split(",");
            String newSectionBeanId = "";
            String name = "";
            String title = "";
            for ( i = 0; i < sectionBeanIds.length; i++)
            {
              String sectionName = (String)portletProperty.get(PortletPropertyContants.PropertyName.columnsName.name() + ":" + i);
              String singleBoardId = (String)portletProperty.get(PortletPropertyContants.PropertyName.singleBoardId.name() + ":" + i);
              String nameTitle = "";
              BaseSection curSection = (BaseSection)AppContext.getBean(sectionBeanIds[i]);
              if ((curSection != null) && (curSection.isAllowUserUsed(singleBoardId)))
              {
                Map<String, String> preference = new HashMap();
                sectionName = Strings.isNotBlank(sectionName) ? ResourceUtil.getString(sectionName) : sectionName;
                preference.put(PortletPropertyContants.PropertyName.columnsName.name(), sectionName);
                preference.put(PortletPropertyContants.PropertyName.ownerId.name(), ownerId);
                preference.put(PortletPropertyContants.PropertyName.spaceId.name(), spaceId);
                if (Strings.isBlank(singleBoardId))
                {
                  String curSectionName = curSection.getName(preference);
                  if (Strings.isBlank(curSectionName)) {
                    continue;
                  }
                  Map<String, String> preference1 = new HashMap();
                  preference1.put(PortletPropertyContants.PropertyName.spaceId.name(), spaceId);
                  String baseSectionName = curSection.getBaseName(preference1);
                  
                  sectionName = Strings.getSafeLimitLengthString(curSectionName, 32, "...") + "(" + baseSectionName + ")";
                  nameTitle = curSectionName + "(" + baseSectionName + ")";
                }
                else
                {
                  preference.put(PortletPropertyContants.PropertyName.singleBoardId.name(), singleBoardId);
                  String curSectionName = curSection.getName(preference);
                  if (Strings.isBlank(curSectionName)) {
                    continue;
                  }
                  String baseSectionName = curSection.getBaseName(preference);
                  sectionName = Strings.getSafeLimitLengthString(curSectionName, 32, "...") + "(" + baseSectionName + ")";
                  nameTitle = curSectionName + "(" + baseSectionName + ")";
                }
                if (i != 0)
                {
                  newSectionBeanId = newSectionBeanId + sectionBeanIds[i] + ",";
                  name = name + "|" + sectionName;
                  title = title + "|" + nameTitle;
                }
                else
                {
                  newSectionBeanId = newSectionBeanId + sectionBeanIds[i];
                  name = name + sectionName;
                  title = title + nameTitle;
                }
              }
            }
            if ((Strings.isNotBlank(name)) && (Strings.isNotBlank(newSectionBeanId)))
            {
              SectionTreeNode node = new SectionTreeNode();
              node.setId(String.valueOf(UUIDLong.longUUID()));
              node.setSectionBeanId(newSectionBeanId);
              node.setSectionName(name);
              node.setEntityId(entityId);
              node.setTitle(title);
              trees.add(node);
            }
          }
        }
      }
    }
    else if (Strings.isNotBlank(entityId))
    {
      Map<String, String> portletProperty = this.pageManager.selectPortletProperty(Long.valueOf(entityId));
      if (portletProperty != null)
      {
        String sectionBeanId = (String)portletProperty.get(PortletPropertyContants.PropertyName.sections.name());
        entityId = (String)portletProperty.get(PortletPropertyContants.PropertyName.entityId.name());
        String[] sectionBeanIds = sectionBeanId.split(",");
        for (i = 0; i < sectionBeanIds.length; i++)
        {
          String sectionName = (String)portletProperty.get(PortletPropertyContants.PropertyName.columnsName.name() + ":" + i);
          String singleBoardId = (String)portletProperty.get(PortletPropertyContants.PropertyName.singleBoardId.name() + ":" + i);
          String title = sectionName;
          BaseSection curSection = (BaseSection)AppContext.getBean(sectionBeanIds[i]);
          if ((curSection != null) && (curSection.isAllowUserUsed(singleBoardId)))
          {
            Map<String, String> preference = new HashMap();
            sectionName = Strings.isNotBlank(sectionName) ? ResourceUtil.getString(sectionName) : sectionName;
            preference.put(PortletPropertyContants.PropertyName.columnsName.name(), sectionName);
            preference.put(PortletPropertyContants.PropertyName.ownerId.name(), ownerId);
            if (Strings.isBlank(singleBoardId))
            {
              String curSectionName = curSection.getName(preference);
              if (Strings.isBlank(curSectionName)) {
                continue;
              }
              String baseSectionName = curSection.getBaseName(Collections.emptyMap());
              sectionName = Strings.getSafeLimitLengthString(curSectionName, 32, "...") + "(" + baseSectionName + ")";
              title = curSectionName + "(" + baseSectionName + ")";
            }
            else
            {
              preference.put(PortletPropertyContants.PropertyName.singleBoardId.name(), singleBoardId);
              String curSectionName = curSection.getName(preference);
              if (Strings.isBlank(curSectionName)) {
                continue;
              }
              String baseSectionName = curSection.getBaseName(preference);
              sectionName = Strings.getSafeLimitLengthString(curSectionName, 32, "...") + "(" + baseSectionName + ")";
              title = curSectionName + "(" + baseSectionName + ")";
            }
            SectionTreeNode node = new SectionTreeNode();
            node.setId(String.valueOf(UUIDLong.longUUID()));
            node.setSectionBeanId(sectionBeanIds[i]);
            node.setSectionName(sectionName);
            node.setTitle(title);
            node.setEntityId(entityId);
            node.setSingleBoardId(singleBoardId);
            node.setOrdinal(String.valueOf(i));
            trees.add(node);
          }
        }
      }
    }
    List<SectionTreeNode> tempNode = new ArrayList();
    boolean notShowEvent = Boolean.valueOf(Functions.getSysFlag("target_showOnlyTimeManager").toString()).booleanValue();
    if (notShowEvent) {
      for (SectionTreeNode temp : trees) {
        if ((!"projectRelevanceSetion".equals(temp.getSectionBeanId())) && 
          (!"eventCalViewSetion".equals(temp.getSectionBeanId()))) {
          tempNode.add(temp);
        }
      }
    } else {
      tempNode.addAll(trees);
    }
    return tempNode;
  }
  
  public List<SectionTreeNode> selectAsyncSectionTreeNode(Map params)
    throws BusinessException
  {
    String sectionType = (String)params.get("sectionType");
    String spaceType = (String)params.get("spaceType");
    String spaceId = (String)params.get("spaceId");
    String isMulti = (String)params.get("isMulti");
    String nodeId = (String)params.get("nodeId");
    ArrayList<BaseSectionSelector> selectors = this.selectorFactory.getSectionsBySelector(sectionType);
    List<SectionTreeNode> trees = new ArrayList();
    if (CollectionUtils.isNotEmpty(selectors)) {
      for (BaseSectionSelector selector : selectors)
      {
        List<SectionTreeNode> tree = selector.selectSectionTreeData(spaceType, spaceId, nodeId);
        if (CollectionUtils.isNotEmpty(tree)) {
          for (SectionTreeNode treeNode : tree)
          {
            String sectionName = treeNode.getSectionName();
            String sectionId = treeNode.getId();
            if (Strings.isBlank(sectionName)) {
              log.error("栏目" + sectionId + "名称为空，被移除。");
            } else if ((!"true".equals(isMulti)) || (!"banner".equals(treeNode.getSectionBeanId()))) {
              trees.add(treeNode);
            }
          }
        }
      }
    }
    return trees;
  }
  
  public boolean isThisSpaceExist(String pagePath)
  {
    User user = AppContext.getCurrentUser();
    if (user.isAdmin()) {
      return true;
    }
    PortalSpaceFix space = this.spaceManager.getSpaceFix(pagePath);
    if (space != null)
    {
      SpaceFixUtil util = new SpaceFixUtil(space.getExtAttributes());
      if ((util.isAllowdefined()) && (space.getState().intValue() == Constants.SpaceState.normal.ordinal())) {
        return true;
      }
      return false;
    }
    return false;
  }
}
