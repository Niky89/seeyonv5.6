package com.seeyon.v3x.online.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.springframework.web.servlet.ModelAndView;

import com.seeyon.ctp.common.AppContext;
import com.seeyon.ctp.common.authenticate.domain.User;
import com.seeyon.ctp.common.constants.Constants.LoginOfflineOperation;
import com.seeyon.ctp.common.controller.BaseController;
import com.seeyon.ctp.common.exceptions.BusinessException;
import com.seeyon.ctp.common.flag.SysFlag;
import com.seeyon.ctp.common.taglibs.functions.Functions;
import com.seeyon.ctp.login.online.OnlineManager;
import com.seeyon.ctp.login.online.OnlineRecorder;
import com.seeyon.ctp.login.online.OnlineUser;
import com.seeyon.ctp.organization.bo.CompareSortEntity;
import com.seeyon.ctp.organization.bo.MemberPost;
import com.seeyon.ctp.organization.bo.V3xOrgAccount;
import com.seeyon.ctp.organization.bo.V3xOrgDepartment;
import com.seeyon.ctp.organization.bo.V3xOrgEntity;
import com.seeyon.ctp.organization.bo.V3xOrgMember;
import com.seeyon.ctp.organization.bo.V3xOrgPost;
import com.seeyon.ctp.organization.bo.V3xOrgTeam;
import com.seeyon.ctp.organization.manager.OrgManager;
import com.seeyon.ctp.organization.manager.OuterWorkerAuthUtil;
import com.seeyon.ctp.organization.webmodel.WebV3xOrgDepartment;
import com.seeyon.ctp.portal.util.Constants;
import com.seeyon.ctp.util.Strings;
import com.seeyon.ctp.util.UniqueList;
import com.seeyon.ctp.util.annotation.CheckRoleAccess;
import com.seeyon.v3x.online.OffLineUserModel;
import com.seeyon.v3x.online.OnlineAccountModel;
import com.seeyon.v3x.online.OnlineUserModel;
import com.seeyon.v3x.online.manager.WIMManager;
import com.seeyon.v3x.peoplerelate.domain.PeopleRelate;
import com.seeyon.v3x.peoplerelate.manager.PeopleRelateManager;

public class OnlineController extends BaseController {
	private OrgManager orgManager;
	private OnlineManager onlineManager;
	private WIMManager wimManager;
	private PeopleRelateManager peopleRelateManager;

	public void setOrgManager(OrgManager orgManager) {
		this.orgManager = orgManager;
	}

	public void setOnlineManager(OnlineManager onlineManager) {
		this.onlineManager = onlineManager;
	}

	public void setWimManager(WIMManager wimManager) {
		this.wimManager = wimManager;
	}

	public void setPeopleRelateManager(PeopleRelateManager peopleRelateManager) {
		this.peopleRelateManager = peopleRelateManager;
	}

	@CheckRoleAccess(roleTypes = { com.seeyon.ctp.organization.OrgConstants.Role_NAME.NULL })
	public ModelAndView showOnlineUser(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView("personalAffair/message/onlineIM");
		User user = AppContext.getCurrentUser();
		boolean isAdmin = user.isAdmin();
		if (!isAdmin) {
			int deptOnlineNumber = 0;
			List<V3xOrgMember> deptMembers = this.orgManager.getMembersByDepartment(user.getDepartmentId(), false);
			if (CollectionUtils.isNotEmpty(deptMembers)) {
				for (V3xOrgMember m : deptMembers) {
					if (this.onlineManager.isOnline(m.getLoginName())) {
						deptOnlineNumber++;
					}
				}
			}
			mav.addObject("deptOnlineNumber", Integer.valueOf(deptOnlineNumber));
		}
		mav.addObject("allOnlineNumber", Integer.valueOf(this.onlineManager.getOnlineNumber()));
		mav.addObject("isAdmin", Boolean.valueOf(user.isAdmin()));

		return mav;
	}

