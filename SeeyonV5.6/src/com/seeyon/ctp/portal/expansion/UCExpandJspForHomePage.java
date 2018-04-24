/*    */ package com.seeyon.ctp.portal.expansion;
/*    */ 
/*    */ import com.seeyon.ctp.common.AppContext;
/*    */ import com.seeyon.ctp.common.authenticate.domain.User;
/*    */ import java.util.ArrayList;
/*    */ import java.util.List;
/*    */ import java.util.Map;
/*    */ 
/*    */ public class UCExpandJspForHomePage extends AbstractExpandJspForHomePage
/*    */ {
/*    */   public List<String> expandJspForHomePage(Map params)
/*    */   {
/* 13 */     if ((AppContext.hasPlugin("uc")) && (!AppContext.getCurrentUser().isAdmin())) {
/* 14 */       String ncJspPath = "/WEB-INF/jsp/apps/uc/uc_connection.jsp";
/* 15 */       List<String> jspPaths = new ArrayList();
/* 16 */       jspPaths.add(ncJspPath);
/* 17 */       return jspPaths;
/*    */     }
/* 19 */     return null;
/*    */   }
/*    */ }


/* Location:              C:\Users\Administrator\Desktop\新建文件夹\seeyon_apps_f111.jar!\com\seeyon\ctp\portal\expansion\UCExpandJspForHomePage.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */