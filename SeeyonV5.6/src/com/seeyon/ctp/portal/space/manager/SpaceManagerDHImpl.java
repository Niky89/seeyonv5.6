package com.seeyon.ctp.portal.space.manager;

import java.util.ArrayList;
import java.util.List;

import com.seeyon.apps.doc.controller.DocController;
import com.seeyon.ctp.common.exceptions.BusinessException;
import com.seeyon.ctp.portal.manager.PortalSYSMenuManager;
import com.seeyon.ctp.portal.space.bo.MenuTreeNode;
import com.seeyon.ctp.privilege.bo.PrivMenuBO;

public class SpaceManagerDHImpl extends SpaceManagerImpl {
	private PortalSYSMenuManager portalSYSMenuManager;

	public void setPortalSYSMenuManager(PortalSYSMenuManager portalSYSMenuManager) {
		this.portalSYSMenuManager = portalSYSMenuManager;
		super.setPortalSYSMenuManager(portalSYSMenuManager);
	}

	public List<MenuTreeNode> getAllUseAbleMenus() throws BusinessException {
		List<MenuTreeNode> menuTree = new ArrayList();

		List<PrivMenuBO> allMenus = this.portalSYSMenuManager.getOrderedSYSMenus();
		MenuTreeNode node = null;
		for (PrivMenuBO pm : allMenus) {
			Long parentId = pm.getParentId();
			Integer ext12 = pm.getExt12();
			//if ((parentId == null) || (parentId.longValue() == 0L) || (ext12 == null) || (ext12.intValue() == 0)
			//		|| (ext12.intValue() == -3)) {
				node = new MenuTreeNode(pm);
				menuTree.add(node);
			//}
		}
		return menuTree;
	}
}
