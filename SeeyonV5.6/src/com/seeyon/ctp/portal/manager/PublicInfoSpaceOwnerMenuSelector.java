package com.seeyon.ctp.portal.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.seeyon.apps.plan.enums.PlanTypeEnum;
import com.seeyon.ctp.common.AppContext;
import com.seeyon.ctp.common.authenticate.domain.User;
import com.seeyon.ctp.common.exceptions.BusinessException;
import com.seeyon.ctp.common.i18n.ResourceUtil;
import com.seeyon.ctp.login.bo.MenuBO;
import com.seeyon.ctp.organization.OrgConstants;
import com.seeyon.ctp.organization.bo.V3xOrgMember;
import com.seeyon.ctp.organization.manager.OrgManager;
import com.seeyon.ctp.portal.po.PortalSpaceFix;
import com.seeyon.ctp.portal.space.manager.SpaceManager;
import com.seeyon.ctp.portal.util.Constants;
import com.seeyon.ctp.privilege.bo.PrivMenuBO;
import com.seeyon.ctp.privilege.manager.PrivilegeCheck;
import com.seeyon.v3x.bbs.domain.V3xBbsBoard;
import com.seeyon.v3x.bbs.manager.BbsBoardManager;
import com.seeyon.v3x.bulletin.domain.BulType;
import com.seeyon.v3x.bulletin.manager.BulDataManager;
import com.seeyon.v3x.bulletin.manager.BulTypeManager;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.inquiry.domain.InquirySurveytype;
import com.seeyon.v3x.inquiry.manager.InquiryManager;
import com.seeyon.v3x.news.domain.NewsType;
import com.seeyon.v3x.news.manager.NewsDataManager;
import com.seeyon.v3x.news.manager.NewsTypeManager;

public class PublicInfoSpaceOwnerMenuSelector extends AbstractSpaceOwnerMenuSelector {
	private static final Log logger = LogFactory.getLog(PublicInfoSpaceOwnerMenuSelector.class);
	private BulDataManager bulDataManager;
	private NewsDataManager newsDataManager;
	private BbsBoardManager bbsBoardManager;
	private InquiryManager inquiryManager;
	private BulTypeManager bulTypeManager;
	private NewsTypeManager newsTypeManager;
	private SpaceManager spaceManager;
	private PrivilegeCheck privilegeCheck;
	private OrgManager orgManager;

	public void setOrgManager(OrgManager orgManager) {
		this.orgManager = orgManager;
	}

	public void setBulDataManager(BulDataManager bulDataManager) {
		this.bulDataManager = bulDataManager;
	}

	public void setNewsDataManager(NewsDataManager newsDataManager) {
		this.newsDataManager = newsDataManager;
	}

	public void setBbsBoardManager(BbsBoardManager bbsBoardManager) {
		this.bbsBoardManager = bbsBoardManager;
	}

	public void setInquiryManager(InquiryManager inquiryManager) {
		this.inquiryManager = inquiryManager;
	}

	public void setBulTypeManager(BulTypeManager bulTypeManager) {
		this.bulTypeManager = bulTypeManager;
	}

	public void setNewsTypeManager(NewsTypeManager newsTypeManager) {
		this.newsTypeManager = newsTypeManager;
	}

	public void setSpaceManager(SpaceManager spaceManager) {
		this.spaceManager = spaceManager;
	}

	public void setPrivilegeCheck(PrivilegeCheck privilegeCheck) {
		this.privilegeCheck = privilegeCheck;
	}