	public ModelAndView showOnlineUserTree(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView("personalAffair/message/onlineTree");
		User user = AppContext.getCurrentUser();
		Long accountId = user.getAccountId();
		boolean isAdmin = user.isAdmin();

		List<V3xOrgAccount> accountList = this.orgManager.accessableAccounts(user.getId());
		Collections.sort(accountList, CompareSortEntity.getInstance());
		Long currentAccountId = getCurrentAccountId(request, user, accountList);

		boolean isShowAccountSwitch = ((Boolean) SysFlag.frontPage_online_showAccountSwitch.getFlag()).booleanValue();

		V3xOrgAccount account = null;
		if (isShowAccountSwitch) {
			account = this.orgManager.getAccountById(currentAccountId);
			if (account == null) {
				account = (V3xOrgAccount) accountList.get(0);
				currentAccountId = account.getId();
			}
		} else {
			account = (V3xOrgAccount) this.orgManager.getAllAccounts().get(0);
		}
		List<OnlineAccountModel> onlineAccountList = new ArrayList();
		OnlineAccountModel onlineAccount;
		if (isShowAccountSwitch) {
			List<String> rootAccountIds = new ArrayList();
			for (V3xOrgAccount acc : accountList) {
				if (acc.getSuperior().longValue() == -1L) {
					rootAccountIds.add(String.valueOf(acc.getId()));
				}
				Long accId = acc.getId();

				int onlineNum = acc.isGroup() ? this.onlineManager.getOnlineNumber()
						: this.onlineManager.getOnlineNumber(accId);
				int num = this.orgManager.getAllMembers(accId).size();

				onlineAccount = new OnlineAccountModel();
				onlineAccount.setAccount(acc);
				onlineAccount.setId(acc.getId());
				onlineAccount.setSuperior(acc.getSuperior());
				onlineAccount.setName(acc.getName() + "(" + onlineNum + "/" + num + ")");

				onlineAccountList.add(onlineAccount);
			}
			if (((user.isAdministrator()) && (CollectionUtils.isEmpty(rootAccountIds)))
					|| ((!user.isAdmin()) && (user.isInternal()) && (CollectionUtils.isEmpty(rootAccountIds)))) {
				boolean isGroupAccessable = Functions.isGroupAccessable(user.getLoginAccount());
				if (isGroupAccessable) {
					V3xOrgAccount rootAccount = this.orgManager.getRootAccount();

					rootAccountIds.add(String.valueOf(rootAccount.getId()));

					int onlineNum = this.onlineManager.getOnlineNumber();
					int num = this.orgManager.getAllMembers(rootAccount.getId()).size();

					 onlineAccount = new OnlineAccountModel();
					onlineAccount.setAccount(rootAccount);
					onlineAccount.setId(rootAccount.getId());
					onlineAccount.setSuperior(rootAccount.getSuperior());
					onlineAccount.setName(rootAccount.getName() + "(" + onlineNum + "/" + num + ")");

					onlineAccountList.add(onlineAccount);
				}
			}
			mav.addObject("rootAccountIds", rootAccountIds);
			mav.addObject("accountList", onlineAccountList);
		}
		boolean isSameAccount = isAdmin ? false : currentAccountId.equals(accountId);

		List<WebV3xOrgDepartment> deptList = null;
		Object internalRight = null;
		if (user.isInternal()) {
			if ((isShowAccountSwitch) && (account.isGroup())) {
				List<V3xOrgAccount> childAccountList = this.orgManager.getChildAccount(account.getId(), false);
				Collections.sort(childAccountList, CompareSortEntity.getInstance());
				mav.addObject("isRoot", Boolean.valueOf(true));
				mav.addObject("childAccountList", childAccountList);
			} else {
				deptList = new ArrayList();
				List<V3xOrgDepartment> dList = this.orgManager.getAllDepartments(account.getId());
				Map<String, V3xOrgDepartment> deptPathMap = new HashMap();
				for (V3xOrgDepartment d : dList) {
					deptPathMap.put(d.getPath(), d);
				}
				Collections.sort(dList, CompareSortEntity.getInstance());
				for (V3xOrgDepartment dept : dList) {
					if ((isAdmin) || (dept.getIsInternal().booleanValue())
							|| (OuterWorkerAuthUtil.canAccessOuterDep(user.getId(), user.getDepartmentId(),
									user.getLoginAccount(), dept, this.orgManager))) {
						V3xOrgDepartment pdept = (V3xOrgDepartment) deptPathMap.get(dept.getParentPath());
						WebV3xOrgDepartment webdept = new WebV3xOrgDepartment();
						webdept.setV3xOrgDepartment(dept);
						if (pdept != null) {
							webdept.setParentId(pdept.getId());
							webdept.setParentName(pdept.getName());
						}
						deptList.add(webdept);
					}
				}
			}
		} else {
			deptList = OuterWorkerAuthUtil.getOuterDeptList(mav, user, currentAccountId, this.orgManager);
			internalRight = this.orgManager.getExternalMemberWorkScope(user.getId(), false);
		}
		if (CollectionUtils.isNotEmpty(deptList)) {
			Map<String, Integer> deptOnlineNumMap = new HashMap();
			Map<String, Integer> deptNumMap = new HashMap();
			for (WebV3xOrgDepartment wdp : deptList) {
				Long deptId = wdp.getV3xOrgDepartment().getId();
				int deptOnlineNum = getOnlineUserListByDepartment(deptId, user, (List) internalRight).size();

				int deptNum = this.orgManager.getMembersByDepartment(deptId, false).size();
				deptOnlineNumMap.put(deptId.toString(), Integer.valueOf(deptOnlineNum));
				deptNumMap.put(deptId.toString(), Integer.valueOf(deptNum));
			}
			mav.addObject("deptOnlineNumMap", deptOnlineNumMap);
			mav.addObject("deptNumMap", deptNumMap);
		}
		mav.addObject("isShowAccountSwitch", Boolean.valueOf(isShowAccountSwitch));
		mav.addObject("currentAccountId", currentAccountId);
		mav.addObject("isSameAccount", Boolean.valueOf(isSameAccount));
		mav.addObject("account", account);
		mav.addObject("deptList", deptList);
		return mav;
	}

