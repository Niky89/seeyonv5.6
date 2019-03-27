package com.seeyon.ctp.common.content.mainbody;

import java.lang.reflect.Method;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.seeyon.ctp.common.AppContext;
import com.seeyon.ctp.common.ModuleType;
import com.seeyon.ctp.common.authenticate.domain.User;
import com.seeyon.ctp.common.content.ContentConfig;
import com.seeyon.ctp.common.content.mainbody.handler.MainbodyHandler;
import com.seeyon.ctp.common.exceptions.BusinessException;
import com.seeyon.ctp.common.i18n.ResourceUtil;
import com.seeyon.ctp.common.po.content.CtpContentAll;
import com.seeyon.ctp.form.bean.FormAuthViewBean;
import com.seeyon.ctp.form.bean.FormBean;
import com.seeyon.ctp.form.bean.FormViewBean;
import com.seeyon.ctp.form.service.FormCacheManager;
import com.seeyon.ctp.form.upgrade.UpgradeUtil;
import com.seeyon.ctp.form.util.StringUtils;
import com.seeyon.ctp.util.DBAgent;
import com.seeyon.ctp.util.DateUtil;
import com.seeyon.ctp.util.FlipInfo;
import com.seeyon.ctp.util.JDBCAgent;
import com.seeyon.ctp.util.ParamUtil;
import com.seeyon.ctp.util.StringUtil;
import com.seeyon.ctp.util.Strings;

import www.seeyon.com.utils.UUIDUtil;

public class MainbodyManagerImpl implements MainbodyManager
{
    private final Map<MainbodyType, MainbodyHandler> contentHandlerMap;
    private final Map<String, Method> getContentListMap;
    private final Map<String, Object> getContentListMgrMap;
    private static final Logger logger;
    private FormCacheManager formCacheManager;
    
    static {
        logger = Logger.getLogger((Class)MainbodyManagerImpl.class);
    }
    
    public MainbodyManagerImpl() {
        this.contentHandlerMap = new HashMap<MainbodyType, MainbodyHandler>();
        this.getContentListMap = new HashMap<String, Method>();
        this.getContentListMgrMap = new HashMap<String, Object>();
    }
    
    public void init() {
        final Map<String, MainbodyHandler> contentHandlers = AppContext.getBeansOfType(MainbodyHandler.class);
        for (final String key : contentHandlers.keySet()) {
            final MainbodyHandler handler = contentHandlers.get(key);
            this.contentHandlerMap.put(handler.getType(), handler);
        }
    }
    
    @Override
    public CtpContentAllBean transContentNewResponse(final ModuleType moduleType, final Long moduleId, final MainbodyType contentType, final String rightId) throws BusinessException {
        final CtpContentAllBean content = new CtpContentAllBean();
        content.setCreateId(AppContext.currentUserId());
        content.setModuleType(moduleType.getKey());
        content.setModuleId(moduleId);
        content.setModuleTemplateId(0L);
        content.setContentTemplateId(0L);
        content.setContentType(contentType.getKey());
        content.setTitle("");
        content.setContent("");
        content.setSort(0);
        content.setRightId(rightId);
        content.setStatus(MainbodyStatus.STATUS_RESPONSE_NEW);
        content.setViewState(1);
        final MainbodyHandler handler = this.getContentHandler(content.getContentType());
        handler.handleContentView(content);
        return content;
    }
    
    @Override
    public List<CtpContentAllBean> transContentViewResponse(final int moduleType, final Long moduleId) throws BusinessException {
        return this.transContentViewResponse(ModuleType.getEnumByKey(moduleType), moduleId, 2, null, -1);
    }
    
    @Override
    public List<CtpContentAllBean> transContentViewResponse(final ModuleType moduleType, final Long moduleId, final Integer viewState, final String rightId) throws BusinessException {
        return this.transContentViewResponse(moduleType, moduleId, viewState, rightId, -1);
    }
    
