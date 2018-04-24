package com.seeyon.v3x.online.controller;

import java.io.PrintWriter;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.servlet.ModelAndView;

import com.seeyon.apps.collaboration.manager.ColManager;
import com.seeyon.apps.collaboration.po.ColSummary;
import com.seeyon.apps.videoconference.manager.VideoConferenceManager;
import com.seeyon.apps.videoconference.util.ParseXML;
import com.seeyon.apps.videoconference.util.VideoConfUtil;
import com.seeyon.apps.videoconference.util.VideoConferenceConfig;
import com.seeyon.cap.videoconference.manager.CreateVideoConferenceManagerCAP;
import com.seeyon.cap.videoconference.manager.DeleteVideoConferenceManagerCAP;
import com.seeyon.cap.videoconference.manager.JoinVideoConferenceManagerCAP;
import com.seeyon.cap.videoconference.manager.StartVideoConferenceManagerCAP;
import com.seeyon.ctp.common.AppContext;
import com.seeyon.ctp.common.authenticate.domain.User;
import com.seeyon.ctp.common.config.SystemConfig;
import com.seeyon.ctp.common.config.manager.ConfigManager;
import com.seeyon.ctp.common.constants.ApplicationCategoryEnum;
import com.seeyon.ctp.common.constants.Constants.LoginUserState;
import com.seeyon.ctp.common.content.affair.AffairManager;
import com.seeyon.ctp.common.dao.paginate.Pagination;
import com.seeyon.ctp.common.excel.DataRecord;
import com.seeyon.ctp.common.excel.DataRow;
import com.seeyon.ctp.common.excel.FileToExcelManager;
import com.seeyon.ctp.common.exceptions.BusinessException;
import com.seeyon.ctp.common.filemanager.manager.FileManager;
import com.seeyon.ctp.common.i18n.ResourceBundleUtil;
import com.seeyon.ctp.common.parser.HTMLFileParser;
import com.seeyon.ctp.common.po.affair.CtpAffair;
import com.seeyon.ctp.common.po.config.ConfigItem;
import com.seeyon.ctp.common.po.usermessage.UserHistoryMessage;
import com.seeyon.ctp.common.usermessage.Constants.MessageFilterOption;
import com.seeyon.ctp.common.usermessage.UserMessageFilterConfigManager;
import com.seeyon.ctp.common.usermessage.UserMessageManager;
import com.seeyon.ctp.common.usermessage.extended.ExtendedMessageSystem;
import com.seeyon.ctp.common.usermessage.extended.ExtendedMessageSystemManager;
import com.seeyon.ctp.common.usermessage.pipeline.MessagePipeline;
import com.seeyon.ctp.common.usermessage.pipeline.MessagePipelineManager;
import com.seeyon.ctp.login.online.OnlineManager;
import com.seeyon.ctp.login.online.OnlineUser;
import com.seeyon.ctp.organization.OrgConstants;
import com.seeyon.ctp.organization.bo.V3xOrgDepartment;
import com.seeyon.ctp.organization.bo.V3xOrgMember;
import com.seeyon.ctp.organization.bo.V3xOrgTeam;
import com.seeyon.ctp.organization.manager.OrgManager;
import com.seeyon.ctp.organization.manager.OrgManagerDirect;
import com.seeyon.ctp.portal.customize.manager.CustomizeManager;
import com.seeyon.ctp.portal.po.PortalSpaceFix;
import com.seeyon.ctp.portal.space.manager.SpaceManager;
import com.seeyon.ctp.portal.util.Constants;
import com.seeyon.ctp.util.UUIDLong;
import com.seeyon.ctp.util.annotation.CheckRoleAccess;
import com.seeyon.v3x.addressbook.manager.AddressBookManager;
import com.seeyon.v3x.bbs.domain.BbsConstants;
import com.seeyon.v3x.bbs.domain.V3xBbsBoard;
import com.seeyon.v3x.bbs.manager.BbsBoardManager;
import com.seeyon.v3x.bulletin.domain.BulType;
import com.seeyon.v3x.bulletin.manager.BulDataManager;
import com.seeyon.v3x.common.taglibs.functions.Functions;
import com.seeyon.v3x.common.web.BaseController;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.inquiry.domain.InquirySurveytype;
import com.seeyon.v3x.inquiry.manager.InquiryManager;
import com.seeyon.v3x.inquiry.webmdoel.SurveyTypeCompose;
import com.seeyon.v3x.mail.manager.MessageMailManager;
import com.seeyon.v3x.mobile.adapter.MobileConstants;
import com.seeyon.v3x.mobile.message.manager.MobileMessageManager;
import com.seeyon.v3x.news.domain.NewsType;
import com.seeyon.v3x.news.manager.NewsDataManager;
import com.seeyon.v3x.online.Constant;
import com.seeyon.v3x.online.manager.WIMManager;
import com.seeyon.v3x.peoplerelate.manager.PeopleRelateManager;
import com.seeyon.v3x.project.manager.ProjectManager;
import com.seeyon.v3x.util.CommonTools;
import com.seeyon.v3x.util.Datetimes;
import com.seeyon.v3x.util.Strings;
import com.seeyon.v3x.util.UniqueList;

public class MessageController extends BaseController {
	private static final Log log = LogFactory.getLog(MessageController.class);
	private UserMessageManager userMessageManager;
	private OnlineManager onlineManager;
	private OrgManager orgManager;
	private FileManager fileManager;
	private MobileMessageManager mobileMessageManager;
	private UserMessageFilterConfigManager userMessageFilterConfigManager;
	private MessageMailManager messageMailManager;
	private InquiryManager inquiryManager;
	private BbsBoardManager bbsBoardManager;
	private BulDataManager bulDataManager;
	private NewsDataManager newsDataManager;
	private SpaceManager spaceManager;
	private MessagePipelineManager messagePipelineManager;
	private ExtendedMessageSystemManager extendedMessageSystemManager;
	private PeopleRelateManager peoplerelateManager;
	private OrgManagerDirect orgManagerDirect;
	private AddressBookManager addressBookManager;
	private SystemConfig systemConfig;
	private ProjectManager projectManager;
	private WIMManager wimManager;
	private AffairManager affairManager;
	private ColManager colManager;
	private VideoConferenceManager videoConferenceManager;
	private FileToExcelManager fileToExcelManager;
	private ConfigManager configManager;
	private CustomizeManager customizeManager;

	public FileToExcelManager getFileToExcelManager() {
		return this.fileToExcelManager;
	}

	public void setFileToExcelManager(FileToExcelManager fileToExcelManager) {
		this.fileToExcelManager = fileToExcelManager;
	}

	public UserMessageManager getUserMessageManager() {
		return this.userMessageManager;
	}

	public OnlineManager getOnlineManager() {
		return this.onlineManager;
	}

	public OrgManager getOrgManager() {
		return this.orgManager;
	}

	public FileManager getFileManager() {
		return this.fileManager;
	}

	public MobileMessageManager getMobileMessageManager() {
		return this.mobileMessageManager;
	}

	public UserMessageFilterConfigManager getUserMessageFilterConfigManager() {
		return this.userMessageFilterConfigManager;
	}

	public MessageMailManager getMessageMailManager() {
		return this.messageMailManager;
	}

	public InquiryManager getInquiryManager() {
		return this.inquiryManager;
	}

	public BbsBoardManager getBbsBoardManager() {
		return this.bbsBoardManager;
	}

	public BulDataManager getBulDataManager() {
		return this.bulDataManager;
	}

	public NewsDataManager getNewsDataManager() {
		return this.newsDataManager;
	}

	public SpaceManager getSpaceManager() {
		return this.spaceManager;
	}

	public MessagePipelineManager getMessagePipelineManager() {
		return this.messagePipelineManager;
	}

	public ExtendedMessageSystemManager getExtendedMessageSystemManager() {
		return this.extendedMessageSystemManager;
	}

	public PeopleRelateManager getPeoplerelateManager() {
		return this.peoplerelateManager;
	}

	public OrgManagerDirect getOrgManagerDirect() {
		return this.orgManagerDirect;
	}

	public AddressBookManager getAddressBookManager() {
		return this.addressBookManager;
	}

	public SystemConfig getSystemConfig() {
		return this.systemConfig;
	}

	public ProjectManager getProjectManager() {
		return this.projectManager;
	}

	public WIMManager getWimManager() {
		return this.wimManager;
	}

	public AffairManager getAffairManager() {
		return this.affairManager;
	}

	public ColManager getColManager() {
		return this.colManager;
	}

	public VideoConferenceManager getVideoConferenceManager() {
		return this.videoConferenceManager;
	}

	public void setWimManager(WIMManager wimManager) {
		this.wimManager = wimManager;
	}

	public void setAffairManager(AffairManager affairManager) {
		this.affairManager = affairManager;
	}

	public void setColManager(ColManager colManager) {
		this.colManager = colManager;
	}