	public ModelAndView showOnlineUserTeam(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView("personalAffair/message/onlineTeam");
		User user = AppContext.getCurrentUser();

		Map<String, Integer> onlineNumMap = new HashMap();
		Map<String, Integer> numMap = new HashMap();

		List<V3xOrgDepartment> deptList = this.orgManager.getDepartmentsByUser(user.getId());

		List<V3xOrgEntity> internalRight = null;
		if (!user.isInternal()) {
			internalRight = this.orgManager.getExternalMemberWorkScope(user.getId(), false);
		}
		Long deptId;
		for (V3xOrgDepartment dept : deptList) {
			deptId = dept.getId();
			int deptOnlineNum = getOnlineUserListByDepartment(deptId, user, internalRight).size();
			int deptNum = this.orgManager.getMembersByDepartment(deptId, false).size();
			onlineNumMap.put(deptId.toString(), Integer.valueOf(deptOnlineNum));
			numMap.put(deptId.toString(), Integer.valueOf(deptNum));
		}
		List<V3xOrgTeam> teamList = this.wimManager.getAllDiscussTeam(user.getId());
		for (V3xOrgTeam team : teamList) {
			Long teamId = team.getId();
			int teamOnlineNum = 0;
			int teamNum = 0;
			List<Long> memberIds = new UniqueList();
			memberIds.addAll(team.getAllMembers());
			memberIds.addAll(team.getAllRelatives());
			for (Long memberId : memberIds) {
				V3xOrgMember member = this.orgManager.getMemberById(memberId);
				if ((member != null) && (member.isValid())) {
					teamNum++;
					if (this.onlineManager.isOnline(member.getLoginName())) {
						teamOnlineNum++;
					}
				}
			}
			onlineNumMap.put(teamId.toString(), Integer.valueOf(teamOnlineNum));
			numMap.put(teamId.toString(), Integer.valueOf(teamNum));
		}
		mav.addObject("deptList", deptList);
		mav.addObject("teamList", teamList);
		mav.addObject("onlineNumMap", onlineNumMap);
		mav.addObject("numMap", numMap);

		return mav;
	}

