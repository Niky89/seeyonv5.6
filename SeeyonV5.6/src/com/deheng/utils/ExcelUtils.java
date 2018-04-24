package com.deheng.utils;

/**
 * Created by zxj on 2016/4/27.
 */


import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.hssf.util.Region;
import org.apache.poi.ss.util.CellRangeAddress;



import javax.servlet.http.HttpServletResponse;

import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * 利用开源组件POI3.0.2动态导出EXCEL文档
 *

 *            应用泛型，代表任意一个符合javabean风格的类
 *            注意这里为了简单起见，boolean型的属性xxx的get器方式为getXxx(),而不是isXxx()
 *            byte[]表jpg格式的图片数据
 */
public class ExcelUtils<T> {

    /*public  void exportExcel(Collection dataset, OutputStream out) {
        exportExcel("测试POI导出EXCEL文档", null,null, dataset, out, "yyyy-MM-dd");
    }

    public  void exportExcel(String[] headers, Collection dataset,
                            OutputStream out) {
        exportExcel("测试POI导出EXCEL文档", headers,null, dataset, out, "yyyy-MM-dd");
    }*/

    public void excelUtils(String title,String typeValue,String[] headers, String[] fieldName,List<Map<String,Object>> data,
                                   String filePath) {
        exportExcel(title,typeValue, headers,fieldName, data, filePath, "yyyy-MM-dd");
    }