	public void setVideoConferenceManager(VideoConferenceManager videoConferenceManager) {
		this.videoConferenceManager = videoConferenceManager;
	}

	public void setProjectManager(ProjectManager projectManager) {
		this.projectManager = projectManager;
	}

	public void setSystemConfig(SystemConfig systemConfig) {
		this.systemConfig = systemConfig;
	}

	public void setUserMessageManager(UserMessageManager userMessageManager) {
		this.userMessageManager = userMessageManager;
	}

	public void setOnlineManager(OnlineManager onlineManager) {
		this.onlineManager = onlineManager;
	}

	public void setOrgManager(OrgManager orgManager) {
		this.orgManager = orgManager;
	}

	public void setFileManager(FileManager fileManager) {
		this.fileManager = fileManager;
	}

	public void setMobileMessageManager(MobileMessageManager mobileMessageManager) {
		this.mobileMessageManager = mobileMessageManager;
	}

	public void setUserMessageFilterConfigManager(UserMessageFilterConfigManager userMessageFilterConfigManager) {
		this.userMessageFilterConfigManager = userMessageFilterConfigManager;
	}

	public void setMessageMailManager(MessageMailManager messageMailManager) {
		this.messageMailManager = messageMailManager;
	}

	public void setBbsBoardManager(BbsBoardManager bbsBoardManager) {
		this.bbsBoardManager = bbsBoardManager;
	}

	public void setNewsDataManager(NewsDataManager newsDataManager) {
		this.newsDataManager = newsDataManager;
	}

	public void setBulDataManager(BulDataManager bulDataManager) {
		this.bulDataManager = bulDataManager;
	}

	public void setInquiryManager(InquiryManager inquiryManager) {
		this.inquiryManager = inquiryManager;
	}

	public void setSpaceManager(SpaceManager spaceManager) {
		this.spaceManager = spaceManager;
	}

	public void setMessagePipelineManager(MessagePipelineManager messagePipelineManager) {
		this.messagePipelineManager = messagePipelineManager;
	}

	public void setExtendedMessageSystemManager(ExtendedMessageSystemManager extendedMessageSystemManager) {
		this.extendedMessageSystemManager = extendedMessageSystemManager;
	}

	public void setPeoplerelateManager(PeopleRelateManager peoplerelateManager) {
		this.peoplerelateManager = peoplerelateManager;
	}

	public void setOrgManagerDirect(OrgManagerDirect orgManagerDirect) {
		this.orgManagerDirect = orgManagerDirect;
	}

	public void setAddressBookManager(AddressBookManager addressBookManager) {
		this.addressBookManager = addressBookManager;
	}

	public void setConfigManager(ConfigManager configManager) {
		this.configManager = configManager;
	}

	public void setCustomizeManager(CustomizeManager customizeManager) {
		this.customizeManager = customizeManager;
	}

	public ModelAndView index(HttpServletRequest request, HttpServletResponse response) throws Exception {
		return null;
	}

	public ModelAndView A8geniusMsgWindow(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView("personalAffair/message/A8geniusMsgWindow");
		User user = AppContext.getCurrentUser();

		boolean isEnableMsgSound = false;
		if ("enable".equals(this.systemConfig.get("SMS_hint"))) {
			isEnableMsgSound = "true"
					.equals(this.customizeManager.getCustomizeValue(user.getId().longValue(), "messageSoundEnabled"));
		}
		mav.addObject("isEnableMsgSound", Boolean.valueOf(isEnableMsgSound));

		mav.addObject("msgClosedEnable", Boolean.valueOf(!"false"
				.equals(this.customizeManager.getCustomizeValue(user.getId().longValue(), "messageViewRemoved"))));

		return mav;
	}

	public ModelAndView createTeam(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView("personalAffair/message/createTeam");
		String createType = request.getParameter("createType");
		Long id = Long.valueOf(NumberUtils.toLong(request.getParameter("otherId")));
		List<V3xOrgMember> memberList = new ArrayList();

		int memberSize = 1;
		if ("2".equals(createType)) {
			V3xOrgMember member = this.orgManager.getMemberById(id);
			memberList.add(member);
			memberSize = 2;
		} else if ("3".equals(createType)) {
			V3xOrgTeam team = this.orgManager.getTeamById(id);
			List<Long> idList = team.getMemberList(OrgConstants.ORGENT_TYPE.Member.ordinal());
			for (Long memberId : idList) {
				V3xOrgMember member = this.orgManager.getMemberById(memberId);
				memberList.add(member);
			}
			mav.addObject("teamName", team.getName());
			mav.addObject("updateTeam", "true");
			memberSize = memberList.size();
		}
		mav.addObject("memberList", memberList);
		mav.addObject("memberSize", Integer.valueOf(100 - memberSize));
		return mav;
	}

	public boolean isExist(String teamName) {
		User user = CurrentUser.get();
		return this.addressBookManager.isExist(4, teamName, user.getId(), user.getLoginAccount(), null);
	}

	public boolean isOnline(Long id) {
		OnlineUser onlineUser = Functions.getOnlineUser(id);
		if ((onlineUser != null) && (onlineUser.getState() != null)
				&& (onlineUser.getState() == LoginUserState.online)) {
			return true;
		}
		return false;
	}

	public String getTeamName(String type, Long id) {
		String teamName = "";
		if (id != null) {
			if ("2".equals(type)) {
				teamName = Functions.getDepartment(id).getName();
			} else if (("3".equals(type)) || ("4".equals(type)) || ("5".equals(type))) {
				teamName = Functions.getTeamName(id);
			}
		}
		return teamName;
	}

	public ModelAndView saveTeam(HttpServletRequest request, HttpServletResponse response) throws Exception {
		User user = CurrentUser.get();
		String createType = request.getParameter("createType");
		String teamMemIDs = request.getParameter("teamMemIDs");
		List<Long> members = CommonTools.parseStr2Ids(teamMemIDs);
		Long id = Long.valueOf(NumberUtils.toLong(request.getParameter("otherId")));

		V3xOrgTeam team = null;
		if (("1".equals(createType)) || ("2".equals(createType))) {
			team = new V3xOrgTeam();
			bind(request, team);
			team.setId(Long.valueOf(NumberUtils.toLong(request.getParameter("dID"))));
			team.setOrgAccountId(user.getLoginAccount());
			team.setOwnerId(user.getId());
			team.setType(OrgConstants.TEAM_TYPE.DISCUSS.ordinal());

			team.setDepId(user.getDepartmentId());
			members.add(user.getId());
			if ("2".equals(createType)) {
				members.add(id);
			}
			team.addTeamMember(members, OrgConstants.ORGENT_TYPE.Member.ordinal());
			this.orgManagerDirect.addTeam(team);
		} else {
			team = this.orgManager.getTeamById(id);
			team.addTeamMember(members, OrgConstants.ORGENT_TYPE.Member.ordinal());
			this.orgManagerDirect.updateTeam(team);
		}
		return null;
	}

	public boolean isOwner(Long teamId) throws Exception {
		User user = CurrentUser.get();
		if (teamId != null) {
			V3xOrgTeam team = this.orgManager.getTeamById(teamId);
			return team.getOwnerId().equals(user.getId());
		}
		return false;
	}

	public boolean isDeleted(Long teamId) throws Exception {
		if (teamId != null) {
			V3xOrgTeam team = this.orgManager.getTeamById(teamId);
			if ((team == null) || (team.getIsDeleted().booleanValue()) || (!team.isValid())) {
				return true;
			}
		}
		return false;
	}

	public ModelAndView deleteTeam(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Long teamId = Long.valueOf(NumberUtils.toLong(request.getParameter("teamId")));
		V3xOrgTeam team = this.orgManager.getTeamById(teamId);
		this.orgManagerDirect.deleteTeam(team);
		return null;
	}

