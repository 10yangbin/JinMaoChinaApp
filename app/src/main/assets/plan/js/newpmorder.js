
/**
 * JS文件功能说明： 
 * 
 * 1 物料点击填充和逻辑的判断 。
 * 2 计划工单报事的跳转。
 * 
 * 3 change 整改按钮的点击处理
 * 4 步骤里面图片的删除点击
 * 5 强制路线的判断
 *  
 *  data——content 时候尽量不用大写，获取数据的类型的时候，001，会变成 1,需要转换成sting。
 * 
 */

var imageStr = new Array();
var index = 0;
var testindex = 1;
var imgIndex = new Array();
var url = 'http://192.168.33.128:8080/longfor/';
var num = 0;
var myMenu;
var t = 0;
var currentCode;
var isScanToSelect = false;

var wuliao ;  //用于保存每条物料的数据
var wuliaoList =new Array();  //用于保存物料的数组对象
var  useNums = new Array();


//处理拍照、扫码、查看照片、提交表单事件

$(document).on(  
		"click",
		"img",
		function() {
//			alert(this.id);
			if (this.id.search(/scan/i) != -1) {
				index = this.id.replace("scan-", "");
				// 扫码
				window.csq_hw.scanQR();

				// 拍照
			} else if (this.id.search(/photo/i) != -1) {

				index = this.id.replace("photo-", "");
				// alert(index);
				// 记录拍照的任务索引
				if (imgIndex.length == 0) {
					imgIndex.push(index);
				} else if (in_array(index, imgIndex)) {
					imgIndex.push(index);
				}
				// alert(JSON.stringify(imgIndex));
				// 拍照
				var scrollPx = $(document).scrollTop();
				var woId = $("#woId").text();
				var data = {
						"scroll" : scrollPx,
						"code" : currentCode,
						"woId" : woId,
						"index" : index,
						"num" : num
				};
				window.csq_io.file_write('takephoto-' + woId, JSON
						.stringify(data), false);
				window.csq_hw.selectImage();

				// 查看照片
			}
		});

window.onload = function() {

	setTimeout(function() {
		var scrolltop = window.sessionStorage.getItem("scroll");
		$(document).scrollTop(scrolltop);
		window.sessionStorage.clear();
	}, 200);
}

$(document).on(
		"click",
		"a",
		function() {

			// 扫码
			if (this.id.search(/scan/i) != -1) {
				index = this.id.replace("scan-", "");
				// 扫码
				window.csq_hw.scanQR();

				// 拍照
			} else if (this.id.search(/photo/i) != -1) {
				index = this.id.replace("photo-", "");

				// 记录拍照的任务索引
				if (imgIndex.length == 0) {
					imgIndex.push(index);
				} else if (in_array(index, imgIndex)) {
					imgIndex.push(index);
				}
				// 拍照
				window.csq_hw.selectImage();
				// 查看照片
			} else if (this.id.search(/review/i) != -1) {
				index = this.id.replace("review-", "");

				var data1 = new data();
				var obj = new jsonResult();

				obj.method = 'scanImage';
				obj.data = data1;

				if (imageStr[index].length > 0) {
					obj.data.imageList = imageStr[index];
					var jsonText = JSON.stringify(obj);
					$.get(url, {
						data : jsonText
					}, function(data) {

					});
				}
			}else if (this.id.search(/del/i) != -1) {
				index = this.id.replace("del-", "");

				var data1 = new data();
				var obj = new jsonResult();
				obj.data = data1;

				if (imageStr[index] != undefined) {
					obj.data.imageList = imageStr[index];
				}
				deleteImageResult(JSON.stringify(obj));

				// 测试上传照片  ？？
			} else if (this.id.search(/upload/i) != -1) {
				var data1 = new data();
				var obj = new jsonResult();

				obj.method = 'uploadImage';
				obj.data = data1;

				for ( var i in imgIndex) {
					var arr = imageStr[imgIndex[i]];
					for ( var j in arr) {
						obj.data.imageList.push(arr[j]);
					}
				}
				// 删除第一个空值元素
				obj.data.imageList.splice(0, 1);

				var jsonText = JSON.stringify(obj);
				$.get(url, {
					data : jsonText
				}, function(data) {

				});

				// 测试上传结果 ？？
				var returnObj = eval('(' + jsonText + ')');
				for ( var j in returnObj.data.imageList) {
					returnObj.data.imageList[j].url = "url"
						+ returnObj.data.imageList[j].path;
				}

				uploadImageResult(JSON.stringify(returnObj));

			}
		});

