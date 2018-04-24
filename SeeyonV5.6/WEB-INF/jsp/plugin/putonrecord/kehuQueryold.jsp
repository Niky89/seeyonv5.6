<%@ page language="java" import="java.util.*,java.io.*"
	pageEncoding="UTF-8"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort()
			+ path + "/";
%>
<!DOCTYPE html>
<html>

<head>
<base href="<%=basePath%>">
<meta charset="utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<title>立案 管理系统</title>
<%@ include file="/WEB-INF/jsp/common/common.jsp"%>
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
			<h1>集团客户查询（公测版）</h1>
		</section>

		<!-- Main content -->

		<!-- 主要内容显示区 -->
		<form action="collisionDetectionSuccess.action"
			class="form-horizontal" method="post" name="form" id="form"
			style="text-align: center;">
			<table width='100%' style="text-align: center;">
				<tr>
					<td>
						<table style="text-align: center; margin: auto;">

							<tr id="row1-1">
								<td id="wtlabel"
									style="text-align: right; font-size: 18px; display: none"><label
									for="client" class="col-sm-2 control-label">
										<!-- 委托人全称： -->
								</label></td>
								<td style="display: none"><input type="hidden"
									name="wtcount" id="wtcount" value="1"> <input
									type="text" class="form-control" name="wt1" id="wt1"
									maxlength="20" size="30"></td>
								<td>
									<!-- <select name="wttype1" class="form-control">
										<option value=0>单位</option>
										<option value=1>个人</option>
								</select>
								 -->
								</td>
								<td id="wtaction" style="display: none">
									<DIV id="wtaddDiv">
										<span id="addImg" class="ico16 repeater_plus_16"></span>
									</DIV>
									<DIV id="wtdelDiv" style="display: none;">
										<span id="delImg" class="ico16 repeater_reduce_16"></span>
									</DIV>
								</td>
							</tr>
							<tr>
								<td colspan="4" height="50px;" style="display: none"><hr /></td>
							</tr>
							<tr id="row2-1">
								<td style="text-align: right; font-size: 18px;" id="dflabel"><label
									for="contrayClient" class="col-sm-2 control-label">客户名称：</label></td>
								<td><input type="hidden" name="dfcount" id="dfcount"
									value="1"> <input type="text" class="form-control"
									name="df1" id="df1" size="30"
									onkeydown="if(event.keyCode==13){check();return false;}" /></td>
								<td>
									<!-- <select name="dftype1" class="form-control">
										<option value=0>单位</option>
										<option value=1>个人</option>
								</select>
								 -->
								</td>
								<td id="dfaction">
									<DIV id="dfaddDiv">
										<span id="addImg" class="ico16 repeater_plus_16"></span>
									</DIV>
									<DIV id="dfdelDiv" style="display: none;">
										<span id="delImg" class="ico16 repeater_reduce_16"></span>
									</DIV>
								</td>
							</tr>
							<tr>
								<td colspan="4" style="text-align: right;">
									<div class="btn-group pull-right" role="group">
										<a class="common_button common_button_gray" name="check"
											id="mycheck"><i class="fa  fa-send-o fa-fw"></i>查询</a>
										<!--  <a
											class="common_button common_button_gray" id="nextstep"
											onclick="$('#form').submit()">
											<i
											class="fa fa-arrow-right fa-fw"></i>下一步</a> <a
											style="display: none;"
											class="common_button common_button_gray" id="quit"
											onclick="window.location.href='contain.jsp'">退出</a>-->
									</div>
								</td>
							</tr>
						</table>
					</td>
				</tr>
			</table>
		</form>

		<div id="content">
			<font size="5" id="message"> </font>
			<table width="1300" id="wttable" border="1" cellspacing="0" bordercolor="#000000"
				width="80%" style="border-collapse: collapse; margin: auto;">
			</table>
			<br> <br> <br> <font size="5" id="message1"> </font>
			<table width="1300" id="dftable" border="1" cellspacing="0" bordercolor="#000000"
				width="80%" style="border-collapse: collapse; margin: auto;"></table>
			<br> <br> <br> <font size="5" id="message2"> </font>
			<table width="1300" id="dftable2" border="1" cellspacing="0" bordercolor="#000000"
				width="80%" style="border-collapse: collapse; margin: auto;"></table>
		</div>

	</div>
	<script type="text/javascript"
		src="<%=basePath%>ajax.do?managerName=coimanager"></script>
	<!-- /主要内容显示区 -->
	<script type="text/javascript">
		var wtalljson = "";
		$(document).ready(function() {
			//默认隐藏删除委托人和对方当事人按钮
			//添加按钮事件
			$("#mycheck").attr("onclick", "check();")
			console.log("绑定事件")
			$("#wtaddDiv").attr("onclick", "addwt();")
			$("#wtdelDiv").attr("onclick", "delwt();")
			$("#dfaddDiv").attr("onclick", "adddf();")
			$("#dfdelDiv").attr("onclick", "deldf();")
			//bindWtData("wt1");
		});

		//添加委托人
		function addwt() {
			//修改计数器
			var wtcount = parseInt($("#wtcount").val());
			wtcount = wtcount + 1;
			$("#wtcount").val((wtcount).toString());
			var html = '<tr id="row1-'+wtcount+'"><td><input type="text" class="form-control" name="wt'+wtcount+'" id="wt'+wtcount+'" size="30"></td>'
					+ '<td></td>' + '</tr>';
			$("#row1-" + (wtcount - 1)).after(html);

			//修改label 跨行
			$("#wtlabel").attr("rowspan", wtcount);
			$("#wtaction").attr("rowspan", wtcount);

			$("#wtdelDiv").show();
		}

		//添加对方当事人
		function adddf() {
			//修改计数器
			var dfcount = parseInt($("#dfcount").val());
			dfcount = dfcount + 1;
			$("#dfcount").val((dfcount).toString());
			var html = '<tr id="row2-'+dfcount+'"><td><input type="text" class="form-control" name="df'+dfcount+'" id="df'+dfcount+'" size="30"></td>'
					+ '<td></td>' + '</tr>';
			$("#row2-" + (dfcount - 1)).after(html);

			//修改label 跨行
			$("#dflabel").attr("rowspan", dfcount);
			$("#dfaction").attr("rowspan", dfcount);

			$("#dfdelDiv").show();
		}

		//删除委托人
		function delwt() {
			var wtcount = parseInt($("#wtcount").val());
			$("#row1-" + wtcount).remove();
			$("#wtcount").val((wtcount - 1).toString());

			//修改label 跨行
			$("#wtlabel").attr("rowspan", wtcount - 1);
			$("#wtaction").attr("rowspan", wtcount - 1);

			if (wtcount - 1 == 1) {
				$("#wtdelDiv").hide();
			}
		}

		//删除对方当事人
		function deldf() {
			var dfcount = parseInt($("#dfcount").val());
			$("#row2-" + dfcount).remove();
			$("#dfcount").val((dfcount - 1).toString());

			//修改label 跨行
			$("#dflabel").attr("rowspan", dfcount - 1);
			$("#dfaction").attr("rowspan", dfcount - 1);

			if (dfcount - 1 == 1) {
				$("#dfdelDiv").hide();
			}
		}

		//检测利益冲突
		//调用ajax验证是否冲突锁定当前页面不能操作
		function check() {
			//采用 平台提供的ajax 调用manager 方式调用
			var coim = new coimanager();
			//遍历创建提交后台的js对象
			var wtcount = parseInt($("#wtcount").val());
			var dfcount = parseInt($("#dfcount").val());
			var wtma = new Array();
			var dfma = new Array();

			for (var i = 1; i <= wtcount; i++) {
				var obj = new Object();
				obj["name"] = $('#wt' + i).val();
				wtma[i - 1] = obj;
			}

			for (var i = 1; i <= dfcount; i++) {
				var obj = new Object();
				obj["name"] = $('#df' + i).val();
				dfma[i - 1] = obj;
			}
			//锁定页面操作等待返回
			$("#message").html("正在验证请稍候...");
			$("#mycheck").attr("disabled", "disabled");
			$("#nextstep").attr("disabled", "disabled");
			// $('input').attr("disabled","disabled")//将input元素设置为disabled
			//$('input').removeAttr("disabled");//去除input元素的disabled属性
			/*
			 coim
			 .checkConflictOfInterest(
			 wtcount,
			 wtma,
			 dfcount,
			 dfma,
			 1,
			 {
			 success : function(data) {
			 //解析json生成table 放到message中去
			 var jsonobj = eval("(" + data + ")");
			 var wtmessage = jsonobj["wt"];

			
			
			 var tr = "<tr style='background-color: #5ba0d0;'><td>客户冲突</td><td>主办律师</td></tr>";
			 var add = false;
			 if (wtmessage != undefined) {
			
			 $.each(wtmessage,
			 function(name, value) {
			 add = true;
			 tr += "<tr><td>" + name
			 + "</td><td>"
			 + value["zbls"]
			 + "</td></tr>";
			 });
			 } else {
			 $("#wttable")
			 .html(
			 "<tr style='background-color: #5ba0d0;'><<td>无利益冲突</td></tr>");
			 }

			 if (add) {
			 $("#wttable").html(tr);
			 add = false;
			 } else {
			 $("#wttable").html(
			 "<tr><td style='font-size:18px;display:none' >委托人没有冲突</td></tr>");

			 }
			 $("#check").removeAttr("disabled");
			 $("#nextstep").removeAttr("disabled");
			 }
			 }); */
			coim
					.checkConflictOfInterest(
							wtcount,
							wtma,
							dfcount,
							dfma,
							2,
							{
								success : function(data) {
									//解析json生成table 放到message中去
									var jsonobj = eval("(" + data + ")");
									var dfmessage = jsonobj["df"];

									//$("#message1").html(dfmessage==undefined?"11":dfmessage);

									var tr = "<tr style='background-color: #3882d0;'><td width="100"><b>单位类型</b></td><td width="100"><b>客户状态</b></td><td>客户名称</td><td width="100">主办律师</td></tr>";
									var add = false;
									/* if(df1!=undefined){
										add=true;
									for(var i=0;i<df1.length;i++){
										var temp=df1[i].split("*");
										tr+="<tr><td>"+temp[0]+"</td><td>"+temp[1]+"</td><td>"+temp[2]+"</td></tr>";
									}
									}
									if(df2!=undefined){
										add=true;
									for(var i=0;i<df2.length;i++){
										var temp=df2[i].split("*");
										tr+="<tr><td>"+temp[0]+"</td><td>"+temp[1]+"</td><td>"+temp[2]+"</td></tr>";
									}
									}*/
									if (dfmessage != undefined) {

										$.each(dfmessage,
												function(name, value) {
													add = true;
													tr += "<tr><td>"
															+ value["ls"]
															+ "</td><td>"
															+ value["latype"]
															+ "</td><td>"
															+ name
															+ "</td><td>"
															+ value["zbls"]
															+ "</td></tr>";
												});
									} else {
										$("#dftable")
												.html(
														"<tr style='background-color: #5ba0d0;'><td style='font-size:18px;'>无利益冲突</td></tr>");
									}

									if (add) {
										$("#dftable").html(tr);
										add = false;
									} else {
										$("#dftable").html(
												"<tr><td>没有查询到此客户</td></tr>");
									}
									$("#mycheck").removeAttr("disabled");
									$("#nextstep").removeAttr("disabled");
									$("#message").html("");
								}
							});

			//增加的ajax预立案客户
			console.log("准备进行第三次ajax请求");
			coim
					.checkConflictOfInterest(
							wtcount,
							wtma,
							dfcount,
							dfma,
							3,
							{
								success : function(data) {
									console.log("第三次ajax请求成功！");
									console.log(data);
									//解析json生成table 放到message中去
									var jsonobj = eval("(" + data + ")");
									var dfmessagey = jsonobj["dfy"];
									//$("#message1").html(dfmessage==undefined?"11":dfmessage);
									var tr2 = '<tr><td border=0 colspan="5"><font size="5" id="message2" style="display: none;">&nbsp;&nbsp;集团预立案客户:</font> </td></tr>'
											+ "<tr style='background-color: #3882d0;'><td width="100">&nbsp;&nbsp;<b>单位类型</b></td><td width="100">&nbsp;&nbsp;<b>客户状态</b></td><td>&nbsp;&nbsp;<b>名称</b></td><td width="100">&nbsp;&nbsp;<b>主办律师</b></td><td width="100">&nbsp;&nbsp;<b>立案时间</b></td></tr>";
									var add2 = false;
									if (dfmessagey != undefined) {

										$
												.each(
														dfmessagey,
														function(name, value) {
															add2 = true;
															tr2 += "<tr><td>&nbsp;&nbsp;"
																	+ value["ls"]
																	+ "</td><td>&nbsp;&nbsp;"
																	+ value["latype"]
																	+ "</td><td>&nbsp;&nbsp;"
																	+ name
																	+ "</td><td>&nbsp;&nbsp;"
																	+ value["zbls"]
																	+ "</td><td>&nbsp;&nbsp;"
																	+ value["latime"]
																	+ "</td></tr>";

														});

									}
									if (add2) {
										$("#dftable2").html(tr2);
										$("#dftable2").attr("border", "1");
										add2 = false;
									} else {
										$("#dftable2")
												.html(
														"<tr><td>预立案对方当事人没有冲突</td></tr>");
										$("#dftable2").attr("border", "0");
									}
									if (isok) {
										$("#message2").html("");
									}
									$("#mycheck").removeAttr("disabled");
									$("#nextstep").removeAttr("disabled");
									$("#message2").css("display", "block");
									$("#ma").css("color", "#C0C0C0");
									isok = true;
								}
							});
		}

		//绑定委托人查询
		function bindWtData(id) {
			reloadwtalljson(id);
			$('#' + id).combobox({
				data : wtalljson,
				valueField : 'id',
				onChange : function(data) {
					$('#' + id).combobox("reload");
				}
			}); //加入onchange 事件一次只取出20条数据
		}
		//重新加载委托人 数据
		function reloadwtalljson(id) {
			var input = $('#' + id).combobox("getValue");
			$
					.post(
							"/seeyon/putonrecord/conflictofinterestcontroller.do?method=getAllPrincipal",
							{
								param : input
							}, function(result) {
								eval(result);
							});
			//alert(wtalljson);
		}
	</script>
</html>