	public List<MenuBO> getSpaceOwnerMenus(String spaceId, String spaceType) throws BusinessException {
		User user = AppContext.getCurrentUser();
		List<MenuBO> menus = new ArrayList();
		try {
			boolean isSpaceManager = this.spaceManager.isManagerOfThisSpace(user.getId(),
					Long.valueOf(Long.parseLong(spaceId)));
			boolean isPublicInfoManager = false;
			V3xBbsBoard board;
			if (("public_custom".equalsIgnoreCase(spaceType)) || ("public_custom_group".equalsIgnoreCase(spaceType))) {
				int spaceTypeInt = "public_custom".equalsIgnoreCase(spaceType)
						? Constants.SpaceType.public_custom.ordinal()
						: Constants.SpaceType.public_custom_group.ordinal();
				isPublicInfoManager = (this.bulDataManager.showManagerMenuOfCustomSpace(user.getId().longValue(),
						Long.parseLong(spaceId), spaceTypeInt))
						|| (this.newsDataManager.showManagerMenuOfCustomSpace(user.getId().longValue(),
								Long.parseLong(spaceId), spaceTypeInt))
						|| (this.inquiryManager.showManagerMenuOfCustomSpace(user.getId(),
								Long.valueOf(Long.parseLong(spaceId)), spaceTypeInt));
				if (!isPublicInfoManager) {
					List<V3xBbsBoard> v3xBbsBoardList = this.bbsBoardManager
							.getAllCustomAccBbsBoard(Long.parseLong(spaceId), spaceTypeInt);
					for (Iterator localIterator = v3xBbsBoardList.iterator(); localIterator.hasNext();) {
						board = (V3xBbsBoard) localIterator.next();
						if (this.bbsBoardManager.validUserIsAdmin(board.getId(), user.getId())) {
							isPublicInfoManager = true;
							break;
						}
					}
				}
			} else if ("corporation".equalsIgnoreCase(spaceType)) {
				isPublicInfoManager = (this.bulDataManager.showManagerMenuOfLoginAccount(user.getId().longValue()))
						|| (this.newsDataManager.showManagerMenuOfLoginAccount(user.getId().longValue()))
						|| (this.inquiryManager.hasManageAuthForAccountSpace(user.getId()));
				if (!isPublicInfoManager) {
					List<V3xBbsBoard> v3xBbsBoardList = this.bbsBoardManager
							.getAllCorporationBbsBoard(user.getLoginAccount().longValue());
					for (V3xBbsBoard board1 : v3xBbsBoardList) {
						if (this.bbsBoardManager.validUserIsAdmin(board1.getId(), user.getId())) {
							isPublicInfoManager = true;
							break;
						}
					}
				}
			} else if ("group".equalsIgnoreCase(spaceType)) {
				isPublicInfoManager = (this.bulTypeManager.isGroupBulTypeManager(user.getId().longValue()))
						|| (this.newsTypeManager.isGroupNewsTypeManager(user.getId().longValue()))
						|| (this.newsTypeManager.isGroupNewsTypeAuth(user.getId().longValue()))
						|| (this.inquiryManager.hasManageAuthForGroupSpace());
				if (!isPublicInfoManager) {
					List<V3xBbsBoard> v3xBbsBoardList = this.bbsBoardManager.getAllGroupBbsBoard();
					for (V3xBbsBoard board1 : v3xBbsBoardList) {
						if (this.bbsBoardManager.validUserIsAdmin(board1.getId(), user.getId())) {
							isPublicInfoManager = true;
							break;
						}
					}
				}
			}
			Map<String, Object> map = null;
			boolean bul = false;
			boolean news = false;
			boolean inq = false;
			boolean bbs = false;
			boolean guest = false;
			if ("corporation".equalsIgnoreCase(spaceType)) {
				map = initWindow("", "", "");
			} else if ("group".equalsIgnoreCase(spaceType)) {
				map = initWindow("true", "", "");
			} else if (("custom".equalsIgnoreCase(spaceType)) || ("public_custom".equalsIgnoreCase(spaceType))
					|| ("public_custom_group".equalsIgnoreCase(spaceType))) {
				map = initWindow("", spaceType, spaceId);
			}
			if (map != null) {
				bul = ((Boolean) map.get("bulMenu")).booleanValue();
				news = ((Boolean) map.get("newsMenu")).booleanValue();
				inq = ((Boolean) map.get("inqMenu")).booleanValue();
				bbs = ((Boolean) map.get("bbsMenu")).booleanValue();
				guest = ((Boolean) map.get("guestMenu")).booleanValue();
			}
			if ("department".equalsIgnoreCase(spaceType)) {
				boolean managerFlag = false;
				managerFlag = this.spaceManager.isManagerOfThisSpace(user.getId(),
						Long.valueOf(Long.parseLong(spaceId)));
				PortalSpaceFix spaceFix = this.spaceManager.getSpaceFix(Long.valueOf(Long.parseLong(spaceId)));
				Long entityId = spaceFix.getEntityId();
				menus.add(getMenuBO(ResourceUtil.getString("guestbook.department.bbs.lable"),
						mergeDepSpaceLink("/bbs.do?method=issuePost&spaceType="
								+ Constants.SpaceType.department.ordinal() + "&showSpaceLacation=true&boardId=",
								String.valueOf(entityId))));
				if (AppContext.hasResourceCode("F02_planCreate")) {
					MenuBO planMenuBO = getMenuBO(ResourceUtil.getString("guestbook.department.plan.lable"),
							"/plan/plan.do?method=newPlan&fromType=department&type="
									+ PlanTypeEnum.anyscope_plan.getValue() + "&refDep=" + entityId);
					planMenuBO.setResourceCode("F02_createDepartmentPlan");
					menus.add(planMenuBO);
				}
				if ((managerFlag) || (canManage(user, entityId, Constants.SpaceType.department.ordinal(),
						OrgConstants.Role_NAME.DepManager.name()))) {
					menus.add(getMenuBO(ResourceUtil.getString("guestbook.department.bulletin.lable"),
							mergeDepSpaceLink("/bulData.do?method=publishListIndex&spaceType="
									+ Constants.SpaceType.department.ordinal() + "&spaceId=" + spaceId + "&bulTypeId=",
									String.valueOf(entityId))));
					menus.add(getMenuBO(ResourceUtil.getString("guestbook.department.bbs.manage.lable"),
							mergeDepSpaceLink(
									"/bbs.do?method=listArticleMain&spaceType="
											+ Constants.SpaceType.department.ordinal() + "&dept=dept&boardId=",
									String.valueOf(entityId))));
					menus.add(
							getMenuBO(ResourceUtil.getString("guestbook.department.bulletin.manage.lable"),
									mergeDepSpaceLink(
											"/bulData.do?method=listMain&spaceType="
													+ Constants.SpaceType.department.ordinal() + "&type=",
											String.valueOf(entityId))));
					menus.add(getMenuBO(ResourceUtil.getString("guestbook.department.leaveword.manage.lable"),
							mergeDepSpaceLink("/guestbook.do?method=moreLeaveWordNew&isManager=true&departmentId=",
									String.valueOf(entityId))));
				}
			}
			if ("corporation".equalsIgnoreCase(spaceType)) {
				if (this.privilegeCheck.checkByReourceCode("F05_bulIndexAccount")) {
					menus.add(
							getMenuBO(ResourceUtil.getString("guestbook.unit.bulletin.lable"),
									mergeSpaceLink(
											"/bulData.do?method=index&spaceType="
													+ Constants.SpaceType.corporation.ordinal() + "&where=space",
											spaceType, spaceId)));
				}
				if (this.privilegeCheck.checkByReourceCode("F05_inquiryIndexAccount")) {
					menus.add(
							getMenuBO(ResourceUtil.getString("guestbook.unit.inquiry.lable"),
									mergeSpaceLink("/inquirybasic.do?method=recent_or_check&spaceType="
											+ Constants.SpaceType.corporation.ordinal() + "&group=account&where=space",
											spaceType, spaceId)));
				}
				if (this.privilegeCheck.checkByReourceCode("F05_bbsIndexAccount")) {
					menus.add(getMenuBO(ResourceUtil.getString("guestbook.unit.bbs.lable"),
							mergeSpaceLink(
									"/bbs.do?method=listLatestFiveArticleAndAllBoard&spaceType="
											+ Constants.SpaceType.corporation.ordinal() + "&where=space",
									spaceType, spaceId)));
				}
				if (this.privilegeCheck.checkByReourceCode("F05_newsIndexAccount")) {
					menus.add(
							getMenuBO(ResourceUtil.getString("guestbook.unit.news.lable"),
									mergeSpaceLink(
											"/newsData.do?method=index&spaceType="
													+ Constants.SpaceType.corporation.ordinal() + "&where=space",
											spaceType, spaceId)));
				}
				if (isPublicInfoManager) {
					if (bul) {
						menus.add(getMenuBO(ResourceUtil.getString("guestbook.unit.bulletin.manage.lable"),
								"/bulData.do?method=listBoard&spaceType=" + Constants.SpaceType.corporation.ordinal()
										+ "&where=space"));
					}
					if (inq) {
						menus.add(getMenuBO(ResourceUtil.getString("guestbook.unit.inquiry.manage.lable"),
								"/inquirybasic.do?method=getAuthoritiesTypeList&spaceType="
										+ Constants.SpaceType.corporation.ordinal()
										+ "&where=space&group=false&spaceId="));
					}
					if (bbs) {
						menus.add(getMenuBO(ResourceUtil.getString("guestbook.unit.bbs.manage.lable"),
								"/bbs.do?method=listAllBoard&spaceType=" + Constants.SpaceType.corporation.ordinal()
										+ "&where=space&group=false&spaceId="));
					}
					if (news) {
						menus.add(getMenuBO(ResourceUtil.getString("guestbook.unit.news.manage.lable"),
								"/newsData.do?method=listBoard&spaceType=" + Constants.SpaceType.corporation.ordinal()
										+ "&where=space"));
					}
				}
			}
			if ("group".equalsIgnoreCase(spaceType)) {
				if (this.privilegeCheck.checkByReourceCode("F05_bulIndexGroup")) {
					menus.add(
							getMenuBO(ResourceUtil.getString("guestbook.group.bulletin.lable"),
									mergeSpaceLink("/bulData.do?method=index&spaceType="
											+ Constants.SpaceType.group.ordinal() + "&where=space", spaceType,
											spaceId)));
				}
				if (this.privilegeCheck.checkByReourceCode("F05_inquiryIndexGroup")) {
					menus.add(getMenuBO(ResourceUtil.getString("guestbook.group.inquiry.lable"),
							mergeSpaceLink(
									"/inquirybasic.do?method=recent_or_check&spaceType="
											+ Constants.SpaceType.group.ordinal() + "&group=group&where=space",
									spaceType, spaceId)));
				}
				if (this.privilegeCheck.checkByReourceCode("F05_bbsIndexGroup")) {
					menus.add(getMenuBO(ResourceUtil.getString("guestbook.group.bbs.lable"),
							mergeSpaceLink(
									"/bbs.do?method=listLatestFiveArticleAndAllBoard&spaceType="
											+ Constants.SpaceType.group.ordinal() + "&group=true&where=space",
									spaceType, spaceId)));
				}
				if (this.privilegeCheck.checkByReourceCode("F05_newsIndexGroup")) {
					menus.add(
							getMenuBO(ResourceUtil.getString("guestbook.group.news.lable"),
									mergeSpaceLink("/newsData.do?method=index&spaceType="
											+ Constants.SpaceType.group.ordinal() + "&where=space", spaceType,
											spaceId)));
				}
				if (isPublicInfoManager) {
					if (bul) {
						menus.add(getMenuBO(ResourceUtil.getString("guestbook.unit.bulletin.manage.lable"),
								"/bulData.do?method=listBoard&spaceType=" + Constants.SpaceType.group.ordinal()
										+ "&where=space"));
					}
					if (inq) {
						menus.add(getMenuBO(ResourceUtil.getString("guestbook.unit.inquiry.manage.lable"),
								"/inquirybasic.do?method=getAuthoritiesTypeList&spaceType="
										+ Constants.SpaceType.group.ordinal() + "&where=space&group=true&spaceId="));
					}
					if (bbs) {
						menus.add(getMenuBO(ResourceUtil.getString("guestbook.unit.bbs.manage.lable"),
								"/bbs.do?method=listAllBoard&spaceType=" + Constants.SpaceType.group.ordinal()
										+ "&where=space&group=true&spaceId="));
					}
					if (news) {
						menus.add(getMenuBO(ResourceUtil.getString("guestbook.unit.news.manage.lable"),
								"/newsData.do?method=listBoard&spaceType=" + Constants.SpaceType.group.ordinal()
										+ "&where=space"));
					}
				}
			}
			if ("custom".equalsIgnoreCase(spaceType)) {
				menus.add(getMenuBO(ResourceUtil.getString("guestbook.custom.bulletin.lable"),
						mergeCustomSpaceLink(
								"/bulData.do?method=bulMore&spaceType=" + Constants.SpaceType.custom.ordinal(),
								spaceType, spaceId)));
				menus.add(getMenuBO(ResourceUtil.getString("guestbook.custom.inquiry.lable"),
						mergeCustomSpaceLink("/inquirybasic.do?method=more_recent_or_check", spaceType, spaceId)));
				menus.add(getMenuBO(ResourceUtil.getString("guestbook.custom.bbs.lable"),
						mergeCustomSpaceLink("/bbs.do?method=deptlistAllArticle", spaceType, spaceId)));
				menus.add(getMenuBO(ResourceUtil.getString("guestbook.custom.news.lable"),
						mergeCustomSpaceLink("/newsData.do?method=newsMore", spaceType, spaceId)));
				if (bul) {
					menus.add(getMenuBO(ResourceUtil.getString("guestbook.unit.bulletin.manage.lable"),
							"/bulData.do?method=listMain&condition=&textfield=&type=" + spaceId + "&spaceType="
									+ Constants.SpaceType.custom.ordinal() + "&custom=&showAudit=&spaceId="));
				}
				if (inq) {
					menus.add(getMenuBO(ResourceUtil.getString("guestbook.unit.inquiry.manage.lable"),
							"/inquirybasic.do?method=survey_index&where=space&surveytypeid=" + spaceId
									+ "&group=&mid=mid&custom=true"));
				}
				if (bbs) {
					menus.add(getMenuBO(ResourceUtil.getString("guestbook.unit.bbs.manage.lable"),
							"/bbs.do?method=listArticleMain&where=space&dept=dept&custom=true&boardId=" + spaceId
									+ "&spaceId=" + spaceId));
				}
				if (news) {
					menus.add(getMenuBO(ResourceUtil.getString("guestbook.unit.news.manage.lable"),
							"/newsData.do?method=listMain&condition=&textfield=&type=" + spaceId + "&spaceType="
									+ Constants.SpaceType.custom.ordinal() + "&spaceId=&showAudit=false&custom=true"));
				}
				if (guest) {
					menus.add(getMenuBO(ResourceUtil.getString("guestbook.leaveword.manage.lable"),
							"/guestbook.do?method=moreLeaveWordNew&custom=true&departmentId=" + spaceId + "&typeId="
									+ spaceId + "&spaceId=" + spaceId + "&spaceType="
									+ Constants.SpaceType.custom.ordinal() + "&type="
									+ Constants.SpaceType.custom.name() + "&from=top"));
				}
			}
			if (("public_custom".equalsIgnoreCase(spaceType)) || ("public_custom_group".equalsIgnoreCase(spaceType))) {
				menus.add(getMenuBO(ResourceUtil.getString("guestbook.custom.bulletin.lable"),
						customAccountAndGroupSpaceLink("/bulData.do?method=index&where=space", spaceType, spaceId)));
				menus.add(getMenuBO(ResourceUtil.getString("guestbook.custom.inquiry.lable"),
						customAccountAndGroupSpaceLink("/inquirybasic.do?method=recent_or_check&where=space", spaceType,
								spaceId)));
				menus.add(
						getMenuBO(ResourceUtil.getString("guestbook.custom.bbs.lable"), customAccountAndGroupSpaceLink(
								"/bbs.do?method=listLatestFiveArticleAndAllBoard&where=space", spaceType, spaceId)));
				menus.add(
						getMenuBO(ResourceUtil.getString("guestbook.custom.news.lable"), customAccountAndGroupSpaceLink(
								"/newsData.do?method=index&orgType=publicCustom&where=space", spaceType, spaceId)));
				if (isPublicInfoManager) {
					if ("public_custom".equalsIgnoreCase(spaceType)) {
						if (bul) {
							menus.add(getMenuBO(ResourceUtil.getString("guestbook.unit.bulletin.manage.lable"),
									"/bulData.do?method=listBoard&where=space&spaceType="
											+ Constants.SpaceType.public_custom.ordinal() + "&spaceId=" + spaceId));
						}
						if (inq) {
							menus.add(getMenuBO(ResourceUtil.getString("guestbook.unit.inquiry.manage.lable"),
									"/inquirybasic.do?method=getAuthoritiesTypeList&where=space&group=false&spaceType="
											+ Constants.SpaceType.public_custom.ordinal() + "&spaceId=" + spaceId));
						}
						if (bbs) {
							menus.add(getMenuBO(ResourceUtil.getString("guestbook.unit.bbs.manage.lable"),
									"/bbs.do?method=listAllBoard&where=space&group=false&spaceType="
											+ Constants.SpaceType.public_custom.ordinal() + "&spaceId=" + spaceId));
						}
						if (news) {
							menus.add(getMenuBO(ResourceUtil.getString("guestbook.unit.news.manage.lable"),
									"/newsData.do?method=listBoard&where=space&spaceType="
											+ Constants.SpaceType.public_custom.ordinal() + "&spaceId=" + spaceId));
						}
					}
					if ("public_custom_group".equalsIgnoreCase(spaceType)) {
						if (bul) {
							menus.add(getMenuBO(ResourceUtil.getString("guestbook.unit.bulletin.manage.lable"),
									"/bulData.do?method=listBoard&where=space&spaceType="
											+ Constants.SpaceType.public_custom_group.ordinal() + "&spaceId="
											+ spaceId));
						}
						if (inq) {
							menus.add(getMenuBO(ResourceUtil.getString("guestbook.unit.inquiry.manage.lable"),
									"/inquirybasic.do?method=getAuthoritiesTypeList&where=space&group=false&spaceType="
											+ Constants.SpaceType.public_custom_group.ordinal() + "&spaceId="
											+ spaceId));
						}
						if (bbs) {
							menus.add(getMenuBO(ResourceUtil.getString("guestbook.unit.bbs.manage.lable"),
									"/bbs.do?method=listAllBoard&where=space&group=false&spaceType="
											+ Constants.SpaceType.public_custom_group.ordinal() + "&spaceId="
											+ spaceId));
						}
						if (news) {
							menus.add(getMenuBO(ResourceUtil.getString("guestbook.unit.news.manage.lable"),
									"/newsData.do?method=listBoard&where=space&spaceType="
											+ Constants.SpaceType.public_custom_group.ordinal() + "&spaceId="
											+ spaceId));
						}
					}
				}
				if (isSpaceManager) {
					menus.add(getMenuBO(ResourceUtil.getString("guestbook.info.board.setting.lable"),
							mergeCustomInfoManageLink("/newsType.do?method=newsManageIndex&flag=account&where=space",
									spaceType, spaceId)));
				}
			}
		} catch (Exception e) {
			logger.error("", e);
		}
		return menus;
	}

