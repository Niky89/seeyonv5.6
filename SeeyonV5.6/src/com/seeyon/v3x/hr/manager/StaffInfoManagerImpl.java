package com.seeyon.v3x.hr.manager;

import com.seeyon.ctp.common.cache.CacheAccessable;
import com.seeyon.ctp.common.cache.CacheFactory;
import com.seeyon.ctp.common.cache.CacheMap;
import com.seeyon.ctp.common.constants.ApplicationCategoryEnum;
import com.seeyon.ctp.common.filemanager.manager.AttachmentManager;
import com.seeyon.ctp.common.filemanager.manager.FileManager;
import com.seeyon.ctp.common.po.filemanager.V3XFile;
import com.seeyon.ctp.organization.bo.V3xOrgMember;
import com.seeyon.ctp.util.Strings;
import com.seeyon.v3x.hr.dao.StaffInfoDao;
import com.seeyon.v3x.hr.domain.Assess;
import com.seeyon.v3x.hr.domain.ContactInfo;
import com.seeyon.v3x.hr.domain.EduExperience;
import com.seeyon.v3x.hr.domain.PostChange;
import com.seeyon.v3x.hr.domain.Relationship;
import com.seeyon.v3x.hr.domain.RewardsAndPunishment;
import com.seeyon.v3x.hr.domain.StaffInfo;
import com.seeyon.v3x.hr.domain.WorkRecord;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class StaffInfoManagerImpl
  implements StaffInfoManager
{
  private static final transient Log LOG = LogFactory.getLog(StaffInfoManagerImpl.class);
  private StaffInfoDao staffInfoDao;
  private FileManager fileManager;
  private AttachmentManager attachmentManager;
  private CacheMap<Long, StaffInfo> StaffInfoByMemberIdCache = null;
  
  public void setStaffInfoDao(StaffInfoDao staffInfoDao)
  {
    this.staffInfoDao = staffInfoDao;
  }
  
  public void setFileManager(FileManager fileManager)
  {
    this.fileManager = fileManager;
  }
  
  public void setAttachmentManager(AttachmentManager attachmentManager)
  {
    this.attachmentManager = attachmentManager;
  }
  
  public void init()
  {
    long start = System.currentTimeMillis();
    CacheAccessable factory = CacheFactory.getInstance(StaffInfoManager.class);
    this.StaffInfoByMemberIdCache = factory.createMap("StaffInfoByMemberIdCache");
    
    List<StaffInfo> staffInfos = this.staffInfoDao.getValidStaffInfos();
    if (CollectionUtils.isNotEmpty(staffInfos))
    {
      for (StaffInfo staffInfo : staffInfos) {
        this.StaffInfoByMemberIdCache.put(staffInfo.getOrg_member_id(), staffInfo);
      }
      LOG.info("职员信息加载完成，耗时：" + (System.currentTimeMillis() - start) + "MS");
    }
  }
  
  public Map<Long, StaffInfo> getStaffInfos(List<Long> staffidList)
    throws Exception
  {
    return this.staffInfoDao.getStaffInfos(staffidList);
  }
  
  public StaffInfo getStaffInfoById(Long memberId)
    throws Exception
  {
    return (StaffInfo)this.StaffInfoByMemberIdCache.get(memberId);
  }
  
  public StaffInfo getStaffInfoByIdFromDB(Long memberId)
    throws Exception
  {
    return this.staffInfoDao.getStaffInfoByMemberId(memberId.longValue());
  }
  
  public void addStaffInfo(StaffInfo staffinfo)
    throws Exception
  {
    staffinfo.setIdIfNew();
    this.staffInfoDao.save(staffinfo);
    this.StaffInfoByMemberIdCache.put(staffinfo.getOrg_member_id(), staffinfo);
  }
  
  public void addStaffInfo(HttpServletRequest request, StaffInfo staffinfo)
    throws Exception
  {
    try
    {
      this.attachmentManager.create(ApplicationCategoryEnum.hr, staffinfo.getId(), staffinfo.getId(), request);
      String imageId = request.getParameter("imageId");
      if (Strings.isNotBlank(imageId))
      {
        V3XFile imageFile = this.fileManager.getV3XFile(Long.valueOf(imageId));
        if (imageFile != null)
        {
          staffinfo.setImage_id(imageFile.getId());
          staffinfo.setImage_datetime(imageFile.getCreateDate());
          staffinfo.setImage_name(imageFile.getFilename());
        }
      }
      addStaffInfo(staffinfo);
    }
    catch (Exception e)
    {
      LOG.error("", e);
    }
  }
  
  public void addStaffInfo(HttpServletRequest request, StaffInfo staffinfo, V3xOrgMember member)
    throws Exception
  {
    addStaffInfo(request, staffinfo);
  }
  
  public void updateStaffInfo(StaffInfo staffinfo)
    throws Exception
  {
    List<StaffInfo> l = new ArrayList(1);
    l.add(staffinfo);
    this.staffInfoDao.updatePatchAll(l);
    this.StaffInfoByMemberIdCache.put(staffinfo.getOrg_member_id(), staffinfo);
  }
  
  public void updateStaffInfo(HttpServletRequest request, StaffInfo staffinfo)
    throws Exception
  {
    try
    {
      this.attachmentManager.deleteByReference(staffinfo.getId(), staffinfo.getId());
      this.attachmentManager.create(ApplicationCategoryEnum.hr, staffinfo.getId(), staffinfo.getId(), request);
      String imageId = request.getParameter("imageId");
      if (Strings.isNotBlank(imageId))
      {
        Long imageIdL = Long.valueOf(imageId);
        V3XFile imageFile = this.fileManager.getV3XFile(imageIdL);
        if (imageFile != null)
        {
          staffinfo.setImage_name(imageFile.getFilename());
          staffinfo.setImage_id(imageFile.getId());
          staffinfo.setImage_datetime(imageFile.getCreateDate());
        }
      }
      updateStaffInfo(staffinfo);
    }
    catch (Exception e)
    {
      LOG.error("", e);
    }
  }
  
  public void updateStaffInfo(HttpServletRequest request, StaffInfo staffinfo, V3xOrgMember member, boolean isNewStaffer)
    throws Exception
  {
    if (isNewStaffer) {
      addStaffInfo(request, staffinfo);
    } else {
      updateStaffInfo(request, staffinfo);
    }
  }
  
  public void deleteStaffInfo(Long staffid)
    throws Exception
  {
    long staffId = staffid.longValue();
    StaffInfo staffinfo = getStaffInfoById(staffid);
    if (staffinfo != null)
    {
      Long imageId = staffinfo.getImage_id();
      if (imageId != null) {
        this.fileManager.deleteFile(imageId, Boolean.valueOf(true));
      }
      this.attachmentManager.deleteByReference(staffinfo.getId());
      this.StaffInfoByMemberIdCache.remove(staffinfo.getOrg_member_id());
    }
    this.staffInfoDao.deleteStaffInfo(staffId);
    this.staffInfoDao.deleteContactInfoByStaffId(staffid.longValue());
    this.staffInfoDao.deleteRelationshipByStaffId(staffId);
    this.staffInfoDao.deleteWorkRecordByStaffId(staffId);
    this.staffInfoDao.deleteEduExperienceByStaffId(staffId);
    this.staffInfoDao.deletePostChangeByStaffId(staffId);
    this.staffInfoDao.deleteAssessByStaffId(staffId);
    this.staffInfoDao.deleteRewardsAndPunishmentByStaffId(staffId);
  }
  
  public ContactInfo getContactInfoById(Long staffid)
    throws Exception
  {
    return this.staffInfoDao.getContactInfoByStafferId(staffid.longValue());
  }
  
  public Map<Long, ContactInfo> getAllContactInfo()
    throws Exception
  {
    return this.staffInfoDao.getAllContactInfo();
  }
  
  public List<Relationship> getRelationshipByStafferId(Long staffid)
    throws Exception
  {
    return this.staffInfoDao.getRelationshipByStafferId(staffid.longValue());
  }
  
  public List<WorkRecord> getWorkRecordByStafferId(Long staffid)
    throws Exception
  {
    return this.staffInfoDao.getWorkRecordByStafferId(staffid.longValue());
  }
  
  public List<EduExperience> getEduExperienceByStafferId(Long staffid)
    throws Exception
  {
    return this.staffInfoDao.getEduExperienceByStafferId(staffid.longValue());
  }
  
  public List<PostChange> getPostChangeByStafferId(Long staffid)
    throws Exception
  {
    return this.staffInfoDao.getPostChangeByStafferId(staffid.longValue());
  }
  
  public List<Assess> getAssessByStafferId(Long staffid)
    throws Exception
  {
    return this.staffInfoDao.getAssessByStafferId(staffid.longValue());
  }
  
  public List<RewardsAndPunishment> getRewardsAndPunishmentByStafferId(Long staffid)
    throws Exception
  {
    return this.staffInfoDao.getRewardsAndPunishmentByStafferId(staffid.longValue());
  }
  
  public Relationship getRelationshipById(Long id)
    throws Exception
  {
    return this.staffInfoDao.getRelationshipById(id.longValue());
  }
  
  public WorkRecord getWorkRecordById(Long id)
    throws Exception
  {
    return this.staffInfoDao.getWorkRecordById(id.longValue());
  }
  
  public EduExperience getEduExperienceById(Long id)
    throws Exception
  {
    return this.staffInfoDao.getEduExperienceById(id.longValue());
  }
  
  public PostChange getPostChangeById(Long id)
    throws Exception
  {
    return this.staffInfoDao.getPostChangeById(id.longValue());
  }
  
  public Assess getAssessById(Long id)
    throws Exception
  {
    return this.staffInfoDao.getAssessById(id.longValue());
  }
  
  public RewardsAndPunishment getRewardsAndPunishmentById(Long id)
    throws Exception
  {
    return this.staffInfoDao.getRewardsAndPunishmentById(id.longValue());
  }
  
  public void updateContactInfo(ContactInfo contactInfo, V3xOrgMember member)
    throws Exception
  {
    this.staffInfoDao.update(contactInfo);
  }
  
  public void addContactInfo(ContactInfo contactInfo, V3xOrgMember member)
    throws Exception
  {
    contactInfo.setIdIfNew();
    this.staffInfoDao.save(contactInfo);
  }
  
  public void updateRelationship(Relationship relationship)
    throws Exception
  {
    this.staffInfoDao.update(relationship);
  }
  
  public void addRelationship(Relationship relationship)
    throws Exception
  {
    relationship.setIdIfNew();
    this.staffInfoDao.save(relationship);
  }
  
  public void updateWorkRecord(WorkRecord workRecord)
    throws Exception
  {
    this.staffInfoDao.update(workRecord);
  }
  
  public void addWorkRecord(WorkRecord workRecord)
    throws Exception
  {
    workRecord.setIdIfNew();
    this.staffInfoDao.save(workRecord);
  }
  
  public void updateEduExperience(EduExperience eduExperience)
    throws Exception
  {
    this.staffInfoDao.update(eduExperience);
  }
  
  public void addEduExperience(EduExperience eduExperience)
    throws Exception
  {
    eduExperience.setIdIfNew();
    this.staffInfoDao.save(eduExperience);
  }
  
  public void updatePostChange(PostChange postChange)
    throws Exception
  {
    this.staffInfoDao.update(postChange);
  }
  
  public void addPostChange(PostChange postChange)
    throws Exception
  {
    postChange.setIdIfNew();
    this.staffInfoDao.save(postChange);
  }
  
  public void updateAssess(Assess assess)
    throws Exception
  {
    this.staffInfoDao.update(assess);
  }
  
  public void addAssess(Assess assess)
    throws Exception
  {
    assess.setIdIfNew();
    this.staffInfoDao.save(assess);
  }
  
  public void updateRewardsAndPunishment(RewardsAndPunishment rewardsAndPunishment)
    throws Exception
  {
    this.staffInfoDao.update(rewardsAndPunishment);
  }
  
  public void addRewardsAndPunishment(RewardsAndPunishment rewardsAndPunishment)
    throws Exception
  {
    rewardsAndPunishment.setIdIfNew();
    this.staffInfoDao.save(rewardsAndPunishment);
  }
  
  public void deleteRelationship(Long id)
    throws Exception
  {
    this.staffInfoDao.deleteRelationship(id.longValue());
  }
  
  public void deleteWorkRecord(Long id)
    throws Exception
  {
    this.staffInfoDao.deleteWorkRecord(id.longValue());
  }
  
  public void deleteEduExperience(Long id)
    throws Exception
  {
    this.staffInfoDao.deleteEduExperience(id.longValue());
  }
  
  public void deletePostChange(Long id)
    throws Exception
  {
    this.staffInfoDao.deletePostChange(id.longValue());
  }
  
  public void deleteAssess(Long id)
    throws Exception
  {
    this.staffInfoDao.deleteAssess(id.longValue());
  }
  
  public void deleteRewardsAndPunishment(Long id)
    throws Exception
  {
    this.staffInfoDao.deleteRewardsAndPunishment(id.longValue());
  }
}
