
/**
 * 计划工单页面数据的缓存,分别保存在业务数据库的 fmwowork (文本和数据), imagefm (图片);
 * 
 * 数据的缓存saveHuancun()，loadAll()恢复
 * 
 * 
 */

var woId = null;
var newnum=0;

function saveHuancun() {
	//alert("调用数据存储方法");
	//获取页面表单数据 
	var formData = $("#callAjaxForm").serialize();
	formData = decodeURIComponent(formData,true);
	woId=$("#woId").text();
	var selectwoId=null;
	//查询表中wo_id
	var sql="Select count(1) as count from fmwowork where fmwo_id='"+woId+"';";
	var selectwoId = window.csq_db.db_select(0, sql, '',2);
	
	//selectwoId =eval(selectwoId);
	selectwoId = eval("(" + selectwoId + ");");
	
	var number=0;
	for (var i = 0; i < selectwoId.length; i++) {
		item = selectwoId[i];	
		number=item.count;
	}
	if(number> 0){
		var time = new Date().getTime();
		var updatesql="update  fmwowork set fmformData='"+formData+"', createtime="+time+" where fmwo_id='"+woId+"';";
		window.csq_db.db_exec(0,updatesql, '',2);
	}else{
		var time = new Date().getTime();
		var insertsql="insert into fmwowork values('"+formData+"','"+woId+"',"+time+");";
		window.csq_db.db_exec(0,insertsql, '',2);
	}
	return true;
}

//将所有存储在sqlLite数据库中的工单相关数据展示在界面
function loadAll() {
	//alert("进来loadAll");
	//获取工单号
	woId=$("#woId").text();
	// 如果数据表不存在，则创建数据表
	window.csq_db.db_exec(0, "create table if not exists fmwowork (fmformData text,fmwo_id text,createtime INTEGER);", '',2);
	window.csq_db.db_exec(0, "create table if not exists imagefm (woid text,path text,wrid text,num text,createtime INTEGER);", '',2);
	//查询该工单号的全部信息
	var data = window.csq_db.db_select(0, "select * from fmwowork where fmwo_id='"+woId+"';", '',2);
	//alert("data:"+data);
	//alert("data.length:"+data.length);
	//alert(data instanceof Array);//判断data是不是数组类型   true为是 
	data = eval("(" + data + ");");
	//alert("data2:"+data);
	if(data==null || data==""){  
		//alert("kong");  
	}else{  
		//alert("bukong");  
		var callAjaxResule=null;
		//包含key，value的数组
		var strs = new Array();
		//只有数据的数组
		var strsplit = new Array();
		//步骤id
		var wrId=null;
		//步骤id对应的value
		var reVal=null;
		var choseid=null;
		for (var i = 0; i < data.length; i++) {
			item = data[i];
			callAjaxResule=item.fmformData;				
		}
		//alert(callAjaxResule);
		//callAjaxResule = "GP-PS-WTPB-CDCQJA005-scan=false&67587=拜拜&67588=555&67591=5句泰康可口可乐了&67589=否&67590=1&67590=2&67590=3&GP-PS-WTPB-CDCQJA008-scan=false&67595=&67596=&67599=&GP-PS-WTPB-CDCQJA006-scan=false&67603=&67604=&67599="
		strs = callAjaxResule.split("&");

		//alert("strs:"+strs);
		//alert("strs:"+strs.length);
		//var wrIds = new Array();
		var mIds = new Array();
		//var wrVals ={};
		for(var i=0;i<strs.length;i++){        
			strsplit=strs[i].split("=");
			//alert("strsplit:"+strsplit);
			//alert("strsplit.length:"+strsplit.length);

			wrId = strsplit[0];
			//alert("wrId:"+wrId);
			if(strsplit.length>=2){
				reVal= strsplit[1];				
			}else{
				reVal= "";
			}
			//item是否查看过
			if(wrId.indexOf("-scan")>0){
				$("input[name='"+wrId+"']").each(function(){
					$(this).attr("value",reVal);
					//alert($(this).attr("value")+" "+$(this).attr("name"));
				});
			}else if(wrId.indexOf("-status")>0){
				$("input[name='"+wrId+"']").each(function(){
					$(this).attr("value",reVal);
					//alert($(this).attr("value")+" "+$(this).attr("name"));
					//alert("11111111---"+reVal);
					var span = $(this).parents("div").find("span").first();
					if(reVal == "true"){
						$(span).removeAttr("class").addClass("over");
	                    $(span).html("已完成");
					}else{
						$(span).removeAttr("class").addClass("no");
	                    $(span).html("未完成");
					}
				});
			}else{
				// input的数据恢复
				$("input[name='"+wrId+"']").each(function(){

					if($(this).attr("type") == "checkbox"){
						if($(this).attr("value") == reVal){
							$(this).attr("checked","true")
							$(this).attr("check","true")
						}
						//修改多个单选无法保存的问题。 杨斌  20160324
					}else if($(this).attr("type") == "radio"){ 
						if($(this).attr("value") == reVal){
							$(this).attr("checked","true")
							$(this).attr("check","true")
						}
					}else if(reVal && document.getElementById(wrId)){
							document.getElementById(wrId).value=reVal;
							
					}else if($(this).attr("type") == "number"){
							document.getElementById(wrId).value=reVal;
					}else {
						//修改radio 是，否数据保存相反的问题 。 杨斌  20160323
						//alert("reVal:"+reVal);
						if(reVal=="否"){
							$("label[for='"+wrId+"-a']").addClass("ui-btn-active ui-radio-on").removeClass("ui-radio-off");
							$("label[for='"+wrId+"-b']").addClass("ui-radio-off").removeClass("ui-btn-active ui-radio-on");
							$("#"+wrId+"-a").attr("checked","none");
							$("#"+wrId+"-a").attr("check","false");
							$("#"+wrId+"-b").attr("check","true");
							$("#"+wrId+"-b").attr("checked","checked");
						}else if(reVal=="是"){
							$("label[for='"+wrId+"-a']").addClass("ui-radio-off").removeClass("ui-btn-actcvive ui-radio-on");
							$("label[for='"+wrId+"-b']").addClass("ui-btn-active ui-radio-on").removeClass("ui-radio-off");
							$("#"+wrId+"-b").attr("check","true");
							$("#"+wrId+"-b").attr("checked","checked");
							$("#"+wrId+"-a").attr("checked","none");
							$("#"+wrId+"-a").attr("check","false");
							
						}

					}
			
				});
				// 文本框的数据恢复
				$("textarea").each(function(){
				    if(reVal && document.getElementById(wrId)){
							document.getElementById(wrId).value=reVal;
					}
			
				});
			}

		} //for循环结束

	}
	//展示照片 王健昌 
	 showimg();
	 lazyload();
	 window.longformain.onPageLoaded();
}


Date.prototype.format = function(format) {
	var o = {
			"M+" : this.getMonth() + 1, // month
			"d+" : this.getDate(), // day
			"h+" : this.getHours(), // hour
			"m+" : this.getMinutes(), // minute
			"s+" : this.getSeconds(), // second
			"q+" : Math.floor((this.getMonth() + 3) / 3), // quarter
			"S" : this.getMilliseconds()
			// millisecond
	}
	if (/(y+)/.test(format))
		format = format.replace(RegExp.$1, (this.getFullYear() + "")
				.substr(4 - RegExp.$1.length));
	for ( var k in o)
		if (new RegExp("(" + k + ")").test(format))
			format = format.replace(RegExp.$1, RegExp.$1.length == 1 ? o[k]
			: ("00" + o[k]).substr(("" + o[k]).length));
	return format;
}

//清除缓存数据
function deleteall(){
	woId=$("#woId").text();
	var sql="Select count(1) as count from fmwowork where fmwo_id='"+woId+"';";	
	var selectwoId = window.csq_db.db_select(0, sql, '',2);
	//alert("selectwoId解析前:"+selectwoId)
	selectwoId =eval(selectwoId);
	var number=0;
	//alert("解析之后:"+selectwoId);
	for (var i = 0; i < selectwoId.length; i++) {
		item = selectwoId[i];	
		//alert("item:"+item.count);
		number=item.count;
	}
	if(number > 0){
		//alert("delete=number:"+number);
		//如果该工单id对应的数据存在, 那么删除
		window.csq_db.db_exec(0, "delete from  fmwowork  where fmwo_id='"+woId+"';", '',2);
		//alert("删除成功");
	}
}

//删除缓存照片
function deleteallimg(){
	woId=$("#woId").text();
	var sql="Select count(1) as count from imagefm where woid='"+woId+"';";
	var selectwoId = window.csq_db.db_select(0, sql, '',2);
	//alert("selectwoId解析前:"+selectwoId)
	selectwoId =eval(selectwoId);
	var number=0;
	//alert("解析之后:"+selectwoId);
	for (var i = 0; i < selectwoId.length; i++) {
		item = selectwoId[i];	
		number=item.count;
	}
	if(number > 0){
		window.csq_db.db_exec(0, "delete from  imagefm  where woid='"+woId+"';", '',2);
		//alert("删除成功");
	}
}


//在数据库中插入图片记录
function insertimage(woId,path,wrId,num){
	//alert("insertimage=woId:"+woId);
	//alert("insertimage=path:"+path);
	//alert("insertimage=wrId:"+wrId);
	//alert("insertimage=num:"+num);
	var time = new Date().getTime();
	var insertimgsql="insert into imagefm (woid,path,wrid,num,createtime) values('"+woId+"','"+path+"','"+wrId+"','"+num+"',"+time+");";
	window.csq_db.db_exec(0,insertimgsql, '',2);
	//alert("插入成功");
}
 