//获取扫码结果
function scanZxingResult(result) {
	// alert(result);
	var objCode = $('#' + index).val();
	// alert("index:"+index);
	objCode = objCode.substring(0, objCode.indexOf('|'));
	// alert("objCode:"+objCode);
	var jsonObj = eval('(' + result + ')');
	var zxingCode = jsonObj.data.zxingCode;
	if (zxingCode.indexOf(objCode) != -1) {
		$('#' + index).val(objCode + '|' + '已验证');
	} else {
		$('#' + index).val(objCode + '|' + '未验证');
	}

}

//获取拍照结果
function cameraResult(result) {
	// alert(result);
	var resultObj = eval('(' + result + ')');
	if (imageStr[index] == undefined) {
		imageStr[index] = resultObj.data.imageList;
		// alert("index1:"+index);
	} else {
		imageStr[index].push(resultObj.data.imageList[0]);
		// alert("index2:"+index);
	}
}

//获取查看照片结果，暂无
function scanImageResult(result) {

}

/**
 * window.csq_hw.selectImage()的回调方法，
 * 
 * @param result
 *            返回的本次拍照的全路径格式为file:///XXXX
 */
function pictureCallback(result) {
	var woId = $("#woId").text();
	window.csq_io.delfile('takephoto-' + woId);
	var data1 = new data();
	var obj = new jsonResult();

	obj.method = 'camera';
	obj.data = data1;
	var jsonText = JSON.stringify(obj);

	obj.data.imageList[0].url = result;

	obj.data.imageList[0].path = testindex + '.jpg';
	testindex = testindex + 1;

	cameraResult(JSON.stringify(obj));

	// alert("newnum2:"+newnum);

	if (t == 0) {
		num = newnum;
	}
	num++;
	t++;
	// alert("num:"+num);
	var woId = $("#woId").text();
	/**
	 * **alert("woId:"+woId); //alert("result:"+result);
	 * //alert("index:"+index); //alert("num:"+num);
	 */
    //alert(result);
	if(result!='null'){
	insertimage(woId, result, index, num);

	var t_html = $("#Photo_" + index).html();
	// $("#Photo_"+index).html("<a ><img id=\""+num+"\" onclick=\"firm(this)\"
	// src=\"" + result + "\"></a>" + t_html);
	
		$("#Photo_" + index).html(
				"<li id='photo_li_" + num + "'><img id=\"" + num
				+ "\" onclick=\"firm(this)\" src=\"" + result
				+ "\" _src=\"" + result + "\" ></li>" + t_html);
	}

}

function pictureCallbackAfterRestart(result) {
	//先回调原处理逻辑
	var data = window.csq_io.file_read('takephoto-' + woId);
	if (data) {
		data = eval("(" + data + ");");
	}
	num = data.num;
	index = data.index;
	pictureCallback(result);
	//延时执行打开相应的条目
	setTimeout(function() {
		var woId = $("#woId").text();
		// alert(woId);
		if (data) {
			var scrolltop = data.scroll;
			var code = data.code;
			//alert(code+" "+scrolltop);
			if (!code)
				return;
			var selectedArrow = $('.arrow[data-code="' + code + '"]');
			//alert(selectedArrow);
			var flag = $(selectedArrow).parents("li").find(".con").css(
			"display");
			if (flag == "none") {
				$(".con").slideUp();
				$(selectedArrow).parents("li").find(".con").slideDown();
			} else {
				$(selectedArrow).parents("li").find(".con").slideUp();
			}
			$(selectedArrow).children(".scan-input").attr("value", true);
			if (scrolltop && !isNaN(scrolltop)) {
				setTimeout(function() {
					$(document).scrollTop(scrolltop);
				}, 200);
			}
		}
		window.csq_io.delfile('takephoto-' + woId);
	}, 200);
}