	public Map<String, String> getMap(String name, String path) {
		Map<String, String> map = new HashMap();
		map.put("name", name);
		map.put("path", path);
		return map;
	}

	public MenuBO getMenuBO(String name, String path) {
		PrivMenuBO bo = new PrivMenuBO();
		bo.setIdIfNew();
		if (StringUtils.isNotEmpty(name)) {
			bo.setName(name);
		}
		if (StringUtils.isNotEmpty(path)) {
			if (path.indexOf("newsData.do") > -1) {
				bo.setIcon("news.png");
			} else if (path.indexOf("bulData.do") > -1) {
				bo.setIcon("bulletin.png");
			} else if (path.indexOf("bbs.do") > -1) {
				if (path.indexOf("issuePost") > -1) {
					bo.setTarget("newWindow");
				}
				bo.setIcon("bbs.png");
			} else if (path.indexOf("inquirybasic.do") > -1) {
				bo.setIcon("survey.png");
			} else if (path.indexOf("newsType.do") > -1) {
				bo.setIcon("messagemodelsetting.png");
			} else if (path.indexOf("guestbook.do") > -1) {
				bo.setIcon("guestBook.png");
			} else if (path.indexOf("plan.do") > -1) {
				bo.setIcon("newplan.png");
			}
		}
		MenuBO m = new MenuBO(bo);
		if (StringUtils.isNotEmpty(path)) {
			m.setUrl(path);
		}
		return m;
	}