	public ModelAndView showMessage(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String view = "personalAffair/message/sendIMMessage";
		ModelAndView mav = new ModelAndView(view);
		String type = request.getParameter("type");
		Long id = Long.valueOf(NumberUtils.toLong(request.getParameter("id")));
		List<V3xOrgMember> members = new ArrayList();
		List<OnlineUser> onlineMembers = new ArrayList();
		List<V3xOrgMember> offlineMembers = new ArrayList();
		boolean haveOnline = false;
		List<Long> memberIds;
		if ("1".equals(type)) {
			V3xOrgMember member = this.orgManager.getMemberById(id);
			haveOnline = this.onlineManager.isOnline(member.getLoginName());
		} else if ("2".equals(type)) {
			V3xOrgDepartment dept = this.orgManager.getDepartmentById(id);
			if (dept != null) {
				members = this.orgManagerDirect.getMembersByDepartment(dept.getId(), dept.getOrgAccountId(),
						Boolean.valueOf(true), Boolean.valueOf(false), Boolean.valueOf(false));
			}
		} else if (("3".equals(type)) || ("4".equals(type)) || ("5".equals(type))) {
			V3xOrgTeam team = this.orgManager.getTeamById(id);
			if (team != null) {
				memberIds = new UniqueList();
				memberIds.addAll(team.getAllMembers());
				memberIds.addAll(team.getAllRelatives());
				members = new ArrayList();
				for (Long memberId : memberIds) {
					V3xOrgMember member = this.orgManager.getMemberById(memberId);
					if ((member != null) && (member.isValid())) {
						members.add(member);
					}
				}
				if ((this.wimManager.isColRelativeTeam(team.getId().longValue()))
						&& ((members.size() == 2) || (members.size() == 1))) {
					mav.addObject("colteam", Boolean.valueOf(true));
				} else {
					mav.addObject("colteam", Boolean.valueOf(false));
				}
			}
		}
		for (V3xOrgMember m : members) {
			OnlineUser onlineUser = Functions.getOnlineUser(m.getId());
			if ((onlineUser != null) && (onlineUser.getState() != null)
					&& (onlineUser.getState() == LoginUserState.online)) {
				onlineMembers.add(onlineUser);
			} else {
				offlineMembers.add(m);
			}
		}
		if ((!onlineMembers.isEmpty()) && (onlineMembers.size() > 1)) {
			haveOnline = true;
		}
		haveOnline = false;
		mav.addObject("type", type);
		mav.addObject("haveOnline", Boolean.valueOf(haveOnline));
		mav.addObject("onlineMembers", onlineMembers);
		mav.addObject("offlineMembers", offlineMembers);
		return mav;
	}

	public Map<String, String> createCollDisscuss(Long summaryId) throws Exception {
		User user = CurrentUser.get();
		Map<String, String> result = new HashMap();
		if (summaryId == null) {
			result.put("cando", "false");
			result.put("success", "false");
			return result;
		}
		ColSummary colSummary = this.colManager.getColSummaryById(summaryId);

		List<CtpAffair> affairs = new ArrayList();
		List<Long> members = new UniqueList();
		for (CtpAffair affair : affairs) {
			members.add(affair.getMemberId());
		}
		if (members.size() > 100) {
			result.put("cando", "false");
			result.put("success", "true");
			return result;
		}
		V3xOrgTeam orgTeam = this.wimManager.getRelativeTeamByColId(summaryId.longValue());
		Object member = new ArrayList();
		if (orgTeam != null) {
			List<V3xOrgMember> list = this.orgManager.getTeamMember(orgTeam.getId());
			member = CommonTools.getEntityIds(list);
		}
		if (orgTeam == null) {
			orgTeam = new V3xOrgTeam();
			orgTeam.setId(Long.valueOf(UUIDLong.longUUID()));
			orgTeam.setOrgAccountId(user.getLoginAccount());
			orgTeam.setType(OrgConstants.TEAM_TYPE.COLTEAM.ordinal());

			orgTeam.setName(colSummary.getSubject());
			orgTeam.addTeamMember(members, OrgConstants.ORGENT_TYPE.Member.ordinal());
			orgTeam.setOwnerId(colSummary.getId());
			this.orgManagerDirect.addTeam(orgTeam);
		} else {
			members.removeAll((Collection) member);
			orgTeam.addTeamMember(members, OrgConstants.ORGENT_TYPE.Member.ordinal());
			this.orgManagerDirect.updateTeam(orgTeam);
		}
		result.put("cando", "true");
		result.put("teamId", String.valueOf(orgTeam.getId()));
		result.put("teamName", orgTeam.getName());
		return result;
	}

	public ModelAndView createMeeting(HttpServletRequest request, HttpServletResponse response) throws Exception {
		User user = CurrentUser.get();
		PrintWriter out = response.getWriter();

		String result = "";

		Map<String, Object> videoParamMap = new HashMap();
		String type = request.getParameter("messageType");

		int num = 0;
		String referenceId = request.getParameter("receiverIds");

		List<V3xOrgMember> orgMembers = new ArrayList();
		String meetingName;
		if ("1".equals(type)) {
			V3xOrgMember member = this.orgManager.getMemberById(Long.valueOf(Long.parseLong(referenceId)));
			orgMembers.add(member);
			orgMembers.add(this.orgManager.getMemberById(user.getId()));
			meetingName = user.getName() + "_" + member.getName() + " 讨论会议";
		} else if ("2".equals(type)) {
			V3xOrgDepartment dept = this.orgManager.getDepartmentById(Long.valueOf(Long.parseLong(referenceId)));
			meetingName = dept.getName() + " 讨论会议";

			orgMembers = this.orgManagerDirect.getMembersByDepartment(dept.getId(), dept.getOrgAccountId(),
					Boolean.valueOf(true), Boolean.valueOf(false), Boolean.valueOf(false));
		} else {
			V3xOrgTeam orgTeam = this.orgManager.getTeamById(Long.valueOf(Long.parseLong(referenceId)));
			meetingName = orgTeam.getName() + " 讨论会议";
			List<Long> members = orgTeam.getAllMembers();
			members.addAll(orgTeam.getAllRelatives());
			for (Long long1 : members) {
				V3xOrgMember member = this.orgManager.getMemberById(long1);
				orgMembers.add(member);
			}
		}
		num = orgMembers.size();
		for (int i = 0; i < num; i++) {
			V3xOrgMember member = (V3xOrgMember) orgMembers.get(i);
			if ((member.getId() != user.getId()) && (this.onlineManager.isOnline(member.getLoginName()))) {
				break;
			}
			if (i == num - 1) {
				result = "({\"success\":\"false\",\"confKey\":\"MainLang.message_vomeeting_createerror_offline\"})";
				out.println(result);
				out.flush();
				out.close();
				return null;
			}
		}
		videoParamMap.put("subject", meetingName);
		videoParamMap.put("startTime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
				.format(VideoConfUtil.toGMTDate(new Date(), "-479")).replace(" ", "T"));
		videoParamMap.put("endTime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
				.format(VideoConfUtil.toGMTDate(new Date(new Date().getTime() + 3600000L), "-480")).replace(" ", "T"));
		videoParamMap.put("attendeeAmount", String.valueOf(num));
		videoParamMap.put("hostName", user.getLoginName());
		videoParamMap.put("agenda", "");
		videoParamMap.put("webBaseUrl", VideoConferenceConfig.WEBBASEURL);
		videoParamMap.put("creator", user.getLoginName());
		videoParamMap.put("userName", user.getLoginName());
		videoParamMap.put("password", this.videoConferenceManager.getEncryptedPassWD(user.getId()));

		String meetingInf = ((CreateVideoConferenceManagerCAP) AppContext
				.getBean("createInfoWareInstantMeetingManagerImplCAP")).createVideoConferenceCap(videoParamMap);

		log.info(meetingInf);
		String confKey = "";
		try {
			meetingInf.replace("\"", "");
			confKey = meetingInf.substring(meetingInf.indexOf("<confKey>") + 9, meetingInf.lastIndexOf("</confKey>"));
			if (!Strings.isBlank(confKey)) {
				result = "({\"success\":\"true\",\"confKey\":\"" + confKey + "\",\"tnum\":\"" + (num - 1) + "\"})";
				out.println(result);
				out.flush();
				out.close();
				return null;
			}
			throw new RuntimeException();
		} catch (Exception localException) {
			result = "({\"success\":\"false\",\"confKey\":\"MainLang.message_vomeeting_createerror\"})";
			out.println(result);
			out.flush();
			out.close();
		}
		return null;
	}

	public ModelAndView joinmeeting(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String confKey = request.getParameter("confKey");
		String hostname = request.getParameter("meeting_id");
		String iscreater = request.getParameter("iscreater");

		Map<String, String> videoParamMap = new HashMap();
		try {
			videoParamMap.put("displayName", CurrentUser.get().getName());
			videoParamMap.put("attendeeName", CurrentUser.get().getName());
			videoParamMap.put("confKey", confKey);
			videoParamMap.put("webBaseUrl", VideoConferenceConfig.WEBBASEURL);
			String email = this.orgManager.getMemberById(CurrentUser.get().getId()).getEmailAddress();
			videoParamMap.put("userName", CurrentUser.get().getLoginName());
			if ((email == null) || ("".equals(email))) {
				videoParamMap.put("email", CurrentUser.get().getLoginName() + "@seeyon.com");
			} else {
				videoParamMap.put("email", email);
			}
			videoParamMap.put("password", this.videoConferenceManager.getEncryptedPassWD(CurrentUser.get().getId()));
		} catch (SQLException e1) {
			this.logger.error(e1);
		} catch (BusinessException e) {
			this.logger.error(e);
		}
		String result;
		if ((Strings.isNotBlank(iscreater)) && ("true".equals(iscreater))) {
			videoParamMap.put("hostName",
					this.orgManager.getMemberById(Long.valueOf(Long.parseLong(hostname))).getLoginName());
			result = ((StartVideoConferenceManagerCAP) AppContext.getBean("startVideoConferenceManagerCAP"))
					.startVideoConferenceCap(videoParamMap);
		} else {
			result = ((JoinVideoConferenceManagerCAP) AppContext.getBean("joinVideoConferenceManagerCAP"))
					.joinVideoConferenceCap(videoParamMap);
		}
		ModelAndView mav = new ModelAndView("personalAffair/message/joinMeeting");

		this.logger.info("参加红杉树视频会议：" + result);

		Map paramMap = ParseXML.parseXML(result);
		try {
			String token = StringUtils.trim((String) paramMap.get("token"));
			String ciURL = StringUtils.trim((String) paramMap.get("ciURL"));

			mav.addObject("ciURL", ciURL);
			mav.addObject("token", token);

			mav.addObject("success", Boolean.valueOf(true));
		} catch (Exception localException) {
			mav.addObject("success", Boolean.valueOf(false));
		}
		return mav;
	}

