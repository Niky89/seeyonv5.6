package com.seeyon.ctp.common.plugin;

import com.seeyon.ctp.cluster.ClusterConfigBean;
import com.seeyon.ctp.common.AppContext;
import com.seeyon.ctp.common.ModuleType;
import com.seeyon.ctp.common.SystemEnvironment;
import com.seeyon.ctp.common.cache.CacheAccessable;
import com.seeyon.ctp.common.cache.CacheFactory;
import com.seeyon.ctp.common.cache.CacheObject;
import com.seeyon.ctp.common.config.PropertiesLoader;
import com.seeyon.ctp.common.constants.SystemProperties;
import com.seeyon.ctp.common.content.ContentConfig;
import com.seeyon.ctp.common.i18n.ResourceBundleUtil;
import com.seeyon.ctp.common.init.DirOnlyFilter;
import com.seeyon.ctp.common.init.MclclzUtil;
import com.seeyon.ctp.product.PlugInList;
import java.io.File;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import org.apache.log4j.Logger;

public final class PluginSystemInit {
	private static final Logger LOGGER = Logger.getLogger(PluginSystemInit.class);
	public static final String INIT_PLUGIN_PATH_DEFAULT = "/plugin";
	public static final String INIT_PLUGIN_PROPERTIES = "/base/conf/plugin.properties";
	public static final String INIT_INDEX_PROPERTIES = "/base/index/indexConfig.properties";
	private static PluginSystemInit instance = null;
	private final Map<String, Boolean> pluginIds4Map = new HashMap();
	private final List<PluginDefinition> pluginDefinitions = new ArrayList();
	private final List<PluginDefinition> allPluginDefinitions = new ArrayList();
	private final Map<Integer, PluginDefinition> category2PluginDefinition = new HashMap();

	public static final PluginSystemInit getInstance() {
		if (instance == null) {
			instance = new PluginSystemInit();
		}
		return instance;
	}