    @Override
    public List<CtpContentAllBean> transContentViewResponse(final ModuleType moduleType, final Long moduleId, final Integer viewState, final String rightId, final Integer index) throws BusinessException {
        return this.transContentViewResponse(moduleType, moduleId, viewState, rightId, index, -1L);
    }
    
    @Override
    public List<CtpContentAllBean> transContentViewResponse(final ModuleType moduleType, final Long moduleId, final Integer viewState, final String rightId, final Integer index, final Long fromCopy) throws BusinessException {
        final List<CtpContentAllBean> contentList = new ArrayList<CtpContentAllBean>();
        if (fromCopy == null || fromCopy == -1L) {
            CtpContentAllBean content = null;
            final List<CtpContentAll> contentPoList = this.getContentList(moduleType, moduleId, rightId);
            boolean showIndex = false;
            if (index != null) {
                if (index + 1 > contentPoList.size()) {
                    throw new BusinessException("index\u8d85\u51fa\u6b63\u6587\u6570\u91cf,moduleId=" + moduleId);
                }
                if (index >= 0) {
                    showIndex = true;
                }
            }
            for (int i = 0; i < contentPoList.size(); ++i) {
                final CtpContentAll contentAll = contentPoList.get(i);
                content = new CtpContentAllBean(contentAll);
                content.putExtraMap("viewTitle", contentAll.getExtraAttr("viewTitle"));
                content.setViewState(viewState);
                content.setStatus(MainbodyStatus.STATUS_RESPONSE_VIEW);
                if (content.getRightId() == null && contentAll.getExtraAttr("rightId") != null) {
                    content.setRightId((String)contentAll.getExtraAttr("rightId"));
                }
                else {
                    content.setRightId(rightId);
                }
                final MainbodyHandler handler = this.getContentHandler(contentAll.getContentType());
                if (handler == null) {
                    throw new BusinessException("Handler not found for content type:" + contentAll.getContentType());
                }
                if (showIndex) {
                    content.putExtraMap("showIndex", showIndex);
                    if (i == index) {
                        handler.handleContentView(content);
                    }
                    else {
                        content.setContent("");
                    }
                }
                else {
                    handler.handleContentView(content);
                }
                contentList.add(content);
            }
        }
        else {
            final Map<String, Object> param = new HashMap<String, Object>();
            final String hql = "from CtpContentAll where id=:id";
            param.put("id", fromCopy);
            final List<CtpContentAll> contentPoList2 = (List<CtpContentAll>)DBAgent.find(hql, param);
            if (contentPoList2.size() <= 0) {
                throw new BusinessException("\u627e\u4e0d\u5230id=" + fromCopy + "\u7684\u6b63\u6587");
            }
            final CtpContentAll contentAll2 = contentPoList2.get(0);
            final CtpContentAllBean content2 = new CtpContentAllBean(contentAll2);
            content2.putExtraMap("viewTitle", contentAll2.getExtraAttr("viewTitle"));
            content2.setViewState(viewState);
            content2.setId(UUIDUtil.getUUIDLong());
            final Date now = DateUtil.currentTimestamp();
            content2.setModifyId(AppContext.currentUserId());
            content2.setModuleId(0L);
            content2.setModifyDate(now);
            content2.setCreateId(AppContext.currentUserId());
            content2.setCreateDate(now);
            content2.setStatus(MainbodyStatus.STATUS_RESPONSE_NEW);
            if (content2.getRightId() == null && contentAll2.getExtraAttr("rightId") != null) {
                content2.setRightId((String)contentAll2.getExtraAttr("rightId"));
            }
            else {
                content2.setRightId(rightId);
            }
            content2.putExtraMap("fromCopy", fromCopy);
            final MainbodyHandler handler2 = this.getContentHandler(contentAll2.getContentType());
            handler2.handleContentView(content2);
            contentList.add(content2);
        }
        return contentList;
    }
    
