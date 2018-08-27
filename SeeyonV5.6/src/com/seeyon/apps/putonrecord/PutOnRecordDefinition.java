package com.seeyon.apps.putonrecord;

import java.util.Calendar;
import java.util.Date;

import com.deheng.Quartz.updatePreFilingTask;
import com.seeyon.ctp.common.AbstractSystemInitializer;
import com.seeyon.ctp.common.timer.TimerHolder;

public class PutOnRecordDefinition extends AbstractSystemInitializer {

	public void destroy() {
		System.out.println("立案插件销毁");
		System.out.println("微信插件销毁");
		// pm.cancel();
	}
	private static final long PERIOD_DAY =24 * 60 * 60 * 1000;//一天執行一次 24 * 60 * 60 * 1000
	public void initialize() {
		System.out.println("立案插件初始化");
		System.out.println("微信插件初始化");

		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.HOUR_OF_DAY, 14); // 凌晨1点
		calendar.set(Calendar.MINUTE, 5);
		calendar.set(Calendar.SECOND, 0);
		Date date = calendar.getTime(); // 第一次执行定时任务的时间
		// 如果第一次执行定时任务的时间 小于当前的时间
		// 此时要在 第一次执行定时任务的时间加一天，以便此任务在下个时间点执行。如果不加一天，任务会立即执行。
		if (date.before(new Date())) {
			date = this.addDay(date, 1);
		}
		
		TimerHolder.newTimer(new updatePreFilingTask(), date, PERIOD_DAY);
		// pm.run();
	}

	// 增加或减少天数
	public Date addDay(Date date, int num) {
		Calendar startDT = Calendar.getInstance();
		startDT.setTime(date);
		startDT.add(Calendar.DAY_OF_MONTH, num);
		return startDT.getTime();
	}

}