	public final void init() {
		long startTime = System.currentTimeMillis();

		File pluginHome = new File(AppContext.getCfgHome(), "/plugin");
		if ((!pluginHome.exists()) || (!pluginHome.isDirectory())) {
			LOGGER.warn("系统插件定义文件路径不存在：" + pluginHome.getAbsolutePath());
			return;
		}
		LOGGER.debug("系统插件定义文件路径：" + pluginHome.getAbsolutePath());

		boolean isCluster = ClusterConfigBean.getInstance().isClusterEnabled();
		CacheAccessable factory = CacheFactory.getInstance(SystemProperties.class);
		String cacheName = "masterProperties";
		boolean isClusterSlave = factory.isExist("masterProperties");
		Properties masterProperties;
		if (isClusterSlave) {
			CacheObject<Properties> cache = factory.getObject("masterProperties");
			masterProperties = (Properties) cache.get();
		} else {
			masterProperties = new Properties();
		}
		File[] plugins = pluginHome.listFiles(new DirOnlyFilter());

		String userProps = System.getProperty("SEEYON_HOME");
		if (userProps == null) {
			userProps = AppContext.getCfgHome() + "/../../../../../";
			LOGGER.warn("未配置SEEYON_HOME环境变量，使用默认路径：" + userProps);
		}
		Properties pluginCustomProps = null;
		if (new File(userProps, "/base/conf/plugin.properties").exists()) {
			pluginCustomProps = PropertiesLoader.load(new File(userProps, "/base/conf/plugin.properties"));
		} else {
			LOGGER.warn(new File(userProps, "/base/conf/plugin.properties").getAbsolutePath() + " 文件不存在，使用插件默认配置！");
		}
		File indexProFile = new File(userProps, "/base/index/indexConfig.properties");
		boolean ignoreIndex = false;
		int regNumber = 0;
		if ((indexProFile.exists()) && ("local".equals(PropertiesLoader.load(indexProFile).getProperty("modelName")))) {
			try {
				regNumber = Integer.parseInt(String.valueOf(MclclzUtil.invoke(c3, "getTotalservernum", null,
						MclclzUtil.invoke(c3, "getInstance", new Class[] { String.class }, null, new Object[] { "" }),
						null)));
			} catch (Exception e) {
				LOGGER.warn(" 获取并发数错误！");
			}
		}
		File[] arrayOfFile1;
		int j = (arrayOfFile1 = plugins).length;
		for (int i = 0; i < j; i++) {
			File plugin = arrayOfFile1[i];
			File pluginCfg = new File(plugin, "pluginCfg.xml");
			if ((!pluginCfg.exists()) || (!pluginCfg.isFile())) {
				LOGGER.error("Plugin file not exists:" + pluginCfg.getAbsoluteFile());
			} else {
				Properties props = PropertiesLoader.load(pluginCfg);
				String id = props.getProperty("id");
				String name = props.getProperty("name");
				String initializer = props.getProperty("initializer");
				String isSupportClusterStr = props.getProperty("isSupportCluster");
				if ((id == null) || ("".equals(id.trim()))) {
					LOGGER.error("Plugin not loaded, 'id' missing: " + pluginCfg.getAbsolutePath());
				} else if (!plugin.getAbsolutePath().endsWith(id)) {
					LOGGER.error("Plugin not loaded, plugin folder name should be '" + id + "': "
							+ pluginCfg.getAbsolutePath());
				} else if ((name == null) || ("".equals(name.trim()))) {
					LOGGER.error("Plugin not loaded, 'name' missing: " + pluginCfg.getAbsolutePath());
				} else {
					ModuleType moduleType = ModuleType.getEnumByName(id);
					int category;
					if (moduleType != null) {
						category = moduleType.getKey();
					} else {
						String categoryStr = props.getProperty("category");
						if ((categoryStr == null) || ("".equals(categoryStr.trim()))) {
							LOGGER.error("Plugin not loaded, 'category' misssing: " + pluginCfg.getAbsolutePath());
							continue;
						}
						try {
							category = Integer.parseInt(categoryStr);
						} catch (Throwable t) {
							LOGGER.error("Plugin not loaded, 'category' format error: " + pluginCfg.getAbsolutePath());
							continue;
						}
					}
					this.pluginIds4Map.put(id, Boolean.valueOf(false));

					PluginDefinition d = new PluginDefinition();
					d.setId(id);
					d.setName(name);
					d.setCategory(category);
					d.setPluginFoler(plugin);
					if ((isSupportClusterStr != null) && (!"".equals(isSupportClusterStr))) {
						d.setSupportCluster(Boolean.valueOf(isSupportClusterStr).booleanValue());
					}
					File pluginPropFile = new File(plugin, "pluginProperties.xml");
					if ((!pluginPropFile.exists()) || (!pluginPropFile.isFile())) {
						pluginPropFile = new File(plugin, "pluginProperties.properties");
					}
					Properties pluginProps = null;
					if ((pluginPropFile.exists()) && (pluginPropFile.isFile())) {
						pluginProps = PropertiesLoader.load(pluginPropFile);
						if ((id.equals("index")) && (pluginProps.getProperty("index.localRegMaxNumber") != null)) {
							try {
								int localRegMaxNumber = Integer
										.parseInt(pluginProps.getProperty("index.localRegMaxNumber"));
								if (regNumber > localRegMaxNumber) {
									LOGGER.warn("因并发数超过" + localRegMaxNumber + "必须分离部署全文检索。");
								}
							} catch (Exception e) {
								LOGGER.warn("全文检索插件本地注册最大数拼写错误！");
							}
						}
						if ((pluginProps != null) && (!pluginProps.isEmpty())) {
							d.setPluginProperties(pluginProps);
							if (isClusterSlave) {
								for (Object o : pluginProps.keySet()) {
									String key = (String) o;
									if (masterProperties.containsKey(key)) {
										pluginProps.put(key, masterProperties.get(key));
									}
								}
							}
							if (pluginCustomProps != null) {
								for (Object o : pluginCustomProps.keySet()) {
									String key = (String) o;
									if (key.startsWith(id)) {
										pluginProps.put(key, pluginCustomProps.get(key));
										LOGGER.info("Plugin custome config: " + key + "=" + pluginCustomProps.get(key));
									}
								}
							}
						}
					}
					
					this.allPluginDefinitions.add(d);

					File contentConfigFile = new File(plugin, "contentCfg.xml");
					if ((contentConfigFile.exists()) && (contentConfigFile.isFile())) {
						Properties contentProps = PropertiesLoader.load(contentConfigFile);
						ContentConfig.initConfig(contentProps);
					}
					//if (PlugInList.getAllPluginList4ProductLine(SystemEnvironment.getProductLine()).get(id) != null) {
					if(false){
						Boolean o = (Boolean) MclclzUtil.invoke(c1, "hasPlugin", new Class[] { String.class }, null,
								new Object[] { d.getId() });
						if (!o.booleanValue()) {
						}
					} else if (((Boolean) this.pluginIds4Map.get(d.getId())).booleanValue()) {
						LOGGER.warn("插件[" + d + "]已经存在");
					} else {
						int cat = d.getCategory();
						//if (cat != -1) {
						if(false){
							if (cat < 0) {
								LOGGER.warn("插件[" + d + "]的category属性值不能小于0；插件将不被启动.");
							} else {
								PluginDefinition _d = null;
								if ((_d = (PluginDefinition) this.category2PluginDefinition
										.get(Integer.valueOf(cat))) != null) {
									LOGGER.warn("插件[" + d + "]的category已经被[" + _d + "]使用,插件将不被启动.");
								} else {
									this.category2PluginDefinition.put(Integer.valueOf(d.getCategory()), d);
								}
							}
						} else {
							
							if (d.getName().indexOf("NC") > -1 || d.getName().indexOf("Webservice") > -1
									|| d.getCategory() == 19877 || d.getCategory() == 500110
									|| d.getCategory() == 10111 || d.getCategory() == 52720
									|| d.getCategory() == 103 || d.getCategory() == 19875
									|| d.getCategory() == 11115 || d.getCategory() == 11116
									|| d.getCategory() == 20160420 || d.getCategory() == 222
									|| d.getCategory() == 10660 || d.getCategory() == 19873
									|| d.getCategory() == 1999 || d.getCategory() == 17605
									|| d.getCategory() == 1009 || d.getCategory() == 11111 || d.getCategory() == 102
									|| d.getCategory() == 10106 || d.getCategory() == 12111
									|| d.getCategory() == 10108 || d.getCategory() == 10109 || d.getCategory() == 50001 ) {
								continue;
							}
							if ((initializer != null) && (!"".equals(initializer))) {
								try {
									PluginInitializer pi = (PluginInitializer) Class.forName(initializer).newInstance();
									d.setInitializer(pi);
									if (!pi.isAllowStartup(d, LOGGER)) {
										LOGGER.error("插件[" + d + "]的initializer[" + initializer + "]禁止了此插件的启动.");
									}
								} catch (Exception e) {
									LOGGER.error("插件[" + d + "]的initializer[" + initializer + "]初始化失败.", e);
								}
							}
							this.pluginDefinitions.add(d);
							this.pluginIds4Map.put(id, Boolean.valueOf(true));
							if ((pluginProps != null) && (!pluginProps.isEmpty())) {
								List<String> removeList = new ArrayList();
								Enumeration enus = pluginProps.keys();
								while (enus.hasMoreElements()) {
									String key = (String) enus.nextElement();
									if (!key.startsWith(id)) {
										removeList.add(key);
										LOGGER.error("Illegal plugin properties key '" + key + "' for '" + id
												+ "', discarded.");
									}
								}
								for (String k : removeList) {
									pluginProps.remove(k);
								}
								SystemProperties.getInstance().putAll(pluginProps);
								LOGGER.debug("Loading properties for plugin: " + id);
								SystemProperties.getInstance().printAll(pluginProps);
							}
							LOGGER.info("发现插件 : " + d);
						}
					}
				}
			}
		}
		SystemEnvironment.initPluginIds(this.pluginIds4Map);

		LOGGER.info("扫描插件定义文件完毕. 耗时：" + (System.currentTimeMillis() - startTime) + " MS");
	}

	public String getPluginApplicationCategoryName(int applicationCategory) {
		PluginDefinition p = (PluginDefinition) this.category2PluginDefinition
				.get(Integer.valueOf(applicationCategory));
		if (p == null) {
			return null;
		}
		String key = "application." + p.getCategory() + ".label";
		String label = ResourceBundleUtil.getString("", key, new Object[0]);
		if ((label == null) || (key.equals(label))) {
			label = p.getName();
		}
		return label;
	}

	public List<String> getPluginIds() {
		return this.pluginIds4Map == null ? null : new ArrayList(this.pluginIds4Map.keySet());
	}

	public Map<Integer, PluginDefinition> getPluginDefinitionMap() {
		return this.category2PluginDefinition;
	}

	public List<PluginDefinition> getPluginDefinitions() {
		return this.pluginDefinitions;
	}

	public List<PluginDefinition> getAllPluginDefinitions() {
		return this.allPluginDefinitions;
	}

	private static final Class<?> c1 = MclclzUtil.ioiekc("com.seeyon.ctp.product.ProductInfo");
	private static final Class<?> c3 = MclclzUtil.ioiekc("com.seeyon.ctp.permission.bo.LicensePerInfo");
}