    @Override
    public List<CtpContentAll> getContentList(final ModuleType moduleType, final Long moduleId, final String rightId) throws BusinessException {
        List<CtpContentAll> contentList = new ArrayList<CtpContentAll>();
        final String getMainbodyList = ContentConfig.getConfig(moduleType).getMainbodyList();
        if ("default".equals(getMainbodyList)) {
            final Map params = new HashMap();
            params.put("moduleType", moduleType.getKey());
            params.put("moduleId", moduleId);
            contentList = (List<CtpContentAll>)DBAgent.findByNamedQuery("ctp_common_content_findContentByModule", params);
            String tempRightId = null;
            if (contentList == null || contentList.size() <= 0) {
                if (Strings.isNotBlank(rightId) && !"-1".equals(rightId)) {
                    if (rightId.indexOf(".") != -1) {
                        final String[] authStrs = rightId.split("[|]");
                        final String ids = authStrs[0];
                        if (ids.contains(",")) {
                            tempRightId = ids.substring(ids.indexOf(".") + 1, ids.indexOf(","));
                        }
                        else {
                            tempRightId = ids.substring(ids.indexOf(".") + 1);
                        }
                    }
                    else {
                        tempRightId = rightId;
                    }
                }
                if (StringUtil.checkNull(tempRightId)) {
                    throw new BusinessException(ResourceUtil.getString("form.exception.datanotexit"));
                }
                final FormAuthViewBean auth = this.formCacheManager.getAuth(Long.parseLong(tempRightId));
                if (auth == null) {
                    throw new BusinessException(ResourceUtil.getString("form.exception.datanotexit"));
                }
                final FormViewBean view = this.formCacheManager.getView(auth.getFormViewId());
                final FormBean b = this.formCacheManager.getForm(view.getFormBeanId());
                final UpgradeUtil u = new UpgradeUtil();
                final JDBCAgent jdbc = new JDBCAgent(true);
                try {
                    u.upgradeSingleUnflowContentData(jdbc, b);
                }
                catch (Exception e) {
                    MainbodyManagerImpl.logger.error((Object)e.getMessage(), (Throwable)e);
                    throw new BusinessException(ResourceUtil.getString("form.exception.datanotexit"));
                }
                finally {
                    jdbc.close();
                }
                jdbc.close();
                contentList = (List<CtpContentAll>)DBAgent.findByNamedQuery("ctp_common_content_findContentByModule", params);
                if (contentList == null || contentList.size() <= 0) {
                    throw new BusinessException(ResourceUtil.getString("form.exception.datanotexit"));
                }
            }
            CtpContentAll tempContent = contentList.get(0);
            Long formId = tempContent.getContentTemplateId();
            FormBean formBean = null;
            if (tempContent.getContentType() == MainbodyType.FORM.getKey()) {
                formBean = this.formCacheManager.getForm((long)formId);
                if (Strings.isNotBlank(rightId) && !"-1".equals(rightId)) {
                    if (StringUtil.checkNull(tempRightId)) {
                        if (rightId.indexOf(".") != -1) {
                            final String[] authStrs2 = rightId.split("[|]");
                            final String ids2 = authStrs2[0];
                            if (ids2.contains(",")) {
                                tempRightId = ids2.substring(ids2.indexOf(".") + 1, ids2.indexOf(","));
                            }
                            else {
                                tempRightId = ids2.substring(ids2.indexOf(".") + 1);
                            }
                        }
                        else {
                            tempRightId = rightId;
                        }
                    }
                    final Iterator<CtpContentAll> iteratorList = contentList.iterator();
                    while (iteratorList.hasNext()) {
                        final CtpContentAll ctpContentAll = iteratorList.next();
                        final Long tempFormId = ctpContentAll.getContentTemplateId();
                        FormBean tempFormBean = null;
                        tempFormBean = this.formCacheManager.getForm((long)tempFormId);
                        if (tempFormBean != null) {
                            final String rightIdStr = String.valueOf(this.formCacheManager.getNewOperationId(tempFormId, Long.parseLong(tempRightId)));
                            final FormAuthViewBean formAuthViewBean = tempFormBean.getAuthViewBeanById(Long.parseLong(rightIdStr));
                            if (formAuthViewBean != null) {
                                formBean = tempFormBean;
                                formId = formBean.getId();
                                tempContent = ctpContentAll;
                            }
                            else {
                                iteratorList.remove();
                            }
                        }
                    }
                }
                if (formBean == null) {
                    throw new BusinessException(ResourceUtil.getString("form.exception.formdatadelete"));
                }
            }
            if (!StringUtil.checkNull(rightId) && (rightId.indexOf("|") != -1 || rightId.indexOf(".") != -1 || rightId.indexOf(",") != -1)) {
                contentList.clear();
                final String[] authStrs2 = rightId.split("[|]");
                String[] array;
                for (int length = (array = authStrs2).length, i = 0; i < length; ++i) {
                    final String authStr = array[i];
                    if (!StringUtil.checkNull(authStr)) {
                        String[] viewAndAuth = authStr.split("[.]");
                        if (viewAndAuth.length <= 1) {
                            final String[] tviewAndAuth = new String[2];
                            final FormAuthViewBean a = formBean.getAuthViewBeanById(Long.parseLong(viewAndAuth[0]));
                            tviewAndAuth[0] = String.valueOf(a.getFormViewId());
                            tviewAndAuth[1] = viewAndAuth[0];
                            viewAndAuth = tviewAndAuth;
                        }
                        final String viewIdStr = viewAndAuth[0];
                        String rightIdStr2 = viewAndAuth[1];
                        Long viewId = Long.parseLong(viewIdStr);
                        if (rightIdStr2 != null) {
                            if (rightIdStr2.indexOf(",") != -1) {
                                final String[] rids = rightIdStr2.split(",");
                                boolean b2 = false;
                                rightIdStr2 = "";
                                String[] array2;
                                for (int length2 = (array2 = rids).length, j = 0; j < length2; ++j) {
                                    final String rid = array2[j];
                                    final Long lrid = Long.parseLong(rid);
                                    rightIdStr2 = String.valueOf(rightIdStr2) + String.valueOf(this.formCacheManager.getNewOperationId(formId, lrid)) + ",";
                                    if (!b2) {
                                        viewId = formBean.getAuthViewBeanById(lrid).getFormViewId();
                                        b2 = true;
                                    }
                                }
                                rightIdStr2 = StringUtils.replaceLast(rightIdStr2, ",", "");
                            }
                            else {
                                rightIdStr2 = String.valueOf(this.formCacheManager.getNewOperationId(formId, Long.parseLong(rightIdStr2)));
                                Long rs=Long.parseLong(rightIdStr2);
                                FormAuthViewBean favb=formBean.getAuthViewBeanById(rs);
                                viewId = favb.getFormViewId();
                            }
                        }
                        final CtpContentAll myContent = new CtpContentAll();
                        myContent.setId(tempContent.getId());
                        myContent.setContent(tempContent.getContent());
                        myContent.setContentDataId(tempContent.getContentDataId());
                        myContent.setContentTemplateId(tempContent.getContentTemplateId());
                        myContent.setContentType(tempContent.getContentType());
                        myContent.setCreateDate(tempContent.getCreateDate());
                        myContent.setCreateId(tempContent.getCreateId());
                        myContent.setModifyDate(tempContent.getModifyDate());
                        myContent.setModifyId(tempContent.getModifyId());
                        myContent.setModuleId(tempContent.getModuleId());
                        myContent.setModuleTemplateId(tempContent.getModuleTemplateId());
                        myContent.setModuleType(tempContent.getModuleType());
                        myContent.setSort(tempContent.getSort());
                        myContent.putExtraAttr("viewTitle", formBean.getFormView((long)viewId).getFormViewName());
                        myContent.setTitle(tempContent.getTitle());
                        myContent.putExtraAttr("rightId", rightIdStr2);
                        contentList.add(myContent);
                    }
                }
            }
        }
        else {
            Method m = this.getContentListMap.get(getMainbodyList);
            try {
                if (m == null) {
                    final int idx = getMainbodyList.lastIndexOf(46);
                    if (idx != -1) {
                        final String clsName = getMainbodyList.substring(0, idx);
                        final String methodName = getMainbodyList.substring(idx + 1);
                        Object mgr = null;
                        Class cls;
                        if (clsName.indexOf(46) == -1) {
                            mgr = AppContext.getBean(clsName);
                            cls = mgr.getClass();
                            this.getContentListMgrMap.put(getMainbodyList, mgr);
                        }
                        else {
                            cls = Class.forName(clsName);
                        }
                        final Class[] types = { ModuleType.class, Long.class };
                        final Method mm = cls.getDeclaredMethod(methodName, (Class[])types);
                        if (mm != null && mm.getReturnType() == List.class) {
                            m = mm;
                            this.getContentListMap.put(getMainbodyList, m);
                        }
                    }
                }
                if (m == null) {
                    throw new BusinessException("Method not found:" + getMainbodyList);
                }
                final Object[] datas = { moduleType, moduleId };
                contentList = (List<CtpContentAll>)m.invoke(this.getContentListMgrMap.get(getMainbodyList), datas);
            }
            catch (Exception e2) {
                throw new BusinessException("Error occured while find method:" + getMainbodyList, e2);
            }
        }
        return contentList;
    }
    
    @Override
    public List<CtpContentAll> getContentListByModuleIdAndModuleType(final ModuleType moduleType, final Long moduleId) {
        return this.getContentListByModuleIdAndModuleType(moduleType, moduleId, true);
    }
    
    @Override
    public List<CtpContentAll> getContentListByModuleIdAndModuleType(final ModuleType moduleType, final Long moduleId, final boolean needSetTitle) {
        List<CtpContentAll> contentList = new ArrayList<CtpContentAll>();
        final Map<String, Object> params = new HashMap<String, Object>();
        params.put("moduleType", moduleType.getKey());
        params.put("moduleId", moduleId);
        contentList = (List<CtpContentAll>)DBAgent.findByNamedQuery("ctp_common_content_findContentByModule", params);
        if (needSetTitle && AppContext.getThreadContext("contentTitle") != null && contentList.size() > 0) {
            for (final CtpContentAll c : contentList) {
                c.setTitle(String.valueOf(AppContext.getThreadContext("contentTitle")));
            }
        }
        return contentList;
    }
    
    @Override
    public List<CtpContentAll> getContentListByContentDataIdAndModuleType(final int moduleType, final Long contentDataId) {
        List<CtpContentAll> contentList = new ArrayList<CtpContentAll>();
        final Map<String, Object> params = new HashMap<String, Object>();
        params.put("moduleType", moduleType);
        params.put("contentDataId", contentDataId);
        contentList = (List<CtpContentAll>)DBAgent.findByNamedQuery("ctp_common_content_findContentByContentDataId", params);
        return contentList;
    }
    
