package com.seeyon.ctp.portal.space.bo;

import com.seeyon.ctp.common.i18n.ResourceUtil;
import com.seeyon.ctp.privilege.bo.PrivMenuBO;
import com.seeyon.ctp.privilege.po.PrivResource;
import com.seeyon.ctp.util.Strings;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MenuTreeNode
  implements Serializable
{
  private static final long serialVersionUID = 4079074195256537027L;
  private String idKey;
  private String pIdKey;
  private String iconKey;
  private String iconSkin = "";
  private String nameKey;
  private String urlKey;
  private String sort;
  private List<MenuTreeNode> items = new ArrayList<MenuTreeNode>();
  private boolean sorted = false;
  private boolean checked = false;
  private String target;
  private boolean expand = true;
  
  public String getTarget()
  {
    return this.target==null?"":this.target;
  }
  
  public void setTarget(String target)
  {
	  if(target==null){
		  this.target="";
	  }else{
		  this.target = target;
	  }
  }
  
  public MenuTreeNode() {}
  
  public MenuTreeNode(PrivMenuBO menuBO)
  {
    this.idKey = ("menu_" + String.valueOf(menuBO.getId()));
    if (menuBO.getParentId() != null) {
      this.pIdKey = ("menu_" + String.valueOf(menuBO.getParentId()));
    }
    this.nameKey = ResourceUtil.getString(menuBO.getName());
    this.iconKey = menuBO.getIcon();
    Long parendId = menuBO.getParentId();
    Integer ext12 = menuBO.getExt12();
    Long ext20 = menuBO.getExt20();
    if ((parendId != null) && (parendId.longValue() == 0L) && (ext12 != null) && (ext12.intValue() == -1))
    {
      this.iconSkin = "treeCustomMenu";
      this.expand = false; //没搞明白ext12 是什么意思。。。也没搞明白为什么这个理要打不开
    }
    else if ((ext12 != null) && (ext12.intValue() == -3))
    {
      this.iconSkin = "treefolderRelatedRystem";
    }
    else
    {
      this.iconSkin = "";
    }
    if ((ext20 != null) && (ext20.longValue() == 1L)) {
      this.checked = true;
    }
    this.urlKey = "";
    this.sort = String.valueOf(menuBO.getSortid());
    this.target = menuBO.getTarget();
  }
  
  public MenuTreeNode(PrivResource res, Long parentId) 
  {
    this.idKey = ("res_" + String.valueOf(res.getId()));
    this.pIdKey = ("menu_" + String.valueOf(parentId));
    this.nameKey = ResourceUtil.getString(res.getResourceName());
    this.urlKey = res.getNavurl();
    this.iconKey = "";
  }
  
  public boolean containsItem(MenuTreeNode o)
  {
    return this.items.contains(o);
  }
  
  public List<MenuTreeNode> getItems()
  {
    if (this.items.size() == 0) {
      return new ArrayList();
    }
    if (!this.sorted)
    {
      Comparator<MenuTreeNode> comparator = new Comparator<MenuTreeNode>()
      {
    	@Override
        public int compare(MenuTreeNode o1, MenuTreeNode o2)
        {
          if ((Strings.isBlank(o1.getSort())) || (Strings.isBlank(o2.getSort()))) {
            return 0;
          }
          int r = Integer.parseInt(o1.getSort()) - Integer.parseInt(o2.getSort());
          return r;
        }
      };
      Collections.sort(this.items, comparator);
      this.sorted = true;
    }
    return this.items;
  }
  
  public void addItem(MenuTreeNode item)
  {
    this.items.add(item);
    this.sorted = false;
  }
  
  public String getIdKey()
  {
    return this.idKey;
  }
  
  public void setIdKey(String idKey)
  {
    this.idKey = idKey;
  }
  
  public String getpIdKey()
  {
    return this.pIdKey;
  }
  
  public void setpIdKey(String pIdKey)
  {
    this.pIdKey = pIdKey;
  }
  
  public String getIconKey()
  {
    return this.iconKey;
  }
  
  public void setIconKey(String iconKey)
  {
    this.iconKey = iconKey;
  }
  
  public String getIconSkin()
  {
    return this.iconSkin;
  }
  
  public void setIconSkin(String iconSkin)
  {
    this.iconSkin = iconSkin;
  }
  
  public String getNameKey()
  {
    return this.nameKey;
  }
  
  public void setNameKey(String nameKey)
  {
    this.nameKey = nameKey;
  }
  
  public String getUrlKey()
  {
    return this.urlKey;
  }
  
  public void setUrlKey(String urlKey)
  {
    this.urlKey = urlKey;
  }
  
  public String getSort()
  {
    return this.sort;
  }
  
  public void setSort(String sort)
  {
    this.sort = sort;
  }
  
  public boolean getChecked()
  {
    return this.checked;
  }
  
  public void setChecked(boolean checked)
  {
    this.checked = checked;
  }
  
  public boolean isExpand()
  {
    return this.expand;
  }
  
  public void setExpand(boolean expand)
  {
    this.expand = expand;
  }
}