//删除选中照片的记录
function deleteimg(num,woId){
	var delimg="delete from  imagefm  where woid='"+woId+"' and num='"+num+"'";
	window.csq_db.db_exec(0,delimg,'',2);
	//alert("删除成功");
}


//把缓存的照片显示在对应的位置上，需要优化，用本地方法展示
 function showimg(){
	//初始化数据变量
	imageStr = new Array();
	index = 0;
	testindex = 1;
	imgIndex = new Array();

	//alert("开始恢复缓存图片");
	woId=$("#woId").text();
	var imgsql="select * from imagefm where woid='"+woId+"'";
	//alert("woId:"+woId);
	//alert("imgsql:"+imgsql);
	var data2 = window.csq_db.db_select(0,imgsql, '',2);
	//alert("dataimg:"+data2);
	data2 = eval("(" + data2 + ");");
	if(data2==null || data2==""){  
		//alert("kong");  
	}else{  
		//alert("bukong");  
		var num = 0;
		var path = null;
		var wrId = null;
		var t_html = null;
		for (var i = 0; i < data2.length; i++) {
			item = data2[i];
			num=item.num;
			path=item.path;
			wrId=item.wrid;
			//t_html = "<li><img src='../images/camera.jpg' id='photo-"+wrId+"'></li>"
			//alert("num:"+num+"||path:"+path+"||wrId:"+wrId);	

			//把照片存在json串里面
			//记录拍照的任务索引
			if(imgIndex.length == 0){
				imgIndex.push(wrId);
			}else if(in_array(wrId,imgIndex)){
				imgIndex.push(wrId);
			}
			var data1 = new data();
			var obj = new jsonResult();
			obj.method = 'camera';
			obj.data = data1;
			var jsonText = JSON.stringify(obj);
			obj.data.imageList[0].url=path;
			obj.data.imageList[0].path=testindex+'.jpg';
			testindex = testindex + 1;
			//cameraResult(JSON.stringify(obj));
			var resultObj = eval('('+JSON.stringify(obj)+')');
			if(imageStr[wrId] == undefined){
				imageStr[wrId] = resultObj.data.imageList;
				//alert("index1:"+wrId);
			}else{
				imageStr[wrId].push(resultObj.data.imageList[0]);
				//alert("index2:"+wrId);
			}

			//在页面上展示照片
			t_html = $("#Photo_"+wrId).html();
		/*	alert(t_html);
			alert(typeof(t_html));
			alert(t_html .indexOf("'photo_li_"+num+"'"));
			alert(t_html .indexOf("\"photo_li_"+num+"\""));*/
			if(!t_html) t_html = '';
			if(t_html .indexOf("'photo_li_"+num+"'") < 0 && t_html .indexOf("\"photo_li_"+num+"\"") < 0 ){
				//alert('no');
				t_html = "<li id='photo_li_"+num+"'><img  _src='"+path+"' id='"+num+"' onclick=\"firm(this)\"></li>"+ t_html;
			   $("#Photo_"+wrId).html(t_html);
			}
			
			if(num>newnum){
				//给num赋值
				newnum = num;
				//alert("newnum1:"+newnum);
			}
		}

	}
}
 
 
 //判断图片是否存在
 function isHasImg(pathImg){
     var ImgObj=new Image();
     ImgObj.src= pathImg;
      if(ImgObj.fileSize > 0 || (ImgObj.width > 0 && ImgObj.height > 0))
      {
        return true;
      } else {
        return false;
     }
 }
 
 //在展示图片的时候，如果图片被在意外删除或损坏不存在了，删除页面的链接地址。
 function delImgNoFile(a) {
			var woId = $("#woId").text();
			//alert(woId);
			deleteimg(a.id, woId);
			document.getElementById("photo_li_" + a.id).parentNode
			.removeChild(document.getElementById("photo_li_" + a.id));
			showimg();
}
 
 
 //计划工单页面的图片的栏加载效果
function lazyload(){
                $("img[_src]").each(function(){//对有_Src属性的img进行操作
                    var rect=this.getBoundingClientRect();
                    //得到元素的盒子位置对象
                    if((rect.top>0&&rect.top<document.documentElement.clientHeight)
                            ||(rect.bottom>0&&rect.bottom<document.documentElement.clientHeight)){
                        //img的顶部再可视区内，或者img的底部在可视区内。
                        $(this).attr("src",$(this).attr("_src"));
                    }else{
                        $(this).removeAttr("src");
                    }
                });


            }
  
//页面滚动，调用懒加载效果         
  $(window).scroll(function(){
                lazyload();
     });

