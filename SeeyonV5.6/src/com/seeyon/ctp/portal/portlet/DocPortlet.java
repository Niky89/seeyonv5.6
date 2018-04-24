/*     */ package com.seeyon.ctp.portal.portlet;
/*     */ 
/*     */ import com.seeyon.ctp.common.config.SystemConfig;
/*     */ import com.seeyon.ctp.common.flag.SysFlag;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class DocPortlet
/*     */   implements BasePortlet
/*     */ {
/*     */   private SystemConfig systemConfig;
/*     */   
/*     */   public void setSystemConfig(SystemConfig systemConfig)
/*     */   {
/*  18 */     this.systemConfig = systemConfig;
/*     */   }
/*     */   
/*     */   public String getId()
/*     */   {
/*  23 */     return "DocPortlet";
/*     */   }
/*     */   
/*     */   public List<ImagePortletLayout> getData()
/*     */   {
/*  28 */     List<ImagePortletLayout> layouts = new ArrayList();
/*  29 */     if ((!((Boolean)SysFlag.sys_isGovVer.getFlag()).booleanValue()) && (!((Boolean)SysFlag.sys_isA6Ver.getFlag()).booleanValue())) {
/*  30 */       ImagePortletLayout knowledgeNavigation = new ImagePortletLayout();
/*     */       
/*  32 */       knowledgeNavigation.setPortletId("KnowledgeNavigation");
/*  33 */       knowledgeNavigation.setResourceCode("F04_showKnowledgeNavigation");
/*  34 */       knowledgeNavigation.setPluginId("doc");
/*  35 */       knowledgeNavigation.setPortletName("个人知识中心");
/*  36 */       knowledgeNavigation.setDisplayName("system.menuname.PersonalKMCenter");
/*  37 */       knowledgeNavigation.setCategory(PortletConstants.PortletCategory.doc.name());
/*  38 */       knowledgeNavigation.setPortletUrl("/doc/knowledgeController.do?method=personalKnowledgeCenterIndex&openFlag=docRead");
/*  39 */       knowledgeNavigation.setPortletUrlType(PortletConstants.UrlType.workspace.name());
/*  40 */       knowledgeNavigation.setSize(PortletConstants.PortletSize.middle.ordinal());
/*  41 */       knowledgeNavigation.setOrder(0);
/*     */       
/*  43 */       List<ImageLayout> knowledgeNavigationIms = new ArrayList();
/*  44 */       ImageLayout knowledgeNavigationImg = new ImageLayout();
/*  45 */       knowledgeNavigationImg.setImageTitle("system.menuname.PersonalKMCenter");
/*  46 */       knowledgeNavigationImg.setImageUrl("d_personalkmcenter.png");
/*  47 */       knowledgeNavigationIms.add(knowledgeNavigationImg);
/*     */       
/*  49 */       knowledgeNavigation.setImageLayouts(knowledgeNavigationIms);
/*     */       
/*  51 */       layouts.add(knowledgeNavigation);
/*     */       
/*  53 */       ImagePortletLayout knowledgeSquareFrame = new ImagePortletLayout();
/*     */       
/*  55 */       knowledgeSquareFrame.setPortletId("KnowledgeSquareFrame");
/*  56 */       knowledgeSquareFrame.setResourceCode("F04_knowledgeSquareFrame");
/*  57 */       knowledgeSquareFrame.setPluginId("doc");
/*  58 */       knowledgeSquareFrame.setPortletName("知识广场");
/*  59 */       knowledgeSquareFrame.setDisplayName("system.menuname.KMSquare");
/*  60 */       knowledgeSquareFrame.setCategory(PortletConstants.PortletCategory.doc.name());
/*  61 */       knowledgeSquareFrame.setPortletUrl("/doc/knowledgeController.do?method=toKnowledgeSquare");
/*  62 */       knowledgeSquareFrame.setPortletUrlType(PortletConstants.UrlType.workspace.name());
/*  63 */       knowledgeSquareFrame.setSize(PortletConstants.PortletSize.middle.ordinal());
/*  64 */       knowledgeSquareFrame.setOrder(1);
/*     */       
/*  66 */       List<ImageLayout> knowledgeSquareFrameIms = new ArrayList();
/*  67 */       ImageLayout knowledgeSquareFrameImg = new ImageLayout();
/*  68 */       knowledgeSquareFrameImg.setImageTitle("system.menuname.KMSquare");
/*  69 */       knowledgeSquareFrameImg.setImageUrl("d_kmsquare.png");
/*  70 */       knowledgeSquareFrameIms.add(knowledgeSquareFrameImg);
/*     */       
/*  72 */       knowledgeSquareFrame.setImageLayouts(knowledgeSquareFrameIms);
/*     */       
/*  74 */       layouts.add(knowledgeSquareFrame);
/*     */     }
/*     */     
/*  77 */     ImagePortletLayout docIndex = new ImagePortletLayout();
/*     */     
/*  79 */     docIndex.setPortletId("DocIndex");
/*  80 */     docIndex.setResourceCode("F04_docIndex");
/*  81 */     docIndex.setPluginId("doc");
/*  82 */     docIndex.setPortletName("文档中心");
/*  83 */     docIndex.setDisplayName("system.menuname.DocCenter");
/*  84 */     docIndex.setCategory(PortletConstants.PortletCategory.doc.name());
/*  85 */     docIndex.setPortletUrl("/doc.do?method=docIndex&openLibType=1");
/*  86 */     docIndex.setPortletUrlType(PortletConstants.UrlType.workspace.name());
/*  87 */     docIndex.setSize(PortletConstants.PortletSize.middle.ordinal());
/*  88 */     docIndex.setOrder(2);
/*     */     
/*  90 */     List<ImageLayout> docIndexIms = new ArrayList();
/*  91 */     ImageLayout docIndexImg = new ImageLayout();
/*  92 */     docIndexImg.setImageTitle("system.menuname.DocCenter");
/*  93 */     docIndexImg.setImageUrl("d_doccenter.png");
/*  94 */     docIndexIms.add(docIndexImg);
/*     */     
/*  96 */     docIndex.setImageLayouts(docIndexIms);
/*     */     
/*  98 */     layouts.add(docIndex);
/*     */     
/* 100 */     String blogEnable = this.systemConfig.get("blog_enable");
/* 101 */     boolean isBlogEnable = (blogEnable != null) && ("enable".equals(blogEnable));
/*     */     
/* 103 */     if (isBlogEnable) {
/* 104 */       ImagePortletLayout blogHome = new ImagePortletLayout();
/*     */       
/* 106 */       blogHome.setPortletId("BlogHome");
/* 107 */       blogHome.setResourceCode("F04_blogHome");
/* 108 */       blogHome.setPluginId("doc");
/* 109 */       blogHome.setPortletName("我的博客");
/* 110 */       blogHome.setDisplayName("system.menuname.Blog");
/* 111 */       blogHome.setCategory(PortletConstants.PortletCategory.doc.name());
/* 112 */       blogHome.setPortletUrl("/blog.do?method=blogHome");
/* 113 */       blogHome.setPortletUrlType(PortletConstants.UrlType.workspace.name());
/* 114 */       blogHome.setSize(PortletConstants.PortletSize.middle.ordinal());
/* 115 */       blogHome.setOrder(3);
/*     */       
/* 117 */       List<ImageLayout> blogHomeIms = new ArrayList();
/* 118 */       ImageLayout blogHomeImg = new ImageLayout();
/* 119 */       blogHomeImg.setImageTitle("system.menuname.Blog");
/* 120 */       blogHomeImg.setImageUrl("d_blog.png");
/* 121 */       blogHomeIms.add(blogHomeImg);
/*     */       
/* 123 */       blogHome.setImageLayouts(blogHomeIms);
/*     */       
/* 125 */       layouts.add(blogHome);
/*     */       
/* 127 */       ImagePortletLayout blogManager = new ImagePortletLayout();
/*     */       
/* 129 */       blogManager.setPortletId("BlogManager");
/* 130 */       blogManager.setResourceCode("F04_listAllArticleAdmin");
/* 131 */       blogManager.setPluginId("doc");
/* 132 */       blogManager.setPortletName("博客管理");
/* 133 */       blogManager.setDisplayName("system.menuname.BlogManagement");
/* 134 */       blogManager.setCategory(PortletConstants.PortletCategory.doc.name());
/* 135 */       blogManager.setPortletUrl("/blog.do?method=listAllArticleAdmin");
/* 136 */       blogManager.setPortletUrlType(PortletConstants.UrlType.workspace.name());
/* 137 */       blogManager.setSize(PortletConstants.PortletSize.middle.ordinal());
/* 138 */       blogManager.setOrder(4);
/*     */       
/* 140 */       List<ImageLayout> blogManagerIms = new ArrayList();
/* 141 */       ImageLayout blogManagerImg = new ImageLayout();
/* 142 */       blogManagerImg.setImageTitle("system.menuname.BlogManagement");
/* 143 */       blogManagerImg.setImageUrl("d_blogmanagement.png");
/* 144 */       blogManagerIms.add(blogManagerImg);
/*     */       
/* 146 */       blogManager.setImageLayouts(blogManagerIms);
/*     */       
/* 148 */       layouts.add(blogManager);
/*     */     }
/*     */     
/* 151 */     String rssEnable = this.systemConfig.get("rss_enable");
/* 152 */     boolean isRssEnable = (blogEnable != null) && ("enable".equals(rssEnable));
/*     */     
/* 154 */     if (isRssEnable) {
/* 155 */       ImagePortletLayout rssMain = new ImagePortletLayout();
/*     */       
/* 157 */       rssMain.setPortletId("RssMain");
/* 158 */       rssMain.setResourceCode("F04_rssMain");
/* 159 */       rssMain.setPluginId("doc");
/* 160 */       rssMain.setPortletName("RSS订阅");
/* 161 */       rssMain.setDisplayName("system.menuname.RSSSubscription");
/* 162 */       rssMain.setCategory(PortletConstants.PortletCategory.doc.name());
/* 163 */       rssMain.setPortletUrl("/rss/rssController.do?method=main");
/* 164 */       rssMain.setPortletUrlType(PortletConstants.UrlType.workspace.name());
/* 165 */       rssMain.setSize(PortletConstants.PortletSize.middle.ordinal());
/* 166 */       rssMain.setOrder(5);
/*     */       
/* 168 */       List<ImageLayout> rssMainIms = new ArrayList();
/* 169 */       ImageLayout rssMainImg = new ImageLayout();
/* 170 */       rssMainImg.setImageTitle("system.menuname.RSSSubscription");
/* 171 */       rssMainImg.setImageUrl("d_rsssubscription.png");
/* 172 */       rssMainIms.add(rssMainImg);
/*     */       
/* 174 */       rssMain.setImageLayouts(rssMainIms);
/*     */       
/* 176 */       layouts.add(rssMain);
/*     */     }
/*     */     
/* 179 */     return layouts;
/*     */   }
/*     */   
/*     */   public ImagePortletLayout getPortlet(String portletId)
/*     */   {
/* 184 */     List<ImagePortletLayout> layouts = getData();
/* 185 */     if (layouts != null) {
/* 186 */       for (ImagePortletLayout layout : layouts) {
/* 187 */         if (portletId.equals(layout.getPortletId())) {
/* 188 */           return layout;
/*     */         }
/*     */       }
/*     */     }
/* 192 */     return null;
/*     */   }
/*     */   
/*     */   public int getDataCount(String portletId)
/*     */   {
/* 197 */     return -1;
/*     */   }
/*     */   
/*     */   public boolean isAllowDataUsed(String portletId)
/*     */   {
/* 202 */     return true;
/*     */   }
/*     */   
/*     */   public boolean isAllowUsed()
/*     */   {
/* 207 */     return true;
/*     */   }
/*     */ }


/* Location:              C:\Users\Administrator\Desktop\新建文件夹\seeyon_apps_f111.jar!\com\seeyon\ctp\portal\portlet\DocPortlet.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */