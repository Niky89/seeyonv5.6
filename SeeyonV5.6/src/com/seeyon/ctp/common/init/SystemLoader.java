package com.seeyon.ctp.common.init;

import com.seeyon.ctp.cluster.notification.NotificationFactory;
import com.seeyon.ctp.cluster.notification.NotificationManager;
import com.seeyon.ctp.common.AppContext;
import com.seeyon.ctp.common.SystemInitializer;
import com.seeyon.ctp.common.code.EnumsConfigLoader;
import com.seeyon.ctp.common.config.PropertiesLoader;
import com.seeyon.ctp.common.config.manager.ConfigManager;
import com.seeyon.ctp.common.constants.SystemProperties;
import com.seeyon.ctp.common.i18n.ResourceLoader;
import com.seeyon.ctp.common.log.Log4JConfigurator;
import com.seeyon.ctp.common.plugin.PluginDefinition;
import com.seeyon.ctp.common.po.config.ConfigItem;
import com.seeyon.ctp.common.security.SecurityHelper;
import com.seeyon.ctp.common.usermessage.UserMessageWorker;
import com.seeyon.ctp.util.Datetimes;
import com.seeyon.ctp.util.JDBCAgent;
import com.seeyon.ctp.util.StringUtil;
import java.io.File;
import java.io.PrintStream;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Date;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import javax.servlet.ServletContext;
import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;

public final class SystemLoader {
	private static Logger LOGGER = Logger.getLogger(SystemLoader.class);
	private static Map<String, SystemInitializer> initialitions;
	public static final String INIT_DEFAULT_CFG_HOME = "/WEB-INF/cfgHome";
	public static final String INIT_SYSTEM_PROPERTIES = "/base/systemProperties.xml";
	public static final String INIT_USER_PROPERTIES = "/base/conf/systemCtp.properties";
	public static final String INIT_PRODUCT_FEATURE = "/base/productFeature.xml";
	static final String fslkdf = "MIICeAIBADANBgkqhkiG9w0BAQEFAASCAmIwggJeAgEAAoGBAMxeXGCpgDNNfD0QPwHdiN6fIqsmwxrJn6uuoUtgoE8ukJRvVMNqxP2xa8Nof+6GWo2etbnmR3Vmrt5WDia+2K6Ky+g+76kSJ6FgaRkAj0/8M7tzTJAxGM1I+O1URb4Kx0qLEkXIX2Dqt1gf3uFFgq3tNaOkucQDHueAVY3sFoHDAgMBAAECgYEAq+s5NMuCICYvvEKdbllJWVVvWaELAc7Y9hi6cOmBEfsu/t9E4/s+adfFuqV+8U2ugXIgl5jjRhyyQDEOSIPFTFUoQVW13gd4+Jbzy1Ai92XmGxSYezmWkMAgLpQ5R/3oNaAtjskEVgtegZhRnpJayrpahJs+wICbdGXDkaaysTECQQD4f5EhIdDBsWQHSN7niVUvFMOY88/yqaELmfd0Ix6XUwhbGDkEY4zo/8PJoYwZjWMS2z99DnB8j6Uhw9Q88KCpAkEA0onAtG2z6/aBoFYrSQy0o4lFhqBtupQuCJa4hh82qkwi6DcptwvgMU0NrAW32u17TpoDBgx6e5i2n2k5EVLWiwJAF0QaWnfIC9qH+wafmB4Lk7Tl+1QYjCfFpEhuGrmPC6wChEToUbjgYJBRzHcLnvjV5dYRQ6wua/snAtpK7Mm6wQJBAJ2icDtni/gXaDTRb7VRIwry47CJ+75f5hueJeieBEL7oIM3ki8wYBrps1viKCdt+g3++FaWDzrtO4cdtJHI8UMCQQDrUoWq1ktFENQov8i5e/OvZ14TiRmvAW4KtTGFAZ4tJDbf9ZT8m/MoVxfbYUzY7aibaPqnUE8cO13PLTPpvuKa";
	static final String mufsdfl = "Nzg4NDM2MTAxMzc1NzA0MDQ1Nzc3ODQ3MzM0OTg2NzgxNjEzNDM5Mzg5OTMyODA2ODcwNDQ0Nzk4NDIyODE2MTk0MTEzMzA2NDcyNjkzNTQzMDg4NjUyODc4NDA0NjUwMDEwMDAyNjI0ODQ4NjMxMzA3MjgzMTc4NzE1ODYzMjE1OTYzMDY3NDkwNTYzNDc1NTg0ODM0NzU1NzQ5MDI2NDkyMDk5NTUyMTIzNDAyOTA2NDIyMzgzMTQ1ODUzMjc3OTM4MDQxMDQ5MTU5NzczOTk0ODY3NzA5NzYwMjQzMDcwNTQzMjA3";