function firm(a) {
	// alert(a.id);
	// 利用对话框返回的值 （true 或者 false）
	// window.open ('', 'newwindow', 'height=100, width=400, top=0, left=0,
	// toolbar=no, menubar=no, scrollbars=no, resizable=no,location=n o,
	// status=no') ;
	if (confirm("是否删除？")) {
		var woId = $("#woId").text();
		//alert(woId);
		deleteimg(a.id, woId);
		document.getElementById("photo_li_" + a.id).parentNode
		.removeChild(document.getElementById("photo_li_" + a.id));
		showimg();
	} else {
		return;
	}

}

//获取删除照片结果
function deleteImageResult(result) {
	var resultObj = eval('(' + result + ')');
	var images = imageStr[index];
	for ( var i in images) {
		if (images[i].path == resultObj.data.imageList[0].path) {
			images.splice(i, 1);
		}
	}

}

//获取上传照片结果  ？？ 是否有使用
function uploadImageResult(result) {
	var resultObj = eval('(' + result + ')');
	var imgs = resultObj.data.imageList;

	for (var i = 0; i < imgs.length;) {
		for (var j = 0; j < imgIndex.length; j++) {
			var img = imageStr[imgIndex[j]];
			for (var k = 0; k < img.length; k++) {
				img[k] = imgs[i];
				$('#' + imgIndex[j]).val(
						$('#' + imgIndex[j]).val() + imgs[i].url + '|');
				i++;
			}
			$('#' + imgIndex[j]).val(
					$('#' + imgIndex[j]).val().substring(0,
							$('#' + imgIndex[j]).val().length - 1));
		}
	}
}

//关闭web接口,暂无
function finishInfoResult(result) {

}

function in_array(val, arr) {
	for (i in arr) {
		if (arr[i] == val) {
			return false;
		}
	}
	return true;
}
//结果对象
function jsonResult() {
	this.method = "";
	this.data = new Object;
}
//结果参数对象
function data() {
	this.content = "";
	this.zxingCode = "";
	this.imageList = new Array({
		"path" : "",
		"url" : ""
	});
}

/**
 * scanQR()的回调方法，
 * 
 * @param phCode
 *            返回的json串 格式为{"id":"code"}
 */

function scanBarcodeSuccessCallBack(phCode) {
	// alert(phCode);
	phCode = eval("(" + phCode + ");");
	phCode = phCode.id;
//	alert(phCode);

	if(isScanToSelect){
		scrollToIndex(phCode);
		return;
	}

	var inputId = index + "-scan";
//	alert(inputId);
	var eqId = $("#" + inputId).attr("code");
//	alert("eqId:"+eqId);
	if (phCode == eqId) {
		document.getElementById(inputId).value = eqId + "|已验证";
		saveHuancun();
	} else {
		window.csq_hw.showToast("该设备不对");
	}
	// var data1 = new data();
	// var obj = new jsonResult();

	// obj.method = 'scanZxing';
	// obj.data = data1;
	// var jsonText = JSON.stringify(obj);
	/*
	 * $.get(url,{data:jsonText},function(data){
	 * 
	 * });
	 */
	// 测试回调函数
	// obj.data.zxingCode = phCode;
	// 王健昌2015-4-25
	var busType = $("#busType").text();
	if (busType == "10") {// 抄表 扫码定位 表上
		location.href = "#" + phCode; // 定位
		var target = $(location.hash);
		if (target.length == 1) {// header向下移动
			var top = target.offset().top - 44;
			if (top >= 0) {
				$('html,body').animate({
					scrollTop : top
				}, 1000);
			}
		}

	}
	// scanZxingResult(JSON.stringify(obj));
}