	public ModelAndView deletemeeting(HttpServletRequest request, HttpServletResponse response) throws Exception {
		User user = CurrentUser.get();
		Map<String, String> videoParamMap = new HashMap();
		videoParamMap.put("confKey", request.getParameter("confKey"));
		videoParamMap.put("webBaseUrl", VideoConferenceConfig.WEBBASEURL);
		videoParamMap.put("userName", user.getLoginName());
		videoParamMap.put("password", this.videoConferenceManager.getEncryptedPassWD(user.getId()));
		String deleteInf = ((DeleteVideoConferenceManagerCAP) AppContext.getBean("deleteVideoConferenceManagerCAP"))
				.deleteVideoConferenceCap(videoParamMap);
		if ((StringUtils.contains(deleteInf, "0x")) || (!StringUtils.contains(deleteInf, "SUCCESS"))) {
			throw new Exception("删除失败！Error Code(RedFir):" + deleteInf);
		}
		return null;
	}

	public ModelAndView showThisHistoryMessage(HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		ModelAndView mav = new ModelAndView("personalAffair/message/showThisHistoryMessage");
		User user = CurrentUser.get();

		List<UserHistoryMessage> messageList = null;
		int type = NumberUtils.toInt(request.getParameter("type"));
		Long id = Long.valueOf(NumberUtils.toLong(request.getParameter("id")));
		String createDate = request.getParameter("createDate");

		int nowPage = 1;
		int size = 0;
		int pageSize = 20;
		int pages = 1;
		int start = 0;

		size = this.userMessageManager.getThisHistoryMessage(com.seeyon.ctp.common.usermessage.Constants.valueOf(type),
				user.getId(), id, createDate);

		pages = (size + pageSize - 1) / pageSize;
		if (pages == 0) {
			pages = 1;
		}
		String nowPagePara = request.getParameter("nowPagePara");
		if ((Strings.isNotBlank(nowPagePara)) && (!"1".equals(nowPagePara))) {
			nowPage = NumberUtils.toInt(nowPagePara, 1);
			start = (nowPage - 1) * pageSize;
		}
		messageList = this.userMessageManager.getThisHistoryMessage(
				com.seeyon.ctp.common.usermessage.Constants.valueOf(type), user.getId(), id, createDate, start,
				pageSize);

		mav.addObject("size", Integer.valueOf(size));
		mav.addObject("pages", Integer.valueOf(pages));
		mav.addObject("nowPage", Integer.valueOf(nowPage));
		mav.addObject("messageList", messageList);
		return mav;
	}

	public ModelAndView showAllHistoryMessage(HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		ModelAndView mav = null;
		if (StringUtils.isNotBlank(request.getParameter("init"))) {
			mav = new ModelAndView("personalAffair/message/showInitPage");
			return mav;
		}
		User user = CurrentUser.get();
		mav = new ModelAndView("personalAffair/message/showAllHistoryMessage");
		int type = NumberUtils.toInt(request.getParameter("type"));
		Long id = Long.valueOf(NumberUtils.toLong(request.getParameter("id")));
		boolean search = StringUtils.isNotBlank(request.getParameter("search"));
		String area = request.getParameter("area");
		String time = request.getParameter("time");
		String content = request.getParameter("content");

		List<UserHistoryMessage> messageList = this.userMessageManager.getAllHistoryMessage(
				com.seeyon.ctp.common.usermessage.Constants.valueOf(type), user.getId(), id, search, area, time,
				content, true);

		int size = Pagination.getRowCount();
		if ((messageList != null) && (size == 0)) {
			size = messageList.size();
		}
		int pageSize = NumberUtils.toInt(request.getParameter("pageSize"));
		if (pageSize < 1) {
			pageSize = 20;
		}
		int pages = (size + pageSize - 1) / pageSize;
		if (pages < 1) {
			pages = 1;
		}
		int page = NumberUtils.toInt(request.getParameter("page"));
		if (page < 1) {
			page = 1;
		} else if (page > pages) {
			page = pages;
		}
		Map<String, List<UserHistoryMessage>> map = new LinkedHashMap();
		if (CollectionUtils.isNotEmpty(messageList)) {
			for (UserHistoryMessage msg : messageList) {
				String date = Datetimes.formatDate(msg.getCreationDate());
				List<UserHistoryMessage> list = (List) map.get(date);
				if (list == null) {
					list = new ArrayList();
					map.put(date, list);
				}
				list.add(msg);
			}
		}
		mav.addObject("size", Integer.valueOf(size));
		mav.addObject("pageSize", Integer.valueOf(pageSize));
		mav.addObject("pages", Integer.valueOf(pages));
		mav.addObject("page", Integer.valueOf(page));
		mav.addObject("map", map);
		return mav;
	}

	public ModelAndView deleteMessage(HttpServletRequest request, HttpServletResponse response) throws Exception {
		User user = CurrentUser.get();
		String deleteType = request.getParameter("deleteType");
		String deleteIds = request.getParameter("deleteIds");
		List<Long> ids = new ArrayList();
		if (StringUtils.isNotBlank(deleteIds)) {
			if (("4".equals(deleteType)) || ("5".equals(deleteType))) {
				ids.add(Long.valueOf(NumberUtils.toLong(deleteIds)));
			} else if ("1".equals(deleteType)) {
				String[] idStrs = deleteIds.split(",");
				String[] arrayOfString1;
				int j = (arrayOfString1 = idStrs).length;
				for (int i = 0; i < j; i++) {
					String str = arrayOfString1[i];
					if (StringUtils.isNotBlank(str)) {
						ids.add(Long.valueOf(NumberUtils.toLong(str)));
					}
				}
			}
		}
		this.userMessageManager.deleteMessage(user.getId(), deleteType, ids);
		if ("1".equals(deleteType)) {
			return super.redirectModelAndView(
					"message.do?method=showMessages&showType=1&type=1&id=" + request.getParameter("id"));
		}
		return super.redirectModelAndView("message.do?method=showMessages&showType=1");
	}

	public ModelAndView showSendDlg(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView modelAndView = new ModelAndView("personalAffair/message/sendMessage");
		String receiverIdsStr = request.getParameter("receiverIds");

		long userId = CurrentUser.get().getId().longValue();
		List<String> rs = new ArrayList();
		boolean checkLevelScope = false;
		if (StringUtils.isNotBlank(receiverIdsStr)) {
			StringTokenizer tokenizer = new StringTokenizer(receiverIdsStr, ",");
			while (tokenizer.hasMoreElements()) {
				String r = (String) tokenizer.nextElement();
				if (Functions.checkLevelScope(Long.valueOf(userId), Long.valueOf(NumberUtils.toLong(r)))) {
					rs.add(r);
				} else {
					checkLevelScope = true;
				}
			}
		}
		if ((rs.isEmpty()) && (checkLevelScope)) {
			PrintWriter out = response.getWriter();
			response.setContentType("text/html;charset=UTF-8");
			out.println("<script type='text/javascript'>");
			out.println("alert('" + ResourceBundleUtil.getString("com.seeyon.v3x.main.resources.i18n.MainResources",
					"message.checkLevelScope.alert", new Object[0]) + "')");
			out.println("window.close()");
			out.println("</script>");
			return null;
		}
		modelAndView.addObject("receiverIds", StringUtils.join(rs, ","));
		modelAndView.addObject("receiverNum", Integer.valueOf(rs.size()));
		return modelAndView;
	}

	public ModelAndView showSendSMSDlg(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView modelAndView = new ModelAndView("personalAffair/message/sendSMS");

		String receiverIdsStr = request.getParameter("receiverIds");
		modelAndView.addObject("receiverIds", receiverIdsStr);

		return modelAndView;
	}

