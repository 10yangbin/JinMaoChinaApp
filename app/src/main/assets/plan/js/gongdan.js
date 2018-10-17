 
/**
 * JS文件功能说明： 
 * 1 arrow 每个设备右边上下箭头的点击动作。 
 * 2 checkStatus(),数据的完整性验证。 
 * 3 change 整改按钮的点击处理
 * 4 步骤里面图片的删除点击
 * 5 强制路线的判断
 *  
 *  data——content 时候尽量不用大写，获取数据的类型的时候，001，会变成 1,需要转换成sting。
 * 
 */

var deviceCode = 'code';
var isSearch = true;
var lastCode = undefined;
var selectedArrow;
var lastArrow;


//页面初始化
$(function() {

	// 国贸项目  整改单的点击处理
	$(".change").click(function(){
		
		var items = $(this).parents("li").find(".con").find("input[type=radio][check=true][data-changeitems=true]");
		
		if(!items || items.length<=0){
			 window.csq_hw.showToast("未获取到待整改项,请检查后重试！");
			 return;
		}
		var data1='';
		for (var i = 0; i < items.length; i++) {
			// data1 = $(items[i]).data('items')
//			var data3 = $(items[i]).attr("data-changeitems");
//			var data4 =  $(items[i]).attr("data-items");
			data1 += ($(items[i]).data('items')+";");
			//alert(data1);
		}
		
		var  woId = $("#woId").text();
		window.location.href = "file:///android_asset/Web/change_apply.html?taskid="+  woId + "&change_items=" + data1;
	});
	
	//箭头的点击动作
	 $(".arrow").click(function() {
		var val = $(this).data("code");
		
		//增加强制路线功能 
		var woType = $("#woType").text();
		if(!woType){
			woType = 2;
		}
		if(woType == 1){
			var route = $(this).data("route");
			if(route >= 1){
				var beforeArrow = $("div[data-route=\""+(route-1)+"\"]");
				if(!checkStatus(beforeArrow)){
					window.csq_hw.showToast("该工单要求您顺序执行，请您检查后重试");
					return;
				}
			}
		}
		
		selectedArrow = this;
		if (lastArrow) {
			// alert($(lastArrow).attr("data-code"));
		}
		if (isSearch || val === deviceCode) {
			var flag = $(this).parents("li").find(".con").css("display");
			if (flag == "none") {
				$(".con").slideUp();
				$(this).parents("li").find(".con").slideDown();
                $(this).parents("li").find(".arrow img").attr("src","../images/arrow2.jpg");
				$(this).children(".scan-input").attr("value", true);
				currentCode = val;
			} else {
				$(this).parents("li").find(".con").slideUp();
                $(this).parents("li").find(".arrow img").attr("src","../images/arrow1.jpg");
			}
			if (isSearch) {
				//alert(lastArrow);
				checkStatus(lastArrow);
				lastArrow = this;
				// ($(lastArrow).attr("data-code"));
			}
		}
		lazyload();
		
	});
	
	
	// 删除照片
	$(".imgList .del").click(function() {
		$(this).parent("li").remove();
	});

	
	$(".imgList li img").click(function() {
		var src = $(this).attr("src");
		// 在此处处理图片
	})

});


String.prototype.getQuery = function(name) {
	var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)");
	var r = this.substr(this.indexOf("\?") + 1).match(reg);
	if (r != null)
		return unescape(r[2]);
	return null;
}


//数据的完整性验证。
function checkStatus(lastArrow) {

	var bool1 = false, bool2 = false, bool3 = false, bool4 = false;
	bool5 = false;
	// alert(lastArrow);
	if (lastArrow) {
		var lastDeviceAll = $(lastArrow).parents("li").find(".con");
		var lastStatus = $(lastArrow).parents("div").find(".status");
		var lastSpan = $(lastStatus).find("span").first();
		var lastStatusInput = $(lastStatus).find("input");
		var inputs = lastDeviceAll.find("input");

		//判断扫码与否
		/*lastDeviceAll.find(".scan-input").each(function() {
			
			//alert("saomao"+$(this).attr("value"));
			
			var text = "11111yiyanzheng";
				
			alert("1-"+text);
			alert("2-"+text == "false");
			alert("3-"+((!text) || (text=="false")));
			
			if (!$(this).attr("value") || $(this).attr("value") == "false") {
				bool4 = true;
				return false;
			}
		});*/
		
		

		//判断多行文本的存储
		lastDeviceAll.find("textarea").each(
				function() {
					var woId = '';
					var strs = new Array();
					var strValue = new Array();
					var dataStr = '';
					var callAjaxResule = null;
					woId = $("#woId").text();
					var data = window.csq_db.db_select(0,
							"select * from fmwowork where fmwo_id='" + woId
									+ "';", '', 2);
					// alert("data:"+data);
					data = eval("(" + data + ");");
					if (data.length > 0) {
						var tmp = $(this).attr("name") + "=";
						callAjaxResule = data[0].fmformData;
						// alert("call"+callAjaxResule);
						strs = callAjaxResule.split(tmp);
						//alert("data2:"+data);
						strValue = strs[1].split("&");

						//alert("strs:"+strValue[0]);
						dataStr = strValue[0];
						//alert(tmp);
						//alert("dataStrLen"+dataStr.length);
						if (dataStr.length <= 0) {
							bool5 = true;
						}
					} else {
						bool5 = true;
					}
				});

		//判断单行文本的存储   //判断数组内容是否为空
		lastDeviceAll.find("input[type=text],input[type=number]").each(
				function() {
					var woId = '';
					var strs = new Array();
					var strValue = new Array();
					var dataStr = '';
					var callAjaxResule = null;
					woId = $("#woId").text();
					var data = window.csq_db.db_select(0,
							"select * from fmwowork where fmwo_id='" + woId
									+ "';", '', 2);
					//alert("data:"+data);
					data = eval("(" + data + ");");
					if (data.length > 0) {
						var tmp = $(this).attr("name") + "=";
						callAjaxResule = data[0].fmformData;
						//alert("call"+callAjaxResule);
						strs = callAjaxResule.split(tmp);
						//alert("data2:"+data);
						strValue = strs[1].split("&");

						//alert("strs:"+strValue[0]);
						dataStr = strValue[0];
						//alert(tmp);
						//lert("dataStrLen"+dataStr.length);
						if (dataStr.length <= 0) {
							bool3 = true;
						} 
					}else {
						bool3 = true;
					}
				});

	/*	alert("1---"+bool1);
		alert("2---"+bool2);
		alert("3---"+bool3);
		alert("4---"+bool4);
		alert("5---"+bool5);*/
		//判断是否查看设备，没有查看，为true。
		if (bool4) {
			$(lastSpan).removeAttr("class").addClass("no");
			$(lastSpan).html("未完成");
			$(lastStatusInput).attr("value", "false");
			return;
		}

		//只要单行，多行，数字内容，其中一个返回值为true，表示内容为空，是未完成状态。
		if (bool3 || bool5) {
			$(lastSpan).removeAttr("class").addClass("no");
			$(lastSpan).html("未完成");
			$(lastStatusInput).attr("value", "false");
			return;
		} else {
			$(lastSpan).removeAttr("class").addClass("over");
			$(lastSpan).html("已完成");
			$(lastStatusInput).attr("value", "true");
		}

		//radio 
		var lastRaidoName = undefined;
		var array = new Array();
		var array1 = new Array();
		//		var length = lastDeviceAll.find("input[type=radio]").length;
		lastDeviceAll.find("input[type=radio],input[type=checkbox]").each(
				function() {

					var name = $(this).attr("name");

					if (lastRaidoName && lastRaidoName != name) {
						//检查名字
						if (in_array(lastRaidoName, array)) {
							bool1 = true;
							return false;
						}
					}
					// 如果name不在array1数组中，添加
					if (in_array(name, array1)) {
						array1.push(name);
					}
					
					// 如果radio被选择，并且名字不在array数组中，添加
					if ($(this).attr("check") == 'true') {
						if (in_array(name, array)) {
							array.push(name);
						}
					}
					// 将本次的点击名字，赋值给最后一个
					lastRaidoName = name;
					//				// alert("anniu:id"+$(this).attr("id")+"type:"+$(this).attr("type"));
					//				bool1 = true;
					//				return false;
				});
		//alert(array1.length+" "+ array.length);
		
		//如果存在的radio数量，大于被点击的，说明有没选择的radio。
		if (array1.length > array.length) {
			bool1 = true;  
		}

		//radio 
		if (bool1) {
			$(lastSpan).removeAttr("class").addClass("no");
			$(lastSpan).html("未完成");
			$(lastStatusInput).attr("value", "false");
			return;
		}

		//判断图片
		lastDeviceAll.find(".imgList").each(function() {
			if ($(this).find("img").length <= 1) {
				bool2 = true;
				return false;
			}
		});
		//判断图片
		if (bool2) {
			$(lastSpan).removeAttr("class").addClass("no");
			$(lastSpan).html("未完成");
			$(lastStatusInput).attr("value", "false");
			return;
		}
		return true;
	}
}
