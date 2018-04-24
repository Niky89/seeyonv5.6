package com.deheng.v3x.hr.controller;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.servlet.ModelAndView;

import com.seeyon.apps.ldap.config.LDAPConfig;
import com.seeyon.ctp.common.AppContext;
import com.seeyon.ctp.common.SystemEnvironment;
import com.seeyon.ctp.common.appLog.AppLogAction;
import com.seeyon.ctp.common.authenticate.domain.User;
import com.seeyon.ctp.common.constants.ApplicationCategoryEnum;
import com.seeyon.ctp.common.ctpenum.manager.EnumManager;
import com.seeyon.ctp.common.filemanager.manager.AttachmentManager;
import com.seeyon.ctp.common.po.ctpenum.CtpEnum;
import com.seeyon.ctp.common.po.ctpenum.CtpEnumItem;
import com.seeyon.ctp.organization.bo.MemberPost;
import com.seeyon.ctp.organization.bo.V3xOrgDepartment;
import com.seeyon.ctp.organization.bo.V3xOrgLevel;
import com.seeyon.ctp.organization.bo.V3xOrgMember;
import com.seeyon.ctp.organization.bo.V3xOrgPost;
import com.seeyon.ctp.organization.bo.V3xOrgPrincipal;
import com.seeyon.ctp.organization.bo.V3xOrgUnit;
import com.seeyon.ctp.organization.webmodel.WebV3xOrgMember;
import com.seeyon.ctp.organization.webmodel.WebV3xOrgModel;
import com.seeyon.ctp.util.CommonTools;
import com.seeyon.ctp.util.Strings;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.hr.controller.HrStaffInfoController;
import com.seeyon.v3x.hr.domain.ContactInfo;
import com.seeyon.v3x.hr.domain.Page;
import com.seeyon.v3x.hr.domain.PageLabel;
import com.seeyon.v3x.hr.domain.Relationship;
import com.seeyon.v3x.hr.domain.RewardsAndPunishment;
import com.seeyon.v3x.hr.domain.StaffInfo;
import com.seeyon.v3x.hr.util.Constants;
import com.seeyon.v3x.hr.webmodel.WebProperty;
import com.seeyon.v3x.hr.webmodel.WebStaffInfo;

public class HrStaffInfo_lawyer_Controller extends HrStaffInfoController {
	private static final transient Log LOG = LogFactory.getLog(HrStaffInfo_lawyer_Controller.class);
	private AttachmentManager attachmentManager;
	private EnumManager enumManager;

	public EnumManager getEnumManager() {
		return enumManager;
	}

	public void setEnumManager(EnumManager enumManager) {
		this.enumManager = enumManager;
		super.setEnumManager(enumManager);
	}

	public AttachmentManager getAttachmentManager() {
		return attachmentManager;
	}

	public void setAttachmentManager(AttachmentManager attachmentManager) {
		this.attachmentManager = attachmentManager;
		super.setAttachmentManager(this.attachmentManager);
	}

	/**
	 * 更新人员关系表
	 */
	@Override
	public ModelAndView updateRelationship(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String staffid = request.getParameter("staffId");
		User user = AppContext.getCurrentUser();

		V3xOrgMember member = this.getOrgManager().getMemberById(Long.valueOf(Long.parseLong(staffid)));
		String ID = request.getParameter("id");
		if ((ID == null) || (ID.equals(""))) {
			Relationship relationship = new Relationship();
			bind(request, relationship);
			String birthday = request.getParameter("birthday");
			String cardid = request.getParameter("cardid");
			if (StringUtils.isNotBlank(birthday)) {
				relationship.setBirthday(new SimpleDateFormat("yyyy-MM-dd").parse(birthday));
			} else {
				relationship.setBirthday(null);
			}
			relationship.setCardid(cardid);

			relationship.setMember_id(Long.valueOf(staffid));
			this.getStaffInfoManager().addRelationship(relationship);
			this.getOperationlogManager().insertOplog(member.getOrgAccountId(), Constants.MODULE_STAFF,
					ApplicationCategoryEnum.hr, "hr.staffInfo.other.add.label", "hr.staffInfo.Relationship.add.label",
					new Object[] { member.getName() });
		} else {
			Relationship rela = this.getStaffInfoManager().getRelationshipById(Long.valueOf(ID));

			bind(request, rela);
			String birthday = request.getParameter("birthday");
			if (StringUtils.isNotBlank(birthday)) {
				rela.setBirthday(new SimpleDateFormat("yyyy-MM-dd").parse(birthday));
			} else {
				rela.setBirthday(null);
			}
			this.getStaffInfoManager().updateRelationship(rela);
			this.getOperationlogManager().insertOplog(member.getOrgAccountId(), Constants.MODULE_STAFF,
					ApplicationCategoryEnum.hr, "hr.staffInfo.other.update.label",
					"hr.staffInfo.Relationship.update.label", new Object[] { member.getName(), });
		}
		String isManager = "";
		String Manager = request.getParameter("Manager");
		if ((Manager != null) && (Manager.equals("true"))) {
			isManager = "Manager";
		}
		user.setRemoteAddr(request.getRemoteAddr());
		this.getAppLogManager().insertLog(user, AppLogAction.Hr_UpdateStaffInfo,
				new String[] { user.getName(), member.getName() });

		return super.redirectModelAndView(
				"/hrStaff.do?method=initHome&infoType=3&staffId=" + staffid + "&isManager=" + isManager,
				"parent.parent");
	}

	/**
	 * 初始化人员档案页面
	 */
	public ModelAndView initInfoHome(HttpServletRequest request, HttpServletResponse response) throws Exception {
		// 增加判断条件 如果是从首页过来的链接无论有无标记皆可通过
		String fromTop = request.getParameter("fromtop");
		if (fromTop == null || !fromTop.equals("top")) {
			if (!AppContext.hasResourceCode("F03_hrStaff")) {// 如果不包含这个代码,无法进入修改界面,
				System.out.println("不包含资源代码F03_hrStaff?????????????????????");
				// return null;// 暂时去掉权限控制,所有人都能通过url访问.将来根据需求在此处添加可控制的权限功能.
			}
		}

		ModelAndView mav = new ModelAndView("hr/staffInfo/infohome");
		mav.addObject("fromTop", fromTop);
		String staffid = request.getParameter("staffId");
		Long staffId = null;
		if ((staffid != null) && (!"".equals(staffid))) {
			staffId = Long.valueOf(staffid);
		} else {
			staffId = CurrentUser.get().getId();
		}
		mav.addObject("staffId", staffId);

		int infotype = 1;
		String str = request.getParameter("infoType");
		if (str != null) {
			infotype = Integer.parseInt(str);
		}
		List<WebProperty> webPages = new ArrayList<WebProperty>();
		List<Page> pages = this.getUserDefinedManager().getPageByModelName("staff");
		for (Page page : pages) {
			WebProperty webPage = new WebProperty();
			List<PageLabel> labels = this.getUserDefinedManager().getPageLabelByPageId(page.getId());
			for (PageLabel label : labels) {
				if (label.getLanguage().equals("zh_CN")) {
					webPage.setPageName_zh(label.getPageLabelValue());
				} else if (label.getLanguage().equals("en")) {
					webPage.setPageName_en(label.getPageLabelValue());
				}
			}
			webPage.setPage_id(page.getId());
			webPage.setPageNo(page.getPageNo());
			webPages.add(webPage);
		}
		String pageID = request.getParameter("page_id");
		if ((pageID != null) && (!"".equals(pageID))) {
			infotype = 0;
			mav.addObject("page_id", Long.valueOf(pageID));
		}
		String isNew = request.getParameter("isNew");
		mav.addObject("webPages", webPages);
		mav.addObject("infoType", Integer.valueOf(infotype));
		mav.addObject("isManager", request.getParameter("isManager"));
		mav.addObject("isReadOnly", request.getParameter("isReadOnly"));
		mav.addObject("isNew", isNew);
		if ((isNew != null) && (isNew.equals("New"))) {
			mav.addObject("secondPostList", "");
		} else {
			V3xOrgMember member = this.getOrgManager().getMemberById(staffId);
			if (member != null) {
				List<MemberPost> memberPosts = member.getSecond_post();
				List<WebV3xOrgModel> secondPostList = new ArrayList<WebV3xOrgModel>();
				if ((memberPosts != null) && (!memberPosts.isEmpty())) {
					StringBuffer deptpostbuffer = new StringBuffer();
					StringBuffer deptpostbufferId = new StringBuffer();
					for (MemberPost memberPost : memberPosts) {
						WebV3xOrgModel webModel = new WebV3xOrgModel();
						StringBuffer sbuffer = new StringBuffer();
						StringBuffer sbufferId = new StringBuffer();
						Long deptid = memberPost.getDepId();
						V3xOrgDepartment v3xdept = this.getOrgManager().getDepartmentById(deptid);
						sbuffer.append(v3xdept.getName());
						sbuffer.append("-");
						sbufferId.append(v3xdept.getId());
						sbufferId.append("_");
						Long postid = memberPost.getPostId();
						V3xOrgPost v3xpost = this.getOrgManager().getPostById(postid);
						sbuffer.append(v3xpost.getName());
						sbufferId.append(v3xpost.getId());
						deptpostbuffer.append(sbuffer.toString());
						deptpostbuffer.append(",");
						deptpostbufferId.append(sbufferId.toString());
						deptpostbufferId.append(",");
						webModel.setSecondPostId(v3xdept.getId() + "_" + v3xpost.getId());
						webModel.setSecondPostType("Department_Post");
						secondPostList.add(webModel);
					}
				}
				mav.addObject("secondPostList", secondPostList);
			}
		}
		if ("1".equals(request.getParameter("load"))) {
			return initSpace(request, response);
		}
		return mav;
	}

	/**
	 * 初始化人员进本信息,其实这一块儿可以不要的直接用父类的方法
	 */
	@Override
	public ModelAndView initSpace(HttpServletRequest request, HttpServletResponse response) throws Exception {

		ModelAndView mav = new ModelAndView();
		int infotype = 1;
		String str = request.getParameter("infoType");
		if (str != null) {
			infotype = Integer.parseInt(str);
		}
		if (infotype == 1) {
			mav = initStafferInfo(request, response);// 人员信息表
		} else if (infotype == 2) {
			mav = initContactInfo(request, response);// 联系信息
		} else {
			mav = initHome(request, response);
		}
		String fromTop = request.getParameter("fromTop");
		mav.addObject("fromTop", fromTop);
		return mav;
	}

	/**
	 * 不知道个什么主页
	 */
	@Override
	public ModelAndView initHome(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView("hr/staffInfo/home");
		int infotype = Integer.parseInt(request.getParameter("infoType"));
		String listType = "";
		if (infotype == 3) {
			listType = "Relationship";
		} else if (infotype == 4) {
			listType = "WorkRecord";
		} else if (infotype == 5) {
			listType = "EduExperience";
		} else if (infotype == 6) {
			listType = "PostChange";
		} else if (infotype == 7) {
			listType = "Assess";
		} else if (infotype == 8) {
			listType = "RewardsAndPunishment";
		}
		String fromTop = request.getParameter("fromTop");
		mav.addObject("fromTop", fromTop);
		mav.addObject("listType", listType);
		mav.addObject("isManager", request.getParameter("isManager"));
		mav.addObject("staffId", request.getParameter("staffId"));
		return mav;
	}

	/**
	 * 更新基本信息
	 */
	@Override
	public ModelAndView updateStaffer(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mav = super.updateStaffer(request, response);
		String fromTop = request.getParameter("fromTop");
		mav.addObject("fromTop", fromTop);
		String url = (String) mav.getModelMap().get("redirectURL");
		url += "&fromTop=" + fromTop;
		mav.addObject("redirectURL", url);
		return mav;
	}

	/**
	 * 基本信息
	 */
	@Override
	public ModelAndView initStafferInfo(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView("hr/staffInfo/staffInfo");
		String fromTop = request.getParameter("fromTop");
		mav.addObject("fromTop", fromTop);
		StaffInfo staffInfo = new StaffInfo();
		V3xOrgMember member = new V3xOrgMember();
		String isNew = request.getParameter("isNew");
		if ((isNew != null) && (isNew.equals("New"))) {
			mav.addObject("staff", staffInfo);
			Integer sid = this.getOrgManager().getMaxMemberSortByAccountId(CurrentUser.get().getLoginAccount());
			member.setSortId(Long.valueOf(Long.valueOf(sid.toString()).longValue() + 1L));
			member.setIdIfNew();
			mav.addObject("member", member);

			V3xOrgPrincipal p = new V3xOrgPrincipal(member.getId(), "", "123456");
			member.setV3xOrgPrincipal(p);

			String securityIds = null;
			String securityNames = null;

			mav.addObject("securityIds", securityIds);
			mav.addObject("securityNames", securityNames);
		} else {
			mav.addObject("editstate", Boolean.valueOf(true));
			Long staffid = Long.valueOf(Long.parseLong(request.getParameter("staffId")));
			member = this.getOrgManager().getMemberById(staffid);
			staffInfo = this.getStaffInfoManager().getStaffInfoById(staffid);
			if (member != null) {
				String imageId = "";
				String imagesrc = null;
				Object property = member.getProperty("imageid");
				if (property != null) {
					imagesrc = member.getProperty("imageid").toString();
					if (Strings.isNotBlank(imagesrc)) {
						mav.addObject("image", Integer.valueOf(0));
					}
				}
				mav.addObject("imgsrc", imagesrc);
				if (staffInfo != null) {
					if (StringUtils.isNotBlank(imagesrc)) {
						mav.addObject("image", Integer.valueOf(0));
						mav.addObject("imgsrc", imagesrc);
						if (staffInfo.getImage_id() != null) {
							imageId = String.valueOf(staffInfo.getImage_id());
						}
					}
					if ((staffInfo.getPolitical_position() == -1) || (staffInfo.getPolitical_position() == 0)) {
						staffInfo.setPolitical_position(2);
					}
					mav.addObject("attachments",
							this.attachmentManager.getByReference(staffInfo.getId(), staffInfo.getId()));
				} else {
					staffInfo = new StaffInfo();
					staffInfo.setPolitical_position(2);
				}
				mav.addObject("imageId", imageId);
				Locale memberLocale = this.getOrgManagerDirect().getMemberLocaleById(staffid);
				mav.addObject("memberLocale", memberLocale);
			}
			mav.addObject("staff", staffInfo);
			mav.addObject("member", member);
			if (member != null) {
				WebStaffInfo webMember = translateV3xOrgMember(member);
				mav.addObject("webMember", webMember);
			}
			String securityIds = null;
			String securityNames = null;

			mav.addObject("securityIds", securityIds);
			mav.addObject("securityNames", securityNames);
		}
		Map<String, CtpEnum> orgMeta = this.enumManager.getEnumsMap(ApplicationCategoryEnum.organization);
		mav.addObject("orgMeta", orgMeta);
		Map<String, CtpEnum> hrMetadata = this.enumManager.getEnumsMap(ApplicationCategoryEnum.hr);
		mav.addObject("hrMetadata", hrMetadata);

		List<V3xOrgLevel> levels = this.getOrgManagerDirect().getAllLevels(CurrentUser.get().getLoginAccount(), false);
		List<V3xOrgLevel> levelsForPage = new ArrayList<V3xOrgLevel>();

		Integer minLevelId = Integer.valueOf(1);
		for (V3xOrgLevel level : levels) {
			if (level.getEnabled().booleanValue()) {
				levelsForPage.add(level);
				if (minLevelId.intValue() < level.getLevelId().intValue()) {
					minLevelId = level.getLevelId();
				}
			}
		}
		mav.addObject("levels", levelsForPage);
		mav.addObject("minLevelId", minLevelId);

		String isReadOnly = request.getParameter("isReadOnly");
		boolean readOnly = false;
		if ((isReadOnly != null) && (isReadOnly.equals("ReadOnly"))) {
			readOnly = true;
		}
		mav.addObject("ReadOnly", Boolean.valueOf(readOnly));

		String isManager = request.getParameter("isManager");
		boolean manager = false;
		if ("Manager".equals(isManager)) {
			manager = true;
		}
		String Manager = request.getParameter("Manager");
		if ((Manager != null) && (Manager.equals("true"))) {
			manager = true;
		}
		if (LDAPConfig.getInstance().getIsEnableLdap()) {
			if (SystemEnvironment.hasPlugin("ldap")) {
				try {
					if ((isNew != null) && (isNew.equals("New"))) {
						mav.addObject("addstate", Boolean.valueOf(true));
					} else {
						mav.addObject("editstate", Boolean.valueOf(true));
					}
					mav.addObject("hasLDAPAD", Boolean.valueOf(true));
				} catch (Exception e) {
					LOG.error("ldap_ad", e);
				}
			}
		}
		mav.addObject("isManager", isManager);
		mav.addObject("Manager", Boolean.valueOf(manager));
		mav.addObject("isNew", isNew);
		return mav;
	}

	/**
	 * 联系信息
	 */
	@Override
	public ModelAndView initContactInfo(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Long staffid = Long.valueOf(Long.parseLong(request.getParameter("staffId")));
		ModelAndView mav = new ModelAndView("hr/staffInfo/contactInfo");
		ContactInfo contactInfo = this.getStaffInfoManager().getContactInfoById(staffid);
		V3xOrgMember member = this.getOrgManager().getMemberById(staffid);
		if (contactInfo == null) {
			contactInfo = new ContactInfo();
		}

		contactInfo.setTelephone(member.getTelNumber());
		contactInfo.setEmail(member.getEmailAddress());

		mav.addObject("contactInfo", contactInfo);

		mav.addObject("member", member);

		String officeNum = member.getOfficeNum();
		mav.addObject("officeNum", officeNum);

		String isReadOnly = request.getParameter("isReadOnly");
		boolean readOnly = false;
		if ((isReadOnly != null) && (isReadOnly.equals("ReadOnly"))) {
			readOnly = true;
		}
		mav.addObject("ReadOnly", Boolean.valueOf(readOnly));

		mav.addObject("isManager", request.getParameter("isManager"));
		return mav;
	}

	/**
	 * hr管理列表,此处应该修改其中的权限以应付现在需要的权限.
	 * 
	 * 这个地方用的是默认的权限分组,加入进去之后就就有了根据accountid 获取人员列表的权限,即根据公司获取的权利
	 * 
	 * 此处做修改 ,增加角色分组配置一个新的角色分组来进行判断,如果是此分组人员即按照部门进行获取人员列表,
	 * 部门级别全部规定为account下的第一级部门即最高级别部门
	 * 
	 */
	@Override
	public ModelAndView initStaffInfoList(HttpServletRequest request, HttpServletResponse response) throws Exception {
		boolean b = false;
		boolean isDep = false;
		boolean isDepLead = false;
		int i = 0;
		long textfieldLong = 0L;
		Integer textfieldInteger = Integer.valueOf(0);
		try {
			User user = AppContext.getCurrentUser();// 从系统中获取当前用户
			/* 测试更新member */
			// V3xOrgMember v3xOrgmember =
			// this.getOrgManager().getMemberById(user.getId());//
			// 根据当前用户的id获取当前用户的orgmember
			// v3xOrgmember.setProperty("weixin", "123");
			// this.getOrgManagerDirect().updateMember(v3xOrgmember);
			// 下面进行权限的判断.
			// 获取部门hr管理员列表
			List<V3xOrgMember> entitys = this.getOrgManager().getMembersByRole(Long.valueOf(user.getAccountId()),
					"部门HR管理员");
			// 遍历之查看是否存在.存在即使用部门列表,否则使用默认
			for (V3xOrgMember om : entitys) {
				if (om.getId().longValue() == user.getId().longValue()) {
					isDep = true;
					break;// 判断存在
				}
			}
			if (!isDep) {
				// 检查是否存在部门主任角色.如果存在.部门主任可以查看本部门的人员信息
				entitys = this.getOrgManager().getMembersByRole(Long.valueOf(user.getAccountId()), "部门主任");
				// 遍历之查看是否存在.存在即使用部门列表,否则使用默认
				for (V3xOrgMember om : entitys) {
					if (om.getId().longValue() == user.getId().longValue()) {
						isDepLead = true;
						break;// 判断存在
					}
				}
			}

			Long departmentId = user.getDepartmentId();// 此处的id应该取本部门最高级别的id
			// 对其进行处理 000000020040
			V3xOrgUnit v3xorgUnit = this.getOrgManager().getUnitById(departmentId);

			String unitpath = v3xorgUnit.getPath();
			String topPath = "";
			if (isDepLead) {
				topPath = unitpath;
				isDep = true;// 在此处设置为真.方便后面使用.
			} else {
				topPath = unitpath.substring(0, 12);// 获取顶端部门
			}
			departmentId = this.getOrgManager().getDepartmentByPath(topPath).getId();// 获取顶端id

			String condition = request.getParameter("condition");
			String textfield = request.getParameter("textfield");
			String textfield1 = request.getParameter("textfield1");
			String textVlaue = "";
			if ((textfield == null) || ("".equals(textfield))) {
				condition = null;
				textVlaue = null;
				textfield = null;
			}
			Boolean enable = Boolean.valueOf(true);
			if ((textfield != null) && (!"".equals(textfield))) {
				enable = null;
				textVlaue = textfield;
				if (("orgDepartmentId".equals(condition)) || ("orgLevelId".equals(condition))
						|| ("orgPostId".equals(condition))) {
					textfieldLong = Long.valueOf(textfield).longValue();
					b = true;
				}
				if (("type".equals(condition)) || ("state".equals(condition))) {
					textfieldInteger = Integer.valueOf(Integer.parseInt(textfield));
					i = 1;
					b = true;
				}
			}
			if (textfield1 != null) {
				request.setAttribute("textfield1s", textfield1);
			}
			List<V3xOrgMember> memberlist = null;
			Long accountId = user.getLoginAccount();
			if (b) {
				if (i == 1) {
					if (isDep) {
						memberlist = this.getOrgManager().getAllMembersByDepartmentId(departmentId, true, null, null,
								true, condition, textfieldInteger, null);
					} else {
						memberlist = this.getOrgManager().getAllMembersByAccountId(accountId, null,
								Boolean.valueOf(true), enable, condition, textfieldInteger, null);
					}
					request.setAttribute("textfileds", textfieldInteger);
				} else {
					if (isDep) {
						memberlist = this.getOrgManager().getAllMembersByDepartmentId(departmentId, true, null, null,
								true, condition, Long.valueOf(textfieldLong), null);
					} else {
						memberlist = this.getOrgManager().getAllMembersByAccountId(accountId, null,
								Boolean.valueOf(true), enable, condition, Long.valueOf(textfieldLong), null);
					}
					request.setAttribute("textfileds", Long.valueOf(textfieldLong));
				}
			} else {
				if (isDep) {
					memberlist = this.getOrgManager().getAllMembersByDepartmentId(departmentId, true, null, null, true,
							condition, textVlaue, null);
				} else {
					memberlist = this.getOrgManager().getAllMembersByAccountId(accountId, null, Boolean.valueOf(true),
							enable, condition, textVlaue, null);
				}
				request.setAttribute("textfileds", textVlaue);
			}
			List<V3xOrgMember> newMembers = new ArrayList();
			for (V3xOrgMember v : memberlist) {
				if (!v.getIsAdmin().booleanValue()) {
					newMembers.add(v);
				}
			}
			memberlist = CommonTools.pagenate(newMembers);

			ModelAndView result = new ModelAndView("hr/staffInfo/staffInfoList");

			Object resultlist = new ArrayList();
			Long deptId = Long.valueOf(-1L);
			Long levelId = Long.valueOf(-1L);
			Long postId = Long.valueOf(-1L);
			if (memberlist != null) {
				for (V3xOrgMember member : memberlist) {
					deptId = member.getOrgDepartmentId();
					levelId = member.getOrgLevelId();
					postId = member.getOrgPostId();

					WebV3xOrgMember webMember = new WebV3xOrgMember();
					webMember.setV3xOrgMember(member);
					V3xOrgDepartment dept = this.getOrgManager().getDepartmentById(deptId);
					if (dept != null) {
						webMember.setDepartmentName(dept.getName());
					}
					V3xOrgLevel level = this.getOrgManager().getLevelById(levelId);
					if (level != null) {
						webMember.setLevelName(level.getName());
					}
					V3xOrgPost post = this.getOrgManager().getPostById(postId);
					if (post != null) {
						webMember.setPostName(post.getName());
					}
					if (LDAPConfig.getInstance().getIsEnableLdap()) {
						if (SystemEnvironment.hasPlugin("ldap")) {
							try {
								result.addObject("hasLDAPAD", Boolean.valueOf(true));
							} catch (Exception e) {
								LOG.error(getClass().getName() + " ldap_ad 显示ldap帐号 error :", e);
							}
						}
					}
					((List) resultlist).add(webMember);
				}
			}
			result.addObject("memberlist", resultlist);

			Map<String, CtpEnum> orgMeta = this.enumManager.getEnumsMap(ApplicationCategoryEnum.organization);
			result.addObject("orgMeta", orgMeta);
			Object hrMetadata = this.enumManager.getEnumsMap(ApplicationCategoryEnum.hr);
			result.addObject("hrMetadata", hrMetadata);

			List<V3xOrgPost> postlist = this.getOrgManager().getAllPosts(user.getLoginAccount());

			result.addObject("postlist", postlist);

			List<V3xOrgLevel> levellist = this.getOrgManager().getAllLevels(user.getLoginAccount());

			result.addObject("levellist", levellist);

			List<V3xOrgDepartment> departmentlist = this.getOrgManager().getAllDepartments(user.getLoginAccount());

			result.addObject("departmentlist", departmentlist);

			return result;
		} catch (Exception e) {
			LOG.error("", e);
		}
		return null;
	}

	/**
	 * 修改家庭关系表
	 */
	public ModelAndView editRelationship(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView("hr/staffInfo/editRelationship");
		String ID = request.getParameter("id");
		if ((ID != null) && (!ID.equals(""))) {
			Relationship relationship = this.getStaffInfoManager().getRelationshipById(Long.valueOf(ID));
			mav.addObject("relationship", relationship);
		}
		mav.addObject("staffId", request.getParameter("staffId"));

		String isManager = request.getParameter("isManager");
		boolean manager = false;
		if ((isManager != null) && (isManager.equals("Manager"))) {
			manager = true;
		}
		mav.addObject("Manager", Boolean.valueOf(manager));

		String isReadOnly = request.getParameter("isReadOnly");
		boolean readOnly = false;
		if ((isReadOnly != null) && (isReadOnly.equals("ReadOnly"))) {
			readOnly = true;
		}
		Map<String, CtpEnum> hrMetadata = this.enumManager.getEnumsMap(ApplicationCategoryEnum.hr);
		mav.addObject("hrMetadata", hrMetadata);

		mav.addObject("ReadOnly", Boolean.valueOf(readOnly));
		return mav;
	}

	/*
	 * 家庭关系列表加载
	 */
	@Override
	public ModelAndView initRelationship(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Long staffid = Long.valueOf(Long.parseLong(request.getParameter("staffId")));
		List<Relationship> list = this.getStaffInfoManager().getRelationshipByStafferId(staffid);

		Map<String, CtpEnum> hrMetadata = this.enumManager.getEnumsMap(ApplicationCategoryEnum.hr);
		CtpEnum e = hrMetadata.get("hr_staffInfo_position");// 获取政治面貌
		List<CtpEnumItem> el = e.getItems();

		ModelAndView mav = new ModelAndView("hr/staffInfo/relationship");

		mav.addObject("position", el);
		mav.addObject("list", CommonTools.pagenate(list));
		V3xOrgMember member = this.getOrgManager().getMemberById(staffid);
		mav.addObject("staff", member);

		mav.addObject("isManager", request.getParameter("isManager"));
		return mav;
	}

