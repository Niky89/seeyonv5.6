package com.seeyon.apps.lawfirm.manager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.seeyon.apps.doc.enums.DocActionEnum;
import com.seeyon.apps.doc.manager.DocAclManager;
import com.seeyon.apps.doc.manager.DocActionManager;
import com.seeyon.apps.doc.manager.DocAlertManager;
import com.seeyon.apps.doc.manager.DocHierarchyManager;
import com.seeyon.apps.doc.po.DocAcl;
import com.seeyon.apps.doc.po.DocResourcePO;
import com.seeyon.apps.doc.util.Constants;
import com.seeyon.apps.doc.util.DocMVCUtils;
import com.seeyon.apps.doc.vo.PotentModel;
import com.seeyon.apps.index.manager.IndexManager;
import com.seeyon.ctp.common.AppContext;
import com.seeyon.ctp.common.constants.ApplicationCategoryEnum;
import com.seeyon.ctp.common.exceptions.BusinessException;
import com.seeyon.ctp.common.i18n.ResourceUtil;
import com.seeyon.ctp.common.operationlog.manager.OperationlogManager;
import com.seeyon.ctp.common.usermessage.MessageContent;
import com.seeyon.ctp.common.usermessage.MessageReceiver;
import com.seeyon.ctp.common.usermessage.UserMessageManager;
import com.seeyon.ctp.organization.bo.V3xOrgMember;
import com.seeyon.ctp.organization.manager.OrgManager;
import com.seeyon.ctp.util.DBAgent;

public class LawDocHelper {
	private static final Log log = LogFactory.getLog(LawDocHelper.class);
/**
 * 
 * @param docResId 资源id
 * @param docLibType 类型
 * @param username 授权用户列表
 * @param zblsCount 主板律师人数
 * @param isReadOnly 是否只读
 * @throws BusinessException 
 */
	public static synchronized void addLawGrant(Long docResId, byte docLibType, List<Long> username, int zblsCount,
			Boolean isReadOnly) throws BusinessException {
		DocHierarchyManager docHierarchyManager = (DocHierarchyManager) AppContext.getBean("docHierarchyManager");
		DocAclManager docAclManager = (DocAclManager) AppContext.getBean("docAclManager");
		DocActionManager docActionManager = (DocActionManager) AppContext.getBean("docActionManager");
		DocAlertManager docAlertManager = (DocAlertManager) AppContext.getBean("docAlertManager");
		IndexManager indexManager = (IndexManager) AppContext.getBean("indexManager");
		OperationlogManager operationlogManager = (OperationlogManager) AppContext.getBean("operationlogManager");
		UserMessageManager userMessageManager = (UserMessageManager) AppContext.getBean("userMessageManager");

		DocResourcePO oprDr = docHierarchyManager.getDocResourceById(docResId);

		DocResourcePO dr = docHierarchyManager.getDocResourceById(docResId);
		List<Long> owners = DocMVCUtils.getLibOwners(dr);
		boolean isGroupRes = docLibType == Constants.GROUP_LIB_TYPE.byteValue();
		Long userId = Long.valueOf(AppContext.currentUserId());

		List<Long> userIds = new ArrayList();
		List<String> userTypes = new ArrayList();
		Map<Long, String> newMap;
		if (username != null) {
			int len = username.size();
			List<String> rows = new ArrayList();
			newMap = new HashMap();
			String utype;
			for (int i = 0; i < len; i++) {
				String[] uids = { ((Long) username.get(i)).toString() };
				if ((uids == null) && (i < len)) {
					len++;
				} else {
					String[] utypes = { "Member" };
					String uid = uids[(uids.length - 1)];
					utype = utypes[(utypes.length - 1)];
					if ((uid != null) || (i >= len)) {
						userIds.add(Long.valueOf(uid));
						userTypes.add(utype);
						String row;
						if (isReadOnly.booleanValue()) {
							row = uid + "," + utype + "," + docResId + ","
									+ "false,false,false,true,true,true,false,false";
						} else {
							if (i == 0) {
								row = uid + "," + utype + "," + docResId + ","
										+ "false,false,false,false,false,false,true,false";
							} else {
								if (i <= zblsCount) {
									row = uid + "," + utype + "," + docResId + ","
											+ "false,false,true,true,true,true,false,false";
								} else {
									row = uid + "," + utype + "," + docResId + ","
											+ "false,false,true,true,true,true,false,false";
								}
							}
						}
						rows.add(row);
						newMap.put(Long.valueOf(Long.parseLong(uid)), row);
					}
				}
			}
			List<PotentModel> objs = new ArrayList();

			List<DocAcl> l = docAclManager.getDocAclListByInherit(docResId);
			Set<Long> set = new HashSet();
			PotentModel p;
			if (CollectionUtils.isNotEmpty(l)) {
				for (DocAcl a : l) {
					set.add(Long.valueOf(a.getUserId()));
				}
				for (Long userid : set) {
					p = new PotentModel();
					p.setUserId(userid);
					if (owners.contains(userid)) {
						p.setIsLibOwner(true);
					}
					for (DocAcl a : l) {
						if (a.getUserId() == userid.longValue()) {
							if (p.getUserName() == null) {
								String userName = Constants.getOrgEntityName(a.getUserType(), a.getUserId(),
										isGroupRes);
								p.setUserName(userName);
								p.setUserType(a.getUserType());
							}
							p.copyAcl(a);
						}
					}
					if (!docAclManager.isNoInherit(p.getUserId(), p.getUserType(), docResId)) {
						p.setInherit(true);
						p.setAlert(false);
						p.setAlertId(Long.valueOf(0L));
						objs.add(p);
					}
				}
			}
			List<List<DocAcl>> l2 = docAclManager.getDocAclListByNew(docResId);
			boolean flag;
			for (List<DocAcl> l3 : l2) {
				p = null;
				flag = false;
				if ((objs != null) && (objs.size() > 0)) {
					for (PotentModel pm : objs) {
						for (Iterator localIterator4 = l3.iterator(); localIterator4.hasNext();) {
							DocAcl temp = (DocAcl) localIterator4.next();
							if (temp.getUserId() == pm.getUserId().longValue()) {
								flag = true;
								p = pm;
								break;
							}
						}
						if (flag) {
							break;
						}
					}
				}
				if (!flag) {
					p = new PotentModel();
				} else {
					p.setAllAcl(false);
				}
				boolean isAlert = false;
				long alertId = 0L;
				for (DocAcl acl2 : l3) {
					if (p.getUserId() == null) {
						p.setUserId(Long.valueOf(acl2.getUserId()));
						p.setUserType(acl2.getUserType());
						String userName = Constants.getOrgEntityName(acl2.getUserType(), acl2.getUserId(), isGroupRes);
						p.setUserName(userName);
					}
					p.copyAcl(acl2);
					isAlert = acl2.getIsAlert();
					if (isAlert) {
						alertId = acl2.getDocAlertId().longValue();
					}
				}
				p.setInherit(false);
				p.setAlert(isAlert);
				p.setAlertId(Long.valueOf(alertId));
				if (!flag) {
					objs.add(p);
				}
			}
			List<String> oldlist = new ArrayList();
			Map<Long, String> oldMap = new HashMap();
			for (PotentModel pmm : objs) {
				String dp = pmm.descPotent(docResId);
				oldlist.add(dp);
				oldMap.put(pmm.getUserId(), dp);
			}
			Set<Long> keySet = oldMap.keySet();
			int minOrder = docAclManager.getMaxOrder();
			for (Iterator temp = keySet.iterator(); temp.hasNext();) {
				long userIdmap = ((Long) temp.next()).longValue();
				if (!owners.contains(Long.valueOf(userIdmap))) {
					Long newalertId;
					if (newMap.containsKey(Long.valueOf(userIdmap))) {
						String newStr = (String) newMap.get(Long.valueOf(userIdmap));
						String oldStr = (String) oldMap.get(Long.valueOf(userIdmap));
						String[] newarr = newStr.split(",");

						boolean isInherit = Boolean.parseBoolean(newarr[9]);

						boolean isAlert = Boolean.parseBoolean(newarr[10]);
						if ((!newStr.equals(oldStr)) || (isInherit)) {
							docAlertManager.deleteAlertByDocResourceIdAndOrgByAcl(docResId.longValue(), newarr[1],
									Long.parseLong(newarr[0]));
							newalertId = null;
							if (isAlert) {
								newalertId = Long.valueOf(docAlertManager.addAlert(docResId, true, (byte) 0, newarr[1],
										Long.valueOf(newarr[0]), userId, true, false, true));
							}
							boolean haspotent = false;
							if (!isInherit) {
								docAclManager.deletePotentByUser(Long.valueOf(Long.parseLong(newarr[2])),
										Long.valueOf(Long.parseLong(newarr[0])), newarr[1], docLibType,
										dr.getDocLibId());
							}
							for (int i = 3; i < 9; i++) {
								int potent = getPotentType(i);
								if (newarr[i].equals("true")) {
									haspotent = true;
									docAclManager.setDeptSharePotent(Long.valueOf(Long.parseLong(newarr[0])), newarr[1],
											Long.valueOf(Long.parseLong(newarr[2])), potent, isAlert, newalertId,
											minOrder++);
								} else if (newarr[9].equals("false")) {
									docAclManager.deletePotentByUser(Long.valueOf(Long.parseLong(newarr[2])),
											Long.valueOf(Long.parseLong(newarr[0])), newarr[1], potent);
								}
							}
							if (!haspotent) {
								if (isAlert) {
									docAlertManager.deleteAlertById(newalertId);
								}
								docAclManager.deletePotentByUser(Long.valueOf(Long.parseLong(newarr[2])),
										Long.valueOf(Long.parseLong(newarr[0])), newarr[1], docLibType,
										dr.getDocLibId());

								docAclManager.setDeptSharePotent(Long.valueOf(Long.parseLong(newarr[0])), newarr[1],
										Long.valueOf(Long.parseLong(newarr[2])), 9, false, null, minOrder++);
							}
						}
					} else {
						String oldStr = (String) oldMap.get(Long.valueOf(userIdmap));
						String[] oldarr = oldStr.split(",");
						docAlertManager.deleteAlertByDocResourceIdAndOrgByAcl(Long.parseLong(oldarr[2]), oldarr[1],
								Long.parseLong(oldarr[0]));

						DocResourcePO currenDr = docHierarchyManager
								.getDocResourceById(Long.valueOf(Long.parseLong(oldarr[2])));
						List<DocResourcePO> list = docAlertManager
								.getSubFolderIds(Long.valueOf(Long.parseLong(oldarr[2])), currenDr);
						if ((list != null) && (!list.isEmpty())) {
							for (DocResourcePO item : list) {
								docAlertManager.deleteAlertByDocResourceIdAndOrgByAcl(item.getId().longValue(),
										oldarr[1], Long.parseLong(oldarr[0]));
							}
						}
						docAclManager.deletePotentByUser(Long.valueOf(Long.parseLong(oldarr[2])),
								Long.valueOf(Long.parseLong(oldarr[0])), oldarr[1], docLibType, dr.getDocLibId());
						docAclManager.setDeptSharePotent(Long.valueOf(Long.parseLong(oldarr[0])), oldarr[1],
								Long.valueOf(Long.parseLong(oldarr[2])), 9, false, null, minOrder++);
					}
				}
			}
			Set<Long> receiverIds = new HashSet();
			Object newkeySet = newMap.keySet();
			for (Object oldarr = ((Set) newkeySet).iterator(); ((Iterator) oldarr).hasNext();) {
				long userIdnew = ((Long) ((Iterator) oldarr).next()).longValue();
				if (!owners.contains(Long.valueOf(userIdnew))) {
					if (!oldMap.containsKey(Long.valueOf(userIdnew))) {
						String newStr = (String) newMap.get(Long.valueOf(userIdnew));
						String[] newarr = newStr.split(",");

						boolean isAlert = Boolean.parseBoolean(newarr[10]);
						Long newalertId = null;
						if (isAlert) {
							newalertId = Long.valueOf(docAlertManager.addAlert(docResId, true, (byte) 0, newarr[1],
									Long.valueOf(newarr[0]), userId, true, false, true));
						}
						boolean haspotent = false;
						for (int i = 3; i < 9; i++) {
							int potent = getPotentType(i);
							if (newarr[i].equals("true")) {
								haspotent = true;
								docAclManager.setDeptSharePotent(Long.valueOf(Long.parseLong(newarr[0])), newarr[1],
										Long.valueOf(Long.parseLong(newarr[2])), potent, isAlert, newalertId,
										minOrder++);
							}
						}
						if (!haspotent) {
							if (isAlert) {
								docAlertManager.deleteAlertById(newalertId);
							}
							docAclManager.setDeptSharePotent(Long.valueOf(Long.parseLong(newarr[0])), newarr[1],
									Long.valueOf(Long.parseLong(newarr[2])), 9, false, null, minOrder++);
						}
						try {
							Set<Long> docAlerts = getAlertMemberIds(String.valueOf(userIdnew), newarr[1], true,
									Byte.valueOf(docLibType));
							receiverIds.addAll(docAlerts);
						} catch (Exception e) {
							log.error("", e);
						}
					}
				}
			}
			try {
				String key = "doc.share.to.label";
				MessageContent cont = MessageContent.get(key,
						new Object[] { ResourceUtil.getString(oprDr.getFrName()), AppContext.currentUserName() });
				Object receivers = MessageReceiver.get(docResId, receiverIds, "message.link.doc.folder.open",
						com.seeyon.ctp.common.usermessage.Constants.LinkOpenType.href, new Object[] { docResId });
				userMessageManager.sendSystemMessage(cont, ApplicationCategoryEnum.doc, AppContext.currentUserId(),
						(Collection) receivers, new Object[0]);
			} catch (Exception e) {
				log.error("", e);
			}
		} else {
			docAclManager.deleteDeptShareByDoc(docResId);
			docAlertManager.deleteShareAlertByDocResourceId(docResId);
		}
		operationlogManager.insertOplog(docResId, Long.valueOf(oprDr.getParentFrId()), ApplicationCategoryEnum.doc,
				"log.doc.share", "log.doc.share.desc",
				new Object[] { AppContext.currentUserName(), ResourceUtil.getString(oprDr.getFrName()) });

		List<DocResourcePO> list = docHierarchyManager.getDocsInFolderByType(docResId.longValue(), "21");
		if (AppContext.hasPlugin("index")) {
			for (DocResourcePO d : list) {
				indexManager.update(d.getId(), Integer.valueOf(ApplicationCategoryEnum.doc.getKey()));

				docHierarchyManager.updateLink(d.getId());
			}
		}
		docActionManager.insertDocAction(userId, Long.valueOf(AppContext.currentAccountId()), new Date(),
				Integer.valueOf(DocActionEnum.share.key()), docResId, "common share", userIds, userTypes);
	}

	private static Set<Long> getAlertMemberIds(String id, String usertype, boolean isPersonLib, Byte docLibType)
			throws BusinessException {
		OrgManager orgManager = (OrgManager) AppContext.getBean("orgManager");

		Set<Long> memberIds = new HashSet();
		if ("Member".equals(usertype)) {
			if (Long.parseLong(id) != AppContext.currentUserId()) {
				memberIds.add(Long.valueOf(Long.parseLong(id)));
			}
		} else {
			String idAndType = usertype + "|" + id;
			Set<V3xOrgMember> membersSet = orgManager.getMembersByTypeAndIds(idAndType);
			for (V3xOrgMember om : membersSet) {
				if ((docLibType == null) || (docLibType.byteValue() != Constants.EDOC_LIB_TYPE.byteValue())
						|| (om.getIsInternal().booleanValue())) {
					if (om.getId().longValue() != AppContext.currentUserId()) {
						memberIds.add(om.getId());
					}
				}
			}
		}
		if (isPersonLib) {
			memberIds.remove(Long.valueOf(AppContext.currentUserId()));
		}
		return memberIds;
	}

	private static int getPotentType(int i) {
		int potent = 0;
		switch (i) {
		case 3:
			potent = 0;
			break;
		case 4:
			potent = 1;
			break;
		case 5:
			potent = 7;
			break;
		case 6:
			potent = 2;
			break;
		case 7:
			potent = 4;
			break;
		case 8:
			potent = 3;
		}
		return potent;
	}

