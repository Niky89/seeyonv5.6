package com.deheng.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import com.seeyon.ctp.common.init.MclclzUtil;
import com.seeyon.ctp.common.init.Xcyskm;

public class LoadSeeyonClass {
	public static void main(String[] args) throws IOException {
		Xcyskm x = new Xcyskm(MclclzUtil.class.getClassLoader());

		//byte[] data = x.loadClassData("com.seeyon.ctp.product.ProductInfo");
		byte[] data = x.loadClassData("com.seeyon.ctp.product.ProductInfo");
		File f = new File("D:/ProductInfo.class");
		FileOutputStream fos = new FileOutputStream(f);
		fos.write(data);
		fos.flush();
		fos.close();
	}
}
