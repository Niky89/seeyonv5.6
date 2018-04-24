package com.seeyon.ctp.portal.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.web.servlet.ModelAndView;

import com.seeyon.ctp.common.AppContext;
import com.seeyon.ctp.common.authenticate.domain.User;
import com.seeyon.ctp.common.config.SystemConfig;
import com.seeyon.ctp.common.constants.ProductEditionEnum;
import com.seeyon.ctp.common.constants.SystemProperties;
import com.seeyon.ctp.common.controller.BaseController;
import com.seeyon.ctp.common.exceptions.BusinessException;
import com.seeyon.ctp.common.i18n.ResourceUtil;
import com.seeyon.ctp.login.bo.MenuBO;
import com.seeyon.ctp.portal.manager.CloudAppDataManager;
import com.seeyon.ctp.portal.po.PortalSpaceFix;
import com.seeyon.ctp.portal.space.bo.MenuTreeNode;
import com.seeyon.ctp.portal.space.manager.SpaceManager;
import com.seeyon.ctp.portal.util.Constants;
import com.seeyon.ctp.util.json.JSONUtil;

public class PortalController
  extends BaseController
{
  private SpaceManager spaceManager;
  private CloudAppDataManager appDataManager;
  public static final String CARD_ENABLED = "enable";
  
  public void setSpaceManager(SpaceManager spaceManager)
  {
    this.spaceManager = spaceManager;
  }
  
  public ModelAndView personalInfo(HttpServletRequest request, HttpServletResponse response)
    throws BusinessException
  {
    ModelAndView mv = new ModelAndView("ctp/portal/person/personalWork");
    Integer productId = SystemProperties.getInstance().getIntegerProperty("system.ProductId");
    if (ProductEditionEnum.a6s.ordinal() == productId.intValue()) {
      mv.addObject("productId", "a6s");
    } else {
      mv.addObject("productId", "");
    }
    SystemConfig systemConfig = (SystemConfig)AppContext.getBean("systemConfig");
    String ci = systemConfig.get("card_enable");
    boolean cardEnabled = (ci != null) && ("enable".equals(ci));
    User user = AppContext.getCurrentUser();
    boolean bind = this.appDataManager.isBind(user.getId());
    
    String personModifyPwd = SystemProperties.getInstance().getProperty("person.disable.modify.password");
    mv.addObject("personModifyPwd", Boolean.valueOf("0".equals(personModifyPwd)));
    mv.addObject("isInternal", Boolean.valueOf(user.isInternal()));
    mv.addObject("isBind", Boolean.valueOf(bind));
    mv.addObject("enabled", CloudAppController.getEnabled());
    mv.addObject("cardEnabled", Boolean.valueOf(cardEnabled));
    return mv;
  }
  
  public ModelAndView personalInfoFrame(HttpServletRequest request, HttpServletResponse response)
    throws BusinessException
  {
    ModelAndView mv = new ModelAndView("ctp/portal/person/personalWorkFrame");
    String targetURL = request.getParameter("path");
    if (targetURL.contains("*")) {
      targetURL = targetURL.replace("*", "&");
    }
    mv.addObject("targetURL", targetURL);
    return mv;
  }
  
  public ModelAndView showSpaceNavigation(HttpServletRequest request, HttpServletResponse response)
    throws BusinessException
  {
    ModelAndView mv = new ModelAndView("ctp/portal/person/spaceNavigationSetting");
    User user = AppContext.getCurrentUser();
    Long memberId = user.getId();
    Long accountId = user.getLoginAccount();
    
    List<String[]> accessedPortalSpaceFixList = this.spaceManager.getAccessSpaceSort(memberId, accountId, AppContext.getLocale(), false, null);
    
    List<String[]> accessedThirdpartySpaces = this.spaceManager.getAccessedThirdpartySpace(memberId, accountId, AppContext.getLocale());
    
    List<String[]> accessedLinkSpaces = this.spaceManager.getAccessedLinkSystemSpace(memberId);
    
    List<String[]> accessedRelatedProjects = this.spaceManager.getAccessedRelatedProjectSpace(memberId);
    
    List<String[]> unSelectedSpaces = new LinkedList<String[]>();
    
    List<String[]> unSelectedLinkSpaces = new ArrayList<String[]>();
    
    List<String[]> unSelectedRelatedProjects = new ArrayList<String[]>();
    
    List<String[]> selectedSpaces = new LinkedList<String[]>();
    
    this.spaceManager.screeningSpaces(accessedPortalSpaceFixList, accessedThirdpartySpaces, accessedLinkSpaces, accessedRelatedProjects, unSelectedSpaces, unSelectedLinkSpaces, unSelectedRelatedProjects, selectedSpaces);
    mv.addObject("unSelectedSpaces", unSelectedSpaces);
    mv.addObject("selectedSpaces", selectedSpaces);
    mv.addObject("linkSpaces", unSelectedLinkSpaces);
    mv.addObject("relatedProjects", unSelectedRelatedProjects);
    long currentAccountId = AppContext.currentAccountId();
    boolean canSetDefaultSpace = this.spaceManager.canSetDefaultSpace();
    mv.addObject("currentAccountId", Long.valueOf(currentAccountId));
    mv.addObject("canSetDefaultSpace", Boolean.valueOf(canSetDefaultSpace));
    return mv;
  }
  
  public ModelAndView showMenuSetting(HttpServletRequest request, HttpServletResponse response)
    throws BusinessException
  {
    ModelAndView mv = new ModelAndView("ctp/portal/person/menuSetting");
    User user = AppContext.getCurrentUser();
    
    List<MenuBO> allMenus = this.spaceManager.getMenusOfMember(user);
    List<MenuBO> customizeMenus = this.spaceManager.getCustomizeMenusOfMember(user, allMenus);
    List<MenuBO> unSelectedMenus = this.spaceManager.getUnselectedMenusOfMember(user, allMenus, customizeMenus);
    mv.addObject("unSelectedMenus", unSelectedMenus);
    mv.addObject("customizeMenus", customizeMenus);
    mv.addObject("currentAccountId", Long.valueOf(AppContext.currentAccountId()));
    return mv;
  }
  
  public ModelAndView showShortcutSet(HttpServletRequest request, HttpServletResponse response)
    throws BusinessException
  {
    ModelAndView mv = new ModelAndView("ctp/portal/person/shortcutSetting");
    User user = AppContext.getCurrentUser();
    
    List<MenuTreeNode> allMenus = this.spaceManager.getShortcutsOfMember(user);
    List<MenuTreeNode> customizeMenus = this.spaceManager.getCustomizeShortcuts(user, allMenus);
    Map<String, MenuTreeNode> allMenusMap = new LinkedHashMap<String, MenuTreeNode>();
    for (MenuTreeNode node : allMenus) {
      allMenusMap.put(node.getIdKey(), node);
    }
    MenuTreeNode rootNode = new MenuTreeNode();
    rootNode.setIdKey("menu_0");
    String rootName = ResourceUtil.getString("menu.menuTree.root.label");
    rootNode.setNameKey(rootName);
    rootNode.setpIdKey(null);
    rootNode.setUrlKey(null);
    rootNode.setIconKey(null);
    List<MenuTreeNode> shortcuts = new ArrayList<MenuTreeNode>();
    if (!CollectionUtils.isEmpty(allMenus)) {
      if (CollectionUtils.isEmpty(customizeMenus)) {
        shortcuts = allMenus;
      } else {
        for (MenuTreeNode node : customizeMenus)
        {
          MenuTreeNode shortCut = (MenuTreeNode)allMenusMap.get(node.getIdKey());
          if (shortCut != null)
          {
            shortCut.setChecked(node.getChecked());
            shortCut.setSort(node.getSort());
            shortcuts.add(shortCut);
          }
        }
      }
    }
    shortcuts.add(rootNode);
    request.setAttribute("ffshortcutTree", shortcuts);
    return mv;
  }
  
  public ModelAndView showSystemNavigation(HttpServletRequest request, HttpServletResponse response)
    throws BusinessException
  {
    ModelAndView mv = new ModelAndView("ctp/portal/navigation");
    User user = AppContext.getCurrentUser();
    List<MenuBO> menus = user.getMenus();
    mv.addObject("menus", menus);
    return mv;
  }
  
  public ModelAndView showProductView(HttpServletRequest request, HttpServletResponse response)
    throws BusinessException
  {
    ModelAndView mv = new ModelAndView("ctp/portal/productView");
    User user = AppContext.getCurrentUser();
    List<MenuBO> menus = user.getMenus();
    mv.addObject("menus", menus);
    return mv;
  }
  
  public ModelAndView showProductSencendView(HttpServletRequest request, HttpServletResponse response)
    throws BusinessException
  {
    String pageName = request.getParameter("pageName");
    ModelAndView mv = new ModelAndView("ctp/portal/productView/" + pageName);
    User user = AppContext.getCurrentUser();
    List<MenuBO> menus = user.getMenus();
    mv.addObject("menus", menus);
    return mv;
  }
  
  public ModelAndView saveUserProductViewSet(HttpServletRequest request, HttpServletResponse response)
    throws BusinessException
  {
	  //SELECT * FROM v5x0617.dbo.CTP_CUSTOMIZE
	  //WHERE ckey='productView'
    String isHide = request.getParameter("isHide");
    User user = AppContext.getCurrentUser();
    if ("true".equals(isHide)) {
      user.setCustomize("productView", "true");
    } else {
      user.setCustomize("productView", "false");
    }
    return null;
  }
  
  public void showCorporationSpace(HttpServletRequest request, HttpServletResponse response)
    throws BusinessException, IOException
  {
    User user = AppContext.getCurrentUser();
    Long loginAccountId = user.getLoginAccount();
    PortalSpaceFix space = this.spaceManager.transCreateCorporationSpace(loginAccountId);
    Map<String, String> content = new HashMap<String,String>();
    if (Integer.valueOf(Constants.SpaceState.normal.ordinal()).equals(space.getState()))
    {
      content.put("result", "true");
      content.put("CorporationSpace", String.valueOf(space.getId()));
    }
    else
    {
      content.put("result", "false");
    }
    super.rendText(response, JSONUtil.toJSONString(content));
  }
  
  public ModelAndView sysMenuSortSetting(HttpServletRequest request, HttpServletResponse response)
    throws BusinessException, IOException
  {
    ModelAndView mv = new ModelAndView("ctp/portal/sysMenuSortSetting");
    return mv;
  }
  
  public ModelAndView del(HttpServletRequest request, HttpServletResponse response)
    throws Exception
  {
    ModelAndView mvRepet = new ModelAndView("ctp/portal/person/result");
    User user = AppContext.getCurrentUser();
    this.appDataManager.deleteCloudAppData(user.getId());
    mvRepet.addObject("delvalue", "ok");
    return mvRepet;
  }
  
  public ModelAndView headImgCutting(HttpServletRequest request, HttpServletResponse response)
    throws BusinessException, IOException
  {
    ModelAndView mv = new ModelAndView("ctp/portal/personalHeadImg/headImgCutting");
    return mv;
  }
  
  public CloudAppDataManager getAppDataManager()
  {
    return this.appDataManager;
  }
  
  public void setAppDataManager(CloudAppDataManager appDataManager)
  {
    this.appDataManager = appDataManager;
  }
}