	public static final void initAfterSpring(ServletContext sc, ApplicationContext wac) {
		File cfgHome = AppContext.getCfgHome();
		AppContext.init(wac, cfgHome);

		ResourceLoader.initResources();

		initSystemInitializer();

		initProductInstallInfo();
		try {
			Object o = MclclzUtil.invoke(c3, "getInstance", new Class[] { String.class }, null, new Object[] { "" });
			Integer serverType = (Integer) MclclzUtil.invoke(c3, "getserverType", null, o, null);
			boolean licensePerServerValid = ((Boolean) MclclzUtil.invoke(c3, "licensePerServerValid", null, o, null))
					.booleanValue();
			if ((serverType.intValue() == 1) && (!licensePerServerValid)) {
				out("系统启动失败，当前系统实际注册用户数超出授权注册用户数限制！");
			}
		} catch (Exception e) {
			out("系统启动失败，注册数授权验证异常：" + e.getMessage());
		}
		UserMessageWorker.getInstance().start();

		NotificationManager.getInstance().enableSend(true);

		EnumsConfigLoader.init();

		SecurityHelper.initSecurityUrlConfig();

		SecurityHelper.initScriptEngine(System.getProperty(SystemProperties.CONFIG_APPLICATION_ROOT_KEY));

		JDBCAgent.loadSqlInFile();

		initThirdPartyLicense();

		System.gc();

		String appName = AppContext.getSystemProperty(SystemProperties.CONFIG_SYSTEM_NAME);
		LOGGER.warn("SEEYON " + appName + " opened for business.");
		Log4JConfigurator.reInitialized();
	}

	public static final String initBeforeSpring(ServletContext sc) {
		File cfgHome = null;
		if (sc != null) {
			String cfg = sc.getInitParameter("cfgHome");
			if (cfg == null) {
				cfg = System.getProperty("cfgHome");
			}
			if (cfg == null) {
				cfg = "/WEB-INF/cfgHome";
			}
			String repa = sc.getRealPath(cfg);
			if (StringUtil.checkNull(repa)) {
				LOGGER.info("通过getRealPath方法不能找到正确路径，尝试用URL去查找.");
				try {
					URL url = sc.getResource(cfg);
					if (url == null) {
						//break label159;
					}
					cfgHome = new File(url.toURI());
				} catch (MalformedURLException e) {
					LOGGER.warn("上下文环境不能找到对应配置实际文件位置。" + e.toString());
				} catch (URISyntaxException e) {
					LOGGER.warn("上下文环境不能找到对应配置实际文件位置。" + e.toString());
				}
			} else {
				cfgHome = new File(repa);
			}
			//label159: 
			if (cfgHome == null) {
				throw new IllegalStateException("不能找到cfghome");
			}
			if ((!cfgHome.exists()) || (!cfgHome.isDirectory())) {
				cfgHome = new File(cfg);
				if (!cfgHome.exists()) {
					throw new IllegalStateException(
							"Configuration home directory does not exists: " + cfgHome.getAbsolutePath());
				}
			}
			AppContext.init(null, cfgHome);
		} else {
			cfgHome = AppContext.getCfgHome();
		}
		initContextParam(sc);

		Properties props = PropertiesLoader.load(new File(cfgHome, "/base/systemProperties.xml"));
		SystemProperties.getInstance().putAll(props);

		Log4JConfigurator.initialize();
		LOGGER.info("cfgHOME路径" + cfgHome.getPath());
		try {
			Properties productFeatureProps = PropertiesLoader.load(new File(cfgHome, "/base/productFeature.xml"));
			SystemProperties.getInstance().putAll(productFeatureProps);
		} catch (Exception e) {
			LOGGER.warn("产品特性文件未配置。");
		}
		String appName = AppContext.getSystemProperty(SystemProperties.CONFIG_SYSTEM_NAME);
		LOGGER.info("SEEYON " + appName + " Starting...");

		SystemProperties.getInstance().printAll(SystemProperties.getInstance().getAllProperties());

		initProductInfo();

		NotificationManager.getInstance().enableSend(false);
		try {
			Object obj = MclclzUtil.invoke(c1, "getInstance");
			MclclzUtil.invoke(c1, "init", null, obj, null);
		} catch (Exception e) {
			System.out.println("plugin error!");
			e.printStackTrace();
		}
		String userProps = System.getProperty("SEEYON_HOME");
		if (userProps == null) {
			userProps = cfgHome.getPath() + "/../../../../../";
			LOGGER.warn("未配置SEEYON_HOME环境变量，使用默认路径：" + userProps);
		}
		if (new File(userProps, "/base/conf/systemCtp.properties").exists()) {
			SystemProperties.getInstance()
					.putAll(PropertiesLoader.load(new File(userProps, "/base/conf/systemCtp.properties")));
		}
		System.getProperties().putAll(SystemProperties.getInstance().getAllProperties());

		String springConfig = initSpringConfig();
		LOGGER.debug("Spring loading:" + springConfig);
		System.setProperty("hibernate.dialect_resolvers", "org.hibernate.dialect.resolver.CTPDialectResolver");

		return springConfig;
	}

