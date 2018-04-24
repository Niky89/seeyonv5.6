package com.deheng.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RunCommand {

	public String run(String command) {
		StringBuffer sb = new StringBuffer();
		Runtime r = Runtime.getRuntime();
		try {
			Process p = r.exec(command);
			InputStream fis = p.getInputStream();
			// 用一个读输出流类去读
			BufferedReader br = new BufferedReader(new InputStreamReader(fis));
			String str = "";
			while ((str = br.readLine()) != null) {
				sb.append(str);
			}
		} catch (IOException e) {
			e.printStackTrace();
			return "false";
		}
		return sb.toString().toLowerCase();
	}

	public List<String> domainList() {
		Util u = new Util();
		Map<String, String> map = u.getAllPorperty();
		List<String> list = new ArrayList<String>();
		for (Map.Entry<String, String> e : map.entrySet()) {
			if (e.getKey().startsWith("domain")) {
				list.add(e.getValue());
			}
		}
		return list;
	}

	public static void main(String[] args) {
		RunCommand cmd = new RunCommand();
		// String resualt =
		// cmd.run("checkuser.exe sailun.com songxiaolong sxl19890407@");
		// System.out.println(resualt);
		List<String> strs = cmd.domainList();
		for (String s : strs) {
			System.out.println(s);
		}
	}

}