//验证是否为空 王健昌  ，这个方法暂时用途不大。
function myCheck() {
	for (var i = 0; i < document.forms.callAjaxForm.elements.length - 1; i++) {
		if (document.forms.callAjaxForm.elements[i].value == "") {
			// alert("当前表单不能有空项");
			window.csq_hw.showToast("当前表单不能有空项");
			document.forms.callAjaxForm.elements[i].focus();
			document.getElementById('disabled').id = "submit";
			var d = document.getElementById('submit');
			d.style.background = "#006E92";
			return false;
		}
	}
	return true;
}
//格式化说明数据 王健昌
function getptext(a) {
	// alert("s");
	var li_id = "li_" + a.id;
	var x = document.getElementById(li_id);
	var str = x.innerHTML;

	if (str.indexOf("<br>") < 0) {
		// alert(fun(str));
		var newstr = fun(str);
		x.innerHTML = newstr;
	}

}
function fun(str) {
	var arr = str.split(".");
	var newstr = "";
	var index = 1;
	for (var i = 1; i < arr.length; i++) {
		if (i != arr.length - 1) {
			newstr = newstr + index + "."
			+ arr[i].substring(0, arr[i].length - 1) + "<br>";
		} else {
			newstr = newstr + index + "." + arr[i] + "<br>";
		}
		index++;
	}
	return newstr;
}

//显示加载器
function showLoader() {
	// 显示加载器.for jQuery Mobile 1.2.0
	$.mobile.loading('show', {
		text : '加载中...', // 加载器中显示的文字
		textVisible : true, // 是否显示文字
		theme : 'a', // 加载器主题样式a-e
		textonly : false, // 是否只显示文字
		html : "" // 要显示的html内容，如图片等
	});
}

//隐藏加载器.for jQuery Mobile 1.2.0
function hideLoader() {
	// 隐藏加载器
	$.mobile.loading('hide');
}


function backTrack() {
	saveHuancun();
	//window.history.go(-1);
	var  woId = $("#woId").text();
	window.location.href = "file:///android_asset/Web/liucheng.html?taskId="+  woId+ "&plan=" +'P';
}

function backCallBack() {
	backTrack();
}

function sendToServer() {

	var bool1 = false, bool2 = false, bool3 = false, bool4 = false;

	//radio 
	var lastRaidoName = undefined;
	var array = new Array();
	var array1 = new Array();
	
	//var length = lastDeviceAll.find("input[type=radio]").length;
	$("input[type=radio],input[type=checkbox]").each(
			function() {
				var name = $(this).attr("name");

				if (lastRaidoName && lastRaidoName != name) {
					//检查名字
					if (in_array(lastRaidoName, array)) {
						bool1 = true;
						return false;
					}
				}
				if (in_array(name, array1)) {
					array1.push(name);
				}
				if ($(this).attr("check") == 'true') {
					if (in_array(name, array)) {
						array.push(name);
					}
				}
				lastRaidoName = name;

	});

	//供选择按钮是否全部点击
	if (array1.length > array.length) {
		bool1 = true;
	}

	if (bool1) {
		window.csq_hw.showToast("您还有没有查看的设备");
		return;
	}

	//在需要图片描述时，是否添加了图片描述
	$(".imgList").each(function() {
		if ($(this).find("img").length <= 1) {
			// if($(this).html() === "") {
			$('body,html').animate({
				scrollTop : $("#" + $(this).attr("id")).offset().top
			}, 300);
			// alert("paizhao:id"+$(this).attr("id"));
			bool2 = true;
			return false;
		}
	});

	//图片
	if (bool2) {
		window.csq_hw.showToast("缺少照片描述！");
		return;
	}

	// 用于存储校验合格的checkbox的name;
	var checkedList = new Array();
	// 用于存储所有的checkbox的name;
	var nameList = new Array();
	var name;
	var lastname = undefined;
	var checked;
	var checkBoxes = $("input[type=checkbox]");

	var hasChecked = true;

	if (checkBoxes.length > 0) {
		hasChecked = false;
	}
	// 对多选进行校验 ，暂时没有多选的功能。
	$(checkBoxes).each(function() {
		name = $(this).attr("name");
		// alert(name);
		checked = $(this).attr("check");
		if (checked && !hasChecked) {
			hasChecked = true;
		}
		if (lastname != undefined && lastname != name) {
			if (nameList.length > checkedList.length) {
				// alert("nameList"+nameList+" checkedList"+checkedList);
				bool3 = true;
				return false;
			}
		}

		if (in_array(name, nameList)) {
			// alert("nameList add "+name);
			nameList.push(name);
		}
		// else{
		// alert("nameList exists "+name);
		// }

		if (in_array(name, checkedList)) {
			if (checked) {
				// alert("checkedList add "+name);
				checkedList.push(name);
			}

			// else{ alert("checkedList 未选中 "+name); }

		} else {
			// alert("checkedList exists "+name);
			return;
		}
		lastname = name;
	});

	if (bool1 || bool2 || (bool3 || !hasChecked)) {
		window.csq_hw.showToast("请拍照或者选择按钮");
		var formResult = false;
	} else {
		var formResult = myCheck();
	}

	window.csq_hw.showToast("正在处理……");
	// 更改提交id，所以只能提交一次
	document.getElementById('submit').id = "disabled";
	var d = document.getElementById('disabled');
	d.style.background = "gray";

	// var formResult=myCheck();
	// alert(formResult);
	if (eval(formResult)) {
		// 加载onload事件
		if (confirm("确认是否提交？")) {
			window.csq_hw.showToast("正在处理……");
			// alert("this.id:"+this.id);
			var data1 = new data();
			var obj = new jsonResult();

			obj.method = 'uploadImage';
			obj.data = data1;

			var formData = $("#callAjaxForm").serialize();
			var woId = $("#woId").text();

			var Param = {
					"callAjaxForm" : formData,
					'borrow': wuliaoList, //   物料  多个 JSON.stringify(wuliaoList)
					"woId" : woId
			};

			// alert("Param:"+JSON.stringify(Param));
			for ( var i in imgIndex) {
				var arr = imageStr[imgIndex[i]];
				for ( var j in arr) {
					obj.data.imageList.push(arr[j]);
				}
			}
			// 删除第一个空值元素
			obj.data.imageList.splice(0, 1);
			var jsonText = JSON.stringify(obj);
			// alert("jsonText:"+jsonText);

			// 或去每张照片本地地址
			// 上传图片和文件 王健昌
			var returnObj = eval('(' + jsonText + ')');
			if (imgIndex.length != 0) {
				// alert("传照片层");
				var uploadurl = '';
				for ( var j in returnObj.data.imageList) {
					uploadurl += "," + returnObj.data.imageList[j].url;
				}
				uploadurl = uploadurl.substring(1);
				var back = window.csq_exe.exec("P", JSON.stringify(Param),
						uploadurl, true);
			} else {
				var back = window.csq_exe.exec("P", JSON.stringify(Param),
						null, true);
			}

			// alert("back1:"+back);
			if (back) {
				window.csq_hw.showToast("提交成功");
				// 关闭onload事件
				// hideLoader();
				// deleteall();//删除缓存数据
				// deleteallimg();//删除缓存照片
				window.csq_hw.showToast("更新计划工单，请稍候");
				//woId = woId.replace("P", "");
				window.location.href = "file:///android_asset/Web/gongdan_list.html?index="
					+ 4 + "&task_type=" +'P';
			} else {

				window.csq_hw.showToast("提交失败");
				document.getElementById('disabled').id = "submit";
				var d = document.getElementById('submit');
				d.style.background = "#006E92";
			}

		} else {
			document.getElementById('disabled').id = "submit";
			var d = document.getElementById('submit');
			d.style.background = "#006E92";
		}
		// 测试删除照片
	} else {
		document.getElementById('disabled').id = "submit";
		var d = document.getElementById('submit');
		d.style.background = "#006E92";
	}

}