	public ModelAndView showOnlineUserList(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView("personalAffair/message/onlineList");
		User user = AppContext.getCurrentUser();
		boolean isAdmin = user.isAdmin();

		List<V3xOrgAccount> accountList = this.orgManager.accessableAccounts(user.getId());
		Long currentAccountId = getCurrentAccountId(request, user, accountList);

		List<V3xOrgEntity> internalRight = null;
		if (!user.isInternal()) {
			internalRight = this.orgManager.getExternalMemberWorkScope(user.getId(), false);
		}
		V3xOrgAccount selectAccount = this.orgManager
				.getAccountById(Long.valueOf(NumberUtils.toLong(request.getParameter("currentAccountId"))));
		boolean isRoot = (selectAccount != null) && (selectAccount.isGroup());

		boolean isRootQuery = "true".equals(request.getParameter("isRoot"));
		mav.addObject("isRoot", Boolean.valueOf((isRoot) || (isRootQuery)));

		List<OnlineUserModel> onlineUserList = new ArrayList();
		List<OffLineUserModel> offlineUserList = new ArrayList();

		boolean isShowOffline = "checked".equals(request.getParameter("showoffline"));

		String condition = request.getParameter("condition");
		boolean isFirstInit = Strings.isBlank(condition);
		OnlineUser u;
		OnlineUser.SecondePost secondPost;
		List<OnlineUserModel> onlineUserModelSet;
		if ((isFirstInit) && (!isAdmin) && (user.isInternal())) {
			String departmentPath = "";
			V3xOrgDepartment department;
			if (user.getLoginAccount() != user.getAccountId()) {
				Map<Long, List<MemberPost>> deptConcurrentMap = this.orgManager
						.getConcurentPostsByMemberId(currentAccountId, user.getId());
				if ((deptConcurrentMap != null) && (!deptConcurrentMap.isEmpty())) {
					Set<Long> deptIds = deptConcurrentMap.keySet();
					Long deptId = (Long) deptIds.toArray()[0];
					department = this.orgManager.getDepartmentById(deptId);
					if (department != null) {
						departmentPath = department.getPath();
					}
				}
			} else {
				 department = this.orgManager.getDepartmentById(user.getDepartmentId());
				if (department != null) {
					departmentPath = department.getPath();
				}
			}
			V3xOrgMember m;
			if (isShowOffline) {
				V3xOrgDepartment dept = this.orgManager.getDepartmentByPath(departmentPath);
				if (dept != null) {
					List<V3xOrgMember> members = this.orgManager.getMembersByDepartment(dept.getId(), false);
					for (  Iterator<V3xOrgMember> department1 = members.iterator(); department1.hasNext();) {
						m = (V3xOrgMember) department1.next();
						if (isOnLine(m, currentAccountId)) {
							V3xOrgPost post = this.orgManager.getPostById(m.getOrgPostId());
							offlineUserList.add(new OffLineUserModel(m, post,
									this.orgManager.getDepartmentById(m.getOrgDepartmentId())));
						}
					}
				}
			}
			List<OnlineUser> onlineUserSet = this.onlineManager.getOnlineList(currentAccountId);
			if ((onlineUserSet != null) && (!onlineUserSet.isEmpty())) {
				for (Iterator<OnlineUser>m1 = onlineUserSet.iterator(); m1.hasNext();) {
					u = (OnlineUser) m1.next();
					if ((u.getAccoutId().equals(currentAccountId)) && ((u.getDepartmentPath().equals(departmentPath))
							|| (u.getDepartmentPath().startsWith(departmentPath + ".")))) {
						if (canDisplay(user, u, internalRight)) {
							onlineUserList.add(new OnlineUserModel(u));
						}
					} else {
						secondPost = u.getSecondePost(currentAccountId, departmentPath);
						if (secondPost != null) {
							OnlineUserModel m2 = new OnlineUserModel(u);
							m2.setDepartmentName(secondPost.getDepartmentSimpleName());
							m2.setPostName(secondPost.getPostName());
							m2.setPluralist(true);
							if (canDisplay(user, u, internalRight)) {
								onlineUserList.add(m2);
							}
						} else if (u.getInternalId() == user.getId()) {
							OnlineUserModel m2 = new OnlineUserModel(u);
							m2.setDepartmentName(null);
							m2.setPostName(null);
							m2.setPluralist(true);
							onlineUserList.clear();
							onlineUserList.add(m2);
							break;
						}
					}
				}
			}
		} else if (((isFirstInit) && (isAdmin)) || ((isFirstInit) && (!user.isInternal()))
				|| ("SelectAccount".equals(condition))) {
			if ((isShowOffline) && (!isRoot)) {
				List<V3xOrgMember> members = this.orgManager.getAllMembers(currentAccountId);
				for (V3xOrgMember member : members) {
					if (isOnLine(member, currentAccountId)) {
						offlineUserList
								.add(new OffLineUserModel(member, this.orgManager.getPostById(member.getOrgPostId()),
										this.orgManager.getDepartmentById(member.getOrgDepartmentId())));
					}
				}
			}
			onlineUserList = getOnlineUserListByAccount(currentAccountId, user, internalRight);

			mav.addObject("queryType", "account");
			mav.addObject("queryValue", currentAccountId);
		} else if ("ByDepartment".equals(condition)) {
			String departmentId = request.getParameter("departmentId");
			if (Strings.isNotBlank(departmentId)) {
				V3xOrgDepartment dept = this.orgManager
						.getDepartmentById(Long.valueOf(NumberUtils.toLong(departmentId)));
				currentAccountId = dept.getOrgAccountId();
				if (isShowOffline) {
					List<V3xOrgMember> members = this.orgManager.getMembersByDepartment(dept.getId(), false);
					for (V3xOrgMember m : members) {
						if (isOnLine(m, currentAccountId)) {
							V3xOrgPost post = this.orgManager.getPostById(m.getOrgPostId());
							offlineUserList.add(new OffLineUserModel(m, post,
									this.orgManager.getDepartmentById(m.getOrgDepartmentId())));
						}
					}
				}
				onlineUserList = getOnlineUserListByDepartment(Long.valueOf(NumberUtils.toLong(departmentId)), user,
						internalRight);
			}
			mav.addObject("queryType", "department");
			mav.addObject("queryValue", departmentId);
		} else {
			List<Long> memberIds;
			if ("ByTeam".equals(condition)) {
				String teamId = request.getParameter("departmentId");
				V3xOrgTeam team = this.orgManager.getTeamById(Long.valueOf(NumberUtils.toLong(teamId)));
				if (team != null) {
					memberIds = new UniqueList();
					memberIds.addAll(team.getAllMembers());
					memberIds.addAll(team.getAllRelatives());
					for (Long memberId : memberIds) {
						V3xOrgMember member = this.orgManager.getMemberById(memberId);
						if ((member != null) && (member.isValid())) {
							OnlineUser onlineUser = this.onlineManager.isOnlineUser(member.getLoginName());
							if (onlineUser != null) {
								onlineUserList.add(new OnlineUserModel(onlineUser));
							} else if (isShowOffline) {
								offlineUserList.add(
										new OffLineUserModel(member, this.orgManager.getPostById(member.getOrgPostId()),
												this.orgManager.getDepartmentById(member.getOrgDepartmentId())));
							}
						}
					}
				}
				mav.addObject("queryType", "team");
				mav.addObject("queryValue", teamId);
			} else {
				OnlineUser onlineUser;
				if ("MyRelate".equals(condition)) {
					List<PeopleRelate> myRelateList = this.peopleRelateManager.getPeopleRelatedList(user.getId());
					for (PeopleRelate p : myRelateList) {
						if (p != null) {
							V3xOrgMember member = this.orgManager.getMemberById(p.getRelateMemberId());
							if ((member != null) && (member.isValid())) {
								onlineUser = this.onlineManager.isOnlineUser(member.getLoginName());
								if (onlineUser != null) {
									onlineUserList.add(new OnlineUserModel(onlineUser));
								} else if (isShowOffline) {
									offlineUserList.add(new OffLineUserModel(member,
											this.orgManager.getPostById(member.getOrgPostId()),
											this.orgManager.getDepartmentById(member.getOrgDepartmentId())));
								}
							}
						}
					}
				} else if ("ByName".equals(condition)) {
					String queryType = request.getParameter("queryType");
					String queryValue = request.getParameter("queryValue");
					String nameKey = request.getParameter("userName");
					if (isRootQuery) {
						for (V3xOrgAccount account : accountList) {
							if (isShowOffline) {
								List<V3xOrgMember> members = this.orgManager.getAllMembers(account.getOrgAccountId());
								for (V3xOrgMember member : members) {
									if ((isOnLine(member, currentAccountId))
											&& (member.getName().indexOf(nameKey) != -1)) {
										offlineUserList.add(new OffLineUserModel(member,
												this.orgManager.getPostById(member.getOrgPostId()),
												this.orgManager.getDepartmentById(member.getOrgDepartmentId())));
									}
								}
							}
							List<OnlineUser> onlineUserSet = this.onlineManager
									.getOnlineList(account.getOrgAccountId());
							if (CollectionUtils.isNotEmpty(onlineUserSet)) {
								for (OnlineUser u1 : onlineUserSet) {
									if (u1.getName().indexOf(nameKey) != -1) {
										onlineUserList.add(new OnlineUserModel(u1));
									}
								}
							}
						}
						mav.addObject("isRootQuery", Boolean.valueOf(true));
					} else {
						onlineUserModelSet = new ArrayList();
						List<V3xOrgMember> members = new ArrayList();
						if ("team".equals(queryType)) {
							V3xOrgTeam team = this.orgManager.getTeamById(Long.valueOf(NumberUtils.toLong(queryValue)));
							if (team != null) {
								memberIds = new UniqueList();
								memberIds.addAll(team.getAllMembers());
								memberIds.addAll(team.getAllRelatives());
								for (Long memberId : memberIds) {
									V3xOrgMember member = this.orgManager.getMemberById(memberId);
									if ((member != null) && (member.isValid())) {
										members.add(member);
										OnlineUser onlineUser1 = this.onlineManager.isOnlineUser(member.getLoginName());
										if (onlineUser1 != null) {
											onlineUserModelSet.add(new OnlineUserModel(onlineUser1));
										}
									}
								}
							}
						} else if ("department".equals(queryType)) {
							V3xOrgDepartment dept = this.orgManager
									.getDepartmentById(Long.valueOf(NumberUtils.toLong(queryValue)));
							members = this.orgManager.getMembersByDepartment(dept.getId(), false);

							onlineUserModelSet = getOnlineUserListByDepartment(
									Long.valueOf(NumberUtils.toLong(queryValue)), user, internalRight);
						} else if ("account".equals(queryType)) {
							members = this.orgManager.getAllMembers(Long.valueOf(NumberUtils.toLong(queryValue)));

							onlineUserModelSet = getOnlineUserListByAccount(
									Long.valueOf(NumberUtils.toLong(queryValue)), user, internalRight);
						} else {
							members = this.orgManager.getAllMembers(currentAccountId);

							onlineUserModelSet = getOnlineUserListByAccount(currentAccountId, user, internalRight);
						}
						for (V3xOrgMember member : members) {
							if ((isOnLine(member, currentAccountId)) && (member.getName().indexOf(nameKey) != -1)) {
								offlineUserList.add(
										new OffLineUserModel(member, this.orgManager.getPostById(member.getOrgPostId()),
												this.orgManager.getDepartmentById(member.getOrgDepartmentId())));
							}
						}
						if (CollectionUtils.isNotEmpty(onlineUserModelSet)) {
							for ( Iterator<OnlineUserModel>memberIds1 = onlineUserModelSet.iterator(); memberIds1.hasNext();) {
								OnlineUserModel u1 = (OnlineUserModel) memberIds1.next();
								if (u1.getName().indexOf(nameKey) != -1) {
									onlineUserList.add(u1);
								}
							}
						}
					}
					mav.addObject("queryType", queryType).addObject("queryValue", queryValue).addObject("userName",
							nameKey);
				}
			}
		}
		boolean canMoveToOffline = false;
		if ((user.isSystemAdmin()) || (user.isGroupAdmin())
				|| ((user.isAdministrator()) && (user.getLoginAccount().equals(currentAccountId)))) {
			canMoveToOffline = true;
		}
		List<OffLineUserModel> newOfflineUserList = new ArrayList();
		for (OffLineUserModel offLineUserModel : offlineUserList) {
			if (Functions.checkLevelScope(user.getId(), offLineUserModel.getId())) {
				newOfflineUserList.add(offLineUserModel);
			}
		}
		mav.addObject("offlineUserList", newOfflineUserList);
		if ((user.isInternal()) && (!user.isAdmin())) {
			List<V3xOrgMember> memberWorkScopeForExternal = this.orgManager.getMemberWorkScopeForExternal(user.getId(),
					false);
			Set<Long> memberWorkScopeForExternalSet = new HashSet(memberWorkScopeForExternal.size());
			for (V3xOrgMember v3xOrgMember : memberWorkScopeForExternal) {
				memberWorkScopeForExternalSet.add(v3xOrgMember.getId());
			}
			for (int j = 0; j < onlineUserList.size(); j++) {
				OnlineUserModel onlineUser = (OnlineUserModel) onlineUserList.get(j);
				if ((!onlineUser.isInternal()) && (!memberWorkScopeForExternalSet.contains(onlineUser.getId()))) {
					onlineUserList.remove(j);
					j--;
				}
			}
			onlineUserList = checkWorkScope(onlineUserList, user);
		}
		mav.addObject("canMoveToOffline", Boolean.valueOf(canMoveToOffline));
		mav.addObject("isShowMemberMenu", Boolean.valueOf(!isAdmin));

		mav.addObject("currentAccountId", currentAccountId);
		mav.addObject("onlineUserList", onlineUserList);
		boolean isShowAccountSwitch = ((Boolean) SysFlag.frontPage_online_showAccountSwitch.getFlag()).booleanValue();
		mav.addObject("isShowAccountShortName", Boolean.valueOf(("ByName".equals(condition)) && (isShowAccountSwitch)));
		return mav;
	}