	public MenuBO getMenuBOItem(String name, String path, String spaceType, String spaceId) {
		PrivMenuBO bo = new PrivMenuBO();
		bo.setIdIfNew();
		bo.setName(name);
		MenuBO m = new MenuBO(bo);
		m.setUrl(path);
		Map<String, Object> map = new HashMap();
		if ("corporation".equalsIgnoreCase(spaceType)) {
			map = initWindow("", "", "");
		} else if ("group".equalsIgnoreCase(spaceType)) {
			map = initWindow("true", "", "");
		} else if (("custom".equalsIgnoreCase(spaceType)) || ("public_custom".equalsIgnoreCase(spaceType))
				|| ("public_custom_group".equalsIgnoreCase(spaceType))) {
			map = initWindow("", spaceType, spaceId);
		}
		boolean bul = ((Boolean) map.get("bulMenu")).booleanValue();
		boolean news = ((Boolean) map.get("newsMenu")).booleanValue();
		boolean inq = ((Boolean) map.get("inqMenu")).booleanValue();
		boolean bbs = ((Boolean) map.get("bbsMenu")).booleanValue();
		boolean guest = ((Boolean) map.get("guestMenu")).booleanValue();
		if ("corporation".equalsIgnoreCase(spaceType)) {
			if (bul) {
				m.addItem(getMenuBO(ResourceUtil.getString("guestbook.unit.bulletin.manage.lable"),
						"/bulData.do?method=listBoard&where=space&spaceType=1"));
			}
			if (inq) {
				m.addItem(getMenuBO(ResourceUtil.getString("guestbook.unit.inquiry.manage.lable"),
						"/inquirybasic.do?method=getAuthoritiesTypeList&where=space&group=false&spaceType=&spaceId="));
			}
			if (bbs) {
				m.addItem(getMenuBO(ResourceUtil.getString("guestbook.unit.bbs.manage.lable"),
						"/bbs.do?method=listAllBoard&where=space&group=false&spaceType=&spaceId="));
			}
			if (news) {
				m.addItem(getMenuBO(ResourceUtil.getString("guestbook.unit.news.manage.lable"),
						"/newsData.do?method=listBoard&where=space&spaceType=1"));
			}
		}
		if ("group".equalsIgnoreCase(spaceType)) {
			if (bul) {
				m.addItem(getMenuBO(ResourceUtil.getString("guestbook.unit.bulletin.manage.lable"),
						"/bulData.do?method=listBoard&where=space&spaceType=0"));
			}
			if (inq) {
				m.addItem(getMenuBO(ResourceUtil.getString("guestbook.unit.inquiry.manage.lable"),
						"/inquirybasic.do?method=getAuthoritiesTypeList&where=space&group=true&spaceType=&spaceId="));
			}
			if (bbs) {
				m.addItem(getMenuBO(ResourceUtil.getString("guestbook.unit.bbs.manage.lable"),
						"/bbs.do?method=listAllBoard&where=space&group=true&spaceType=&spaceId="));
			}
			if (news) {
				m.addItem(getMenuBO(ResourceUtil.getString("guestbook.unit.news.manage.lable"),
						"/newsData.do?method=listBoard&where=space&spaceType=0"));
			}
		}
		if ("custom".equalsIgnoreCase(spaceType)) {
			if (bul) {
				m.addItem(getMenuBO(ResourceUtil.getString("guestbook.unit.bulletin.manage.lable"),
						"/bulData.do?method=list&condition=&textfield=&type=" + spaceId + "&spaceType="
								+ Constants.SpaceType.custom.ordinal() + "&custom=&showAudit=&spaceId="));
			}
			if (inq) {
				m.addItem(getMenuBO(ResourceUtil.getString("guestbook.unit.inquiry.manage.lable"),
						"/inquirybasic.do?method=survey_index&where=space&surveytypeid=" + spaceId
								+ "&group=&mid=mid&custom=true"));
			}
			if (bbs) {
				m.addItem(getMenuBO(ResourceUtil.getString("guestbook.unit.bbs.manage.lable"),
						"/bbs.do?method=listArticleMain&where=space&dept=dept&custom=true&boardId=" + spaceId));
			}
			if (news) {
				m.addItem(getMenuBO(ResourceUtil.getString("guestbook.unit.news.manage.lable"),
						"/newsData.do?method=list&condition=&textfield=&type=" + spaceId + "&spaceType="
								+ Constants.SpaceType.custom.ordinal() + "&spaceId=&showAudit=false&custom=true"));
			}
			if (guest) {
				m.addItem(getMenuBO(ResourceUtil.getString("guestbook.leaveword.manage.lable"),
						"/guestbook.do?method=moreLeaveWordNew&custom=true&departmentId=" + spaceId + "&typeId="
								+ spaceId + "&spaceId=" + spaceId + "&spaceType=" + Constants.SpaceType.custom.ordinal()
								+ "&type=" + Constants.SpaceType.custom.name() + "&from=top"));
			}
		}
		if ("public_custom".equalsIgnoreCase(spaceType)) {
			if (bul) {
				m.addItem(getMenuBO(ResourceUtil.getString("guestbook.unit.bulletin.manage.lable"),
						"/bulData.do?method=listBoard&where=space&spaceType="
								+ Constants.SpaceType.public_custom.ordinal() + "&spaceId=" + spaceId));
			}
			if (inq) {
				m.addItem(getMenuBO(ResourceUtil.getString("guestbook.unit.inquiry.manage.lable"),
						"/inquirybasic.do?method=getAuthoritiesTypeList&where=space&group=false&spaceType="
								+ Constants.SpaceType.public_custom.ordinal() + "&spaceId=" + spaceId));
			}
			if (bbs) {
				m.addItem(getMenuBO(ResourceUtil.getString("guestbook.unit.bbs.manage.lable"),
						"/bbs.do?method=listAllBoard&where=space&group=false&spaceType="
								+ Constants.SpaceType.public_custom.ordinal() + "&spaceId=" + spaceId));
			}
			if (news) {
				m.addItem(getMenuBO(ResourceUtil.getString("guestbook.unit.news.manage.lable"),
						"/newsData.do?method=listBoard&where=space&spaceType="
								+ Constants.SpaceType.public_custom.ordinal() + "&spaceId=" + spaceId));
			}
		}
		if ("public_custom_group".equalsIgnoreCase(spaceType)) {
			if (bul) {
				m.addItem(getMenuBO(ResourceUtil.getString("guestbook.unit.bulletin.manage.lable"),
						"/bulData.do?method=listBoard&where=space&spaceType="
								+ Constants.SpaceType.public_custom_group.ordinal() + "&spaceId=" + spaceId));
			}
			if (inq) {
				m.addItem(getMenuBO(ResourceUtil.getString("guestbook.unit.inquiry.manage.lable"),
						"/inquirybasic.do?method=getAuthoritiesTypeList&where=space&group=false&spaceType="
								+ Constants.SpaceType.public_custom_group.ordinal() + "&spaceId=" + spaceId));
			}
			if (bbs) {
				m.addItem(getMenuBO(ResourceUtil.getString("guestbook.unit.bbs.manage.lable"),
						"/bbs.do?method=listAllBoard&where=space&group=false&spaceType="
								+ Constants.SpaceType.public_custom_group.ordinal() + "&spaceId=" + spaceId));
			}
			if (news) {
				m.addItem(getMenuBO(ResourceUtil.getString("guestbook.unit.news.manage.lable"),
						"/newsData.do?method=listBoard&where=space&spaceType="
								+ Constants.SpaceType.public_custom_group.ordinal() + "&spaceId=" + spaceId));
			}
		}
		return m;
	}