function saveCache() {

	$(".arrow").each(function() {
		checkStatus(this);
	});

	document.getElementById('huancun').id = "disabledh";
	var d = document.getElementById('disabledh');
	d.style.background = "gray";
	var test = saveHuancun();

	if (test = "true") {
		window.csq_hw.showToast("缓存成功");
		document.getElementById('disabledh').id = "huancun";
		var d = document.getElementById('huancun');
		d.style.background = "#990033";
	}
}

// radio 的点击处理 
function to_change(a) {

	if ($(a).attr("type") == 'radio') {// 单选不让取消掉
		//alert($(a).attr("type"));
		$("input[name='" + a.name + "']").attr("check", "false");
	}

	if ($(a).attr("check") != 'false' && $(a).attr("check")) {
		$(a).removeAttr("check");
	} else {
		$(a).attr("check", "true");
	}
	// alert("check " + $(a).attr("check") + " " +"checked " +
	// $(a).attr("checked"));
	saveHuancun();
}

/**
 * 1 扫码去定位到某个设备并打开
 * 
 * 2 添加强制路线的例外
 * 
 */
function scrollToIndex(code){
	isScanToSelect =  false;

	$("header").find("input").attr("value", code);
	deviceCode = code;
	//此处表示更换设备 需对上个设备是否巡视完成进行更改
	var arrows = $(".arrow");
	var selectedArrow;
	var lastArrow;
	var code1;
	var index = 0;
	var isError = true;
	for (var i = 0; i < arrows.length; i++) {
		code1 = $(arrows[i]).data("code");
		if (code1 === deviceCode) {
			selectedArrow = arrows[i];
			index = i;
			isError = false;
		} else if (code1 === lastCode) {
			lastArrow = arrows[i];
		}
	}

	//alert(deviceCode);
	//先找到箭头 然后修改是否打开的标志位 并打开
	if (isError) {
		window.csq_hw.showToast("你扫的设备有误！");
	} else {

		var woType = $("#woType").text();
		if(!woType){
			woType = 2;
		}
		if( woType && woType == 1){
			var route = $(selectedArrow).data("route");
			if(route >= 1){
				var beforeArrow = $("div[data-route=\""+(route-1)+"\"]");
				if(!checkStatus(beforeArrow)){
					window.csq_hw.showToast("该工单要求您顺序执行，请您检查后重试");
					return;
				}
			}
		}

		currentCode=deviceCode;
		var flag = $(selectedArrow).parents("li").find(".con").css("display");
		if (flag == "none") {
			$(".con").slideUp();
			$(selectedArrow).parents("li").find(".con").slideDown();
		} else {
			$(selectedArrow).parents("li").find(".con").slideUp();
		}
//		$(selectedArrow).children(".scan-input").attr("value", true);
		//alert($(selectedArrow).children(".scan-input").attr("value"));
		setTimeout(function() {
			var top = $(selectedArrow).parent();
			var singleHeight = top.outerHeight();
			//计算高度
			var height =(index-1) * singleHeight + 140+(index+1)*8;
			$(document).scrollTop(height);
		}, 200);
	}
	if (lastArrow && deviceCode && deviceCode != lastCode) {
		checkStatus(lastArrow);

	}
	//  alert("lastArrow:" + lastArrow + " deviceCode:" + deviceCode + "lastCode" + lastCode);

	lastCode = deviceCode;
	lazyload();

}

/**
 * 扫码定位到设备的点击事件
 */
function scanToSelect(){
	isScanToSelect = true;
	window.csq_hw.scanQR();
}



// 计划工单相关的报事 
function planBaoShi(){
	var  woId = $("#woId").text();
	window.location.href = "file:///android_asset/Web/baoshi.html?taskid="+  woId;

}

