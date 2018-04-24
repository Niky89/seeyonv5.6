package com.seeyon.v3x.hr.controller;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;

import com.seeyon.apps.agent.manager.AgentIntercalateManager;
import com.seeyon.apps.ldap.config.LDAPConfig;
import com.seeyon.ctp.common.AppContext;
import com.seeyon.ctp.common.SystemEnvironment;
import com.seeyon.ctp.common.appLog.AppLogAction;
import com.seeyon.ctp.common.appLog.manager.AppLogManager;
import com.seeyon.ctp.common.authenticate.domain.User;
import com.seeyon.ctp.common.constants.ApplicationCategoryEnum;
import com.seeyon.ctp.common.content.affair.AffairManager;
import com.seeyon.ctp.common.ctpenum.manager.EnumManager;
import com.seeyon.ctp.common.excel.DataRecord;
import com.seeyon.ctp.common.excel.DataRow;
import com.seeyon.ctp.common.excel.FileToExcelManager;
import com.seeyon.ctp.common.filemanager.manager.AttachmentManager;
import com.seeyon.ctp.common.i18n.LocaleContext;
import com.seeyon.ctp.common.operationlog.manager.OperationlogManager;
import com.seeyon.ctp.common.po.ctpenum.CtpEnum;
import com.seeyon.ctp.common.po.ctpenum.CtpEnumItem;
import com.seeyon.ctp.organization.bo.CompareSortEntity;
import com.seeyon.ctp.organization.bo.MemberPost;
import com.seeyon.ctp.organization.bo.OrganizationMessage;
import com.seeyon.ctp.organization.bo.V3xOrgAccount;
import com.seeyon.ctp.organization.bo.V3xOrgDepartment;
import com.seeyon.ctp.organization.bo.V3xOrgEntity;
import com.seeyon.ctp.organization.bo.V3xOrgLevel;
import com.seeyon.ctp.organization.bo.V3xOrgMember;
import com.seeyon.ctp.organization.bo.V3xOrgPost;
import com.seeyon.ctp.organization.bo.V3xOrgPrincipal;
import com.seeyon.ctp.organization.bo.V3xOrgRole;
import com.seeyon.ctp.organization.dao.OrgHelper;
import com.seeyon.ctp.organization.manager.OrgManager;
import com.seeyon.ctp.organization.manager.OrgManagerDirect;
import com.seeyon.ctp.organization.principal.PrincipalManager;
import com.seeyon.ctp.organization.services.OrganizationServices;
import com.seeyon.ctp.organization.webmodel.WebV3xOrgMember;
import com.seeyon.ctp.organization.webmodel.WebV3xOrgModel;
import com.seeyon.ctp.portal.space.manager.SpaceManager;
import com.seeyon.ctp.privilege.manager.MenuManager;
import com.seeyon.ctp.util.CommonTools;
import com.seeyon.ctp.util.UUIDLong;
import com.seeyon.ctp.util.annotation.CheckRoleAccess;
import com.seeyon.v3x.common.dao.paginate.Pagination;
import com.seeyon.v3x.common.i18n.ResourceBundleUtil;
import com.seeyon.v3x.common.metadata.manager.MetadataManager;
import com.seeyon.v3x.common.search.manager.SearchManager;
import com.seeyon.v3x.common.taglibs.functions.Functions;
import com.seeyon.v3x.common.web.BaseController;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.common.web.util.ExportHelper;
import com.seeyon.v3x.hr.domain.Assess;
import com.seeyon.v3x.hr.domain.ContactInfo;
import com.seeyon.v3x.hr.domain.EduExperience;
import com.seeyon.v3x.hr.domain.Page;
import com.seeyon.v3x.hr.domain.PageLabel;
import com.seeyon.v3x.hr.domain.PageProperty;
import com.seeyon.v3x.hr.domain.PostChange;
import com.seeyon.v3x.hr.domain.PropertyLabel;
import com.seeyon.v3x.hr.domain.Relationship;
import com.seeyon.v3x.hr.domain.Repository;
import com.seeyon.v3x.hr.domain.RewardsAndPunishment;
import com.seeyon.v3x.hr.domain.StaffInfo;
import com.seeyon.v3x.hr.domain.WorkRecord;
import com.seeyon.v3x.hr.manager.StaffInfoManager;
import com.seeyon.v3x.hr.manager.UserDefinedManager;
import com.seeyon.v3x.hr.util.Constants;
import com.seeyon.v3x.hr.util.EncryptUtil;
import com.seeyon.v3x.hr.util.HqlSearchHelper;
import com.seeyon.v3x.hr.util.SalaryUserDefinedHelper;
import com.seeyon.v3x.hr.webmodel.WebLabel;
import com.seeyon.v3x.hr.webmodel.WebProperty;
import com.seeyon.v3x.hr.webmodel.WebStaffInfo;
import com.seeyon.v3x.util.Datetimes;
import com.seeyon.v3x.util.Strings;

@CheckRoleAccess(roleTypes = { com.seeyon.ctp.organization.OrgConstants.Role_NAME.HrAdmin })
public class HrStaffInfoController extends BaseController {
	private static final transient Log LOG = LogFactory.getLog(HrStaffInfoController.class);
	private MetadataManager metadataManager;
	private EnumManager enumManager;
	private StaffInfoManager staffInfoManager;
	private OrgManager orgManager;
	private AttachmentManager attachmentManager;
	private UserDefinedManager userDefinedManager;
	private OrgManagerDirect orgManagerDirect;
	private SearchManager searchManager;
	private FileToExcelManager fileToExcelManager;
	private AffairManager affairManager;
	private MenuManager menuManager;
	private OperationlogManager operationlogManager;
	private AppLogManager appLogManager;
	private AgentIntercalateManager agentIntercalateManager;
	private String jsonView;
	private PrincipalManager principalManager;
	private SpaceManager spaceManager;
	private OrganizationServices organizationServices;

	public String getJsonView() {
		return this.jsonView;
	}

	public void setJsonView(String jsonView) {
		this.jsonView = jsonView;
	}

	public AgentIntercalateManager getAgentIntercalateManager() {
		return this.agentIntercalateManager;
	}

	public SpaceManager getSpaceManager() {
		return this.spaceManager;
	}

	public OrganizationServices getOrganizationServices() {
		return this.organizationServices;
	}

	public void setOrganizationServices(OrganizationServices organizationServices) {
		this.organizationServices = organizationServices;
	}

	public void setSpaceManager(SpaceManager spaceManager) {
		this.spaceManager = spaceManager;
	}

	public PrincipalManager getPrincipalManager() {
		return this.principalManager;
	}

	public void setPrincipalManager(PrincipalManager principalManager) {
		this.principalManager = principalManager;
	}

	public void setOperationlogManager(OperationlogManager operationlogManager) {
		this.operationlogManager = operationlogManager;
	}

	public MenuManager getMenuManager() {
		return this.menuManager;
	}

	public AffairManager getAffairManager() {
		return this.affairManager;
	}

	public void setAffairManager(AffairManager affairManager) {
		this.affairManager = affairManager;
	}

	public OrgManagerDirect getOrgManagerDirect() {
		return this.orgManagerDirect;
	}

	public void setOrgManagerDirect(OrgManagerDirect orgManagerDirect) {
		this.orgManagerDirect = orgManagerDirect;
	}

	public void setEnumManager(EnumManager enumManager) {
		this.enumManager = enumManager;
	}

	public MetadataManager getMetadataManager() {
		return this.metadataManager;
	}

	public OrgManager getOrgManager() {
		return this.orgManager;
	}

	public void setOrgManager(OrgManager orgManager) {
		this.orgManager = orgManager;
	}

	public StaffInfoManager getStaffInfoManager() {
		return this.staffInfoManager;
	}

	public void setStaffInfoManager(StaffInfoManager staffInfoManager) {
		this.staffInfoManager = staffInfoManager;
	}

	public UserDefinedManager getUserDefinedManager() {
		return this.userDefinedManager;
	}

	public void setUserDefinedManager(UserDefinedManager userDefinedManager) {
		this.userDefinedManager = userDefinedManager;
	}

	public SearchManager getSearchManager() {
		return this.searchManager;
	}

	public void setSearchManager(SearchManager searchManager) {
		this.searchManager = searchManager;
	}

	public FileToExcelManager getFileToExcelManager() {
		return this.fileToExcelManager;
	}

	public void setFileToExcelManager(FileToExcelManager fileToExcelManager) {
		this.fileToExcelManager = fileToExcelManager;
	}

	public ModelAndView index(HttpServletRequest request, HttpServletResponse response) throws Exception {
		return null;
	}

	public ModelAndView initInfoHome(HttpServletRequest request, HttpServletResponse response) throws Exception {
		if (!AppContext.hasResourceCode("F03_hrStaff")) {
			return null;
		}
		ModelAndView mav = new ModelAndView("hr/staffInfo/infohome");

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
		List<WebProperty> webPages = new ArrayList();
		List<Page> pages = this.userDefinedManager.getPageByModelName("staff");
		for (Page page : pages) {
			WebProperty webPage = new WebProperty();
			List<PageLabel> labels = this.userDefinedManager.getPageLabelByPageId(page.getId());
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
			V3xOrgMember member = this.orgManager.getMemberById(staffId);
			if (member != null) {
				List<MemberPost> memberPosts = member.getSecond_post();
				List<WebV3xOrgModel> secondPostList = new ArrayList();
				if ((memberPosts != null) && (!memberPosts.isEmpty())) {
					StringBuffer deptpostbuffer = new StringBuffer();
					StringBuffer deptpostbufferId = new StringBuffer();
					for (MemberPost memberPost : memberPosts) {
						WebV3xOrgModel webModel = new WebV3xOrgModel();
						StringBuffer sbuffer = new StringBuffer();
						StringBuffer sbufferId = new StringBuffer();
						Long deptid = memberPost.getDepId();
						V3xOrgDepartment v3xdept = this.orgManager.getDepartmentById(deptid);
						sbuffer.append(v3xdept.getName());
						sbuffer.append("-");
						sbufferId.append(v3xdept.getId());
						sbufferId.append("_");
						Long postid = memberPost.getPostId();
						V3xOrgPost v3xpost = this.orgManager.getPostById(postid);
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
/*
 * 花名册显示页面
 */
	public ModelAndView initNameList(HttpServletRequest request, HttpServletResponse response) throws Exception {
		try {
			ModelAndView mav = new ModelAndView("hr/staffInfo/nameList");

			String isPage = request.getParameter("ispage");
			List<WebStaffInfo> resultlist = new ArrayList();
			String staffIds = request.getParameter("staffIds");
			String[][] staffAreas = Strings.getSelectPeopleElements(staffIds);
			Long accountId = Long.valueOf(CurrentUser.get().getLoginAccount().longValue());
			if (staffAreas != null) {
				List<V3xOrgMember> memberList = new ArrayList();
				boolean isAccount = false;
				V3xOrgMember mem;
				for (int y = 0; y < staffAreas.length; y++) {
					if (staffAreas[y][0].equals("Department")) {
						List<V3xOrgMember> memberList1 = this.orgManager
								.getMembersByDepartment(Long.valueOf(Long.parseLong(staffAreas[y][1])), false);
						for (Iterator localIterator1 = memberList1.iterator(); localIterator1.hasNext();) {
							mem = (V3xOrgMember) localIterator1.next();
							if ((!memberList.contains(mem)) && (mem.getOrgAccountId().equals(accountId))) {
								memberList.add(mem);
							}
						}
						Collections.sort(memberList, CompareSortEntity.getInstance());
					} else if (staffAreas[y][0].equals("Account")) {
						isAccount = true;
					}
				}
				if (isAccount) {
					memberList.clear();
					memberList = this.orgManager.getAllMembers(accountId);
					Collections.sort(memberList, CompareSortEntity.getInstance());
				}
				if ((isPage != null) && (isPage.equals("page"))) {
					memberList = CommonTools.pagenate(memberList);
				}
				List<Long> memberIdList = new ArrayList(memberList.size());
				for (V3xOrgMember member : memberList) {
					memberIdList.add(member.getId());
				}
				Map<Long, StaffInfo> staffs = this.staffInfoManager.getStaffInfos(memberIdList);
				int i = 1 + Pagination.getFirstResult();
				for (V3xOrgMember member : memberList) {
					WebStaffInfo staffinfo = translateV3xOrgMember(member);
					staffinfo.setPeople_type(member.getIsInternal().booleanValue());
					staffinfo.setType(Byte.valueOf((byte) member.getType().intValue()));
					if (member.getBirthday() != null) {
						staffinfo.setAge(setAgeByBirthday(member.getBirthday()));
					}
					StaffInfo staff = (StaffInfo) staffs.get(member.getId());
					if (staff != null) {
						staffinfo.setSex(member.getGender() + "");
						staffinfo.setNation(staff.getNation());
						staffinfo.setSpecialty(staff.getSpecialty());
						staffinfo.setID_card(staff.getID_card());
						staffinfo.setEdu_level(staff.getEdu_level());
						staffinfo.setPolitical_position(staff.getPolitical_position());
						staffinfo.setMarriage(staff.getMarriage());
						staffinfo.setWork_starting_date(staff.getWork_starting_date());
						staffinfo.setRecord_wage(EncryptUtil.doubleFormat(staff.getRecord_wage()));
					}
					staffinfo.setBirthday(member.getBirthday());
					staffinfo.setNameList_number(i);
					i++;
					if ((staffinfo.getPolitical_position() == -1) || (staffinfo.getPolitical_position() == 0)) {
						staffinfo.setPolitical_position(2);
					}
					resultlist.add(staffinfo);
				}
			}
			if ((isPage != null) && (isPage.equals("page"))) {
				mav.addObject("isPage", "true");
			} else {
				mav.addObject("isPage", "false");
			}
			mav.addObject("staffIds", staffIds);
			mav.addObject("memberlist", resultlist);
			String title = request.getParameter("title");
			mav.addObject("title", title);

			String items = request.getParameter("items");
			mav.addObject("items", items);

			Map<String, CtpEnum> orgMeta = this.enumManager.getEnumsMap(ApplicationCategoryEnum.organization);
			mav.addObject("orgMeta", orgMeta);
			return mav;
		} catch (Exception e) {
			LOG.error("", e);
		}
		return null;
	}

	public int setAgeByBirthday(Date birthday) {
		int age = 1;
		if (birthday != null) {
			Calendar now = Calendar.getInstance();
			int year1 = now.get(1);
			Calendar bd = Calendar.getInstance();
			bd.setTime(birthday);
			int year2 = bd.get(1);
			age = year1 - year2;
		}
		if (age == 0) {
			age = 1;
		}
		return age;
	}

	public ModelAndView export(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String exportType = request.getParameter("exportType");
		if ("page".equals(exportType)) {
			return ExportHelper.excutePageMethod(this, request, response, "pageMethod");
		}
		Pagination.withoutPagination(null);
		Pagination.setFirstResult(Integer.valueOf(0));
		Pagination.setMaxResults(Integer.valueOf(Integer.MAX_VALUE));
		ModelAndView mav = ExportHelper.excutePageMethod(this, request, response, "pageMethod");
		if (mav != null) {
			List<WebStaffInfo> webStaffInfoList = (List) mav.getModel().get("memberlist");
			if ((webStaffInfoList != null) && (!webStaffInfoList.isEmpty()) && ("excel".equals(exportType))) {
				exportNameListAsExcel(request, response, webStaffInfoList);
			}
		}
		return null;
	}

	private void exportNameListAsExcel(HttpServletRequest request, HttpServletResponse response,
			List<WebStaffInfo> webStaffInfoList) {
		String title = request.getParameter("title");
		String[] items = request.getParameter("items").split(",");
		DataRecord record = new DataRecord();
		record.setSheetName(title);
		record.setTitle(title);
		String resource = "com.seeyon.v3x.hr.resource.i18n.HRResources";
		String fileName = ResourceBundleUtil.getString(resource, "hr.nameList.label", new Object[0]);
		String str = ResourceBundleUtil.getString(resource, "hr.nameList.number.label", new Object[0]) + ",";
		int i = 1;
		String sex_female = ResourceBundleUtil.getString(resource, "hr.staffInfo.female.label", new Object[0]);
		String sex_male = ResourceBundleUtil.getString(resource, "hr.staffInfo.male.label", new Object[0]);
		String juniorschool = ResourceBundleUtil.getString(resource, "hr.staffInfo.juniorschool.label", new Object[0]);
		String seniorschool = ResourceBundleUtil.getString(resource, "hr.staffInfo.seniorschool.label", new Object[0]);
		String juniorcollege = ResourceBundleUtil.getString(resource, "hr.staffInfo.juniorcollege.label",
				new Object[0]);
		String university = ResourceBundleUtil.getString(resource, "hr.staffInfo.university.label", new Object[0]);
		String postgraduate = ResourceBundleUtil.getString(resource, "hr.staffInfo.postgraduate.label", new Object[0]);
		String doctor = ResourceBundleUtil.getString(resource, "hr.staffInfo.doctor.label", new Object[0]);
		String edu_level_other = ResourceBundleUtil.getString(resource, "hr.staffInfo.other.label", new Object[0]);
		ResourceBundleUtil.getString(resource, "hr.staffInfo.commie.label", new Object[0]);
		ResourceBundleUtil.getString(resource, "hr.staffInfo.others.label", new Object[0]);
		String position_1 = ResourceBundleUtil.getString(resource, "hr.staffInfo.position.1", new Object[0]);
		String position_2 = ResourceBundleUtil.getString(resource, "hr.staffInfo.position.2", new Object[0]);
		String position_3 = ResourceBundleUtil.getString(resource, "hr.staffInfo.position.3", new Object[0]);
		String position_4 = ResourceBundleUtil.getString(resource, "hr.staffInfo.position.4", new Object[0]);
		String single = ResourceBundleUtil.getString(resource, "hr.staffInfo.single.label", new Object[0]);
		String married = ResourceBundleUtil.getString(resource, "hr.staffInfo.married.label", new Object[0]);
		for (WebStaffInfo staffinfo : webStaffInfoList) {
			DataRow row = new DataRow();
			row.addDataCell(String.valueOf(i), 1);
			String[] arrayOfString1;
			int j = (arrayOfString1 = items).length;
			for (int i1 = 0; i1 < j; i1++) {
				String item = arrayOfString1[i1];
				if ("1".equals(item)) {
					str =

							str + ResourceBundleUtil.getString(resource, "hr.staffInfo.name.label", new Object[0])
									+ ",";
					row.addDataCell(staffinfo.getName(), 1);
				} else if ("2".equals(item)) {
					str =

							str + ResourceBundleUtil.getString(resource, "hr.staffInfo.sex.label", new Object[0]) + ",";
					if ("1".equals(staffinfo.getSex())) {
						row.addDataCell(sex_male, 1);
					} else if ("2".equals(staffinfo.getSex())) {
						row.addDataCell(sex_female, 1);
					} else {
						row.addDataCell("", 1);
					}
				} else if ("3".equals(item)) {
					str =

							str + ResourceBundleUtil.getString(resource, "hr.staffInfo.nation.label", new Object[0])
									+ ",";
					row.addDataCell(staffinfo.getNation(), 1);
				} else if ("4".equals(item)) {
					str =

							str + ResourceBundleUtil.getString(resource, "hr.staffInfo.age.label", new Object[0]) + ",";
					if (staffinfo.getAge() != 0) {
						row.addDataCell(String.valueOf(staffinfo.getAge()), 1);
					} else {
						row.addDataCell("", 1);
					}
				} else if ("5".equals(item)) {
					str =

							str + ResourceBundleUtil.getString(resource, "hr.staffInfo.specialty.label", new Object[0])
									+ ",";
					row.addDataCell(staffinfo.getSpecialty(), 1);
				} else if ("6".equals(item)) {
					str =

							str + ResourceBundleUtil.getString(resource, "hr.staffInfo.IDcard.label", new Object[0])
									+ ",";
					row.addDataCell(staffinfo.getID_card(), 1);
				} else if ("7".equals(item)) {
					str =

							str + ResourceBundleUtil.getString(resource, "hr.staffInfo.edulevel.label", new Object[0])
									+ ",";
					int edu = staffinfo.getEdu_level();
					if (edu == 1) {
						row.addDataCell(juniorschool, 1);
					} else if (edu == 2) {
						row.addDataCell(seniorschool, 1);
					} else if (edu == 3) {
						row.addDataCell(juniorcollege, 1);
					} else if (edu == 4) {
						row.addDataCell(university, 1);
					} else if (edu == 5) {
						row.addDataCell(postgraduate, 1);
					} else if (edu == 6) {
						row.addDataCell(doctor, 1);
					} else if (edu == 7) {
						row.addDataCell(edu_level_other, 1);
					} else {
						row.addDataCell("", 1);
					}
				} else if ("8".equals(item)) {
					str =

							str + ResourceBundleUtil.getString(resource, "hr.staffInfo.position.label", new Object[0])
									+ ",";
					if (1 == staffinfo.getPolitical_position()) {
						row.addDataCell(position_1, 1);
					} else if (2 == staffinfo.getPolitical_position()) {
						row.addDataCell(position_2, 1);
					} else if (3 == staffinfo.getPolitical_position()) {
						row.addDataCell(position_3, 1);
					} else {
						row.addDataCell(position_4, 1);
					}
				} else if ("9".equals(item)) {
					str =

							str + ResourceBundleUtil.getString(resource, "hr.staffInfo.marriage.label", new Object[0])
									+ ",";
					if (1 == staffinfo.getMarriage()) {
						row.addDataCell(single, 1);
					} else if (2 == staffinfo.getMarriage()) {
						row.addDataCell(married, 1);
					} else {
						row.addDataCell("", 1);
					}
				} else if ("10".equals(item)) {
					str =

							str + ResourceBundleUtil.getString(resource, "hr.staffInfo.workStartingDate.label",
									new Object[0]) + ",";
					if (staffinfo.getWork_starting_date() != null) {
						row.addDataCell(Datetimes.formatDate(staffinfo.getWork_starting_date()).toString(), 1);
					} else {
						row.addDataCell("", 1);
					}
				} else if ("11".equals(item)) {
					str =

							str + ResourceBundleUtil.getString(resource, "hr.staffInfo.recordWage.label", new Object[0])
									+ ",";
					if (staffinfo.getRecord_wage() == null) {
						row.addDataCell("", 1);
					} else {
						row.addDataCell(String.valueOf(staffinfo.getRecord_wage()), 1);
					}
				} else if ("12".equals(item)) {
					str =

							str + ResourceBundleUtil.getString(resource, "hr.staffInfo.memberno.label", new Object[0])
									+ ",";
					row.addDataCell(staffinfo.getCode(), 1);
				} else if ("13".equals(item)) {
					str =

							str + ResourceBundleUtil.getString(resource, "hr.staffInfo.peopleType.label", new Object[0])
									+ ",";
					String isInternal = ResourceBundleUtil.getString(
							"com.seeyon.v3x.organization.resources.i18n.OrganizationResources", "people.isInternal",
							new Object[0]);
					if (!staffinfo.getPeople_type()) {
						isInternal = ResourceBundleUtil.getString(
								"com.seeyon.v3x.organization.resources.i18n.OrganizationResources", "people.out",
								new Object[0]);
					}
					row.addDataCell(isInternal, 1);
				} else if ("14".equals(item)) {
					str =

							str + ResourceBundleUtil.getString(resource, "hr.staffInfo.staffstate.label", new Object[0])
									+ ",";
					String state = ResourceBundleUtil.getString(
							"com.seeyon.v3x.organization.resources.i18n.OrganizationResources",
							"org.metadata.member_state.in", new Object[0]);
					if (2 == staffinfo.getState().byteValue()) {
						state = ResourceBundleUtil.getString(
								"com.seeyon.v3x.organization.resources.i18n.OrganizationResources",
								"org.metadata.member_state.out", new Object[0]);
					}
					row.addDataCell(state, 1);
				} else if ("15".equals(item)) {
					str =

							str + ResourceBundleUtil.getString(resource, "hr.staffInfo.stafftype.label", new Object[0])
									+ ",";
					String type = ResourceBundleUtil.getString(
							"com.seeyon.v3x.organization.resources.i18n.OrganizationResources",
							"org.metadata.member_type.nomal", new Object[0]);
					if (2 == staffinfo.getType().byteValue()) {
						type = ResourceBundleUtil.getString(
								"com.seeyon.v3x.organization.resources.i18n.OrganizationResources",
								"org.metadata.member_type.unnomal", new Object[0]);
					}
					row.addDataCell(type, 1);
				} else if ("16".equals(item)) {
					str =

							str + ResourceBundleUtil.getString(resource, "hr.staffInfo.department.label", new Object[0])
									+ ",";
					row.addDataCell(staffinfo.getDepartment_name(), 1);
				} else if ("17".equals(item)) {
					str =

							str + ResourceBundleUtil.getString(resource,
									new StringBuilder("hr.staffInfo.postlevel.label").append(Functions.suffix())
											.toString(),
									new Object[0]) + ",";

					row.addDataCell(staffinfo.getLevel_name(), 1);
				} else if ("18".equals(item)) {
					str =

							str + ResourceBundleUtil.getString(resource, "hr.staffInfo.primaryPostId.label",
									new Object[0]) + ",";
					row.addDataCell(staffinfo.getPost_name(), 1);
				} else if ("19".equals(item)) {
					str =

							str + ResourceBundleUtil.getString(resource, "hr.staffInfo.secondPostId.label",
									new Object[0]) + ",";
					row.addDataCell(staffinfo.getSecond_posts(), 1);
				} else if ("20".equals(item)) {
					str = str + ResourceBundleUtil.getString(resource, "hr.staffInfo.birthday.label", new Object[0])
							+ ",";
					row.addDataCell(staffinfo.getBirthdayStr(), 5);
				}
			}
			record.addDataRow(new DataRow[] { row });
			if (i == 1) {
				String[] columnNames = str.split(",");
				record.setColumnName(columnNames);
			}
			i++;
		}
		try {
			this.fileToExcelManager.save(response, fileName, new DataRecord[] { record });
		} catch (Exception e) {
			LOG.error("", e);
		}
	}

	/**
	 * @deprecated
	 */
	public ModelAndView exportNameListExcel(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String staffIds = request.getParameter("staffIds");
		String[][] staffAreas = Strings.getSelectPeopleElements(staffIds);

		String title = request.getParameter("title");
		String[] items = request.getParameter("items").split(",");
		DataRecord record = new DataRecord();
		record.setSheetName(title);
		record.setTitle(title);
		String resource = "com.seeyon.v3x.hr.resource.i18n.HRResources";
		String str = ResourceBundleUtil.getString(resource, "hr.nameList.number.label", new Object[0]) + ",";
		int i = 1;
		String sex_female = ResourceBundleUtil.getString(resource, "hr.staffInfo.female.label", new Object[0]);
		String sex_male = ResourceBundleUtil.getString(resource, "hr.staffInfo.male.label", new Object[0]);
		String juniorschool = ResourceBundleUtil.getString(resource, "hr.staffInfo.juniorschool.label", new Object[0]);
		String seniorschool = ResourceBundleUtil.getString(resource, "hr.staffInfo.seniorschool.label", new Object[0]);
		String juniorcollege = ResourceBundleUtil.getString(resource, "hr.staffInfo.juniorcollege.label",
				new Object[0]);
		String university = ResourceBundleUtil.getString(resource, "hr.staffInfo.university.label", new Object[0]);
		String postgraduate = ResourceBundleUtil.getString(resource, "hr.staffInfo.postgraduate.label", new Object[0]);
		String doctor = ResourceBundleUtil.getString(resource, "hr.staffInfo.doctor.label", new Object[0]);
		String edu_level_other = ResourceBundleUtil.getString(resource, "hr.staffInfo.other.label", new Object[0]);
		ResourceBundleUtil.getString(resource, "hr.staffInfo.commie.label", new Object[0]);
		ResourceBundleUtil.getString(resource, "hr.staffInfo.others.label", new Object[0]);
		String position_1 = ResourceBundleUtil.getString(resource, "hr.staffInfo.position.1", new Object[0]);
		String position_2 = ResourceBundleUtil.getString(resource, "hr.staffInfo.position.2", new Object[0]);
		String position_3 = ResourceBundleUtil.getString(resource, "hr.staffInfo.position.3", new Object[0]);
		String position_4 = ResourceBundleUtil.getString(resource, "hr.staffInfo.position.4", new Object[0]);
		String single = ResourceBundleUtil.getString(resource, "hr.staffInfo.single.label", new Object[0]);
		String married = ResourceBundleUtil.getString(resource, "hr.staffInfo.married.label", new Object[0]);

		List<V3xOrgMember> mems = new ArrayList();
		if ((staffAreas != null) && (staffAreas.length > 0)) {
			for (int y = 0; y < staffAreas.length; y++) {
				Object staffs;
				Iterator localIterator3;
				V3xOrgMember member;
				String[] arrayOfString1;
				int j;
				if (staffAreas[y][0].equals("Department")) {
					List<V3xOrgMember> memberList = new ArrayList();
					Set<V3xOrgMember> memberList2 = new HashSet();
					List<V3xOrgMember> memberList1 = this.orgManager
							.getMembersByDepartment(Long.valueOf(Long.parseLong(staffAreas[y][1])), false);
					memberList2.addAll(memberList1);
					for (V3xOrgMember meb : memberList2) {
						if (!mems.contains(meb)) {
							mems.add(meb);
							memberList.add(meb);
						}
					}
					List<Long> memberIdList = new ArrayList(memberList.size());
					for (V3xOrgMember member1 : memberList) {
						memberIdList.add(member1.getId());
					}
					staffs = this.staffInfoManager.getStaffInfos(memberIdList);
					for (localIterator3 = memberList.iterator(); localIterator3.hasNext();) {
						member = (V3xOrgMember) localIterator3.next();
						DataRow row = new DataRow();
						WebStaffInfo staffinfo = translateV3xOrgMember((V3xOrgMember) member);
						if (((V3xOrgMember) member).getBirthday() != null) {
							staffinfo.setAge(setAgeByBirthday(((V3xOrgMember) member).getBirthday()));
						}
						staffinfo.setPeople_type(((V3xOrgMember) member).getIsInternal().booleanValue());
						StaffInfo staff = (StaffInfo) ((Map) staffs).get(((V3xOrgMember) member).getId());
						if (staff != null) {
							staffinfo.setSex(((V3xOrgMember) member).getGender() + "");
							staffinfo.setNation(staff.getNation());

							staffinfo.setSpecialty(staff.getSpecialty());
							staffinfo.setID_card(staff.getID_card());
							staffinfo.setEdu_level(staff.getEdu_level());
							staffinfo.setPolitical_position(staff.getPolitical_position());
							staffinfo.setMarriage(staff.getMarriage());
							if (staff.getWork_starting_date() != null) {
								staffinfo.setWork_starting_date(staff.getWork_starting_date());
							}
							if (staff.getRecord_wage() != null) {
								staffinfo.setRecord_wage(EncryptUtil.doubleFormat(staff.getRecord_wage()));
							}
						}
						staffinfo.setNameList_number(i);
						row.addDataCell(String.valueOf(i), 1);

						j = (arrayOfString1 = items).length;
						for (i = 0; i < j; i++) {
							String item = arrayOfString1[i];
							if ("1".equals(item)) {
								str =

										str + ResourceBundleUtil.getString(resource, "hr.staffInfo.name.label",
												new Object[0]) + ",";
								row.addDataCell(staffinfo.getName(), 1);
							} else if ("2".equals(item)) {
								str =

										str + ResourceBundleUtil.getString(resource, "hr.staffInfo.sex.label",
												new Object[0]) + ",";
								if ("1".equals(staffinfo.getSex())) {
									row.addDataCell(sex_male, 1);
								} else if ("2".equals(staffinfo.getSex())) {
									row.addDataCell(sex_female, 1);
								} else {
									row.addDataCell("", 1);
								}
							} else if ("3".equals(item)) {
								str =

										str + ResourceBundleUtil.getString(resource, "hr.staffInfo.nation.label",
												new Object[0]) + ",";
								row.addDataCell(staffinfo.getNation(), 1);
							} else if ("4".equals(item)) {
								str =

										str + ResourceBundleUtil.getString(resource, "hr.staffInfo.age.label",
												new Object[0]) + ",";
								if (staffinfo.getAge() != 0) {
									row.addDataCell(String.valueOf(staffinfo.getAge()), 1);
								} else {
									row.addDataCell("", 1);
								}
							} else if ("5".equals(item)) {
								str =

										str + ResourceBundleUtil.getString(resource, "hr.staffInfo.specialty.label",
												new Object[0]) + ",";
								row.addDataCell(staffinfo.getSpecialty(), 1);
							} else if ("6".equals(item)) {
								str =

										str + ResourceBundleUtil.getString(resource, "hr.staffInfo.IDcard.label",
												new Object[0]) + ",";
								row.addDataCell(staffinfo.getID_card(), 1);
							} else if ("7".equals(item)) {
								str =

										str + ResourceBundleUtil.getString(resource, "hr.staffInfo.edulevel.label",
												new Object[0]) + ",";
								int edu = staffinfo.getEdu_level();
								if (edu == 1) {
									row.addDataCell(juniorschool, 1);
								} else if (edu == 2) {
									row.addDataCell(seniorschool, 1);
								} else if (edu == 3) {
									row.addDataCell(juniorcollege, 1);
								} else if (edu == 4) {
									row.addDataCell(university, 1);
								} else if (edu == 5) {
									row.addDataCell(postgraduate, 1);
								} else if (edu == 6) {
									row.addDataCell(doctor, 1);
								} else if (edu == 7) {
									row.addDataCell(edu_level_other, 1);
								} else {
									row.addDataCell("", 1);
								}
							} else if ("8".equals(item)) {
								str =

										str + ResourceBundleUtil.getString(resource, "hr.staffInfo.position.label",
												new Object[0]) + ",";
								if (1 == staffinfo.getPolitical_position()) {
									row.addDataCell(position_1, 1);
								} else if (2 == staffinfo.getPolitical_position()) {
									row.addDataCell(position_2, 1);
								} else if (3 == staffinfo.getPolitical_position()) {
									row.addDataCell(position_3, 1);
								} else {
									row.addDataCell(position_4, 1);
								}
							} else if ("9".equals(item)) {
								str =

										str + ResourceBundleUtil.getString(resource, "hr.staffInfo.marriage.label",
												new Object[0]) + ",";
								if (1 == staffinfo.getMarriage()) {
									row.addDataCell(single, 1);
								} else if (2 == staffinfo.getMarriage()) {
									row.addDataCell(married, 1);
								} else {
									row.addDataCell("", 1);
								}
							} else if ("10".equals(item)) {
								str =

										str + ResourceBundleUtil.getString(resource,
												"hr.staffInfo.workStartingDate.label", new Object[0]) + ",";
								if (staffinfo.getWork_starting_date() != null) {
									row.addDataCell(

											Datetimes.formatDate(staffinfo.getWork_starting_date()).toString(), 1);
								} else {
									row.addDataCell("", 1);
								}
							} else if ("11".equals(item)) {
								str =

										str + ResourceBundleUtil.getString(resource, "hr.staffInfo.recordWage.label",
												new Object[0]) + ",";
								if (staffinfo.getRecord_wage() == null) {
									row.addDataCell("", 1);
								} else {
									row.addDataCell(String.valueOf(staffinfo.getRecord_wage()), 1);
								}
							} else if ("12".equals(item)) {
								str =

										str + ResourceBundleUtil.getString(resource, "hr.staffInfo.memberno.label",
												new Object[0]) + ",";
								row.addDataCell(staffinfo.getCode(), 1);
							} else if ("13".equals(item)) {
								str =

										str + ResourceBundleUtil.getString(resource, "hr.staffInfo.peopleType.label",
												new Object[0]) + ",";
								String isInternal = ResourceBundleUtil.getString(
										"com.seeyon.v3x.organization.resources.i18n.OrganizationResources",
										"people.isInternal", new Object[0]);
								if (!staffinfo.getPeople_type()) {
									isInternal = ResourceBundleUtil.getString(
											"com.seeyon.v3x.organization.resources.i18n.OrganizationResources",
											"people.out", new Object[0]);
								}
								row.addDataCell(isInternal, 1);
							} else if ("14".equals(item)) {
								str =

										str + ResourceBundleUtil.getString(resource, "hr.staffInfo.staffstate.label",
												new Object[0]) + ",";
								String state = ResourceBundleUtil.getString(
										"com.seeyon.v3x.organization.resources.i18n.OrganizationResources",
										"org.metadata.member_state.in", new Object[0]);
								if (2 == staffinfo.getState().byteValue()) {
									state = ResourceBundleUtil.getString(
											"com.seeyon.v3x.organization.resources.i18n.OrganizationResources",
											"org.metadata.member_state.out", new Object[0]);
								}
								row.addDataCell(state, 1);
							} else if ("15".equals(item)) {
								str =

										str + ResourceBundleUtil.getString(resource, "hr.staffInfo.stafftype.label",
												new Object[0]) + ",";
								String type = ResourceBundleUtil.getString(
										"com.seeyon.v3x.organization.resources.i18n.OrganizationResources",
										"org.metadata.member_type.nomal", new Object[0]);
								if (2 == staffinfo.getType().byteValue()) {
									type = ResourceBundleUtil.getString(
											"com.seeyon.v3x.organization.resources.i18n.OrganizationResources",
											"org.metadata.member_type.unnomal", new Object[0]);
								}
								row.addDataCell(type, 1);
							} else if ("16".equals(item)) {
								str =

										str + ResourceBundleUtil.getString(resource, "hr.staffInfo.department.label",
												new Object[0]) + ",";
								row.addDataCell(staffinfo.getDepartment_name(), 1);
							} else if ("17".equals(item)) {
								str =

										str + ResourceBundleUtil.getString(resource, "hr.staffInfo.postlevel.label",
												new Object[0]) + ",";
								row.addDataCell(staffinfo.getLevel_name(), 1);
							} else if ("18".equals(item)) {
								str =

										str + ResourceBundleUtil.getString(resource, "hr.staffInfo.primaryPostId.label",
												new Object[0]) + ",";
								row.addDataCell(staffinfo.getPost_name(), 1);
							} else if ("19".equals(item)) {
								str =

										str + ResourceBundleUtil.getString(resource, "hr.staffInfo.secondPostId.label",
												new Object[0]) + ",";
								row.addDataCell(staffinfo.getSecond_posts(), 1);
							}
						}
						record.addDataRow(new DataRow[] { row });
						if (i == 1) {
							String[] columnNames = str.split(",");
							record.setColumnName(columnNames);
						}
						i++;
					}
				} else if (staffAreas[y][0].equals("Account")) {
					List<V3xOrgMember> memberList = new ArrayList();
					Set<V3xOrgMember> memberList2 = new HashSet();
					List<V3xOrgMember> memberList1 = this.orgManagerDirect
							.getAllMembers(Long.valueOf(Long.parseLong(staffAreas[y][1])), false);
					memberList2.addAll(memberList1);
					for (staffs = memberList2.iterator(); ((Iterator) staffs).hasNext();) {
						V3xOrgMember mem = (V3xOrgMember) ((Iterator) staffs).next();
						if (!mems.contains(mem)) {
							mems.add(mem);
							memberList.add(mem);
						}
					}
					List<Long> memberIdList = new ArrayList(memberList.size());
					for (Iterator<V3xOrgMember> member1 = memberList.iterator(); ((Iterator) member1).hasNext();) {
						V3xOrgMember member2 = (V3xOrgMember) ((Iterator) member1).next();
						memberIdList.add(member2.getId());
					}
					staffs = this.staffInfoManager.getStaffInfos(memberIdList);
					for (V3xOrgMember member1 : memberList) {
						DataRow row = new DataRow();
						WebStaffInfo staffinfo = translateV3xOrgMember(member1);

						staffinfo.setPeople_type(member1.getIsInternal().booleanValue());
						if (member1.getBirthday() != null) {
							staffinfo.setAge(setAgeByBirthday(member1.getBirthday()));
						}
						StaffInfo staff = (StaffInfo) ((Map) staffs).get(member1.getId());
						if (staff != null) {
							staffinfo.setSex(member1.getGender() + "");
							staffinfo.setNation(staff.getNation());

							staffinfo.setSpecialty(staff.getSpecialty());
							staffinfo.setID_card(staff.getID_card());
							staffinfo.setEdu_level(staff.getEdu_level());
							staffinfo.setPolitical_position(staff.getPolitical_position());
							staffinfo.setMarriage(staff.getMarriage());
							if (staff.getWork_starting_date() != null) {
								staffinfo.setWork_starting_date(staff.getWork_starting_date());
							}
							if (staff.getRecord_wage() != null) {
								staffinfo.setRecord_wage(EncryptUtil.doubleFormat(staff.getRecord_wage()));
							}
						}
						staffinfo.setNameList_number(i);
						row.addDataCell(String.valueOf(i), 1);

						j = (arrayOfString1 = items).length;
						for (i = 0; i < j; i++) {
							String item = arrayOfString1[i];
							if ("1".equals(item)) {
								str =

										str + ResourceBundleUtil.getString(resource, "hr.staffInfo.name.label",
												new Object[0]) + ",";
								row.addDataCell(staffinfo.getName(), 1);
							} else if ("2".equals(item)) {
								str =

										str + ResourceBundleUtil.getString(resource, "hr.staffInfo.sex.label",
												new Object[0]) + ",";
								if ("1".equals(staffinfo.getSex())) {
									row.addDataCell(sex_male, 1);
								} else if ("2".equals(staffinfo.getSex())) {
									row.addDataCell(sex_female, 1);
								} else {
									row.addDataCell("", 1);
								}
							} else if ("3".equals(item)) {
								str =

										str + ResourceBundleUtil.getString(resource, "hr.staffInfo.nation.label",
												new Object[0]) + ",";
								row.addDataCell(staffinfo.getNation(), 1);
							} else if ("4".equals(item)) {
								str =

										str + ResourceBundleUtil.getString(resource, "hr.staffInfo.age.label",
												new Object[0]) + ",";
								if (staffinfo.getAge() != 0) {
									row.addDataCell(String.valueOf(staffinfo.getAge()), 1);
								} else {
									row.addDataCell("", 1);
								}
							} else if ("5".equals(item)) {
								str =

										str + ResourceBundleUtil.getString(resource, "hr.staffInfo.specialty.label",
												new Object[0]) + ",";
								row.addDataCell(staffinfo.getSpecialty(), 1);
							} else if ("6".equals(item)) {
								str =

										str + ResourceBundleUtil.getString(resource, "hr.staffInfo.IDcard.label",
												new Object[0]) + ",";
								row.addDataCell(staffinfo.getID_card(), 1);
							} else if ("7".equals(item)) {
								str =

										str + ResourceBundleUtil.getString(resource, "hr.staffInfo.edulevel.label",
												new Object[0]) + ",";
								int edu = staffinfo.getEdu_level();
								if (edu == 1) {
									row.addDataCell(juniorschool, 1);
								} else if (edu == 2) {
									row.addDataCell(seniorschool, 1);
								} else if (edu == 3) {
									row.addDataCell(juniorcollege, 1);
								} else if (edu == 4) {
									row.addDataCell(university, 1);
								} else if (edu == 5) {
									row.addDataCell(postgraduate, 1);
								} else if (edu == 6) {
									row.addDataCell(doctor, 1);
								} else if (edu == 7) {
									row.addDataCell(edu_level_other, 1);
								} else {
									row.addDataCell("", 1);
								}
							} else if ("8".equals(item)) {
								str =

										str + ResourceBundleUtil.getString(resource, "hr.staffInfo.position.label",
												new Object[0]) + ",";
								if (1 == staffinfo.getPolitical_position()) {
									row.addDataCell(position_1, 1);
								} else if (2 == staffinfo.getPolitical_position()) {
									row.addDataCell(position_2, 1);
								} else if (3 == staffinfo.getPolitical_position()) {
									row.addDataCell(position_3, 1);
								} else {
									row.addDataCell(position_4, 1);
								}
							} else if ("9".equals(item)) {
								str =

										str + ResourceBundleUtil.getString(resource, "hr.staffInfo.marriage.label",
												new Object[0]) + ",";
								if (1 == staffinfo.getMarriage()) {
									row.addDataCell(single, 1);
								} else if (2 == staffinfo.getMarriage()) {
									row.addDataCell(married, 1);
								} else {
									row.addDataCell("", 1);
								}
							} else if ("10".equals(item)) {
								str =

										str + ResourceBundleUtil.getString(resource,
												"hr.staffInfo.workStartingDate.label", new Object[0]) + ",";
								if (staffinfo.getWork_starting_date() != null) {
									row.addDataCell(

											Datetimes.formatDate(staffinfo.getWork_starting_date()).toString(), 1);
								} else {
									row.addDataCell("", 1);
								}
							} else if ("11".equals(item)) {
								str =

										str + ResourceBundleUtil.getString(resource, "hr.staffInfo.recordWage.label",
												new Object[0]) + ",";
								if (staffinfo.getRecord_wage() == null) {
									row.addDataCell("", 1);
								} else {
									row.addDataCell(String.valueOf(staffinfo.getRecord_wage()), 1);
								}
							} else if ("12".equals(item)) {
								str =

										str + ResourceBundleUtil.getString(resource, "hr.staffInfo.memberno.label",
												new Object[0]) + ",";
								row.addDataCell(staffinfo.getCode(), 1);
							} else if ("13".equals(item)) {
								str =

										str + ResourceBundleUtil.getString(resource, "hr.staffInfo.peopleType.label",
												new Object[0]) + ",";
								String isInternal = ResourceBundleUtil.getString(
										"com.seeyon.v3x.organization.resources.i18n.OrganizationResources",
										"people.isInternal", new Object[0]);
								if (!staffinfo.getPeople_type()) {
									isInternal = ResourceBundleUtil.getString(
											"com.seeyon.v3x.organization.resources.i18n.OrganizationResources",
											"people.out", new Object[0]);
								}
								row.addDataCell(isInternal, 1);
							} else if ("14".equals(item)) {
								str =

										str + ResourceBundleUtil.getString(resource, "hr.staffInfo.staffstate.label",
												new Object[0]) + ",";
								String state = ResourceBundleUtil.getString(
										"com.seeyon.v3x.organization.resources.i18n.OrganizationResources",
										"org.metadata.member_state.in", new Object[0]);
								if (2 == staffinfo.getState().byteValue()) {
									state = ResourceBundleUtil.getString(
											"com.seeyon.v3x.organization.resources.i18n.OrganizationResources",
											"org.metadata.member_state.out", new Object[0]);
								}
								row.addDataCell(state, 1);
							} else if ("15".equals(item)) {
								str =

										str + ResourceBundleUtil.getString(resource, "hr.staffInfo.stafftype.label",
												new Object[0]) + ",";
								String type = ResourceBundleUtil.getString(
										"com.seeyon.v3x.organization.resources.i18n.OrganizationResources",
										"org.metadata.member_type.nomal", new Object[0]);
								if (2 == staffinfo.getType().byteValue()) {
									type = ResourceBundleUtil.getString(
											"com.seeyon.v3x.organization.resources.i18n.OrganizationResources",
											"org.metadata.member_type.unnomal", new Object[0]);
								}
								row.addDataCell(type, 1);
							} else if ("16".equals(item)) {
								str =

										str + ResourceBundleUtil.getString(resource, "hr.staffInfo.department.label",
												new Object[0]) + ",";
								row.addDataCell(staffinfo.getDepartment_name(), 1);
							} else if ("17".equals(item)) {
								str =

										str + ResourceBundleUtil.getString(resource, "hr.staffInfo.postlevel.label",
												new Object[0]) + ",";
								row.addDataCell(staffinfo.getLevel_name(), 1);
							} else if ("18".equals(item)) {
								str =

										str + ResourceBundleUtil.getString(resource, "hr.staffInfo.primaryPostId.label",
												new Object[0]) + ",";
								row.addDataCell(staffinfo.getPost_name(), 1);
							} else if ("19".equals(item)) {
								str =

										str + ResourceBundleUtil.getString(resource, "hr.staffInfo.secondPostId.label",
												new Object[0]) + ",";
								row.addDataCell(staffinfo.getSecond_posts(), 1);
							}
						}
						record.addDataRow(new DataRow[] { row });
						if (i == 1) {
							String[] columnNames = str.split(",");
							record.setColumnName(columnNames);
						}
						i++;
					}
				}
			}
			try {
				this.fileToExcelManager.save(response, "staffInfo", new DataRecord[] { record });
			} catch (Exception e) {
				LOG.error("", e);
			}
		}
		return null;
	}

	public ModelAndView exportMember(HttpServletRequest request, HttpServletResponse response) throws Exception {
		User user = AppContext.getCurrentUser();
		String resource = "com.seeyon.v3x.organization.resources.i18n.OrganizationResources";
		String resource1 = "com.seeyon.v3x.hr.resource.i18n.HRResources";
		List<V3xOrgMember> memberlist = this.orgManager.getAllMembers(user.getLoginAccount());
		V3xOrgAccount account = null;
		Map<String, CtpEnum> orgMeta = this.metadataManager.getMetadataMap(null);
		StaffInfo staffer = null;
		CtpEnumItem memberTypeItem = null;
		CtpEnumItem memberStateItem = null;
		String memberType = "";
		String memberState = "";
		V3xOrgMember member = null;
		V3xOrgDepartment dept = null;
		String deptName = "";
		V3xOrgPost post = null;

		String postName = "";
		String secondPostName = "";
		V3xOrgLevel level = null;
		String levelName = "";

		String roleNames = "";

		String primaryLanguange = "";

		String state_Enabled = ResourceBundleUtil.getString(resource, "org.account_form.enable.use", new Object[0]);
		String state_Disabled = ResourceBundleUtil.getString(resource, "org.account_form.enable.unuse", new Object[0]);
		ResourceBundleUtil.getString(resource, "org.member_form.primaryLanguange.zh_CN", new Object[0]);
		ResourceBundleUtil.getString(resource, "org.member_form.primaryLanguange.zh", new Object[0]);
		ResourceBundleUtil.getString(resource, "org.member_form.primaryLanguange.en", new Object[0]);
		String member_type_inner = ResourceBundleUtil.getString(resource, "org.member_form.type.inner", new Object[0]);
		String member_type_out = ResourceBundleUtil.getString(resource, "org.member_form.type.out", new Object[0]);
		String member_name = ResourceBundleUtil.getString(resource, "org.member_form.name.label", new Object[0]);
		String member_loginName = ResourceBundleUtil.getString(resource, "org.member_form.loginName.label",
				new Object[0]);
		String member_password = ResourceBundleUtil.getString(resource, "org.member_form.password.label",
				new Object[0]);
		String member_primaryLanguange = ResourceBundleUtil.getString(resource, "org.member_form.primaryLanguange",
				new Object[0]);
		String member_kind = ResourceBundleUtil.getString(resource, "org.member_form.kind", new Object[0]);
		String member_state = ResourceBundleUtil.getString(resource, "org.state.lable", new Object[0]);
		String member_code = ResourceBundleUtil.getString(resource, "org.member_form.code", new Object[0]);
		String member_sortId = ResourceBundleUtil.getString(resource, "org.member_form.sort", new Object[0]);
		String member_deptName = ResourceBundleUtil.getString(resource, "org.member_form.deptName.label",
				new Object[0]);
		String member_primaryPost = ResourceBundleUtil.getString(resource, "org.member_form.primaryPost.label",
				new Object[0]);
		String member_secondPost = ResourceBundleUtil.getString(resource, "org.member_form.secondPost.label",
				new Object[0]);
		String member_levelName = ResourceBundleUtil.getString(resource, "org.member_form.levelName.label",
				new Object[0]);
		String member_type = ResourceBundleUtil.getString(resource, "org.member_form.type", new Object[0]);
		String member_memberState = ResourceBundleUtil.getString(resource, "org.member_form.member.state",
				new Object[0]);
		String member_roles = ResourceBundleUtil.getString(resource, "org.member_form.roles", new Object[0]);
		String member_tel = ResourceBundleUtil.getString(resource, "org.member_form.tel", new Object[0]);
		String member_account = ResourceBundleUtil.getString(resource, "org.member_form.account", new Object[0]);
		String member_description = ResourceBundleUtil.getString(resource, "org.member_form.description",
				new Object[0]);
		String member_list = ResourceBundleUtil.getString(resource, "org.member_form.list", new Object[0]);

		String staff_sex = ResourceBundleUtil.getString(resource1, "hr.staffInfo.sex.label", new Object[0]);
		String staff_nation = ResourceBundleUtil.getString(resource1, "hr.staffInfo.nation.label", new Object[0]);
		String staff_birthplace = ResourceBundleUtil.getString(resource1, "hr.staffInfo.birthplace.label",
				new Object[0]);
		String staff_age = ResourceBundleUtil.getString(resource1, "hr.staffInfo.age.label", new Object[0]);
		String staff_usedName = ResourceBundleUtil.getString(resource1, "hr.staffInfo.usedname.label", new Object[0]);
		String staff_brithday = ResourceBundleUtil.getString(resource1, "hr.staffInfo.birthday.label", new Object[0]);
		String staff_IDCard = ResourceBundleUtil.getString(resource1, "hr.staffInfo.IDcard.label", new Object[0]);
		String staff_specialty = ResourceBundleUtil.getString(resource1, "hr.staffInfo.specialty.label", new Object[0]);
		String staff_workingtime = ResourceBundleUtil.getString(resource1, "hr.staffInfo.workingtime.label",
				new Object[0]);
		String staff_workStartingDate = ResourceBundleUtil.getString(resource1, "hr.staffInfo.workStartingDate.label",
				new Object[0]);
		String staff_recordWage = ResourceBundleUtil.getString(resource1, "hr.staffInfo.recordWage.label",
				new Object[0]);
		String staff_edulevel = ResourceBundleUtil.getString(resource1, "hr.staffInfo.edulevel.label", new Object[0]);
		String staff_position = ResourceBundleUtil.getString(resource1, "hr.staffInfo.position.label", new Object[0]);
		String staff_marriage = ResourceBundleUtil.getString(resource1, "hr.staffInfo.marriage.label", new Object[0]);
		String staff_hobby = ResourceBundleUtil.getString(resource1, "hr.staffInfo.hobby.label", new Object[0]);
		String staff_remark = ResourceBundleUtil.getString(resource1, "hr.staffInfo.remark.label", new Object[0]);

		String sex_female = ResourceBundleUtil.getString(resource1, "hr.staffInfo.female.label", new Object[0]);
		String sex_male = ResourceBundleUtil.getString(resource1, "hr.staffInfo.male.label", new Object[0]);
		String juniorschool = ResourceBundleUtil.getString(resource1, "hr.staffInfo.juniorschool.label", new Object[0]);
		String seniorschool = ResourceBundleUtil.getString(resource1, "hr.staffInfo.seniorschool.label", new Object[0]);
		String juniorcollege = ResourceBundleUtil.getString(resource1, "hr.staffInfo.juniorcollege.label",
				new Object[0]);
		String university = ResourceBundleUtil.getString(resource1, "hr.staffInfo.university.label", new Object[0]);
		String postgraduate = ResourceBundleUtil.getString(resource1, "hr.staffInfo.postgraduate.label", new Object[0]);
		String doctor = ResourceBundleUtil.getString(resource1, "hr.staffInfo.doctor.label", new Object[0]);
		String edu_level_other = ResourceBundleUtil.getString(resource1, "hr.staffInfo.other.label", new Object[0]);
		ResourceBundleUtil.getString(resource1, "hr.staffInfo.commie.label", new Object[0]);
		ResourceBundleUtil.getString(resource1, "hr.staffInfo.others.label", new Object[0]);
		String position_1 = ResourceBundleUtil.getString(resource1, "hr.staffInfo.position.1", new Object[0]);
		String position_2 = ResourceBundleUtil.getString(resource1, "hr.staffInfo.position.2", new Object[0]);
		String position_3 = ResourceBundleUtil.getString(resource1, "hr.staffInfo.position.3", new Object[0]);
		String position_4 = ResourceBundleUtil.getString(resource1, "hr.staffInfo.position.4", new Object[0]);
		String single = ResourceBundleUtil.getString(resource1, "hr.staffInfo.single.label", new Object[0]);
		String married = ResourceBundleUtil.getString(resource1, "hr.staffInfo.married.label", new Object[0]);
		if ((memberlist != null) && (memberlist.size() > 0)) {
			DataRow[] datarow = new DataRow[memberlist.size()];
			for (int i = 0; i < memberlist.size(); i++) {
				member = (V3xOrgMember) memberlist.get(i);
				staffer = this.staffInfoManager.getStaffInfoById(member.getId());
				primaryLanguange = "";
				DataRow row = new DataRow();
				row.addDataCell(member.getName(), 1);
				row.addDataCell(member.getLoginName(), 1);

				row.addDataCell("", 1);

				row.addDataCell(primaryLanguange, 1);
				if (member.getIsInternal().booleanValue()) {
					row.addDataCell(member_type_inner, 1);
				} else {
					row.addDataCell(member_type_out, 1);
				}
				if (member.getEnabled().booleanValue()) {
					row.addDataCell(state_Enabled, 1);
				} else {
					row.addDataCell(state_Disabled, 1);
				}
				row.addDataCell(member.getCode(), 1);

				row.addDataCell(member.getSortId().toString(), 1);
				dept = this.orgManager.getDepartmentById(member.getOrgDepartmentId());
				if (dept != null) {
					deptName = dept.getName();
				}
				row.addDataCell(deptName, 1);
				post = this.orgManager.getPostById(member.getOrgPostId());

				List<MemberPost> memberPosts = member.getSecond_post();
				if ((memberPosts != null) && (!memberPosts.isEmpty())) {
					for (MemberPost memberPost : memberPosts) {
						V3xOrgDepartment sndDept = this.orgManager.getDepartmentById(memberPost.getDepId());
						V3xOrgPost sndPost = this.orgManager.getPostById(memberPost.getPostId());
						if ((sndDept != null) && (sndPost != null)) {
							String sndName = sndDept.getName() + "-" + sndPost.getName();
							secondPostName = secondPostName + sndName + ",";
						}
					}
				}
				if (post != null) {
					postName = post.getName();
				}
				row.addDataCell(postName, 1);
				row.addDataCell(secondPostName, 1);
				level = this.orgManager.getLevelById(member.getOrgLevelId());
				if (level != null) {
					levelName = level.getName();
				}
				row.addDataCell(levelName, 1);
				memberTypeItem = ((CtpEnum) orgMeta.get("org_property_member_type"))
						.getItem(member.getType().toString());
				if (memberTypeItem != null) {
					memberType = ResourceBundleUtil.getString(resource, memberTypeItem.getLabel(), new Object[] { "" });
				}
				row.addDataCell(memberType, 1);
				memberStateItem = ((CtpEnum) orgMeta.get("org_property_member_state"))
						.getItem(member.getState().toString());
				if (memberStateItem != null) {
					memberState = ResourceBundleUtil.getString(resource, memberStateItem.getLabel(),
							new Object[] { "" });
				}
				row.addDataCell(memberState, 1);

				row.addDataCell(roleNames, 1);
				if ((member.getTelNumber() != null) && (!member.getTelNumber().equals(""))) {
					row.addDataCell(member.getTelNumber(), 1);
				} else {
					row.addDataCell("", 1);
				}
				account = this.orgManager.getAccountById(member.getOrgAccountId());
				row.addDataCell(account.getName(), 1);
				if (staffer != null) {
					if ("1".equals(member.getGender())) {
						row.addDataCell(sex_male, 1);
					} else if ("2".equals(member.getGender())) {
						row.addDataCell(sex_female, 1);
					}
					row.addDataCell(staffer.getNation(), 1);
					row.addDataCell(staffer.getBirthplace(), 1);
					row.addDataCell(staffer.getAge(), 1);
					row.addDataCell(staffer.getUsedname(), 1);
					if (member.getBirthday() != null) {
						row.addDataCell(Datetimes.formatDate(member.getBirthday()).toString(), 1);
					} else {
						row.addDataCell("", 1);
					}
					row.addDataCell(staffer.getID_card(), 1);
					row.addDataCell(staffer.getSpecialty(), 1);
					row.addDataCell(String.valueOf(staffer.getWorking_time()), 1);
					if (staffer.getWork_starting_date() != null) {
						row.addDataCell(Datetimes.formatDate(staffer.getWork_starting_date()).toString(), 1);
					} else {
						row.addDataCell("", 1);
					}
					if (staffer.getRecord_wage() != null) {
						row.addDataCell(String.valueOf(staffer.getRecord_wage()), 1);
					} else {
						row.addDataCell("", 1);
					}
					int edu = staffer.getEdu_level();
					if (edu == 1) {
						row.addDataCell(juniorschool, 1);
					} else if (edu == 2) {
						row.addDataCell(seniorschool, 1);
					} else if (edu == 3) {
						row.addDataCell(juniorcollege, 1);
					} else if (edu == 4) {
						row.addDataCell(university, 1);
					} else if (edu == 5) {
						row.addDataCell(postgraduate, 1);
					} else if (edu == 6) {
						row.addDataCell(doctor, 1);
					} else if (edu == 7) {
						row.addDataCell(edu_level_other, 1);
					} else {
						row.addDataCell("", 1);
					}
					if (1 == staffer.getPolitical_position()) {
						row.addDataCell(position_1, 1);
					} else if (2 == staffer.getPolitical_position()) {
						row.addDataCell(position_2, 1);
					} else if (3 == staffer.getPolitical_position()) {
						row.addDataCell(position_3, 1);
					} else {
						row.addDataCell(position_4, 1);
					}
					if (1 == staffer.getMarriage()) {
						row.addDataCell(single, 1);
					}
					if (2 == staffer.getMarriage()) {
						row.addDataCell(married, 1);
					} else {
						row.addDataCell("", 1);
					}
					row.addDataCell(staffer.getHobby(), 1);
					row.addDataCell(staffer.getRemark(), 1);
				} else {
					row.addDataCell("", 1);
					row.addDataCell("", 1);
					row.addDataCell("", 1);
					row.addDataCell("", 1);
					row.addDataCell("", 1);
					row.addDataCell("", 1);
					row.addDataCell("", 1);
					row.addDataCell("", 1);
					row.addDataCell("", 1);
					row.addDataCell("", 1);
					row.addDataCell("", 1);
					row.addDataCell("", 1);
					row.addDataCell("", 1);
					row.addDataCell("", 1);
					row.addDataCell("", 1);
					row.addDataCell("", 1);
				}
				row.addDataCell(member.getDescription(), 1);

				datarow[i] = row;
			}
			DataRecord dataRecord = new DataRecord();
			try {
				dataRecord.addDataRow(datarow);
			} catch (Exception e) {
				LOG.error("", e);
			}
			String[] columnName = { member_name, member_loginName, member_password, member_primaryLanguange,
					member_kind, member_state, member_code, member_sortId, member_deptName, member_primaryPost,
					member_secondPost, member_levelName, member_type, member_memberState, member_roles, member_tel,
					member_account, staff_sex, staff_nation, staff_birthplace, staff_age, staff_usedName,
					staff_brithday, staff_IDCard, staff_specialty, staff_workingtime, staff_workStartingDate,
					staff_recordWage, staff_edulevel, staff_position, staff_marriage, staff_hobby, staff_remark,
					member_description };
			dataRecord.setColumnName(columnName);
			dataRecord.setTitle(member_list);
			dataRecord.setSheetName(member_list);
			try {
				this.fileToExcelManager.save(response, member_list + "-" + user.getLoginName(),
						new DataRecord[] { dataRecord });
			} catch (Exception e) {
				LOG.error("", e);
			}
		}
		return null;
	}

	public ModelAndView importExcel(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView modelAndView = new ModelAndView("hr/staffInfo/selectImportExcel");
		return modelAndView;
	}

	public ModelAndView importMember(HttpServletRequest request, HttpServletResponse response) throws Exception {
		V3xOrgAccount account = null;

		V3xOrgMember member = null;
		V3xOrgPost post = null;
		V3xOrgLevel level = null;
		StaffInfo staffer = null;
		String repeat = request.getParameter("repeat");
		String language = request.getParameter("language");

		String resource = "com.seeyon.v3x.organization.resources.i18n.OrganizationResources";
		String resource1 = "com.seeyon.v3x.hr.resource.i18n.HRResources";
		Locale locale = null;
		if (language.equals("zh_CN")) {
			locale = Locale.CHINA;
		} else if (language.equals("en")) {
			locale = Locale.ENGLISH;
		}
		String sex_female = ResourceBundleUtil.getString(resource1, locale, "hr.staffInfo.female.label", new Object[0]);
		String sex_male = ResourceBundleUtil.getString(resource1, locale, "hr.staffInfo.male.label", new Object[0]);
		String juniorschool = ResourceBundleUtil.getString(resource1, locale, "hr.staffInfo.juniorschool.label",
				new Object[0]);
		String seniorschool = ResourceBundleUtil.getString(resource1, locale, "hr.staffInfo.seniorschool.label",
				new Object[0]);
		String juniorcollege = ResourceBundleUtil.getString(resource1, locale, "hr.staffInfo.juniorcollege.label",
				new Object[0]);
		String university = ResourceBundleUtil.getString(resource1, locale, "hr.staffInfo.university.label",
				new Object[0]);
		String postgraduate = ResourceBundleUtil.getString(resource1, locale, "hr.staffInfo.postgraduate.label",
				new Object[0]);
		String doctor = ResourceBundleUtil.getString(resource1, locale, "hr.staffInfo.doctor.label", new Object[0]);
		String edu_level_other = ResourceBundleUtil.getString(resource1, locale, "hr.staffInfo.other.label",
				new Object[0]);
		String commie = ResourceBundleUtil.getString(resource1, locale, "hr.staffInfo.commie.label", new Object[0]);
		ResourceBundleUtil.getString(resource1, locale, "hr.staffInfo.position.1", new Object[0]);
		ResourceBundleUtil.getString(resource1, locale, "hr.staffInfo.position.2", new Object[0]);
		ResourceBundleUtil.getString(resource1, locale, "hr.staffInfo.position.3", new Object[0]);
		ResourceBundleUtil.getString(resource1, locale, "hr.staffInfo.position.4", new Object[0]);

		String others = ResourceBundleUtil.getString(resource1, locale, "hr.staffInfo.others.label", new Object[0]);
		String single = ResourceBundleUtil.getString(resource1, locale, "hr.staffInfo.single.label", new Object[0]);
		String married = ResourceBundleUtil.getString(resource1, locale, "hr.staffInfo.married.label", new Object[0]);

		String state_Enabled = ResourceBundleUtil.getString(resource, locale, "org.account_form.enable.use",
				new Object[0]);
		ResourceBundleUtil.getString(resource, locale, "org.member_form.primaryLanguange.zh_CN", new Object[0]);
		ResourceBundleUtil.getString(resource, locale, "org.member_form.primaryLanguange.zh", new Object[0]);
		ResourceBundleUtil.getString(resource, locale, "org.member_form.primaryLanguange.en", new Object[0]);
		String member_type_inner = ResourceBundleUtil.getString(resource, locale, "org.member_form.type.inner",
				new Object[0]);
		String member_type_out = ResourceBundleUtil.getString(resource, locale, "org.member_form.type.out",
				new Object[0]);
		List<CtpEnumItem> memberTypes = this.metadataManager.getMetadataItems("org_property_member_type");
		Map<String, String> typeMap = new HashMap();
		for (CtpEnumItem item : memberTypes) {
			typeMap.put(item.getLabel(), item.getValue());
		}
		String typelable = "";
		for (CtpEnumItem item : memberTypes) {
			typelable = ResourceBundleUtil.getString(resource, locale, item.getLabel(), new Object[] { "" });
			if (!typelable.equals("")) {
				typeMap.put(typelable, item.getValue());
			}
		}
		List<CtpEnumItem> memberStates = this.metadataManager.getMetadataItems("org_property_member_state");
		Object stateMap = new HashMap();
		for (CtpEnumItem item : memberStates) {
			((Map) stateMap).put(item.getLabel(), item.getValue());
		}
		String statelable = "";
		for (CtpEnumItem item : memberStates) {
			statelable = ResourceBundleUtil.getString(resource, locale, item.getLabel(), new Object[] { "" });
			if (!statelable.equals("")) {
				((Map) stateMap).put(statelable, item.getValue());
			}
		}
		Object myrole = new ArrayList();
		String[] roles = (String[]) null;
		V3xOrgRole role = null;
		boolean isMemberNull = false;

		String impURL = request.getParameter("impURL");
		String fileURL = impURL.replace("\\", "/");
		try {
			File file = new File(fileURL);
			List<List<String>> memberList = this.fileToExcelManager.readExcel(file);
			List<String> proList = new ArrayList();
			for (int i = 2; i < memberList.size(); i++) {
				new HashMap();
				proList = (List) memberList.get(i);

				member = this.orgManager.getMemberByLoginName((String) proList.get(1));
				if (member == null) {
					isMemberNull = true;
					member = new V3xOrgMember();
				}
				if ((proList.get(16) != null) && (!((String) proList.get(16)).equals(""))) {
					account = this.orgManager.getAccountByName((String) proList.get(16));
				}
				if ((proList.get(0) != null) && (!((String) proList.get(0)).equals(""))) {
					member.setName((String) proList.get(0));
				}
				if (proList.get(1) != null) {
					((String) proList.get(1)).equals("");
				}
				if (proList.get(2) != null) {
					((String) proList.get(2)).equals("");
				}
				if (((String) proList.get(4)).equals(member_type_inner)) {
					member.setIsInternal(Boolean.valueOf(true));
				} else if (((String) proList.get(4)).equals(member_type_out)) {
					member.setIsInternal(Boolean.valueOf(false));
				}
				if (((String) proList.get(5)).equals(state_Enabled)) {
					member.setEnabled(Boolean.valueOf(true));
				} else {
					member.setEnabled(Boolean.valueOf(false));
				}
				member.setCode((String) proList.get(6));
				if ((proList.get(9) != null) && (!((String) proList.get(9)).equals(""))) {
					post = (V3xOrgPost) this.orgManager.getMemberByName((String) proList.get(9));
					if (post != null) {
						member.setOrgPostId(post.getId());
					}
				}
				if ((proList.get(11) != null) && (!((String) proList.get(11)).equals(""))) {
					level = (V3xOrgLevel) this.orgManager.getMemberByName((String) proList.get(11));
					if (level != null) {
						member.setOrgLevelId(level.getId());
					}
				}
				if ((typeMap.get(proList.get(12)) != null) && (!((String) typeMap.get(proList.get(12))).equals(""))) {
					member.setType(new Integer((String) typeMap.get(proList.get(12))));
				}
				if ((((Map) stateMap).get(proList.get(13)) != null)
						&& (!((String) ((Map) stateMap).get(proList.get(13))).equals(""))) {
					member.setState(new Byte((String) ((Map) stateMap).get(proList.get(13))).byteValue());
				}
				if ((proList.get(14) != null) && (!((String) proList.get(14)).equals(""))) {
					roles = ((String) proList.get(14)).split(",");
					String[] arrayOfString1;
					int j = (arrayOfString1 = roles).length;
					for (int i1 = 0; i1 < j; i1++) {
						String roleName = arrayOfString1[i1];
						role = this.orgManager.getRoleByName(roleName, null);
						if (role != null) {
							((List) myrole).add(role.getId());
						}
					}
				}
				member.setOrgAccountId(account.getId());
				member.setDescription((String) proList.get(33));
				if (isMemberNull) {
					this.orgManagerDirect.addMember(member);
				} else if (repeat.equals("0")) {
					this.orgManagerDirect.updateMember(member);
				}
				staffer = this.staffInfoManager.getStaffInfoById(member.getId());
				if (staffer == null) {
					staffer = new StaffInfo();
				}
				staffer.setOrg_member_id(member.getId());
				if (!sex_male.equals(proList.get(17))) {
					sex_female.equals(proList.get(17));
				}
				staffer.setNation((String) proList.get(18));
				staffer.setBirthplace((String) proList.get(19));
				staffer.setUsedname((String) proList.get(21));
				if ((proList.get(22) != null) && (!"".equals(proList.get(22)))) {
					member.setCreateTime(Datetimes.parse((String) proList.get(22), "yyyy-MM-dd"));
				}
				staffer.setID_card((String) proList.get(23));
				staffer.setSpecialty((String) proList.get(24));
				if ((proList.get(26) != null) && (!"".equals(proList.get(26)))) {
					staffer.setWork_starting_date(Datetimes.parse((String) proList.get(26), "yyyy-MM-dd"));
				}
				if ((proList.get(27) != null) && (!"".equals(proList.get(27)))) {
					staffer.setRecord_wage(Double.valueOf((String) proList.get(27)));
				}
				if (juniorschool.equals(proList.get(28))) {
					staffer.setEdu_level(1);
				} else if (seniorschool.equals(proList.get(28))) {
					staffer.setEdu_level(2);
				} else if (juniorcollege.equals(proList.get(28))) {
					staffer.setEdu_level(3);
				} else if (university.equals(proList.get(28))) {
					staffer.setEdu_level(4);
				} else if (postgraduate.equals(proList.get(28))) {
					staffer.setEdu_level(5);
				} else if (doctor.equals(proList.get(28))) {
					staffer.setEdu_level(6);
				} else if (edu_level_other.equals(proList.get(28))) {
					staffer.setEdu_level(7);
				}
				if (commie.equals(proList.get(29))) {
					staffer.setPolitical_position(1);
				} else if (others.equals(proList.get(29))) {
					staffer.setPolitical_position(2);
				}
				if (single.equals(proList.get(30))) {
					staffer.setMarriage(1);
				} else if (married.equals(proList.get(30))) {
					staffer.setMarriage(2);
				}
				staffer.setHobby((String) proList.get(31));
				staffer.setRemark((String) proList.get(32));
			}
		} catch (Exception e) {
			LOG.error("", e);
		}
		return super.redirectModelAndView("/hrStaff.do?method=initStaffInfoList");
	}

	public ModelAndView initSetNameList(HttpServletRequest request, HttpServletResponse response) throws Exception {
		if (!AppContext.hasResourceCode("F03_hrStaff")) {
			return null;
		}
		ModelAndView mav = new ModelAndView("hr/staffInfo/setNameList");
		return mav;
	}

	public ModelAndView highLevelSerchList(HttpServletRequest request, HttpServletResponse response) throws Exception {
		if (!AppContext.hasResourceCode("F03_hrStaff")) {
			return null;
		}
		ModelAndView mav = new ModelAndView("hr/staffInfo/setSearchList");
		Object hrMetadata = this.enumManager.getEnumsMap(ApplicationCategoryEnum.hr);
		mav.addObject("hrMetadata", hrMetadata);
		return mav;
	}

	public ModelAndView initStaffListFrame(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView("hr/staffInfo/staffListFrame");
		return mav;
	}

	public ModelAndView initStaffInfoList(HttpServletRequest request, HttpServletResponse response) throws Exception {
		boolean b = false;
		int i = 0;
		long textfieldLong = 0L;
		Integer textfieldInteger = Integer.valueOf(0);
		try {
			User user = AppContext.getCurrentUser();
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
					memberlist = this.orgManager.getAllMembersByAccountId(accountId, null, Boolean.valueOf(true),
							enable, condition, textfieldInteger, null);
					request.setAttribute("textfileds", textfieldInteger);
				} else {
					memberlist = this.orgManager.getAllMembersByAccountId(accountId, null, Boolean.valueOf(true),
							enable, condition, Long.valueOf(textfieldLong), null);
					request.setAttribute("textfileds", Long.valueOf(textfieldLong));
				}
			} else {
				memberlist = this.orgManager.getAllMembersByAccountId(accountId, null, Boolean.valueOf(true), enable,
						condition, textVlaue, null);
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
					V3xOrgDepartment dept = this.orgManager.getDepartmentById(deptId);
					if (dept != null) {
						webMember.setDepartmentName(dept.getName());
					}
					V3xOrgLevel level = this.orgManager.getLevelById(levelId);
					if (level != null) {
						webMember.setLevelName(level.getName());
					}
					V3xOrgPost post = this.orgManager.getPostById(postId);
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

			List<V3xOrgPost> postlist = this.orgManager.getAllPosts(user.getLoginAccount());

			result.addObject("postlist", postlist);

			List<V3xOrgLevel> levellist = this.orgManager.getAllLevels(user.getLoginAccount());

			result.addObject("levellist", levellist);

			List<V3xOrgDepartment> departmentlist = this.orgManager.getAllDepartments(user.getLoginAccount());

			result.addObject("departmentlist", departmentlist);

			return result;
		} catch (Exception e) {
			LOG.error("", e);
		}
		return null;
	}

	public ModelAndView highLevelQueryList(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView result = new ModelAndView("hr/staffInfo/staffInfoList");

		String de = request.getParameter("department");
		String le = request.getParameter("level");
		String po = request.getParameter("post");

		String se = request.getParameter("sex");
		String st = request.getParameter("study");
		String pol = request.getParameter("polity");
		String ma = request.getParameter("marriage");

		String fT1 = request.getParameter("fromTime1");
		String tT1 = request.getParameter("toTime1");
		String fT2 = request.getParameter("fromTime2");
		String tT2 = request.getParameter("toTime2");
		try {
			User user = AppContext.getCurrentUser();
			List<V3xOrgMember> memberlist = new ArrayList();
			if (((st != null) && (!st.equals("")) && (Integer.valueOf(st).intValue() != -1))
					|| ((pol != null) && (!pol.equals("")) && (Integer.valueOf(pol).intValue() != -1))
					|| ((ma != null) && (!ma.equals("")) && (Integer.valueOf(ma).intValue() != -1))
					|| ((fT1 != null) && (!fT1.equals(""))) || ((fT2 != null) && (!fT2.equals("")))
					|| ((tT1 != null) && (!tT1.equals(""))) || ((tT2 != null) && (!tT2.equals("")))) {
				memberlist = HqlSearchHelper.highSearchMember(de, le, po, se, st, pol, ma, fT1, tT1, fT2, tT2,
						this.searchManager, this.orgManagerDirect, this.principalManager, true);
			} else {
				memberlist = HqlSearchHelper.highSearchMember(se, de, le, po, this.searchManager, this.orgManagerDirect,
						this.principalManager, true);
			}
			List<WebV3xOrgMember> resultlist = new ArrayList();
			Long deptId = Long.valueOf(-1L);
			Long levelId = Long.valueOf(-1L);
			Long postId = Long.valueOf(-1L);
			if (memberlist != null) {
				for (V3xOrgMember member : memberlist) {
					if ((!member.getIsAdmin().booleanValue()) && (member.isValid())) {
						deptId = member.getOrgDepartmentId();
						levelId = member.getOrgLevelId();
						postId = member.getOrgPostId();

						WebV3xOrgMember webMember = new WebV3xOrgMember();
						webMember.setV3xOrgMember(member);
						V3xOrgDepartment dept = this.orgManager.getDepartmentById(deptId);
						if (dept != null) {
							webMember.setDepartmentName(dept.getName());
						}
						V3xOrgLevel level = this.orgManager.getLevelById(levelId);
						if (level != null) {
							webMember.setLevelName(level.getName());
						}
						V3xOrgPost post = this.orgManager.getPostById(postId);
						if (post != null) {
							webMember.setPostName(post.getName());
						}
						resultlist.add(webMember);
					}
				}
			}
			result.addObject("memberlist", resultlist);

			Map<String, CtpEnum> orgMeta = this.enumManager.getEnumsMap(ApplicationCategoryEnum.organization);
			result.addObject("orgMeta", orgMeta);
			Object hrMetadata = this.enumManager.getEnumsMap(ApplicationCategoryEnum.hr);
			result.addObject("hrMetadata", hrMetadata);

			List<V3xOrgPost> postlist = this.orgManager.getAllPosts(user.getLoginAccount());
			result.addObject("postlist", postlist);

			List<V3xOrgLevel> levellist = this.orgManager.getAllLevels(user.getLoginAccount());
			result.addObject("levellist", levellist);

			List<V3xOrgDepartment> departmentlist = this.orgManager.getAllDepartments(user.getLoginAccount());
			result.addObject("departmentlist", departmentlist);

			return result;
		} catch (Exception e) {
			LOG.error("", e);
		}
		return null;
	}

	public ModelAndView initInfoToolBar(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView("hr/staffInfo/infotoolbar");
		int infotype = 1;
		String str = request.getParameter("infpoType");
		if (str != null) {
			infotype = Integer.parseInt(str);
		}
		LOG.debug("infoType  :   " + infotype);
		List<WebProperty> webPages = new ArrayList();
		List<Page> pages = this.userDefinedManager.getPageByModelName("staff");
		for (Page page : pages) {
			WebProperty webPage = new WebProperty();
			List<PageLabel> labels = this.userDefinedManager.getPageLabelByPageId(page.getId());
			for (PageLabel label : labels) {
				if (label.getLanguage().equals("zh_CN")) {
					webPage.setPageName_zh(label.getPageLabelValue());
				} else {
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
		LOG.debug("webPages  size  : " + webPages.size());
		mav.addObject("webPages", webPages);
		mav.addObject("infoType", Integer.valueOf(infotype));
		mav.addObject("staffId", request.getParameter("staffId"));
		mav.addObject("isManager", request.getParameter("isManager"));
		mav.addObject("isNew", request.getParameter("isNew"));
		return mav;
	}

	public ModelAndView initDetail(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView("hr/staffInfo/detailHome");
		String infoType = request.getParameter("infoType");
		int infotype = 0;
		if (infoType != null) {
			infotype = Integer.parseInt(infoType);
		}
		String type = "";
		if (infotype == 3) {
			type = "Relationship";
		}
		if (infotype == 4) {
			type = "WorkRecord";
		}
		if (infotype == 5) {
			type = "EduExperience";
		}
		if (infotype == 6) {
			type = "PostChange";
		}
		if (infotype == 7) {
			type = "Assess";
		}
		if (infotype == 8) {
			type = "RewardsAndPunishment";
		}
		mav.addObject("type", type);
		mav.addObject("id", request.getParameter("id"));
		mav.addObject("isNew", request.getParameter("isNew"));

		mav.addObject("isReadOnly", request.getParameter("isReadOnly"));
		mav.addObject("isManager", request.getParameter("isManager"));
		mav.addObject("staffId", request.getParameter("staffId"));
		return mav;
	}

	public ModelAndView initSpace(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView();
		int infotype = 1;
		String str = request.getParameter("infoType");
		if (str != null) {
			infotype = Integer.parseInt(str);
		}
		if (infotype == 1) {
			mav = initStafferInfo(request, response);
		} else if (infotype == 2) {
			mav = initContactInfo(request, response);
		} else {
			mav = initHome(request, response);
		}
		return mav;
	}

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
		mav.addObject("listType", listType);
		mav.addObject("isManager", request.getParameter("isManager"));
		mav.addObject("staffId", request.getParameter("staffId"));
		return mav;
	}

	public ModelAndView initStafferInfo(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView("hr/staffInfo/staffInfo");
		StaffInfo staffInfo = new StaffInfo();
		V3xOrgMember member = new V3xOrgMember();
		String isNew = request.getParameter("isNew");
		if ((isNew != null) && (isNew.equals("New"))) {
			mav.addObject("staff", staffInfo);
			Integer sid = this.orgManager.getMaxMemberSortByAccountId(CurrentUser.get().getLoginAccount());
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
			member = this.orgManager.getMemberById(staffid);
			staffInfo = this.staffInfoManager.getStaffInfoById(staffid);
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
				Locale memberLocale = this.orgManagerDirect.getMemberLocaleById(staffid);
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

		List<V3xOrgLevel> levels = this.orgManagerDirect.getAllLevels(CurrentUser.get().getLoginAccount(), false);
		List<V3xOrgLevel> levelsForPage = new ArrayList();

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

	public ModelAndView initContactInfo(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Long staffid = Long.valueOf(Long.parseLong(request.getParameter("staffId")));
		ModelAndView mav = new ModelAndView("hr/staffInfo/contactInfo");
		ContactInfo contactInfo = this.staffInfoManager.getContactInfoById(staffid);
		V3xOrgMember member = this.orgManager.getMemberById(staffid);
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

	public ModelAndView initRelationship(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Long staffid = Long.valueOf(Long.parseLong(request.getParameter("staffId")));
		List<Relationship> list = this.staffInfoManager.getRelationshipByStafferId(staffid);

		ModelAndView mav = new ModelAndView("hr/staffInfo/relationship");

		mav.addObject("list", CommonTools.pagenate(list));
		V3xOrgMember member = this.orgManager.getMemberById(staffid);
		mav.addObject("staff", member);

		mav.addObject("isManager", request.getParameter("isManager"));
		return mav;
	}

	public ModelAndView initWorkRecord(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Long staffid = Long.valueOf(Long.parseLong(request.getParameter("staffId")));
		ModelAndView mav = new ModelAndView("hr/staffInfo/workRecord");

		List<WorkRecord> list = this.staffInfoManager.getWorkRecordByStafferId(staffid);

		mav.addObject("list", CommonTools.pagenate(list));

		V3xOrgMember member = this.orgManager.getMemberById(staffid);
		mav.addObject("staff", member);

		mav.addObject("isManager", request.getParameter("isManager"));
		return mav;
	}

	public ModelAndView initEduExperience(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView("hr/staffInfo/eduExperience");
		Long staffid = Long.valueOf(Long.parseLong(request.getParameter("staffId")));
		List<EduExperience> list = this.staffInfoManager.getEduExperienceByStafferId(staffid);
		mav.addObject("list", CommonTools.pagenate(list));

		V3xOrgMember member = this.orgManager.getMemberById(staffid);
		mav.addObject("staff", member);

		mav.addObject("isManager", request.getParameter("isManager"));
		return mav;
	}

	public ModelAndView initPostChange(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView("hr/staffInfo/postChange");
		Long staffid = Long.valueOf(Long.parseLong(request.getParameter("staffId")));
		List<PostChange> list = this.staffInfoManager.getPostChangeByStafferId(staffid);
		mav.addObject("list", CommonTools.pagenate(list));

		V3xOrgMember member = this.orgManager.getMemberById(staffid);
		mav.addObject("staff", member);

		mav.addObject("isManager", request.getParameter("isManager"));
		return mav;
	}

	public ModelAndView initAssess(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView("hr/staffInfo/assess");
		Long staffid = Long.valueOf(Long.parseLong(request.getParameter("staffId")));
		List<Assess> list = this.staffInfoManager.getAssessByStafferId(staffid);
		mav.addObject("list", CommonTools.pagenate(list));

		V3xOrgMember member = this.orgManager.getMemberById(staffid);
		mav.addObject("staff", member);

		mav.addObject("isManager", request.getParameter("isManager"));
		return mav;
	}

	public ModelAndView initRewardsAndPunishment(HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		ModelAndView mav = new ModelAndView("hr/staffInfo/rewardsAndPunishment");
		Long staffid = Long.valueOf(Long.parseLong(request.getParameter("staffId")));
		List<RewardsAndPunishment> list = this.staffInfoManager.getRewardsAndPunishmentByStafferId(staffid);
		mav.addObject("list", CommonTools.pagenate(list));

		V3xOrgMember member = this.orgManager.getMemberById(staffid);
		mav.addObject("staff", member);

		mav.addObject("isManager", request.getParameter("isManager"));
		return mav;
	}

	public WebStaffInfo translateV3xOrgMember(V3xOrgMember member)
			throws com.seeyon.v3x.common.exceptions.BusinessException,
			com.seeyon.ctp.common.exceptions.BusinessException {
		Long accId = member.getOrgAccountId();
		Long deptId = member.getOrgDepartmentId();
		Long levelId = member.getOrgLevelId();
		Long postId = member.getOrgPostId();
		WebStaffInfo webstaffinfo = new WebStaffInfo();
		V3xOrgAccount acc = this.orgManager.getAccountById(accId);
		if (acc != null) {
			webstaffinfo.setOrg_name(acc.getName());
		}
		V3xOrgEntity dept = this.orgManager.getDepartmentById(deptId);
		if (dept != null) {
			webstaffinfo.setDepartment_name(dept.getName());
		}
		V3xOrgEntity level = this.orgManager.getLevelById(levelId);
		if (level != null) {
			if (level.getEnabled().booleanValue()) {
				webstaffinfo.setLevel_name(level.getName());
				webstaffinfo.setOrgLevelId(level.getId());
			} else {
				member.setOrgLevelId(Long.valueOf(-1L));
			}
		}
		V3xOrgEntity post = this.orgManager.getPostById(postId);
		if (post != null) {
			if (post.getEnabled().booleanValue()) {
				webstaffinfo.setPost_name(post.getName());
				webstaffinfo.setOrgPostId(post.getId());
			} else {
				member.setOrgPostId(Long.valueOf(-1L));
			}
		}
		webstaffinfo.setSex(member.getGender() + "");
		webstaffinfo.setName(member.getName());
		webstaffinfo.setType(Byte.valueOf(member.getType().toString()));
		webstaffinfo.setCode(member.getCode());
		webstaffinfo.setState(Byte.valueOf(member.getState().toString()));
		webstaffinfo.setId(member.getId());
		webstaffinfo.setPeople_type(member.getIsInternal().booleanValue());

		List<MemberPost> Memberposts = member.getSecond_post();
		if ((Memberposts != null) && (!Memberposts.isEmpty())) {
			StringBuffer deptpostbuffer = new StringBuffer();
			StringBuffer deptpostIdsBuffer = new StringBuffer();
			for (MemberPost memberPost : Memberposts) {
				StringBuffer sbuffer = new StringBuffer();
				StringBuffer idsbuffer = new StringBuffer();
				Long deptid = memberPost.getDepId();
				V3xOrgDepartment v3xdept = this.orgManager.getDepartmentById(deptid);
				Long postid = memberPost.getPostId();
				V3xOrgPost v3xpost = this.orgManager.getPostById(postid);
				if ((v3xdept != null) && (v3xdept.getEnabled().booleanValue()) && (v3xpost != null)
						&& (v3xpost.getEnabled().booleanValue())) {
					sbuffer.append(v3xdept.getName());
					sbuffer.append("-");
					idsbuffer.append(deptid);
					idsbuffer.append("_");
					sbuffer.append(v3xpost.getName());
					idsbuffer.append(postid);
					deptpostbuffer.append(sbuffer.toString());
					deptpostIdsBuffer.append(idsbuffer.toString());
					deptpostbuffer.append(",");
					deptpostIdsBuffer.append(",");
				}
			}
			if (deptpostbuffer.length() > 0) {
				String deptpostStr = deptpostbuffer.toString();
				String deptpostIdsStr = deptpostIdsBuffer.toString();
				deptpostStr = deptpostStr.substring(0, deptpostStr.length() - 1);
				deptpostIdsStr = deptpostIdsStr.substring(0, deptpostIdsStr.length() - 1);
				webstaffinfo.setSecond_posts(deptpostStr);
				webstaffinfo.setSecond_posts_ids(deptpostIdsStr);
			}
		}
		return webstaffinfo;
	}

	public ModelAndView editRelationship(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView("hr/staffInfo/editRelationship");
		String ID = request.getParameter("id");
		if ((ID != null) && (!ID.equals(""))) {
			Relationship relationship = this.staffInfoManager.getRelationshipById(Long.valueOf(ID));
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
		mav.addObject("ReadOnly", Boolean.valueOf(readOnly));
		return mav;
	}

	public ModelAndView editWorkRecord(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView("hr/staffInfo/editWorkRecord");
		String ID = request.getParameter("id");
		if ((ID != null) && (!ID.equals(""))) {
			WorkRecord workRecord = this.staffInfoManager.getWorkRecordById(Long.valueOf(ID));
			mav.addObject("workRecord", workRecord);
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

	public ModelAndView editEduExperience(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView("hr/staffInfo/editEduExperience");
		String ID = request.getParameter("id");
		if ((ID != null) && (!ID.equals(""))) {
			EduExperience eduExperience = this.staffInfoManager.getEduExperienceById(Long.valueOf(ID));
			mav.addObject("eduExperience", eduExperience);
			mav.addObject("attachments",
					this.attachmentManager.getByReference(eduExperience.getId(), eduExperience.getId()));
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

	public ModelAndView editPostChange(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView("hr/staffInfo/editPostChange");
		String ID = request.getParameter("id");
		if ((ID != null) && (!ID.equals(""))) {
			PostChange postChange = this.staffInfoManager.getPostChangeById(Long.valueOf(ID));
			mav.addObject("postChange", postChange);
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

	public ModelAndView editAssess(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView("hr/staffInfo/editAssess");
		String ID = request.getParameter("id");
		if ((ID != null) && (!ID.equals(""))) {
			Assess assess = this.staffInfoManager.getAssessById(Long.valueOf(ID));
			mav.addObject("assess", assess);
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

	public ModelAndView editRewardsAndPunishment(HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		ModelAndView mav = new ModelAndView("hr/staffInfo/editRewardsAndPunishment");
		String ID = request.getParameter("id");
		if ((ID != null) && (!ID.equals(""))) {
			RewardsAndPunishment rewardsAndPunishment = this.staffInfoManager
					.getRewardsAndPunishmentById(Long.valueOf(ID));
			mav.addObject("rewardsAndPunishment", rewardsAndPunishment);
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

	public ModelAndView updateStaffer(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String ldapEntry = request.getParameter("ldapUserCodes");
		String selectOU = request.getParameter("selectOU");
		String isNew = request.getParameter("isNew");
		User user = AppContext.getCurrentUser();
		String workTime;
		SimpleDateFormat sf;
		Map<String, Object> map;
		if ((isNew != null) && (isNew.equals("New"))) {
			V3xOrgMember member = new V3xOrgMember();
			member.setIdIfNew();
			bind(request, member);
			String loginName = request.getParameter("loginName");
			String password = request.getParameter("password");
			member.setV3xOrgPrincipal(new V3xOrgPrincipal(member.getId(), loginName, password));

			member.setOrgAccountId(user.getLoginAccount());

			String strSecondPostIds = request.getParameter("second_post_ids");
			String[] arrSecondPosts = strSecondPostIds.split(",");
			if ((arrSecondPosts != null) && (arrSecondPosts.length > 0)) {
				String[] arrayOfString1;
				int j = (arrayOfString1 = arrSecondPosts).length;
				for (int i = 0; i < j; i++) {
					String secondpostid = arrayOfString1[i];
					String[] arrDeptPosts = secondpostid.split("_");
					if ((arrDeptPosts[0] != null) && (!arrDeptPosts[0].equals("")) && (arrDeptPosts[1] != null)
							&& (!arrDeptPosts[1].equals(""))) {
						member.addSecondPost(Long.valueOf(Long.parseLong(arrDeptPosts[0])),
								Long.valueOf(Long.parseLong(arrDeptPosts[1])));
					}
				}
			}
			StaffInfo staffinfo = new StaffInfo();
			staffinfo.setIdIfNew();
			String wage = request.getParameter("recordWage");
			if ((wage != null) && (!wage.equals(""))) {
				Double record_wage = Double.valueOf(wage);
				staffinfo.setRecord_wage(record_wage);
			}
			String workingTime = request.getParameter("workingTime");
			if ((workingTime != null) && (!workingTime.equals(""))) {
				staffinfo.setWorking_time(Integer.valueOf(workingTime).intValue());
			}
			bind(request, staffinfo);
			String recordWage = request.getParameter("recordWage");
			if (StringUtils.isNotBlank(recordWage)) {
				staffinfo.setRecord_wage(Double.valueOf(recordWage));
			} else {
				staffinfo.setRecord_wage(null);
			}
			String gender = request.getParameter("gender");
			staffinfo.setSex(gender);
			staffinfo.setOrg_member_id(member.getId());
			String birthday = request.getParameter("birthday");
			workTime = request.getParameter("work_starting_date");
			sf = new SimpleDateFormat("yyyy-MM-dd");
			if (StringUtils.isNotBlank(birthday)) {
				staffinfo.setBirthday(sf.parse(birthday));
			} else {
				staffinfo.setBirthday(null);
			}
			if (StringUtils.isNotBlank(workTime)) {
				staffinfo.setWork_starting_date(sf.parse(workTime));
			} else {
				staffinfo.setWork_starting_date(null);
			}
			staffinfo.getImage_id();
			staffinfo.getImage_datetime();
			this.staffInfoManager.addStaffInfo(request, staffinfo, member);

			map = new HashMap();
			map.put("gender", Integer.valueOf(Integer.parseInt(gender)));
			map.put("birthday", staffinfo.getBirthday());
			if (staffinfo.getImage_id() != null) {
				map.put("imageid",
						"/fileUpload.do?method=showRTE&fileId=" + staffinfo.getImage_id() + "&createDate="
								+ new SimpleDateFormat("yyyy-MM-dd").format(staffinfo.getImage_datetime())
								+ "&type=image".trim());
			}
			member.setProperties(map);
			V3xOrgPrincipal p = new V3xOrgPrincipal(member.getId(), loginName, password);
			member.setV3xOrgPrincipal(p);
			String as = request.getParameter("primaryLanguange");
			Locale local = LocaleContext.parseLocale(as);
			this.orgManagerDirect.setMemberLocale(member, local);
			OrganizationMessage message = this.orgManagerDirect.addMember(member);
			this.operationlogManager.insertOplog(member.getOrgAccountId(), Constants.MODULE_STAFF,
					ApplicationCategoryEnum.hr, "hr.staffInfo.operation.add.label", "hr.staffInfo.info.add.label",
					new Object[] { member.getName() });
			try {
				OrgHelper.throwBusinessExceptionTools(message);
			} catch (com.seeyon.ctp.common.exceptions.BusinessException e) {
				super.infoCloseOrFresh(request, response, e.getMessage());
				return null;
			}
			String securityIdsStr = request.getParameter("securityIds");
			if ((securityIdsStr != null) && (securityIdsStr.length() > 0)) {
				String[] securityIds = securityIdsStr.split(",");
				List<Long> securityIdsList = new ArrayList();
				String[] arrayOfString2;
				int m = (arrayOfString2 = securityIds).length;
				for (int k = 0; k < m; k++) {
					String idStr = arrayOfString2[k];
					securityIdsList.add(Long.valueOf(Long.parseLong(idStr)));
				}
			}
			if (LDAPConfig.getInstance().getIsEnableLdap()) {
				if (SystemEnvironment.hasPlugin("ldap")) {
					try {
						if (StringUtils.isBlank(ldapEntry)) {
							user.setRemoteAddr(request.getRemoteAddr());
							this.appLogManager.insertLog(user, AppLogAction.LDAP_Account_Create,
									new String[] { member.getName(), selectOU });
						} else {
							response.getWriter();

							user.setRemoteAddr(request.getRemoteAddr());
							this.appLogManager.insertLog(user, AppLogAction.LDAP_Account_Bing_Create,
									new String[] { member.getName(), ldapEntry });
						}
					} catch (Exception e) {
						LOG.error("ldap/ad 添加人员绑定不成功！", e);
					}
				}
			}
			try {
				response.setContentType("text/html;charset=UTF-8");
				PrintWriter out = response.getWriter();
				out.println("<script type='text/javascript'>");
				out.println("alert(parent.v3x.getMessage(\"HRLang.hr_staffInfo_operationSuccessful_label\"))");
				out.println("</script>");
			} catch (IOException e) {
				LOG.error("", e);
			}
			user.setRemoteAddr(request.getRemoteAddr());
			this.appLogManager.insertLog(user, AppLogAction.Hr_NewStaffInfo,
					new String[] { user.getName(), member.getName() });
			return super.redirectModelAndView("/hrStaff.do?method=initInfoHome&staffId=" + member.getId()
					+ "&isReadOnly=ReadOnly&isManager=Manager", "parent");
		}
		String loginName = request.getParameter("loginName");
		String password = request.getParameter("password");
		Long staffid = Long.valueOf(Long.parseLong(request.getParameter("staffId")));
		V3xOrgMember member = this.orgManager.getMemberById(staffid);
		V3xOrgMember member1 = null;
		if (member != null) {
			member1 = member;
		}
		PrintWriter out = response.getWriter();
		StaffInfo staffinfo = this.staffInfoManager.getStaffInfoById(staffid);

		V3xOrgMember memberBeforeUpdate = new V3xOrgMember();
		memberBeforeUpdate.setId(member.getId());
		BeanUtils.copyProperties(memberBeforeUpdate, member);

		String strSecondPostIds = request.getParameter("second_post_ids");
		String[] arrSecondPosts = strSecondPostIds.split(",");
		if ((arrSecondPosts != null) && (arrSecondPosts.length > 0)) {
			member.setSecond_post(new ArrayList());
			for (int x = 0; x < arrSecondPosts.length; x++) {
				String secondpostid = "";
				String[] arrDeptPosts = secondpostid.split("_");
				if ((arrDeptPosts[0] != null) && (!arrDeptPosts[0].equals("")) && (arrDeptPosts[1] != null)
						&& (!arrDeptPosts[1].equals(""))) {
					member.addSecondPost(Long.valueOf(Long.parseLong(arrDeptPosts[0])),
							Long.valueOf(Long.parseLong(arrDeptPosts[1])));
				}
			}
		}
		bind(request, member);

		member.setV3xOrgPrincipal(new V3xOrgPrincipal(member.getId(), loginName, password));
		memberBeforeUpdate.getLoginName().equals(member.getLoginName());

		V3xOrgMember newMember = new V3xOrgMember();
		newMember.setId(member.getId());
		BeanUtils.copyProperties(newMember, member);
		String recordWage;
		String as;
		if (staffinfo == null) {
			StaffInfo staff = new StaffInfo();
			staff.setIdIfNew();
			String wage = request.getParameter("recordWage");
			if ((wage != null) && (!wage.equals(""))) {
				Double record_wage = Double.valueOf(wage);
				staff.setRecord_wage(record_wage);
			}
			String workingTime = request.getParameter("workingTime");
			if ((workingTime != null) && (!workingTime.equals(""))) {
				staff.setWorking_time(Integer.valueOf(workingTime).intValue());
			}
			bind(request, staff);
			String gender = request.getParameter("gender");
			staff.setSex(gender);
			staff.setOrg_member_id(staffid);

			String birthday = request.getParameter("birthday");
			workTime = request.getParameter("work_starting_date");
			sf = new SimpleDateFormat("yyyy-MM-dd");
			if (StringUtils.isNotBlank(birthday)) {
				staff.setBirthday(sf.parse(birthday));
			} else {
				staff.setBirthday(null);
			}
			if (StringUtils.isNotBlank(workTime)) {
				staff.setWork_starting_date(sf.parse(workTime));
			} else {
				staff.setWork_starting_date(null);
			}
			recordWage = request.getParameter("recordWage");
			if (StringUtils.isNotBlank(recordWage)) {
				staff.setRecord_wage(Double.valueOf(recordWage));
			} else {
				staff.setRecord_wage(null);
			}
			this.staffInfoManager.updateStaffInfo(request, staff, member, false);
			map = new HashMap();
			map.put("gender", Integer.valueOf(Integer.parseInt(gender)));
			map.put("birthday", staff.getBirthday());
			if (member1 != null) {
				map.put("telnumber", member1.getTelNumber());
				map.put("officenumber", member.getOfficeNum());
				map.put("emailaddress", member1.getEmailAddress());
			}
			if (staff.getImage_id() != null) {
				map.put("imageid", "/fileUpload.do?method=showRTE&fileId=" + staff.getImage_id() + "&createDate="
						+ new SimpleDateFormat("yyyy-MM-dd").format(staff.getImage_datetime()) + "&type=image".trim());
			}
			member.setProperties(map);
			as = request.getParameter("primaryLanguange");
			Locale local = LocaleContext.parseLocale(as);
			this.orgManagerDirect.setMemberLocale(member, local);
			OrganizationMessage updateMember = this.orgManagerDirect.updateMember(member);
			try {
				OrgHelper.throwBusinessExceptionTools(updateMember);
			} catch (com.seeyon.ctp.common.exceptions.BusinessException e) {
				alertError(out, response, e);
				return null;
			}
			this.operationlogManager.insertOplog(member.getOrgAccountId(), Constants.MODULE_STAFF,
					ApplicationCategoryEnum.hr, "hr.staffInfo.operation.update.label", "hr.staffInfo.info.update.label",
					new Object[] { member.getName() });
		} else {
			String wage = request.getParameter("recordWage");
			if ((wage != null) && (!wage.equals(""))) {
				Double record_wage = Double.valueOf(wage);
				staffinfo.setRecord_wage(record_wage);
			}
			String workingTime = request.getParameter("workingTime");
			if ((workingTime != null) && (!workingTime.equals(""))) {
				staffinfo.setWorking_time(Integer.valueOf(workingTime).intValue());
			}
			bind(request, staffinfo);
			String birthday = request.getParameter("birthday");
			workTime = request.getParameter("work_starting_date");
			sf = new SimpleDateFormat("yyyy-MM-dd");
			if (StringUtils.isNotBlank(birthday)) {
				staffinfo.setBirthday(sf.parse(birthday));
			} else {
				staffinfo.setBirthday(null);
			}
			if (StringUtils.isNotBlank(workTime)) {
				staffinfo.setWork_starting_date(sf.parse(workTime));
			} else {
				staffinfo.setWork_starting_date(null);
			}
			recordWage = request.getParameter("recordWage");
			if (StringUtils.isNotBlank(recordWage)) {
				staffinfo.setRecord_wage(Double.valueOf(recordWage));
			} else {
				staffinfo.setRecord_wage(null);
			}
			as = request.getParameter("primaryLanguange");
			Locale local = LocaleContext.parseLocale(as);
			this.orgManagerDirect.setMemberLocale(member, local);
			String gender = request.getParameter("gender");
			staffinfo.setSex(gender);
			staffinfo.setOrg_member_id(staffid);

			this.staffInfoManager.updateStaffInfo(request, staffinfo, member, false);
			map = new HashMap();
			map.put("gender", Integer.valueOf(Integer.parseInt(gender)));
			map.put("birthday", staffinfo.getBirthday());
			if (member1 != null) {
				map.put("telnumber", member1.getTelNumber());
				map.put("officenumber", member.getOfficeNum());
				map.put("emailaddress", member1.getEmailAddress());
			}
			if (staffinfo.getImage_id() != null) {
				map.put("imageid",
						"/fileUpload.do?method=showRTE&fileId=" + staffinfo.getImage_id() + "&createDate="
								+ new SimpleDateFormat("yyyy-MM-dd").format(staffinfo.getImage_datetime())
								+ "&type=image".trim());
			}
			member.setProperties(map);
			OrganizationMessage updateMember = this.orgManagerDirect.updateMember(member);
			try {
				OrgHelper.throwBusinessExceptionTools(updateMember);
			} catch (com.seeyon.ctp.common.exceptions.BusinessException e) {
				response.setContentType("text/html;charset=UTF-8");
				alertError(out, response, e);
				return null;
			}
			this.operationlogManager.insertOplog(member.getOrgAccountId(), Constants.MODULE_STAFF,
					ApplicationCategoryEnum.hr, "hr.staffInfo.operation.update.label", "hr.staffInfo.info.update.label",
					new Object[] { member.getName() });
		}
		if ((memberBeforeUpdate.getEnabled().booleanValue()) && (!member.getEnabled().booleanValue())) {
			this.agentIntercalateManager.cancelUserAgent(member.getId(), user);
		}
		String securityIdsStr = request.getParameter("securityIds");
		if ((securityIdsStr != null) && (securityIdsStr.length() > 0)) {
			String[] securityIds = securityIdsStr.split(",");
			List<Long> securityIdsList = new ArrayList();
		}
		try {
			isChangeOrgInfo(memberBeforeUpdate, newMember);
		} catch (Exception e) {
			this.logger.error("推送模板报错", e);
		}
		try {
			response.setContentType("text/html;charset=UTF-8");
			out = response.getWriter();
			out.println("<script type='text/javascript'>");
			out.println("alert(parent.v3x.getMessage(\"HRLang.hr_staffInfo_operationSuccessful_label\"))");
			out.println("</script>");
		} catch (IOException e) {
			LOG.error("", e);
		}
		user.setRemoteAddr(request.getRemoteAddr());
		this.appLogManager.insertLog(user, AppLogAction.Hr_UpdateStaffInfo,
				new String[] { user.getName(), member.getName() });

		member.getEnabled().booleanValue();

		String isManager = request.getParameter("isManager");
		String staId = request.getParameter("staffId");
		if ((isManager != null) && (!isManager.equals(""))) {
			return super.redirectModelAndView("/hrStaff.do?method=initInfoHome&staffId=" + staId
					+ "&isReadOnly=ReadOnly&isManager=Manager&load=1", "parent");
		}
		return super.refreshWorkspace();
	}

	private boolean isChangeOrgInfo(V3xOrgMember memberBeforeUpdate, V3xOrgMember member) {
		if ((memberBeforeUpdate.getOrgDepartmentId().longValue() != member.getOrgDepartmentId().longValue())
				|| (memberBeforeUpdate.getOrgLevelId().longValue() != member.getOrgLevelId().longValue())
				|| (memberBeforeUpdate.getOrgPostId().longValue() != member.getOrgPostId().longValue())) {
			return true;
		}
		List<MemberPost> secondPostBeforUpdate = memberBeforeUpdate.getSecond_post();
		List<MemberPost> secondPost = member.getSecond_post();
		if (secondPost.size() == secondPostBeforUpdate.size()) {
			int i = 0;
			for (MemberPost post : secondPost) {
				if (secondPostBeforUpdate.contains(post)) {
					i++;
				}
			}
			if (i != secondPost.size()) {
				return true;
			}
		} else {
			return true;
		}
		return false;
	}

	public ModelAndView updateContactInfo(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Long staffid = Long.valueOf(Long.parseLong(request.getParameter("staffId")));
		V3xOrgMember member = this.orgManager.getMemberById(staffid);
		User user = AppContext.getCurrentUser();

		V3xOrgMember memberBeforeUpdate = new V3xOrgMember();
		memberBeforeUpdate.setId(Long.valueOf(-1L));
		BeanUtils.copyProperties(memberBeforeUpdate, member);
		member.setIdIfNew();
		member.setProperty("emailaddress", request.getParameter("email"));
		member.setProperty("address", request.getParameter("address"));
		member.setProperty("telnumber", request.getParameter("telephone"));
		member.setProperty("officenumber", request.getParameter("officeNum"));
		member.setProperty("postalcode",
				request.getParameter("postalcode") == null ? "" : request.getParameter("postalcode").toString());
		member.setProperty("website",
				request.getParameter("website") == null ? "" : request.getParameter("website").toString());
		member.setProperty("blog", request.getParameter("blog") == null ? "" : request.getParameter("blog").toString());
		ContactInfo contact = this.staffInfoManager.getContactInfoById(staffid);
		if (contact == null) {
			ContactInfo contactInfo = new ContactInfo();
			bind(request, contactInfo);
			contactInfo.setMember_id(staffid);
			this.staffInfoManager.addContactInfo(contactInfo, member);
			this.orgManagerDirect.updateMember(member);

			this.operationlogManager.insertOplog(member.getOrgAccountId(), Constants.MODULE_STAFF,
					ApplicationCategoryEnum.hr, "hr.staffInfo.other.add.label", "hr.staffInfo.ContactInfo.add.label",
					new Object[] { member.getName() });
		} else {
			bind(request, contact);
			contact.setMember_id(staffid);
			member.setProperty("emailaddress", request.getParameter("email"));
			member.setProperty("telnumber", request.getParameter("telephone"));
			member.setProperty("officenumber", request.getParameter("officeNum"));
			this.staffInfoManager.updateContactInfo(contact, member);
			this.orgManagerDirect.updateMember(member);
			this.operationlogManager.insertOplog(member.getOrgAccountId(), Constants.MODULE_STAFF,
					ApplicationCategoryEnum.hr, "hr.staffInfo.other.update.label",
					"hr.staffInfo.ContactInfo.update.label", new Object[] { member.getName() });
		}
		user.setRemoteAddr(request.getRemoteAddr());
		this.appLogManager.insertLog(user, AppLogAction.Hr_UpdateStaffInfo,
				new String[] { user.getName(), member.getName() });

		return super.redirectModelAndView(
				"/hrStaff.do?method=initContactInfo&staffId=" + staffid + "&infoType=2&isReadOnly=ReadOnly");
	}

	public ModelAndView updateRelationship(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String staffid = request.getParameter("staffId");
		User user = AppContext.getCurrentUser();
		V3xOrgMember member = this.orgManager.getMemberById(Long.valueOf(Long.parseLong(staffid)));
		String ID = request.getParameter("id");
		if ((ID == null) || (ID.equals(""))) {
			Relationship relationship = new Relationship();
			bind(request, relationship);
			String birthday = request.getParameter("birthday");
			if (StringUtils.isNotBlank(birthday)) {
				relationship.setBirthday(new SimpleDateFormat("yyyy-MM-dd").parse(birthday));
			} else {
				relationship.setBirthday(null);
			}
			relationship.setMember_id(Long.valueOf(staffid));
			this.staffInfoManager.addRelationship(relationship);
			this.operationlogManager.insertOplog(member.getOrgAccountId(), Constants.MODULE_STAFF,
					ApplicationCategoryEnum.hr, "hr.staffInfo.other.add.label", "hr.staffInfo.Relationship.add.label",
					new Object[] { member.getName() });
		} else {
			Relationship rela = this.staffInfoManager.getRelationshipById(Long.valueOf(ID));

			bind(request, rela);
			String birthday = request.getParameter("birthday");
			if (StringUtils.isNotBlank(birthday)) {
				rela.setBirthday(new SimpleDateFormat("yyyy-MM-dd").parse(birthday));
			} else {
				rela.setBirthday(null);
			}
			this.staffInfoManager.updateRelationship(rela);
			this.operationlogManager.insertOplog(member.getOrgAccountId(), Constants.MODULE_STAFF,
					ApplicationCategoryEnum.hr, "hr.staffInfo.other.update.label",
					"hr.staffInfo.Relationship.update.label", new Object[] { member.getName() });
		}
		String isManager = "";
		String Manager = request.getParameter("Manager");
		if ((Manager != null) && (Manager.equals("true"))) {
			isManager = "Manager";
		}
		user.setRemoteAddr(request.getRemoteAddr());
		this.appLogManager.insertLog(user, AppLogAction.Hr_UpdateStaffInfo,
				new String[] { user.getName(), member.getName() });

		return super.redirectModelAndView(
				"/hrStaff.do?method=initHome&infoType=3&staffId=" + staffid + "&isManager=" + isManager,
				"parent.parent");
	}

	public ModelAndView updateWorkRecord(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String staffid = request.getParameter("staffId");
		User user = AppContext.getCurrentUser();
		V3xOrgMember member = this.orgManager.getMemberById(Long.valueOf(Long.parseLong(staffid)));
		String ID = request.getParameter("id");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String start_time = request.getParameter("start_time");
		String end_time = request.getParameter("end_time");
		if ((ID == null) || ("".equals(ID))) {
			WorkRecord workrecord = new WorkRecord();
			bind(request, workrecord);
			if (StringUtils.isNotBlank(start_time)) {
				workrecord.setStart_time(sdf.parse(start_time));
			} else {
				workrecord.setStart_time(null);
			}
			if (StringUtils.isNotBlank(end_time)) {
				workrecord.setEnd_time(sdf.parse(end_time));
			} else {
				workrecord.setEnd_time(null);
			}
			workrecord.setMember_id(Long.valueOf(staffid));
			this.staffInfoManager.addWorkRecord(workrecord);
			this.operationlogManager.insertOplog(member.getOrgAccountId(), Constants.MODULE_STAFF,
					ApplicationCategoryEnum.hr, "hr.staffInfo.other.add.label", "hr.staffInfo.WorkRecord.add.label",
					new Object[] { member.getName() });
		} else {
			WorkRecord work = this.staffInfoManager.getWorkRecordById(Long.valueOf(ID));
			bind(request, work);
			if (StringUtils.isNotBlank(start_time)) {
				work.setStart_time(sdf.parse(start_time));
			} else {
				work.setStart_time(null);
			}
			if (StringUtils.isNotBlank(end_time)) {
				work.setEnd_time(sdf.parse(end_time));
			} else {
				work.setEnd_time(null);
			}
			this.staffInfoManager.updateWorkRecord(work);
			this.operationlogManager.insertOplog(member.getOrgAccountId(), Constants.MODULE_STAFF,
					ApplicationCategoryEnum.hr, "hr.staffInfo.other.update.label",
					"hr.staffInfo.WorkRecord.update.label", new Object[] { member.getName() });
		}
		String isManager = "";
		String Manager = request.getParameter("Manager");
		if ((Manager != null) && (Manager.equals("true"))) {
			isManager = "Manager";
		}
		user.setRemoteAddr(request.getRemoteAddr());
		this.appLogManager.insertLog(user, AppLogAction.Hr_UpdateStaffInfo,
				new String[] { user.getName(), member.getName() });

		return super.redirectModelAndView(
				"/hrStaff.do?method=initHome&infoType=4&staffId=" + staffid + "&isManager=" + isManager,
				"parent.parent");
	}

	public ModelAndView updateEduExperience(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String staffid = request.getParameter("staffId");
		User user = AppContext.getCurrentUser();
		V3xOrgMember member = this.orgManager.getMemberById(Long.valueOf(Long.parseLong(staffid)));
		String ID = request.getParameter("id");
		SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
		String start_time = request.getParameter("start_time");
		String end_time = request.getParameter("end_time");
		if ((ID == null) || (ID.equals(""))) {
			EduExperience eduExperience = new EduExperience();
			bind(request, eduExperience);
			if (StringUtils.isNotBlank(start_time)) {
				eduExperience.setStart_time(sf.parse(start_time));
			} else {
				eduExperience.setStart_time(null);
			}
			if (StringUtils.isNotBlank(end_time)) {
				eduExperience.setEnd_time(sf.parse(end_time));
			} else {
				eduExperience.setEnd_time(null);
			}
			eduExperience.setMember_id(Long.valueOf(staffid));
			this.staffInfoManager.addEduExperience(eduExperience);
			this.attachmentManager.create(ApplicationCategoryEnum.hr, eduExperience.getId(), eduExperience.getId(),
					request);
			this.operationlogManager.insertOplog(member.getOrgAccountId(), Constants.MODULE_STAFF,
					ApplicationCategoryEnum.hr, "hr.staffInfo.other.add.label", "hr.staffInfo.EduExperience.add.label",
					new Object[] { member.getName() });
		} else {
			EduExperience edu = this.staffInfoManager.getEduExperienceById(Long.valueOf(ID));
			bind(request, edu);
			if (StringUtils.isNotBlank(start_time)) {
				edu.setStart_time(sf.parse(start_time));
			} else {
				edu.setStart_time(null);
			}
			if (StringUtils.isNotBlank(end_time)) {
				edu.setEnd_time(sf.parse(end_time));
			} else {
				edu.setEnd_time(null);
			}
			this.staffInfoManager.updateEduExperience(edu);
			this.attachmentManager.deleteByReference(edu.getId(), edu.getId());
			this.attachmentManager.create(ApplicationCategoryEnum.hr, edu.getId(), edu.getId(), request);
			this.operationlogManager.insertOplog(member.getOrgAccountId(), Constants.MODULE_STAFF,
					ApplicationCategoryEnum.hr, "hr.staffInfo.other.update.label",
					"hr.staffInfo.EduExperience.update.label", new Object[] { member.getName() });
		}
		String isManager = "";
		String Manager = request.getParameter("Manager");
		if ((Manager != null) && (Manager.equals("true"))) {
			isManager = "Manager";
		}
		user.setRemoteAddr(request.getRemoteAddr());
		this.appLogManager.insertLog(user, AppLogAction.Hr_UpdateStaffInfo,
				new String[] { user.getName(), member.getName() });

		return super.redirectModelAndView(
				"/hrStaff.do?method=initHome&infoType=5&staffId=" + staffid + "&isManager=" + isManager,
				"parent.parent");
	}

	public ModelAndView updatePostChange(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String staffid = request.getParameter("staffId");
		User user = AppContext.getCurrentUser();
		V3xOrgMember member = this.orgManager.getMemberById(Long.valueOf(Long.parseLong(staffid)));
		String ID = request.getParameter("id");
		SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
		String start_time = request.getParameter("start_time");
		String end_time = request.getParameter("end_time");
		if ((ID == null) || (ID.equals(""))) {
			PostChange postChange = new PostChange();
			bind(request, postChange);
			if (StringUtils.isNotBlank(start_time)) {
				postChange.setStart_time(sf.parse(start_time));
			} else {
				postChange.setStart_time(null);
			}
			if (StringUtils.isNotBlank(end_time)) {
				postChange.setEnd_time(sf.parse(end_time));
			} else {
				postChange.setEnd_time(null);
			}
			postChange.setMember_id(Long.valueOf(staffid));
			this.staffInfoManager.addPostChange(postChange);
			this.operationlogManager.insertOplog(member.getOrgAccountId(), Constants.MODULE_STAFF,
					ApplicationCategoryEnum.hr, "hr.staffInfo.other.add.label", "hr.staffInfo.PostChange.add.label",
					new Object[] { member.getName() });
		} else {
			PostChange post = this.staffInfoManager.getPostChangeById(Long.valueOf(ID));
			bind(request, post);
			if (StringUtils.isNotBlank(start_time)) {
				post.setStart_time(sf.parse(start_time));
			} else {
				post.setStart_time(null);
			}
			if (StringUtils.isNotBlank(end_time)) {
				post.setEnd_time(sf.parse(end_time));
			} else {
				post.setEnd_time(null);
			}
			this.staffInfoManager.updatePostChange(post);
			this.operationlogManager.insertOplog(member.getOrgAccountId(), Constants.MODULE_STAFF,
					ApplicationCategoryEnum.hr, "hr.staffInfo.other.update.label",
					"hr.staffInfo.PostChange.update.label", new Object[] { member.getName() });
		}
		String isManager = "";
		String Manager = request.getParameter("Manager");
		if ((Manager != null) && (Manager.equals("true"))) {
			isManager = "Manager";
		}
		user.setRemoteAddr(request.getRemoteAddr());
		this.appLogManager.insertLog(user, AppLogAction.Hr_UpdateStaffInfo,
				new String[] { user.getName(), member.getName() });

		return super.redirectModelAndView(
				"/hrStaff.do?method=initHome&infoType=6&staffId=" + staffid + "&isManager=" + isManager,
				"parent.parent");
	}

	public ModelAndView updateAssess(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String staffid = request.getParameter("staffId");
		User user = AppContext.getCurrentUser();
		V3xOrgMember member = this.orgManager.getMemberById(Long.valueOf(Long.parseLong(staffid)));
		String ID = request.getParameter("id");
		SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
		String begin_date = request.getParameter("begin_date");
		String end_date = request.getParameter("end_date");
		if ((ID == null) || (ID.equals(""))) {
			Assess assess = new Assess();
			bind(request, assess);
			assess.setMember_id(Long.valueOf(staffid));
			if (StringUtils.isNotBlank(begin_date)) {
				assess.setBegin_date(sf.parse(begin_date));
			} else {
				assess.setBegin_date(null);
			}
			if (StringUtils.isNotBlank(end_date)) {
				assess.setEnd_date(sf.parse(end_date));
			} else {
				assess.setEnd_date(null);
			}
			this.staffInfoManager.addAssess(assess);
			this.operationlogManager.insertOplog(member.getOrgAccountId(), Constants.MODULE_STAFF,
					ApplicationCategoryEnum.hr, "hr.staffInfo.other.add.label", "hr.staffInfo.Assess.add.label",
					new Object[] { member.getName() });
		} else {
			Assess ass = this.staffInfoManager.getAssessById(Long.valueOf(ID));
			bind(request, ass);
			if (StringUtils.isNotBlank(begin_date)) {
				ass.setBegin_date(sf.parse(begin_date));
			} else {
				ass.setBegin_date(null);
			}
			if (StringUtils.isNotBlank(end_date)) {
				ass.setEnd_date(sf.parse(end_date));
			} else {
				ass.setEnd_date(null);
			}
			this.staffInfoManager.updateAssess(ass);
			this.operationlogManager.insertOplog(member.getOrgAccountId(), Constants.MODULE_STAFF,
					ApplicationCategoryEnum.hr, "hr.staffInfo.other.update.label", "hr.staffInfo.Assess.update.label",
					new Object[] { member.getName() });
		}
		String isManager = "";
		String Manager = request.getParameter("Manager");
		if ((Manager != null) && (Manager.equals("true"))) {
			isManager = "Manager";
		}
		user.setRemoteAddr(request.getRemoteAddr());
		this.appLogManager.insertLog(user, AppLogAction.Hr_UpdateStaffInfo,
				new String[] { user.getName(), member.getName() });

		return super.redirectModelAndView(
				"/hrStaff.do?method=initHome&infoType=7&staffId=" + staffid + "&isManager=" + isManager,
				"parent.parent");
	}

	public ModelAndView updateRewardsAndPunishment(HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		String staffid = request.getParameter("staffId");
		SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
		String time = request.getParameter("time");
		String ID = request.getParameter("id");
		V3xOrgMember member = this.orgManager.getMemberById(Long.valueOf(Long.parseLong(staffid)));
		if ((ID == null) || (ID.equals(""))) {
			RewardsAndPunishment rewardsAndPunishment = new RewardsAndPunishment();
			bind(request, rewardsAndPunishment);
			rewardsAndPunishment.setMember_id(Long.valueOf(staffid));
			if (StringUtils.isNotBlank(time)) {
				rewardsAndPunishment.setTime(sf.parse(time));
			} else {
				rewardsAndPunishment.setTime(null);
			}
			this.staffInfoManager.addRewardsAndPunishment(rewardsAndPunishment);
			this.operationlogManager.insertOplog(member.getOrgAccountId(), Constants.MODULE_STAFF,
					ApplicationCategoryEnum.hr, "hr.staffInfo.other.add.label",
					"hr.staffInfo.RewardsAndPunishment.add.label", new Object[] { member.getName() });
		} else {
			RewardsAndPunishment reward = this.staffInfoManager.getRewardsAndPunishmentById(Long.valueOf(ID));
			bind(request, reward);
			if (StringUtils.isNotBlank(time)) {
				reward.setTime(sf.parse(time));
			} else {
				reward.setTime(null);
			}
			this.staffInfoManager.updateRewardsAndPunishment(reward);
			this.operationlogManager.insertOplog(member.getOrgAccountId(), Constants.MODULE_STAFF,
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
		this.appLogManager.insertLog(user, AppLogAction.Hr_UpdateStaffInfo,
				new String[] { user.getName(), member.getName() });

		return super.redirectModelAndView(
				"/hrStaff.do?method=initHome&infoType=8&staffId=" + staffid + "&isManager=" + isManager,
				"parent.parent");
	}

	public String[] getIds(String strIds) {
		if ((strIds != null) && (!strIds.equals(""))) {
			strIds = strIds.substring(0, strIds.lastIndexOf(','));
			String[] arrIds = strIds.split(",");
			return arrIds;
		}
		return null;
	}

	public ModelAndView deleteStaffer(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String[] membetIds = request.getParameter("staffIds").split(",");

		PrintWriter out = response.getWriter();
		User user = AppContext.getCurrentUser();

		List<String[]> logLabels = new ArrayList();
		String[] arrayOfString1;
		int j = (arrayOfString1 = membetIds).length;
		for (int i = 0; i < j; i++) {
			String string = arrayOfString1[i];
			Long ids = Long.valueOf(Long.parseLong(string));
			V3xOrgMember mem = this.orgManager.getMemberById(ids);
			mem.setIsDeleted(Boolean.valueOf(true));
			if (LDAPConfig.getInstance().getIsEnableLdap()) {
				if (SystemEnvironment.hasPlugin("ldap")) {
					try {
						List<V3xOrgMember> memberList = new ArrayList();
						memberList.add(mem);
						LOG.debug(mem.getLoginName() + "delete HR Mem");
					} catch (Exception e) {
						LOG.error("ldap_ad 删除人员绑定不成功！", e);
					}
				}
			}
			this.staffInfoManager.deleteStaffInfo(ids);
			this.orgManagerDirect.deleteMember(mem);
			String[] delLog = new String[2];
			delLog[0] = user.getName();
			delLog[1] = mem.getName();
			logLabels.add(delLog);
			this.operationlogManager.insertOplog(mem.getOrgAccountId(), Constants.MODULE_STAFF,
					ApplicationCategoryEnum.hr, "hr.staffInfo.operation.delete.label", "hr.staffInfo.info.delete.label",
					new Object[] { mem.getName() });
		}
		try {
			response.setContentType("text/html;charset=UTF-8");
			out = response.getWriter();
			out.println("<script type='text/javascript'>");
			out.println("alert(\"" + ResourceBundleUtil.getString("com.seeyon.v3x.hr.resource.i18n.HRResources",
					"hr.staffInfo.operationSuccessful.label", new Object[0]) + "\");");
			out.println("</script>");
		} catch (IOException e) {
			LOG.error("", e);
		}
		user.setRemoteAddr(request.getRemoteAddr());
		this.appLogManager.insertLogs(user, AppLogAction.Hr_DeleteStaffInfo, logLabels);

		return super.redirectModelAndView("/hrStaff.do?method=initStaffInfoList");
	}

	public ModelAndView deleteRelationship(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String[] arrIDs = getIds(request.getParameter("ids"));
		String isManager = request.getParameter("isManager");
		String staffId = request.getParameter("staffId");
		V3xOrgMember member = this.orgManager.getMemberById(Long.valueOf(Long.parseLong(staffId)));
		if ((arrIDs != null) && (arrIDs.length > 0)) {
			String[] arrayOfString1;
			int j = (arrayOfString1 = arrIDs).length;
			for (int i = 0; i < j; i++) {
				String strID = arrayOfString1[i];
				Long id = Long.valueOf(Long.parseLong(strID));
				this.staffInfoManager.deleteRelationship(id);
				this.operationlogManager.insertOplog(member.getOrgAccountId(), Constants.MODULE_STAFF,
						ApplicationCategoryEnum.hr, "hr.staffInfo.other.delete.label",
						"hr.staffInfo.Relationship.delete.label", new Object[] { member.getName() });
			}
		}
		User user = AppContext.getCurrentUser();

		user.setRemoteAddr(request.getRemoteAddr());
		this.appLogManager.insertLog(user, AppLogAction.Hr_UpdateStaffInfo,
				new String[] { user.getName(), member.getName() });

		return super.redirectModelAndView(
				"/hrStaff.do?method=initHome&infoType=3&staffId=" + staffId + "&isManager=" + isManager, "parent");
	}

	public ModelAndView deleteWorkRecord(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String[] arrIDs = getIds(request.getParameter("ids"));
		String isManager = request.getParameter("isManager");
		String staffId = request.getParameter("staffId");
		V3xOrgMember member = this.orgManager.getMemberById(Long.valueOf(Long.parseLong(staffId)));
		if ((arrIDs != null) && (arrIDs.length > 0)) {
			String[] arrayOfString1;
			int j = (arrayOfString1 = arrIDs).length;
			for (int i = 0; i < j; i++) {
				String strID = arrayOfString1[i];
				Long id = Long.valueOf(Long.parseLong(strID));
				this.staffInfoManager.deleteWorkRecord(id);
				this.operationlogManager.insertOplog(member.getOrgAccountId(), Constants.MODULE_STAFF,
						ApplicationCategoryEnum.hr, "hr.staffInfo.other.delete.label",
						"hr.staffInfo.WorkRecord.delete.label", new Object[] { member.getName() });
			}
		}
		User user = AppContext.getCurrentUser();

		user.setRemoteAddr(request.getRemoteAddr());
		this.appLogManager.insertLog(user, AppLogAction.Hr_UpdateStaffInfo,
				new String[] { user.getName(), member.getName() });

		return super.redirectModelAndView(
				"/hrStaff.do?method=initHome&infoType=4&staffId=" + staffId + "&isManager=" + isManager, "parent");
	}

	public ModelAndView deleteEduExperience(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String[] arrIDs = getIds(request.getParameter("ids"));
		String isManager = request.getParameter("isManager");
		String staffId = request.getParameter("staffId");
		V3xOrgMember member = this.orgManager.getMemberById(Long.valueOf(Long.parseLong(staffId)));
		if ((arrIDs != null) && (arrIDs.length > 0)) {
			String[] arrayOfString1;
			int j = (arrayOfString1 = arrIDs).length;
			for (int i = 0; i < j; i++) {
				String strID = arrayOfString1[i];
				Long id = Long.valueOf(Long.parseLong(strID));
				this.staffInfoManager.deleteEduExperience(id);
				this.attachmentManager.deleteByReference(id);
				this.operationlogManager.insertOplog(member.getOrgAccountId(), Constants.MODULE_STAFF,
						ApplicationCategoryEnum.hr, "hr.staffInfo.other.delete.label",
						"hr.staffInfo.EduExperience.delete.label", new Object[] { member.getName() });
			}
		}
		User user = AppContext.getCurrentUser();

		user.setRemoteAddr(request.getRemoteAddr());
		this.appLogManager.insertLog(user, AppLogAction.Hr_UpdateStaffInfo,
				new String[] { user.getName(), member.getName() });

		return super.redirectModelAndView(
				"/hrStaff.do?method=initHome&infoType=5&staffId=" + staffId + "&isManager=" + isManager, "parent");
	}

	public ModelAndView deletePostChange(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String[] arrIDs = getIds(request.getParameter("ids"));
		String isManager = request.getParameter("isManager");
		String staffId = request.getParameter("staffId");
		V3xOrgMember member = this.orgManager.getMemberById(Long.valueOf(Long.parseLong(staffId)));
		if ((arrIDs != null) && (arrIDs.length > 0)) {
			String[] arrayOfString1;
			int j = (arrayOfString1 = arrIDs).length;
			for (int i = 0; i < j; i++) {
				String strID = arrayOfString1[i];
				Long id = Long.valueOf(Long.parseLong(strID));
				this.staffInfoManager.deletePostChange(id);
				this.operationlogManager.insertOplog(member.getOrgAccountId(), Constants.MODULE_STAFF,
						ApplicationCategoryEnum.hr, "hr.staffInfo.other.delete.label",
						"hr.staffInfo.PostChange.delete.label", new Object[] { member.getName() });
			}
		}
		User user = AppContext.getCurrentUser();

		user.setRemoteAddr(request.getRemoteAddr());
		this.appLogManager.insertLog(user, AppLogAction.Hr_UpdateStaffInfo,
				new String[] { user.getName(), member.getName() });

		return super.redirectModelAndView(
				"/hrStaff.do?method=initHome&infoType=6&staffId=" + staffId + "&isManager=" + isManager, "parent");
	}

	public ModelAndView deleteAssess(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String[] arrIDs = getIds(request.getParameter("ids"));
		String isManager = request.getParameter("isManager");
		String staffId = request.getParameter("staffId");
		V3xOrgMember member = this.orgManager.getMemberById(Long.valueOf(Long.parseLong(staffId)));
		if ((arrIDs != null) && (arrIDs.length > 0)) {
			String[] arrayOfString1;
			int j = (arrayOfString1 = arrIDs).length;
			for (int i = 0; i < j; i++) {
				String strID = arrayOfString1[i];
				Long id = Long.valueOf(Long.parseLong(strID));
				this.staffInfoManager.deleteAssess(id);
				this.operationlogManager.insertOplog(member.getOrgAccountId(), Constants.MODULE_STAFF,
						ApplicationCategoryEnum.hr, "hr.staffInfo.other.delete.label",
						"hr.staffInfo.Assess.delete.label", new Object[] { member.getName() });
			}
		}
		User user = AppContext.getCurrentUser();

		user.setRemoteAddr(request.getRemoteAddr());
		this.appLogManager.insertLog(user, AppLogAction.Hr_UpdateStaffInfo,
				new String[] { user.getName(), member.getName() });

		return super.redirectModelAndView(
				"/hrStaff.do?method=initHome&infoType=7&staffId=" + staffId + "&isManager=" + isManager, "parent");
	}

	public ModelAndView deleteRewardsAndPunishment(HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		String[] arrIDs = getIds(request.getParameter("ids"));
		String isManager = request.getParameter("isManager");
		String staffId = request.getParameter("staffId");
		V3xOrgMember member = this.orgManager.getMemberById(Long.valueOf(Long.parseLong(staffId)));
		if ((arrIDs != null) && (arrIDs.length > 0)) {
			String[] arrayOfString1;
			int j = (arrayOfString1 = arrIDs).length;
			for (int i = 0; i < j; i++) {
				String strID = arrayOfString1[i];
				Long id = Long.valueOf(Long.parseLong(strID));
				this.staffInfoManager.deleteRewardsAndPunishment(id);
				this.operationlogManager.insertOplog(member.getOrgAccountId(), Constants.MODULE_STAFF,
						ApplicationCategoryEnum.hr, "hr.staffInfo.other.delete.label",
						"hr.staffInfo.RewardsAndPunishment.delete.label", new Object[] { member.getName() });
			}
		}
		User user = AppContext.getCurrentUser();

		user.setRemoteAddr(request.getRemoteAddr());
		this.appLogManager.insertLog(user, AppLogAction.Hr_UpdateStaffInfo,
				new String[] { user.getName(), member.getName() });

		return super.redirectModelAndView(
				"/hrStaff.do?method=initHome&infoType=8&staffId=" + staffId + "&isManager=" + isManager, "parent");
	}

	public ModelAndView userDefinedHome(HttpServletRequest request, HttpServletResponse response) throws Exception {
		return new ModelAndView("hr/staffInfo/userDefinedHome");
	}

	/**
	 * 修改user打开列表
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView initUserDefinedPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView("hr/staffInfo/userDefinedPage");
		Long page_id = Long.valueOf(Long.parseLong(request.getParameter("page_id")));
		Page page = this.userDefinedManager.getPageById(page_id);
		if (page != null) {
			mav.addObject("page", page);
			mav.addObject("repair", Integer.valueOf(page.getRepair()));
		}
		List<PageLabel> pageLabels = this.userDefinedManager.getPageLabelByPageId(page_id);
		String pageLabelName_zh = "";
		String pageLabelName_en = "";
		for (PageLabel label : pageLabels) {
			if (label.getLanguage().equals("zh_CN")) {
				pageLabelName_zh = label.getPageLabelValue();
			} else {
				pageLabelName_en = label.getPageLabelValue();
			}
		}
		String staffid = request.getParameter("staffId");
		Long staffId = null;
		if (Strings.isNotBlank(staffid)) {
			staffId = Long.valueOf(NumberUtils.toLong(staffid));
		} else {
			staffId = CurrentUser.get().getId();
		}
		V3xOrgMember staff = this.orgManager.getMemberById(staffId);

		List<WebProperty> webProperties = new ArrayList();
		List<WebLabel> webLabels_zh = new ArrayList();
		List<WebLabel> webLabels_en = new ArrayList();
		List<Integer> propertyTypes = new ArrayList();
		List<Map<Long, Repository>> propertyValues = new ArrayList();
		List<PageProperty> properties = this.userDefinedManager.getPropertyByPageId(page_id);// 所有字段
		String ids = request.getParameter("ids");
		List<Long> reposityIds = CommonTools.parseStr2Ids(ids);
		int i = 0;
		if (CollectionUtils.isNotEmpty(properties)) { // 加载编辑表单
			for (PageProperty property : properties) {
				Long propertyId = property.getId();
				int propertyType = property.getType().intValue();
				propertyTypes.add(Integer.valueOf(propertyType));

				WebProperty webProperty = new WebProperty();
				webProperty.setPage_id(page_id);
				webProperty.setProperty_id(propertyId);
				webProperty.setPropertyType(propertyType);
				webProperty.setPageName_zh(pageLabelName_zh);
				webProperty.setPageName_en(pageLabelName_en);
				if (property.getNot_null() == 1) {
					webProperty.setNot_null("no");
				} else {
					webProperty.setNot_null("yes");
				}
				List<PropertyLabel> labels = this.userDefinedManager.getPropertyLabelByPropertyId(propertyId);
				for (PropertyLabel label : labels) {
					WebLabel webLabel = new WebLabel();
					String value = label.getPropertyLabelValue();
					if (label.getLanguage().equals("zh_CN")) {
						webProperty.setLabelName_zh(value == null ? "" : value);
						webLabel.setLabelName_zh(value == null ? "" : value);
						webLabels_zh.add(webLabel);
					} else {
						webProperty.setLabelName_en(value == null ? "" : value);
						webLabel.setLavelName_en(value == null ? "" : value);
						webLabels_en.add(webLabel);
					}
				}
				if (CollectionUtils.isNotEmpty(reposityIds)) {// 如果ids 是空不加载此处
					Long reposityId = Long.valueOf(0L);
					if (reposityIds.size() > i) {
						reposityId = (Long) reposityIds.get(i);

						Repository repository = this.userDefinedManager.getRepositoryById(reposityId);
						webProperty.setRepository_id(reposityId);
						if (property.getType().longValue() == 1L) {
							webProperty.setF1(repository.getF1());
						} else if (property.getType().longValue() == 2L) {
							if (repository.getF2() == null) {
								webProperty.setF2(null);
							} else {
								webProperty.setF2(new BigDecimal(repository.getF2().doubleValue()));
							}
						} else if (property.getType().longValue() == 3L) {
							if (repository.getF3() == null) {
								webProperty.setF3(null);
							} else {
								webProperty.setF3(repository.getF3());
							}
						} else if (property.getType().longValue() == 4L) {
							webProperty.setF4(repository.getF4());
						} else {
							webProperty.setF5(repository.getF5());
						}
					}
					i++;
				}
				if (webProperty.getRepository_id() == null) {
					webProperty.setRepository_id(propertyId);
				}
				webProperties.add(webProperty);// 创建当前的编辑页

				Map<Long, Repository> repositories = this.userDefinedManager
						.getRepositoryByMemberIdAndPropertyIdAndPageId(staffId, propertyId, page_id);
				propertyValues.add(repositories);// 获取所有数据
			}
			// 增加附加显示
			if (page != null) {
				// 用这表的第一条数据作为存储的id 只要第一条不换附件就在
				try {
					mav.addObject("attachments",
							this.attachmentManager.getByReference(reposityIds.get(0), reposityIds.get(0)));
				} catch (Exception e) {
					//空的时候报错.不做任何处理
				}
			}
		}
		mav.addObject("staffId", staffId);
		mav.addObject("staff", staff);
		mav.addObject("page_id", page_id);
		mav.addObject("pageLabelName_zh", pageLabelName_zh);
		mav.addObject("pageLabelName_en", pageLabelName_en);
		mav.addObject("webProperties", webProperties);
		mav.addObject("webLabels_zh", webLabels_zh);
		mav.addObject("webLabels_en", webLabels_en);
		if (CollectionUtils.isNotEmpty(webLabels_zh)) {
			mav.addObject("label", "label");
		}
		mav.addObject("propertyTypes", propertyTypes);
		mav.addObject("propertyValues", propertyValues);
		mav.addObject("ids", ids);

		mav.addObject("operation", request.getParameter("operation"));
		mav.addObject("show", Boolean.valueOf("Show".equals(request.getParameter("isShow"))));
		mav.addObject("save", Boolean.valueOf("Save".equals(request.getParameter("isSave"))));
		Boolean res = Boolean.valueOf(false);
		if (Boolean.parseBoolean(request.getParameter("dis"))) {
			res = Boolean.valueOf(true);
		}
		mav.addObject("dis", res);
		return mav;
	}

	public ModelAndView ajaxLogname(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String logName = request.getParameter("logName");
		V3xOrgMember member = null;
		try {
			member = this.orgManager.getMemberByLoginName(logName);
		} catch (Exception e) {
			LOG.error("", e);
		}
		String name = "";
		boolean flag = false;
		if (member != null) {
			name = member.getName();
			flag = true;
		}
		getJsonView();
		JSONArray jsonArray = new JSONArray();
		JSONObject jsonObject = new JSONObject();
		if (flag) {
			jsonObject.put("res", "true");
			jsonObject.put("namme", name);
		} else {
			jsonObject.put("res", "false");
		}
		jsonArray.put(jsonObject);
		response.setCharacterEncoding("utf-8");
		PrintWriter out = response.getWriter();
		out.write(jsonArray.toString());
		return null;
	}

	public ModelAndView addUserDefined(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String operation = request.getParameter("operation");
		Long member_id = Long.valueOf(Long.parseLong(request.getParameter("staffId")));
		Long page_id = Long.valueOf(Long.parseLong(request.getParameter("page_id")));
		List<Repository> repositorys = new ArrayList();
		Repository repository;
		Long firstid = 0L;
		int i = 0;
		if ("Save".equals(operation)) {
			List<PageProperty> pageProperties = this.userDefinedManager.getPropertyByPageId(page_id);
			Long operationId = Long.valueOf(UUIDLong.longUUID());
			for (PageProperty property : pageProperties) {
				repository = new Repository();
				repository.setIdIfNew();
				if (i == 0) {
					firstid = repository.getId();
					i = 999;
				}
				repository.setMemberId(member_id);
				repository.setPage_id(page_id);
				repository.setProperty_id(property.getId());
				repository.setOperation_id(operationId);
				Date createTime = new Date();
				repository.setCreateTime(createTime);

				int propertyType = property.getType().intValue();
				String propertyValue = request.getParameter(String.valueOf(property.getId()));
				SalaryUserDefinedHelper.addRepositorys(propertyType, propertyValue, repository);

				repositorys.add(repository);

			}
			try {
				// 新建附件
				this.attachmentManager.create(ApplicationCategoryEnum.hr, firstid, firstid, request);
			} catch (Exception e) {
				System.err.println(e.getMessage());
			}
			this.userDefinedManager.addAllRepository(repositorys);
		} else if ("Update".equals(operation)) {
			List<Long> repositoryIds = CommonTools.parseStr2Ids(request, "ids");
			String propertyIds = "";
			Long operationId = null;
			for (Long repositoryId : repositoryIds) {
				repository = this.userDefinedManager.getRepositoryById(repositoryId);
				propertyIds = propertyIds + repository.getProperty_id() + ",";
				operationId = repository.getOperation_id();
				int propertyType = ServletRequestUtils.getIntParameter(request, repositoryId + "_Type").intValue();
				String propertyValue = request.getParameter(String.valueOf(repository.getId()));
				SalaryUserDefinedHelper.updateRepositorys(propertyType, propertyValue, repository);

				repositorys.add(repository);

			}
			List<PageProperty> pageProperties = this.userDefinedManager.getPropertyByPageId(page_id);
			for (PageProperty property : pageProperties) {
				if (!propertyIds.contains(property.getId().toString())) {
					repository = new Repository();
					repository.setIdIfNew();
					repository.setMemberId(member_id);
					repository.setPage_id(page_id);
					repository.setProperty_id(property.getId());
					repository.setOperation_id(operationId);
					Date createTime = new Date();
					repository.setCreateTime(createTime);

					int propertyType = property.getType().intValue();
					String propertyValue = request.getParameter(String.valueOf(property.getId()));
					SalaryUserDefinedHelper.addRepositorys(propertyType, propertyValue, repository);

					repositorys.add(repository);
				}
			}
			// 更新附件用第一个标签的id
			this.attachmentManager.deleteByReference(repositoryIds.get(0), repositoryIds.get(0));
			String str = this.attachmentManager.create(ApplicationCategoryEnum.hr, repositoryIds.get(0),
					repositoryIds.get(0), request);
			this.userDefinedManager.updateAllRepository(repositorys);
		}
		return super.redirectModelAndView(
				"/hrStaff.do?method=userDefinedHome&staffId=" + member_id + "&page_id=" + page_id, "parent");
	}

	public ModelAndView detoryUserDefined(HttpServletRequest request, HttpServletResponse response) throws Exception {
		List<Long> repositoryIds = CommonTools.parseStr2Ids(request, "ids");
		this.userDefinedManager.deleteRepositoryByIds(repositoryIds);
		return super.redirectModelAndView("/hrStaff.do?method=userDefinedHome&staffId="
				+ request.getParameter("staffId") + "&page_id=" + request.getParameter("page_id"), "parent");
	}

	private void alertError(PrintWriter out, HttpServletResponse response, Exception e) throws IOException {
		response.setContentType("text/html;charset=UTF-8");
		out = response.getWriter();
		out.println("<script type='text/javascript'>");
		out.println("alert(\"" + StringEscapeUtils.escapeJavaScript(e.getMessage()) + "\")");
		out.println("</script>");
	}

	public AttachmentManager getAttachmentManager() {
		return this.attachmentManager;
	}

	public void setAttachmentManager(AttachmentManager attachmentManager) {
		this.attachmentManager = attachmentManager;
	}

	public OperationlogManager getOperationlogManager() {
		return this.operationlogManager;
	}

	public AppLogManager getAppLogManager() {
		return this.appLogManager;
	}

	public void setAppLogManager(AppLogManager appLogManager) {
		this.appLogManager = appLogManager;
	}

	public void setAgentIntercalateManager(AgentIntercalateManager agentIntercalateManager) {
		this.agentIntercalateManager = agentIntercalateManager;
	}
}