	public ModelAndView sendSMS(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String receiverIdsStr = request.getParameter("receiverIds");

		List<Long> legitimacyReceiverIdsList = new ArrayList();
		String noTelNumberMembers = null;
		String content = request.getParameter("content");
		if ((receiverIdsStr != null) && (receiverIdsStr.length() > 0)) {
			String[] receiverIdsStrArray = receiverIdsStr.split(",");
			String separator = Constant.getValueFromCommonRes("common.separator.label");
			String[] arrayOfString1;
			int j = (arrayOfString1 = receiverIdsStrArray).length;
			for (int i = 0; i < j; i++) {
				String receiverIdStr = arrayOfString1[i];
				Long memberId = Long.valueOf(Long.parseLong(receiverIdStr));
				try {
					V3xOrgMember member = this.orgManager.getMemberById(memberId);
					if (member != null) {
						if (Strings.isNotBlank(member.getTelNumber())) {
							legitimacyReceiverIdsList.add(memberId);
						} else if (noTelNumberMembers != null) {
							noTelNumberMembers = noTelNumberMembers + separator + member.getName();
						} else {
							noTelNumberMembers = member.getName();
						}
					}
				} catch (BusinessException e) {
					log.error("", e);
				}
			}
		}
		if (!legitimacyReceiverIdsList.isEmpty()) {
			Long[] receiverIdsArray = (Long[]) legitimacyReceiverIdsList
					.toArray(new Long[legitimacyReceiverIdsList.size()]);
			this.mobileMessageManager.sendMobilePersonMessage(content, MobileConstants.getCurrentId(), new Date(),
					receiverIdsArray);
			if (noTelNumberMembers != null) {
				super.rendJavaScript(response, "parent.showSendResult('" + noTelNumberMembers + "');");
			} else {
				super.rendJavaScript(response, "parent.showSendResult();");
			}
		} else {
			super.rendJavaScript(response, "parent.showErrorResult();");
		}
		return null;
	}

	@CheckRoleAccess(roleTypes = { com.seeyon.ctp.organization.OrgConstants.Role_NAME.AccountAdministrator })
	public ModelAndView showUnitMessageSetting(HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		return super.redirectModelAndView("/message.do?method=showMessageSetting");
	}

	public ModelAndView showMessageSetting(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView("personalAffair/message/messageSettingIndex");
		String messageType = request.getParameter("messageType");

		List<MessagePipeline> allTypes = this.messagePipelineManager.getAllMessagePipeline();
		List<MessagePipeline> showTypes = new ArrayList();
		if (CollectionUtils.isNotEmpty(allTypes)) {
			for (MessagePipeline pipeline : allTypes) {
				if (pipeline.isShowSetting()) {
					showTypes.add(pipeline);
				}
			}
		}
		if (Strings.isBlank(messageType)) {
			messageType = ((MessagePipeline) showTypes.get(0)).getName();
		}
		mav.addObject("messageType", messageType);
		mav.addObject("allTypes", showTypes);
		return mav;
	}