	public String mergeDepSpaceLink(String url, String spaceId) {
		User user = AppContext.getCurrentUser();
		if (!"".equals(spaceId)) {
			return url + spaceId;
		}
		if (user.getDepartmentId() != null) {
			return url + user.getDepartmentId();
		}
		return url;
	}

	public String mergeSpaceLink(String url, String spaceType, String spaceId) {
		String shType = "personal";
		if (("department".equals(spaceType)) || ("custom".equals(spaceType))) {
			shType = "corporation";
		} else if (("corporation".equals(spaceType)) || ("group".equals(spaceType))
				|| ("public_custom".equals(spaceType)) || ("public_custom_group".equals(spaceType))) {
			shType = "public_";
		}
		return url + "&space_id=" + spaceId + "&type=" + spaceType + "&showSpace=" + shType;
	}

	public String mergeCustomSpaceLink(String url, String spaceType, String spaceId) {
		String sType = spaceType;
		String shType = "personal";
		if (("department".equals(spaceType)) || ("custom".equals(spaceType))) {
			if ("custom".equals(spaceType)) {
				sType = String.valueOf(Constants.SpaceType.custom.ordinal());
			}
			shType = "corporation";
		} else if (("corporation".equals(spaceType)) || ("group".equals(spaceType))
				|| ("public_custom".equals(spaceType))) {
			shType = "public_";
		}
		return url + "&departmentId=" + spaceId + "&typeId=" + spaceId + "&spaceId=" + spaceId + "&spaceType=" + sType
				+ "&type=" + spaceType + "&showSpace=" + shType + "&custom=true&from=top";
	}

