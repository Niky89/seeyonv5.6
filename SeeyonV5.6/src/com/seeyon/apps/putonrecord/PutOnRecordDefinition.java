package com.seeyon.apps.putonrecord;

import com.seeyon.ctp.common.AbstractSystemInitializer;

public class PutOnRecordDefinition extends AbstractSystemInitializer {

	public void destroy() {
		System.out.println("立案插件销毁");
		System.out.println("微信插件销毁");
		//pm.cancel();
	}

	public void initialize() {
		System.out.println("立案插件初始化");
		System.out.println("微信插件初始化");
		//pm.run();
	}
}