	/**
	 * 新建一个专门用于个人查看和修改的人员信息表
	 */

	/**
	 * 用于更新人员信息表中的微信userid
	 */
	public ModelAndView updateQYWX(HttpServletRequest request, HttpServletResponse response) throws Exception {
		User user = AppContext.getCurrentUser();// 从系统中获取当前用户
		if (user == null) {
			return null;
		}
		V3xOrgMember v3xOrgmember = this.getOrgManager().getMemberById(user.getId());//
		// 根据当前用户的id获取当前用户的orgmember
		HttpSession session = request.getSession(false);
		String qyuserid = request.getParameter("qywxuserid");
		if (qyuserid == null) {
			return null;
		}
		if (qyuserid != null && !"".equals(qyuserid)) {
			v3xOrgmember.setProperty("weixin", qyuserid);
			this.getOrgManagerDirect().updateMember(v3xOrgmember);
		}
		// 用完了移除sessionid
		session.removeAttribute("qywxuserid");
		response.sendRedirect("/seeyon/main.do?method=main");
		return null;
	}

	/**
	 * 奖惩新增附件
	 */
	@Override
	public ModelAndView editRewardsAndPunishment(HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		ModelAndView mav = new ModelAndView("hr/staffInfo/editRewardsAndPunishment");
		String ID = request.getParameter("id");
		if ((ID != null) && (!ID.equals(""))) {
			RewardsAndPunishment rewardsAndPunishment = this.getStaffInfoManager()
					.getRewardsAndPunishmentById(Long.valueOf(ID));
			mav.addObject("rewardsAndPunishment", rewardsAndPunishment);
			mav.addObject("attachments",
					this.attachmentManager.getByReference(rewardsAndPunishment.getId(), rewardsAndPunishment.getId()));
		}
		mav.addObject("staffId", request.getParameter("staffId"));

		String isManager = request.getParameter("isManager");
		boolean manager = false;
		if ((isManager != null) && (isManager.equals("Manager"))) {
			manager = true;
		}
		mav.addObject("Manager", Boolean.valueOf(manager));

		String isReadOnly = request.getParameter("isReadOnly");
		boolean readOnly = false;
		if ((isReadOnly != null) && (isReadOnly.equals("ReadOnly"))) {
			readOnly = true;
		}
		mav.addObject("ReadOnly", Boolean.valueOf(readOnly));
		return mav;
	}