	public String customAccountAndGroupSpaceLink(String url, String spaceType, String spaceId) {
		String shType = "personal";
		int spaceTypeInt = Constants.SpaceType.public_custom_group.ordinal();
		if (("public_custom_group".equals(spaceType)) || ("public_custom".equals(spaceType))) {
			shType = "public_";
		}
		if ("public_custom".equals(spaceType)) {
			spaceTypeInt = Constants.SpaceType.public_custom.ordinal();
		}
		return url + "&spaceType=" + spaceTypeInt + "&spaceId=" + spaceId + "&showSpace=" + shType + "&group=&from=top";
	}

	public String mergeCustomInfoManageLink(String url, String spaceType, String spaceId) {
		String shType = "personal";
		if (("department".equals(spaceType)) || ("custom".equals(spaceType))) {
			shType = "corporation";
		} else if (("corporation".equals(spaceType)) || ("group".equals(spaceType))
				|| ("public_custom".equals(spaceType))) {
			shType = "public_";
		}
		return url + "&departmentId=" + spaceId + "&typeId=" + spaceId + "&spaceId=" + spaceId + "&spaceType="
				+ spaceType + "&type=" + spaceType + "&showSpace=" + shType;
	}

	public Map<String, Object> initWindow(String group, String spaceType, String spaceId) {
		Map<String, Object> map = new HashMap();
		long memberId = AppContext.getCurrentUser().getId().longValue();
		boolean groupFlag = (group != null) && ("true".equals(group));
		boolean bul = false;
		boolean news = false;
		boolean inq = false;
		boolean bbs = false;
		boolean guest = false;
		try {
			if (groupFlag) {
				bul = this.bulTypeManager.isGroupBulTypeManager(memberId);
				if (!bul) {
					bul = this.bulTypeManager.isGroupBulTypeAuth(memberId);
				}
				news = this.newsTypeManager.isGroupNewsTypeManager(memberId);
				if (!news) {
					news = this.newsTypeManager.isGroupNewsTypeAuth(memberId);
				}
				inq = this.inquiryManager.hasManageAuthForGroupSpace();

				List<V3xBbsBoard> v3xBbsBoardList = this.bbsBoardManager.getAllGroupBbsBoard();
				for (V3xBbsBoard board : v3xBbsBoardList) {
					if (this.bbsBoardManager.validUserIsAdmin(board.getId(), Long.valueOf(memberId))) {
						bbs = true;
						break;
					}
				}
			} else if ("custom".equalsIgnoreCase(spaceType)) {
				createPublicInfoType(Long.valueOf(Long.parseLong(spaceId)));

				boolean isSpaceManager = this.spaceManager.isManagerOfThisSpace(Long.valueOf(memberId),
						Long.valueOf(Long.parseLong(spaceId)));
				if (isSpaceManager) {
					bul = true;
					news = true;
					inq = true;
					bbs = true;
					guest = true;
					map.put("custom", Boolean.valueOf(true));
					map.put("spaceTypeInt", Integer.valueOf(Constants.SpaceType.custom.ordinal()));
				}
			} else {
				Object board;
				if (("public_custom".equalsIgnoreCase(spaceType))
						|| ("public_custom_group".equalsIgnoreCase(spaceType))) {
					int spaceTypeInt = "public_custom".equalsIgnoreCase(spaceType)
							? Constants.SpaceType.public_custom.ordinal()
							: Constants.SpaceType.public_custom_group.ordinal();
					bul = this.bulDataManager.showManagerMenuOfCustomSpace(memberId, Long.parseLong(spaceId),
							spaceTypeInt);
					news = this.newsDataManager.showManagerMenuOfCustomSpace(memberId, Long.parseLong(spaceId),
							spaceTypeInt);
					inq = this.inquiryManager.showManagerMenuOfCustomSpace(Long.valueOf(memberId),
							Long.valueOf(Long.parseLong(spaceId)), spaceTypeInt);
					List<V3xBbsBoard> v3xBbsBoardList = this.bbsBoardManager
							.getAllCustomAccBbsBoard(Long.parseLong(spaceId), spaceTypeInt);
					for (Iterator localIterator2 = v3xBbsBoardList.iterator(); localIterator2.hasNext();) {
						board = (V3xBbsBoard) localIterator2.next();
						if (this.bbsBoardManager.validUserIsAdmin(((V3xBbsBoard) board).getId(),
								Long.valueOf(memberId))) {
							bbs = true;
							break;
						}
					}
					map.put("spaceTypeInt", Integer.valueOf(spaceTypeInt));
					map.put("customSpace", Boolean.valueOf(true));
				} else {
					bul = this.bulDataManager.showManagerMenuOfLoginAccount(memberId);

					news = this.newsDataManager.showManagerMenuOfLoginAccount(memberId);
					try {
						inq = this.inquiryManager.hasManageAuthForAccountSpace(Long.valueOf(memberId),
								CurrentUser.get().getLoginAccount().longValue());
					} catch (Exception e) {
						logger.error("", e);
					}
					List<V3xBbsBoard> v3xBbsBoardList = this.bbsBoardManager
							.getAllCorporationBbsBoard(CurrentUser.get().getLoginAccount().longValue());
					for (board = v3xBbsBoardList.iterator(); ((Iterator) board).hasNext();) {
						V3xBbsBoard board1 = (V3xBbsBoard) ((Iterator) board).next();
						if (this.bbsBoardManager.validUserIsAdmin(board1.getId(), CurrentUser.get().getId())) {
							bbs = true;
							break;
						}
					}
				}
			}
		} catch (Exception localException1) {
		}
		map.put("bulMenu", Boolean.valueOf(bul));
		map.put("newsMenu", Boolean.valueOf(news));
		map.put("inqMenu", Boolean.valueOf(inq));
		map.put("bbsMenu", Boolean.valueOf(bbs));
		map.put("guestMenu", Boolean.valueOf(guest));
		map.put("groupFlag", Boolean.valueOf(groupFlag));
		return map;
	}

