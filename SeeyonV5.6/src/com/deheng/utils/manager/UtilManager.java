package com.deheng.utils.manager;

import com.seeyon.ctp.common.*;
import com.seeyon.ctp.util.*;
import java.util.*;
import com.seeyon.ctp.common.exceptions.*;
import com.seeyon.ctp.common.authenticate.domain.*;
import java.sql.*;
import com.seeyon.ctp.util.annotation.*;

public class UtilManager
{
    @AjaxAccess
    public Map<String, String> getBanGongShi() {
        final Map<String, String> resultMap = new HashMap<String, String>();
        final User user = AppContext.getCurrentUser();
        final long departmentId = user.getDepartmentId();
         JDBCAgent dba = new JDBCAgent(true);
         String sql = "select path from org_unit where id="+departmentId;
        try {
            dba.execute(sql);
            ResultSet result = dba.getQueryResult();
            String path = null;
            if (result.next()) {
                path = result.getString("path");
            }
            if (path.length() > 16) {
                path = path.substring(0, path.length() - 4);
            }
            final String sql2 = "select * from org_unit where IS_DELETED=0 and IS_ENABLE=1 and path='" + path + "'";
            dba.execute(sql2);
            result = dba.getQueryResult();
            if (result.next()) {
                resultMap.put(result.getString("name"), String.valueOf(result.getString("type")) + "|" + result.getLong("id"));
            }
        }
        catch (BusinessException e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
        catch (SQLException e2) {
            e2.printStackTrace();
            System.out.println(e2.getMessage());
        }catch(Exception e3){
        	System.out.println(e3.getMessage());
        }
        dba.close();
        return resultMap;
    }
    
    @AjaxAccess
    public Map<String, String> getBanGongShiByUserId(final String userid) {
        final Map<String, String> resultMap = new HashMap<String, String>();
         JDBCAgent dba = new JDBCAgent(true);
       
         String depSql = "select b.id as id from org_member a   left join  org_unit b on a.org_department_id= b.id  where a.id=" + userid;
        long departmentId = 0L;
        try {
            dba.execute(depSql);
            ResultSet result = dba.getQueryResult();
            if (!result.next()) {
                return resultMap;
            }
            departmentId = result.getLong("id");
            final List<String> param = new ArrayList<String>();
            param.add(new StringBuilder(String.valueOf(departmentId)).toString());
            final String sql = "select * from org_unit where id=?";  
            dba.execute(sql, param);
            result = dba.getQueryResult();
            String path = null;
            if (result.next()) {
                path = result.getString("path");
            }
            final String sql2 = "select * from org_unit where IS_DELETED=0 and IS_ENABLE=1 and path='" + path.substring(0, path.length() - 4) + "'";
            dba.execute(sql2);
            result = dba.getQueryResult();
            if (result.next()) {
                resultMap.put(result.getString("name"), String.valueOf(result.getString("type")) + "|" + result.getLong("id"));
            }
        }
        catch (BusinessException e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
        catch (SQLException e2) {
            e2.printStackTrace();
            System.out.println(e2.getMessage());
        }catch(Exception e3){
        	System.out.println(e3.getMessage());
        }
        dba.close();
        return resultMap;
    }
}
