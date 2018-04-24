<%@ page language="java" import="java.util.*,java.io.*"
	pageEncoding="UTF-8"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort()
			+ path + "/";
%>
<!DOCTYPE html>
<html>
<script type="text/javascript">
  var _ctxPath = '/seeyon', _ctxServer = 'http://192.168.1.100:8080/seeyon';
  var _locale = 'zh_CN',_isDevelop = false,_sessionid = 'FACFC3E90C552FE45C5CEB72B6F075C7',_isModalDialog = false;
  var _editionI18nSuffix = '';
  
  var _resourceCode = "";
  var seeyonProductId="2";
</script>
<link href="/seeyon/common/images/A8/favicon.ico?V=V5_1SP1_2014-11-19" type="image/x-icon" rel="icon"/>
<link rel="stylesheet" href="/seeyon/common/all-min.css?V=V5_1SP1_2014-11-19">

<link rel="stylesheet" href="/seeyon/skin/default/skin.css?V=V5_1SP1_2014-11-19">


<script type="text/javascript" src="/seeyon/i18n_zh_CN.js?V=V5_1SP1_2014-11-19"></script>



<script type="text/javascript" src="/seeyon/common/js/ui/calendar/calendar-zh_CN.js?V=V5_1SP1_2014-11-19"></script>

<script type="text/javascript" src="/seeyon/main.do?method=headerjs&login=-633994730"></script>
<script type="text/javascript">

var addinMenus = new Array();


$.ctx._currentPathId = 'form_formData_showLog';
$.ctx._pageSize = 20;
$.ctx.fillmaps = null;

// ä¿è¯æ©å±çjsæåæ§è¡
$(document).ready(function() {
    $(window).load(function() {
        
    });
});

</script>
<script type="text/javascript" src="/seeyon/common/js/orgIndex/jquery.tokeninput.js?V=V5_1SP1_2014-11-19"></script>

<head>
<base href="<%=basePath%>">
<meta charset="utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<title>立案 管理系统</title>
<%@ include file="/WEB-INF/jsp/common/common.jsp"%>
<script type="text/javascript"
src="${path}/ajax.do?managerName=ncBusiBindManager"></script>


	<style type="text/css">
	input {
		font-size: 18px !important;
	}
	</style>
</head>