	public static Long findFolderByFullPath(String fullPath, String appName, String flag, String spaceType,
			String pigeonholeType, String validAcl, String id, String docLibType) {
		try {
			DocHierarchyManager docHierarchyManager = (DocHierarchyManager) AppContext.getBean("docHierarchyManager");

			Long fid = Long.valueOf(-1L);
			String[] paths = fullPath.split("\\\\");

			OrgManager orgManager = (OrgManager) AppContext.getBean("orgManager");
			String hqlDocLib;
			if (fullPath.startsWith("集团文档")) {
				hqlDocLib = "SELECT d.id FROM DocLibPO d WHERE d.type=5 AND d.name='" + paths[0] + "' AND d.domainId=0";
			} else {
				Long domainId = AppContext.getCurrentUser().getAccountId();
				hqlDocLib = "SELECT d.id FROM DocLibPO d WHERE d.type=0 AND d.name='" + paths[0] + "' AND d.domainId="
						+ domainId;
			}
			long docLibId = 0L;

			log.info("ColFinished.....00022 ");
			List<Long> lstDocId = DBAgent.find(hqlDocLib);
			log.info("ColFinished.....00023 ");
			if ((lstDocId != null) && (!lstDocId.isEmpty())) {
				docLibId = ((Long) lstDocId.get(0)).longValue();
			}
			log.info("ColFinished.....00024");
			String hqlResource;
			if (fullPath.startsWith("集团文档")) {
				hqlResource = "SELECT docR.id from DocResourcePO  docR where docR.docLibId = " + docLibId
						+ " and docR.frType = 47 and  docR.frName='" + paths[0] + "'";
			} else {
				hqlResource = "SELECT docR.id from DocResourcePO  docR where docR.docLibId = " + docLibId
						+ " and docR.frType = 31 and  docR.frName='" + paths[0] + "'";
			}
			log.info("ColFinished.....00025 " + hqlResource);
			List<Long> lstparentId = DBAgent.find(hqlResource);
			log.info("ColFinished.....00026");
			if ((lstparentId != null) && (!lstparentId.isEmpty())) {
				fid = (Long) lstparentId.get(0);
			}
			int findLevel = 1;
			Boolean found = Boolean.valueOf(false);
			while (findLevel < paths.length) {
				String folderToFind = paths[findLevel];

				List<DocResourcePO> folders = docHierarchyManager.findFoldersWithOutAcl(fid);

				for (DocResourcePO dpo : folders) {
					String frName = dpo.getFrName();
					if (frName != null) {
						if ((fullPath.startsWith("案件文档\\")) && (findLevel == 1)) {
							if (frName.startsWith(folderToFind)) {
								fid = dpo.getId();
								found = Boolean.valueOf(true);
								break;
							}
						} else if (frName.equals(folderToFind)) {
							fid = dpo.getId();
							found = Boolean.valueOf(true);
							break;

						}
					}
				}
				if (found.booleanValue()) {
					findLevel++;
				} else {
					return Long.valueOf(-1L);
				}
			}

			return fid;
		} catch (Exception ex) {
			log.error("", ex);
		}
		return Long.valueOf(-1L);
	}
}