	public ModelAndView showMessageSettingDetail(HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		ModelAndView modelAndView = new ModelAndView("personalAffair/message/messageSetting");
		String messageType = request.getParameter("messageType");
		modelAndView.addObject("type", messageType);

		User user = AppContext.getCurrentUser();
		long memberId = user.getId().longValue();
		long accountId = user.getLoginAccount().longValue();
		modelAndView.addObject("isAdmin", Boolean.valueOf(user.isAdmin()));
		boolean isAllowCustom = true;
		ConfigItem item = this.configManager.getConfigItem("message_setting", messageType + "_custom_enable",
				Long.valueOf(accountId));
		if (item != null) {
			isAllowCustom = Boolean.parseBoolean(item.getConfigValue());
		}
		modelAndView.addObject("isAllowCustom", Boolean.valueOf(isAllowCustom));

		MessagePipeline messagePipeline = this.messagePipelineManager.getMessagePipeline(messageType);
		String errorMsg = messagePipeline.isAllowSetting(user);
		if (Strings.isBlank(errorMsg)) {
			List<Integer> _enabledAppEnum = messagePipeline.getAllowSettingCategory(user);
			if ((_enabledAppEnum != null) && (!_enabledAppEnum.isEmpty())) {
				modelAndView.addObject("enabledAppEnum", _enabledAppEnum);
			}
			Map<Integer, Set<String>> userMessageConfigMap = null;
			if (user.isAdmin()) {
				userMessageConfigMap = this.userMessageFilterConfigManager.getUserMessageConfig(accountId, messageType);
			} else if (isAllowCustom) {
				userMessageConfigMap = this.userMessageFilterConfigManager.getUserMessageConfig(memberId, messageType);
			} else {
				userMessageConfigMap = this.userMessageFilterConfigManager.getUserMessageConfig(accountId, messageType);
			}
			modelAndView.addObject("messageConfigMap", userMessageConfigMap);

			List<BulType> typeList = this.bulDataManager.getAllTypeListExcludeDept();
			modelAndView.addObject("bulletinTypes", typeList);
			modelAndView.addObject("bulletinTypes_size",
					Integer.valueOf(CollectionUtils.isEmpty(typeList) ? 0 : typeList.size()));
			modelAndView.addObject("bulletinTypes_id",
					CollectionUtils.isEmpty(typeList) ? "" : ((BulType) typeList.get(0)).getAccountId());

			List<BulType> groupTypeList = this.bulDataManager.groupAllBoardList();
			modelAndView.addObject("groupBulletinTypes", groupTypeList);
			modelAndView.addObject("groupBulletinTypes_size",
					Integer.valueOf(CollectionUtils.isEmpty(groupTypeList) ? 0 : groupTypeList.size()));
			modelAndView.addObject("groupBulletinTypes_id",
					CollectionUtils.isEmpty(groupTypeList) ? "" : ((BulType) groupTypeList.get(0)).getAccountId());

			List<NewsType> newsTypeList = this.newsDataManager.getAllTypeList(user.getLoginAccount().longValue());
			modelAndView.addObject("newsTypes", newsTypeList);
			modelAndView.addObject("newsTypes_size",
					Integer.valueOf(CollectionUtils.isEmpty(newsTypeList) ? 0 : newsTypeList.size()));
			modelAndView.addObject("newsTypes_id",
					CollectionUtils.isEmpty(newsTypeList) ? "" : ((NewsType) newsTypeList.get(0)).getAccountId());

			List<NewsType> groupNewsTypeList = this.newsDataManager.getGroupAllTypeList();
			modelAndView.addObject("groupNewsTypes", groupNewsTypeList);
			modelAndView.addObject("groupNewsTypes_size",
					Integer.valueOf(CollectionUtils.isEmpty(groupNewsTypeList) ? 0 : groupNewsTypeList.size()));
			modelAndView.addObject("groupNewsTypes_id", CollectionUtils.isEmpty(groupNewsTypeList) ? ""
					: ((NewsType) groupNewsTypeList.get(0)).getAccountId());

			List<V3xBbsBoard> bbsBoards = this.bbsBoardManager
					.getAllCorporationBbsBoard(user.getLoginAccount().longValue());
			modelAndView.addObject("bbsBoards", bbsBoards);
			modelAndView.addObject("bbsBoards_size",
					Integer.valueOf(CollectionUtils.isEmpty(bbsBoards) ? 0 : bbsBoards.size()));
			modelAndView.addObject("bbsBoards_id",
					CollectionUtils.isEmpty(bbsBoards) ? "" : ((V3xBbsBoard) bbsBoards.get(0)).getAccountId());

			List<V3xBbsBoard> groupBbsBoards = this.bbsBoardManager.getAllGroupBbsBoard();
			modelAndView.addObject("groupBbsBoards", groupBbsBoards);
			modelAndView.addObject("groupBbsBoards_size",
					Integer.valueOf(CollectionUtils.isEmpty(groupBbsBoards) ? 0 : groupBbsBoards.size()));
			modelAndView.addObject("groupBbsBoards_id", CollectionUtils.isEmpty(groupBbsBoards) ? ""
					: ((V3xBbsBoard) groupBbsBoards.get(0)).getAccountId());

			List<SurveyTypeCompose> inquiryTypes = this.inquiryManager.getUserIndexInquiryList(false, false);
			modelAndView.addObject("inquiryTypes", inquiryTypes);
			modelAndView.addObject("inquiryTypes_size",
					Integer.valueOf(CollectionUtils.isEmpty(inquiryTypes) ? 0 : inquiryTypes.size()));
			modelAndView.addObject("inquiryTypes_id", CollectionUtils.isEmpty(inquiryTypes) ? ""
					: ((SurveyTypeCompose) inquiryTypes.get(0)).getInquirySurveytype().getAccountId());

			List<SurveyTypeCompose> groupInquiryTypes = this.inquiryManager.getGroupInquiryTypeList();
			modelAndView.addObject("groupInquiryTypes", groupInquiryTypes);
			modelAndView.addObject("groupInquiryTypes_size",
					Integer.valueOf(CollectionUtils.isEmpty(groupInquiryTypes) ? 0 : groupInquiryTypes.size()));
			modelAndView.addObject("groupInquiryTypes_id", CollectionUtils.isEmpty(groupInquiryTypes) ? ""
					: ((SurveyTypeCompose) groupInquiryTypes.get(0)).getInquirySurveytype().getAccountId());
			if (!user.isAdmin()) {
				List<PortalSpaceFix> allSpace = this.spaceManager.getAccessSpace(Long.valueOf(memberId),
						Long.valueOf(accountId));

				List<Map<String, Object>> AllDepartmentBulList = new ArrayList();

				List<Map<String, Object>> AllDepartmentBbsList = new ArrayList();

				List<Map<String, Object>> AllCustomBulList = new ArrayList();

				List<Map<String, Object>> AllCustomNewsList = new ArrayList();

				List<Map<String, Object>> AllCustomBbsList = new ArrayList();

				List<Map<String, Object>> AllCustomInquiryList = new ArrayList();

				List<Map<String, Object>> AllPublicCustomBulList = new ArrayList();

				List<Map<String, Object>> AllPublicCustomNewsList = new ArrayList();

				List<Map<String, Object>> AllPublicCustomBbsList = new ArrayList();

				List<Map<String, Object>> AllPublicCustomInquiryList = new ArrayList();

				List<Map<String, Object>> AllPublicCustomGroupBulList = new ArrayList();

				List<Map<String, Object>> AllPublicCustomGroupNewsList = new ArrayList();

				List<Map<String, Object>> AllPublicCustomGroupBbsList = new ArrayList();

				List<Map<String, Object>> AllPublicCustomGroupInquiryList = new ArrayList();
				if (CollectionUtils.isNotEmpty(allSpace)) {
					for (PortalSpaceFix space : allSpace) {
						Map<String, Object> bulMap = null;
						Map<String, Object> newsMap = null;
						Map<String, Object> bbsMap = null;
						Map<String, Object> inquiryMap = null;
						int bulTypeInt = 0;
						String newsType = "";
						int bbsTypeInt = 0;
						int inquiryTypeInt = 0;
						Long spaceId = Long.valueOf(0L);
						Long entityId = Long.valueOf(0L);
						Long aId = Long.valueOf(0L);
						if (Constants.SpaceType.department.ordinal() == space.getType().intValue()) {
							bulTypeInt = Constants.SpaceType.department.ordinal();
							newsType = "";
							bbsTypeInt = Constants.SpaceType.department.ordinal();
							inquiryTypeInt = Constants.SpaceType.department.ordinal();
							spaceId = space.getId();
							entityId = space.getEntityId();
							aId = space.getAccountId();
						} else if (Constants.SpaceType.custom.ordinal() == space.getType().intValue()) {
							bulTypeInt = Constants.SpaceType.custom.ordinal();
							newsType = "custom";
							bbsTypeInt = BbsConstants.BBS_BOARD_AFFILITER.CUSTOM.ordinal();
							inquiryTypeInt = InquirySurveytype.Space_Type_Custom.intValue();
							spaceId = space.getId();
						} else if (Constants.SpaceType.public_custom.ordinal() == space.getType().intValue()) {
							bulTypeInt = Constants.SpaceType.public_custom.ordinal();
							newsType = "publicCustom";
							bbsTypeInt = Constants.SpaceType.public_custom.ordinal();
							inquiryTypeInt = Constants.SpaceType.public_custom.ordinal();
							spaceId = space.getId();
						} else if (Constants.SpaceType.public_custom_group.ordinal() == space.getType().intValue()) {
							bulTypeInt = Constants.SpaceType.public_custom_group.ordinal();
							newsType = "publicCustomGroup";
							bbsTypeInt = Constants.SpaceType.public_custom_group.ordinal();
							inquiryTypeInt = Constants.SpaceType.public_custom_group.ordinal();
							spaceId = space.getId();
						}
						List<BulType> BulList = this.bulDataManager.getAllTypeListOfCustom(
								(aId.longValue() == 0L ? spaceId : entityId).longValue(), bulTypeInt);
						List<NewsType> NewsList = this.newsDataManager.getAllTypeList(spaceId.longValue(), newsType);
						List<V3xBbsBoard> BbsList = this.bbsBoardManager.getAllCustomAccBbsBoard(
								(aId.longValue() == 0L ? spaceId : aId).longValue(), bbsTypeInt);
						List<InquirySurveytype> InquiryList = this.inquiryManager.getCustomAccInquiryTypeList(spaceId,
								inquiryTypeInt);
						if (CollectionUtils.isNotEmpty(BulList)) {
							bulMap = new HashMap();
							bulMap.put("list", BulList);
							bulMap.put("size", Integer.valueOf(BulList.size()));
							bulMap.put("list_accountId", ((BulType) BulList.get(0)).getAccountId());
							bulMap.put("spaceName", space.getSpacename());
						}
						if (CollectionUtils.isNotEmpty(NewsList)) {
							newsMap = new HashMap();
							newsMap.put("list", NewsList);
							newsMap.put("size", Integer.valueOf(NewsList.size()));
							newsMap.put("list_accountId", ((NewsType) NewsList.get(0)).getAccountId());
							newsMap.put("spaceName", space.getSpacename());
						}
						if (CollectionUtils.isNotEmpty(BbsList)) {
							bbsMap = new HashMap();
							bbsMap.put("list", BbsList);
							bbsMap.put("size", Integer.valueOf(BbsList.size()));
							bbsMap.put("list_accountId", ((V3xBbsBoard) BbsList.get(0)).getAccountId());
							bbsMap.put("spaceName", space.getSpacename());
						}
						if (CollectionUtils.isNotEmpty(InquiryList)) {
							inquiryMap = new HashMap();
							inquiryMap.put("list", InquiryList);
							inquiryMap.put("size", Integer.valueOf(InquiryList.size()));
							inquiryMap.put("list_accountId", ((InquirySurveytype) InquiryList.get(0)).getAccountId());
							inquiryMap.put("spaceName", space.getSpacename());
						}
						if (Constants.SpaceType.department.ordinal() == space.getType().intValue()) {
							if ((bulMap != null) && (bulMap.size() > 0)) {
								AllDepartmentBulList.add(bulMap);
							}
							if ((bbsMap != null) && (bbsMap.size() > 0)) {
								AllDepartmentBbsList.add(bbsMap);
							}
						}
						if (Constants.SpaceType.custom.ordinal() == space.getType().intValue()) {
							if ((bulMap != null) && (bulMap.size() > 0)) {
								AllCustomBulList.add(bulMap);
							}
							if ((newsMap != null) && (newsMap.size() > 0)) {
								AllCustomNewsList.add(newsMap);
							}
							if ((bbsMap != null) && (bbsMap.size() > 0)) {
								AllCustomBbsList.add(bbsMap);
							}
							if ((inquiryMap != null) && (inquiryMap.size() > 0)) {
								AllCustomInquiryList.add(inquiryMap);
							}
						}
						if (Constants.SpaceType.public_custom.ordinal() == space.getType().intValue()) {
							if ((bulMap != null) && (bulMap.size() > 0)) {
								AllPublicCustomBulList.add(bulMap);
							}
							if ((newsMap != null) && (newsMap.size() > 0)) {
								AllPublicCustomNewsList.add(newsMap);
							}
							if ((bbsMap != null) && (bbsMap.size() > 0)) {
								AllPublicCustomBbsList.add(bbsMap);
							}
							if ((inquiryMap != null) && (inquiryMap.size() > 0)) {
								AllPublicCustomInquiryList.add(inquiryMap);
							}
						}
						if (Constants.SpaceType.public_custom_group.ordinal() == space.getType().intValue()) {
							if ((bulMap != null) && (bulMap.size() > 0)) {
								AllPublicCustomGroupBulList.add(bulMap);
							}
							if ((newsMap != null) && (newsMap.size() > 0)) {
								AllPublicCustomGroupNewsList.add(newsMap);
							}
							if ((bbsMap != null) && (bbsMap.size() > 0)) {
								AllPublicCustomGroupBbsList.add(bbsMap);
							}
							if ((inquiryMap != null) && (inquiryMap.size() > 0)) {
								AllPublicCustomGroupInquiryList.add(inquiryMap);
							}
						}
					}
				}
				modelAndView.addObject("AllDepartmentBulList", AllDepartmentBulList);
				modelAndView.addObject("AllDepartmentBbsList", AllDepartmentBbsList);
				modelAndView.addObject("AllCustomBulList", AllCustomBulList);
				modelAndView.addObject("AllCustomNewsList", AllCustomNewsList);
				modelAndView.addObject("AllCustomBbsList", AllCustomBbsList);
				modelAndView.addObject("AllCustomInquiryList", AllCustomInquiryList);
				modelAndView.addObject("AllPublicCustomBulList", AllPublicCustomBulList);
				modelAndView.addObject("AllPublicCustomNewsList", AllPublicCustomNewsList);
				modelAndView.addObject("AllPublicCustomBbsList", AllPublicCustomBbsList);
				modelAndView.addObject("AllPublicCustomInquiryList", AllPublicCustomInquiryList);
				modelAndView.addObject("AllPublicCustomGroupBulList", AllPublicCustomGroupBulList);
				modelAndView.addObject("AllPublicCustomGroupNewsList", AllPublicCustomGroupNewsList);
				modelAndView.addObject("AllPublicCustomGroupBbsList", AllPublicCustomGroupBbsList);
				modelAndView.addObject("AllPublicCustomGroupInquiryList", AllPublicCustomGroupInquiryList);

				List<PortalSpaceFix> guestbookList = this.spaceManager.getSpacesOfSection("guestbookSection",
						Long.valueOf(memberId), Long.valueOf(accountId));
				modelAndView.addObject("guestbookList", guestbookList);
				modelAndView.addObject("guestbookList_size", Integer.valueOf(guestbookList.size()));

				Object projectList = this.projectManager.getIndexProjectListOnly(memberId, -1);
				modelAndView.addObject("projectList", projectList);
				modelAndView.addObject("projectList_size", Integer
						.valueOf(CollectionUtils.isEmpty((Collection) projectList) ? 0 : ((List) projectList).size()));
			}
			List<ExtendedMessageSystem> otherMsgSystemList = this.extendedMessageSystemManager.getAllExtendedSystem();
			modelAndView.addObject("otherMsgSystemList", otherMsgSystemList);
			modelAndView.addObject("otherMsgSystemList_size",
					Integer.valueOf(otherMsgSystemList == null ? 0 : otherMsgSystemList.size()));
		} else {
			modelAndView.addObject("errorMsg", errorMsg);
		}
		return modelAndView;
	}