<body class="hold-transition skin-blue sidebar-mini sidebar-collapse"
	style="text-align: center;">

	<!-- Content Wrapper. Contains page content -->
	<div class="content-wrapper">
		<!-- Content Header (Page header) -->
		<section class="content-header">
			<h1>案件信息查询</h1>
		</section>

		<!-- Main content -->

		<!-- 主要内容显示区 -->
		<form action="collisionDetectionSuccess.action"
			class="form-horizontal" method="post" name="form" id="form"
			style="text-align: left;">
			 
						<table style="align: center; margin: auto;">
							
							
							<tr id="row1-1">
								<td  id="kehuname1" style="text-align: right;"><label for="contrayClient" >客户名称：</label>
								<input type="text" class="form-control"	name="kehuname" id="kehuname"></td>
								<td  id="ajbh1" style="text-align: right;"><label	for="contrayClient" >案件编号：</label>
								<input type="text" name="ajbh" id="ajbh" /></td> 						
								<td  colspan=2 id="latime" ><label	for="contrayClient" >&nbsp;&nbsp;立案时间：</label>		
								<input id="mycal1" type="text" class="comp" comp="type:'calendar',ifFormat:'%Y-%m-%d ',showsTime:false">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;到&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<input id="mycal2" type="text" class="comp" comp="type:'calendar',ifFormat:'%Y-%m-%d ',showsTime:false">
								</td>	
							</tr>
							<tr id="row2-1">
								<td   id="khstyle1" style="text-align: right;"><label	for="contrayClient"  >客户类型：</label>
								<input type="text" name="khstyle" id="khstyle" /></td> 
								<td   id="ajlb1" style="text-align: right;"><label	for="contrayClient" >案件类型：</label>
								<input type="text" name="ajlb" id="ajlb" /></td> 
								<!-- <td id="ajstate1"><label  for="contrayClient" class="col-sm-2 control-label">案件状态：</label>
								<input type="text" name="ajstate" id="ajstate" /></td>  -->
								<td  id="zblawyer1" style="text-align: right;"><label	for="contrayClient"  >&nbsp;&nbsp;主办律师：</label>
								<input type="text" name="zblawyer" id="zblawyer" /></td>
								<td  id="hylb1" style="text-align: right;" ><label	for="contrayClient" >行业类别：</label>
								<input type="text" name="hylb" id="hylb" /></td> 		
							</tr>
							<tr id="row3-1">
								<td  id="khjb1" style="text-align: right;"><label	for="contrayClient"  >客户级别：</label>
								<input type="text" name="khjb" id="khjb" /></td> 
								<td  id="ahmainyw1" style="text-align: right;"><label	for="contrayClient"  >&nbsp;&nbsp;客户主营业务：</label>
								<input type="text" name="ahmainyw" id="ahmainyw" /></td> 
								<td   id="ayperson1" style="text-align: right;"><label	for="contrayClient"  >&nbsp;&nbsp;案&nbsp;源&nbsp;人&nbsp;：</label>
								<input type="text" name="ayperson" id="ayperson" /></td> 
								<td   id="tbbgs1"  style="text-align: right;"><label	for="contrayClient"  >&nbsp;&nbsp;填报办公室：</label>
								<input type="text" name="tbbgs" id="tbbgs" /></td> 	
							</tr>
							
							<tr id="row4-1">
							
								<td   id="dycity1" style="text-align: right;"><label for="contrayClient"  >地&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;域：</label>
									<input type="text" name="dycity" id="dycity" /></td> 
								<td id="department1"  style="text-align: right;"><label	for="client"  >部&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;门：</label>
									<input type="text" class="form-control"	name="department" id="department"></td>	
							</tr>	
							<tr>
								<td colspan="8 " style="text-align: center;font-size: 18px;" >
								&nbsp;<br/>
									<div class="btn-group pull-right" role="group">
										<a class="common_button common_button_gray" onclick="query()"><i
											class="fa  fa-send-o fa-fw"></i><label style="font-size:18px;"> 查询</label></a><!--  <a
											class="common_button common_button_gray" id="nextstep"
											onclick="$('#form').submit()">
											<i
											class="fa fa-arrow-right fa-fw"></i>下一步</a> <a
											style="display: none;"
											class="common_button common_button_gray" id="quit"
											onclick="window.location.href='contain.jsp'">退出</a>-->
											&nbsp;&nbsp;&nbsp;<a class="common_button common_button_gray" onclick="dcExcel()"><i
											class="fa  fa-send-o fa-fw"></i><label style="font-size:18px;"> 导出EXCEL</label></a>
									</div>
								</td>
							</tr>
						</table>
						<table class="flexme3 " style="display: none" id="mytable"></table>
						<div id="grid_detail">
							<div id="titleDiv" class="clearfix margin_t_20 margin_b_10"
								style="display: none">
								<h2 class="left margin_0">信息管理</h2>
								<div class="font_size12 left margin_l_10">
									<div class="margin_t_10 font_size14">
										总计 <span class="font_bold color_black">4</span> 条
									</div>
								</div>
							</div>
							<iframe id="viewFrame" class="calendar_show_iframe" src=""
								width="100%" height="100%" frameborder="no"></iframe>
						</div>
		</form>


	</div>
	<script type="text/javascript"
		src="<%=basePath%>ajax.do?managerName=anjianmanager"></script>
	<!-- /主要内容显示区 -->
	<script type="text/javascript">
	//导出excel
	
	function dcExcel(){
		
		var gridPramsObj =new Object();
		//kehuname=" + kehuname + "&latime1=" + latime1 +"&latime2=" + latime2 + "&zblawyer=" + zblawyer + "&ajbh=" + ajbh + "&khstyle=" + khstyle + "&ajlb=" + ajlb + "&hylb=" + hylb + "&khjb=" + khjb + "&ahmainyw=" + ahmainyw + "&ayperson=" + ayperson + "&tbbgs=" + tbbgs + "&dycity=" + dycity + "&department=" + department
		gridPramsObj.kehuname =  document.getElementById("kehuname").value;	
		gridPramsObj.latime1 = document.getElementById("mycal1").value;
		gridPramsObj.latime2 = document.getElementById("mycal2").value;
		gridPramsObj.zblawyer = document.getElementById("zblawyer").value;
		gridPramsObj.ajbh = document.getElementById("ajbh").value;
		gridPramsObj.khstyle = document.getElementById("khstyle").value;
		gridPramsObj.ajlb = document.getElementById("ajlb").value;
		//gridPramsObj.ajstate = document.getElementById("ajstate").value;
		gridPramsObj.hylb = document.getElementById("hylb").value;
		gridPramsObj.khjb = document.getElementById("khjb").value;
		gridPramsObj.ahmainyw = document.getElementById("ahmainyw").value;
		gridPramsObj.ayperson = document.getElementById("ayperson").value;
		gridPramsObj.tbbgs = document.getElementById("tbbgs").value;
		gridPramsObj.dycity = document.getElementById("dycity").value;
		gridPramsObj.department = document.getElementById("department").value;
		
		var url = "<%=basePath%>putonrecord/anjiancontroller.do?method=dcExcel&kehuname=" + gridPramsObj.kehuname + "&latime1=" + gridPramsObj.latime1 + "&latime2=" + gridPramsObj.latime2 +  "&zblawyer=" + gridPramsObj.zblawyer + "&ajbh=" + gridPramsObj.ajbh + "&khstyle=" + gridPramsObj.khstyle + "&ajlb=" + gridPramsObj.ajlb + "&hylb=" + gridPramsObj.hylb + "&khjb=" + gridPramsObj.khjb + "&ahmainyw=" + gridPramsObj.ahmainyw + "&ayperson=" + gridPramsObj.ayperson + "&tbbgs=" + gridPramsObj.tbbgs + "&dycity=" + gridPramsObj.dycity + "&department=" + gridPramsObj.department;
		//window.open(url);
		openCtpWindow({"url":url});

		 
	}
	function query()
	{
		var gridPramsObj =new Object();
		
		gridPramsObj.kehuname =  document.getElementById("kehuname").value;	
		gridPramsObj.latime1 = document.getElementById("mycal1").value;
		gridPramsObj.latime2 = document.getElementById("mycal2").value;
		gridPramsObj.zblawyer = document.getElementById("zblawyer").value;
		gridPramsObj.ajbh = document.getElementById("ajbh").value;
		gridPramsObj.khstyle = document.getElementById("khstyle").value;
		gridPramsObj.ajlb = document.getElementById("ajlb").value;
		//gridPramsObj.ajstate = document.getElementById("ajstate").value;
		gridPramsObj.hylb = document.getElementById("hylb").value;
		gridPramsObj.khjb = document.getElementById("khjb").value;
		gridPramsObj.ahmainyw = document.getElementById("ahmainyw").value;
		gridPramsObj.ayperson = document.getElementById("ayperson").value;
		gridPramsObj.tbbgs = document.getElementById("tbbgs").value;
		gridPramsObj.dycity = document.getElementById("dycity").value;
		gridPramsObj.department = document.getElementById("department").value;
		
		var mytable = $("#mytable").ajaxgrid({
			
			 //click: gridclk,
			 // dblclick: griddblclick,
			  colModel: [
			    {
			      display: '案件编号',
			      name: 'ajbh',
			      width: '10%',
			      sortable: true,
			      align: 'center'
			     
			    },
			    {
			      display: '案件名称',
			      name: 'ajmc',
			      width: '15%',
			      sortable: true,
			      align: 'center'
			     
			    },
			    {
			      display: '主办律师',
			      name: 'zblawyer',
			      width: '5%',
			      sortable: true,
			      align: 'center'
			     
			    },
			    {
			      display: '填报人',
			      name: 'tbname',
			      width: '5%',
			      sortable: true,
			      align: 'center'
			     
			    },
			    {
			      display: '所属部门',
			      name: 'department',
			      width: '10%',
			      sortable: true,
			      align: 'center'
			     
			    },
			    {
			      display: '办公室',
			      name: 'tbbgs',
			      width: '5%',
			      sortable: true,
			      align: 'center'
			     
			    },
			    {
			      display: '案件类别',
			      name: 'ajlb',
			      width: '15%',
			      sortable: true,
			      align: 'center'
			     
			    },
			    {
			      display: '立案时间',
			      name: 'latime',
			      width: '8%',
			      sortable: true,
			      align: 'center'
			     
			    },
			    {
			      display: '案件标的额',
			      name: 'ajbde',
			      width: '5%',
			      sortable: true,
			      align: 'center'
			     
			    },
			    {
			      display: '特殊案件',
			      name: 'sfsytsaj',
			      width: '5%',
			      sortable: true,
			      align: 'center'
			     
			    },
			    {
			      display: '审级',
			      name: 'sj',
			      width: '3%',
			      sortable: true,
			      align: 'center'
			     
			    },
			    {
			      display: '案源人',
			      name: 'ayperson',
			      width: '5%',
			      sortable: true,
			      align: 'center'
			     
			    },
			    {
			      display: '案件简况',
			      name: 'ajjk',
			      width: '15%',
			      sortable: true,
			      align: 'center'
			     
			    },
// 			    {
// 			      display: '类别',
// 			      name: 'hylb',
// 			      width: '5%',
// 			      sortable: true,
// 			      align: 'center'
			     
// 			    },
			    			    
			    {
			      display: '委托人',
			      name: 'wtname',
			      width: '10%',
			      sortable: true,
			      align: 'center'
				     
				    },
			    {
			      display: '对方当事人',
			      name: 'dfname',
			      width: '10%',
			      sortable: true,
			      align: 'center'
			     
			    },
			    
			    {
			      display: '展业费使用分配人员姓名',
			      name: 'zyfname',
			      width: '8%',
			      sortable: true,
			      align: 'center'
			     
			    },
			    {
			      display: '展业费使用分配人员部门',
			      name: 'zyfdepartment',
			      width: '8%',
			      sortable: true,
			      align: 'center'
			     
			    },
			    {
			      display: '展业费使用分配比例',
			      name: 'zyfbl',
			      width: '8%',
			      sortable: true,
			      align: 'center'
			     
			    }],
			    managerName: "anjianmanager",
			    managerMethod: "queryAnJian",
			    parentId: 'center',
			    callBackTotle:callBackTotal,
			    vChangeParam: {
				      overflow: 'hidden',
				      position: 'relative'
				},
			    slideToggleBtn: true,
			    showTableToggleBtn: true,
			    //vChange: true
			    
			  });
		
		
		$("#mytable").ajaxgridLoad(gridPramsObj);
		
		
		
		
	}
	function gridclk(data, r, c) {
		alert(111);
		
	}
	function griddblclick() {
		alert(222);
	
	}
	function callBackTotal(n){
		$("#titleDiv").find("div>div>span").html(n);
	}
	
	</script>
</html>