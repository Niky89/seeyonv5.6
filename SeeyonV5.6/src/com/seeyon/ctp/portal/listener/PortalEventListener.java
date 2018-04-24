package com.seeyon.ctp.portal.listener;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;

import com.seeyon.ctp.portal.po.PortalSpaceSecurity;
import com.seeyon.ctp.portal.space.event.AddSpaceEvent;
import com.seeyon.ctp.portal.space.event.DeleteSpaceEvent;
import com.seeyon.ctp.portal.space.event.UpdateSpaceEvent;
import com.seeyon.ctp.portal.space.manager.SpaceManager;
import com.seeyon.ctp.portal.util.Constants;
import com.seeyon.ctp.util.annotation.ListenEvent;
import com.seeyon.v3x.bbs.domain.V3xBbsBoard;
import com.seeyon.v3x.bbs.manager.BbsBoardManager;
import com.seeyon.v3x.bulletin.domain.BulType;
import com.seeyon.v3x.bulletin.manager.BulTypeManager;
import com.seeyon.v3x.inquiry.domain.InquirySurveytype;
import com.seeyon.v3x.inquiry.manager.InquiryManager;
import com.seeyon.v3x.news.domain.NewsType;
import com.seeyon.v3x.news.manager.NewsTypeManager;

public class PortalEventListener {
	private BulTypeManager bulTypeManager;
	private NewsTypeManager newsTypeManager;
	private InquiryManager inquiryManager;
	private BbsBoardManager bbsBoardManager;
	private SpaceManager spaceManager;

	public NewsTypeManager getNewsTypeManager() {
		return this.newsTypeManager;
	}

	public void setNewsTypeManager(NewsTypeManager newsTypeManager) {
		this.newsTypeManager = newsTypeManager;
	}

	public InquiryManager getInquiryManager() {
		return this.inquiryManager;
	}

	public void setInquiryManager(InquiryManager inquiryManager) {
		this.inquiryManager = inquiryManager;
	}

	public BbsBoardManager getBbsBoardManager() {
		return this.bbsBoardManager;
	}

	public void setBbsBoardManager(BbsBoardManager bbsBoardManager) {
		this.bbsBoardManager = bbsBoardManager;
	}

	public SpaceManager getSpaceManager() {
		return this.spaceManager;
	}

	public void setSpaceManager(SpaceManager spaceManager) {
		this.spaceManager = spaceManager;
	}

	public BulTypeManager getBulTypeManager() {
		return this.bulTypeManager;
	}

	public void setBulTypeManager(BulTypeManager bulTypeManager) {
		this.bulTypeManager = bulTypeManager;
	}

	@ListenEvent(event = AddSpaceEvent.class)
	public void onAddSpace(AddSpaceEvent evt) throws Exception {
		int spaceType = evt.getSpaceFix().getType().intValue();
		Long spaceId = evt.getSpaceFix().getId();
		String spaceName = evt.getSpaceFix().getSpacename();
		Long entityId = evt.getSpaceFix().getEntityId();
		Long accountId = evt.getSpaceFix().getAccountId();
		BulType bulType = this.bulTypeManager.getByDeptId(spaceId);
		if (spaceType == Constants.SpaceType.custom.ordinal()) {
			if (bulType == null) {
				this.bulTypeManager.saveCustomBulType(spaceId, spaceName, spaceType);
			}
			NewsType newsType = this.newsTypeManager.getById(spaceId);
			if (newsType == null) {
				this.newsTypeManager.saveCustomNewsType(spaceId, entityId, spaceName, spaceType);
			}
			InquirySurveytype surveytype = this.inquiryManager.getSurveyTypeById(spaceId);
			if (surveytype == null) {
				this.inquiryManager.saveCustomInquirySurveytype(spaceId, spaceName, spaceType);
			}
			V3xBbsBoard board = this.bbsBoardManager.getBoardById(spaceId);
			if (board == null) {
				this.bbsBoardManager.createDepartmentBbsBoard(spaceId, entityId, spaceName, spaceType);
			}
		} else if ((spaceType == Constants.SpaceType.department.ordinal()) && (bulType == null)) {
			this.bulTypeManager.createBulTypeByDept(spaceName, entityId, accountId);
		}
	}

	@ListenEvent(event = DeleteSpaceEvent.class)
	public void onDeleteSpace(DeleteSpaceEvent evt) throws Exception {
		int spaceType = evt.getSpaceFix().getType().intValue();
		Long spaceId = evt.getSpaceFix().getId();
		if (spaceType == Constants.SpaceType.custom.ordinal()) {
			this.bbsBoardManager.deleteV3xBbsBoard(spaceId);
			InquirySurveytype surveytype = this.inquiryManager.getSurveyTypeById(spaceId);
			if (surveytype != null) {
				surveytype.setFlag(Integer.valueOf(1));
				this.inquiryManager.updateInquiryType(surveytype);
			}
		} else if ((spaceType == Constants.SpaceType.public_custom.ordinal())
				|| (spaceType == Constants.SpaceType.public_custom_group.ordinal())) {
			List<InquirySurveytype> surveyTypes = this.inquiryManager.getInquiryTypeListByUserAuth(spaceId.longValue());
			if ((surveyTypes != null) && (surveyTypes.size() > 0)) {
				for (InquirySurveytype surveyType : surveyTypes) {
					surveyType.setFlag(Integer.valueOf(1));
					this.inquiryManager.updateInquiryType(surveyType);
				}
			}
		}
	}

	@ListenEvent(event = UpdateSpaceEvent.class)
	public void onUpdateSpace(UpdateSpaceEvent evt) throws Exception {
		int spaceType = evt.getSpaceFix().getType().intValue();
		Long spaceId = evt.getSpaceFix().getId();
		String spaceName = evt.getSpaceFix().getSpacename();
		BulType bulType = this.bulTypeManager.getById(spaceId);
		if (spaceType == Constants.SpaceType.custom.ordinal()) {
			if (bulType != null) {
				bulType.setTypeName(spaceName);
				this.bulTypeManager.save(bulType);
			}
			NewsType newsType = this.newsTypeManager.getById(spaceId);
			if (newsType != null) {
				newsType.setTypeName(spaceName);
				this.newsTypeManager.save(newsType);
			}
			InquirySurveytype surveytype = this.inquiryManager.getSurveyTypeById(spaceId);
			if (surveytype != null) {
				surveytype.setTypeName(spaceName);
				this.inquiryManager.updateInquiryType(surveytype);
			}
			List<Long> admins = new ArrayList();
			List<PortalSpaceSecurity> portalList = evt.getSpaceSecurities();
			if (CollectionUtils.isNotEmpty(portalList)) {
				for (PortalSpaceSecurity list : portalList) {
					admins.add(list.getEntityId());
				}
				V3xBbsBoard board = this.bbsBoardManager.getBoardById(spaceId);
				if (board != null) {
					board.setName(spaceName);
					this.bbsBoardManager.updateV3xBbsBoard(board, admins);
				}
			}
		} else {
			Constants.SpaceType.department.ordinal();
		}
	}
}