	public ModelAndView updateMessageSetting(HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		String messageType = request.getParameter("messageType");

		User user = AppContext.getCurrentUser();
		long memberId = user.getId().longValue();
		Timestamp stamp;
		if (user.isAdmin()) {
			memberId = AppContext.currentAccountId();
			ConfigItem item = this.configManager.getConfigItem("message_setting", messageType + "_custom_enable",
					Long.valueOf(memberId));
			String allowCustom = "on".equals(request.getParameter("allowCustom")) ? "true" : "false";
			Date date = new Date();
			stamp = new Timestamp(date.getTime());
			if (item == null) {
				item = new ConfigItem();
				item.setNewId();
				item.setConfigCategory("message_setting");
				item.setConfigItem(messageType + "_custom_enable");
				item.setConfigValue(allowCustom);
				item.setCreateDate(stamp);
				item.setModifyDate(stamp);
				item.setOrgAccountId(Long.valueOf(memberId));
				this.configManager.addConfigItem(item);
			} else {
				item.setConfigValue(allowCustom);
				item.setModifyDate(stamp);
				this.configManager.updateConfigItem(item);
			}
			this.userMessageFilterConfigManager.saveMessageSetting(item);
		}
		Map<Integer, Set<String>> userMessageConfigMap = new HashMap();

		String[] selectedApps = request.getParameterValues("App");
		Object localObject1;
		Integer appEnum;
		Set<String> configSet;
		String[] configOptions;
		if ((selectedApps != null) && (selectedApps.length > 0)) {
			for (int i = 0; i < selectedApps.length; i++) {
				String appEnumStr = selectedApps[i];
				appEnum = Integer.valueOf(Integer.parseInt(appEnumStr));
				configSet = new HashSet();
				if ("ALL".equals(request.getParameter("AllInput_" + appEnum))) {
					configSet.add(MessageFilterOption.ALL.name());
				} else {
					String[] arrayOfString1;
					int j;
					if ((appEnum.intValue() == ApplicationCategoryEnum.bulletin.ordinal())
							|| (appEnum.intValue() == ApplicationCategoryEnum.news.ordinal())
							|| (appEnum.intValue() == ApplicationCategoryEnum.bbs.ordinal())
							|| (appEnum.intValue() == ApplicationCategoryEnum.inquiry.ordinal())
							|| (appEnum.intValue() == ApplicationCategoryEnum.guestbook.ordinal())) {
						String[] appIndex = request.getParameterValues("App" + appEnum + "_Q");
						if (appIndex != null) {
							j = (arrayOfString1 = appIndex).length;
							for (i = 0; i < j; i++) {
								String strIndex = arrayOfString1[i];
								for (i = 0; i < Integer.parseInt(strIndex); i++) {
									configOptions = request.getParameterValues("Option" + appEnum + "_" + (i + 1));
									if ((configOptions != null) && (configOptions.length > 0)) {
										String[] arrayOfString2;
										int m = (arrayOfString2 = configOptions).length;
										for (int k = 0; k < m; k++) {
											String optionValue = arrayOfString2[k];
											configSet.add(optionValue);
										}
									}
								}
							}
						}
					} else {
						configOptions = request.getParameterValues("Option_" + appEnum);
						if ((configOptions != null) && (configOptions.length > 0)) {
							j = (arrayOfString1 = configOptions).length;
							for (i = 0; i < j; i++) {
								String optionValue = arrayOfString1[i];
								configSet.add(optionValue);
							}
						}
					}
				}
				userMessageConfigMap.put(appEnum, configSet);
			}
		}
		if (user.isAdmin()) {
			Set<String> bulSet = (Set) userMessageConfigMap
					.get(Integer.valueOf(ApplicationCategoryEnum.bulletin.ordinal()));
			if (bulSet == null) {
				bulSet = new HashSet();
				userMessageConfigMap.put(Integer.valueOf(ApplicationCategoryEnum.bulletin.ordinal()), bulSet);
			}
			if (!bulSet.contains("ALL")) {
				List<BulType> bulTypeList = this.bulDataManager.getAllCustomTypes();
				if (Strings.isNotEmpty(bulTypeList)) {
					for (localObject1 = bulTypeList.iterator(); ((Iterator) localObject1).hasNext();) {
						BulType bulType = (BulType) ((Iterator) localObject1).next();
						bulSet.add(String.valueOf(bulType.getId()));
					}
				}
			}
			Set<String> newsSet = (Set) userMessageConfigMap
					.get(Integer.valueOf(ApplicationCategoryEnum.news.ordinal()));
			if (newsSet == null) {
				newsSet = new HashSet();
				userMessageConfigMap.put(Integer.valueOf(ApplicationCategoryEnum.news.ordinal()), newsSet);
			}
			if (!newsSet.contains("ALL")) {
				List<NewsType> newsTypeList = this.newsDataManager.getAllCustomTypes();
				if (Strings.isNotEmpty((Collection) newsTypeList)) {
					for (NewsType newsType : newsTypeList) {
						newsSet.add(String.valueOf(newsType.getId()));
					}
				}
			}
			Set bbsSet = userMessageConfigMap.get(Integer.valueOf(ApplicationCategoryEnum.bbs.ordinal()));
			if (bbsSet == null) {
				bbsSet = new HashSet();
				userMessageConfigMap.put(Integer.valueOf(ApplicationCategoryEnum.bbs.ordinal()), bbsSet);
			}
			if (!((Set) bbsSet).contains("ALL")) {
				List<V3xBbsBoard> boardList = this.bbsBoardManager.getAllCustomBoards();
				if (Strings.isNotEmpty((Collection) boardList)) {
					for (V3xBbsBoard board : boardList) {
						((Set) bbsSet).add(String.valueOf(board.getId()));
					}
				}
			}
			Set inquirySet = userMessageConfigMap.get(Integer.valueOf(ApplicationCategoryEnum.inquiry.ordinal()));
			if (inquirySet == null) {
				inquirySet = new HashSet();
				userMessageConfigMap.put(Integer.valueOf(ApplicationCategoryEnum.inquiry.ordinal()), inquirySet);
			}
			if (!((Set) inquirySet).contains("ALL")) {
				List<InquirySurveytype> inquiryTypeList = this.inquiryManager.getAllCustomTypes();
				if (Strings.isNotEmpty(inquiryTypeList)) {
					for (InquirySurveytype inquiryType : inquiryTypeList) {
						((Set) inquirySet).add(String.valueOf(inquiryType.getId()));
					}
				}
			}
		}
		this.userMessageFilterConfigManager.saveUserMessageConfig(memberId, messageType, userMessageConfigMap);
		super.rendJavaScript(response,
				"alert('" + com.seeyon.ctp.system.Constants.getString4CurrentUser("system.manager.ok", new Object[0])
						+ "');parent.location.href=parent.location");
		return null;
	}

	public void deleteAttachmentsOfPerMsg(String[] filesIdArray, String[] createDateArray) {
		if (filesIdArray.length == 0) {
			return;
		}
		try {
			for (int i = 0; i < filesIdArray.length; i++) {
				Long fileId = Long.valueOf(Long.parseLong(filesIdArray[i]));
				Date createDate = Datetimes.parseDatetime(createDateArray[i]);
				this.fileManager.deleteFile(fileId, createDate, Boolean.valueOf(true));
			}
		} catch (Exception localException) {
		}
	}

	/**
	 * @deprecated
	 */
	public boolean isEnableMsgSound() {
		boolean isEnableMsgSound = false;
		String enableMsgSoundConfig = this.systemConfig.get("SMS_hint");
		if (enableMsgSoundConfig != null) {
			isEnableMsgSound = "enable".equals(enableMsgSoundConfig);
		}
		if (!isEnableMsgSound) {
			return false;
		}
		Long memberId = CurrentUser.get().getId();
		try {
			this.orgManager.getMemberById(memberId);

			String enableMsgSound = "";
			isEnableMsgSound = "true".equals(enableMsgSound);
		} catch (BusinessException e) {
			log.warn("", e);
		}
		return isEnableMsgSound;
	}

	public boolean testSendEMail(String smtpHostName, String sysEmailAddress, String emailPassword,
			String recEmailAddress, String userName, String smtpPort) {
		if ((Strings.isBlank(userName)) || (Strings.isBlank(smtpHostName)) || (Strings.isBlank(sysEmailAddress))
				|| (Strings.isBlank(emailPassword)) || (Strings.isBlank(recEmailAddress))
				|| (Strings.isBlank(smtpPort))) {
			return false;
		}
		return this.messageMailManager.testEmailSend(smtpHostName, sysEmailAddress, emailPassword, recEmailAddress,
				userName, smtpPort);
	}

	public ModelAndView showSendPersonalSMSDlg(HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		ModelAndView modelAndView = new ModelAndView("personalAffair/message/sendPersonalSMS");
		return modelAndView;
	}

	public ModelAndView sendPersonalSMS(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String phoneMembers = request.getParameter("phoneMembers");
		String content = request.getParameter("content");
		this.mobileMessageManager.sendPersonalMessage(content, MobileConstants.getCurrentId(), new Date(),
				phoneMembers);
		super.rendJavaScript(response, "parent.showSendPersonalSMSResult();");
		return null;
	}

	public ModelAndView showMessages(HttpServletRequest request, HttpServletResponse response) throws Exception {
		User member = CurrentUser.get();
		ModelAndView modelAndView = new ModelAndView("personalAffair/message/showHistoryMessage");
		String showType = request.getParameter("showType");
		if ("0".equals(showType)) {
			String condition = request.getParameter("condition");
			String textField1 = request.getParameter("textfield");
			String textField2 = request.getParameter("textfield1");
			String readType = request.getParameter("readType");
			List<UserHistoryMessage> messageList = this.userMessageManager.getAllSystemMessages(
					member.getId().longValue(), condition, textField1, textField2, Boolean.valueOf(true), readType);
			List<ExtendedMessageSystem> otherapp = this.extendedMessageSystemManager.getAllExtendedSystem();
			modelAndView.addObject("otherapp", otherapp);
			modelAndView.addObject("messageList", messageList);
		} else {
			List<UserHistoryMessage> messageTreeList = this.userMessageManager.getHistoryMessageTree(member.getId());
			List<Long> memberList = new ArrayList();
			List<Long> deptList = new ArrayList();
			List<Long> teamList = new ArrayList();
			List<V3xOrgMember> memberListTemp = new ArrayList();
			List<V3xOrgDepartment> deptListTemp = new ArrayList();
			List<V3xOrgTeam> teamListTemp = new ArrayList();
			for (UserHistoryMessage message : messageTreeList) {
				Long id = null;
				if (message.getMessageType() == 1) {
					id = member.getId() == message.getSenderId() ? message.getReceiverId() : message.getSenderId();
					if ((!memberList.contains(id)) && (id != member.getId())) {
						V3xOrgMember m = this.orgManager.getMemberById(id);
						if (m != null) {
							memberListTemp.add(m);
							memberList.add(id);
						}
					}
				} else if (message.getMessageType() == 2) {
					id = message.getReferenceId();
					if (!deptList.contains(id)) {
						V3xOrgDepartment d = this.orgManager.getDepartmentById(id);
						if (d != null) {
							deptListTemp.add(d);
							deptList.add(id);
						}
					}
				} else if ((message.getMessageType() == 3) || (message.getMessageType() == 4)
						|| (message.getMessageType() == 5)) {
					id = message.getReferenceId();
					if (!teamList.contains(id)) {
						V3xOrgTeam t = this.orgManager.getTeamById(id);
						if (t != null) {
							teamListTemp.add(t);
							teamList.add(id);
						}
					}
				}
			}
			memberList.clear();
			deptList.clear();
			teamList.clear();
			Iterator<V3xOrgMember> iter1 = memberListTemp.iterator();
			while (iter1.hasNext()) {
				V3xOrgMember o = (V3xOrgMember) iter1.next();
				memberList.add(o.getId());
			}
			Object iter2 = deptListTemp.iterator();
			while (((Iterator) iter2).hasNext()) {
				V3xOrgDepartment d = (V3xOrgDepartment) ((Iterator) iter2).next();
				deptList.add(d.getId());
			}
			Iterator<V3xOrgTeam> iter3 = teamListTemp.iterator();
			while (iter3.hasNext()) {
				V3xOrgTeam t = (V3xOrgTeam) iter3.next();
				teamList.add(t.getId());
			}
			modelAndView.addObject("memberList", memberList);
			modelAndView.addObject("memberSize", Integer.valueOf(memberList.size()));
			modelAndView.addObject("deptList", deptList);
			modelAndView.addObject("teamList", teamList);
			modelAndView.addObject("teamSize", Integer.valueOf(deptList.size() + teamList.size()));
		}
		return modelAndView;
	}

	public ModelAndView removeMessages(HttpServletRequest request, HttpServletResponse response) throws Exception {
		User member = CurrentUser.get();
		Long internalId = member.getId();
		String showType = request.getParameter("showType");
		if ("0".equals(showType)) {
			this.userMessageManager.removeAllMessages(internalId.longValue(), 0);
		} else {
			this.userMessageManager.removeAllMessages(internalId.longValue(), 1);
		}
		return super.redirectModelAndView("/message.do?method=showMessages&isClear=Y&showType=" + showType);
	}

	public ModelAndView saveAsExcel(HttpServletRequest request, HttpServletResponse response) throws Exception {
		User member = CurrentUser.get();
		Long internalId = member.getId();
		String showType = request.getParameter("showType");
		String fileName = null;
		DataRecord myDataRecord = new DataRecord();
		String categoryStr;
		if ("0".equals(showType)) {
			fileName = Constant.getValueFromMainRes("message.tag.systemMessage.label", new String[0]);
			List<UserHistoryMessage> messageList = this.userMessageManager.getAllSystemMessages(internalId.longValue(),
					null, null, null, Boolean.valueOf(false));

			myDataRecord.setColumnName(
					new String[] { Constant.getValueFromMainRes("message.tableHeader.category", new String[0]),
							Constant.getValueFromMainRes("message.tableHeader.sender", new String[0]),
							Constant.getValueFromCommonRes("common.date.sendtime.label"),
							Constant.getValueFromMainRes("message.tableHeader.title", new String[0]) });

			myDataRecord.setColumnWith(new short[] { 12, 12, 24, 50 });
			if ((messageList != null) && (!messageList.isEmpty())) {
				for (UserHistoryMessage obj : messageList) {
					DataRow row = new DataRow();

					categoryStr = Constant.getApplicationCategory(obj.getMessageCategory().intValue());

					row.addDataCell(categoryStr, 1);
					row.addDataCell(obj.getSenderName(), 1);
					row.addDataCell(Datetimes.formateToLocaleDatetime(obj.getCreationDate()), 6);
					row.addDataCell(obj.getMessageContent(), 1);
					myDataRecord.addDataRow(new DataRow[] { row });
				}
			}
		} else {
			myDataRecord.setColumnName(
					new String[] { Constant.getValueFromMainRes("message.tableHeader.sender", new String[0]),
							Constant.getValueFromMainRes("message.tableHeader.title", new String[0]) });
			fileName = Constant.getValueFromMainRes("message.tag.personMessage.label", new String[0]);
			myDataRecord.setColumnWith(new short[] { 40, 120 });
			String exportType = request.getParameter("exportType");
			Long exportId = Long.valueOf(NumberUtils.toLong(request.getParameter("exportId")));
			List<UserHistoryMessage> messageList = this.userMessageManager.getAllHistoryMessage(null, internalId,
					exportId, true, exportType, null, null, false);
			if ((messageList != null) && (!((List) messageList).isEmpty())) {
				for (UserHistoryMessage obj : messageList) {
					DataRow row = new DataRow();
					HTMLFileParser parser = new HTMLFileParser();
					parser.setStr(obj.getMessageContent());
					row.addDataCell(
							obj.getSenderName() + "  " + Datetimes.formateToLocaleDatetime(obj.getCreationDate()), 1);
					row.addDataCell(parser.getContentString(), 1);
					myDataRecord.addDataRow(new DataRow[] { row });
				}
			}
		}
		myDataRecord.setSheetName(fileName);
		myDataRecord.setTitle(fileName);

		this.fileToExcelManager.save(response, fileName, new DataRecord[] { myDataRecord });
		return null;
	}
}