	private boolean canManage(User user, Long typeId, int spaceType, String roleName) throws BusinessException {
		List<Long> managerEntity = new ArrayList();
		List<PortalSpaceFix> spacePath = this.spaceManager.getAccessSpace(user.getId(), user.getLoginAccount());
		if ((spacePath != null) && (spacePath.size() > 0)) {
			for (PortalSpaceFix portalSpaceFix : spacePath) {
				List<V3xOrgMember> listMember = this.spaceManager.getSpaceMemberBySecurity(portalSpaceFix.getId(),
						spaceType);
				List<Long> members = new ArrayList();
				for (V3xOrgMember member : listMember) {
					members.add(member.getId());
				}
				if ((portalSpaceFix.getType().intValue() == spaceType)
						&& ((this.orgManager.isRole(user.getId(), portalSpaceFix.getEntityId(), roleName,
								new OrgConstants.MemberPostType[0])) || (members.contains(user.getId())))) {
					managerEntity.add(portalSpaceFix.getEntityId());
				}
			}
		}
		return managerEntity.contains(typeId);
	}

	private void createPublicInfoType(Long spaceId) throws Exception {
		PortalSpaceFix fix = this.spaceManager.getSpaceFix(spaceId);

		BulType bulType = this.bulTypeManager.getByDeptId(spaceId);
		if (bulType == null) {
			this.bulTypeManager.saveCustomBulType(spaceId, fix.getSpacename(), fix.getType().intValue());
		}
		NewsType newsType = this.newsTypeManager.getById(spaceId);
		if (newsType == null) {
			this.newsTypeManager.saveCustomNewsType(spaceId, fix.getEntityId(), fix.getSpacename(),
					fix.getType().intValue());
		}
		InquirySurveytype surveytype = this.inquiryManager.getSurveyTypeById(spaceId);
		if (surveytype == null) {
			this.inquiryManager.saveCustomInquirySurveytype(spaceId, fix.getSpacename(), fix.getType().intValue());
		}
		V3xBbsBoard board = this.bbsBoardManager.getBoardById(spaceId);
		if (board == null) {
			this.bbsBoardManager.createDepartmentBbsBoard(spaceId, fix.getEntityId(), fix.getSpacename(),
					fix.getType().intValue());
		}
	}
}