	private boolean isOnLine(V3xOrgMember m, Long currentAccountId) {
		boolean isOnLine = this.onlineManager.isOnline(m.getLoginName());
		OnlineUser onlineUser = this.onlineManager.isOnlineUser(m.getLoginName());
		return (!isOnLine) || ((isOnLine) && (m.getIsInternal().booleanValue()) && (onlineUser != null)
				&& (!onlineUser.getCurrentAccountId().equals(currentAccountId)));
	}

	private Long getCurrentAccountId(HttpServletRequest request, User user, List<V3xOrgAccount> accountList)
			throws Exception {
		Long currentAccountId = user.getLoginAccount();
		String accountIdStr = request.getParameter("currentAccountId");
		if (Strings.isNotBlank(accountIdStr)) {
			currentAccountId = Long.valueOf(NumberUtils.toLong(accountIdStr));
		} else if ((user.isSystemAdmin()) || (user.isGroupAdmin()) || (user.isAuditAdmin())) {
			V3xOrgAccount rootAccount = this.orgManager.getRootAccount();
			for (V3xOrgAccount account : accountList) {
				if ((!account.isGroup()) && (account.getSuperior().equals(rootAccount.getId()))) {
					currentAccountId = account.getId();
					break;
				}
			}
		}
		return currentAccountId;
	}