    /**
     * 这是一个通用的方法，利用了JAVA的反射机制，可以将放置在JAVA集合中并且符合一定条件的数据以EXCEL 的形式输出到指定IO设备上
     *
     * @param title
     *            表格标题名
     * @param typeValue
     * 			     查询条件       
     * @param headers
     *            表格属性列名数组
     * @param dataset
     *            需要显示的数据集合,集合中一定要放置符合List<Map<String,Object>>风格的类的对象。此方法支持的
     *            javabean属性的数据类型有基本数据类型及String,Date,byte[](图片数据)
     * @param
     *            //与输出设备关联的流对象，可以将EXCEL文档导出到本地文件或者网络中
     * @param pattern
     *            如果有时间数据，设定输出格式。默认为"yyy-MM-dd"
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public  void exportExcel(String title,String typeValue, String[] headers,String[] sFields,
                            List<Map<String,Object>> data, String filePath, String pattern) {
        OutputStream out = null;

        // 声明一个工作薄
        HSSFWorkbook workbook = new HSSFWorkbook();
        // 生成一个表格
        HSSFSheet sheet = workbook.createSheet(title);
        // 设置表格默认列宽度为15个字节
        sheet.setDefaultColumnWidth((short) 15);
        // 生成一个样式
        HSSFCellStyle style = workbook.createCellStyle();
        // 设置这些样式
        style.setFillForegroundColor(HSSFColor.WHITE.index);
        style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
        style.setBorderBottom(HSSFCellStyle.BORDER_THIN);
        style.setBorderLeft(HSSFCellStyle.BORDER_THIN);
        style.setBorderRight(HSSFCellStyle.BORDER_THIN);
        style.setBorderTop(HSSFCellStyle.BORDER_THIN);
        style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        // 生成一个字体
        HSSFFont font = workbook.createFont();
        font.setColor(HSSFColor.BLACK.index);
        font.setFontHeightInPoints((short) 12);
        font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
        // 把字体应用到当前的样式
        style.setFont(font);
        // 生成并设置另一个样式
        HSSFCellStyle style2 = workbook.createCellStyle();
        style2.setFillForegroundColor(HSSFColor.WHITE.index);
        style2.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
        style2.setBorderBottom(HSSFCellStyle.BORDER_THIN);
        style2.setBorderLeft(HSSFCellStyle.BORDER_THIN);
        style2.setBorderRight(HSSFCellStyle.BORDER_THIN);
        style2.setBorderTop(HSSFCellStyle.BORDER_THIN);
        style2.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        style2.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
        // 生成另一个字体
        HSSFFont font2 = workbook.createFont();
        font2.setBoldweight(HSSFFont.BOLDWEIGHT_NORMAL);
        // 把字体应用到当前的样式
        style2.setFont(font2);

        // 声明一个画图的顶级管理器
        HSSFPatriarch patriarch = sheet.createDrawingPatriarch();
        // 定义注释的大小和位置,详见文档
        HSSFComment comment = patriarch.createComment(new HSSFClientAnchor(0,
                0, 0, 0, (short) 4, 2, (short) 6, 5));
        // 设置注释内容
        comment.setString(new HSSFRichTextString("可以在POI中添加注释！"));
        // 设置注释作者，当鼠标移动到单元格上是可以在状态栏中看到该内容.
        comment.setAuthor("lmep");

        // 产生表格标题行
        HSSFRow row = sheet.createRow(0);
        HSSFCell name = row.createCell(0);
        name.setCellValue(title);
        name.setCellStyle(style);
        sheet.addMergedRegion(new CellRangeAddress(0,0,(short)0,(short)(headers.length-1)));
        
        int index = 1;
        if(!"".equals(typeValue) && typeValue !=null){
        	// 条件行
        	HSSFRow row1 = sheet.createRow(index++);
        	HSSFCell name1 = row1.createCell(0);
        	for (short i = 1; i < headers.length; i++) {
                HSSFCell cell = row1.createCell(i);
                cell.setCellStyle(style);
            }
        	name1.setCellValue(typeValue);
        	sheet.addMergedRegion(new CellRangeAddress(1,1,(short)0,(short)(headers.length-1)));
        	name1.setCellStyle(style);
        }
        
        HSSFRow header = sheet.createRow(index++);
        for (short i = 0; i < headers.length; i++) {
            HSSFCell cell = header.createCell(i);
            cell.setCellStyle(style);
            HSSFRichTextString text = new HSSFRichTextString(headers[i]);
            cell.setCellValue(text);
        }
        

        // 遍历集合数据，产生数据行
        //把dataSet 改成List<Map>
        //遍历list 获取值map
        //遍历map 获取keyset 为列命.value设置 为值         

        HSSFFont font3 = workbook.createFont();
        font3.setColor(HSSFColor.BLACK.index);
        Pattern p = Pattern.compile("^//d+(//.//d+)?$");
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        index=index-1;
        
        for (Map<String,Object> m:data) {//遍历list
        	//获取map
            index++;
            row = sheet.createRow(index);  
           
    		for(int i=0;i<sFields.length;i++){
    			HSSFCell cell = row.createCell(i);
    			
    			cell.setCellStyle(style2);
    			for (Map.Entry e:m.entrySet()) {//遍历map.entryset
    				
        			String str= sFields[i];//得到列名
        			if(str.equals(e.getKey())){//如果得到的列名与遍历map里面的列名一样
        				Object value= e.getValue();
        				String textValue = "";
        				//判断是否为boolean类型的值
        				if (value instanceof Boolean) {
                            boolean bValue = (Boolean) value;
                            textValue = "是";
                            if (!bValue) {
                                textValue = "否";
                            }
                        } else if (value instanceof Date) {//日期类型的值
                            Date date = (Date) value;
                            textValue = sdf.format(date);
                        } else if (value instanceof byte[]) {
                            // 有图片时，设置行高为60px;
                            row.setHeightInPoints(60);
                            // 设置图片所在列宽度为80px,注意这里单位的一个换算
                            sheet.setColumnWidth(i, (short) (35.7 * 80));
                            // sheet.autoSizeColumn(i);
                            byte[] bsValue = (byte[]) value;
                            HSSFClientAnchor anchor = new HSSFClientAnchor(0, 0,
                                    1023, 255, (short) 6, index, (short) 6, index);
                            anchor.setAnchorType(2);
                            patriarch.createPicture(anchor, workbook.addPicture(
                                    bsValue, HSSFWorkbook.PICTURE_TYPE_JPEG));
                        } else {
                            // 其它数据类型都当作字符串简单处理
                            textValue = value+"";
                        }
                        // 如果不是图片数据，就利用正则表达式判断textValue是否全部由数字组成
                        if (textValue != null) {
                        	
                            Matcher matcher = p.matcher(textValue);
                            if (matcher.matches()) {
                                // 是数字当作double处理
                                cell.setCellValue(Double.parseDouble(textValue));
                            } else {
                                HSSFRichTextString richString = new HSSFRichTextString(
                                        "null".equals(textValue)?"":textValue);
                                richString.applyFont(font3);
                              
                                cell.setCellValue(richString);
                                 
                            }
                            
                        }
                        break;
                        
        			}
        		}
        	}
        }
        
        
        try {
            out = new FileOutputStream(filePath);
            workbook.write(out);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }finally {
            if(out!=null)
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }

    }

    public static void downLoadFile(HttpServletResponse response,String filePath)  {
        File file = new File(filePath);
        InputStream fis = null;
        OutputStream toClient = null;

        try {
            response.reset();
            response.setCharacterEncoding("UTF-8");
//            response.setContentType("application/octet-stream");
            response.setContentType("application/x-download");
            response.setHeader("Content-Disposition", "attachment;filename="
                    + new String(file.getName().getBytes("utf-8"), "utf-8"));
            response.setHeader("Content-Length", "" + file.length());
            toClient = new BufferedOutputStream(
                    response.getOutputStream());
            fis = new BufferedInputStream(new FileInputStream(filePath));
            byte[] buffer = new byte[fis.available()];
            fis.read(buffer);
            toClient.write(buffer);
            toClient.flush();
            response.flushBuffer();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if(fis!=null)
                    fis.close();
                if(toClient!=null)
                toClient.close();
                
                File delfile = new File(filePath);
    			delfile.delete();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    public static String cellFormulaAddStr(int[] startArr,String cellEng,int addIndex){
    	String result = "";
    	if(startArr != null && !"".equals(cellEng) && cellEng !=null){
    		for(int index:startArr){
    			result += "+"+cellEng+(index+addIndex);
    		}
    	}
		return result.replaceFirst("\\+", "");
	}
}
