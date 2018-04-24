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
<meta name="viewport" content="width=device-width, initial-scale=1" />
<title></title>
<%@ include file="/WEB-INF/jsp/common/common.jsp"%>
<link rel="stylesheet"
	href="/seeyon/common/css/weui.min.css" type="text/css" />

	<script type="text/javascript" src="http://res.wx.qq.com/open/js/jweixin-1.0.0.js"></script>
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
			<br/>
			<h3>集团客户查询(公测版)</h3>
		</section>

		<!-- Main content -->

		<!-- 主要内容显示区 -->
		<form action="collisionDetectionSuccess.action"
			class="form-horizontal" method="post" name="form" id="form"
			style="text-align: center;">
			<table width='100%' style="text-align: center;">
				<tr>
					<td>
						<table style="text-align: center; margin: auto;" width="100%">
						
							<tr id="row1-1">
								<td id="wtlabel" style="text-align: right;font-size: 18px;display:none" ><label
									for="client" class="col-sm-2 control-label"><!-- 委托人全称： --></label></td>
								<td style="display:none"><input type="hidden" name="wtcount" id="wtcount"
									value="1"> <input type="text" class="form-control"
									name="wt1" id="wt1"  size="auto"></td>
								<td>
								<!-- <select name="wttype1" class="form-control">
										<option value=0>单位</option>
										<option value=1>个人</option>
								</select>
								 -->
								</td>
								<td id="wtaction" style="display:none">
									<DIV id="wtaddDiv">
										<span id="addImg" class="ico16 repeater_plus_16"></span>
									</DIV>
									<DIV id="wtdelDiv" style="display: none;">
										<span id="delImg" class="ico16 repeater_reduce_16"></span>
									</DIV>
								</td>
							</tr>
							<tr>
								<td colspan="4" height="50px;" style="display:none"><hr /></td>
							</tr>
							<tr id="row2-1">
								<td style="text-align: left ;font-size: 18px;" id="dflabel"><label
									for="contrayClient" class="col-sm-2 control-label">客户名称：</label></td>
									</tr>
									<tr>
								<td><input type="hidden" name="dfcount" id="dfcount"
									value="1"> <input type="text" class="weui_input"
									name="df1" id="df1" width="100%" style="height: 30px !important"</td>
								
								
							</tr>
							<tr>
								<td colspan="2" style="text-align: right;">
									<div class="btn-group pull-right" role="group">
										<a class="weui_btn weui_btn_primary" style="background-color: #074888 !important" id="check"><i
											class="fa  fa-send-o fa-fw"></i>查询</a><!--  <a
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

		<div id="content" style="text-align: left; ">
			<font size="5" id="message0" style="margin:auto;"></font><br>
			
			<div id="wttable" cellspacing="0" bordercolor="#000000" style="display: none;" 
				  width="100%">
			</div>
			<br>
			<br>
			<br>
			
			<div id="dftable" cellspacing="0" bordercolor="#000000" 
				 style="display: none;" width="100%"></div>
			<div id="dftable2" cellspacing="0" bordercolor="#000000" 
				 style="display: none;" width="100%"></div>
		</div>

	</div>

	<div id="loadingToast" class="weui_loading_toast" style="display:none;">
    <div class="weui_mask_transparent"></div>
    <div class="weui_toast">
        <div class="weui_loading">
            <!-- :) -->
            <div class="weui_loading_leaf weui_loading_leaf_0"></div>
            <div class="weui_loading_leaf weui_loading_leaf_1"></div>
            <div class="weui_loading_leaf weui_loading_leaf_2"></div>
            <div class="weui_loading_leaf weui_loading_leaf_3"></div>
            <div class="weui_loading_leaf weui_loading_leaf_4"></div>
            <div class="weui_loading_leaf weui_loading_leaf_5"></div>
            <div class="weui_loading_leaf weui_loading_leaf_6"></div>
            <div class="weui_loading_leaf weui_loading_leaf_7"></div>
            <div class="weui_loading_leaf weui_loading_leaf_8"></div>
            <div class="weui_loading_leaf weui_loading_leaf_9"></div>
            <div class="weui_loading_leaf weui_loading_leaf_10"></div>
            <div class="weui_loading_leaf weui_loading_leaf_11"></div>
        </div>
        <p class="weui_toast_content">数据加载中</p>
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
		$("#check").attr("onclick", "check();")
		$("#wtaddDiv").attr("onclick", "addwt();")
		$("#wtdelDiv").attr("onclick", "delwt();")
		$("#dfaddDiv").attr("onclick", "adddf();")
		$("#dfdelDiv").attr("onclick", "deldf();")
		//bindWtData("wt1");

        function onBridgeReady() {
            WeixinJSBridge.call('hideOptionMenu');
        }

        if (typeof WeixinJSBridge == "undefined") {
            if (document.addEventListener) {
                document.addEventListener('WeixinJSBridgeReady', onBridgeReady, false);
            } else if (document.attachEvent) {
                document.attachEvent('WeixinJSBridgeReady', onBridgeReady);
                document.attachEvent('onWeixinJSBridgeReady', onBridgeReady);
            }
        } else {
            onBridgeReady();
        }
    

	});

	//添加委托人
	function addwt() {
		//修改计数器
		var wtcount = parseInt($("#wtcount").val());
		wtcount = wtcount + 1;
		$("#wtcount").val((wtcount).toString());
		var html = '<tr id="row1-'+wtcount+'"><td><input type="text" class="form-control" name="wt'+wtcount+'" id="wt'+wtcount+'" size="30"></td>'
				+ '<td></td>'
				+ '</tr>';
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
				+ '<td></td>'
				+ '</tr>';
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
		$("#check").attr("disabled", "disabled");
		$("#nextstep").attr("disabled", "disabled");
		// $('input').attr("disabled","disabled")//将input元素设置为disabled
		//$('input').removeAttr("disabled");//去除input元素的disabled属性
		var isok=false;
		//锁定页面操作等待返回
		$("#loadingToast").css("display","block");
	coim.checkConflictOfInterest(
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

									var tr = '<h3 class="page_title" style="color:#074888">集团已有客户:</h3>'+
									"<div style='background-color: #074888;color:white !important' class='weui_cell'><div class='weui_cell_bd weui_cell_primary' width='61.8%'>名称</div><div class='weui_cell_bd weui_cell_primary weui_cell_ft' style='color:white !important'>主办律师</div></div>";
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
													tr += "<div class='weui_cell'><div class='weui_cell_bd weui_cell_primary'>" + name
															+ "</div><div class='weui_cell_ft'>"
															+ value["zbls"]
															+ "</div></div>";
												});
										
									} 

									if (add) {
										$("#dftable").html(tr);
										//$("#dftable").attr("border","1");
										//$("#dftable").css("border-style","solid");
										add = false;
									} else {
										$("#dftable").html(
												"<i class='weui_icon_success_circle'>对方当事人没有冲突</i>");
										$("#dftable").css("border-style","none");
									}
									
									$("#loadingToast").css("display","none");
									$("#dftable").css("display","block");
									$("#check").removeAttr("disabled");
									$("#nextstep").removeAttr("disabled");
									$("#message1").css("display","block");
									isok=true;
								}
							});
	
	coim.checkConflictOfInterest(
			wtcount,
			wtma,
			dfcount,
			dfma,
			3,
			{
				success : function(data) {
					//解析json生成table 放到message中去
					var jsonobj = eval("(" + data + ")");
					var dfmessage = jsonobj["df"];

					//$("#message1").html(dfmessage==undefined?"11":dfmessage);

					var tr = '<h3 class="page_title" style="color:#074888">集团预立案客户:</h3>'+
					"<div style='background-color: #074888;color:white !important' class='weui_cell'><div class='weui_cell_bd weui_cell_primary' width='61.8%'>名称</div><div class='weui_cell_bd weui_cell_primary weui_cell_ft' style='color:white !important'>主办律师</div></div>";
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
									tr += "<div class='weui_cell'><div class='weui_cell_bd weui_cell_primary'>" + name
											+ "</div><div class='weui_cell_ft'>"
											+ value["zbls"]
											+ "</div></div>";
								});
						
					} 

					if (add) {
						$("#dftable2").html(tr);
						//$("#dftable").attr("border","1");
						//$("#dftable").css("border-style","solid");
						add = false;
					} else {
						$("#dftable2").html(
								"<i class='weui_icon_success_circle'>对方当事人没有冲突</i>");
						$("#dftable2").css("border-style","none");
					}
					
					$("#loadingToast").css("display","none");
					$("#dftable2").css("display","block");
					$("#check").removeAttr("disabled");
					$("#nextstep").removeAttr("disabled");
					$("#message1").css("display","block");
					isok=true;
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