	private List<OnlineUserModel> getOnlineUserListByAccount(Long currentAccountId, User user,
			List<V3xOrgEntity> internalRight) throws Exception {
		List<OnlineUserModel> onlineUserList = new ArrayList();
		List<OnlineUser> onlineUserSet = this.onlineManager.getOnlineList(currentAccountId);
		if ((onlineUserSet != null) && (!onlineUserSet.isEmpty())) {
			for (OnlineUser u : onlineUserSet) {
				if (u.getAccoutId().equals(currentAccountId)) {
					if (canDisplay(user, u, internalRight)) {
						onlineUserList.add(new OnlineUserModel(u));
					}
				} else {
					OnlineUser.SecondePost secondPost = u.getSecondePost(currentAccountId, "");
					if (secondPost != null) {
						OnlineUserModel m = new OnlineUserModel(u);
						m.setDepartmentName(secondPost.getDepartmentSimpleName());
						m.setPostName(secondPost.getPostName());
						m.setPluralist(true);
						if (canDisplay(user, u, internalRight)) {
							onlineUserList.add(m);
						}
					} else if ((u.getInternalId() == user.getId())
							|| (u.getCurrentAccountId().longValue() == currentAccountId.longValue())) {
						OnlineUserModel m = new OnlineUserModel(u);
						m.setDepartmentName(null);
						m.setPostName(null);
						m.setPluralist(true);
						onlineUserList.add(m);
					} else if ((!user.isInternal()) && (canDisplay(user, u, internalRight))) {
						OnlineUserModel m = new OnlineUserModel(u);
						onlineUserList.add(m);
					}
				}
			}
		}
		return onlineUserList;
	}

	private List<OnlineUserModel> getOnlineUserListByDepartment(Long departmentId, User user,
			List<V3xOrgEntity> internalRight) throws Exception {
		List<OnlineUserModel> onlineUserList = new ArrayList();
		V3xOrgDepartment department = this.orgManager.getDepartmentById(departmentId);
		if (department != null) {
			String departmentPath = department.getPath();
			Long currentAccountId = department.getOrgAccountId();
			List<OnlineUser> onlineUserSet = this.onlineManager.getOnlineList(currentAccountId);
			if ((onlineUserSet != null) && (!onlineUserSet.isEmpty())) {
				for (OnlineUser u : onlineUserSet) {
					if ((u != null) && (u.getAccoutId() != null) && (u.getDepartmentPath() != null)) {
						if ((u.getAccoutId().equals(currentAccountId))
								&& ((u.getDepartmentPath().equals(departmentPath))
										|| (u.getDepartmentPath().startsWith(departmentPath)))) {
							if (canDisplay(user, u, internalRight)) {
								onlineUserList.add(new OnlineUserModel(u));
							}
						} else {
							OnlineUser.SecondePost secondPost = u.getSecondePost(currentAccountId, departmentPath);
							if (secondPost != null) {
								OnlineUserModel m = new OnlineUserModel(u);
								m.setDepartmentName(secondPost.getDepartmentSimpleName());
								m.setPostName(secondPost.getPostName());
								m.setPluralist(true);
								if (canDisplay(user, u, internalRight)) {
									onlineUserList.add(m);
								}
							}
						}
					}
				}
			}
		}
		return onlineUserList;
	}

	private List<OnlineUserModel> checkWorkScope(List<OnlineUserModel> onlineUserList, User user) {
		for (int j = 0; j < onlineUserList.size(); j++) {
			OnlineUserModel onlineUser = (OnlineUserModel) onlineUserList.get(j);
			if (!Functions.checkLevelScope(user.getId(), onlineUser.getId())) {
				onlineUserList.remove(j);
				j--;
			}
		}
		return onlineUserList;
	}