	/**
	 * 奖惩档案保存
	 */
	public ModelAndView updateRewardsAndPunishment(HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		String staffid = request.getParameter("staffId");
		SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
		String time = request.getParameter("time");
		String ID = request.getParameter("id");
		V3xOrgMember member = this.getOrgManager().getMemberById(Long.valueOf(Long.parseLong(staffid)));
		if ((ID == null) || (ID.equals(""))) {
			RewardsAndPunishment rewardsAndPunishment = new RewardsAndPunishment();
			bind(request, rewardsAndPunishment);
			rewardsAndPunishment.setMember_id(Long.valueOf(staffid));
			if (StringUtils.isNotBlank(time)) {
				rewardsAndPunishment.setTime(sf.parse(time));
			} else {
				rewardsAndPunishment.setTime(null);
			}
			this.getStaffInfoManager().addRewardsAndPunishment(rewardsAndPunishment);
			this.attachmentManager.create(ApplicationCategoryEnum.hr, rewardsAndPunishment.getId(),
					rewardsAndPunishment.getId(), request);
			this.getOperationlogManager().insertOplog(member.getOrgAccountId(), Constants.MODULE_STAFF,
					ApplicationCategoryEnum.hr, "hr.staffInfo.other.add.label",
					"hr.staffInfo.RewardsAndPunishment.add.label", new Object[] { member.getName() });
		} else {
			RewardsAndPunishment reward = this.getStaffInfoManager().getRewardsAndPunishmentById(Long.valueOf(ID));
			bind(request, reward);
			if (StringUtils.isNotBlank(time)) {
				reward.setTime(sf.parse(time));
			} else {
				reward.setTime(null);
			}
			this.getStaffInfoManager().updateRewardsAndPunishment(reward);
			this.attachmentManager.deleteByReference(reward.getId(), reward.getId());
			this.attachmentManager.create(ApplicationCategoryEnum.hr, reward.getId(), reward.getId(), request);
			this.getOperationlogManager().insertOplog(member.getOrgAccountId(), Constants.MODULE_STAFF,
					ApplicationCategoryEnum.hr, "hr.staffInfo.other.update.label",
					"hr.staffInfo.RewardsAndPunishment.update.label", new Object[] { member.getName() });
		}
		String isManager = "";
		String Manager = request.getParameter("Manager");
		if ((Manager != null) && (Manager.equals("true"))) {
			isManager = "Manager";
		}
		User user = AppContext.getCurrentUser();

		user.setRemoteAddr(request.getRemoteAddr());
		this.getAppLogManager().insertLog(user, AppLogAction.Hr_UpdateStaffInfo,
				new String[] { user.getName(), member.getName() });

		return super.redirectModelAndView(
				"/hrStaff.do?method=initHome&infoType=8&staffId=" + staffid + "&isManager=" + isManager,
				"parent.parent");
	}
}