	public static final void destroy(ServletContext sc) {
		NotificationManager.getInstance().enableSend(false);

		UserMessageWorker.getInstance().stop();

		Iterator<String> ite = initialitions.keySet().iterator();
		while (ite.hasNext()) {
			String beanName = (String) ite.next();
			SystemInitializer initializer = (SystemInitializer) initialitions.get(beanName);
			try {
				initializer.destroy();
				LOGGER.info("System destroyed [" + beanName + "].");
			} catch (Exception e) {
				LOGGER.error("System destroyed [" + beanName + "].", e);
			}
		}
		System.gc();
	}

	private static void initContextParam(ServletContext sc) {
		if (sc != null) {
			Enumeration<String> names = sc.getInitParameterNames();
			while (names.hasMoreElements()) {
				String name = (String) names.nextElement();
				String value = sc.getInitParameter(name);

				SystemProperties.getInstance().put(name, value);
			}
			String rootPath = sc.getRealPath("");
			SystemProperties.getInstance().put(SystemProperties.CONFIG_APPLICATION_ROOT_KEY, rootPath);
			System.setProperty(SystemProperties.CONFIG_APPLICATION_ROOT_KEY, rootPath);
		}
	}

	private static void initSystemInitializer() {
		initialitions = SystemProperties.getBeansOfTypeAndSysConfig(SystemInitializer.class,
				SystemProperties.CONFIG_INITIALIZER);
		Iterator<String> ite = initialitions.keySet().iterator();
		while (ite.hasNext()) {
			String beanName = (String) ite.next();
			SystemInitializer initializer = (SystemInitializer) initialitions.get(beanName);
			long startTime = System.currentTimeMillis();
			initializer.initialize();

			LOGGER.info("Initializer [" + beanName + "] 耗时：" + (System.currentTimeMillis() - startTime) + " MS");
		}
	}

	private static void initProductInfo() {
		try {
			MclclzUtil.invoke(c2, "init", null, c2.newInstance(), null);
		} catch (Throwable e) {
			e.printStackTrace();
			out("初始化产品文件的效验码无效, " + e.getMessage());
		}
	}

	private static String bytes2Hex(byte[] bts) {
		String des = "";
		String tmp = null;
		for (int i = 0; i < bts.length; i++) {
			tmp = Integer.toHexString(bts[i] & 0xFF);
			if (tmp.length() == 1) {
				des = des + "0";
			}
			des = des + tmp;
		}
		return des;
	}

	private static void out(String message) {
		System.out.println("**************************************************************************");
		System.out.println("");
		System.out.println("Exception,Error : " + message);
		System.out.println("");
		System.out.println("**************************************************************************");
		try {
			Thread.sleep(5000L);
		} catch (Throwable localThrowable) {
		}
		System.exit(-1);
	}

	private static String initSpringConfig() {
		StringBuffer buf = new StringBuffer();

		File springHome = new File(AppContext.getCfgHome(), "spring");
		loadSpringInPath(springHome, buf);

		Object obj = MclclzUtil.invoke(c1, "getInstance");
		List<PluginDefinition> defs = (List) MclclzUtil.invoke(c1, "getPluginDefinitions", null, obj, null);
		for (PluginDefinition def : defs) {
			loadSpringInPath(new File(def.getPluginFoler(), "spring"), buf);
		}
		buf.deleteCharAt(0);

		return buf.toString();
	}

	private static void loadSpringInPath(File springPath, StringBuffer buf) {
		if ((springPath.exists()) && (springPath.isDirectory())) {
			File[] springCfgs = springPath.listFiles(new FileOnlyFilter());
			File[] arrayOfFile1;
			int j = (arrayOfFile1 = springCfgs).length;
			for (int i = 0; i < j; i++) {
				File springCfg = arrayOfFile1[i];
				buf.append(",\nfile:").append(springCfg.getAbsolutePath().replaceAll(" ", "%20"));
			}
		}
	}

	private static void initProductInstallInfo() {
		ConfigManager cm = (ConfigManager) AppContext.getBean("configManager");
		ConfigItem c = cm.getConfigItem("ctp_product_information", "installDate");
		if (c == null) {
			String date = Datetimes.formatDate(new Date());
			cm.addConfigItem("ctp_product_information", "installDate", date);
			cm.addConfigItem("ctp_product_information", "upgrageDate", date);

			SystemProperties.getInstance().put("product.install.date", date);
			SystemProperties.getInstance().put("product.upgrage.date", date);
		} else {
			String date = c.getConfigValue();
			SystemProperties.getInstance().put("product.install.date", date);

			c = cm.getConfigItem("ctp_product_information", "upgrageDate");
			date = c.getConfigValue();
			SystemProperties.getInstance().put("product.upgrage.date", date);
		}
	}

	private static final Class<?> c1 = MclclzUtil.ioiekc("com.seeyon.ctp.common.plugin.PluginSystemInit");
	private static final Class<?> c2 = MclclzUtil.ioiekc("com.seeyon.ctp.product.ProductInfo");
	private static final Class<?> c3 = MclclzUtil.ioiekc("com.seeyon.ctp.permission.bo.LicensePerInfo");

	/* Error */
	private static void initThirdPartyLicense() {
	
	}
}