	private boolean canDisplay(User user, OnlineUser u, List<V3xOrgEntity> internalRight) throws BusinessException {
		if ((user.isSystemAdmin()) || (user.isAuditAdmin()) || (user.isGroupAdmin()) || (user.isAdministrator())) {
			return true;
		}
		List<V3xOrgTeam> outerTeam;
		Long memberId;
		List<Long> depIds;
		if (user.isInternal()) {
			V3xOrgMember umember = this.orgManager.getMemberById(u.getInternalId());
			if (umember != null) {
				if (umember.getIsInternal().booleanValue()) {
					return true;
				}
				V3xOrgDepartment uDepartment = this.orgManager.getDepartmentById(u.getDepartmentId());
				if ((uDepartment.getPath().indexOf(".") > 0)
						&& (uDepartment.getPath().indexOf(".") == uDepartment.getPath().lastIndexOf("."))) {
					return true;
				}
				V3xOrgDepartment userDepartment = this.orgManager.getDepartmentById(user.getDepartmentId());
				if ((uDepartment.getParentPath().equals(userDepartment.getParentPath()))
						&& (uDepartment.getOrgAccountId().equals(userDepartment.getOrgAccountId()))) {
					return true;
				}
				outerTeam = null;
				if (user.getLoginAccount() == u.getAccoutId()) {
					outerTeam = this.orgManager.getTeamsByMember(user.getId(), u.getAccoutId());
				} else {
					outerTeam = this.orgManager.getTeamsByMember(user.getId(), u.getAccoutId());
					if (outerTeam != null) {
						outerTeam.addAll(this.orgManager.getTeamsByMember(user.getId(), user.getLoginAccount()));
					} else {
						outerTeam = this.orgManager.getTeamsByMember(user.getId(), user.getLoginAccount());
					}
				}
				List<Long> mems;
				if ((outerTeam != null) && (!outerTeam.isEmpty())) {
					Iterator localIterator2;
					for (Iterator localIterator1 = outerTeam.iterator(); localIterator1.hasNext(); localIterator2
							.hasNext()) {
						V3xOrgTeam team = (V3xOrgTeam) localIterator1.next();
						mems = team.getAllMembers();
						localIterator2 = mems.iterator();
						memberId = (Long) localIterator2.next();
						if (memberId.longValue() == u.getInternalId().longValue()) {
							return true;
						}
					}
				}
				internalRight = this.orgManager.getExternalMemberWorkScope(umember.getId(), false);
				depIds = this.orgManager.getUserDomainIDs(user.getId(), V3xOrgEntity.VIRTUAL_ACCOUNT_ID,
						new String[] { "Department" });
				for (V3xOrgEntity entity : internalRight) {
					if (entity.getId().longValue() == user.getId().longValue()) {
						return true;
					}
					if (entity.getId().longValue() == user.getDepartmentId().longValue()) {
						return true;
					}
					if (entity.getId().longValue() == user.getLoginAccount().longValue()) {
						return true;
					}
					if (depIds.contains(Long.valueOf(entity.getId().longValue()))) {
						return true;
					}
				}
			}
		} else {
			if ((user.getId() == u.getInternalId()) || (u.getDepartmentId().equals(user.getDepartmentId()))) {
				return true;
			}
			V3xOrgDepartment uDepartment = this.orgManager.getDepartmentById(u.getDepartmentId());
			V3xOrgDepartment userDepartment = this.orgManager.getDepartmentById(user.getDepartmentId());
			if ((uDepartment.getParentPath().equals(userDepartment.getParentPath()))
					&& (uDepartment.getOrgAccountId().equals(userDepartment.getOrgAccountId()))) {
				return true;
			}
			if (internalRight == null) {
				internalRight = this.orgManager.getExternalMemberWorkScope(user.getId(), false);
			}
			for (V3xOrgEntity entity : internalRight) {
				if (entity.getId().longValue() == u.getInternalId().longValue()) {
					return true;
				}
				if (entity.getId().longValue() == u.getDepartmentId().longValue()) {
					return true;
				}
				if (entity.getId().longValue() == u.getAccoutId().longValue()) {
					return true;
				}
			}
			List<V3xOrgTeam> outerTeam1 = null;
			if (user.getLoginAccount() == u.getAccoutId()) {
				outerTeam1 = this.orgManager.getTeamsByMember(user.getId(), u.getAccoutId());
			} else {
				outerTeam1 = this.orgManager.getTeamsByMember(user.getId(), u.getAccoutId());
				if (outerTeam1 != null) {
					outerTeam1.addAll(this.orgManager.getTeamsByMember(user.getId(), user.getLoginAccount()));
				} else {
					outerTeam1 = this.orgManager.getTeamsByMember(user.getId(), user.getLoginAccount());
				}
			}
//			if ((outerTeam1 != null) && (!outerTeam1.isEmpty())) {
//				for ( Iterator<V3xOrgTeam>depIds1 = outerTeam.iterator(); depIds1.hasNext(); memberId.hasNext()) {
//					V3xOrgTeam team = (V3xOrgTeam) depIds.next();
//					Object mems = team.getAllMembers();
//					memberId = ((List) mems).iterator();
//					Long memberId = (Long) memberId.next();
//					if (memberId.longValue() == u.getInternalId().longValue()) {
//						return true;
//					}
//				}
//			}
		}
		return false;
	}

	public ModelAndView moveMemberToOffline(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String loginName = request.getParameter("loginName");
		String currentAccountId = request.getParameter("currentAccountId");

		OnlineRecorder.moveToOffline(loginName, LoginOfflineOperation.adminKickoff);
		return super.redirectModelAndView("/online.do?method=showOnlineUserList&currentAccountId=" + currentAccountId);
	}
}
