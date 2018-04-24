package com.seeyon.v3x.system.util;

import com.seeyon.ctp.common.excel.DataRecord;
import com.seeyon.ctp.common.excel.DataRow;
import com.seeyon.ctp.common.excel.FileToExcelManager;
import java.util.List;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ExportToExcelUtil
{
  private static Log log = LogFactory.getLog(ExportToExcelUtil.class);
  
  public static void exportToExcel(HttpServletResponse response, FileToExcelManager fileToExcelManager, String fileName, List<Object[]> data, String[] columnName, String title, String sheetName)
  {
    DataRecord record = new DataRecord();
    DataRow row = null;
    DataRow[] rows = (DataRow[])null;
    try
    {
      if ((data != null) && (!data.isEmpty()))
      {
        rows = new DataRow[data.size()];
        int i = 0;
        for (Object[] obj : data)
        {
          row = new DataRow();
          Object[] arrayOfObject1;
          int j = (arrayOfObject1 = obj).length;
          for (int ii = 0; ii < j; ii++)
          {
            Object cell = arrayOfObject1[ii];
            row.addDataCell(cell == null ? "" : cell.toString(), 1);
          }
          rows[i] = row;
          i++;
        }
        record.addDataRow(rows);
      }
      record.setColumnName(columnName);
      if (title != null) {
        record.setTitle(title);
      }
      if (sheetName != null) {
        record.setSheetName(sheetName);
      }
      fileToExcelManager.save(response, fileName, new DataRecord[] { record });
    }
    catch (Exception e)
    {
      log.error("", e);
    }
  }
}