    @Override
    public boolean transContentSaveOrUpdate(final CtpContentAllBean content) throws BusinessException {
        final Date currentDate = DateUtil.currentTimestamp();
        final User user = AppContext.getCurrentUser();
        final Long id = content.getId();
        final MainbodyHandler handler = this.getContentHandler(content.getContentType());
        if (id == null || id == -1L || id == 0L) {
            final Long moduleTemplateId = (Long) content.getExtraMap().get("moduleTemplateId");
            if (moduleTemplateId != null) {
                content.setModuleTemplateId(moduleTemplateId);
            }
            content.setCreateDate((content.getCreateDate() == null) ? currentDate : content.getCreateDate());
            content.setCreateId((content.getCreateId() == null || content.getCreateId() == 0L) ? AppContext.currentUserId() : ((long)content.getCreateId()));
            content.setSort(0);
            content.setId(UUIDUtil.getUUIDLong());
            content.setStatus(MainbodyStatus.STATUS_POST_SAVE);
        }
        else {
            content.setModifyId(user.getId());
            content.setModifyDate(currentDate);
            content.setStatus(MainbodyStatus.STATUS_POST_UPDATE);
        }
        handler.beforeSaveContent(content);
        if (handler.handleContentSaveOrUpdate(content)) {
            final CtpContentAll contentAll = content.toContentAll();
            DBAgent.saveOrUpdate(contentAll);
            handler.afterSaveContent(content);
            return true;
        }
        return false;
    }
    
    @Override
    public MainbodyHandler getContentHandler(final Integer contentType) throws BusinessException {
        final MainbodyHandler handler = this.contentHandlerMap.get(MainbodyType.getEnumByKey(contentType));
        if (handler == null) {
            throw new BusinessException("\u4e0d\u652f\u6301\u7684\u6b63\u6587\u7c7b\u578b\uff1a" + contentType);
        }
        return handler;
    }
    
    @Override
    public void saveOrUpdateContentAll(final CtpContentAll contentAll) throws BusinessException {
        DBAgent.saveOrUpdate(contentAll);
    }
    
    @Override
    public void deleteContentAllByModuleId(final ModuleType moduleType, final Long moduleId) throws BusinessException {
        DBAgent.bulkUpdate("delete from CtpContentAll cc where moduleId = ? and moduleType = ?", moduleId, moduleType.getKey());
    }
    
    @Override
    public FlipInfo testContentList(final FlipInfo fi, final Map paramsIn) throws BusinessException {
        final List<CtpContentAllBean> contentList = new ArrayList<CtpContentAllBean>();
        final Map params = new HashMap();
        params.put("moduleType", ParamUtil.getInt(paramsIn, "moduleType", ModuleType.collaboration.getKey()));
        final List<CtpContentAll> contentAllList = (List<CtpContentAll>)DBAgent.findByNamedQuery("ctp_common_content_findContentByModuleType", params, fi);
        for (final CtpContentAll contentAll : contentAllList) {
            final CtpContentAllBean content = new CtpContentAllBean(contentAll);
            contentList.add(content);
        }
        return fi;
    }
    
    public FormCacheManager getFormCacheManager() {
        return this.formCacheManager;
    }
    
    public void setFormCacheManager(final FormCacheManager formCacheManager) {
        this.formCacheManager = formCacheManager;
    }
    
    @Override
    public int updateContentTitle(final Long id, final String title) {
        final Map<String, Object> param = new HashMap<String, Object>();
        param.put("id", id);
        param.put("title", title);
        return DBAgent.bulkUpdate("update CtpContentAll c set c.title=:title where c.id=:id", param);
    }
    
    @Override
    public void deleteById(final Long id) throws BusinessException {
        final Map<String, Object> param = new HashMap<String, Object>();
        param.put("id", id);
        final List<CtpContentAll> contents = (List<CtpContentAll>)DBAgent.find("from CtpContentAll c where c.id=:id", param);
        if (contents.size() > 0) {
            final CtpContentAll content = contents.get(0);
            if (content != null) {
                try {
                    final boolean isDeleteFormData = Integer.valueOf(20).equals(content.getContentType());
                    MainbodyService.getInstance().deleteContentAllByModuleId(ModuleType.getEnumByKey(content.getModuleType()), content.getModuleId(), isDeleteFormData);
                }
                catch (SQLException e) {
                    throw new BusinessException(e);
                }
            }
        }
    }
    
    @Override
    public Map getByModuleIdAndType(final Map<String, Object> map) throws BusinessException {
        final Map<String, Object> param = new HashMap<String, Object>();
        final String hql = " from CtpContentAll c  where c.moduleType=:moduleType and c.moduleId=:moduleId";
        param.put("moduleType", map.get("moduleType"));
        param.put("moduleId", map.get("moduleId"));
        final List<CtpContentAll> find = (List<CtpContentAll>)DBAgent.find(hql, param);
        final Map reMap = new HashMap();
        if (!Strings.isEmpty(find)) {
            reMap.put("contentId", find.get(0).getId());
        }
        else {
            reMap.put("contentId", 0);
        }
        return reMap;
    }
}
